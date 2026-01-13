<template>
    <swiper
      :modules="[Autoplay, Navigation, Pagination]"
      :autoplay="{ delay: 4000 }"
      :loop="true"
      :pagination="{ clickable: true }"
      :navigation="true"
      class="banner-swiper"
    >
      <swiper-slide v-for="(img, index) in images" :key="index">
        <img :src="img" class="banner-image" />
      </swiper-slide>
    </swiper>
  </template>
  
  <script setup>
  import { Swiper, SwiperSlide } from 'swiper/vue';
  import { Autoplay, Navigation, Pagination } from 'swiper/modules';
  
  import 'swiper/css';
  import 'swiper/css/navigation';
  import 'swiper/css/pagination';
  
  const images = [
    '/img/banner/banner1.png',
    '/img/banner/banner2.png',
    '/img/banner/banner3.jpg',
  ];
  </script>
  
  <style scoped>
  .banner-swiper {
    width: 100%;
    height: 400px;
    border-radius: 12px 12px 12px 12px;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
  
  .banner-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  </style>
  