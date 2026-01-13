<template>
  <div class="system-logs">
    <div class="page-header">
      <h1>系統日誌記錄</h1>
      <div class="header-actions">
        <button class="btn-export" @click="exportLogs">
          <i class="fa fa-download"></i> 匯出記錄
        </button>
      </div>
    </div>

    <!-- 過濾器 -->
    <div class="filter-panel">
      <div class="filter-form">
        <div class="form-row">
          <div class="form-group">
            <label>日誌類型</label>
            <select v-model="filters.logType" class="select-control">
              <option value="all">所有類型</option>
              <option value="login">登入記錄</option>
              <option value="action">操作記錄</option>
            </select>
          </div>
          
          <div class="form-group">
            <label>使用者</label>
            <input 
              type="text" 
              v-model="filters.username" 
              placeholder="輸入使用者名稱"
              class="text-control"
            />
          </div>
          
          <div class="form-group" v-if="filters.logType === 'action' || filters.logType === 'all'">
            <label>操作類型</label>
            <select v-model="filters.actionType" class="select-control">
              <option value="">全部</option>
              <option value="LOGIN">登入</option>
              <option value="LOGOUT">登出</option>
              <option value="CREATE">建立</option>
              <option value="UPDATE">更新</option>
              <option value="DELETE">刪除</option>
              <option value="APPROVE">核准</option>
              <option value="REJECT">拒絕</option>
            </select>
          </div>
          
          <div class="form-group">
            <label>日期範圍</label>
            <div class="date-range">
              <input 
                type="date" 
                v-model="filters.startDate" 
                placeholder="開始日期"
                class="date-control"
              />
              <span class="date-separator">至</span>
              <input 
                type="date" 
                v-model="filters.endDate" 
                placeholder="結束日期"
                class="date-control"
              />
            </div>
          </div>
        </div>
        
        <div class="form-actions">
          <button class="btn-primary" @click="applyFilters">
            <i class="fa fa-search"></i> 搜尋
          </button>
          <button class="btn-secondary" @click="resetFilters">
            <i class="fa fa-redo"></i> 重置
          </button>
        </div>
      </div>
    </div>

    <!-- 登入記錄表格 -->
    <div v-if="filters.logType === 'login' || filters.logType === 'all'" class="logs-section">
      <h2>登入記錄</h2>
      <div class="table-container">
        <table class="logs-table" v-if="!loadingLogin && loginLogs.length > 0">
          <thead>
            <tr>
              <th>ID</th>
              <th>使用者</th>
              <th>登入時間</th>
              <th>IP位址</th>
              <th>裝置資訊</th>
              <th>狀態</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="log in loginLogs" :key="'login-' + log.id">
              <td>{{ log.id }}</td>
              <td>{{ log.username }}</td>
              <td>{{ formatDate(log.login_time) }}</td>
              <td>{{ log.login_ip }}</td>
              <td>{{ log.login_device }}</td>
              <td>
                <span :class="['status-badge', log.login_status === 1 ? 'status-success' : 'status-failed']">
                  {{ log.login_status === 1 ? '成功' : '失敗' }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
        
        <!-- 登入記錄分頁 -->
        <div v-if="!loadingLogin && loginLogs.length > 0" class="pagination">
          <button 
            :disabled="loginPagination.currentPage <= 1" 
            @click="changePage('login', loginPagination.currentPage - 1)"
            class="pagination-btn"
          >
            <i class="fa fa-chevron-left"></i>
          </button>
          
          <span class="page-info">
            {{ loginPagination.currentPage }} / {{ loginPagination.totalPages }}
          </span>
          
          <button 
            :disabled="loginPagination.currentPage >= loginPagination.totalPages" 
            @click="changePage('login', loginPagination.currentPage + 1)"
            class="pagination-btn"
          >
            <i class="fa fa-chevron-right"></i>
          </button>
        </div>
        
        <!-- 載入中 -->
        <div v-if="loadingLogin" class="loading-state">
          <i class="fa fa-spinner fa-spin"></i> 載入中...
        </div>
        
        <!-- 無資料 -->
        <div v-if="!loadingLogin && loginLogs.length === 0" class="empty-state">
          <i class="fa fa-search"></i>
          <p>沒有找到登入記錄</p>
        </div>
      </div>
    </div>

    <!-- 操作記錄表格 -->
    <div v-if="filters.logType === 'action' || filters.logType === 'all'" class="logs-section">
      <h2>操作記錄</h2>
      <div class="table-container">
        <table class="logs-table" v-if="!loadingAction && actionLogs.length > 0">
          <thead>
            <tr>
              <th>ID</th>
              <th>使用者</th>
              <th>操作類型</th>
              <th>目標類型</th>
              <th>目標ID</th>
              <th>詳細資訊</th>
              <th>操作時間</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="log in actionLogs" :key="'action-' + log.id">
              <td>{{ log.id }}</td>
              <td>{{ log.username }}</td>
              <td>
                <span class="action-type" :class="getActionTypeClass(log.action_type)">
                  {{ log.action_type }}
                </span>
              </td>
              <td>{{ log.target_type }}</td>
              <td>{{ log.target_id }}</td>
              <td>
                <button v-if="log.details" class="btn-view-details" @click="viewLogDetails(log)">
                  <i class="fa fa-eye"></i> 查看
                </button>
                <span v-else>-</span>
              </td>
              <td>{{ formatDate(log.created_at) }}</td>
            </tr>
          </tbody>
        </table>
        
        <!-- 操作記錄分頁 -->
        <div v-if="!loadingAction && actionLogs.length > 0" class="pagination">
          <button 
            :disabled="actionPagination.currentPage <= 1" 
            @click="changePage('action', actionPagination.currentPage - 1)"
            class="pagination-btn"
          >
            <i class="fa fa-chevron-left"></i>
          </button>
          
          <span class="page-info">
            {{ actionPagination.currentPage }} / {{ actionPagination.totalPages }}
          </span>
          
          <button 
            :disabled="actionPagination.currentPage >= actionPagination.totalPages" 
            @click="changePage('action', actionPagination.currentPage + 1)"
            class="pagination-btn"
          >
            <i class="fa fa-chevron-right"></i>
          </button>
        </div>
        
        <!-- 載入中 -->
        <div v-if="loadingAction" class="loading-state">
          <i class="fa fa-spinner fa-spin"></i> 載入中...
        </div>
        
        <!-- 無資料 -->
        <div v-if="!loadingAction && actionLogs.length === 0" class="empty-state">
          <i class="fa fa-search"></i>
          <p>沒有找到操作記錄</p>
        </div>
      </div>
    </div>
    
    <!-- 日誌詳情對話框 -->
    <div class="modal" v-if="showLogDetailsModal">
      <div class="modal-content">
        <div class="modal-header">
          <h2>操作詳情</h2>
          <button class="btn-close" @click="showLogDetailsModal = false">
            <i class="fa fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div v-if="selectedLog" class="log-details">
            <div class="log-detail-item">
              <strong>操作者：</strong> {{ selectedLog.username }}
            </div>
            <div class="log-detail-item">
              <strong>操作類型：</strong> 
              <span class="action-type" :class="getActionTypeClass(selectedLog.action_type)">
                {{ selectedLog.action_type }}
              </span>
            </div>
            <div class="log-detail-item">
              <strong>目標類型：</strong> {{ selectedLog.target_type }}
            </div>
            <div class="log-detail-item">
              <strong>目標ID：</strong> {{ selectedLog.target_id }}
            </div>
            <div class="log-detail-item">
              <strong>操作時間：</strong> {{ formatDate(selectedLog.created_at) }}
            </div>
            <div class="log-detail-item">
              <strong>詳細資訊：</strong>
              <pre class="log-details-json">{{ formatLogDetails(selectedLog.details) }}</pre>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useLogStore } from '@/stores/log';
