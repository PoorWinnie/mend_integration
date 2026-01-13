<template>
  <div class="container-fluid">
    <div class="row">
      <div class="col-12">
        <MainContent 
          :currentUser="currentUser" 
          :currentView="currentView"
          @view-changed="handleViewChange"
        />
      </div>
    </div>

    <BackToTop />
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import MainContent from '@/views/reviews/MainContent.vue';
import BackToTop from '@/views/reviews/BackToTop.vue';
import { useUserStore } from '@/stores/userStore';
import { storeToRefs } from 'pinia';

export default defineComponent({
  name: 'RootLayout',
  components: {
    MainContent,
    BackToTop
  },
  setup() {
    const currentView = ref('connector');

    // ✅ 使用 Pinia 拿取使用者資料
    const userStore = useUserStore();
    const { currentUser } = storeToRefs(userStore);

    const handleViewChange = (view) => {
      console.log('視圖變更為:', view);
      currentView.value = view;
      localStorage.setItem('currentView', view);
    };

    onMounted(() => {
      console.log('RootLayout 組件掛載完成');
      localStorage.removeItem('currentView');
      currentView.value = 'connector';
      localStorage.setItem('currentView', 'connector');
    });

    return {
      currentView,
      handleViewChange,
      currentUser // ✅ 傳給 template 使用
    };
  }
});
</script>

<style scoped>
.login-container {
  max-width: 500px;
  margin: 0 auto;
  padding: 20px;
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style>
