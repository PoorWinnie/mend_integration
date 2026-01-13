// src/services/reportService.js
import axios from 'axios';
import { validateReport } from '@/utils/validators';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

/**
 * 檢舉評價
 * @param {Object} reportData 檢舉數據
 * @returns {Promise<Object>} 檢舉結果
 */
export const reportReview = async (reportData) => {
  // 驗證檢舉數據
  const validation = validateReport(reportData);
  if (!validation.isValid) {
    throw new Error(Object.values(validation.errors)[0]);
  }
  
  try {
    // 實際環境中應該呼叫真實 API
    // const response = await axios.post(`${API_BASE_URL}/api/review-reports/review`, reportData);
    
    // 模擬檢舉提交
    await new Promise(resolve => setTimeout(resolve, 600));
    
    return { 
      success: true,
      message: '檢舉已提交，管理員將會審核'
    };
  } catch (error) {
    console.error('檢舉評價失敗:', error);
    throw error;
  }
};

/**
 * 檢舉回覆
 * @param {Object} reportData 檢舉數據
 * @returns {Promise<Object>} 檢舉結果
 */
export const reportReply = async (reportData) => {
  // 驗證檢舉數據
  const validation = validateReport(reportData);
  if (!validation.isValid) {
    throw new Error(Object.values(validation.errors)[0]);
  }
  
  try {
    // 實際環境中應該呼叫真實 API
    // const response = await axios.post(`${API_BASE_URL}/api/review-reports/reply`, reportData);
    
    // 模擬檢舉提交
    await new Promise(resolve => setTimeout(resolve, 600));
    
    return { 
      success: true,
      message: '檢舉已提交，管理員將會審核'
    };
  } catch (error) {
    console.error('檢舉回覆失敗:', error);
    throw error;
  }
};

/**
 * 檢查使用者是否已檢舉評價
 * @param {number} userId 用戶ID
 * @param {number} reviewId 評價ID
 * @returns {Promise<boolean>} 是否已檢舉
 */
export const hasUserReportedReview = async (userId, reviewId) => {
  try {
    // 實際環境中應該呼叫真實 API
    // const response = await axios.get(`${API_BASE_URL}/api/review-reports/check`, {
    //   params: { userId, reviewId }
    // });
    
    // 模擬檢查結果
    await new Promise(resolve => setTimeout(resolve, 300));
    
    return Math.random() < 0.3; // 30%機率已檢舉
  } catch (error) {
    console.error('檢查檢舉狀態失敗:', error);
    throw error;
  }
};

/**
 * 獲取待處理的檢舉列表
 * @param {string} target 檢舉目標類型 ('review' 或 'reply')
 * @returns {Promise<Array>} 檢舉列表
 */
export const getPendingReports = async (target = null) => {
  try {
    // 實際環境中應該呼叫真實 API
    // const params = target ? { target } : {};
    // const response = await axios.get(`${API_BASE_URL}/api/review-reports/pending`, { params });
    
    // 模擬獲取待處理檢舉
    await new Promise(resolve => setTimeout(resolve, 800));
    
    // 模擬檢舉列表
    const mockReports = [];
    const reportCount = Math.floor(Math.random() * 8) + 2; // 2-10條檢舉
    
    const reportReasons = [
      '不實內容',
      '廣告垃圾內容',
      '冒犯性言論',
      '不當或敏感內容',
      '侵犯隱私權',
      '其他原因'
    ];
    
    for (let i = 0; i < reportCount; i++) {
      const isReviewReport = !target || target === 'review' || (target === 'all' && Math.random() < 0.7);
      
      const mockReport = {
        id: 1000 + i,
        target: isReviewReport ? 'review' : 'reply',
        targetId: 2000 + i,
        reason: reportReasons[Math.floor(Math.random() * reportReasons.length)],
        detail: Math.random() < 0.7 ? '這則評價/回覆包含不當內容，請審核。' : '',
        reporterId: 3000 + i,
        reporterName: `用戶${3000 + i}`,
        reportedAt: new Date(Date.now() - Math.floor(Math.random() * 7 * 24 * 60 * 60 * 1000)).toISOString(),
        status: 'pending',
        targetContent: isReviewReport 
          ? '這是一則可能含有不當內容的評價，需要管理員審核。'
          : '這是一則可能含有不當內容的回覆，需要管理員審核。',
        targetAuthor: `用戶${4000 + i}`
      };
      
      mockReports.push(mockReport);
    }
    
    return mockReports;
  } catch (error) {
    console.error('獲取待處理檢舉失敗:', error);
    throw error;
  }
};

/**
 * 處理檢舉 (駁回)
 * @param {number} reportId 檢舉ID
 * @param {string} handlerNote 處理備註
 * @returns {Promise<Object>} 處理結果
 */
export const rejectReport = async (reportId, handlerNote) => {
  try {
    // 實際環境中應該呼叫真實 API
    // const response = await axios.put(`${API_BASE_URL}/api/review-reports/${reportId}/process`, {
    //   status: 'rejected',
    //   handlerNote
    // });
    
    // 模擬處理檢舉
    await new Promise(resolve => setTimeout(resolve, 600));
    
    return { 
      success: true,
      message: '檢舉已駁回'
    };
  } catch (error) {
    console.error(`駁回檢舉 ${reportId} 失敗:`, error);
    throw error;
  }
};

/**
 * 處理檢舉 (批准並移除內容)
 * @param {number} reportId 檢舉ID
 * @param {string} handlerNote 處理備註
 * @returns {Promise<Object>} 處理結果
 */
export const approveAndRemoveReport = async (reportId, handlerNote) => {
  try {
    // 實際環境中應該呼叫真實 API
    // const response = await axios.put(`${API_BASE_URL}/api/review-reports/${reportId}/approve-and-remove`, {
    //   handlerNote
    // });
    
    // 模擬處理檢舉
    await new Promise(resolve => setTimeout(resolve, 600));
    
    return { 
      success: true,
      message: '檢舉已批准，內容已移除'
    };
  } catch (error) {
    console.error(`批准檢舉 ${reportId} 並移除內容失敗:`, error);
    throw error;
  }
};

/**
 * 獲取檢舉類型名稱
 * @param {string} reportType 檢舉類型
 * @returns {string} 檢舉類型名稱
 */
export const getReportTypeName = (reportType) => {
  const types = {
    'fake_content': '不實內容',
    'spam': '廣告垃圾內容',
    'offensive': '冒犯性言論',
    'inappropriate': '不當或敏感內容',
    'privacy': '侵犯隱私權',
    'other': '其他原因'
  };
  
  return types[reportType] || '未知原因';
};

export default {
  reportReview,
  reportReply,
  hasUserReportedReview,
  getPendingReports,
  rejectReport,
  approveAndRemoveReport,
  getReportTypeName
};