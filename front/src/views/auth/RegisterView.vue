<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-header">
        <h1>註冊帳號</h1>
        <p>加入露營趣，開始您的露營冒險</p>
      </div>

      <form @submit.prevent="register" class="register-form">
        <!-- API 錯誤提示 -->
        <div v-if="apiError" class="alert alert-danger">
          {{ apiError }}
        </div>

        <!-- 用戶名稱 -->
        <div class="form-group">
          <label for="username">用戶名稱</label>
          <input 
            id="username" 
            v-model="form.username" 
            type="text" 
            class="form-control" 
            :class="{ 'is-invalid': errors.username }"
            placeholder="請輸入用戶名稱"
          >
          <div v-if="errors.username" class="invalid-feedback">
            {{ errors.username }}
          </div>
        </div>

        <!-- 電子郵件 -->
        <div class="form-group">
          <label for="email">電子郵件</label>
          <input 
            id="email" 
            v-model="form.email" 
            type="email" 
            class="form-control" 
            :class="{ 'is-invalid': errors.email }"
            placeholder="請輸入電子郵件"
          >
          <div v-if="errors.email" class="invalid-feedback">
            {{ errors.email }}
          </div>
        </div>

        <!-- 手機號碼 (選填) -->
        <div class="form-group">
          <label for="phone">手機號碼 (選填)</label>
          <input 
            id="phone" 
            v-model="form.phone" 
            type="tel" 
            class="form-control" 
            :class="{ 'is-invalid': errors.phone }"
            placeholder="例如: 0912345678"
          >
          <div v-if="errors.phone" class="invalid-feedback">
            {{ errors.phone }}
          </div>
        </div>

        <!-- 密碼 -->
        <div class="form-group">
          <label for="password">密碼</label>
          <div class="password-input-group">
            <input 
              id="password" 
              v-model="form.password" 
              :type="showPassword ? 'text' : 'password'" 
              class="form-control" 
              :class="{ 'is-invalid': errors.password }"
              placeholder="請輸入密碼" 
              @input="checkPasswordStrength"
            >
            <button 
              type="button" 
              class="password-toggle-btn" 
              @click="showPassword = !showPassword"
            >
              <i :class="showPassword ? 'fa fa-eye-slash' : 'fa fa-eye'"></i>
            </button>
          </div>
          <div v-if="errors.password" class="invalid-feedback">
            {{ errors.password }}
          </div>
          
          <!-- 密碼強度指示器 -->
          <div v-if="form.password" class="password-strength">
            <div class="strength-bar">
              <div 
                class="strength-bar-value" 
                :class="strengthClass" 
                :style="{ width: (passwordStrength * 25) + '%' }"
              ></div>
            </div>
            <span class="strength-text" :class="strengthClass">
              {{ strengthText }}
            </span>
          </div>
        </div>

        <!-- 確認密碼 -->
        <div class="form-group">
          <label for="passwordConfirm">確認密碼</label>
          <div class="password-input-group">
            <input 
              id="passwordConfirm" 
              v-model="form.passwordConfirm" 
              :type="showPasswordConfirm ? 'text' : 'password'" 
              class="form-control" 
              :class="{ 'is-invalid': errors.passwordConfirm }"
              placeholder="請再次輸入密碼"
            >
            <button 
              type="button" 
              class="password-toggle-btn" 
              @click="showPasswordConfirm = !showPasswordConfirm"
            >
              <i :class="showPasswordConfirm ? 'fa fa-eye-slash' : 'fa fa-eye'"></i>
            </button>
          </div>
          <div v-if="errors.passwordConfirm" class="invalid-feedback">
            {{ errors.passwordConfirm }}
          </div>
        </div>

        <!-- 註冊類型 -->
        <div class="form-group">
          <label>註冊為</label>
          <div class="role-options">
            <label class="role-option">
              <input 
                type="radio" 
                v-model="form.role" 
                value="camper"
              >
              <div class="role-card">
                <i class="fas fa-campground"></i>
                <span>露營者</span>
                <p>尋找營地、預約營位並探索自然</p>
              </div>
            </label>
            <label class="role-option">
              <input 
                type="radio" 
                v-model="form.role" 
                value="owner"
              >
              <div class="role-card">
                <i class="fas fa-store"></i>
                <span>營地主</span>
                <p>刊登營地、管理預約及經營業務</p>
              </div>
            </label>
          </div>
        </div>

        <!-- 服務條款 -->
        <div class="form-group">
          <div class="form-check">
            <input 
              id="agreeTerms" 
              v-model="form.agreeTerms" 
              type="checkbox" 
              class="form-check-input" 
              :class="{ 'is-invalid': errors.agreeTerms }"
            >
            <label class="form-check-label" for="agreeTerms">
              我已閱讀並同意 <a href="#" target="_blank">服務條款</a> 和 <a href="#" target="_blank">隱私政策</a>
            </label>
            <div v-if="errors.agreeTerms" class="invalid-feedback">
              {{ errors.agreeTerms }}
            </div>
          </div>
        </div>

        <!-- 送出按鈕 -->
        <div class="form-group">
          <button 
            type="submit" 
            class="btn btn-primary btn-block" 
            :disabled="isLoading"
          >
            <span v-if="isLoading">
              <i class="fas fa-spinner fa-spin"></i> 處理中...
            </span>
            <span v-else>註冊帳號</span>
          </button>
        </div>

        <div class="login-link">
          已有帳號？<router-link to="/login">登入</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue';
