// src/auth-guard.js
import { useAuthStore } from './stores/auth';
import authService from './services/auth/authService';

// 根據不同角色的頁面存取策略
const accessControlMap = {
  // 遊客可訪問路由
  guest: [
    '/',
    '/about',
    '/login',
    '/register',
    '/admin/login',       // 添加管理員登入頁面
    '/admin/register',    // 添加管理員註冊頁面
    '/forgot-password',
    '/member',            // 會員中心
    '/camping/detail',    // 露營地詳情
  ],
  
  // 一般使用者可訪問路由
  user: [
    '/profile',
    '/booking',
    '/favorites',
    '/reviews',
    '/messages',
  ],
  
  // 露營地主可訪問路由
  camp_owner: [
    '/camp-management',
    '/camp-bookings',
    '/camp-reviews',
    '/camp-stats',
  ],
  
  // 管理員可訪問路由
  admin: [
    '/admin',
    '/admin/dashboard',
    '/admin/users',
    '/admin/roles',
    '/admin/permissions',
    '/admin/camps',
    '/admin/bookings',
    '/admin/reviews',
    '/admin/logs',
    '/admin/settings',
  ],
  
  // 超級管理員可訪問路由
  super_admin: [
    // 超級管理員可訪問所有路由
  ]
};

// 檢查是否有權限訪問某個路由
function hasRouteAccess(route, userRole) {
  // 允許訪問所有公共路由
  if (accessControlMap.guest.some(path => route.path.startsWith(path))) {
    return true;
  }
  
  // 如果是一般使用者
  if (userRole === 'USER' && accessControlMap.user.some(path => route.path.startsWith(path))) {
    return true;
  }
  
  // 如果是露營地主
  if (userRole === 'CAMP_OWNER' && (
    accessControlMap.user.some(path => route.path.startsWith(path)) || 
    accessControlMap.camp_owner.some(path => route.path.startsWith(path))
  )) {
    return true;
  }
  
  // 如果是管理員
  if (userRole === 'ADMIN' && (
    accessControlMap.admin.some(path => route.path.startsWith(path))
  )) {
    return true;
  }
  
  // 如果是超級管理員
  if (userRole === 'SUPER_ADMIN') {
    return true; // 超級管理員可訪問所有頁面
  }
  
  return false;
}

// 路由守衛
export function setupAuthGuard(router) {
  router.beforeEach((to, from, next) => {
    const authStore = useAuthStore();
    
    // 嘗試從localStorage恢復認證狀態 (如果需要)
    if (!authStore.isLoggedIn) {
      authStore.checkAndRestoreAuth();
    }
    
    const isLoggedIn = authStore.isLoggedIn;
    const userRole = authStore.role;
    const isAdmin = userRole === 'ADMIN' || userRole === 'SUPER_ADMIN';
    
    // 設定頁面標題
    document.title = to.meta.title || '露營網站';
    
    // 登入頁面特殊處理
    if (to.path === '/login') {
      // 如果已登入且是管理員，導向管理後台
      if (isLoggedIn && isAdmin) {
        return next('/admin');
      }
      // 如果已登入且是一般用戶，導向首頁
      else if (isLoggedIn) {
        return next('/');
      }
    }
    
    // 管理員登入頁面特殊處理
    if (to.path === '/admin/login') {
      // 如果已登入且是管理員，導向管理後台
      if (isLoggedIn && isAdmin) {
        return next('/admin');
      }
      // 如果已登入但不是管理員，導向無權限頁面
      else if (isLoggedIn) {
        return next('/403');
      }
    }
    
    // 檢查路徑是否存在
    if (to.matched.length === 0) {
      console.warn('路由未匹配:', to.path);
      return next('/404');
    }
    
    // 對於管理後台頁面
    if (to.path.startsWith('/admin') && !to.path.includes('/login') && !to.path.includes('/register')) {
      if (!isLoggedIn) {
        // 未登入時導向管理員登入頁
        return next(`/admin/login?redirect=${to.path}`);
      } else if (!isAdmin) {
        // 非管理員導向無權限頁面
        return next('/403');
      }
    }
    
    // 對於需要登入的普通頁面
    if (!isLoggedIn && !accessControlMap.guest.some(path => to.path.startsWith(path))) {
      // 儲存目標頁面，登入後重定向
      return next(`/login?redirect=${to.path}`);
    }
    
    // 檢查是否有權限訪問該路由
    if (isLoggedIn && !hasRouteAccess(to, userRole)) {
      // 無權限時，重定向到 403 頁面
      return next('/403');
    }
    
    // 允許訪問
    next();
  });
}

export default setupAuthGuard;