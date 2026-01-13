// 路由配置 (index.js)
// 在現有代碼中添加或修改以下部分：

import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import authService from '../services/auth/authService'

// 延遲加載其他視圖
const AboutView = () => import('../views/AboutView.vue')

// 身份驗證相關頁面
const LoginView = () => import('../views/auth/LoginView.vue')
const RegisterView = () => import('../views/auth/RegisterView.vue')
const ForgotPasswordView = () => import('../views/auth/ForgotPasswordView.vue')

// 使用者相關頁面
const ProfileView = () => import('../views/user/ProfileView.vue')
const BookingView = () => import('../views/user/BookingView.vue')
const FavoritesView = () => import('../views/user/FavoritesView.vue')

// 會員中心頁面 (替代原來的露營地頁面)
const MemberView = () => import('../views/member/MemberView.vue')
const CampDetailView = () => import('../views/camping/CampDetailView.vue')
const MyCampView = () => import('@/views/camping/MyCampView.vue')
const MyCampEditView = () => import('@/views/camping/MyCampEditView.vue')
const MyCampCreateView = () => import('@/views/camping/MyCampCreateView.vue')
const MyCampSiteEditView = () => import('@/views/camping/MyCampSiteEditView.vue')
const MyCampSiteCreateView = () => import('@/views/camping/MyCampSiteCreateView.vue')
const SearchBookingResultsView = () => import('@/views/camping/SearchBookingResultsView.vue')


// 管理員相關頁面
const AdminDashboard = () => import('../views/admin/DashboardView.vue')

// 使用已有的管理員相關組件
const UserList = () => import('../views/admin/UserList.vue')
const RoleList = () => import('../views/admin/RoleList.vue')
// 修正這裡：使用正確的管理頁面組件
const CampManagement = () => import('../views/admin/CampManagement.vue')
const BookingManagement = () => import('../views/admin/BookingManagement.vue')
const ReviewManagement = () => import('../views/admin/ReviewManagement.vue')

// 新增的後台管理頁面
const SystemLogs = () => import('../views/admin/SystemLogs.vue')
const PermissionManagement = () => import('../views/admin/PermissionManagement.vue')
const RoleRequests = () => import('../views/admin/RoleRequests.vue')

// 錯誤頁面
const NotFoundView = () => import('../views/NotFoundView.vue')
const ForbiddenView = () => import('../views/ForbiddenView.vue')

//-------------------------------------

//-------------------------------------

//reviews-------------------------------------
const AdminReviewsView = () => import('../views/reviews/AdminReviewsView.vue')
const RootLayout = () => import('../views/reviews/RootLayout.vue')


//--------------------------------------------

const CampBooking = () => import('../views/ShoppingCart/CampBooking.vue')
const ShoppingCart = () => import('../views/ShoppingCart/ShoppingCart.vue')
const OrderItems = () => import('../views/ShoppingCart/OrderItems.vue')
const OrderSuccess = () => import('../views/ShoppingCart/OrderSuccess.vue')
const Order = () => import('../views/ShoppingCart/Order.vue')

//-------------------------------------


// 目前僅有產品列表頁
const Product = () => import('@/views/pages/Product.vue')
const ProductFront = () => import('@/views/pages/ProductFront.vue')



