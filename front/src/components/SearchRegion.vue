<template>
  <div class="filter-group">
    <div class="filter-description">地區篩選</div>
    <div class="feature-filter">
      <button
        v-for="(label, value) in regions"
        :key="value"
        class="feature-btn"
        :class="{ active: modelValue === value }"
        @click="emit('update:modelValue', modelValue === value ? null : value)"
      >
        {{ label }}
      </button>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  modelValue: String,
});

const emit = defineEmits(["update:modelValue"]);

const regions = {
  北部: "北部",
  中部: "中部",
  南部: "南部",
  東部: "東部",
};
</script>

<style scoped>
.feature-filter {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;
}
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
.feature-btn:hover {
  border-color: #007bff;
  background-color: rgba(0, 123, 255, 0.05);
  color: #007bff;
}
.feature-btn.active {
  background-color: #007bff;
  color: white;
  border-color: #007bff;
  box-shadow: 0 0 8px rgba(0, 123, 255, 0.6);
}

.filter-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 16px;
}

.filter-description {
  font-weight: bold;
  font-size: 20px;
  margin-bottom: 12px;
}
</style>
