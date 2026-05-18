<script setup>
import movieImage from '@/assets/movies/image.png'
</script>

<template>
  <main class="page-shell min-h-screen px-4 pb-16 pt-32 sm:px-6 lg:px-10">
    <section class="mx-auto grid max-w-6xl items-center gap-8 lg:grid-cols-[0.9fr_1.1fr]">
      <div class="overflow-hidden rounded-[2rem] ios-media">
        <img class="h-[560px] w-full object-cover" :src="movieImage" alt="Poster de película" />
      </div>

      <div class="ios-surface rounded-[1.75rem] p-6 sm:p-8 lg:p-10">
        <p class="mb-3 text-sm uppercase tracking-[0.28em] text-emerald-700">Movs App</p>
        <h1 class="text-4xl font-semibold tracking-normal text-stone-950 sm:text-5xl">
          Cine, guardado a tu ritmo
        </h1>
        <p class="mt-5 text-base leading-7 text-stone-600">
          Organiza títulos, vuelve a tus historias favoritas y mantén tu selección lista para la próxima noche.
        </p>
        <div class="mt-8 grid gap-3 sm:grid-cols-3">
          <div class="rounded-4 border border-stone-200 bg-white/70 p-4">
            <p class="mb-1 text-2xl font-semibold text-stone-950">6</p>
            <p class="m-0 text-sm text-stone-500">Películas</p>
          </div>
          <div class="rounded-4 border border-stone-200 bg-white/70 p-4">
            <p class="mb-1 text-2xl font-semibold text-stone-950">4</p>
            <p class="m-0 text-sm text-stone-500">Géneros</p>
          </div>
          <div class="rounded-4 border border-stone-200 bg-white/70 p-4">
            <p class="mb-1 text-2xl font-semibold text-stone-950">1</p>
            <p class="m-0 text-sm text-stone-500">Lista</p>
          </div>
        </div>
      </div>
    </section>
  </main>
</template>
