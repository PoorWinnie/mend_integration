<template>
  <div class="review-detail-container">
    <div class="back-link">
      <router-link :to="backLink" class="btn-back">
        <i class="fas fa-arrow-left"></i> 返回營地頁面
      </router-link>
    </div>
    
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner">
        <i class="fas fa-spinner fa-spin"></i>
      </div>
      <p>載入評價中...</p>
    </div>
    
    <div v-else-if="error" class="error-container">
      <div class="error-icon">
        <i class="fas fa-exclamation-circle"></i>
      </div>
      <h3>發生錯誤</h3>
      <p>{{ error }}</p>
      <button @click="loadReview" class="btn-retry">
        重試
      </button>
    </div>
    
    <div v-else-if="review" class="review-detail-content">
      <div v-if="review.is_reported" class="review-reported-banner">
        <i class="fas fa-shield-alt"></i> 此評價正在審核中，暫時隱藏
      </div>

      <div class="review-header">
        <div class="star-title">
          <i class="fas fa-star"></i> 營地評價 <span class="review-count">({{ totalReviews || 11 }})</span>
        </div>
      </div>

      <div class="review-main-info">
        <div class="user-info">
          <div class="user-avatar">
            <i class="fas fa-user"></i>
          </div>
          <div class="user-details">
            <div class="username">{{ review.username || '匿名用戶' }}</div>
            <div class="review-date">{{ formatDate(review.created_at) }}</div>
          </div>
        </div>
        
        <div class="rating-container">
          <div class="stars">
            <i v-for="n in 5" :key="n" :class="['fas', n <= review.rating ? 'fa-star' : 'fa-star-o']"></i>
          </div>
        </div>
      </div>
      
      <div class="review-content">
        <div class="review-text">{{ review.content }}</div>
        
        <div v-if="review.has_images" class="review-images">
          <div v-for="(image, index) in review.images" :key="index" class="review-image">
            <img :src="image" :alt="`評價圖片 ${index+1}`" />
          </div>
        </div>
      </div>

      <div class="review-advantages-disadvantages">
        <div class="advantage-section">
          <div class="icon-title">
            <i class="fas fa-thumbs-up"></i> 優點
          </div>
          <div class="content">
            {{ review.advantages || '我是優點' }}
          </div>
        </div>
        <div class="disadvantage-section">
          <div class="icon-title">
            <i class="fas fa-thumbs-down"></i> 缺點
          </div>
          <div class="content">
            {{ review.disadvantages || '我是缺點' }}
          </div>
        </div>
      </div>
      
      <div class="rating-details">
        <div class="rating-item">
          <span>清潔度</span>
          <div class="stars">
            <i v-for="n in 5" :key="n" :class="['fas', n <= (review.cleanliness || 5) ? 'fa-star' : 'fa-star-o']"></i>
          </div>
        </div>
        <div class="rating-item">
          <span>便利性</span>
          <div class="stars">
            <i v-for="n in 5" :key="n" :class="['fas', n <= (review.convenience || 5) ? 'fa-star' : 'fa-star-o']"></i>
          </div>
        </div>
        <div class="rating-item">
          <span>友善度</span>
          <div class="stars">
            <i v-for="n in 5" :key="n" :class="['fas', n <= (review.friendliness || 5) ? 'fa-star' : 'fa-star-o']"></i>
          </div>
        </div>
      </div>
      
      <div v-if="review.has_reply" class="owner-reply">
        <div class="reply-header">
          <div class="reply-icon">
            <i class="fas fa-reply"></i> {{ review.reply_by || currentUser.displayName }}回覆
          </div>
          <div class="reply-date">{{ formatDate(review.replied_at) }}</div>
        </div>
        <div class="reply-content">
          {{ review.reply_content || 'null' }}
        </div>
        <div v-if="isOwnReply(review)" class="reply-actions">
          <button @click="showEditReplyModal = true" class="btn-edit-reply">
            <i class="fas fa-pen"></i> 編輯回覆
          </button>
          <button @click="confirmDeleteReply" class="btn-delete-reply">
            <i class="fas fa-trash"></i> 刪除回覆
          </button>
        </div>
      </div>
      
      <div class="review-actions">
        <div class="likes-section">
          <button @click="toggleLike" class="btn-like" :class="{ liked: isLiked }">
            <i class="fas fa-thumbs-up"></i>
            <span>{{ likesCount || 100 }}</span>
          </button>
        </div>
        <div class="other-actions">
          <button v-if="!review.has_reply && isLoggedIn" @click="showReplyModal = true" class="btn-reply">
            <i class="fas fa-reply"></i> 回覆
          </button>
          <button v-else-if="!review.has_reply && !isLoggedIn" @click="navigateToLogin" class="btn-reply">
            <i class="fas fa-sign-in-alt"></i> 登入回覆
          </button>
          <button v-if="!review.is_reported" @click="showReportModal = true" class="btn-report">
            <i class="fas fa-flag"></i> 檢舉
          </button>
        </div>
      </div>
    </div>
    
    <!-- 檢舉彈窗 -->
    <div v-if="showReportModal" class="modal-overlay">
      <div class="modal-container">
        <div class="modal-header">
          <h2>檢舉評價</h2>
          <button class="close-btn" @click="showReportModal = false">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="report-section">
            <div class="report-label">檢舉原因</div>
            <select v-model="selectedReportReason" class="report-select">
              <option value="" disabled>請選擇</option>
              <option v-for="reason in reportReasons" :key="reason" :value="reason">{{ reason }}</option>
            </select>
          </div>
          
          <div class="report-section">
            <div class="report-label">詳細說明</div>
            <textarea 
              v-model="reportDetail"
              placeholder="請詳細說明檢舉原因..."
              class="report-textarea"
            ></textarea>
          </div>
          
          <div class="report-warning">
            <i class="fas fa-exclamation-triangle"></i>
            提交檢舉後，此評價將會被暫時隱藏，直到管理員審核完成。
          </div>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="showReportModal = false">取消</button>
          <button 
            class="submit-btn"
            :disabled="!selectedReportReason" 
            @click="submitReport"
          >
            提交檢舉
          </button>
        </div>
      </div>
    </div>
    
    <!-- 回覆評價彈窗 -->
    <div v-if="showReplyModal" class="modal-overlay">
      <div class="modal-container">
        <div class="modal-header">
          <h2>回覆評價</h2>
          <button class="close-btn" @click="showReplyModal = false">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="reply-review-info">
            <div class="reply-user">{{ review?.username || '用戶null' }}</div>
            <div class="reply-rating">
              <i v-for="n in 5" :key="n" :class="['fas', n <= (review?.rating || 1) ? 'fa-star' : 'fa-star-o']"></i>
              <span>{{ review?.rating || 1 }}/5</span>
            </div>
            <div class="reply-content-preview">{{ review?.content || '超爛 跟老闆要什麼沒什麼 不懂怎麼會有人想來這個地方' }}</div>
          </div>
          
          <div class="reply-form">
            <div class="reply-label">回覆內容</div>
            <textarea 
              v-model="replyContent" 
              placeholder="請輸入您的回覆..." 
              class="reply-textarea"
            ></textarea>
            
            <div class="reply-info">
              <i class="fas fa-info-circle"></i>
              您的回覆將會顯示在此評價下方，並署名您的使用者名稱 ({{ currentUser.displayName }})。
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="showReplyModal = false">取消</button>
          <button 
            class="submit-btn"
            :disabled="!replyContent.trim()" 
            @click="submitReply"
          >
            送出回覆
          </button>
        </div>
      </div>
    </div>
    
    <!-- 編輯回覆彈窗 -->
    <div v-if="showEditReplyModal" class="modal-overlay">
      <div class="modal-container">
        <div class="modal-header">
          <h2>編輯回覆</h2>
          <button class="close-btn" @click="showEditReplyModal = false">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="reply-form">
            <div class="reply-label">回覆內容</div>
            <textarea 
              v-model="editReplyContent" 
              placeholder="請輸入您的回覆..." 
              class="reply-textarea"
            ></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="showEditReplyModal = false">取消</button>
          <button 
            class="submit-btn"
            :disabled="!editReplyContent.trim()" 
            @click="updateReply"
          >
            送出
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useReviewStore } from '@/stores/reviewStore';
import { useToast } from '@/composables/useToast';
import { useConfirm } from '@/composables/useConfirm';
import { formatDate } from '@/utils/dateFormatter';

