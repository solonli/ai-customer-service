import React, { useState } from 'react';
import { Table, Button, Space, Modal, Form, Input, Select, Tag } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import './UserManagement.css';

interface UserItem {
  id: string;
  username: string;
  nickname: string;
  email: string;
  role: 'admin' | 'agent' | 'user';
  status: 'active' | 'inactive';
  createdAt: string;
}

const mockData: UserItem[] = [
  {
    id: '1',
    username: 'admin',
    nickname: '管理员',
    email: 'admin@example.com',
    role: 'admin',
    status: 'active',
    createdAt: '2024-01-01 00:00:00',
  },
  {
    id: '2',
    username: 'agent001',
    nickname: '客服小王',
    email: 'wang@example.com',
    role: 'agent',
    status: 'active',
    createdAt: '2024-01-05 10:30:00',
  },
];

const roleColors: Record<string, string> = {
  admin: 'red',
  agent: 'blue',
  user: 'default',
};

const roleText: Record<string, string> = {
  admin: '管理员',
  agent: '客服',
  user: '用户',
};

export const UserManagement: React.FC = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [form] = Form.useForm();

  const columns: ColumnsType<UserItem> = [
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username',
    },
    {
      title: '昵称',
      dataIndex: 'nickname',
      key: 'nickname',
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      key: 'email',
    },
    {
      title: '角色',
      dataIndex: 'role',
      key: 'role',
      render: (role: string) => <Tag color={roleColors[role]}>{roleText[role]}</Tag>,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => (
        <Tag color={status === 'active' ? 'green' : 'default'}>
          {status === 'active' ? '正常' : '禁用'}
        </Tag>
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
    },
    {
      title: '操作',
      key: 'action',
      render: () => (
        <Space>
          <Button type="link" size="small" icon={<EditOutlined />}>
            编辑
          </Button>
          <Button type="link" size="small" danger icon={<DeleteOutlined />}>
            删除
          </Button>
        </Space>
      ),
    },
  ];

  const handleAdd = () => {
    setIsModalOpen(true);
  };

  const handleOk = () => {
    form.validateFields().then(() => {
      setIsModalOpen(false);
      form.resetFields();
    });
  };

  const handleCancel = () => {
    setIsModalOpen(false);
    form.resetFields();
  };

  return (
    <div className="user-management">
      <div className="page-header">
        <h2>用户管理</h2>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          添加用户
        </Button>
      </div>
      <Table columns={columns} dataSource={mockData} rowKey="id" />
      <Modal
        title="添加用户"
        open={isModalOpen}
        onOk={handleOk}
        onCancel={handleCancel}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="username" label="用户名" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="nickname" label="昵称" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="email" label="邮箱" rules={[{ required: true, type: 'email' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="role" label="角色" rules={[{ required: true }]}>
            <Select
              options={[
                { value: 'admin', label: '管理员' },
                { value: 'agent', label: '客服' },
                { value: 'user', label: '用户' },
              ]}
            />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};
