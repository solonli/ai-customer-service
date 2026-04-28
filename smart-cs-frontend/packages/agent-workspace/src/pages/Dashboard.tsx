import React from 'react';
import { Card, Row, Col, Statistic } from 'antd';
import { MessageOutlined, TeamOutlined, ClockCircleOutlined, CheckCircleOutlined } from '@ant-design/icons';
import './Dashboard.css';

export const Dashboard: React.FC = () => {
  return (
    <div className="dashboard">
      <h2>工作台</h2>
      <Row gutter={16}>
        <Col span={6}>
          <Card>
            <Statistic
              title="待处理会话"
              value={12}
              prefix={<MessageOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="进行中会话"
              value={8}
              prefix={<TeamOutlined />}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="平均响应时间"
              value={45}
              suffix="秒"
              prefix={<ClockCircleOutlined />}
              valueStyle={{ color: '#faad14' }}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="今日完成"
              value={156}
              prefix={<CheckCircleOutlined />}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
};
