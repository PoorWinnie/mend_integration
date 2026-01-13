<template>
    <span>
        <span v-if="total > 0">查到{{ total }}筆資料</span>
        <span v-else>查無資料</span>，每頁顯示
        <select @change="doChange" :value="modelValue">
            <option v-for="option in options" :key="option" :value="option">{{ option }}</option>
        </select>
        筆
    </span>
</template>

<script setup>
const props = defineProps(["total", "options", "modelValue"])
const emits = defineEmits(["rowsChanged", "update:modelValue"])
function doChange(event) {
    emits('update:modelValue', Number(event.target.value));
    emits('rowsChanged');
}
</script>
