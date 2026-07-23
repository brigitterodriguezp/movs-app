<script setup>
import MovieCard from '@/components/MovieCard/MovieCard.vue'
import MovieGridSkeleton from '@/components/skeletons/MovieGridSkeleton/MovieGridSkeleton.vue'
import { addFavorite, getFavorites, getMovies, removeFavorite } from '@/services/api'
import { Heart, Library, Search } from '@lucide/vue'
import { computed, onMounted, ref } from 'vue'

const isLoading = ref(true)
const searchQuery = ref('')
const allMovies = ref([])
const favoriteIds = ref(new Set())
const onlyFavorites = ref(false)
const errorMessage = ref('')
const skeletonMovies = Array.from({ length: 8 }, (_, id) => ({ id }))

const movies = computed(() => {
  const q = searchQuery.value.toLowerCase().trim()
  const source = onlyFavorites.value
    ? allMovies.value.filter((movie) => favoriteIds.value.has(movie.id))
    : allMovies.value
  if (!q) return source
  return source.filter(
    (m) =>
      m.title?.toLowerCase().includes(q) ||
      m.mood?.toLowerCase().includes(q) ||
      m.year?.includes(q) ||
      m.description?.toLowerCase().includes(q)
  )
})

async function toggleFavorite(movieId) {
  try {
    if (favoriteIds.value.has(movieId)) await removeFavorite(movieId)
    else await addFavorite(movieId)
    const next = new Set(favoriteIds.value)
    next.has(movieId) ? next.delete(movieId) : next.add(movieId)
    favoriteIds.value = next
  } catch (error) {
    errorMessage.value = error.message
  }
}

onMounted(async () => {
  try {
    const [moviesData, favoritesData] = await Promise.all([getMovies(), getFavorites()])
    allMovies.value = moviesData.map((movie) => ({
      id: movie.id,
      title: movie.titulo,
      year: String(movie.anio),
      mood: movie.genero,
      description: movie.descripcion,
      sourceUrl: movie.imagenUrl,
    }))
    favoriteIds.value = new Set(favoritesData.map((movie) => movie.id))
  } catch (error) {
    errorMessage.value = error.message
  } finally {
    isLoading.value = false
  }
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
            <Search :size="16" class="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2" style="color: var(--color-text-muted);" />
            <input
              v-model="searchQuery"
              class="form-control rounded-pill py-2 pl-10 pr-4 text-sm"
              placeholder="Buscar películas..."
              type="text"
            />
          </div>
          <span class="ios-chip w-fit rounded-pill px-4 py-2 text-sm" style="color: var(--color-text-secondary);">
            {{ movies.length }} de {{ allMovies.length }} títulos reales
          </span>
          <button class="ios-chip rounded-pill px-4 py-2 text-sm" type="button" @click="onlyFavorites = !onlyFavorites">
            <Heart :size="16" class="inline" /> {{ onlyFavorites ? 'Ver catálogo' : 'Mis favoritos' }}
          </button>
        </div>
      </div>

      <MovieGridSkeleton v-if="isLoading" :movies="skeletonMovies" />
      <p v-else-if="errorMessage" class="ios-surface rounded-[1.35rem] p-6 text-center text-rose-700">{{ errorMessage }}</p>
      <div v-else-if="!movies.length" class="ios-surface rounded-[1.35rem] p-8 text-center">
        <Search :size="40" class="mx-auto mb-4" style="color: var(--color-text-muted);" />
        <h2 class="mb-2 text-xl font-semibold" style="color: var(--color-text);">Sin resultados</h2>
        <p style="color: var(--color-text-muted);">No encontramos películas con ese criterio.</p>
      </div>
      <div v-else class="movie-bento-grid">
        <MovieCard
          v-for="(movie, index) in movies"
          :key="movie.id"
          :movie="movie"
          :favorite="favoriteIds.has(movie.id)"
          :variant="movie.variant"
          :delay="index * 70"
          @toggle-favorite="toggleFavorite"
        />
      </div>
    </section>
  </main>
</template>

<style scoped src="./MovieAppView.css"></style>
