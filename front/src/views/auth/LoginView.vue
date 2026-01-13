<template>
  <div class="login-container">
    <div class="login-card">
      <h1>{{ isAdminLogin ? '管理員登入' : '會員登入' }}</h1>

      <!-- 登入表單 -->
      <form @submit.prevent="handleLogin" class="login-form">
        <!-- 錯誤訊息 -->
        <div v-if="error" class="error-message">
          {{ error }}
        </div>

        <!-- 使用者名稱 -->
        <div class="form-group">
          <label for="username">帳號</label>
          <input type="text" id="username" v-model="username" placeholder="請輸入帳號" required />
        </div>

        <!-- 密碼 -->
        <div class="form-group">
          <label for="password">密碼</label>
          <input type="password" id="password" v-model="password" placeholder="請輸入密碼" required />
        </div>

        <!-- 記住我 -->
        <div class="form-group checkbox">
          <input type="checkbox" id="remember" v-model="remember" />
          <label for="remember">記住我</label>
        </div>

        <!-- 登入按鈕 -->
        <button type="submit" class="btn-login" :disabled="loading">
          {{ loading ? '登入中...' : '登入' }}
        </button>

        <!-- 切換登入類型 -->
        <div class="login-type-toggle" @click="toggleLoginType">
          切換到{{ isAdminLogin ? '會員' : '管理員' }}登入
        </div>

        <!-- 其他選項 -->
        <div class="login-options">
          <router-link to="/forgot-password">忘記密碼？</router-link>
          <router-link v-if="!isAdminLogin" to="/register">註冊新帳號</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import authService from '@/services/auth/authService';

export default {
  name: 'LoginView',
  props: {
    isAdmin: {
      type: Boolean,
      default: false
    }
  },
  setup(props) {
    const username = ref('');
    const password = ref('');
    const remember = ref(false);
    const isAdminLogin = ref(props.isAdmin);
    const error = ref('');
    const loading = ref(false);

    const router = useRouter();
    const route = useRoute();

    // 在組件載入時檢查當前路由
    onMounted(() => {
      // 檢查當前路徑是否為管理員登入頁面
      if (route.path === '/admin/login' || route.query.admin === 'true') {
        isAdminLogin.value = true;
      }
      
      // 開發環境下自動填充超級管理員帳密
      if (process.env.NODE_ENV === 'development' && isAdminLogin.value) {
        username.value = 'superadmin';
        password.value = 'Admin@123';
      }
    });

    // 切換登入類型
    const toggleLoginType = () => {
      isAdminLogin.value = !isAdminLogin.value;
      error.value = '';

      if (isAdminLogin.value) {
        router.push('/admin/login');
      } else {
        router.push('/login');
      }
    };

    // 處理登入
    const handleLogin = async () => {
      loading.value = true;
      error.value = '';

      try {
        let result;

        // 根據登入類型呼叫不同的登入方法
        if (isAdminLogin.value) {
          // 特殊處理 superadmin 登入
          if (username.value === 'superadmin') {
            console.log('偵測到 superadmin 登入，將特殊處理');
          }

          // 嘗試管理員登入
          try {
            // 管理員登入
            result = await authService.adminLogin(username.value, password.value);

            // 登入後導向
            const redirectPath = route.query.redirect || '/admin';
            router.push(redirectPath);
          } catch (adminErr) {
            console.error('常規管理員登入失敗:', adminErr);
            
            // 如果是開發環境，嘗試使用模擬登入
            if (process.env.NODE_ENV === 'development' && username.value === 'superadmin') {
              console.log('嘗試使用模擬管理員登入');
              
              // 硬編碼的超級管理員帳號，僅用於開發環境
              if (password.value === 'Admin@123') {
                // 創建模擬用戶數據
                const mockUser = {
                  id: 1,
                  username: 'superadmin',
                  email: 'super@example.com',
                  roles: ['SUPER_ADMIN']
                };
                
                // 模擬登入成功，存儲用戶數據
                localStorage.setItem('adminAccessToken', 'mock-token-for-development');
                localStorage.setItem('adminRefreshToken', 'mock-refresh-token');
                localStorage.setItem('userRole', 'SUPER_ADMIN');
                localStorage.setItem('userRolesList', JSON.stringify(['SUPER_ADMIN']));
                localStorage.setItem('user', JSON.stringify(mockUser));
                
                console.log('模擬管理員登入成功');
                
                // 登入後導向
                const redirectPath = route.query.redirect || '/admin';
                router.push(redirectPath);
                return;
              }
            }
            
            // 如果真實登入和模擬登入都失敗，顯示錯誤
            throw adminErr;
          }
        } else {
          // 一般用戶登入
          result = await authService.login(username.value, password.value);

          // 登入後導向
          const redirectPath = route.query.redirect || '/';
          router.push(redirectPath);
        }
      } catch (err) {
        console.error('登入錯誤:', err);
        error.value = '登入失敗，請檢查您的帳號和密碼';
      } finally {
        loading.value = false;
      }
    };

    return {
      username,
      password,
      remember,
      isAdminLogin,
      error,
      loading,
      toggleLoginType,
      handleLogin
    };
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
  background-color: #f0faf9;
}

.login-card {
  background: white;
  border-radius: 8px;
  padding: 35px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

h1 {
  text-align: center;
  margin-bottom: 25px;
  color: #81D8D0;
  font-size: 26px;
  font-weight: 600;
}

.login-form {
  display: flex;
  flex-direction: column;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
  font-size: 15px;
}

input[type="text"],
input[type="password"] {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 15px;
  transition: border-color 0.2s;
}

input[type="text"]:focus,
input[type="password"]:focus {
  outline: none;
  border-color: #81D8D0;
  box-shadow: 0 0 0 2px rgba(129, 216, 208, 0.1);
}

.checkbox {
  display: flex;
  align-items: center;
}

.checkbox input {
  margin-right: 10px;
  accent-color: #81D8D0;
}

.checkbox label {
  margin-bottom: 0;
  font-weight: normal;
  color: #555;
}

.btn-login {
  background-color: #81D8D0;
  color: white;
  border: none;
  padding: 13px;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s, transform 0.2s;
  margin-top: 10px;
}

.btn-login:hover {
  background-color: #6BC0B9;
  transform: translateY(-1px);
}

.btn-login:disabled {
  background-color: #B0E6E2;
  cursor: not-allowed;
  transform: none;
}

.login-type-toggle {
  text-align: center;
  margin-top: 25px;
  color: #81D8D0;
  cursor: pointer;
  font-size: 14px;
  transition: color 0.2s;
}

.login-type-toggle:hover {
  color: #6BC0B9;
  text-decoration: underline;
}

.login-options {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.login-options a {
  color: #81D8D0;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.2s;
}

.login-options a:hover {
  color: #6BC0B9;
  text-decoration: underline;
}

.error-message {
  background-color: #fee2e2;
  color: #dc2626;
  padding: 12px 15px;
  border-radius: 4px;
  margin-bottom: 20px;
  font-size: 14px;
  border-left: 3px solid #dc2626;
}

@media (max-width: 480px) {
  .login-card {
    padding: 25px 20px;
  }
}
</style>