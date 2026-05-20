<script setup>
import { computed, ref } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { Clapperboard, Home, Info, LogIn, LogOut, Moon, Sun, UserPlus } from '@lucide/vue'

const route = useRoute()
const router = useRouter()
const isDark = ref(document.documentElement.classList.contains('dark'))

const hasSession = computed(() => {
  route.fullPath
  return Boolean(localStorage.getItem('movieSession'))
})

function signOut() {
  localStorage.removeItem('movieSession')
  router.push('/signin')
}

function toggleTheme() {
  isDark.value = !isDark.value
  document.documentElement.classList.toggle('dark', isDark.value)
  localStorage.setItem('movieTheme', isDark.value ? 'dark' : 'light')
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
          <span>Inicio</span>
        </RouterLink>
        <RouterLink class="nav-link rounded-pill px-3 icon-link about-nav-link" to="/about">
          <Info :size="17" />
          <span>Acerca</span>
        </RouterLink>
        <RouterLink class="nav-link rounded-pill px-3 icon-link" to="/app">
          <Clapperboard :size="17" />
          <span>App</span>
        </RouterLink>
        <RouterLink v-if="!hasSession" class="nav-link rounded-pill px-3 icon-link" to="/signin">
          <LogIn :size="17" />
          <span>Entrar</span>
        </RouterLink>
        <RouterLink v-if="!hasSession" class="btn btn-outline-dark rounded-pill px-4 soft-button icon-link nav-action" to="/signup">
          <UserPlus :size="17" />
          <span>Registro</span>
        </RouterLink>
        <button v-if="hasSession" class="btn btn-outline-dark rounded-pill px-4 soft-button icon-link nav-action" type="button" @click="signOut">
          <LogOut :size="17" />
          <span>Salir</span>
        </button>
        <button
          class="btn btn-outline-dark rounded-pill px-3 soft-button icon-link theme-toggle"
          type="button"
          :aria-label="isDark ? 'Activar modo claro' : 'Activar modo oscuro'"
          :title="isDark ? 'Modo claro' : 'Modo oscuro'"
          @click="toggleTheme"
        >
          <Sun v-if="isDark" :size="17" />
          <Moon v-else :size="17" />
        </button>
      </div>
    </div>
  </nav>
</template>
