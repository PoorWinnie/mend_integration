<template>
  <div class="container my-5" v-if="campArea">
    <!-- 1. 營區名稱 -->
    <div class="text-center mb-4">
      <h1 class="display-5">{{ campArea.campAreaName }}</h1>
      <p class="text-muted">{{ campArea.location }}</p>
      <button class="btn btn-primary ms-3" @click="goToReviews">
        <i class="bi bi-star-fill me-1"></i> 查看評價
      </button>
    </div>

    <!-- 2. 營區基本資訊 -->
    <div class="row mb-4">
      <div class="col-md-6">
        <ul class="list-group">
          <li class="list-group-item">
            <strong>地址：</strong>{{ campArea.address }}
          </li>
          <li class="list-group-item">
            <strong>電話：</strong>{{ campArea.phone }}
          </li>
          <li class="list-group-item">
            <strong>Email：</strong>{{ campArea.email }}
          </li>
          <li class="list-group-item">
            <strong>網站：</strong>
            <a :href="campArea.website_url" target="_blank">{{
              campArea.websiteUrl
            }}</a>
          </li>
        </ul>
      </div>
      <div class="col-md-6">
        <ul class="list-group">
          <li class="list-group-item">
            <strong>海拔：</strong>{{ campArea.altitude }} 公尺
          </li>
          <li class="list-group-item">
            <strong>交通方式：</strong>{{ campArea.transportation }}
          </li>
          <li class="list-group-item">
            <strong>注意事項：</strong>{{ campArea.notice }}
          </li>
          <li class="list-group-item">
            <strong>固定休假日：</strong>{{ readableWeekdays }}
          </li>
        </ul>
      </div>
    </div>

    <!-- 3. 營區圖片區 -->
    <div class="mb-5">
      <h2 class="mb-3">營區圖片</h2>
      <div class="row g-3">
        <div
          class="col-6 col-md-4"
          v-for="(img, index) in campArea.mainImageUrl"
          :key="index"
        >
          <img
            :src="path + img.imageUrl"
            class="img-fluid rounded shadow-sm camp-image"
            alt="營區圖片"
          />
        </div>
      </div>
    </div>

    <!-- 營位資訊表格 -->
    <div
      class="mb-5"
      v-if="campArea.campSites && campArea.campSites.length > 0"
    >
      <h2 class="mb-3">營地資訊</h2>
      <table class="table table-hover">
        <thead>
          <tr>
            <th>營地名稱</th>
            <th>型態</th>
            <th>尺寸</th>
            <th>帳數</th>
            <th>平日</th>
            <th>假日</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="site in sortedCampSites" :key="site.campSiteId">
            <td>
              <!-- 傳到modal裡的已經是spot了取值就 spot.xxx -->
              <button
                class="btn btn-link p-0"
                @click="openSpotModal(site.campSpots?.[0])"
              >
                {{ site.campSiteName }}
              </button>
            </td>
            <td>{{ site.siteType }}</td>
            <td>{{ site.campSpots?.[0]?.areaSize ?? "-" }}</td>
            <td>{{ site.campSpots?.[0]?.maxTents ?? "-" }}</td>
            <td>${{ site.campSpots?.[0]?.pricePerNight ?? "-" }}</td>
            <td>${{ site.campSpots?.[0]?.priceHoliday ?? "-" }}</td>
          </tr>
        </tbody>
      </table>
    </div>
    <CampSpotModal v-model:spot="selectedSpot" />
    <!-- 設施資訊卡片 -->
    <div
      class="mb-5"
      v-if="
        (campArea.facilityNames?.length || 0) +
          (campArea.wifiNames?.length || 0) +
          (campArea.bathroomNames?.length || 0) +
          (campArea.parkingNames?.length || 0) +
          (campArea.petPolicyNames?.length || 0) >
        0
      "
    >
      <h2 class="mb-4">設施資訊</h2>
      <div class="row g-4">
        <!-- 設備 -->
        <div class="col-md-4" v-if="campArea.facilityNames?.length > 0">
          <div class="card h-100 shadow-sm">
            <div class="card-body">
              <h5 class="card-title"><i class="bi bi-tools me-2"></i>設備</h5>
              <ul class="list-unstyled mb-0">
                <li v-for="(item, i) in campArea.facilityNames" :key="i">
                  - {{ item }}
                </li>
              </ul>
            </div>
          </div>
        </div>

        <!-- Wi-Fi -->
        <div class="col-md-4" v-if="campArea.wifiNames?.length > 0">
          <div class="card h-100 shadow-sm">
            <div class="card-body">
              <h5 class="card-title"><i class="bi bi-wifi me-2"></i>Wi-Fi</h5>
              <ul class="list-unstyled mb-0">
                <li v-for="(item, i) in campArea.wifiNames" :key="i">
                  - {{ item }}
                </li>
              </ul>
            </div>
          </div>
        </div>

        <!-- 浴室 -->
        <div class="col-md-4" v-if="campArea.bathroomNames?.length > 0">
          <div class="card h-100 shadow-sm">
            <div class="card-body">
              <h5 class="card-title"><i class="bi bi-shower me-2"></i>浴室</h5>
              <ul class="list-unstyled mb-0">
                <li v-for="(item, i) in campArea.bathroomNames" :key="i">
                  - {{ item }}
                </li>
              </ul>
            </div>
          </div>
        </div>

        <!-- 停車場 -->
        <div class="col-md-6" v-if="campArea.parkingNames?.length > 0">
          <div class="card h-100 shadow-sm">
            <div class="card-body">
              <h5 class="card-title">
                <i class="bi bi-car-front me-2"></i>停車場
              </h5>
              <ul class="list-unstyled mb-0">
                <li v-for="(item, i) in campArea.parkingNames" :key="i">
                  - {{ item }}
                </li>
              </ul>
            </div>
          </div>
        </div>

        <!-- 寵物政策 -->
        <div class="col-md-6" v-if="campArea.petPolicyNames?.length > 0">
          <div class="card h-100 shadow-sm">
            <div class="card-body">
              <h5 class="card-title"><i class="bi bi-paw me-2"></i>寵物政策</h5>
              <ul class="list-unstyled mb-0">
                <li v-for="(item, i) in campArea.petPolicyNames" :key="i">
                  - {{ item }}
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
      <!-- 營區地圖 -->
      <!-- 改用 v-show 確保 #map 會被渲染出來 -->
      <div
        class="mb-5"
        id="map"
        v-show="campArea?.coordinates"
        style="width: 100%; height: 400px"
      ></div>
    </div>
  </div>

  <!-- 載入中 -->
  <div v-else class="text-center my-5">
    <div class="spinner-border text-primary" role="status">
      <span class="visually-hidden">載入中...</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from "vue";
