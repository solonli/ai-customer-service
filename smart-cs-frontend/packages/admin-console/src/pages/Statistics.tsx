import React from 'react';
import { Card, Row, Col } from 'antd';
import './Statistics.css';

export const Statistics: React.FC = () => {
  return (
    <div className="statistics">
      <h2>数据统计</h2>
      <Row gutter={16}>
        <Col span={12}>
          <Card title="会话趋势">
            <div className="chart-placeholder">
              <p>会话量趋势图表</p>
              <p className="chart-note">（集成 @ant-design/charts 后显示）</p>
            </div>
          </Card>
        </Col>
        <Col span={12}>
          <Card title="响应时间分布">
            <div className="chart-placeholder">
              <p>响应时间分布图表</p>
              <p className="chart-note">（集成 @ant-design/charts 后显示）</p>
            </div>
          </Card>
        </Col>
      </Row>
      <Row gutter={16} style={{ marginTop: 16 }}>
        <Col span={12}>
          <Card title="知识库使用统计">
            <div className="chart-placeholder">
              <p>知识库使用统计图表</p>
              <p className="chart-note">（集成 @ant-design/charts 后显示）</p>
            </div>
          </Card>
        </Col>
        <Col span={12}>
          <Card title="客服绩效统计">
            <div className="chart-placeholder">
              <p>客服绩效统计图表</p>
              <p className="chart-note">（集成 @ant-design/charts 后显示）</p>
            </div>
          </Card>
        </Col>
      </Row>
    </div>
  );
};
