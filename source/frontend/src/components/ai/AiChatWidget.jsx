import { useRef, useState } from 'react';
import { useLocation } from 'react-router-dom';
import {
  Bot,
  ChevronDown,
  MessageCircle,
  Plus,
  Send,
  ShieldCheck,
  Sparkles,
  Trash2,
  X,
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import aiChatService from '../../services/aiChatService';
import './AiChatWidget.css';

const STARTER_PROMPTS = [
  'Hôm nay tôi có lịch tập gì?',
  'Phân tích thực đơn gần nhất của tôi',
  'Gói tập của tôi còn bao lâu?',
  'Tôi nên ăn gì trước buổi tập?',
];

const AiChatWidget = () => {
  const { user } = useAuth();
  const location = useLocation();
  const [isOpen, setIsOpen] = useState(false);
  const [initialized, setInitialized] = useState(false);
  const [conversations, setConversations] = useState([]);
  const [activeConversation, setActiveConversation] = useState(null);
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [deepAnalysis, setDeepAnalysis] = useState(false);
  const [loading, setLoading] = useState(false);
  const [sending, setSending] = useState(false);
  const [error, setError] = useState('');
  const abortControllerRef = useRef(null);
  const messageListRef = useRef(null);
  const localMessageIdRef = useRef(0);

  const shouldRender =
    user?.role === 'MEMBER' && location.pathname.startsWith('/member/');

  const scrollToBottom = () => {
    window.requestAnimationFrame(() => {
      const element = messageListRef.current;
      if (element) element.scrollTop = element.scrollHeight;
    });
  };

  const loadConversation = async (conversation) => {
    setLoading(true);
    setError('');
    try {
      const data = await aiChatService.getMessages(conversation.id);
      setActiveConversation(conversation);
      setMessages(data);
      scrollToBottom();
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    } finally {
      setLoading(false);
    }
  };

  const initializeChat = async () => {
    setLoading(true);
    setError('');
    try {
      let items = await aiChatService.getConversations();
      if (items.length === 0) {
        const created = await aiChatService.createConversation();
        items = [created];
      }
      setConversations(items);
      setActiveConversation(items[0]);
      const history = await aiChatService.getMessages(items[0].id);
      setMessages(history);
      setInitialized(true);
      scrollToBottom();
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleToggle = () => {
    const nextOpen = !isOpen;
    setIsOpen(nextOpen);
    if (nextOpen && !initialized) initializeChat();
  };

  const handleNewConversation = async () => {
    if (sending) return;
    setLoading(true);
    setError('');
    try {
      const created = await aiChatService.createConversation();
      setConversations((current) => [created, ...current]);
      setActiveConversation(created);
      setMessages([]);
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleSelectConversation = async (event) => {
    const selectedId = Number(event.target.value);
    const selected = conversations.find((item) => item.id === selectedId);
    if (selected && selected.id !== activeConversation?.id) {
      await loadConversation(selected);
    }
  };

  const handleDeleteConversation = async () => {
    if (!activeConversation || sending) return;
    if (!window.confirm('Bạn có chắc muốn xóa toàn bộ hội thoại này?')) return;

    setLoading(true);
    setError('');
    try {
      await aiChatService.deleteConversation(activeConversation.id);
      let remaining = conversations.filter(
        (item) => item.id !== activeConversation.id,
      );
      if (remaining.length === 0) {
        const created = await aiChatService.createConversation();
        remaining = [created];
      }
      setConversations(remaining);
      setActiveConversation(remaining[0]);
      const history = await aiChatService.getMessages(remaining[0].id);
      setMessages(history);
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleConsentChange = async (event) => {
    if (!activeConversation || sending) return;
    const consent = event.target.checked;
    if (
      consent &&
      !window.confirm(
        'Cho phép GymPro gửi các chỉ số trong hồ sơ thể chất của bạn tới Gemini để cá nhân hóa câu trả lời khi có liên quan? Không gửi email, số điện thoại hoặc thông tin đăng nhập.',
      )
    ) {
      return;
    }

    try {
      const updated = await aiChatService.updateConsent(
        activeConversation.id,
        consent,
      );
      setActiveConversation(updated);
      setConversations((current) =>
        current.map((item) => (item.id === updated.id ? updated : item)),
      );
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    }
  };

  const sendMessage = async (text) => {
    const cleanText = text.trim();
    if (!cleanText || !activeConversation || sending) return;

    localMessageIdRef.current += 1;
    const localId = localMessageIdRef.current;
    const userMessage = {
      id: `local-user-${localId}`,
      role: 'USER',
      content: cleanText,
    };
    const assistantId = `local-ai-${localId}`;
    setMessages((current) => [
      ...current,
      userMessage,
      { id: assistantId, role: 'ASSISTANT', content: '', streaming: true },
    ]);
    setInput('');
    setError('');
    setSending(true);
    scrollToBottom();

    const controller = new AbortController();
    abortControllerRef.current = controller;

    try {
      await aiChatService.streamMessage(
        activeConversation.id,
        { message: cleanText, deepAnalysis },
        (eventName, data) => {
          if (eventName === 'chunk') {
            setMessages((current) =>
              current.map((message) =>
                message.id === assistantId
                  ? { ...message, content: message.content + (data.text || '') }
                  : message,
              ),
            );
            scrollToBottom();
          } else if (eventName === 'done') {
            setMessages((current) =>
              current.map((message) =>
                message.id === assistantId
                  ? { ...data, streaming: false }
                  : message,
              ),
            );
          } else if (eventName === 'error') {
            throw new Error(data.message || 'GymPro AI không thể trả lời.');
          }
        },
        controller.signal,
      );

      const refreshed = await aiChatService.getConversations();
      setConversations(refreshed);
      const currentConversation = refreshed.find(
        (item) => item.id === activeConversation.id,
      );
      if (currentConversation) setActiveConversation(currentConversation);
    } catch (err) {
      if (err.name === 'AbortError') {
        setMessages((current) =>
          current.filter((message) => message.id !== assistantId),
        );
      } else {
        setError(err.message || 'GymPro AI tạm thời không khả dụng.');
        setMessages((current) =>
          current.filter((message) => message.id !== assistantId),
        );
      }
    } finally {
      setSending(false);
      abortControllerRef.current = null;
    }
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    sendMessage(input);
  };

  const handleClose = () => {
    abortControllerRef.current?.abort();
    setIsOpen(false);
  };

  if (!shouldRender) return null;

  return (
    <div className="ai-chat-root">
      {isOpen && (
        <section className="ai-chat-panel" aria-label="Trợ lý GymPro AI">
          <header className="ai-chat-header">
            <div className="ai-chat-brand">
              <span className="ai-chat-brand-icon"><Bot size={20} /></span>
              <div>
                <strong>GymPro AI</strong>
                <small>{sending ? 'Đang trả lời...' : 'Trợ lý thể hình'}</small>
              </div>
            </div>
            <div className="ai-chat-header-actions">
              <button
                type="button"
                title="Cuộc trò chuyện mới"
                onClick={handleNewConversation}
                disabled={sending}
              >
                <Plus size={18} />
              </button>
              <button type="button" title="Đóng" onClick={handleClose}>
                <X size={18} />
              </button>
            </div>
          </header>

          <div className="ai-chat-toolbar">
            <label className="ai-chat-conversation-select">
              <select
                value={activeConversation?.id || ''}
                onChange={handleSelectConversation}
                disabled={loading || sending}
              >
                {conversations.map((conversation) => (
                  <option key={conversation.id} value={conversation.id}>
                    {conversation.title}
                  </option>
                ))}
              </select>
              <ChevronDown size={14} />
            </label>
            <button
              type="button"
              className="ai-chat-delete"
              title="Xóa hội thoại"
              onClick={handleDeleteConversation}
              disabled={!activeConversation || sending}
            >
              <Trash2 size={16} />
            </button>
          </div>

          <div className="ai-chat-privacy">
            <label>
              <input
                type="checkbox"
                checked={Boolean(activeConversation?.physicalDataConsent)}
                onChange={handleConsentChange}
                disabled={!activeConversation || sending}
              />
              <ShieldCheck size={15} />
              Dùng hồ sơ thể chất để cá nhân hóa
            </label>
          </div>

          <div className="ai-chat-messages" ref={messageListRef}>
            {loading && <div className="ai-chat-status">Đang tải...</div>}

            {!loading && messages.length === 0 && (
              <div className="ai-chat-welcome">
                <Bot size={34} />
                <h3>Chào bạn, tôi là GymPro AI</h3>
                <p>Tôi có thể giải thích lịch tập, thực đơn và gói tập của bạn.</p>
                <div className="ai-chat-starters">
                  {STARTER_PROMPTS.map((prompt) => (
                    <button
                      type="button"
                      key={prompt}
                      onClick={() => sendMessage(prompt)}
                      disabled={sending}
                    >
                      {prompt}
                    </button>
                  ))}
                </div>
              </div>
            )}

            {messages.map((message) => (
              <div
                key={message.id}
                className={`ai-chat-message ${
                  message.role === 'USER' ? 'user' : 'assistant'
                }`}
              >
                {message.role === 'ASSISTANT' && (
                  <span className="ai-message-avatar"><Bot size={15} /></span>
                )}
                <div className="ai-message-bubble">
                  {message.content || (message.streaming ? (
                    <span className="ai-typing">● ● ●</span>
                  ) : null)}
                </div>
              </div>
            ))}
          </div>

          {error && <div className="ai-chat-error">{error}</div>}

          <div className="ai-chat-options">
            <label title="Dùng Gemini 3.6 Flash cho câu hỏi khó, tốn quota hơn">
              <input
                type="checkbox"
                checked={deepAnalysis}
                onChange={(event) => setDeepAnalysis(event.target.checked)}
                disabled={sending}
              />
              <Sparkles size={14} />
              Phân tích sâu
            </label>
            <span>Free Tier: tối đa 10 lượt/giờ</span>
          </div>

          <form className="ai-chat-input" onSubmit={handleSubmit}>
            <textarea
              value={input}
              onChange={(event) => setInput(event.target.value)}
              onKeyDown={(event) => {
                if (event.key === 'Enter' && !event.shiftKey) {
                  event.preventDefault();
                  handleSubmit(event);
                }
              }}
              maxLength={2000}
              rows={1}
              placeholder="Hỏi GymPro AI..."
              disabled={sending || !activeConversation}
            />
            <button
              type="submit"
              disabled={!input.trim() || sending || !activeConversation}
              aria-label="Gửi tin nhắn"
            >
              <Send size={18} />
            </button>
          </form>
          <p className="ai-chat-disclaimer">
            AI chỉ mang tính tham khảo, không thay thế tư vấn y tế.
          </p>
        </section>
      )}

      <button
        type="button"
        className={`ai-chat-launcher ${isOpen ? 'open' : ''}`}
        onClick={handleToggle}
        aria-label={isOpen ? 'Đóng GymPro AI' : 'Mở GymPro AI'}
      >
        {isOpen ? <X size={24} /> : <MessageCircle size={26} />}
        {!isOpen && <span>GymPro AI</span>}
      </button>
    </div>
  );
};

export default AiChatWidget;
