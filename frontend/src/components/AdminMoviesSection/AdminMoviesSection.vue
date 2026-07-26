<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import {
  ChevronLeft, ChevronRight, ChevronsLeft, ChevronsRight, Clapperboard, Edit3, Film, LoaderCircle, Plus, Search, Sparkles, Trash2, X
} from '@lucide/vue'
import { completeMovieFromTitle, createMovie, deleteMovie, getMoviesPage, updateMovie } from '@/services/api'

const genreOptions = [
  ['Action', 'Acción'], ['Adventure', 'Aventura'], ['Animation', 'Animación'],
  ['Comedy', 'Comedia'], ['Crime', 'Crimen'], ['Documentary', 'Documental'],
  ['Drama', 'Drama'], ['Family', 'Familiar'], ['Fantasy', 'Fantasía'],
  ['History', 'Historia'], ['Horror', 'Terror'], ['Music', 'Musical'],
  ['Mystery', 'Misterio'], ['Romance', 'Romance'], ['Science Fiction', 'Ciencia ficción'],
  ['Thriller', 'Suspenso'], ['War', 'Bélica'], ['Western', 'Oeste'],
]
const variantOptions = [
  ['movie-card-featured', 'Destacada'],
  ['movie-card-wide', 'Ancha'],
  ['movie-card-tall', 'Vertical'],
]

const movies = ref([])
const searchQuery = ref('')
const isLoading = ref(true)
const error = ref('')
const notice = ref('')
const currentPage = ref(1)
const pageSize = 5
const totalMovies = ref(0)
const totalPages = ref(1)
const showModal = ref(false)
const editingMovie = ref(null)
const deleteTarget = ref(null)
const saving = ref(false)
const completing = ref(false)
const assistantNotice = ref('')
const defaultPoster = `${import.meta.env.BASE_URL}default_movie.png`

const emptyForm = () => ({
  titulo: '',
  anio: new Date().getFullYear(),
  genero: '',
  descripcion: '',
  imagenUrl: '',
  variante: '',
})
const form = reactive(emptyForm())

const paginatedMovies = computed(() => movies.value)
const visiblePages = computed(() => {
  const size = 5
  const start = Math.max(1, Math.min(currentPage.value - 2, totalPages.value - size + 1))
  const end = Math.min(totalPages.value, start + size - 1)
  return Array.from({ length: end - start + 1 }, (_, index) => start + index)
})
const visibleFrom = computed(() => totalMovies.value ? (currentPage.value - 1) * pageSize + 1 : 0)
const visibleTo = computed(() => Math.min(currentPage.value * pageSize, totalMovies.value))

let searchTimer
watch(searchQuery, () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => loadMovies(1), 350)
})

function goToPage(page) {
  const target = Math.min(Math.max(Number(page), 1), totalPages.value)
  if (target !== currentPage.value) loadMovies(target)
}

function fillForm(movie = null) {
  Object.assign(form, movie ? {
    titulo: movie.titulo || '',
    anio: movie.anio || new Date().getFullYear(),
    genero: movie.genero || '',
    descripcion: movie.descripcion || '',
    imagenUrl: movie.imagenUrl || '',
    variante: movie.variante || '',
  } : emptyForm())
}

function openCreate() {
  editingMovie.value = null
  fillForm()
  error.value = ''
  assistantNotice.value = ''
  showModal.value = true
}

function openEdit(movie) {
  editingMovie.value = movie
  fillForm(movie)
  error.value = ''
  assistantNotice.value = ''
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  editingMovie.value = null
}

