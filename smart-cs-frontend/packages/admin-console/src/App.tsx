import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Layout } from './components/Layout';
import { Dashboard } from './pages/Dashboard';
import { UserManagement } from './pages/UserManagement';
import { SystemConfig } from './pages/SystemConfig';
import { Statistics } from './pages/Statistics';
import './App.css';

const App: React.FC = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<Navigate to="/dashboard" replace />} />
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="users" element={<UserManagement />} />
          <Route path="statistics" element={<Statistics />} />
          <Route path="system" element={<SystemConfig />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
};

export default App;
