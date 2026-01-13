<template>
    <div class="member-center">
        <div class="member-header">
            <h1 class="page-title">會員中心</h1>
            <p class="welcome-message" v-if="isLoggedIn">歡迎回來，<span>{{ username }}</span></p>
        </div>
        
        <div class="member-dashboard">
            <!-- 會員資訊卡 -->
            <div class="info-card profile-card">
                <div class="card-header">
                    <h2><i class="fa fa-user"></i> 會員資訊</h2>
                </div>
                <div class="card-body">
                    <div class="member-profile">
                        <div class="member-avatar">
                            <i class="fa fa-user-circle"></i>
                        </div>
                        <div v-if="isLoggedIn" class="profile-details">
                            <h3 class="member-name">{{ username }}</h3>
                            <div class="member-level">{{ userInfo.level || '一般會員' }}</div>
                            <p class="member-since">加入時間：{{ formatDate(userInfo.createdAt) }}</p>
                            <div class="action-buttons">
                                <router-link to="/profile" class="member-btn">
                                    <i class="fa fa-user-edit"></i> 編輯個人資料
                                </router-link>
                            </div>
                        </div>
                        <div v-else class="login-prompt">
                            <p>您尚未登入，請先登入以查看會員資訊。</p>
                            <div class="action-buttons">
                                <router-link to="/login" class="member-btn">
                                    <i class="fa fa-sign-in-alt"></i> 登入
                                </router-link>
                                <router-link to="/register" class="member-btn-secondary">
                                    <i class="fa fa-user-plus"></i> 註冊
                                </router-link>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- 最近活動 -->
            <div class="info-card">
                <div class="card-header">
                    <h2><i class="fa fa-history"></i> 最近活動</h2>
                    <router-link v-if="activities.length > 0" to="/activities" class="view-all">
                        查看全部 <i class="fa fa-angle-right"></i>
                    </router-link>
                </div>
                <div class="card-body">
                    <div v-if="isLoggedIn">
                        <div v-if="loading" class="loading-indicator">
                            <div class="spinner">
                                <i class="fa fa-spinner fa-spin"></i>
                            </div>
                            <p>載入中...</p>
                        </div>
                        <div v-else-if="activities.length > 0" class="activity-list">
                            <div class="activity-item" v-for="(activity, index) in activities" :key="index">
                                <div class="activity-icon">
                                    <i :class="getActivityIcon(activity.type)"></i>
                                </div>
                                <div class="activity-content">
                                    <div class="activity-title">{{ activity.title }}</div>
                                    <div class="activity-desc" v-if="activity.description">{{ activity.description }}</div>
                                    <div class="activity-date">{{ formatDate(activity.date) }}</div>
                                </div>
                            </div>
                        </div>
                        <div v-else class="empty-state">
                            <i class="fa fa-calendar-day"></i>
                            <p>暫無活動記錄</p>
                        </div>
                    </div>
                    <div v-else class="login-prompt">
                        <p>登入後即可查看您的活動記錄。</p>
                    </div>
                </div>
            </div>
            
            <!-- 最近預約 -->
            <div class="info-card">
                <div class="card-header">
                    <h2><i class="fa fa-campground"></i> 最近預約</h2>
                    <router-link v-if="recentBookings.length > 0" to="/booking" class="view-all">
                        查看全部 <i class="fa fa-angle-right"></i>
                    </router-link>
                </div>
                <div class="card-body">
                    <div v-if="isLoggedIn">
                        <div v-if="loadingBookings" class="loading-indicator">
                            <div class="spinner">
                                <i class="fa fa-spinner fa-spin"></i>
                            </div>
                            <p>載入中...</p>
                        </div>
                        <div v-else-if="recentBookings.length > 0" class="booking-list">
                            <div class="booking-item" v-for="(booking, index) in recentBookings" :key="index">
                                <div class="booking-title">{{ booking.campsite }}</div>
                                <div class="booking-details">
                                    <span class="booking-date"><i class="fa fa-calendar-alt"></i> {{ formatDateRange(booking.startDate, booking.endDate) }}</span>
                                    <span class="booking-status" :class="getStatusClass(booking.status)">
                                        {{ getStatusText(booking.status) }}
                                    </span>
                                </div>
                                <div class="booking-actions">
                                    <router-link :to="`/booking/${booking.id}`" class="action-link">
                                        查看詳情
                                    </router-link>
                                </div>
                            </div>
                        </div>
                        <div v-else class="empty-state">
                            <i class="fa fa-calendar-plus"></i>
                            <p>您目前沒有預約記錄</p>
                            <router-link to="/" class="member-btn">
                                <i class="fa fa-search"></i> 探索露營地
                            </router-link>
                        </div>
                    </div>
                    <div v-else class="login-prompt">
                        <p>登入後即可查看您的預約記錄。</p>
                    </div>
                </div>
            </div>
            
            <!-- 快速功能 -->
            <div class="info-card quick-actions-card">
                <div class="card-header">
                    <h2><i class="fa fa-th-large"></i> 快速功能</h2>
                </div>
                <div class="card-body">
                    <div class="quick-links">
                        <router-link to="/booking" class="quick-link">
                            <div class="quick-link-icon">
                                <i class="fa fa-calendar-check"></i>
                            </div>
                            <div class="quick-link-text">
                                <span class="quick-link-title">我的預約</span>
                                <span class="quick-link-desc">查看和管理您的所有預約</span>
                            </div>
                        </router-link>
                        
                        <router-link to="/favorites" class="quick-link">
                            <div class="quick-link-icon">
                                <i class="fa fa-heart"></i>
                            </div>
                            <div class="quick-link-text">
                                <span class="quick-link-title">收藏露營地</span>
                                <span class="quick-link-desc">查看您收藏的所有營地</span>
                            </div>
                        </router-link>
                        
                        <router-link to="/reviews" class="quick-link">
                            <div class="quick-link-icon">
                                <i class="fa fa-star"></i>
                            </div>
                            <div class="quick-link-text">
                                <span class="quick-link-title">我的評論</span>
                                <span class="quick-link-desc">管理您的營地評論</span>
                            </div>
                        </router-link>
                        
                        <router-link to="/profile" class="quick-link">
                            <div class="quick-link-icon">
                                <i class="fa fa-user-edit"></i>
                            </div>
                            <div class="quick-link-text">
                                <span class="quick-link-title">個人資料</span>
                                <span class="quick-link-desc">更新您的個人資訊</span>
                            </div>
                        </router-link>
                        
                        <a href="#" class="quick-link" @click.prevent="handleLogout" v-if="isLoggedIn">
                            <div class="quick-link-icon logout-icon">
                                <i class="fa fa-sign-out-alt"></i>
                            </div>
                            <div class="quick-link-text">
                                <span class="quick-link-title">登出</span>
                                <span class="quick-link-desc">登出您的帳號</span>
                            </div>
                        </a>
                        
                        <router-link to="/login" class="quick-link" v-else>
                            <div class="quick-link-icon">
                                <i class="fa fa-sign-in-alt"></i>
                            </div>
                            <div class="quick-link-text">
                                <span class="quick-link-title">登入</span>
                                <span class="quick-link-desc">登入您的帳號</span>
                            </div>
                        </router-link>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../../stores/auth';
