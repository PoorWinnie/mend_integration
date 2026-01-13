// src/api/userApi.js
import axios from 'axios';

// 使用統一的API基礎URL
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

// 創建 axios 實例，設定基本請求路徑
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  }
});

// 請求攔截器 - 添加認證 token
apiClient.interceptors.request.use(
  (config) => {
    // 修改為使用accessToken，與authService一致
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 用戶 API 方法
export default {
  // 獲取所有用戶
  getAllUsers(params = {}) {
    return apiClient.get('/api/users', { params });
  },
  
  // 根據 ID 獲取單個用戶
  getUserById(id) {
    return apiClient.get(`/api/users/${id}`);
  },
  
  // 創建新用戶
  createUser(userData) {
    return apiClient.post('/api/users', userData);
  },
  
  // 更新用戶資料
  updateUser(id, userData) {
    return apiClient.put(`/api/users/${id}`, userData);
  },
  
  // 刪除用戶
  deleteUser(id) {
    return apiClient.delete(`/api/users/${id}`);
  },
  
  // 變更用戶狀態 (啟用/停用)
  changeUserStatus(id, status) {
    return apiClient.patch(`/api/users/${id}/status`, { status });
  },
  
  // 獲取用戶登錄歷史
  getUserLoginHistory(id, params = {}) {
    return apiClient.get(`/api/users/${id}/login-history`, { params });
  },
  
  // 獲取用戶操作日誌
  getUserActionLogs(id, params = {}) {
    return apiClient.get(`/api/users/${id}/action-logs`, { params });
  },
  
  // 添加用戶角色
  addUserRole(userId, roleId) {
    return apiClient.post(`/api/users/${userId}/roles`, { role_id: roleId });
  },
  
  // 刪除用戶角色
  removeUserRole(userId, roleId) {
    return apiClient.delete(`/api/users/${userId}/roles/${roleId}`);
  },
  
  // 獲取用戶所有角色
  getUserRoles(userId) {
    return apiClient.get(`/api/users/${userId}/roles`);
  }
};