const route = useRoute();
const router = useRouter();
const reviewStore = useReviewStore();
const { showToast } = useToast();
const { confirm } = useConfirm();

// 評價資料
const review = ref(null);
const loading = ref(true);
const error = ref(null);
const totalReviews = ref(11);

// 登入相關
const isLoggedIn = ref(true); // 假設用戶已登入
const currentUser = ref({
  id: 1,
  username: 'User123',
  displayName: 'User123' // 這應該從您的認證系統獲取
});

// 返回連結
const backLink = computed(() => {
  return route.query.from ? `/camp/${route.query.campId || ''}` : '/reviews';
});

// 檢舉功能
const showReportModal = ref(false);
const selectedReportReason = ref('');
const reportDetail = ref('');
const reportReasons = [
  '不實內容',
  '廣告垃圾內容',
  '冒犯性言論',
  '不當或敏感內容',
  '侵犯隱私權',
  '其他原因'
];

// 回覆功能
const showReplyModal = ref(false);
const replyContent = ref('');
const showEditReplyModal = ref(false);
const editReplyContent = ref('');

// 點讚功能
const isLiked = ref(false);
const likesCount = ref(0);
// 判斷回覆是否為當前用戶的
const isOwnReply = (review) => {
  // 在實際應用中，這裡應該比較用戶ID或其他唯一標識符
  // 這裡簡化處理，假設所有回覆都是當前用戶的，便於測試
  return true;
};

