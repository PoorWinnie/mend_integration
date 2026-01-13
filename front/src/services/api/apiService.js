import axios from 'axios';
import authService from '../auth/authService';

// 創建一個 axios 實例
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  }
});

// 請求攔截器，自動添加令牌
api.interceptors.request.use(
  (config) => {
    // 根據不同的 API 路徑，添加不同的令牌
    if (config.url.startsWith('/admin')) {
      const adminToken = localStorage.getItem('adminAccessToken');
      if (adminToken) {
        config.headers.Authorization = `Bearer ${adminToken}`;
      }
    } else {
      const token = localStorage.getItem('accessToken');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 響應攔截器，處理令牌過期
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    
    // 如果錯誤代碼為 401 (未授權) 並且還沒有嘗試過刷新令牌
    if (error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      try {
        // 根據請求路徑判斷使用哪個刷新令牌函數
        if (originalRequest.url.startsWith('/admin')) {
          await authService.refreshAdminToken();
          originalRequest.headers.Authorization = `Bearer ${localStorage.getItem('adminAccessToken')}`;
        } else {
          await authService.refreshToken();
          originalRequest.headers.Authorization = `Bearer ${localStorage.getItem('accessToken')}`;
        }
        
        // 使用新令牌重新嘗試請求
        return api(originalRequest);
      } catch (refreshError) {
        // 如果刷新令牌也失敗，則登出使用者
        authService.logout();
        return Promise.reject(refreshError);
      }
    }
    
    return Promise.reject(error);
  }
);

const apiService = {
  // 通用 CRUD 操作
  get(url, params) {
    return api.get(url, { params });
  },
  
  post(url, data) {
    return api.post(url, data);
  },
  
  put(url, data) {
    return api.put(url, data);
  },
  
  delete(url) {
    return api.delete(url);
  },
  
  // 文件上傳
  upload(url, formData, onProgress) {
    return api.post(url, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      onUploadProgress: (progressEvent) => {
        if (onProgress) {
          const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          onProgress(percentCompleted);
        }
      }
    });
  },
  
  // 獲取具體模型的所有資料
  getAll(resource) {
    return this.get(`/${resource}`);
  },
  
  // 獲取單一資料
  getOne(resource, id) {
    return this.get(`/${resource}/${id}`);
  },
  
  // 創建資料
  create(resource, data) {
    return this.post(`/${resource}`, data);
  },
  
  // 更新資料
  update(resource, id, data) {
    return this.put(`/${resource}/${id}`, data);
  },
  
  // 刪除資料
  remove(resource, id) {
    return this.delete(`/${resource}/${id}`);
  }
};

export default apiService; 