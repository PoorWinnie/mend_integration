<template>
    <div class="camp-management">
      <div class="page-header">
        <h1>露營地管理</h1>
        <button class="btn-primary" @click="openAddCampModal">
          <i class="fa fa-plus"></i> 新增露營地
        </button>
      </div>
      
      <!-- 搜索与筛选 -->
      <div class="filter-container">
        <div class="search-box">
          <input 
            type="text" 
            v-model="searchQuery" 
            placeholder="搜尋露營地名稱或地點" 
            @input="handleSearch"
          />
          <button class="btn-search">
            <i class="fa fa-search"></i>
          </button>
        </div>
        
        <div class="filter-options">
          <select v-model="regionFilter" @change="handleFilter" class="select-filter">
            <option value="">所有地區</option>
            <option value="north">北部</option>
            <option value="central">中部</option>
            <option value="south">南部</option>
            <option value="east">東部</option>
            <option value="islands">離島</option>
          </select>
          
          <select v-model="statusFilter" @change="handleFilter" class="select-filter">
            <option value="">所有狀態</option>
            <option value="active">上架中</option>
            <option value="inactive">已下架</option>
            <option value="pending">審核中</option>
          </select>
        </div>
      </div>
      
      <!-- 露营地列表 -->
      <div class="camp-grid" v-if="!loading && camps.length > 0">
        <div class="camp-card" v-for="camp in camps" :key="camp.id">
          <div class="camp-image" :style="{ backgroundImage: `url(${camp.image_url})` }">
            <span :class="['camp-status', `status-${camp.status}`]">
              {{ getStatusText(camp.status) }}
            </span>
          </div>
          <div class="camp-info">
            <h3>{{ camp.name }}</h3>
            <p class="camp-location">
              <i class="fa fa-map-marker-alt"></i> {{ camp.location }}
            </p>
            <div class="camp-meta">
              <div class="meta-item">
                <i class="fa fa-dollar-sign"></i> {{ formatPrice(camp.price) }} / 晚
              </div>
              <div class="meta-item">
                <i class="fa fa-star"></i> {{ camp.rating.toFixed(1) }}
                <span class="review-count">({{ camp.review_count }})</span>
              </div>
            </div>
            <div class="camp-features">
              <span v-if="camp.has_pool" class="feature-tag">泳池</span>
              <span v-if="camp.allows_pets" class="feature-tag">寵物友善</span>
              <span v-if="camp.has_wifi" class="feature-tag">WiFi</span>
              <span v-if="camp.allows_barbecue" class="feature-tag">烤肉</span>
            </div>
          </div>
          <div class="camp-actions">
            <button class="btn-view" @click="viewCampDetails(camp.id)" title="查看詳情">
              <i class="fa fa-eye"></i>
            </button>
            <button class="btn-edit" @click="editCamp(camp.id)" title="編輯">
              <i class="fa fa-edit"></i>
            </button>
            <button 
              class="btn-toggle-status" 
              @click="toggleCampStatus(camp.id, camp.status)" 
              :title="camp.status === 'active' ? '下架' : '上架'"
            >
              <i :class="['fa', camp.status === 'active' ? 'fa-toggle-on' : 'fa-toggle-off']"></i>
            </button>
            <button class="btn-delete" @click="deleteCamp(camp.id)" title="刪除">
              <i class="fa fa-trash"></i>
            </button>
          </div>
        </div>
      </div>
      
      <!-- 加载中 -->
      <div v-if="loading" class="loading-container">
        <div class="loading-spinner">
          <i class="fa fa-spinner fa-spin"></i>
        </div>
        <span>載入中...</span>
      </div>
      
      <!-- 无数据显示 -->
      <div v-if="!loading && camps.length === 0" class="empty-container">
        <div class="empty-icon">
          <i class="fa fa-campground"></i>
        </div>
        <h3>沒有找到露營地</h3>
        <p>嘗試調整搜尋條件或添加新露營地</p>
        <button class="btn-primary" @click="openAddCampModal">
          <i class="fa fa-plus"></i> 新增露營地
        </button>
      </div>
      
      <!-- 分页控制 -->
      <div v-if="!loading && camps.length > 0" class="pagination">
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
      
      <!-- 添加/编辑营地模态框 -->
      <div class="modal" v-if="showCampFormModal">
        <div class="modal-content">
          <div class="modal-header">
            <h2>{{ isEditMode ? '編輯露營地' : '新增露營地' }}</h2>
            <button class="btn-close" @click="showCampFormModal = false">
              <i class="fa fa-times"></i>
            </button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="submitCampForm">
              <div class="form-group">
                <label for="camp-name">露營地名稱 <span class="required">*</span></label>
                <input 
                  type="text" 
                  id="camp-name" 
                  v-model="campForm.name" 
                  required
                />
              </div>
              
              <div class="form-row">
                <div class="form-group">
                  <label for="camp-region">地區 <span class="required">*</span></label>
                  <select id="camp-region" v-model="campForm.region" required>
                    <option value="north">北部</option>
                    <option value="central">中部</option>
                    <option value="south">南部</option>
                    <option value="east">東部</option>
                    <option value="islands">離島</option>
                  </select>
                </div>
                
                <div class="form-group">
                  <label for="camp-price">價格 (每晚) <span class="required">*</span></label>
                  <input 
                    type="number" 
                    id="camp-price" 
                    v-model="campForm.price" 
                    min="0" 
                    required
                  />
                </div>
              </div>
              
              <div class="form-group">
                <label for="camp-location">詳細地址 <span class="required">*</span></label>
                <input 
                  type="text" 
                  id="camp-location" 
                  v-model="campForm.location" 
                  required
                />
              </div>
              
              <div class="form-group">
                <label for="camp-description">露營地描述 <span class="required">*</span></label>
                <textarea 
                  id="camp-description" 
                  v-model="campForm.description" 
                  rows="4" 
                  required
                ></textarea>
              </div>
              
              <div class="form-group">
                <label for="camp-image">主圖片URL <span class="required">*</span></label>
                <input 
                  type="text" 
                  id="camp-image" 
                  v-model="campForm.image_url" 
                  required
                />
              </div>
              
              <div class="form-group">
                <label>特色設施</label>
                <div class="features-checkboxes">
                  <div class="feature-checkbox">
                    <input type="checkbox" id="has-pool" v-model="campForm.has_pool" />
                    <label for="has-pool">游泳池</label>
                  </div>
                  <div class="feature-checkbox">
                    <input type="checkbox" id="allows-pets" v-model="campForm.allows_pets" />
                    <label for="allows-pets">寵物友善</label>
                  </div>
                  <div class="feature-checkbox">
                    <input type="checkbox" id="has-wifi" v-model="campForm.has_wifi" />
                    <label for="has-wifi">WiFi</label>
                  </div>
                  <div class="feature-checkbox">
                    <input type="checkbox" id="allows-barbecue" v-model="campForm.allows_barbecue" />
                    <label for="allows-barbecue">烤肉設施</label>
                  </div>
                  <div class="feature-checkbox">
                    <input type="checkbox" id="has-shower" v-model="campForm.has_shower" />
                    <label for="has-shower">淋浴設施</label>
                  </div>
                  <div class="feature-checkbox">
                    <input type="checkbox" id="has-toilet" v-model="campForm.has_toilet" />
                    <label for="has-toilet">廁所</label>
                  </div>
                  <div class="feature-checkbox">
                    <input type="checkbox" id="has-electricity" v-model="campForm.has_electricity" />
                    <label for="has-electricity">供電</label>
                  </div>
                  <div class="feature-checkbox">
                    <input type="checkbox" id="has-parking" v-model="campForm.has_parking" />
                    <label for="has-parking">停車場</label>
                  </div>
                </div>
              </div>
              
              <div class="form-group">
                <label for="camp-status">狀態</label>
                <select id="camp-status" v-model="campForm.status">
                  <option value="active">上架中</option>
                  <option value="inactive">已下架</option>
                  <option value="pending">審核中</option>
                </select>
              </div>
              
              <div class="modal-footer">
                <button type="button" class="btn-secondary" @click="showCampFormModal = false">取消</button>
                <button type="submit" class="btn-primary">{{ isEditMode ? '儲存變更' : '建立露營地' }}</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, computed, onMounted, reactive } from 'vue';
  import { useConfirm } from '@/composables/useConfirm';
  import { useToast } from '@/composables/useToast';
  import campApi from '@/api/campApi';
  
  const { confirm } = useConfirm();
  const { showToast } = useToast();
  
  // 数据状态
  const camps = ref([]);
  const loading = ref(false);
  const error = ref(null);
  
  // 分页
  const currentPage = ref(1);
  const pageSize = ref(9);
  const totalItems = ref(0);
  const totalPages = computed(() => Math.ceil(totalItems.value / pageSize.value));
  
  // 筛选
  const searchQuery = ref('');
  const regionFilter = ref('');
  const statusFilter = ref('');
  
  // 模态框状态
  const showCampFormModal = ref(false);
  const isEditMode = ref(false);
  
  // 营地表单
  const initialFormState = {
    id: null,
    name: '',
    region: 'north',
    location: '',
    description: '',
    price: 2000,
    image_url: '',
    has_pool: false,
    allows_pets: false,
    has_wifi: false,
    allows_barbecue: false,
    has_shower: true,
    has_toilet: true,
    has_electricity: true,
    has_parking: true,
    status: 'active'
  };
  
  const campForm = reactive({...initialFormState});
  
  // 获取状态文本
  const getStatusText = (status) => {
    const statusMap = {
      'active': '上架中',
      'inactive': '已下架',
      'pending': '審核中'
    };
    return statusMap[status] || status;
  };
  
  // 格式化价格
  const formatPrice = (price) => {
    return `NT$ ${price.toLocaleString()}`;
  };
  
  // 处理搜索
  const handleSearch = () => {
    currentPage.value = 1;
    loadCamps();
  };
  
  // 处理筛选
  const handleFilter = () => {
    currentPage.value = 1;
    loadCamps();
  };
  
  // 切换页码
  const changePage = (page) => {
    currentPage.value = page;
    loadCamps();
  };
  
  // 加载营地数据
  const loadCamps = async () => {
    loading.value = true;
    error.value = null;
    
    try {
      const response = await campApi.getCamps({
        page: currentPage.value,
        limit: pageSize.value,
        search: searchQuery.value,
        region: regionFilter.value,
        status: statusFilter.value
      });
      
      camps.value = response.data.camps;
      totalItems.value = response.data.total;
    } catch (err) {
      error.value = err.message || '載入露營地資料失敗';
      showToast(error.value, 'error');
    } finally {
      loading.value = false;
    }
  };
  
  // 查看营地详情
  const viewCampDetails = (campId) => {
    // 这里可以实现跳转到详情页或者打开详情模态框
    window.open(`/camping/detail/${campId}`, '_blank');
  };
  
  // 编辑营地
  const editCamp = async (campId) => {
    loading.value = true;
    
    try {
      const response = await campApi.getCampById(campId);
      Object.assign(campForm, response.data);
      isEditMode.value = true;
      showCampFormModal.value = true;
    } catch (err) {
      showToast(err.message || '獲取露營地資料失敗', 'error');
    } finally {
      loading.value = false;
    }
  };
  
  // 切换营地状态
  const toggleCampStatus = async (campId, currentStatus) => {
    const newStatus = currentStatus === 'active' ? 'inactive' : 'active';
    const actionText = newStatus === 'active' ? '上架' : '下架';
    
    const confirmed = await confirm({
      title: `確認${actionText}`,
      message: `確定要${actionText}此露營地嗎？`,
      confirmText: '確定',
      cancelText: '取消',
      confirmVariant: 'primary'
    });
    
    if (!confirmed) return;
    
    try {
      await campApi.updateCampStatus(campId, newStatus);
      showToast(`露營地已${actionText}`, 'success');
      loadCamps();
    } catch (err) {
      showToast(err.message || `${actionText}失敗`, 'error');
    }
  };
  
  // 删除营地
  const deleteCamp = async (campId) => {
    const confirmed = await confirm({
      title: '確認刪除',
      message: '確定要刪除此露營地嗎？此操作無法恢復！',
      confirmText: '刪除',
      cancelText: '取消',
      confirmVariant: 'danger'
    });
    
    if (!confirmed) return;
    
    try {
      await campApi.deleteCamp(campId);
      showToast('露營地已刪除', 'success');
      loadCamps();
    } catch (err) {
      showToast(err.message || '刪除露營地失敗', 'error');
    }
  };
  
  // 打开添加营地模态框
  const openAddCampModal = () => {
    Object.assign(campForm, initialFormState);
    isEditMode.value = false;
    showCampFormModal.value = true;
  };
  
  // 提交营地表单
  const submitCampForm = async () => {
    try {
      if (isEditMode.value) {
        await campApi.updateCamp(campForm.id, campForm);
        showToast('露營地已更新', 'success');
      } else {
        await campApi.createCamp(campForm);
        showToast('露營地已建立', 'success');
      }
      
      showCampFormModal.value = false;
      loadCamps();
    } catch (err) {
      showToast(err.message || '儲存露營地失敗', 'error');
    }
  };
  
  // 初始化
  onMounted(() => {
    loadCamps();
  });
  </script>
  
  <style scoped>
  .camp-management {
    width: 100%;
    padding: 20px;
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
  
  .btn-primary {
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
  
  .btn-primary:hover {
    background-color: var(--tiffany-blue-dark);
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
    min-width: 120px;
  }
  
  .select-filter:focus {
    border-color: var(--tiffany-blue);
    box-shadow: 0 0 0 3px rgba(129, 216, 208, 0.1);
    outline: none;
  }
  
  .camp-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 20px;
    margin-bottom: 20px;
  }
  
  .camp-card {
    background-color: var(--white);
    border-radius: 12px;
    overflow: hidden;
    box-shadow: var(--shadow);
    transition: transform 0.2s, box-shadow 0.2s;
  }
  
  .camp-card:hover {
    transform: translateY(-5px);
    box-shadow: var(--shadow-md);
  }
  
  .camp-image {
    height: 180px;
    background-size: cover;
    background-position: center;
    position: relative;
  }
  
  .camp-status {
    position: absolute;
    top: 10px;
    right: 10px;
    padding: 4px 8px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 500;
  }
  
  .status-active {
    background-color: #ecfdf5;
    color: #10b981;
  }
  
  .status-inactive {
    background-color: #f3f4f6;
    color: #6b7280;
  }
  
  .status-pending {
    background-color: #fef3c7;
    color: #d97706;
  }
  
  .camp-info {
    padding: 15px;
  }
  
  .camp-info h3 {
    margin: 0 0 8px 0;
    color: var(--gray-800);
    font-size: 16px;
  }
  
  .camp-location {
    display: flex;
    align-items: center;
    gap: 5px;
    color: var(--gray-500);
    margin-bottom: 10px;
    font-size: 14px;
  }
  
  .camp-meta {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
  }
  
  .meta-item {
    display: flex;
    align-items: center;
    gap: 5px;
    color: var(--gray-700);
    font-size: 14px;
  }
  
  .meta-item:first-child {
    color: var(--tiffany-blue);
    font-weight: 500;
  }
  
  .review-count {
    color: var(--gray-500);
    font-size: 12px;
  }
  
  .camp-features {
    display: flex;
    flex-wrap: wrap;
    gap: 5px;
    margin-bottom: 10px;
  }
  
  .feature-tag {
    background-color: var(--tiffany-bg);
    color: var(--tiffany-blue);
    font-size: 12px;
    padding: 2px 8px;
    border-radius: 20px;
  }
  
  .camp-actions {
    display: flex;
    justify-content: space-between;
    padding: 10px 15px;
    border-top: 1px solid var(--gray-100);
  }
  
  .camp-actions button {
    width: 34px;
    height: 34px;
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
  
  .btn-edit {
    background-color: #f0fdf4;
    color: #10b981;
  }
  
  .btn-toggle-status {
    background-color: #f8fafc;
    color: #64748b;
  }
  
  .btn-delete {
    background-color: #fee2e2;
    color: #ef4444;
  }
  
  .btn-view:hover,
  .btn-edit:hover,
  .btn-toggle-status:hover,
  .btn-delete:hover {
    filter: brightness(0.95);
    transform: translateY(-2px);
  }
  
  /* 分页 */
  .pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 15px 0;
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
    background-color: var(--white);
    border-radius: 12px;
    box-shadow: var(--shadow);
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
    margin-bottom: 20px;
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
  
  .form-group {
    margin-bottom: 20px;
  }
  
  .form-row {
    display: flex;
    gap: 20px;
    margin-bottom: 20px;
  }
  
  .form-row .form-group {
    flex: 1;
    margin-bottom: 0;
  }
  
  .form-group label {
    display: block;
    margin-bottom: 8px;
    font-size: 14px;
    font-weight: 500;
    color: var(--gray-700);
  }
  
  .form-group input,
  .form-group textarea,
  .form-group select {
    width: 100%;
    padding: 10px 15px;
    border: 1px solid var(--gray-200);
    border-radius: 10px;
    font-size: 14px;
    transition: all 0.3s;
  }
  
  .form-group input:focus,
  .form-group textarea:focus,
  .form-group select:focus {
    border-color: var(--tiffany-blue);
    box-shadow: 0 0 0 3px rgba(129, 216, 208, 0.1);
    outline: none;
  }
  
  .features-checkboxes {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 10px;
    padding: 15px;
    background-color: var(--gray-50);
    border-radius: 10px;
  }
  
  .feature-checkbox {
    display: flex;
    align-items: center;
    gap: 8px;
  }
  
  .feature-checkbox input {
    width: auto;
  }
  
  .required {
    color: #ef4444;
  }
  
  .modal-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    margin-top: 20px;
  }
  
  .btn-secondary {
    background-color: var(--gray-100);
    color: var(--gray-700);
    border: none;
    padding: 10px 20px;
    border-radius: 10px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.3s;
  }
  
  .btn-secondary:hover {
    background-color: var(--gray-200);
  }
  
  @media (max-width: 768px) {
    .filter-container {
      flex-direction: column;
      align-items: flex-start;
    }
    
    .search-box {
      width: 100%;
    }
    
    .filter-options {
      width: 100%;
    }
    
    .select-filter {
      flex: 1;
    }
    
    .form-row {
      flex-direction: column;
      gap: 15px;
    }
  }
  </style>