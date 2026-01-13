<template>
  <div class="booking-cart card p-3 shadow-sm">
    <h5 class="mb-3">購買清單</h5>

    <ul class="list-group mb-3">
      <li
        v-for="item in selectedSpots"
        :key="item.spotId"
        class="list-group-item"
      >
        <div class="d-flex justify-content-between align-items-start">
          <div>
            <div class="fw-bold">{{ item.spotName }}</div>
            <small class="text-muted">
              {{ item.quantity }} 帳 × {{ stayNights }} 晚
            </small>

            <!-- 每日價格明細 -->
            <ul
              v-if="item.dateDetails?.length"
              class="price-details mt-2 ps-3 small text-muted"
            >
              <li v-for="(detail, idx) in item.dateDetails" :key="idx">
                {{ detail.date }}（{{ detail.isHoliday ? "假日" : "平日" }}）×
                ${{ detail.unitPrice }}
              </li>
            </ul>
          </div>

          <div class="text-end">
            <span class="text-danger fw-bold">${{ item.subtotal }}</span>
            <button
              class="btn btn-sm btn-outline-danger ms-2"
              @click="remove(item.spotId)"
            >
              ✕
            </button>
          </div>
        </div>
      </li>
    </ul>

    <div class="d-flex justify-content-between mb-3">
      <span class="fw-bold">總計</span>
      <span class="fw-bold text-danger">${{ totalAmount }}</span>
    </div>

    <button
      class="btn btn-danger w-100 text-center"
      :disabled="cartItems.length === 0"
      @click="submitBooking"
    >
      立即訂位
    </button>
  </div>
</template>

<script setup>
import { storeToRefs } from "pinia";
import { useBookingCartStore } from "@/stores/bookingCartStore";
import { useCampSearchStore } from "@/stores/campSearchStore";
import { computed, onMounted, onBeforeUnmount } from "vue";
import { onBeforeRouteLeave } from "vue-router";
import { useBookingStore } from "@/stores/useBookingStore";
import { useRouter } from "vue-router";

// 拿 Pinia store 資料
const cartStore = useBookingCartStore();
const { selectedSpots, totalAmount } = storeToRefs(cartStore);

const bookingStore = useBookingStore();
const router = useRouter();

//拿查詢區間
const campSearchStore = useCampSearchStore();
const checkInDate = campSearchStore.searchPayload?.checkInDate;
const checkOutDate = campSearchStore.searchPayload?.checkOutDate;

//計算總晚數
const stayNights = computed(() => {
  if (!checkInDate || !checkOutDate) return 0;
  const inDate = new Date(checkInDate);
  const outDate = new Date(checkOutDate);
  const diff = outDate.getTime() - inDate.getTime();
  return Math.max(1, diff / (1000 * 60 * 60 * 24));
});

const cartItems = selectedSpots;
const total = totalAmount;

//移除某一項
const remove = (spotId) => {
  cartStore.updateSpotQuantity(spotId, { spotId, quantity: 0 });
};

//點擊訂位
// const handleClick = (event) => {
//   if (selectedSpots.value.length === 0) {
//     event.preventDefault();
//     return;
//   }
//   cartStore.confirmCart?.(); // 若你有 confirmCart 方法
// };

const submitBooking = () => {
  if (cartItems.length === 0) {
    alert("請先選擇露營項目再進行訂位");
    return;
  }

  const searchStore = useCampSearchStore(); // ✅ 取得搜尋時存的日期
  const checkInDate = searchStore.searchPayload.checkInDate;
  const checkOutDate = searchStore.searchPayload.checkOutDate;
  const selectedSpot = cartItems.value.find((item) => item.quantity > 0);
  const campSpotId = selectedSpot.spotId;

  // const campSpotId = 1;
  // const startDate = new Date('2025-06-01');
  // const endDate = new Date('2025-06-03');

  bookingStore.setBookingInfo(campSpotId, checkInDate, checkOutDate);

  // 跳轉到 campBooking 頁面
  router.push("/campBooking");
};

// 離開或刷新時清空（未確認情況）
onBeforeRouteLeave((to, from, next) => {
  if (!cartStore.confirmed) {
    cartStore.clear();
  }
  next();
});
function clearIfNotConfirmed() {
  if (!cartStore.confirmed) {
    cartStore.clear();
  }
}
onMounted(() => {
  window.addEventListener("beforeunload", clearIfNotConfirmed);
});
onBeforeUnmount(() => {
  window.removeEventListener("beforeunload", clearIfNotConfirmed);
});
</script>

<style scoped>
.booking-cart {
  max-width: 600px;
  margin-left: auto;
  position: sticky;
  top: 100px;
  background-color: #fff;
  border-radius: 8px;
}
.price-details {
  list-style: none;
  padding-left: 0;
  font-size: 13px;
}
</style>
