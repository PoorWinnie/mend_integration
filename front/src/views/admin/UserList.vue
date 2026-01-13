<template>
  <div class="user-management-container">
    <div class="page-header">
      <h1>使用者管理</h1>
      <p>管理系統使用者資料與權限</p>
    </div>
    
    <!-- 搜索與過濾 -->
    <div class="filter-container">
      <div class="search-box">
        <input 
          type="text" 
          v-model="searchQuery" 
          placeholder="搜尋使用者名稱或電子郵件" 
          @input="handleSearch"
        />
        <button class="btn-search">
          <i class="fa fa-search"></i>
        </button>
      </div>
      
      <div class="filter-options">
        <select v-model="roleFilter" @change="handleFilter" class="select-filter">
          <option value="">所有角色</option>
          <option value="ADMIN">管理員</option>
          <option value="USER">露營者</option>
          <option value="CAMP_OWNER">營地主</option>
        </select>
        
        <select v-model="statusFilter" @change="handleFilter" class="select-filter">
          <option value="">所有狀態</option>
          <option value="active">啟用</option>
          <option value="inactive">停用</option>
          <option value="pending">待驗證</option>
        </select>
      </div>
      
      <button class="btn-add" @click="openCreateUserModal">
        <i class="fa fa-plus"></i> 新增使用者
      </button>
    </div>
    
    <!-- 使用者列表 -->
    <div class="user-table-container">
      <div v-if="loading" class="loading-container">
        <div class="loading-spinner">
          <i class="fa fa-spinner fa-spin"></i>
        </div>
        <span>載入中...</span>
      </div>
      
      <table v-else class="user-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>使用者名稱</th>
            <th>電子郵件</th>
            <th>角色</th>
            <th>註冊日期</th>
            <th>狀態</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in displayedUsers" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.email }}</td>
            <td>
              <span :class="getRoleClass(user.role)">{{ getRoleName(user.role) }}</span>
            </td>
            <td>{{ formatDate(user.createdAt) }}</td>
            <td>
              <span :class="getStatusClass(user.status)">{{ getStatusName(user.status) }}</span>
            </td>
            <td class="actions">
              <button class="btn-view" @click="viewUser(user.id)" title="查看詳情">
                <i class="fa fa-eye"></i>
              </button>
              <button class="btn-edit" @click="editUser(user.id)" title="編輯使用者">
                <i class="fa fa-edit"></i>
              </button>
              <button class="btn-delete" @click="deleteUser(user.id)" title="刪除使用者">
                <i class="fa fa-trash"></i>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
      
      <!-- 分頁控制 -->
      <div v-if="!loading && displayedUsers.length > 0" class="pagination">
        <button 
          :disabled="currentPage <= 1" 
          @click="changePage(currentPage - 1)" 
          class="pagination-btn"
        >
          <i class="fa fa-chevron-left"></i>
        </button>
        
        <span class="pagination-info">{{ currentPage }} / {{ totalPages }}</span>
        
        <button 
          :disabled="currentPage >= totalPages" 
          @click="changePage(currentPage + 1)" 
          class="pagination-btn"
        >
          <i class="fa fa-chevron-right"></i>
        </button>
      </div>
      
      <!-- 無數據顯示 -->
      <div v-if="!loading && displayedUsers.length === 0" class="empty-container">
        <div class="empty-icon">
          <i class="fa fa-users"></i>
        </div>
        <h3>沒有找到使用者</h3>
        <p>嘗試調整搜尋條件或添加新使用者</p>
        <button class="btn-primary" @click="openCreateUserModal">
          <i class="fa fa-plus"></i> 新增使用者
        </button>
      </div>
    </div>
    
    <!-- 使用者詳情模態框 -->
    <div class="modal" v-if="showUserDetailModal">
      <div class="modal-content">
        <div class="modal-header">
          <h2>使用者詳情</h2>
          <button class="btn-close" @click="showUserDetailModal = false">
            <i class="fa fa-times"></i>
          </button>
        </div>
        <div class="modal-body" v-if="selectedUser">
          <div class="user-details">
            <div class="user-avatar">
              <i class="fa fa-user"></i>
            </div>
            <div class="detail-item">
              <label>使用者ID</label>
              <span>{{ selectedUser.id }}</span>
            </div>
            <div class="detail-item">
              <label>使用者名稱</label>
              <span>{{ selectedUser.username }}</span>
            </div>
            <div class="detail-item">
              <label>電子郵件</label>
              <span>{{ selectedUser.email }}</span>
            </div>
            <div class="detail-item">
              <label>角色</label>
              <span :class="getRoleClass(selectedUser.role)">{{ getRoleName(selectedUser.role) }}</span>
            </div>
            <div class="detail-item">
              <label>狀態</label>
              <span :class="getStatusClass(selectedUser.status)">{{ getStatusName(selectedUser.status) }}</span>
            </div>
            <div class="detail-item">
              <label>註冊日期</label>
              <span>{{ formatDate(selectedUser.createdAt) }}</span>
            </div>
            <div class="detail-item">
              <label>最後登入</label>
              <span>{{ selectedUser.lastLogin ? formatDate(selectedUser.lastLogin) : '從未登入' }}</span>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn-secondary" @click="showUserDetailModal = false">關閉</button>
            <button class="btn-primary" @click="editUser(selectedUser.id)">編輯</button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 新增/編輯使用者模態框 -->
    <div class="modal" v-if="showUserFormModal">
      <div class="modal-content">
        <div class="modal-header">
          <h2>{{ isEditMode ? '編輯使用者' : '新增使用者' }}</h2>
          <button class="btn-close" @click="showUserFormModal = false">
            <i class="fa fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submitUserForm">
            <div class="form-group">
              <label for="username">使用者名稱</label>
              <input 
                type="text" 
                id="username" 
                v-model="userForm.username" 
                required
              />
            </div>
            <div class="form-group">
              <label for="email">電子郵件</label>
              <input 
                type="email" 
                id="email" 
                v-model="userForm.email" 
                required
              />
            </div>
            <div class="form-group" v-if="!isEditMode">
              <label for="password">密碼</label>
              <input 
                type="password" 
                id="password" 
                v-model="userForm.password" 
                required
              />
            </div>
            <div class="form-group">
              <label for="role">角色</label>
              <select id="role" v-model="userForm.role" required class="select-filter">
                <option value="ADMIN">管理員</option>
                <option value="USER">露營者</option>
                <option value="CAMP_OWNER">營地主</option>
              </select>
            </div>
            <div class="form-group">
              <label for="status">狀態</label>
              <select id="status" v-model="userForm.status" required class="select-filter">
                <option value="active">啟用</option>
                <option value="inactive">停用</option>
                <option value="pending">待驗證</option>
              </select>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn-secondary" @click="showUserFormModal = false">取消</button>
              <button type="submit" class="btn-primary">{{ isEditMode ? '儲存變更' : '建立使用者' }}</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue';

