<template>
  <div class="booking-container">
    <div class="booking-header">
      <h1>我的預約</h1>
      <p>管理您的露營預約</p>
    </div>
    
    <!-- 預約標籤頁 -->
    <div class="booking-tabs">
      <button 
        @click="activeTab = 'upcoming'" 
        :class="{ active: activeTab === 'upcoming' }"
      >
        即將到來 <span class="tab-count">({{ upcomingBookings.length }})</span>
      </button>
      <button 
        @click="activeTab = 'history'" 
        :class="{ active: activeTab === 'history' }"
      >
        歷史預約 <span class="tab-count">({{ historyBookings.length }})</span>
      </button>
      <button 
        @click="activeTab = 'canceled'" 
        :class="{ active: activeTab === 'canceled' }"
      >
        已取消 <span class="tab-count">({{ canceledBookings.length }})</span>
      </button>
    </div>
    
    <!-- 載入中提示 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>載入中...</p>
    </div>
    
    <!-- 即將到來的預約 -->
    <div v-else-if="activeTab === 'upcoming'" class="booking-list">
      <div v-if="upcomingBookings.length === 0" class="empty-state">
        <i class="fa fa-calendar-check"></i>
        <p>您目前沒有即將到來的預約</p>
        <router-link to="/camping" class="btn-explore">探索露營地</router-link>
      </div>
      
      <div v-else class="booking-card" v-for="booking in upcomingBookings" :key="booking.id">
        <div class="booking-date">
          <span class="month">{{ formatMonth(booking.checkInDate) }}</span>
          <span class="day">{{ formatDay(booking.checkInDate) }}</span>
        </div>
        
        <div class="booking-info">
          <h3>{{ booking.campName }}</h3>
          <div class="booking-details">
            <p><i class="fa fa-map-marker-alt"></i> {{ booking.location }}</p>
            <p><i class="fa fa-calendar-alt"></i> {{ formatDate(booking.checkInDate) }} 至 {{ formatDate(booking.checkOutDate) }}</p>
            <p><i class="fa fa-users"></i> {{ booking.guests }} 位旅客</p>
            <p><i class="fa fa-receipt"></i> 訂單編號: {{ booking.orderNumber }}</p>
          </div>
          <div class="booking-status" :class="booking.status.toLowerCase()">
            {{ getStatusText(booking.status) }}
          </div>
        </div>
        
        <div class="booking-actions">
          <button @click="viewBookingDetail(booking.id)" class="btn-view">
            <i class="fa fa-eye"></i> 查看詳情
          </button>
          <button 
            @click="cancelBooking(booking.id)" 
            class="btn-cancel" 
            :disabled="cancelingBookings.includes(booking.id)"
          >
            <i :class="[cancelingBookings.includes(booking.id) ? 'fa fa-spinner fa-spin' : 'fa fa-times']"></i>
            {{ cancelingBookings.includes(booking.id) ? '取消中...' : '取消預約' }}
          </button>
        </div>
      </div>
    </div>
    
    <!-- 歷史預約 -->
    <div v-else-if="activeTab === 'history'" class="booking-list">
      <div v-if="historyBookings.length === 0" class="empty-state">
        <i class="fa fa-history"></i>
        <p>您目前沒有歷史預約記錄</p>
        <router-link to="/camping" class="btn-explore">探索露營地</router-link>
      </div>
      
      <div v-else class="booking-card" v-for="booking in historyBookings" :key="booking.id">
        <div class="booking-date">
          <span class="month">{{ formatMonth(booking.checkInDate) }}</span>
          <span class="day">{{ formatDay(booking.checkInDate) }}</span>
        </div>
        
        <div class="booking-info">
          <h3>{{ booking.campName }}</h3>
          <div class="booking-details">
            <p><i class="fa fa-map-marker-alt"></i> {{ booking.location }}</p>
            <p><i class="fa fa-calendar-alt"></i> {{ formatDate(booking.checkInDate) }} 至 {{ formatDate(booking.checkOutDate) }}</p>
            <p><i class="fa fa-users"></i> {{ booking.guests }} 位旅客</p>
            <p><i class="fa fa-receipt"></i> 訂單編號: {{ booking.orderNumber }}</p>
          </div>
          <div class="booking-status completed">
            已完成
          </div>
        </div>
        
        <div class="booking-actions">
          <button @click="viewBookingDetail(booking.id)" class="btn-view">
            <i class="fa fa-eye"></i> 查看詳情
          </button>
          <button 
            v-if="!booking.hasReview" 
            @click="writeReview(booking)" 
            class="btn-review"
          >
            <i class="fa fa-star"></i> 撰寫評價
          </button>
          <span v-else class="review-completed">
            <i class="fa fa-check-circle"></i> 已評價
          </span>
        </div>
      </div>
    </div>
    
    <!-- 已取消的預約 -->
    <div v-else-if="activeTab === 'canceled'" class="booking-list">
      <div v-if="canceledBookings.length === 0" class="empty-state">
        <i class="fa fa-ban"></i>
        <p>您目前沒有已取消的預約</p>
        <router-link to="/camping" class="btn-explore">探索露營地</router-link>
      </div>
      
      <div v-else class="booking-card" v-for="booking in canceledBookings" :key="booking.id">
        <div class="booking-date">
          <span class="month">{{ formatMonth(booking.checkInDate) }}</span>
          <span class="day">{{ formatDay(booking.checkInDate) }}</span>
        </div>
        
        <div class="booking-info">
          <h3>{{ booking.campName }}</h3>
          <div class="booking-details">
            <p><i class="fa fa-map-marker-alt"></i> {{ booking.location }}</p>
            <p><i class="fa fa-calendar-alt"></i> {{ formatDate(booking.checkInDate) }} 至 {{ formatDate(booking.checkOutDate) }}</p>
            <p><i class="fa fa-users"></i> {{ booking.guests }} 位旅客</p>
            <p><i class="fa fa-receipt"></i> 訂單編號: {{ booking.orderNumber }}</p>
          </div>
          <div class="booking-status canceled">
            已取消
          </div>
        </div>
        
        <div class="booking-actions">
          <button @click="viewBookingDetail(booking.id)" class="btn-view">
            <i class="fa fa-eye"></i> 查看詳情
          </button>
          <button @click="rebookCamp(booking.campId)" class="btn-rebook">
            <i class="fa fa-redo"></i> 重新預約
          </button>
        </div>
      </div>
    </div>
    
    <!-- 評價對話框 -->
    <div v-if="showReviewModal" class="modal-overlay" @click.self="closeReviewModal">
      <div class="modal review-modal">
        <div class="modal-header">
          <h2>評價您的露營體驗</h2>
          <button @click="closeReviewModal" class="btn-close" :disabled="reviewLoading">
            <i class="fa fa-times"></i>
          </button>
        </div>
        
        <div class="modal-body">
          <div class="review-camp-info">
            <h3>{{ currentBooking.campName }}</h3>
            <p><i class="fa fa-calendar-alt"></i> {{ formatDate(currentBooking.checkInDate) }} 至 {{ formatDate(currentBooking.checkOutDate) }}</p>
          </div>
          
          <div class="rating-container">
            <p>整體評分</p>
            <div class="stars">
              <i 
                v-for="star in 5" 
                :key="star" 
                :class="[
                  'fa', 
                  star <= reviewForm.rating ? 'fa-star' : 'fa-star-o',
                  { 'active': star <= reviewForm.rating }
                ]"
                @click="reviewForm.rating = star"
              ></i>
            </div>
          </div>
          
          <div class="form-group">
            <label for="reviewTitle">標題</label>
            <input 
              type="text" 
              id="reviewTitle" 
              v-model="reviewForm.title" 
              placeholder="簡短描述您的體驗" 
              maxlength="50"
            />
            <div class="input-counter">{{ reviewForm.title.length }}/50</div>
          </div>
          
          <div class="form-group">
            <label for="reviewContent">評價內容</label>
            <textarea 
              id="reviewContent" 
              v-model="reviewForm.content" 
              rows="5" 
              placeholder="分享您的露營體驗..."
              maxlength="500"
            ></textarea>
            <div class="input-counter">{{ reviewForm.content.length }}/500</div>
          </div>
          
          <div class="form-group">
            <label>上傳照片 (可選)</label>
            <div class="photo-upload">
              <label for="reviewPhotos" class="photo-upload-btn">
                <i class="fa fa-camera"></i> 選擇照片
                <input 
                  type="file" 
                  id="reviewPhotos" 
                  accept="image/*" 
                  multiple 
                  @change="handlePhotoUpload" 
                  style="display: none;" 
                />
              </label>
              <span class="photo-hint">最多可上傳5張照片</span>
            </div>
            
            <div v-if="reviewForm.photos.length > 0" class="photo-preview">
              <div 
                v-for="(photo, index) in reviewForm.photos" 
                :key="index" 
                class="photo-item"
              >
                <img :src="photo.preview" alt="Review photo" />
                <button @click="removePhoto(index)" class="remove-photo">
                  <i class="fa fa-times"></i>
                </button>
              </div>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button @click="closeReviewModal" class="btn-cancel-modal" :disabled="reviewLoading">取消</button>
          <button @click="submitReview" class="btn-submit" :disabled="reviewLoading || !isReviewValid">
            <i v-if="reviewLoading" class="fa fa-spinner fa-spin mr-2"></i>
            {{ reviewLoading ? '提交中...' : '提交評價' }}
          </button>
        </div>
      </div>
    </div>
    
    <!-- 取消預約確認對話框 -->
    <div v-if="showCancelModal" class="modal-overlay" @click.self="closeCancelModal">
      <div class="modal">
        <div class="modal-header">
          <h2>取消預約</h2>
          <button @click="closeCancelModal" class="btn-close" :disabled="cancelLoading">
            <i class="fa fa-times"></i>
          </button>
        </div>
        
        <div class="modal-body">
          <p>確定要取消此預約嗎？請注意取消政策：</p>
          <ul class="cancel-policy">
            <li>入住日7天前取消，可獲得100%退款</li>
            <li>入住日3至7天前取消，可獲得50%退款</li>
            <li>入住日3天內取消，不予退款</li>
          </ul>
          
          <div class="form-group">
            <label for="cancelReason">取消原因 (可選)</label>
            <select id="cancelReason" v-model="cancelForm.reason">
              <option value="">請選擇原因</option>
              <option value="SCHEDULE_CONFLICT">行程衝突</option>
              <option value="WEATHER">天氣因素</option>
              <option value="PERSONAL">個人因素</option>
              <option value="OTHER">其他原因</option>
            </select>
          </div>
          
          <div v-if="cancelForm.reason === 'OTHER'" class="form-group">
            <label for="cancelNote">詳細說明</label>
            <textarea id="cancelNote" v-model="cancelForm.note" rows="3" placeholder="請說明取消原因..."></textarea>
          </div>
        </div>
        
        <div class="modal-footer">
          <button @click="closeCancelModal" class="btn-cancel-modal" :disabled="cancelLoading">返回</button>
          <button @click="confirmCancel" class="btn-confirm" :disabled="cancelLoading">
            <i v-if="cancelLoading" class="fa fa-spinner fa-spin mr-2"></i>
            {{ cancelLoading ? '處理中...' : '確認取消' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useToast } from '@/composables/useToast';
import apiService from '@/services/api/apiService';

export default {
  name: 'BookingView',
  setup() {
    const router = useRouter();
    const { showToast } = useToast();
    const bookings = ref([]);
    const loading = ref(true);
    const activeTab = ref('upcoming');
    const showReviewModal = ref(false);
    const showCancelModal = ref(false);
    const currentBookingId = ref(null);
    const currentBooking = ref({});
    const reviewLoading = ref(false);
    const cancelLoading = ref(false);
    const cancelingBookings = ref([]); // 用於追蹤正在取消中的訂單
    
    // 評價表單
    const reviewForm = reactive({
      rating: 0,
      title: '',
      content: '',
      photos: []
    });
    
    // 取消表單
    const cancelForm = reactive({
      reason: '',
      note: ''
    });
    
    // 評價表單驗證
    const isReviewValid = computed(() => {
      return (
        reviewForm.rating > 0 && 
        reviewForm.title.trim() !== '' && 
        reviewForm.content.trim() !== ''
      );
    });
    
    // 獲取預約分類
    const upcomingBookings = computed(() => {
      return bookings.value.filter(booking => 
        ['CONFIRMED', 'PENDING'].includes(booking.status) && 
        new Date(booking.checkInDate) >= new Date()
      );
    });
    
    const historyBookings = computed(() => {
      return bookings.value.filter(booking => 
        booking.status === 'COMPLETED' || 
        (booking.status === 'CONFIRMED' && new Date(booking.checkOutDate) < new Date())
      );
    });
    
    const canceledBookings = computed(() => {
      return bookings.value.filter(booking => booking.status === 'CANCELED');
    });
    
    // 獲取用戶預約
    const fetchBookings = async () => {
      loading.value = true;
      try {
        const response = await apiService.get('/users/bookings');
        bookings.value = response.data;
      } catch (error) {
        console.error('獲取預約失敗:', error);
        showToast('獲取預約失敗，請稍後再試', 'error');
      } finally {
        loading.value = false;
      }
    };
    
    // 查看預約詳情
    const viewBookingDetail = (bookingId) => {
      router.push(`/booking/${bookingId}`);
    };
    
    // 開啟取消預約對話框
    const cancelBooking = (bookingId) => {
      const booking = bookings.value.find(b => b.id === bookingId);
      if (!booking) return;
      
      currentBookingId.value = bookingId;
      cancelForm.reason = '';
      cancelForm.note = '';
      showCancelModal.value = true;
    };
    
    // 關閉取消預約對話框
    const closeCancelModal = () => {
      if (cancelLoading.value) return;
      showCancelModal.value = false;
    };
    
    // 確認取消預約
    const confirmCancel = async () => {
      cancelLoading.value = true;
      try {
        await apiService.put(`/bookings/${currentBookingId.value}/cancel`, {
          reason: cancelForm.reason,
          note: cancelForm.note
        });
        
        // 更新本地數據
        const bookingIndex = bookings.value.findIndex(b => b.id === currentBookingId.value);
        if (bookingIndex > -1) {
          bookings.value[bookingIndex].status = 'CANCELED';
        }
        
        showCancelModal.value = false;
        showToast('預約已成功取消', 'success');
      } catch (error) {
        console.error('取消預約失敗:', error);
        showToast('取消預約失敗: ' + (error.response?.data?.message || '未知錯誤'), 'error');
      } finally {
        cancelLoading.value = false;
      }
    };
    
    // 快速取消預約 (不彈對話框)
    const quickCancelBooking = async (bookingId) => {
      if (cancelingBookings.value.includes(bookingId)) return;
      
      cancelingBookings.value.push(bookingId);
      
      try {
        await apiService.put(`/bookings/${bookingId}/cancel`);
        
        // 更新本地數據
        const bookingIndex = bookings.value.findIndex(b => b.id === bookingId);
        if (bookingIndex > -1) {
          bookings.value[bookingIndex].status = 'CANCELED';
        }
        
        showToast('預約已成功取消', 'success');
      } catch (error) {
        console.error('取消預約失敗:', error);
        showToast('取消預約失敗: ' + (error.response?.data?.message || '未知錯誤'), 'error');
      } finally {
        cancelingBookings.value = cancelingBookings.value.filter(id => id !== bookingId);
      }
    };
    
    // 重新預約
    const rebookCamp = (campId) => {
      router.push(`/camping/detail/${campId}`);
    };
    
    // 寫評價
    const writeReview = (booking) => {
      currentBooking.value = booking;
      currentBookingId.value = booking.id;
      resetReviewForm();
      showReviewModal.value = true;
    };
    
    // 關閉評價對話框
    const closeReviewModal = () => {
      if (reviewLoading.value) return;
      showReviewModal.value = false;
    };
    
    // 重置評價表單
    const resetReviewForm = () => {
      reviewForm.rating = 0;
      reviewForm.title = '';
      reviewForm.content = '';
      reviewForm.photos = [];
    };
    
    // 處理照片上傳
    const handlePhotoUpload = (event) => {
      const files = Array.from(event.target.files);
      
      // 檢查照片數量限制
      if (reviewForm.photos.length + files.length > 5) {
        showToast('最多只能上傳5張照片', 'error');
        return;
      }
      
      // 處理每個照片文件
      files.forEach(file => {
        // 檢查文件類型
        if (!file.type.match('image.*')) {
          showToast('請上傳圖片文件', 'error');
          return;
        }
        
        // 檢查文件大小 (限制為2MB)
        if (file.size > 2 * 1024 * 1024) {
          showToast('圖片大小不能超過2MB', 'error');
          return;
        }
        
        // 創建預覽URL並添加到照片列表
        const reader = new FileReader();
        reader.onload = (e) => {
          reviewForm.photos.push({
            file: file,
            preview: e.target.result
          });
        };
        reader.readAsDataURL(file);
      });
      
      // 重置文件輸入以允許重複選擇相同文件
      event.target.value = '';
    };
    
    // 移除照片
    const removePhoto = (index) => {
      reviewForm.photos.splice(index, 1);
    };
    
    // 提交評價
    const submitReview = async () => {
      if (!isReviewValid.value) {
        showToast('請填寫完整評價信息', 'error');
        return;
      }
      
      reviewLoading.value = true;
      try {
        // 首先上傳圖片（如果有）
        let photoUrls = [];
        if (reviewForm.photos.length > 0) {
          const formData = new FormData();
          reviewForm.photos.forEach(photo => {
            formData.append('photos', photo.file);
          });
          
          const photoResponse = await apiService.post('/uploads/review-photos', formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          });
          
          photoUrls = photoResponse.data.urls || [];
        }
        
        // 提交評價
        await apiService.post(`/bookings/${currentBookingId.value}/review`, {
          rating: reviewForm.rating,
          title: reviewForm.title,
          content: reviewForm.content,
          photos: photoUrls
        });
        
        // 更新本地數據
        const bookingIndex = bookings.value.findIndex(b => b.id === currentBookingId.value);
        if (bookingIndex > -1) {
          bookings.value[bookingIndex].hasReview = true;
        }
        
        showReviewModal.value = false;
        showToast('評價已成功提交，感謝您的回饋！', 'success');
      } catch (error) {
        console.error('提交評價失敗:', error);
        showToast('提交評價失敗: ' + (error.response?.data?.message || '未知錯誤'), 'error');
      } finally {
        reviewLoading.value = false;
      }
    };
    
    // 格式化日期
    const formatDate = (dateString) => {
      const date = new Date(dateString);
      return new Intl.DateTimeFormat('zh-TW', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      }).format(date);
    };
    
    // 格式化月份
    const formatMonth = (dateString) => {
      const date = new Date(dateString);
      return new Intl.DateTimeFormat('zh-TW', { month: 'short' }).format(date);
    };
    
    // 格式化日
    const formatDay = (dateString) => {
      const date = new Date(dateString);
      return date.getDate();
    };
    
    // 獲取狀態文字
    const getStatusText = (status) => {
      const statusMap = {
        'PENDING': '待確認',
        'CONFIRMED': '已確認',
        'COMPLETED': '已完成',
        'CANCELED': '已取消'
      };
      return statusMap[status] || status;
    };
    
    onMounted(() => {
      fetchBookings();
    });
    
    return {
      bookings,
      loading,
      activeTab,
      upcomingBookings,
      historyBookings,
      canceledBookings,
      showReviewModal,
      showCancelModal,
      currentBooking,
      reviewForm,
      cancelForm,
      reviewLoading,
      cancelLoading,
      cancelingBookings,
      isReviewValid,
      viewBookingDetail,
      cancelBooking,
      closeCancelModal,
      confirmCancel,
      rebookCamp,
      writeReview,
      closeReviewModal,
      handlePhotoUpload,
      removePhoto,
      submitReview,
      formatDate,
      formatMonth,
      formatDay,
      getStatusText
    };
  }
};
</script>

