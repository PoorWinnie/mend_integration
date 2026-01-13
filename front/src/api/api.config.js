// src/api/api.config.js
// 集中管理API配置

// 後端API的基礎URL
export const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

// 常用路徑前綴
export const API_PATHS = {
  AUTH: '/api/auth',
  PUBLIC: '/api/public',
  USERS: '/api/users',
  PERMISSIONS: '/api/permissions',
  ROLES: '/api/roles'
};

// 獲取完整API路徑
export const getApiUrl = (path) => {
  return `${API_BASE_URL}${path}`;
};

// 默認請求頭
export const DEFAULT_HEADERS = {
  'Content-Type': 'application/json'
};

// 認證相關的方法
export const getAuthHeader = () => {
  const token = localStorage.getItem('accessToken');
  return token ? { Authorization: `Bearer ${token}` } : {};
};

export default {
  API_BASE_URL,
  API_PATHS,
  getApiUrl,
  DEFAULT_HEADERS,
  getAuthHeader
};