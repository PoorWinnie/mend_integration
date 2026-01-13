<template>
  <div class="feature-filter">
    <button
      v-for="feature in features"
      :key="feature.tagId"
      class="feature-btn"
      :class="{ active: selectedFeatures.includes(feature.tagId) }"
      @click="toggleFeature(feature.tagId)"
    >
      {{ feature.tagName }}
    </button>
  </div>
</template>

<script setup>


const props = defineProps({
  features: Array,
  selectedFeatures: {
    type: Array,
    default: () => [],
  },
});
const emit = defineEmits(["update:selectedFeatures"]);

function toggleFeature(id) {
  const next = [...props.selectedFeatures];
  const index = next.indexOf(id);
  if (index > -1) {
    next.splice(index, 1);
  } else {
    next.push(id);
  }
  emit('update:selectedFeatures', next);
}
</script>

<style scoped>
.feature-filter {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;
}

/* 預設樣式 */
.feature-btn {
  padding: 8px 14px;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 30px;
  background-color: white;
  color: #555;
  cursor: pointer;
  transition: all 0.3s ease;
}

/* 滑鼠 hover */
.feature-btn:hover {
  border-color: #28a745;
  background-color: rgba(40, 167, 69, 0.05);
  color: #28a745;
}

/* 被選中的狀態 */
.feature-btn.active {
  background-color: #28a745;
  color: white;
  border-color: #28a745;
  box-shadow: 0 0 8px rgba(40, 167, 69, 0.6);
}


</style>
