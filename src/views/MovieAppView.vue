<script setup>
import movieImage from '@/assets/movies/image.png'
import MovieCard from '@/components/MovieCard.vue'

const movies = [
  {
    title: 'Invierno en Jeju',
    year: '2026',
    mood: 'Drama suave',
    description: 'Una historia íntima sobre memoria, familia y segundas oportunidades.',
  },
  {
    title: 'Noche Verde',
    year: '2025',
    mood: 'Suspenso',
    description: 'Dos desconocidos se encuentran en una ciudad silenciosa antes del amanecer.',
  },
  {
    title: 'Carta de Jeju',
    year: '2024',
    mood: 'Romance',
    description: 'Un amor pequeño crece entre mareas, canciones antiguas y promesas pendientes.',
  },
  {
    title: 'La Última Función',
    year: '2023',
    mood: 'Melancolía',
    description: 'Una sala de cine a punto de cerrar reúne a quienes nunca olvidaron una película.',
  },
  {
    title: 'Casa de Lluvia',
    year: '2022',
    mood: 'Misterio',
    description: 'Una familia vuelve a una casa vacía donde cada habitación recuerda algo distinto.',
  },
  {
    title: 'Después del Té',
    year: '2021',
    mood: 'Slice of life',
    description: 'Una pausa delicada sobre crecer, perder miedo y aprender a quedarse.',
  },
]
</script>

<template>
  <main class="page-shell min-h-screen px-4 pb-16 pt-32 sm:px-6 lg:px-10">
    <section class="mx-auto max-w-7xl">
      <div class="mb-8 flex flex-col justify-between gap-4 md:flex-row md:items-end">
        <div>
          <p class="mb-2 text-sm uppercase tracking-[0.28em] text-emerald-700">Mi selección</p>
          <h1 class="text-4xl font-semibold tracking-normal text-stone-950 sm:text-5xl">Grid de películas</h1>
        </div>
        <span class="ios-chip w-fit rounded-pill px-4 py-2 text-sm text-stone-600">
          {{ movies.length }} títulos guardados
        </span>
      </div>

      <div class="grid gap-5 sm:grid-cols-2 xl:grid-cols-3">
        <MovieCard
          v-for="(movie, index) in movies"
          :key="movie.title"
          :movie="movie"
          :image="movieImage"
          :delay="index * 70"
        />
      </div>
    </section>
  </main>
</template>
