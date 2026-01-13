import { defineStore } from 'pinia'

export const useCartStore = defineStore('cart', {
    state: () => ({
        selectedItems: [],
        userName: ''
    }),
    actions: {
        setSelectedItems(items) {
            this.selectedItems = items;
        },
        setUserName(name) {
            this.userName = name;  // ✅ 登入成功後設定
        },
        resetCart() {
            this.selectedItems = [];
            this.userName = '';
        }
    },
    persist: true
})
