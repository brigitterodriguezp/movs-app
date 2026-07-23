<script setup>
import { Calendar, Film, Heart, Tag } from '@lucide/vue'

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
    class="movie-card group overflow-hidden rounded-[1.5rem]"
    :class="variant"
    :style="{ animationDelay: `${delay}ms` }"
  >
    <div class="relative aspect-[4/5] overflow-hidden">
      <img
        v-if="image"
        class="h-full w-full object-cover transition duration-700 ease-out group-hover:scale-[1.035]"
        :src="image"
        :alt="movie.title"
      />
      <div v-else class="flex h-full items-center justify-center bg-stone-200 text-stone-500">
        <Film :size="54" />
      </div>
      <div class="absolute inset-0 bg-gradient-to-t from-black/70 via-black/10 to-transparent"></div>
      <span class="movie-mood-chip absolute left-4 top-4 inline-flex items-center gap-1 rounded-pill px-3 py-1 text-xs font-medium">
        <Tag :size="13" />
        {{ movie.mood }}
      </span>
      <button
        class="absolute right-4 top-4 rounded-full bg-white/90 p-2 text-rose-600 shadow"
        type="button"
        :aria-label="favorite ? 'Quitar de favoritos' : 'Agregar a favoritos'"
        @click="$emit('toggle-favorite', movie.id)"
      >
        <Heart :size="18" :fill="favorite ? 'currentColor' : 'none'" />
      </button>
    </div>
    <div class="p-5">
      <div class="mb-3 flex items-center justify-between gap-3">
        <h2 class="m-0 text-xl font-semibold tracking-normal text-stone-950">{{ movie.title }}</h2>
        <span class="inline-flex items-center gap-1 text-sm text-stone-500">
          <Calendar :size="15" />
          {{ movie.year }}
        </span>
      </div>
      <p class="m-0 text-sm leading-6 text-stone-600">{{ movie.description }}</p>
      <a v-if="movie.sourceUrl" :href="movie.sourceUrl" target="_blank" rel="noreferrer" class="mt-3 inline-block text-sm text-stone-500 underline">Ficha en TMDb</a>
    </div>
  </article>
</template>
