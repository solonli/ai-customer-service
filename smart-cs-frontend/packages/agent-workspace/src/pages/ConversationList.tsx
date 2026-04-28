import React, { useState, useEffect } from 'react';
import { Table, Tag, Button, Space, Input, Select, Card, Badge } from 'antd';
import { useNavigate } from 'react-router-dom';
import type { ColumnsType } from 'antd/es/table';
import './ConversationList.css';

const { Search } = Input;
const { Option } = Select;

interface ConversationItem {
  id: string;
  sessionNo: string;
  userName: string;
  status: 'waiting' | 'active' | 'closed';
  priority: 'low' | 'normal' | 'high' | 'urgent';
  lastMessage: string;
  channel: string;
  createdAt: string;
  lastMessageTime: string;
  unreadCount: number;
}

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
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<ConversationItem[]>([]);
  const [statusFilter, setStatusFilter] = useState<string>('all');

  useEffect(() => {
    fetchConversations();
  }, [statusFilter]);

  const fetchConversations = async () => {
    setLoading(true);
    try {
      const response = await fetch(`/api/v1/sessions?status=${statusFilter === 'all' ? '' : statusFilter}`);
      if (response.ok) {
        const result = await response.json();
        setData(result.data || []);
      } else {
        setData(getMockData());
      }
    } catch (error) {
      console.error('获取会话列表失败', error);
      setData(getMockData());
    } finally {
      setLoading(false);
    }
  };

  const getMockData = (): ConversationItem[] => [
    {
      id: '1',
      sessionNo: 'S20240115001',
      userName: '张三',
      status: 'waiting',
      priority: 'high',
      lastMessage: '请问什么时候发货？',
      channel: 'Web',
      createdAt: '2024-01-15 10:30:00',
      lastMessageTime: '2024-01-15 10:35:00',
      unreadCount: 3,
    },
    {
      id: '2',
      sessionNo: 'S20240115002',
      userName: '李四',
      status: 'active',
      priority: 'normal',
      lastMessage: '好的，谢谢',
      channel: 'Web',
      createdAt: '2024-01-15 10:25:00',
      lastMessageTime: '2024-01-15 10:28:00',
      unreadCount: 0,
    },
    {
      id: '3',
      sessionNo: 'S20240115003',
      userName: '王五',
      status: 'waiting',
      priority: 'urgent',
      lastMessage: '我要投诉！',
      channel: 'App',
      createdAt: '2024-01-15 10:20:00',
      lastMessageTime: '2024-01-15 10:22:00',
      unreadCount: 5,
    },
  ];

  const handleJoin = (id: string) => {
    navigate(`/conversations/${id}`);
  };

  const handleView = (id: string) => {
    navigate(`/conversations/${id}`);
  };

  const columns: ColumnsType<ConversationItem> = [
    {
      title: '会话编号',
      dataIndex: 'sessionNo',
      key: 'sessionNo',
      width: 140,
    },
    {
      title: '用户',
      dataIndex: 'userName',
      key: 'userName',
      width: 100,
      render: (text: string, record) => (
        <Space>
          <span>{text}</span>
          {record.unreadCount > 0 && (
            <Badge count={record.unreadCount} size="small" />
          )}
        </Space>
      ),
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
      width: 80,
      render: (priority: string) => (
        <Tag color={priorityColors[priority]}>{priorityText[priority]}</Tag>
      ),
    },
    {
      title: '渠道',
      dataIndex: 'channel',
      key: 'channel',
      width: 80,
    },
    {
      title: '最后消息',
      dataIndex: 'lastMessage',
      key: 'lastMessage',
      ellipsis: true,
    },
    {
      title: '最后消息时间',
      dataIndex: 'lastMessageTime',
      key: 'lastMessageTime',
      width: 160,
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_, record) => (
        <Space>
          {record.status === 'waiting' && (
            <Button type="primary" size="small" onClick={() => handleJoin(record.id)}>
              接入
            </Button>
          )}
          <Button type="link" size="small" onClick={() => handleView(record.id)}>
            查看
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <div className="conversation-list">
      <Card>
        <div className="conversation-toolbar">
          <Space>
            <Select
              value={statusFilter}
              onChange={setStatusFilter}
              style={{ width: 120 }}
            >
              <Option value="all">全部状态</Option>
              <Option value="waiting">等待中</Option>
              <Option value="active">进行中</Option>
              <Option value="closed">已关闭</Option>
            </Select>
            <Search
              placeholder="搜索会话"
              style={{ width: 200 }}
              onSearch={(value) => console.log('搜索:', value)}
            />
          </Space>
        </div>
        
        <Table 
          columns={columns} 
          dataSource={data} 
          rowKey="id"
          loading={loading}
          pagination={{
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total) => `共 ${total} 条`,
          }}
        />
      </Card>
    </div>
  );
};
