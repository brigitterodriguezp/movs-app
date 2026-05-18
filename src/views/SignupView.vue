<script setup>
import { reactive, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { KeyRound, Mail, User, UserPlus, Users } from '@lucide/vue'

const router = useRouter()
const error = ref('')

const form = reactive({
  nombres: '',
  apellidos: '',
  correo: '',
  clave: '',
})

function submitSignup() {
  error.value = ''

  if (!form.nombres || !form.apellidos || !form.correo || !form.clave) {
    error.value = 'Completa todos los campos.'
    return
  }

  const user = {
    nombres: form.nombres.trim(),
    apellidos: form.apellidos.trim(),
    correo: form.correo.trim().toLowerCase(),
    clave: form.clave,
  }

  localStorage.setItem('movieUser', JSON.stringify(user))
  localStorage.setItem('movieSession', JSON.stringify({ correo: user.correo }))
  router.push('/app')
}
</script>

<template>
  <main class="auth-shell flex min-h-screen items-start justify-center px-4 pb-14 pt-36 sm:pt-40">
    <section class="auth-panel w-full max-w-5xl rounded-[1.75rem] p-4 sm:p-6">
      <div class="ios-surface grid gap-8 rounded-[1.35rem] p-6 md:grid-cols-[0.8fr_1.2fr] sm:p-8">
        <div class="flex flex-col justify-between gap-8">
          <div>
            <p class="mb-2 inline-flex items-center gap-2 text-sm font-medium text-emerald-700">
              <UserPlus :size="17" />
              <span>Movs App</span>
            </p>
            <h1 class="mb-3 text-3xl font-semibold tracking-normal text-stone-950">Crear cuenta</h1>
            <p class="m-0 text-sm leading-6 text-stone-500">Guarda tus películas favoritas en una sesión local.</p>
          </div>
          <RouterLink class="w-fit font-medium text-stone-950" to="/signin">Ya tengo cuenta</RouterLink>
        </div>

        <form class="grid gap-3 md:grid-cols-2" @submit.prevent="submitSignup">
          <div>
            <label class="form-label inline-flex items-center gap-2 text-sm text-stone-700" for="nombres">
              <User :size="15" />
              <span>Nombres</span>
            </label>
            <input id="nombres" v-model="form.nombres" class="form-control rounded-pill px-4 py-3" type="text" />
          </div>
          <div>
            <label class="form-label inline-flex items-center gap-2 text-sm text-stone-700" for="apellidos">
              <Users :size="15" />
              <span>Apellidos</span>
            </label>
            <input id="apellidos" v-model="form.apellidos" class="form-control rounded-pill px-4 py-3" type="text" />
          </div>
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

          <button class="btn btn-dark rounded-pill py-3 soft-button icon-link justify-center md:col-span-2" type="submit">
            <UserPlus :size="17" />
            <span>Registrarme</span>
          </button>
        </form>
      </div>
    </section>
  </main>
</template>