// Toast通知功能
const showToast = (message, type = 'info') => {
  // 簡單的alert模擬toast功能
  alert(`${type.toUpperCase()}: ${message}`);
};

// 使用者資料 - 模擬數據
const allUsers = ref([
  {
    id: 1,
    username: 'admin',
    email: 'admin@example.com',
    role: 'ADMIN',
    status: 'active',
    createdAt: '2025-05-01T00:00:00',
    lastLogin: '2025-05-15T10:30:00'
  },
  {
    id: 2,
    username: 'camper1',
    email: 'camper1@example.com',
    role: 'USER',
    status: 'active',
    createdAt: '2025-04-15T00:00:00',
    lastLogin: '2025-05-10T08:45:00'
  },
  {
    id: 3,
    username: 'owner1',
    email: 'owner1@example.com',
    role: 'CAMP_OWNER',
    status: 'active',
    createdAt: '2025-04-20T00:00:00',
    lastLogin: '2025-05-12T14:20:00'
  },
  {
    id: 4,
    username: 'camper2',
    email: 'camper2@example.com',
    role: 'USER',
    status: 'inactive',
    createdAt: '2025-03-10T00:00:00',
    lastLogin: '2025-04-05T16:20:00'
  },
  {
    id: 5,
    username: 'owner2',
    email: 'owner2@example.com',
    role: 'CAMP_OWNER',
    status: 'pending',
    createdAt: '2025-05-05T00:00:00',
    lastLogin: null
  },
  {
    id: 6,
    username: 'superadmin',
    email: 'superadmin@example.com',
    role: 'SUPER_ADMIN',
    status: 'active',
    createdAt: '2025-01-01T00:00:00',
    lastLogin: '2025-05-16T09:00:00'
  },
  {
    id: 7,
    username: 'camper3',
    email: 'camper3@example.com',
    role: 'USER',
    status: 'active',
    createdAt: '2025-02-20T00:00:00',
    lastLogin: '2025-04-25T11:30:00'
  },
  {
    id: 8,
    username: 'camper4',
    email: 'camper4@example.com',
    role: 'USER',
    status: 'pending',
    createdAt: '2025-05-10T00:00:00',
    lastLogin: null
  },
  {
    id: 9,
    username: 'owner3',
    email: 'owner3@example.com',
    role: 'CAMP_OWNER',
    status: 'active',
    createdAt: '2025-03-15T00:00:00',
    lastLogin: '2025-04-18T13:45:00'
  },
  {
    id: 10,
    username: 'owner4',
    email: 'owner4@example.com',
    role: 'CAMP_OWNER',
    status: 'inactive',
    createdAt: '2025-02-10T00:00:00',
    lastLogin: '2025-03-05T10:15:00'
  },
  {
    id: 11,
    username: 'admin2',
    email: 'admin2@example.com',
    role: 'ADMIN',
    status: 'active',
    createdAt: '2025-04-01T00:00:00',
    lastLogin: '2025-05-14T16:45:00'
  },
  {
    id: 12,
    username: 'camper5',
    email: 'camper5@example.com',
    role: 'USER',
    status: 'active',
    createdAt: '2025-04-25T00:00:00',
    lastLogin: '2025-05-13T09:20:00'
  }
]);

