const createSocketUrl = () => {
  const configuredUrl = import.meta.env.VITE_CHAT_WS_URL?.trim();
  if (configuredUrl) return configuredUrl;
  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
  return `${protocol}//${window.location.host}/api/ws/chat`;
};

class ChatWebSocketClient {
  constructor({ onEvent, onConnectionChange }) {
    this.onEvent = onEvent;
    this.onConnectionChange = onConnectionChange;
    this.socket = null;
    this.authenticated = false;
    this.manuallyClosed = false;
    this.reconnectTimer = null;
    this.reconnectAttempts = 0;
    this.heartbeatTimer = null;
    this.subscriptions = new Set();
  }

  connect() {
    this.manuallyClosed = false;
    if (this.socket
      && [WebSocket.OPEN, WebSocket.CONNECTING].includes(this.socket.readyState)) return;

    const token = localStorage.getItem('token');
    if (!token) {
      this.onConnectionChange?.(false);
      return;
    }

    const socket = new WebSocket(createSocketUrl());
    this.socket = socket;

    socket.onopen = () => {
      this.reconnectAttempts = 0;
      this.sendRaw({ type: 'AUTH', token });
    };

    socket.onmessage = (event) => {
      let payload;
      try {
        payload = JSON.parse(event.data);
      } catch {
        return;
      }

      if (payload.type === 'AUTHENTICATED') {
        this.authenticated = true;
        this.onConnectionChange?.(true);
        this.subscriptions.forEach(conversationId => {
          this.sendRaw({ type: 'SUBSCRIBE', conversationId });
        });
        this.startHeartbeat();
      }
      this.onEvent?.(payload);
    };

    socket.onerror = () => {
      this.onConnectionChange?.(false);
    };

    socket.onclose = () => {
      if (this.socket === socket) this.socket = null;
      this.authenticated = false;
      this.stopHeartbeat();
      this.onConnectionChange?.(false);
      if (!this.manuallyClosed) this.scheduleReconnect();
    };
  }

  subscribe(conversationId) {
    if (!conversationId) return;
    this.subscriptions.add(Number(conversationId));
    if (this.authenticated) {
      this.sendRaw({ type: 'SUBSCRIBE', conversationId: Number(conversationId) });
    }
  }

  unsubscribe(conversationId) {
    if (!conversationId) return;
    this.subscriptions.delete(Number(conversationId));
    if (this.authenticated) {
      this.sendRaw({ type: 'UNSUBSCRIBE', conversationId: Number(conversationId) });
    }
  }

  sendMessage(conversationId, message) {
    if (!this.authenticated) {
      throw new Error('Kết nối chat thời gian thực đang được khôi phục.');
    }
    this.sendRaw({
      type: 'SEND_MESSAGE',
      conversationId: Number(conversationId),
      message,
    });
  }

  isConnected() {
    return Boolean(this.authenticated && this.socket?.readyState === WebSocket.OPEN);
  }

  disconnect() {
    this.manuallyClosed = true;
    this.authenticated = false;
    this.subscriptions.clear();
    this.stopHeartbeat();
    window.clearTimeout(this.reconnectTimer);
    this.reconnectTimer = null;
    this.socket?.close(1000, 'Client closed');
    this.socket = null;
    this.onConnectionChange?.(false);
  }

  sendRaw(payload) {
    if (this.socket?.readyState === WebSocket.OPEN) {
      this.socket.send(JSON.stringify(payload));
    }
  }

  scheduleReconnect() {
    window.clearTimeout(this.reconnectTimer);
    const delay = Math.min(10_000, 1_000 * (2 ** this.reconnectAttempts));
    this.reconnectAttempts += 1;
    this.reconnectTimer = window.setTimeout(() => this.connect(), delay);
  }

  startHeartbeat() {
    this.stopHeartbeat();
    this.heartbeatTimer = window.setInterval(() => {
      if (this.authenticated) this.sendRaw({ type: 'PING' });
    }, 25_000);
  }

  stopHeartbeat() {
    window.clearInterval(this.heartbeatTimer);
    this.heartbeatTimer = null;
  }
}

export const createChatWebSocketClient = callbacks => new ChatWebSocketClient(callbacks);

export default createChatWebSocketClient;
