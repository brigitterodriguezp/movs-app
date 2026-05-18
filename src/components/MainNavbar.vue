<script setup>
import { computed } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const hasSession = computed(() => {
  route.fullPath
  return Boolean(localStorage.getItem('movieSession'))
})

function signOut() {
  localStorage.removeItem('movieSession')
  router.push('/signin')
}
</script>

<template>
  <nav class="fixed-top glass-nav mx-auto mt-3 w-[92%] rounded-pill px-3 py-2">
    <div class="main-navbar-inner">
      <RouterLink class="navbar-brand fw-semibold tracking-tight" to="/">Movs App</RouterLink>

      <div class="main-navbar-links">
        <RouterLink class="nav-link rounded-pill px-3" to="/">Home</RouterLink>
        <RouterLink class="nav-link rounded-pill px-3" to="/about">About</RouterLink>
        <RouterLink class="nav-link rounded-pill px-3" to="/app">App</RouterLink>
        <RouterLink v-if="!hasSession" class="nav-link rounded-pill px-3" to="/signin">Entrar</RouterLink>
        <RouterLink v-if="!hasSession" class="btn btn-outline-dark rounded-pill px-4 soft-button" to="/signup">
          Registrarse
        </RouterLink>
        <button v-if="hasSession" class="btn btn-outline-dark rounded-pill px-4 soft-button" type="button" @click="signOut">
          Salir
        </button>
      </div>
    </div>
  </nav>
</template>
