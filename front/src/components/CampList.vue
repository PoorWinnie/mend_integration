<template>
  <div class="container">
    <div class="row gy-4">
      <div class="col-md-6" v-for="item in visibleCamps" :key="item.campAreaId">
        <CampCard :campArea="item" />
      </div>
    </div>

    <!-- 分頁按鈕 -->
    <div class="pagination mt-4" v-if="total > rows">
      <button
        class="page-btn"
        :disabled="currentPage === 1"
        @click="goToPage(currentPage - 1)"
      >
        上一頁
      </button>
      <button
        class="page-btn"
        v-for="page in totalPages"
        :key="page"
        :class="{ active: page === currentPage }"
        @click="goToPage(page)"
      >
        {{ page }}
      </button>
      <button
        class="page-btn"
        :disabled="currentPage === totalPages"
        @click="goToPage(currentPage + 1)"
      >
        下一頁
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import CampCard from "./CampCard.vue";
import axiosapi from "@/plugins/axios.js";
import Swal from "sweetalert2";

const camps = ref([]);
const total = ref(0);
const rows = ref(5);
const currentPage = ref(1);
const totalPages = ref(1);
const currentKeyword = ref("");
const currentFeatureIds = ref([]);
const currentRegion = ref(null);
const currentAltitudeRange = ref([]);
const emit = defineEmits(["update:total", "update:totalPages"]);

const visibleCamps = computed(() => {
  return camps.value.filter((camp) => camp.status === "Active");
});
const props = defineProps({
  pageSize: {
    type: Number,
    default: 5,
  },
});

defineExpose({ callFind, currentPage, totalPages, clearFilters });


async function callFind(
  nameKeyword = "",
  featureIds = [],
  region = null,
  altitudeRange = [],
  page = 1,
  pageSize = 5
) {
  Swal.fire({
    title: "載入中...",
    allowOutsideClick: false,
    didOpen: () => Swal.showLoading(),
  });
  currentPage.value = page;
  const body = {
    start: (page - 1) * pageSize,
    rows: props.pageSize,
    sort: "campAreaId",
    dir: false,
    campAreaName: nameKeyword,
    featureIds,
    region,
    altitudeRange,
  };
  try {
    const response = await axiosapi.post("/api/public/camp-areas/filter", body);

    //先處理 success = false 的情況
    if (response.data.success === false) {
      Swal.close(); // 關掉 loading
      await Swal.fire({
        icon: "info",
        text: response.data.message || "查無符合條件的營區",
      });

      camps.value = [];
      total.value = 0;
      totalPages.value = 1;
      emit("update:total", total.value);
      emit("update:totalPages", totalPages.value);
      return; // 中止後續邏輯
    }

    //這裡才檢查 list/count 是否正常
    if (
      !response.data ||
      !Array.isArray(response.data.list) ||
      typeof response.data.count !== "number"
    ) {
      throw new Error("後端回傳格式錯誤");
    }

    //正常流程
    camps.value = response.data.list;
    total.value = response.data.count;
    emit("update:total", total.value);

    totalPages.value = Math.ceil(total.value / props.pageSize);
    emit("update:totalPages", totalPages.value);

    currentKeyword.value = nameKeyword;
    currentFeatureIds.value = featureIds;
  } catch (error) {
    console.error("發生錯誤：", error);
    Swal.fire({ icon: "error", text: "訊息：" + error.message });
  } finally {
    Swal.close(); // 放最後統一結束 loading（不會影響 alert 彈出）
  }
}

function goToPage(page) {
  if (page < 1 || page > totalPages.value) return;
  currentPage.value = page;
  // 重新帶入目前查詢條件
  callFind(
    currentKeyword.value,
    currentFeatureIds.value,
    currentRegion.value,
    currentAltitudeRange.value,
    page,
    props.pageSize
  );
}

function clearFilters() {
  currentKeyword.value = "";
  currentFeatureIds.value = [];
  currentRegion.value = null;
  currentAltitudeRange.value = [];
  currentPage.value = 1;

  callFind("", [], null, [], 1, props.pageSize);
}


</script>

<style scoped>
.page-btn {
  padding: 8px 12px;
  border: 1px solid #ccc;
  background: white;
  border-radius: 4px;
  margin: 0 2px;
  cursor: pointer;
}
.page-btn.active {
  background-color: #ff7f00;
  color: white;
  border-color: #ff7f00;
}
.page-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.card {
  max-width: 540px;
  margin-left: auto;
  margin-right: auto;
}
</style>
