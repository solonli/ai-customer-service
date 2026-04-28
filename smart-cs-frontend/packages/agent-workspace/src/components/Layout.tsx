import React, { useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { Layout, Menu } from 'antd';
import {
  DashboardOutlined,
  MessageOutlined,
  BookOutlined,
  UserOutlined,
} from '@ant-design/icons';

const { Header, Sider, Content } = Layout;

export const Layout: React.FC = () => {
  const [collapsed, setCollapsed] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  const menuItems = [
    {
      key: '/dashboard',
      icon: <DashboardOutlined />,
      label: '工作台',
    },
    {
      key: '/conversations',
      icon: <MessageOutlined />,
      label: '会话管理',
    },
    {
      key: '/knowledge',
      icon: <BookOutlined />,
      label: '知识库',
    },
  ];

  return (
    <Layout className="app-layout">
      <Sider collapsible collapsed={collapsed} onCollapse={setCollapsed}>
        <div className="logo">
          <h2>{collapsed ? 'CS' : '智能客服'}</h2>
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
        />
      </Sider>
      <Layout>
        <Header className="header">
          <div className="header-right">
            <UserOutlined style={{ marginRight: 8 }} />
            <span>客服人员</span>
          </div>
        </Header>
        <Content className="content">
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
};