import { useToast } from '../../composables/useToast';
import apiService from '../../services/api/apiService';

export default {
  name: 'MemberView',
  setup() {
    const router = useRouter();
    const authStore = useAuthStore();
    const { showToast } = useToast();

    // 用戶資訊
    const userInfo = ref({});
    // 用戶活動
    const activities = ref([]);
    const loading = ref(false);
    // 用戶預約
    const recentBookings = ref([]);
    const loadingBookings = ref(false);

    // 計算屬性
    const isLoggedIn = computed(() => authStore.isLoggedIn);
    const username = computed(() => {
        return authStore.fullName || authStore.username || '';
    });

    // 獲取用戶信息
    const fetchUserInfo = async () => {
        if (!isLoggedIn.value) return;
        
        try {
            loading.value = true;
            // 獲取API數據
            const response = await apiService.get('/users/profile');
            
            if (response.data) {
                userInfo.value = response.data;
            } else {
                // 基本信息
                userInfo.value = {
                    fullName: authStore.fullName || '',
                    username: authStore.username || '',
                    level: '一般會員',
                    createdAt: authStore.user?.createdAt || new Date().toISOString()
                };
            }
        } catch (error) {
            console.error('獲取用戶信息失敗:', error);
            // 使用Auth Store中的基本信息
            userInfo.value = {
                fullName: authStore.fullName || '',
                username: authStore.username || '',
                level: '一般會員',
                createdAt: authStore.user?.createdAt || new Date().toISOString()
            };
        } finally {
            loading.value = false;
        }
    };

    // 獲取用戶活動
    const fetchUserActivities = async () => {
        if (!isLoggedIn.value) return;
        
        try {
            loading.value = true;
            // 獲取API數據
            const response = await apiService.get('/users/activities');
            
            if (response.data && Array.isArray(response.data)) {
                activities.value = response.data;
            } else {
                activities.value = [];
            }
        } catch (error) {
            console.error('獲取活動記錄失敗:', error);
            activities.value = [];
        } finally {
            loading.value = false;
        }
    };

    // 獲取用戶最近預約
    const fetchRecentBookings = async () => {
        if (!isLoggedIn.value) return;
        
        try {
            loadingBookings.value = true;
            // 獲取API數據
            const response = await apiService.get('/users/bookings', { params: { limit: 3 } });
            
            if (response.data && Array.isArray(response.data)) {
                recentBookings.value = response.data;
            } else {
                recentBookings.value = [];
            }
        } catch (error) {
            console.error('獲取預約記錄失敗:', error);
            recentBookings.value = [];
        } finally {
            loadingBookings.value = false;
        }
    };

    // 格式化日期
    const formatDate = (dateString) => {
        if (!dateString) return '';
        try {
            const date = new Date(dateString);
            return date.toLocaleDateString('zh-TW', {
                year: 'numeric',
                month: 'long',
                day: 'numeric'
            });
        } catch (e) {
            return '';
        }
    };

    // 格式化日期範圍
    const formatDateRange = (startDate, endDate) => {
        if (!startDate || !endDate) return '';
        
        try {
            const start = new Date(startDate);
            const end = new Date(endDate);
            
            // 如果在同一個月，只顯示一次月份
            if (start.getMonth() === end.getMonth() && start.getFullYear() === end.getFullYear()) {
                return `${start.getFullYear()}年${start.getMonth() + 1}月${start.getDate()}日 - ${end.getDate()}日`;
            } else {
                return `${formatDate(startDate)} - ${formatDate(endDate)}`;
            }
        } catch (e) {
            return '';
        }
    };

    // 根據活動類型獲取對應圖標
    const getActivityIcon = (type) => {
        const iconMap = {
            'booking': 'fa fa-campground',
            'review': 'fa fa-star',
            'favorite': 'fa fa-heart',
            'login': 'fa fa-sign-in-alt',
            'register': 'fa fa-user-plus',
            'profile': 'fa fa-user-edit',
            'payment': 'fa fa-credit-card',
            'message': 'fa fa-comment',
            'notification': 'fa fa-bell'
        };
        return iconMap[type] || 'fa fa-history';
    };

    // 獲取預約狀態文字
    const getStatusText = (status) => {
        const statusMap = {
            'pending': '待確認',
            'confirmed': '已確認',
            'completed': '已完成',
            'cancelled': '已取消'
        };
        return statusMap[status] || status;
    };

    // 獲取預約狀態樣式
    const getStatusClass = (status) => {
        return `status-${status}`;
    };

    // 在組件掛載時檢查並更新登入狀態，並獲取數據
    onMounted(async () => {
        // 檢查是否有 token 但沒有用戶信息
        const token = localStorage.getItem('accessToken');
        const userJson = localStorage.getItem('user');
        
        if (token && userJson && !authStore.user) {
            try {
                // 從 localStorage 讀取用戶資料更新到 store
                const userData = JSON.parse(userJson);
                if (userData) {
                    console.log('從快取恢復用戶資訊');
                    // 手動更新 store 狀態
                    authStore.$patch({
                        user: userData,
                        accessToken: token,
                        role: localStorage.getItem('userRole') || ''
                    });
                }
            } catch (error) {
                console.error('讀取用戶資料失敗:', error);
            }
        }

        // 獲取用戶數據
        if (isLoggedIn.value) {
            await Promise.all([
                fetchUserInfo(),
                fetchUserActivities(),
                fetchRecentBookings()
            ]).catch(err => {
                console.error('獲取用戶數據時發生錯誤:', err);
            });
        }
    });

    // 處理登出
    const handleLogout = async () => {
        try {
            await authStore.logout();
            router.push('/');
            showToast('登出成功', 'success');
        } catch (error) {
            console.error('登出失敗:', error);
            // 即使API調用失敗，也嘗試清除本地狀態
            localStorage.removeItem('accessToken');
            localStorage.removeItem('user');
            localStorage.removeItem('userRole');
            authStore.$reset();
            router.push('/');
            showToast('登出成功', 'success');
        }
    };

    return {
        isLoggedIn,
        username,
        userInfo,
        activities,
        loading,
        recentBookings,
        loadingBookings,
        formatDate,
        formatDateRange,
        getActivityIcon,
        getStatusText,
        getStatusClass,
        handleLogout
    };
  }
}
</script>

