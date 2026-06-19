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

function toggleTheme(e) {
  const x = e.clientX || e.pageX || 0
  const y = e.clientY || e.pageY || 0
  document.documentElement.style.setProperty('--theme-x', `${x}px`)
  document.documentElement.style.setProperty('--theme-y', `${y}px`)

  if (document.startViewTransition) {
    const next = !isDark.value
    document.documentElement.classList.add('theme-transition')
    const transition = document.startViewTransition(() => {
      isDark.value = next
      document.documentElement.classList.toggle('dark', next)
      localStorage.setItem('movieTheme', next ? 'dark' : 'light')
    })
    transition.finished.finally(() => {
      document.documentElement.classList.remove('theme-transition')
    })
  } else {
    isDark.value = !isDark.value
    document.documentElement.classList.toggle('dark', isDark.value)
    localStorage.setItem('movieTheme', isDark.value ? 'dark' : 'light')
  }
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
      <RouterLink class="navbar-brand fw-semibold tracking-tight me-3 text-decoration-none d-inline-flex align-items-center gap-1 d-none d-md-flex" to="/" style="color: var(--color-accent);">
        <Clapperboard :size="18" />
        Movs App
      </RouterLink>

      <div class="main-navbar-links">
        <RouterLink class="nav-link rounded-pill px-3 icon-link" to="/">
          <Home :size="17" />
          <span class="d-none d-md-inline">Inicio</span>
        </RouterLink>
        <RouterLink class="nav-link rounded-pill px-3 icon-link about-nav-link d-none d-md-flex" to="/about">
          <Info :size="17" />
          <span class="d-none d-md-inline">Acerca</span>
        </RouterLink>
        <RouterLink v-if="hasSession" class="nav-link rounded-pill px-3 icon-link" to="/movies">
          <Clapperboard :size="17" />
          <span class="d-none d-md-inline">Películas</span>
        </RouterLink>
        <RouterLink v-if="isAdmin" class="nav-link rounded-pill px-3 icon-link d-none d-md-flex" to="/admin">
          <ShieldCheck :size="17" />
          <span class="d-none d-md-inline">Admin</span>
        </RouterLink>
        <RouterLink v-if="!hasSession" class="nav-link rounded-pill px-3 icon-link" to="/signin">
          <LogIn :size="17" />
          <span class="d-none d-md-inline">Entrar</span>
        </RouterLink>
        <RouterLink v-if="!hasSession" class="btn rounded-pill px-4 soft-button icon-link nav-action d-none d-md-flex" to="/signup">
          <UserPlus :size="17" />
          <span class="d-none d-md-inline">Registro</span>
        </RouterLink>
        <RouterLink
          class="nav-link rounded-pill px-3 icon-link d-md-none"
          to="/app"
          @click="closeMenu"
        >
          <User :size="17" />
        </RouterLink>
        <div
          v-if="hasSession"
          class="position-relative user-dropdown-wrap"
          tabindex="-1"
          @blur="handleBlur"
        >
          <button
            class="btn rounded-pill px-3 py-2 soft-button icon-link d-none d-md-flex"
            type="button"
            @click="showMenu = !showMenu"
          >
            <User :size="17" />
            <span class="d-none d-md-inline">{{ firstName }}</span>
            <ChevronDown :size="14" class="d-none d-md-inline" />
          </button>
          <div
            v-if="showMenu"
            class="user-dropdown-menu position-absolute end-0 w-48 overflow-hidden rounded-xl shadow-lg"
            style="z-index: 30; background: var(--color-dropdown-bg); border: 1px solid var(--color-dropdown-border); backdrop-filter: blur(26px); -webkit-backdrop-filter: blur(26px);"
          >
            <RouterLink
              class="d-flex align-items-center gap-2 px-4 py-2.5 text-sm dropdown-item-link"
              to="/app"
              @click="closeMenu"
            >
              <User :size="16" />
              Mi cuenta
            </RouterLink>
            <button
              class="d-flex w-full align-items-center gap-2 px-4 py-2.5 text-sm dropdown-item-link"
              type="button"
              @click="signOut"
            >
              <LogOut :size="16" />
              Salir
            </button>
          </div>
        </div>
        <button
          class="btn rounded-pill px-3 soft-button icon-link theme-toggle"
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
