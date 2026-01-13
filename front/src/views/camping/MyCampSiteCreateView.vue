<template>
  <div class="container my-5">
    <h2>新增營地與營位</h2>

    <form @submit.prevent="submitForm">
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
            <label class="form-label">描述</label>
            <textarea
              v-model="form.campSpot.description"
              class="form-control"
              rows="3"
            />
          </div>

          <!-- 圖片上傳 -->
          <div class="mb-3 col-md-6">
            <label class="form-label">衛浴設施圖片</label>
            <input
              class="form-control"
              type="file"
              accept="image/*"
              @change="handleImageUpload"
            />
            <div
              v-if="imagePreview"
              class="mt-2 position-relative"
              style="max-height: 200px"
            >
              <img
                :src="imagePreview"
                class="img-thumbnail w-100"
                style="max-height: 200px; object-fit: contain"
              />
              <button
                type="button"
                class="btn btn-sm btn-danger position-absolute"
                style="top: 8px; right: 8px; z-index: 10"
                @click="removeImage"
              >
                ✕
              </button>
            </div>
          </div>
        </div>
      </div>

      <div class="text-end">
        <button type="submit" class="btn btn-success">新增</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter, useRoute } from "vue-router";
import axiosapi from "@/plugins/axios";

const router = useRouter();
const route = useRoute();
const campAreaId = ref(route.params.campAreaId); // 這裡會拿到你 router-link 傳來的 campAreaId

const form = ref({
  campSiteName: "星空A區",
  siteType: "草地",
  statusSwitch: true,
  campSpot: {
    spotName: "A1營位",
    areaSize: "5x5 公尺",
    maxTents: 20,
    pricePerNight: 1000,
    priceHoliday: 1500,
    hasElectricity: false,
    hasWater: true,
    description: "靠近溪邊，夜晚可觀星，適合親子露營。",
  },
});

const imageFile = ref(null);
const imagePreview = ref(null);


const removeImage = () => {
  imageFile.value = null;
  imagePreview.value = null;
};


const handleImageUpload = (e) => {
  const file = e.target.files[0];
  if (file) {
    imageFile.value = file;
    imagePreview.value = URL.createObjectURL(file);
  }
};

const submitForm = async () => {
  try {
    const requestPayload = {
      campAreaId: Number(campAreaId.value),
      campSiteName: form.value.campSiteName,
      siteType: form.value.siteType,
      description: form.value.campSpot.description,
      maxCapacity: form.value.campSpot.maxTents,
      recommendedFor: "",
      status: form.value.statusSwitch ? "Available" : "Unavailable",
      campSpots: [
        {
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
          description:form.value.campSpot.description
        },
      ],
    };



    const formData = new FormData();
    formData.append(
      "request",
      new Blob([JSON.stringify(requestPayload)], {
        type: "application/json",
      })
    );

    if (imageFile.value) {
      formData.append("images", imageFile.value);
    }

    await axiosapi.post("/api/owner/camp-sites", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });

    alert("新增成功！");
    router.push("/my-camp");
  } catch (error) {
    console.error("新增失敗", error);
    alert("新增失敗，請稍後再試");
  }
};
</script>

<style scoped></style>