import { useRoute } from "vue-router";
import axiosapi from "@/plugins/axios.js";
import CampSpotModal from "@/components/CampSpotModal.vue";
import { nextTick } from "vue";

const campArea = ref(null);
const selectedSpot = ref(null);
const path = import.meta.env.VITE_API_URL || "";
const route = useRoute();

const weekdayNames = [
  "星期日",
  "星期一",
  "星期二",
  "星期三",
  "星期四",
  "星期五",
  "星期六",
];

function openSpotModal(spot) {
  selectedSpot.value = spot;
}

const readableWeekdays = computed(() => {
  if (!campArea.value?.offWeekdays || campArea.value.offWeekdays.length === 0)
    return "無";
  return campArea.value.offWeekdays
    .map((index) => weekdayNames[index])
    .join("、");
});

const sortedCampSites = computed(() => {
  if (!campArea.value?.campSites) return [];
  return [...campArea.value.campSites].sort((a, b) => {
    const aId = a.campSpots?.[0]?.campSpotId ?? Infinity;
    const bId = b.campSpots?.[0]?.campSpotId ?? Infinity;
    return aId - bId;
  });
});

onMounted(async () => {
  const id = route.params.id;
  const response = await axiosapi.get(`/api/public/camp-areas/${id}`);
  campArea.value = response.data.list[0];
});

// 經緯度解析工具：從 "POINT (121.5514 24.8665)" 拆成 { lat, lng }
function parseCoordinates(pointString) {
  const match = pointString?.match(/\(([-\d.]+)\s+([-\d.]+)\)/);
  if (match) {
    const lng = parseFloat(match[1]);
    const lat = parseFloat(match[2]);
    return { lat, lng };
  }
  return null;
}

// 載入 Google Maps 並初始化地圖
function loadGoogleMapsScript(callback) {
  if (document.getElementById("google-maps")) {
    callback(); // 已載入過
    return;
  }
  const script = document.createElement("script");
  script.id = "google-maps";
  script.src = `https://maps.googleapis.com/maps/api/js?key=AIzaSyC5rqMAiOgPsitGVPV5Ph2Q05GJIZFBe4E&callback=initMap`;
  script.async = true;
  window.initMap = callback;
  document.body.appendChild(script);
}

function initMapWithCampArea() {
  const coords = parseCoordinates(campArea.value?.coordinates);
  if (!coords) return;

  const map = new google.maps.Map(document.getElementById("map"), {
    center: coords,
    zoom: 14,
  });

  new google.maps.Marker({
    position: coords,
    map,
    title: campArea.value.campAreaName,
  });
}

watch(campArea, async (val) => {
  if (val?.coordinates) {
    await nextTick(); // 等待 DOM 中的 #map 被掛載進來
    loadGoogleMapsScript(initMapWithCampArea);
  }
});
</script>

<style scoped>
.camp-image {
  width: 350px;
  height: 230px;
  object-fit: cover; /* 重點：保持比例，但會裁切 */
  object-position: center; /* 圖片以中心點對齊裁切 */
}

#map {
  margin-top: 50px;
}
</style>
