// src/api/permissionApi.js
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

// 權限 API 方法 (注意：此文件名為permissionApi，但原始內容與userApi相同)
export default {
  // 獲取所有權限
  getAllPermissions(params = {}) {
    return apiClient.get('/api/permissions', { params });
  },
  
  // 根據 ID 獲取單個權限
  getPermissionById(id) {
    return apiClient.get(`/api/permissions/${id}`);
  },
  
  // 創建新權限
  createPermission(permissionData) {
    return apiClient.post('/api/permissions', permissionData);
  },
  
  // 更新權限資料
  updatePermission(id, permissionData) {
    return apiClient.put(`/api/permissions/${id}`, permissionData);
  },
  
  // 刪除權限
  deletePermission(id) {
    return apiClient.delete(`/api/permissions/${id}`);
  },
  
  // 獲取特定模塊的所有權限
  getPermissionsByModule(module, params = {}) {
    return apiClient.get(`/api/permissions/module/${module}`, { params });
  },
  
  // 獲取擁有特定權限的所有角色
  getRolesWithPermission(permissionId) {
    return apiClient.get(`/api/permissions/${permissionId}/roles`);
  },
  
  // 檢查當前用戶是否擁有特定權限
  checkUserPermission(permissionName) {
    return apiClient.get(`/api/permissions/check/${permissionName}`);
  }
};