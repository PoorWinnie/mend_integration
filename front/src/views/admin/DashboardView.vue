<template>
  <div class="admin-dashboard">
    <!-- 標題與使用者信息 -->
    <header class="dashboard-header">
      <div class="title-section">
        <h1 class="page-title">系統管理中心 - {{ role }}</h1>
      </div>
      <div class="action-buttons">
        <button class="btn-export" @click="exportData">
          <i class="fa fa-download"></i> 匯出報表
        </button>
      </div>
    </header>
    
    <!-- 統計卡片行 -->
    <div class="stats-cards">
      <!-- 總用戶數 -->
      <div class="stat-card">
        <div class="stat-icon user-icon">
          <i class="fa fa-users"></i>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.userCount || stats.totalUsers }}</div>
          <div class="stat-label">總用戶數</div>
          <div class="stat-change" :class="{ 'positive': stats.userGrowth > 0, 'negative': stats.userGrowth < 0 }">
            <i :class="['fa', stats.userGrowth > 0 ? 'fa-arrow-up' : 'fa-arrow-down']"></i>
            {{ Math.abs(stats.userGrowth) }}% 相較上月
          </div>
        </div>
      </div>
      
      <!-- 活躍用戶 -->
      <div class="stat-card">
        <div class="stat-icon active-icon">
          <i class="fa fa-chart-line"></i>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.bookingCount || stats.activeUsers }}</div>
          <div class="stat-label">活躍使用者</div>
          <div class="stat-change" :class="{ 'positive': stats.bookingGrowth > 0, 'negative': stats.bookingGrowth < 0 }">
            <i :class="['fa', stats.bookingGrowth > 0 ? 'fa-arrow-up' : 'fa-arrow-down']"></i>
            {{ Math.abs(stats.bookingGrowth || stats.activeGrowth) }}% 相較上月
          </div>
        </div>
      </div>
      
      <!-- 待審核露營地 -->
      <div class="stat-card">
        <div class="stat-icon pending-icon">
          <i class="fa fa-campground"></i>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.campCount || stats.pendingCamps }}</div>
          <div class="stat-label">待審核露營地</div>
          <div class="stat-change" :class="{ 'positive': stats.campGrowth > 0, 'negative': stats.campGrowth < 0 }">
            <i :class="['fa', stats.campGrowth > 0 ? 'fa-arrow-up' : 'fa-arrow-down']"></i>
            {{ Math.abs(stats.campGrowth) }}% 相較上月
          </div>
        </div>
      </div>
      
      <!-- 系統警報 -->
      <div class="stat-card">
        <div class="stat-icon alert-icon">
          <i class="fa fa-exclamation-triangle"></i>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ alerts || stats.alerts }}</div>
          <div class="stat-label">系統警報</div>
          <div class="stat-change" :class="{ 'positive': alertGrowth < 0, 'negative': alertGrowth > 0 }">
            <i :class="['fa', alertGrowth > 0 ? 'fa-arrow-up' : 'fa-arrow-down']"></i>
            {{ Math.abs(alertGrowth || stats.alertGrowth) }}% 相較上月
          </div>
        </div>
      </div>
    </div>
    
    <!-- 系統使用圖表 -->
    <div class="chart-container">
      <div class="section-header">
        <h2>系統使用概況</h2>
      </div>
      
      <div class="chart-placeholder" v-if="loading">
        <div class="loading-spinner">
          <i class="fa fa-spinner fa-spin"></i>
          <span>圖表載入中...</span>
        </div>
      </div>
      <canvas id="usageChart" ref="usageChartRef" v-else></canvas>
      
      <div class="period-buttons">
        <button 
          @click="changePeriod('week')" 
          :class="{ active: chartPeriod === 'week' }"
        >週</button>
        <button 
          @click="changePeriod('month')" 
          :class="{ active: chartPeriod === 'month' }"
        >月</button>
        <button 
          @click="changePeriod('year')" 
          :class="{ active: chartPeriod === 'year' }"
        >年</button>
      </div>
    </div>
    
    <!-- 最近註冊用戶 -->
    <div class="recent-section">
      <div class="section-header">
        <h2>最近新增的使用者</h2>
        <button class="btn-view-all" @click="viewAllUsers">查看全部</button>
      </div>
      
      <div class="table-loading" v-if="loading">
        <i class="fa fa-spinner fa-spin"></i> 資料載入中...
      </div>
      
      <table class="data-table" v-else>
        <thead>
          <tr>
            <th>名稱</th>
            <th>郵箱</th>
            <th>角色</th>
            <th>註冊時間</th>
            <th>狀態</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in recentUsers" :key="user.id">
            <td>{{ user.fullName || user.username }}</td>
            <td>{{ user.email }}</td>
            <td>{{ getRoleName(user.role) }}</td>
            <td>{{ formatDate(user.createdAt) }}</td>
            <td>
              <span class="status-badge" :class="user.status">
                {{ getStatusText(user.status) }}
              </span>
            </td>
            <td>
              <button class="btn-action edit" @click="editUser(user.id)">
                <i class="fa fa-pencil-alt"></i>
              </button>
              <button class="btn-action delete" @click="deleteUser(user.id)">
                <i class="fa fa-trash"></i>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <!-- 系統動態 -->
    <div class="recent-activities">
      <div class="section-header">
        <h2>系統動態</h2>
        <button class="btn-view-all" @click="viewAllLogs">查看全部</button>
      </div>
      
      <div class="activities-list">
        <div class="activity-item" v-for="(log, index) in systemLogs" :key="index" :class="log.level">
          <div class="activity-icon" :class="getActivityClass(log.level)">
            <i :class="['fa', getActivityIcon(log.level)]"></i>
          </div>
          <div class="activity-content">
            <div class="activity-time">{{ formatTime(log.timestamp) }}</div>
            <div class="activity-level">{{ log.level.toUpperCase() }}</div>
            <div class="activity-message">{{ log.message }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import Chart from 'chart.js/auto';

// 如果您有這些store，請取消註釋
// import { useActivityStore } from '@/stores/activity';
// import { useStatsStore } from '@/stores/stats';
// import { useUserStore } from '@/stores/user';

export default {
  name: 'DashboardView',
  setup() {
    const router = useRouter();
    const authStore = useAuthStore();
    
    // 如果您有這些store，請取消註釋
    // const activityStore = useActivityStore();
    // const statsStore = useStatsStore();
    // const userStore = useUserStore();
    
    // 基本狀態
    const role = ref('超級管理員');
    const loading = ref(true);
    const chartPeriod = ref('month');
    const alerts = ref(3);
    const alertGrowth = ref(2);
    
    // 圖表引用
    const usageChartRef = ref(null);
    let usageChart = null;
    
    // 統計數據 (兼容兩種數據格式)
    const stats = reactive({
      totalUsers: 1234,
      userGrowth: 12,
      activeUsers: 856,
      activeGrowth: 8,
      pendingCamps: 23,
      campGrowth: -5,
      alerts: 3,
      alertGrowth: 2,
      
      // Dashboard.vue 格式
      userCount: 0,
      bookingCount: 0,
      campCount: 0,
      bookingGrowth: 0
    });
    
    // 用戶數據
    const recentUsers = ref([
      {
        id: 1,
        fullName: '王大明',
        email: 'wang@example.com',
        role: '一般使用者',
        status: 'active',
        createdAt: new Date('2023-11-22T08:30:00')
      },
      {
        id: 2,
        fullName: '陳小華',
        email: 'chen@example.com',
        role: '營地管理者',
        status: 'pending',
        createdAt: new Date('2023-11-21T14:15:00')
      },
      {
        id: 3,
        fullName: '林美玲',
        email: 'lin@example.com',
        role: '一般使用者',
        status: 'active',
        createdAt: new Date('2023-11-20T09:45:00')
      },
      {
        id: 4,
        fullName: '張志明',
        email: 'chang@example.com',
        role: '一般使用者',
        status: 'inactive',
        createdAt: new Date('2023-11-19T16:20:00')
      },
      {
        id: 5,
        fullName: '黃雅芳',
        email: 'huang@example.com',
        role: '一般使用者',
        status: 'active',
        createdAt: new Date('2023-11-18T11:10:00')
      }
    ]);
    
    // 系統日誌
    const systemLogs = ref([
      {
        timestamp: new Date('2023-11-22T15:30:00'),
        level: 'info',
        message: '使用者 ID 1234 成功登入'
      },
      {
        timestamp: new Date('2023-11-22T14:45:00'),
        level: 'warning',
        message: '使用者 ID 5678 嘗試重設密碼'
      },
      {
        timestamp: new Date('2023-11-22T13:20:00'),
        level: 'error',
        message: '資料庫連接失敗 - 重試中'
      },
      {
        timestamp: new Date('2023-11-22T12:10:00'),
        level: 'info',
        message: '系統備份完成'
      },
      {
        timestamp: new Date('2023-11-22T11:05:00'),
        level: 'info',
        message: '新增露營地 ID 342 - 等待審核'
      }
    ]);
    
    // 切換圖表時間週期
    const changePeriod = (period) => {
      chartPeriod.value = period;
      loading.value = true;
      
      // 模擬API請求
      setTimeout(() => {
        initUsageChart();
      }, 500);
    };
    
    // 查看所有用戶
    const viewAllUsers = () => {
      router.push('/admin/users');
    };
    
    // 查看所有日誌
    const viewAllLogs = () => {
      router.push('/admin/logs');
    };
    
    // 編輯用戶
    const editUser = (userId) => {
      router.push(`/admin/users/edit/${userId}`);
    };
    
    // 刪除用戶
    const deleteUser = (userId) => {
      if (confirm(`確定要刪除 ID 為 ${userId} 的使用者嗎？`)) {
        console.log(`刪除使用者 ${userId}`);
        // 如果有API可以調用
        // userStore.deleteUser(userId)
        //   .then(() => fetchRecentUsers())
        //   .catch(error => console.error('刪除失敗:', error));
      }
    };
    
    // 匯出報表
    const exportData = () => {
      alert('報表匯出功能即將開發完成');
    };
    
    // 格式化日期
    const formatDate = (date) => {
      if (!date) return '';
      const dateObj = new Date(date);
      return new Intl.DateTimeFormat('zh-TW', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      }).format(dateObj);
    };
    
    // 格式化時間
    const formatTime = (date) => {
      if (!date) return '';
      const dateObj = new Date(date);
      return new Intl.DateTimeFormat('zh-TW', {
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
      }).format(dateObj);
    };
    
    // 格式化相對時間
    const formatTimeAgo = (date) => {
      if (!date) return '';
      const now = new Date();
      const time = new Date(date);
      const diff = Math.floor((now - time) / 60000); // 分鐘差
      
      if (diff < 1) return '剛剛';
      if (diff < 60) return `${diff}分鐘前`;
      
      const hours = Math.floor(diff / 60);
      if (hours < 24) return `${hours}小時前`;
      
      const days = Math.floor(hours / 24);
      if (days < 30) return `${days}天前`;
      
      const months = Math.floor(days / 30);
      return `${months}個月前`;
    };
    
    // 獲取狀態文字
    const getStatusText = (status) => {
      const statusMap = {
        'active': '正常',
        'inactive': '停用',
        'pending': '待驗證'
      };
      return statusMap[status] || status;
    };
    
    // 獲取角色名稱
    const getRoleName = (roleCode) => {
      const roleMap = {
        'admin': '管理員',
        'ADMIN': '管理員',
        'camper': '一般使用者',
        'USER': '一般使用者',
        'owner': '營地管理者',
        'CAMP_OWNER': '營地管理者',
        'SUPER_ADMIN': '超級管理員'
      };
      return roleMap[roleCode] || roleCode;
    };
    
    // 獲取活動圖標
    const getActivityIcon = (level) => {
      const icons = {
        'info': 'fa-info-circle',
        'warning': 'fa-exclamation-circle',
        'error': 'fa-times-circle'
      };
      return icons[level] || 'fa-circle';
    };
    
    // 獲取活動CSS類別
    const getActivityClass = (level) => {
      return level || 'info';
    };
    
    // 初始化系統使用概況圖表
    const initUsageChart = () => {
      if (!usageChartRef.value) return;
      
      if (usageChart) {
        usageChart.destroy();
      }
      
      const ctx = usageChartRef.value.getContext('2d');
      
      // 根據不同的時間週期調整數據
      let chartData;
      if (chartPeriod.value === 'week') {
        chartData = {
          labels: ['週一', '週二', '週三', '週四', '週五', '週六', '週日'],
          values: [65, 59, 80, 81, 56, 55, 40]
        };
      } else if (chartPeriod.value === 'month') {
        chartData = {
          labels: ['1月', '2月', '3月', '4月', '5月', '6月'],
          values: [150, 180, 220, 300, 280, 350]
        };
      } else { // year
        chartData = {
          labels: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
          values: [650, 590, 800, 810, 560, 550, 400, 650, 700, 580, 900, 950]
        };
      }
      
      usageChart = new Chart(ctx, {
        type: 'line',
        data: {
          labels: chartData.labels,
          datasets: [{
            label: '系統使用量',
            data: chartData.values,
            backgroundColor: 'rgba(129, 216, 208, 0.2)',
            borderColor: '#81D8D0',
            borderWidth: 2,
            tension: 0.4,
            pointBackgroundColor: '#81D8D0',
            fill: true
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            y: {
              beginAtZero: true,
              grid: {
                color: 'rgba(0, 0, 0, 0.05)'
              }
            },
            x: {
              grid: {
                display: false
              }
            }
          },
          plugins: {
            legend: {
              display: false
            }
          }
        }
      });
      
      loading.value = false;
    };
    
    // 獲取數據
    const fetchData = async () => {
      loading.value = true;
      
      try {
        // 如果有實際API，可以取消註釋這些
        /*
        // 獲取統計數據
        const statsData = await statsStore.fetchStats();
        Object.assign(stats, statsData);
        
        // 獲取最近活動
        const users = await userStore.getUsers({ page: 1, limit: 5, sort: 'createdAt:desc' });
        if (users && users.users) {
          recentUsers.value = users.users;
        }
        
        // 獲取系統日誌
        const logs = await activityStore.fetchSystemLogs();
        if (logs) {
          systemLogs.value = logs;
        }
        */
        
        // 更新用户角色信息
        if (authStore.user) {
          role.value = authStore.user.role === 'SUPER_ADMIN' ? '超級管理員' : '管理員';
        }
        
        // 初始化图表
        setTimeout(() => {
          initUsageChart();
        }, 500);
      } catch (error) {
        console.error('Error fetching dashboard data:', error);
      } finally {
        loading.value = false;
      }
    };
    
    // 監聽圖表過濾條件變更
    watch(() => chartPeriod.value, (newValue) => {
      loading.value = true;
      
      // 實際應用中，這裡應該根據週期調用不同的API
      setTimeout(() => {
        initUsageChart();
      }, 500);
    });
    
    onMounted(() => {
      fetchData();
    });
    
    return {
      role,
      stats,
      chartPeriod,
      loading,
      alerts,
      alertGrowth,
      recentUsers,
      systemLogs,
      usageChartRef,
      changePeriod,
      viewAllUsers,
      viewAllLogs,
      editUser,
      deleteUser,
      exportData,
      formatDate,
      formatTime,
      formatTimeAgo,
      getStatusText,
      getRoleName,
      getActivityIcon,
      getActivityClass
    };
  }
}
</script>

<style scoped>
.admin-dashboard {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-title {
  font-size: 24px;
  color: var(--gray-800, #2c3e50);
  margin: 0;
  font-weight: 700;
}

.btn-export {
  background-color: var(--tiffany-blue, #81D8D0);
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 5px;
  cursor: pointer;
  display: flex;
  align-items: center;
  transition: background-color 0.3s;
}

.btn-export:hover {
  background-color: var(--tiffany-blue-dark, #5ec4bd);
}

.btn-export i {
  margin-right: 8px;
}

/* 統計卡片樣式 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background-color: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  color: white;
  font-size: 24px;
}

.user-icon {
  background-color: var(--tiffany-blue, #81D8D0);
}

.active-icon {
  background-color: #4dabf7;
}

.pending-icon {
  background-color: #f59e0b;
}

.alert-icon {
  background-color: #fa5252;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 5px;
  color: var(--gray-800, #2c3e50);
}

.stat-label {
  color: var(--gray-500, #6b7280);
  margin-bottom: 8px;
}

.stat-change {
  font-size: 14px;
  display: flex;
  align-items: center;
}

.stat-change i {
  margin-right: 5px;
}

.positive, .increase {
  color: #10b981;
}

.negative, .decrease {
  color: #ef4444;
}

/* 圖表容器 */
.chart-container {
  background-color: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 30px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  font-size: 18px;
  color: var(--gray-800, #2c3e50);
  font-weight: 600;
  margin: 0;
}

.chart-placeholder {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  background-color: var(--gray-50, #f9fafb);
}

.loading-spinner {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: var(--tiffany-blue, #81D8D0);
}

.loading-spinner i {
  font-size: 30px;
  margin-bottom: 10px;
}

#usageChart {
  height: 300px !important;
}

.period-buttons {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  gap: 10px;
}

.period-buttons button {
  background-color: var(--white, white);
  border: 1px solid var(--gray-200, #e5e7eb);
  border-radius: 8px;
  padding: 8px 15px;
  min-width: 50px;
  cursor: pointer;
  transition: all 0.3s;
  color: var(--gray-600, #4b5563);
  font-weight: 500;
}

.period-buttons button:hover {
  border-color: var(--tiffany-blue, #81D8D0);
  color: var(--tiffany-blue, #81D8D0);
}

.period-buttons button.active {
  background-color: var(--tiffany-blue, #81D8D0);
  color: white;
  border-color: var(--tiffany-blue, #81D8D0);
}

/* 表格樣式 */
.btn-view-all {
  background-color: transparent;
  color: var(--tiffany-blue, #81D8D0);
  border: 1px solid var(--tiffany-blue, #81D8D0);
  padding: 6px 12px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s;
}

.btn-view-all:hover {
  background-color: var(--tiffany-blue-light, #e0f7f5);
}

.table-loading {
  display: flex;
  justify-content: center;
  padding: 30px;
  color: var(--gray-500, #6b7280);
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  background-color: white;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.data-table th, .data-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid var(--gray-100, #f3f4f6);
}

.data-table th {
  background-color: var(--gray-50, #f9fafb);
  color: var(--gray-600, #4b5563);
  font-weight: 600;
}

.status-badge {
  padding: 5px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.active {
  background-color: #dcfce7;
  color: #16a34a;
}

.status-badge.pending {
  background-color: #fff7ed;
  color: #ea580c;
}

.status-badge.inactive {
  background-color: #fee2e2;
  color: #dc2626;
}

.btn-action {
  background-color: transparent;
  border: none;
  cursor: pointer;
  padding: 5px;
  margin-right: 5px;
  transition: color 0.2s;
}

.btn-action.edit {
  color: #3b82f6;
}

.btn-action.edit:hover {
  color: #2563eb;
}

.btn-action.delete {
  color: #ef4444;
}

.btn-action.delete:hover {
  color: #dc2626;
}

.recent-section {
  margin-bottom: 30px;
}

/* 系統日誌/活動新樣式 */
.recent-activities {
  margin-bottom: 30px;
}

.activities-list {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.activity-item {
  padding: 15px 20px;
  display: flex;
  align-items: flex-start;
  border-bottom: 1px solid var(--gray-100, #f3f4f6);
  transition: background-color 0.2s;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-item:hover {
  background-color: var(--tiffany-bg, #f0faf9);
}

.activity-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  color: white;
  font-size: 16px;
}

.activity-icon.info {
  background-color: #dbeafe;
  color: #3b82f6;
}

.activity-icon.warning {
  background-color: #fef3c7;
  color: #d97706;
}

.activity-icon.error {
  background-color: #fee2e2;
  color: #dc2626;
}

.activity-content {
  flex: 1;
}

.activity-time {
  color: var(--gray-400, #9ca3af);
  font-size: 12px;
  margin-bottom: 4px;
}

.activity-level {
  font-weight: 600;
  margin-bottom: 4px;
  font-size: 14px;
}

.info .activity-level {
  color: #3b82f6;
}

.warning .activity-level {
  color: #d97706;
}

.error .activity-level {
  color: #dc2626;
}

.activity-message {
  color: var(--gray-700, #374151);
  font-size: 14px;
}

/* 響應式設計 */
@media (max-width: 1024px) {
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .activity-item {
    flex-direction: column;
  }
  
  .activity-icon {
    margin-right: 0;
    margin-bottom: 10px;
  }
}

@media (max-width: 480px) {
  .dashboard-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .stat-card {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .stat-icon {
    margin-right: 0;
    margin-bottom: 15px;
  }
  
  .data-table {
    font-size: 12px;
  }
  
  .data-table th, .data-table td {
    padding: 8px;
  }
}
</style>