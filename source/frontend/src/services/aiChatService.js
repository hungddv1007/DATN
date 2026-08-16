import api from './api';

const createConversation = async () => {
  const response = await api.post('/member/ai/conversations', {});
  return response.data;
};

const getConversations = async () => {
  const response = await api.get('/member/ai/conversations');
  return response.data;
};

const getMessages = async (conversationId) => {
  const response = await api.get(
    `/member/ai/conversations/${conversationId}/messages`,
  );
  return response.data;
};

const updateConsent = async (conversationId, consent) => {
  const response = await api.patch(
    `/member/ai/conversations/${conversationId}/consent`,
    { consent },
  );
  return response.data;
};

const deleteConversation = async (conversationId) => {
  await api.delete(`/member/ai/conversations/${conversationId}`);
};

const requestHandoff = async (conversationId) => (await api.post(
  `/member/ai/conversations/${conversationId}/handoff`, { consent: true },
)).data;

const sendHumanMessage = async (conversationId, message) => (await api.post(
  `/member/ai/conversations/${conversationId}/human-messages`, { message },
)).data;

const streamMessage = async (
  conversationId,
  payload,
  onEvent,
  signal,
) => {
  const token = localStorage.getItem('token');
  const response = await fetch(
    `/api/member/ai/conversations/${conversationId}/messages`,
    {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
      body: JSON.stringify(payload),
      signal,
    },
  );

  if (!response.ok) {
    let message = 'Không thể gửi tin nhắn tới GymPro AI.';
    try {
      const errorBody = await response.json();
      message = errorBody.message || message;
    } catch {
      // Phản hồi lỗi không có JSON hợp lệ.
    }
    throw new Error(message);
  }

  if (!response.body) {
    throw new Error('Trình duyệt không hỗ trợ nhận phản hồi trực tuyến.');
  }

  const reader = response.body.getReader();
  const decoder = new TextDecoder();
  let buffer = '';

  const processBlock = (block) => {
    let eventName = 'message';
    const dataLines = [];
    block.split('\n').forEach((line) => {
      if (line.startsWith('event:')) {
        eventName = line.slice(6).trim();
      } else if (line.startsWith('data:')) {
        dataLines.push(line.slice(5).trimStart());
      }
    });
    if (dataLines.length === 0) return;

    const rawData = dataLines.join('\n');
    let parsedData = rawData;
    try {
      parsedData = JSON.parse(rawData);
    } catch {
      // Giữ nguyên chuỗi nếu server gửi dữ liệu SSE không phải JSON.
    }
    onEvent(eventName, parsedData);
  };

  while (true) {
    const { value, done } = await reader.read();
    buffer += decoder.decode(value || new Uint8Array(), { stream: !done });
    buffer = buffer.replace(/\r\n/g, '\n');

    let separatorIndex = buffer.indexOf('\n\n');
    while (separatorIndex >= 0) {
      const block = buffer.slice(0, separatorIndex);
      buffer = buffer.slice(separatorIndex + 2);
      if (block.trim()) processBlock(block);
      separatorIndex = buffer.indexOf('\n\n');
    }

    if (done) break;
  }

  if (buffer.trim()) processBlock(buffer);
};

export default {
  createConversation,
  getConversations,
  getMessages,
  updateConsent,
  deleteConversation,
  requestHandoff,
  sendHumanMessage,
  streamMessage,
};
