import { defineStore } from 'pinia'

export const useCampSearchStore = defineStore('campSearch', {
  state: () => ({
    searchPayload: null
  }),
  actions: {
    setSearchPayload(payload) {
      this.searchPayload = payload
    },
    clearSearchPayload() {
      this.searchPayload = null
    }
  },
  persist: true  
})
