import React from 'react';
import { Card, Form, Input, Switch, Select, Button, Divider, message } from 'antd';
import './SystemConfig.css';

export const SystemConfig: React.FC = () => {
  const [form] = Form.useForm();

  const handleSave = () => {
    message.success('配置保存成功');
  };

  return (
    <div className="system-config">
      <h2>系统配置</h2>
      <Card title="基础配置">
        <Form form={form} layout="vertical" initialValues={{ enableBot: true, language: 'zh-CN' }}>
          <Form.Item name="systemName" label="系统名称">
            <Input placeholder="请输入系统名称" defaultValue="智能客服系统" />
          </Form.Item>
          <Form.Item name="welcomeMessage" label="欢迎语">
            <Input.TextArea
              placeholder="请输入欢迎语"
              defaultValue="您好！欢迎使用智能客服系统，请问有什么可以帮助您的？"
              rows={3}
            />
          </Form.Item>
          <Form.Item name="enableBot" label="启用智能机器人" valuePropName="checked">
            <Switch />
          </Form.Item>
          <Form.Item name="language" label="系统语言">
            <Select
              options={[
                { value: 'zh-CN', label: '简体中文' },
                { value: 'en-US', label: 'English' },
              ]}
            />
          </Form.Item>
        </Form>
      </Card>
      <Divider />
      <Card title="消息配置">
        <Form layout="vertical">
          <Form.Item label="消息保留天数">
            <Input type="number" defaultValue={30} style={{ width: 200 }} />
          </Form.Item>
          <Form.Item label="最大附件大小(MB)">
            <Input type="number" defaultValue={10} style={{ width: 200 }} />
          </Form.Item>
        </Form>
      </Card>
      <Divider />
      <div className="config-actions">
        <Button type="primary" onClick={handleSave}>
          保存配置
        </Button>
        <Button style={{ marginLeft: 8 }}>重置</Button>
      </div>
    </div>
  );
};
