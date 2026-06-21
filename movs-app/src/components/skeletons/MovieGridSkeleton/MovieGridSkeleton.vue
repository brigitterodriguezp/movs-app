<script setup>
defineProps({
  movies: {
    type: Array,
    required: true,
  },
})
</script>

<template>
  <div class="movie-bento-grid" aria-label="Cargando películas">
    <article
      v-for="skeleton in movies"
      :key="`skeleton-${skeleton.id}`"
      class="movie-card movie-card-skeleton rounded-[1.5rem]"
      :class="skeleton.variant"
    >
      <div class="movie-skeleton-poster"></div>
      <div class="movie-skeleton-body">
        <span class="movie-skeleton-line movie-skeleton-title"></span>
        <span class="movie-skeleton-line movie-skeleton-meta"></span>
        <span class="movie-skeleton-line"></span>
        <span class="movie-skeleton-line movie-skeleton-short"></span>
      </div>
    </article>
  </div>
</template>
