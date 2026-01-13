<template>
  <div class="favorites-container">
    <div class="favorites-header">
      <h1>我的收藏</h1>
      <p>管理您收藏的露營地</p>
    </div>
    
    <!-- 載入中提示 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>載入中...</p>
    </div>
    
    <!-- 空狀態 -->
    <div v-else-if="favorites.length === 0" class="empty-state">
      <i class="fa fa-heart"></i>
      <p>您尚未收藏任何露營地</p>
      <router-link to="/camping" class="btn-explore">探索露營地</router-link>
    </div>
    
    <!-- 收藏列表 -->
    <div v-else class="favorites-grid">
      <div class="favorite-card" v-for="camp in favorites" :key="camp.id">
        <button class="btn-remove" @click="removeFavorite(camp.id)" :disabled="removingIds.includes(camp.id)">
          <i class="fa" :class="[removingIds.includes(camp.id) ? 'fa-spinner fa-spin' : 'fa-times']"></i>
        </button>
        
        <div class="favorite-image" :style="{ backgroundImage: `url(${camp.image})` }">
          <div class="favorite-price">
            NT$ {{ formatNumber(camp.price) }} <span>/ 晚</span>
          </div>
        </div>
        
        <div class="favorite-info">
          <h3>{{ camp.name }}</h3>
          <p class="favorite-location">
            <i class="fa fa-map-marker-alt"></i> {{ camp.location }}
          </p>
          <div class="favorite-rating">
            <span class="stars">
              <i class="fa fa-star"></i>
              <span>{{ camp.rating.toFixed(1) }}</span>
            </span>
            <span class="reviews">{{ camp.reviewCount }} 則評價</span>
          </div>
          <p class="favorite-features">
            <span v-if="camp.hasPool"><i class="fa fa-swimming-pool"></i> 泳池</span>
            <span v-if="camp.allowsPets"><i class="fa fa-paw"></i> 寵物友善</span>
            <span v-if="camp.hasWifi"><i class="fa fa-wifi"></i> Wifi</span>
          </p>
        </div>
        
        <div class="favorite-actions">
          <button @click="viewCampDetail(camp.id)" class="btn-view">
            查看詳情
          </button>
          <button @click="makeBooking(camp.id)" class="btn-book">
            立即預約
          </button>
        </div>
      </div>
    </div>
    
    <!-- 確認移除對話框 -->
    <div v-if="showRemoveModal" class="modal-overlay">
      <div class="modal">
        <div class="modal-header">
          <h2>移除收藏</h2>
          <button @click="closeRemoveModal" class="btn-close" :disabled="removeLoading">
            <i class="fa fa-times"></i>
          </button>
        </div>
        
        <div class="modal-body">
          <p>確定要將此露營地從收藏中移除嗎？</p>
        </div>
        
        <div class="modal-footer">
          <button @click="closeRemoveModal" class="btn-cancel-modal" :disabled="removeLoading">取消</button>
          <button @click="confirmRemove" class="btn-confirm" :disabled="removeLoading">
            <i v-if="removeLoading" class="fa fa-spinner fa-spin mr-2"></i>
            {{ removeLoading ? '移除中...' : '確認移除' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useToast } from '@/composables/useToast';
import apiService from '@/services/api/apiService';

export default {
  name: 'FavoritesView',
  setup() {
    const router = useRouter();
    const { showToast } = useToast();
    const favorites = ref([]);
    const loading = ref(true);
    const showRemoveModal = ref(false);
    const removeLoading = ref(false);
    const currentCampId = ref(null);
    const removingIds = ref([]);
    
    // 獲取收藏列表
    const fetchFavorites = async () => {
      loading.value = true;
      try {
        const response = await apiService.get('/users/favorites');
        favorites.value = response.data;
      } catch (error) {
        console.error('獲取收藏失敗:', error);
        showToast('獲取收藏失敗，請稍後再試', 'error');
      } finally {
        loading.value = false;
      }
    };
    
    // 格式化數字
    const formatNumber = (num) => {
      return new Intl.NumberFormat('zh-TW').format(num);
    };
    
    // 查看露營地詳情
    const viewCampDetail = (campId) => {
      router.push(`/camping/detail/${campId}`);
    };
    
    // 預約露營地
    const makeBooking = (campId) => {
      router.push(`/camping/detail/${campId}?booking=true`);
    };
    
    // 移除收藏 (彈出確認對話框)
    const removeFavorite = (campId) => {
      currentCampId.value = campId;
      showRemoveModal.value = true;
    };
    
    // 快速移除收藏 (不彈對話框)
    const quickRemoveFavorite = async (campId) => {
      if (removingIds.value.includes(campId)) return;
      
      removingIds.value.push(campId);
      
      try {
        await apiService.delete(`/favorites/${campId}`);
        favorites.value = favorites.value.filter(camp => camp.id !== campId);
        showToast('已從收藏中移除', 'success');
      } catch (error) {
        console.error('移除收藏失敗:', error);
        showToast('移除收藏失敗: ' + (error.response?.data?.message || '未知錯誤'), 'error');
      } finally {
        removingIds.value = removingIds.value.filter(id => id !== campId);
      }
    };
    
    // 關閉移除對話框
    const closeRemoveModal = () => {
      if (removeLoading.value) return;
      showRemoveModal.value = false;
    };
    
    // 確認移除
    const confirmRemove = async () => {
      removeLoading.value = true;
      try {
        await apiService.delete(`/favorites/${currentCampId.value}`);
        favorites.value = favorites.value.filter(camp => camp.id !== currentCampId.value);
        showToast('已從收藏中移除', 'success');
        showRemoveModal.value = false;
      } catch (error) {
        console.error('移除收藏失敗:', error);
        showToast('移除收藏失敗: ' + (error.response?.data?.message || '未知錯誤'), 'error');
      } finally {
        removeLoading.value = false;
      }
    };
    
    onMounted(() => {
      fetchFavorites();
    });
    
    return {
      favorites,
      loading,
      showRemoveModal,
      removeLoading,
      removingIds,
      formatNumber,
      viewCampDetail,
      makeBooking,
      removeFavorite,
      closeRemoveModal,
      confirmRemove
    };
  }
};
</script>

<style scoped>
.favorites-container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 20px;
}

