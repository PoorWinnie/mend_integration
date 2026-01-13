<template>
    <div class="admin-layout">
      <!-- 左侧导航栏 -->
      <div class="admin-sidebar" :class="{ 'collapsed': sidebarCollapsed }">
        <div class="sidebar-header">
          <img src="/logo.svg" alt="系统Logo" class="logo" />
          <h1 v-if="!sidebarCollapsed">后台管理系统</h1>
        </div>
        
        <div class="sidebar-menu">
          <!-- 权限管理模块 -->
          <div class="menu-group">
            <div class="menu-title">
              <i class="fa fa-key"></i>
              <span v-if="!sidebarCollapsed">權限管理</span>
            </div>
            
            <router-link 
              to="/admin/users" 
              class="menu-item" 
              active-class="active"
            >
              <i class="fa fa-users"></i>
              <span v-if="!sidebarCollapsed">使用者管理</span>
            </router-link>
            
            <router-link 
              to="/admin/roles" 
              class="menu-item" 
              active-class="active"
            >
              <i class="fa fa-user-tag"></i>
              <span v-if="!sidebarCollapsed">角色管理</span>
            </router-link>
            
            <router-link 
              to="/admin/role-requests" 
              class="menu-item" 
              active-class="active"
            >
              <i class="fa fa-user-plus"></i>
              <span v-if="!sidebarCollapsed">角色申請審核</span>
            </router-link>
          </div>
          
          <!-- 露营地管理模块 -->
          <div class="menu-group">
            <div class="menu-title">
              <i class="fa fa-campground"></i>
              <span v-if="!sidebarCollapsed">營地管理</span>
            </div>
            
            <router-link 
              to="/admin/camps" 
              class="menu-item" 
              active-class="active"
            >
              <i class="fa fa-mountain"></i>
              <span v-if="!sidebarCollapsed">露營地管理</span>
            </router-link>
            
            <router-link 
              to="/admin/bookings" 
              class="menu-item" 
              active-class="active"
            >
              <i class="fa fa-calendar-check"></i>
              <span v-if="!sidebarCollapsed">預約管理</span>
            </router-link>
            
            <router-link 
              to="/admin/reviews" 
              class="menu-item" 
              active-class="active"
            >
              <i class="fa fa-star"></i>
              <span v-if="!sidebarCollapsed">評價管理</span>
            </router-link>
          </div>
        </div>
      </div>
      
      <!-- 右侧内容区 -->
      <div class="admin-content" :class="{ 'expanded': sidebarCollapsed }">
        <!-- 顶部导航栏 -->
        <div class="admin-header">
          <div class="header-left">
            <button @click="toggleSidebar" class="toggle-sidebar">
              <i class="fa fa-bars"></i>
            </button>
            <h2 class="page-title">{{ currentPageTitle }}</h2>
          </div>
          
          <div class="header-right">
            <div class="user-dropdown" ref="userDropdown">
              <div class="dropdown-toggle" @click="toggleUserMenu">
                <div class="user-avatar">
                  <i class="fa fa-user"></i>
                </div>
                <span class="username">{{ username }}</span>
                <i class="fa fa-chevron-down"></i>
              </div>
              
              <div class="dropdown-menu" v-if="showUserMenu">
                <router-link to="/admin/profile" class="dropdown-item">
                  <i class="fa fa-user-circle"></i> 個人資料
                </router-link>
                <div class="dropdown-divider"></div>
                <button @click="logout" class="dropdown-item">
                  <i class="fa fa-sign-out-alt"></i> 登出
                </button>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 主要内容区 -->
        <div class="admin-main">
          <slot></slot>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue';
  import { useRouter, useRoute } from 'vue-router';
  import { useAuthStore } from '@/stores/auth';
  
  export default {
    name: 'AdminLayout',
    
    setup() {
      const router = useRouter();
      const route = useRoute();
      const authStore = useAuthStore();
      
      const sidebarCollapsed = ref(false);
      const showUserMenu = ref(false);
      const userDropdown = ref(null);
      
      // 用户信息
      const username = computed(() => {
        return authStore.user?.username || '';
      });
      
      // 当前页面标题
      const currentPageTitle = computed(() => {
        const pathMap = {
          '/admin/users': '使用者管理',
          '/admin/roles': '角色管理',
          '/admin/role-requests': '角色申請審核',
          '/admin/camps': '露營地管理',
          '/admin/bookings': '預約管理',
          '/admin/reviews': '評價管理',
          '/admin/profile': '個人資料'
        };
        
        return pathMap[route.path] || '控制面板';
      });
      
      // 切换侧边栏状态
      const toggleSidebar = () => {
        sidebarCollapsed.value = !sidebarCollapsed.value;
        // 保存状态到本地存储
        localStorage.setItem('sidebarCollapsed', sidebarCollapsed.value);
      };
      
      // 切换用户菜单
      const toggleUserMenu = () => {
        showUserMenu.value = !showUserMenu.value;
      };
      
      // 登出功能
      const logout = async () => {
        try {
          await authStore.logout();
          router.push('/login');
        } catch (error) {
          console.error('登出失敗:', error);
        }
      };
      
      // 点击外部关闭用户菜单
      const handleClickOutside = (event) => {
        if (userDropdown.value && !userDropdown.value.contains(event.target)) {
          showUserMenu.value = false;
        }
      };
      
      // 监听路由变化
      watch(route, () => {
        showUserMenu.value = false;
      });
      
      onMounted(() => {
        // 从本地存储恢复侧边栏状态
        const savedState = localStorage.getItem('sidebarCollapsed');
        if (savedState !== null) {
          sidebarCollapsed.value = savedState === 'true';
        }
        
        // 添加点击外部关闭菜单的事件
        document.addEventListener('click', handleClickOutside);
      });
      
      onBeforeUnmount(() => {
        document.removeEventListener('click', handleClickOutside);
      });
      
      return {
        sidebarCollapsed,
        showUserMenu,
        userDropdown,
        username,
        currentPageTitle,
        toggleSidebar,
        toggleUserMenu,
        logout
      };
    }
  };
  </script>
  
  <style scoped>
  .admin-layout {
    display: flex;
    min-height: 100vh;
    background-color: #f9fafb;
  }
  
  /* 侧边栏样式 */
  .admin-sidebar {
    width: 250px;
    background-color: #1e293b;
    color: #e2e8f0;
    transition: all 0.3s;
    z-index: 10;
    position: fixed;
    top: 0;
    left: 0;
    height: 100vh;
    overflow-y: auto;
  }
  
  .admin-sidebar.collapsed {
    width: 70px;
  }
  
  .sidebar-header {
    display: flex;
    align-items: center;
    padding: 20px;
    border-bottom: 1px solid #334155;
  }
  
  .logo {
    width: 32px;
    height: 32px;
    margin-right: 15px;
  }
  
  .sidebar-header h1 {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
    white-space: nowrap;
    overflow: hidden;
  }
  
  .sidebar-menu {
    padding: 20px 0;
  }
  
  .menu-group {
    margin-bottom: 20px;
  }
  
  .menu-title {
    padding: 10px 20px;
    font-size: 12px;
    text-transform: uppercase;
    letter-spacing: 1px;
    color: #94a3b8;
    display: flex;
    align-items: center;
    gap: 10px;
  }
  
  .menu-item {
    display: flex;
    align-items: center;
    padding: 12px 20px;
    color: #cbd5e1;
    text-decoration: none;
    transition: all 0.3s;
    position: relative;
    gap: 10px;
  }
  
  .menu-item i {
    font-size: 16px;
    width: 20px;
    text-align: center;
  }
  
  .menu-item:hover {
    background-color: #334155;
    color: #ffffff;
  }
  
  .menu-item.active {
    background-color: #0fb886;
    color: white;
  }
  
  /* 内容区样式 */
  .admin-content {
    flex: 1;
    margin-left: 250px;
    transition: all 0.3s;
  }
  
  .admin-content.expanded {
    margin-left: 70px;
  }
  
  .admin-header {
    background-color: #ffffff;
    height: 60px;
    padding: 0 20px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    position: sticky;
    top: 0;
    z-index: 5;
  }
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 15px;
  }
  
  .toggle-sidebar {
    background: none;
    border: none;
    color: #64748b;
    font-size: 18px;
    cursor: pointer;
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 8px;
    transition: all 0.3s;
  }
  
  .toggle-sidebar:hover {
    background-color: #f1f5f9;
    color: #0f172a;
  }
  
  .page-title {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
    color: #1e293b;
  }
  
  .header-right {
    display: flex;
    align-items: center;
  }
  
  .user-dropdown {
    position: relative;
  }
  
  .dropdown-toggle {
    display: flex;
    align-items: center;
    gap: 10px;
    cursor: pointer;
    padding: 8px 12px;
    border-radius: 8px;
    transition: all 0.3s;
  }
  
  .dropdown-toggle:hover {
    background-color: #f1f5f9;
  }
  
  .user-avatar {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background-color: #0fb886;
    color: white;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .username {
    font-weight: 500;
    color: #1e293b;
  }
  
  .dropdown-menu {
    position: absolute;
    top: 100%;
    right: 0;
    margin-top: 8px;
    background-color: white;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
    border-radius: 8px;
    width: 200px;
    padding: 5px 0;
    z-index: 10;
  }
  
  .dropdown-item {
    display: flex;
    align-items: center;
    padding: 10px 15px;
    gap: 10px;
    color: #475569;
    text-decoration: none;
    cursor: pointer;
    transition: all 0.2s;
    border: none;
    background: none;
    width: 100%;
    text-align: left;
  }
  
  .dropdown-item:hover {
    background-color: #f1f5f9;
  }
  
  .dropdown-divider {
    height: 1px;
    background-color: #e2e8f0;
    margin: 5px 0;
  }
  
  .admin-main {
    padding: 20px;
  }
  
  /* 响应式设计 */
  @media (max-width: 768px) {
    .admin-sidebar {
      width: 0;
      opacity: 0;
    }
    
    .admin-sidebar.collapsed {
      width: 250px;
      opacity: 1;
    }
    
    .admin-content {
      margin-left: 0;
    }
    
    .admin-content.expanded {
      margin-left: 0;
    }
    
    .page-title {
      display: none;
    }
  }
  </style>