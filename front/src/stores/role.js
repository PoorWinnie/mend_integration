import { defineStore } from 'pinia';
import axios from 'axios';
import { useToast } from '../composables/useToast';

export const useRoleStore = defineStore('role', {
  state: () => ({
    roles: [],
    permissions: [],
    roleRequests: [], // 角色請求陣列
    usersWithRoles: [], // 帶有角色信息的用戶列表
    loading: false,
    error: null
  }),
  
  getters: {
    // 獲取管理員用戶列表
    adminUsers: (state) => {
      return state.usersWithRoles.filter(user => 
        user.role?.code === 'admin' || user.role?.code === 'super_admin');
    },
    
    // 獲取營地管理員用戶列表
    campOwnerUsers: (state) => {
      return state.usersWithRoles.filter(user => 
        user.role?.code === 'owner' || user.role?.code === 'camp_owner');
    },
    
    // 獲取一般會員用戶列表
    regularUsers: (state) => {
      return state.usersWithRoles.filter(user => 
        user.role?.code === 'user' || user.role?.code === 'camper');
    }
  },
  
  actions: {
    async getRoles() {
      this.loading = true;
      this.error = null;
      
      try {
        // 模擬API調用
        // const response = await axios.get('/api/roles');
        // return response.data;
        
        // 模擬數據 - 确保包含 一般管理員、營地管理員和一般會員
        await new Promise(resolve => setTimeout(resolve, 500));
        
        const mockRoles = [
          {
            id: 1,
            name: '超級管理員',
            code: 'super_admin',
            description: '系統最高權限管理員，可以管理系統所有功能和設置。',
            isDefault: false,
            userCount: 1,
            permissions: [
              { id: 1, name: '管理使用者' },
              { id: 2, name: '管理角色和權限' },
              { id: 3, name: '管理露營地' },
              { id: 4, name: '管理預訂' },
              { id: 5, name: '管理系統設置' },
              { id: 6, name: '查看報表' }
            ]
          },
          {
            id: 2,
            name: '一般管理員',
            code: 'admin',
            description: '系統管理員可以訪問大部分功能和數據，協助管理系統。',
            isDefault: false,
            userCount: 5,
            permissions: [
              { id: 1, name: '管理使用者' },
              { id: 3, name: '管理露營地' },
              { id: 4, name: '管理預訂' },
              { id: 6, name: '查看報表' }
            ]
          },
          {
            id: 3,
            name: '營地管理員',
            code: 'camp_owner',
            description: '營地管理員可以管理自己的露營地，處理預訂請求，查看營地數據和評論。',
            isDefault: false,
            userCount: 25,
            permissions: [
              { id: 12, name: '管理露營地資訊' },
              { id: 13, name: '處理預訂請求' },
              { id: 14, name: '查看營地數據' },
              { id: 15, name: '回覆評論' },
              { id: 16, name: '管理營地照片' }
            ]
          },
          {
            id: 4,
            name: '一般會員',
            code: 'user',
            description: '一般使用者可以瀏覽露營地，進行預訂，管理個人資料和預訂記錄。',
            isDefault: true,
            userCount: 120,
            permissions: [
              { id: 7, name: '瀏覽露營地' },
              { id: 8, name: '預訂露營地' },
              { id: 9, name: '管理個人資料' },
              { id: 10, name: '查看預訂記錄' },
              { id: 11, name: '撰寫評論' }
            ]
          }
        ];
        
        this.roles = mockRoles;
        return { roles: mockRoles };
      } catch (error) {
        this.error = error.message || '載入角色列表失敗';
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async getRoleById(id) {
      this.loading = true;
      this.error = null;
      id = parseInt(id);
      
      try {
        // 實際使用時改成真實API調用
        // const response = await axios.get(`/api/roles/${id}`);
        // return response.data;
        
        // 如果已經有本地角色數據，先嘗試從中查找
        const localRole = this.roles.find(role => role.id === id);
        if (localRole) {
          return localRole;
        }
        
        // 模擬數據
        await new Promise(resolve => setTimeout(resolve, 300));
        
        const mockRoles = [
          {
            id: 1,
            name: '超級管理員',
            code: 'super_admin',
            description: '系統最高權限管理員，可以管理系統所有功能和設置。',
            isDefault: false,
            permissions: [
              { id: 1, name: '管理使用者' },
              { id: 2, name: '管理角色和權限' },
              { id: 3, name: '管理露營地' },
              { id: 4, name: '管理預訂' },
              { id: 5, name: '管理系統設置' },
              { id: 6, name: '查看報表' }
            ]
          },
          {
            id: 2,
            name: '一般管理員',
            code: 'admin',
            description: '系統管理員可以訪問大部分功能和數據，協助管理系統。',
            isDefault: false,
            permissions: [
              { id: 1, name: '管理使用者' },
              { id: 3, name: '管理露營地' },
              { id: 4, name: '管理預訂' },
              { id: 6, name: '查看報表' }
            ]
          },
          {
            id: 3,
            name: '營地管理員',
            code: 'camp_owner',
            description: '營地管理員可以管理自己的露營地，處理預訂請求，查看營地數據和評論。',
            isDefault: false,
            permissions: [
              { id: 12, name: '管理露營地資訊' },
              { id: 13, name: '處理預訂請求' },
              { id: 14, name: '查看營地數據' },
              { id: 15, name: '回覆評論' },
              { id: 16, name: '管理營地照片' }
            ]
          },
          {
            id: 4,
            name: '一般會員',
            code: 'user',
            description: '一般使用者可以瀏覽露營地，進行預訂，管理個人資料和預訂記錄。',
            isDefault: true,
            permissions: [
              { id: 7, name: '瀏覽露營地' },
              { id: 8, name: '預訂露營地' },
              { id: 9, name: '管理個人資料' },
              { id: 10, name: '查看預訂記錄' },
              { id: 11, name: '撰寫評論' }
            ]
          }
        ];
        
        const role = mockRoles.find(role => role.id === id);
        
        if (!role) {
          throw new Error('角色不存在');
        }
        
        return role;
      } catch (error) {
        this.error = error.message || '獲取角色詳情失敗';
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async getPermissions() {
      this.loading = true;
      this.error = null;
      
      try {
        // 實際使用時改成真實API調用
        // const response = await axios.get('/api/permissions');
        // return response.data;
        
        // 如果已經有權限數據，直接返回
        if (this.permissions.length > 0) {
          return { permissions: this.permissions };
        }
        
        // 模擬數據
        await new Promise(resolve => setTimeout(resolve, 300));
        
        const mockPermissions = [
          { id: 1, name: '管理使用者', code: 'manage_users' },
          { id: 2, name: '管理角色和權限', code: 'manage_roles' },
          { id: 3, name: '管理露營地', code: 'manage_camps' },
          { id: 4, name: '管理預訂', code: 'manage_bookings' },
          { id: 5, name: '管理系統設置', code: 'manage_settings' },
          { id: 6, name: '查看報表', code: 'view_reports' },
          { id: 7, name: '瀏覽露營地', code: 'view_camps' },
          { id: 8, name: '預訂露營地', code: 'book_camps' },
          { id: 9, name: '管理個人資料', code: 'manage_profile' },
          { id: 10, name: '查看預訂記錄', code: 'view_bookings' },
          { id: 11, name: '撰寫評論', code: 'write_reviews' },
          { id: 12, name: '管理露營地資訊', code: 'manage_camp_info' },
          { id: 13, name: '處理預訂請求', code: 'handle_bookings' },
          { id: 14, name: '查看營地數據', code: 'view_camp_stats' },
          { id: 15, name: '回覆評論', code: 'reply_reviews' },
          { id: 16, name: '管理營地照片', code: 'manage_camp_photos' },
          { id: 17, name: '管理文章和公告', code: 'manage_articles' },
          { id: 18, name: '編輯網站內容', code: 'edit_content' },
          { id: 19, name: '管理多媒體資源', code: 'manage_media' }
        ];
        
        this.permissions = mockPermissions;
        return { permissions: mockPermissions };
      } catch (error) {
        this.error = error.message || '載入權限列表失敗';
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async createRole(roleData) {
      this.loading = true;
      this.error = null;
      
      try {
        // 實際使用時改成真實API調用
        // const response = await axios.post('/api/roles', roleData);
        // const newRole = response.data;
        
        // 模擬數據
        await new Promise(resolve => setTimeout(resolve, 500));
        
        // 模擬 API 響應，創建一個新角色對象
        const newId = Math.max(0, ...this.roles.map(r => r.id)) + 1; // 生成新的 ID
        
        // 將權限ID轉換為權限對象
        const permissions = [];
        if (roleData.permissionIds && Array.isArray(roleData.permissionIds)) {
          for (const permId of roleData.permissionIds) {
            const perm = this.permissions.find(p => p.id === permId);
            if (perm) permissions.push({ id: perm.id, name: perm.name, code: perm.code });
          }
        }
        
        const newRole = {
          id: newId,
          name: roleData.name,
          code: roleData.code,
          description: roleData.description,
          isDefault: roleData.isDefault || false,
          permissions: permissions,
          userCount: 0 // 新角色默認沒有用戶
        };
        
        // 將新角色添加到本地陣列
        this.roles.push(newRole);
        
        const toast = useToast();
        toast.success('角色創建成功');
        
        return { success: true, message: '角色創建成功', role: newRole };
      } catch (error) {
        this.error = error.message || '創建角色失敗';
        const toast = useToast();
        toast.error('創建角色失敗: ' + this.error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async updateRole(id, roleData) {
      this.loading = true;
      this.error = null;
      id = parseInt(id);
      
      try {
        // 實際使用時改成真實API調用
        // const response = await axios.put(`/api/roles/${id}`, roleData);
        // const updatedRole = response.data;
        
        // 模擬數據
        await new Promise(resolve => setTimeout(resolve, 500));
        
        // 將權限ID轉換為權限對象
        const permissions = [];
        if (roleData.permissionIds && Array.isArray(roleData.permissionIds)) {
          for (const permId of roleData.permissionIds) {
            const perm = this.permissions.find(p => p.id === permId);
            if (perm) permissions.push({ id: perm.id, name: perm.name, code: perm.code });
          }
        }
        
        // 模擬API返回的更新後角色數據
        const updatedRole = {
          id: id,
          name: roleData.name,
          code: roleData.code,
          description: roleData.description,
          isDefault: roleData.isDefault || false,
          permissions: permissions
        };
        
        // 更新本地角色數據
        const index = this.roles.findIndex(role => role.id === id);
        if (index !== -1) {
          // 保留用戶數量
          updatedRole.userCount = this.roles[index].userCount;
          // 更新角色
          this.roles[index] = updatedRole;
        } else {
          console.error('找不到要更新的角色:', id);
          throw new Error('找不到要更新的角色');
        }
        
        const toast = useToast();
        toast.success('角色更新成功');
        
        return { success: true, message: '角色更新成功', role: updatedRole };
      } catch (error) {
        this.error = error.message || '更新角色失敗';
        const toast = useToast();
        toast.error('更新角色失敗: ' + this.error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async deleteRole(id) {
      this.loading = true;
      this.error = null;
      id = parseInt(id);
      
      try {
        // 檢查是否為默認角色
        const role = this.roles.find(r => r.id === id);
        if (!role) {
          throw new Error('找不到該角色');
        }
        
        if (role.isDefault) {
          throw new Error('不能刪除默認角色');
        }
        
        // 檢查是否有用戶使用此角色
        if (role.userCount && role.userCount > 0) {
          throw new Error('該角色下有用戶，不能刪除');
        }
        
        // 實際使用時改成真實API調用
        // const response = await axios.delete(`/api/roles/${id}`);
        // return response.data;
        
        // 模擬數據
        await new Promise(resolve => setTimeout(resolve, 500));
        
        // 從本地陣列中移除角色
        const index = this.roles.findIndex(role => role.id === id);
        if (index !== -1) {
          this.roles.splice(index, 1);
        }
        
        const toast = useToast();
        toast.success('角色刪除成功');
        
        return { success: true, message: '角色刪除成功' };
      } catch (error) {
        this.error = error.message || '刪除角色失敗';
        const toast = useToast();
        toast.error('刪除角色失敗: ' + this.error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    // 獲取待處理的角色請求
    async getPendingRoleRequests() {
      this.loading = true;
      this.error = null;
      
      try {
        // 實際使用時改成真實API調用
        // const response = await axios.get('/api/role-requests?status=pending');
        // this.roleRequests = response.data;
        // return this.roleRequests;
        
        // 模擬數據
        await new Promise(resolve => setTimeout(resolve, 500));
        
        const mockRoleRequests = [
          {
            id: 1,
            userId: 101,
            username: '陳小明',
            email: 'chen@example.com',
            phone: '0912-345-678',
            requestedRole: {
              id: 3,
              name: '營地管理員',
              code: 'camp_owner'
            },
            reason: '我擁有一處位於陽明山的露營地，希望能在平台上管理並接受預訂。',
            supportingDocuments: ['business_license.pdf', 'camp_photos.zip'],
            status: 'pending',
            createdAt: '2023-07-15T09:23:45'
          },
          {
            id: 2,
            userId: 102,
            username: '林美玲',
            email: 'lin@example.com',
            phone: '0923-456-789',
            requestedRole: {
              id: 3,
              name: '營地管理員',
              code: 'camp_owner'
            },
            reason: '我們家有一個森林露營地，位於宜蘭，設施完善，希望能加入平台。',
            supportingDocuments: ['camping_license.pdf', 'insurance_certificate.pdf'],
            status: 'pending',
            createdAt: '2023-07-18T14:35:22'
          },
          {
            id: 3,
            userId: 103,
            username: '王大華',
            email: 'wang@example.com',
            phone: '0934-567-890',
            requestedRole: {
              id: 2,
              name: '一般管理員',
              code: 'admin'
            },
            reason: '我有多年的營地管理經驗，希望能協助平台管理及維護。',
            supportingDocuments: ['resume.pdf', 'recommendation_letter.pdf'],
            status: 'pending',
            createdAt: '2023-07-20T11:15:30'
          }
        ];
        
        this.roleRequests = mockRoleRequests;
        return mockRoleRequests;
      } catch (error) {
        this.error = error.message || '載入角色請求失敗';
        const toast = useToast();
        toast.error('載入角色請求失敗: ' + this.error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    // 處理角色請求 (批准或拒絕)
    async processRoleRequest(requestId, action, reason = '') {
      this.loading = true;
      this.error = null;
      requestId = parseInt(requestId);
      
      try {
        // 實際使用時改成真實API調用
        // const response = await axios.put(`/api/role-requests/${requestId}`, {
        //   action: action, // 'approve' 或 'reject'
        //   reason: reason
        // });
        
        // 模擬數據
        await new Promise(resolve => setTimeout(resolve, 500));
        
        // 更新本地狀態
        const index = this.roleRequests.findIndex(req => req.id === requestId);
        if (index !== -1) {
          this.roleRequests[index].status = action === 'approve' ? 'approved' : 'rejected';
          this.roleRequests[index].reason = reason;
          this.roleRequests[index].processedAt = new Date().toISOString();
        } else {
          throw new Error('找不到對應的角色請求');
        }
        
        // 如果批准，更新對應用戶的角色
        if (action === 'approve' && this.usersWithRoles.length > 0) {
          const request = this.roleRequests[index];
          const userIndex = this.usersWithRoles.findIndex(user => user.id === request.userId);
          if (userIndex !== -1 && request.requestedRole) {
            this.usersWithRoles[userIndex].role = {
              id: request.requestedRole.id,
              name: request.requestedRole.name,
              code: request.requestedRole.code
            };
          }
        }
        
        const toast = useToast();
        toast.success(action === 'approve' ? '角色請求已批准' : '角色請求已拒絕');
        
        return { 
          success: true, 
          message: action === 'approve' ? '角色請求已批准' : '角色請求已拒絕'
        };
      } catch (error) {
        this.error = error.message || '處理角色請求失敗';
        const toast = useToast();
        toast.error('處理角色請求失敗: ' + this.error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    // 獲取帶有角色信息的用戶列表
    async getUsersWithRoles() {
      this.loading = true;
      this.error = null;
      
      try {
        // 實際使用時改成真實API調用
        // const response = await axios.get('/api/users?include=roles');
        // this.usersWithRoles = response.data;
        // return this.usersWithRoles;
        
        // 模擬數據
        await new Promise(resolve => setTimeout(resolve, 700));
        
        const mockUsers = [
          {
            id: 1,
            username: '系統管理員',
            email: 'admin@example.com',
            phone: '0912-345-678',
            status: 'active',
            role: {
              id: 1,
              name: '超級管理員',
              code: 'super_admin'
            },
            createdAt: '2023-01-01T00:00:00'
          },
          {
            id: 2,
            username: '張小明',
            email: 'zhang@example.com',
            phone: '0923-456-789',
            status: 'active',
            role: {
              id: 2,
              name: '一般管理員',
              code: 'admin'
            },
            createdAt: '2023-03-15T10:20:30'
          },
          {
            id: 3,
            username: '李大華',
            email: 'li@example.com',
            phone: '0934-567-890',
            status: 'active',
            role: {
              id: 2,
              name: '一般管理員',
              code: 'admin'
            },
            createdAt: '2023-04-10T14:25:40'
          },
          {
            id: 4,
            username: '陳小雯',
            email: 'chen@example.com',
            phone: '0945-678-901',
            status: 'active',
            role: {
              id: 3,
              name: '營地管理員',
              code: 'camp_owner'
            },
            createdAt: '2023-02-20T09:15:25',
            campInfo: {
              name: '陽明山露營區',
              location: '台北市陽明山'
            }
          },
          {
            id: 5,
            username: '王小花',
            email: 'wang@example.com',
            phone: '0956-789-012',
            status: 'active',
            role: {
              id: 3,
              name: '營地管理員',
              code: 'camp_owner'
            },
            createdAt: '2023-03-05T11:30:45',
            campInfo: {
              name: '宜蘭森林露營區',
              location: '宜蘭縣冬山鄉'
            }
          },
          {
            id: 6,
            username: '林大山',
            email: 'lin@example.com',
            phone: '0967-890-123',
            status: 'active',
            role: {
              id: 4,
              name: '一般會員',
              code: 'user'
            },
            createdAt: '2023-05-12T16:40:20'
          },
          {
            id: 7,
            username: '吳小芳',
            email: 'wu@example.com',
            phone: '0978-901-234',
            status: 'active',
            role: {
              id: 4,
              name: '一般會員',
              code: 'user'
            },
            createdAt: '2023-06-18T08:50:15'
          }
        ];
        
        // 添加更多一般會員以模擬大量數據
        for(let i = 8; i <= 30; i++) {
          mockUsers.push({
            id: i,
            username: `會員${i}`,
            email: `user${i}@example.com`,
            phone: `09${Math.floor(10 + Math.random() * 89)}-${Math.floor(100 + Math.random() * 899)}-${Math.floor(100 + Math.random() * 899)}`,
            status: 'active',
            role: {
              id: 4,
              name: '一般會員',
              code: 'user'
            },
            createdAt: `2023-0${Math.floor(1 + Math.random() * 9)}-${Math.floor(10 + Math.random() * 20)}T${Math.floor(10 + Math.random() * 10)}:${Math.floor(10 + Math.random() * 49)}:${Math.floor(10 + Math.random() * 49)}`
          });
        }
        
        this.usersWithRoles = mockUsers;
        return mockUsers;
      } catch (error) {
        this.error = error.message || '載入用戶列表失敗';
        const toast = useToast();
        toast.error('載入用戶列表失敗: ' + this.error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    // 變更用戶角色
    async changeUserRole(userId, roleId) {
      this.loading = true;
      this.error = null;
      userId = parseInt(userId);
      roleId = parseInt(roleId);
      
      try {
        // 實際使用時改成真實API調用
        // const response = await axios.put(`/api/users/${userId}/role`, {
        //   roleId: roleId
        // });
        
        // 模擬數據
        await new Promise(resolve => setTimeout(resolve, 500));
        
        // 更新本地狀態
        const userIndex = this.usersWithRoles.findIndex(user => user.id === userId);
        const roleIndex = this.roles.findIndex(role => role.id === roleId);
        
        if (userIndex === -1) {
          throw new Error('找不到對應的用戶');
        }
        
        if (roleIndex === -1) {
          throw new Error('找不到對應的角色');
        }
        
        // 減少舊角色的用戶數
        const oldRoleId = this.usersWithRoles[userIndex].role?.id;
        if (oldRoleId) {
          const oldRoleIndex = this.roles.findIndex(role => role.id === oldRoleId);
          if (oldRoleIndex !== -1 && this.roles[oldRoleIndex].userCount > 0) {
            this.roles[oldRoleIndex].userCount -= 1;
          }
        }
        
        // 增加新角色的用戶數
        this.roles[roleIndex].userCount += 1;
        
        // 更新用戶的角色
        this.usersWithRoles[userIndex].role = {
          id: this.roles[roleIndex].id,
          name: this.roles[roleIndex].name,
          code: this.roles[roleIndex].code
        };
        
        const toast = useToast();
        toast.success('用戶角色更改成功');
        
        return { 
          success: true, 
          message: '用戶角色更改成功'
        };
      } catch (error) {
        this.error = error.message || '更改用戶角色失敗';
        const toast = useToast();
        toast.error('更改用戶角色失敗: ' + this.error);
        throw error;
      } finally {
        this.loading = false;
      }
    }
  }
});