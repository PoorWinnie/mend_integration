<template>
  <div class="forbidden-container">
    <div class="forbidden-content">
      <div class="error-code">403</div>
      <h1>無權限訪問</h1>
      <p>很抱歉，您沒有權限訪問此頁面。</p>
      <div class="action-buttons">
        <router-link to="/" class="btn-home">
          <i class="fa fa-home"></i> 返回首頁
        </router-link>
        <router-link to="/login" class="btn-login">
          <i class="fa fa-sign-in-alt"></i> 重新登入
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ForbiddenView'
}
</script>

<style scoped>
.forbidden-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  padding: 20px;
  background-color: #f8f9fa;
}

.forbidden-content {
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
  color: #ef4444;
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

.btn-home, .btn-login {
  display: inline-flex;
  align-items: center;
  padding: 10px 20px;
  border-radius: 5px;
  text-decoration: none;
  transition: all 0.3s;
}

.btn-home {
  background-color: #0fb886;
  color: white;
}

.btn-login {
  border: 1px solid #0fb886;
  color: #0fb886;
}

.btn-home:hover {
  background-color: #0d9e75;
}

.btn-login:hover {
  background-color: rgba(15, 184, 134, 0.1);
}

.btn-home i, .btn-login i {
  margin-right: 8px;
}

@media (max-width: 480px) {
  .action-buttons {
    flex-direction: column;
    gap: 10px;
  }
}
</style> 