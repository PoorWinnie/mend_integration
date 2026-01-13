// campApi.js
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

// 暫時模擬數據
const mockCamps = [
  {
    id: 1,
    name: '山林露營地',
    region: 'north',
    location: '新北市三峽區',
    description: '位於山區的優美露營地，有完善的設施和絕佳的景觀。',
    price: 2500,
    image_url: 'https://via.placeholder.com/350x200?text=Camp+1',
    rating: 4.5,
    review_count: 120,
    status: 'active',
    has_pool: true,
    allows_pets: true,
    has_wifi: true,
    allows_barbecue: true,
    has_shower: true,
    has_toilet: true,
    has_electricity: true,
    has_parking: true
  },
  {
    id: 2,
    name: '海濱露營區',
    region: 'east',
    location: '宜蘭縣頭城鎮',
    description: '靠近海邊的露營地，可以享受海濱風光和沙灘活動。',
    price: 2200,
    image_url: 'https://via.placeholder.com/350x200?text=Camp+2',
    rating: 4.2,
    review_count: 98,
    status: 'active',
    has_pool: false,
    allows_pets: true,
    has_wifi: false,
    allows_barbecue: true,
    has_shower: true,
    has_toilet: true,
    has_electricity: true,
    has_parking: true
  },
  {
    id: 3,
    name: '森林步道營地',
    region: 'central',
    location: '南投縣魚池鄉',
    description: '被森林環繞的露營地，附近有多條步道可以探索自然。',
    price: 1800,
    image_url: 'https://via.placeholder.com/350x200?text=Camp+3',
    rating: 4.7,
    review_count: 156,
    status: 'active',
    has_pool: false,
    allows_pets: false,
    has_wifi: false,
    allows_barbecue: true,
    has_shower: true,
    has_toilet: true,
    has_electricity: false,
    has_parking: true
  }
];

export default {
  // 獲取營地列表
  getCamps(params = {}) {
    // 實際環境中，應該是真實API調用
    // return apiClient.get('/api/camps', { params });
    
    // 模擬數據和過濾
    return new Promise((resolve) => {
      setTimeout(() => {
        let filteredCamps = [...mockCamps];
        
        // 搜索過濾
        if (params.search) {
          const search = params.search.toLowerCase();
          filteredCamps = filteredCamps.filter(
            camp => camp.name.toLowerCase().includes(search) || 
                   camp.location.toLowerCase().includes(search)
          );
        }
        
        // 地區過濾
        if (params.region) {
          filteredCamps = filteredCamps.filter(camp => camp.region === params.region);
        }
        
        // 狀態過濾
        if (params.status) {
          filteredCamps = filteredCamps.filter(camp => camp.status === params.status);
        }
        
        // 分頁處理
        const page = params.page || 1;
        const limit = params.limit || 10;
        const start = (page - 1) * limit;
        const end = start + limit;
        const paginatedCamps = filteredCamps.slice(start, end);
        
        resolve({
          data: {
            camps: paginatedCamps,
            total: filteredCamps.length
          }
        });
      }, 500);
    });
  },
  
  // 根據ID獲取營地
  getCampById(id) {
    // 實際環境中，應該是真實API調用
    // return apiClient.get(`/api/camps/${id}`);
    
    // 模擬數據
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const camp = mockCamps.find(c => c.id === id);
        if (camp) {
          resolve({ data: camp });
        } else {
          reject(new Error('營地不存在'));
        }
      }, 300);
    });
  },
  
  // 創建新的營地
  createCamp(campData) {
    // 實際環境中，應該是真實API調用
    // return apiClient.post('/api/camps', campData);
    
    // 模擬創建
    return new Promise(resolve => {
      setTimeout(() => {
        const newCamp = {
          ...campData,
          id: mockCamps.length + 1,
          rating: 0,
          review_count: 0
        };
        mockCamps.push(newCamp);
        resolve({ data: newCamp });
      }, 500);
    });
  },
  
  // 更新營地
  updateCamp(id, campData) {
    // 實際環境中，應該是真實API調用
    // return apiClient.put(`/api/camps/${id}`, campData);
    
    // 模擬更新
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const index = mockCamps.findIndex(c => c.id === id);
        if (index !== -1) {
          mockCamps[index] = { ...mockCamps[index], ...campData };
          resolve({ data: mockCamps[index] });
        } else {
          reject(new Error('營地不存在'));
        }
      }, 500);
    });
  },
  
  // 更新營地狀態
  updateCampStatus(id, status) {
    // 實際環境中，應該是真實API調用
    // return apiClient.patch(`/api/camps/${id}/status`, { status });
    
    // 模擬更新狀態
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const index = mockCamps.findIndex(c => c.id === id);
        if (index !== -1) {
          mockCamps[index].status = status;
          resolve({ data: mockCamps[index] });
        } else {
          reject(new Error('營地不存在'));
        }
      }, 300);
    });
  },
  
  // 刪除營地
  deleteCamp(id) {
    // 實際環境中，應該是真實API調用
    // return apiClient.delete(`/api/camps/${id}`);
    
    // 模擬刪除
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const index = mockCamps.findIndex(c => c.id === id);
        if (index !== -1) {
          mockCamps.splice(index, 1);
          resolve({ data: { success: true } });
        } else {
          reject(new Error('營地不存在'));
        }
      }, 300);
    });
  },
  
  // 獲取所有營地 (用於選擇框)
  getAllCamps() {
    // 簡化版的營地列表，僅包含id和name
    return new Promise(resolve => {
      setTimeout(() => {
        const simpleCamps = mockCamps.map(camp => ({
          id: camp.id,
          name: camp.name
        }));
        resolve({ data: simpleCamps });
      }, 200);
    });
  }
};