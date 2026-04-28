import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './hooks/useAuth';
import LoginPage from './pages/Login';
import Layout from './layouts/MainLayout';
import Dashboard from './pages/Dashboard';
import Knowledge from './pages/Knowledge';
import Ticket from './pages/Ticket';
import User from './pages/User';

const ProtectedRoute = ({ children }: { children: React.ReactNode }) => {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" />;
};

const AppContent = () => {
  const { isAuthenticated } = useAuth();
  
  if (!isAuthenticated) {
    return <LoginPage />;
  }
  
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/knowledge" element={<Knowledge />} />
        <Route path="/ticket" element={<Ticket />} />
        <Route path="/user" element={<User />} />
        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </Layout>
  );
};

const App = () => {
  return (
    <AuthProvider>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="*" element={<AppContent />} />
      </Routes>
    </AuthProvider>
  );
};

export default App;
