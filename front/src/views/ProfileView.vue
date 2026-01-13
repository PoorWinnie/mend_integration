<template>
  <div class="profile-container">
    <!-- 加載提示 -->
    <div v-if="isLoading" class="loading-overlay">
      <div class="loading-spinner"></div>
      <p>載入中...</p>
    </div>

    <!-- 成功通知提示 -->
    <div v-if="showSuccess" class="success-notification">
      <div class="success-icon">
        <i class="fa fa-check-circle"></i>
      </div>
      <div class="success-message">{{ successMessage }}</div>
      <button @click="hideSuccessNotification" class="close-notification">
        <i class="fa fa-times"></i>
      </button>
    </div>

    <!-- 頭像上傳進度覆蓋層 -->
    <div v-if="uploadingAvatar" class="avatar-upload-overlay">
      <div class="upload-progress-container">
        <i class="fa fa-spinner fa-spin"></i>
        <p>上傳頭像中...</p>
        <div class="upload-progress-bar">
          <div class="upload-progress-fill" :style="{width: `${avatarUploadProgress}%`}"></div>
        </div>
        <span>{{ avatarUploadProgress }}%</span>
      </div>
    </div>

    <div class="profile-header">
      <h1>個人資料</h1>
      <p>管理您的個人信息和帳戶設置</p>
    </div>
    
    <div class="profile-content">
      <!-- 左側選單 -->
      <div class="profile-sidebar">
        <div class="profile-avatar">
          <div class="avatar-image">
            <img v-if="user.avatar || previewAvatar" :src="previewAvatar || user.avatar" alt="User Avatar" />
            <i v-else class="fa fa-user-circle"></i>
            <!-- 頭像懸停覆蓋層 -->
            <div class="avatar-hover-overlay">
              <button @click="triggerFileUpload" class="btn-upload-avatar">
                <i class="fa fa-camera"></i>
              </button>
            </div>
          </div>
          <!-- 隱藏的文件上傳輸入框 -->
          <input 
            ref="avatarFileInput" 
            type="file" 
            @change="handleAvatarChange" 
            accept="image/*" 
            style="display:none" 
          />
          <div class="avatar-info">
            <div class="user-name">{{ user.username }}</div>
            <div class="user-email">{{ user.email }}</div>
          </div>
          <label for="avatar-upload" class="btn-change-avatar">
            <i class="fa fa-camera"></i> 更換頭像
            <input id="avatar-upload" type="file" @change="handleAvatarChange" accept="image/*" style="display:none" />
          </label>
        </div>
        
        <div class="profile-nav">
          <button 
            @click="activeTab = 'info'" 
            :class="{ active: activeTab === 'info' }"
          >
            <i class="fa fa-user"></i> 基本資料
          </button>
          <button 
            @click="activeTab = 'account'" 
            :class="{ active: activeTab === 'account' }"
          >
            <i class="fa fa-lock"></i> 帳戶安全
          </button>
          <button 
            @click="activeTab = 'notification'" 
            :class="{ active: activeTab === 'notification' }"
          >
            <i class="fa fa-bell"></i> 通知設定
          </button>
          <button 
            @click="activeTab = 'preference'" 
            :class="{ active: activeTab === 'preference' }"
          >
            <i class="fa fa-cog"></i> 偏好設定
          </button>
        </div>
      </div>
      
      <!-- 右側內容 -->
      <div class="profile-body">
        <!-- 基本資料 -->
        <div v-if="activeTab === 'info'" class="profile-tab">
          <h2>基本資料</h2>
          <p class="tab-desc">您可以在此更新個人基本資料</p>
          
          <form @submit.prevent="updateProfile" class="profile-form">
            <!-- 姓名 -->
            <div class="form-group">
              <label for="fullName">姓名</label>
              <input 
                type="text" 
                id="fullName" 
                v-model="profileForm.fullName" 
                placeholder="請輸入您的姓名"
              />
            </div>
            
            <!-- 手機號碼 -->
            <div class="form-group">
              <label for="phone">手機號碼</label>
              <input 
                type="tel" 
                id="phone" 
                v-model="profileForm.phone" 
                placeholder="請輸入您的手機號碼"
              />
            </div>
            
            <!-- 生日 -->
            <div class="form-group">
              <label for="birthday">生日</label>
              <input 
                type="date" 
                id="birthday" 
                v-model="profileForm.birthday"
              />
            </div>
            
            <!-- 性別 -->
            <div class="form-group">
              <label>性別</label>
              <div class="radio-group">
                <div class="radio-item">
                  <input 
                    type="radio" 
                    id="male" 
                    value="male" 
                    v-model="profileForm.gender"
                  />
                  <label for="male">男</label>
                </div>
                <div class="radio-item">
                  <input 
                    type="radio" 
                    id="female" 
                    value="female" 
                    v-model="profileForm.gender"
                  />
                  <label for="female">女</label>
                </div>
                <div class="radio-item">
                  <input 
                    type="radio" 
                    id="other" 
                    value="other" 
                    v-model="profileForm.gender"
                  />
                  <label for="other">其他</label>
                </div>
              </div>
            </div>
            
            <!-- 地址 -->
            <div class="form-group">
              <label for="address">地址</label>
              <input 
                type="text" 
                id="address" 
                v-model="profileForm.address" 
                placeholder="請輸入您的地址"
              />
            </div>
            
            <!-- 自我介紹 -->
            <div class="form-group">
              <label for="bio">自我介紹</label>
              <textarea 
                id="bio" 
                v-model="profileForm.bio" 
                rows="4" 
                placeholder="簡單介紹您自己"
              ></textarea>
            </div>
            
            <div class="form-actions">
              <button type="submit" class="btn-save" :disabled="loading">
                {{ loading ? '儲存中...' : '儲存變更' }}
              </button>
              <button type="button" class="btn-cancel" @click="resetForm">
                取消
              </button>
            </div>
          </form>
        </div>
        
        <!-- 帳戶安全 -->
        <div v-if="activeTab === 'account'" class="profile-tab">
          <h2>帳戶安全</h2>
          <p class="tab-desc">管理您的帳戶安全設定</p>
          
          <div class="security-section">
            <h3>修改密碼</h3>
            <form @submit.prevent="changePassword" class="profile-form">
              <div class="form-group">
                <label for="currentPassword">目前密碼</label>
                <input 
                  type="password" 
                  id="currentPassword" 
                  v-model="passwordForm.currentPassword" 
                  placeholder="請輸入目前密碼"
                  required
                />
              </div>
              
              <div class="form-group">
                <label for="newPassword">新密碼</label>
                <input 
                  type="password" 
                  id="newPassword" 
                  v-model="passwordForm.newPassword" 
                  placeholder="請輸入新密碼"
                  required
                />
                <small class="form-hint">密碼須包含至少8個字符，包括大小寫字母和數字</small>
              </div>
              
              <div class="form-group">
                <label for="confirmPassword">確認新密碼</label>
                <input 
                  type="password" 
                  id="confirmPassword" 
                  v-model="passwordForm.confirmPassword" 
                  placeholder="請再次輸入新密碼"
                  required
                />
              </div>
              
              <div class="form-actions">
                <button type="submit" class="btn-save" :disabled="passwordLoading">
                  {{ passwordLoading ? '更新中...' : '更新密碼' }}
                </button>
              </div>
            </form>
          </div>
          
          <div class="security-section">
            <h3>雙重驗證</h3>
            <div class="toggle-container">
              <label class="toggle">
                <input type="checkbox" v-model="twoFactorEnabled" @change="toggleTwoFactor" :disabled="twoFactorLoading" />
                <span class="slider"></span>
              </label>
              <span>{{ twoFactorEnabled ? '已啟用' : '未啟用' }}</span>
            </div>
            <p class="security-desc">
              開啟雙重驗證後，登入時需要輸入手機驗證碼，可提高帳號安全性。
            </p>
          </div>
        </div>
        
        <!-- 通知設定 -->
        <div v-if="activeTab === 'notification'" class="profile-tab">
          <h2>通知設定</h2>
          <p class="tab-desc">管理您接收的通知方式</p>
          
          <div class="notification-section">
            <div class="notification-item">
              <div class="notification-info">
                <h3>電子郵件通知</h3>
                <p>接收露營預約、帳號安全等相關郵件</p>
              </div>
              <label class="toggle">
                <input type="checkbox" v-model="notifications.email" />
                <span class="slider"></span>
              </label>
            </div>
            
            <div class="notification-item">
              <div class="notification-info">
                <h3>簡訊通知</h3>
                <p>接收預約確認、行程提醒等簡訊</p>
              </div>
              <label class="toggle">
                <input type="checkbox" v-model="notifications.sms" />
                <span class="slider"></span>
              </label>
            </div>
            
            <div class="notification-item">
              <div class="notification-info">
                <h3>推廣活動</h3>
                <p>接收特惠活動、新營地資訊等推廣訊息</p>
              </div>
              <label class="toggle">
                <input type="checkbox" v-model="notifications.marketing" />
                <span class="slider"></span>
              </label>
            </div>
            
            <div class="form-actions">
              <button @click="saveNotifications" class="btn-save" :disabled="notificationLoading">
                {{ notificationLoading ? '儲存中...' : '儲存設定' }}
              </button>
            </div>
          </div>
        </div>
        
        <!-- 偏好設定 -->
        <div v-if="activeTab === 'preference'" class="profile-tab">
          <h2>偏好設定</h2>
          <p class="tab-desc">設定您的使用偏好</p>
          
          <div class="preference-section">
            <h3>露營偏好</h3>
            <div class="form-group">
              <label>您喜歡的露營方式</label>
              <div class="checkbox-group">
                <div class="checkbox-item">
                  <input type="checkbox" id="tent" v-model="preferences.camping.tent" />
                  <label for="tent">帳篷</label>
                </div>
                <div class="checkbox-item">
                  <input type="checkbox" id="rv" v-model="preferences.camping.rv" />
                  <label for="rv">露營車</label>
                </div>
                <div class="checkbox-item">
                  <input type="checkbox" id="cabin" v-model="preferences.camping.cabin" />
                  <label for="cabin">小木屋</label>
                </div>
                <div class="checkbox-item">
                  <input type="checkbox" id="glamping" v-model="preferences.camping.glamping" />
                  <label for="glamping">豪華露營</label>
                </div>
              </div>
            </div>
            
            <div class="form-group">
              <label>您喜歡的露營環境</label>
              <div class="checkbox-group">
                <div class="checkbox-item">
                  <input type="checkbox" id="mountain" v-model="preferences.environment.mountain" />
                  <label for="mountain">山區</label>
                </div>
                <div class="checkbox-item">
                  <input type="checkbox" id="forest" v-model="preferences.environment.forest" />
                  <label for="forest">森林</label>
                </div>
                <div class="checkbox-item">
                  <input type="checkbox" id="beach" v-model="preferences.environment.beach" />
                  <label for="beach">海邊</label>
                </div>
                <div class="checkbox-item">
                  <input type="checkbox" id="lake" v-model="preferences.environment.lake" />
                  <label for="lake">湖邊</label>
                </div>
              </div>
            </div>
            
            <h3 class="mt-4">顯示設定</h3>
            <div class="form-group">
              <label for="language">語言</label>
              <select id="language" v-model="preferences.display.language">
                <option value="zh-TW">繁體中文</option>
                <option value="en">English</option>
                <option value="ja">日本語</option>
              </select>
            </div>
            
            <div class="form-actions">
              <button @click="savePreferences" class="btn-save" :disabled="preferenceLoading">
                {{ preferenceLoading ? '儲存中...' : '儲存設定' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed, watch } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useToast } from '@/composables/useToast';
