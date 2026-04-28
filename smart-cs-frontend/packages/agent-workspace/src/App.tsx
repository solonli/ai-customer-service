import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Layout } from './components/Layout';
import { Dashboard } from './pages/Dashboard';
import { ConversationList } from './pages/ConversationList';
import { KnowledgeBase } from './pages/KnowledgeBase';
import './App.css';

const App: React.FC = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<Navigate to="/dashboard" replace />} />
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="conversations" element={<ConversationList />} />
          <Route path="knowledge" element={<KnowledgeBase />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
};

export default App;