import { useRouter } from 'vue-router';
import authService from '../../services/auth/authService'; // 修正路徑
import { useToast } from '../../composables/useToast'; // 修正路徑

const router = useRouter();
const { showToast } = useToast();
const isLoading = ref(false);
const apiError = ref('');
const showPassword = ref(false);
const showPasswordConfirm = ref(false);
const passwordStrength = ref(0);

const form = reactive({
  username: '',
  email: '',
  password: '',
  passwordConfirm: '',
  phone: '',
  role: 'camper',
  agreeTerms: false
});

const errors = reactive({
  username: '',
  email: '',
  password: '',
  passwordConfirm: '',
  phone: '',
  agreeTerms: ''
});

// 密碼強度文字
const strengthText = computed(() => {
  if (passwordStrength.value === 0) return '';
  if (passwordStrength.value === 1) return '弱';
  if (passwordStrength.value === 2) return '中';
  if (passwordStrength.value === 3) return '強';
  if (passwordStrength.value === 4) return '非常強';
});

// 密碼強度 CSS 類
const strengthClass = computed(() => {
  if (passwordStrength.value <= 1) return 'weak';
  if (passwordStrength.value === 2) return 'medium';
  if (passwordStrength.value === 3) return 'strong';
  if (passwordStrength.value === 4) return 'very-strong';
});

// 檢查密碼強度
const checkPasswordStrength = () => {
  const password = form.password;
  let score = 0;
  
  if (!password) {
    passwordStrength.value = 0;
    return;
  }
  
  // 長度檢查
  if (password.length >= 8) score += 1;
  if (password.length >= 12) score += 1;
  
  // 複雜度檢查
  if (/[A-Z]/.test(password)) score += 1;
  if (/[a-z]/.test(password)) score += 1;
  if (/[0-9]/.test(password)) score += 1;
  if (/[^A-Za-z0-9]/.test(password)) score += 1;
  
  // 計算總分 (最高 4 分)
  passwordStrength.value = Math.min(4, Math.floor(score / 2));
};

// 驗證表單
const validateForm = () => {
  let isValid = true;
  
  // 重置錯誤訊息
  Object.keys(errors).forEach(key => {
    errors[key] = '';
  });
  
  // 驗證用戶名稱
  if (!form.username.trim()) {
    errors.username = '請輸入用戶名稱';
    isValid = false;
  } else if (form.username.length < 3) {
    errors.username = '用戶名稱至少需要3個字符';
    isValid = false;
  }
  
  // 驗證電子郵件
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!form.email.trim()) {
    errors.email = '請輸入電子郵件';
    isValid = false;
  } else if (!emailRegex.test(form.email)) {
    errors.email = '請輸入有效的電子郵件地址';
    isValid = false;
  }
  
  // 驗證手機號碼 (如果填寫了)
  if (form.phone) {
    const phoneRegex = /^09\d{8}$/;
    if (!phoneRegex.test(form.phone)) {
      errors.phone = '請輸入有效的手機號碼格式 (例如: 0912345678)';
      isValid = false;
    }
  }
  
  // 驗證密碼
  if (!form.password) {
    errors.password = '請輸入密碼';
    isValid = false;
  } else if (form.password.length < 8) {
    errors.password = '密碼至少需要8個字符';
    isValid = false;
  } else if (passwordStrength.value < 2) {
    errors.password = '密碼強度太弱，請使用更複雜的密碼';
    isValid = false;
  }
  
  // 驗證確認密碼
  if (!form.passwordConfirm) {
    errors.passwordConfirm = '請確認您的密碼';
    isValid = false;
  } else if (form.password !== form.passwordConfirm) {
    errors.passwordConfirm = '兩次輸入的密碼不一致';
    isValid = false;
  }
  
  // 驗證服務條款
  if (!form.agreeTerms) {
    errors.agreeTerms = '您必須同意服務條款和隱私政策才能繼續';
    isValid = false;
  }
  
  return isValid;
};

