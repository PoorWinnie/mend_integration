<template>
  <div id="app">
    <!-- 會員端 -->
    <template v-if="!isAdminPage">
      <!-- 頂部導航欄 -->
      <AppHeader />

      <!-- 前台內容區域 -->
      <div class="app-container">
        <main>
          <router-view />
        </main>
      </div>

      <!-- 頁尾 -->
      <AppFooter />
    </template>

    <!-- 管理員後台佈局 -->
    <AdminLayout v-else>
      <router-view />
    </AdminLayout>

    <!-- 通用組件 -->
    <Toast />
    <ConfirmDialog />
  </div>
</template>

<script setup>
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle.min.js";
import { ref, computed,onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from './stores/auth';
import AppHeader from './components/AppHeader.vue';
import AppFooter from './components/AppFooter.vue';
import AdminLayout from './components/AdminLayout.vue';
import Toast from './components/Toast.vue';
import ConfirmDialog from './components/ConfirmDialog.vue';

// 路由和認證存儲
const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

// 計算是否在管理員頁面
const isAdminPage = computed(() => {
  return route.path.startsWith('/admin');
});


onMounted(() => {
  authStore.checkAndRestoreAuth();
});

</script>

<style>
/* 全局樣式 */
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+TC:wght@300;400;500;700&family=Playfair+Display:wght@400;700&display=swap');

:root {
  /* 真正的Tiffany藍綠色 */
  --tiffany-blue: #81D8D0;
  --tiffany-blue-light: #a5e8e2;
  --tiffany-blue-dark: #5ebfb7;
  --tiffany-blue-accent: #2ab3a9;
  --tiffany-bg: #f0f9f8;

  /* 輔助色彩 */
  --white: #ffffff;
  --gray-50: #f9fafb;
  --gray-100: #f3f4f6;
  --gray-200: #e5e7eb;
  --gray-300: #d1d5db;
  --gray-400: #9ca3af;
  --gray-500: #6b7280;
  --gray-600: #4b5563;
  --gray-700: #374151;
  --gray-800: #1f2937;
  --gray-900: #111827;

  /* 沿用原有設計的顏色 */
  --primary: #1e2838;
  /* 深藍色標題背景 */
  --primary-light: #2c3e58;
  --primary-dark: #15202c;
  --secondary: #606060;
  /* 灰色文字 */
  --accent: var(--tiffany-blue);
  /* Tiffany藍綠色 */
  --accent-hover: var(--tiffany-blue-dark);
  --background: #f8f8f8;
  --card-bg: var(--white);
  --text-primary: #333333;
  --text-secondary: #606060;
  --text-light: #a0a0a0;
  --border-color: #e0e0e0;

  /* 功能色 */
  --success: #10b981;
  --warning: #f59e0b;
  --danger: #ef4444;
  --info: #3b82f6;

  /* 陰影 */
  --shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.05);
  --shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  --shadow-md: 0 6px 10px rgba(0, 0, 0, 0.08);
  --shadow-lg: 0 10px 20px rgba(0, 0, 0, 0.1);
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Noto Sans TC', Arial, sans-serif;
  font-size: 16px;
  line-height: 1.6;
  color: var(--text-primary);
  background-color: var(--background);
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

a {
  text-decoration: none;
  color: inherit;
  transition: all 0.3s ease;
}

ul,
ol {
  list-style: none;
}

/* 保留基本佈局樣式，其他樣式移至對應組件中 */
.app-container {
  flex: 1;
  padding: 5px;
  margin-top: 40px;
}

/* 可以保留其他需要的全局樣式，或按需移至對應組件 */
</style>