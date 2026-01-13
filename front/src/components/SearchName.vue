<template>
  <div class="search-name">
    <div
      class="d-flex align-items-center w-100 justify-content-center"
      style="max-width: 1000px"
    >
      <input
        type="text"
        class="form-control me-2"
        placeholder="請輸入營區名稱關鍵字"
        v-model="keyword"
        @keyup.enter="handleSearch"
        style="width: 100%; max-width: 600px"
      />
      <button
        class="btn btn-success me-2"
        :disabled="loading"
        @click="handleSearch"
      >
        <span v-if="loading">搜尋中...</span>
        <span v-else>搜尋</span>
      </button>

      <!-- 清除按鈕 -->
      <button
        class="btn btn-orange"
        :disabled="loading || !keyword"
        @click="clearSearch"
      >
        清除
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";

const keyword = ref("");
const loading = ref(false);
const emits = defineEmits(["search"]);

async function handleSearch() {
  loading.value = true;
  await emits("search", keyword.value);
  loading.value = false;
}

function clearSearch() {
  keyword.value = "";
  emits("search", ""); // 發出空字串代表清除搜尋條件
}
</script>

<style scoped>
.search-name {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  margin-bottom: 20px;
}

.btn-orange {
  background-color: #ff7f00; /* 橘色背景 */
  color: white;              /* 白色文字 */
  border: none;
  transition: background-color 0.3s ease;
}

.btn-orange:hover,
.btn-orange:focus {
  background-color: #e86f00; /* 深一點的橘色 */
  color: white;              /* 保持文字白色 */
}

.btn-orange:disabled {
  background-color: #ffc299; /* 淺橘色表示禁用 */
  color: white;              /* 保持文字白色 */
  opacity: 1;                /* 不要自動變淡 */
  cursor: not-allowed;
}

</style>
