// @/stores/bookingCartStore.js
import { defineStore } from 'pinia';

export const useBookingCartStore = defineStore('bookingCart', {
  state: () => ({
    selectedSpots: [],
    confirmed: false,
  }),
  actions: {
    updateSpotQuantity(spotId, spotData) {
      const idx = this.selectedSpots.findIndex(s => s.spotId === spotId);
      if (spotData.quantity === 0) {
        this.selectedSpots.splice(idx, 1); // 刪除
      } else if (idx >= 0) {
        this.selectedSpots[idx] = spotData;
      } else {
        this.selectedSpots.push(spotData);
      }
    },
    clear() {
      this.selectedSpots = [];
    },
  },
  getters: {
    totalAmount(state) {
      return state.selectedSpots.reduce((sum, s) => sum + s.subtotal, 0);
    }
  }
});
