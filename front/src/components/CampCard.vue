<template>
  <div
    class="card mb-4 shadow rounded-4 border-0"
    style="max-width: 540px; background-color: #e6f4ea; cursor: pointer"
    @click="goToDetail"
  >
    <div class="row g-0" style="height: 180px">
      <div class="col-md-4 d-flex align-items-center">
        <img
          v-if="campArea.mainImageUrl?.length"
          :src="`${path}${campArea.mainImageUrl[0].imageUrl}`"
          class="img-fluid rounded-start"
          alt="圖片"
          style="height: 100%; width: 100%; object-fit: cover"
        />
      </div>
      <div class="col-md-8 d-flex">
        <div class="card-body d-flex flex-column justify-content-center">
          <h5 class="card-title text-success fw-bold">
            {{ campArea.campAreaName }}
          </h5>
          <p class="card-text text-muted small">{{ campArea.location }}</p>
          <p class="card-text text-muted small">
            海拔：{{ campArea.altitude }} m
          </p>

          <div class="d-flex flex-wrap gap-2 mt-2">
            <span
              v-for="(tag, index) in campArea.featureTagNames"
              :key="index"
              class="badge rounded-pill"
              style="background-color: #81c784; color: white"
            >
              {{ tag }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";

const props = defineProps(["campArea"]);
const path = import.meta.env.VITE_API_URL;

const router = useRouter();

function goToDetail() {
  router.push(`/camp/${props.campArea.campAreaId}`);
}
</script>

<style scoped>
.card {
  overflow: hidden;
  transition: transform 0.3s;
}
.card:hover {
  transform: translateY(-5px);
}
</style>
