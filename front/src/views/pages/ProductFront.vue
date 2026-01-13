<template>
  <!-- container-fluid 提供流動式容器，寬度為 100%，py-4 表示上下內邊距為4單位 -->
  <div class="container-fluid py-4">
    <!-- 彈性盒子布局，在手機版時為上下排列(flex-column)，在中型設備以上為左右排列(flex-md-row) -->
    <div class="d-flex flex-column flex-md-row">
      <!-- 篩選側邊欄區域 -->
      <!-- filter-column 自定義類別控制側邊欄寬度，me-md-4 在中型設備上提供右側邊距 -->
      <div class="filter-column me-md-4">
        <!-- 篩選器組件 -->
        <ProductFilter @filter-changed="applyFilters" @reset-filters="resetFilters" />
      </div>
      <!-- 商品列表區域 -->
      <!-- content-column 自定義類別控制內容區域，使用 flex: 1 自動填充剩餘空間 -->
      <div class="content-column">
        <!-- 加載中提示 -->
        <div v-if="loading" class="loading-container">
          <div class="spinner-border text-tiffany" role="status">
            <span class="visually-hidden">載入中...</span>
          </div>
          <div class="mt-2">載入商品中...</div>
        </div>

        <!-- 商品列表 -->
        <div v-else>
          <!-- 結果計數和分頁選擇器 -->
          <div class="results-header mb-3">
            <div class="results-count">
              <ProductSelect :total="total" :options="[8, 12, 16, 24, 32]"
                v-model="rows" @rows-changed="onRowsChanged">
              </ProductSelect>
            </div>
            <div class="page-info">
              第 {{ current }} 頁 / 共 {{ pages }} 頁
            </div>
          </div>
          
          <!-- 上方分頁控制 -->
          <div class="pagination-wrapper mb-3">
            <Paginate v-if="total > 0"
              :first-last-button="true" first-button-text="&lt;&lt;" last-button-text="&gt;&gt;"
              prev-text="&lt;" next-text="&gt;" :initial-page="current" v-model="current"
              :page-count="pages" :click-handler="refreshProducts">
            </Paginate>
          </div>
          
          <!-- Bootstrap 網格系統，g-4 設定網格間距為4單位 -->
          <div class="row row-cols-2 row-cols-sm-2 row-cols-md-3 row-cols-lg-4 g-4">
            <!-- 使用更簡潔的 Bootstrap 5 列欄佈局方式 -->
            <div class="col" v-for="product in filteredProducts" :key="product.productId">
              <!-- 商品卡片組件，傳入單個商品數據 -->
              <ProductFrontCard :product="product" />
            </div>
          </div>
          
          <!-- 當沒有商品時顯示的提示訊息 -->
          <div v-if="filteredProducts.length === 0" class="no-products-message">
            沒有符合條件的商品
          </div>
          
          <!-- 下方分頁控制 -->
          <div class="pagination-wrapper mt-4">
            <Paginate v-if="total > 0"
              :first-last-button="true" first-button-text="&lt;&lt;" last-button-text="&gt;&gt;"
              prev-text="&lt;" next-text="&gt;" :initial-page="current" v-model="current"
              :page-count="pages" :click-handler="refreshProducts">
            </Paginate>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// 導入 Vue Composition API 的核心功能
import { ref, computed, onMounted } from 'vue';
// 導入篩選器組件
import ProductFilter from '@/components/product/ProductFilter.vue';
// 導入商品卡片組件
import ProductFrontCard from '@/components/product/ProductFrontCard.vue';
// 導入分頁相關組件
import Paginate from 'vuejs-paginate-next';
import ProductSelect from '@/components/product/ProductSelect.vue';
// 導入axios配置
import axiosapi from '@/plugins/axios.js';

// 使用 ref 創建響應式數據
// 存儲所有商品的數組
const products = ref([]);
// 儲存當前選擇的類別ID
const selectedCategoryId = ref(null);
// 加載狀態
const loading = ref(false);

// 最近一次的篩選參數
const lastFilterParams = ref(null);

// 分頁相關
const current = ref(1);
const start = ref(0);
const rows = ref(12);
const pages = ref(0);
const total = ref(0);

// 在script setup頂部，添加一個allFilteredProducts引用
const allFilteredProducts = ref(null);

// 從 API 獲取所有商品數據的函數
async function fetchAllProducts(page = 1) {
  loading.value = true;
  current.value = page;
  start.value = (page - 1) * rows.value;

  try {
    const response = await axiosapi.post('/ajax/products/find', {
      start: start.value,
      rows: rows.value,
      sort: 'productId',
      dir: false
    });
    if (response.status === 200) {
      products.value = response.data.list || [];
      total.value = response.data.count || 0;
      pages.value = Math.ceil(total.value / rows.value);
      console.log('獲取的所有產品:', products.value.length, '總數:', total.value);
    } else {
      console.error('獲取商品失敗:', response.status);
    }
  } catch (error) {
    console.error('獲取商品時發生錯誤:', error);
  } finally {
    loading.value = false;
  }
}

