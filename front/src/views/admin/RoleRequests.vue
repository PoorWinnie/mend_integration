<template>
  <div class="role-requests">
    <div class="page-header">
      <div class="header-content">
        <h1>角色申請審核</h1>
        <p class="header-description">管理用戶提交的角色變更申請</p>
      </div>
      
      <div class="header-stats">
        <div class="stat-card" :class="{ 'active': filters.status === 'PENDING' || filters.status === '' }" @click="quickFilter('PENDING')">
          <div class="stat-icon pending">
            <i class="fa fa-clock"></i>
          </div>
          <div class="stat-content">
            <span class="stat-value">{{ stats.pendingCount || 0 }}</span>
            <span class="stat-label">待審核</span>
          </div>
        </div>
        
        <div class="stat-card" :class="{ 'active': filters.status === 'APPROVED' }" @click="quickFilter('APPROVED')">
          <div class="stat-icon approved">
            <i class="fa fa-check-circle"></i>
          </div>
          <div class="stat-content">
            <span class="stat-value">{{ stats.approvedCount || 0 }}</span>
            <span class="stat-label">已核准</span>
          </div>
        </div>
        
        <div class="stat-card" :class="{ 'active': filters.status === 'REJECTED' }" @click="quickFilter('REJECTED')">
          <div class="stat-icon rejected">
            <i class="fa fa-times-circle"></i>
          </div>
          <div class="stat-content">
            <span class="stat-value">{{ stats.rejectedCount || 0 }}</span>
            <span class="stat-label">已拒絕</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 過濾器 -->
    <div class="filter-panel">
      <div class="panel-header">
        <h2><i class="fa fa-filter"></i> 進階篩選</h2>
        <button class="btn-toggle" @click="toggleFilterPanel">
          <i :class="showFilters ? 'fa fa-chevron-up' : 'fa fa-chevron-down'"></i>
        </button>
      </div>
      
      <div class="filter-form" v-if="showFilters">
        <div class="form-row">
          <div class="form-group">
            <label>申請角色</label>
            <div class="select-wrapper">
              <select v-model="filters.roleId">
                <option value="">全部角色</option>
                <option v-for="role in roles" :key="role.id" :value="role.id">
                  {{ role.name }}
                </option>
              </select>
              <i class="fa fa-chevron-down"></i>
            </div>
          </div>
          
          <div class="form-group">
            <label>申請人</label>
            <div class="input-icon-wrapper">
              <i class="fa fa-user"></i>
              <input 
                type="text" 
                v-model="filters.username" 
                placeholder="輸入申請人名稱"
              />
            </div>
          </div>
          
          <div class="form-group date-group">
            <label>申請日期</label>
            <div class="date-range">
              <div class="input-icon-wrapper">
                <i class="fa fa-calendar"></i>
                <input 
                  type="date" 
                  v-model="filters.startDate" 
                  placeholder="開始日期"
                />
              </div>
              <span class="date-separator">至</span>
              <div class="input-icon-wrapper">
                <i class="fa fa-calendar"></i>
                <input 
                  type="date" 
                  v-model="filters.endDate" 
                  placeholder="結束日期"
                />
              </div>
            </div>
          </div>
        </div>
        
        <div class="form-actions">
          <button class="btn-secondary" @click="resetFilters">
            <i class="fa fa-redo"></i> 重置篩選
          </button>
          <button class="btn-primary" @click="applyFilters">
            <i class="fa fa-search"></i> 搜尋
          </button>
        </div>
      </div>
    </div>

    <!-- 申請列表卡片視圖 -->
    <div v-if="!loading && requests.length > 0" class="request-cards">
      <div 
        v-for="request in requests" 
        :key="request.id" 
        class="request-card"
        :class="getRequestCardClass(request)"
      >
        <div class="card-status-bar" :class="getStatusClass(request.status)"></div>
        <div class="card-header">
          <div class="user-avatar">
            <i class="fa fa-user"></i>
          </div>
          <div class="request-info">
            <div class="username">{{ request.username }}</div>
            <div class="email">{{ request.email }}</div>
          </div>
          <div class="request-status">
            <span class="status-badge" :class="getStatusClass(request.status)">
              {{ getStatusText(request.status) }}
            </span>
          </div>
        </div>
        
        <div class="card-body">
          <div class="detail-item">
            <div class="detail-label"><i class="fa fa-user-tag"></i> 申請角色</div>
            <div class="detail-value role-value">{{ getRoleName(request.requested_role_id) }}</div>
          </div>
          
          <div class="detail-item">
            <div class="detail-label"><i class="fa fa-calendar-alt"></i> 申請時間</div>
            <div class="detail-value">{{ formatDate(request.created_at) }}</div>
          </div>
          
          <div class="detail-item reason-item">
            <div class="detail-label"><i class="fa fa-comment-alt"></i> 申請理由</div>
            <div class="detail-value reason-value">
              <p>{{ request.request_reason || '無申請理由' }}</p>
            </div>
          </div>
          
          <div v-if="request.status !== 'PENDING'" class="detail-item">
            <div class="detail-label"><i class="fa fa-user-check"></i> 審核者</div>
            <div class="detail-value">{{ request.reviewer_name || '系統管理員' }}</div>
          </div>
          
          <div v-if="request.status !== 'PENDING'" class="detail-item">
            <div class="detail-label"><i class="fa fa-clock"></i> 審核時間</div>
            <div class="detail-value">{{ formatDate(request.reviewed_at) }}</div>
          </div>
          
          <div v-if="request.status === 'REJECTED'" class="detail-item reason-item">
            <div class="detail-label"><i class="fa fa-times-circle"></i> 拒絕理由</div>
            <div class="detail-value reason-value">
              <p>{{ request.reject_reason || '無拒絕理由' }}</p>
            </div>
          </div>
        </div>
        
        <div class="card-footer">
          <template v-if="request.status === 'PENDING'">
            <button class="btn-approve" @click="approveRequest(request.id)">
              <i class="fa fa-check"></i> 核准
            </button>
            <button class="btn-reject" @click="showRejectModal(request.id)">
              <i class="fa fa-times"></i> 拒絕
            </button>
          </template>
          <button v-else class="btn-view-details" @click="viewRequestDetails(request)">
            <i class="fa fa-eye"></i> 查看詳情
          </button>
        </div>
      </div>
    </div>
    
    <!-- 載入中 -->
    <div v-if="loading" class="loading-state">
      <div class="loading-spinner">
        <i class="fa fa-spinner fa-spin"></i>
      </div>
      <p>載入資料中，請稍候...</p>
    </div>
    
    <!-- 無數據 -->
    <div v-if="!loading && requests.length === 0" class="empty-state">
      <div class="empty-icon">
        <i class="fa fa-clipboard-list"></i>
      </div>
      <h3>暫無申請記錄</h3>
      <p v-if="hasActiveFilters">沒有符合當前篩選條件的申請記錄</p>
      <p v-else>目前沒有任何角色申請記錄</p>
      <button v-if="hasActiveFilters" class="btn-primary" @click="resetFilters">
        <i class="fa fa-redo"></i> 清除篩選條件
      </button>
    </div>
    
    <!-- 分頁控制 -->
    <div v-if="!loading && requests.length > 0" class="pagination">
      <button 
        class="btn-page"
        :disabled="pagination.currentPage <= 1" 
        @click="changePage(pagination.currentPage - 1)"
      >
        <i class="fa fa-chevron-left"></i>
      </button>
      
      <div class="page-numbers">
        <button 
          v-for="page in visiblePageNumbers" 
          :key="page" 
          class="btn-page-number" 
          :class="{ active: page === pagination.currentPage }"
          @click="changePage(page)"
        >
          {{ page }}
        </button>
      </div>
      
      <button 
        class="btn-page"
        :disabled="pagination.currentPage >= pagination.totalPages" 
        @click="changePage(pagination.currentPage + 1)"
      >
        <i class="fa fa-chevron-right"></i>
      </button>
    </div>
    
    <!-- 拒絕理由對話框 -->
    <div class="modal" v-if="showRejectReasonModal" @click.self="cancelReject">
      <div class="modal-content">
        <div class="modal-header">
          <h2><i class="fa fa-times-circle"></i> 拒絕申請</h2>
          <button class="btn-close" @click="cancelReject">
            <i class="fa fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label for="reject-reason">拒絕理由 <span class="required">*</span></label>
            <textarea 
              id="reject-reason" 
              v-model="rejectReason" 
              rows="4"
              placeholder="請填寫拒絕此申請的理由..."
            ></textarea>
            <small class="form-hint">請說明拒絕申請的具體原因，這將顯示給申請人。</small>
          </div>
          <div class="form-actions">
            <button type="button" class="btn-secondary" @click="cancelReject">
              取消
            </button>
            <button type="button" class="btn-danger" @click="rejectRequest">
              <i class="fa fa-times"></i> 確定拒絕
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 申請詳情對話框 -->
    <div class="modal" v-if="showDetailModal" @click.self="closeDetailModal">
      <div class="modal-content">
        <div class="modal-header" :class="selectedRequest ? getStatusClass(selectedRequest.status) : ''">
          <h2>
            <i :class="getStatusIcon(selectedRequest?.status)"></i> 
            申請詳情
          </h2>
          <button class="btn-close" @click="closeDetailModal">
            <i class="fa fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div v-if="selectedRequest" class="request-details">
            <div class="detail-section">
              <h3><i class="fa fa-user"></i> 申請人資訊</h3>
              <div class="detail-row">
                <div class="detail-label">申請人</div>
                <div class="detail-value bold">{{ selectedRequest.username }}</div>
              </div>
              <div class="detail-row">
                <div class="detail-label">電子郵件</div>
                <div class="detail-value">{{ selectedRequest.email }}</div>
              </div>
            </div>
            
            <div class="detail-section">
              <h3><i class="fa fa-clipboard-list"></i> 申請內容</h3>
              <div class="detail-row">
                <div class="detail-label">申請角色</div>
                <div class="detail-value bold">{{ getRoleName(selectedRequest.requested_role_id) }}</div>
              </div>
              <div class="detail-row">
                <div class="detail-label">申請理由</div>
                <div class="detail-value reason-box">{{ selectedRequest.request_reason || '無申請理由' }}</div>
              </div>
              <div class="detail-row">
                <div class="detail-label">申請時間</div>
                <div class="detail-value">{{ formatDate(selectedRequest.created_at) }}</div>
              </div>
            </div>
            
            <div class="detail-section">
              <h3><i class="fa fa-clipboard-check"></i> 申請狀態</h3>
              <div class="detail-row">
                <div class="detail-label">目前狀態</div>
                <div class="detail-value">
                  <span class="status-badge" :class="getStatusClass(selectedRequest.status)">
                    {{ getStatusText(selectedRequest.status) }}
                  </span>
                </div>
              </div>
              <template v-if="selectedRequest.status !== 'PENDING'">
                <div class="detail-row">
                  <div class="detail-label">審核者</div>
                  <div class="detail-value">{{ selectedRequest.reviewer_name || '系統管理員' }}</div>
                </div>
                <div class="detail-row">
                  <div class="detail-label">審核時間</div>
                  <div class="detail-value">{{ formatDate(selectedRequest.reviewed_at) }}</div>
                </div>
                <div v-if="selectedRequest.status === 'REJECTED'" class="detail-row">
                  <div class="detail-label">拒絕理由</div>
                  <div class="detail-value reason-box">{{ selectedRequest.reject_reason || '無拒絕理由' }}</div>
                </div>
              </template>
            </div>
            
            <div v-if="selectedRequest.status === 'PENDING'" class="detail-actions">
              <button class="btn-approve" @click="approveDetailRequest">
                <i class="fa fa-check"></i> 核准申請
              </button>
              <button class="btn-reject" @click="showRejectModalFromDetail">
                <i class="fa fa-times"></i> 拒絕申請
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue';
import { useToast } from '@/composables/useToast';
import { useAuthStore } from '@/stores/auth';
import { useRoleStore } from '@/stores/role'; // 引入角色管理 store

