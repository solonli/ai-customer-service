import React, { useState, useRef, useEffect } from 'react';
import { Input, Button, message, Avatar, Spin, Rate, Modal } from 'antd';
import { SendOutlined, RobotOutlined, UserOutlined, CustomerServiceOutlined } from '@ant-design/icons';
import { useChatStore } from '../../stores/chatStore';
import { useWebSocket } from '../../hooks/useWebSocket';
import MessageList from '../MessageList';
import './ChatWindow.css';

interface ChatWindowProps {
  botName?: string;
  welcomeMessage?: string;
}

const ChatWindow: React.FC<ChatWindowProps> = ({
  botName = '智能客服',
  welcomeMessage = '您好！我是智能客服小助手，请问有什么可以帮您？'
}) => {
  const [inputValue, setInputValue] = useState('');
  const [showSatisfaction, setShowSatisfaction] = useState(false);
  const [rating, setRating] = useState(0);
  const [feedback, setFeedback] = useState('');
  const messagesEndRef = useRef<HTMLDivElement>(null);
  
  const { currentSession, addMessage, updateSessionStatus } = useChatStore();
  
  const { status, send } = useWebSocket({
    url: 'ws://localhost:8080/ws/chat',
    token: 'mock-token',
    onMessage: (data) => {
      if (data.type === 'chat_reply') {
        addMessage(currentSession?.id || '', {
          id: data.data.message_id,
          sessionId: currentSession?.id || '',
          senderType: data.data.sender_type,
          content: data.data.content,
          messageType: data.data.message_type,
          createdAt: new Date().toISOString(),
        });
      } else if (data.type === 'session_transferred') {
        updateSessionStatus(currentSession?.id || '', 'transferred');
        message.info(`已转接人工客服 ${data.data.agent_name}，请稍候...`);
      }
    },
  });

  useEffect(() => {
    if (currentSession?.messages.length === 0) {
      addMessage(currentSession.id, {
        id: 'welcome',
        sessionId: currentSession.id,
        senderType: 'bot',
        content: welcomeMessage,
        messageType: 'text',
        createdAt: new Date().toISOString(),
      });
    }
  }, []);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [currentSession?.messages]);

  const handleSend = () => {
    if (!inputValue.trim()) return;
    
    const messageContent = inputValue.trim();
    setInputValue('');
    
    addMessage(currentSession?.id || '', {
      id: `msg-${Date.now()}`,
      sessionId: currentSession?.id || '',
      senderType: 'user',
      content: messageContent,
      messageType: 'text',
      createdAt: new Date().toISOString(),
    });
    
    send({
      type: 'chat_message',
      msg_id: `msg-${Date.now()}`,
      timestamp: Date.now(),
      data: {
        message_type: 'text',
        content: messageContent,
      },
    });
  };

  const handleTransfer = () => {
    send({
      type: 'transfer_request',
      msg_id: `transfer-${Date.now()}`,
      timestamp: Date.now(),
      data: { reason: '用户主动请求转人工' },
    });
  };

  const handleEndSession = () => {
    setShowSatisfaction(true);
  };

  const handleSubmitSatisfaction = () => {
    send({
      type: 'satisfaction',
      msg_id: `satisfaction-${Date.now()}`,
      timestamp: Date.now(),
      data: {
        session_id: currentSession?.id,
        score: rating,
        feedback,
      },
    });
    setShowSatisfaction(false);
    updateSessionStatus(currentSession?.id || '', 'closed');
    message.success('感谢您的评价！');
  };

  return (
    <div className="chat-window">
      <div className="chat-header">
        <div className="header-left">
          <RobotOutlined className="bot-icon" />
          <span className="bot-name">{botName}</span>
        </div>
        <div className="header-right">
          <Button type="link" onClick={handleTransfer}>
            <CustomerServiceOutlined /> 转人工
          </Button>
          <Button type="link" onClick={handleEndSession}>
            结束会话
          </Button>
        </div>
      </div>
      
      <div className="chat-body">
        {status === 'connecting' && (
          <div className="loading">
            <Spin tip="连接中..." />
          </div>
        )}
        <MessageList messages={currentSession?.messages || []} />
        <div ref={messagesEndRef} />
      </div>
      
      <div className="chat-footer">
        <Input
          placeholder="请输入消息..."
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
          onPressEnter={handleSend}
          className="message-input"
        />
        <Button type="primary" icon={<SendOutlined />} onClick={handleSend}>
          发送
        </Button>
      </div>
      
      <Modal
        title="服务评价"
        open={showSatisfaction}
        onOk={handleSubmitSatisfaction}
        onCancel={() => setShowSatisfaction(false)}
        okText="提交"
        cancelText="取消"
      >
        <div className="satisfaction-content">
          <p>请对我们的服务进行评价：</p>
          <Rate value={rating} onChange={setRating} />
          <Input.TextArea
            placeholder="请输入您的意见和建议..."
            value={feedback}
            onChange={(e) => setFeedback(e.target.value)}
            rows={4}
            style={{ marginTop: 16 }}
          />
        </div>
      </Modal>
    </div>
  );
};

export default ChatWindow;