// 加載評價
const loadReview = async () => {
  const reviewId = route.params.id;
  if (!reviewId) {
    router.push('/reviews');
    return;
  }
  
  loading.value = true;
  error.value = null;
  
  try {
    // 在實際應用中，這裡應該調用API獲取評價詳情
    // 這裡簡化處理，直接構造一個模擬的評價對象
    
    // 模擬API調用
    // const result = await reviewStore.fetchReviewById(reviewId);
    
    // 模擬評價資料
    review.value = {
      id: reviewId,
      username: "用戶123",
      rating: 4,
      content: "這個營地很棒，環境優美，設施齊全。唯一的缺點是週末人比較多。",
      advantages: "環境優美，設施齊全，服務態度好",
      disadvantages: "週末人較多，停車位不足",
      created_at: new Date().toISOString(),
      has_images: true,
      images: [
        "https://picsum.photos/200/200",
        "https://picsum.photos/201/200",
        "https://picsum.photos/200/201"
      ],
      cleanliness: 4,
      convenience: 3,
      friendliness: 5,
      has_reply: false,
      is_reported: false,
      likes_count: 42
    };
    
    likesCount.value = review.value.likes_count || 0;
    
    // 判斷回覆是否為當前用戶
    if (review.value.has_reply) {
      review.value.reply_by = review.value.reply_by || currentUser.value.displayName;
      editReplyContent.value = review.value.reply_content || '';
    }
  } catch (err) {
    error.value = err.message || '無法載入評價';
    showToast(error.value, 'error');
  } finally {
    loading.value = false;
  }
};

// 導航到登入頁面
const navigateToLogin = () => {
  router.push('/login?redirect=' + route.fullPath);
};

