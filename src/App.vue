<script setup>
import { onMounted } from 'vue'
import { RouterView } from 'vue-router'
import MainNavbar from '@/components/MainNavbar/MainNavbar.vue'
import AppFooter from '@/components/AppFooter/AppFooter.vue'
import seedUsers from '@/data/users.json'

onMounted(() => {
  const existing = JSON.parse(localStorage.getItem('movieUsers') || '[]')
  for (const seed of seedUsers) {
    const idx = existing.findIndex((u) => u.id === seed.id)
    if (idx !== -1) {
      existing[idx] = seed
    } else {
      existing.push(seed)
    }
  }
  localStorage.setItem('movieUsers', JSON.stringify(existing))
})
</script>

<template>
  <div class="flex min-h-screen flex-col text-stone-950">
    <MainNavbar />
    <RouterView />
    <AppFooter />
  </div>
</template>