<style scoped>
/* 全局變量 */
:root {
    --tiffany-blue: #81D8D0;
    --tiffany-blue-light: #A7E8E1;
    --tiffany-blue-dark: #5EBFB7;
    --tiffany-blue-bg: #F0FAFA;
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
}

/* 基本佈局 */
.member-center {
    max-width: 1200px;
    margin: 0 auto;
    padding: 40px 20px;
    background-color: #f9fafb;
    min-height: calc(100vh - 80px);
}

.member-header {
    margin-bottom: 30px;
    position: relative;
}

.page-title {
    font-size: 32px;
    color: var(--gray-900);
    margin: 0 0 10px 0;
    font-weight: 800;
    position: relative;
    display: inline-block;
}

.page-title::after {
    content: '';
    position: absolute;
    bottom: -5px;
    left: 0;
    width: 100px;
    height: 4px;
    background: linear-gradient(90deg, var(--tiffany-blue) 0%, var(--tiffany-blue-dark) 100%);
    border-radius: 2px;
}

.welcome-message {
    font-size: 18px;
    color: var(--gray-600);
    margin: 10px 0 0 0;
}

.welcome-message span {
    color: var(--tiffany-blue-dark);
    font-weight: 600;
}

.member-dashboard {
    display: grid;
    grid-template-columns: repeat(12, 1fr);
    gap: 24px;
}

