<template>
    <div class="card product-card h-100">
        <!-- 圖片容器 -->
        <div class="img-container">
            <!-- 修改為根據 productId 動態生成圖片 URL -->
            <img v-if="product.productId" class="card-img-top" :alt="product.name" :src="imageUrl">
            <div v-else class="card-img-placeholder d-flex align-items-center justify-content-center">
                <span>無商品圖片</span>
            </div>
        </div>
        <div class="card-body d-flex flex-column">
            <h5 class="card-title">{{ product.name }}</h5>
            <div class="mt-auto">
                <div class="price-stock-container">
                    <div class="card-text text-danger fw-bold">價格：${{ product.price }}</div>
                    <div class="card-text stock-text"
                        :class="{ 'text-success': product.stock > 0, 'text-secondary': product.stock <= 0 }">
                        {{ product.stock > 0 ? '有庫存' : '缺貨中' }}
                    </div>
                </div>
                <div class="d-grid mt-2">
                    <button class="btn btn-primary btn-sm" :disabled="product.stock <= 0" @click="addToCart">
                        <i class="fas fa-shopping-cart me-1"></i> 加入購物車
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue';
import axiosapi from '@/plugins/axios.js';
import { useCartStore } from '@/stores/cartStore';
import Swal from 'sweetalert2'

const props = defineProps(["product"]);
const cartStore = useCartStore();


// 動態生成圖片 URL，添加時間戳避免快取問題
const imageUrl = computed(() => {
    if (!props.product.productId) return '';
    const timestamp = new Date().getTime();
    return `${axiosapi.defaults.baseURL}/ajax/products/images/${props.product.productId}?t=${timestamp}`;
});


async function addToCart() {
    const productdata = {
        username: cartStore.userName,
        productId: props.product.productId
    }
    try {
        const response = await axiosapi.post('/api/cart', productdata)
        const { statusCode } = response.data;

        Swal.fire('成功', '商品成功加入購物車', 'success')
        // if (statusCode === 1) {
        //     Swal.fire('提示', '商品已存在於購物車中', 'info')
        // } else {
        //     Swal.fire('成功', '商品成功加入購物車', 'success')
        // }
    } catch (error) {
        Swal.fire('錯誤', '加入購物車失敗，請稍後再試', 'error')
    }
}
</script>

<style scoped>
.product-card {
    transition: all 0.3s ease;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    border-radius: 8px;
    overflow: hidden;
    height: 100%;
    border: none;
    display: flex;
    flex-direction: column;
}

.product-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);
}

.img-container {
    height: 200px;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
}

.card-img-top {
    width: 100%;
    height: 100%;
    object-fit: contain;
}

.card-img-placeholder {
    height: 100%;
    width: 100%;
    background-color: #f0f0f0;
    color: #999;
}

.card-title {
    font-size: 1rem;
    height: 3rem;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    margin-bottom: 0.5rem;
}

.card-body {
    padding: 1rem;
    flex-grow: 1;
    display: flex;
    flex-direction: column;
}

.price-stock-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 0.5rem;
}

.stock-text {
    font-size: 0.85rem;
    font-weight: 500;
}

.btn-primary {
    background-color: #3490dc;
    border-color: #3490dc;
}

.btn-primary:hover:not(:disabled) {
    background-color: #2779bd;
    border-color: #2779bd;
}
</style>
