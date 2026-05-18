<script setup>
import cover001 from '@/assets/movies/001-cover.png'
import ninera from '@/assets/movies/002-ninera.png'
import scaryMovie from '@/assets/movies/003-scary-movie.png'
import littleWomen from '@/assets/movies/004-little-women.png'
import joker from '@/assets/movies/005-joker.png'
import frightening from '@/assets/movies/006-the-frightening.png'
import marilyn from '@/assets/movies/007-marilyn-monroe.png'
import loveUntangled from '@/assets/movies/009-love-untangled.png'
import MovieCard from '@/components/MovieCard.vue'
import { Library } from '@lucide/vue'

const movies = [
  {
    title: 'Cover Story',
    year: '2026',
    mood: 'Drama',
    description: 'Una historia íntima para volver cuando la noche pide calma.',
    image: cover001,
    variant: 'movie-card-featured',
  },
  {
    title: 'La Niñera',
    year: '2025',
    mood: 'Suspenso',
    description: 'Una casa tranquila empieza a guardar demasiados secretos.',
    image: ninera,
    variant: 'movie-card-tall',
  },
  {
    title: 'Scary Movie',
    year: '2024',
    mood: 'Terror',
    description: 'Risas oscuras, sustos rápidos y una noche imposible de pausar.',
    image: scaryMovie,
  },
  {
    title: 'Little Women',
    year: '2023',
    mood: 'Drama',
    description: 'Decisiones grandes en habitaciones pequeñas.',
    image: littleWomen,
  },
  {
    title: 'Joker',
    year: '2022',
    mood: 'Crimen',
    description: 'Una mirada intensa a una ciudad que ya no sabe escuchar.',
    image: joker,
    variant: 'movie-card-wide',
  },
  {
    title: 'The Frightening',
    year: '2021',
    mood: 'Misterio',
    description: 'Algo se mueve entre pasillos donde nadie debería estar.',
    image: frightening,
  },
  {
    title: 'Marilyn Monroe',
    year: '2020',
    mood: 'Biografía',
    description: 'Luz, cámara y una silueta que nunca dejó de aparecer.',
    image: marilyn,
  },
  {
    title: 'Love Untangled',
    year: '2025',
    mood: 'Romance',
    description: 'Primer amor, nervios y una confesión esperando su momento.',
    image: loveUntangled,
    variant: 'movie-card-wide',
  },
]
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

      <div class="movie-bento-grid">
        <MovieCard
          v-for="(movie, index) in movies"
          :key="movie.title"
          :movie="movie"
          :image="movie.image"
          :variant="movie.variant"
          :delay="index * 70"
        />
      </div>
    </section>
  </main>
</template>
