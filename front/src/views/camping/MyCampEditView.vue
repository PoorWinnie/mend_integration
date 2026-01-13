<template>
  <div class="container my-5">
    <h2>編輯營地：{{ form.campAreaName }}</h2>

    <form @submit.prevent="submitForm">
      <!-- 基本欄位：雙欄排版 -->
      <div class="row">
        <div class="mb-3 col-md-6">
          <label class="form-label">營地名稱</label>
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

        <!-- 描述 -->
        <div class="mb-3 col-md-6">
          <label class="form-label">營區描述</label>
          <textarea
            v-model="form.description"
            class="form-control"
            rows="3"
          ></textarea>
        </div>

        <!-- 座標 -->
        <div class="mb-3 col-md-6">
          <label class="form-label">座標（POINT 格式）</label>
          <input
            v-model="form.coordinates"
            class="form-control"
            placeholder="POINT (經度 緯度)"
          />
        </div>

        <!-- 網站 URL -->
        <div class="mb-3 col-md-6">
          <label class="form-label">網站網址</label>
          <input v-model="form.websiteUrl" class="form-control" />
        </div>

        <!-- 公告 -->
        <div class="mb-3 col-md-6">
          <label class="form-label">注意事項</label>
          <textarea
            v-model="form.notice"
            class="form-control"
            rows="2"
          ></textarea>
        </div>

        <!-- 海拔 -->
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
          multiple
          @change="handleFileUpload"
          class="form-control"
        />

        <div v-if="imageList.length" class="mt-3">
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
              v-for="(img, index) in imageList"
              :key="index"
              class="position-relative"
            >
              <img
                :src="getImageUrl(img.url)"
                class="img-thumbnail w-100"
                style="height: 350px; object-fit: cover"
              />
              <button
                type="button"
                class="btn btn-sm btn-danger position-absolute top-0 end-0"
                @click="removeImage(index, img)"
              >
                ✕
              </button>
            </SwiperSlide>
          </Swiper>
        </div>
      </div>

      <!-- 固定休假日 -->
      <div class="mb-3">
        <label class="form-label">固定休假日（可複選）</label>
        <div class="d-flex flex-wrap gap-3">
          <div
            class="form-check"
            v-for="(label, index) in weekdayLabels"
            :key="index"
          >
            <input
              class="form-check-input"
              type="checkbox"
              :value="index"
              v-model="form.offWeekdays"
              :id="'weekday-' + index"
            />
            <label class="form-check-label" :for="'weekday-' + index">{{
              label
            }}</label>
          </div>
        </div>
      </div>

      <!-- 特定休假日 -->
      <div class="mb-3">
        <label class="form-label">特別休假日</label>
        <VueDatePicker
          v-model="form.offDates"
          :multi-dates="true"
          :format="'yyyy-MM-dd'"
          :enable-time-picker="false"
          placeholder="選擇日期"
        />
      </div>

      <!-- 多選：特色（橫向排列） -->
      <div class="mb-3">
        <label class="form-label">特色</label>
        <div class="d-flex flex-wrap gap-3">
          <div
            class="form-check"
            v-for="tag in allFeatures"
            :key="tag.id"
            :style="{ minWidth: '150px' }"
          >
            <input
              class="form-check-input"
              type="checkbox"
              :value="tag.tagId"
              v-model="form.featureTagIds"
              :id="`feature-${tag.id}`"
              :checked="form.featureTagIds.includes(tag.id)"
            />
            <label class="form-check-label" :for="`feature-${tag.id}`">{{
              tag.tagName
            }}</label>
          </div>
        </div>
      </div>

      <!-- 多選：WiFi -->
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

      <!-- 多選：停車場 -->
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

      <!-- 多選：設施 -->
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

      <!-- 多選：浴室 -->
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

      <!-- 多選：寵物政策 -->
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

      <!-- 營地狀態切換 -->
      <div class="mb-3">
        <label class="form-label">營地狀態</label>
        <select v-model="form.status" class="form-select">
          <option value="Active">營運中</option>
          <option value="Inactive">已關閉</option>
        </select>
      </div>

      <button type="submit" class="btn btn-success">儲存修改</button>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import axiosapi from "@/plugins/axios";
import { Swiper, SwiperSlide } from "swiper/vue";
import { Navigation, Pagination } from "swiper/modules";
import "swiper/css";
import "swiper/css/navigation";
import VueDatePicker from "@vuepic/vue-datepicker";
import "@vuepic/vue-datepicker/dist/main.css";
import SystemLogs from "../admin/SystemLogs.vue";

