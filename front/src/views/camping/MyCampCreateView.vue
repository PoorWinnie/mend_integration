<template>
  <div class="container my-5">
    <h2>新增營區</h2>
    <form @submit.prevent="submitForm">
      <div class="row">
        <div class="mb-3 col-md-6">
          <label class="form-label">營區名稱</label>
          <input v-model="form.campAreaName" class="form-control" />
        </div>

        <div class="mb-3 col-md-6">
          <label class="form-label">地點</label>
          <input v-model="form.location" class="form-control" />
        </div>

        <div class="mb-3 col-md-6">
          <label class="form-label">地址</label>
          <input v-model="form.address" class="form-control" />
        </div>

        <div class="mb-3 col-md-6">
          <label class="form-label">電話</label>
          <input v-model="form.phone" class="form-control" />
        </div>

        <div class="mb-3 col-md-6">
          <label class="form-label">Email</label>
          <input v-model="form.email" class="form-control" />
        </div>

        <div class="mb-3 col-md-6">
          <label class="form-label">交通資訊</label>
          <textarea
            v-model="form.transportation"
            class="form-control"
            rows="2"
          ></textarea>
        </div>

        <div class="mb-3 col-md-6">
          <label class="form-label">營區描述</label>
          <textarea
            v-model="form.description"
            class="form-control"
            rows="3"
          ></textarea>
        </div>

        <div class="mb-3 col-md-6">
          <label class="form-label">座標（POINT 格式）</label>
          <input
            v-model="form.coordinates"
            class="form-control"
            placeholder="POINT (經度 緯度)"
          />
        </div>

        <div class="mb-3 col-md-6">
          <label class="form-label">網站網址</label>
          <input v-model="form.websiteUrl" class="form-control" />
        </div>

        <div class="mb-3 col-md-6">
          <label class="form-label">注意事項</label>
          <textarea
            v-model="form.notice"
            class="form-control"
            rows="2"
          ></textarea>
        </div>

        <div class="mb-3 col-md-6">
          <label class="form-label">海拔高度 (m)</label>
          <input type="number" v-model="form.altitude" class="form-control" />
        </div>
      </div>

      <!-- 圖片上傳與預覽 -->
      <div class="mb-3">
        <label class="form-label">營區圖片</label>
        <input
          type="file"
          accept="image/*"
          multiple
          @change="handleFileUpload"
          class="form-control"
        />

        <div v-if="imagePreviews.length" class="mt-3">
          <Swiper
            class="mySwiper"
            :modules="modules"
            :slides-per-view="3"
            :space-between="20"
            navigation
            :pagination="{ clickable: true }"
            style="
              --swiper-navigation-color: #000;
              --swiper-navigation-size: 20px;
            "
          >
            <SwiperSlide
              v-for="(src, index) in imagePreviews"
              :key="index"
              class="position-relative"
            >
              <img
                :src="src"
                class="img-thumbnail w-100"
                style="height: 350px; object-fit: cover"
              />
              <button
                type="button"
                class="btn btn-sm btn-danger position-absolute top-0 end-0"
                @click="removeImage(index)"
              >
                ✕
              </button>
            </SwiperSlide>
          </Swiper>
        </div>
      </div>
      <!-- 固定休假日 -->
      <div class="mb-3">
        <label class="form-label">固定休假日</label>
        <div class="d-flex flex-wrap gap-3">
          <div
            v-for="(label, idx) in ['日', '一', '二', '三', '四', '五', '六']"
            :key="idx"
            class="form-check"
          >
            <input
              class="form-check-input"
              type="checkbox"
              :value="idx"
              v-model="form.offWeekdays"
              :id="`weekday-${idx}`"
            />
            <label class="form-check-label" :for="`weekday-${idx}`">
              星期{{ label }}
            </label>
          </div>
        </div>
      </div>

      <!-- 特定休假日（日期選擇器） -->
      <div class="mb-3">
        <label class="form-label">特別休假日</label>
        <div class="d-flex flex-column gap-2">
          <div
            v-for="(date, index) in form.offDates"
            :key="index"
            class="d-flex align-items-center gap-2"
          >
            <Datepicker
              v-model="form.offDates[index]"
              :format="formatDate"
              input-class="form-control"
            />
            <button
              type="button"
              class="btn btn-sm btn-outline-danger"
              @click="removeOffDate(index)"
            >
              ✕
            </button>
          </div>
          <button
            type="button"
            class="btn btn-sm btn-outline-primary mt-2"
            @click="addOffDate"
          >
            新增日期
          </button>
        </div>
      </div>

      <div class="mb-3">
        <label class="form-label">特色</label>
        <div class="d-flex flex-wrap gap-3">
          <div
            v-for="tag in allFeatures"
            :key="tag.id"
            class="form-check"
            :style="{ minWidth: '150px' }"
          >
            <input
              class="form-check-input"
              type="checkbox"
              :value="tag.tagId"
              v-model="form.featureTagIds"
              :id="`feature-${tag.id}`"
            />
            <label class="form-check-label" :for="`feature-${tag.id}`">{{
              tag.tagName
            }}</label>
          </div>
        </div>
      </div>

      <div class="mb-3">
        <label class="form-label">WiFi 設施</label>
        <div class="d-flex flex-wrap gap-3">
          <div class="form-check" v-for="item in allWifis" :key="item.id">
            <input
              class="form-check-input"
              type="checkbox"
              :value="item.id"
              v-model="form.wifiIds"
            />
            <label class="form-check-label">{{ item.name }}</label>
          </div>
        </div>
      </div>

      <div class="mb-3">
        <label class="form-label">停車場</label>
        <div class="d-flex flex-wrap gap-3">
          <div class="form-check" v-for="item in allParkings" :key="item.id">
            <input
              class="form-check-input"
              type="checkbox"
              :value="item.id"
              v-model="form.parkingIds"
            />
            <label class="form-check-label">{{ item.name }}</label>
          </div>
        </div>
      </div>

      <div class="mb-3">
        <label class="form-label">設施</label>
        <div class="d-flex flex-wrap gap-3">
          <div class="form-check" v-for="item in allFacilities" :key="item.id">
            <input
              class="form-check-input"
              type="checkbox"
              :value="item.id"
              v-model="form.facilityIds"
            />
            <label class="form-check-label">{{ item.name }}</label>
          </div>
        </div>
      </div>

      <div class="mb-3">
        <label class="form-label">浴室</label>
        <div class="d-flex flex-wrap gap-3">
          <div class="form-check" v-for="item in allBathrooms" :key="item.id">
            <input
              class="form-check-input"
              type="checkbox"
              :value="item.id"
              v-model="form.bathroomIds"
            />
            <label class="form-check-label">{{ item.name }}</label>
          </div>
        </div>
      </div>

      <div class="mb-3">
        <label class="form-label">寵物政策</label>
        <div class="d-flex flex-wrap gap-3">
          <div class="form-check" v-for="item in allPetPolicies" :key="item.id">
            <input
              class="form-check-input"
              type="checkbox"
              :value="item.id"
              v-model="form.petPolicyIds"
            />
            <label class="form-check-label">{{ item.name }}</label>
          </div>
        </div>
      </div>

      <div class="mb-3">
        <label class="form-label">營地狀態</label>
        <select v-model="form.status" class="form-select">
          <option value="Active">營運中</option>
          <option value="Inactive">已關閉</option>
        </select>
      </div>

      <button type="submit" class="btn btn-success">儲存新增</button>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import axiosapi from "@/plugins/axios";
