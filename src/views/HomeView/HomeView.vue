<script setup>
import HomeSkeleton from '@/components/skeletons/HomeSkeleton/HomeSkeleton.vue'
import coverImage from '@/assets/movies/main-cover.png'
import videoTrailer from '@/assets/videos/video_trailer.mp4'
import { Play, Pause, UserPlus } from '@lucide/vue'
import { onMounted, ref } from 'vue'

const isLoading = ref(true)
const isPlaying = ref(false)
const videoVisible = ref(false)
const videoRef = ref(null)
const coverRef = ref(null)

onMounted(() => {
  window.setTimeout(() => {
    isLoading.value = false
  }, 650)
})

function toggleVideo() {
  if (!videoRef.value) return
  if (!isPlaying.value) {
    videoVisible.value = true
    isPlaying.value = true
    const savedTime = parseFloat(localStorage.getItem('movieTrailerTime') || '0')
    videoRef.value.currentTime = savedTime
    videoRef.value.volume = 0
    videoRef.value.play()
    const interval = setInterval(() => {
      if (!videoRef.value) { clearInterval(interval); return }
      videoRef.value.volume = Math.min(1, videoRef.value.volume + 0.04)
      if (videoRef.value.volume >= 1) clearInterval(interval)
    }, 60)
  } else {
    const interval = setInterval(() => {
      if (!videoRef.value) { clearInterval(interval); return }
      videoRef.value.volume = Math.max(0, videoRef.value.volume - 0.04)
      if (videoRef.value.volume <= 0) {
        clearInterval(interval)
        videoRef.value.pause()
        isPlaying.value = false
        videoVisible.value = false
      }
    }, 60)
  }
}

function onTimeUpdate() {
  if (videoRef.value) {
    localStorage.setItem('movieTrailerTime', videoRef.value.currentTime)
  }
}

function onVideoEnded() {
  isPlaying.value = false
  videoVisible.value = false
}
</script>

<template>
  <main class="page-shell flex min-h-[60vh] flex-col items-center px-4 pt-28">
    <HomeSkeleton v-if="isLoading" />
    <section
      v-else
      ref="coverRef"
      class="home-cover group relative overflow-hidden rounded-[2rem]"
      :class="isPlaying ? 'w-full max-w-none' : 'w-[92%] max-w-6xl md:w-[80%]'"
    >
      <img
        class="h-[72vh] min-h-[460px] w-full object-cover"
        :class="{ 'opacity-0': videoVisible, 'group-hover:scale-[1.025]': !isPlaying }"
        :src="coverImage"
        alt="Cover de película"
      />
      <video
        ref="videoRef"
        :src="videoTrailer"
        playsinline
        @timeupdate="onTimeUpdate"
        @ended="onVideoEnded"
        class="absolute inset-0 h-full w-full object-cover transition-opacity duration-700 ease-out"
        :class="videoVisible ? 'opacity-100' : 'opacity-0'"
        style="pointer-events: none;"
      ></video>
      <div class="absolute inset-0 bg-gradient-to-t from-black/80 via-black/25 to-transparent"></div>
      <div class="absolute bottom-0 left-0 right-0 p-6 text-white sm:p-10 lg:p-14">
        <div
          class="max-w-3xl transition-all duration-500 ease-out"
          :class="isPlaying ? 'opacity-0 pointer-events-none' : 'opacity-100'"
        >
          <p class="mb-3 text-sm uppercase tracking-[0.35em]" style="color: rgba(255, 45, 85, 0.75);">Cine coreano</p>
          <h1 class="text-balance text-4xl font-semibold tracking-normal sm:text-6xl">
            Películas para días lentos
          </h1>
          <p class="mt-5 max-w-xl text-base leading-7 text-white/80 sm:text-lg">
            Guarda historias, vuelve a ellas y encuentra tu próxima película sin ruido.
          </p>
          <p class="mt-6 max-w-lg text-sm italic text-white/75 sm:text-base">
            "La vida también florece entre mandarinas."
          </p>
        </div>
        <div class="mt-8 flex flex-wrap gap-3" :class="isPlaying ? 'justify-center' : ''">
          <button class="btn btn-light rounded-pill px-4 py-2 soft-button icon-link" type="button" @click.stop="toggleVideo">
            <Play v-if="!isPlaying" :size="17" />
            <Pause v-else :size="17" />
            <span v-if="!isPlaying">Ver ahora</span>
          </button>
          <RouterLink v-if="!isPlaying" class="btn btn-outline-light rounded-pill px-4 py-2 soft-button icon-link" to="/signup">
            <UserPlus :size="17" />
            <span>Crear cuenta</span>
          </RouterLink>
        </div>
      </div>
      <div
        v-show="isPlaying"
        class="absolute bottom-0 left-0 right-0 flex items-center justify-center pb-8 sm:pb-12 pointer-events-none"
      >
        <p class="text-balance text-center text-lg italic text-white/85 sm:text-2xl">
          "La vida florece entre mandarinas."
        </p>
      </div>
    </section>
  </main>
</template>

<style scoped src="./HomeView.css"></style>
