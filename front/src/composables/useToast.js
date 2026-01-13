// src/composables/useToast.js
import { ref } from 'vue';

// 創建一個簡單的 Toast 通知系統
// 這是一個單例模式，確保整個應用只有一個 toast 實例
const visible = ref(false);
const message = ref('');
const type = ref('info'); // info, success, error, warning
const timeout = ref(null);

export function useToast() {
  // 顯示 toast 通知
  const showToast = (msg, toastType = 'info', duration = 3000) => {
    // 如果已經有 toast 在顯示，先清除它
    if (timeout.value) {
      clearTimeout(timeout.value);
      timeout.value = null;
    }
    
    // 設置新的 toast
    message.value = msg;
    type.value = toastType;
    visible.value = true;
    
    // 設置自動關閉
    timeout.value = setTimeout(() => {
      visible.value = false;
    }, duration);
  };
  
  // 手動關閉 toast
  const hideToast = () => {
    visible.value = false;
    if (timeout.value) {
      clearTimeout(timeout.value);
      timeout.value = null;
    }
  };
  
  return {
    visible,
    message,
    type,
    showToast,
    hideToast
  };
}

// 在全局註冊 Toast 組件
export function setupToast(app) {
  // 這個函數可以在 main.js 中調用來註冊全局 Toast 組件
  app.component('Toast', {
    template: `
      <transition name="toast">
        <div v-if="visible" :class="['toast', 'toast-' + type]">
          <div class="toast-content">
            <i :class="getIconClass()"></i>
            <span>{{ message }}</span>
          </div>
          <button class="toast-close" @click="hideToast">
            <i class="fa fa-times"></i>
          </button>
        </div>
      </transition>
    `,
    setup() {
      const { visible, message, type, hideToast } = useToast();
      
      const getIconClass = () => {
        const classes = {
          'info': 'fa fa-info-circle',
          'success': 'fa fa-check-circle',
          'error': 'fa fa-times-circle',
          'warning': 'fa fa-exclamation-triangle'
        };
        return classes[type.value] || classes.info;
      };
      
      return {
        visible,
        message,
        type,
        hideToast,
        getIconClass
      };
    }
  });
}

// 全局 Toast 樣式，需在 App.vue 或 main.js 中引入
export const toastStyles = `
.toast {
  position: fixed;
  top: 20px;
  right: 20px;
  max-width: 350px;
  padding: 15px 20px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 9999;
}

.toast-content {
  display: flex;
  align-items: center;
  gap: 10px;
}

.toast-content i {
  font-size: 18px;
}

.toast-info {
  background-color: #e0f2fe;
  color: #0ea5e9;
}

.toast-success {
  background-color: #dcfce7;
  color: #16a34a;
}

.toast-error {
  background-color: #fee2e2;
  color: #dc2626;
}

.toast-warning {
  background-color: #fef3c7;
  color: #d97706;
}

.toast-close {
  background: none;
  border: none;
  color: inherit;
  opacity: 0.7;
  cursor: pointer;
}

.toast-close:hover {
  opacity: 1;
}

.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}
`;