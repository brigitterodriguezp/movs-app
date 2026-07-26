<script setup>
import MovieCard from '@/components/MovieCard/MovieCard.vue'
import MovieGridSkeleton from '@/components/skeletons/MovieGridSkeleton/MovieGridSkeleton.vue'
import {
  addFavorite, generateWithOllama, getFavorites, getFavoritesPage,
  getMovieMetadata, getMoviePosterUrl, getMoviesPage, removeFavorite,
} from '@/services/api'
import { ArrowLeft, ArrowRight, ChevronsLeft, ChevronsRight, Heart, Library, LoaderCircle, RotateCcw, Search, Send, Sparkles } from '@lucide/vue'
import { nextTick, onMounted, ref, watch } from 'vue'

const GENRE_LABELS = {
  Action: 'Acción', Adventure: 'Aventura', Animation: 'Animación', Comedy: 'Comedia',
  Crime: 'Crimen', Documentary: 'Documental', Drama: 'Drama', Family: 'Familiar',
  Fantasy: 'Fantasía', History: 'Historia', Horror: 'Terror', Music: 'Musical',
  Mystery: 'Misterio', Romance: 'Romance', 'Science Fiction': 'Ciencia ficción',
  Thriller: 'Suspenso', War: 'Bélica', Western: 'Oeste',
}

const GENRE_INTENTS = [
  { genre: 'Thriller', words: ['suspenso', 'thriller', 'tensión', 'tension', 'intriga'] },
  { genre: 'Mystery', words: ['misterio', 'misteriosa', 'misterioso', 'investigación'] },
  { genre: 'Horror', words: ['terror', 'miedo', 'aterradora', 'aterrador'] },
  { genre: 'Romance', words: ['romance', 'romántica', 'romantica', 'amor'] },
  { genre: 'Comedy', words: ['comedia', 'divertida', 'divertido', 'risa'] },
  { genre: 'Science Fiction', words: ['ciencia ficción', 'ciencia ficcion', 'espacial', 'futurista'] },
  { genre: 'Action', words: ['acción', 'accion', 'peleas', 'explosiones'] },
  { genre: 'Adventure', words: ['aventura', 'viaje', 'exploración'] },
  { genre: 'Drama', words: ['drama', 'dramática', 'dramatico', 'emocional'] },
  { genre: 'Family', words: ['familiar', 'familia', 'niños', 'ninos'] },
  { genre: 'Animation', words: ['animación', 'animacion', 'animada', 'animado'] },
  { genre: 'Crime', words: ['crimen', 'criminal', 'mafia', 'policial'] },
]

const aleAvatar = `${import.meta.env.BASE_URL}bot_ollama.png`

const isLoading = ref(true)
const searchQuery = ref('')
const allMovies = ref([])
const favoriteIds = ref(new Set())
const onlyFavorites = ref(false)
const errorMessage = ref('')
const actionError = ref('')
const assistantQuestion = ref('')
const assistantResponse = ref('')
const assistantError = ref('')
const isGenerating = ref(false)
const currentPage = ref(0)
const totalPages = ref(0)
const totalMovies = ref(0)
const skeletonMovies = Array.from({ length: 5 }, (_, id) => ({ id }))
let searchTimer
let suppressSearchReload = false
const suggestedQuestions = [
  'Quiero algo de suspenso',
  'Una película romántica',
  'Recomiéndame según mis favoritos',
]

const movies = allMovies

function mapMovie(movie) {
  return {
    id: movie.id,
    title: movie.titulo,
    year: String(movie.anio),
    mood: GENRE_LABELS[movie.genero] || movie.genero,
    apiGenre: movie.genero,
    description: movie.descripcion,
    sourceUrl: movie.imagenUrl,
    image: getMoviePosterUrl(movie.id),
    variant: movie.variante,
  }
}

function shortDescription(value, maxLength = 220) {
  const text = String(value || '').trim()
  if (text.length <= maxLength) return text
  const cut = text.slice(0, maxLength + 1)
  const lastSpace = cut.lastIndexOf(' ')
  return `${cut.slice(0, lastSpace > 0 ? lastSpace : maxLength).trim()}…`
}

