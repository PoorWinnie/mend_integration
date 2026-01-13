<template>
    <!-- 側邊欄 -->
    <div class="admin-sidebar" :class="{ 'collapsed': collapsed }">
        <div class="sidebar-header">
            <div class="logo">
                <i class="fa fa-campground"></i>
                <span>西米露</span>
            </div>
            <div class="sidebar-toggle" @click="toggleSidebar">
                <i class="fa fa-bars"></i>
            </div>
        </div>

        <div class="user-profile">
            <div class="user-avatar">
                <i class="fa fa-user-circle"></i>
            </div>
            <div class="user-info">
                <h4>{{ username }}</h4>
                <span class="user-role" :class="{ 'super-admin': isSuperAdmin }" :title="roleDebugInfo">{{ roleName
                }}</span>
            </div>
        </div>

        <nav class="sidebar-menu">
            <!-- 所有角色都能看到的共用功能 -->
            <div class="menu-category">主要功能</div>
            <router-link to="/admin" class="menu-item" exact>
                <i class="fa fa-tachometer-alt"></i>
                <span>儀表板</span>
            </router-link>

            <!-- 超級管理員才能看到的功能 -->
            <template v-if="isSuperAdmin">
                <router-link to="/admin/users" class="menu-item">
                    <i class="fa fa-users"></i>
                    <span>使用者管理</span>
                </router-link>
                <router-link to="/admin/roles" class="menu-item">
                    <i class="fa fa-user-tag"></i>
                    <span>角色權限</span>
                </router-link>
                <router-link to="/admin/permissions" class="menu-item">
                    <i class="fa fa-key"></i>
                    <span>權限管理</span>
                </router-link>
                <router-link to="/admin/role-requests" class="menu-item">
                    <i class="fa fa-user-plus"></i>
                    <span>角色申請審核</span>
                </router-link>
            </template>

            <!-- 超級管理員和一般管理員可見的功能 -->
            <template v-if="isSuperAdmin || isGeneralAdmin">
                <div class="menu-category">營運管理</div>
                <router-link to="/admin/products" class="menu-item">
                    <i class="fa fa-shopping-bag"></i>
                    <span>產品管理</span>
                </router-link>
                <router-link to="/admin/orders" class="menu-item">
                    <i class="fa fa-file-invoice"></i>
                    <span>訂單管理</span>
                </router-link>
                <router-link to="/admin/camps" class="menu-item">
                    <i class="fa fa-campground"></i>
                    <span>露營地管理</span>
                </router-link>
                <router-link to="/admin/bookings" class="menu-item">
                    <i class="fa fa-calendar-check"></i>
                    <span>預約管理</span>
                </router-link>
                <router-link to="/admin/reviews" class="menu-item">
                    <i class="fa fa-star"></i>
                    <span>評價管理</span>
                </router-link>
            </template>

            <!-- 營地管理員可見的功能 -->
            <template v-if="isCampOwner">
                <div class="menu-category">營地管理</div>
                <router-link to="/admin/my-camp" class="menu-item">
                    <i class="fa fa-campground"></i>
                    <span>我的營地</span>
                </router-link>
                <router-link to="/admin/camp-bookings" class="menu-item">
                    <i class="fa fa-calendar-check"></i>
                    <span>預約管理</span>
                </router-link>
                <router-link to="/admin/camp-orders" class="menu-item">
                    <i class="fa fa-file-invoice"></i>
                    <span>訂單管理</span>
                </router-link>
                <router-link to="/admin/camp-products" class="menu-item">
                    <i class="fa fa-shopping-bag"></i>
                    <span>商品管理</span>
                </router-link>
                <router-link to="/admin/camp-reviews" class="menu-item">
                    <i class="fa fa-star"></i>
                    <span>評價回覆</span>
                </router-link>
            </template>

            <!-- 系統設定 - 根據權限顯示 -->
            <div class="menu-category">系統設定</div>
            <template v-if="isSuperAdmin">
                <router-link to="/admin/logs" class="menu-item">
                    <i class="fa fa-history"></i>
                    <span>系統日誌</span>
                </router-link>
            </template>
            <router-link to="/admin/settings" class="menu-item" v-if="isSuperAdmin || isGeneralAdmin">
                <i class="fa fa-cog"></i>
                <span>系統設定</span>
            </router-link>
            <router-link to="/admin/profile" class="menu-item">
                <i class="fa fa-user-cog"></i>
                <span>個人設定</span>
            </router-link>
        </nav>

        <div class="sidebar-footer">
            <button @click="switchToMember" class="btn-mode-switch">
                <i class="fa fa-exchange-alt"></i>
                <span>切換至會員版</span>
            </button>
            <button @click="handleLogout" class="btn-logout">
                <i class="fa fa-sign-out-alt"></i>
                <span>登出系統</span>
            </button>
        </div>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';

