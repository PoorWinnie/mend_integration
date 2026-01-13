<template>
    <div class="product-page">
        <div class="header-row">
            <div class="title-section">
                <h3>產品管理</h3>
            </div>
            <div class="search-section">
                <input type="text" class="form-control" placeholder="請輸入產品名稱" v-model="findName" @input="callFind(1)">
            </div>
            <div class="select-section">
                <ProductSelect :total="total" :options="[2, 3, 4, 5, 7, 10]"
                            v-model="rows" @rows-changed="callFind(1)">
                </ProductSelect>
            </div>
            <div class="button-section">
                <button type="button" class="btn btn-tiffany add-btn" @click="openModal('insert')">新增產品</button>
            </div>
        </div>    
        
        <div class="pagination-wrapper">
            <Paginate v-if="total > 0"
                :first-last-button="true" first-button-text="&lt;&lt;" last-button-text="&gt;&gt;"
                prev-text="&lt;" next-text="&gt;" :initial-page="current" v-model="current"
                :page-count="pages" :click-handler="callFind">
            </Paginate>
        </div>
        <br>

        <div class="row row-cols-1 row-cols-md-2 row-cols-lg-4 g-4">
            <div class="col" v-for="item in products" :key="`product-${item.productId}`">
                <ProductCard :product="item" @openUpdate="openModal" @delete="callRemove"></ProductCard>
            </div>
        </div>

        <div class="pagination-wrapper bottom-pagination">
            <Paginate v-if="total > 0"
                :first-last-button="true" first-button-text="&lt;&lt;" last-button-text="&gt;&gt;"
                prev-text="&lt;" next-text="&gt;" :initial-page="current" v-model="current"
                :page-count="pages" :click-handler="callFind">
            </Paginate>
        </div>

        <ProductModal ref="productModal" v-model:product="product"
                    :is-show-insert-button="isShowInsertButton"
                    @insert="callCreate" @update="callModify">
        </ProductModal>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axiosapi from '@/plugins/axios.js';
import Swal from 'sweetalert2';
import Paginate from 'vuejs-paginate-next';
import ProductCard from '@/components/product/ProductCard.vue';
import ProductSelect from '@/components/product/ProductSelect.vue';
import ProductModal from '@/components/product/ProductModal.vue';

const products = ref([]);
const findName = ref("");
const productModal = ref(null);
const product = ref({});
const isShowInsertButton = ref(true);

// 分頁相關
const current = ref(1);
const start = ref(0);
const rows = ref(2);
const pages = ref(0);
const total = ref(0);
const lastPageRows = ref(0);

function openModal(action, _type, id) {
    if(action === "insert") {
        isShowInsertButton.value = true;
        product.value = {};
        productModal.value.showModal();
    } else {
        isShowInsertButton.value = false;
        callFindById(id);
    }
}

async function callFindById(id) {
    Swal.fire({ text: '載入中...', allowOutsideClick: false, showConfirmButton: false });
    try {
        const response = await axiosapi.get(`/ajax/products/${id}`);
        // 回傳格式為 { list: [ {...} ] }
        if(response.data && response.data.list && response.data.list.length > 0) {
            product.value = response.data.list[0];
        } else {
            product.value = {};
        }
        productModal.value.showModal();
        setTimeout(() => Swal.close(), 500);
    } catch (error) {
        Swal.fire({ icon: 'error', text: '訊息：'+error.message });
    }
}

async function callFind(page) {
    if(page) {
        current.value = page;
        start.value = (page - 1) * rows.value;
    } else {
        current.value = 1;
        start.value = 0;
    }
    const body = {
        start: start.value,
        rows: rows.value,
        name: findName.value || null,
        sort: "productId",
        dir: false
    };
    try {
        Swal.fire({ text: '載入中...', allowOutsideClick: false, showConfirmButton: false });
        const response = await axiosapi.post("/ajax/products/find", body);
        products.value = response.data.list;
        total.value = response.data.count;
        lastPageRows.value = total.value % rows.value;
        pages.value = Math.ceil(total.value / rows.value);
        
        setTimeout(() => Swal.close(), 500);
    } catch (error) {
        console.error('查詢產品錯誤:', error);
        Swal.fire({ icon: 'error', text: '訊息：'+error.message });
    }
}

async function callRemove(id) {
    const result = await Swal.fire({ text: "確定刪除？", icon: "question", showCancelButton: true });
    if(result.isConfirmed) {
        try {
            const response = await axiosapi.delete(`/ajax/products/${id}`);
            if(response.data.success) {
                await Swal.fire({ icon: 'success', text: response.data.message });
                if(current.value > 1 && lastPageRows.value === 1) {
                    current.value = current.value - 1;
                }
                callFind(current.value);
            } else {
                Swal.fire({ icon: 'warning', text: response.data.message });
            }
        } catch(error) {
            Swal.fire({ icon: 'error', text: '訊息：'+error.message });
        }
    }
}

