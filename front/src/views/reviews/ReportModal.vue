<!-- views/reviews/ReportModal.vue -->
<template>
  <div class="modal fade" id="reportModal" tabindex="-1" aria-labelledby="reportModalLabel" aria-hidden="true" ref="modalElement">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="reportModalLabel">檢舉{{ targetType === 'review' ? '評價' : '回覆' }}</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submitForm">
            <div class="form-group mb-3">
              <label for="reportReason">檢舉原因</label>
              <select class="form-select" id="reportReason" v-model="formData.reason" required>
                <option value="" disabled selected>請選擇檢舉原因</option>
                <option value="SPAM">垃圾訊息</option>
                <option value="HARASSMENT">騷擾/不當言論</option>
                <option value="HATE_SPEECH">仇恨言論</option>
                <option value="MISINFORMATION">錯誤資訊</option>
                <option value="VIOLENCE">暴力內容</option>
                <option value="OTHER">其他原因</option>
              </select>
            </div>
            <div class="form-group mb-3">
              <label for="reportDescription">詳細說明</label>
              <textarea 
                class="form-control" 
                id="reportDescription" 
                v-model="formData.description" 
                rows="4" 
                placeholder="請詳細說明檢舉原因..."
                required
              ></textarea>
            </div>
            <!-- 隱藏輸入欄位儲存評價ID -->
            <input type="hidden" v-model="formData.reviewId" />
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
          <button type="button" class="btn btn-danger" @click="submitForm" :disabled="loading">
            <span v-if="loading" class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>
            確認檢舉
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, nextTick, watch } from 'vue';
import { Modal } from 'bootstrap';
import axiosapi from '@/plugins/axios.js';
import Swal from 'sweetalert2';