const users = ref([]);
const loading = ref(false);
const error = ref(null);

// 分頁
const currentPage = ref(1);
const pageSize = ref(5); // 設為較小值以便測試分頁
const totalItems = ref(0);
const totalPages = computed(() => Math.ceil(totalItems.value / pageSize.value));

// 搜尋和過濾
const searchQuery = ref('');
const roleFilter = ref('');
const statusFilter = ref('');

// 模態框狀態
const showUserDetailModal = ref(false);
const showUserFormModal = ref(false);
const isEditMode = ref(false);
const selectedUser = ref(null);

// 使用者表單
const userForm = reactive({
  id: null,
  username: '',
  email: '',
  password: '',
  role: 'USER',
  status: 'active'
});

// 過濾後的使用者列表
const filteredUsers = computed(() => {
  return allUsers.value.filter(user => {
    // 搜尋條件
    const matchesSearch = searchQuery.value === '' || 
      user.username.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      user.email.toLowerCase().includes(searchQuery.value.toLowerCase());
      
    // 角色過濾
    const matchesRole = roleFilter.value === '' || user.role === roleFilter.value;
    
    // 狀態過濾
    const matchesStatus = statusFilter.value === '' || user.status === statusFilter.value;
    
    return matchesSearch && matchesRole && matchesStatus;
  });
});

// 當前頁面顯示的用戶
const displayedUsers = computed(() => {
  const startIndex = (currentPage.value - 1) * pageSize.value;
  const endIndex = startIndex + pageSize.value;
  return filteredUsers.value.slice(startIndex, endIndex);
});

// 獲取角色名稱
const getRoleName = (role) => {
  const roles = {
    'ADMIN': '管理員',
    'SUPER_ADMIN': '超級管理員',
    'USER': '露營者',
    'CAMP_OWNER': '營地主'
  };
  return roles[role] || role;
};

// 獲取角色類別
const getRoleClass = (role) => {
  const roleClasses = {
    'ADMIN': 'role-admin',
    'SUPER_ADMIN': 'role-admin',
    'USER': 'role-camper',
    'CAMP_OWNER': 'role-owner'
  };
  return `badge ${roleClasses[role] || ''}`;
};

// 獲取狀態名稱
const getStatusName = (status) => {
  const statuses = {
    'active': '啟用',
    'inactive': '停用',
    'pending': '待驗證'
  };
  return statuses[status] || status;
};