async function callCreate() {
    Swal.fire({ text: '載入中...', allowOutsideClick: false, showConfirmButton: false });
    try {
        // 複製數據，移除不必要字段
        const tempData = JSON.parse(JSON.stringify(product.value));
        if(tempData.createdAt) delete tempData.createdAt;
        
        // 創建一個簡化的請求對象，只包含必要字段
        const data = {
            name: tempData.name || null,
            price: tempData.price ? String(tempData.price) : null,
            stock: typeof tempData.stock === 'string' ? parseInt(tempData.stock) : tempData.stock,
            description: tempData.description || null,
            imgUrl: tempData.imgUrl || null,
            clickCount: typeof tempData.clickCount === 'string' ? 
                parseInt(tempData.clickCount) : 
                (tempData.clickCount || 0) // 新商品默認為0
        };
        
        // 處理商品類型 - 提取productTypeId而不是使用整個productType對象
        if(tempData.productTypeId) {
            // 直接設置 productTypeId 而非 productType 對象
            data.productTypeId = parseInt(tempData.productTypeId);
        }
        
        console.log("新增產品資料:", JSON.stringify(data));
        
        // 發送POST請求
        const response = await axiosapi.post("/ajax/products", data);
        
        if(response.data && response.data.success) {
            const createdProductId = response.data.product ? response.data.product.productId : null;
            let message = response.data.message || '新增成功';
            
            // 處理圖片上傳
            let imageUploadSuccess = true;
            const imageFile = productModal.value.getImageFile();
            
            if(imageFile && createdProductId) {
                console.log(`新產品ID: ${createdProductId} - 上傳圖片`);
                try {
                    const uploadResult = await uploadProductImage(createdProductId, imageFile);
                    imageUploadSuccess = !!uploadResult && uploadResult.success;
                } catch(error) {
                    console.error("圖片上傳錯誤:", error);
                    imageUploadSuccess = false;
                }
                
                if(!imageUploadSuccess) {
                    message += '，但圖片上傳失敗';
                }
            }
            
            await Swal.fire({ icon: 'success', text: message });
            productModal.value.hideModal();
            callFind(current.value);
        } else {
            Swal.fire({
                icon: 'warning',
                text: response.data ? response.data.message : '新增失敗'
            });
        }
    } catch(error) {
        console.error("新增產品錯誤:", error);
        if(error.response) {
            console.error("狀態碼:", error.response.status);
            console.error("錯誤詳情:", error.response.data);
        }
        Swal.fire({ icon: 'error', text: `新增失敗：${error.message}` });
    }
}

async function callModify() {
    Swal.fire({ text: '載入中...', allowOutsideClick: false, showConfirmButton: false });
    try {
        // 檢查productId是否存在
        if(!product.value.productId) {
            Swal.fire({ icon: 'error', text: '產品ID不能為空' });
            return;
        }
        
        // 確保productId是整數
        const productId = parseInt(product.value.productId);
        if(isNaN(productId)) {
            Swal.fire({ icon: 'error', text: '產品ID必須是有效數字' });
            return;
        }
        
        // 標記是否有圖片上傳，用於後續處理
        const imageFile = productModal.value.getImageFile();
        let imageUploadSuccess = false;
        
        // 先上傳圖片（如果有）
        if(imageFile) {
            try {
                console.log(`先上傳圖片到產品ID: ${productId}`);
                
                // 直接使用uploadProductImage函數上傳圖片
                const uploadResult = await uploadProductImage(productId, imageFile);
                imageUploadSuccess = !!uploadResult && uploadResult.success;
                
                if(imageUploadSuccess) {
                    console.log('圖片上傳成功，繼續更新產品數據');
                } else {
                    console.error('圖片上傳失敗:', uploadResult ? uploadResult.message : '未知錯誤');
                }
            } catch(error) {
                console.error("圖片上傳過程出錯:", error);
                imageUploadSuccess = false;
            }
        }
        
        // 複製數據，移除createdAt字段
        const tempData = JSON.parse(JSON.stringify(product.value));
        delete tempData.createdAt;
        
        // 創建請求對象 - 使用純JavaScript對象，避免自定義類
        const data = {
            productId: productId,
            name: tempData.name || null,
            description: tempData.description || null,
            // 直接使用字符串格式的價格，而不是BigDecimal對象
            price: tempData.price ? tempData.price.toString() : null,
            stock: typeof tempData.stock === 'string' ? parseInt(tempData.stock) : tempData.stock,
            imgUrl: tempData.imgUrl || null,
            clickCount: typeof tempData.clickCount === 'string' ? parseInt(tempData.clickCount) : tempData.clickCount
        };
        
        // 處理商品類型 - 這是最關鍵的修正
        if(tempData.productTypeId) {
            // 創建商品類型對象
            data.productType = {
                id: parseInt(tempData.productTypeId)
            };
        }
        
        console.log("發送PUT請求資料:", JSON.stringify(data));
        
        // 發送PUT請求更新產品數據
        try {
            const response = await axiosapi.put(`/ajax/products/${productId}`, data);
            
            if(response.data && response.data.success) {
                let message = '修改成功';
                if(imageFile && !imageUploadSuccess) {
                    message += '，但圖片上傳失敗';
                }
                
                await Swal.fire({ icon: 'success', text: message });
                productModal.value.hideModal();
                
                // 重新加載產品列表
                callFind(current.value);
            } else {
                Swal.fire({
                    icon: 'warning',
                    text: response.data ? response.data.message : '修改失敗'
                });
            }
        } catch(error) {
            console.error("PUT請求錯誤:", error);
            if(error.response) {
                console.error("狀態碼:", error.response.status);
                console.error("錯誤數據:", error.response.data);
            }
            Swal.fire({
                icon: 'error',
                text: `修改失敗：${error.message}`
            });
        }
    } catch(error) {
        console.error("整體執行錯誤:", error);
        Swal.fire({ icon: 'error', text: '執行過程中發生錯誤' });
    }
}

