<template>
  <div class="filter-sidebar">
    <h4 class="filter-title">篩選器</h4>
    
    <!-- 搜索框 -->
    <div class="filter-section">
      <div class="filter-label">搜尋產品</div>
      <div class="input-group mb-3">
        <input 
          type="text" 
          class="form-control" 
          placeholder="輸入產品名稱..." 
          v-model="filterData.name"
          @input="debounceSearch"
        >
        <button class="btn btn-outline-secondary" type="button" @click="applyFilters">
          <i class="fas fa-search"></i>
        </button>
      </div>
    </div>
    
    <div class="filter-divider"></div>
    
    <!-- 庫存 -->
    <div class="filter-section d-flex align-items-center mb-3">
      <span class="fw-bold me-2">僅顯示有庫存</span>
      <div class="form-check form-switch m-0">
        <input 
          class="form-check-input" 
          type="checkbox" 
          v-model="filterData.inStockOnly" 
          @change="applyFilters"
        >
      </div>
    </div>
    
    <div class="filter-divider"></div>
    
    <!-- 分類 -->
    <div class="filter-section">
      <div class="filter-label">分類</div>
      <div v-if="categories.length">
        <div class="category-buttons">
          <button 
            class="btn btn-outline-secondary btn-sm w-100 text-start mb-1"
            :class="{active: filterData.productTypeId === null}"
            @click="selectCategory(null)"
          >
            全部分類
          </button>
          <button 
            v-for="productcate in categories" 
            :key="productcate.id"
            class="btn btn-outline-secondary btn-sm w-100 text-start mb-1"
            :class="{active: filterData.productTypeId === productcate.id}"
            @click="selectCategory(productcate.id)"
          >
            {{ productcate.typeName }}
          </button>
        </div>
      </div>
      <div v-else class="text-muted">載入中...</div>
    </div>
    
    <div class="filter-divider"></div>
    
    <!-- 價格 -->
    <div class="filter-section">
      <div class="filter-label">價格</div>
      <div class="mb-2">
        <button 
          v-for="range in priceRanges" 
          :key="range.label"
          class="btn btn-outline-secondary btn-sm w-100 text-start mb-1 filter-range-btn"
          :class="{active: isPriceRangeActive(range)}"
          @click="selectPriceRange(range)"
        >
          {{ range.label }}
        </button>
      </div>
      <div class="mb-3">
        <div class="d-flex align-items-center">
          <span class="me-1">$</span>
          <input 
            type="number" 
            class="form-control filter-minmax me-1" 
            v-model.number="filterData.minPrice" 
            placeholder="min"
          >
          <span class="mx-1">到</span>
          <span class="me-1">$</span>
          <input 
            type="number" 
            class="form-control filter-minmax" 
            v-model.number="filterData.maxPrice" 
            placeholder="max"
          >
        </div>
        <button class="btn btn-outline-secondary btn-sm mt-2 w-100" @click="applyFilters">
          應用價格範圍
        </button>
      </div>
    </div>
    
    <div class="filter-divider"></div>
    
    <!-- 排序 -->
    <div class="filter-section">
      <div class="filter-label">排序方式</div>
      <select class="form-select mb-2" v-model="filterData.sortBy" @change="applyFilters">
        <option value="productId">預設</option>
        <option value="price">價格</option>
        <option value="name">名稱</option>
        <option value="createdAt">上架時間</option>
      </select>
      <div class="form-check form-check-inline">
        <input 
          class="form-check-input" 
          type="radio" 
          id="sortAsc" 
          :value="false" 
          v-model="filterData.sortDesc"
          @change="applyFilters"
        >
        <label class="form-check-label" for="sortAsc">升序</label>
      </div>
      <div class="form-check form-check-inline">
        <input 
          class="form-check-input" 
          type="radio" 
          id="sortDesc" 
          :value="true" 
          v-model="filterData.sortDesc"
          @change="applyFilters"
        >
        <label class="form-check-label" for="sortDesc">降序</label>
      </div>
    </div>
    
    <div class="filter-divider"></div>
    
    <!-- 重置按鈕 -->
    <button class="btn btn-primary w-100" @click="resetFilters">
      重置所有篩選
    </button>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import axiosapi from '@/plugins/axios.js';

