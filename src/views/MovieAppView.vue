<script setup>
import MovieCard from '@/components/MovieCard.vue'
import MovieGridSkeleton from '@/components/skeletons/MovieGridSkeleton.vue'
import moviesApi from '@/data/movies.json'
import { Library } from '@lucide/vue'
import { onMounted, ref } from 'vue'

const movieImages = import.meta.glob('../assets/movies/*.png', {
  eager: true,
  import: 'default',
})

const movies = moviesApi.map((movie) => ({
  ...movie,
  image: movieImages[`../assets/movies/${movie.image}`],
}))

const isLoading = ref(true)
const skeletonMovies = moviesApi.map((movie) => ({
  id: movie.id,
  variant: movie.variant || '',
}))

onMounted(() => {
  window.setTimeout(() => {
    isLoading.value = false
  }, 750)
})
</script>

<template>
  <main class="page-shell px-4 pb-4 pt-32 sm:px-6 lg:px-10">
    <section class="mx-auto max-w-7xl">
      <div class="mb-8 flex flex-col justify-between gap-4 md:flex-row md:items-end">
        <div>
          <p class="mb-2 inline-flex items-center gap-2 text-sm uppercase tracking-[0.28em] text-emerald-700">
            <Library :size="17" />
            <span>Mi selección</span>
          </p>
          <h1 class="text-4xl font-semibold tracking-normal text-stone-950 sm:text-5xl">Movies App</h1>
        </div>
        <span class="ios-chip w-fit rounded-pill px-4 py-2 text-sm text-stone-600">
          {{ movies.length }} títulos guardados
        </span>
      </div>

      <MovieGridSkeleton v-if="isLoading" :movies="skeletonMovies" />
      <div v-else class="movie-bento-grid">
        <MovieCard
          v-for="(movie, index) in movies"
          :key="movie.id"
          :movie="movie"
          :image="movie.image"
          :variant="movie.variant"
          :delay="index * 70"
        />
      </div>
    </section>
  </main>
</template>
