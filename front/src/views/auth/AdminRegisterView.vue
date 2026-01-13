<template>
    <div class="admin-register-container">
      <div class="register-card">
        <div class="register-header">
          <h1>管理員註冊</h1>
          <p>建立新的管理員帳號</p>
        </div>
        
        <div v-if="registrationError" class="alert alert-danger">
          {{ registrationError }}
        </div>
        
        <form @submit.prevent="register" class="register-form">
          <!-- 用戶名稱 -->
          <div class="form-group">
            <label for="username">管理員帳號</label>
            <input 
              type="text" 
              id="username" 
              v-model="form.username" 
              required 
            />
          </div>
          
          <!-- 電子郵件 -->
          <div class="form-group">
            <label for="email">電子郵件</label>
            <input 
              type="email" 
              id="email" 
              v-model="form.email" 
              required 
            />
          </div>
          
          <!-- 密碼 -->
          <div class="form-group">
            <label for="password">密碼</label>
            <input 
              type="password" 
              id="password" 
              v-model="form.password" 
              required 
            />
          </div>
          
          <!-- 確認密碼 -->
          <div class="form-group">
            <label for="passwordConfirm">確認密碼</label>
            <input 
              type="password" 
              id="passwordConfirm" 
              v-model="form.passwordConfirm" 
              required 
            />
          </div>
          
          <!-- 管理員金鑰 - 防止任何人註冊成管理員 -->
          <div class="form-group">
            <label for="adminKey">管理員金鑰</label>
            <input 
              type="password" 
              id="adminKey" 
              v-model="form.adminKey" 
              required 
            />
            <small class="form-text text-muted">請輸入管理員金鑰，此金鑰由系統管理者提供</small>
          </div>
          
          <button 
            type="submit" 
            class="btn-submit" 
            :disabled="isLoading"
          >
            <span v-if="isLoading">註冊中...</span>
            <span v-else>註冊管理員帳號</span>
          </button>
        </form>
        
        <div class="login-link">
          已有管理員帳號？ <router-link to="/admin/login">登入管理後台</router-link>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { ref, reactive } from 'vue';
  import { useRouter } from 'vue-router';
  import authService from '../services/auth/authService';
  
  export default {
    name: 'AdminRegisterView',
    setup() {
      const router = useRouter();
      const isLoading = ref(false);
      const registrationError = ref('');
      
      const form = reactive({
        username: '',
        email: '',
        password: '',
        passwordConfirm: '',
        adminKey: '' // 管理員註冊金鑰
      });
      
      const register = async () => {
        // 基本驗證
        if (form.password !== form.passwordConfirm) {
          registrationError.value = '兩次輸入的密碼不一致';
          return;
        }
        
        isLoading.value = true;
        registrationError.value = '';
        
        try {
          // 呼叫管理員註冊 API
          await authService.registerAdmin({
            username: form.username,
            email: form.email,
            password: form.password,
            adminKey: form.adminKey
          });
          
          // 註冊成功後自動登入
          await authService.adminLogin(form.username, form.password);
          
          // 導向管理後台
          router.push('/admin');
        } catch (error) {
          console.error('管理員註冊失敗:', error);
          registrationError.value = error.message || '註冊失敗，請檢查您的資料或聯繫系統管理員';
        } finally {
          isLoading.value = false;
        }
      };
      
      return {
        form,
        isLoading,
        registrationError,
        register
      };
    }
  };
  </script>
  
  <style scoped>
  .admin-register-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background-color: #f5f7fa;
    padding: 20px;
  }
  
  .register-card {
    background: white;
    border-radius: 8px;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
    padding: 30px;
    width: 100%;
    max-width: 450px;
  }
  
  .register-header {
    text-align: center;
    margin-bottom: 25px;
  }
  
  .register-header h1 {
    color: #2c3e50;
    font-size: 24px;
    margin-bottom: 8px;
  }
  
  .register-header p {
    color: #7f8c8d;
    font-size: 15px;
  }
  
  .form-group {
    margin-bottom: 20px;
  }
  
  .form-group label {
    display: block;
    margin-bottom: 8px;
    font-weight: 500;
    color: #34495e;
  }
  
  .form-group input {
    width: 100%;
    padding: 12px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 16px;
  }
  
  .form-group .form-text {
    font-size: 12px;
    margin-top: 5px;
    color: #7f8c8d;
  }
  
  .alert {
    padding: 12px;
    border-radius: 4px;
    margin-bottom: 20px;
  }
  
  .alert-danger {
    background-color: #fef2f2;
    border: 1px solid #fee2e2;
    color: #ef4444;
  }
  
  .btn-submit {
    width: 100%;
    padding: 12px;
    background: #1e2838;
    color: white;
    border: none;
    border-radius: 4px;
    font-size: 16px;
    font-weight: 500;
    cursor: pointer;
  }
  
  .btn-submit:hover {
    background: #2c3e58;
  }
  
  .btn-submit:disabled {
    background: #95a5a6;
    cursor: not-allowed;
  }
  
  .login-link {
    text-align: center;
    margin-top: 20px;
    font-size: 14px;
  }
  
  .login-link a {
    color: #10b981;
    text-decoration: none;
  }
  
  .login-link a:hover {
    text-decoration: underline;
  }
  </style>