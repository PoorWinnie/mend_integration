<!-- ReplyEditModal.vue - 編輯回覆的模態窗口組件 -->
<template>
    <div class="modal fade" id="editReplyModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">編輯回覆</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <div v-if="review" class="review-preview mb-3">
              <div class="alert alert-secondary p-3">
                <div class="d-flex justify-content-between align-items-center mb-2">
                  <span class="fw-bold">{{ review.userName }}</span>
                  <div class="rating">
                    <i class="bi bi-star-fill text-warning me-1"></i>
                    <span>{{ review.overallRating }}/5</span>
                  </div>
                </div>
                <p class="mb-0">{{ review.reviewText }}</p>
              </div>
            </div>
            
            <form @submit.prevent="submitEdit">
              <div class="mb-3">
                <label for="editReplyText" class="form-label">回覆內容</label>
                <textarea 
                  id="editReplyText" 
                  class="form-control" 
                  v-model="editReplyText" 
                  rows="4" 
                  placeholder="請輸入您的回覆..."
                  required
                ></textarea>
              </div>
              
              <div class="alert alert-info">
                <i class="bi bi-info-circle-fill me-2"></i>
                作為營地主人，您的回覆將以官方身份顯示在評價下方。
              </div>
              
              <div class="d-flex justify-content-end">
                <button type="button" class="btn btn-outline-secondary me-2" data-bs-dismiss="modal">取消</button>
                <button type="submit" class="btn btn-success" :disabled="submitting">
                  <i class="bi" :class="submitting ? 'bi-hourglass-split' : 'bi-check-circle'"></i>
                  {{ submitting ? '更新中...' : '更新回覆' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
import { ref, onMounted, watch } from 'vue';
import { Modal } from 'bootstrap';
import axiosapi from '@/plugins/axios.js';

// Props 定义
const props = defineProps({
  review: {
    type: Object,
    default: null
  }
});

// Emits 定义
const emit = defineEmits(['edited']);

// 响应式数据
const modal = ref(null);
const editReplyText = ref('');
const submitting = ref(false);

// 当 review 变化时，更新回覆文字
watch(() => props.review, (newReview) => {
  if (newReview && newReview.replyText) {
    editReplyText.value = newReview.replyText;
  }
}, { immediate: true });

// 初始化模态窗口
onMounted(() => {
  const modalElement = document.getElementById('editReplyModal');
  if (modalElement) {
    modal.value = new Modal(modalElement);
  }
});

// 显示模态框
const show = () => {
  if (modal.value && props.review) {
    // 设置编辑文字为当前回复
    editReplyText.value = props.review.replyText || '';
    modal.value.show();
  }
};

// 隐藏模态框
const hide = () => {
  if (modal.value) {
    modal.value.hide();
  }
};

// 提交编辑
const submitEdit = async () => {
  if (!props.review) {
    alert('找不到要编辑的评价');
    return;
  }

  if (!editReplyText.value.trim()) {
    alert('请输入回复内容');
    return;
  }

  submitting.value = true;

  try {
    console.log(`提交编辑回复 ${props.review.id}...`);

    // 调用 API 提交编辑回复
    const response = await axiosapi.put(`/api/public/reviews/${props.review.id}/reply`, {
      replyText: editReplyText.value.trim()
    });

    console.log('编辑回复 API 响应:', response.data);

    // 关闭 Modal
    hide();

    // 通知父组件编辑成功
    emit('edited', props.review.id);

    // 保存当前视图状态到 localStorage
    localStorage.setItem('currentView', 'reviews');

    // 显示成功消息
    alert('回复已成功更新');
  } catch (error) {
    console.error('编辑回复失败:', error);
    alert('编辑回复失败: ' + (error.response?.data?.message || error.message || '请稍后再试'));
  } finally {
    submitting.value = false;
  }
};
</script>

<style scoped>
.review-preview .rating {
  display: flex;
  align-items: center;
}

.review-preview .fw-bold {
  color: var(--forest-dark);
}
</style>