.favorites-header {
  margin-bottom: 30px;
}

.favorites-header h1 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.favorites-header p {
  color: #6b7280;
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

.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.favorite-card {
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  position: relative;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.favorite-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.15);
}

.btn-remove {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.9);
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 1;
  color: #ef4444;
  transition: background-color 0.3s;
}

.btn-remove:hover:not(:disabled) {
  background-color: #ef4444;
  color: white;
}

.btn-remove:disabled {
  cursor: not-allowed;
  opacity: 0.7;
}

.favorite-image {
  height: 200px;
  background-size: cover;
  background-position: center;
  position: relative;
}

.favorite-price {
  position: absolute;
  bottom: 10px;
  left: 10px;
  background-color: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 5px 10px;
  border-radius: 5px;
  font-weight: bold;
}

.favorite-price span {
  font-weight: normal;
  font-size: 14px;
}

.favorite-info {
  padding: 15px;
}

.favorite-info h3 {
  margin-bottom: 10px;
  color: #2c3e50;
}

.favorite-location {
  color: #6b7280;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
}

.favorite-location i {
  margin-right: 5px;
}

.favorite-rating {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.stars {
  display: flex;
  align-items: center;
  color: #facc15;
  margin-right: 10px;
}

.stars i {
  margin-right: 5px;
}

.reviews {
  color: #6b7280;
  font-size: 14px;
}

.favorite-features {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 15px;
}

.favorite-features span {
  font-size: 14px;
  color: #6b7280;
  display: flex;
  align-items: center;
}

.favorite-features span i {
  margin-right: 5px;
}

.favorite-actions {
  display: flex;
  padding: 0 15px 15px;
  gap: 10px;
}

.favorite-actions button {
  flex: 1;
  padding: 10px 0;
  border-radius: 5px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
}

.btn-view {
  background-color: #f3f4f6;
  color: #4b5563;
}

.btn-view:hover {
  background-color: #e5e7eb;
}

.btn-book {
  background-color: #0fb886;
  color: white;
}

.btn-book:hover {
  background-color: #0d9e75;
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
}

.modal {
  background-color: white;
  border-radius: 10px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e5e7eb;
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
  text-align: center;
}

.modal-footer {
  padding: 20px;
  border-top: 1px solid #e5e7eb;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
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
  .favorites-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .favorite-actions {
    flex-direction: column;
  }
}
</style>