// 屬性
const props = defineProps({
    collapsed: {
        type: Boolean,
        default: false
    }
});

// 事件
const emit = defineEmits(['toggle-sidebar']);

// 路由和認證存儲
const router = useRouter();
const authStore = useAuthStore();

// 檢查角色的通用函數
function hasRole(roleStr, targetRoleArray) {
    if (!roleStr) return false;

    // 處理陣列形式的角色
    if (Array.isArray(roleStr)) {
        return roleStr.some(r => targetRoleArray.some(target =>
            String(r).toUpperCase().includes(String(target).toUpperCase())));
    }

    // 處理字串形式的角色
    return targetRoleArray.some(target =>
        String(roleStr).toUpperCase().includes(String(target).toUpperCase()));
}

// 角色權限計算
const isSuperAdmin = computed(() => {
    // 先直接檢查本地存儲
    const storedRole = localStorage.getItem('userRole');
    if (storedRole === 'SUPER_ADMIN') return true;

    // 檢查用戶名是否為 superadmin
    if (authStore.user?.username === 'superadmin') return true;

    // 檢查 authStore 中的狀態
    if (authStore.isSuperAdmin || authStore.role === 'SUPER_ADMIN') return true;

    // 檢查存儲的角色列表
    try {
        const rolesListStr = localStorage.getItem('userRolesList');
        if (rolesListStr) {
            const rolesList = JSON.parse(rolesListStr);
            if (Array.isArray(rolesList) && rolesList.includes('SUPER_ADMIN')) {
                return true;
            }
        }
    } catch (e) {
        console.error('解析角色列表時出錯', e);
    }

    // 最後檢查一些可能的角色表示形式
    const possibleRoles = ['SUPER_ADMIN', 'SUPERADMIN', 'ROLE_SUPER_ADMIN', 'SUPER', 'ADMIN_SUPER'];

    return hasRole(authStore.role, possibleRoles) ||
        hasRole(authStore.user?.role, possibleRoles) ||
        hasRole(authStore.user?.roles, possibleRoles) ||
        hasRole(authStore.roles, possibleRoles);
});

const isGeneralAdmin = computed(() => {
    if (isSuperAdmin.value) return false; // 超级管理员不应该同时是一般管理员

    const possibleRoles = ['ADMIN', 'ROLE_ADMIN', 'GENERAL_ADMIN'];
    return hasRole(authStore.role, possibleRoles) ||
        hasRole(authStore.user?.role, possibleRoles) ||
        hasRole(authStore.user?.roles, possibleRoles) ||
        hasRole(authStore.roles, possibleRoles);
});

const isCampOwner = computed(() => {
    if (isSuperAdmin.value || isGeneralAdmin.value) return false; // 避免角色重叠

    const possibleRoles = ['CAMP_OWNER', 'CAMPOWNER', 'OWNER', 'ROLE_CAMP_OWNER'];
    return hasRole(authStore.role, possibleRoles) ||
        hasRole(authStore.user?.role, possibleRoles) ||
        hasRole(authStore.user?.roles, possibleRoles) ||
        hasRole(authStore.roles, possibleRoles);
});

