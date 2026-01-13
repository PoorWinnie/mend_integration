<template>
  <div class="reviews-view">
    <!-- 評價相關功能（所有用戶可見） -->
    <ReviewFilter 
      @filter-applied="applyFilters"
      @add-review="handleAddReviewClick"
      :canAddReview="true"
    />
    
    <ReviewsList 
      :campSiteId="1"
      :filters="filters"
      :currentUser="currentUser"
      @handle-report="handleReport"
      ref="reviewsListRef"
    />

    <!-- 新增評價 Modal -->
    <div class="modal fade" id="addReviewModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">新增評價</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <AddReviewForm 
              :campSiteId="1"
              :userId="currentUser.id"
              :userName="currentUser.name"
              @review-added="handleReviewAdded"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 檢舉 Modal -->
    <div class="modal fade" id="handleReportModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">處理檢舉</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <div v-if="selectedReport">
              <div class="alert alert-info">
                <strong>檢舉類型：</strong> {{ selectedReport.target === 'review' ? '評價' : '回覆' }}
                <hr>
                <strong>檢舉原因：</strong> {{ getReportTypeName(selectedReport.reportType) }}
                <hr>
                <strong>詳細說明：</strong> {{ selectedReport.reason }}
              </div>
              
              <div class="form-group mb-3">
                <label for="handlerNote" class="form-label">處理備註：</label>
                <textarea id="handlerNote" class="form-control" v-model="reportHandlerNote" rows="3"></textarea>
              </div>
              
              <div class="d-flex gap-2 justify-content-end">
                <button class="btn btn-outline-secondary" data-bs-dismiss="modal">取消</button>
                <button class="btn btn-danger" @click="approveReport">批准檢舉並移除內容</button>
                <button class="btn btn-success" @click="rejectReport">駁回檢舉</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue';
import { Modal } from 'bootstrap';
import axiosapi from '@/plugins/axios.js';
import Swal from 'sweetalert2';

import AdminReportManager from '@/views/reviews/AdminReportManager.vue';
import ReviewFilter from '@/views/reviews/ReviewFilter.vue';
import ReviewsList from '@/views/reviews/ReviewsList.vue';
import AddReviewForm from '@/views/reviews/AddReviewForm.vue';

