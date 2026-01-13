<template>
    <div ref="example" class="modal fade" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5">產品資料</h1>
                    <button type="button" class="btn-close" @click="hideModal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <!-- 顯示當前產品圖片 -->
                        <div class="text-center mb-3" v-if="product.productId">
                            <img :src="getProductImageUrl(product.productId)" 
                                class="product-preview-img mb-2" alt="產品圖片">
                        </div>
                        <!-- 圖片上傳區域 -->
                        <div class="image-upload-container">
                            <label for="product-image" class="form-label">產品圖片</label>
                            <input type="file" class="form-control" id="product-image" 
                                   accept="image/jpeg, image/png, image/gif" 
                                   @change="handleImageUpload">
                            <div class="form-text">支援 JPG、PNG 和 GIF 格式圖片，建議尺寸 500x500 像素。</div>
                        </div>
                        <div v-if="imagePreview" class="text-center mt-3">
                            <p>預覽圖片:</p>
                            <img :src="imagePreview" class="product-preview-img" alt="圖片預覽">
                        </div>
                    </div>
                    <table class="w-100">
                        <tbody>
                            <tr>
                                <td>ID : </td>
                                <td>
                                    <input type="text" class="form-control" :value="product.productId"
                                           @input="doInput('productId', $event.target.value)">
                                </td>
                            </tr>
                            <tr>
                                <td>名稱 : </td>
                                <td>
                                    <input type="text" class="form-control" :value="product.name"
                                           @input="doInput('name', $event.target.value)">
                                </td>
                            </tr>
                            <tr>
                                <td>價格 : </td>
                                <td>
                                    <input type="number" class="form-control" :value="product.price"
                                           @input="doInput('price', $event.target.value)">
                                </td>
                            </tr>
                            <tr>
                                <td>庫存 : </td>
                                <td>
                                    <input type="number" class="form-control" :value="product.stock"
                                           @input="doInput('stock', $event.target.value)">
                                </td>
                            </tr>
                            <tr>
                                <td>產品類型 : </td>
                                <td>
                                    <select class="form-select" :value="product.productTypeId"
                                            @change="doInput('productTypeId', $event.target.value)">
                                        <option value="">請選擇類型</option>
                                        <option v-for="type in productTypes" :key="type.id" :value="type.id">
                                            {{ type.typeName }}
                                        </option>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td>描述 : </td>
                                <td>
                                    <textarea class="form-control" :value="product.description"
                                           @input="doInput('description', $event.target.value)"></textarea>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" @click="submitProduct"
                            v-show="isShowInsertButton">新增</button>
                    <button type="button" class="btn btn-primary" @click="submitProduct"
                            v-show="!isShowInsertButton">修改</button>
                    <button type="button" class="btn btn-secondary" @click="hideModal">關閉</button>
                </div>
            </div>
        </div>
    </div>
</template>
    
<script setup>
import { ref, onMounted } from 'vue'
import axiosapi from '@/plugins/axios.js';

const props = defineProps(["product", "isShowInsertButton"]);
const emits = defineEmits(["update:product", "insert", "update"]);

const imageFile = ref(null);
const imagePreview = ref(null);
const productTypes = ref([]);

// 獲取產品類型列表
async function fetchProductTypes() {
    try {
        // 確保獲取最新的 token
        const token = localStorage.getItem('token');
        console.log('當前使用的 token:', token);
        
        const response = await axiosapi.get('/ajax/product-types', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        if(response.data && response.data.list) {
            productTypes.value = response.data.list;
        }
    } catch (error) {
        console.error('獲取產品類型失敗:', error);
        
        // 如果是 401 錯誤，可以添加錯誤處理邏輯
        if (error.response && error.response.status === 401) {
            // 可以在這裡添加重新登入的邏輯
            console.log('需要重新登入');
        }
    }
}

// 根據ID獲取產品類型名稱
function getSelectedProductTypeName(typeId) {
    if (!typeId) return "";
    
    // 確保typeId是整數
    const id = parseInt(typeId);
    const foundType = productTypes.value.find(type => type.id === id);
    return foundType ? foundType.typeName : "";
}

// 獲取產品圖片URL (添加時間戳防止快取)
function getProductImageUrl(productId) {
    if (!productId) return '';
    const timestamp = new Date().getTime();
    console.log(`${productId}`);
    return `${axiosapi.defaults.baseURL}/ajax/products/images/${productId}?t=${timestamp}`;
}

function doInput(field, value) {
    emits("update:product", {
        ...props.product,
        [field]: value
    });
}

function handleImageUpload(event) {
    const file = event.target.files[0];
    if (file) {
        imageFile.value = file;
        // 建立預覽圖片
        const reader = new FileReader();
        reader.onload = (e) => {
            imagePreview.value = e.target.result;
        };
        reader.readAsDataURL(file);
    }
}

function submitProduct() {
    // 修改提交邏輯，不再自行上傳圖片，統一由父組件處理
    if (props.isShowInsertButton) {
        emits("insert");
    } else {
        emits("update");
    }
}

// 獲取上傳的圖片文件，供父組件使用
function getImageFile() {
    return imageFile.value;
}

import bootstrap from 'bootstrap/dist/js/bootstrap.bundle.min.js';
const example = ref(null);
const modal = ref(null);
onMounted(function() {
    modal.value = new bootstrap.Modal(example.value);
    fetchProductTypes(); // 組件掛載時獲取產品類型
});
function showModal() {
    modal.value.show();
    // 重置圖片預覽
    imagePreview.value = null;
    imageFile.value = null;
}
function hideModal() {
    modal.value.hide();
}
defineExpose({
    showModal, hideModal, getImageFile, getSelectedProductTypeName
});
</script>

<style scoped>
table {
    width: 100%;
    margin-bottom: 1rem;
}

table td {
    padding: 0.5rem;
}

.product-preview-img {
    max-width: 200px;
    max-height: 200px;
    border: 1px solid #dee2e6;
    border-radius: 0.25rem;
    object-fit: contain;
}

.image-upload-container {
    border: 1px dashed #dee2e6;
    padding: 1rem;
    border-radius: 0.25rem;
    background-color: #f8f9fa;
}
</style>
