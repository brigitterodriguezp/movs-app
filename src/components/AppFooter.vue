<script setup>
import { Clapperboard } from '@lucide/vue'
</script>

<template>
  <footer class="site-footer px-4 py-3">
    <div class="mx-auto max-w-7xl border-t border-stone-200 pt-3 text-center text-sm text-stone-500">
      <p class="m-0 inline-flex items-center justify-center gap-2">
        <Clapperboard :size="16" />
        <span>Movs App 2026</span>
      </p>
    </div>
  </footer>
</template>
