<template>
  <div class="booking-date-summary alert alert-info text-center mb-4">
    <div>
      <strong>入住：</strong>{{ formatDate(checkIn) }}　 <strong>退房：</strong
      >{{ formatDate(checkOut) }}
    </div>
    <div class="mt-1">
      <strong>{{ stayDays }} 天 {{ stayNights }} 夜</strong>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useCampSearchStore } from "@/stores/campSearchStore";

const searchStore = useCampSearchStore();
const checkIn = computed(() => searchStore.searchPayload?.checkInDate);
const checkOut = computed(() => searchStore.searchPayload?.checkOutDate);

// 顯示日期 + 星期幾
const formatDate = (dateStr) => {
  if (!dateStr) return "-";
  const date = new Date(dateStr);
  const days = ["日", "一", "二", "三", "四", "五", "六"];
  const weekday = days[date.getDay()];
  return `${date.getFullYear()}/${
    date.getMonth() + 1
  }/${date.getDate()}（${weekday}）`;
};

// 天數計算
const stayDays = computed(() => {
  if (!checkIn.value || !checkOut.value) return 0;
  const start = new Date(checkIn.value);
  const end = new Date(checkOut.value);
  const diff = (end - start) / (1000 * 60 * 60 * 24) +1 ;
  return diff > 0 ? diff : 0;
});

const stayNights = computed(() => {
  return stayDays.value > 0 ? stayDays.value - 1 : 0;
});
</script>

<style scoped>
.booking-date-summary {
  font-size: 20px;
  padding: 12px 16px;
  border-radius: 6px;
  line-height: 1.5;
  width: 500px;
  margin: 0 auto; /* 水平置中 */
  background-color: #e6f4ea; /* 淡綠色 */
  border: 1px solid #b2d8b2; /* 綠色外框 */
}

</style>
