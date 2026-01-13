<!-- views/reviews/ReviewEditModal.vue -->
<template>
  <div class="modal fade" id="reviewEditModal" tabindex="-1" aria-labelledby="reviewEditModalLabel" aria-hidden="true" ref="modalElement">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="reviewEditModalLabel">編輯評價</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submitForm">
            <!-- 整體評分 -->
            <div class="form-group mb-3">
              <label for="editOverallRating">整體評分</label>
              <div class="rating-select">
                <div class="stars-container">
                  <i v-for="star in 5" :key="'overall-'+star"
                     :class="['bi', star <= formData.overallRating ? 'bi-star-fill' : 'bi-star', 'star-icon']"
                     @click="formData.overallRating = star"></i>
                </div>
                <span class="rating-value">{{ formData.overallRating }}/5</span>
              </div>
            </div>
            
            <!-- 清潔度評分 -->
            <div class="form-group mb-3">
              <label for="editCleanlinessRating">清潔度評分</label>
              <div class="rating-select">
                <div class="stars-container">
                  <i v-for="star in 5" :key="'cleanliness-'+star"
                     :class="['bi', star <= formData.cleanlinessRating ? 'bi-star-fill' : 'bi-star', 'star-icon']"
                     @click="formData.cleanlinessRating = star"></i>
                </div>
                <span class="rating-value">{{ formData.cleanlinessRating }}/5</span>
              </div>
            </div>
            
            <!-- 便利性評分 -->
            <div class="form-group mb-3">
              <label for="editConvenienceRating">便利性評分</label>
              <div class="rating-select">
                <div class="stars-container">
                  <i v-for="star in 5" :key="'convenience-'+star"
                     :class="['bi', star <= formData.convenienceRating ? 'bi-star-fill' : 'bi-star', 'star-icon']"
                     @click="formData.convenienceRating = star"></i>
                </div>
                <span class="rating-value">{{ formData.convenienceRating }}/5</span>
              </div>
            </div>
            
            <!-- 友善度評分 -->
            <div class="form-group mb-3">
              <label for="editFriendlinessRating">友善度評分</label>
              <div class="rating-select">
                <div class="stars-container">
                  <i v-for="star in 5" :key="'friendliness-'+star"
                     :class="['bi', star <= formData.friendlinessRating ? 'bi-star-fill' : 'bi-star', 'star-icon']"
                     @click="formData.friendlinessRating = star"></i>
                </div>
                <span class="rating-value">{{ formData.friendlinessRating }}/5</span>
              </div>
            </div>

            <!-- 優點輸入 -->
            <div class="form-group mb-3">
              <label for="editPros">優點</label>
              <textarea class="form-control" id="editPros" v-model="formData.pros" 
                rows="2" placeholder="分享這個營地的優點..."></textarea>
            </div>

            <!-- 缺點輸入 -->
            <div class="form-group mb-3">
              <label for="editCons">缺點</label>
              <textarea class="form-control" id="editCons" v-model="formData.cons" 
                rows="2" placeholder="分享這個營地的缺點..."></textarea>
            </div>

            <!-- 詳細評論 -->
            <div class="form-group mb-3">
              <label for="editReviewText">評論內容</label>
              <textarea class="form-control" id="editReviewText" v-model="formData.reviewText" 
                rows="4" placeholder="分享您的詳細體驗..."></textarea>
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
          <button type="button" class="btn btn-primary" @click="submitForm">儲存變更</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, nextTick } from 'vue';
import { Modal } from 'bootstrap';
import axiosapi from '@/plugins/axios.js';

export default {
  name: 'ReviewEditModal',
  props: {
    review: {
      type: Object,
      default: () => null
    },
    campSiteId: {
      type: [Number, String],
      required: true
    }
  },
  emits: ['edited'],
  setup(props, { emit }) {
    const modalElement = ref(null);
    let bootstrapModal = null;
    
    // 擴展 formData 包含所有必要的評分欄位
    const formData = reactive({
      id: null,
      overallRating: 5,
      cleanlinessRating: 5, // 添加清潔度評分
      convenienceRating: 5, // 添加便利性評分
      friendlinessRating: 5, // 添加友善度評分
      pros: '',
      cons: '',
      reviewText: '',
      reviewIsVisible: true
    });
    
    // 初始化模態框
    onMounted(() => {
      nextTick(() => {
        if (modalElement.value) {
          bootstrapModal = new Modal(modalElement.value);
          console.log('Modal initialized successfully');
        } else {
          console.error('Modal element not found');
        }
      });
    });
    
    // 顯示模態框的方法
    const show = (reviewData) => {
      if (reviewData) {
        // 填充表單數據，確保所有必要欄位都有預設值
        formData.id = reviewData.id;
        formData.overallRating = reviewData.overallRating || 5;
        formData.cleanlinessRating = reviewData.cleanlinessRating || 5;
        formData.convenienceRating = reviewData.convenienceRating || 5;
        formData.friendlinessRating = reviewData.friendlinessRating || 5;
        formData.pros = reviewData.pros || '';
        formData.cons = reviewData.cons || '';
        formData.reviewText = reviewData.reviewText || '';
        formData.reviewIsVisible = reviewData.reviewIsVisible !== false;
        
        console.log('編輯模態框數據已設置:', formData);
      }
      
      // 確保模態框實例已創建
      nextTick(() => {
        if (!bootstrapModal && modalElement.value) {
          bootstrapModal = new Modal(modalElement.value);
        }
        
        // 顯示模態框
        if (bootstrapModal) {
          bootstrapModal.show();
        } else {
          console.error('無法初始化模態框');
        }
      });
    };
    
    // 提交表單
    const submitForm = async () => {
      try {
        console.log('提交編輯評價:', formData);
        
        // 確保傳送所有必要的評分欄位
        const response = await axiosapi.put(`/api/public/reviews/${formData.id}`, {
          overallRating: formData.overallRating,
          cleanlinessRating: formData.cleanlinessRating,
          convenienceRating: formData.convenienceRating,
          friendlinessRating: formData.friendlinessRating,
          pros: formData.pros,
          cons: formData.cons,
          reviewText: formData.reviewText,
          reviewIsVisible: formData.reviewIsVisible,
          campSiteId: props.campSiteId
        });
        
        console.log('評價更新成功:', response.data);
        
        // 關閉模態框
        if (bootstrapModal) {
          bootstrapModal.hide();
        }
        
        // 通知父組件評價已編輯成功
        emit('edited', response.data);
        
        // 顯示成功訊息
        alert('評價已成功更新!');
      } catch (error) {
        console.error('更新評價失敗:', error);
        alert('更新評價失敗: ' + (error.response?.data?.message || '請稍後再試'));
      }
    };
    
    return {
      modalElement,
      formData,
      show,
      submitForm
    };
  }
}
</script>

<style scoped>
.rating-select {
  display: flex;
  align-items: center;
  margin-top: 0.5rem;
}

.stars-container {
  display: flex;
  gap: 8px;
}

.star-icon {
  font-size: 1.5rem;
  color: #ffc107;
  cursor: pointer;
  transition: all 0.2s;
}

.star-icon:hover {
  transform: scale(1.2);
}

.rating-value {
  margin-left: 10px;
  font-weight: bold;
}
</style>