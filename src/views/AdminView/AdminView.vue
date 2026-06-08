<script setup>
import { onMounted, ref, computed } from 'vue'
import { BadgeCheck, Search, ShieldCheck, Users } from '@lucide/vue'

const allUsers = ref([])
const isLoading = ref(true)
const searchQuery = ref('')

const filteredUsers = computed(() => {
  const q = searchQuery.value.toLowerCase().trim()
  if (!q) return allUsers.value
  return allUsers.value.filter(
    (u) =>
      u.nombre?.toLowerCase().includes(q) ||
      u.correo?.toLowerCase().includes(q) ||
      u.rol?.toLowerCase().includes(q) ||
      u.suscripcion?.plan?.toLowerCase().includes(q) ||
      u.suscripcion?.estado?.toLowerCase().includes(q)
  )
})

function daysUntilExpiry(expiracion) {
  const diff = new Date(expiracion) - new Date()
  return Math.ceil(diff / 86400000)
}

function expiracionColor(dias) {
  if (dias < 0) return 'text-red-600'
  if (dias < 30) return 'text-amber-600'
  return 'text-emerald-600'
}

onMounted(() => {
  window.setTimeout(() => {
    allUsers.value = JSON.parse(localStorage.getItem('movieUsers') || '[]')
    isLoading.value = false
  }, 650)
})
</script>

<template>
  <main class="page-shell px-4 pb-4 pt-32 sm:px-6 lg:px-10">
    <section class="mx-auto max-w-7xl">
      <div class="mb-8 flex flex-col justify-between gap-4 md:flex-row md:items-end">
        <div>
          <p class="mb-2 inline-flex items-center gap-2 text-sm font-medium text-emerald-700">
            <ShieldCheck :size="17" />
            <span>Administración</span>
          </p>
          <h1 class="text-4xl font-semibold tracking-normal text-stone-950 sm:text-5xl">Panel de administración</h1>
          <p class="mt-2 text-stone-500">Gestión de usuarios registrados y sus suscripciones.</p>
        </div>
        <div class="relative w-full md:w-72">
          <Search :size="16" class="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-stone-400" />
          <input
            v-model="searchQuery"
            class="form-control rounded-pill py-2 pl-9 pr-4 text-sm"
            placeholder="Buscar usuarios..."
            type="text"
          />
        </div>
      </div>

      <div v-if="isLoading" class="page-skeleton">
        <div class="ios-surface rounded-[1.35rem] p-6">
          <div class="page-skeleton-line page-skeleton-heading compact mb-4" />
          <div class="page-skeleton-line page-skeleton-text mb-2" />
          <div class="page-skeleton-line page-skeleton-text mb-2" />
          <div class="page-skeleton-line page-skeleton-text short" />
        </div>
      </div>

      <div v-else-if="!allUsers.length" class="ios-surface rounded-[1.35rem] p-8 text-center">
        <Users :size="40" class="mx-auto mb-4 text-stone-400" />
        <h2 class="mb-2 text-xl font-semibold text-stone-700">No hay usuarios registrados</h2>
        <p class="text-stone-500">Espera a que los usuarios se registren para gestionarlos.</p>
      </div>

      <div v-else class="ios-surface overflow-hidden rounded-[1.35rem]">
        <div class="overflow-x-auto">
          <table class="w-full text-left text-sm">
            <thead>
              <tr class="border-b border-stone-200 dark:border-stone-700">
                <th class="px-4 py-3 font-medium text-stone-500 sm:px-6">ID</th>
                <th class="px-4 py-3 font-medium text-stone-500 sm:px-6">Nombre</th>
                <th class="px-4 py-3 font-medium text-stone-500 sm:px-6">Correo</th>
                <th class="px-4 py-3 font-medium text-stone-500 sm:px-6">Rol</th>
                <th class="px-4 py-3 font-medium text-stone-500 sm:px-6">Plan</th>
                <th class="px-4 py-3 font-medium text-stone-500 sm:px-6">Estado</th>
                <th class="px-4 py-3 font-medium text-stone-500 sm:px-6">Expira</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="(user, index) in filteredUsers"
                :key="user.id || index"
                class="border-b border-stone-100 last:border-0 dark:border-stone-800"
              >
                <td class="px-4 py-3 text-stone-600 sm:px-6">{{ user.id || index + 1 }}</td>
                <td class="px-4 py-3 font-medium text-stone-900 sm:px-6">{{ user.nombre }}</td>
                <td class="px-4 py-3 text-stone-600 sm:px-6">{{ user.correo }}</td>
                <td class="px-4 py-3 sm:px-6">
                  <span
                    class="inline-flex items-center gap-1 rounded-pill px-2.5 py-0.5 text-xs font-medium"
                    :class="user.rol === 'admin'
                      ? 'bg-indigo-100 text-indigo-800 dark:bg-indigo-900/40 dark:text-indigo-300'
                      : 'bg-stone-100 text-stone-600 dark:bg-stone-800 dark:text-stone-400'"
                  >
                    {{ user.rol === 'admin' ? 'Admin' : 'Usuario' }}
                  </span>
                </td>
                <td class="px-4 py-3 text-stone-600 sm:px-6">
                  <span class="inline-flex items-center gap-1">
                    <BadgeCheck :size="13" class="text-emerald-600" />
                    {{ user.suscripcion?.nombre || user.suscripcion?.plan || '—' }}
                  </span>
                </td>
                <td class="px-4 py-3 sm:px-6">
                  <span
                    class="inline-flex items-center gap-1 rounded-pill px-2.5 py-0.5 text-xs font-medium"
                    :class="user.suscripcion?.estado === 'active'
                      ? 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900/40 dark:text-emerald-300'
                      : 'bg-stone-100 text-stone-600 dark:bg-stone-800 dark:text-stone-400'"
                  >
                    {{ user.suscripcion?.estado === 'active' ? 'Activa' : user.suscripcion?.estado || '—' }}
                  </span>
                </td>
                <td
                  class="px-4 py-3 font-medium sm:px-6"
                  :class="user.suscripcion?.fecha_expiracion ? expiracionColor(daysUntilExpiry(user.suscripcion.fecha_expiracion)) : 'text-stone-400'"
                >
                  <template v-if="user.suscripcion?.fecha_expiracion">
                    {{ user.suscripcion.fecha_expiracion }}
                    <span v-if="daysUntilExpiry(user.suscripcion.fecha_expiracion) >= 0">
                      ({{ daysUntilExpiry(user.suscripcion.fecha_expiracion) }}d)
                    </span>
                    <span v-else class="text-red-600">
                      (vencida)
                    </span>
                  </template>
                  <span v-else>—</span>
                </td>
              </tr>
              <tr v-if="!filteredUsers.length">
                <td class="px-4 py-8 text-center text-stone-400" colspan="7">
                  No se encontraron usuarios con ese criterio.
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="border-t border-stone-100 px-4 py-3 text-xs text-stone-400 dark:border-stone-800 sm:px-6">
          {{ filteredUsers.length }} de {{ allUsers.length }} usuarios
        </div>
      </div>
    </section>
  </main>
</template>