// 通知功能
const { showToast } = useToast();
const authStore = useAuthStore();
const roleStore = useRoleStore(); // 角色管理 store

// 獲取角色
const roles = ref([]);

// 資料狀態
const requests = ref([]);
const loading = ref(false);
const error = ref(null);
const showFilters = ref(false);

// 統計數據
const stats = reactive({
  pendingCount: 0,
  approvedCount: 0,
  rejectedCount: 0
});

// 分頁
const pagination = reactive({
  currentPage: 1,
  pageSize: 6, // 每頁顯示6條，更適合卡片佈局
  totalItems: 0,
  totalPages: computed(() => Math.ceil(pagination.totalItems / pagination.pageSize))
});

// 計算可見頁碼
const visiblePageNumbers = computed(() => {
  const totalPages = pagination.totalPages;
  const current = pagination.currentPage;
  
  if (totalPages <= 5) {
    return Array.from({ length: totalPages }, (_, i) => i + 1);
  }
  
  if (current <= 3) {
    return [1, 2, 3, 4, 5];
  }
  
  if (current >= totalPages - 2) {
    return [totalPages - 4, totalPages - 3, totalPages - 2, totalPages - 1, totalPages];
  }
  
  return [current - 2, current - 1, current, current + 1, current + 2];
});

// 過濾器
const filters = reactive({
  status: '',
  roleId: '',
  username: '',
  startDate: '',
  endDate: ''
});

