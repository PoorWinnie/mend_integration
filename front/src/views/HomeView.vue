<template>
  <div class="container">
    <BannerComponent />
    <SearchName @search="handleSearch" />
    <SearchFilters />
    <!-- 特色篩選 -->
    <div class="filter-card">
      <div class="filter-card-title">特色篩選</div>
      <SearchFeature
        :features="allFeatures"
        v-model:selectedFeatures="selectedFeatureIds"
      />
    </div>

    <!-- 區域 + 海拔篩選 -->
    <div class="filter-card">
      <div class="filter-row">
        <SearchRegion v-model="selectedRegion" />
        <SearchAltitude v-model="selectedAltitude" />
      </div>
    </div>
    <!-- 清除篩選條件按鈕 -->
    <button class="btn btn-orange" @click="clearFilters">
      清除所有篩選條件
    </button>

    <div class="d-flex justify-content-front align-items-center mb-3">
      <label for="rowsPerPage" class="me-2">每頁顯示</label>
      <select
        id="rowsPerPage"
        v-model="rows"
        @change="onRowsChange"
        class="form-select w-auto"
      >
        <option :value="5">5</option>
        <option :value="10">10</option>
        <option :value="20">20</option>
      </select>

      <div class="mb-3">
        <div class="info-summary">
          目前找到 <span class="highlight">{{ total }}</span> 筆營區， 總共
          <span class="highlight">{{ totalPages }}</span> 頁。
        </div>
      </div>
    </div>

    <CampList
      ref="campListRef"
      :page-size="rows"
      v-model:total="total"
      v-model:totalPages="totalPages"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import SearchFilters from "@/components/SearchFilters.vue";
import CampList from "@/components/CampList.vue";
import SearchName from "@/components/SearchName.vue";
import SearchFeature from "@/components/SearchFeature.vue";
import axiosapi from "@/plugins/axios.js";
import BannerComponent from "@/components/BannerComponent.vue";
import SearchRegion from "@/components/SearchRegion.vue";
import SearchAltitude from "@/components/SearchAltitude.vue";

const campListRef = ref(null);
const allFeatures = ref([]);
const selectedFeatureIds = ref([]);
const keyword = ref("");
const selectedRegion = ref(null);
const selectedAltitude = ref(null);
const currentPage = ref(1);
const rows = ref(10);
const total = ref(0);
const totalPages = ref(1);




function onRowsChange() {
  currentPage.value = 1;
  const alt = selectedAltitude.value ? [selectedAltitude.value] : [];
  (currentPage.value = 1),
    campListRef.value.callFind(
      keyword.value,
      selectedFeatureIds.value,
      selectedRegion.value,
      alt,
      currentPage.value
      // rows.value
    );
}

async function fetchFeatures() {
  const response = await axiosapi.get("/api/public/camp-areas/features");
  allFeatures.value = response.data;

}

function handleSearch(kw) {
  keyword.value = kw;
  searchCamps();
}

//監聽selectedFeatureIds變化，就立即執行處理函式
watch([selectedFeatureIds, selectedRegion, selectedAltitude], () => {
  searchCamps();
});

function searchCamps() {
  if (campListRef.value) {
    const alt = selectedAltitude.value ? [selectedAltitude.value] : [];
    (currentPage.value = 1),
      campListRef.value.callFind(
        keyword.value,
        selectedFeatureIds.value,
        selectedRegion.value,
        alt,
        currentPage.value
        // rows.value
      );
  }
}

function clearFilters() {
  // 清空父層的選取條件
  keyword.value = "";
  selectedFeatureIds.value = [];
  selectedRegion.value = null;
  selectedAltitude.value = null;

  // 呼叫 CampList 子元件的清除方法
  if (campListRef.value?.clearFilters) {
    campListRef.value.clearFilters();
  }
}


onMounted(async () => {
  await fetchFeatures(); // 等特徵標籤載入完再查營地
  searchCamps();
});
</script>

<style scoped>
.page-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

.filter-card {
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 20px;
  background-color: white;
}

.filter-card-title {
  font-weight: bold;
  font-size: 20px;
  margin-bottom: 12px;
}

.filter-row {
  display: flex;
  gap: 50px;
  flex-wrap: wrap;
  align-items: flex-start;
  height: 100px;
}

.info-summary {
  font-size: 18px;
  margin-top: 20px;
  margin-left: 20px;
  color: #333;
}

.highlight {
  color: #007bff; /* 藍色 */
  font-weight: bold;
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