// 提交檢舉
const submitReport = async () => {
  if (!selectedReportReason.value) {
    showToast('請選擇檢舉理由', 'error');
    return;
  }
  
  try {
    // 在實際應用中，這裡應該調用API提交檢舉
    // 這裡簡化處理，直接更新前端狀態
    
    // 更新評價狀態為已檢舉
    review.value.is_reported = true;
    showReportModal.value = false;
    showToast('檢舉已提交，管理員會盡快審核', 'success');
    
    // 實際的API調用 - 在生產環境取消註釋
    // await reviewStore.reportReview({
    //   reviewId: review.value.id,
    //   reason: selectedReportReason.value,
    //   detail: reportDetail.value
    // });
  } catch (error) {
    showToast('檢舉提交失敗: ' + (error.message || '未知錯誤'), 'error');
  }
};

// 提交回覆
const submitReply = async () => {
  if (!replyContent.value.trim()) {
    showToast('請輸入回覆內容', 'error');
    return;
  }
  
  try {
    // 在實際應用中，這裡應該調用API提交回覆
    // 這裡簡化處理，直接更新前端狀態
    
    // 更新評價資料
    review.value.has_reply = true;
    review.value.reply_content = replyContent.value;
    review.value.replied_at = new Date().toISOString();
    review.value.reply_by = currentUser.value.displayName;
    
    showReplyModal.value = false;
    showToast('回覆已成功送出', 'success');
    
    // 實際的API調用 - 在生產環境取消註釋
    // await reviewStore.replyToReview(review.value.id, replyContent.value);
  } catch (error) {
    showToast('回覆送出失敗: ' + (error.message || '未知錯誤'), 'error');
  }
};

// 更新回覆
const updateReply = async () => {
  if (!editReplyContent.value.trim()) {
    showToast('請輸入回覆內容', 'error');
    return;
  }
  
  try {
    // 在實際應用中，這裡應該調用API更新回覆
    // 這裡簡化處理，直接更新前端狀態
    
    // 更新評價資料
    review.value.reply_content = editReplyContent.value;
    review.value.replied_at = new Date().toISOString();
    
    showEditReplyModal.value = false;
    showToast('回覆已成功更新', 'success');
    
    // 實際的API調用 - 在生產環境取消註釋
    // await reviewStore.updateReply(review.value.id, editReplyContent.value);
  } catch (error) {
    showToast('回覆更新失敗: ' + (error.message || '未知錯誤'), 'error');
  }
};

// 確認刪除回覆
const confirmDeleteReply = async () => {
  const result = await confirm({
    title: '刪除回覆',
    message: '確定要刪除此回覆嗎？',
    confirmText: '刪除',
    cancelText: '取消',
    confirmVariant: 'danger'
  });
  
  if (result) {
    try {
      // 在實際應用中，這裡應該調用API刪除回覆
      // 這裡簡化處理，直接更新前端狀態
      
      // 更新評價資料
      review.value.has_reply = false;
      review.value.reply_content = '';
      review.value.replied_at = null;
      review.value.reply_by = '';
      
      showToast('回覆已成功刪除', 'success');
      
      // 實際的API調用 - 在生產環境取消註釋
      // await reviewStore.deleteReply(review.value.id);
    } catch (error) {
      showToast('回覆刪除失敗: ' + (error.message || '未知錯誤'), 'error');
    }
  }
};

// 點讚功能
const toggleLike = async () => {
  try {
    // 在實際應用中，這裡應該調用API來切換點讚狀態
    // 這裡簡化處理，直接切換前端狀態
    isLiked.value = !isLiked.value;
    likesCount.value = isLiked.value 
      ? likesCount.value + 1 
      : Math.max(0, likesCount.value - 1);
    
    showToast(isLiked.value ? '成功點讚!' : '已取消點讚', 'success');
    
    // 實際的API調用 - 在生產環境取消註釋
    // const result = await reviewStore.toggleLike(review.value.id);
  } catch (error) {
    showToast('操作失敗: ' + (error.message || '未知錯誤'), 'error');
  }
};

