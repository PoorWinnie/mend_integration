<template>
  <!-- 頂部導航欄 -->
  <header class="app-header">
    <div class="header-left">
      <!-- Logo -->
      <div class="logo">
        <router-link to="/" class="logo-link">
          <span class="logo-icon">
            <img src="/img/logo/logo-small.jpg" alt="EasyCamp Icon" />
          </span>
          <span class="logo-text">
            <img src="/img/logo/logo2.png" alt="EasyCamp Text Logo" />
          </span>
        </router-link>
      </div>

      <!-- 主導航菜單 -->
      <nav class="main-nav">
        <!-- <router-link to="/booking">營地訂位</router-link> -->
        <router-link to="/reviews">營地評論</router-link>
        <router-link to="/productFront">購物商城</router-link>
        <router-link to="/member">會員中心</router-link>
        <router-link to="/shoppingCart">購物車</router-link>
        <!-- 其他主導航項目 -->
      </nav>
    </div>

    <div class="header-right">
      <!-- 使用者選單 -->
      <div v-if="isLoggedIn" class="user-menu">
        <span @click="toggleUserDropdown" class="user-menu-trigger">
          <i class="fa fa-user-circle user-icon"></i> {{ username }}
          <i class="fa fa-caret-down"></i>
        </span>
        <div v-show="userDropdownOpen" class="user-dropdown-menu">
          <router-link to="/profile" @click="closeDropdown"
            ><i class="fa fa-id-card"></i> 個人資料</router-link>
            <router-link to="/my-camp" v-if="true" @click="closeDropdown" class="dropdown-item" href="/camp-area"><i class="fa-solid fa-campground"></i>我的營區</router-link>
          <router-link to="/booking" @click="closeDropdown"
            ><i class="fa fa-calendar-alt"></i> 我的預約</router-link>
          <router-link to="/favorites" @click="closeDropdown"
            ><i class="fa fa-heart"></i> 收藏露營地</router-link>

          <div class="dropdown-divider"></div>
          <template v-if="hasAdminAccess">
            <router-link to="/admin"
              ><i class="fa fa-cogs"></i> 管理後台</router-link
            >
            <div class="dropdown-divider"></div>
          </template>
          <a href="#" @click.prevent="handleLogout"
            ><i class="fa fa-sign-out-alt"></i> 登出</a
          >
        </div>
      </div>

      <!-- 未登入選項 -->
      <div v-else class="auth-buttons">
        <router-link to="/login" class="btn-login">登入</router-link>
        <router-link to="/register" class="btn-register">註冊</router-link>
      </div>

      <!-- 切換身份按鈕 -->
      <div class="role-switcher" v-if="hasAdminAccess">
        <button @click="switchRole" class="btn-switch-role">
          <i class="fa fa-exchange-alt"></i>
          {{ isAdminPage ? "會員模式" : "管理模式" }}
        </button>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useAuthStore } from "../stores/auth";

// 路由和認證存儲
const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

// 使用者下拉選單狀態
const userDropdownOpen = ref(false);

// 檢查角色的通用函數
function hasRole(roleStr, targetRoleArray) {
  if (!roleStr) return false;

  // 處理陣列形式的角色
  if (Array.isArray(roleStr)) {
    return roleStr.some((r) =>
      targetRoleArray.some((target) =>
        String(r).toUpperCase().includes(String(target).toUpperCase())
      )
    );
  }

  // 處理字串形式的角色
  return targetRoleArray.some((target) =>
    String(roleStr).toUpperCase().includes(String(target).toUpperCase())
  );
}

// 角色權限計算
const isSuperAdmin = computed(() => {
  // 先直接檢查本地存儲
  const storedRole = localStorage.getItem("userRole");
  if (storedRole === "SUPER_ADMIN") return true;

  // 檢查用戶名是否為 superadmin
  if (authStore.user?.username === "superadmin") return true;

  // 檢查 authStore 中的狀態
  if (authStore.isSuperAdmin || authStore.role === "SUPER_ADMIN") return true;

  // 檢查存儲的角色列表
  try {
    const rolesListStr = localStorage.getItem("userRolesList");
    if (rolesListStr) {
      const rolesList = JSON.parse(rolesListStr);
      if (Array.isArray(rolesList) && rolesList.includes("SUPER_ADMIN")) {
        return true;
      }
    }
  } catch (e) {
    console.error("解析角色列表時出錯", e);
  }

  // 最後檢查一些可能的角色表示形式
  const possibleRoles = [
    "SUPER_ADMIN",
    "SUPERADMIN",
    "ROLE_SUPER_ADMIN",
    "SUPER",
    "ADMIN_SUPER",
  ];

  return (
    hasRole(authStore.role, possibleRoles) ||
    hasRole(authStore.user?.role, possibleRoles) ||
    hasRole(authStore.user?.roles, possibleRoles) ||
    hasRole(authStore.roles, possibleRoles)
  );
});

const isGeneralAdmin = computed(() => {
  if (isSuperAdmin.value) return false; // 超级管理员不应该同时是一般管理员

  const possibleRoles = ["ADMIN", "ROLE_ADMIN", "GENERAL_ADMIN"];
  return (
    hasRole(authStore.role, possibleRoles) ||
    hasRole(authStore.user?.role, possibleRoles) ||
    hasRole(authStore.user?.roles, possibleRoles) ||
    hasRole(authStore.roles, possibleRoles)
  );
});

