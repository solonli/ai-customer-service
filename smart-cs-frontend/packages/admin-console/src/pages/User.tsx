import React from 'react';
import { Card, Button, Table, Tag, Space, message } from 'antd';

const User: React.FC = () => {
  const columns = [
    {
      title: '用户编号',
      dataIndex: 'userNo',
      key: 'userNo',
    },
    {
      title: '昵称',
      dataIndex: 'nickName',
      key: 'nickName',
    },
    {
      title: '真实姓名',
      dataIndex: 'realName',
      key: 'realName',
    },
    {
      title: '类型',
      dataIndex: 'userType',
      key: 'userType',
      render: (type: number) => {
        const labels = ['终端用户', '客服', '管理员'];
        return <Tag color="blue">{labels[type - 1]}</Tag>;
      },
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: number) => {
        const colors = ['default', 'green'];
        const labels = ['禁用', '正常'];
        return <Tag color={colors[status]}>{labels[status]}</Tag>;
      },
    },
    {
      title: '最后登录',
      dataIndex: 'lastLoginTime',
      key: 'lastLoginTime',
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
    },
  ];

  const mockData = [
    {
      key: 1,
      userNo: 'U000001',
      nickName: '系统管理员',
      realName: '管理员',
      userType: 3,
      status: 1,
      lastLoginTime: '2026-04-28 10:30:00',
      createdAt: '2026-01-01 00:00:00',
    },
  ];

  return (
    <Card>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
        <h3>用户管理</h3>
      </div>

      <Table
        columns={columns}
        dataSource={mockData}
        pagination={{
          pageSize: 20,
        }}
      />
    </Card>
  );
};

export default User;