/* 卡片樣式 */
.info-card {
    background-color: white;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
    transition: transform 0.3s, box-shadow 0.3s;
    border: none;
    grid-column: span 6;
}

.profile-card {
    grid-column: span 12;
}

.quick-actions-card {
    grid-column: span 12;
}

.info-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
}

.card-header {
    background: linear-gradient(135deg, var(--tiffany-blue) 0%, var(--tiffany-blue-dark) 100%);
    padding: 20px 24px;
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.card-header h2 {
    margin: 0;
    font-size: 20px;
    font-weight: 600;
    color: white;
    display: flex;
    align-items: center;
    gap: 10px;
}

.view-all {
    color: white;
    font-size: 14px;
    display: flex;
    align-items: center;
    gap: 5px;
    text-decoration: none;
    opacity: 0.9;
    transition: opacity 0.2s;
}

.view-all:hover {
    opacity: 1;
    text-decoration: underline;
}

.card-body {
    padding: 24px;
}

/* 會員資訊 */
.member-profile {
    display: flex;
    align-items: center;
    gap: 30px;
}

.member-avatar {
    width: 100px;
    height: 100px;
    background-color: var(--tiffany-blue-bg);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 5px 15px rgba(129, 216, 208, 0.2);
    border: 3px solid white;
    flex-shrink: 0;
}

.member-avatar i {
    font-size: 50px;
    color: var(--tiffany-blue);
}

.profile-details {
    flex: 1;
}

.member-name {
    font-size: 24px;
    font-weight: 700;
    color: var(--gray-900);
    margin: 0 0 8px 0;
}

.member-level {
    display: inline-block;
    background-color: var(--tiffany-blue-bg);
    color: var(--tiffany-blue-dark);
    padding: 5px 12px;
    border-radius: 20px;
    font-size: 14px;
    font-weight: 600;
    margin-bottom: 12px;
}

.member-since {
    color: var(--gray-500);
    font-size: 15px;
    margin: 0 0 15px 0;
}

.action-buttons {
    display: flex;
    gap: 12px;
    margin-top: 15px;
}

.member-btn {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    background: linear-gradient(135deg, var(--tiffany-blue) 0%, var(--tiffany-blue-dark) 100%);
    color: white;
    padding: 10px 20px;
    border-radius: 8px;
    font-weight: 600;
    font-size: 15px;
    transition: all 0.3s ease;
    border: none;
    cursor: pointer;
    text-decoration: none;
    box-shadow: 0 4px 10px rgba(94, 191, 183, 0.25);
}

.member-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 15px rgba(94, 191, 183, 0.35);
}

