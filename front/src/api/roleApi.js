// src/api/roleApi.js
import axios from 'axios';

// 使用統一的API基礎URL
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

// 創建 axios 實例，設定基本請求路徑
const apiClient = axios.create({
  baseURL: API_BASE_URL, // 基礎URL不包含重複的/api
  withCredentials: true, // 啟用憑據
  headers: {
    'Content-Type': 'application/json',
  }
});

// 請求攔截器 - 添加認證 token
apiClient.interceptors.request.use(
  (config) => {
    // 嘗試從多個可能的存儲位置獲取token
    const token = localStorage.getItem('accessToken') || 
                 localStorage.getItem('token') || 
                 sessionStorage.getItem('accessToken');
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 響應攔截器 - 處理常見錯誤
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    // 處理401未授權錯誤
    if (error.response && error.response.status === 401) {
      console.log('認證已失效，請重新登入');
      // 可選：重定向到登入頁面
      // window.location.href = '/login';
    }
    
    // 控制台輸出詳細錯誤信息，幫助調試
    console.error('API請求錯誤:', {
      url: error.config?.url,
      method: error.config?.method,
      status: error.response?.status,
      data: error.response?.data
    });
    
    return Promise.reject(error);
  }
);

// 角色 API 方法
export default {
  // 獲取所有角色
  getAllRoles(params = {}) {
    return apiClient.get('/api/roles', { params });
  },
  
  // 根據 ID 獲取單個角色
  getRoleById(id) {
    return apiClient.get(`/api/roles/${id}`);
  },
  
  // 創建新角色
  createRole(roleData) {
    return apiClient.post('/api/roles', roleData);
  },
  
  // 更新角色資料
  updateRole(id, roleData) {
    return apiClient.put(`/api/roles/${id}`, roleData);
  },
  
  // 刪除角色
  deleteRole(id) {
    return apiClient.delete(`/api/roles/${id}`);
  },
  
  // 獲取角色的所有權限
  getRolePermissions(roleId) {
    return apiClient.get(`/api/roles/${roleId}/permissions`);
  },
  
  // 添加權限到角色
  addPermissionToRole(roleId, permissionId) {
    return apiClient.post(`/api/roles/${roleId}/permissions`, { permission_id: permissionId });
  },
  
  // 從角色中移除權限
  removePermissionFromRole(roleId, permissionId) {
    return apiClient.delete(`/api/roles/${roleId}/permissions/${permissionId}`);
  },
  
  // 獲取具有特定角色的所有用戶
  getUsersWithRole(roleId, params = {}) {
    return apiClient.get(`/api/roles/${roleId}/users`, { params });
  },
  
  // 獲取角色請求 (如果後端支持此功能)
  getRoleRequests(params = {}) {
    return apiClient.get('/api/role-requests', { params });
  },
  
  // 處理角色請求 (如果後端支持此功能)
  processRoleRequest(requestId, approved) {
    return apiClient.post(`/api/role-requests/${requestId}/process`, { approved });
  },
  
  // 測試連接，可用於檢查API連接狀態
  testConnection() {
    return apiClient.get('/api/health');
  }
};