import { useToast } from '@/composables/useToast';

// 通知功能
const { showToast } = useToast();
const logStore = useLogStore();

// 狀態
const loginLogs = ref([]);
const actionLogs = ref([]);
const loadingLogin = ref(false);
const loadingAction = ref(false);
const error = ref(null);

// 分頁
const loginPagination = reactive({
  currentPage: 1,
  pageSize: 10,
  totalItems: 0,
  totalPages: computed(() => Math.ceil(loginPagination.totalItems / loginPagination.pageSize))
});

const actionPagination = reactive({
  currentPage: 1,
  pageSize: 10,
  totalItems: 0,
  totalPages: computed(() => Math.ceil(actionPagination.totalItems / actionPagination.pageSize))
});

// 過濾參數
const filters = reactive({
  logType: 'all',
  username: '',
  actionType: '',
  startDate: '',
  endDate: ''
});

// 日誌詳情模態框
const showLogDetailsModal = ref(false);
const selectedLog = ref(null);

// 載入登入日誌
const loadLoginLogs = async () => {
  if (filters.logType !== 'login' && filters.logType !== 'all') return;
  
  loadingLogin.value = true;
  error.value = null;
  
  try {
    // 建立查詢參數
    const params = {
      page: loginPagination.currentPage,
      limit: loginPagination.pageSize
    };
    
    if (filters.username) {
      params.username = filters.username;
    }
    
    if (filters.startDate) {
      params.start_date = filters.startDate;
    }
    
    if (filters.endDate) {
      params.end_date = filters.endDate;
    }
    
    const response = await logStore.getUserLoginHistory(params);
    loginLogs.value = response.logins || [];
    loginPagination.totalItems = response.total || 0;
  } catch (err) {
    error.value = err.message || '載入登入記錄失敗';
    showToast(error.value, 'error');
  } finally {
    loadingLogin.value = false;
  }
};