// 檢查是否有啟用的過濾條件
const hasActiveFilters = computed(() => {
  return filters.status !== '' || 
         filters.roleId !== '' || 
         filters.username !== '' || 
         filters.startDate !== '' || 
         filters.endDate !== '';
});

// 模態對話框
const showRejectReasonModal = ref(false);
const showDetailModal = ref(false);
const rejectReason = ref('');
const selectedRequestId = ref(null);
const selectedRequest = ref(null);

// 使用本地存儲的申請數據
const localRequests = ref([
  {
    id: 1,
    username: '林小華',
    email: 'lin@example.com',
    requested_role_id: 3, // 營地主
    request_reason: '我擁有一個新的露營地，需要在平台上進行管理。位於新竹縣五峰鄉，佔地約 3000 坪，目前有 30 個營位可供使用。',
    status: 'PENDING',
    created_at: new Date(2025, 4, 9, 10, 30, 0).toISOString(),
    reviewer_name: null,
    reviewed_at: null,
    reject_reason: null
  },
  {
    id: 2,
    username: '王大明',
    email: 'wang@example.com',
    requested_role_id: 3, // 營地主
    request_reason: '申請成為營地主，我的營地位於南投縣魚池鄉，已取得合法經營執照，希望能在平台上架。',
    status: 'PENDING',
    created_at: new Date(2025, 4, 10, 14, 15, 0).toISOString(),
    reviewer_name: null,
    reviewed_at: null,
    reject_reason: null
  }
]);

