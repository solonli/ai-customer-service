export interface User {
  id: string;
  username: string;
  nickname: string;
  avatar?: string;
  email?: string;
  phone?: string;
  role: 'user' | 'agent' | 'admin';
  status: 'online' | 'offline' | 'busy';
  createdAt: string;
  updatedAt: string;
}

export interface Message {
  id: string;
  conversationId: string;
  senderId: string;
  senderType: 'user' | 'agent' | 'bot';
  content: string;
  contentType: 'text' | 'image' | 'file' | 'audio' | 'video';
  status: 'sending' | 'sent' | 'delivered' | 'read' | 'failed';
  createdAt: string;
}

export interface Conversation {
  id: string;
  userId: string;
  agentId?: string;
  status: 'waiting' | 'active' | 'closed';
  priority: 'low' | 'normal' | 'high' | 'urgent';
  subject?: string;
  tags?: string[];
  createdAt: string;
  updatedAt: string;
  lastMessage?: Message;
}

export interface Knowledge {
  id: string;
  title: string;
  content: string;
  category: string;
  tags: string[];
  status: 'draft' | 'published' | 'archived';
  viewCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
  timestamp: number;
}

export interface PageResult<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
  pages: number;
}
