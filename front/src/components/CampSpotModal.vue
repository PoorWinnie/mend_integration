<template>
  <div
    class="modal fade"
    id="spotModal"
    tabindex="-1"
    aria-labelledby="spotModalLabel"
    ref="modalRef"
  >
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">{{ spot?.spotName || "營位資訊" }}</h5>
          <button
            type="button"
            class="btn-close"
            @click="closeModal"
            aria-label="Close"
          ></button>
        </div>
        <div class="modal-body" v-if="spot">
          <p><strong>尺寸：</strong> {{ spot?.areaSize }}</p>
          <p><strong>可搭帳數：</strong> {{ spot?.maxTents }}</p>
          <!-- <p><strong>可容納人數：</strong> {{ spot?.maxPeople }}</p> -->
          <p><strong>平日價格：</strong> ${{ spot?.pricePerNight }}</p>
          <p><strong>假日價格：</strong> ${{ spot?.priceHoliday }}</p>
          <p><strong>說明：</strong> {{ spot?.description }}</p>
          <p>
            <strong>有插頭：</strong> {{ spot?.hasElectricity ? "有" : "無" }}
          </p>
          <p><strong>飲水機：</strong> {{ spot?.hasWater ? "有" : "無" }}</p>

          <div class="mb-3">
            <p><strong>廁所/衛浴：</strong></p>
            <img
              v-if="spot.toiletImage"
              :src="path + spot.toiletImage"
              alt="廁所/衛浴圖片"
              class="img-fluid rounded"
              style="width: 230px; height: 350px; object-fit: contain"
            />
            <p v-else>尚無廁所圖片</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from "vue";
import * as bootstrap from "bootstrap";

const props = defineProps({
  spot: Object,
});
const emit = defineEmits(["update:spot"]); // 用來通知父層清空 spot
const path = import.meta.env.VITE_API_URL || ""; // 圖片網址前綴

const modalRef = ref(null);
let modalInstance = null;

// Modal初始化
onMounted(() => {
  if (modalRef.value) {
    modalInstance = new bootstrap.Modal(modalRef.value, {
      backdrop: true, // 點擊背景可以關閉
      keyboard: true, // 按ESC可以關閉
      focus: true, // 打開時自動聚焦
    });

    // 當 Modal 關閉時清空資料
    modalRef.value.addEventListener("hidden.bs.modal", () => {
      emit("update:spot", null); // 通知父層把 spot 清掉
    });
  }
});

// 監聽 props.spot，如果有新的資料就打開 Modal
watch(
  () => props.spot,
  (newSpot) => {
    if (newSpot && modalInstance) {
      modalInstance.show();
    }
  }
);

function closeModal() {
  if (modalInstance) {
    modalInstance.hide();
  }
}
</script>

<style scoped></style>