// 載入角色申請
const loadRoleRequests = async () => {
  loading.value = true;
  
  try {
    // 模擬網絡延遲
    await new Promise(resolve => setTimeout(resolve, 800));
    
    // 應用過濾器
    let filteredRequests = [...localRequests.value];
    
    // 按狀態過濾
    if (filters.status) {
      filteredRequests = filteredRequests.filter(req => req.status === filters.status);
    }
    
    // 按角色過濾
    if (filters.roleId) {
      filteredRequests = filteredRequests.filter(req => req.requested_role_id === parseInt(filters.roleId));
    }
    
    // 按用戶名過濾
    if (filters.username) {
      const searchLower = filters.username.toLowerCase();
      filteredRequests = filteredRequests.filter(req => 
        req.username.toLowerCase().includes(searchLower)
      );
    }
    
    // 按日期過濾
    if (filters.startDate) {
      const startDate = new Date(filters.startDate);
      filteredRequests = filteredRequests.filter(req => 
        new Date(req.created_at) >= startDate
      );
    }
    
    if (filters.endDate) {
      const endDate = new Date(filters.endDate);
      endDate.setHours(23, 59, 59, 999); // 設為當天結束時間
      filteredRequests = filteredRequests.filter(req => 
        new Date(req.created_at) <= endDate
      );
    }
    
    // 計算統計數據
    stats.pendingCount = localRequests.value.filter(req => req.status === 'PENDING').length;
    stats.approvedCount = localRequests.value.filter(req => req.status === 'APPROVED').length;
    stats.rejectedCount = localRequests.value.filter(req => req.status === 'REJECTED').length;
    
    // 分頁
    pagination.totalItems = filteredRequests.length;
    const start = (pagination.currentPage - 1) * pagination.pageSize;
    const end = start + pagination.pageSize;
    
    requests.value = filteredRequests.slice(start, end);
  } catch (error) {
    console.error('載入申請資料失敗:', error);
    showToast('載入申請資料失敗', 'error');
  } finally {
    loading.value = false;
  }
};

// 載入角色數據
const loadRoles = async () => {
  try {
    // 獲取角色管理中的角色數據
    const response = await roleStore.getRoles();
    roles.value = response.roles || [];
  } catch (error) {
    console.error('載入角色資料失敗:', error);
    showToast('載入角色資料失敗', 'error');
  }
};

// 獲取角色名稱
const getRoleName = (roleId) => {
  if (!roleId) return '未知角色';
  
  const role = roles.value.find(r => r.id === roleId);
  return role ? role.name : `角色 ${roleId}`;
};

// 快速篩選狀態
const quickFilter = (status) => {
  filters.status = status === filters.status ? '' : status;
  pagination.currentPage = 1;
  loadRoleRequests();
};

// 切換過濾面板
const toggleFilterPanel = () => {
  showFilters.value = !showFilters.value;
};

// 獲取狀態文字
const getStatusText = (status) => {
  const statusMap = {
    'PENDING': '待審核',
    'APPROVED': '已核准',
    'REJECTED': '已拒絕'
  };
  return statusMap[status] || status;
};

// 獲取狀態圖標
const getStatusIcon = (status) => {
  const iconMap = {
    'PENDING': 'fa fa-clock',
    'APPROVED': 'fa fa-check-circle',
    'REJECTED': 'fa fa-times-circle'
  };
  return iconMap[status] || 'fa fa-info-circle';
};

// 獲取狀態CSS類名
const getStatusClass = (status) => {
  const statusMap = {
    'PENDING': 'status-pending',
    'APPROVED': 'status-approved',
    'REJECTED': 'status-rejected'
  };
  return statusMap[status] || '';
};