// 計算使用者名稱
const username = computed(() => {
    if (!authStore.user) return '';
    return authStore.user.full_name || authStore.user.username;
});

// 計算用戶角色名稱
const roleName = computed(() => {
    // 檢查存儲的角色和前端計算的角色，確保超級管理員優先
    if (localStorage.getItem('userRole') === 'SUPER_ADMIN') return '超級管理員';
    if (authStore.role === 'SUPER_ADMIN') return '超級管理員';
    if (authStore.isSuperAdmin) return '超級管理員';

    // 檢查用戶名是否為 superadmin (特殊情況)
    if (authStore.user?.username === 'superadmin') return '超級管理員';

    // 檢查存儲的角色列表
    try {
        const rolesListStr = localStorage.getItem('userRolesList');
        if (rolesListStr) {
            const rolesList = JSON.parse(rolesListStr);
            if (Array.isArray(rolesList) && rolesList.includes('SUPER_ADMIN')) {
                return '超級管理員';
            }
        }
    } catch (e) {
        console.error('解析角色列表時出錯', e);
    }

    // 其他角色照常判斷
    if (isGeneralAdmin.value) return '一般管理員';
    if (isCampOwner.value) return '營地管理員';
    return '一般使用者';
});

// 新增：用於調試的角色信息
const roleDebugInfo = computed(() => {
    return JSON.stringify({
        localRole: localStorage.getItem('userRole'),
        storeRole: authStore.role,
        isSuperAdmin: isSuperAdmin.value,
        isGeneralAdmin: isGeneralAdmin.value,
        username: authStore.user?.username,
        rolesList: localStorage.getItem('userRolesList')
    }, null, 2);
});

// 切換側邊欄折疊狀態
const toggleSidebar = () => {
    emit('toggle-sidebar');
};

// 處理登出
const handleLogout = async () => {
    try {
        await authStore.logout();
        router.push('/');
    } catch (error) {
        console.error('登出失敗:', error);
    }
};

// 切換到會員模式
const switchToMember = () => {
    router.push('/');
};
</script>

<style scoped>
/* 側邊欄樣式 */
.admin-sidebar {
    width: 260px;
    background-color: var(--white);
    box-shadow: var(--shadow);
    transition: all 0.3s ease;
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 990;
    display: flex;
    flex-direction: column;
    overflow-x: hidden;
}

.admin-sidebar.collapsed {
    width: 80px;
}

.sidebar-header {
    padding: 20px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    border-bottom: 1px solid var(--gray-100);
    height: 70px;
}

.sidebar-header .logo {
    display: flex;
    align-items: center;
    font-size: 22px;
    font-weight: 700;
    color: var(--tiffany-blue);
    margin-right: 0;
}

.sidebar-header .logo i {
    margin-right: 10px;
    font-size: 22px;
}

.admin-sidebar.collapsed .sidebar-header .logo span {
    display: none;
}

.sidebar-toggle {
    width: 30px;
    height: 30px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    color: var(--gray-600);
    background-color: var(--gray-100);
    transition: all 0.3s ease;
}

.sidebar-toggle:hover {
    background-color: var(--tiffany-blue-light);
    color: var(--white);
}

.user-profile {
    padding: 20px;
    display: flex;
    align-items: center;
    border-bottom: 1px solid var(--gray-100);
}

.user-avatar {
    width: 42px;
    height: 42px;
    border-radius: 12px;
    overflow: hidden;
    margin-right: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: var(--tiffany-bg);
    color: var(--tiffany-blue);
}

.user-avatar i {
    font-size: 24px;
}

.user-info h4 {
    font-size: 15px;
    margin-bottom: 4px;
    color: var(--gray-800);
}

.user-role {
    font-size: 12px;
    color: var(--tiffany-blue);
    background-color: var(--tiffany-bg);
    padding: 3px 8px;
    border-radius: 4px;
}

