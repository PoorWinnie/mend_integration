import { defineStore } from 'pinia'
import { ref } from 'vue'

export default defineStore('product', function() {
    const products = ref([])
    function setProducts(list) {
        products.value = list
    }
    return {
        products, setProducts
    }
})
