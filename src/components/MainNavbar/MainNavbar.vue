<script setup>
import { computed, ref } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { ChevronDown, Clapperboard, Home, Info, LogIn, LogOut, Moon, ShieldCheck, Sun, User, UserPlus, Users } from '@lucide/vue'

const route = useRoute()
const router = useRouter()
const isDark = ref(document.documentElement.classList.contains('dark'))
const showMenu = ref(false)

const hasSession = computed(() => {
  route.fullPath
  return Boolean(localStorage.getItem('movieSession'))
})

const sessionData = computed(() => {
  route.fullPath
  const s = localStorage.getItem('movieSession')
  return s ? JSON.parse(s) : null
})

const isAdmin = computed(() => sessionData.value?.rol === 'admin')

const firstName = computed(() => {
  const users = JSON.parse(localStorage.getItem('movieUsers') || '[]')
  const u = users.find((x) => x.correo === sessionData.value?.correo)
  return u?.nombre?.split(' ')[0] || 'Mi cuenta'
})

function closeMenu() {
  showMenu.value = false
}

function signOut() {
  closeMenu()
  localStorage.removeItem('movieSession')
  router.push('/signin')
}

function toggleTheme() {
  isDark.value = !isDark.value
  document.documentElement.classList.toggle('dark', isDark.value)
  localStorage.setItem('movieTheme', isDark.value ? 'dark' : 'light')
}

function handleBlur(e) {
  if (!e.currentTarget.contains(e.relatedTarget)) {
    showMenu.value = false
  }
}
</script>

<template>
  <nav class="fixed-top glass-nav mx-auto mt-3 w-fit max-w-[95vw] rounded-pill px-3 py-2">
    <div class="main-navbar-inner">
      <RouterLink class="navbar-brand fw-semibold tracking-tight me-3 text-decoration-none" to="/">
        Movs App
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
        <RouterLink v-if="hasSession" class="nav-link rounded-pill px-3 icon-link" to="/movies">
          <Clapperboard :size="17" />
          <span>Películas</span>
        </RouterLink>
        <RouterLink v-if="isAdmin" class="nav-link rounded-pill px-3 icon-link" to="/admin">
          <ShieldCheck :size="17" />
          <span>Admin</span>
        </RouterLink>
        <RouterLink v-if="!hasSession" class="nav-link rounded-pill px-3 icon-link" to="/signin">
          <LogIn :size="17" />
          <span>Entrar</span>
        </RouterLink>
        <RouterLink v-if="!hasSession" class="btn btn-outline-dark rounded-pill px-4 soft-button icon-link nav-action" to="/signup">
          <UserPlus :size="17" />
          <span>Registro</span>
        </RouterLink>
        <div
          v-if="hasSession"
          class="position-relative user-dropdown-wrap"
          tabindex="-1"
          @blur="handleBlur"
        >
          <button
            class="btn rounded-pill px-3 py-2 soft-button icon-link"
            type="button"
            @click="showMenu = !showMenu"
          >
            <User :size="17" />
            <span class="d-none d-md-inline">{{ firstName }}</span>
            <ChevronDown :size="14" class="d-none d-md-inline" />
          </button>
          <div
            v-if="showMenu"
            class="user-dropdown-menu position-absolute end-0 w-48 overflow-hidden rounded-xl border border-stone-200 bg-white shadow-lg dark:border-stone-700 dark:bg-stone-900"
            style="z-index: 30;"
          >
            <RouterLink
              class="d-flex align-items-center gap-2 px-4 py-2.5 text-sm text-stone-900 dark:text-stone-200"
              to="/app"
              @click="closeMenu"
            >
              <User :size="16" />
              Mi cuenta
            </RouterLink>
            <button
              class="d-flex w-full align-items-center gap-2 px-4 py-2.5 text-sm text-stone-900 dark:text-stone-200"
              type="button"
              @click="signOut"
            >
              <LogOut :size="16" />
              Salir
            </button>
          </div>
        </div>
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

<style scoped src="./MainNavbar.css"></style>
