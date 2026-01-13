<template>
  <div class="forgot-password-container">
    <div class="forgot-password-card">
      <h1>忘記密碼</h1>
      
      <!-- 表單 -->
      <form @submit.prevent="handleSubmit" class="forgot-password-form">
        <!-- 成功訊息 -->
        <div v-if="successMessage" class="success-message">
          {{ successMessage }}
        </div>
        
        <!-- 錯誤訊息 -->
        <div v-if="error" class="error-message">
          {{ error }}
        </div>
        
        <!-- 電子郵件 -->
        <div class="form-group">
          <label for="email">電子郵件</label>
          <input 
            type="email" 
            id="email" 
            v-model="email" 
            placeholder="請輸入您的註冊電子郵件" 
            required
          />
        </div>
        
        <!-- 提交按鈕 -->
        <button type="submit" class="btn-submit" :disabled="loading">
          {{ loading ? '處理中...' : '重設密碼' }}
        </button>
        
        <!-- 返回登入頁面 -->
        <div class="login-link">
          想起密碼了？<router-link to="/login">返回登入</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

export default {
  name: 'ForgotPasswordView',
  setup() {
    const email = ref('');
    const error = ref('');
    const loading = ref(false);
    const successMessage = ref('');
    
    const authStore = useAuthStore();
    const router = useRouter();
    
    // 處理表單提交
    const handleSubmit = async () => {
      loading.value = true;
      error.value = '';
      successMessage.value = '';
      
      try {
        await authStore.forgotPassword(email.value);
        successMessage.value = '密碼重設連結已發送到您的電子郵件，請檢查收件匣';
        // 清空表單
        email.value = '';
      } catch (err) {
        error.value = err.response?.data?.message || '無法發送重設密碼郵件，請稍後再試';
      } finally {
        loading.value = false;
      }
    };
    
    return {
      email,
      error,
      loading,
      successMessage,
      handleSubmit
    };
  }
};
</script>

<style scoped>
.forgot-password-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  padding: 20px;
  background-color: #f8f9fa;
}

.forgot-password-card {
  background: white;
  border-radius: 10px;
  padding: 30px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

h1 {
  text-align: center;
  margin-bottom: 25px;
  color: #2c3e50;
}

.forgot-password-form {
  display: flex;
  flex-direction: column;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 5px;
  color: #4a5568;
  font-weight: 500;
}

input[type="email"] {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

.btn-submit {
  background-color: #10b981;
  color: white;
  border: none;
  padding: 12px;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.btn-submit:hover {
  background-color: #059669;
}

.btn-submit:disabled {
  background-color: #94d3a2;
  cursor: not-allowed;
}

.login-link {
  text-align: center;
  margin-top: 20px;
}

.login-link a {
  color: #0fb886;
  text-decoration: none;
  font-weight: 500;
}

.error-message {
  background-color: #fee2e2;
  color: #b91c1c;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 15px;
}

.success-message {
  background-color: #dcfce7;
  color: #16a34a;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 15px;
}
</style> 