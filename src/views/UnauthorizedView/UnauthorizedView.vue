<script setup>
import { ShieldX } from '@lucide/vue'
</script>

<template>
  <main class="page-shell flex min-h-screen items-center justify-center px-4 pb-14 pt-36">
    <div class="ios-surface w-full max-w-lg rounded-[1.75rem] p-8 text-center">
      <ShieldX :size="56" class="mx-auto mb-4" style="color: var(--color-error);" />
      <h1 class="mb-2 text-3xl font-semibold" style="color: var(--color-text);">403</h1>
      <h2 class="mb-3 text-xl font-semibold" style="color: var(--color-text-secondary);">Acceso no autorizado</h2>
      <p class="mb-6" style="color: var(--color-text-muted);">No tienes permisos para acceder a esta sección.</p>
      <RouterLink class="btn rounded-pill px-5 py-2 soft-button glass-accent-btn" to="/">
        Volver al inicio
      </RouterLink>
    </div>
  </main>
</template>
