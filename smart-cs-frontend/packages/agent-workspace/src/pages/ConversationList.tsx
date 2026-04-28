import React from 'react';
import { Table, Tag, Button, Space } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import './ConversationList.css';

interface ConversationItem {
  id: string;
  userName: string;
  status: 'waiting' | 'active' | 'closed';
  priority: 'low' | 'normal' | 'high' | 'urgent';
  lastMessage: string;
  createdAt: string;
}

const mockData: ConversationItem[] = [
  {
    id: '1',
    userName: '张三',
    status: 'waiting',
    priority: 'high',
    lastMessage: '请问什么时候发货？',
    createdAt: '2024-01-15 10:30:00',
  },
  {
    id: '2',
    userName: '李四',
    status: 'active',
    priority: 'normal',
    lastMessage: '好的，谢谢',
    createdAt: '2024-01-15 10:25:00',
  },
];

const statusColors: Record<string, string> = {
  waiting: 'orange',
  active: 'green',
  closed: 'default',
};

const priorityColors: Record<string, string> = {
  low: 'default',
  normal: 'blue',
  high: 'orange',
  urgent: 'red',
};

const priorityText: Record<string, string> = {
  low: '低',
  normal: '普通',
  high: '高',
  urgent: '紧急',
};

export const ConversationList: React.FC = () => {
  const columns: ColumnsType<ConversationItem> = [
    {
      title: '会话ID',
      dataIndex: 'id',
      key: 'id',
      width: 100,
    },
    {
      title: '用户',
      dataIndex: 'userName',
      key: 'userName',
      width: 120,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: string) => (
        <Tag color={statusColors[status]}>
          {status === 'waiting' ? '等待中' : status === 'active' ? '进行中' : '已关闭'}
        </Tag>
      ),
    },
    {
      title: '优先级',
      dataIndex: 'priority',
      key: 'priority',
      width: 100,
      render: (priority: string) => (
        <Tag color={priorityColors[priority]}>{priorityText[priority]}</Tag>
      ),
    },
    {
      title: '最后消息',
      dataIndex: 'lastMessage',
      key: 'lastMessage',
      ellipsis: true,
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 180,
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: () => (
        <Space>
          <Button type="link" size="small">
            接入
          </Button>
          <Button type="link" size="small">
            查看
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <div className="conversation-list">
      <h2>会话管理</h2>
      <Table columns={columns} dataSource={mockData} rowKey="id" />
    </div>
  );
};