.member-btn-secondary {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    background-color: white;
    color: var(--tiffany-blue-dark);
    padding: 10px 20px;
    border-radius: 8px;
    font-weight: 600;
    font-size: 15px;
    transition: all 0.3s ease;
    border: 1px solid var(--tiffany-blue-light);
    cursor: pointer;
    text-decoration: none;
}

.member-btn-secondary:hover {
    background-color: var(--tiffany-blue-bg);
    transform: translateY(-2px);
    box-shadow: 0 4px 10px rgba(94, 191, 183, 0.15);
}

.login-prompt {
    text-align: center;
    padding: 20px;
}

.login-prompt p {
    margin-bottom: 20px;
    color: var(--gray-600);
    font-size: 16px;
}

/* 活動列表 */
.activity-list {
    max-height: 350px;
    overflow-y: auto;
    padding-right: 5px;
}

.activity-list::-webkit-scrollbar {
    width: 5px;
}

.activity-list::-webkit-scrollbar-track {
    background: var(--gray-100);
    border-radius: 5px;
}

.activity-list::-webkit-scrollbar-thumb {
    background-color: var(--gray-300);
    border-radius: 5px;
}

.activity-list::-webkit-scrollbar-thumb:hover {
    background-color: var(--gray-400);
}

.activity-item {
    display: flex;
    align-items: flex-start;
    padding: 16px;
    border-bottom: 1px solid var(--gray-100);
    transition: background-color 0.2s;
}

.activity-item:hover {
    background-color: var(--tiffany-blue-bg);
}

.activity-item:last-child {
    border-bottom: none;
}

.activity-icon {
    width: 45px;
    height: 45px;
    background-color: white;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 15px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
    flex-shrink: 0;
}

.activity-icon i {
    color: var(--tiffany-blue);
    font-size: 20px;
}

.activity-content {
    flex: 1;
}

.activity-title {
    font-weight: 600;
    font-size: 16px;
    margin-bottom: 5px;
    color: var(--gray-800);
}

.activity-desc {
    color: var(--gray-600);
    font-size: 14px;
    margin-bottom: 8px;
    line-height: 1.5;
}

.activity-date {
    font-size: 13px;
    color: var(--gray-500);
}

/* 預約樣式 */
.booking-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.booking-item {
    background-color: white;
    border-radius: 12px;
    padding: 18px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    transition: all 0.3s ease;
    border-left: 4px solid var(--tiffany-blue);
}

.booking-item:hover {
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
    transform: translateX(5px);
}

.booking-title {
    font-weight: 700;
    font-size: 18px;
    margin-bottom: 12px;
    color: var(--gray-800);
}

