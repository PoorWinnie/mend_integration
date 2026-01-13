<template>
    <div class="review-management">
      <div class="page-header">
        <h1>評價管理</h1>
      </div>
      
      <!-- 搜索与筛选 -->
      <div class="filter-container">
        <div class="search-box">
          <input 
            type="text" 
            v-model="searchQuery" 
            placeholder="搜尋評價內容或使用者名稱" 
            @input="handleSearch"
          />
          <button class="btn-search">
            <i class="fa fa-search"></i>
          </button>
        </div>
        
        <div class="filter-options">
          <select v-model="ratingFilter" @change="handleFilter" class="select-filter">
            <option value="">所有評分</option>
            <option value="5">5 星</option>
            <option value="4">4 星</option>
            <option value="3">3 星</option>
            <option value="2">2 星</option>
            <option value="1">1 星</option>
          </select>
          
          <select v-model="reportedFilter" @change="handleFilter" class="select-filter">
            <option value="">所有狀態</option>
            <option value="reported">被檢舉</option>
            <option value="not_reported">未檢舉</option>
          </select>
          
          <select v-model="campFilter" @change="handleFilter" class="select-filter">
            <option value="">所有露營地</option>
            <option v-for="camp in camps" :key="camp.id" :value="camp.id">
              {{ camp.name }}
            </option>
          </select>
        </div>
      </div>
      
      <!-- 评价列表 -->
      <div class="review-list-container">
        <!-- 网格视图 -->
        <div v-if="!loading && reviews.length > 0" class="review-grid">
          <div class="review-card" v-for="review in reviews" :key="review.id">
            <div class="review-header">
              <div class="review-info">
                <div class="user-info">
                  <div class="user-avatar">
                    <i class="fa fa-user"></i>
                  </div>
                  <div class="user-details">
                    <div class="username">{{ review.username }}</div>
                    <div class="review-date">{{ formatDate(review.created_at) }}</div>
                  </div>
                </div>
                <div class="review-rating">
                  <div class="stars">
                    <i v-for="n in 5" :key="n" :class="['fa', n <= review.rating ? 'fa-star' : 'fa-star-o']"></i>
                  </div>
                  <div class="rating-value">{{ review.rating }}.0</div>
                </div>
              </div>
              <div class="review-camp">
                {{ review.camp_name }}
              </div>
            </div>
            <div class="review-content">
              <div class="review-title">{{ review.title }}</div>
              <div class="review-text">{{ review.content }}</div>
              <div v-if="review.has_images" class="review-images">
                <div 
                  v-for="(image, index) in review.images.slice(0, 3)" 
                  :key="index" 
                  class="image-thumbnail"
                  :style="{ backgroundImage: `url(${image})` }"
                ></div>
                <div v-if="review.images.length > 3" class="more-images">
                  +{{ review.images.length - 3 }}
                </div>
              </div>
            </div>
            <div class="review-actions">
              <button class="btn-view" @click="viewReviewDetails(review.id)" title="查看詳情">
                <i class="fa fa-eye"></i>
              </button>
              <button class="btn-reply" @click="replyToReview(review.id)" title="回覆評價">
                <i class="fa fa-reply"></i>
              </button>
              <button v-if="review.is_reported" class="btn-flag" @click="handleReport(review.id)" title="已被檢舉">
                <i class="fa fa-flag"></i>
              </button>
              <button class="btn-delete" @click="deleteReview(review.id)" title="刪除評價">
                <i class="fa fa-trash"></i>
              </button>
            </div>
            <div v-if="review.is_reported" class="report-badge">
              被檢舉
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
        <div v-if="!loading && reviews.length === 0" class="empty-container">
          <div class="empty-icon">
            <i class="fa fa-star"></i>
          </div>
          <h3>沒有找到評價</h3>
          <p>嘗試調整搜尋條件以查看評價記錄</p>
        </div>
        
        <!-- 分页控制 -->
        <div v-if="!loading && reviews.length > 0" class="pagination">
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
      
      <!-- 评价详情模态框 -->
      <div class="modal" v-if="showReviewDetailModal">
        <div class="modal-content">
          <div class="modal-header">
            <h2>評價詳情</h2>
            <button class="btn-close" @click="showReviewDetailModal = false">
              <i class="fa fa-times"></i>
            </button>
          </div>
          <div class="modal-body" v-if="selectedReview">
            <div class="review-detail-header">
              <div class="user-info">
                <div class="user-avatar">
                  <i class="fa fa-user"></i>
                </div>
                <div class="user-details">
                  <div class="username">{{ selectedReview.username }}</div>
                  <div class="review-date">{{ formatDate(selectedReview.created_at) }}</div>
                </div>
              </div>
              <div class="review-rating">
                <div class="stars">
                  <i v-for="n in 5" :key="n" :class="['fa', n <= selectedReview.rating ? 'fa-star' : 'fa-star-o']"></i>
                </div>
                <div class="rating-value">{{ selectedReview.rating }}.0</div>
              </div>
            </div>
            
            <div class="camp-info-section">
              <div class="camp-image" :style="{ backgroundImage: `url(${selectedReview.camp_image})` }"></div>
              <div class="camp-details">
                <h3>{{ selectedReview.camp_name }}</h3>
                <p>{{ selectedReview.camp_location }}</p>
              </div>
            </div>
            
            <div class="review-content-section">
              <h3>{{ selectedReview.title }}</h3>
              <p>{{ selectedReview.content }}</p>
              
              <div v-if="selectedReview.has_images" class="review-images-gallery">
                <div 
                  v-for="(image, index) in selectedReview.images" 
                  :key="index" 
                  class="gallery-image"
                  :style="{ backgroundImage: `url(${image})` }"
                ></div>
              </div>
            </div>
            
            <div v-if="selectedReview.is_reported" class="report-section">
              <h3>檢舉資訊</h3>
              <div class="report-details">
                <div class="report-item">
                  <span class="label">檢舉者:</span>
                  <span>{{ selectedReview.reporter_name }}</span>
                </div>
                <div class="report-item">
                  <span class="label">檢舉時間:</span>
                  <span>{{ formatDate(selectedReview.reported_at) }}</span>
                </div>
                <div class="report-item">
                  <span class="label">檢舉理由:</span>
                  <span>{{ selectedReview.report_reason }}</span>
                </div>
              </div>
              <div class="report-actions">
                <button @click="dismissReport(selectedReview.id)" class="btn-dismiss">
                  <i class="fa fa-check"></i> 駁回檢舉
                </button>
              </div>
            </div>
            
            <div class="reply-section">
              <h3>管理員回覆</h3>
              <div v-if="selectedReview.has_reply" class="existing-reply">
                <p>{{ selectedReview.reply_content }}</p>
                <div class="reply-info">
                  <span>回覆者: {{ selectedReview.reply_by }}</span>
                  <span>回覆時間: {{ formatDate(selectedReview.replied_at) }}</span>
                </div>
              </div>
              <div v-else class="reply-form">
                <textarea 
                  v-model="replyContent" 
                  placeholder="輸入回覆內容..." 
                  rows="4"
                ></textarea>
                <button @click="submitReply(selectedReview.id)" class="btn-submit-reply">
                  <i class="fa fa-paper-plane"></i> 送出回覆
                </button>
              </div>
            </div>
            
            <div class="modal-footer">
              <button class="btn-secondary" @click="showReviewDetailModal = false">關閉</button>
              <button class="btn-delete" @click="deleteReview(selectedReview.id)">
                <i class="fa fa-trash"></i> 刪除評價
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 回复评价模态框 -->
      <div class="modal" v-if="showReplyModal">
        <div class="modal-content">
          <div class="modal-header">
            <h2>回覆評價</h2>
            <button class="btn-close" @click="showReplyModal = false">
              <i class="fa fa-times"></i>
            </button>
          </div>
          <div class="modal-body" v-if="selectedReview">
            <div class="reply-review-content">
              <div class="review-card mini">
                <div class="review-header">
                  <div class="user-info">
                    <div class="username">{{ selectedReview.username }}</div>
                    <div class="stars">
                      <i v-for="n in 5" :key="n" :class="['fa', n <= selectedReview.rating ? 'fa-star' : 'fa-star-o']"></i>
                    </div>
                  </div>
                  <div class="review-date">{{ formatDate(selectedReview.created_at) }}</div>
                </div>
                <div class="review-text">
                  {{ selectedReview.content }}
                </div>
              </div>
            </div>
            
            <div class="reply-form">
              <label>您的回覆</label>
              <textarea 
                v-model="replyContent" 
                placeholder="輸入回覆內容..." 
                rows="4"
                required
              ></textarea>
              <p class="form-hint">回覆將顯示在評價下方，並以管理員身份發布。</p>
            </div>
            
            <div class="modal-footer">
              <button class="btn-secondary" @click="showReplyModal = false">取消</button>
              <button class="btn-primary" @click="submitReply(selectedReview.id)">
                <i class="fa fa-paper-plane"></i> 送出回覆
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
  import reviewApi from '@/api/reviewApi';
  import campApi from '@/api/campApi';
  
  const { confirm } = useConfirm();
  const { showToast } = useToast();
  
  // 数据状态
  const reviews = ref([]);
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
  const ratingFilter = ref('');
  const reportedFilter = ref('');
  const campFilter = ref('');
  
  // 模态框状态
  const showReviewDetailModal = ref(false);
  const showReplyModal = ref(false);
  const selectedReview = ref(null);
  const replyContent = ref('');
  
  // 格式化日期
  const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-TW');
  };
  
  // 处理搜索
  const handleSearch = () => {
    currentPage.value = 1;
    loadReviews();
  };
  
  // 处理筛选
  const handleFilter = () => {
    currentPage.value = 1;
    loadReviews();
  };
  
  // 切换页码
  const changePage = (page) => {
    currentPage.value = page;
    loadReviews();
  };
  
  // 加载评价数据
  const loadReviews = async () => {
    loading.value = true;
    error.value = null;
    
    try {
      const response = await reviewApi.getReviews({
        page: currentPage.value,
        limit: pageSize.value,
        search: searchQuery.value,
        rating: ratingFilter.value,
        reported: reportedFilter.value,
        camp_id: campFilter.value
      });
      
      reviews.value = response.data.reviews;
      totalItems.value = response.data.total;
    } catch (err) {
      error.value = err.message || '載入評價資料失敗';
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
  
  // 查看评价详情
  const viewReviewDetails = async (reviewId) => {
    loading.value = true;
    
    try {
      const response = await reviewApi.getReviewById(reviewId);
      selectedReview.value = response.data;
      replyContent.value = '';
      showReviewDetailModal.value = true;
    } catch (err) {
      showToast(err.message || '獲取評價詳情失敗', 'error');
    } finally {
      loading.value = false;
    }
  };
  
  // 回复评价
  const replyToReview = async (reviewId) => {
    loading.value = true;
    
    try {
      const response = await reviewApi.getReviewById(reviewId);
      selectedReview.value = response.data;
      replyContent.value = selectedReview.value.has_reply ? selectedReview.value.reply_content : '';
      showReplyModal.value = true;
    } catch (err) {
      showToast(err.message || '獲取評價詳情失敗', 'error');
    } finally {
      loading.value = false;
    }
  };
  
  // 提交回复
  const submitReply = async (reviewId) => {
    if (!replyContent.value.trim()) {
      showToast('請輸入回覆內容', 'error');
      return;
    }
    
    try {
      await reviewApi.replyToReview(reviewId, replyContent.value);
      showToast('回覆已送出', 'success');
      showReplyModal.value = false;
      showReviewDetailModal.value = false;
      loadReviews();
    } catch (err) {
      showToast(err.message || '回覆評價失敗', 'error');
    }
  };
  
  // 处理举报
  const handleReport = async (reviewId) => {
    await viewReviewDetails(reviewId);
  };
  
  // 驳回举报
  const dismissReport = async (reviewId) => {
    const confirmed = await confirm({
      title: '駁回檢舉',
      message: '確定要駁回此檢舉嗎？',
      confirmText: '駁回',
      cancelText: '取消',
      confirmVariant: 'primary'
    });
    
    if (!confirmed) return;
    
    try {
      await reviewApi.dismissReport(reviewId);
      showToast('檢舉已駁回', 'success');
      showReviewDetailModal.value = false;
      loadReviews();
    } catch (err) {
      showToast(err.message || '駁回檢舉失敗', 'error');
    }
  };
  
  // 删除评价
  const deleteReview = async (reviewId) => {
    const confirmed = await confirm({
      title: '刪除評價',
      message: '確定要刪除此評價嗎？此操作無法恢復！',
      confirmText: '刪除',
      cancelText: '取消',
      confirmVariant: 'danger'
    });
    
    if (!confirmed) return;
    
    try {
      await reviewApi.deleteReview(reviewId);
      showToast('評價已刪除', 'success');
      showReviewDetailModal.value = false;
      showReplyModal.value = false;
      loadReviews();
    } catch (err) {
      showToast(err.message || '刪除評價失敗', 'error');
    }
  };
  
  // 初始化
  onMounted(() => {
    Promise.all([loadReviews(), loadCamps()]);
  });
  </script>
  
  <style scoped>
  .review-management {
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
    display: flex;
    justify-content: space-between;
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
  
  .review-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 20px;
    margin-bottom: 20px;
  }
  
  .review-card {
    background-color: var(--white);
    border-radius: 12px;
    box-shadow: var(--shadow);
    padding: 20px;
    position: relative;
    transition: transform 0.2s, box-shadow 0.2s;
  }
  
  .review-card:hover {
    transform: translateY(-5px);
    box-shadow: var(--shadow-md);
  }
  
  .review-header {
    margin-bottom: 15px;
  }
  
  .review-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
  }
  
  .user-info {
    display: flex;
    align-items: center;
    gap: 10px;
  }
  
  .user-avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background-color: var(--gray-100);
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--gray-500);
  }
  
  .user-details {
    display: flex;
    flex-direction: column;
  }
  
  .username {
    font-weight: 500;
    color: var(--gray-800);
  }
  
  .review-date {
    font-size: 12px;
    color: var(--gray-500);
  }
  
  .review-rating {
    display: flex;
    align-items: center;
    gap: 5px;
  }
  
  .stars {
    color: #fbbf24;
  }
  
  .rating-value {
    font-weight: 600;
    color: var(--gray-800);
  }
  
  .review-camp {
    font-size: 14px;
    color: var(--tiffany-blue);
    font-weight: 500;
  }
  
  .review-content {
    margin-bottom: 15px;
  }
  
  .review-title {
    font-weight: 600;
    margin-bottom: 5px;
    color: var(--gray-800);
  }
  
  .review-text {
    color: var(--gray-700);
    margin-bottom: 10px;
    line-height: 1.5;
  }
  
  .review-images {
    display: flex;
    gap: 10px;
  }
  
  .image-thumbnail {
    width: 80px;
    height: 80px;
    border-radius: 8px;
    background-size: cover;
    background-position: center;
  }
  
  .more-images {
    width: 80px;
    height: 80px;
    border-radius: 8px;
    background-color: rgba(0, 0, 0, 0.5);
    color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 500;
  }
  
  .review-actions {
    display: flex;
    gap: 10px;
  }
  
  .review-actions button {
    width: 36px;
    height: 36px;
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
  
  .btn-reply {
    background-color: #e0f2fe;
    color: #0369a1;
  }
  
  .btn-flag {
    background-color: #fee2e2;
    color: #ef4444;
  }
  
  .btn-delete {
    background-color: #fee2e2;
    color: #ef4444;
  }
  
  .review-actions button:hover {
    filter: brightness(0.95);
    transform: translateY(-2px);
  }
  
  .report-badge {
    position: absolute;
    top: 10px;
    right: 10px;
    background-color: #ef4444;
    color: white;
    font-size: 12px;
    font-weight: 500;
    padding: 4px 8px;
    border-radius: 20px;
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
  
  .review-detail-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }
  
  .camp-info-section {
    display: flex;
    gap: 15px;
    margin-bottom: 20px;
    padding: 15px;
    background-color: var(--gray-50);
    border-radius: 10px;
  }
  
  .camp-image {
    width: 100px;
    height: 100px;
    border-radius: 10px;
    background-size: cover;
    background-position: center;
  }
  
  .camp-details {
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
  
  .camp-details h3 {
    margin: 0 0 5px 0;
    font-size: 16px;
    color: var(--gray-800);
  }
  
  .camp-details p {
    margin: 0;
    color: var(--gray-500);
    font-size: 14px;
  }
  
  .review-content-section {
    margin-bottom: 20px;
  }
  
  .review-content-section h3 {
    margin: 0 0 10px 0;
    font-size: 18px;
    color: var(--gray-800);
  }
  
  .review-content-section p {
    margin: 0 0 15px 0;
    color: var(--gray-700);
    line-height: 1.6;
  }
  
  .review-images-gallery {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 10px;
    margin-top: 15px;
  }
  
  .gallery-image {
    height: 150px;
    border-radius: 10px;
    background-size: cover;
    background-position: center;
  }
  
  .report-section {
    margin-bottom: 20px;
    background-color: #fef2f2;
    padding: 15px;
    border-radius: 10px;
    border-left: 4px solid #ef4444;
  }
  
  .report-section h3 {
    margin: 0 0 10px 0;
    font-size: 16px;
    color: #b91c1c;
  }
  
  .report-details {
    margin-bottom: 15px;
  }
  
  .report-item {
    margin-bottom: 5px;
    display: flex;
    gap: 10px;
  }
  
  .report-item .label {
    font-weight: 500;
    color: #b91c1c;
  }
  
  .report-actions {
    display: flex;
    justify-content: flex-end;
  }
  
  .btn-dismiss {
    padding: 8px 15px;
    background-color: white;
    border: 1px solid #dc2626;
    color: #dc2626;
    border-radius: 8px;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 5px;
    transition: all 0.2s;
  }
  
  .btn-dismiss:hover {
    background-color: #fee2e2;
  }
  
  .reply-section {
    margin-bottom: 20px;
  }
  
  .reply-section h3 {
    margin: 0 0 10px 0;
    font-size: 16px;
    color: var(--gray-800);
  }
  
  .existing-reply {
    background-color: var(--tiffany-bg);
    padding: 15px;
    border-radius: 10px;
    margin-bottom: 10px;
  }
  
  .existing-reply p {
    margin: 0 0 10px 0;
    color: var(--gray-700);
  }
  
  .reply-info {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: var(--gray-500);
  }
  
  .reply-form textarea {
    width: 100%;
    padding: 12px 15px;
    border: 1px solid var(--gray-200);
    border-radius: 10px;
    resize: vertical;
    margin-bottom: 10px;
    font-size: 14px;
    transition: all 0.3s;
  }
  
  .reply-form textarea:focus {
    border-color: var(--tiffany-blue);
    box-shadow: 0 0 0 3px rgba(129, 216, 208, 0.1);
    outline: none;
  }
  
  .reply-form label {
    display: block;
    margin-bottom: 8px;
    font-weight: 500;
    color: var(--gray-700);
  }
  
  .form-hint {
    font-size: 12px;
    color: var(--gray-500);
    margin-top: 5px;
  }
  
  .btn-submit-reply {
    padding: 10px 15px;
    background-color: var(--tiffany-blue);
    color: white;
    border: none;
    border-radius: 10px;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 5px;
    transition: all 0.3s;
  }
  
  .btn-submit-reply:hover {
    background-color: var(--tiffany-blue-dark);
  }
  
  .modal-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    margin-top: 20px;
  }
  
  .btn-secondary,
  .btn-primary,
  .btn-delete {
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
  
  .btn-primary {
    background-color: var(--tiffany-blue);
    color: white;
  }
  
  .btn-primary:hover {
    background-color: var(--tiffany-blue-dark);
  }
  
  .btn-delete {
    background-color: #fee2e2;
    color: #ef4444;
  }
  
  .btn-delete:hover {
    background-color: #fecaca;
  }
  
  /* 小型评价卡片（用于回复模态框） */
  .review-card.mini {
    box-shadow: none;
    padding: 15px;
    background-color: var(--gray-50);
    margin-bottom: 20px;
  }
  
  .review-card.mini:hover {
    transform: none;
  }
  
  .review-card.mini .review-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
  }
  
  .review-card.mini .stars {
    font-size: 12px;
    margin-top: 5px;
  }
  
  @media (max-width: 768px) {
    .filter-container {
      flex-direction: column;
    }
    
    .search-box {
      width: 100%;
    }
    
    .filter-options {
      width: 100%;
      flex-wrap: wrap;
    }
    
    .select-filter {
      flex: 1;
      min-width: 0;
    }
    
    .review-grid {
      grid-template-columns: 1fr;
    }
    
    .camp-info-section {
      flex-direction: column;
    }
    
    .camp-image {
      width: 100%;
      height: 150px;
    }
    
    .review-images-gallery {
      grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
    }
    
    .gallery-image {
      height: 100px;
    }
  }
  </style>