<style scoped>
.booking-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.booking-header {
  margin-bottom: 30px;
}

.booking-header h1 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.booking-header p {
  color: #6b7280;
}

.booking-tabs {
  display: flex;
  border-bottom: 1px solid #e5e7eb;
  margin-bottom: 20px;
}

.booking-tabs button {
  padding: 12px 20px;
  background: none;
  border: none;
  border-bottom: 2px solid transparent;
  color: #6b7280;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.tab-count {
  font-weight: normal;
  color: #94a3b8;
}

.booking-tabs button:hover {
  color: #0fb886;
}

.booking-tabs button.active {
  color: #0fb886;
  border-bottom-color: #0fb886;
}

.booking-tabs button.active .tab-count {
  color: #0fb886;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #6b7280;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #0fb886;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 10px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.empty-state {
  text-align: center;
  padding: 60px 0;
  color: #6b7280;
}

.empty-state i {
  font-size: 60px;
  margin-bottom: 20px;
  color: #d1d5db;
}

.empty-state p {
  margin-bottom: 20px;
  font-size: 18px;
}

.btn-explore {
  display: inline-block;
  padding: 10px 20px;
  background-color: #0fb886;
  color: white;
  border-radius: 5px;
  text-decoration: none;
  transition: background-color 0.3s;
}

