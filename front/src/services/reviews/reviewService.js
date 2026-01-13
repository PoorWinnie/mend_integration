// src/services/reviewService.js
import reviewApi from '@/api/reviewApi';
import { validateReview, validateReply, validateReport } from '@/utils/validators';

/**
 * 獲取評價列表
 * @param {Object} params 查詢參數
 * @returns {Promise<Object>} 評價列表數據
 */
export const getReviews = async (params = {}) => {
  try {
    const response = await reviewApi.getReviews(params);
    return response.data;
  } catch (error) {
    console.error('獲取評價失敗:', error);
    throw error;
  }
};

/**
 * 獲取單個評價
 * @param {number} id 評價ID
 * @returns {Promise<Object>} 評價數據
 */
export const getReview = async (id) => {
  try {
    const response = await reviewApi.getReviewById(id);
    return response.data;
  } catch (error) {
    console.error(`獲取評價 ${id} 失敗:`, error);
    throw error;
  }
};

/**
 * 創建新評價
 * @param {Object} reviewData 評價數據
 * @returns {Promise<Object>} 創建的評價
 */
export const createReview = async (reviewData) => {
  // 首先驗證評價數據
  const validation = validateReview(reviewData);
  if (!validation.isValid) {
    throw new Error(Object.values(validation.errors)[0]);
  }
  
  try {
    const response = await reviewApi.createReview(reviewData);
    return response.data;
  } catch (error) {
    console.error('創建評價失敗:', error);
    throw error;
  }
};

/**
 * 更新評價
 * @param {number} id 評價ID
 * @param {Object} reviewData 更新的評價數據
 * @returns {Promise<Object>} 更新後的評價
 */
export const updateReview = async (id, reviewData) => {
  // 驗證更新的數據
  const validation = validateReview(reviewData);
  if (!validation.isValid) {
    throw new Error(Object.values(validation.errors)[0]);
  }
  
  try {
    const response = await reviewApi.updateReview(id, reviewData);
    return response.data;
  } catch (error) {
    console.error(`更新評價 ${id} 失敗:`, error);
    throw error;
  }
};

/**
 * 刪除評價
 * @param {number} id 評價ID
 * @returns {Promise<boolean>} 是否成功刪除
 */
export const deleteReview = async (id) => {
  try {
    await reviewApi.deleteReview(id);
    return true;
  } catch (error) {
    console.error(`刪除評價 ${id} 失敗:`, error);
    throw error;
  }
};

/**
 * 回覆評價
 * @param {number} id 評價ID
 * @param {string} replyText 回覆內容
 * @returns {Promise<Object>} 更新後的評價
 */
export const replyToReview = async (id, replyText) => {
  // 驗證回覆內容
  const validation = validateReply(replyText);
  if (!validation.isValid) {
    throw new Error(validation.message);
  }
  
  try {
    const response = await reviewApi.replyToReview(id, replyText);
    return response.data;
  } catch (error) {
    console.error(`回覆評價 ${id} 失敗:`, error);
    throw error;
  }
};

/**
 * 檢舉評價
 * @param {number} reviewId 評價ID
 * @param {Object} reportData 檢舉數據
 * @returns {Promise<Object>} 檢舉結果
 */
export const reportReview = async (reviewId, reportData) => {
  // 驗證檢舉數據
  const validation = validateReport(reportData);
  if (!validation.isValid) {
    throw new Error(Object.values(validation.errors)[0]);
  }
  
  try {
    const response = await reviewApi.reportReview(reviewId, reportData);
    return response.data;
  } catch (error) {
    console.error(`檢舉評價 ${reviewId} 失敗:`, error);
    throw error;
  }
};

/**
 * 切換評價的點讚狀態
 * @param {number} reviewId 評價ID
 * @param {number} userId 用戶ID
 * @returns {Promise<Object>} 點讚結果
 */
export const toggleLike = async (reviewId, userId) => {
  try {
    const response = await reviewApi.toggleLike(reviewId, userId);
    return response.data;
  } catch (error) {
    console.error(`點讚評價 ${reviewId} 失敗:`, error);
    throw error;
  }
};

/**
 * 取得評價分析資料
 * @param {number} campId 營地ID (可選)
 * @returns {Promise<Object>} 評價分析資料
 */
export const getReviewAnalytics = async (campId) => {
  try {
    // 這裡可以呼叫實際的API
    // 但這裡我們使用模擬資料
    const ratings = [5, 4, 3, 2, 1];
    const ratingCounts = { 5: 35, 4: 28, 3: 15, 2: 10, 1: 6 };
    const totalReviews = Object.values(ratingCounts).reduce((sum, count) => sum + count, 0);
    
    // 計算平均分數
    let weightedSum = 0;
    for (const rating of ratings) {
      weightedSum += rating * ratingCounts[rating];
    }
    const averageRating = totalReviews > 0 ? weightedSum / totalReviews : 0;
    
    // 計算百分比
    const ratingPercentages = {};
    for (const rating of ratings) {
      ratingPercentages[rating] = totalReviews > 0 
        ? Math.round((ratingCounts[rating] / totalReviews) * 100) 
        : 0;
    }
    
    return {
      totalReviews,
      averageRating,
      ratingCounts,
      ratingPercentages
    };
  } catch (error) {
    console.error('獲取評價分析失敗:', error);
    throw error;
  }
};

export default {
  getReviews,
  getReview,
  createReview,
  updateReview,
  deleteReview,
  replyToReview,
  reportReview,
  toggleLike,
  getReviewAnalytics
};