// 獲取請求卡片的CSS類名
const getRequestCardClass = (request) => {
  return {
    'card-pending': request.status === 'PENDING',
    'card-approved': request.status === 'APPROVED',
    'card-rejected': request.status === 'REJECTED'
  };
};

// 應用過濾器
const applyFilters = () => {
  pagination.currentPage = 1;
  loadRoleRequests();
};

// 重置過濾器
const resetFilters = () => {
  filters.status = '';
  filters.roleId = '';
  filters.username = '';
  filters.startDate = '';
  filters.endDate = '';
  applyFilters();
};

// 切換頁碼
const changePage = (page) => {
  pagination.currentPage = page;
  loadRoleRequests();
};

// 核准申請
const approveRequest = async (requestId) => {
  if (!confirm('確定要核准此角色申請嗎？')) {
    return;
  }
  
  try {
    // 找到請求並更新狀態
    const requestIndex = localRequests.value.findIndex(req => req.id === requestId);
    
    if (requestIndex !== -1) {
      const request = localRequests.value[requestIndex];
      const requestedRoleId = request.requested_role_id;
      
      // 更新角色申請狀態
      localRequests.value[requestIndex] = {
        ...request,
        status: 'APPROVED',
        reviewer_name: '系統管理員',
        reviewed_at: new Date().toISOString()
      };
      
      // 根據用戶名從要更新的用戶
      // 在實際應用中，可能需要調用 API 更新用戶角色
      console.log(`用戶 ${request.username} 的角色已變更為 ${getRoleName(requestedRoleId)}`);
      
      // 重新載入資料
      await loadRoleRequests();
      
      showToast('已核准申請', 'success');
    } else {
      showToast('找不到對應的申請記錄', 'error');
    }
  } catch (error) {
    console.error('核准申請失敗:', error);
    showToast('核准申請失敗', 'error');
  }
};

// 從詳情視圖核准申請
const approveDetailRequest = async () => {
  if (!selectedRequest.value) return;
  
  await approveRequest(selectedRequest.value.id);
  closeDetailModal();
};

// 顯示拒絕對話框
const showRejectModal = (requestId) => {
  selectedRequestId.value = requestId;
  rejectReason.value = '';
  showRejectReasonModal.value = true;
};

// 從詳情視圖顯示拒絕對話框
const showRejectModalFromDetail = () => {
  if (!selectedRequest.value) return;
  
  selectedRequestId.value = selectedRequest.value.id;
  rejectReason.value = '';
  showDetailModal.value = false;
  showRejectReasonModal.value = true;
};

// 取消拒絕
const cancelReject = () => {
  showRejectReasonModal.value = false;
  // 如果是從詳情頁來的，返回詳情頁
  if (selectedRequest.value && selectedRequest.value.id === selectedRequestId.value) {
    showDetailModal.value = true;
  }
};

// 拒絕申請
const rejectRequest = async () => {
  if (!rejectReason.value.trim()) {
    showToast('請填寫拒絕理由', 'error');
    return;
  }
  
  try {
    // 找到請求並更新狀態
    const requestIndex = localRequests.value.findIndex(req => req.id === selectedRequestId.value);
    
    if (requestIndex !== -1) {
      // 更新角色申請狀態
      localRequests.value[requestIndex] = {
        ...localRequests.value[requestIndex],
        status: 'REJECTED',
        reviewer_name: '系統管理員',
        reviewed_at: new Date().toISOString(),
        reject_reason: rejectReason.value
      };
      
      // 重新載入資料
      await loadRoleRequests();
      
      showRejectReasonModal.value = false;
      showToast('已拒絕申請', 'success');
    } else {
      showToast('找不到對應的申請記錄', 'error');
    }
  } catch (error) {
    console.error('拒絕申請失敗:', error);
    showToast('拒絕申請失敗', 'error');
  }
};

// 查看申請詳情
const viewRequestDetails = (request) => {
  selectedRequest.value = { ...request };
  showDetailModal.value = true;
};

// 關閉詳情模態框
const closeDetailModal = () => {
  showDetailModal.value = false;
  selectedRequest.value = null;
};

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleString('zh-TW');
};

// 初始化
onMounted(async () => {
  // 並行載入數據
  await Promise.all([loadRoles(), loadRoleRequests()]);
});
</script>

