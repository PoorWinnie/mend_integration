// stores/useBookingStore.js
import { defineStore } from 'pinia'

export const useBookingStore = defineStore('booking', {
    state: () => ({
        campSpotId: null,
        dateRange: {
            startDate: null,
            endDate: null,
        }
    }),
    actions: {
        setBookingInfo(id, startDate, endDate) {
            this.campSpotId = id
            this.dateRange.startDate = new Date(startDate)
            this.dateRange.endDate = new Date(endDate)
        },
    },
    persist: true
})
