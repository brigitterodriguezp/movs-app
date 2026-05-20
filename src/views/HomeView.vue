<script setup>
import HomeSkeleton from '@/components/skeletons/HomeSkeleton.vue'
import coverImage from '@/assets/movies/main-cover.png'
import { Play, UserPlus } from '@lucide/vue'
import { onMounted, ref } from 'vue'

const isLoading = ref(true)

onMounted(() => {
  window.setTimeout(() => {
    isLoading.value = false
  }, 650)
})
</script>

<template>
  <main class="page-shell flex min-h-screen items-center justify-center px-4 py-28">
    <HomeSkeleton v-if="isLoading" />
    <section v-else class="home-cover group relative w-[80%] max-w-6xl overflow-hidden rounded-[2rem]">
      <img
        class="h-[76vh] min-h-[540px] w-full object-cover transition duration-700 ease-out group-hover:scale-[1.025]"
        :src="coverImage"
        alt="Cover de película"
      />
      <div class="absolute inset-0 bg-gradient-to-t from-black/80 via-black/25 to-transparent"></div>
      <div class="absolute bottom-0 left-0 max-w-3xl p-6 text-white sm:p-10 lg:p-14">
        <p class="mb-3 text-sm uppercase tracking-[0.35em] text-emerald-200/90">Cine coreano</p>
        <h1 class="text-balance text-4xl font-semibold tracking-normal sm:text-6xl">
          Películas para días lentos
        </h1>
        <p class="mt-5 max-w-xl text-base leading-7 text-white/80 sm:text-lg">
          Guarda historias, vuelve a ellas y encuentra tu próxima película sin ruido.
        </p>
        <p class="mt-6 max-w-lg text-sm italic text-white/75 sm:text-base">
          "La vida también florece entre mandarinas."
        </p>
        <div class="mt-8 flex flex-wrap gap-3">
          <RouterLink class="btn btn-light rounded-pill px-4 py-2 soft-button icon-link" to="/app">
            <Play :size="17" />
            <span>Explorar</span>
          </RouterLink>
          <RouterLink class="btn btn-outline-light rounded-pill px-4 py-2 soft-button icon-link" to="/signup">
            <UserPlus :size="17" />
            <span>Crear cuenta</span>
          </RouterLink>
        </div>
      </div>
    </section>
  </main>
</template>
