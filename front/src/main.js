import { createApp } from 'vue'
import { createPinia } from 'pinia'
import axios from 'axios'

import App from './App.vue'
import router from './router'
import setupAuthGuard from './auth-guard'
import { toastStyles } from './composables/useToast'
import { confirmStyles } from './composables/useConfirm'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

const app = createApp(App)

// 創建狀態管理
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
app.use(pinia)

// 設置路由
app.use(router)

// 設置身份驗證守衛
setupAuthGuard(router)

// 添加全局樣式
const style = document.createElement('style')
style.textContent = toastStyles + confirmStyles
document.head.appendChild(style)

// 掛載應用
app.mount('#app')