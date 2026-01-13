<template>
  <div class="container my-5">
    <h2>編輯營地與營位</h2>

    <div v-if="isLoading" class="text-center my-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
      <p class="mt-2 text-muted">載入中...</p>
    </div>

    <form v-else @submit.prevent="submitForm">
      <!-- 營地資訊 -->
      <div class="card mb-4">
        <div class="card-header">營地資訊</div>
        <div class="card-body row">
          <div class="mb-3 col-md-6">
            <label class="form-label">營地名稱</label>
            <input v-model="form.campSiteName" class="form-control" />
          </div>

          <div class="mb-3 col-md-6">
            <label class="form-label">營地類型</label>
            <select v-model="form.siteType" class="form-select">
              <option value="草地">草地</option>
              <option value="棧板">棧板</option>
              <option value="雨棚">雨棚</option>
              <option value="石頭地">石頭地</option>
            </select>
          </div>

          <div class="mb-3 col-md-6">
            <label class="form-label">開放狀態</label>
            <div class="form-check form-switch">
              <input
                class="form-check-input"
                type="checkbox"
                v-model="form.statusSwitch"
              />
              <label class="form-check-label">
                {{ form.statusSwitch ? "開放中" : "不開放" }}
              </label>
            </div>
          </div>
        </div>
      </div>

      <!-- 營位資訊 -->
      <div class="card mb-4">
        <div class="card-header">營位資訊</div>
        <div class="card-body row">
          <div class="mb-3 col-md-6">
            <label class="form-label">營位名稱</label>
            <input v-model="form.campSpot.spotName" class="form-control" />
          </div>

          <div class="mb-3 col-md-6">
            <label class="form-label">營位大小</label>
            <input v-model="form.campSpot.areaSize" class="form-control" />
          </div>

          <div class="mb-3 col-md-6">
            <label class="form-label">最多帳數</label>
            <input
              type="number"
              v-model="form.campSpot.maxTents"
              class="form-control"
            />
          </div>

          <div class="mb-3 col-md-6">
            <label class="form-label">平日價格</label>
            <input
              type="number"
              v-model="form.campSpot.pricePerNight"
              class="form-control"
            />
          </div>

          <div class="mb-3 col-md-6">
            <label class="form-label">假日價格</label>
            <input
              type="number"
              v-model="form.campSpot.priceHoliday"
              class="form-control"
            />
          </div>

          <div class="col-md-6 d-flex align-items-center">
            <div class="form-check form-switch me-3">
              <input
                class="form-check-input"
                type="checkbox"
                v-model="form.campSpot.hasElectricity"
              />
              <label class="form-check-label">提供電源</label>
            </div>

            <div class="form-check form-switch">
              <input
                class="form-check-input"
                type="checkbox"
                v-model="form.campSpot.hasWater"
              />
              <label class="form-check-label">提供水源</label>
            </div>
          </div>

          <div class="mb-3 col-md-6">
            <label class="form-label">衛浴設施圖片</label>
            <input
              class="form-control"
              type="file"
              accept="image/*"
              @change="handleImageUpload"
            />
            <!-- 圖片預覽與刪除 -->
            <div v-if="imagePreview" class="mt-2 position-relative">
              <img
                :src="getImageUrl(imagePreview)"
                class="img-thumbnail w-100"
                style="max-height: 300px; object-fit: contain"
              />

              <button
                type="button"
                class="btn btn-sm btn-danger position-absolute top-0 end-0"
                @click="removeImage"
              >
                ✕
              </button>
            </div>
          </div>

          <div class="mb-3 col-12">
            <label class="form-label">描述</label>
            <textarea
              v-model="form.campSpot.description"
              class="form-control"
              rows="3"
            />
          </div>
        </div>
      </div>

      <div class="text-end">
        <button type="submit" class="btn btn-success">儲存修改</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import axiosapi from "@/plugins/axios";

const route = useRoute();
const router = useRouter();
const isLoading = ref(true);
const imageFile = ref(null);
const imagePreview = ref(null);
const removedImage = ref(false); // 是否刪除了原本的圖片

const form = ref({
  campAreaId:"",
  campSiteId: null,
  campSiteName: "",
  siteType: "",
  statusSwitch: true,
  campSpot: {
    campSpotId: null,
    spotName: "",
    areaSize: "",
    maxTents: 0,
    pricePerNight: 0,
    priceHoliday: 0,
    hasElectricity: false,
    hasWater: false,
    description: "",
    toiletImage: null,
  },
});

const removeImage = () => {
  imagePreview.value = null;
  imageFile.value = null;
  removedImage.value = true;
};

const getImageUrl = (url) => {
  const path = String(url || "");
  return path.startsWith("http") || path.startsWith("/")
    ? `${import.meta.env.VITE_API_URL}${path}`
    : path;
};

const handleImageUpload = (e) => {
  const file = e.target.files[0];
  if (file) {
    imageFile.value = file;
    imagePreview.value = URL.createObjectURL(file);

  }
};

const fetchData = async () => {
  try {

    const response = await axiosapi.get(
      `/api/owner/camp-spots/${route.params.campSpotId}`
    );
    const data = response.data;
    form.value = {
      campSiteId: data.campSiteId,
      campSiteName: data.campSiteName,
      siteType: data.siteType,
      statusSwitch: data.status === "Available",
      campSpot: { ...data.campSpot },
    };
    

    if (data.campSpot.toiletImage) {
      imagePreview.value = data.campSpot.toiletImage;
    }
  } catch (error) {
    console.error("載入失敗", error);
    alert("載入資料失敗");
  } finally {
    isLoading.value = false;
  }
};

const submitForm = async () => {
  try {
    const fd = new FormData();

    const requestPayload = {
      campSite: {
        campSiteName: form.value.campSiteName,
        siteType: form.value.siteType,
        description: form.value.campSpot.description,
        maxCapacity: form.value.campSpot.maxTents,
        recommendedFor: "",
        status: form.value.statusSwitch ? "Available" : "Unavailable",
      },
      campSpots: [
        {
          campSpotId: form.value.campSpot.campSpotId,
          spotName: form.value.campSpot.spotName,
          areaSize: form.value.campSpot.areaSize,
          maxTents: form.value.campSpot.maxTents,
          maxPeople: 4,
          pricePerNight: form.value.campSpot.pricePerNight,
          priceWeekend: form.value.campSpot.pricePerNight,
          priceHoliday: form.value.campSpot.priceHoliday,
          hasElectricity: form.value.campSpot.hasElectricity,
          hasWater: form.value.campSpot.hasWater,
          toiletImage: "",
          description: form.value.campSpot.description,
        },
      ],
    };

    fd.append(
      "request",
      new Blob([JSON.stringify(requestPayload)], { type: "application/json" })
    );
    if (imageFile.value) {
      fd.append("images", imageFile.value);
    }
    if (removedImage.value) {
      fd.append("removeToiletImage", "true");
    }

    await axiosapi.put(`/api/owner/camp-sites/${form.value.campSiteId}`, fd, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });

    alert("更新成功！");
    router.push("/my-camp");
  } catch (error) {
    console.error("更新失敗", error);
    alert("更新失敗，請稍後再試");
  }
};

onMounted(fetchData);
</script>
