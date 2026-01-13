<template>
  <div class="container">
    <h2 class="my-4">搜尋結果</h2>
    <BookingDateSummary />

    <div
      v-if="loading"
      class="d-flex justify-content-center align-items-center my-5"
      style="height: 200px"
    >
      <div class="text-center">
        <div
          class="spinner-border text-primary"
          role="status"
          style="width: 3rem; height: 3rem"
        >
          <span class="visually-hidden">Loading...</span>
        </div>
        <div class="mt-3 text-muted fs-5">營位資料載入中...</div>
      </div>
    </div>

    <div v-else-if="groupedResults.length === 0">查無符合條件的營區</div>
    <div v-else class="row">
      <!-- 左側：營位卡片 -->
      <div class="col-lg-8">
        <!-- 顯示目前載入狀態 -->
        <p class="text-muted">
          已載入 {{ results.length }} / {{ totalCount }} 筆營位
        </p>

        <div
          v-for="group in groupedResults"
          :key="group.campAreaId"
          class="card mb-4 p-3 shadow-sm"
        >
          <div class="row g-3">
            <div
              class="col-md-4 d-flex justify-content-center align-items-center"
            >
              <img
                :src="getImageUrl(group.campAreaImageUrl)"
                alt="營區圖片"
                class="img-fluid rounded"
                @error="handleImageError($event)"
              />
            </div>
            <div class="col-md-8">
              <h5>{{ group.campAreaName }}</h5>
              <p class="text-muted">地址：{{ group.address }}</p>
              <ul class="list-group list-group-flush">
                <li
                  v-for="spot in group.spots"
                  :key="spot.campSpotId"
                  class="list-group-item d-flex justify-content-between align-items-center"
                >
                  <div>
                    <strong>{{ spot.spotName }}</strong
                    >｜ 可訂帳數：{{ spot.availableTents }}｜ 平日：{{
                      spot.pricePerNight
                    }}｜ 假日：{{ spot.priceWeekend }}
                  </div>

                  <div class="d-flex align-items-center">
                    <label class="me-2">帳數：</label>
                    <select
                      :value="getSelectedQuantity(spot.campSpotId)"
                      @change="
                        (e) => updateQuantity(spot, Number(e.target.value))
                      "
                    >
                      <option value="0">0</option>
                      <option v-for="n in spot.maxTents" :key="n" :value="n">
                        {{ n }}
                      </option>
                    </select>
                  </div>
                </li>
              </ul>
            </div>
          </div>
        </div>

        <!-- 載入更多按鈕 -->
        <div v-if="hasMore && !loadingMore" class="text-center my-3">
          <button class="btn btn-outline-primary" @click="loadMore">
            載入更多營位
          </button>
        </div>

        <!-- 沒有更多資料時顯示 -->
        <div v-if="!hasMore" class="text-center text-muted my-3">
          <span>已經到底囉 🚩</span>
        </div>
      </div>

      <!-- 右側：購物車 -->
      <div class="col-lg-4">
        <BookingCart />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import BookingCart from "@/components/BookingCart.vue";
import BookingDateSummary from "@/components/BookingDateSummary.vue";
import axios from "@/plugins/axios.js";
import { useCampSearchStore } from "@/stores/campSearchStore";
import { useBookingCartStore } from "@/stores/bookingCartStore";
import { storeToRefs } from "pinia";

const cartStore = useBookingCartStore();
const { selectedSpots } = storeToRefs(cartStore);

const results = ref([]);
const loading = ref(true);
const loadingMore = ref(false);
const currentPage = ref(0);
const hasMore = ref(true);
const totalCount = ref(0);

const searchStore = useCampSearchStore();
const payload = searchStore.searchPayload;

const checkInDate = computed(() => searchStore.searchPayload?.checkInDate);
const checkOutDate = computed(() => searchStore.searchPayload?.checkOutDate);

const getImageUrl = (url) => {
  return url ? `${import.meta.env.VITE_API_URL}${url}` : "/img/no-image.png";
};

const handleImageError = (event) => {
  event.target.src = "/img/no-image.png";
};

const getSelectedQuantity = (spotId) => {
  const match = selectedSpots.value.find((s) => s.spotId === spotId);
  return match?.quantity || 0;
};

const updateQuantity = (spot, quantity) => {
  const inDate = new Date(checkInDate.value);
  const outDate = new Date(checkOutDate.value);
  const totalDays = [];
  let current = new Date(inDate);

  while (current < outDate) {
    const date = new Date(current);
    const isHoliday = date.getDay() === 0 || date.getDay() === 6;
    const unit = isHoliday ? spot.priceWeekend : spot.pricePerNight;

    totalDays.push({
      date: date.toISOString().split("T")[0],
      isHoliday,
      unitPrice: unit,
    });

    current.setDate(current.getDate() + 1);
  }

  const subtotal =
    totalDays.reduce((sum, d) => sum + d.unitPrice, 0) * quantity;

  cartStore.updateSpotQuantity(spot.campSpotId, {
    spotId: spot.campSpotId,
    spotName: spot.spotName,
    quantity,
    unitPrice: "-", // 單價可自訂表示
    subtotal,
    dateDetails: totalDays,
  });
};

// 分組：將 results 根據 campAreaId 分組成 groupedResults
const groupedResults = computed(() => {
  const map = new Map();
  for (const item of results.value) {
    if (!map.has(item.campAreaId)) {
      map.set(item.campAreaId, {
        campAreaId: item.campAreaId,
        campAreaName: item.campAreaName,
        address: item.address,
        campAreaImageUrl: item.campAreaImageUrl,
        spots: [],
      });
    }
    map.get(item.campAreaId).spots.push(item);
  }
  return Array.from(map.values());
});

const loadResults = async (append = false) => {
  if (!payload) return;

  const rows = payload.rows || 6;
  const start = currentPage.value * rows;

  try {
    loadingMore.value = true;

    const res = await axios.post("/api/public/camp-spots/search", {
      ...payload,
      start,
      rows,
    });

    const newList = res.data.list || [];

    if (append) {
      results.value.push(...newList);
    } else {
      currentPage.value = 0; // reset
      results.value = newList;
    }

    totalCount.value = res.data.count || 0;
    hasMore.value = results.value.length < totalCount.value;
  } catch (err) {
    console.error("搜尋失敗", err);
    hasMore.value = false;
  } finally {
    loading.value = false;
    loadingMore.value = false;
  }
};

const loadMore = async () => {
  currentPage.value++;
  await loadResults(true);
};

onMounted(async () => {
  if (!payload) {
    console.warn("⚠️ 沒有搜尋條件！");
    loading.value = false;
    return;
  }
  await loadResults(); // 初始載入
});
</script>

<style>
.card {
  border-radius: 12px;
}

img.img-fluid {
  max-height: 200px;
  object-fit: cover;
}
</style>
