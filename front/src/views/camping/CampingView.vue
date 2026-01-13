<template>
  <div class="camping-container">
    <!-- Banner 輪播 -->
    <BannerComponent />

    <!-- 搜尋篩選區塊 -->
    <div class="section">
      <SearchFilters />
    </div>

    <div class="section">
      <SearchName @search="handleSearch" />
    </div>

    <div class="section">
      <SearchFeature :features="allFeatures" @update:selectedFeatures="handleFeatureSelect" />
    </div>

    <!-- 營地列表 -->
    <div class="section">
      <CampList ref="campListRef" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import SearchFilters from '@/components/SearchFilters.vue';
import CampList from '@/components/CampList.vue';
import SearchName from '@/components/SearchName.vue';
import SearchFeature from '@/components/SearchFeature.vue';
import BannerComponent from '@/components/BannerComponent.vue';
import axiosapi from '@/plugins/axios.js';

const campListRef = ref(null);
const allFeatures = ref([]);
const selectedFeatureIds = ref([]);
const keyword = ref('');

async function fetchFeatures() {
  const response = await axiosapi.get("/api/public/camp-areas/features");
  allFeatures.value = response.data;
}

function handleSearch(kw) {
  keyword.value = kw;
  searchCamps();
}

function handleFeatureSelect(ids) {
  selectedFeatureIds.value = ids;
  searchCamps();
}

function searchCamps() {
  if (campListRef.value) {
    campListRef.value.callFind(keyword.value, selectedFeatureIds.value);
  }
}

// 日期顯示格式函數（避免 undefined）
const formatRange = (range) => {
  if (!Array.isArray(range) || range.length !== 2 || !range[0] || !range[1]) {
    return '';
  }

  const format = (date) => {
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    return `${month}月${day}日`;
  };

  return `${format(range[0])} - ${format(range[1])}`;
};


onMounted(async () => {
  await fetchFeatures();
  searchCamps();

});
</script>

<style scoped>

</style>
