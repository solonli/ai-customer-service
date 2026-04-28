import React, { useState, useEffect, useRef } from 'react';
import { useParams } from 'react-router-dom';
import { Card, Input, Button, Avatar, Tag, Space, Spin, Empty, message } from 'antd';
import { SendOutlined, UserOutlined, RobotOutlined, TeamOutlined } from '@ant-design/icons';
import './ConversationDetail.css';

interface Message {
  id: string;
  senderType: 'user' | 'bot' | 'agent';
  senderName: string;
  content: string;
  timestamp: string;
}

interface SessionInfo {
  id: string;
  sessionNo: string;
  userId: string;
  userName: string;
  status: number;
  channel: string;
  createdAt: string;
}

export const ConversationDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [messages, setMessages] = useState<Message[]>([]);
  const [inputValue, setInputValue] = useState('');
  const [loading, setLoading] = useState(true);
  const [sessionInfo, setSessionInfo] = useState<SessionInfo | null>(null);
  const [wsConnected, setWsConnected] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const wsRef = useRef<WebSocket | null>(null);

  useEffect(() => {
    fetchSessionInfo();
    fetchMessages();
    connectWebSocket();

    return () => {
      if (wsRef.current) {
        wsRef.current.close();
      }
    };
  }, [id]);

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const fetchSessionInfo = async () => {
    try {
      const response = await fetch(`/api/v1/sessions/${id}`);
      if (response.ok) {
        const data = await response.json();
        setSessionInfo(data.data);
      }
    } catch (error) {
      console.error('获取会话信息失败', error);
    }
  };

  const fetchMessages = async () => {
    try {
      setLoading(true);
      const response = await fetch(`/api/v1/sessions/${id}/messages`);
      if (response.ok) {
        const data = await response.json();
        setMessages(data.data || []);
      }
    } catch (error) {
      console.error('获取消息列表失败', error);
    } finally {
      setLoading(false);
    }
  };

  const connectWebSocket = () => {
    const token = localStorage.getItem('token');
    const wsUrl = `ws://localhost:8081/ws/session/${id}?token=${token}`;
    
    wsRef.current = new WebSocket(wsUrl);
    
    wsRef.current.onopen = () => {
      setWsConnected(true);
      console.log('WebSocket连接成功');
    };
    
    wsRef.current.onmessage = (event) => {
      const data = JSON.parse(event.data);
      if (data.type === 'message') {
        setMessages(prev => [...prev, data.payload]);
      }
    };
    
    wsRef.current.onclose = () => {
      setWsConnected(false);
      console.log('WebSocket连接关闭');
    };
    
    wsRef.current.onerror = (error) => {
      console.error('WebSocket错误', error);
      setWsConnected(false);
    };
  };

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  const sendMessage = () => {
    if (!inputValue.trim()) return;
    
    const message: Message = {
      id: Date.now().toString(),
      senderType: 'agent',
      senderName: '客服',
      content: inputValue,
      timestamp: new Date().toISOString(),
    };
    
    if (wsRef.current && wsConnected) {
      wsRef.current.send(JSON.stringify({
        type: 'message',
        content: inputValue,
      }));
    } else {
      setMessages(prev => [...prev, message]);
    }
    
    setInputValue('');
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      sendMessage();
    }
  };

  const renderMessage = (msg: Message) => {
    const isUser = msg.senderType === 'user';
    const isAgent = msg.senderType === 'agent';
    
    return (
      <div key={msg.id} className={`message-item ${isUser ? 'message-user' : 'message-other'}`}>
        <Avatar 
          icon={isUser ? <UserOutlined /> : isAgent ? <TeamOutlined /> : <RobotOutlined />}
          className="message-avatar"
        />
        <div className="message-content">
          <div className="message-header">
            <span className="message-sender">{msg.senderName}</span>
            <span className="message-time">
              {new Date(msg.timestamp).toLocaleString()}
            </span>
          </div>
          <div className="message-bubble">{msg.content}</div>
        </div>
      </div>
    );
  };

  if (loading) {
    return (
      <div className="conversation-detail loading">
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div className="conversation-detail">
      <div className="conversation-header">
        <div className="session-info">
          <h3>会话: {sessionInfo?.sessionNo || id}</h3>
          <Space>
            <Tag color={sessionInfo?.status === 1 ? 'green' : 'default'}>
              {sessionInfo?.status === 1 ? '进行中' : '已结束'}
            </Tag>
            <Tag>{sessionInfo?.channel || 'Web'}</Tag>
            <span className="ws-status">
              {wsConnected ? '🟢 已连接' : '🔴 未连接'}
            </span>
          </Space>
        </div>
        <div className="user-info">
          <span>用户: {sessionInfo?.userName || '访客'}</span>
        </div>
      </div>
      
      <div className="message-list">
        {messages.length === 0 ? (
          <Empty description="暂无消息" />
        ) : (
          messages.map(renderMessage)
        )}
        <div ref={messagesEndRef} />
      </div>
      
      <div className="message-input">
        <Input.TextArea
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
          onKeyPress={handleKeyPress}
          placeholder="输入消息..."
          autoSize={{ minRows: 2, maxRows: 4 }}
        />
        <Button 
          type="primary" 
          icon={<SendOutlined />}
          onClick={sendMessage}
          disabled={!inputValue.trim()}
        >
          发送
        </Button>
      </div>
    </div>
  );
};