async function localizeMovie(movie) {
  try {
    const metadata = await getMovieMetadata(movie.id)
    return {
      ...movie,
      title: metadata.titulo,
      description: shortDescription(metadata.descripcion),
      fullDescription: metadata.descripcion,
      image: metadata.posterUrl,
    }
  } catch {
    return {
      ...movie,
      description: shortDescription(movie.description),
      fullDescription: movie.description,
    }
  }
}

async function loadPage(page = 0) {
  isLoading.value = true
  errorMessage.value = ''
  try {
    const loader = onlyFavorites.value ? getFavoritesPage : getMoviesPage
    const data = await loader(page, searchQuery.value.trim())
    const pageMovies = data.contenido.map(mapMovie)
    allMovies.value = await Promise.all(pageMovies.map(localizeMovie))
    currentPage.value = data.pagina
    totalPages.value = data.totalPaginas
    totalMovies.value = data.totalElementos
  } catch (error) {
    errorMessage.value = error.message
  } finally {
    isLoading.value = false
  }
}

function changePage(page) {
  if (page < 0 || page >= totalPages.value || page === currentPage.value) return
  loadPage(page).then(() => {
    document.querySelector('.movie-bento-grid')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
  })
}

function toggleFavoritesView() {
  onlyFavorites.value = !onlyFavorites.value
  loadPage(0)
}

watch(searchQuery, () => {
  if (suppressSearchReload) {
    suppressSearchReload = false
    return
  }
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => loadPage(0), 350)
})

async function toggleFavorite(movieId) {
  try {
    actionError.value = ''
    if (favoriteIds.value.has(movieId)) await removeFavorite(movieId)
    else await addFavorite(movieId)
    const next = new Set(favoriteIds.value)
    next.has(movieId) ? next.delete(movieId) : next.add(movieId)
    favoriteIds.value = next
    if (onlyFavorites.value) await loadPage(Math.min(currentPage.value, Math.max(0, totalPages.value - 1)))
  } catch (error) {
    actionError.value = error.message
  }
}

onMounted(async () => {
  try {
    await loadPage(0)
    try {
      const favoritesData = await getFavorites()
      favoriteIds.value = new Set(favoritesData.map((movie) => movie.id))
    } catch (error) {
      actionError.value = error.message
    }
  } catch (error) {
    errorMessage.value = error.message
  }
})

function buildRecommendationPrompt(question, candidates, correction = '') {
  const catalog = candidates
    .map((movie) => `- ${movie.title} (${movie.year}), ${movie.mood}: ${movie.description.slice(0, 240)}`)
    .join('\n')

  return `Selecciona entre UNA y TRES películas coherentes con la preferencia del usuario.
Solo puedes elegir títulos del catálogo proporcionado.
Devuelve únicamente los títulos exactos separados por el carácter |.
No uses Markdown, explicaciones, argumentos ni nombres de personajes.
${correction}

CATÁLOGO:
${catalog}

PREFERENCIA DEL USUARIO: ${question}`
}