// 獲取狀態類別
const getStatusClass = (status) => {
  return `badge status-${status}`;
};

// 處理搜索
const handleSearch = () => {
  currentPage.value = 1;
  updateTotalItems();
};

// 處理過濾
const handleFilter = () => {
  currentPage.value = 1;
  updateTotalItems();
};

// 切換頁碼
const changePage = (page) => {
  currentPage.value = page;
};

// 更新總項目數
const updateTotalItems = () => {
  totalItems.value = filteredUsers.value.length;
};

// 載入使用者列表 - 模擬API請求
const loadUsers = async () => {
  loading.value = true;
  try {
    // 模擬API延遲
    await new Promise(resolve => setTimeout(resolve, 500));
    
    // 設置總項目數
    updateTotalItems();
    
    showToast('用戶數據載入成功', 'success');
  } catch (err) {
    error.value = '載入使用者資料失敗';
    showToast(error.value, 'error');
  } finally {
    loading.value = false;
  }
};

// 查看用戶詳情
const viewUser = async (userId) => {
  loading.value = true;
  try {
    // 模擬API延遲
    await new Promise(resolve => setTimeout(resolve, 300));
    
    // 找到用戶
    const user = allUsers.value.find(u => u.id === userId);
    if (!user) {
      throw new Error('找不到該用戶');
    }
    
    selectedUser.value = user;
    showUserDetailModal.value = true;
  } catch (err) {
    showToast(err.message || '獲取使用者詳情失敗', 'error');
  } finally {
    loading.value = false;
  }
};

// 編輯用戶
const editUser = async (userId) => {
  loading.value = true;
  try {
    // 模擬API延遲
    await new Promise(resolve => setTimeout(resolve, 300));
    
    // 找到用戶
    const user = allUsers.value.find(u => u.id === userId);
    if (!user) {
      throw new Error('找不到該用戶');
    }
    
    // 複製用戶資料到表單
    Object.assign(userForm, {
      id: user.id,
      username: user.username,
      email: user.email,
      password: '', // 安全考慮，不帶入密碼
      role: user.role,
      status: user.status
    });
    
    isEditMode.value = true;
    showUserFormModal.value = true;
  } catch (err) {
    showToast(err.message || '獲取使用者資料失敗', 'error');
  } finally {
    loading.value = false;
  }
};

// 刪除用戶
const deleteUser = async (userId) => {
  if (!confirm('確定要刪除此使用者嗎？')) {
    return;
  }
  
  loading.value = true;
  try {
    // 模擬API延遲
    await new Promise(resolve => setTimeout(resolve, 500));
    
    // 找到用戶索引
    const userIndex = allUsers.value.findIndex(u => u.id === userId);
    if (userIndex === -1) {
      throw new Error('找不到該用戶');
    }
    
    // 從數組中移除
    allUsers.value.splice(userIndex, 1);
    updateTotalItems();
    
    // 如果當前頁沒有數據了，回到上一頁
    if (displayedUsers.value.length === 0 && currentPage.value > 1) {
      currentPage.value--;
    }
    
    showToast('使用者已刪除', 'success');
  } catch (err) {
    showToast(err.message || '刪除使用者失敗', 'error');
  } finally {
    loading.value = false;
  }
};

// 打開創建用戶模態窗口
const openCreateUserModal = () => {
  resetUserForm();
  isEditMode.value = false;
  showUserFormModal.value = true;
};

// 重置表單
const resetUserForm = () => {
  Object.assign(userForm, {
    id: null,
    username: '',
    email: '',
    password: '',
    role: 'USER',
    status: 'active'
  });
};

