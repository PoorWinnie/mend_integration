<template>
  <transition name="toast">
    <div v-if="visible" :class="['toast', 'toast-' + type]">
      <div class="toast-content">
        <i :class="getIconClass()"></i>
        <span>{{ message }}</span>
      </div>
      <button class="toast-close" @click="hideToast">
        <i class="fa fa-times"></i>
      </button>
    </div>
  </transition>
</template>

<script setup>
import { useToast } from '../composables/useToast';

const { visible, message, type, hideToast } = useToast();

const getIconClass = () => {
  const classes = {
    'info': 'fa fa-info-circle',
    'success': 'fa fa-check-circle',
    'error': 'fa fa-times-circle',
    'warning': 'fa fa-exclamation-triangle'
  };
  return classes[type.value] || classes.info;
};
</script>

<style scoped>
/* 樣式已經在 toastStyles 中定義，不需要在這裡重複 */
</style>