// log.js
import { defineStore } from 'pinia'
import axios from 'axios'

export const useLogStore = defineStore('log', {
  state: () => ({
    logs: [],
    loading: false,
    error: null,
    total: 0,
    page: 1,
    limit: 10,
    logLevels: ['info', 'warning', 'error', 'debug', 'critical']
  }),
  
  getters: {
    getLogs: (state) => state.logs,
    isLoading: (state) => state.loading,
    hasError: (state) => state.error !== null,
    getTotal: (state) => state.total,
    getCurrentPage: (state) => state.page,
    getPageLimit: (state) => state.limit
  },
  
  actions: {
    async fetchLogs(page = 1, limit = 10, filters = {}) {
      this.loading = true;
      this.error = null;
      try {
        // 模擬API調用 - 實際部署時替換為真實API
        // const response = await axios.get('/api/logs', {
        //   params: { page, limit, ...filters },
        //   withCredentials: true
        // });
        
        // 模擬數據 - 實際部署時刪除這段
        await new Promise(resolve => setTimeout(resolve, 500));
        const mockLogs = Array(20).fill(null).map((_, index) => ({
          id: index + 1,
          timestamp: new Date(Date.now() - Math.random() * 10000000000),
          level: this.logLevels[Math.floor(Math.random() * this.logLevels.length)],
          message: `系統日誌測試消息 ${index + 1}`,
          source: ['系統', '用戶', '資料庫', '網絡'][Math.floor(Math.random() * 4)],
          details: `詳細信息...`
        }));
        
        // 實際部署時使用真實API回應
        // this.logs = response.data.logs || response.data;
        // this.total = response.data.total || response.data.length;
        
        // 模擬數據賦值 - 實際部署時刪除這段
        this.logs = mockLogs;
        this.total = 100; // 模擬總數
        
        this.page = page;
        this.limit = limit;
        return this.logs;
      } catch (error) {
        console.error('Error fetching logs:', error);
        this.error = error.message || '無法取得系統日誌';
        return [];
      } finally {
        this.loading = false;
      }
    },
    
    async clearLogs() {
      this.loading = true;
      this.error = null;
      try {
        // 實際API調用
        // await axios.delete('/api/logs', { withCredentials: true });
        
        // 模擬API延遲
        await new Promise(resolve => setTimeout(resolve, 500));
        
        this.logs = [];
        this.total = 0;
        return true;
      } catch (error) {
        console.error('Error clearing logs:', error);
        this.error = error.message || '無法清除系統日誌';
        return false;
      } finally {
        this.loading = false;
      }
    },
    
    async exportLogs(format = 'csv') {
      this.loading = true;
      this.error = null;
      try {
        // 實際部署時實現真實的導出功能
        // const response = await axios.get(`/api/logs/export?format=${format}`, {
        //   responseType: 'blob',
        //   withCredentials: true
        // });
        
        // 模擬延遲
        await new Promise(resolve => setTimeout(resolve, 800));
        
        // 這裡只是顯示提示，實際部署時應處理檔案下載
        alert(`已請求以 ${format.toUpperCase()} 格式導出日誌`);
        return true;
      } catch (error) {
        console.error('Error exporting logs:', error);
        this.error = error.message || '無法導出系統日誌';
        return false;
      } finally {
        this.loading = false;
      }
    },
    
    // 新增: 獲取用戶登入歷史
    async getUserLoginHistory(params = {}) {
      this.loading = true;
      this.error = null;
      
      try {
        // 實際API調用
        // const response = await axios.get('/api/logs/login-history', {
        //   params,
        //   withCredentials: true
        // });
        
        // 模擬數據
        await new Promise(resolve => setTimeout(resolve, 500));
        
        // 生成模擬登入記錄
        const mockLoginHistory = Array(15).fill(null).map((_, index) => ({
          id: index + 1,
          username: `user${Math.floor(Math.random() * 10) + 1}`,
          login_time: new Date(Date.now() - Math.random() * 10000000000).toISOString(),
          login_ip: `192.168.1.${Math.floor(Math.random() * 255) + 1}`,
          login_device: ['Chrome on Windows', 'Safari on Mac', 'Firefox on Linux', 'Edge on Windows', 'Chrome on Android', 'Safari on iOS'][Math.floor(Math.random() * 6)],
          login_status: Math.random() > 0.2 ? 1 : 0
        }));
        
        // 過濾模擬數據
        let filteredHistory = [...mockLoginHistory];
        
        if (params.username) {
          filteredHistory = filteredHistory.filter(
            item => item.username.includes(params.username)
          );
        }
        
        if (params.start_date) {
          const startDate = new Date(params.start_date);
          filteredHistory = filteredHistory.filter(
            item => new Date(item.login_time) >= startDate
          );
        }
        
        if (params.end_date) {
          const endDate = new Date(params.end_date);
          filteredHistory = filteredHistory.filter(
            item => new Date(item.login_time) <= endDate
          );
        }
        
        // 分頁
        const page = params.page || 1;
        const limit = params.limit || 10;
        const start = (page - 1) * limit;
        const end = start + limit;
        
        return {
          logins: filteredHistory.slice(start, end),
          total: filteredHistory.length
        };
      } catch (error) {
        console.error('Error fetching login history:', error);
        this.error = error.message || '無法獲取登入歷史';
        return { logins: [], total: 0 };
      } finally {
        this.loading = false;
      }
    },
    
    // 新增: 獲取用戶操作日誌
    async getUserActionLogs(params = {}) {
      this.loading = true;
      this.error = null;
      
      try {
        // 實際API調用
        // const response = await axios.get('/api/logs/action-logs', {
        //   params,
        //   withCredentials: true
        // });
        
        // 模擬數據
        await new Promise(resolve => setTimeout(resolve, 500));
        
        // 生成模擬操作日誌
        const actionTypes = ['LOGIN', 'LOGOUT', 'CREATE', 'UPDATE', 'DELETE', 'APPROVE', 'REJECT'];
        const targetTypes = ['User', 'Campsite', 'Booking', 'Review', 'Role', 'Permission'];
        
        const mockActionLogs = Array(20).fill(null).map((_, index) => {
          const actionType = actionTypes[Math.floor(Math.random() * actionTypes.length)];
          const targetType = targetTypes[Math.floor(Math.random() * targetTypes.length)];
          
          const details = {
            before: { id: Math.floor(Math.random() * 100) + 1, status: 'draft' },
            after: { id: Math.floor(Math.random() * 100) + 1, status: 'published' },
            changes: ['status'],
            ip: `192.168.1.${Math.floor(Math.random() * 255) + 1}`,
            browser: 'Chrome',
            os: 'Windows'
          };
          
          return {
            id: index + 1,
            username: `user${Math.floor(Math.random() * 10) + 1}`,
            action_type: actionType,
            target_type: targetType,
            target_id: Math.floor(Math.random() * 100) + 1,
            details: JSON.stringify(details),
            created_at: new Date(Date.now() - Math.random() * 10000000000).toISOString()
          };
        });
        
        // 過濾模擬數據
        let filteredLogs = [...mockActionLogs];
        
        if (params.username) {
          filteredLogs = filteredLogs.filter(
            item => item.username.includes(params.username)
          );
        }
        
        if (params.action_type) {
          filteredLogs = filteredLogs.filter(
            item => item.action_type === params.action_type
          );
        }
        
        if (params.start_date) {
          const startDate = new Date(params.start_date);
          filteredLogs = filteredLogs.filter(
            item => new Date(item.created_at) >= startDate
          );
        }
        
        if (params.end_date) {
          const endDate = new Date(params.end_date);
          filteredLogs = filteredLogs.filter(
            item => new Date(item.created_at) <= endDate
          );
        }
        
        // 分頁
        const page = params.page || 1;
        const limit = params.limit || 10;
        const start = (page - 1) * limit;
        const end = start + limit;
        
        return {
          logs: filteredLogs.slice(start, end),
          total: filteredLogs.length
        };
      } catch (error) {
        console.error('Error fetching action logs:', error);
        this.error = error.message || '無法獲取操作日誌';
        return { logs: [], total: 0 };
      } finally {
        this.loading = false;
      }
    }
  }
})