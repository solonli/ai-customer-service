import React, { useState } from 'react';
import { 
  Card, 
  Button, 
  Table, 
  Tag, 
  Space, 
  Modal, 
  Form, 
  Input, 
  Select, 
  message,
  Descriptions,
} from 'antd';
import { SearchOutlined } from '@ant-design/icons';
import { useRequest } from 'ahooks';
import { ticketApi } from '../api';

const { TextArea } = Input;
const { Option } = Select;

const Ticket: React.FC = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [viewModalOpen, setViewModalOpen] = useState(false);
  const [selectedTicket, setSelectedTicket] = useState<any>(null);
  const [statusFilter, setStatusFilter] = useState<number | undefined>(undefined);
  const [form] = Form.useForm();

  const { data, loading, refresh } = useRequest(() => 
    ticketApi.list({ page: 1, size: 20, status: statusFilter })
  );

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
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: any) => (
        <Space size="middle">
          <Button 
            type="link" 
            onClick={() => handleView(record)}
          >
            查看
          </Button>
          {record.status === 0 && (
            <Button 
              type="link" 
              onClick={() => handleAssign(record.id)}
            >
              分配
            </Button>
          )}
        </Space>
      ),
    },
  ];

  const handleView = (record: any) => {
    setSelectedTicket(record);
    setViewModalOpen(true);
  };

  const handleAssign = async (id: number) => {
    try {
      await ticketApi.assign(id, 1);
      message.success('分配成功');
      refresh();
    } catch (error) {
      console.error(error);
    }
  };

  const handleUpdateStatus = async (status: number) => {
    if (!selectedTicket) return;
    try {
      await ticketApi.updateStatus(selectedTicket.id, status);
      message.success('状态更新成功');
      setViewModalOpen(false);
      refresh();
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <Card>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
        <h3>工单管理</h3>
        <Space>
          <Select
            placeholder="状态筛选"
            allowClear
            style={{ width: 150 }}
            onChange={setStatusFilter}
          >
            <Option value={0}>待处理</Option>
            <Option value={1}>处理中</Option>
            <Option value={2}>待确认</Option>
            <Option value={3}>已解决</Option>
            <Option value={4}>已关闭</Option>
          </Select>
        </Space>
      </div>

      <Table
        columns={columns}
        dataSource={data?.data?.records || []}
        rowKey="id"
        loading={loading}
        pagination={{
          pageSize: 20,
        }}
      />

      <Modal
        title="工单详情"
        open={viewModalOpen}
        onCancel={() => setViewModalOpen(false)}
        onOk={() => setViewModalOpen(false)}
        width={600}
        footer={selectedTicket?.status === 1 ? [
          <Button key="solve" type="primary" onClick={() => handleUpdateStatus(3)}>
            标记已解决
          </Button>,
          <Button key="close" onClick={() => handleUpdateStatus(4)}>
            关闭工单
          </Button>,
        ] : undefined}
      >
        {selectedTicket && (
          <Descriptions bordered column={1}>
            <Descriptions.Item label="工单编号">{selectedTicket.ticketNo}</Descriptions.Item>
            <Descriptions.Item label="标题">{selectedTicket.title}</Descriptions.Item>
            <Descriptions.Item label="描述">{selectedTicket.description}</Descriptions.Item>
            <Descriptions.Item label="优先级">
              {selectedTicket.priority === 1 ? '低' : 
               selectedTicket.priority === 2 ? '中' : 
               selectedTicket.priority === 3 ? '高' : '紧急'}
            </Descriptions.Item>
            <Descriptions.Item label="状态">
              <Tag color={selectedTicket.status === 0 ? 'default' : 
                            selectedTicket.status === 1 ? 'processing' : 
                            selectedTicket.status === 2 ? 'warning' : 
                            selectedTicket.status === 3 ? 'success' : 'default'}>
                {selectedTicket.status === 0 ? '待处理' : 
                 selectedTicket.status === 1 ? '处理中' : 
                 selectedTicket.status === 2 ? '待确认' : 
                 selectedTicket.status === 3 ? '已解决' : '已关闭'}
              </Tag>
            </Descriptions.Item>
            <Descriptions.Item label="创建时间">{selectedTicket.createdAt}</Descriptions.Item>
          </Descriptions>
        )}
      </Modal>
    </Card>
  );
};

export default Ticket;
