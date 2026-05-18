<script setup>
import { computed } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { Clapperboard, Home, Info, LogIn, LogOut, UserPlus } from '@lucide/vue'

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
      <RouterLink class="navbar-brand fw-semibold tracking-tight icon-link" to="/">
        <Clapperboard :size="18" />
        <span>Movs App</span>
      </RouterLink>

      <div class="main-navbar-links">
        <RouterLink class="nav-link rounded-pill px-3 icon-link" to="/">
          <Home :size="17" />
          <span>Home</span>
        </RouterLink>
        <RouterLink class="nav-link rounded-pill px-3 icon-link" to="/about">
          <Info :size="17" />
          <span>About</span>
        </RouterLink>
        <RouterLink class="nav-link rounded-pill px-3 icon-link" to="/app">
          <Clapperboard :size="17" />
          <span>App</span>
        </RouterLink>
        <RouterLink v-if="!hasSession" class="nav-link rounded-pill px-3 icon-link" to="/signin">
          <LogIn :size="17" />
          <span>Entrar</span>
        </RouterLink>
        <RouterLink v-if="!hasSession" class="btn btn-outline-dark rounded-pill px-4 soft-button icon-link" to="/signup">
          <UserPlus :size="17" />
          <span>Registrarse</span>
        </RouterLink>
        <button v-if="hasSession" class="btn btn-outline-dark rounded-pill px-4 soft-button icon-link" type="button" @click="signOut">
          <LogOut :size="17" />
          <span>Salir</span>
        </button>
      </div>
    </div>
  </nav>
</template>
