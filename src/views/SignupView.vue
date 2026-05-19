<script setup>
import { reactive } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { KeyRound, Mail, User, UserPlus, Users } from '@lucide/vue'

const router = useRouter()

const form = reactive({
  nombres: '',
  apellidos: '',
  correo: '',
  clave: '',
})

const errors = reactive({
  nombres: '',
  apellidos: '',
  correo: '',
  clave: '',
})

function submitSignup() {
  errors.nombres = ''
  errors.apellidos = ''
  errors.correo = ''
  errors.clave = ''

  if (!form.nombres.trim()) {
    errors.nombres = 'Ingresa tus nombres.'
  }

  if (!form.apellidos.trim()) {
    errors.apellidos = 'Ingresa tus apellidos.'
  }

  if (!form.correo.trim()) {
    errors.correo = 'Ingresa tu correo.'
  }

  if (!form.clave) {
    errors.clave = 'Ingresa una clave.'
  }

  if (errors.nombres || errors.apellidos || errors.correo || errors.clave) {
    return
  }

  const correo = form.correo.trim().toLowerCase()
  const savedUser = JSON.parse(localStorage.getItem('movieUser') || 'null')

  if (savedUser?.correo === correo) {
    errors.correo = 'Ya existe una cuenta con ese correo.'
    return
  }

  const user = {
    nombres: form.nombres.trim(),
    apellidos: form.apellidos.trim(),
    correo,
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
            <p class="m-0 text-sm leading-6 text-stone-500">Crea tu cuenta y empieza a organizar tus películas favoritas.</p>
          </div>
          <RouterLink class="w-fit font-medium text-stone-950" to="/signin">Ya tengo cuenta</RouterLink>
        </div>

        <form class="grid gap-3 md:grid-cols-2" @submit.prevent="submitSignup">
          <div>
            <label class="form-label auth-field-label text-sm text-stone-700" for="nombres">
              <User :size="15" />
              <span>Nombres</span>
            </label>
            <input id="nombres" v-model="form.nombres" class="form-control rounded-pill px-4 py-3" type="text" />
            <p v-if="errors.nombres" class="auth-field-error">{{ errors.nombres }}</p>
          </div>
          <div>
            <label class="form-label auth-field-label text-sm text-stone-700" for="apellidos">
              <Users :size="15" />
              <span>Apellidos</span>
            </label>
            <input id="apellidos" v-model="form.apellidos" class="form-control rounded-pill px-4 py-3" type="text" />
            <p v-if="errors.apellidos" class="auth-field-error">{{ errors.apellidos }}</p>
          </div>
          <div>
            <label class="form-label auth-field-label text-sm text-stone-700" for="correo">
              <Mail :size="15" />
              <span>Correo</span>
            </label>
            <input id="correo" v-model="form.correo" class="form-control rounded-pill px-4 py-3" type="email" />
            <p v-if="errors.correo" class="auth-field-error">{{ errors.correo }}</p>
          </div>
          <div>
            <label class="form-label auth-field-label text-sm text-stone-700" for="clave">
              <KeyRound :size="15" />
              <span>Clave</span>
            </label>
            <input id="clave" v-model="form.clave" class="form-control rounded-pill px-4 py-3" type="password" />
            <p v-if="errors.clave" class="auth-field-error">{{ errors.clave }}</p>
          </div>

          <button class="btn btn-dark rounded-pill py-3 soft-button icon-link justify-center md:col-span-2" type="submit">
            <UserPlus :size="17" />
            <span>Registrarme</span>
          </button>
        </form>
      </div>
    </section>
  </main>
</template>