// 應用篩選條件的處理函數
async function applyFilters(filterParams) {
  loading.value = true;
  // 保存類別ID，用於前端篩選
  selectedCategoryId.value = filterParams.categoryId;
  lastFilterParams.value = filterParams;
  
  // 重置為第一頁
  current.value = 1;
  start.value = 0;
  
  try {
    // 如果需要前端類別篩選，獲取所有數據一次性處理
    if (selectedCategoryId.value) {
      // 創建請求對象，但不包含分頁參數，獲取所有符合其他條件的數據
      const requestData = {
        ...filterParams.jsonRequest,
        start: 0,
        rows: 1000 // 獲取大量數據以便前端處理
      };
      
      console.log('向後端發送篩選請求（獲取所有數據）:', requestData);
      const response = await axiosapi.post('/ajax/products/find', requestData);
      
      if (response.status === 200) {
        // 獲取所有符合其他條件的產品
        const allProducts = response.data.list || [];
        
        // 前端按類別過濾
        const filteredByCategory = allProducts.filter(product => 
          product.productType && product.productType.id === selectedCategoryId.value
        );
        
        // 保存所有過濾後的產品，用於分頁
        allFilteredProducts.value = filteredByCategory;
        
        // 更新總數和頁數
        total.value = filteredByCategory.length;
        pages.value = Math.ceil(total.value / rows.value);
        
        // 獲取當前頁面應顯示的產品
        updateCurrentPageProducts();
      }
    } else {
      // 無類別篩選，直接使用後端分頁
      const requestData = {
        ...filterParams.jsonRequest,
        start: start.value,
        rows: rows.value
      };
      
      console.log('向後端發送篩選請求（使用後端分頁）:', requestData);
      const response = await axiosapi.post('/ajax/products/find', requestData);
      
      if (response.status === 200) {
        products.value = response.data.list || [];
        total.value = response.data.count || 0;
        pages.value = Math.ceil(total.value / rows.value);
        allFilteredProducts.value = null; // 清空緩存的產品列表
      }
    }
  } catch (error) {
    console.error('篩選過程發生錯誤:', error);
  } finally {
    loading.value = false;
  }
}

// 刷新產品列表（用於分頁和每頁數量變更）
async function refreshProducts(page) {
  if (!page) {
    page = 1;
  }
  
  current.value = page;
  start.value = (page - 1) * rows.value;
  
  // 如果有類別篩選，使用前端分頁處理已過濾的產品
  if (selectedCategoryId.value && allFilteredProducts.value) {
    updateCurrentPageProducts();
    return;
  }
  
  // 如果無類別篩選或沒有緩存的過濾產品，直接請求後端
  if (lastFilterParams.value) {
    const requestData = {
      ...lastFilterParams.value.jsonRequest,
      start: start.value,
      rows: rows.value
    };
    
    loading.value = true;
    try {
      const response = await axiosapi.post('/ajax/products/find', requestData);
      if (response.status === 200) {
        products.value = response.data.list || [];
        total.value = response.data.count || 0;
        pages.value = Math.ceil(total.value / rows.value);
      }
    } catch (error) {
      console.error('刷新產品列表時發生錯誤:', error);
    } finally {
      loading.value = false;
    }
  } else {
    // 沒有篩選條件，直接獲取所有產品
    fetchAllProducts(page);
  }
}

// 更新當前頁面顯示的產品
function updateCurrentPageProducts() {
  if (!allFilteredProducts.value) return;
  
  const startIndex = (current.value - 1) * rows.value;
  const endIndex = Math.min(startIndex + rows.value, allFilteredProducts.value.length);
  
  if (startIndex < allFilteredProducts.value.length) {
    products.value = allFilteredProducts.value.slice(startIndex, endIndex);
  } else if (allFilteredProducts.value.length > 0) {
    // 如果當前頁超出範圍，返回到第一頁
    current.value = 1;
    products.value = allFilteredProducts.value.slice(0, Math.min(rows.value, allFilteredProducts.value.length));
  } else {
    products.value = [];
  }
}

// 當每頁顯示數量改變時，需要重新計算頁數和當前頁產品
function onRowsChanged() {
  pages.value = Math.ceil(total.value / rows.value);
  
  // 確保當前頁不超過總頁數
  if (current.value > pages.value && pages.value > 0) {
    current.value = pages.value;
  }
  
  if (selectedCategoryId.value && allFilteredProducts.value) {
    updateCurrentPageProducts();
  } else {
    refreshProducts(current.value);
  }
}

