<template>
    <!-- 管理後台佈局 -->
    <div class="admin-layout">
        <!-- 側邊欄 -->
        <AdminSidebar v-if="!isAdminLoginPage" :collapsed="sidebarCollapsed" @toggle-sidebar="toggleSidebar" />

        <!-- 主內容區 -->
        <div class="admin-main" :class="{ 'full-width': isAdminLoginPage || sidebarCollapsed }">
            <!-- 頂部導航 -->
            <AdminHeader v-if="!isAdminLoginPage" @toggle-sidebar="toggleSidebar" />

            <!-- 頁面內容 -->
            <div class="admin-content">
                <slot></slot>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRoute } from 'vue-router';
import AdminSidebar from './AdminSidebar.vue';
import AdminHeader from './AdminHeader.vue';

// 路由和認證
const route = useRoute();

// 側邊欄狀態
const sidebarCollapsed = ref(false);

// 計算是否在管理員登入頁面
const isAdminLoginPage = computed(() => {
    return route.path === '/admin/login';
});

// 切換側邊欄折疊狀態
const toggleSidebar = () => {
    sidebarCollapsed.value = !sidebarCollapsed.value;
};
</script>

<style scoped>
/* 管理後台樣式 */
.admin-layout {
    display: flex;
    min-height: 100vh;
    background-color: var(--gray-100);
}

/* 主內容區域 */
.admin-main {
    margin-left: 260px;
    flex: 1;
    transition: all 0.3s ease;
    min-height: 100vh;
    display: flex;
    flex-direction: column;
}

.admin-main.full-width {
    margin-left: 80px;
}

/* 內容區 */
.admin-content {
    padding: 25px;
    background-color: var(--gray-100);
    flex: 1;
}

/* 響應式設計 */
@media (max-width: 1200px) {
    .admin-main {
        margin-left: 240px;
    }

    .admin-main.full-width {
        margin-left: 0;
    }
}

@media (max-width: 768px) {
    .admin-main {
        margin-left: 0;
    }

    .admin-content {
        padding: 15px;
    }
}
</style>