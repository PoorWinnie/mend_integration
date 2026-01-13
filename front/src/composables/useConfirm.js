// src/composables/useConfirm.js
import { ref, reactive } from 'vue';

// 創建一個簡單的確認對話框系統
const visible = ref(false);
const options = reactive({
  title: '',
  message: '',
  confirmText: '確認',
  cancelText: '取消',
  confirmVariant: 'primary'
});
let resolvePromise = null;

export function useConfirm() {
  // 顯示確認對話框並返回一個 Promise
  const confirm = (opts = {}) => {
    return new Promise((resolve) => {
      // 配置選項
      options.title = opts.title || '確認';
      options.message = opts.message || '你確定要執行此操作嗎？';
      options.confirmText = opts.confirmText || '確認';
      options.cancelText = opts.cancelText || '取消';
      options.confirmVariant = opts.confirmVariant || 'primary';
      
      // 儲存 resolve 函數以便之後調用
      resolvePromise = resolve;
      
      // 顯示對話框
      visible.value = true;
    });
  };
  
  // 用戶確認
  const handleConfirm = () => {
    visible.value = false;
    if (resolvePromise) {
      resolvePromise(true);
      resolvePromise = null;
    }
  };
  
  // 用戶取消
  const handleCancel = () => {
    visible.value = false;
    if (resolvePromise) {
      resolvePromise(false);
      resolvePromise = null;
    }
  };
  
  return {
    visible,
    options,
    confirm,
    handleConfirm,
    handleCancel
  };
}

// 在全局註冊 Confirm 組件
export function setupConfirm(app) {
  app.component('ConfirmDialog', {
    template: `
      <transition name="confirm-fade">
        <div v-if="visible" class="confirm-overlay">
          <div class="confirm-dialog">
            <div class="confirm-header">
              <h4>{{ options.title }}</h4>
            </div>
            <div class="confirm-body">
              <p>{{ options.message }}</p>
            </div>
            <div class="confirm-footer">
              <button class="btn btn-secondary" @click="handleCancel">
                {{ options.cancelText }}
              </button>
              <button :class="['btn', 'btn-' + options.confirmVariant]" @click="handleConfirm">
                {{ options.confirmText }}
              </button>
            </div>
          </div>
        </div>
      </transition>
    `,
    setup() {
      const { visible, options, handleConfirm, handleCancel } = useConfirm();
      
      return {
        visible,
        options,
        handleConfirm,
        handleCancel
      };
    }
  });
}

// 全局 Confirm 樣式，需在 App.vue 或 main.js 中引入
export const confirmStyles = `
.confirm-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.confirm-dialog {
  background-color: white;
  border-radius: 8px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  overflow: hidden;
}

.confirm-header {
  padding: 15px 20px;
  border-bottom: 1px solid #e5e7eb;
}

.confirm-header h4 {
  margin: 0;
  font-size: 18px;
  color: #1f2937;
}

.confirm-body {
  padding: 20px;
}

.confirm-body p {
  margin: 0;
  color: #4b5563;
}

.confirm-footer {
  padding: 15px 20px;
  border-top: 1px solid #e5e7eb;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn {
  padding: 8px 16px;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: all 0.2s;
}

.btn-secondary {
  background-color: #f3f4f6;
  color: #4b5563;
}

.btn-secondary:hover {
  background-color: #e5e7eb;
}

.btn-primary {
  background-color: #3b82f6;
  color: white;
}

.btn-primary:hover {
  background-color: #2563eb;
}

.btn-danger {
  background-color: #ef4444;
  color: white;
}

.btn-danger:hover {
  background-color: #dc2626;
}

.confirm-fade-enter-active,
.confirm-fade-leave-active {
  transition: opacity 0.3s;
}

.confirm-fade-enter-from,
.confirm-fade-leave-to {
  opacity: 0;
}
`;