import apiService from '@/services/api/apiService';

export default {
  name: 'ProfileView',
  setup() {
    const authStore = useAuthStore();
    const { showToast } = useToast();
    const user = ref({...authStore.user} || {});
    const activeTab = ref('info');
    const loading = ref(false);
    const passwordLoading = ref(false);
    const notificationLoading = ref(false);
    const preferenceLoading = ref(false);
    const twoFactorEnabled = ref(false);
    const twoFactorLoading = ref(false);
    const isLoading = ref(true);
    
    // 自定義成功通知
    const showSuccess = ref(false);
    const successMessage = ref('');
    const successTimeout = ref(null);
    
    // 頭像上傳相關
    const avatarFileInput = ref(null);
    const uploadingAvatar = ref(false);
    const avatarUploadProgress = ref(0);
    const previewAvatar = ref(null);
    
    // 顯示成功通知的方法
    const displaySuccessNotification = (message) => {
      successMessage.value = message;
      showSuccess.value = true;
      
      // 清除之前的計時器
      if (successTimeout.value) {
        clearTimeout(successTimeout.value);
      }
      
      // 3秒後自動隱藏
      successTimeout.value = setTimeout(() => {
        showSuccess.value = false;
      }, 3000);
    };
    
    // 手動隱藏成功通知
    const hideSuccessNotification = () => {
      showSuccess.value = false;
      if (successTimeout.value) {
        clearTimeout(successTimeout.value);
      }
    };
    
    // 觸發文件上傳對話框
    const triggerFileUpload = () => {
      if (avatarFileInput.value) {
        avatarFileInput.value.click();
      }
    };
    
    // 處理頭像上傳
    const handleAvatarChange = async (event) => {
      const file = event.target.files[0];
      if (!file) return;
      
      // 檢查文件類型
      const validImageTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
      if (!validImageTypes.includes(file.type)) {
        showToast('請選擇有效的圖片格式 (JPG, PNG, GIF, WEBP)', 'error');
        return;
      }
      
      // 檢查文件大小 (最大5MB)
      const maxSizeInBytes = 5 * 1024 * 1024;
      if (file.size > maxSizeInBytes) {
        showToast('圖片大小不能超過5MB', 'error');
        return;
      }
      
      // 預覽圖片
      const reader = new FileReader();
      reader.onload = (e) => {
        previewAvatar.value = e.target.result;
      };
      reader.readAsDataURL(file);
      
      // 創建FormData對象
      const formData = new FormData();
      formData.append('avatar', file);
      
      // 開始上傳
      uploadingAvatar.value = true;
      avatarUploadProgress.value = 0;
      
      try {
        // 使用帶進度條的上傳方法
        const response = await apiService.uploadWithProgress(
          '/users/avatar', 
          formData, 
          (progressEvent) => {
            const percentCompleted = Math.round(
              (progressEvent.loaded * 100) / progressEvent.total
            );
            avatarUploadProgress.value = percentCompleted;
          }
        );
        
        // 上傳成功處理
        if (response && response.data && response.data.avatarUrl) {
          // 更新用戶頭像
          user.value.avatar = response.data.avatarUrl;
          
          // 更新 Auth Store
          authStore.updateUserAvatar(response.data.avatarUrl);
          
          // 保存到 localStorage
          saveUserDataToLocalStorage({
            avatar: response.data.avatarUrl
          });
          
          // 顯示成功通知
          displaySuccessNotification('頭像更新成功');
        } else {
          throw new Error('服務器返回的數據無效');
        }
      } catch (error) {
        console.error('上傳頭像失敗:', error);
        
        // 保留本地預覽，但顯示錯誤訊息
        showToast('上傳頭像失敗: ' + (error.response?.data?.message || '伺服器錯誤'), 'error');
        
        // 保存本地預覽到localStorage，以便下次加載
        if (previewAvatar.value) {
          saveUserDataToLocalStorage({
            tempAvatar: previewAvatar.value
          });
        }
      } finally {
        uploadingAvatar.value = false;
        // 重置文件輸入
        if (event.target) {
          event.target.value = '';
        }
      }
    };
    
    // 儲存數據到 localStorage 的輔助函數
    const saveUserDataToLocalStorage = (userData) => {
      try {
        // 獲取當前存儲的數據
        const existingData = JSON.parse(localStorage.getItem('userData') || '{}');
        
        // 合併新數據，確保不覆蓋其他設定
        const mergedData = { ...existingData, ...userData };
        
        // 保存到 localStorage
        localStorage.setItem('userData', JSON.stringify(mergedData));
        
        // 也更新當前的 Auth Store
        if (userData.username || userData.email || userData.avatar) {
          authStore.updateUserInfo(userData);
        }
        
        console.log('數據已成功保存到 localStorage');
        return true;
      } catch (error) {
        console.error('保存數據到 localStorage 失敗:', error);
        return false;
      }
    };
    
    // 從 localStorage 載入數據
    const loadUserDataFromLocalStorage = () => {
      try {
        const savedData = localStorage.getItem('userData');
        if (savedData) {
          return JSON.parse(savedData);
        }
        return null;
      } catch (error) {
        console.error('從 localStorage 載入數據失敗:', error);
        return null;
      }
    };
    
    // 表單數據
    const profileForm = reactive({
      fullName: '',
      phone: '',
      birthday: '',
      gender: '',
      address: '',
      bio: ''
    });
    
    const passwordForm = reactive({
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    });
    
    const notifications = reactive({
      email: true,
      sms: false,
      marketing: false
    });
    
    const preferences = reactive({
      camping: {
        tent: true,
        rv: false,
        cabin: true,
        glamping: false
      },
      environment: {
        mountain: true,
        forest: true,
        beach: false,
        lake: false
      },
      display: {
        language: 'zh-TW'
      }
    });
    
    // 更新個人資料
    const updateProfile = async () => {
      loading.value = true;
      try {
        const response = await apiService.put('/users/profile', profileForm);
        user.value = response.data;
        
        // 保存到 localStorage
        const saveSuccess = saveUserDataToLocalStorage({
          ...response.data,
          profileForm: { ...profileForm }
        });
        
        // 顯示成功通知 - 使用自定義通知
        displaySuccessNotification('個人資料更新成功');
        
        // 同時也使用 Toast 通知（如果需要）
        showToast('個人資料更新成功', 'success');
      } catch (error) {
        console.error('更新個人資料失敗:', error);
        showToast('更新個人資料失敗: ' + (error.response?.data?.message || '未知錯誤'), 'error');
        
        // 嘗試保存到本地，即使 API 失敗
        saveUserDataToLocalStorage({
          profileForm: { ...profileForm }
        });
      } finally {
        loading.value = false;
      }
    };
    
    // 重置表單
    const resetForm = () => {
      // 優先使用用戶數據
      if (user.value && Object.keys(user.value).length > 0) {
        Object.assign(profileForm, {
          fullName: user.value.full_name || '',
          phone: user.value.phone || '',
          birthday: user.value.birthday || '',
          gender: user.value.gender || '',
          address: user.value.address || '',
          bio: user.value.bio || ''
        });
      } else {
        // 如果用戶數據不可用，嘗試從 localStorage 讀取
        const savedData = loadUserDataFromLocalStorage();
        if (savedData && savedData.profileForm) {
          Object.assign(profileForm, savedData.profileForm);
        }
      }
    };
    
    // 更改密碼
    const changePassword = async () => {
      // 驗證新密碼和確認密碼是否一致
      if (passwordForm.newPassword !== passwordForm.confirmPassword) {
        showToast('新密碼和確認密碼不一致', 'error');
        return;
      }
      
      // 驗證密碼強度
      const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$/;
      if (!passwordRegex.test(passwordForm.newPassword)) {
        showToast('密碼須包含至少8個字符，包括大小寫字母和數字', 'error');
        return;
      }
      
      passwordLoading.value = true;
      try {
        await apiService.put('/users/password', {
          currentPassword: passwordForm.currentPassword,
          newPassword: passwordForm.newPassword
        });
        
        // 清空表單
        passwordForm.currentPassword = '';
        passwordForm.newPassword = '';
        passwordForm.confirmPassword = '';
        
        // 顯示自定義成功通知
        displaySuccessNotification('密碼更新成功');
        
        // Toast 通知
        showToast('密碼更新成功', 'success');
      } catch (error) {
        console.error('更新密碼失敗:', error);
        showToast('更新密碼失敗: ' + (error.response?.data?.message || '未知錯誤'), 'error');
      } finally {
        passwordLoading.value = false;
      }
    };
    
    // 切換雙重驗證
    const toggleTwoFactor = async () => {
      twoFactorLoading.value = true;
      try {
        await apiService.put('/users/security/2fa', {
          enabled: twoFactorEnabled.value
        });
        
        // 保存到 localStorage
        saveUserDataToLocalStorage({
          security: { twoFactorEnabled: twoFactorEnabled.value }
        });
        
        // 顯示自定義成功通知
        displaySuccessNotification(twoFactorEnabled.value ? '雙重驗證已啟用' : '雙重驗證已停用');
        
        showToast(
          twoFactorEnabled.value ? '雙重驗證已啟用' : '雙重驗證已停用', 
          'success'
        );
      } catch (error) {
        console.error('更新雙重驗證設定失敗:', error);
        // 回復原狀態
        twoFactorEnabled.value = !twoFactorEnabled.value;
        showToast('更新雙重驗證設定失敗: ' + (error.response?.data?.message || '未知錯誤'), 'error');
        
        // 仍然保存到 localStorage
        saveUserDataToLocalStorage({
          security: { twoFactorEnabled: twoFactorEnabled.value }
        });
      } finally {
        twoFactorLoading.value = false;
      }
    };
    
    // 保存通知設定
    const saveNotifications = async () => {
      notificationLoading.value = true;
      try {
        await apiService.put('/users/notifications', notifications);
        
        // 保存到 localStorage
        saveUserDataToLocalStorage({
          notifications: { ...notifications }
        });
        
        // 顯示自定義成功通知
        displaySuccessNotification('通知設定已儲存');
        
        showToast('通知設定已儲存', 'success');
      } catch (error) {
        console.error('儲存通知設定失敗:', error);
        showToast('儲存通知設定失敗: ' + (error.response?.data?.message || '未知錯誤'), 'error');
        
        // 即使 API 失敗，仍然保存到 localStorage
        saveUserDataToLocalStorage({
          notifications: { ...notifications }
        });
      } finally {
        notificationLoading.value = false;
      }
    };
    
    // 保存偏好設定
    const savePreferences = async () => {
      preferenceLoading.value = true;
      try {
        await apiService.put('/users/preferences', preferences);
        
        // 保存到 localStorage
        saveUserDataToLocalStorage({
          preferences: JSON.parse(JSON.stringify(preferences))
        });
        
        // 顯示自定義成功通知
        displaySuccessNotification('偏好設定已儲存');
        
        showToast('偏好設定已儲存', 'success');
      } catch (error) {
        console.error('儲存偏好設定失敗:', error);
        showToast('儲存偏好設定失敗: ' + (error.response?.data?.message || '未知錯誤'), 'error');
        
        // 即使 API 失敗，仍然保存到 localStorage
        saveUserDataToLocalStorage({
          preferences: JSON.parse(JSON.stringify(preferences))
        });
      } finally {
        preferenceLoading.value = false;
      }
    };
    
    // 在登出時保存數據
    watch(() => authStore.isAuthenticated, (newValue, oldValue) => {
      // 如果從已登入變為未登入（登出）
      if (oldValue === true && newValue === false) {
        console.log('用戶登出，確保數據已保存到 localStorage');
        // 最後一次保存所有數據到 localStorage
        saveUserDataToLocalStorage({
          username: user.value.username,
          email: user.value.email,
          avatar: user.value.avatar,
          full_name: profileForm.fullName,
          phone: profileForm.phone,
          birthday: profileForm.birthday,
          gender: profileForm.gender,
          address: profileForm.address,
          bio: profileForm.bio,
          notifications: { ...notifications },
          preferences: JSON.parse(JSON.stringify(preferences)),
          security: { twoFactorEnabled: twoFactorEnabled.value },
          profileForm: { ...profileForm }
        });
      }
    });
    
    // 頁面加載時獲取用戶完整資料
    onMounted(async () => {
      isLoading.value = true;
      
      // 從 localStorage 讀取之前保存的數據作為備用
      const savedUserData = loadUserDataFromLocalStorage();
      let apiSuccess = true;
      
      // 檢查是否有臨時頭像
      if (savedUserData && savedUserData.tempAvatar) {
        previewAvatar.value = savedUserData.tempAvatar;
      }
      
      try {
        // 獲取用戶詳細信息
        const response = await apiService.get('/users/profile');
        user.value = response.data;
        
        // 更新 Auth Store
        authStore.updateUserInfo(response.data);
        
        // 重置表單
        resetForm();
      } catch (error) {
        console.error('獲取用戶資料失敗:', error);
        apiSuccess = false;
        
        // 如果 API 請求失敗，使用 localStorage 中的數據
        if (savedUserData) {
          user.value = { ...savedUserData };
          
          // 設置表單數據
          if (savedUserData.profileForm) {
            Object.assign(profileForm, savedUserData.profileForm);
          } else {
            // 嘗試從基本用戶數據提取
            Object.assign(profileForm, {
              fullName: savedUserData.full_name || '',
              phone: savedUserData.phone || '',
              birthday: savedUserData.birthday || '',
              gender: savedUserData.gender || '',
              address: savedUserData.address || '',
              bio: savedUserData.bio || ''
            });
          }
          
          showToast('使用本地存儲的用戶資料', 'info');
        }
      }
      
      // 獲取通知設定
      try {
        if (apiSuccess) {
          const notificationRes = await apiService.get('/users/notifications');
          Object.assign(notifications, notificationRes.data);
        }
      } catch (error) {
        console.error('獲取通知設定失敗:', error);
        
        // 如果 API 請求失敗，使用 localStorage 中的數據
        if (savedUserData && savedUserData.notifications) {
          Object.assign(notifications, savedUserData.notifications);
          showToast('使用本地存儲的通知設定', 'info');
        }
      }
      
      // 獲取偏好設定
      try {
        if (apiSuccess) {
          const preferenceRes = await apiService.get('/users/preferences');
          if (preferenceRes.data.camping) {
            Object.assign(preferences.camping, preferenceRes.data.camping);
          }
          if (preferenceRes.data.environment) {
            Object.assign(preferences.environment, preferenceRes.data.environment);
          }
          if (preferenceRes.data.display) {
            Object.assign(preferences.display, preferenceRes.data.display);
          }
        }
      } catch (error) {
        console.error('獲取偏好設定失敗:', error);
        
        // 如果 API 請求失敗，使用 localStorage 中的數據
        if (savedUserData && savedUserData.preferences) {
          if (savedUserData.preferences.camping) {
            Object.assign(preferences.camping, savedUserData.preferences.camping);
          }
          if (savedUserData.preferences.environment) {
            Object.assign(preferences.environment, savedUserData.preferences.environment);
          }
          if (savedUserData.preferences.display) {
            Object.assign(preferences.display, savedUserData.preferences.display);
          }
          showToast('使用本地存儲的偏好設定', 'info');
        }
      }
      
      // 獲取雙重驗證狀態
      try {
        if (apiSuccess) {
          const securityRes = await apiService.get('/users/security');
          twoFactorEnabled.value = securityRes.data.twoFactorEnabled || false;
        }
      } catch (error) {
        console.error('獲取雙重驗證狀態失敗:', error);
        
        // 如果 API 請求失敗，使用 localStorage 中的數據
        if (savedUserData && savedUserData.security && savedUserData.security.twoFactorEnabled !== undefined) {
          twoFactorEnabled.value = savedUserData.security.twoFactorEnabled;
          showToast('使用本地存儲的安全設定', 'info');
        }
      }
      
      // 確保 localStorage 保存了所有最新的數據
      saveUserDataToLocalStorage({
        ...user.value,
        notifications: { ...notifications },
        preferences: JSON.parse(JSON.stringify(preferences)),
        security: { twoFactorEnabled: twoFactorEnabled.value },
        profileForm: { ...profileForm }
      });
      
      // 結束加載狀態
      isLoading.value = false;
    });
    
    return {
      user,
      activeTab,
      profileForm,
      passwordForm,
      notifications,
      preferences,
      loading,
      passwordLoading,
      notificationLoading,
      preferenceLoading,
      twoFactorEnabled,
      twoFactorLoading,
      isLoading,
      updateProfile,
      resetForm,
      changePassword,
      toggleTwoFactor,
      saveNotifications,
      savePreferences,
      handleAvatarChange,
      showSuccess,
      successMessage,
      hideSuccessNotification,
      // 新增的頭像上傳功能
      avatarFileInput,
      uploadingAvatar,
      avatarUploadProgress,
      previewAvatar,
      triggerFileUpload
    };
  }
};
</script>

