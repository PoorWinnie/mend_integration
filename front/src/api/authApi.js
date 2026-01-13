// src/api/authApi.js
import axios from 'axios';

// 設定 API 基礎路徑 - 從環境變數或使用默認值
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

// 建立一個 axios 實例 - 注意不再附加/api
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 添加請求攔截器
apiClient.interceptors.request.use(
  (config) => {
    console.log('API 客戶端請求:', config.url, config.data);
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 添加響應攔截器
apiClient.interceptors.response.use(
  (response) => {
    console.log('API 客戶端響應:', response.status);
    return response;
  },
  (error) => {
    console.error('API 客戶端錯誤:', error.response?.status, error.response?.data);
    return Promise.reject(error);
  }
);

export default {
  // 使用者註冊 - 使用完整路徑
  register(userData) {
    // 調整資料格式
    const registerData = {
      username: userData.username,
      email: userData.email,
      password: userData.password,
      fullName: userData.fullName || userData.username,
      phone: userData.phone || null,
      role: userData.role === 'owner' ? 'CAMP_OWNER' : 'USER'
    };
    
    return apiClient.post('/api/auth/register', registerData);
  },
  
  // 使用者登入 - 使用完整路徑
  login(credentials) {
    return apiClient.post('/api/auth/login', credentials);
  },
  
  // 忘記密碼
  forgotPassword(email) {
    return apiClient.post('/api/auth/forgot-password', { email });
  },
  
  // 重設密碼
  resetPassword(token, newPassword) {
    return apiClient.post('/api/auth/reset-password', { token, password: newPassword });
  },
  
  // 登出
  logout(refreshToken) {
    return apiClient.post('/api/auth/logout', { refreshToken });
  },
  
  // 刷新 Token
  refreshToken(refreshToken) {
    return apiClient.post('/api/auth/refresh', { token: refreshToken });
  }
};