<template>
    <!-- 頂部導航 -->
    <header class="admin-header">
        <div class="header-left">
            <button @click="toggleSidebar" class="sidebar-toggle-btn">
                <i class="fa fa-bars"></i>
            </button>
            <div class="breadcrumb">
                <span>系統管理</span>
                <i class="fa fa-chevron-right separator"></i>
                <span>{{ currentPageTitle }}</span>
            </div>
        </div>
        <div class="header-right">
            <div class="search-box">
                <i class="fa fa-search"></i>
                <input type="text" placeholder="搜尋..." />
            </div>
            <div class="header-actions">
                <button class="header-btn">
                    <i class="fa fa-bell"></i>
                    <span class="badge">3</span>
                </button>
                <button class="header-btn">
                    <i class="fa fa-envelope"></i>
                    <span class="badge">5</span>
                </button>
                <div class="user-dropdown" @click="toggleUserDropdown">
                    <i class="fa fa-user-circle"></i>
                    <span>{{ username }}</span>
                    <i class="fa fa-caret-down"></i>

                    <div class="dropdown-menu" v-if="userDropdownOpen">
                        <a href="#"><i class="fa fa-user"></i> 個人資料</a>
                        <a href="#"><i class="fa fa-cog"></i> 設定</a>
                        <div class="dropdown-divider"></div>
                        <a href="#" @click.prevent="handleLogout"><i class="fa fa-sign-out-alt"></i> 登出</a>
                    </div>
                </div>
            </div>
        </div>
    </header>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '../stores/auth';

// 事件
const emit = defineEmits(['toggle-sidebar']);

// 路由和認證存儲
const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

// 使用者下拉選單狀態
const userDropdownOpen = ref(false);

// 計算使用者名稱
const username = computed(() => {
    if (!authStore.user) return '';
    return authStore.user.full_name || authStore.user.username;
});

// 計算當前頁面標題
const currentPageTitle = computed(() => {
    const pathMap = {
        '/admin': '儀表板',
        '/admin/users': '使用者管理',
        '/admin/roles': '角色權限',
        '/admin/permissions': '權限管理',
        '/admin/role-requests': '角色申請審核',
        '/admin/camps': '露營地管理',
        '/admin/bookings': '預約管理',
        '/admin/reviews': '評價管理',
        '/admin/logs': '系統日誌',
        '/admin/settings': '系統設定',
        '/admin/products': '產品管理',
        '/admin/orders': '訂單管理',
        '/admin/my-camp': '我的營地',
        '/admin/camp-bookings': '預約管理',
        '/admin/camp-orders': '訂單管理',
        '/admin/camp-products': '商品管理',
        '/admin/camp-reviews': '評價回覆',
        '/admin/profile': '個人設定'
    };
    return pathMap[route.path] || '管理中心';
});

// 切換使用者下拉選單
const toggleUserDropdown = () => {
    userDropdownOpen.value = !userDropdownOpen.value;
};

// 切換側邊欄
const toggleSidebar = () => {
    emit('toggle-sidebar');
};

// 處理登出
const handleLogout = async () => {
    try {
        await authStore.logout();
        userDropdownOpen.value = false;
        router.push('/');
    } catch (error) {
        console.error('登出失敗:', error);
    }
};

// 點擊外部關閉下拉選單
const closeDropdownOnOutsideClick = (event) => {
    const userMenu = document.querySelector('.user-dropdown');
    if (userMenu && !userMenu.contains(event.target)) {
        userDropdownOpen.value = false;
    }
};
</script>

<style scoped>
/* 頂部導航欄 */
.admin-header {
    height: 70px;
    background-color: var(--white);
    box-shadow: var(--shadow-sm);
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 25px;
    position: sticky;
    top: 0;
    z-index: 980;
}

.admin-header .header-left {
    display: flex;
    align-items: center;
}

.sidebar-toggle-btn {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    border: none;
    background-color: var(--gray-100);
    border-radius: 8px;
    margin-right: 20px;
    cursor: pointer;
    color: var(--gray-600);
    transition: all 0.3s ease;
}

.sidebar-toggle-btn:hover {
    background-color: var(--gray-200);
}

.breadcrumb {
    display: flex;
    align-items: center;
    color: var(--gray-600);
    font-size: 15px;
}

.breadcrumb .separator {
    margin: 0 10px;
    font-size: 12px;
    color: var(--gray-400);
}

.admin-header .header-right {
    display: flex;
    align-items: center;
}

.search-box {
    display: flex;
    align-items: center;
    background-color: var(--gray-100);
    border-radius: 8px;
    padding: 8px 15px;
    margin-right: 20px;
    width: 250px;
}

.search-box i {
    color: var(--gray-500);
    margin-right: 10px;
}

.search-box input {
    border: none;
    background: transparent;
    outline: none;
    color: var(--gray-700);
    font-size: 14px;
    width: 100%;
}

.header-actions {
    display: flex;
    align-items: center;
}

.header-btn {
    width: 40px;
    height: 40px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-left: 10px;
    border: none;
    background-color: var(--gray-100);
    color: var(--gray-600);
    cursor: pointer;
    position: relative;
    transition: all 0.3s ease;
}

.header-btn:hover {
    background-color: var(--tiffany-bg);
    color: var(--tiffany-blue-accent);
}

.badge {
    position: absolute;
    top: -5px;
    right: -5px;
    background-color: #ff4d4f;
    color: white;
    font-size: 10px;
    width: 18px;
    height: 18px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
}

.user-dropdown {
    display: flex;
    align-items: center;
    background-color: var(--gray-100);
    border-radius: 8px;
    padding: 8px 15px;
    margin-left: 15px;
    cursor: pointer;
    position: relative;
    transition: all 0.3s ease;
}

.user-dropdown:hover {
    background-color: var(--tiffany-bg);
}

.user-dropdown i:first-child {
    font-size: 22px;
    color: var(--tiffany-blue);
    margin-right: 10px;
}

.user-dropdown span {
    margin-right: 10px;
    font-weight: 500;
    color: var(--gray-700);
}

.user-dropdown i:last-child {
    color: var(--gray-500);
    font-size: 12px;
}

.user-dropdown .dropdown-menu {
    position: absolute;
    top: calc(100% + 10px);
    right: 0;
    width: 200px;
    background-color: var(--white);
    border-radius: 8px;
    box-shadow: var(--shadow-md);
    padding: 8px 0;
    z-index: 1000;
}

.user-dropdown .dropdown-menu a {
    display: flex;
    align-items: center;
    padding: 10px 20px;
    color: var(--gray-700);
    transition: all 0.2s ease;
}

.user-dropdown .dropdown-menu a i {
    margin-right: 10px;
    width: 18px;
    text-align: center;
    color: var(--gray-600);
    font-size: 16px;
}

.user-dropdown .dropdown-menu a:hover {
    background-color: var(--tiffany-bg);
    color: var(--tiffany-blue-accent);
}

.user-dropdown .dropdown-menu a:hover i {
    color: var(--tiffany-blue-accent);
}

.dropdown-divider {
    height: 1px;
    background-color: var(--gray-100);
    margin: 6px 0;
}

/* 響應式設計 */
@media (max-width: 1200px) {
    .search-box {
        width: 200px;
    }
}

@media (max-width: 768px) {
    .breadcrumb {
        display: none;
    }

    .admin-header {
        padding: 0 15px;
    }

    .search-box {
        display: none;
    }

    .user-dropdown span {
        display: none;
    }
}
</style>