<style scoped>
.profile-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  position: relative;
}

/* 成功通知樣式 */
.success-notification {
  position: fixed;
  top: 20px;
  right: 20px;
  display: flex;
  align-items: center;
  padding: 15px 20px;
  background-color: #0fb886;
  color: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

.success-icon {
  margin-right: 10px;
  font-size: 20px;
}

.success-message {
  flex: 1;
  font-size: 16px;
}

.close-notification {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  padding: 0;
  margin-left: 10px;
  font-size: 16px;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.8);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #0fb886;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 10px;
}
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.profile-header {
  margin-bottom: 30px;
}

.profile-header h1 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.profile-header p {
  color: #6b7280;
}

.profile-content {
  display: flex;
  gap: 30px;
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.profile-sidebar {
  width: 250px;
  background-color: #f9fafb;
  padding: 20px;
  border-right: 1px solid #e5e7eb;
}

.profile-avatar {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-bottom: 20px;
  margin-bottom: 20px;
  border-bottom: 1px solid #e5e7eb;
}

.avatar-image {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background-color: #e5e7eb;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 15px;
  overflow: hidden;
  position: relative; /* 添加相對定位用於懸停效果 */
}
.avatar-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-image i {
  font-size: 60px;
  color: #9ca3af;
}

/* 頭像懸停覆蓋層 */
.avatar-hover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  cursor: pointer;
}