export default {
  name: 'ReviewsView',
  components: {
    AdminReportManager,
    ReviewFilter,
    ReviewsList,
    AddReviewForm
  },
  props: {
    currentUser: {
      type: Object,
      required: true
    }
  },
  emits: ['handle-report'],
  setup(props, { emit }) {

    const filters = reactive({
      keyword: '',
      sortBy: 'createdAt',
      direction: 'DESC',
      minRating: 0
    });

    const addReviewModal = ref(null);
    const handleReportModal = ref(null);
    const reviewsListRef = ref(null);
    const selectedReport = ref(null);
    const reportHandlerNote = ref('');

    const reportTypes = {
      1: '不實內容',
      2: '冒犯性內容或語言',
      3: '廣告或垃圾訊息',
      4: '暴力或仇恨言論',
      5: '侵犯隱私權',
      6: '其他'
    };

    const getReportTypeName = (type) => reportTypes[type] || '未知';

    const applyFilters = (newFilters) => {
      Object.assign(filters, newFilters);
    };

    const openAddReviewModal = () => {
      const modalElement = document.getElementById('addReviewModal');
      if (modalElement) {
        const modal = new Modal(modalElement);
        modal.show();
        addReviewModal.value = modal;
      }
    };

    const closeAddReviewModal = () => {
      const modalElement = document.getElementById('addReviewModal');
      if (modalElement) {
        try {
          addReviewModal.value?.hide();
          Modal.getInstance(modalElement)?.hide();
        } catch (e) {
          console.log('關閉新增評價模態視窗失敗:', e);
        }
      }
    };

    const handleAddReviewClick = () => {
      openAddReviewModal();
    };

    const handleReviewAdded = () => {
      closeAddReviewModal();

      Swal.fire({
                    icon: 'success',
                    title: '成功',
                    text: '評價已成功添加！',
                    confirmButtonText: '確定',
                })
      localStorage.setItem('currentView', 'reviews');

      if (reviewsListRef.value?.fetchReviews) {
        setTimeout(() => {
          reviewsListRef.value.fetchReviews();
        }, 100);
      }
    };

    const handleReport = (report) => {
      selectedReport.value = report;
      reportHandlerNote.value = '';

      const modalElement = document.getElementById('handleReportModal');
      if (modalElement) {
        const modal = new Modal(modalElement);
        modal.show();
        handleReportModal.value = modal;
      }
    };

    const closeReportModal = () => {
      const modalElement = document.getElementById('handleReportModal');
      if (modalElement) {
        try {
          handleReportModal.value?.hide();
          Modal.getInstance(modalElement)?.hide();
        } catch (e) {
          console.log('關閉檢舉模態視窗失敗:', e);
        }
      }
    };

    const approveReport = async () => {
      try {
        if (!selectedReport.value) return;

        await axiosapi.put(`/api/public/review-reports/${selectedReport.value.id}/approve-and-remove`, {
          handlerNote: reportHandlerNote.value
        });

        closeReportModal();
        selectedReport.value = null;
        alert('檢舉已批准，內容已移除');

        localStorage.setItem('currentView', 'reviews');
        if (reviewsListRef.value?.fetchReviews) {
          setTimeout(() => {
            reviewsListRef.value.fetchReviews();
          }, 100);
        }
      } catch (error) {
        console.error('處理檢舉失敗:', error);
        alert('處理失敗，請稍後再試');
      }
    };

    const rejectReport = async () => {
      try {
        if (!selectedReport.value) return;

        await axiosapi.put(`/api/public/review-reports/${selectedReport.value.id}/process`, {
          status: 'rejected',
          handlerNote: reportHandlerNote.value
        });

        closeReportModal();
        selectedReport.value = null;
        alert('檢舉已駁回，內容已恢復可見');

        localStorage.setItem('currentView', 'reviews');
        if (reviewsListRef.value?.fetchReviews) {
          setTimeout(() => {
            reviewsListRef.value.fetchReviews();
          }, 100);
        }
      } catch (error) {
        console.error('處理檢舉失敗:', error);
        alert('處理失敗，請稍後再試');
      }
    };

    return {
      filters,
      selectedReport,
      reportHandlerNote,
      reviewsListRef,
      getReportTypeName,
      applyFilters,
      handleAddReviewClick,
      handleReviewAdded,
      handleReport,
      approveReport,
      rejectReport
    };
  }
};
</script>

<style scoped>
/* 管理員控制區塊 */
.admin-controls {
  background-color: white;
  border-radius: 12px;
  padding: 15px 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  margin-bottom: 20px;
}

.admin-title {
  font-size: 1.3rem;
  margin: 0;
  color: var(--forest-dark);
}

.control-buttons .btn {
  position: relative;
  padding: 8px 16px;
  border-width: 0;
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.2s ease;
}

/* 評價管理按鈕 - 深藍色主題 */
.control-buttons .btn-admin-reviews {
  color: #0d6efd;
  background-color: rgba(13, 110, 253, 0.1);
}

.control-buttons .btn-admin-reviews:hover {
  background-color: rgba(13, 110, 253, 0.2);
}

.control-buttons .btn-admin-reviews.active {
  color: white;
  background-color: #0d6efd;
}

/* 檢舉管理按鈕 - 深紅色主題 */
.control-buttons .btn-admin-reports {
  color: #dc3545;
  background-color: rgba(220, 53, 69, 0.1);
}

.control-buttons .btn-admin-reports:hover {
  background-color: rgba(220, 53, 69, 0.2);
}

.control-buttons .btn-admin-reports.active {
  color: white;
  background-color: #dc3545;
}

@media (max-width: 768px) {
  .admin-controls {
    padding: 12px 15px;
  }
  
  .admin-controls .d-flex {
    flex-direction: column;
    gap: 10px;
  }
  
  .control-buttons {
    display: flex;
    width: 100%;
  }
  
  .control-buttons .btn {
    flex: 1;
  }
}
</style>