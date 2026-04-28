import { create } from 'zustand';
import { createContext, useContext } from 'react';

interface User {
  userId: number;
  userNo: string;
  nickName: string;
  avatarUrl: string;
  userType: number;
}

interface AuthState {
  isAuthenticated: boolean;
  user: User | null;
  accessToken: string | null;
  login: (token: string, user: User) => void;
  logout: () => void;
}

const useAuthStore = create<AuthState>((set) => ({
  isAuthenticated: false,
  user: null,
  accessToken: null,
  
  login: (token, user) => set({
    isAuthenticated: true,
    accessToken: token,
    user,
  }),
  
  logout: () => set({
    isAuthenticated: false,
    accessToken: null,
    user: null,
  }),
}));

interface AuthContextType {
  isAuthenticated: boolean;
  user: User | null;
  accessToken: string | null;
  login: (token: string, user: User) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const auth = useAuthStore();
  return <AuthContext.Provider value={auth}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