<style scoped>
:root {
  --tiffany-blue: #81D8D0;
  --tiffany-blue-light: #A7E7E1;
  --tiffany-blue-dark: #5EBFB7;
  --tiffany-blue-bg: #F0FAFA;
  --pending-color: #f59e0b;
  --pending-bg: #fffbeb;
  --pending-light: #fef3c7;
  --approved-color: #10b981;
  --approved-bg: #ecfdf5;
  --approved-light: #d1fae5;
  --rejected-color: #ef4444;
  --rejected-bg: #fef2f2;
  --rejected-light: #fee2e2;
  --white: #ffffff;
  --gray-50: #f9fafb;
  --gray-100: #f3f4f6;
  --gray-200: #e5e7eb;
  --gray-300: #d1d5db;
  --gray-400: #9ca3af;
  --gray-500: #6b7280;
  --gray-600: #4b5563;
  --gray-700: #374151;
  --gray-800: #1f2937;
  --radius-sm: 6px;
  --radius: 10px;
  --radius-lg: 15px;
  --shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.05);
  --shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  --shadow-md: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
  --transition: all 0.3s ease;
}

.role-requests {
  padding: 30px;
  max-width: 1400px;
  margin: 0 auto;
}

/* 頁面標題 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 30px;
  flex-wrap: wrap;
  gap: 25px;
}

.header-content {
  flex: 1;
  min-width: 250px;
}

.page-header h1 {
  font-size: 32px;
  background: linear-gradient(135deg, var(--tiffany-blue-dark) 0%, var(--tiffany-blue) 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin: 0 0 8px 0;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.header-description {
  color: var(--gray-500);
  font-size: 16px;
  margin: 0;
}

.header-stats {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  border-radius: var(--radius);
  background-color: var(--white);
  box-shadow: var(--shadow-sm);
  min-width: 150px;
  cursor: pointer;
  transition: var(--transition);
  border: 1px solid transparent;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow);
}

.stat-card.active {
  border-color: currentColor;
  background-color: rgba(255, 255, 255, 0.9);
}

.stat-icon {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.stat-icon.pending {
  background-color: var(--pending-light);
  color: var(--pending-color);
}

.stat-icon.approved {
  background-color: var(--approved-light);
  color: var(--approved-color);
}

.stat-icon.rejected {
  background-color: var(--rejected-light);
  color: var(--rejected-color);
}

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  line-height: 1;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: var(--gray-500);
}

/* 過濾面板 */
.filter-panel {
  background-color: var(--white);
  border-radius: var(--radius-lg);
  padding: 0;
  margin-bottom: 30px;
  box-shadow: var(--shadow);
  overflow: hidden;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 25px;
  background: linear-gradient(135deg, var(--tiffany-blue) 0%, var(--tiffany-blue-dark) 100%);
  color: var(--white);
}

.panel-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
}

.btn-toggle {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  color: var(--white);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: var(--transition);
}

.btn-toggle:hover {
  background: rgba(255, 255, 255, 0.3);
}

.filter-form {
  padding: 25px;
}

.form-row {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 25px;
}

.form-group {
  flex: 1;
  min-width: 200px;
}

.form-group.date-group {
  flex: 2;
  min-width: 350px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: var(--gray-700);
  font-weight: 500;
  font-size: 15px;
}

.select-wrapper, 
.input-icon-wrapper {
  position: relative;
}

.select-wrapper select, 
.input-icon-wrapper input {
  width: 100%;
  padding: 10px 15px 10px 40px;
  border: 1px solid var(--gray-200);
  border-radius: var(--radius);
  background-color: var(--white);
  font-size: 15px;
  color: var(--gray-800);
  transition: var(--transition);
  appearance: none;
}

.select-wrapper select:focus, 
.input-icon-wrapper input:focus {
  border-color: var(--tiffany-blue);
  box-shadow: 0 0 0 3px rgba(129, 216, 208, 0.1);
  outline: none;
}

.select-wrapper i, 
.input-icon-wrapper i {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--gray-400);
  pointer-events: none;
}

.select-wrapper i.fa-chevron-down {
  left: auto;
  right: 15px;
}

.date-range {
  display: flex;
  align-items: center;
  gap: 10px;
}

.date-separator {
  color: var(--gray-500);
  font-weight: 500;
  flex-shrink: 0;
}

.date-range .input-icon-wrapper {
  flex: 1;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
}

/* 按鈕樣式 */
.btn-primary {
  background: linear-gradient(135deg, var(--tiffany-blue) 0%, var(--tiffany-blue-dark) 100%);
  color: var(--white);
  padding: 12px 20px;
  border: none;
  border-radius: var(--radius);
  font-weight: 600;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: var(--transition);
  box-shadow: 0 3px 6px rgba(129, 216, 208, 0.3);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(129, 216, 208, 0.4);
}

.btn-secondary {
  background-color: var(--gray-100);
  color: var(--gray-700);
  padding: 12px 20px;
  border: none;
  border-radius: var(--radius);
  font-weight: 600;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: var(--transition);
}

.btn-secondary:hover {
  background-color: var(--gray-200);
}

