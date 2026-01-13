<template>
    <div class="booking-management">
      <div class="page-header">
        <h1>預約管理</h1>
      </div>
      
      <!-- 搜索与筛选 -->
      <div class="filter-container">
        <div class="filter-row">
          <div class="search-box">
            <input 
              type="text" 
              v-model="searchQuery" 
              placeholder="搜尋預約編號或使用者名稱" 
              @input="handleSearch"
            />
            <button class="btn-search">
              <i class="fa fa-search"></i>
            </button>
          </div>
          
          <div class="filter-options">
            <select v-model="statusFilter" @change="handleFilter" class="select-filter">
              <option value="">所有狀態</option>
              <option value="pending">待確認</option>
              <option value="confirmed">已確認</option>
              <option value="completed">已完成</option>
              <option value="cancelled">已取消</option>
            </select>
            
            <select v-model="campFilter" @change="handleFilter" class="select-filter">
              <option value="">所有露營地</option>
              <option v-for="camp in camps" :key="camp.id" :value="camp.id">
                {{ camp.name }}
              </option>
            </select>
          </div>
        </div>
        
        <div class="filter-row">
          <div class="date-range">
            <label>入營日期範圍:</label>
            <div class="date-inputs">
              <input 
                type="date" 
                v-model="dateRange.startDate" 
                @change="handleFilter"
              />
              <span>至</span>
              <input 
                type="date" 
                v-model="dateRange.endDate" 
                @change="handleFilter"
              />
            </div>
          </div>
          
          <div class="filter-actions">
            <button class="btn-reset" @click="resetFilters">
              <i class="fa fa-redo"></i> 重置
            </button>
            <button class="btn-export" @click="exportBookings">
              <i class="fa fa-file-export"></i> 匯出資料
            </button>
          </div>
        </div>
      </div>
      
      <!-- 预约列表 -->
      <div class="booking-list-container">
        <table class="booking-table" v-if="!loading && bookings.length > 0">
          <thead>
            <tr>
              <th>預約編號</th>
              <th>使用者</th>
              <th>露營地</th>
              <th>入營日期</th>
              <th>天數</th>
              <th>人數</th>
              <th>金額</th>
              <th>狀態</th>
              <th>建立時間</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="booking in bookings" :key="booking.id">
              <td>{{ booking.booking_number }}</td>
              <td>
                <div class="user-info">
                  <span class="username">{{ booking.username }}</span>
                  <span class="email">{{ booking.email }}</span>
                </div>
              </td>
              <td>{{ booking.camp_name }}</td>
              <td>{{ formatDate(booking.check_in_date) }}</td>
              <td>{{ booking.days }}</td>
              <td>{{ booking.guests }}</td>
              <td>{{ formatPrice(booking.total_price) }}</td>
              <td>
                <span :class="['status-badge', `status-${booking.status}`]">
                  {{ getStatusText(booking.status) }}
                </span>
              </td>
              <td>{{ formatDate(booking.created_at) }}</td>
              <td class="actions">
                <button class="btn-view" @click="viewBookingDetails(booking.id)" title="查看詳情">
                  <i class="fa fa-eye"></i>
                </button>
                
                <template v-if="booking.status === 'pending'">
                  <button class="btn-confirm" @click="confirmBooking(booking.id)" title="確認預約">
                    <i class="fa fa-check"></i>
                  </button>
                  <button class="btn-cancel" @click="cancelBooking(booking.id)" title="取消預約">
                    <i class="fa fa-times"></i>
                  </button>
                </template>
                
                <button v-if="booking.status === 'confirmed'" class="btn-complete" @click="completeBooking(booking.id)" title="完成預約">
                  <i class="fa fa-check-double"></i>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
        
        <!-- 加载中 -->
        <div v-if="loading" class="loading-container">
          <div class="loading-spinner">
            <i class="fa fa-spinner fa-spin"></i>
          </div>
          <span>載入中...</span>
        </div>
        
        <!-- 无数据显示 -->
        <div v-if="!loading && bookings.length === 0" class="empty-container">
          <div class="empty-icon">
            <i class="fa fa-calendar-check"></i>
          </div>
          <h3>沒有找到預約</h3>
          <p>嘗試調整搜尋條件以查看預約記錄</p>
        </div>
        
        <!-- 分页控制 -->
        <div v-if="!loading && bookings.length > 0" class="pagination">
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
      </div>
      
      <!-- 预约详情模态框 -->
      <div class="modal" v-if="showBookingDetailModal">
        <div class="modal-content">
          <div class="modal-header">
            <h2>預約詳情</h2>
            <button class="btn-close" @click="showBookingDetailModal = false">
              <i class="fa fa-times"></i>
            </button>
          </div>
          <div class="modal-body" v-if="selectedBooking">
            <div class="detail-section">
              <h3>預約資訊</h3>
              <div class="detail-grid">
                <div class="detail-item">
                  <div class="detail-label">預約編號</div>
                  <div class="detail-value">{{ selectedBooking.booking_number }}</div>
                </div>
                <div class="detail-item">
                  <div class="detail-label">狀態</div>
                  <div class="detail-value">
                    <span :class="['status-badge', `status-${selectedBooking.status}`]">
                      {{ getStatusText(selectedBooking.status) }}
                    </span>
                  </div>
                </div>
                <div class="detail-item">
                  <div class="detail-label">建立時間</div>
                  <div class="detail-value">{{ formatDateTime(selectedBooking.created_at) }}</div>
                </div>
                <div class="detail-item">
                  <div class="detail-label">總金額</div>
                  <div class="detail-value price">{{ formatPrice(selectedBooking.total_price) }}</div>
                </div>
              </div>
            </div>
            
            <div class="detail-section">
              <h3>入住資訊</h3>
              <div class="detail-grid">
                <div class="detail-item">
                  <div class="detail-label">入營日期</div>
                  <div class="detail-value">{{ formatDate(selectedBooking.check_in_date) }}</div>
                </div>
                <div class="detail-item">
                  <div class="detail-label">離營日期</div>
                  <div class="detail-value">{{ formatDate(selectedBooking.check_out_date) }}</div>
                </div>
                <div class="detail-item">
                  <div class="detail-label">天數</div>
                  <div class="detail-value">{{ selectedBooking.days }} 天</div>
                </div>
                <div class="detail-item">
                  <div class="detail-label">人數</div>
                  <div class="detail-value">{{ selectedBooking.guests }} 人</div>
                </div>
              </div>
            </div>
            
            <div class="detail-section">
              <h3>使用者資訊</h3>
              <div class="detail-grid">
                <div class="detail-item">
                  <div class="detail-label">姓名</div>
                  <div class="detail-value">{{ selectedBooking.username }}</div>
                </div>
                <div class="detail-item">
                  <div class="detail-label">電子郵件</div>
                  <div class="detail-value">{{ selectedBooking.email }}</div>
                </div>
                <div class="detail-item">
                  <div class="detail-label">電話</div>
                  <div class="detail-value">{{ selectedBooking.phone || '無' }}</div>
                </div>
                <div class="detail-item">
                  <div class="detail-label">備註</div>
                  <div class="detail-value">{{ selectedBooking.note || '無' }}</div>
                </div>
              </div>
            </div>
            
            <div class="detail-section">
              <h3>露營地資訊</h3>
              <div class="camp-card">
                <div class="camp-image" :style="{ backgroundImage: `url(${selectedBooking.camp_image})` }"></div>
                <div class="camp-info">
                  <h4>{{ selectedBooking.camp_name }}</h4>
                  <p class="camp-location">{{ selectedBooking.camp_location }}</p>
                  <div class="camp-price">{{ formatPrice(selectedBooking.camp_price) }} / 晚</div>
                </div>
              </div>
            </div>
            
            <div class="modal-footer">
              <button type="button" class="btn-secondary" @click="showBookingDetailModal = false">關閉</button>
              
              <template v-if="selectedBooking.status === 'pending'">
                <button type="button" class="btn-confirm" @click="confirmBooking(selectedBooking.id)">
                  <i class="fa fa-check"></i> 確認預約
                </button>
                <button type="button" class="btn-cancel" @click="cancelBooking(selectedBooking.id)">
                  <i class="fa fa-times"></i> 取消預約
                </button>
              </template>
              
              <button 
                v-if="selectedBooking.status === 'confirmed'" 
                type="button" 
                class="btn-complete" 
                @click="completeBooking(selectedBooking.id)"
              >
                <i class="fa fa-check-double"></i> 完成預約
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, computed, onMounted, reactive } from 'vue';
  import { useConfirm } from '@/composables/useConfirm';
  import { useToast } from '@/composables/useToast';
  import bookingApi from '@/api/bookingApi';
  import campApi from '@/api/campApi';
  
  const { confirm } = useConfirm();
  const { showToast } = useToast();
  
  // 数据状态
  const bookings = ref([]);
  const camps = ref([]);
  const loading = ref(false);
  const error = ref(null);
  
  // 分页
  const currentPage = ref(1);
  const pageSize = ref(10);
  const totalItems = ref(0);
  const totalPages = computed(() => Math.ceil(totalItems.value / pageSize.value));
  
  // 筛选
  const searchQuery = ref('');
  const statusFilter = ref('');
  const campFilter = ref('');
  const dateRange = reactive({
    startDate: '',
    endDate: ''
  });
  
  // 模态框状态
  const showBookingDetailModal = ref(false);
  const selectedBooking = ref(null);
  
  // 获取状态文本
  const getStatusText = (status) => {
    const statusMap = {
      'pending': '待確認',
      'confirmed': '已確認',
      'completed': '已完成',
      'cancelled': '已取消'
    };
    return statusMap[status] || status;
  };
  
  // 格式化日期
  const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-TW');
  };
  
  // 格式化日期时间
  const formatDateTime = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleString('zh-TW');
  };
  
  // 格式化价格
  const formatPrice = (price) => {
    return `NT$ ${Number(price).toLocaleString()}`;
  };
  
  // 处理搜索
  const handleSearch = () => {
    currentPage.value = 1;
    loadBookings();
  };
  
  // 处理筛选
  const handleFilter = () => {
    currentPage.value = 1;
    loadBookings();
  };
  
  // 重置筛选
  const resetFilters = () => {
    searchQuery.value = '';
    statusFilter.value = '';
    campFilter.value = '';
    dateRange.startDate = '';
    dateRange.endDate = '';
    currentPage.value = 1;
    loadBookings();
  };
  
  // 切换页码
  const changePage = (page) => {
    currentPage.value = page;
    loadBookings();
  };
  
  // 加载预约数据
  const loadBookings = async () => {
    loading.value = true;
    error.value = null;
    
    try {
      const response = await bookingApi.getBookings({
        page: currentPage.value,
        limit: pageSize.value,
        search: searchQuery.value,
        status: statusFilter.value,
        camp_id: campFilter.value,
        start_date: dateRange.startDate,
        end_date: dateRange.endDate
      });
      
      bookings.value = response.data.bookings;
      totalItems.value = response.data.total;
    } catch (err) {
      error.value = err.message || '載入預約資料失敗';
      showToast(error.value, 'error');
    } finally {
      loading.value = false;
    }
  };
  
  // 加载露营地列表
  const loadCamps = async () => {
    try {
      const response = await campApi.getAllCamps();
      camps.value = response.data || [];
    } catch (err) {
      showToast(err.message || '載入露營地資料失敗', 'error');
    }
  };
  
  // 查看预约详情
  const viewBookingDetails = async (bookingId) => {
    loading.value = true;
    
    try {
      const response = await bookingApi.getBookingById(bookingId);
      selectedBooking.value = response.data;
      showBookingDetailModal.value = true;
    } catch (err) {
      showToast(err.message || '獲取預約詳情失敗', 'error');
    } finally {
      loading.value = false;
    }
  };
  
  // 确认预约
  const confirmBooking = async (bookingId) => {
    const confirmed = await confirm({
      title: '確認預約',
      message: '確定要確認此預約嗎？',
      confirmText: '確認',
      cancelText: '取消',
      confirmVariant: 'primary'
    });
    
    if (!confirmed) return;
    
    try {
      await bookingApi.updateBookingStatus(bookingId, 'confirmed');
      showToast('預約已確認', 'success');
      
      // 刷新预约列表
      loadBookings();
      
      // 如果是在详情模态框中确认的，更新详情数据
      if (showBookingDetailModal.value && selectedBooking.value) {
        selectedBooking.value.status = 'confirmed';
      }
    } catch (err) {
      showToast(err.message || '確認預約失敗', 'error');
    }
  };
  
  // 取消预约
  const cancelBooking = async (bookingId) => {
    const confirmed = await confirm({
      title: '取消預約',
      message: '確定要取消此預約嗎？',
      confirmText: '取消預約',
      cancelText: '返回',
      confirmVariant: 'danger'
    });
    
    if (!confirmed) return;
    
    try {
      await bookingApi.updateBookingStatus(bookingId, 'cancelled');
      showToast('預約已取消', 'success');
      
      // 刷新预约列表
      loadBookings();
      
      // 如果是在详情模态框中取消的，更新详情数据
      if (showBookingDetailModal.value && selectedBooking.value) {
        selectedBooking.value.status = 'cancelled';
      }
    } catch (err) {
      showToast(err.message || '取消預約失敗', 'error');
    }
  };
  
  // 完成预约
  const completeBooking = async (bookingId) => {
    const confirmed = await confirm({
      title: '完成預約',
      message: '確定要將此預約標記為已完成嗎？',
      confirmText: '完成',
      cancelText: '取消',
      confirmVariant: 'primary'
    });
    
    if (!confirmed) return;
    
    try {
      await bookingApi.updateBookingStatus(bookingId, 'completed');
      showToast('預約已完成', 'success');
      
      // 刷新预约列表
      loadBookings();
      
      // 如果是在详情模态框中完成的，更新详情数据
      if (showBookingDetailModal.value && selectedBooking.value) {
        selectedBooking.value.status = 'completed';
      }
    } catch (err) {
      showToast(err.message || '完成預約失敗', 'error');
    }
  };
  
  // 导出预约数据
  const exportBookings = async () => {
    try {
      // 这里可以调用导出API或自行处理导出逻辑
      showToast('匯出功能開發中', 'info');
    } catch (err) {
      showToast(err.message || '匯出資料失敗', 'error');
    }
  };
  
  // 初始化
  onMounted(() => {
    Promise.all([loadBookings(), loadCamps()]);
  });
  </script>
  
  <style scoped>
  .booking-management {
    width: 100%;
    padding: 20px;
  }
  
  .page-header {
    margin-bottom: 24px;
  }
  
  .page-header h1 {
    margin: 0;
    color: var(--gray-800);
    font-size: 24px;
    font-weight: 700;
  }
  
  .filter-container {
    background-color: var(--white);
    border-radius: 12px;
    box-shadow: var(--shadow);
    padding: 20px;
    margin-bottom: 20px;
  }
  
  .filter-row {
    display: flex;
    justify-content: space-between;
    margin-bottom: 15px;
  }
  
  .filter-row:last-child {
    margin-bottom: 0;
  }
  
  .search-box {
    position: relative;
    width: 300px;
  }
  
  .search-box input {
    width: 100%;
    padding: 10px 40px 10px 15px;
    border: 1px solid var(--gray-200);
    border-radius: 10px;
    background-color: var(--white);
    font-size: 14px;
    transition: all 0.3s;
  }
  
  .search-box input:focus {
    border-color: var(--tiffany-blue);
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
    color: var(--gray-400);
    cursor: pointer;
    font-size: 16px;
  }
  
  .btn-search:hover {
    color: var(--tiffany-blue);
  }
  
  .filter-options {
    display: flex;
    gap: 10px;
  }
  
  .select-filter {
    padding: 10px 15px;
    border: 1px solid var(--gray-200);
    border-radius: 10px;
    background-color: var(--white);
    font-size: 14px;
    color: var(--gray-700);
    transition: all 0.3s;
    min-width: 150px;
  }
  
  .select-filter:focus {
    border-color: var(--tiffany-blue);
    box-shadow: 0 0 0 3px rgba(129, 216, 208, 0.1);
    outline: none;
  }
  
  .date-range {
    display: flex;
    align-items: center;
    gap: 10px;
  }
  
  .date-range label {
    font-size: 14px;
    color: var(--gray-700);
  }
  
  .date-inputs {
    display: flex;
    align-items: center;
    gap: 10px;
  }
  
  .date-inputs input {
    padding: 10px 15px;
    border: 1px solid var(--gray-200);
    border-radius: 10px;
    background-color: var(--white);
    font-size: 14px;
    transition: all 0.3s;
  }
  
  .date-inputs input:focus {
    border-color: var(--tiffany-blue);
    box-shadow: 0 0 0 3px rgba(129, 216, 208, 0.1);
    outline: none;
  }
  
  .filter-actions {
    display: flex;
    gap: 10px;
  }
  
  .btn-reset,
  .btn-export {
    padding: 10px 15px;
    border-radius: 10px;
    font-size: 14px;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 5px;
    transition: all 0.3s;
  }
  
  .btn-reset {
    background-color: var(--gray-100);
    color: var(--gray-700);
    border: none;
  }
  
  .btn-reset:hover {
    background-color: var(--gray-200);
  }
  
  .btn-export {
    background-color: var(--tiffany-bg);
    color: var(--tiffany-blue);
    border: 1px solid var(--tiffany-blue-light);
  }
  
  .btn-export:hover {
    background-color: var(--tiffany-blue-light);
    color: white;
  }
  
  .booking-list-container {
    background-color: var(--white);
    border-radius: 12px;
    box-shadow: var(--shadow);
    overflow: hidden;
  }
  
  .booking-table {
    width: 100%;
    border-collapse: collapse;
  }
  
  .booking-table th,
  .booking-table td {
    padding: 15px;
    text-align: left;
    border-bottom: 1px solid var(--gray-100);
  }
  
  .booking-table th {
    background-color: var(--gray-50);
    color: var(--gray-700);
    font-weight: 600;
  }
  
  .booking-table tr:hover td {
    background-color: var(--tiffany-bg);
  }
  
  .user-info {
    display: flex;
    flex-direction: column;
  }
  
  .username {
    font-weight: 500;
  }
  
  .email {
    font-size: 12px;
    color: var(--gray-500);
  }
  
  .status-badge {
    display: inline-block;
    padding: 4px 8px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 500;
  }
  
  .status-pending {
    background-color: #fef3c7;
    color: #d97706;
  }
  
  .status-confirmed {
    background-color: #e0f2fe;
    color: #0369a1;
  }
  
  .status-completed {
    background-color: #ecfdf5;
    color: #10b981;
  }
  
  .status-cancelled {
    background-color: #fee2e2;
    color: #ef4444;
  }
  
  .actions {
    display: flex;
    gap: 5px;
  }
  
  .actions button {
    width: 32px;
    height: 32px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    border: none;
    cursor: pointer;
    transition: all 0.2s;
  }
  
  .btn-view {
    background-color: var(--tiffany-bg);
    color: var(--tiffany-blue);
  }
  
  .btn-confirm {
    background-color: #e0f2fe;
    color: #0369a1;
  }
  
  .btn-cancel {
    background-color: #fee2e2;
    color: #ef4444;
  }
  
  .btn-complete {
    background-color: #ecfdf5;
    color: #10b981;
  }
  
  .actions button:hover {
    filter: brightness(0.95);
    transform: translateY(-2px);
  }
  
  /* 分页 */
  .pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 15px;
    gap: 10px;
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
  
  .pagination-btn:hover:not(:disabled) {
    border-color: var(--tiffany-blue);
    color: var(--tiffany-blue);
  }
  
  .pagination-btn:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
  
  .pagination-info {
    margin: 0 15px;
    color: var(--gray-600);
    font-size: 14px;
  }
  
  /* 载入与空状态 */
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
    color: var(--tiffany-blue);
    margin-bottom: 15px;
  }
  
  .empty-icon {
    font-size: 48px;
    color: var(--gray-300);
    margin-bottom: 15px;
  }
  
  .empty-container h3 {
    font-size: 18px;
    color: var(--gray-700);
    margin-bottom: 10px;
  }
  
  .empty-container p {
    color: var(--gray-500);
  }
  
  /* 模态框 */
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
    max-width: 700px;
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
  }
  
  .btn-close {
    background: none;
    border: none;
    font-size: 18px;
    color: var(--gray-400);
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
    background-color: var(--gray-100);
    color: var(--gray-700);
  }
  
  .modal-body {
    padding: 20px;
  }
  
  .detail-section {
    margin-bottom: 20px;
  }
  
  .detail-section h3 {
    font-size: 16px;
    color: var(--gray-800);
    margin-bottom: 15px;
    padding-bottom: 5px;
    border-bottom: 1px solid var(--gray-100);
  }
  
  .detail-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 15px;
  }
  
  .detail-item {
    display: flex;
    flex-direction: column;
  }
  
  .detail-label {
    font-size: 14px;
    color: var(--gray-500);
    margin-bottom: 5px;
  }
  
  .detail-value {
    font-size: 16px;
    color: var(--gray-800);
  }
  
  .detail-value.price {
    color: var(--tiffany-blue);
    font-weight: 500;
  }
  
  .camp-card {
    display: flex;
    gap: 15px;
    background-color: var(--gray-50);
    border-radius: 10px;
    overflow: hidden;
  }
  
  .camp-image {
    width: 100px;
    height: 100px;
    background-size: cover;
    background-position: center;
  }
  
  .camp-info {
    padding: 10px;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
  
  .camp-info h4 {
    margin: 0 0 5px 0;
    font-size: 16px;
    color: var(--gray-800);
  }
  
  .camp-location {
    font-size: 14px;
    color: var(--gray-500);
    margin-bottom: 5px;
  }
  
  .camp-price {
    font-size: 14px;
    color: var(--tiffany-blue);
    font-weight: 500;
  }
  
  .modal-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    margin-top: 20px;
  }
  
  .btn-secondary,
  .btn-confirm,
  .btn-cancel,
  .btn-complete {
    padding: 10px 20px;
    border-radius: 10px;
    font-weight: 500;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 8px;
    transition: all 0.3s;
    border: none;
  }
  
  .btn-secondary {
    background-color: var(--gray-100);
    color: var(--gray-700);
  }
  
  .btn-secondary:hover {
    background-color: var(--gray-200);
  }
  
  .btn-confirm {
    background-color: #e0f2fe;
    color: #0369a1;
  }
  
  .btn-confirm:hover {
    background-color: #bae6fd;
  }
  
  .btn-cancel {
    background-color: #fee2e2;
    color: #ef4444;
  }
  
  .btn-cancel:hover {
    background-color: #fecaca;
  }
  
  .btn-complete {
    background-color: #ecfdf5;
    color: #10b981;
  }
  
  .btn-complete:hover {
    background-color: #d1fae5;
  }
  
  @media (max-width: 768px) {
    .filter-row {
      flex-direction: column;
      gap: 15px;
    }
    
    .search-box {
      width: 100%;
    }
    
    .filter-options {
      width: 100%;
    }
    
    .date-range {
      flex-direction: column;
      align-items: flex-start;
    }
    
    .date-inputs {
      width: 100%;
    }
    
    .date-inputs input {
      flex: 1;
    }
    
    .filter-actions {
      width: 100%;
      justify-content: space-between;
    }
    
    .btn-reset,
    .btn-export {
      flex: 1;
      justify-content: center;
    }
    
    .camp-card {
      flex-direction: column;
    }
    
    .camp-image {
      width: 100%;
      height: 150px;
    }
  }
  </style>