// 初始化
onMounted(() => {
  loadReview();
});
</script>

<style scoped>
.review-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.back-link {
  margin-bottom: 20px;
}

.btn-back {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border: 1px solid #ddd;
  background-color: #f1f1f1;
  color: #333;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  text-decoration: none;
  transition: all 0.2s;
}

.btn-back:hover {
  background-color: #e0e0e0;
}

/* 載入中和錯誤狀態 */
.loading-container,
.error-container {
  text-align: center;
  padding: 40px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.loading-spinner {
  font-size: 30px;
  color: #81d8d0;
  margin-bottom: 10px;
}

.error-icon {
  font-size: 40px;
  color: #ef4444;
  margin-bottom: 10px;
}

.error-container h3 {
  margin-bottom: 10px;
  font-size: 18px;
}

.btn-retry {
  margin-top: 10px;
  padding: 8px 16px;
  background-color: #81d8d0;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-retry:hover {
  background-color: #5dc2b9;
}

/* 評價詳情 */
.review-detail-content {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 30px;
  margin-bottom: 20px;
  position: relative;
  transition: transform 0.2s, box-shadow 0.2s;
}

.review-detail-content:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.review-reported-banner {
  background-color: #f8f9fa;
  border: 1px solid #dee2e6;
  padding: 12px 16px;
  border-radius: 6px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #6c757d;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
  padding-bottom: 15px;
}

.star-title {
  font-size: 20px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 8px;
}

.star-title i {
  color: #ffc107;
}

.review-count {
  font-size: 16px;
  color: #6c757d;
  font-weight: normal;
}

.review-main-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 50px;
  height: 50px;
  background-color: #f1f1f1;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #888;
  font-size: 20px;
}

.username {
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 4px;
}

.review-date {
  font-size: 14px;
  color: #666;
}

.rating-container {
  display: flex;
  align-items: center;
}

.stars {
  color: #ffc107;
  font-size: 18px;
}

.review-content {
  margin-bottom: 20px;
}

.review-text {
  line-height: 1.6;
  margin-bottom: 20px;
}

.review-images {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 20px;
}

.review-image {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.2s;
}

.review-image:hover {
  transform: scale(1.05);
}

