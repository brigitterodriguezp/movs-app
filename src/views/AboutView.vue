<script setup>
import coverStory from '@/assets/movies/001-cover.png'
import ninera from '@/assets/movies/002-ninera.png'
import scaryMovie from '@/assets/movies/003-scary-movie.png'
import littleWomen from '@/assets/movies/004-little-women.png'
import joker from '@/assets/movies/005-joker.png'
import frightening from '@/assets/movies/006-the-frightening.png'
import marilyn from '@/assets/movies/007-marilyn-monroe.png'
import loveUntangled from '@/assets/movies/009-love-untangled.png'
import { Clapperboard, Film, Layers } from '@lucide/vue'

const carouselMovies = [
  {
    title: 'Cover Story',
    mood: 'Drama',
    phrase: 'Cada portada guarda un secreto.',
    image: coverStory,
  },
  {
    title: 'La Niñera',
    mood: 'Suspenso',
    phrase: 'La calma de la casa nunca fue inocente.',
    image: ninera,
  },
  {
    title: 'Scary Movie',
    mood: 'Terror',
    phrase: '¿Cuál es tu película de miedo favorita?',
    image: scaryMovie,
  },
  {
    title: 'Little Women',
    mood: 'Drama',
    phrase: 'No le temo a las tormentas.',
    image: littleWomen,
  },
  {
    title: 'Joker',
    mood: 'Crimen',
    phrase: 'Why so serious?',
    image: joker,
  },
  {
    title: 'The Frightening',
    mood: 'Misterio',
    phrase: 'No mires atrás cuando algo susurra.',
    image: frightening,
  },
  {
    title: 'Marilyn Monroe',
    mood: 'Biografía',
    phrase: 'Diamonds are a girl’s best friend.',
    image: marilyn,
  },
  {
    title: 'Love Untangled',
    mood: 'Romance',
    phrase: 'El primer amor siempre enreda el corazón.',
    image: loveUntangled,
  },
]
</script>

<template>
  <main class="page-shell px-4 pb-4 pt-32 sm:px-6 lg:px-10">
    <section class="mx-auto grid max-w-6xl items-center gap-8 lg:grid-cols-[0.9fr_1.1fr]">
      <div
        id="aboutMoviesCarousel"
        class="carousel slide overflow-hidden rounded-[2rem] ios-media about-carousel"
        data-bs-ride="carousel"
        data-bs-interval="2800"
        data-bs-wrap="true"
        data-bs-pause="false"
        data-bs-touch="true"
      >
        <div class="carousel-indicators">
          <button
            v-for="(movie, index) in carouselMovies"
            :key="movie.title"
            type="button"
            data-bs-target="#aboutMoviesCarousel"
            :data-bs-slide-to="index"
            :class="{ active: index === 0 }"
            :aria-current="index === 0 ? 'true' : undefined"
            :aria-label="`Ver ${movie.title}`"
          ></button>
        </div>

        <div class="carousel-inner">
          <div
            v-for="(movie, index) in carouselMovies"
            :key="movie.title"
            class="carousel-item"
            :class="{ active: index === 0 }"
            data-bs-interval="3200"
          >
            <img class="about-carousel-image" :src="movie.image" :alt="`Poster de ${movie.title}`" />
            <div class="about-carousel-caption">
              <span>{{ movie.mood }}</span>
              <div>
                <strong>{{ movie.title }}</strong>
                <p>{{ movie.phrase }}</p>
              </div>
            </div>
          </div>
        </div>

        <button class="carousel-control-prev" type="button" data-bs-target="#aboutMoviesCarousel" data-bs-slide="prev" aria-label="Anterior">
          <span class="carousel-control-prev-icon" aria-hidden="true"></span>
        </button>
        <button class="carousel-control-next" type="button" data-bs-target="#aboutMoviesCarousel" data-bs-slide="next" aria-label="Siguiente">
          <span class="carousel-control-next-icon" aria-hidden="true"></span>
        </button>
      </div>

      <div class="ios-surface rounded-[1.75rem] p-6 sm:p-8 lg:p-10">
        <p class="mb-3 inline-flex items-center gap-2 text-sm uppercase tracking-[0.28em] text-emerald-700">
          <Clapperboard :size="17" />
          <span>Movs App</span>
        </p>
        <h1 class="text-4xl font-semibold tracking-normal text-stone-950 sm:text-5xl">
          Cine, guardado a tu ritmo
        </h1>
        <p class="mt-5 text-base leading-7 text-stone-600">
          Organiza títulos, vuelve a tus historias favoritas y mantén tu selección lista para la próxima noche.
        </p>
        <div class="mt-8 grid gap-3 sm:grid-cols-3">
          <div class="rounded-4 border border-stone-200 bg-white/70 p-4">
            <Film class="mb-3 text-emerald-700" :size="21" />
            <p class="mb-1 text-2xl font-semibold text-stone-950">8</p>
            <p class="m-0 text-sm text-stone-500">Películas</p>
          </div>
          <div class="rounded-4 border border-stone-200 bg-white/70 p-4">
            <Layers class="mb-3 text-emerald-700" :size="21" />
            <p class="mb-1 text-2xl font-semibold text-stone-950">6</p>
            <p class="m-0 text-sm text-stone-500">Géneros</p>
          </div>
          <div class="rounded-4 border border-stone-200 bg-white/70 p-4">
            <Clapperboard class="mb-3 text-emerald-700" :size="21" />
            <p class="mb-1 text-2xl font-semibold text-stone-950">1</p>
            <p class="m-0 text-sm text-stone-500">Lista</p>
          </div>
        </div>
      </div>
    </section>
  </main>
</template>
