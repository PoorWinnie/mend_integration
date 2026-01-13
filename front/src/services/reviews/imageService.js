// src/services/imageService.js
import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

/**
 * 上傳單個圖片
 * @param {File} file 圖片文件
 * @param {number} reviewId 評價ID (可選)
 * @returns {Promise<Object>} 上傳結果
 */
export const uploadImage = async (file, reviewId = null) => {
  try {
    // 檢查文件類型
    if (!file.type.startsWith('image/')) {
      throw new Error('只能上傳圖片檔案');
    }
    
    // 檢查檔案大小 (限制 5MB)
    const maxSize = 5 * 1024 * 1024; // 5MB
    if (file.size > maxSize) {
      throw new Error('圖片大小不能超過 5MB');
    }
    
    const formData = new FormData();
    formData.append('file', file);
    
    if (reviewId) {
      formData.append('reviewId', reviewId);
    }
    
    // 實際環境中應該發送到真實 API
    // const response = await axios.post(`${API_BASE_URL}/api/review-images/upload`, formData, {
    //   headers: {
    //     'Content-Type': 'multipart/form-data'
    //   }
    // });
    
    // 模擬上傳成功
    await new Promise(resolve => setTimeout(resolve, 800));
    
    // 生成隨機URL作為模擬結果
    const mockImageUrl = `https://via.placeholder.com/350x200?text=Uploaded_Image_${Math.floor(Math.random() * 1000)}`;
    
    return {
      success: true,
      imageUrl: mockImageUrl,
      id: Math.floor(Math.random() * 1000),
      fileName: file.name
    };
  } catch (error) {
    console.error('上傳圖片失敗:', error);
    throw error;
  }
};

/**
 * 批量上傳多個圖片
 * @param {Array<File>} files 圖片文件數組
 * @param {number} reviewId 評價ID (可選)
 * @returns {Promise<Array<Object>>} 上傳結果數組
 */
export const uploadMultipleImages = async (files, reviewId = null) => {
  try {
    // 驗證文件數量不超過限制
    const maxCount = 5;
    if (files.length > maxCount) {
      throw new Error(`最多只能上傳 ${maxCount} 張圖片`);
    }
    
    // 創建上傳任務數組
    const uploadTasks = Array.from(files).map(file => uploadImage(file, reviewId));
    
    // 並行上傳所有圖片
    const results = await Promise.all(uploadTasks);
    return results;
  } catch (error) {
    console.error('批量上傳圖片失敗:', error);
    throw error;
  }
};

/**
 * 檢查文件是否為有效的圖片類型
 * @param {File} file 文件
 * @returns {boolean} 是否為有效圖片
 */
export const isValidImageFile = (file) => {
  const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
  return validTypes.includes(file.type);
};

/**
 * 獲取支持的圖片類型描述
 * @returns {string} 類型描述
 */
export const getSupportedImageTypes = () => {
  return 'JPG, PNG, GIF, WEBP';
};

/**
 * 將文件轉換為數據 URL (Base64)
 * @param {File} file 文件
 * @returns {Promise<string>} 數據 URL
 */
export const fileToDataUrl = (file) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(reader.result);
    reader.onerror = (error) => reject(error);
    reader.readAsDataURL(file);
  });
};

/**
 * 獲取評價的所有圖片
 * @param {number} reviewId 評價ID
 * @returns {Promise<Array<Object>>} 圖片數據數組
 */
export const getReviewImages = async (reviewId) => {
  try {
    // 實際環境中應該呼叫真實 API
    // const response = await axios.get(`${API_BASE_URL}/api/review-images/review/${reviewId}`);
    
    // 模擬獲取評價圖片
    await new Promise(resolve => setTimeout(resolve, 500));
    
    // 模擬結果
    const mockImages = [];
    const imageCount = Math.floor(Math.random() * 5) + 1; // 1-5張圖片
    
    for (let i = 0; i < imageCount; i++) {
      mockImages.push({
        id: 1000 + i,
        url: `https://via.placeholder.com/350x200?text=Review_Image_${i}`,
        fileName: `image_${i}.jpg`
      });
    }
    
    return mockImages;
  } catch (error) {
    console.error(`獲取評價 ${reviewId} 的圖片失敗:`, error);
    throw error;
  }
};

/**
 * 刪除圖片
 * @param {number} imageId 圖片ID
 * @returns {Promise<void>}
 */
export const deleteImage = async (imageId) => {
  try {
    // 實際環境中應該呼叫真實 API
    // await axios.delete(`${API_BASE_URL}/api/review-images/${imageId}`);
    
    // 模擬刪除圖片
    await new Promise(resolve => setTimeout(resolve, 500));
    
    return { success: true };
  } catch (error) {
    console.error(`刪除圖片 ${imageId} 失敗:`, error);
    throw error;
  }
};

/**
 * 處理圖片 URL，確保可以正確顯示
 * @param {string} url 圖片 URL
 * @returns {string} 處理後的 URL
 */
export const processImageUrl = (url) => {
  if (!url) return '';
  
  // 如果已經是完整 URL
  if (url.startsWith('http')) {
    return url;
  }
  
  // 如果是相對路徑，確保路徑正確
  return url.startsWith('/') ? `${API_BASE_URL}${url}` : `${API_BASE_URL}/${url}`;
};

export default {
  uploadImage,
  uploadMultipleImages,
  getReviewImages,
  deleteImage,
  isValidImageFile,
  getSupportedImageTypes,
  fileToDataUrl,
  processImageUrl
};