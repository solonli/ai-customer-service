import React from 'react';
import { Layout as AntLayout, Menu, Avatar, Dropdown, Typography } from 'antd';
import {
  DashboardOutlined,
  BookOutlined,
  FileTextOutlined,
  UserOutlined,
  SettingOutlined,
  LogoutOutlined,
} from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import logo from '../assets/logo.svg';

const { Header, Sider, Content } = AntLayout;
const { Title } = Typography;

interface LayoutProps {
  children: React.ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useAuth();

  const menuItems = [
    {
      key: '/dashboard',
      icon: <DashboardOutlined />,
      label: '数据概览',
    },
    {
      key: '/knowledge',
      icon: <BookOutlined />,
      label: '知识库管理',
    },
    {
      key: '/ticket',
      icon: <FileTextOutlined />,
      label: '工单管理',
    },
    {
      key: '/user',
      icon: <UserOutlined />,
      label: '用户管理',
    },
  ];

  const dropdownItems = [
    {
      key: 'profile',
      label: '个人设置',
      icon: <SettingOutlined />,
    },
    {
      type: 'divider',
    },
    {
      key: 'logout',
      label: '退出登录',
      icon: <LogoutOutlined />,
      danger: true,
      onClick: () => {
        logout();
        localStorage.removeItem('accessToken');
        navigate('/login');
      },
    },
  ];

  return (
    <AntLayout style={{ minHeight: '100vh' }}>
      <Sider
        theme="dark"
        width={220}
        style={{
          overflow: 'auto',
          height: '100vh',
          position: 'fixed',
          left: 0,
          top: 0,
          bottom: 0,
        }}
      >
        <div style={{ 
          display: 'flex', 
          alignItems: 'center', 
          justifyContent: 'center',
          height: 64,
          borderBottom: '1px solid rgba(255, 255, 255, 0.1)',
        }}>
          <div style={{ 
            display: 'flex', 
            alignItems: 'center', 
            gap: 8, 
            color: '#fff' 
          }}>
            <Title level={4} style={{ margin: 0, color: '#fff' }}>智能客服</Title>
          </div>
        </div>
        
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname]}
          onClick={({ key }) => navigate(key)}
          items={menuItems}
          style={{ marginTop: 16 }}
        />
      </Sider>

      <AntLayout style={{ marginLeft: 220 }}>
        <Header style={{ 
          background: '#fff', 
          padding: '0 24px', 
          display: 'flex', 
          justifyContent: 'space-between', 
          alignItems: 'center',
          boxShadow: '0 1px 4px rgba(0,21,41,.08)',
        }}>
          <div />
          
          <Dropdown menu={{ items: dropdownItems }} placement="bottomRight">
            <div style={{ 
              display: 'flex', 
              alignItems: 'center', 
              cursor: 'pointer', 
              gap: 12 
            }}>
              <Avatar 
                src={user?.avatarUrl} 
                icon={!user?.avatarUrl && <UserOutlined />}
              />
              <span>{user?.nickName}</span>
            </div>
          </Dropdown>
        </Header>

        <Content style={{ 
          background: '#f0f2f5', 
          minHeight: 280, 
          padding: 24,
        }}>
          {children}
        </Content>
      </AntLayout>
    </AntLayout>
  );
};

export default Layout;
