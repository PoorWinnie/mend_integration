// src/stores/user.js
import { defineStore } from 'pinia';
import axios from 'axios';

export const useUserStore = defineStore('user', {
  state: () => ({
    users: [],
    loading: false,
    error: null
  }),
  
  actions: {
    async getUsers(params = {}) {
      this.loading = true;
      this.error = null;
      
      try {
        // 模擬API調用
        // const response = await axios.get('/api/users', { params });
        // return response.data;
        
        // 模擬數據
        await new Promise(resolve => setTimeout(resolve, 500));
        
        const mockUsers = [
          {
            id: 1,
            username: 'admin',
            email: 'admin@example.com',
            role: 'admin',
            createdAt: '2023-01-15T08:30:00Z',
            status: 'active',
            lastLogin: '2023-11-22T10:15:30Z'
          },
          {
            id: 2,
            username: 'johnsmith',
            email: 'john.smith@example.com',
            role: 'camper',
            createdAt: '2023-03-20T14:15:00Z',
            status: 'active',
            lastLogin: '2023-11-20T08:30:45Z'
          },
          {
            id: 3,
            username: 'campowner',
            email: 'owner@campsite.com',
            role: 'owner',
            createdAt: '2023-04-10T09:45:00Z',
            status: 'active',
            lastLogin: '2023-11-21T16:20:15Z'
          },
          {
            id: 4,
            username: 'newuser',
            email: 'new.user@example.com',
            role: 'camper',
            createdAt: '2023-06-05T11:20:00Z',
            status: 'pending',
            lastLogin: null
          },
          {
            id: 5,
            username: 'disableduser',
            email: 'disabled@example.com',
            role: 'camper',
            createdAt: '2023-02-28T16:30:00Z',
            status: 'inactive',
            lastLogin: '2023-09-15T14:10:22Z'
          }
        ];
        
        // 過濾
        let filteredUsers = [...mockUsers];
        
        if (params.search) {
          const search = params.search.toLowerCase();
          filteredUsers = filteredUsers.filter(user => 
            user.username.toLowerCase().includes(search) || 
            user.email.toLowerCase().includes(search)
          );
        }
        
        if (params.role) {
          filteredUsers = filteredUsers.filter(user => 
            user.role === params.role
          );
        }
        
        if (params.status) {
          filteredUsers = filteredUsers.filter(user => 
            user.status === params.status
          );
        }
        
        // 分頁
        const page = params.page || 1;
        const limit = params.limit || 10;
        const startIndex = (page - 1) * limit;
        const endIndex = startIndex + limit;
        const paginatedUsers = filteredUsers.slice(startIndex, endIndex);
        
        return {
          users: paginatedUsers,
          total: filteredUsers.length
        };
      } catch (error) {
        this.error = error.message || '載入使用者列表失敗';
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async getUserById(id) {
      this.loading = true;
      this.error = null;
      
      try {
        // 模擬API調用
        // const response = await axios.get(`/api/users/${id}`);
        // return response.data;
        
        // 模擬數據
        await new Promise(resolve => setTimeout(resolve, 300));
        
        const mockUsers = [
          {
            id: 1,
            username: 'admin',
            email: 'admin@example.com',
            role: 'admin',
            createdAt: '2023-01-15T08:30:00Z',
            status: 'active',
            lastLogin: '2023-11-22T10:15:30Z'
          },
          {
            id: 2,
            username: 'johnsmith',
            email: 'john.smith@example.com',
            role: 'camper',
            createdAt: '2023-03-20T14:15:00Z',
            status: 'active',
            lastLogin: '2023-11-20T08:30:45Z'
          },
          {
            id: 3,
            username: 'campowner',
            email: 'owner@campsite.com',
            role: 'owner',
            createdAt: '2023-04-10T09:45:00Z',
            status: 'active',
            lastLogin: '2023-11-21T16:20:15Z'
          },
          {
            id: 4,
            username: 'newuser',
            email: 'new.user@example.com',
            role: 'camper',
            createdAt: '2023-06-05T11:20:00Z',
            status: 'pending',
            lastLogin: null
          },
          {
            id: 5,
            username: 'disableduser',
            email: 'disabled@example.com',
            role: 'camper',
            createdAt: '2023-02-28T16:30:00Z',
            status: 'inactive',
            lastLogin: '2023-09-15T14:10:22Z'
          }
        ];
        
        const user = mockUsers.find(user => user.id === id);
        
        if (!user) {
          throw new Error('使用者不存在');
        }
        
        return user;
      } catch (error) {
        this.error = error.message || '獲取使用者詳情失敗';
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async createUser(userData) {
      this.loading = true;
      this.error = null;
      
      try {
        // 模擬API調用
        // const response = await axios.post('/api/users', userData);
        // return response.data;
        
        // 模擬數據
        await new Promise(resolve => setTimeout(resolve, 500));
        
        return { success: true, message: '使用者創建成功' };
      } catch (error) {
        this.error = error.message || '創建使用者失敗';
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async updateUser(id, userData) {
      this.loading = true;
      this.error = null;
      
      try {
        // 模擬API調用
        // const response = await axios.put(`/api/users/${id}`, userData);
        // return response.data;
        
        // 模擬數據
        await new Promise(resolve => setTimeout(resolve, 500));
        
        return { success: true, message: '使用者更新成功' };
      } catch (error) {
        this.error = error.message || '更新使用者失敗';
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async deleteUser(id) {
      this.loading = true;
      this.error = null;
      
      try {
        // 模擬API調用
        // const response = await axios.delete(`/api/users/${id}`);
        // return response.data;
        
        // 模擬數據
        await new Promise(resolve => setTimeout(resolve, 500));
        
        return { success: true, message: '使用者刪除成功' };
      } catch (error) {
        this.error = error.message || '刪除使用者失敗';
        throw error;
      } finally {
        this.loading = false;
      }
    }
  }
});