<template>
  <section
    class="about-skeleton-grid page-skeleton mx-auto grid max-w-6xl items-center gap-8 lg:grid-cols-[0.9fr_1.1fr]"
    aria-label="Cargando acerca de Movs App"
  >
    <div class="about-carousel overflow-hidden rounded-[2rem] ios-media">
      <div class="page-skeleton-media about-skeleton-media"></div>
    </div>

    <div class="ios-surface rounded-[1.75rem] p-6 sm:p-8 lg:p-10">
      <span class="page-skeleton-line page-skeleton-kicker"></span>
      <span class="page-skeleton-line page-skeleton-heading"></span>
      <span class="page-skeleton-line page-skeleton-text"></span>
      <span class="page-skeleton-line page-skeleton-text short"></span>
      <div class="about-skeleton-stats">
        <span class="page-skeleton-card"></span>
        <span class="page-skeleton-card"></span>
        <span class="page-skeleton-card"></span>
      </div>
    </div>
  </section>
</template>

<style scoped src="./AboutSkeleton.css"></style>