/* 新增：超級管理員角色樣式 */
.user-role.super-admin {
    background-color: #ffe7ba;
    color: #d48806;
    font-weight: 600;
}

.admin-sidebar.collapsed .user-profile {
    padding: 20px 0;
    justify-content: center;
}

.admin-sidebar.collapsed .user-avatar {
    margin-right: 0;
}

.admin-sidebar.collapsed .user-info {
    display: none;
}

.sidebar-menu {
    flex: 1;
    padding: 20px 0;
    overflow-y: auto;
}

.menu-category {
    padding: 0 20px;
    margin: 10px 0;
    font-size: 11px;
    text-transform: uppercase;
    color: var(--gray-500);
    letter-spacing: 0.5px;
}

.admin-sidebar.collapsed .menu-category {
    text-align: center;
    padding: 0 5px;
}

.menu-item {
    display: flex;
    align-items: center;
    padding: 12px 20px;
    color: var(--gray-700);
    margin: 2px 10px;
    border-radius: 8px;
    transition: all 0.3s ease;
}

.admin-sidebar.collapsed .menu-item {
    padding: 12px 0;
    justify-content: center;
}

.menu-item i {
    font-size: 18px;
    width: 22px;
    margin-right: 12px;
    text-align: center;
}

.admin-sidebar.collapsed .menu-item i {
    margin-right: 0;
}

.admin-sidebar.collapsed .menu-item span {
    display: none;
}

.menu-item:hover {
    background-color: var(--tiffany-bg);
    color: var(--tiffany-blue-accent);
}

.menu-item.router-link-active {
    background-color: var(--tiffany-blue);
    color: var(--white);
}

.menu-item.router-link-active:hover {
    background-color: var(--tiffany-blue-dark);
}

.sidebar-footer {
    padding: 20px;
    border-top: 1px solid var(--gray-100);
}

.admin-sidebar.collapsed .sidebar-footer {
    padding: 10px;
}

.admin-sidebar.collapsed .btn-mode-switch span,
.admin-sidebar.collapsed .btn-logout span {
    display: none;
}

.btn-mode-switch {
    background-color: var(--tiffany-bg);
    color: var(--tiffany-blue-accent);
    border: none;
    padding: 12px;
    border-radius: 8px;
    width: 100%;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 12px;
    transition: all 0.3s ease;
    font-size: 14px;
}

.btn-mode-switch i {
    margin-right: 8px;
}

.admin-sidebar.collapsed .btn-mode-switch i {
    margin-right: 0;
}

.btn-mode-switch:hover {
    background-color: var(--tiffany-blue-light);
    color: var(--white);
}

.btn-logout {
    background-color: #fff1f0;
    color: #ff4d4f;
    border: none;
    padding: 12px;
    border-radius: 8px;
    width: 100%;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.3s ease;
    font-size: 14px;
}

.btn-logout i {
    margin-right: 8px;
}

.admin-sidebar.collapsed .btn-logout i {
    margin-right: 0;
}

.btn-logout:hover {
    background-color: #ff4d4f;
    color: var(--white);
}

/* 響應式設計 */
@media (max-width: 1200px) {
    .admin-sidebar {
        width: 240px;
    }
}

@media (max-width: 768px) {
    .admin-sidebar {
        width: 0;
        transform: translateX(-100%);
    }

    .admin-sidebar.collapsed {
        width: 260px;
        transform: translateX(0);
    }

    .sidebar-header .logo span,
    .user-info,
    .menu-item span {
        display: block;
    }

    .user-profile {
        justify-content: flex-start;
    }

    .menu-category {
        text-align: left;
        padding: 0 20px;
    }

    .menu-item {
        justify-content: flex-start;
        padding: 12px 20px;
    }

    .menu-item i {
        margin-right: 12px;
    }

    .sidebar-footer {
        display: block;
    }
}
</style>