// 載入操作日誌
const loadActionLogs = async () => {
  if (filters.logType !== 'action' && filters.logType !== 'all') return;
  
  loadingAction.value = true;
  error.value = null;
  
  try {
    // 建立查詢參數
    const params = {
      page: actionPagination.currentPage,
      limit: actionPagination.pageSize
    };
    
    if (filters.username) {
      params.username = filters.username;
    }
    
    if (filters.actionType) {
      params.action_type = filters.actionType;
    }
    
    if (filters.startDate) {
      params.start_date = filters.startDate;
    }
    
    if (filters.endDate) {
      params.end_date = filters.endDate;
    }
    
    const response = await logStore.getUserActionLogs(params);
    actionLogs.value = response.logs || [];
    actionPagination.totalItems = response.total || 0;
  } catch (err) {
    error.value = err.message || '載入操作記錄失敗';
    showToast(error.value, 'error');
  } finally {
    loadingAction.value = false;
  }
};

// 切換頁碼
const changePage = (logType, page) => {
  if (logType === 'login') {
    loginPagination.currentPage = page;
    loadLoginLogs();
  } else if (logType === 'action') {
    actionPagination.currentPage = page;
    loadActionLogs();
  }
};

// 應用過濾器
const applyFilters = () => {
  // 重置頁碼
  loginPagination.currentPage = 1;
  actionPagination.currentPage = 1;
  
  // 重新載入數據
  if (filters.logType === 'login' || filters.logType === 'all') {
    loadLoginLogs();
  }
  
  if (filters.logType === 'action' || filters.logType === 'all') {
    loadActionLogs();
  }
};

// 重置過濾器
const resetFilters = () => {
  filters.logType = 'all';
  filters.username = '';
  filters.actionType = '';
  filters.startDate = '';
  filters.endDate = '';
  
  applyFilters();
};

// 匯出日誌
const exportLogs = async () => {
  try {
    await logStore.exportLogs(filters);
    showToast('日誌匯出成功，檔案已開始下載', 'success');
  } catch (err) {
    showToast(err.message || '匯出日誌失敗', 'error');
  }
};

// 查看日誌詳情
const viewLogDetails = (log) => {
  selectedLog.value = log;
  showLogDetailsModal.value = true;
};

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleString('zh-TW');
};

// 獲取操作類型CSS類
const getActionTypeClass = (actionType) => {
  const classMap = {
    'LOGIN': 'action-login',
    'LOGOUT': 'action-logout',
    'CREATE': 'action-create',
    'UPDATE': 'action-update',
    'DELETE': 'action-delete',
    'APPROVE': 'action-approve',
    'REJECT': 'action-reject'
  };
  
  return classMap[actionType] || '';
};

// 格式化日誌詳情
const formatLogDetails = (details) => {
  if (!details) return '';
  
  try {
    // 如果是JSON字串，格式化它
    if (typeof details === 'string') {
      const parsed = JSON.parse(details);
      return JSON.stringify(parsed, null, 2);
    }
    
    // 如果已經是對象，直接格式化
    return JSON.stringify(details, null, 2);
  } catch (err) {
    // 如果不是JSON，直接返回原始字串
    return details;
  }
};

// 初始化
onMounted(() => {
  loadLoginLogs();
  loadActionLogs();
});
</script>

