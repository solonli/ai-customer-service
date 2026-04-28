import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { ChatPage } from './pages/ChatPage';
import './App.css';

const App: React.FC = () => {
  return (
    <BrowserRouter>
      <div className="app">
        <Routes>
          <Route path="/" element={<ChatPage />} />
          <Route path="/chat/:conversationId" element={<ChatPage />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
};

export default App;
