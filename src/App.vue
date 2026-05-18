<script setup>
import { RouterView } from 'vue-router'
import MainNavbar from '@/components/MainNavbar.vue'
import AppFooter from '@/components/AppFooter.vue'
</script>

<template>
  <div class="flex min-h-screen flex-col text-stone-950">
    <MainNavbar />
    <RouterView />
    <AppFooter />
  </div>
</template>