<style scoped>
.system-logs {
  padding: 20px;
  width: 100%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0;
  color: var(--gray-800);
  font-size: 24px;
  font-weight: 700;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.btn-export {
  background-color: var(--tiffany-blue);
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

.btn-export:hover {
  background-color: var(--tiffany-blue-dark);
}

/* 過濾面板 */
.filter-panel {
  background-color: var(--white);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: var(--shadow);
}

.filter-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-row {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.form-group {
  flex: 1;
  min-width: 200px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: var(--gray-600);
  font-weight: 500;
  font-size: 14px;
}

.select-control,
.text-control,
.date-control {
  width: 100%;
  padding: 10px 15px;
  border: 1px solid var(--gray-200);
  border-radius: 10px;
  background-color: var(--white);
  font-size: 14px;
  color: var(--gray-700);
  transition: all 0.3s;
}

.select-control:focus,
.text-control:focus,
.date-control:focus {
  border-color: var(--tiffany-blue);
  box-shadow: 0 0 0 3px rgba(129, 216, 208, 0.1);
  outline: none;
}

.date-range {
  display: flex;
  align-items: center;
  gap: 10px;
}

.date-separator {
  color: var(--gray-500);
}

.form-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.btn-primary,
.btn-secondary {
  border: none;
  border-radius: 10px;
  padding: 10px 15px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.3s;
}

.btn-primary {
  background-color: var(--tiffany-blue);
  color: white;
}

.btn-primary:hover {
  background-color: var(--tiffany-blue-dark);
}

.btn-secondary {
  background-color: var(--gray-100);
  color: var(--gray-700);
}

.btn-secondary:hover {
  background-color: var(--gray-200);
}

/* 日誌部分 */
.logs-section {
  margin-bottom: 30px;
}

.logs-section h2 {
  font-size: 18px;
  color: var(--gray-800);
  margin-bottom: 16px;
  font-weight: 600;
}

.table-container {
  background-color: var(--white);
  border-radius: 12px;
  box-shadow: var(--shadow);
  overflow: hidden;
}

.logs-table {
  width: 100%;
  border-collapse: collapse;
}

.logs-table th,
.logs-table td {
  padding: 15px;
  text-align: left;
  border-bottom: 1px solid var(--gray-100);
}

.logs-table th {
  background-color: var(--gray-50);
  font-weight: 600;
  color: var(--gray-700);
  font-size: 14px;
}

.logs-table tr:hover {
  background-color: var(--tiffany-bg);
}

.status-badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-success {
  background-color: #dcfce7;
  color: #16a34a;
}

.status-failed {
  background-color: #fee2e2;
  color: #dc2626;
}

.action-type {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.action-login {
  background-color: #e0f2fe;
  color: #0284c7;
}

.action-logout {
  background-color: #f1f5f9;
  color: #475569;
}

.action-create {
  background-color: #dcfce7;
  color: #16a34a;
}

.action-update {
  background-color: #fef3c7;
  color: #d97706;
}

.action-delete {
  background-color: #fee2e2;
  color: #dc2626;
}

.action-approve {
  background-color: #dbeafe;
  color: #2563eb;
}

.action-reject {
  background-color: #fce7f3;
  color: #db2777;
}

.btn-view-details {
  background: none;
  border: none;
  color: var(--tiffany-blue);
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 4px 8px;
  border-radius: 6px;
  transition: all 0.2s;
}

.btn-view-details:hover {
  background-color: var(--tiffany-bg);
}

/* 分頁 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 15px;
  gap: 15px;
  border-top: 1px solid var(--gray-100);
}

.pagination-btn {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  border: 1px solid var(--gray-200);
  background-color: var(--white);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-btn:hover:not(:disabled) {
  border-color: var(--tiffany-blue);
  color: var(--tiffany-blue);
}

.page-info {
  color: var(--gray-600);
  font-size: 14px;
}

/* 載入和空狀態 */
.loading-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.loading-state i,
.empty-state i {
  font-size: 36px;
  color: var(--gray-300);
  margin-bottom: 15px;
}

.loading-state {
  color: var(--tiffany-blue);
}

.empty-state p {
  color: var(--gray-500);
  margin: 0;
}

/* 模態對話框 */
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
  background-color: var(--white);
  border-radius: 12px;
  width: 100%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: var(--shadow-lg);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid var(--gray-100);
}

.modal-header h2 {
  margin: 0;
  font-size: 18px;
  color: var(--gray-800);
  font-weight: 600;
}

.btn-close {
  background: none;
  border: none;
  cursor: pointer;
  color: var(--gray-400);
  font-size: 18px;
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.btn-close:hover {
  background-color: var(--gray-100);
  color: var(--gray-700);
}

.modal-body {
  padding: 20px;
}

.log-details {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.log-detail-item {
  display: flex;
  gap: 5px;
  align-items: center;
  flex-wrap: wrap;
}

.log-detail-item strong {
  color: var(--gray-700);
  min-width: 80px;
}

.log-details-json {
  background-color: var(--gray-50);
  padding: 15px;
  border-radius: 8px;
  overflow: auto;
  font-family: monospace;
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 300px;
  font-size: 13px;
  color: var(--gray-800);
  line-height: 1.6;
  margin-top: 5px;
  width: 100%;
}

/* 響應式調整 */
@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
    gap: 15px;
  }
  
  .form-group {
    width: 100%;
  }
  
  .logs-table {
    font-size: 14px;
  }
  
  .logs-table th,
  .logs-table td {
    padding: 10px;
  }
  
  .logs-table th:nth-child(1),
  .logs-table td:nth-child(1) {
    display: none;
  }
  
  .logs-table th:nth-child(4),
  .logs-table td:nth-child(4) {
    display: none;
  }
}

@media (max-width: 480px) {
  .date-range {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .logs-table th:nth-child(5),
  .logs-table td:nth-child(5) {
    display: none;
  }
}
</style>