.btn-danger {
  background-color: var(--rejected-color);
  color: var(--white);
  padding: 12px 20px;
  border: none;
  border-radius: var(--radius);
  font-weight: 600;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: var(--transition);
  box-shadow: 0 3px 6px rgba(239, 68, 68, 0.2);
}

.btn-danger:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(239, 68, 68, 0.3);
}

/* 請求卡片 */
.request-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 25px;
  margin-bottom: 30px;
}

.request-card {
  background-color: var(--white);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow);
  overflow: hidden;
  transition: var(--transition);
  position: relative;
  border: 1px solid var(--gray-100);
}

.request-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-md);
}

.card-status-bar {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 5px;
}

.card-status-bar.status-pending {
  background: linear-gradient(90deg, var(--pending-color), #fbbf24);
}

.card-status-bar.status-approved {
  background: linear-gradient(90deg, var(--approved-color), #34d399);
}

.card-status-bar.status-rejected {
  background: linear-gradient(90deg, var(--rejected-color), #f87171);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  border-bottom: 1px solid var(--gray-100);
}

.user-avatar {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  background-color: var(--tiffany-blue-bg);
  color: var(--tiffany-blue-dark);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.request-info {
  flex: 1;
}

.username {
  font-weight: 600;
  color: var(--gray-800);
  font-size: 16px;
  margin-bottom: 3px;
}

.email {
  color: var(--gray-500);
  font-size: 14px;
}

.status-badge {
  display: inline-block;
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
}

.status-badge.status-pending {
  background-color: var(--pending-light);
  color: var(--pending-color);
}

.status-badge.status-approved {
  background-color: var(--approved-light);
  color: var(--approved-color);
}

.status-badge.status-rejected {
  background-color: var(--rejected-light);
  color: var(--rejected-color);
}

.card-body {
  padding: 20px;
}

.detail-item {
  margin-bottom: 15px;
}

.detail-item:last-child {
  margin-bottom: 0;
}

.detail-label {
  color: var(--gray-500);
  font-size: 14px;
  margin-bottom: 5px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.detail-label i {
  color: var(--tiffany-blue);
}

.detail-value {
  color: var(--gray-800);
  font-size: 15px;
}

.detail-value.role-value {
  font-weight: 600;
  color: var(--tiffany-blue-dark);
}

.reason-item {
  padding: 12px;
  background-color: var(--gray-50);
  border-radius: var(--radius);
  margin-top: 15px;
}

.reason-value {
  max-height: 80px;
  overflow-y: auto;
  font-size: 14px;
  line-height: 1.5;
}

.reason-value::-webkit-scrollbar {
  width: 4px;
}

.reason-value::-webkit-scrollbar-track {
  background: var(--gray-100);
}

.reason-value::-webkit-scrollbar-thumb {
  background: var(--gray-300);
  border-radius: 10px;
}

.reason-value p {
  margin: 0;
}

.card-footer {
  padding: 15px 20px;
  display: flex;
  gap: 10px;
  border-top: 1px solid var(--gray-100);
}

.btn-approve, .btn-reject, .btn-view-details {
  padding: 10px 15px;
  border: none;
  border-radius: var(--radius);
  font-weight: 500;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  transition: var(--transition);
  flex: 1;
  justify-content: center;
}

.btn-approve {
  background-color: var(--approved-bg);
  color: var(--approved-color);
}

.btn-approve:hover {
  background-color: var(--approved-light);
  transform: translateY(-2px);
}

.btn-reject {
  background-color: var(--rejected-bg);
  color: var(--rejected-color);
}

.btn-reject:hover {
  background-color: var(--rejected-light);
  transform: translateY(-2px);
}

.btn-view-details {
  background-color: var(--tiffany-blue-bg);
  color: var(--tiffany-blue-dark);
}

.btn-view-details:hover {
  background-color: var(--tiffany-blue-light);
  transform: translateY(-2px);
}

/* 分頁控制 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 30px;
  gap: 8px;
}

.btn-page {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: var(--white);
  border: 1px solid var(--gray-200);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: var(--transition);
  color: var(--gray-700);
}

.btn-page:hover:not(:disabled) {
  background-color: var(--tiffany-blue-bg);
  border-color: var(--tiffany-blue-light);
  color: var(--tiffany-blue-dark);
}

.btn-page:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  gap: 8px;
}

.btn-page-number {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: var(--white);
  border: 1px solid var(--gray-200);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: var(--transition);
  color: var(--gray-700);
  font-weight: 500;
}

.btn-page-number:hover {
  background-color: var(--tiffany-blue-bg);
  border-color: var(--tiffany-blue-light);
  color: var(--tiffany-blue-dark);
}

.btn-page-number.active {
  background: linear-gradient(135deg, var(--tiffany-blue) 0%, var(--tiffany-blue-dark) 100%);
  color: var(--white);
  border-color: var(--tiffany-blue);
  font-weight: 600;
}

/* 載入和空狀態 */
.loading-state, .empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 30px;
  text-align: center;
  background-color: var(--white);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow);
}

.loading-spinner {
  font-size: 50px;
  color: var(--tiffany-blue);
  margin-bottom: 20px;
  animation: spin 1.5s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-state p, .empty-state p {
  color: var(--gray-500);
  margin: 10px 0 0 0;
  font-size: 16px;
}

.empty-icon {
  font-size: 60px;
  color: var(--gray-300);
  margin-bottom: 15px;
}

.empty-state h3 {
  color: var(--gray-700);
  margin: 0;
  font-size: 20px;
}

.empty-state .btn-primary {
  margin-top: 20px;
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
  backdrop-filter: blur(5px);
}

.modal-content {
  background-color: var(--white);
  border-radius: var(--radius-lg);
  width: 90%;
  max-width: 550px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: var(--shadow-md);
}

.modal-content::-webkit-scrollbar {
  width: 6px;
}

.modal-content::-webkit-scrollbar-track {
  background: var(--gray-100);
  border-radius: 10px;
}

.modal-content::-webkit-scrollbar-thumb {
  background: var(--gray-300);
  border-radius: 10px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 25px;
  border-bottom: 1px solid var(--gray-200);
  background-color: var(--white);
  border-radius: var(--radius-lg) var(--radius-lg) 0 0;
}

.modal-header.status-pending {
  background-color: var(--pending-bg);
  color: var(--pending-color);
  border-bottom: none;
}

.modal-header.status-approved {
  background: linear-gradient(135deg, var(--approved-color), #34d399);
  color: var(--white);
  border-bottom: none;
}

.modal-header.status-rejected {
  background: linear-gradient(135deg, var(--rejected-color), #f87171);
  color: var(--white);
  border-bottom: none;
}

.modal-header h2 {
  font-size: 20px;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.btn-close {
  background: none;
  border: none;
  color: currentColor;
  cursor: pointer;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: var(--transition);
  font-size: 18px;
  opacity: 0.7;
}

.btn-close:hover {
  opacity: 1;
  background-color: rgba(0, 0, 0, 0.1);
}

.status-pending .btn-close:hover,
.status-approved .btn-close:hover,
.status-rejected .btn-close:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.modal-body {
  padding: 25px;
}

.form-group textarea {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid var(--gray-200);
  border-radius: var(--radius);
  background-color: var(--white);
  resize: vertical;
  min-height: 120px;
  font-size: 15px;
  transition: var(--transition);
}

.form-group textarea:focus {
  border-color: var(--tiffany-blue);
  box-shadow: 0 0 0 3px rgba(129, 216, 208, 0.1);
  outline: none;
}

.form-hint {
  display: block;
  color: var(--gray-500);
  font-size: 13px;
  margin-top: 6px;
}

.required {
  color: var(--rejected-color);
}

/* 詳情模態框 */
.request-details {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.detail-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-section h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--gray-700);
  margin: 0 0 8px 0;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--gray-200);
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-section h3 i {
  color: var(--tiffany-blue);
}

.detail-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 5px;
}

.detail-row .detail-label {
  width: 100px;
  flex-shrink: 0;
  margin-bottom: 0;
}

.detail-row .detail-value {
  flex: 1;
}

.detail-value.bold {
  font-weight: 600;
}

.detail-value.reason-box {
  background-color: var(--gray-50);
  padding: 12px;
  border-radius: var(--radius);
  line-height: 1.5;
  white-space: pre-line;
}

.detail-actions {
  display: flex;
  gap: 15px;
  margin-top: 10px;
}

.detail-actions .btn-approve,
.detail-actions .btn-reject {
  flex: 1;
  padding: 12px 15px;
}

/* 響應式調整 */
@media (max-width: 1024px) {
  .role-requests {
    padding: 20px;
  }
  
  .request-cards {
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  }
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .header-stats {
    width: 100%;
    justify-content: space-between;
  }
  
  .stat-card {
    min-width: 100px;
    padding: 10px;
  }
  
  .form-row {
    flex-direction: column;
    gap: 15px;
  }
  
  .date-range {
    flex-direction: column;
    gap: 15px;
  }
  
  .date-separator {
    display: none;
  }
  
  .request-cards {
    grid-template-columns: 1fr;
  }
  
  .detail-row {
    flex-direction: column;
    gap: 5px;
  }
  
  .detail-row .detail-label {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .form-actions {
    flex-direction: column;
  }
  
  .btn-primary, .btn-secondary, .btn-danger {
    width: 100%;
  }
  
  .page-numbers {
    display: none;
  }
  
  .pagination {
    justify-content: space-between;
  }
}
</style>