async function completeWithAle() {
  const title = form.titulo.trim()
  error.value = ''
  assistantNotice.value = ''
  if (!title) {
    error.value = 'Escribe primero el título de la película.'
    return
  }

  completing.value = true
  try {
    const result = await completeMovieFromTitle(title)
    form.titulo = result.titulo || title
    form.anio = result.anio || form.anio
    form.genero = result.genero || ''
    form.variante = result.variante || ''
    form.descripcion = result.descripcion || ''
    form.imagenUrl = result.posterUrl || ''
    assistantNotice.value = 'Ale completó la información de la película.'
  } catch (err) {
    error.value = err.message || 'Ale no pudo encontrar esa película.'
  } finally {
    completing.value = false
  }
}

function useDefaultPoster(event) {
  if (event.currentTarget.src !== defaultPoster) event.currentTarget.src = defaultPoster
}

async function loadMovies(page = currentPage.value) {
  isLoading.value = true
  error.value = ''
  try {
    const data = await getMoviesPage(page - 1, searchQuery.value.trim())
    if (!data.contenido.length && page > 1 && data.totalPaginas > 0) {
      await loadMovies(data.totalPaginas)
      return
    }
    movies.value = data.contenido
    totalMovies.value = data.totalElementos
    totalPages.value = Math.max(1, data.totalPaginas)
    currentPage.value = data.pagina + 1
  } catch (err) {
    error.value = err.message || 'No se pudieron cargar las películas.'
  } finally {
    isLoading.value = false
  }
}

async function saveMovie() {
  error.value = ''
  notice.value = ''
  const payload = {
    titulo: form.titulo.trim(),
    anio: Number(form.anio),
    genero: form.genero.trim(),
    descripcion: form.descripcion.trim(),
    imagenUrl: form.imagenUrl.trim(),
    variante: form.variante.trim() || null,
  }
  if (!payload.titulo || !payload.genero || !payload.descripcion || !payload.imagenUrl || payload.anio < 1888 || payload.anio > 2100) {
    error.value = 'Completa los campos obligatorios y usa un año entre 1888 y 2100.'
    return
  }

  saving.value = true
  try {
    if (editingMovie.value) {
      const updated = await updateMovie(editingMovie.value.id, payload)
      const index = movies.value.findIndex((movie) => movie.id === updated.id)
      if (index >= 0) movies.value.splice(index, 1, updated)
      notice.value = 'Película actualizada correctamente.'
    } else {
      const created = await createMovie(payload)
      movies.value.unshift(created)
      await loadMovies(1)
      notice.value = 'Película creada correctamente.'
    }
    closeModal()
  } catch (err) {
    error.value = err.message || 'No se pudo guardar la película.'
  } finally {
    saving.value = false
  }
}

async function confirmDelete() {
  if (!deleteTarget.value) return
  error.value = ''
  notice.value = ''
  try {
    await deleteMovie(deleteTarget.value.id)
    movies.value = movies.value.filter((movie) => movie.id !== deleteTarget.value.id)
    await loadMovies(Math.min(currentPage.value, totalPages.value))
    deleteTarget.value = null
    notice.value = 'Película eliminada correctamente.'
  } catch (err) {
    error.value = err.message || 'No se pudo eliminar la película.'
  }
}

onMounted(loadMovies)
</script>

