<template>
  <div class="cart-container">
    <div class="cart-header">🛒 營地預約資訊</div>
    <div class="cart-item">
      <h3>{{ campData.campAreaName }}</h3>
      <div class="camp-image">
        <img :src="campData.imageUrl" />
      </div>
      <p>
        日期：{{ formattedStartDate }} - {{ formattedEndDate }}（共
        {{ days }} 天）
      </p>
      <p>營位：{{ campData.campSitesName }}</p>
      <p>營地點：{{ campData.campSpotsName }}</p>
      <div class="item-actions">
        <label>帳篷數量：
              <input type="number" v-model.number="quantity" min="1" />
        </label>
      </div>
      <p>每個帳篷/晚：${{ campData.unitPrice }}</p>
    </div>
    <div class="cart-footer">
      <div class="cart-total">
        <span>總金額</span>
        <span>NT${{ totalPrice }}</span>
      </div>
      <button class="checkout-btn" @click="checkout">前往預約</button>
    </div>
  </div>
</template>

<script setup>
import { onMounted, watch, ref, computed, reactive } from "vue";
import { useBookingStore } from "@/stores/useBookingStore";
import { differenceInCalendarDays, format } from "date-fns";
import axiosapi from "@/plugins/axios.js";
import Swal from 'sweetalert2';
import { useRouter } from 'vue-router';

const router = useRouter();
const quantity = ref(1);
const path = import.meta.env.VITE_API_URL;
const bookingStore = useBookingStore();
const isDataLoaded = ref(false);
const formattedStartDate = computed(() => {
  return campData.startDate
    ? format(new Date(campData.startDate), "yyyy/MM/dd")
    : "";
});

const formattedEndDate = computed(() => {
  return campData.endDate
    ? format(new Date(campData.endDate), "yyyy/MM/dd")
    : "";
});

const campData = reactive({
  campAreaName: "",
  campSitesName: "",
  campSpotsName: "",
  imageUrl: "",
  startDate: "",
  endDate: "",
  unitPrice: 0,
});

const days = computed(() => {
  const start = new Date(campData.startDate);
  const end = new Date(campData.endDate);
  return differenceInCalendarDays(end, start) || 1;
});

onMounted(init);
watch(() => bookingStore.campSpotId, init, { immediate: true });

async function init() {
  const spotId = bookingStore.campSpotId;
  if (!spotId) return;
  await fetchCampData(spotId);
}

async function fetchCampData(spotId) {
  try {
    const { data } = await axiosapi.get(`/api/public/camp-spots/${spotId}`);

    // 根據你的 DTO 欄位填充前端資料
    campData.campAreaName = data.campAreaName;
    campData.campSitesName = data.campSiteName;
    campData.campSpotsName = data.spotName;
    campData.imageUrl = `${path}${data.imageUrl}`;
    campData.startDate = bookingStore.dateRange.startDate;
    campData.endDate = bookingStore.dateRange.endDate;
    campData.unitPrice = data.priceWeekend;

    isDataLoaded.value = true;
  } catch (error) {
    console.error("獲取營位資料失敗", error);
  }
}

const totalPrice = computed(() =>
  campData.unitPrice * quantity.value * days.value
);

function checkout() {
  Swal.fire({
    icon: 'success',
    title: '預約成功！',
    confirmButtonText: '確定',
    confirmButtonColor: '#4a7c59',
  }).then((result) => {
    if (result.isConfirmed) {
      router.push('/'); 
    }
  });
}
</script>

<style scoped>
.cart-container {
  max-width: 500px;
  margin: 2rem auto;
  background-color: white;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.cart-header {
  background-color: #a8d5ba;
  padding: 1rem;
  font-size: 1.25rem;
  font-weight: bold;
  text-align: center;
  color: #4a7c59;
}

.cart-item {
  padding: 1rem;
  border-bottom: 1px solid #dde9da;
}

.cart-item h3 {
  margin: 0 0 0.25rem 0;
  color: #4a7c59;
}

.cart-item p {
  margin: 0.25rem 0;
  color: #444;
  font-size: 0.95rem;
}

.camp-image img {
  width: 100%;
  height: auto;
  border-radius: 8px;
}

.item-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 0.5rem;
}

.item-actions input {
  width: 40px;
  text-align: center;
}

.delete-btn {
  background: none;
  border: none;
  color: #888;
  cursor: pointer;
}

.cart-footer {
  padding: 1rem;
  background-color: #f0f5f2;
}

.cart-total {
  display: flex;
  justify-content: space-between;
  font-weight: bold;
  color: #4a7c59;
  margin-bottom: 1rem;
}

.checkout-btn {
  width: 100%;
  padding: 0.75rem;
  background-color: #a8d5ba;
  border: none;
  color: white;
  font-size: 1rem;
  font-weight: bold;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.checkout-btn:hover {
  background-color: #4a7c59;
}
</style>
