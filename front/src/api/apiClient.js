// src/services/api/apiClient.js
import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://localhost:8080', // 修正：不再包含重複的 '/api'
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // 啟用憑證
});

// 請求攔截器 - 添加認證令牌
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 響應攔截器 - 處理常見錯誤
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    // 處理401錯誤 - 重定向到登入頁面
    if (error.response && error.response.status === 401) {
      // 清除任何存儲的認證信息
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      
      // 如果不是登入頁面，重定向到登入頁面
      if (!window.location.pathname.includes('/login')) {
        window.location.href = '/login';
      }
    }
    
    return Promise.reject(error);
  }
);

export default apiClient;