export default {
  name: 'ReportModal',
  props: {
    review: {
      type: Object,
      default: () => null
    },
    targetType: {
      type: String,
      default: 'review',
      validator: (value) => ['review', 'reply'].includes(value)
    },
    userId: {
      type: [Number, String],
      required: true
    },
    reviewId: {
      type: [Number, String],
      default: null
    }
  },
  emits: ['reported'],
  setup(props, { emit }) {
    const modalElement = ref(null);
    let bootstrapModal = null;
    const loading = ref(false);
    
    const formData = reactive({
      reason: '',
      description: '',
      reviewId: null,
      targetType: props.targetType
    });
    
    // 監聽review和reviewId的變化
    watch(() => props.review, (newReview) => {
      if (newReview && newReview.id) {
        formData.reviewId = newReview.id;
      }
    }, { immediate: true });
    
    watch(() => props.reviewId, (newId) => {
      if (newId) {
        formData.reviewId = newId;
      }
    }, { immediate: true });
    
    // 初始化模態框
    onMounted(() => {
      nextTick(() => {
        if (modalElement.value) {
          bootstrapModal = new Modal(modalElement.value);
          console.log('Report Modal initialized successfully');
        } else {
          console.error('Report Modal element not found');
        }
      });
      
      // 初始化時設置reviewId
      if (props.review && props.review.id) {
        formData.reviewId = props.review.id;
      } else if (props.reviewId) {
        formData.reviewId = props.reviewId;
      }
    });
    
    // 顯示模態框的方法
    const show = (reviewIdParam) => {
      // 重置表單數據
      formData.reason = '';
      formData.description = '';
      
      // 優先使用傳入的reviewId參數
      if (reviewIdParam) {
        formData.reviewId = reviewIdParam;
        console.log(`準備檢舉${props.targetType === 'review' ? '評價' : '回覆'} ID: ${reviewIdParam}`);
      }
      // 如果沒有傳入參數，則使用props中的review
      else if (props.review && props.review.id) {
        formData.reviewId = props.review.id;
        console.log(`準備檢舉${props.targetType === 'review' ? '評價' : '回覆'} ID: ${props.review.id}`);
      }
      // 最後嘗試使用props中的reviewId
      else if (props.reviewId) {
        formData.reviewId = props.reviewId;
        console.log(`準備檢舉${props.targetType === 'review' ? '評價' : '回覆'} ID: ${props.reviewId}`);
      } else {
        console.warn('警告: 未提供評價ID，檢舉功能可能無法正常運作');
      }
      
      formData.targetType = props.targetType;
      
      // 確保模態框實例已創建
      nextTick(() => {
        if (!bootstrapModal && modalElement.value) {
          bootstrapModal = new Modal(modalElement.value);
        }
        
        // 顯示模態框
        if (bootstrapModal) {
          bootstrapModal.show();
        } else {
          console.error('無法初始化檢舉模態框');
        }
      });
    };
    
    // 提交表單 - 修正版本
    const submitForm = async () => {
      // 表單驗證
      if (!formData.reason) {
        Swal.fire({
          icon: 'warning',
          title: '請選擇檢舉原因',
          confirmButtonText: '確定'
        });
        return;
      }
      
      if (!formData.description || formData.description.trim().length < 10) {
        Swal.fire({
          icon: 'warning',
          title: '詳細說明不足',
          text: '請提供詳細的檢舉說明 (至少10個字符)',
          confirmButtonText: '確定'
        });
        return;
      }
      
      // 確保必要的ID存在
      if (!formData.reviewId) {
        let possibleId = null;
        if (props.review && props.review.id) {
          possibleId = props.review.id;
          formData.reviewId = possibleId;
        } else if (props.reviewId) {
          possibleId = props.reviewId;
          formData.reviewId = possibleId;
        }
        
        if (!formData.reviewId) {
          console.error('檢舉失敗: 缺少評價ID，無法從任何來源獲取ID');
          Swal.fire({
            icon: 'error',
            title: '檢舉失敗',
            text: '缺少必要的評價資訊，請重新操作或聯繫客服',
            confirmButtonText: '確定'
          });
          return;
        }
      }
      
      // 確保用戶ID存在
      if (!props.userId) {
        console.error('檢舉失敗: 缺少用戶ID');
        Swal.fire({
          icon: 'error',
          title: '檢舉失敗',
          text: '請先登入才能進行檢舉',
          confirmButtonText: '確定'
        });
        return;
      }
      
      try {
        loading.value = true;
        
        // 先嘗試簡化版本，只提供基本字段
        const simpleData = {
          userId: Number(props.userId), // 使用駝峰式命名（可能實體類使用駝峰命名法）
          reviewId: Number(formData.reviewId),
          reason: formData.reason,
          reportDescription: formData.description,
          reportTarget: formData.targetType.toUpperCase(),
          reportType: formData.targetType === 'review' ? 1 : 2,
          status: "PENDING"
        };
        
        console.log('提交檢舉數據 (簡化版):', simpleData);
        
        // 嘗試特定端點
        const specificEndpoint = formData.targetType === 'review' 
                          ? '/api/public/review-reports/review' 
                          : '/api/public/review-reports/reply';
        
        try {
          const response = await axiosapi.post(specificEndpoint, simpleData);
          console.log('檢舉提交成功:', response.data);
          handleSuccess();
          return;
        } catch (specificError) {
          console.log('特定端點失敗，嘗試通用端點...');
          
          // 嘗試通用端點
          try {
            const response = await axiosapi.post('/api/public/review-reports', simpleData);
            console.log('檢舉提交成功 (通用端點):', response.data);
            handleSuccess();
            return;
          } catch (genericError) {
            console.log('通用端點也失敗，嘗試 ReviewReport 對象...');
            
            // 嘗試提供完整的 ReviewReport 對象
            const reviewReport = {
              id: null,
              reviewId: Number(formData.reviewId),
              reporterId: Number(props.userId),
              userId: Number(props.userId), // 嘗試多種變體
              user_id: Number(props.userId),
              reportReason: formData.reason,
              reason: formData.reason,
              reportDescription: formData.description,
              description: formData.description,
              reportTarget: formData.targetType.toUpperCase(),
              report_target: formData.targetType.toUpperCase(),
              reportType: formData.targetType === 'review' ? 1 : 2,
              report_type: formData.targetType === 'review' ? 1 : 2,
              status: "PENDING",
              reportDate: new Date().toISOString(),
              reportedAt: new Date().toISOString(),
              created_at: new Date().toISOString(),
              updated_at: new Date().toISOString(),
              isProcessed: false,
              handler_note: null,
              handlerNote: null
            };
            
            console.log('提交完整 ReviewReport 對象:', reviewReport);
            
            try {
              const response = await axiosapi.post(specificEndpoint, reviewReport);
              console.log('檢舉提交成功 (完整對象):', response.data);
              handleSuccess();
              return;
            } catch (fullObjectError) {
              console.log('完整對象也失敗，嘗試使用 DTO 對象...');
              
              // 最後嘗試：使用最接近 DTO 的結構
              const reportDTO = {
                reportReviewId: Number(formData.reviewId),
                reportContent: formData.description,
                reportReason: formData.reason,
                reportUserId: Number(props.userId),
                reportType: formData.targetType === 'review' ? 1 : 2,
                // 添加所有可能的用戶ID變體
                userId: Number(props.userId),
                reporterId: Number(props.userId),
                user_id: Number(props.userId),
                reporter_id: Number(props.userId)
              };
              
              console.log('提交最終的 DTO 嘗試:', reportDTO);
              
              try {
                // 嘗試多個可能的端點
                let finalResponse;
                try {
                  finalResponse = await axiosapi.post('/api/review-reports', reportDTO);
                } catch (e1) {
                  try {
                    finalResponse = await axiosapi.post('/api/reviews/reports', reportDTO);
                  } catch (e2) {
                    try {
                      finalResponse = await axiosapi.post('/api/reports/reviews', reportDTO);
                    } catch (e3) {
                      finalResponse = await axiosapi.post('/api/public/review-reports', reportDTO);
                    }
                  }
                }
                
                console.log('檢舉提交成功 (最終嘗試):', finalResponse.data);
                handleSuccess();
                return;
              } catch (finalError) {
                throw finalError; // 所有嘗試都失敗，拋出最後的錯誤
              }
            }
          }
        }
      } catch (error) {
        console.error('所有提交檢舉嘗試都失敗:', error);
        
        // 顯示詳細的錯誤信息
        let errorMessage = '請稍後再試';
        let errorDetails = '';
        
        if (error.response) {
          console.log('錯誤響應數據:', error.response.data);
          errorMessage = error.response.data.message || 
                        error.response.data.error || 
                        `伺服器錯誤 (${error.response.status})`;
                          
          if (errorMessage.includes('user_id')) {
            errorDetails = '用戶ID錯誤或缺失';
          } else if (errorMessage.includes('review_id')) {
            errorDetails = '評價ID錯誤或缺失';
          } else if (errorMessage.includes('constraint')) {
            errorDetails = '數據庫約束錯誤，某些必要欄位缺失或格式錯誤';
          }
        }
        
        Swal.fire({
          icon: 'error',
          title: '檢舉提交失敗',
          html: `
            <p>${errorMessage}</p>
            ${errorDetails ? `<p><small>${errorDetails}</small></p>` : ''}
            <p>請聯繫管理員提供以下信息:</p>
            <ul>
              <li>用戶ID: ${props.userId}</li>
              <li>評價ID: ${formData.reviewId}</li>
              <li>報告類型: ${formData.targetType}</li>
            </ul>
          `,
          confirmButtonText: '確定'
        });
      } finally {
        loading.value = false;
      }
    };
    
    // 處理成功提交
    const handleSuccess = () => {
      // 關閉模態框
      if (bootstrapModal) {
        bootstrapModal.hide();
      }
      
      // 通知父組件檢舉已提交
      emit('reported', {
        reviewId: formData.reviewId,
        success: true
      });
      
      // 顯示成功訊息
      Swal.fire({
        icon: 'success',
        title: '檢舉已提交',
        text: '管理員將會進行審核。謝謝您的反饋！',
        confirmButtonText: '確定'
      });
      
      // 重置表單
      formData.reason = '';
      formData.description = '';
    };
    
    return {
      modalElement,
      formData,
      loading,
      show,
      submitForm
    };
  }
}
</script>

<style scoped>
/* 使用 Bootstrap 預設樣式 */
.modal-header {
  background-color: #f8f9fa;
  border-bottom: 1px solid #dee2e6;
}

.modal-footer {
  background-color: #f8f9fa;
  border-top: 1px solid #dee2e6;
}

.btn-danger {
  background-color: #dc3545;
  border-color: #dc3545;
}

.btn-danger:hover {
  background-color: #bb2d3b;
  border-color: #b02a37;
}

.form-select:focus,
.form-control:focus {
  border-color: #86b7fe;
  box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
}
</style>