const weekdayLabels = ["日", "一", "二", "三", "四", "五", "六"];

const route = useRoute();
const router = useRouter();

const form = ref({
  campAreaName: "",
  location: "",
  address: "",
  phone: "",
  email: "",
  transportation: "",
  description: "",
  coordinates: "",
  websiteUrl: "",
  notice: "",
  altitude: null,
  featureTagIds: [],
  wifiIds: [],
  parkingIds: [],
  facilityIds: [],
  bathroomIds: [],
  petPolicyIds: [],
  status: "Active",
  offWeekdays: [],
  offDates: [],
});

const allFeatures = ref([]);
const allWifis = ref([]);
const allParkings = ref([]);
const allFacilities = ref([]);
const allBathrooms = ref([]);
const allPetPolicies = ref([]);
const imageFiles = ref([]);
const imageList = ref([]);
const removedImageIds = ref([]);
const mainImageRemoved = ref(false);
const modules = [Navigation, Pagination];

const handleFileUpload = (event) => {
  const files = Array.from(event.target.files);
  files.forEach((file) => {
    imageFiles.value.push(file);
    imageList.value.push({ url: URL.createObjectURL(file), file });
  });
};

const removeImage = (index, img) => {
  imageList.value.splice(index, 1);

  if (img.file) {
    imageFiles.value = imageFiles.value.filter((f) => f !== img.file);
  } else if (img.id) {
    removedImageIds.value.push(img.id);
  } else if (form.value.mainImageUrl && img.url === form.value.mainImageUrl) {
    mainImageRemoved.value = true;
    form.value.mainImageUrl = null;
  }
};

const getImageUrl = (url) => {
  const path = String(url || "");
  return path.startsWith("http") || path.startsWith("/")
    ? `${import.meta.env.VITE_API_URL}${path}`
    : path;
};

const fetchCamp = async () => {
  const res = await axiosapi.get(`/api/owner/camp-areas/${route.params.id}`);
  const data = res.data.list[0];
  Object.assign(form.value, data);
  form.value.offWeekdays = data.offWeekdays || [];
  form.value.offDates = (data.offDates || []).map((d) => new Date(d));

  if (Array.isArray(data.mainImageUrl)) {
    data.mainImageUrl.forEach((img) => {
      imageList.value.push({
        id: img.imageId,
        url: img.imageUrl,
        description: img.description,
      });
    });
  }

  if (Array.isArray(data.imageList)) {
    data.imageList.forEach((img) => {
      imageList.value.push({ id: img.id, url: img.imageUrl });
    });
  }

  // 圖片列表固定排序（依 id 升序）
  imageList.value.sort((a, b) => {
    if (a.id && b.id) return a.id - b.id;
    return 0;
  });
};

const fetchFeatureTags = async () => {
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
};

const submitForm = async () => {
  try {
    const formData = new FormData();

    // 正確送出 request (整包 JSON)
    formData.append(
      "request",
      new Blob([JSON.stringify(form.value)], { type: "application/json" })
    );

    form.value.offDates = form.value.offDates.map((d) =>
      typeof d === "string" ? d : d.toISOString().split("T")[0]
    );

    // 加上圖片等
    imageFiles.value.forEach((file) => {
      formData.append("images", file);
    });
    removedImageIds.value.forEach((id) => {
      formData.append("removeImageIds", id);
    });
    if (mainImageRemoved.value) {
      formData.append("removeMainImage", "true");
    }

    for (let pair of formData.entries()) {
      if (pair[1] instanceof File) {
        console.log(`${pair[0]}: [檔案] ${pair[1].name}`);
      } else {
        console.log(`${pair[0]}: ${pair[1]}`);
      }
    }

    await axiosapi.put(`/api/owner/camp-areas/${route.params.id}`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    alert("營地資訊已更新");
    router.push("/my-camp");
  } catch (error) {
    alert("更新失敗，請稍後再試");
    console.error(error);
  }
};

onMounted(() => {
  fetchCamp();

  fetchFeatureTags();
});
</script>

<style scoped>
::v-deep(.swiper-button-next),
::v-deep(.swiper-button-prev) {
  color: #000; /* 箭頭顏色 */
  font-size: 24px;
  z-index: 10;
  top: 50%; /* 垂直置中一點 */
  background-color: rgba(255, 255, 255, 0.6); /* 半透明背景 */
  border-radius: 50%;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.3s;
}

.swiper-button-next:hover,
.swiper-button-prev:hover {
  background-color: rgba(0, 0, 0, 0.8);
  color: #fff;
}
</style>
