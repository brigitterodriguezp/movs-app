<script setup>
import { ChevronDown, Film, Heart, X } from '@lucide/vue'
import { onUnmounted, ref } from 'vue'

const descriptionOpen = ref(false)
const modalOpen = ref(false)

const defaultMovieImage = `${import.meta.env.BASE_URL}default_movie.png`

function useFallbackImage(event) {
  const image = event.currentTarget
  const fallback = new URL(defaultMovieImage, window.location.origin).href
  if (image.src !== fallback) image.src = defaultMovieImage
}

function openModal() {
  modalOpen.value = true
  document.body.style.overflow = 'hidden'
}

function closeModal() {
  modalOpen.value = false
  document.body.style.overflow = ''
}

onUnmounted(() => {
  document.body.style.overflow = ''
})

defineProps({
  movie: {
    type: Object,
    required: true,
  },
  image: {
    type: String,
    default: '',
  },
  delay: {
    type: Number,
    default: 0,
  },
  variant: {
    type: String,
    default: '',
  },
  favorite: {
    type: Boolean,
    default: false,
  },
})

defineEmits(['toggle-favorite'])
</script>

<template>
  <article
    class="movie-card group overflow-hidden rounded-xl"
    :class="variant"
    :style="{ animationDelay: `${delay}ms` }"
  >
    <div
      class="movie-card-poster relative cursor-pointer overflow-hidden"
      role="button"
      tabindex="0"
      :aria-label="`Ver información completa de ${movie.title}`"
      @click="openModal"
      @keydown.enter="openModal"
      @keydown.space.prevent="openModal"
    >
      <img
        v-if="image"
        class="h-full w-full object-cover transition duration-700 ease-out group-hover:scale-[1.035]"
        :src="image"
        :alt="movie.title"
        @error="useFallbackImage"
      />
      <div v-else class="flex h-full items-center justify-center bg-stone-200 text-stone-500">
        <Film :size="54" />
      </div>
      <div class="absolute inset-0 bg-gradient-to-t from-black/90 via-black/5 to-transparent"></div>
      <button
        class="movie-favorite absolute right-3 top-3 rounded-full p-2 text-white"
        type="button"
        :aria-label="favorite ? 'Quitar de favoritos' : 'Agregar a favoritos'"
        @click.stop="$emit('toggle-favorite', movie.id)"
      >
        <Heart :size="18" :fill="favorite ? 'currentColor' : 'none'" />
      </button>
      <div class="absolute inset-x-0 bottom-0 p-3 text-white">
        <h2 class="movie-card-title m-0 text-base font-semibold">{{ movie.title }}</h2>
        <p class="m-0 mt-1 text-xs text-white/70">{{ movie.mood }} · {{ movie.year }}</p>
      </div>
    </div>
    <div class="movie-card-details px-3 py-2.5">
      <button
        class="movie-description-toggle flex w-full items-center justify-between text-xs font-medium"
        type="button"
        :aria-expanded="descriptionOpen"
        @click="descriptionOpen = !descriptionOpen"
      >
        <span>Sinopsis</span>
        <ChevronDown :size="15" :class="{ 'rotate-180': descriptionOpen }" />
      </button>
      <Transition name="description-drop">
        <p v-if="descriptionOpen" class="movie-description m-0 pt-2 text-xs leading-5">{{ movie.description }}</p>
      </Transition>
    </div>
  </article>

  <Teleport to="body">
    <Transition name="movie-modal">
      <div v-if="modalOpen" class="movie-modal-backdrop fixed inset-0 flex items-center justify-center p-4" @click.self="closeModal">
        <article class="movie-modal relative grid w-full max-w-3xl overflow-hidden rounded-2xl md:grid-cols-[0.85fr_1.15fr]" role="dialog" aria-modal="true" :aria-label="movie.title">
          <button class="movie-modal-close absolute right-3 top-3 z-10 rounded-full p-2" type="button" aria-label="Cerrar detalles" @click="closeModal">
            <X :size="20" />
          </button>
          <img
            v-if="image"
            class="h-64 w-full object-cover md:h-full"
            :src="image"
            :alt="movie.title"
            @error="useFallbackImage"
          />
          <div v-else class="flex min-h-64 items-center justify-center bg-stone-200 text-stone-500">
            <Film :size="64" />
          </div>
          <div class="movie-modal-content p-6 sm:p-8">
            <p class="mb-2 text-sm font-semibold uppercase tracking-[0.18em]">{{ movie.mood }} · {{ movie.year }}</p>
            <h2 class="m-0 text-3xl font-semibold">{{ movie.title }}</h2>
            <h3 class="mb-2 mt-6 text-sm font-semibold uppercase tracking-[0.15em]">Sinopsis completa</h3>
            <p class="m-0 text-sm leading-7">{{ movie.fullDescription || movie.description }}</p>
          </div>
        </article>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.movie-card {
  position: relative;
  background: #fff;
  border: 1px solid rgba(28, 25, 23, 0.12);
  transition: transform 180ms ease, box-shadow 180ms ease, border-color 180ms ease;
}

