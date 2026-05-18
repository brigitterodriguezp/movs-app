<script setup>
import { Calendar, Tag } from '@lucide/vue'

defineProps({
  movie: {
    type: Object,
    required: true,
  },
  image: {
    type: String,
    required: true,
  },
  delay: {
    type: Number,
    default: 0,
  },
  variant: {
    type: String,
    default: '',
  },
})
</script>

<template>
  <article
    class="movie-card group overflow-hidden rounded-[1.5rem]"
    :class="variant"
    :style="{ animationDelay: `${delay}ms` }"
  >
    <div class="relative aspect-[4/5] overflow-hidden">
      <img
        class="h-full w-full object-cover transition duration-700 ease-out group-hover:scale-[1.035]"
        :src="image"
        :alt="movie.title"
      />
      <div class="absolute inset-0 bg-gradient-to-t from-black/70 via-black/10 to-transparent"></div>
      <span class="absolute left-4 top-4 inline-flex items-center gap-1 rounded-pill bg-white/85 px-3 py-1 text-xs font-medium text-stone-800">
        <Tag :size="13" />
        {{ movie.mood }}
      </span>
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
    </div>
  </article>
</template>
