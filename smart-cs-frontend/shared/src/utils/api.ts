import axios from 'axios';

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

request.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default request;

export const api = {
  get: <T>(url: string, params?: Record<string, unknown>) =>
    request.get<T, T>(url, { params }),
  post: <T>(url: string, data?: unknown) =>
    request.post<T, T>(url, data),
  put: <T>(url: string, data?: unknown) =>
    request.put<T, T>(url, data),
  delete: <T>(url: string) =>
    request.delete<T, T>(url),
};
