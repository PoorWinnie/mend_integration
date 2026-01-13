<template>
  <div class="search-filters">
    <h2 class="search-title">搜尋條件</h2>
    <div class="filters-container">
      <!-- 縣市 -->
      <div class="filter-group">
        <select v-model="selectedCity">
          <option value="">全部縣市</option>
          <option v-for="city in cities" :key="city" :value="city">
            {{ city }}
          </option>
        </select>
      </div>

      <!-- 行政區 -->
      <div class="filter-group">
        <select v-model="selectedDistrict" :disabled="!districts.length">
          <option value="">全部行政區</option>
          <option v-for="area in districts" :key="area" :value="area">
            {{ area }}
          </option>
        </select>
      </div>

      <!-- 日期選擇器區塊 -->
      <div class="filter-group">
        <VueDatePicker
          v-model="dateRange"
          :format="formatRange"
          :range="true"
          :clearable="true"
          placeholder="選擇入住與退房日期"
          :min-date="new Date()"
          :enable-time-picker="false"
          :auto-apply="true"
          :teleport="false"
          class="custom-datepicker"
          locale="zh"
        />
      </div>

      <!-- 帳數 -->
      <div class="filter-group">
        <select v-model="selectedTentCount">
          <option value="0">不限帳數</option>
          <option value="1">1 帳</option>
          <option value="2">2 帳</option>
          <option value="3">3 帳</option>
          <option value="4">4 帳以上</option>
        </select>
      </div>

      <!-- 海拔高度 -->
      <div class="filter-group">
        <select v-model="selectedAltitudeRange">
          <option value="">不限海拔</option>
          <option value="0-299">300m 以下</option>
          <option value="300-500">300-500</option>
          <option value="501-800">501-800</option>
          <option value="801-1000">800-1000</option>
          <option value="1001-9999">1000 以上</option>
        </select>
      </div>

      <!-- 搜尋按鈕 -->
      <button class="btn btn-success me-2" @click="searchStores">搜尋</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import VueDatePicker from "@vuepic/vue-datepicker";
import "@vuepic/vue-datepicker/dist/main.css";
import cityDistricts from "@/assets/data/CityCountyData.json";
import { useRouter } from "vue-router";
import { useCampSearchStore } from "@/stores/campSearchStore"; // Pinia store

const router = useRouter();
const searchStore = useCampSearchStore();

const cities = ref([]);
const districts = ref([]);
const selectedCity = ref("");
const selectedDistrict = ref("");
const selectedTentCount = ref("0");
const dateRange = ref([]);
const selectedAltitudeRange = ref("");

const formatRange = (range) => {
  if (!Array.isArray(range) || range.length !== 2 || !range[0] || !range[1])
    return "";
  const format = (date) =>
    `${date.getMonth() + 1}月${date.getDate().toString().padStart(2, "0")}日`;
  return `${format(range[0])} - ${format(range[1])}`;
};

onMounted(() => {
  cities.value = cityDistricts.map((city) => city.CityName);
  const today = new Date();
  const tomorrow = new Date();
  tomorrow.setDate(today.getDate() + 1);
  dateRange.value = [today, tomorrow];
});

watch(selectedCity, (newCity) => {
  const found = cityDistricts.find((city) => city.CityName === newCity);
  districts.value = found ? found.AreaList.map((area) => area.AreaName) : [];
  selectedDistrict.value = "";
});

const searchStores = () => {

  const rawRegion =
    selectedCity.value && selectedDistrict.value
      ? selectedCity.value + selectedDistrict.value
      : selectedCity.value || "";

  const region = rawRegion.replace(/臺/g, "台");

  const [rawCheckInDate, rawCheckOutDate] = dateRange.value || [];

  if (!rawCheckInDate || !rawCheckOutDate) {
    window.alert("請選擇入住與退房日期");
    return;
  }

  const formatDate = (date) => date.toISOString().split("T")[0];
  const checkInDate = formatDate(rawCheckInDate);
  const checkOutDate = formatDate(rawCheckOutDate);

  let minAltitude = null;
  let maxAltitude = null;
  if (selectedAltitudeRange.value) {
    const [min, max] = selectedAltitudeRange.value.split("-").map(Number);
    minAltitude = min;
    maxAltitude = max;
  }

  searchStore.setSearchPayload({
    region,
    checkInDate,
    checkOutDate,
    minAltitude,
    maxAltitude,

    requiredTents:
      selectedTentCount.value !== "0" ? Number(selectedTentCount.value) : null,

    start: 0,
    rows: 6,
    sort: "campAreaId",
    dir: true,
  });

  router.push({ name: "camp-spot-booking-search" });
};
</script>


<style scoped>
.search-filters {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-top: 30px;
  margin-bottom: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.search-title {
  font-size: 20px;
  margin-bottom: 15px;
  font-weight: bold;
}

.filters-container {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  align-items: flex-end;
}

.filter-group {
  flex: 1;
  min-width: 150px;
}

select,
input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 14px;
}

/* scoped CSS */
.custom-datepicker {
  width: 100%;
}

.custom-datepicker .dp__input_wrap {
  border: 1px solid #ccc;
  border-radius: 4px;
  background-color: #fff;
  box-shadow: none;
}

.custom-datepicker .dp__input {
  border: none;
  padding: 10px;
  font-size: 14px;
  width: 100%;
  box-shadow: none;
}

.custom-datepicker .dp__input:focus {
  outline: none;
  box-shadow: 0 0 0 2px rgba(255, 127, 0, 0.2);
}

.custom-icon {
  color: #ff7f00;
  font-size: 1.2rem;
  margin-right: 8px;
}
.search-input {
  flex: 2;
  min-width: 200px;
}

.search-btn {
  padding: 10px 20px;
  background-color: #ff7f00;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.search-btn:hover {
  background-color: #e67300;
}

@media (max-width: 768px) {
  .filters-container {
    flex-direction: column;
  }

  .filter-group,
  .search-input {
    width: 100%;
  }
}
</style>