<template>
  <section class="mx-auto max-w-7xl">
    <div class="mb-6 flex flex-wrap items-end justify-between gap-4">
      <div>
        <p class="mb-2 inline-flex items-center gap-2 text-sm font-medium" style="color: var(--color-accent-text);">
          <Clapperboard :size="17" />
          <span>Administración</span>
        </p>
        <h1 class="text-4xl font-semibold tracking-normal sm:text-5xl" style="color: var(--color-text);">Panel de administración</h1>
        <p class="mt-2" style="color: var(--color-text-muted);">{{ totalMovies }} películas registradas.</p>
      </div>
      <div class="flex flex-wrap items-center gap-3">
        <button class="btn rounded-pill px-4 py-2 soft-button icon-link glass-accent-btn" type="button" @click="openCreate">
          <Plus :size="16" />
          Añadir película
        </button>
        <div class="relative w-full sm:w-64">
          <Search :size="15" class="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2" style="color: var(--color-text-muted);" />
          <input v-model="searchQuery" class="form-control rounded-pill py-2 pl-10 pr-4 text-sm" placeholder="Buscar título, género o año..." type="search" />
        </div>
      </div>
    </div>

    <p v-if="notice" class="mb-4 rounded-xl px-4 py-3 text-sm" style="background: var(--color-accent-bg); color: var(--color-accent-text);">{{ notice }}</p>
    <p v-if="error && !showModal" class="mb-4 rounded-xl px-4 py-3 text-sm" style="background: color-mix(in srgb, var(--color-error) 12%, transparent); color: var(--color-error);">{{ error }}</p>

    <div v-if="isLoading" class="admin-movie-surface rounded-[1.35rem] p-6">
      <div class="page-skeleton-line page-skeleton-heading compact mb-4" />
      <div class="page-skeleton-line page-skeleton-text mb-2" />
      <div class="page-skeleton-line page-skeleton-text short" />
    </div>

    <div v-else-if="!paginatedMovies.length" class="admin-movie-surface rounded-[1.35rem] p-8 text-center">
      <Film :size="40" class="mx-auto mb-4" style="color: var(--color-text-muted);" />
      <h2 class="mb-2 text-xl font-semibold" style="color: var(--color-text);">No se encontraron películas</h2>
      <p style="color: var(--color-text-muted);">Cambia la búsqueda o añade una película.</p>
    </div>

    <div v-else class="admin-movie-surface overflow-hidden rounded-[1.35rem]">
      <div class="overflow-x-auto">
        <table class="w-full text-left text-sm">
          <thead>
            <tr class="border-b" style="border-color: var(--color-border);">
              <th class="px-4 py-3 font-medium sm:px-6" style="color: var(--color-text-muted);">No.</th>
              <th class="px-4 py-3 font-medium sm:px-6" style="color: var(--color-text-muted);">Título</th>
              <th class="px-4 py-3 font-medium sm:px-6" style="color: var(--color-text-muted);">Año</th>
              <th class="px-4 py-3 font-medium sm:px-6" style="color: var(--color-text-muted);">Género</th>
              <th class="px-4 py-3 font-medium sm:px-6" style="color: var(--color-text-muted);">Imagen</th>
              <th class="px-4 py-3 font-medium sm:px-6" style="color: var(--color-text-muted);">Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(movie, index) in paginatedMovies" :key="movie.id" class="border-b last:border-0" style="border-color: var(--color-border-subtle);">
              <td class="px-4 py-3 sm:px-6" style="color: var(--color-text-secondary);">{{ (currentPage - 1) * pageSize + index + 1 }}</td>
              <td class="max-w-64 px-4 py-3 font-medium sm:px-6" style="color: var(--color-text);">{{ movie.titulo }}</td>
              <td class="px-4 py-3 sm:px-6" style="color: var(--color-text-secondary);">{{ movie.anio }}</td>
              <td class="px-4 py-3 sm:px-6" style="color: var(--color-text-secondary);">{{ movie.genero }}</td>
              <td class="max-w-56 truncate px-4 py-3 sm:px-6" :title="movie.imagenUrl" style="color: var(--color-text-muted);">{{ movie.imagenUrl }}</td>
              <td class="px-4 py-3 sm:px-6">
                <div v-if="deleteTarget?.id === movie.id" class="flex items-center gap-2 whitespace-nowrap">
                  <span class="text-xs" style="color: var(--color-text-muted);">¿Eliminar?</span>
                  <button class="btn rounded-pill px-2 py-1" type="button" style="color: var(--color-error);" @click="confirmDelete"><Trash2 :size="14" /></button>
                  <button class="btn rounded-pill px-2 py-1" type="button" style="color: var(--color-text);" @click="deleteTarget = null"><X :size="14" /></button>
                </div>
                <div v-else class="flex items-center gap-1">
                  <button class="btn rounded-pill px-2 py-1" type="button" title="Editar" style="color: var(--color-accent);" @click="openEdit(movie)"><Edit3 :size="14" /></button>
                  <button class="btn rounded-pill px-2 py-1" type="button" title="Eliminar" style="color: var(--color-error);" @click="deleteTarget = movie"><Trash2 :size="14" /></button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="flex flex-wrap items-center justify-between gap-3 border-t px-4 py-3 text-xs sm:px-6" style="border-color: var(--color-border); color: var(--color-text-muted);">
        <span>Mostrando {{ visibleFrom }}–{{ visibleTo }} de {{ totalMovies }}</span>
        <div class="flex flex-wrap items-center justify-center gap-2">
          <button class="movie-page-button btn rounded-pill px-2 py-1" type="button" aria-label="Ir a la primera página" title="Primera página" :disabled="currentPage === 1" @click="goToPage(1)"><ChevronsLeft :size="14" /></button>
          <button class="movie-page-button btn rounded-pill px-2 py-1" type="button" :disabled="currentPage === 1" @click="goToPage(currentPage - 1)"><ChevronLeft :size="14" /></button>
          <button v-for="page in visiblePages" :key="page" class="movie-page-button btn rounded-pill px-2.5 py-1" :class="{ active: page === currentPage }" type="button" @click="goToPage(page)">{{ page }}</button>
          <button class="movie-page-button btn rounded-pill px-2 py-1" type="button" :disabled="currentPage === totalPages" @click="goToPage(currentPage + 1)"><ChevronRight :size="14" /></button>
          <button class="movie-page-button btn rounded-pill px-2 py-1" type="button" aria-label="Ir a la última página" title="Última página" :disabled="currentPage === totalPages" @click="goToPage(totalPages)"><ChevronsRight :size="14" /></button>
          <span>de {{ totalPages }}</span>
        </div>
      </div>
    </div>

    <Teleport to="body">
      <div v-if="showModal" class="movie-modal-overlay" @click.self="closeModal">
        <div class="movie-modal-panel">
          <div class="flex items-center justify-between border-b px-5 py-4" style="border-color: var(--color-border);">
            <h2 class="text-xl font-semibold" style="color: var(--color-text);">{{ editingMovie ? 'Editar película' : 'Añadir película' }}</h2>
            <button class="btn rounded-pill p-2" type="button" style="color: var(--color-text);" @click="closeModal"><X :size="18" /></button>
          </div>
          <form class="grid gap-4 p-5" @submit.prevent="saveMovie">
            <div class="grid gap-4 sm:grid-cols-2">
              <label class="text-sm" style="color: var(--color-text);">Título<input v-model="form.titulo" class="form-control mt-1 rounded-xl" maxlength="160" required /></label>
              <label class="text-sm" style="color: var(--color-text);">Año<input v-model.number="form.anio" class="form-control mt-1 rounded-xl" type="number" min="1888" max="2100" required /></label>
              <label class="text-sm" style="color: var(--color-text);">Género<select v-model="form.genero" class="form-control mt-1 rounded-xl" required><option value="" disabled>Selecciona un género</option><option v-for="([value, label]) in genreOptions" :key="value" :value="value">{{ label }}</option></select></label>
              <label class="text-sm" style="color: var(--color-text);">Presentación<select v-model="form.variante" class="form-control mt-1 rounded-xl"><option value="">Estándar</option><option v-for="([value, label]) in variantOptions" :key="value" :value="value">{{ label }}</option></select></label>
            </div>
            <div v-if="!editingMovie" class="ale-helper flex flex-wrap items-center justify-between gap-3 rounded-xl p-3">
              <div class="flex items-center gap-3">
                <span class="ale-helper-icon"><Sparkles :size="17" /></span>
                <div>
                  <p class="m-0 text-sm font-medium">Completar con Ale</p>
                  <p class="m-0 text-xs">Busca por el título, año, género, presentación, sinopsis y póster.</p>
                </div>
              </div>
              <button class="ale-complete-button btn rounded-pill px-4 py-2 text-sm" type="button" :disabled="completing || !form.titulo.trim()" @click="completeWithAle">
                <LoaderCircle v-if="completing" :size="16" class="animate-spin" />
                <Sparkles v-else :size="16" />
                {{ completing ? 'Buscando…' : 'Completar' }}
              </button>
            </div>
            <p v-if="assistantNotice" class="m-0 rounded-xl px-4 py-3 text-sm assistant-success">{{ assistantNotice }}</p>
            <div class="grid gap-4 sm:grid-cols-[7rem_1fr]">
              <img v-if="form.imagenUrl" :src="form.imagenUrl" :alt="`Póster de ${form.titulo || 'la película'}`" class="movie-poster-preview" @error="useDefaultPoster" />
              <div class="grid gap-4" :class="{ 'sm:col-span-2': !form.imagenUrl }">
                <label class="text-sm" style="color: var(--color-text);">URL de la imagen<input v-model="form.imagenUrl" class="form-control mt-1 rounded-xl" maxlength="255" required /></label>
                <label class="text-sm" style="color: var(--color-text);">Descripción<textarea v-model="form.descripcion" class="form-control mt-1 rounded-xl" maxlength="1000" rows="5" required /></label>
              </div>
            </div>
            <p v-if="error" class="m-0 rounded-xl px-4 py-3 text-sm" style="background: color-mix(in srgb, var(--color-error) 12%, transparent); color: var(--color-error);">{{ error }}</p>
            <div class="flex justify-end gap-3">
              <button class="btn rounded-pill px-4 py-2 soft-button" type="button" @click="closeModal">Cancelar</button>
              <button class="btn rounded-pill px-4 py-2 glass-accent-btn" type="submit" :disabled="saving">{{ saving ? 'Guardando…' : 'Guardar película' }}</button>
            </div>
          </form>
        </div>
      </div>
    </Teleport>
  </section>
