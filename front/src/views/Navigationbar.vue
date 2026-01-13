<template>
    <nav class="navbar navbar-expand-lg bg-body-tertiary">
        <div class="container-fluid">
            <span class="navbar-brand">Front</span>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNavDropdown" aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNavDropdown">
                <ul class="navbar-nav">
                    <li class="nav-item">
                        <RouterLink class="nav-link active" aria-current="page" to="/productBack">產品管理</RouterLink>
                    </li>
                    <li class="nav-item">
                        <RouterLink class="nav-link" to="/productFront">產品前台</RouterLink>
                    </li>
                    <!-- 若未來有登入功能可啟用
                    <li class="nav-item">
                        <RouterLink class="nav-link" aria-current="page" to="/secure/login">登入</RouterLink>
                    </li>
                    -->
                    <!-- 這裡可根據 userStore 狀態顯示更多功能 -->
                </ul>
            </div>
        </div>
    </nav>
</template>
    
<script setup>
    // 若有 user 狀態管理可啟用
    // import useUserStore from '@/stores/user.js';
    // const userStore = useUserStore();
</script>
    
<style>
/* 可依需求自訂樣式 */
</style>