.review-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 優缺點區域 */
.review-advantages-disadvantages {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.advantage-section,
.disadvantage-section {
  flex: 1;
  padding: 15px;
  border-radius: 8px;
}

.advantage-section {
  background-color: #e8f5e9;
}

.disadvantage-section {
  background-color: #ffebee;
}

.icon-title {
  font-weight: bold;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.advantage-section .icon-title {
  color: #66bb6a;
}

.disadvantage-section .icon-title {
  color: #ef5350;
}

.content {
  color: #333;
}

/* 評分細節 */
.rating-details {
  display: flex;
  gap: 30px;
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.rating-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

.rating-item span {
  font-size: 14px;
  color: #666;
}

.rating-item .stars {
  font-size: 16px;
}

/* 使用者回覆 */
.owner-reply {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #e0f7fa;
  border-left: 4px solid #81d8d0;
  border-radius: 4px;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.reply-icon {
  font-weight: bold;
  color: #81d8d0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.reply-date {
  font-size: 14px;
  color: #666;
}

.reply-content {
  line-height: 1.5;
  margin-bottom: 10px;
}

.reply-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.btn-edit-reply,
.btn-delete-reply {
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: all 0.2s;
}

.btn-edit-reply {
  background-color: #e0f7fa;
  color: #00acc1;
  border: 1px solid #00acc1;
}

.btn-edit-reply:hover {
  background-color: #b2ebf2;
}

.btn-delete-reply {
  background-color: #ffebee;
  color: #e57373;
  border: 1px solid #e57373;
}

.btn-delete-reply:hover {
  background-color: #ffcdd2;
}

/* 評價操作區 */
.review-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.likes-section {
  display: flex;
  align-items: center;
}

.btn-like {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 8px 12px;
  border: 1px solid #ddd;
  background-color: white;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-like:hover,
.btn-like.liked {
  background-color: #f5f5f5;
  color: #81d8d0;
  border-color: #81d8d0;
}

.other-actions {
  display: flex;
  gap: 10px;
}

.btn-reply,
.btn-report {
  padding: 8px 12px;
  border: 1px solid #ddd;
  background-color: white;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: all 0.2s;
}

.btn-reply {
  color: #81d8d0;
}

.btn-reply:hover {
  background-color: #e0f7fa;
  border-color: #81d8d0;
}

.btn-report {
  color: #e57373;
}

.btn-report:hover {
  background-color: #ffebee;
  border-color: #e57373;
}

/* 彈窗樣式 */
.modal-overlay {
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
  animation: fadeIn 0.2s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-container {
  background-color: white;
  border-radius: 8px;
  width: 100%;
  max-width: 500px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  animation: zoomIn 0.3s ease-out;
}

@keyframes zoomIn {
  from { transform: scale(0.95); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}

.modal-header {
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eee;
}

.modal-header h2 {
  margin: 0;
  font-size: 18px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  color: #999;
  transition: color 0.2s;
}

.close-btn:hover {
  color: #333;
}

.modal-body {
  padding: 20px;
}

.modal-footer {
  padding: 15px 20px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  border-top: 1px solid #eee;
}

/* 檢舉表單 */
.report-section {
  margin-bottom: 20px;
}

.report-label {
  margin-bottom: 8px;
  font-weight: bold;
}

.report-select,
.report-textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.report-select:focus,
.report-textarea:focus {
  border-color: #81d8d0;
  outline: none;
  box-shadow: 0 0 0 2px rgba(129, 216, 208, 0.2);
}

.report-textarea {
  min-height: 100px;
  resize: vertical;
}

.report-warning {
  margin-top: 10px;
  padding: 10px;
  background-color: #fff3e0;
  border-radius: 4px;
  color: #e65100;
  font-size: 14px;
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

/* 回覆表單 */
.reply-review-info {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f5f5;
  border-radius: 8px;
}

.reply-user {
  font-weight: bold;
  margin-bottom: 5px;
}

.reply-rating {
  margin-bottom: 10px;
  color: #ffc107;
}

.reply-content-preview {
  color: #555;
  font-size: 14px;
  max-height: 80px;
  overflow-y: auto;
}

.reply-form {
  margin-bottom: 20px;
}

.reply-label {
  margin-bottom: 8px;
  font-weight: bold;
}

.reply-textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  min-height: 120px;
  resize: vertical;
  margin-bottom: 10px;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.reply-textarea:focus {
  border-color: #81d8d0;
  outline: none;
  box-shadow: 0 0 0 2px rgba(129, 216, 208, 0.2);
}

.reply-info {
  padding: 10px;
  background-color: #e0f7fa;
  border-radius: 4px;
  color: #0097a7;
  font-size: 14px;
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

/* 按鈕樣式 */
.cancel-btn,
.submit-btn {
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.cancel-btn {
  background-color: #f5f5f5;
  color: #333;
  border: 1px solid #ddd;
}

.cancel-btn:hover {
  background-color: #e0e0e0;
}

.submit-btn {
  background-color: #81d8d0;
  color: white;
  border: none;
}

.submit-btn:hover:not(:disabled) {
  background-color: #5dc2b9;
}

.submit-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .review-advantages-disadvantages {
    flex-direction: column;
  }
  
  .rating-details {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .rating-item {
    flex-direction: row;
    justify-content: space-between;
    width: 100%;
  }
  
  .review-actions {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
  
  .other-actions {
    width: 100%;
    justify-content: space-between;
  }
}
</style>