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
  InputNumber,
  Popconfirm,
} from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import { useRequest } from 'ahooks';
import { knowledgeApi } from '../api';

const { TextArea } = Input;
const { Option } = Select;

const Knowledge: React.FC = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingItem, setEditingItem] = useState<any>(null);
  const [form] = Form.useForm();

  const { data, loading, refresh } = useRequest(() => knowledgeApi.list({ page: 1, size: 20 }));

  const columns = [
    {
      title: '标题',
      dataIndex: 'title',
      key: 'title',
    },
    {
      title: '分类',
      dataIndex: 'categoryName',
      key: 'categoryName',
    },
    {
      title: '关键词',
      dataIndex: 'keywords',
      key: 'keywords',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: number) => {
        const colors = ['default', 'green', 'red'];
        const labels = ['草稿', '已发布', '已下架'];
        return <Tag color={colors[status]}>{labels[status]}</Tag>;
      },
    },
    {
      title: '有效命中',
      dataIndex: 'effectiveCount',
      key: 'effectiveCount',
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
            icon={<EditOutlined />} 
            onClick={() => handlePublish(record.id)}
            disabled={record.status === 1}
          >
            发布
          </Button>
          <Popconfirm
            title="确认删除？"
            onConfirm={() => handleDelete(record.id)}
            okText="确认"
            cancelText="取消"
          >
            <Button 
              type="link" 
              danger 
              icon={<DeleteOutlined />}
            >
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const handlePublish = async (id: number) => {
    try {
      await knowledgeApi.publish(id);
      message.success('发布成功');
      refresh();
    } catch (error) {
      console.error(error);
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await knowledgeApi.delete(id);
      message.success('删除成功');
      refresh();
    } catch (error) {
      console.error(error);
    }
  };

  const handleAdd = () => {
    setEditingItem(null);
    form.resetFields();
    setIsModalOpen(true);
  };

  const handleSave = async (values: any) => {
    try {
      await knowledgeApi.create(values);
      message.success('创建成功');
      setIsModalOpen(false);
      refresh();
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <Card>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
        <h3>知识库管理</h3>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新增知识
        </Button>
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
        title={editingItem ? '编辑知识' : '新增知识'}
        open={isModalOpen}
        onCancel={() => setIsModalOpen(false)}
        onOk={() => form.submit()}
        width={700}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSave}
        >
          <Form.Item
            label="标题"
            name="title"
            rules={[{ required: true, message: '请输入标题' }]}
          >
            <Input placeholder="请输入标题" />
          </Form.Item>

          <Form.Item
            label="分类"
            name="categoryId"
            rules={[{ required: true, message: '请选择分类' }]}
          >
            <Select placeholder="请选择分类">
              <Option value={1}>常见问题</Option>
              <Option value={2}>产品咨询</Option>
              <Option value={3}>订单相关</Option>
              <Option value={4}>售后服务</Option>
            </Select>
          </Form.Item>

          <Form.Item
            label="内容"
            name="content"
            rules={[{ required: true, message: '请输入内容' }]}
          >
            <TextArea rows={6} placeholder="请输入内容" />
          </Form.Item>

          <Form.Item
            label="关键词"
            name="keywords"
            help="多个关键词用逗号分隔"
          >
            <Input placeholder="请输入关键词，多个用逗号分隔" />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  );
};

export default Knowledge;
