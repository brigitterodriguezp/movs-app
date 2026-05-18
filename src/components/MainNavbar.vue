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
  <nav class="navbar navbar-expand-lg fixed-top glass-nav mx-auto mt-3 w-[92%] rounded-pill px-3">
    <div class="container-fluid">
      <RouterLink class="navbar-brand fw-semibold tracking-tight" to="/">Movs App</RouterLink>
      <button
        class="navbar-toggler border-0"
        type="button"
        data-bs-toggle="collapse"
        data-bs-target="#mainNav"
        aria-controls="mainNav"
        aria-expanded="false"
        aria-label="Abrir navegación"
      >
        <span class="navbar-toggler-icon"></span>
      </button>
      <div id="mainNav" class="collapse navbar-collapse">
        <div class="navbar-nav ms-auto align-items-lg-center gap-lg-2">
          <RouterLink class="nav-link rounded-pill px-3" to="/">Home</RouterLink>
          <RouterLink class="nav-link rounded-pill px-3" to="/about">About</RouterLink>
          <RouterLink class="nav-link rounded-pill px-3" to="/app">App</RouterLink>
          <RouterLink v-if="!hasSession" class="nav-link rounded-pill px-3" to="/signin">Signin</RouterLink>
          <RouterLink v-if="!hasSession" class="btn btn-dark rounded-pill px-4 soft-button" to="/signup">
            Signup
          </RouterLink>
          <button v-if="hasSession" class="btn btn-outline-dark rounded-pill px-4 soft-button" type="button" @click="signOut">
            Salir
          </button>
        </div>
      </div>
    </div>
  </nav>
</template>
