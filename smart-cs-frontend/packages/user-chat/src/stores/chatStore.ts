import { create } from 'zustand';

export interface Message {
  id: string;
  sessionId: string;
  senderType: 'user' | 'bot' | 'agent' | 'system';
  content: string;
  messageType: 'text' | 'image' | 'card';
  createdAt: string;
}

export interface Session {
  id: string;
  status: 'active' | 'transferred' | 'closed';
  messages: Message[];
}

interface ChatState {
  currentSession: Session | null;
  sessions: Session[];
  addMessage: (sessionId: string, message: Message) => void;
  setCurrentSession: (session: Session) => void;
  updateSessionStatus: (sessionId: string, status: Session['status']) => void;
  createSession: () => string;
}

export const useChatStore = create<ChatState>((set, get) => ({
  currentSession: null,
  sessions: [],
  
  createSession: () => {
    const sessionId = `session-${Date.now()}`;
    const newSession: Session = {
      id: sessionId,
      status: 'active',
      messages: [],
    };
    set((state) => ({
      sessions: [...state.sessions, newSession],
      currentSession: newSession,
    }));
    return sessionId;
  },
  
  addMessage: (sessionId, message) => set((state) => {
    const sessions = state.sessions.map(s => 
      s.id === sessionId ? { ...s, messages: [...s.messages, message] } : s
    );
    const currentSession = state.currentSession?.id === sessionId
      ? { ...state.currentSession, messages: [...state.currentSession.messages, message] }
      : state.currentSession;
    return { sessions, currentSession };
  }),
  
  setCurrentSession: (session) => set({ currentSession: session }),
  
  updateSessionStatus: (sessionId, status) => set((state) => {
    const sessions = state.sessions.map(s =>
      s.id === sessionId ? { ...s, status } : s
    );
    const currentSession = state.currentSession?.id === sessionId
      ? { ...state.currentSession, status }
      : state.currentSession;
    return { sessions, currentSession };
  }),
}));