import { Swiper, SwiperSlide } from "swiper/vue";
import { Navigation, Pagination } from "swiper/modules";
import "swiper/css";
import "swiper/css/navigation";
import "swiper/css/pagination";
import Datepicker  from "@vuepic/vue-datepicker";
const modules = [Navigation, Pagination];

const router = useRouter();

const form = ref({
  campAreaName: "星空谷露營區",
  location: "花蓮縣秀林鄉",
  address: "花蓮縣秀林鄉富世村星光路99號",
  phone: "03-9876543",
  email: "contact@starglencamp.com",
  transportation: "經台9線轉入星光產業道路約5公里",
  description: "海拔1200公尺，夜晚可見滿天星斗，設有觀星平台與生態導覽。",
  coordinates: "POINT (121.518 24.163)",
  websiteUrl: "https://starglencamp.com",
  notice: "入園後請遵守靜音時段（22:00 - 07:00），禁止施放煙火",
  altitude: 1200,
  featureTagIds: [3, 4],       // 改成其他特色編號
  wifiIds: [1],                // 改不同 wifi 設施編號
  parkingIds: [1, 3],
  facilityIds: [1, 3],
  bathroomIds: [1],
  petPolicyIds: [1],
  status: "Active",
  offWeekdays: [2, 4],         // 週二、週四公休
  offDates: ["2025-06-05", "2025-06-10"],  // 指定休息日
});


const imageFiles = ref([]);
const imagePreviews = ref([]);

const addOffDate = () => {
  form.value.offDates.push('');
};

const removeOffDate = (index) => {
  form.value.offDates.splice(index, 1);
};

const formatDate = (date) => {
  return new Date(date).toISOString().split('T')[0]; // yyyy-MM-dd
};


const handleFileUpload = (event) => {
  const files = Array.from(event.target.files);
  files.forEach((file) => {
    imageFiles.value.push(file);
    const previewUrl = URL.createObjectURL(file);
    imagePreviews.value.push(previewUrl);
  });
};

const removeImage = (index) => {
  imageFiles.value.splice(index, 1);
  imagePreviews.value.splice(index, 1);
};

const submitForm = async () => {
  const formData = new FormData();
  formData.append(
    "request",
    new Blob([JSON.stringify(form.value)], { type: "application/json" })
  );
  imageFiles.value.forEach((file) => {
    formData.append("images", file);
  });

  try {
    await axiosapi.post("/api/owner/camp-areas", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    alert("新增成功！");
    router.push("/my-camp");
  } catch (err) {
    console.error(err);
    alert("新增失敗，請稍後再試");
  }
};

const allFeatures = ref([]);
const allWifis = ref([]);
const allParkings = ref([]);
const allFacilities = ref([]);
const allBathrooms = ref([]);
const allPetPolicies = ref([]);

onMounted(async () => {
  allFeatures.value = (
    await axiosapi.get("/api/public/camp-areas/features")
  ).data;
  allWifis.value = (
    await axiosapi.get("/api/public/camp-areas/options/wifi")
  ).data;
  allParkings.value = (
    await axiosapi.get("/api/public/camp-areas/options/parking")
  ).data;
  allFacilities.value = (
    await axiosapi.get("/api/public/camp-areas/options/facility")
  ).data;
  allBathrooms.value = (
    await axiosapi.get("/api/public/camp-areas/options/bathroom")
  ).data;
  allPetPolicies.value = (
    await axiosapi.get("/api/public/camp-areas/options/pet-policy")
  ).data;
});
</script>
