<template>
    <div class="body">
        <div class="header">
            🏕️ 西米露購物車
        </div>

        <div class="cart-container">
            <div class="cart-header">
                <span>
                    <p>全選</p>
                    <input type="checkbox" v-model="selectAll">
                </span>
                <span>商品</span>
                <span>單價</span>
                <span>數量</span>
                <span>總計</span>
                <span>操作</span>
            </div>

            <div class="cart-item" v-for="item in cartItems" :key="item.id">
                <input type="checkbox" v-model="item.selected">
                <div class="item-info">
                    <img :src="item.image" :alt="item.name">
                    <p><strong>{{ item.name }}</strong></p>
                </div>
                <div class="price">
                    <p>${{ item.price }}</p>
                </div>
                <div class="quantity_box">
                    <div class="quantity">
                        <button @click="decrease(item.id)">−</button>
                        <input type="text" v-model.number="item.quantity">
                        <button @click="increase(item.id)">+</button>
                    </div>
                </div>
                <div class="price">
                    <p>${{ item.price * item.quantity }}</p>
                </div>
                <div class="delete">
                    <button @click="removeItem(item.id)">刪除</button>
                </div>
            </div>

            <div class="cart-footer">
                <div class="total-price">
                    💰 總金額: <span>${{ totalPrice }}</span>
                </div>

                <div class="checkout">
                    <button @click="checkout">去買單</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import axiosapi from '@/plugins/axios.js';
import { useRouter } from 'vue-router';
import { useCartStore } from '@/stores/cartStore';
import Swal from 'sweetalert2';

const cartItems = ref([]);
const router = useRouter();
const cartStore = useCartStore();

onMounted(function () {
    callFind();
});

async function callFind() {
    try {
        //模擬登入讀取使用者狀態
        // cartStore.setUserName('test1');

        const response = await axiosapi.get(`/api/cart/user/${cartStore.userName}`);

        cartItems.value = response.data.map((item, index) => ({
            id: index + 1,
            name: item.name,
            price: item.price,
            quantity: item.quantity,
            image: item.imageUrl,
            selected: false
        }));

    } catch (error) {
        console.error('error:',error);
    }
}

const checkout = async () => {
    // 取出所有選中的商品
    const selectedItems = cartItems.value.filter(item => item.selected);

    if (selectedItems.length === 0) {

        Swal.fire({
            icon: 'warning',
            title: '提醒',
            text: '請至少選擇一個商品結帳！',
            confirmButtonText: '確定',
            confirmButtonColor: '#66bb6a', // 自訂按鈕顏色
        });

        return;
    } else {

        const simplifiedItems = selectedItems.map(item => ({
            name: item.name,
            price: item.price,
            quantity: item.quantity,
            image: item.image
        }));

        // 跳轉到結帳頁面，並傳遞選中的商品資料
        cartStore.setSelectedItems(simplifiedItems);
        router.push({
            name: 'OrderItems',  // 這是你 Vue Router 中設定的名稱
        });
    }
};

    const selectAll = computed({
        get: () => cartItems.value.every(item => item.selected),
        set: (value) => cartItems.value.forEach(item => (item.selected = value))
    });

    const increase = (id) => {
        const item = cartItems.value.find(i => i.id === id);
        if (item) item.quantity++;
    };

    const decrease = (id) => {
        const item = cartItems.value.find(i => i.id === id);
        if (item && item.quantity > 1) item.quantity--;
    };

    const removeItem = (id) => {
        cartItems.value = cartItems.value.filter(item => item.id !== id);
    };

    const totalPrice = computed(() => {
        return cartItems.value
            .filter(item => item.selected)
            .reduce((sum, item) => sum + item.price * item.quantity, 0);
    });
</script>

<style scoped>
.header {
    background-color: #81c784;
    color: #fffde7;
    padding: 30px 15px;
    text-align: center;
    font-size: 28px;
    font-weight: bold;
    font-family: 'Segoe UI', 'Microsoft JhengHei', 'Arial Rounded MT Bold', sans-serif;
    letter-spacing: 1px;
    position: relative;
}

.cart-container {
    width: 80%;
    margin: 20px auto;
    background-color: white;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 0 10px rgba(76, 175, 80, 0.2);
}

.cart-header,
.cart-item {
    display: grid;
    grid-template-columns: 1fr 2fr 1fr 1fr 1fr 1fr;
    align-items: center;
    padding: 10px;
    border-bottom: 1px solid #c8e6c9;
    text-align: center;
}

.cart-header {
    background-color: #dcedc8;
    font-weight: bold;
}

.cart-item .item-info {
    display: flex;
    align-items: center;
    gap: 10px;
    justify-content: flex-start;
}

.cart-item img {
    width: 80px;
    height: auto;
    border-radius: 5px;
}

.quantity_box {
    display: flex;
    align-items: center;
    justify-content: center;
}

.quantity {
    display: flex;
    align-items: center;
    justify-content: center;
    border: 1px solid #ccc;
    border-radius: 5px;
    width: 100px;
    overflow: hidden;
}

.quantity button {
    background: none;
    border: none;
    padding: 5px 10px;
    cursor: pointer;
    font-size: 18px;
}

.quantity input {
    width: 30px;
    text-align: center;
    border: none;
    font-size: 16px;
}

.quantity button:focus,
.quantity input:focus {
    outline: none;
}

.price {
    color: black;
}

.delete button {
    color: #c62828;
    cursor: pointer;
    background: none;
    border: none;
}

.cart-footer {
    display: flex;
    justify-content: space-between;
    margin-top: 20px;
    align-items: center;
}

.checkout button {
    background-color: #66bb6a;
    color: white;
    border: none;
    padding: 10px 20px;
    font-size: 16px;
    cursor: pointer;
    border-radius: 5px;
    transition: background-color 0.2s ease;
}

.checkout button:hover {
    background-color: #4caf50;
}

.discount-section,
.shipping-info {
    margin: 10px 0;
    font-size: 14px;
    color: #388e3c;
}

.total-price {
    font-size: 20px;
    font-weight: bold;
    color: black;
}
</style>