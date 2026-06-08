<script setup>
import { ShieldX } from '@lucide/vue'
</script>

<template>
  <main class="page-shell flex min-h-screen items-center justify-center px-4 pb-14 pt-36">
    <div class="ios-surface w-full max-w-lg rounded-[1.75rem] p-8 text-center">
      <ShieldX :size="56" class="mx-auto mb-4 text-red-500" />
      <h1 class="mb-2 text-3xl font-semibold text-stone-950">403</h1>
      <h2 class="mb-3 text-xl font-semibold text-stone-700">Acceso no autorizado</h2>
      <p class="mb-6 text-stone-500">No tienes permisos para acceder a esta sección.</p>
      <RouterLink class="btn btn-dark rounded-pill px-5 py-2 soft-button" to="/">
        Volver al inicio
      </RouterLink>
    </div>
  </main>
</template>