const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        // 公共頁面
        {
            path: '/',
            name: 'home',
            component: HomeView,
            meta: {
                title: '首頁 - 露營網站',
                layout: 'default'
            }
        },
        {
            path: '/about',
            name: 'about',
            component: AboutView,
            meta: {
                title: '關於我們 - 露營網站',
                layout: 'default'
            }
        },

        // 身份驗證頁面 - 使用auth布局
        {
            path: '/login',
            name: 'login',
            component: LoginView,
            meta: {
                title: '登入 - 露營網站',
                layout: 'auth'
            }
        },
        {
            path: '/register',
            name: 'register',
            component: RegisterView,
            meta: {
                title: '註冊 - 露營網站',
                layout: 'auth'
            }
        },
        {
            path: '/forgot-password',
            name: 'forgot-password',
            component: ForgotPasswordView,
            meta: {
                title: '忘記密碼 - 露營網站',
                layout: 'auth'
            }
        },

        // 管理員登入頁面 - 使用auth布局
        {
            path: '/admin/login',
            name: 'admin-login',
            component: LoginView,
            props: { isAdmin: true },
            meta: {
                title: '管理員登入 - 露營網站',
                layout: 'auth'
            }
        },

        // 會員中心頁面 (替代露營地頁面) - 使用default布局
        {
            path: '/member',
            name: 'member',
            component: MemberView,
            meta: {
                title: '會員中心 - 露營網站',
                layout: 'default'
            }
        },
        {
            path: '/camp/:id',
            name: 'camp-detail',
            component: CampDetailView,
            meta: {
                title: '露營地詳情 - 露營網站',
                layout: 'default'
            }
        },
        {
            path: '/my-camp',
            name: 'my-camp',
            component: MyCampView,
            meta: {
                title: '我的露營地',
                layout: 'default'
            }
        },
        {
            path: '/my-camp/edit/:id',
            name: 'my-camp-edit',
            component: MyCampEditView,
            meta: {
                title: '我的露營區-編輯',
                layout: 'default'
            }
        },
        {
            path: '/my-camp/edit-site/:campSpotId',
            name: 'EditCampSite',
            component: MyCampSiteEditView,
            meta: {
                title: '營位營地-編輯',
                layout: 'default'
            }
        },
        {
            path: '/my-camp/create',
            name: 'my-camp-create',
            component: MyCampCreateView,
            meta: {
                title: '我的露營區-新增',
                layout: 'default'
            }
        },
        {
            path: '/my-camp/create-site/:campAreaId',
            name: 'my-camp-create-site',
            component: MyCampSiteCreateView,
            meta: {
                title: '我的露營地營位-新增',
                layout: 'default'
            }
        },
        {
            path: '/camp-spots/booking/search',
            name: 'camp-spot-booking-search',
            component: SearchBookingResultsView,
            meta: {
                title: '營位訂房搜尋',
                layout: 'default'
            }
        },

        // 使用者頁面 (需要登入) - 使用default布局
        {
            path: '/profile',
            name: 'profile',
            component: ProfileView,
            meta: {
                requiresAuth: true,
                title: '個人資料 - 露營網站',
                layout: 'default'
            }
        },
        {
            path: '/booking',
            name: 'booking',
            component: BookingView,
            meta: {
                requiresAuth: true,
                title: '我的預約 - 露營網站',
                layout: 'default'
            }
        },
        {
            path: '/favorites',
            name: 'favorites',
            component: FavoritesView,
            meta: {
                requiresAuth: true,
                title: '收藏露營地 - 露營網站',
                layout: 'default'
            }
        },

        //產品
        {
            path: '/admin/products',
            name: 'productBack',
            component: Product,
            meta: {
                requiresAuth: true,
                title: '商品後台',
                layout: 'default'
            }
        },
        {
            path: '/productFront',
            name: 'productFront',
            component: ProductFront,
            meta: {
                // requiresAuth: true,
                title: '商品前台',
                layout: 'default'
            }
        },


        // 管理員頁面 (需要管理員權限) - 使用admin布局
        {
            path: '/admin',
            name: 'admin',
            component: AdminDashboard,
            meta: {
                requiresAuth: true,
                requiresAdmin: true,
                title: '管理中心 - 露營網站',
                layout: 'admin'
            }
        },
        {
            path: '/admin/users',
            name: 'user-management',
            component: UserList,
            meta: {
                requiresAuth: true,
                requiresAdmin: true,
                title: '使用者管理 - 露營網站',
                layout: 'admin'
            }
        },
        {
            path: '/admin/roles',
            name: 'role-management',
            component: RoleList,
            meta: {
                requiresAuth: true,
                requiresAdmin: true,
                title: '角色管理 - 露營網站',
                layout: 'admin'
            }
        },
        {
            path: '/admin/camps',
            name: 'camp-management',
            component: CampManagement,
            meta: {
                requiresAuth: true,
                requiresAdmin: true,
                title: '露營地管理 - 露營網站',
                layout: 'admin'
            }
        },
        {
            path: '/admin/bookings',
            name: 'booking-management',
            component: BookingManagement,
            meta: {
                requiresAuth: true,
                requiresAdmin: true,
                title: '預約管理 - 露營網站',
                layout: 'admin'
            }
        },
        // {
        //     path: '/admin/reviews',
        //     name: 'review-management',
        //     component: ReviewManagement,
        //     meta: {
        //         requiresAuth: true,
        //         requiresAdmin: true,
        //         title: '評價管理 - 露營網站',
        //         layout: 'admin'
        //     }
        // },

        // 新增的後台管理路由 - 使用admin布局
        {
            path: '/admin/logs',
            name: 'system-logs',
            component: SystemLogs,
            meta: {
                requiresAuth: true,
                requiresAdmin: true,
                title: '系統日誌 - 露營網站',
                layout: 'admin'
            }
        },
        {
            path: '/admin/permissions',
            name: 'permission-management',
            component: PermissionManagement,
            meta: {
                requiresAuth: true,
                requiresAdmin: true,
                title: '權限管理 - 露營網站',
                layout: 'admin'
            }
        },
        {
            path: '/admin/role-requests',
            name: 'role-requests',
            component: RoleRequests,
            meta: {
                requiresAuth: true,
                requiresAdmin: true,
                title: '角色申請審核 - 露營網站',
                layout: 'admin'
            }
        },

        // 錯誤頁面
        {
            path: '/403',
            name: 'forbidden',
            component: ForbiddenView,
            meta: {
                title: '無權訪問 - 露營網站',
                layout: 'auth'
            }
        },
        {
            path: '/:pathMatch(.*)*',
            name: 'not-found',
            component: NotFoundView,
            meta: {
                title: '頁面未找到 - 露營網站',
                layout: 'auth'
            }
        },
        //購物車start----------------------------

    {
      path: '/campBooking',
      name: 'CampBooking',
      component: CampBooking,
      meta: { 
        layout: 'default'
      }
    },
    {
      path: '/shoppingCart',
      name: 'ShoppingCart',
      component: ShoppingCart,
      meta: { 
        layout: 'default'
      }
    },
    {
      path: '/orderItems',
      name: 'OrderItems',
      component: OrderItems,
      meta: { 
        layout: 'default'
      }
    },
    {
      path: '/orderItems/success',
      name: 'OrderSuccess',
      component: OrderSuccess,
      meta: { 
        layout: 'default'
      }
    },
    {
      path: '/admin/orders',
      name: 'Order',
      component: Order,
      meta: { 
        layout: 'admin'
      }
    },
    //end------------------------------------


    //---------------------------------------
    {
      path: '/reviews',
      name: 'reviews',
      component: RootLayout,
      meta: { 
        layout: 'default'
      }
    },
    {
      path: '/admin/reviews',
      name: 'AdminReviewsView',
      component: AdminReviewsView,
      meta: { 
        layout: 'admin'
      }
    },
    //---------------------------------------
    ]
})

