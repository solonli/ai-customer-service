import React from 'react';
import { Card, Row, Col, Statistic, Progress } from 'antd';
import { UserOutlined, MessageOutlined, TeamOutlined, CheckCircleOutlined } from '@ant-design/icons';
import './Dashboard.css';

export const Dashboard: React.FC = () => {
  return (
    <div className="admin-dashboard">
      <h2>控制台</h2>
      <Row gutter={16}>
        <Col span={6}>
          <Card>
            <Statistic
              title="总用户数"
              value={11280}
              prefix={<UserOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="今日会话"
              value={2356}
              prefix={<MessageOutlined />}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="在线客服"
              value={42}
              prefix={<TeamOutlined />}
              valueStyle={{ color: '#faad14' }}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="解决率"
              value={94.5}
              suffix="%"
              prefix={<CheckCircleOutlined />}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
      </Row>
      <Row gutter={16} style={{ marginTop: 24 }}>
        <Col span={12}>
          <Card title="系统资源">
            <div className="resource-item">
              <span>CPU 使用率</span>
              <Progress percent={45} status="active" />
            </div>
            <div className="resource-item">
              <span>内存使用率</span>
              <Progress percent={68} status="active" />
            </div>
            <div className="resource-item">
              <span>磁盘使用率</span>
              <Progress percent={32} />
            </div>
          </Card>
        </Col>
        <Col span={12}>
          <Card title="服务状态">
            <div className="service-status">
              <div className="service-item">
                <span className="status-dot online"></span>
                <span>网关服务</span>
              </div>
              <div className="service-item">
                <span className="status-dot online"></span>
                <span>认证服务</span>
              </div>
              <div className="service-item">
                <span className="status-dot online"></span>
                <span>对话服务</span>
              </div>
              <div className="service-item">
                <span className="status-dot online"></span>
                <span>知识库服务</span>
              </div>
            </div>
          </Card>
        </Col>
      </Row>
    </div>
  );
};