:global(.dark) .movie-card {
  background: #181818;
  border-color: rgba(255, 255, 255, 0.1);
}

.movie-card:hover {
  z-index: 2;
  transform: translateY(-3px) scale(1.018);
  border-color: rgba(28, 25, 23, 0.22);
  box-shadow: 0 16px 36px rgba(28, 25, 23, 0.18);
}

.movie-card-poster {
  aspect-ratio: 16 / 9;
}

.movie-card-title {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.movie-favorite {
  background: rgba(20, 20, 20, 0.72);
  border: 1px solid rgba(255, 255, 255, 0.35);
  backdrop-filter: blur(8px);
}

.movie-favorite:hover {
  background: rgba(229, 9, 20, 0.92);
  border-color: transparent;
}

.movie-card-details {
  color: #292524;
}

.movie-description-toggle {
  color: #57534e;
}

.movie-description-toggle:hover {
  color: #1c1917;
}

.movie-description-toggle svg {
  transition: transform 180ms ease;
}

.movie-description {
  color: #57534e;
}

:global(.dark) .movie-card-details,
:global(.dark) .movie-description-toggle {
  color: rgba(255, 255, 255, 0.76);
}

:global(.dark) .movie-description-toggle:hover {
  color: #fff;
}

:global(.dark) .movie-description {
  color: rgba(255, 255, 255, 0.62);
}

.description-drop-enter-active,
.description-drop-leave-active {
  transition: opacity 160ms ease, transform 160ms ease;
}

.description-drop-enter-from,
.description-drop-leave-to {
  opacity: 0;
  transform: translateY(-5px);
}

.movie-modal-backdrop {
  z-index: 9999;
  background: rgba(12, 10, 9, 0.72);
  backdrop-filter: blur(12px);
}

.movie-modal {
  max-height: min(88vh, 46rem);
  background: #fff;
  color: #1c1917;
  box-shadow: 0 30px 90px rgba(0, 0, 0, 0.38);
}

.movie-modal-content {
  overflow-y: auto;
}

.movie-modal-content > p:first-child,
.movie-modal-content h3 {
  color: #78716c;
}

.movie-modal-close {
  color: #fff;
  background: rgba(20, 20, 20, 0.72);
  backdrop-filter: blur(8px);
}

:global(.dark) .movie-modal {
  background: #181818;
  color: #fff;
}

:global(.dark) .movie-modal-content > p:first-child,
:global(.dark) .movie-modal-content h3,
:global(.dark) .movie-modal-content > p:last-child {
  color: rgba(255, 255, 255, 0.68);
}

.movie-modal-enter-active,
.movie-modal-leave-active {
  transition: opacity 180ms ease;
}

.movie-modal-enter-active .movie-modal,
.movie-modal-leave-active .movie-modal {
  transition: transform 180ms ease;
}

.movie-modal-enter-from,
.movie-modal-leave-to {
  opacity: 0;
}

.movie-modal-enter-from .movie-modal,
.movie-modal-leave-to .movie-modal {
  transform: translateY(12px) scale(0.98);
}
</style>
