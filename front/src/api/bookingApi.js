// bookingApi.js
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
const mockBookings = [
  {
    id: 1,
    booking_number: 'BK-2023-0001',
    username: '張小明',
    email: 'chang@example.com',
    camp_name: '山林露營地',
    camp_image: 'https://via.placeholder.com/350x200?text=Camp+1',
    camp_location: '新北市三峽區',
    camp_price: 2500,
    check_in_date: '2023-08-15',
    check_out_date: '2023-08-17',
    days: 2,
    guests: 4,
    total_price: 5000,
    status: 'confirmed',
    created_at: '2023-08-01T10:15:30',
    phone: '0912-345-678',
    note: '需要靠近廁所的營位'
  },
  {
    id: 2,
    booking_number: 'BK-2023-0002',
    username: '李大華',
    email: 'lee@example.com',
    camp_name: '海濱露營區',
    camp_image: 'https://via.placeholder.com/350x200?text=Camp+2',
    camp_location: '宜蘭縣頭城鎮',
    camp_price: 2200,
    check_in_date: '2023-08-20',
    check_out_date: '2023-08-22',
    days: 2,
    guests: 2,
    total_price: 4400,
    status: 'pending',
    created_at: '2023-08-05T14:22:45',
    phone: '0923-456-789',
    note: ''
  },
  {
    id: 3,
    booking_number: 'BK-2023-0003',
    username: '王美玲',
    email: 'wang@example.com',
    camp_name: '森林步道營地',
    camp_image: 'https://via.placeholder.com/350x200?text=Camp+3',
    camp_location: '南投縣魚池鄉',
    camp_price: 1800,
    check_in_date: '2023-08-25',
    check_out_date: '2023-08-28',
    days: 3,
    guests: 6,
    total_price: 5400,
    status: 'completed',
    created_at: '2023-08-10T09:05:15',
    phone: '0934-567-890',
    note: '需要額外的木柴'
  }
];

export default {
  // 獲取預約列表
  getBookings(params = {}) {
    // 實際環境中，應該是真實API調用
    // return apiClient.get('/api/bookings', { params });
    
    // 模擬數據和過濾
    return new Promise((resolve) => {
      setTimeout(() => {
        let filteredBookings = [...mockBookings];
        
        // 搜索過濾
        if (params.search) {
          const search = params.search.toLowerCase();
          filteredBookings = filteredBookings.filter(
            booking => booking.booking_number.toLowerCase().includes(search) || 
                      booking.username.toLowerCase().includes(search) ||
                      booking.email.toLowerCase().includes(search)
          );
        }
        
        // 狀態過濾
        if (params.status) {
          filteredBookings = filteredBookings.filter(booking => booking.status === params.status);
        }
        
        // 營地過濾
        if (params.camp_id) {
          filteredBookings = filteredBookings.filter(
            booking => booking.camp_id === params.camp_id
          );
        }
        
        // 日期範圍過濾
        if (params.start_date) {
          filteredBookings = filteredBookings.filter(
            booking => new Date(booking.check_in_date) >= new Date(params.start_date)
          );
        }
        
        if (params.end_date) {
          filteredBookings = filteredBookings.filter(
            booking => new Date(booking.check_in_date) <= new Date(params.end_date)
          );
        }
        
        // 分頁處理
        const page = params.page || 1;
        const limit = params.limit || 10;
        const start = (page - 1) * limit;
        const end = start + limit;
        const paginatedBookings = filteredBookings.slice(start, end);
        
        resolve({
          data: {
            bookings: paginatedBookings,
            total: filteredBookings.length
          }
        });
      }, 500);
    });
  },
  
  // 根據ID獲取預約
  getBookingById(id) {
    // 實際環境中，應該是真實API調用
    // return apiClient.get(`/api/bookings/${id}`);
    
    // 模擬數據
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const booking = mockBookings.find(b => b.id === id);
        if (booking) {
          resolve({ data: booking });
        } else {
          reject(new Error('預約不存在'));
        }
      }, 300);
    });
  },
  
  // 更新預約狀態
  updateBookingStatus(id, status) {
    // 實際環境中，應該是真實API調用
    // return apiClient.patch(`/api/bookings/${id}/status`, { status });
    
    // 模擬更新狀態
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const index = mockBookings.findIndex(b => b.id === id);
        if (index !== -1) {
          mockBookings[index].status = status;
          resolve({ data: mockBookings[index] });
        } else {
          reject(new Error('預約不存在'));
        }
      }, 300);
    });
  },
  
  // 創建新的預約
  createBooking(bookingData) {
    // 實際環境中，應該是真實API調用
    // return apiClient.post('/api/bookings', bookingData);
    
    // 模擬創建
    return new Promise(resolve => {
      setTimeout(() => {
        const newBooking = {
          ...bookingData,
          id: mockBookings.length + 1,
          booking_number: `BK-2023-000${mockBookings.length + 1}`,
          created_at: new Date().toISOString()
        };
        mockBookings.push(newBooking);
        resolve({ data: newBooking });
      }, 500);
    });
  },
  
  // 更新預約
  updateBooking(id, bookingData) {
    // 實際環境中，應該是真實API調用
    // return apiClient.put(`/api/bookings/${id}`, bookingData);
    
    // 模擬更新
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const index = mockBookings.findIndex(b => b.id === id);
        if (index !== -1) {
          mockBookings[index] = { ...mockBookings[index], ...bookingData };
          resolve({ data: mockBookings[index] });
        } else {
          reject(new Error('預約不存在'));
        }
      }, 500);
    });
  },
  
  // 刪除預約
  deleteBooking(id) {
    // 實際環境中，應該是真實API調用
    // return apiClient.delete(`/api/bookings/${id}`);
    
    // 模擬刪除
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const index = mockBookings.findIndex(b => b.id === id);
        if (index !== -1) {
          mockBookings.splice(index, 1);
          resolve({ data: { success: true } });
        } else {
          reject(new Error('預約不存在'));
        }
      }, 300);
    });
  }
};