.booking-details {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 12px;
    margin-bottom: 12px;
}

.booking-date {
    color: var(--gray-600);
    font-size: 14px;
    display: flex;
    align-items: center;
    gap: 8px;
}

.booking-date i {
    color: var(--tiffany-blue);
}

.booking-status {
    padding: 5px 12px;
    border-radius: 20px;
    font-size: 13px;
    font-weight: 600;
}

.status-pending {
    background-color: #FEF9C3;
    color: #CA8A04;
}

.status-confirmed {
    background-color: #DCFCE7;
    color: #16A34A;
}

.status-completed {
    background-color: #E0F2FE;
    color: #0284C7;
}

.status-cancelled {
    background-color: #FEE2E2;
    color: #DC2626;
}

.booking-actions {
    text-align: right;
    padding-top: 8px;
    border-top: 1px solid var(--gray-100);
}

.action-link {
    color: var(--tiffany-blue-dark);
    font-size: 14px;
    font-weight: 500;
    text-decoration: none;
    display: inline-block;
    padding: 5px 0;
    transition: color 0.2s;
}

.action-link:hover {
    color: var(--tiffany-blue);
    text-decoration: underline;
}

/* 快速功能 */
.quick-links {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
}

.quick-link {
    display: flex;
    align-items: center;
    background-color: white;
    border-radius: 12px;
    padding: 20px;
    transition: all 0.3s ease;
    text-decoration: none;
    color: var(--gray-800);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    border: 1px solid var(--gray-100);
}

.quick-link:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
    border-color: var(--tiffany-blue-light);
}

.quick-link-icon {
    width: 50px;
    height: 50px;
    background-color: var(--tiffany-blue-bg);
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 15px;
    flex-shrink: 0;
}

.quick-link-icon i {
    color: var(--tiffany-blue);
    font-size: 22px;
}

.logout-icon {
    background-color: #FEE2E2;
}

.logout-icon i {
    color: #DC2626;
}

.quick-link-text {
    flex: 1;
}

.quick-link-title {
    display: block;
    font-weight: 600;
    font-size: 16px;
    margin-bottom: 5px;
}

.quick-link-desc {
    display: block;
    color: var(--gray-500);
    font-size: 13px;
}

/* 空狀態與載入 */
.empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 40px 20px;
    color: var(--gray-500);
    text-align: center;
}

.empty-state i {
    font-size: 40px;
    color: var(--gray-300);
    margin-bottom: 15px;
}

.empty-state p {
    margin-bottom: 20px;
    font-size: 16px;
}

.loading-indicator {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 40px 20px;
    color: var(--gray-500);
}

.spinner {
    margin-bottom: 15px;
}

.spinner i {
    font-size: 30px;
    color: var(--tiffany-blue);
}

/* 響應式設計 */
@media (max-width: 1024px) {
    .member-dashboard {
        grid-template-columns: repeat(12, 1fr);
    }
    
    .info-card {
        grid-column: span 6;
    }
    
    .profile-card, .quick-actions-card {
        grid-column: span 12;
    }
    
    .quick-links {
        grid-template-columns: repeat(3, 1fr);
    }
}

@media (max-width: 768px) {
    .member-center {
        padding: 30px 15px;
    }
    
    .member-dashboard {
        grid-template-columns: 1fr;
        gap: 20px;
    }
    
    .info-card {
        grid-column: 1;
    }
    
    .quick-links {
        grid-template-columns: repeat(2, 1fr);
    }
    
    .member-profile {
        flex-direction: column;
        text-align: center;
    }
    
    .member-avatar {
        margin: 0 auto 20px;
    }
    
    .action-buttons {
        justify-content: center;
    }
}

@media (max-width: 480px) {
    .quick-links {
        grid-template-columns: 1fr;
    }
    
    .booking-details {
        flex-direction: column;
        align-items: flex-start;
    }
    
    .member-btn, .member-btn-secondary {
        width: 100%;
        justify-content: center;
    }
}
</style>