import React, { useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { Layout, Menu, Dropdown, Avatar } from 'antd';
import {
  DashboardOutlined,
  TeamOutlined,
  BarChartOutlined,
  SettingOutlined,
  UserOutlined,
  LogoutOutlined,
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
      label: '控制台',
    },
    {
      key: '/users',
      icon: <TeamOutlined />,
      label: '用户管理',
    },
    {
      key: '/statistics',
      icon: <BarChartOutlined />,
      label: '数据统计',
    },
    {
      key: '/system',
      icon: <SettingOutlined />,
      label: '系统配置',
    },
  ];

  const userMenuItems = [
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: '个人中心',
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
    },
  ];

  return (
    <Layout className="app-layout">
      <Sider collapsible collapsed={collapsed} onCollapse={setCollapsed}>
        <div className="logo">
          <h2>{collapsed ? 'CS' : '智能客服管理'}</h2>
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
            <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
              <div className="user-info">
                <Avatar icon={<UserOutlined />} />
                <span className="username">管理员</span>
              </div>
            </Dropdown>
          </div>
        </Header>
        <Content className="content">
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
};
