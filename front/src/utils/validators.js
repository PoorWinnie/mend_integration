// validators.js - 表單驗證工具

/**
 * 檢查輸入是否為空
 * @param {string} value 輸入值
 * @returns {boolean} 是否為空
 */
export const isEmpty = (value) => {
  return value === undefined || value === null || value.trim() === '';
};

/**
 * 檢查是否為有效的電子郵件格式
 * @param {string} email 電子郵件
 * @returns {boolean} 是否有效
 */
export const isValidEmail = (email) => {
  if (isEmpty(email)) return false;
  
  // 簡單的電子郵件格式驗證正則表達式
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

/**
 * 檢查是否為有效的手機號碼格式（台灣）
 * @param {string} phone 手機號碼
 * @returns {boolean} 是否有效
 */
export const isValidPhone = (phone) => {
  if (isEmpty(phone)) return false;
  
  // 台灣手機號碼格式 (09開頭，再接8位數字)
  const phoneRegex = /^09\d{8}$/;
  return phoneRegex.test(phone);
};

/**
 * 檢查密碼強度
 * @param {string} password 密碼
 * @returns {Object} 密碼強度結果
 */
export const checkPasswordStrength = (password) => {
  if (isEmpty(password)) {
    return {
      isValid: false,
      strength: 0,
      message: '請輸入密碼'
    };
  }
  
  // 檢查長度
  const isLengthValid = password.length >= 8;
  
  // 檢查是否包含數字
  const hasNumber = /\d/.test(password);
  
  // 檢查是否包含小寫字母
  const hasLowerCase = /[a-z]/.test(password);
  
  // 檢查是否包含大寫字母
  const hasUpperCase = /[A-Z]/.test(password);
  
  // 檢查是否包含特殊符號
  const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(password);
  
  // 計算強度 (0-4)
  let strength = 0;
  if (isLengthValid) strength++;
  if (hasNumber) strength++;
  if (hasLowerCase && hasUpperCase) strength++;
  if (hasSpecialChar) strength++;
  
  // 設置提示訊息
  let message = '';
  switch (strength) {
    case 0:
      message = '密碼太弱';
      break;
    case 1:
      message = '密碼強度低';
      break;
    case 2:
      message = '密碼強度中等';
      break;
    case 3:
      message = '密碼強度良好';
      break;
    case 4:
      message = '密碼強度極佳';
      break;
    default:
      message = '';
  }
  
  return {
    isValid: isLengthValid,
    strength,
    message,
    details: {
      isLengthValid,
      hasNumber,
      hasLowerCase,
      hasUpperCase,
      hasSpecialChar
    }
  };
};

/**
 * 檢查兩個值是否相符
 * @param {string} value1 第一個值
 * @param {string} value2 第二個值
 * @returns {boolean} 是否相符
 */
export const isMatching = (value1, value2) => {
  return value1 === value2;
};

/**
 * 檢查是否為有效的網址
 * @param {string} url 網址
 * @returns {boolean} 是否有效
 */
export const isValidUrl = (url) => {
  if (isEmpty(url)) return false;
  
  try {
    new URL(url);
    return true;
  } catch (error) {
    return false;
  }
};

/**
 * 檢查是否為有效的評價星級 (1-5)
 * @param {number} rating 評價星級
 * @returns {boolean} 是否有效
 */
export const isValidRating = (rating) => {
  return Number.isInteger(rating) && rating >= 1 && rating <= 5;
};

/**
 * 驗證評價數據
 * @param {Object} reviewData 評價數據
 * @returns {Object} 驗證結果
 */
export const validateReview = (reviewData) => {
  const errors = {};
  
  // 檢查評價標題
  if (isEmpty(reviewData.title)) {
    errors.title = '請輸入評價標題';
  } else if (reviewData.title.length < 3) {
    errors.title = '標題至少需要3個字元';
  } else if (reviewData.title.length > 100) {
    errors.title = '標題不能超過100個字元';
  }
  
  // 檢查評價內容
  if (isEmpty(reviewData.content)) {
    errors.content = '請輸入評價內容';
  } else if (reviewData.content.length < 10) {
    errors.content = '評價內容至少需要10個字元';
  } else if (reviewData.content.length > 1000) {
    errors.content = '評價內容不能超過1000個字元';
  }
  
  // 檢查評分
  if (!reviewData.rating || !isValidRating(reviewData.rating)) {
    errors.rating = '請給予1到5星的評分';
  }
  
  // 檢查詳細評分（如果提供）
  if (reviewData.cleanliness !== undefined && !isValidRating(reviewData.cleanliness)) {
    errors.cleanliness = '清潔度評分必須是1到5星';
  }
  
  if (reviewData.convenience !== undefined && !isValidRating(reviewData.convenience)) {
    errors.convenience = '便利性評分必須是1到5星';
  }
  
  if (reviewData.friendliness !== undefined && !isValidRating(reviewData.friendliness)) {
    errors.friendliness = '友善度評分必須是1到5星';
  }
  
  // 檢查營地ID
  if (!reviewData.campId) {
    errors.campId = '請選擇要評價的營地';
  }
  
  // 檢查圖片（如果提供）
  if (reviewData.images) {
    if (reviewData.images.length > 5) {
      errors.images = '最多只能上傳5張圖片';
    }
  }
  
  return {
    isValid: Object.keys(errors).length === 0,
    errors
  };
};

/**
 * 驗證回覆內容
 * @param {string} replyText 回覆內容
 * @returns {Object} 驗證結果
 */
export const validateReply = (replyText) => {
  if (isEmpty(replyText)) {
    return {
      isValid: false,
      message: '請輸入回覆內容'
    };
  }
  
  if (replyText.length < 5) {
    return {
      isValid: false,
      message: '回覆內容至少需要5個字元'
    };
  }
  
  if (replyText.length > 500) {
    return {
      isValid: false,
      message: '回覆內容不能超過500個字元'
    };
  }
  
  return {
    isValid: true,
    message: ''
  };
};

/**
 * 驗證檢舉資料
 * @param {Object} reportData 檢舉資料
 * @returns {Object} 驗證結果
 */
export const validateReport = (reportData) => {
  const errors = {};
  
  // 檢查檢舉原因
  if (isEmpty(reportData.reason)) {
    errors.reason = '請選擇檢舉原因';
  }
  
  // 檢查詳細說明
  if (reportData.detail && reportData.detail.length > 500) {
    errors.detail = '詳細說明不能超過500個字元';
  }
  
  return {
    isValid: Object.keys(errors).length === 0,
    errors
  };
};

export default {
  isEmpty,
  isValidEmail,
  isValidPhone,
  checkPasswordStrength,
  isMatching,
  isValidUrl,
  isValidRating,
  validateReview,
  validateReply,
  validateReport
};