function cleanAiResponse(value) {
  return String(value || '')
    .replace(/^\s*(aquí tienes|mi recomendación es|recomendación)\s*:?\s*/i, '')
    .replace(/^\s*[-*#>]+\s*/gm, '')
    .replace(/\*\*|__|`/g, '')
    .replace(/\n{2,}/g, ' ')
    .replace(/\s{2,}/g, ' ')
    .trim()
}

async function classifyGenre(question) {
  const allowedGenres = Object.keys(GENRE_LABELS)
  const prompt = `Clasifica la preferencia del usuario en un único género de esta lista:
${allowedGenres.join(' | ')}
Devuelve solamente el género exacto en inglés, sin Markdown ni explicación.
PREFERENCIA: ${question}`
  const result = await generateWithOllama(prompt)
  const answer = cleanAiResponse(result.response).toLocaleLowerCase('es')
  return allowedGenres.find((genre) => answer.includes(genre.toLocaleLowerCase('es'))) || ''
}

async function askOllama(question = assistantQuestion.value) {
  const cleanQuestion = question.trim()
  if (!cleanQuestion || isGenerating.value) return

  assistantQuestion.value = cleanQuestion
  assistantError.value = ''
  assistantResponse.value = ''
  isGenerating.value = true
  try {
    const wantsFavorites = /favorit/i.test(cleanQuestion)
    const normalizedQuestion = cleanQuestion.toLocaleLowerCase('es')
    const intent = GENRE_INTENTS.find((item) => item.words.some((word) => normalizedQuestion.includes(word)))
    const genre = wantsFavorites ? '' : (intent?.genre || await classifyGenre(cleanQuestion))

    if (!wantsFavorites && !genre) {
      assistantResponse.value = 'No pude identificar un género coherente para esa preferencia.'
      return
    }

    const loader = wantsFavorites ? getFavoritesPage : getMoviesPage
    const data = await loader(0, genre)
    const candidates = await Promise.all(data.contenido.map(mapMovie).map(localizeMovie))

    if (!candidates.length) {
      assistantResponse.value = 'No encontré películas coherentes con esa preferencia.'
      return
    }

    let selected = []
    for (let attempt = 0; attempt < 2 && !selected.length; attempt += 1) {
      const correction = attempt
        ? 'Tu respuesta anterior no fue válida. Revisa de nuevo y usa solamente títulos exactos del catálogo.'
        : ''
      const result = await generateWithOllama(buildRecommendationPrompt(cleanQuestion, candidates, correction))
      const answer = cleanAiResponse(result.response).toLocaleLowerCase('es')
      selected = candidates.filter((movie) => answer.includes(movie.title.toLocaleLowerCase('es'))).slice(0, 3)
    }

    if (!selected.length) selected = candidates.slice(0, 3)

    clearTimeout(searchTimer)
    if (searchQuery.value) {
      suppressSearchReload = true
      searchQuery.value = ''
    }
    onlyFavorites.value = wantsFavorites
    allMovies.value = selected
    currentPage.value = 0
    totalPages.value = 1
    totalMovies.value = selected.length
    assistantResponse.value = `Mostrando ${selected.length} ${selected.length === 1 ? 'película recomendada' : 'películas recomendadas'} para “${cleanQuestion}”.`
    await nextTick()
    document.querySelector('.movie-bento-grid')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
  } catch (error) {
    assistantError.value = error.message
  } finally {
    isGenerating.value = false
  }
}

async function resetRecommendation() {
  clearTimeout(searchTimer)
  assistantQuestion.value = ''
  assistantResponse.value = ''
  assistantError.value = ''
  actionError.value = ''
  onlyFavorites.value = false
  if (searchQuery.value) {
    suppressSearchReload = true
    searchQuery.value = ''
  }
  await loadPage(0)
}
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
            {{ movies.length }} de {{ totalMovies }} títulos
          </span>
          <button class="ios-chip rounded-pill px-4 py-2 text-sm" type="button" @click="toggleFavoritesView">
            <Heart :size="16" class="inline" /> {{ onlyFavorites ? 'Ver catálogo' : 'Mis favoritos' }}
          </button>
        </div>
      </div>

      <section class="ollama-recommender ios-surface mb-8 overflow-hidden rounded-[1.75rem] p-5 sm:p-7" aria-labelledby="ollama-title">
        <div class="ollama-recommender-grid">
          <div class="flex items-center gap-4">
            <img class="ale-avatar h-28 w-24 shrink-0 object-contain sm:h-32 sm:w-28" :src="aleAvatar" alt="Ale, recomendadora de Movs App" />
            <div>
              <p class="mb-1 inline-flex items-center gap-2 text-sm font-semibold uppercase tracking-[0.2em]" style="color: var(--color-accent-text);">
                <Sparkles :size="17" /> Te ayudaré a escoger la mejor película para ti
              </p>
              <h2 id="ollama-title" class="mb-1 text-2xl font-semibold" style="color: var(--color-text);">¿Qué vemos hoy?</h2>
              <p class="m-0 max-w-xl text-sm leading-6" style="color: var(--color-text-muted);">
                Cuéntale a Ale qué te apetece y te mostrará películas disponibles según tus preferencias y favoritos.
              </p>
            </div>
          </div>

          <form class="ollama-form" @submit.prevent="askOllama()">
            <label class="sr-only" for="ollama-question">Preferencias para la recomendación</label>
            <div class="flex gap-2">
              <input
                id="ollama-question"
                v-model="assistantQuestion"
                class="form-control min-w-0 flex-1 rounded-pill px-4 py-2.5 text-sm"
                maxlength="500"
                placeholder="Ej.: algo de misterio para esta noche..."
                type="text"
                :disabled="isGenerating || isLoading"
              />
              <button
                class="ollama-send soft-button inline-flex shrink-0 items-center gap-2 rounded-pill px-4 py-2.5 text-sm font-semibold"
                type="submit"
                :disabled="isGenerating || !assistantQuestion.trim() || !allMovies.length"
              >
                <LoaderCircle v-if="isGenerating" :size="17" class="animate-spin" />
                <Send v-else :size="17" />
                <span class="hidden sm:inline">{{ isGenerating ? 'Ale está pensando...' : 'Preguntar a Ale' }}</span>
              </button>
            </div>
            <div class="mt-3 flex flex-wrap gap-2">
              <button
                v-for="suggestion in suggestedQuestions"
                :key="suggestion"
                class="ios-chip rounded-pill px-3 py-1.5 text-xs"
                type="button"
                :disabled="isGenerating"
                @click="askOllama(suggestion)"
              >
                {{ suggestion }}
              </button>
            </div>
          </form>
        </div>

        <div v-if="assistantResponse" class="ollama-answer mt-5 rounded-[1.25rem] p-4 sm:p-5" aria-live="polite">
          <div class="flex items-start gap-3">
            <Sparkles :size="19" class="mt-0.5 shrink-0" style="color: var(--color-accent);" />
            <div class="min-w-0 flex-1">
              <p class="mb-1 text-xs font-semibold uppercase tracking-[0.16em]" style="color: var(--color-accent-text);">Recomendaciones de Ale</p>
              <p class="m-0 whitespace-pre-line text-sm leading-6" style="color: var(--color-text-secondary);">{{ assistantResponse }}</p>
              <button
                class="ios-chip mt-3 inline-flex items-center gap-2 rounded-pill px-3 py-1.5 text-xs font-semibold"
                type="button"
                aria-label="Reiniciar recomendación"
                title="Reiniciar recomendación"
                @click="resetRecommendation"
              >
                <RotateCcw :size="15" /> Reiniciar
              </button>
            </div>
          </div>
        </div>
        <p v-else-if="assistantError" class="auth-error mb-0 mt-4 rounded-xl px-4 py-3 text-sm" role="alert">{{ assistantError }}</p>
      </section>

      <p v-if="actionError" class="auth-error mb-5 rounded-xl px-4 py-3 text-sm" role="alert">{{ actionError }}</p>

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
          :image="movie.image"
          :favorite="favoriteIds.has(movie.id)"
          :variant="movie.variant"
          :delay="index * 70"
          @toggle-favorite="toggleFavorite"
        />
      </div>

      <nav v-if="!isLoading && !errorMessage && totalPages > 1" class="catalog-pagination mt-7 flex items-center justify-center gap-3" aria-label="Paginación del catálogo">
        <button
          class="ios-chip inline-flex items-center justify-center rounded-full p-2.5"
          type="button"
          aria-label="Ir a la primera página"
          title="Primera página"
          :disabled="currentPage === 0"
          @click="changePage(0)"
        >
          <ChevronsLeft :size="17" />
        </button>
        <button
          class="ios-chip inline-flex items-center justify-center rounded-full p-2.5"
          type="button"
          aria-label="Ir a la página anterior"
          title="Página anterior"
          :disabled="currentPage === 0"
          @click="changePage(currentPage - 1)"
        >
          <ArrowLeft :size="17" />
        </button>
        <span class="text-sm" style="color: var(--color-text-muted);">
          Página {{ currentPage + 1 }} de {{ totalPages }}
        </span>
        <button
          class="ios-chip inline-flex items-center justify-center rounded-full p-2.5"
          type="button"
          aria-label="Ir a la página siguiente"
          title="Página siguiente"
          :disabled="currentPage + 1 >= totalPages"
          @click="changePage(currentPage + 1)"
        >
          <ArrowRight :size="17" />
        </button>
        <button
          class="ios-chip inline-flex items-center justify-center rounded-full p-2.5"
          type="button"
          aria-label="Ir a la última página"
          title="Última página"
          :disabled="currentPage + 1 >= totalPages"
          @click="changePage(totalPages - 1)"
        >
          <ChevronsRight :size="17" />
        </button>
      </nav>
    </section>
  </main>
</template>

<style scoped src="./MovieAppView.css"></style>
