import axios from 'axios';
import { message } from 'antd';
import { useAuth } from './useAuth';

const api = axios.create({
  baseURL: '/api/v1',
  timeout: 15000,
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => {
    if (response.data.code === 200) {
      return response.data;
    }
    message.error(response.data.message || '请求失败');
    return Promise.reject(new Error(response.data.message));
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('accessToken');
      window.location.href = '/login';
    }
    message.error(error.message || '网络错误');
    return Promise.reject(error);
  },
);

export const authApi = {
  login: (data: { username: string; password: string }) =>
    api.post('/auth/login', data),
  logout: () => api.post('/auth/logout'),
};

export const knowledgeApi = {
  list: (params: any) => api.get('/knowledge', { params }),
  detail: (id: number) => api.get(`/knowledge/${id}`),
  create: (data: any) => api.post('/knowledge', data),
  publish: (id: number) => api.post(`/knowledge/${id}/publish`),
  delete: (id: number) => api.delete(`/knowledge/${id}`),
  search: (data: any) => api.post('/knowledge/search', data),
};

export const ticketApi = {
  list: (params: any) => api.get('/tickets', { params }),
  detail: (id: number) => api.get(`/tickets/${id}`),
  create: (data: any) => api.post('/tickets', data),
  assign: (id: number, handlerId: number) => api.post(`/tickets/${id}/assign`, null, { params: { handlerId } }),
  updateStatus: (id: number, status: number) => api.post(`/tickets/${id}/status`, null, { params: { status } }),
  delete: (id: number) => api.delete(`/tickets/${id}`),
};

export default api;