// 註冊方法
const register = async () => {
  if (!validateForm()) {
    return;
  }
  
  isLoading.value = true;
  apiError.value = '';
  
  try {
    // 調整資料格式以適應後端期望的格式
    const userData = {
      username: form.username,
      email: form.email,
      password: form.password,
      fullName: form.username, // 使用用戶名稱作為全名
      phone: form.phone || null,
      role: form.role === 'owner' ? 'owner' : 'user' // 保持與後端一致的角色格式
    };
    
    console.log('準備註冊:', userData);
    
    // 嘗試註冊
    await authService.register(userData);
    
    // 註冊成功通知
    showToast('註冊成功！請登入您的帳號', 'success');
    
    // 註冊成功，導向登入頁
    router.push({ 
      path: '/login',
      query: { registered: 'success' } 
    });
  } catch (error) {
    console.error('註冊錯誤:', error);
    
    // 處理API錯誤
    apiError.value = error.message || '註冊失敗，請稍後再試';
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 40px 20px;
  background-color: var(--background);
}

.register-card {
  width: 100%;
  max-width: 600px;
  background-color: var(--card-bg);
  border-radius: 8px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.register-header {
  padding: 30px;
  text-align: center;
  border-bottom: 1px solid var(--border-color);
}

.register-header h1 {
  font-size: 24px;
  color: var(--primary);
  margin-bottom: 10px;
}

.register-header p {
  color: var(--text-secondary);
}

.register-form {
  padding: 30px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: var(--text-primary);
}

.form-control {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 16px;
  transition: border-color 0.2s;
}

.form-control:focus {
  border-color: var(--accent);
  outline: none;
  box-shadow: 0 0 0 2px rgba(16, 185, 129, 0.1);
}

.form-control.is-invalid {
  border-color: var(--danger);
  background-position: right calc(0.375em + 0.1875rem) center;
  background-repeat: no-repeat;
}

.invalid-feedback {
  color: var(--danger);
  font-size: 14px;
  margin-top: 5px;
}

.password-input-group {
  position: relative;
}

.password-toggle-btn {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: var(--text-secondary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.password-toggle-btn:focus {
  outline: none;
}

.password-strength {
  margin-top: 10px;
}

.strength-bar {
  height: 4px;
  background-color: #e0e0e0;
  border-radius: 2px;
  margin-bottom: 5px;
  overflow: hidden;
}

.strength-bar-value {
  height: 100%;
  transition: width 0.3s ease;
}

.strength-bar-value.weak {
  background-color: #ef4444;
}

.strength-bar-value.medium {
  background-color: #f59e0b;
}

.strength-bar-value.strong {
  background-color: #10b981;
}

.strength-bar-value.very-strong {
  background-color: #3b82f6;
}

.strength-text {
  font-size: 12px;
  font-weight: 500;
  text-transform: uppercase;
}

.strength-text.weak {
  color: #ef4444;
}

.strength-text.medium {
  color: #f59e0b;
}

.strength-text.strong {
  color: #10b981;
}

.strength-text.very-strong {
  color: #3b82f6;
}

.role-options {
  display: flex;
  gap: 15px;
  margin-top: 10px;
}

.role-option {
  flex: 1;
  cursor: pointer;
  margin: 0;
}

.role-option input {
  position: absolute;
  opacity: 0;
}

.role-card {
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  transition: all 0.3s ease;
}

.role-option input:checked + .role-card {
  border-color: var(--accent);
  background-color: rgba(16, 185, 129, 0.05);
}

.role-card i {
  font-size: 24px;
  color: var(--accent);
  margin-bottom: 10px;
  display: block;
}

.role-card span {
  font-weight: 600;
  display: block;
  margin-bottom: 5px;
}

.role-card p {
  color: var(--text-secondary);
  font-size: 14px;
  margin: 0;
}

.form-check {
  display: flex;
  align-items: flex-start;
  padding-left: 0;
}

.form-check-input {
  margin-right: 10px;
  margin-top: 5px;
}

.form-check-label {
  font-size: 14px;
  color: var(--text-secondary);
}

.form-check-label a {
  color: var(--accent);
  text-decoration: none;
}

.form-check-label a:hover {
  text-decoration: underline;
}

.btn {
  display: inline-block;
  padding: 12px 20px;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
}

.btn-primary {
  background-color: var(--accent);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: var(--accent-hover);
}

.btn-primary:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn-block {
  width: 100%;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  color: var(--text-secondary);
}

.login-link a {
  color: var(--accent);
  text-decoration: none;
  font-weight: 500;
}

.login-link a:hover {
  text-decoration: underline;
}

.alert {
  padding: 12px 16px;
  border-radius: 4px;
  margin-bottom: 20px;
  font-size: 14px;
}

.alert-danger {
  background-color: rgba(239, 68, 68, 0.1);
  color: #dc2626;
  border-left: 3px solid #dc2626;
}

@media (max-width: 576px) {
  .role-options {
    flex-direction: column;
  }
  
  .register-card {
    box-shadow: none;
  }
}
</style>