// 重置所有篩選條件
function resetFilters() {
  selectedCategoryId.value = null;
  lastFilterParams.value = null;
  current.value = 1;
  fetchAllProducts(1);
}

// 計算屬性：根據類別ID在前端進行篩選
const filteredProducts = computed(() => {
  // 如果沒有選擇類別，直接返回所有產品
  if (!selectedCategoryId.value) {
    return products.value;
  }
  
  // 根據類別ID過濾產品
  return products.value.filter(product => 
    product.productType && product.productType.id === selectedCategoryId.value
  );
});

// 組件掛載時執行的鉤子函數
onMounted(() => {
  // 掛載時獲取所有商品數據
  fetchAllProducts(1);
});
</script>

<style scoped>
/* 主容器樣式 */
.container-fluid {
  background: #e8f9f8;   /* 超淺Tiffany綠背景色 */
  min-height: 100vh;     /* 最小高度為視口高度，確保填滿整個視口 */
  padding: 2rem;         /* 內邊距為 2rem */
}

/* 篩選器列樣式 */
.filter-column {
  width: 100%;           /* 在小屏幕上寬度為 100% */
  flex-shrink: 0;        /* 禁止收縮，保持固定寬度 */
  margin-bottom: 1.5rem; /* 底部外邊距，在小屏幕上提供與內容區域的間隔 */
}

/* 內容區域樣式 */
.content-column {
  flex: 1;               /* 占用剩餘空間 */
}

/* 結果頭部樣式 */
.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 10px 15px;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.05);
  border-left: 4px solid #0abab5; /* Tiffany綠邊框 */
}

.results-count {
  font-weight: 500;
}

.page-info {
  font-size: 0.9rem;
  color: #0abab5; /* Tiffany綠文字 */
}

/* 分頁容器樣式 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
}

/* 加載中樣式 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem 0;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.05);
  border-top: 3px solid #0abab5; /* Tiffany綠上邊框 */
}

/* 無商品提示樣式 */
.no-products-message {
  padding: 2rem;                        /* 內邊距 */
  text-align: center;                   /* 文字居中 */
  color: #6c757d;                       /* 文字顏色為灰色 */
  font-size: 1.2rem;                    /* 字體大小 */
  background: #fff;                     /* 背景色為白色 */
  border-radius: 8px;                   /* 圓角 */
  box-shadow: 0 2px 10px rgba(0,0,0,0.05); /* 陰影效果 */
  border-top: 3px solid #0abab5; /* Tiffany綠上邊框 */
}

/* 中型設備（768px 及以上）的樣式 */
@media (min-width: 768px) {
  .filter-column {
    width: 240px;        /* 在中型設備上，篩選器寬度固定為 240px */
    margin-bottom: 0;    /* 移除底部外邊距，因為此時是左右排列 */
  }
}

/* 中型設備以下（最大寬度 768px）的樣式 */
@media (max-width: 768px) {
  .container-fluid {
    padding: 1rem;       /* 減少內邊距 */
  }
  
  .row.g-4 {
    --bs-gutter-x: 0.75rem;  /* 減少網格間距 */
  }
  
  .results-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
}

/* 小型設備（最大寬度 576px）的樣式 */
@media (max-width: 576px) {
  .container-fluid {
    padding: 0.75rem;    /* 進一步減少內邊距 */
  }
  
  .row.g-4 {
    --bs-gutter-x: 0.5rem;   /* 進一步減少網格間距 */
  }
}

/* 分頁器樣式 */
:deep(.pagination) {
  display: flex;
  padding-left: 0;
  list-style: none;
  border-radius: 0.25rem;
  margin: 1rem 0;
}

:deep(.pagination li) {
  margin: 0 2px;
}

:deep(.pagination li.active a) {
  background-color: #0abab5; /* Tiffany綠色按鈕 */
  color: white;
  border-color: #0abab5;
}

:deep(.pagination li a) {
  position: relative;
  display: block;
  padding: 0.5rem 0.75rem;
  margin-left: -1px;
  line-height: 1.25;
  color: #0abab5; /* Tiffany綠色文字 */
  background-color: #fff;
  border: 1px solid #dee2e6;
  text-decoration: none;
  cursor: pointer;
  border-radius: 0.25rem;
}

:deep(.pagination li a:hover) {
  background-color: #e6f7f6; /* 淺Tiffany綠背景 */
  border-color: #0abab5; /* Tiffany綠邊框 */
}

:deep(.pagination li.disabled a) {
  color: #6c757d;
  pointer-events: none;
  background-color: #fff;
  border-color: #dee2e6;
}
</style>

