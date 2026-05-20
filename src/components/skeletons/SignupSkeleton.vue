<template>
  <section
    class="auth-panel page-skeleton signup-skeleton w-full max-w-5xl rounded-[1.75rem] p-4 sm:p-6"
    aria-label="Cargando registro"
  >
    <div class="ios-surface grid gap-8 rounded-[1.35rem] p-6 md:grid-cols-[0.8fr_1.2fr] sm:p-8">
      <div class="signup-skeleton-side">
        <span class="page-skeleton-line page-skeleton-kicker"></span>
        <span class="page-skeleton-line page-skeleton-heading compact"></span>
        <span class="page-skeleton-line page-skeleton-text"></span>
        <span class="page-skeleton-line page-skeleton-text short"></span>
      </div>

      <div class="signup-skeleton-form">
        <span v-for="item in 4" :key="`field-${item}`" class="page-skeleton-input"></span>
        <span class="page-skeleton-line page-skeleton-label wide"></span>
        <div class="signup-skeleton-plans">
          <span class="page-skeleton-card"></span>
          <span class="page-skeleton-card"></span>
        </div>
        <span class="page-skeleton-line page-skeleton-label wide"></span>
        <span class="page-skeleton-input wide"></span>
        <span class="page-skeleton-input wide"></span>
        <span class="page-skeleton-button wide"></span>
      </div>
    </div>
  </section>
</template>
