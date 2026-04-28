import React from 'react';
import { Input, Button, Card, List, Tag } from 'antd';
import { SearchOutlined, PlusOutlined } from '@ant-design/icons';
import './KnowledgeBase.css';

const mockKnowledge = [
  {
    id: '1',
    title: '如何修改收货地址？',
    category: '订单问题',
    tags: ['订单', '地址'],
    viewCount: 1234,
  },
  {
    id: '2',
    title: '退换货流程说明',
    category: '售后服务',
    tags: ['退换货', '流程'],
    viewCount: 2345,
  },
];

export const KnowledgeBase: React.FC = () => {
  return (
    <div className="knowledge-base">
      <div className="knowledge-header">
        <h2>知识库</h2>
        <Button type="primary" icon={<PlusOutlined />}>
          新增知识
        </Button>
      </div>
      <div className="knowledge-search">
        <Input
          placeholder="搜索知识..."
          prefix={<SearchOutlined />}
          style={{ width: 300 }}
        />
      </div>
      <List
        grid={{ gutter: 16, column: 2 }}
        dataSource={mockKnowledge}
        renderItem={(item) => (
          <List.Item>
            <Card title={item.title} hoverable>
              <div className="knowledge-meta">
                <span className="category">{item.category}</span>
                <div className="tags">
                  {item.tags.map((tag) => (
                    <Tag key={tag}>{tag}</Tag>
                  ))}
                </div>
                <span className="view-count">浏览: {item.viewCount}</span>
              </div>
            </Card>
          </List.Item>
        )}
      />
    </div>
  );
};
