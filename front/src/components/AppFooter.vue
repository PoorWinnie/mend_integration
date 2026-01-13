<template>
    <!-- 頁尾 -->
    <footer class="app-footer">
        <div class="footer-content">
            <div class="footer-section">
                <h3><i class="fa fa-campground"></i>西米露</h3>
                <p>探索自然，發現美好。</p>
                <div class="social-icons">
                    <a href="#" aria-label="Facebook"><i class="fab fa-facebook"></i></a>
                    <a href="#" aria-label="Instagram"><i class="fab fa-instagram"></i></a>
                    <a href="#" aria-label="Twitter"><i class="fab fa-twitter"></i></a>
                    <a href="#" aria-label="YouTube"><i class="fab fa-youtube"></i></a>
                </div>
            </div>
            <div class="footer-section">
                <h3>快速連結</h3>
                <ul>
                    <li><router-link to="/"><i class="fa fa-home"></i> 首頁</router-link></li>
                    <li><router-link to="/member"><i class="fa fa-user"></i> 會員中心</router-link></li>
                    <li><router-link to="/about"><i class="fa fa-info-circle"></i> 關於我們</router-link></li>
                    <li><router-link to="/faq"><i class="fa fa-question-circle"></i> 常見問題</router-link></li>
                    <li><router-link to="/privacy"><i class="fa fa-shield-alt"></i> 隱私政策</router-link></li>
                </ul>
            </div>
            <div class="footer-section">
                <h3>聯絡我們</h3>
                <p><i class="fa fa-envelope"></i> info@camping.example.com</p>
                <p><i class="fa fa-phone"></i> +886 2 12345678</p>
                <p><i class="fa fa-map-marker-alt"></i> 台北市松山區美好露營路123號</p>
            </div>
            <div class="footer-section">
                <h3>訂閱電子報</h3>
                <p>接收最新露營資訊和優惠</p>
                <div class="newsletter-form">
                    <input type="email" placeholder="您的電子郵件" />
                    <button>訂閱</button>
                </div>
            </div>
        </div>
        <div class="footer-bottom">
            <p>&copy; 2023 露營趣 - 版權所有</p>
        </div>
    </footer>
</template>

<script setup>
// 頁尾通常不需要太多邏輯，如果有需要的話可以添加
</script>

<style scoped>
/* 頁尾樣式 */
.app-footer {
    background-color: var(--primary);
    color: white;
    padding: 50px 30px 20px;
    margin-top: 40px;
    position: relative;
}

.app-footer::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: var(--accent);
}

.footer-content {
    display: flex;
    justify-content: space-between;
    max-width: 1200px;
    margin: 0 auto;
    flex-wrap: wrap;
    gap: 20px;
}

.footer-section {
    margin-bottom: 25px;
    min-width: 200px;
    flex: 1;
}

.footer-section h3 {
    margin-bottom: 20px;
    color: white;
    font-size: 18px;
    font-weight: 700;
    display: flex;
    align-items: center;
    position: relative;
    padding-bottom: 10px;
}

.footer-section h3::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    width: 30px;
    height: 2px;
    background-color: var(--accent);
}

.footer-section h3 i {
    margin-right: 10px;
    color: var(--accent);
}

.footer-section p {
    margin-bottom: 15px;
    color: rgba(255, 255, 255, 0.7);
    line-height: 1.7;
    font-size: 14px;
}

.footer-section ul {
    list-style: none;
}

.footer-section li {
    margin-bottom: 12px;
}

.footer-section a {
    color: rgba(255, 255, 255, 0.7);
    transition: all 0.3s;
    display: flex;
    align-items: center;
    font-size: 14px;
}

.footer-section a i {
    margin-right: 10px;
    width: 16px;
    text-align: center;
    color: var(--accent);
}

.footer-section a:hover {
    color: white;
    transform: translateX(3px);
}

.social-icons {
    display: flex;
    margin-top: 20px;
}

.social-icons a {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background-color: rgba(255, 255, 255, 0.1);
    color: white;
    margin-right: 12px;
    transition: all 0.3s ease;
}

.social-icons a:hover {
    background-color: var(--accent);
    transform: translateY(-3px);
}

.newsletter-form {
    display: flex;
    margin-top: 15px;
    position: relative;
    overflow: hidden;
    border-radius: 4px;
}

.newsletter-form input {
    flex: 1;
    padding: 12px 15px;
    border: none;
    border-radius: 4px 0 0 4px;
    outline: none;
    font-family: inherit;
    font-size: 14px;
    color: var(--text-primary);
}

.newsletter-form button {
    background-color: var(--accent);
    color: white;
    border: none;
    padding: 0 20px;
    border-radius: 0 4px 4px 0;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.3s;
}

.newsletter-form button:hover {
    background-color: var(--accent-hover);
}

.footer-bottom {
    text-align: center;
    margin-top: 30px;
    padding-top: 20px;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
    color: rgba(255, 255, 255, 0.5);
    font-size: 13px;
}

/* 響應式設計 */
@media (max-width: 768px) {
    .footer-content {
        flex-direction: column;
        gap: 25px;
    }

    .footer-section {
        width: 100%;
        margin-bottom: 0;
    }

    .app-footer {
        padding: 40px 20px 20px;
    }
}
</style>