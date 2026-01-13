<template>
    <div>
        <div class="header">🏕️ 結帳頁面</div>
        <div class="container">
            <div class="title">訂單商品</div>
            <table>
                <tr>
                    <th>商品</th>
                    <th>單價</th>
                    <th>數量</th>
                    <th>總價</th>
                </tr>
                <tr v-for="(item, index) in cartItems" :key="index">
                    <td>
                        <img :src="item.image" :alt="item.name">
                        <p><strong>{{ item.name }}</strong></p>
                    </td>
                    <td>${{ item.price }}</td>
                    <td>{{ item.quantity }}</td>
                    <td>${{ item.price * item.quantity }}</td>
                </tr>
            </table>

            <div class="checkout-section">
                <h3>優惠券</h3>
                <div class="coupon-input-group">
                    <input v-model="couponCodeInput" placeholder="請輸入優惠券編號" />
                    <button @click="redeemCoupon">使用</button>
                </div>

                <div v-if="appliedCouponName">
                    <p>目前優惠券：{{ appliedCouponName }}</p>
                    <p>折扣金額：${{ couponApplied }}</p>
                    <button @click="clearCoupon" class="cancel-coupon">取消使用</button>
                </div>
                <div v-else>
                    <p>目前優惠券：無</p>
                </div>
            </div>

            <div class="checkout-section">
                <h3>付款方式</h3>
                <p style="margin-top: 10px;">目前付款方式：{{ selectedPaymentMethod || '未選擇' }}</p>
                <div class="payment-methods">
                    <button :class="{ selected: selectedPaymentMethod === '信用卡' }"
                        @click="selectPaymentMethod('信用卡')">信用卡</button>
                    <button :class="{ selected: selectedPaymentMethod === '貨到付款' }"
                        @click="selectPaymentMethod('貨到付款')">貨到付款</button>
                </div>
            </div>

            <div class="checkout-section">
                <h3>總金額</h3>
                <p>商品總金額: ${{ totalAmount }}</p>
                <p>優惠券折抵: ${{ couponApplied }}</p>
                <p class="total">付款金額: ${{ totalAmount - couponApplied }}</p>
                <div class="payment-methods">
                    <button @click="placeOrder">前往結帳</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import axiosapi from '@/plugins/axios.js';
import { useRouter } from 'vue-router';
import { useCartStore } from '@/stores/cartStore';
import Swal from 'sweetalert2';

const router = useRouter();
const cartStore = useCartStore();
const cartItems = ref([]);
const couponCodeInput = ref('');
const appliedCouponName = ref('');
const couponApplied = ref(0); // 實際折扣金額
const selectedPaymentMethod = ref('');
const appliedCouponId = ref(null);

const redeemCoupon = async () => {
    if (!couponCodeInput.value) {
        Swal.fire({
            icon: 'warning',
            title: '提醒',
            text: '請輸入優惠券編號！',
            confirmButtonText: '確定',
            confirmButtonColor: '#66bb6a', // 自訂按鈕顏色
        });
        return;
    }

    try {
        const response = await axiosapi.get(`/api/coupons/${couponCodeInput.value}`);
        const { couponId, name, discountAmount } = response.data;
        appliedCouponId.value = couponId;
        appliedCouponName.value = name;
        couponApplied.value = discountAmount;
        Swal.fire('優惠券使用成功', `折扣 $${discountAmount}`, 'success');
    } catch (error) {
        appliedCouponId.value = null;
        appliedCouponName.value = '';
        couponApplied.value = 0;
        Swal.fire('優惠券錯誤', error.response?.data || '找不到該優惠券', 'error');
    }
};

const clearCoupon = () => {
    couponCodeInput.value = '';
    appliedCouponId.value = null;
    appliedCouponName.value = '';
    couponApplied.value = 0;
};

onMounted(() => {
    cartItems.value = cartStore.selectedItems;
});

watch(
    () => cartStore.selectedItems,
    (newItems) => {
        cartItems.value = newItems;

        // 如果真的空的才跳轉（而且是資料同步完成後才判斷）
        if (cartItems.value.length === 0) {
            router.push({ name: 'ShoppingCart' });
        }
    },
    { immediate: true } // 加這個，讓 watch 一開始就會執行一次
);

// 計算商品總金額
const totalAmount = computed(() => {
    return cartItems.value.reduce((total, item) => total + item.price * item.quantity, 0);
});

// 選擇付款方式
const selectPaymentMethod = (method) => {
    selectedPaymentMethod.value = method;
};

