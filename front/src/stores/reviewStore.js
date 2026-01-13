// src/stores/reviewStore.js
import { defineStore } from 'pinia';
import reviewApi from '@/api/reviewApi';

export const useReviewStore = defineStore('review', {
  state: () => ({
    reviews: [],
    currentReview: null,
    totalReviews: 0,
    ratingStats: {
      1: 0,
      2: 0,
      3: 0,
      4: 0,
      5: 0
    },
    loading: false,
    error: null,
    filters: {
      keyword: '',
      minRating: 0,
      sortBy: 'createdAt', 
      direction: 'DESC',
      campId: null
    }
  }),

  getters: {
    hasReviews: (state) => state.reviews.length > 0,
    averageRating: (state) => {
      if (state.totalReviews === 0) return 0;
      
      let totalScore = 0;
      Object.keys(state.ratingStats).forEach(key => {
        totalScore += Number(key) * state.ratingStats[key];
      });
      
      return totalScore / state.totalReviews;
    }
  },

  actions: {
    async fetchReviews(params = {}) {
      this.loading = true;
      this.error = null;
      
      try {
        // 合併過濾條件和參數
        const queryParams = {
          ...this.filters,
          ...params
        };
        
        const result = await reviewApi.getReviews(queryParams);
        
        this.reviews = result.data.reviews || [];
        this.totalReviews = result.data.total || 0;
        
        if (result.data.ratingStats) {
          this.ratingStats = result.data.ratingStats;
        }
        
        return result.data;
      } catch (error) {
        this.error = error.message || '無法載入評價';
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async fetchReviewById(id, userId = null) {
      this.loading = true;
      this.error = null;
      
      try {
        const review = await reviewApi.getReviewById(id);
        this.currentReview = review.data;
        return review.data;
      } catch (error) {
        this.error = error.message || `無法載入評價 ID: ${id}`;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async createReview(reviewData) {
      this.loading = true;
      this.error = null;
      
      try {
        const newReview = await reviewApi.createReview(reviewData);
        
        // 更新本地資料
        this.reviews = [newReview.data, ...this.reviews];
        this.totalReviews++;
        
        // 更新評分統計
        const rating = newReview.data.overallRating || newReview.data.rating;
        if (rating && this.ratingStats[rating]) {
          this.ratingStats[rating]++;
        }
        
        return newReview.data;
      } catch (error) {
        this.error = error.message || '創建評價失敗';
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async updateReview(id, reviewData) {
      this.loading = true;
      this.error = null;
      
      try {
        const updatedReview = await reviewApi.updateReview(id, reviewData);
        
        // 更新本地資料
        const index = this.reviews.findIndex(r => r.id === id);
        if (index !== -1) {
          this.reviews[index] = updatedReview.data;
        }
        
        return updatedReview.data;
      } catch (error) {
        this.error = error.message || `更新評價失敗 ID: ${id}`;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async deleteReview(id) {
      this.loading = true;
      this.error = null;
      
      try {
        await reviewApi.deleteReview(id);
        
        // 更新本地資料
        const review = this.reviews.find(r => r.id === id);
        if (review) {
          const rating = review.overallRating || review.rating;
          if (rating && this.ratingStats[rating]) {
            this.ratingStats[rating]--;
          }
          
          this.reviews = this.reviews.filter(r => r.id !== id);
          this.totalReviews--;
        }
        
        return true;
      } catch (error) {
        this.error = error.message || `刪除評價失敗 ID: ${id}`;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async replyToReview(id, replyText) {
      this.loading = true;
      this.error = null;
      
      try {
        const updatedReview = await reviewApi.replyToReview(id, replyText);
        
        // 更新本地資料
        const index = this.reviews.findIndex(r => r.id === id);
        if (index !== -1) {
          this.reviews[index] = {
            ...this.reviews[index],
            has_reply: true,
            reply_content: replyText,
            reply_by: '系統管理員',
            replied_at: new Date().toISOString()
          };
        }
        
        return updatedReview.data;
      } catch (error) {
        this.error = error.message || `回覆評價失敗 ID: ${id}`;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async dismissReport(id) {
      this.loading = true;
      this.error = null;
      
      try {
        const result = await reviewApi.dismissReport(id);
        
        // 更新本地資料
        const index = this.reviews.findIndex(r => r.id === id);
        if (index !== -1) {
          this.reviews[index] = {
            ...this.reviews[index],
            is_reported: false,
            reporter_name: null,
            reported_at: null,
            report_reason: null
          };
        }
        
        return result.data;
      } catch (error) {
        this.error = error.message || `駁回檢舉失敗 ID: ${id}`;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    setFilters(newFilters) {
      this.filters = {
        ...this.filters,
        ...newFilters
      };
    },
    
    resetFilters() {
      this.filters = {
        keyword: '',
        minRating: 0,
        sortBy: 'createdAt',
        direction: 'DESC',
        campId: null
      };
    }
  }
});