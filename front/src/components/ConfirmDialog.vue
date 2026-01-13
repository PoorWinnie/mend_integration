<template>
    <transition name="confirm-fade">
        <div v-if="visible" class="confirm-overlay">
            <div class="confirm-dialog">
                <div class="confirm-header">
                    <h4>{{ options.title }}</h4>
                </div>
                <div class="confirm-body">
                    <p>{{ options.message }}</p>
                </div>
                <div class="confirm-footer">
                    <button class="btn btn-secondary" @click="handleCancel">
                        {{ options.cancelText }}
                    </button>
                    <button :class="['btn', 'btn-' + options.confirmVariant]" @click="handleConfirm">
                        {{ options.confirmText }}
                    </button>
                </div>
            </div>
        </div>
    </transition>
</template>

<script setup>
import { useConfirm } from '../composables/useConfirm';

const { visible, options, handleConfirm, handleCancel } = useConfirm();
</script>

<style scoped>
/* 樣式已經在 confirmStyles 中定義，不需要在這裡重複 */
</style>