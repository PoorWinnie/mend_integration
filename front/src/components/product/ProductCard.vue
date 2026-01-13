<template>
    <div class="card product-card">
        <div class="img-container">
            <img class="card-img-top" :alt="product.name" :src="imageUrl" ref="productImage">
        </div>
        <div class="card-body">
            <h5 class="card-title" :title="product.name">{{ product.name }}</h5>
            <div class="card-text price-text">價格：<span>{{ product.price }}</span></div>
            <div class="card-text stock-text">
                庫存：<span :class="{'text-success': product.stock > 0, 'text-danger': product.stock <= 0}">
                    {{ product.stock }}
                </span>
            </div>
            <div class="row mt-2 button-row">
                <div class="col text-start">
                    <button class="btn btn-primary btn-action"
                            @click="emits('openUpdate', 'update', '', product.productId)">
                        <i class="fas fa-edit me-1"></i> 開啟修改
                    </button>
                </div>
                <div class="col text-end">
                    <button class="btn btn-danger btn-action"
                            @click="emits('delete', product.productId)">
                        <i class="fas fa-trash-alt me-1"></i> 刪除
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>
    
<script setup>
import { ref, computed, onMounted } from 'vue';
import axiosapi from '@/plugins/axios.js';

const props = defineProps({
    product: { type: Object, required: true }
});

const emits = defineEmits(["openUpdate", "delete"]);
const productImage = ref(null);

// 添加時間戳以避免瀏覽器快取問題
const imageUrl = computed(() => {
    if (!props.product.productId) return '';
    const timestamp = new Date().getTime();
    return `${axiosapi.defaults.baseURL}/ajax/products/images/${props.product.productId}?t=${timestamp}`;
});

// 初始化時確保圖片正確加載
onMounted(() => {
    if (productImage.value && props.product.productId) {
        // 確保圖片加載
        productImage.value.onerror = () => {
            console.log(`圖片載入失敗: ${props.product.productId}`);
        };
    }
});

// 獲取產品圖片URL（添加時間戳防止快取）
function getProductImageUrl(productId) {
    if (!productId) return '';
    const timestamp = new Date().getTime();
    return `${axiosapi.defaults.baseURL}/ajax/products/images/${productId}?t=${timestamp}`;
}
</script>

<style scoped>
.product-card {
    height: 100%;
    width: 100%;
    display: flex;
    flex-direction: column;
    border-radius: 15px;
    overflow: hidden;
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
    transition: all 0.3s ease;
    border: none;
    min-height: 400px; /* 增加最小高度 */
}

.product-card:hover {
    transform: translateY(-6px);
    box-shadow: 0 12px 25px rgba(0, 0, 0, 0.18);
}

.img-container {
    height: 250px; /* 增加圖片容器高度 */
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #f8f9fa;
    border-bottom: 1px solid rgba(0, 0, 0, 0.05);
    padding: 1rem;
}

.card-img-top {
    width: 100%;
    height: 100%;
    object-fit: contain;
    transition: transform 0.3s ease;
}

.product-card:hover .card-img-top {
    transform: scale(1.05);
}

.card-body {
    flex-grow: 1;
    display: flex;
    flex-direction: column;
    padding: 1.5rem; /* 增加內邊距 */
}

.card-title {
    font-weight: 600;
    font-size: 1.25rem; /* 增加字體大小 */
    margin-bottom: 1rem;
    color: #343a40;
    min-height: 3rem; /* 固定高度確保一致性 */
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    line-height: 1.5;
}

.price-text {
    font-weight: 500;
    text-align: right;
    margin-bottom: 0.75rem;
    color: #6c757d;
    font-size: 1.1rem; /* 增加字體大小 */
}

.price-text span {
    color: #dc3545;
    font-weight: 700;
    font-size: 1.3rem; /* 增加價格字體大小 */
}

.stock-text {
    text-align: right;
    margin-bottom: 1.25rem;
    color: #6c757d;
    font-size: 1.1rem; /* 增加字體大小 */
}

.stock-text span {
    font-weight: 600;
}

.row.mt-2.button-row {
    margin-top: auto !important;
}

.btn-action {
    width: 100%;
    border-radius: 10px;
    font-weight: 500;
    padding: 0.65rem 0.75rem; /* 增加按鈕內邊距 */
    transition: all 0.2s;
    box-shadow: 0 3px 6px rgba(0, 0, 0, 0.12);
    font-size: 1.05rem; /* 增加按鈕文字大小 */
}

.btn-action:hover {
    transform: translateY(-3px);
    box-shadow: 0 5px 10px rgba(0, 0, 0, 0.18);
}

.btn-action:active {
    transform: translateY(-1px);
}

/* 添加媒體查詢以確保在小屏幕上顯示良好 */
@media (max-width: 1200px) {
    .product-card {
        min-height: 380px;
    }
    
    .img-container {
        height: 220px;
    }
    
    .card-title {
        font-size: 1.15rem;
    }

}

@media (max-width: 768px) {
    .product-card {
        min-height: 350px;
    }
    
    .img-container {
        height: 200px;
    }
    
    .card-body {
        padding: 1.25rem;
    }
}
</style>