// 更新頁面標題並檢查認證
router.beforeEach((to, from, next) => {
    // 設置頁面標題
    document.title = to.meta.title || '露營網站';

    // 檢查認證狀態
    const isLoggedIn = authService.isLoggedIn();
    const isAdmin = authService.isAdmin();

    // 處理管理員登入頁面特殊情況
    if (to.name === 'admin-login' && isLoggedIn) {
        if (isAdmin) {
            // 已登入的管理員嘗試訪問登入頁面，直接導向管理後台
            return next({ name: 'admin' });
        } else {
            // 已登入但不是管理員的用戶嘗試訪問管理員頁面，導向無權限頁面
            return next({ name: 'forbidden' });
        }
    }

    // 處理一般登入頁面特殊情況
    if (to.name === 'login' && isLoggedIn) {
        if (isAdmin) {
            // 已登入的管理員訪問普通登入頁，導向管理後台
            return next({ name: 'admin' });
        } else {
            // 已登入的普通用戶訪問普通登入頁，導向首頁
            return next({ name: 'home' });
        }
    }

    // 檢查是否需要認證及權限
    const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
    const requiresAdmin = to.matched.some(record => record.meta.requiresAdmin);

    if (requiresAuth && !isLoggedIn) {
        // 需要登入但還沒登入
        if (requiresAdmin) {
            // 如果是管理頁面，導向管理員登入頁
            return next({
                name: 'admin-login',
                query: { redirect: to.fullPath }
            });
        } else {
            // 如果是一般頁面，導向一般登入頁
            return next({
                name: 'login',
                query: { redirect: to.fullPath }
            });
        }
    } else if (requiresAdmin && !isAdmin) {
        // 需要管理員權限但沒有
        return next({ name: 'forbidden' });
    } else {
        // 正常導航
        next();
    }
});

export default router