</template>

<style scoped>
.admin-movie-surface { background: #fff; border: 1px solid #e5e7eb; }
.movie-page-button { color: var(--color-text) !important; border-color: var(--color-border) !important; }
.movie-page-button.active { color: #fff !important; background: var(--color-accent) !important; border-color: var(--color-accent) !important; }
.movie-page-button:disabled { color: var(--color-text-muted) !important; opacity: .55; }
.movie-modal-overlay { position: fixed; inset: 0; z-index: 60; display: flex; align-items: center; justify-content: center; padding: 1rem; background: rgba(17,24,39,.32); }
.movie-modal-panel { --color-text: #111827; --color-text-secondary: #374151; --color-text-muted: #6b7280; --color-border: #e5e7eb; --color-border-subtle: #f3f4f6; width: min(100%, 44rem); max-height: 92vh; overflow-y: auto; border: 1px solid #e5e7eb; border-radius: 1rem; background: #fff; box-shadow: 0 20px 55px rgba(15,23,42,.16); }
.ale-helper { border: 1px solid #ede9fe; background: #faf8ff; color: #4c1d95; }
.ale-helper-icon { display: grid; width: 2rem; height: 2rem; place-items: center; border-radius: 999px; background: #ede9fe; color: #7c3aed; }
.ale-helper p:last-child { color: #6b7280; }
.ale-complete-button { display: inline-flex; align-items: center; gap: .4rem; border: 0; background: #7c3aed; color: #fff; }
.ale-complete-button:hover:not(:disabled) { background: #6d28d9; color: #fff; }
.ale-complete-button:disabled { opacity: .5; }
.assistant-success { background: #ecfdf5; color: #047857; }
.movie-poster-preview { width: 7rem; aspect-ratio: 2 / 3; border-radius: .75rem; object-fit: cover; background: #f3f4f6; }
</style>