.avatar-image:hover .avatar-hover-overlay {
  opacity: 1;
}

.btn-upload-avatar {
  background: none;
  border: none;
  color: white;
  font-size: 24px;
  cursor: pointer;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: rgba(255, 255, 255, 0.2);
  transition: background-color 0.3s;
}

.btn-upload-avatar:hover {
  background-color: rgba(255, 255, 255, 0.3);
}

/* 頭像上傳進度覆蓋層 */
.avatar-upload-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1100;
}

.upload-progress-container {
  background-color: white;
  padding: 30px;
  border-radius: 10px;
  width: 300px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.upload-progress-container i {
  font-size: 40px;
  color: #0fb886;
  margin-bottom: 20px;
}

.upload-progress-container p {
  margin-bottom: 20px;
  color: #4b5563;
}

.upload-progress-bar {
  height: 10px;
  background-color: #e5e7eb;
  border-radius: 5px;
  overflow: hidden;
  margin-bottom: 10px;
}

.upload-progress-fill {
  height: 100%;
  background-color: #0fb886;
  transition: width 0.3s ease;
}

.avatar-info {
  text-align: center;
  margin-bottom: 15px;
}

.user-name {
  font-weight: bold;
  font-size: 18px;
  margin-bottom: 5px;
}

.user-email {
  color: #6b7280;
  font-size: 14px;
}

.btn-change-avatar {
  padding: 8px 12px;
  border: none;
  background-color: transparent;
  color: #0fb886;
  cursor: pointer;
  border-radius: 5px;
  font-size: 14px;
  transition: background-color 0.3s;
  display: inline-block;
}

.btn-change-avatar:hover {
  background-color: rgba(15, 184, 134, 0.1);
}

.profile-nav {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.profile-nav button {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  background: none;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  text-align: left;
  color: #4b5563;
  transition: all 0.3s;
}

.profile-nav button i {
  margin-right: 10px;
  width: 20px;
  text-align: center;
}

.profile-nav button:hover {
  background-color: #f3f4f6;
}

.profile-nav button.active {
  background-color: #0fb886;
  color: white;
}

.profile-body {
  flex: 1;
  padding: 30px;
}

.profile-tab h2 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.tab-desc {
  color: #6b7280;
  margin-bottom: 30px;
}

.profile-form {
  max-width: 600px;
}
.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #4b5563;
  font-weight: 500;
}

.form-hint {
  display: block;
  margin-top: 5px;
  color: #6b7280;
  font-size: 12px;
}

.form-group input[type="text"],
.form-group input[type="tel"],
.form-group input[type="date"],
.form-group input[type="password"],
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 10px;
  border: 1px solid #d1d5db;
  border-radius: 5px;
  font-size: 16px;
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}