// 下訂單邏輯
const placeOrder = async () => {
    if (!selectedPaymentMethod.value) {
        Swal.fire({
            icon: 'warning',
            title: '提醒',
            text: '請選擇付款方式！',
            confirmButtonText: '確定',
            confirmButtonColor: '#66bb6a', // 自訂按鈕顏色
        });
        return;
    }

    const orderData = {
        userName: cartStore.userName,
        couponId: appliedCouponId.value, // 根據需求填入優惠券 ID
        totalPrice: totalAmount.value,
        discountAmount: couponApplied.value,
        finalPrice: totalAmount.value - couponApplied.value,
        status: '已付款', // 可以依需求填寫初始狀態
        paymentMethod: selectedPaymentMethod.value,
        items: cartItems.value.map(item => ({
            productName: item.name,
            price: item.price,
            quantity: item.quantity,
            totalPrice: item.price * item.quantity
        }))
    };

    if (selectedPaymentMethod.value === '信用卡') {
        try {

            Swal.fire({
                title: '載入中',
                text: '正在為您導向付款頁面...',
                allowOutsideClick: false,
                allowEscapeKey: false,
                didOpen: () => {
                    Swal.showLoading();
                }
            });

            // 發送請求到後端獲得綠界付款表單
            const response = await axiosapi.post('/api/ecpay/checkout', orderData);
            const formHtml = response.data;

            // 在頁面動態插入並提交表單
            const div = document.createElement('div');
            div.innerHTML = formHtml;
            document.body.appendChild(div);
            div.querySelector('form').submit(); // 自動送出表單
        } catch (error) {
            Swal.close();
            Swal.fire({
                icon: 'error',
                title: '錯誤',
                text: '請求付款頁面失敗，請稍後再試。',
                confirmButtonText: '確定',
                confirmButtonColor: '#f44336', // 自訂按鈕顏色
            });
            return;
        }
        axiosapi.post('/api/orders', orderData)
    }
    else {
        axiosapi.post('/api/orders', orderData)
            .then(response => {
                Swal.fire({
                    icon: 'success',
                    title: '成功',
                    text: '訂單已成功提交！',
                    confirmButtonText: '確定',
                    confirmButtonColor: '#66bb6a',
                }).then(() => {
                    router.push({ name: 'OrderSuccess' }); // 👈 這裡改成你要跳轉的頁面
                });
            })
            .catch(error => {
                Swal.fire({
                    icon: 'error',
                    title: '錯誤',
                    text: '訂單提交失敗，請稍後再試。',
                });
            });
    }
}
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

.container {
    max-width: 900px;
    margin: 20px auto;
    background: white;
    padding: 20px;
    border-radius: 10px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.checkout-section {
    margin-top: 20px;
    padding: 15px;
    border: 1px solid #c8e6c9;
    border-radius: 5px;
}

.title {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 20px;
}

img {
    width: 80px;
    height: auto;
    border-radius: 5px;
}

table {
    width: 100%;
    border-collapse: collapse;
}

th,
td {
    padding: 15px;
    text-align: left;
    border-bottom: 1px solid #c8e6c9;
}

th {
    background-color: #dcedc8;
    font-weight: bold;
}

.total {
    font-size: 20px;
    font-weight: bold;
    color: black;
}

.payment-methods button {
    padding: 10px 15px;
    border: none;
    margin-right: 10px;
    cursor: pointer;
    background: #66bb6a;
    color: white;
    border-radius: 5px;
}

.payment-methods button:hover {
    background: #4caf50;
}

.payment-methods button.selected {
    background-color: #2e7d32;
    /* 深綠色 */
    font-weight: bold;
    box-shadow: 0 0 0 2px #a5d6a7;
}

.coupon-input-group {
    display: flex;
    align-items: center;
    gap: 10px;
    /* 這裡控制間距 */
    margin-bottom: 10px;
}

.coupon-input-group input {
    width: 200px;
    padding: 8px;
    border: 1px solid #ccc;
    border-radius: 5px;
}

.coupon-input-group button {
    padding: 8px 12px;
    border: none;
    background-color: #66bb6a;
    color: white;
    border-radius: 5px;
    cursor: pointer;
}

.coupon-input-group button:hover {
    background-color: #4caf50;
}

.cancel-coupon {
    margin-top: 5px;
    padding: 6px 12px;
    background-color: #ef5350;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
}
.cancel-coupon:hover {
    background-color: #d32f2f;
}
</style>