import React, { useState, useRef, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { Button, Input } from '@smart-cs/shared';
import { useWebSocket } from '@smart-cs/shared';
import { Message } from '@smart-cs/shared';
import './ChatPage.css';

export const ChatPage: React.FC = () => {
  const { conversationId } = useParams();
  const [messages, setMessages] = useState<Message[]>([]);
  const [inputValue, setInputValue] = useState('');
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const { readyState, send } = useWebSocket(
    `ws://${window.location.hostname}:8101/ws/chat`,
    {
      onMessage: (event) => {
        const message = JSON.parse(event.data) as Message;
        setMessages((prev) => [...prev, message]);
      },
    }
  );

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSend = () => {
    if (!inputValue.trim()) return;

    const message: Message = {
      id: Date.now().toString(),
      conversationId: conversationId || 'default',
      senderId: 'user',
      senderType: 'user',
      content: inputValue,
      contentType: 'text',
      status: 'sending',
      createdAt: new Date().toISOString(),
    };

    send(JSON.stringify(message));
    setMessages((prev) => [...prev, message]);
    setInputValue('');
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  return (
    <div className="chat-page">
      <header className="chat-header">
        <h1>智能客服</h1>
        <span className="status">
          {readyState === WebSocket.OPEN ? '🟢 在线' : '🔴 离线'}
        </span>
      </header>

      <main className="chat-messages">
        {messages.length === 0 ? (
          <div className="empty-state">
            <p>👋 您好！请问有什么可以帮助您的？</p>
          </div>
        ) : (
          messages.map((msg) => (
            <div
              key={msg.id}
              className={`message message-${msg.senderType}`}
            >
              <div className="message-content">{msg.content}</div>
              <div className="message-time">
                {new Date(msg.createdAt).toLocaleTimeString()}
              </div>
            </div>
          ))
        )}
        <div ref={messagesEndRef} />
      </main>

      <footer className="chat-input">
        <Input
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
          onKeyPress={handleKeyPress}
          placeholder="请输入您的问题..."
        />
        <Button onClick={handleSend} disabled={!inputValue.trim()}>
          发送
        </Button>
      </footer>
    </div>
  );
};