.btn-explore:hover {
  background-color: #0d9e75;
}

.booking-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.booking-card {
  display: flex;
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  padding: 20px;
  gap: 20px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.booking-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.15);
}

.booking-date {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-width: 70px;
  border-right: 1px solid #e5e7eb;
  padding-right: 20px;
}

.booking-date .month {
  font-size: 14px;
  color: #6b7280;
  text-transform: uppercase;
}

.booking-date .day {
  font-size: 24px;
  font-weight: bold;
  color: #2c3e50;
}

.booking-info {
  flex: 1;
  position: relative;
}

.booking-info h3 {
  margin-bottom: 10px;
  color: #2c3e50;
}

.booking-details {
  color: #6b7280;
}

.booking-details p {
  margin-bottom: 8px;
  display: flex;
  align-items: center;
}

.booking-details i {
  margin-right: 10px;
  width: 16px;
  text-align: center;
}

.booking-status {
  position: absolute;
  top: 0;
  right: 0;
  padding: 5px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.booking-status.pending {
  background-color: #fff7ed;
  color: #ea580c;
}

.booking-status.confirmed {
  background-color: #ecfdf5;
  color: #16a34a;
}

.booking-status.completed {
  background-color: #f8fafc;
  color: #0e7490;
}

.booking-status.canceled {
  background-color: #fef2f2;
  color: #dc2626;
}

.booking-actions {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 10px;
  min-width: 120px;
}

.booking-actions button {
  padding: 8px 0;
  border-radius: 5px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
}

.booking-actions button i {
  margin-right: 5px;
}

.booking-actions button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn-view {
  background-color: #f9fafb;
  color: #4b5563;
}

.btn-view:hover {
  background-color: #f3f4f6;
}

.btn-cancel {
  background-color: #fee2e2;
  color: #dc2626;
}

.btn-cancel:hover:not(:disabled) {
  background-color: #fecaca;
}

.btn-review {
  background-color: #ecfdf5;
  color: #16a34a;
}

.btn-review:hover {
  background-color: #d1fae5;
}

.btn-rebook {
  background-color: #eff6ff;
  color: #3b82f6;
}

.btn-rebook:hover {
  background-color: #dbeafe;
}

.review-completed {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #16a34a;
  font-size: 14px;
}

.review-completed i {
  margin-right: 5px;
}

/* 模態窗口 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
  padding: 20px;
}

.modal {
  background-color: white;
  border-radius: 10px;
  width: 100%;
  max-width: 500px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  max-height: 90vh;
  overflow-y: auto;
}

.review-modal {
  max-width: 600px;
}

.modal-header {
  position: sticky;
  top: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e5e7eb;
  background-color: white;
  z-index: 2;
}

.modal-header h2 {
  margin: 0;
  color: #2c3e50;
}

.btn-close {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 18px;
  color: #6b7280;
}

.btn-close:disabled {
  cursor: not-allowed;
  opacity: 0.7;
}

.modal-body {
  padding: 20px;
}

.review-camp-info {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e5e7eb;
}

.review-camp-info h3 {
  margin-bottom: 10px;
  color: #2c3e50;
}

.review-camp-info p {
  display: flex;
  align-items: center;
  color: #6b7280;
}

.review-camp-info p i {
  margin-right: 8px;
}

.rating-container {
  margin-bottom: 20px;
  text-align: center;
}

.rating-container p {
  margin-bottom: 10px;
  color: #4b5563;
  font-weight: 500;
}

.stars {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 10px;
}

.stars i {
  font-size: 30px;
  color: #d1d5db;
  cursor: pointer;
  transition: all 0.3s;
}

.stars i:hover {
  transform: scale(1.1);
}

.stars i.active {
  color: #facc15;
}

.form-group {
  margin-bottom: 20px;
  position: relative;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #4b5563;
  font-weight: 500;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 10px;
  border: 1px solid #d1d5db;
  border-radius: 5px;
  font-size: 16px;
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}

.input-counter {
  position: absolute;
  right: 10px;
  bottom: 10px;
  font-size: 12px;
  color: #6b7280;
}

.photo-upload {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.photo-upload-btn {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 8px 15px;
  background-color: #f3f4f6;
  color: #4b5563;
  border-radius: 5px;
  cursor: pointer;
  transition: all 0.3s;
}

.photo-upload-btn:hover {
  background-color: #e5e7eb;
}

.photo-hint {
  font-size: 14px;
  color: #6b7280;
}

.photo-preview {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
}

.photo-item {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: 5px;
  overflow: hidden;
}

.photo-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-photo {
  position: absolute;
  top: 5px;
  right: 5px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.remove-photo:hover {
  background-color: rgba(239, 68, 68, 0.8);
}

.cancel-policy {
  padding-left: 20px;
  margin-bottom: 20px;
}

.cancel-policy li {
  margin-bottom: 5px;
  color: #4b5563;
}

.modal-footer {
  position: sticky;
  bottom: 0;
  padding: 20px;
  border-top: 1px solid #e5e7eb;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  background-color: white;
  z-index: 2;
}

.btn-cancel-modal {
  padding: 10px 20px;
  background-color: #f3f4f6;
  color: #4b5563;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.btn-cancel-modal:disabled {
  cursor: not-allowed;
  opacity: 0.7;
}

.btn-submit {
  padding: 10px 20px;
  background-color: #0fb886;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-submit:disabled {
  background-color: #94d3a2;
  cursor: not-allowed;
}

.btn-confirm {
  padding: 10px 20px;
  background-color: #ef4444;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-confirm:disabled {
  background-color: #fca5a5;
  cursor: not-allowed;
}

.mr-2 {
  margin-right: 8px;
}

@media (max-width: 768px) {
  .booking-card {
    flex-direction: column;
  }
  
  .booking-date {
    border-right: none;
    border-bottom: 1px solid #e5e7eb;
    padding-right: 0;
    padding-bottom: 15px;
    flex-direction: row;
    gap: 10px;
  }
  
  .booking-actions {
    flex-direction: row;
    width: 100%;
  }
  
  .booking-actions button {
    flex: 1;
  }
  
  .booking-status {
    position: static;
    display: inline-block;
    margin-top: 10px;
  }
}

@media (max-width: 480px) {
  .booking-tabs {
    flex-direction: column;
    gap: 5px;
    border-bottom: none;
  }
  
  .booking-tabs button {
    text-align: left;
    border-left: 3px solid transparent;
    border-bottom: none;
    padding: 10px 15px;
  }
  
  .booking-tabs button.active {
    border-left-color: #0fb886;
    border-bottom: none;
    background-color: #f0f9f6;
  }
  
  .modal {
    max-width: 100%;
  }
  
  .stars i {
    font-size: 24px;
  }
  
  .booking-actions {
    flex-direction: column;
  }
  
  .modal-footer {
    flex-direction: column;
  }
  
  .btn-cancel-modal, .btn-submit, .btn-confirm {
    width: 100%;
  }
}
</style>Z