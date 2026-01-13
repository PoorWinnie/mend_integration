// reviewApi.js
import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  }
});

// 請求攔截器
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken') || 
                  localStorage.getItem('token') || 
                  sessionStorage.getItem('accessToken');
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 模擬數據
const mockReviews = [
  {
    id: 1,
    username: '張小明',
    camp_name: '山林露營地',
    camp_image: 'https://via.placeholder.com/350x200?text=Camp+1',
    camp_location: '新北市三峽區',
    rating: 5,
    title: '絕佳的露營體驗',
    content: '這是我去過最棒的露營地，環境優美，設施完善，服務人員也很親切。下次一定會再來！',
    created_at: '2023-08-05T10:15:30',
    has_images: true,
    images: [
      'https://via.placeholder.com/350x200?text=Review+Image+1',
      'https://via.placeholder.com/350x200?text=Review+Image+2',
      'https://via.placeholder.com/350x200?text=Review+Image+3'
    ],
    is_reported: false,
    has_reply: true,
    reply_content: '感謝您的支持，我們很高興您喜歡我們的露營地，期待您的再次光臨！',
    reply_by: '山林露營地管理員',
    replied_at: '2023-08-06T14:22:45'
  },
  {
    id: 2,
    username: '李大華',
    camp_name: '海濱露營區',
    camp_image: 'https://via.placeholder.com/350x200?text=Camp+2',
    camp_location: '宜蘭縣頭城鎮',
    rating: 3,
    title: '一般的體驗',
    content: '露營地的位置還不錯，靠近海邊風景很美，但是設施有點舊，衛生間也不太乾淨，希望能改進。',
    created_at: '2023-08-10T14:22:45',
    has_images: false,
    images: [],
    is_reported: true,
    reporter_name: '海濱露營區',
    reported_at: '2023-08-11T09:05:15',
    report_reason: '評論含有不實信息',
    has_reply: false,
    reply_content: '',
    reply_by: '',
    replied_at: ''
  },
  {
    id: 3,
    username: '王美玲',
    camp_name: '森林步道營地',
    camp_image: 'https://via.placeholder.com/350x200?text=Camp+3',
    camp_location: '南投縣魚池鄉',
    rating: 4,
    title: '愜意的森林體驗',
    content: '環境非常好，被大自然包圍的感覺很棒，晚上可以看到很多星星。唯一的缺點是沒有電，手機無法充電。',
    created_at: '2023-08-15T09:05:15',
    has_images: true,
    images: [
      'https://via.placeholder.com/350x200?text=Review+Image+4',
      'https://via.placeholder.com/350x200?text=Review+Image+5'
    ],
    is_reported: false,
    has_reply: false,
    reply_content: '',
    reply_by: '',
    replied_at: ''
  }
];

export default {
  // 獲取評價列表
  getReviews(params = {}) {
    // 實際環境中，應該是真實API調用
    // return apiClient.get('/api/reviews', { params });
    
    // 模擬數據和過濾
    return new Promise((resolve) => {
      setTimeout(() => {
        let filteredReviews = [...mockReviews];
        
        // 搜索過濾
        if (params.search) {
          const search = params.search.toLowerCase();
          filteredReviews = filteredReviews.filter(
            review => review.content.toLowerCase().includes(search) || 
                      review.username.toLowerCase().includes(search) ||
                      review.title.toLowerCase().includes(search)
          );
        }
        
        // 評分過濾
        if (params.rating) {
          filteredReviews = filteredReviews.filter(
            review => review.rating === parseInt(params.rating)
          );
        }
        
        // 被檢舉過濾
        if (params.reported === 'reported') {
          filteredReviews = filteredReviews.filter(review => review.is_reported);
        } else if (params.reported === 'not_reported') {
          filteredReviews = filteredReviews.filter(review => !review.is_reported);
        }
        
        // 營地過濾
        if (params.camp_id) {
          filteredReviews = filteredReviews.filter(
            review => review.camp_id === params.camp_id
          );
        }
        
        // 分頁處理
        const page = params.page || 1;
        const limit = params.limit || 10;
        const start = (page - 1) * limit;
        const end = start + limit;
        const paginatedReviews = filteredReviews.slice(start, end);
        
        resolve({
          data: {
            reviews: paginatedReviews,
            total: filteredReviews.length
          }
        });
      }, 500);
    });
  },
  
  // 根據ID獲取評價
  getReviewById(id) {
    // 實際環境中，應該是真實API調用
    // return apiClient.get(`/api/reviews/${id}`);
    
    // 模擬數據
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const review = mockReviews.find(r => r.id === id);
        if (review) {
          resolve({ data: review });
        } else {
          reject(new Error('評價不存在'));
        }
      }, 300);
    });
  },
  
  // 回覆評價
  replyToReview(id, content) {
    // 實際環境中，應該是真實API調用
    // return apiClient.post(`/api/reviews/${id}/reply`, { content });
    
    // 模擬回覆
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const index = mockReviews.findIndex(r => r.id === id);
        if (index !== -1) {
          mockReviews[index].has_reply = true;
          mockReviews[index].reply_content = content;
          mockReviews[index].reply_by = '系統管理員';
          mockReviews[index].replied_at = new Date().toISOString();
          resolve({ data: mockReviews[index] });
        } else {
          reject(new Error('評價不存在'));
        }
      }, 300);
    });
  },
  
  // 駁回檢舉
  dismissReport(id) {
    // 實際環境中，應該是真實API調用
    // return apiClient.post(`/api/reviews/${id}/dismiss-report`);
    
    // 模擬駁回檢舉
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const index = mockReviews.findIndex(r => r.id === id);
        if (index !== -1) {
          mockReviews[index].is_reported = false;
          mockReviews[index].reporter_name = '';
          mockReviews[index].reported_at = '';
          mockReviews[index].report_reason = '';
          resolve({ data: mockReviews[index] });
        } else {
          reject(new Error('評價不存在'));
        }
      }, 300);
    });
  },
  
  // 刪除評價
  deleteReview(id) {
    // 實際環境中，應該是真實API調用
    // return apiClient.delete(`/api/reviews/${id}`);
    
    // 模擬刪除
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const index = mockReviews.findIndex(r => r.id === id);
        if (index !== -1) {
          mockReviews.splice(index, 1);
          resolve({ data: { success: true } });
        } else {
          reject(new Error('評價不存在'));
        }
      }, 300);
    });
  }
};