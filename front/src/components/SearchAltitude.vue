<template>
  <div class="filter-group">
    <div class="filter-description">海拔篩選</div>
    <div class="feature-filter">
      <button
        v-for="(label, value) in altitudeOptions"
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
  modelValue: [String, Number],
});
const emit = defineEmits(["update:modelValue"]);

const altitudeOptions = {
  1: "300m 以下",
  2: "301–500m",
  3: "501–800m",
  4: "800m 以上",
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
  border-color: #28a745;
  background-color: rgba(40, 167, 69, 0.05);
  color: #28a745;
}
.feature-btn.active {
  background-color: #28a745;
  color: white;
  border-color: #28a745;
  box-shadow: 0 0 8px rgba(40, 167, 69, 0.6);
}

.filter-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 20px;
}

.filter-description {
  font-weight: bold;
  font-size: 20px;
  margin-bottom: 12px;
}
</style>
