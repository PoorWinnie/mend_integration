<template>
  <div class="admin-reviews-view">
    <div class="admin-controls mb-4">
      <div class="d-flex justify-content-between align-items-center">
        <h2 class="admin-title"><i class="bi bi-shield-check me-2"></i>管理員專區</h2>
        <div class="control-buttons">
          <button 
            class="btn btn-admin-reviews me-2" 
            @click="adminView = 'reviews'"
            :class="{ active: adminView === 'reviews' }"
          >
            <i class="bi bi-list-stars me-1"></i>
            評價管理
          </button>
          <button 
            class="btn btn-admin-reports" 
            @click="adminView = 'reports'"
            :class="{ active: adminView === 'reports' }"
          >
            <i class="bi bi-flag-fill me-1"></i>
            檢舉管理
          </button>
        </div>
      </div>
    </div>

    <AdminReportManager v-if="adminView === 'reports'" :currentUser="currentUser" />

    <ReviewsList 
      v-if="adminView === 'reviews'"
      :campSiteId="1"
      :filters="filters"
        :currentUser="currentUser"
      ref="reviewsListRef"
    />
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import AdminReportManager from '@/views/reviews/AdminReportManager.vue';
import ReviewsList from '@/views/reviews/ReviewsList.vue';
import { useUserStore } from '@/stores/userStore';

const userStore = useUserStore();
const currentUser = userStore.currentUser;

// ✅ 開發測試用（正式環境請移除）
  userStore.setCurrentUser({
    id: 303,
    name: '測試用戶',
    roles: ['USER']
  });

const adminView = ref('reviews');
const filters = reactive({
  keyword: '',
  sortBy: 'createdAt',
  direction: 'DESC',
  minRating: 0
});

const reviewsListRef = ref(null);
</script>