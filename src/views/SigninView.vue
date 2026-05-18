<script setup>
import { reactive, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { KeyRound, LogIn, Mail } from '@lucide/vue'

const router = useRouter()
const error = ref('')

const form = reactive({
  correo: '',
  clave: '',
})

function submitSignin() {
  error.value = ''
  const user = JSON.parse(localStorage.getItem('movieUser') || 'null')
  const correo = form.correo.trim().toLowerCase()

  if (!user) {
    error.value = 'Primero crea una cuenta.'
    return
  }

  if (user.correo !== correo || user.clave !== form.clave) {
    error.value = 'Correo o clave incorrectos.'
    return
  }

  localStorage.setItem('movieSession', JSON.stringify({ correo }))
  router.push('/app')
}
</script>

<template>
  <main class="auth-shell flex min-h-screen items-start justify-center px-4 pb-14 pt-36 sm:pt-40">
    <section class="auth-panel w-full max-w-md rounded-[1.75rem] p-4 sm:p-6">
      <div class="ios-surface rounded-[1.35rem] p-6 sm:p-8">
        <p class="mb-2 inline-flex items-center gap-2 text-sm font-medium text-emerald-700">
          <LogIn :size="17" />
          <span>Movs App</span>
        </p>
        <h1 class="mb-2 text-3xl font-semibold tracking-normal text-stone-950">Iniciar sesión</h1>
        <p class="mb-6 text-sm leading-6 text-stone-500">Entra con tu correo y clave guardados en el navegador.</p>

        <form class="d-grid gap-3" @submit.prevent="submitSignin">
          <div>
            <label class="form-label inline-flex items-center gap-2 text-sm text-stone-700" for="correo">
              <Mail :size="15" />
              <span>Correo</span>
            </label>
            <input id="correo" v-model="form.correo" class="form-control rounded-pill px-4 py-3" type="email" />
          </div>
          <div>
            <label class="form-label inline-flex items-center gap-2 text-sm text-stone-700" for="clave">
              <KeyRound :size="15" />
              <span>Clave</span>
            </label>
            <input id="clave" v-model="form.clave" class="form-control rounded-pill px-4 py-3" type="password" />
          </div>

          <p v-if="error" class="m-0 rounded-4 bg-red-50 px-4 py-3 text-sm text-red-700">{{ error }}</p>

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