const categories = ref([]);
const priceRanges = [
  { label: '$0 - $500', min: 0, max: 500 },
  { label: '$500 - $1000', min: 500, max: 1000 },
  { label: '$1000 - $1500', min: 1000, max: 1500 },
  { label: '$1500+', min: 1500, max: null }
];

// 創建篩選數據對象
const filterData = reactive({
  name: '',
  productTypeId: null,
  minPrice: '',
  maxPrice: '',
  inStockOnly: false,
  sortBy: 'productId',
  sortDesc: false
});

const emit = defineEmits(['filter-changed', 'reset-filters']);

// 加載分類數據
onMounted(async () => {
  try {
    const response = await axiosapi.get('/ajax/product-types');
    if (response.status === 200) {
      categories.value = response.data.list || [];
    }
  } catch (error) {
    console.error('獲取分類失敗:', error);
  }
});

// 創建一個debounce函數用於處理搜索輸入
let searchTimeout = null;
function debounceSearch() {
  clearTimeout(searchTimeout);
  searchTimeout = setTimeout(() => {
    applyFilters();
  }, 500);
}

// 選擇類別
function selectCategory(categoryId) {
  filterData.productTypeId = categoryId;
  applyFilters();
}

// 選擇價格範圍
function selectPriceRange(range) {
  filterData.minPrice = range.min;
  filterData.maxPrice = range.max;
  applyFilters();
}

// 檢查價格範圍是否激活
function isPriceRangeActive(range) {
  return filterData.minPrice === range.min && filterData.maxPrice === range.max;
}

// 應用所有篩選
function applyFilters() {
  // 創建一個符合後端API期望的JSON請求對象
  const jsonRequest = {
    start: 0,
    rows: 1000,  // 大數值以獲取所有結果
    sort: filterData.sortBy,
    dir: filterData.sortDesc
  };
  
  // 名稱篩選
  if (filterData.name && filterData.name.trim() !== '') {
    jsonRequest.name = filterData.name.trim();
  }
  
  // 價格範圍篩選
  if (filterData.minPrice !== '' && filterData.minPrice !== null) {
    jsonRequest.startPrice = filterData.minPrice;
  }
  
  if (filterData.maxPrice !== '' && filterData.maxPrice !== null) {
    jsonRequest.endPrice = filterData.maxPrice;
  }
  
  // 庫存篩選
  if (filterData.inStockOnly) {
    jsonRequest.startStock = 0; // 設置為0，因為後端使用greaterThan，所以只會顯示庫存>0的產品
  }
  
  console.log('應用篩選條件:', jsonRequest);
  emit('filter-changed', { 
    jsonRequest: jsonRequest,
    categoryId: filterData.productTypeId  // 類型ID是在前端處理的，不發送到後端
  });
}

// 重置所有篩選器
function resetFilters() {
  filterData.name = '';
  filterData.productTypeId = null;
  filterData.minPrice = '';
  filterData.maxPrice = '';
  filterData.inStockOnly = false;
  filterData.sortBy = 'productId';
  filterData.sortDesc = false;
  
  emit('reset-filters');
  applyFilters();
}
</script>

<style scoped>
.filter-sidebar {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.10);
  padding: 20px;
  width: 100%;
  margin-bottom: 24px;
  min-width: 200px;
}

.filter-title {
  font-size: 1.2rem;
  font-weight: bold;
  margin-bottom: 15px;
  text-align: center;
}

.filter-section {
  margin-bottom: 15px;
}

.filter-label {
  font-weight: bold;
  margin-bottom: 8px;
  font-size: 1rem;
}

.filter-divider {
  border-bottom: 1px solid #e0e0e0;
  margin: 12px 0;
}

.category-buttons {
  max-height: 200px;
  overflow-y: auto;
  padding-right: 5px;
}

.filter-range-btn.active {
  background: #e3e3e3;
  font-weight: bold;
}

.filter-minmax {
  border-radius: 4px;
  border: 1px solid #bbb;
  font-size: 0.9rem;
  padding: 4px 6px;
}

@media (max-width: 768px) {
  .filter-sidebar {
    position: sticky;
    top: 1rem;
    min-width: 100%;
  }
}

/* 處理輸入框溢出問題 */
.d-flex.align-items-center {
  flex-wrap: wrap;
}

@media (min-width: 768px) {
  .d-flex.align-items-center {
    gap: 4px;
  }
}
</style>

