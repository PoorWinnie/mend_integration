// src/stores/auth.js
import { defineStore } from 'pinia';
import authService from '../services/auth/authService';
import { useCartStore } from './cartStore';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    // 使用者資訊 用localstorage當頁面刷新後，就可以從瀏覽器儲存中把使用者資訊還原回 state。
    user: JSON.parse(localStorage.getItem('user')) || null,
    // 使用者角色
    role: localStorage.getItem('userRole') || null,
    // 一般使用者令牌
    accessToken: localStorage.getItem('accessToken') || null,
    refreshToken: localStorage.getItem('refreshToken') || null,
    // 管理員令牌
    adminAccessToken: localStorage.getItem('adminAccessToken') || null,
    adminRefreshToken: localStorage.getItem('adminRefreshToken') || null,
    // 登入加載狀態
    loading: false,
    // 錯誤訊息
    error: null,
  }),

  getters: {
    // 檢查是否已登入
    isLoggedIn: (state) => !!state.accessToken || !!state.adminAccessToken,
    // 檢查是否為管理員
    isAdmin: (state) => state.role === 'ADMIN' || state.role === 'SUPER_ADMIN',
    // 新增：檢查是否為超級管理員
    isSuperAdmin: (state) => state.role === 'SUPER_ADMIN',
    // 獲取使用者名稱
    username: (state) => state.user ? state.user.username : '',
    // 獲取顯示名稱
    fullName: (state) => state.user ? (state.user.full_name || state.user.fullName) : '',
    isCampOwner: (state) => state.role === 'CAMP_OWNER',
  },

  actions: {
    // 使用者登入
    async login(username, password) {
      this.loading = true;
      this.error = null;

      try {
        const response = await authService.login(username, password);

        this.user = response.user;
        this.role = response.role;
        this.accessToken = response.accessToken || response.token;
        this.refreshToken = response.refreshToken;

        localStorage.setItem('user', JSON.stringify(this.user));
        localStorage.setItem('userRole', this.role);
        localStorage.setItem('accessToken', this.accessToken);
        localStorage.setItem('refreshToken', this.refreshToken);

        return response;
      } catch (error) {
        this.error = error.response?.data?.message || '登入失敗';
        throw error;
      } finally {
        this.loading = false;
      }
    },

    // 管理員登入
    async adminLogin(username, password) {
      this.loading = true;
      this.error = null;

      try {
        const response = await authService.adminLogin(username, password);

        this.user = response.user;

        // 特別處理 superadmin 用戶
        if (username === 'superadmin') {
          console.log('Pinia store 中特殊處理 superadmin 用戶');
          this.role = 'SUPER_ADMIN';
        } else {
          // 修正：優先使用SUPER_ADMIN角色
          if (Array.isArray(response.roles) && response.roles.length > 0) {
            if (response.roles.includes('SUPER_ADMIN')) {
              this.role = 'SUPER_ADMIN';
            } else {
              this.role = response.roles[0];
            }
          } else {
            this.role = response.role;
          }
        }

        this.adminAccessToken = response.accessToken || response.token;
        this.adminRefreshToken = response.refreshToken;
        localStorage.setItem('user', JSON.stringify(this.user));
        localStorage.setItem('userRole', this.role);
        localStorage.setItem('adminAccessToken', this.adminAccessToken);
        localStorage.setItem('adminRefreshToken', this.adminRefreshToken);


        console.log('Pinia store 更新角色信息:', this.role);

        return response;
      } catch (error) {
        this.error = error.response?.data?.message || '管理員登入失敗';
        throw error;
      } finally {
        this.loading = false;
      }
    },

    // 登出
    async logout() {
      try {
        await authService.logout();
      } finally {
        // 無論成功與否，都清除本地存儲
        this.user = null;
        this.role = null;
        this.accessToken = null;
        this.refreshToken = null;
        this.adminAccessToken = null;
        this.adminRefreshToken = null;
        // 確保清除localStorage
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('adminAccessToken');
        localStorage.removeItem('adminRefreshToken');
        localStorage.removeItem('userRole');
        localStorage.removeItem('user');

        const cartStore = useCartStore();
        cartStore.resetCart();
      }
    },

    // 註冊 - 更新處理邏輯
    async register(userData) {
      this.loading = true;
      this.error = null;

      try {
        // 調整註冊數據格式
        const registrationData = {
          username: userData.username,
          email: userData.email,
          password: userData.password,
          fullName: userData.fullName || userData.username,
          phone: userData.phone || null,
          role: userData.role === 'owner' ? 'CAMP_OWNER' : 'USER'
        };

        console.log('註冊提交數據:', registrationData);
        const response = await authService.register(registrationData);

        // 如果註冊自動登入，設置用戶信息
        if (response && (response.token || response.accessToken)) {
          this.user = response.user || userData;
          this.role = response.role || (userData.role === 'owner' ? 'CAMP_OWNER' : 'USER');
          this.accessToken = response.token || response.accessToken;
          this.refreshToken = response.refreshToken || '';


          localStorage.setItem('user', JSON.stringify(this.user));
          localStorage.setItem('userRole', this.role);
          localStorage.setItem('accessToken', this.accessToken);
          localStorage.setItem('refreshToken', this.refreshToken);


          // 直接執行登入
          try {
            await this.login(userData.username, userData.password);
          } catch (err) {
            console.warn('註冊後自動登入失敗:', err);
          }
        }

        return response;
      } catch (error) {
        this.error = error.response?.data?.message || '註冊失敗';
        throw error;
      } finally {
        this.loading = false;
      }
    },

    // 刷新令牌
    async refreshToken() {
      try {
        const response = await authService.refreshToken();
        if (response) {
          this.accessToken = response.accessToken || response.token;
        }
        return response;
      } catch (error) {
        // 刷新失敗，登出
        await this.logout();
        throw error;
      }
    },

    // 刷新管理員令牌
    async refreshAdminToken() {
      try {
        const response = await authService.refreshAdminToken();
        if (response) {
          this.adminAccessToken = response.accessToken || response.token;
        }
        return response;
      } catch (error) {
        // 刷新失敗，登出
        await this.logout();
        throw error;
      }
    },

    // 手動更新用戶資料
    setUser(userData) {
      this.user = userData;
      // 可選：更新localStorage
      localStorage.setItem('user', JSON.stringify(userData));
    },

    // 檢查並恢復登入狀態
    checkAndRestoreAuth() {
      const token = localStorage.getItem('accessToken') || localStorage.getItem('adminAccessToken');
      const userJson = localStorage.getItem('user');

      if (token && userJson && !this.user) {
        try {
          const userData = JSON.parse(userJson);
          this.user = userData;
          this.role = localStorage.getItem('userRole');
          this.accessToken = localStorage.getItem('accessToken');
          this.adminAccessToken = localStorage.getItem('adminAccessToken');
          console.log('已從localStorage恢復用戶登入狀態');
          return true;
        } catch (error) {
          console.error('恢復登入狀態失敗:', error);
          return false;
        }
      }
      return false;
    }
  }
});