// 提交表單
const submitUserForm = async () => {
  loading.value = true;
  try {
    // 模擬API延遲
    await new Promise(resolve => setTimeout(resolve, 700));
    
    if (isEditMode.value) {
      // 更新用戶
      const userIndex = allUsers.value.findIndex(u => u.id === userForm.id);
      if (userIndex === -1) {
        throw new Error('找不到該用戶');
      }
      
      // 更新用戶資料
      allUsers.value[userIndex] = {
        ...allUsers.value[userIndex],
        username: userForm.username,
        email: userForm.email,
        role: userForm.role,
        status: userForm.status
      };
      
      showToast('使用者更新成功', 'success');
    } else {
      // 創建新用戶
      const newId = Math.max(...allUsers.value.map(u => u.id)) + 1;
      const now = new Date().toISOString();
      
      const newUser = {
        id: newId,
        username: userForm.username,
        email: userForm.email,
        role: userForm.role,
        status: userForm.status,
        createdAt: now,
        lastLogin: null
      };
      
      allUsers.value.push(newUser);
      updateTotalItems();
      
      showToast('使用者創建成功', 'success');
    }
    
    showUserFormModal.value = false;
  } catch (err) {
    showToast(err.message || '保存使用者失敗', 'error');
  } finally {
    loading.value = false;
  }
};

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return new Intl.DateTimeFormat('zh-TW', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date);
};

onMounted(() => {
  loadUsers();
});
</script>

