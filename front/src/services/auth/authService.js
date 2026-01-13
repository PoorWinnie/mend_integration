// src/services/auth/authService.js
import axios from 'axios';
import { useCartStore } from '@/stores/cartStore';
import { useUserStore } from '@/stores/userStore';


// 獲取API基礎URL - 從環境變數或使用默認值
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

// 創建 axios 實例，正確配置基礎URL
const api = axios.create({
  baseURL: API_BASE_URL,  // 使用完整的後端API地址
  headers: {
    'Content-Type': 'application/json'
  }
  // 移除了 withCredentials: true 設置
});

// 添加請求攔截器，記錄發送的請求
api.interceptors.request.use(
  (config) => {
    // 添加認證標頭
    const token = localStorage.getItem('accessToken');
    const adminToken = localStorage.getItem('adminAccessToken');

    if (token && !config.url.includes('/admin')) {
      config.headers.Authorization = `Bearer ${token}`;
    } else if (adminToken && config.url.includes('/admin')) {
      config.headers.Authorization = `Bearer ${adminToken}`;
    }

    console.log('發送請求:', config.url, config.data);
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 添加響應攔截器，處理錯誤響應
api.interceptors.response.use(
  (response) => {
    console.log('收到響應:', response.status, response.data);
    return response;
  },
  (error) => {
    console.error('響應錯誤:', error.response?.status, error.response?.data || error.message);

    // 如果是認證錯誤 (401)，清除令牌並重載頁面
    if (error.response && error.response.status === 401) {
      if (!error.config.url.includes('/auth/login')) {
        console.warn('認證失敗，嘗試重新登入');
        // 只在非登入請求出現 401 時清除
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('adminAccessToken');
        localStorage.removeItem('adminRefreshToken');
        localStorage.removeItem('userRole');
        localStorage.removeItem('userRolesList');  // 確保也清除這個
        localStorage.removeItem('user');
      }
    }

    return Promise.reject(error);
  }
);

const authService = {
  // 檢查是否已登入
  isLoggedIn() {
    return !!localStorage.getItem('accessToken') || !!localStorage.getItem('adminAccessToken');
  },

  // 檢查是否為管理員
  isAdmin() {
    const role = localStorage.getItem('userRole');
    return role === 'ADMIN' || role === 'SUPER_ADMIN';
  },

  // 使用者登入
  async login(username, password) {
    try {
      console.log(`嘗試登入: ${username} 到 ${API_BASE_URL}/api/auth/login`);

      const response = await api.post('/api/auth/login', { username, password });
      const data = response.data;

      console.log("登入成功，伺服器返回數據:", data);

      // 保存登入信息到本地存儲
      localStorage.setItem('accessToken', data.token || data.accessToken);
      localStorage.setItem('refreshToken', data.refreshToken || '');
      localStorage.setItem('userRole', data.role || data.roles?.[0] || 'USER');
      localStorage.setItem('user', JSON.stringify(data.user || data));

      const cartStore = useCartStore();
      cartStore.setUserName(username)

      const userStore = useUserStore();
      userStore.setCurrentUser({
        id: 101,       // 測試資料
        name: '測試用戶',
        roles: ['USER']
      });

      return data;
    } catch (error) {
      console.error('登入失敗:', error);

      // 添加更詳細的錯誤日誌
      if (error.response) {
        console.error('伺服器錯誤響應:', {
          status: error.response.status,
          data: error.response.data,
          headers: error.response.headers
        });
      } else if (error.request) {
        console.error('未收到響應，可能是網絡問題');
      }

      throw error;
    }
  },

  // 管理員登入
  async adminLogin(username, password) {
    try {
      console.log(`嘗試管理員登入: ${username} 到 ${API_BASE_URL}/api/auth/admin/login`);

      // 嘗試正式登入
      const response = await api.post('/api/auth/admin/login', { username, password });
      const data = response.data;

      console.log("管理員登入成功，伺服器返回數據:", data);

      // 保存管理員登入信息到本地存儲
      localStorage.setItem('adminAccessToken', data.token || data.accessToken);
      localStorage.setItem('adminRefreshToken', data.refreshToken || '');

      // 特殊處理 superadmin 用戶
      if (username === 'superadmin') {
        console.log('檢測到特殊用戶 superadmin，強制設置為超級管理員');
        localStorage.setItem('userRole', 'SUPER_ADMIN');

        // 儲存角色列表，確保包含 SUPER_ADMIN
        const roles = Array.isArray(data.roles) ? [...data.roles] : [];
        if (!roles.includes('SUPER_ADMIN')) {
          roles.push('SUPER_ADMIN');
        }
        localStorage.setItem('userRolesList', JSON.stringify(roles));
      } else {
        // 修正：優先選擇SUPER_ADMIN角色，確保超級管理員權限被正確識別
        if (Array.isArray(data.roles) && data.roles.length > 0) {
          // 檢查是否有SUPER_ADMIN角色
          if (data.roles.includes('SUPER_ADMIN')) {
            localStorage.setItem('userRole', 'SUPER_ADMIN');
          } else {
            localStorage.setItem('userRole', data.roles[0]);
          }
          // 將完整角色列表保存為JSON字符串
          localStorage.setItem('userRolesList', JSON.stringify(data.roles));
        } else {
          localStorage.setItem('userRole', data.role || 'ADMIN');
        }
      }

      localStorage.setItem('user', JSON.stringify(data.user || data));

      // 記錄角色信息到控制台以便調試
      console.log('管理員登入成功，角色信息:', {
        role: localStorage.getItem('userRole'),
        allRoles: localStorage.getItem('userRolesList')
      });

      return data;
    } catch (error) {
      console.error('管理員登入失敗:', error);

      // 添加更詳細的錯誤日誌
      if (error.response) {
        console.error('伺服器錯誤響應:', {
          status: error.response.status,
          data: error.response.data,
          headers: error.response.headers
        });
      } else if (error.request) {
        console.error('未收到響應，可能是網絡問題');
      }

      // 嘗試使用模擬登入 (僅開發環境)
      if (username === 'superadmin') {
        console.log('嘗試使用模擬登入');

        try {
          const mockData = {
            token: 'mock-token-for-development',
            refreshToken: 'mock-refresh-token',
            id: 1,
            username: 'superadmin',
            email: 'super@example.com',
            roles: ['SUPER_ADMIN']
          };

          localStorage.setItem('adminAccessToken', mockData.token);
          localStorage.setItem('adminRefreshToken', mockData.refreshToken);
          localStorage.setItem('userRole', 'SUPER_ADMIN');
          localStorage.setItem('userRolesList', JSON.stringify(['SUPER_ADMIN']));
          localStorage.setItem('user', JSON.stringify(mockData));

          console.log('模擬管理員登入成功');
          return mockData;
        } catch (mockError) {
          console.error('模擬登入也失敗:', mockError);
        }
      }

      throw error;
    }
  },

  // 用戶登出
  async logout() {
    try {
      // 檢查當前用戶類型
      const isAdmin = localStorage.getItem('userRole') === 'ADMIN' ||
        localStorage.getItem('userRole') === 'SUPER_ADMIN';

      // 選擇對應的登出端點
      const logoutEndpoint = isAdmin ? '/api/auth/admin/logout' : '/api/auth/logout';

      // 只有在有令牌的情況下才發送登出請求
      if (this.isLoggedIn()) {
        await api.post(logoutEndpoint);
      }

      // 無論請求是否成功，都清除本地存儲
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('adminAccessToken');
      localStorage.removeItem('adminRefreshToken');
      localStorage.removeItem('userRole');
      localStorage.removeItem('userRolesList');  // 確保也清除這個
      localStorage.removeItem('user');

      // 確認是管理員還是一般用戶，然後導向對應的登入頁面
      if (isAdmin) {
        window.location.href = '/admin/login'; // 導向管理員登入頁面
      } else {
        window.location.href = '/login'; // 導向一般用戶登入頁面
      }

      return { success: true };
    } catch (error) {
      console.error('登出錯誤:', error);

      // 即使發生錯誤，也清除本地存儲並進行跳轉
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('adminAccessToken');
      localStorage.removeItem('adminRefreshToken');
      localStorage.removeItem('userRole');
      localStorage.removeItem('userRolesList');  // 確保也清除這個
      localStorage.removeItem('user');

      const isAdmin = localStorage.getItem('userRole') === 'ADMIN' ||
        localStorage.getItem('userRole') === 'SUPER_ADMIN';
      if (isAdmin) {
        window.location.href = '/admin/login';
      } else {
        window.location.href = '/login';
      }

      throw error;
    }
  },

  // 註冊新使用者
  async register(userData) {
    try {
      console.log('準備註冊:', userData);

      // 調整資料格式以適應後端期望的格式
      const registerData = {
        username: userData.username,
        email: userData.email,
        password: userData.password,
        fullName: userData.fullName || userData.username,
        phone: userData.phone || null,
        role: userData.role === 'owner' ? 'CAMP_OWNER' : 'USER'
      };

      console.log('註冊資料:', registerData);

      // 使用 /api/auth/register
      const response = await api.post('/api/auth/register', registerData);

      console.log('註冊成功:', response.data);

      // 註冊成功後可以直接登入，儲存token和用戶資料
      if (response.data.token || response.data.accessToken) {
        localStorage.setItem('accessToken', response.data.token || response.data.accessToken);
        localStorage.setItem('refreshToken', response.data.refreshToken || '');
        localStorage.setItem('userRole', response.data.roles?.[0] || response.data.role || 'USER');
        localStorage.setItem('user', JSON.stringify(response.data.user || response.data));
      }

      return response.data;
    } catch (error) {
      console.error('註冊最終錯誤:', error);

      // 提供友好的錯誤訊息
      let errorMessage = '註冊失敗，請稍後再試';

      if (error.response) {
        if (error.response.data?.message) {
          // 翻譯常見的錯誤訊息
          if (error.response.data.message.includes('Username is already taken')) {
            errorMessage = '此用戶名已被使用，請嘗試其他用戶名';
          } else if (error.response.data.message.includes('Email is already in use')) {
            errorMessage = '此電子郵件已被使用，請使用其他電子郵件';
          } else {
            errorMessage = error.response.data.message;
          }
        }
      } else if (error.message) {
        errorMessage = error.message;
      }

      throw new Error(errorMessage);
    }
  },

  // 新增：管理員註冊
  async registerAdmin(adminData) {
    try {
      console.log('準備註冊管理員:', adminData);

      // 調整資料格式以適應後端期望的格式
      const registerData = {
        username: adminData.username,
        email: adminData.email,
        password: adminData.password,
        fullName: adminData.fullName || adminData.username,
        adminKey: adminData.adminKey || '', // 管理員註冊金鑰
        role: 'ADMIN' // 指定為管理員角色
      };

      console.log('管理員註冊資料:', registerData);

      // 使用管理員註冊 API 端點
      const response = await api.post('/api/auth/admin/register', registerData);

      console.log('管理員註冊成功:', response.data);

      // 註冊成功後可以直接登入，儲存token和用戶資料
      if (response.data.token || response.data.accessToken) {
        localStorage.setItem('adminAccessToken', response.data.token || response.data.accessToken);
        localStorage.setItem('adminRefreshToken', response.data.refreshToken || '');
        localStorage.setItem('userRole', response.data.roles?.[0] || response.data.role || 'ADMIN');
        localStorage.setItem('user', JSON.stringify(response.data.user || response.data));
      }

      return response.data;
    } catch (error) {
      console.error('管理員註冊錯誤:', error);

      // 提供友好的錯誤訊息
      let errorMessage = '管理員註冊失敗，請稍後再試';

      if (error.response) {
        if (error.response.data?.message) {
          if (error.response.data.message.includes('Username is already taken')) {
            errorMessage = '此管理員帳號已被使用';
          } else if (error.response.data.message.includes('Email is already in use')) {
            errorMessage = '此電子郵件已被使用';
          } else if (error.response.data.message.includes('Invalid admin key')) {
            errorMessage = '管理員金鑰無效';
          } else {
            errorMessage = error.response.data.message;
          }
        }
      } else if (error.message) {
        errorMessage = error.message;
      }

      throw new Error(errorMessage);
    }
  },

  // 重置密碼
  async resetPassword(email) {
    try {
      const response = await api.post('/api/auth/reset-password', { email });
      return response.data;
    } catch (error) {
      console.error('重置密碼錯誤:', error);
      throw error;
    }
  },

  // 刷新一般使用者令牌
  async refreshToken() {
    try {
      const refreshToken = localStorage.getItem('refreshToken');
      if (!refreshToken) {
        throw new Error('沒有可用的刷新令牌');
      }

      const response = await api.post('/api/auth/refresh-token', { refreshToken });
      const data = response.data;

      localStorage.setItem('accessToken', data.token || data.accessToken);
      if (data.refreshToken) {
        localStorage.setItem('refreshToken', data.refreshToken);
      }

      return data;
    } catch (error) {
      console.error('刷新令牌錯誤:', error);
      throw error;
    }
  },

  // 刷新管理員令牌
  async refreshAdminToken() {
    try {
      const refreshToken = localStorage.getItem('adminRefreshToken');
      if (!refreshToken) {
        throw new Error('沒有可用的管理員刷新令牌');
      }

      const response = await api.post('/api/auth/admin/refresh-token', { refreshToken });
      const data = response.data;

      localStorage.setItem('adminAccessToken', data.token || data.accessToken);
      if (data.refreshToken) {
        localStorage.setItem('adminRefreshToken', data.refreshToken);
      }

      return data;
    } catch (error) {
      console.error('刷新管理員令牌錯誤:', error);
      throw error;
    }
  },

  // 獲取當前用戶信息
  async getCurrentUser() {
    try {
      const response = await api.get('/api/auth/me');
      return response.data;
    } catch (error) {
      console.error('獲取用戶信息錯誤:', error);
      throw error;
    }
  }
};

export default authService;