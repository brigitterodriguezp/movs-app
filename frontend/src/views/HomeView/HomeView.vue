<script setup>
import HomeSkeleton from '@/components/skeletons/HomeSkeleton/HomeSkeleton.vue'
import coverImage from '@/assets/movies/main-cover.png'
import videoTrailer from '@/assets/videos/video_trailer.mp4'
import { Play, Pause } from '@lucide/vue'
import { onMounted, onUnmounted, ref } from 'vue'

const isLoading = ref(true)
const isPlaying = ref(false)
const videoVisible = ref(false)
const showWelcome = ref(!localStorage.getItem('welcomeDone'))
const videoRef = ref(null)
const coverRef = ref(null)

onMounted(() => {
  if (showWelcome.value) {
    document.documentElement.style.overflow = 'hidden'
    document.body.style.overflow = 'hidden'
  }
  window.setTimeout(() => {
    isLoading.value = false
  }, 650)
})

onUnmounted(() => {
  document.documentElement.style.overflow = ''
  document.body.style.overflow = ''
})

function startVideo() {
  if (!videoRef.value) return
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
}

function toggleVideo() {
  if (!videoRef.value) return
  if (!isPlaying.value) {
    startVideo()
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

function dismissWelcome() {
  localStorage.setItem('welcomeDone', 'true')
  document.documentElement.style.overflow = ''
  document.body.style.overflow = ''
  showWelcome.value = false
  startVideo()
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
  <div
    v-if="showWelcome && !isLoading"
    class="fixed inset-0 z-[9999] flex items-center justify-center"
    style="background: var(--color-page);"
  >
    <div class="ios-surface relative z-10 w-[88%] max-w-3xl rounded-[2rem] flex overflow-hidden min-h-[28rem]">
      <div class="w-[40%] relative hidden sm:block min-h-[28rem]">
        <img
          :src="coverImage"
          class="absolute inset-0 h-full w-full object-cover"
          alt=""
        />
      </div>
      <div class="flex-1 p-8 sm:p-10 text-center flex flex-col justify-center gap-3">
        <h3 class="text-4xl font-black sm:text-5xl" style="color: var(--color-text);">Si la vida te da mandarinas</h3>
        <p class="text-sm leading-6" style="color: var(--color-text-muted); font-style: italic;">
          Sooni nunca pidió permiso. Gwansik construyó un mundo para sus versos.
        </p>

        <button
          class="mt-6 w-full rounded-pill px-5 py-2.5 soft-button icon-link justify-center gap-2 text-base glass-accent-btn"
          style="border: 1px solid var(--color-accent-border);"
          type="button"
          @click="dismissWelcome"
        >
          <Play :size="18" />
          <span>Continuar</span>
        </button>
      </div>
    </div>
  </div>

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
          <p class="mt-6 max-w-lg text-sm font-bold text-white/75 sm:text-base">
            Si la vida te da mandarinas
          </p>
          <p class="mt-4 text-[10px] uppercase tracking-[0.25em] opacity-60">
            Esperanza, Resiliencia, Vida
          </p>
        </div>
        <div class="mt-6 flex flex-wrap gap-3" :class="isPlaying ? 'justify-center' : ''">
          <button class="btn btn-light rounded-pill px-4 py-2 soft-button icon-link" type="button" @click.stop="toggleVideo">
            <Play v-if="!isPlaying" :size="17" />
            <Pause v-else :size="17" />
            <span v-if="!isPlaying">Ver ahora</span>
          </button>
        </div>
      </div>
    </section>
  </main>
</template>

<style scoped src="./HomeView.css"></style>