<style scoped>
.user-management-container {
  width: 100%;
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  margin-bottom: 6px;
  color: var(--gray-800, #1F2937);
  font-size: 24px;
  font-weight: 700;
}

.page-header p {
  color: var(--gray-500, #6B7280);
  font-size: 15px;
}

.filter-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 15px;
}

.search-box {
  position: relative;
  width: 300px;
}

.search-box input {
  width: 100%;
  padding: 10px 40px 10px 15px;
  border: 1px solid var(--gray-200, #E5E7EB);
  border-radius: 10px;
  background-color: var(--white, #FFFFFF);
  font-size: 14px;
  transition: all 0.3s;
}

.search-box input:focus {
  border-color: var(--tiffany-blue, #81D8D0);
  box-shadow: 0 0 0 3px rgba(129, 216, 208, 0.1);
  outline: none;
}

.btn-search {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: var(--gray-400, #9CA3AF);
  cursor: pointer;
  font-size: 16px;
}

.btn-search:hover {
  color: var(--tiffany-blue, #81D8D0);
}

.filter-options {
  display: flex;
  gap: 10px;
}

.select-filter {
  padding: 10px 15px;
  border: 1px solid var(--gray-200, #E5E7EB);
  border-radius: 10px;
  background-color: var(--white, #FFFFFF);
  font-size: 14px;
  color: var(--gray-700, #374151);
  transition: all 0.3s;
  min-width: 120px;
}

.select-filter:focus {
  border-color: var(--tiffany-blue, #81D8D0);
  box-shadow: 0 0 0 3px rgba(129, 216, 208, 0.1);
  outline: none;
}

.btn-add {
  background-color: var(--tiffany-blue, #81D8D0);
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 10px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
  transition: all 0.3s;
}

.btn-add:hover {
  background-color: var(--tiffany-blue-dark, #66ACA6);
}

.user-table-container {
  background-color: var(--white, #FFFFFF);
  border-radius: 12px;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.user-table {
  width: 100%;
  border-collapse: collapse;
}

.user-table th,
.user-table td {
  padding: 15px;
  text-align: left;
  border-bottom: 1px solid var(--gray-100, #F3F4F6);
}

.user-table th {
  background-color: var(--gray-50, #F9FAFB);
  font-weight: 600;
  color: var(--gray-700, #374151);
  font-size: 14px;
}

.user-table tr:hover {
  background-color: var(--tiffany-bg, #F0FFFD);
}

.badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.role-admin {
  background-color: #fee2e2;
  color: #b91c1c;
}

.role-camper {
  background-color: #e0f2fe;
  color: #0369a1;
}

.role-owner {
  background-color: #f0fdf4;
  color: #16a34a;
}

.status-active {
  background-color: #ecfdf5;
  color: #16a34a;
}

.status-inactive {
  background-color: #f3f4f6;
  color: #6b7280;
}

.status-pending {
  background-color: #fff7ed;
  color: #c2410c;
}

.actions {
  display: flex;
  gap: 8px;
}

.actions button {
  width: 34px;
  height: 34px;
  border-radius: 8px;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-view {
  background-color: var(--tiffany-bg, #F0FFFD);
  color: var(--tiffany-blue, #81D8D0);
}

.btn-edit {
  background-color: #f0fdf4;
  color: #16a34a;
}

.btn-delete {
  background-color: #fee2e2;
  color: #b91c1c;
}

.btn-view:hover,
.btn-edit:hover,
.btn-delete:hover {
  filter: brightness(0.95);
  transform: translateY(-2px);
}

/* 分頁 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 15px;
  border-top: 1px solid var(--gray-100, #F3F4F6);
}

.pagination-btn {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  border: 1px solid var(--gray-200, #E5E7EB);
  background-color: var(--white, #FFFFFF);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.pagination-btn:hover:not(:disabled) {
  border-color: var(--tiffany-blue, #81D8D0);
  color: var(--tiffany-blue, #81D8D0);
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-info {
  margin: 0 15px;
  color: var(--gray-600, #4B5563);
  font-size: 14px;
}

/* 載入與空狀態 */
.loading-container,
.empty-container {
  padding: 60px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.loading-spinner {
  font-size: 36px;
  color: var(--tiffany-blue, #81D8D0);
  margin-bottom: 15px;
}

.empty-icon {
  font-size: 48px;
  color: var(--gray-300, #D1D5DB);
  margin-bottom: 15px;
}

.empty-container h3 {
  font-size: 18px;
  color: var(--gray-700, #374151);
  margin-bottom: 10px;
}

.empty-container p {
  color: var(--gray-500, #6B7280);
  margin-bottom: 20px;
}

/* 模態框 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  background-color: var(--white, #FFFFFF);
  border-radius: 12px;
  width: 100%;
  max-width: 550px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid var(--gray-100, #F3F4F6);
}

.modal-header h2 {
  margin: 0;
  font-size: 18px;
  color: var(--gray-800, #1F2937);
}

.btn-close {
  background: none;
  border: none;
  font-size: 18px;
  color: var(--gray-400, #9CA3AF);
  cursor: pointer;
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.btn-close:hover {
  background-color: var(--gray-100, #F3F4F6);
  color: var(--gray-700, #374151);
}

.modal-body {
  padding: 20px;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.user-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background-color: var(--tiffany-bg, #F0FFFD);
  color: var(--tiffany-blue, #81D8D0);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  margin: 0 auto 20px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.detail-item label {
  font-size: 14px;
  color: var(--gray-500, #6B7280);
}

.detail-item span {
  font-size: 16px;
  color: var(--gray-800, #1F2937);
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: var(--gray-700, #374151);
}

.form-group input {
  width: 100%;
  padding: 10px 15px;
  border: 1px solid var(--gray-200, #E5E7EB);
  border-radius: 10px;
  font-size: 14px;
  transition: all 0.3s;
}

.form-group input:focus {
  border-color: var(--tiffany-blue, #81D8D0);
  box-shadow: 0 0 0 3px rgba(129, 216, 208, 0.1);
  outline: none;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.btn-primary,
.btn-secondary {
  padding: 10px 20px;
  border-radius: 10px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
}

.btn-primary {
  background-color: var(--tiffany-blue, #81D8D0);
  color: white;
}

.btn-primary:hover {
  background-color: var(--tiffany-blue-dark, #66ACA6);
}

.btn-secondary {
  background-color: var(--gray-100, #F3F4F6);
  color: var(--gray-700, #374151);
}

.btn-secondary:hover {
  background-color: var(--gray-200, #E5E7EB);
}

@media (max-width: 768px) {
  .filter-container {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-box {
    width: 100%;
  }
  
  .filter-options {
    width: 100%;
  }
  
  .filter-options select {
    flex: 1;
  }
  
  .btn-add {
    width: 100%;
    justify-content: center;
  }
  
  .user-table th:nth-child(1),
  .user-table td:nth-child(1) {
    display: none;
  }
  
  .user-table th:nth-child(5),
  .user-table td:nth-child(5) {
    display: none;
  }
}

@media (max-width: 480px) {
  .user-table th:nth-child(3),
  .user-table td:nth-child(3) {
    display: none;
  }
  
  .actions {
    flex-direction: column;
    gap: 5px;
  }
  
  .user-avatar {
    width: 60px;
    height: 60px;
    font-size: 30px;
  }
}
</style>