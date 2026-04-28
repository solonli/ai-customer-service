import React from 'react';
import { Form, Input, Button, Card, Typography, message } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { authApi } from '../api';

const { Title } = Typography;

const LoginPage: React.FC = () => {
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const { login } = useAuth();

  const handleSubmit = async (values: any) => {
    try {
      const result = await authApi.login(values);
      
      localStorage.setItem('accessToken', result.data.accessToken);
      login(result.data.accessToken, result.data.userInfo);
      
      message.success('登录成功');
      navigate('/dashboard');
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <div style={{ 
      display: 'flex', 
      justifyContent: 'center', 
      alignItems: 'center', 
      minHeight: '100vh',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
    }}>
      <Card style={{ width: 400, borderRadius: 8 }}>
        <div style={{ textAlign: 'center', marginBottom: 24 }}>
          <Title level={3} style={{ marginBottom: 8 }}>智能客服管理后台</Title>
          <span style={{ color: '#999' }}>请登录您的账户</span>
        </div>
        
        <Form
          form={form}
          onFinish={handleSubmit}
          size="large"
        >
          <Form.Item
            name="username"
            rules={[{ required: true, message: '请输入用户名' }]}
          >
            <Input 
              prefix={<UserOutlined />} 
              placeholder="请输入用户名/手机号" 
            />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[{ required: true, message: '请输入密码' }]}
          >
            <Input.Password 
              prefix={<LockOutlined />} 
              placeholder="请输入密码" 
            />
          </Form.Item>

          <Form.Item>
            <Button 
              type="primary" 
              htmlType="submit" 
              block 
              size="large"
            >
              登录
            </Button>
          </Form.Item>
        </Form>
        
        <div style={{ textAlign: 'center', color: '#999', fontSize: 12 }}>
          测试账号：任意输入，直接登录
        </div>
      </Card>
    </div>
  );
};

export default LoginPage;
