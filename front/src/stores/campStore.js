// src/stores/campStore.js
import { defineStore } from 'pinia';

export const useCampStore = defineStore('camp', {
  state: () => ({
    camps: [],
    popularCamps: [],
    currentCamp: null,
    loading: false,
    error: null,
    isUserLoggedIn: false // 模擬登入狀態，實際應從認證服務獲取
  }),

  actions: {
    async fetchPopularCamps() {
      this.loading = true;
      
      try {
        // 模擬 API 延遲
        await new Promise(resolve => setTimeout(resolve, 500));
        
        // 模擬熱門營地數據
        const popularCamps = [
          {
            id: 1,
            name: '山林露營地',
            location: '新北市三峽區',
            image: 'https://via.placeholder.com/350x200?text=Camp+1',
            rating: 4.8,
            reviewCount: 120
          },
          {
            id: 2,
            name: '海濱露營區',
            location: '宜蘭縣頭城鎮',
            image: 'https://via.placeholder.com/350x200?text=Camp+2',
            rating: 4.2,
            reviewCount: 85
          },
          {
            id: 3,
            name: '森林步道營地',
            location: '南投縣魚池鄉',
            image: 'https://via.placeholder.com/350x200?text=Camp+3',
            rating: 4.5,
            reviewCount: 62
          }
        ];
        
        this.popularCamps = popularCamps;
        this.loading = false;
        return popularCamps;
      } catch (error) {
        this.error = error.message || '載入熱門營地失敗';
        this.loading = false;
        throw error;
      }
    }
  }
});