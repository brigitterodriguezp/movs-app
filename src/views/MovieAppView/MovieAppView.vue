<script setup>
import MovieCard from '@/components/MovieCard/MovieCard.vue'
import MovieGridSkeleton from '@/components/skeletons/MovieGridSkeleton/MovieGridSkeleton.vue'
import moviesApi from '@/data/movies.json'
import { Library, Search } from '@lucide/vue'
import { computed, onMounted, ref } from 'vue'

const movieImages = import.meta.glob('../../assets/movies/*.png', {
  eager: true,
  import: 'default',
})

const allMovies = moviesApi.map((movie) => ({
  ...movie,
  image: movieImages[`../../assets/movies/${movie.image}`],
}))

const isLoading = ref(true)
const searchQuery = ref('')
const skeletonMovies = moviesApi.map((movie) => ({
  id: movie.id,
  variant: movie.variant || '',
}))

const movies = computed(() => {
  const q = searchQuery.value.toLowerCase().trim()
  if (!q) return allMovies
  return allMovies.filter(
    (m) =>
      m.title?.toLowerCase().includes(q) ||
      m.mood?.toLowerCase().includes(q) ||
      m.year?.includes(q) ||
      m.description?.toLowerCase().includes(q)
  )
})

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
          <p class="mb-2 inline-flex items-center gap-2 text-sm uppercase tracking-[0.28em]" style="color: var(--color-accent-text);">
            <Library :size="17" />
            <span>Mi selección</span>
          </p>
          <h1 class="text-4xl font-semibold tracking-normal text-stone-950 sm:text-5xl">Movies App</h1>
        </div>
        <div class="flex items-center gap-3">
          <div class="relative">
            <Search :size="16" class="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-stone-400" />
            <input
              v-model="searchQuery"
              class="form-control rounded-pill py-2 pl-9 pr-4 text-sm"
              placeholder="Buscar películas..."
              type="text"
            />
          </div>
          <span class="ios-chip w-fit rounded-pill px-4 py-2 text-sm text-stone-600">
            {{ movies.length }} de {{ allMovies.length }} títulos
          </span>
        </div>
      </div>

      <MovieGridSkeleton v-if="isLoading" :movies="skeletonMovies" />
      <div v-else-if="!movies.length" class="ios-surface rounded-[1.35rem] p-8 text-center">
        <Search :size="40" class="mx-auto mb-4 text-stone-400" />
        <h2 class="mb-2 text-xl font-semibold text-stone-700">Sin resultados</h2>
        <p class="text-stone-500">No encontramos películas con ese criterio.</p>
      </div>
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

<style scoped src="./MovieAppView.css"></style>