const isCampOwner = computed(() => {
  if (isSuperAdmin.value || isGeneralAdmin.value) return false; // 避免角色重叠
  const possibleRoles = ["CAMP_OWNER", "CAMPOWNER", "OWNER", "ROLE_CAMP_OWNER"];
  return (
    hasRole(authStore.role, possibleRoles) ||
    hasRole(authStore.user?.role, possibleRoles) ||
    hasRole(authStore.user?.roles, possibleRoles) ||
    hasRole(authStore.roles, possibleRoles)
  );
});

const hasAdminAccess = computed(() => {
  return isSuperAdmin.value || isGeneralAdmin.value || isCampOwner.value;
});

// 計算是否在管理員頁面
const isAdminPage = computed(() => {
  return route.path.startsWith("/admin");
});

// 計算使用者名稱
const username = computed(() => {
  if (!authStore.user) return "";
  return authStore.user.full_name || authStore.user.username;
});

// 計算是否已登入
const isLoggedIn = computed(() => {
  return authStore.isLoggedIn;
});

// 切換使用者下拉選單
const toggleUserDropdown = () => {
  userDropdownOpen.value = !userDropdownOpen.value;
};

// 處理登出
const handleLogout = async () => {
  try {
    await authStore.logout();
    userDropdownOpen.value = false;
    router.push("/");
  } catch (error) {
    console.error("登出失敗:", error);
  }
};

// 切換角色（會員/管理員）
const switchRole = () => {
  if (isAdminPage.value) {
    router.push("/");
  } else if (hasAdminAccess.value) {
    router.push("/admin");
  } else {
    router.push("/admin/login");
  }
};
//監聽下拉式選單
const closeDropdown = () => {
  userDropdownOpen.value = false;
};
</script>

<style scoped>
.app-header {
  background-color: var(--card-bg);
  box-shadow: var(--shadow);
  position: sticky;
  top: 0;
  z-index: 100;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  padding: 0 40px;
  height: 90px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-left,
.header-right {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
  margin-left: 150px; /* ✅ 與中間選單拉出間距 */
}

.logo {
  gap: 8px;
  margin-right: 150px;
}

.logo-icon img {
  height: 50px;
  width: auto;
  object-fit: contain;
  margin-right: 30px;
}

.logo-text img {
  height: 80px;
  width: auto;
  object-fit: contain;
}


.main-nav {
  display: flex;
  gap: 48px;
  justify-content: center;
  align-items: center;
  flex: 1;
}

.main-nav a {
  font-size: 18px;
  color: var(--text-primary);
  font-weight: 500;
  text-decoration: none;
  padding: 12px 16px;
  position: relative;
}

.main-nav a:hover,
.main-nav a.router-link-active {
  color: var(--accent);
}

.main-nav a::after {
  content: "";
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background: var(--accent);
  transition: width 0.3s ease;
}

.main-nav a:hover::after,
.main-nav a.router-link-active::after {
  width: 100%;
}



.user-menu {
  position: relative;
}

.user-menu-trigger {
  cursor: pointer;
  display: flex;
  align-items: center;
  padding: 8px 16px;
  border-radius: 4px;
  background-color: rgba(129, 216, 208, 0.05);
  font-weight: 500;
  transition: background-color 0.3s;
}

.user-menu-trigger:hover {
  background-color: rgba(129, 216, 208, 0.1);
}

.user-icon {
  margin-right: 8px;
  color: var(--accent);
}

.user-menu-trigger i:last-child {
  margin-left: 6px;
  font-size: 14px;
  color: var(--accent);
}

.user-dropdown-menu {
  position: absolute;
  top: calc(100% + 5px);
  right: 0;
  width: 220px;
  background-color: var(--card-bg);
  border-radius: 6px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  padding: 8px 0;
  z-index: 999;
  animation: dropdown 0.2s ease-out;
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.user-dropdown-menu a {
  display: flex;
  align-items: center;
  padding: 10px 16px;
  color: var(--text-primary);
  font-weight: 500;
}

.user-dropdown-menu a:hover {
  background-color: rgba(129, 216, 208, 0.05);
  color: var(--accent);
}

.user-dropdown-menu a i {
  width: 20px;
  margin-right: 10px;
  color: var(--accent);
  font-size: 16px;
}

.dropdown-divider {
  height: 1px;
  background-color: rgba(0, 0, 0, 0.05);
  margin: 6px 0;
}

.auth-buttons {
  display: flex;
  gap: 12px;
}

.btn-login,
.btn-register {
  padding: 8px 16px;
  border-radius: 4px;
  font-weight: 500;
  transition: all 0.3s;
}

.btn-login {
  color: var(--accent);
  border: 1px solid var(--accent);
  background-color: transparent;
}

.btn-register {
  background-color: var(--accent);
  color: white;
  border: none;
}

.btn-login:hover {
  background-color: rgba(129, 216, 208, 0.05);
}

.btn-register:hover {
  background-color: var(--accent-hover);
}

.btn-switch-role {
  margin-left: 12px;
  padding: 8px 16px;
  border: 1px solid #ccc;
  background-color: #f9f9f9;
  border-radius: 4px;
  font-size: 14px;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  font-weight: 500;
  transition: background-color 0.3s;
}

.btn-switch-role i {
  margin-right: 8px;
}

.btn-switch-role:hover {
  background-color: #ececec;
}

/* Responsive */
@media (max-width: 768px) {
  .main-nav {
    display: none;
  }

  .app-header {
    padding: 0 20px;
  }

  .auth-buttons {
    flex-direction: column;
    align-items: center;
  }

  .btn-login,
  .btn-register {
    width: 100%;
    margin: 4px 0;
    text-align: center;
  }
}
</style>
