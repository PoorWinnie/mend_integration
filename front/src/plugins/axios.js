import axios from "axios";
import { useAuthStore } from '@/stores/auth';

const instance = axios.create({
    baseURL: import.meta.env.VITE_API_URL,
});

instance.interceptors.request.use((config) => {
    const token = localStorage.getItem('accessToken');
  
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
  
    return config;
  }, (error) => {
    return Promise.reject(error);
  });
  
instance.interceptors.response.use(
    function (response) {
      return response;
    },

    function (error) {
      console.log("interceptors error", error);
      
      // 新增：處理評價相關的404錯誤
      if (
        error.response && 
        error.response.status === 404 && 
        error.config.url.includes('/reviews/')
      ) {
        console.log('攔截到評價404錯誤:', error.config.url);
        
        // 創建一個虛擬的成功響應
        const reviewId = error.config.url.split('/reviews/')[1].split('?')[0];
        
        console.log(`為評價ID ${reviewId} 創建替代響應`);
        
        // 返回一個替代響應
        return Promise.resolve({
          data: {
            id: parseInt(reviewId),
            reviewText: '此評價已被刪除',
            replyText: '此回覆已被刪除',
            pros: '無法獲取',
            cons: '無法獲取',
            deleted: true,
            error: false,
            userName: '(已刪除用戶)',
            overallRating: 0,
            cleanlinessRating: 0,
            convenienceRating: 0,
            friendlinessRating: 0,
            createdAt: new Date().toISOString()
          }
        });
      }
  
      // 如果是未授權 (401)
      if (error.response && error.response.status === 401) {
        console.warn("⚠️ Token 過期或未授權，將導向登入頁");
        // 可選：清除登入資訊
        localStorage.clear();
        // 導向登入頁（視情況改成 router.push('/login')）
        // window.location.href = "/login";
      }
  
      // 如果是禁止訪問 (403)
      else if (error.response && error.response.status === 403) {
        console.warn("⚠️ 權限不足，導向 /403 頁面");
        window.location.href = "/403";
      }
  
      return Promise.reject(error);
    }
  );
  
export default instance;