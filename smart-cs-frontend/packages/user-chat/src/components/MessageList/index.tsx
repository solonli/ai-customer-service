import React from 'react';
import { Avatar, Typography } from 'antd';
import { RobotOutlined, UserOutlined, CustomerServiceOutlined } from '@ant-design/icons';
import type { Message } from '../../stores/chatStore';
import './MessageList.css';

const { Text } = Typography;

interface MessageListProps {
  messages: Message[];
}

const MessageList: React.FC<MessageListProps> = ({ messages }) => {
  const getSenderIcon = (senderType: Message['senderType']) => {
    switch (senderType) {
      case 'bot':
        return <RobotOutlined />;
      case 'agent':
        return <CustomerServiceOutlined />;
      default:
        return <UserOutlined />;
    }
  };

  const getSenderName = (senderType: Message['senderType']) => {
    switch (senderType) {
      case 'bot':
        return '智能客服';
      case 'agent':
        return '人工客服';
      default:
        return '我';
    }
  };

  const formatTime = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleTimeString('zh-CN', { 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  };

  return (
    <div className="message-list">
      {messages.map((message) => (
        <div 
          key={message.id} 
          className={`message-item ${message.senderType === 'user' ? 'user-message' : 'bot-message'}`}
        >
          {message.senderType !== 'user' && (
            <Avatar 
              className="message-avatar" 
              icon={getSenderIcon(message.senderType)}
              style={{ backgroundColor: message.senderType === 'bot' ? '#1890ff' : '#52c41a' }}
            />
          )}
          <div className="message-content">
            <div className="message-header">
              <Text type="secondary" className="sender-name">
                {getSenderName(message.senderType)}
              </Text>
              <Text type="secondary" className="message-time">
                {formatTime(message.createdAt)}
              </Text>
            </div>
            <div className="message-body">
              {message.messageType === 'text' && (
                <div className="text-message">{message.content}</div>
              )}
              {message.messageType === 'image' && (
                <img src={message.content} alt="message" className="image-message" />
              )}
            </div>
          </div>
          {message.senderType === 'user' && (
            <Avatar 
              className="message-avatar" 
              icon={<UserOutlined />}
              style={{ backgroundColor: '#87d068' }}
            />
          )}
        </div>
      ))}
    </div>
  );
};

export default MessageList;
