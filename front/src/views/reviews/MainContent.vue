<template>
  <div class="main-content">
    <!-- 切換視圖按鈕 (只在查看評價時顯示) -->
    <div v-if="currentView === 'reviews'" class="back-button mb-3">
      <button class="btn btn-outline-secondary" @click="goHome">
        <i class="bi bi-arrow-left me-1"></i>
        返回營地頁面
      </button>
    </div>

    <!-- 使用v-if和v-else-if確保只有一個視圖會顯示 -->
    <CampSiteConnector 
      v-if="currentView === 'connector'"
      @view-reviews="changeView('reviews')"
    />

    <ReviewsView 
      v-else-if="currentView === 'reviews'"
      :currentUser="currentUser"
      @handle-report="$emit('handle-report', $event)"
    />
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { defineEmits, defineProps } from 'vue'
import CampSiteConnector from '@/views/reviews/CampSiteConnector.vue'
import ReviewsView from '@/views/reviews/ReviewsView.vue'

const props = defineProps({
  currentUser: {
    type: Object,
    required: true  
  },
  currentView: {
    type: String,
    default: 'connector'
  }
});

// 定義 emits
const emit = defineEmits(['view-changed', 'handle-report']);

// 日誌
console.log('MainContent 初始化，當前視圖：', props.currentView);

// 切換視圖方法
const changeView = (view) => {
  console.log('MainContent: 切換視圖為', view);
  emit('view-changed', view);
};

// Router
const router = useRouter()

// 返回首頁
const goHome = () => {
  router.push('/')
}
</script>

<style scoped>
.main-content {
  margin-bottom: 30px;
}

.back-button {
  max-width: 1200px;
  margin: 0 auto 15px;
  padding: 0 20px;
}
</style>