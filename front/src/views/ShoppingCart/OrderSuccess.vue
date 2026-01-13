<template>
    <div>
        <div class="header">🏕️ 訂單完成</div>
        <div class="container">
            <div class="checkmark-circle">
                <div class="checkmark">✓</div>
            </div>
            <div class="success-title">訂單已成功提交</div>
            <div class="thank-you">
                <p style="margin-bottom: 16px;">
                    感謝您的訂購，您的訂單已成功處理並提交。
                </p>
                <p>
                    如果你有任何疑問，請隨時聯繫我們的客服團隊
                </p>
            </div>
            <button class="home-button" @click="goHome">返回首頁</button>
        </div>
    </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
const goHome = () => {
    router.push({
            name: 'home',  // 這是你 Vue Router 中設定的名稱
        });
}
</script>

<style scoped>
body {
    font-family: Arial, sans-serif;
    background-color: #e8f5e9;
    margin: 0;
    padding: 20px;
}

.header {
    background-color: #81c784;
    color: #fffde7;
    padding: 30px 15px;
    text-align: center;
    font-size: 28px;
    font-weight: bold;
    font-family: 'Segoe UI', 'Microsoft JhengHei', 'Arial Rounded MT Bold', sans-serif;
    letter-spacing: 1px;
}

.container {
    max-width: 600px;
    margin: 40px auto;
    background: white;
    padding: 40px 20px;
    border-radius: 10px;
    text-align: center;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.checkmark-circle {
    width: 100px;
    height: 100px;
    border-radius: 50%;
    background: #66bb6a;
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 0 auto 20px;
}

.checkmark {
    font-size: 50px;
    color: white;
}

.success-title {
    font-size: 24px;
    font-weight: bold;
    color: #2e7d32;
    margin-bottom: 10px;
}

.thank-you {
    font-size: 18px;
    color: #555;
    margin-bottom: 30px;
}

.home-button {
    padding: 12px 24px;
    font-size: 16px;
    background-color: #66bb6a;
    color: white;
    border: none;
    border-radius: 5px;
    text-decoration: none;
    display: inline-block;
    transition: background-color 0.3s ease;
    cursor: pointer;
}

.home-button:hover {
    background-color: #4caf50;
}
</style>