.radio-group,
.checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}
.radio-item,
.checkbox-item {
  display: flex;
  align-items: center;
}

.radio-item input,
.checkbox-item input {
  margin-right: 8px;
}

.form-actions {
  display: flex;
  gap: 15px;
  margin-top: 30px;
}

.btn-save,
.btn-cancel {
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.3s;
}

.btn-save {
  background-color: #0fb886;
  color: white;
  border: none;
}

.btn-save:hover {
  background-color: #0d9e75;
}

.btn-save:disabled {
  background-color: #94d3a2;
  cursor: not-allowed;
}

.btn-cancel {
  background-color: transparent;
  border: 1px solid #d1d5db;
  color: #4b5563;
}
.btn-cancel:hover {
  background-color: #f3f4f6;
}

.security-section,
.notification-section,
.preference-section {
  margin-bottom: 40px;
}

.security-section h3,
.notification-section h3,
.preference-section h3 {
  color: #4b5563;
  margin-bottom: 15px;
}

.security-desc {
  color: #6b7280;
  font-size: 14px;
  margin-top: 10px;
}

.toggle-container {
  display: flex;
  align-items: center;
  gap: 10px;
}

.notification-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #e5e7eb;
}

.notification-item:last-child {
  border-bottom: none;
}

.notification-info h3 {
  margin-bottom: 5px !important;
}

.notification-info p {
  color: #6b7280;
  font-size: 14px;
}

.toggle {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 26px;
}

.toggle input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: .4s;
  border-radius: 26px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 18px;
  width: 18px;
  left: 4px;
  bottom: 4px;
  background-color: white;
  transition: .4s;
  border-radius: 50%;
}

input:checked + .slider {
  background-color: #0fb886;
}

input:checked + .slider:before {
  transform: translateX(24px);
}

.mt-4 {
  margin-top: 25px;
}

@media (max-width: 768px) {
  .profile-content {
    flex-direction: column;
  }
  
  .profile-sidebar {
    width: 100%;
    border-right: none;
    border-bottom: 1px solid #e5e7eb;
  }

  .avatar-image {
    width: 80px;
    height: 80px;
  }

  .avatar-image i {
    font-size: 45px;
  }
  
  .success-notification {
    width: 90%;
    right: 5%;
  }
}

@media (max-width: 480px) {
  .form-actions {
    flex-direction: column;
  }

  .btn-save, .btn-cancel {
    width: 100%;
  }

  .radio-group, .checkbox-group {
    flex-direction: column;
    gap: 8px;
  }
}
</style>