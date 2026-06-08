<script setup>
import SigninSkeleton from '@/components/skeletons/SigninSkeleton/SigninSkeleton.vue'
import { onMounted, reactive, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { Eye, EyeOff, KeyRound, LogIn, Mail } from '@lucide/vue'
import signinCover from '@/assets/movies/main-cover.png'
import seedUsers from '@/data/users.json'

const router = useRouter()
const error = ref('')
const isLoading = ref(true)

const form = reactive({
  correo: '',
  clave: '',
  recordar: false,
})
const showClave = ref(false)

onMounted(() => {
  const existing = JSON.parse(localStorage.getItem('movieUsers') || '[]')
  for (const seed of seedUsers) {
    const idx = existing.findIndex((u) => u.id === seed.id)
    if (idx !== -1) {
      existing[idx] = seed
    } else {
      existing.push(seed)
    }
  }
  localStorage.setItem('movieUsers', JSON.stringify(existing))
  const saved = localStorage.getItem('rememberedUser')
  if (saved) {
    const data = JSON.parse(saved)
    form.correo = data.correo
    form.clave = data.clave
    form.recordar = true
  }
  window.setTimeout(() => {
    isLoading.value = false
  }, 650)
})

function submitSignin() {
  error.value = ''
  const savedUsers = JSON.parse(localStorage.getItem('movieUsers') || '[]')
  const correo = form.correo.trim().toLowerCase()

  if (!savedUsers.length) {
    error.value = 'Primero crea una cuenta.'
    return
  }

  const user = savedUsers.find((u) => u.correo === correo && u.password === form.clave)

  if (!user) {
    error.value = 'Correo o clave incorrectos.'
    return
  }

  if (form.recordar) {
    localStorage.setItem('rememberedUser', JSON.stringify({ correo, clave: form.clave }))
  } else {
    localStorage.removeItem('rememberedUser')
  }

  localStorage.setItem('movieSession', JSON.stringify({ correo, rol: user.rol }))
  router.push(user.rol === 'admin' ? '/admin' : '/app')
}
</script>

<template>
  <main class="auth-shell flex min-h-screen items-start justify-center px-4 pb-14 pt-36 sm:pt-40">
    <SigninSkeleton v-if="isLoading" />
    <section v-else class="auth-panel signin-panel w-full max-w-5xl rounded-[1.75rem] p-4 sm:p-6">
      <div class="hidden overflow-hidden rounded-[1.35rem] signin-promo lg:block">
        <img :src="signinCover" alt="Portada promocional de películas" />
        <div class="signin-promo-overlay">
          <p>Historias listas para tu próxima noche</p>
          <h2>Vuelve a tus películas favoritas sin perder el hilo.</h2>
          <span>Drama, suspenso y romance guardados en un solo lugar.</span>
        </div>
      </div>

      <div class="ios-surface signin-form-surface rounded-[1.35rem] p-6 sm:p-8">
        <p class="mb-2 inline-flex items-center gap-2 text-sm font-medium text-emerald-700">
          <LogIn :size="17" />
          <span>Movs App</span>
        </p>
        <h1 class="mb-2 text-3xl font-semibold tracking-normal text-stone-950">Iniciar sesión</h1>
        <p class="mb-6 text-sm leading-6 text-stone-500">Entra para continuar con tu selección de películas.</p>

        <form class="d-grid gap-3" @submit.prevent="submitSignin">
          <div>
            <label class="form-label auth-field-label text-sm text-stone-700" for="correo">
              <Mail :size="15" />
              <span>Correo</span>
            </label>
            <input id="correo" v-model="form.correo" class="form-control rounded-pill px-4 py-3" type="email" />
          </div>
          <div>
            <label class="form-label auth-field-label text-sm text-stone-700" for="clave">
              <KeyRound :size="15" />
              <span>Clave</span>
            </label>
            <div class="position-relative">
              <input
                id="clave"
                v-model="form.clave"
                class="form-control rounded-pill px-4 py-3"
                :type="showClave ? 'text' : 'password'"
              />
              <button
                class="btn btn-link position-absolute top-50 end-0 translate-middle-y p-2 text-stone-400"
                type="button"
                tabindex="-1"
                @click="showClave = !showClave"
              >
                <Eye v-if="!showClave" :size="18" />
                <EyeOff v-else :size="18" />
              </button>
            </div>
          </div>

          <label class="d-flex align-items-center gap-2 text-sm text-stone-600" style="cursor: pointer">
            <input v-model="form.recordar" type="checkbox" class="form-check-input m-0" />
            Recordar contraseña
          </label>

          <p v-if="error" class="auth-error m-0 rounded-4 px-4 py-3 text-sm">{{ error }}</p>

          <button class="btn btn-dark rounded-pill py-3 soft-button icon-link justify-center" type="submit">
            <LogIn :size="17" />
            <span>Entrar</span>
          </button>
        </form>

        <p class="mt-5 mb-0 text-center text-sm text-stone-500">
          ¿No tienes cuenta?
          <RouterLink class="font-medium text-stone-950" to="/signup">Regístrate</RouterLink>
        </p>
      </div>
    </section>
  </main>
</template>

<style scoped src="./SigninView.css"></style>
