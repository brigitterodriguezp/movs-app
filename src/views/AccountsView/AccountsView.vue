<script setup>
import { onMounted, ref } from 'vue'
import { BadgeCheck, Calendar, Clock, CreditCard, Mail, ShieldCheck, User } from '@lucide/vue'

const session = JSON.parse(localStorage.getItem('movieSession') || '{}')
const user = ref(null)
const isLoading = ref(true)

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
    const users = JSON.parse(localStorage.getItem('movieUsers') || '[]')
    user.value = users.find((u) => u.correo === session.correo) || null
    isLoading.value = false
  }, 650)
})
</script>

<template>
  <main class="page-shell flex justify-center px-4 pb-4 pt-28 sm:px-6">
    <section class="w-fit">
      <div class="mb-4">
        <p class="mb-1 inline-flex items-center gap-2 text-xs font-medium text-emerald-700">
          <BadgeCheck :size="14" />
          <span>Mi cuenta</span>
        </p>
        <h1 class="text-2xl font-semibold tracking-normal text-stone-950 sm:text-3xl">Mi suscripción</h1>
      </div>

      <div v-if="isLoading" class="page-skeleton">
        <div class="ios-surface rounded-[1.35rem] p-6">
          <div class="page-skeleton-line page-skeleton-heading compact mb-3" />
          <div class="page-skeleton-line page-skeleton-text mb-2" />
          <div class="page-skeleton-line page-skeleton-text short" />
        </div>
      </div>

      <div v-else-if="!user" class="ios-surface rounded-[1.35rem] p-8 text-center">
        <User :size="40" class="mx-auto mb-4 text-stone-400" />
        <h2 class="mb-2 text-xl font-semibold text-stone-700">Cuenta no encontrada</h2>
        <p class="mb-4 text-stone-500">No pudimos encontrar tu información de suscripción.</p>
        <RouterLink class="btn btn-dark rounded-pill px-5 py-2 soft-button icon-link" to="/signup">
          <User :size="17" />
          <span>Crear cuenta</span>
        </RouterLink>
      </div>

      <div v-else class="ios-surface rounded-[1.35rem] p-4 sm:p-5">
        <div class="flex items-center gap-2">
          <h2 class="truncate text-lg font-semibold text-stone-950">{{ user.nombre }}</h2>
          <span
            class="inline-flex shrink-0 items-center gap-1 rounded-pill px-2.5 py-0.5 text-[0.65rem] font-medium"
            :class="user.suscripcion?.estado === 'active'
              ? 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900/40 dark:text-emerald-300'
              : 'bg-stone-100 text-stone-600 dark:bg-stone-800 dark:text-stone-400'"
          >
            <ShieldCheck :size="11" />
            {{ user.suscripcion?.estado === 'active' ? 'Activa' : user.suscripcion?.estado }}
          </span>
        </div>

        <p class="mt-0.5 inline-flex items-center gap-1.5 text-sm text-stone-500">
          <Mail :size="13" />
          {{ user.correo }}
        </p>

        <div class="mt-3 flex flex-wrap gap-3">
          <div class="flex-1 min-w-[14rem] rounded-xl border border-stone-300 bg-white p-3 dark:border-stone-700 dark:bg-stone-800/50">
            <p class="mb-0.5 text-[0.6rem] font-medium uppercase tracking-wider text-stone-500">Plan</p>
            <p class="text-base font-semibold text-stone-900">{{ user.suscripcion?.nombre || user.suscripcion?.plan }}</p>
            <p class="text-xs text-stone-500">{{ user.suscripcion?.precio }}/mes</p>
            <div class="mt-1.5 flex flex-wrap gap-x-3 gap-y-0.5">
              <span
                v-for="benefit in user.suscripcion?.beneficios"
                :key="benefit"
                class="inline-flex items-center gap-1 text-[0.65rem] text-stone-600 dark:text-stone-400"
              >
                <ShieldCheck :size="10" class="text-emerald-600" />
                {{ benefit }}
              </span>
            </div>
          </div>

          <div class="relative flex-1 min-w-[14rem] overflow-hidden rounded-xl bg-gradient-to-br from-stone-800 to-stone-950 p-3 text-white shadow-lg">
            <div class="mb-2 flex items-center justify-between">
              <span class="text-[0.55rem] font-medium uppercase tracking-widest text-stone-400">{{ user.pago?.marca || 'Tarjeta' }}</span>
              <CreditCard :size="15" class="text-stone-400" />
            </div>
            <p class="mb-2 font-mono text-sm tracking-[0.2em]">
              •••• •••• •••• {{ user.pago?.ultimos4 || '••••' }}
            </p>
            <div class="flex items-center justify-between text-[0.6rem]">
              <div>
                <p class="text-stone-400">Titular</p>
                <p class="font-medium truncate max-w-[9rem]">{{ user.pago?.titular || user.nombre }}</p>
              </div>
              <div class="text-right shrink-0">
                <p class="text-stone-400">Vence</p>
                <p class="font-medium">{{ user.pago?.vencimiento || '—' }}</p>
              </div>
            </div>
            <div class="absolute -bottom-3 -right-3 h-14 w-14 rounded-full bg-white/5" />
            <div class="absolute -bottom-1 -right-1 h-7 w-7 rounded-full bg-white/5" />
          </div>
        </div>

        <div class="mt-3 flex flex-wrap gap-x-5 gap-y-1 border-t border-stone-100 pt-3 text-[0.65rem] dark:border-stone-800">
          <p class="inline-flex items-center gap-1.5 text-stone-500">
            <Calendar :size="12" />
            Inicio: {{ user.suscripcion?.fecha_inicio || '—' }}
          </p>
          <p
            class="inline-flex items-center gap-1.5 font-medium"
            :class="user.suscripcion?.fecha_expiracion ? expiracionColor(daysUntilExpiry(user.suscripcion.fecha_expiracion)) : 'text-stone-500'"
          >
            <Clock :size="12" />
            <template v-if="user.suscripcion?.fecha_expiracion">
              Expira: {{ user.suscripcion.fecha_expiracion }}
              <template v-if="daysUntilExpiry(user.suscripcion.fecha_expiracion) >= 0">
                ({{ daysUntilExpiry(user.suscripcion.fecha_expiracion) }}d)
              </template>
              <template v-else>(vencida)</template>
            </template>
            <span v-else>Sin fecha</span>
          </p>
        </div>
      </div>
    </section>
  </main>
</template>