// 上傳產品圖片
async function uploadProductImage(productId, imageFile) {
    if (!productId || !imageFile) {
        console.error('上傳失敗: 產品ID或圖片文件為空');
        return { success: false, message: '產品ID或圖片文件不能為空' };
    }
    
    try {
        // 確保productId是整數
        const validProductId = parseInt(productId);
        if (isNaN(validProductId)) {
            console.error('上傳失敗: 產品ID不是有效數字', productId);
            return { success: false, message: '產品ID必須是有效數字' };
        }
        
        console.log(`開始上傳圖片到產品ID: ${validProductId}`);
        
        // 準備表單數據
        const formData = new FormData();
        formData.append('image', imageFile);
        
        // 確保URL格式正確
        const uploadUrl = `${axiosapi.defaults.baseURL}/ajax/products/images/upload/${validProductId}`.replace(/([^:]\/)\/+/g, "$1");
        
        console.log(`上傳URL: ${uploadUrl}`);
        
        // 發送請求
        const response = await axiosapi.post(
            uploadUrl, 
            formData, 
            {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            }
        );
        
        console.log('圖片上傳回應:', response.data);
        
        // 檢查回應
        if (response.data && response.data.success) {
            return response.data;
        } else {
            const errorMessage = response.data ? response.data.message : '圖片上傳失敗';
            console.error('上傳失敗:', errorMessage);
            return { success: false, message: errorMessage };
        }
    } catch (error) {
        console.error('上傳圖片發生錯誤:', error);
        if (error.response) {
            console.error('回應狀態:', error.response.status);
            console.error('回應數據:', error.response.data);
        }
        return { success: false, message: error.message };
    }
}

onMounted(() => {
    callFind();
});
</script>

<style scoped>
.product-page {
    background-color: #e8f9f8; /* 超淺Tiffany綠背景 */
    padding: 20px;
    border-radius: 8px;
    min-height: 100vh;
}

.header-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;
    flex-wrap: wrap;
    gap: 10px;
    background-color: white;
    padding: 15px;
    border-radius: 8px;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
    border-left: 4px solid #0abab5; /* Tiffany綠邊框 */
}

.title-section {
    width: 20%;
    min-width: 150px;
}

.title-section h3 {
    margin: 0;
    color: #0abab5; /* Tiffany綠文字 */
    font-weight: bold;
    text-shadow: none;
}

.search-section {
    width: 30%;
    min-width: 200px;
}

.search-section input {
    border: 1px solid #e0e0e0;
    border-radius: 8px;
    padding: 8px 15px;
    box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.05);
}

.search-section input:focus {
    border-color: #0abab5;
    box-shadow: 0 0 0 0.2rem rgba(10, 186, 181, 0.25);
}

.select-section {
    width: 25%;
    min-width: 180px;
}

.button-section {
    width: 15%;
    min-width: 120px;
}

.add-btn {
    width: 100%;
    border-radius: 8px;
    background-color: #0abab5; /* Tiffany綠按鈕 */
    border: none;
    font-weight: bold;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    transition: all 0.2s ease-in-out;
}

.add-btn:hover {
    background-color: #089994;
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.btn-tiffany {
    background-color: #0abab5;
    border-color: #0abab5;
    color: white;
}

.btn-tiffany:hover {
    background-color: #089994;
    border-color: #089994;
    color: white;
}

.pagination-wrapper {
    display: flex;
    justify-content: center;
    width: 100%;
    margin: 15px 0;
}

.pagination-wrapper ul {
    margin: 0 auto;
}

.bottom-pagination {
    margin-top: 20px;
    margin-bottom: 10px;
}

.row-cols-1 > .col {
    margin-bottom: 1.5rem;
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
