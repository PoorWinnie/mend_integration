<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <div class="error-code">404</div>
      <h1>頁面未找到</h1>
      <p>很抱歉，您訪問的頁面不存在或已被移除。</p>
      <div class="action-buttons">
        <router-link to="/" class="btn-home">
          <i class="fa fa-home"></i> 返回首頁
        </router-link>
        <button @click="goBack" class="btn-back">
          <i class="fa fa-arrow-left"></i> 返回上一頁
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NotFoundView',
  methods: {
    goBack() {
      this.$router.go(-1);
    }
  }
}
</script>

<style scoped>
.not-found-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  padding: 20px;
  background-color: #f8f9fa;
}

.not-found-content {
  text-align: center;
  max-width: 500px;
  padding: 40px;
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.error-code {
  font-size: 100px;
  font-weight: bold;
  color: #3b82f6;
  margin-bottom: 20px;
}

h1 {
  color: #2c3e50;
  margin-bottom: 20px;
}

p {
  color: #6b7280;
  margin-bottom: 30px;
  font-size: 18px;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.btn-home, .btn-back {
  display: inline-flex;
  align-items: center;
  padding: 10px 20px;
  border-radius: 5px;
  text-decoration: none;
  transition: all 0.3s;
  font-size: 16px;
}

.btn-home {
  background-color: #0fb886;
  color: white;
  border: none;
}

.btn-back {
  border: 1px solid #0fb886;
  color: #0fb886;
  background: none;
  cursor: pointer;
}

.btn-home:hover {
  background-color: #0d9e75;
}

.btn-back:hover {
  background-color: rgba(15, 184, 134, 0.1);
}

.btn-home i, .btn-back i {
  margin-right: 8px;
}

@media (max-width: 480px) {
  .action-buttons {
    flex-direction: column;
    gap: 10px;
  }
}
</style> 