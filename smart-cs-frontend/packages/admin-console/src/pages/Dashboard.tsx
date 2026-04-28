import React from 'react';
import { Card, Row, Col, Statistic, Table, Tag, message } from 'antd';
import { 
  MessageOutlined, 
  FileTextOutlined, 
  UserOutlined, 
  LikeOutlined 
} from '@ant-design/icons';
import { useRequest } from 'ahooks';
import { ticketApi } from '../api';

const Dashboard: React.FC = () => {
  const { data: ticketData } = useRequest(() => ticketApi.list({ page: 1, size: 10 }));

  const stats = [
    {
      title: '今日会话',
      value: '1,234',
      icon: <MessageOutlined />,
      color: '#1890ff',
    },
    {
      title: '转人工率',
      value: '15.2%',
      icon: <FileTextOutlined />,
      color: '#52c41a',
    },
    {
      title: '工单数量',
      value: '89',
      icon: <FileTextOutlined />,
      color: '#faad14',
    },
    {
      title: '满意度',
      value: '4.8分',
      icon: <LikeOutlined />,
      color: '#f5222d',
    },
  ];

  const columns = [
    {
      title: '工单编号',
      dataIndex: 'ticketNo',
      key: 'ticketNo',
    },
    {
      title: '标题',
      dataIndex: 'title',
      key: 'title',
    },
    {
      title: '优先级',
      dataIndex: 'priority',
      key: 'priority',
      render: (priority: number) => {
        const colors = ['default', 'blue', 'orange', 'red'];
        const labels = ['低', '中', '高', '紧急'];
        return <Tag color={colors[priority - 1]}>{labels[priority - 1]}</Tag>;
      },
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: number) => {
        const colors = ['default', 'processing', 'warning', 'success', 'default'];
        const labels = ['待处理', '处理中', '待确认', '已解决', '已关闭'];
        return <Tag color={colors[status]}>{labels[status]}</Tag>;
      },
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
    },
  ];

  return (
    <div>
      <Row gutter={16} style={{ marginBottom: 24 }}>
        {stats.map((stat, index) => (
          <Col span={6} key={index}>
            <Card>
              <Statistic
                title={stat.title}
                value={stat.value}
                prefix={<span style={{ color: stat.color }}>{stat.icon}</span>}
              />
            </Card>
          </Col>
        ))}
      </Row>

      <Card title="最新工单" style={{ marginBottom: 24 }}>
        <Table
          columns={columns}
          dataSource={ticketData?.data?.records || []}
          rowKey="id"
          pagination={false}
        />
      </Card>
    </div>
  );
};

export default Dashboard;
