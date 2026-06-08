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
  if (dias < 0)   return 'text-red-500'
  if (dias < 30) return 'text-amber-600'
  return ''
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
        <p class="mb-1 inline-flex items-center gap-2 text-xs font-medium" style="color: var(--color-accent-text);">
          <BadgeCheck :size="14" />
          <span>Mi cuenta</span>
        </p>
        <h1 class="text-2xl font-semibold tracking-normal sm:text-3xl" style="color: var(--color-text);">Mi suscripción</h1>
      </div>

      <div v-if="isLoading" class="page-skeleton">
        <div class="ios-surface rounded-[1.35rem] p-6">
          <div class="page-skeleton-line page-skeleton-heading compact mb-3" />
          <div class="page-skeleton-line page-skeleton-text mb-2" />
          <div class="page-skeleton-line page-skeleton-text short" />
        </div>
      </div>

      <div v-else-if="!user" class="ios-surface rounded-[1.35rem] p-8 text-center">
        <User :size="40" class="mx-auto mb-4" style="color: var(--color-text-muted);" />
        <h2 class="mb-2 text-xl font-semibold" style="color: var(--color-text);">Cuenta no encontrada</h2>
        <p class="mb-4" style="color: var(--color-text-muted);">No pudimos encontrar tu información de suscripción.</p>
        <RouterLink class="btn rounded-pill px-5 py-2 soft-button icon-link" to="/signup" style="background: var(--color-accent); color: #fff; border-color: var(--color-accent);">
          <User :size="17" />
          <span>Crear cuenta</span>
        </RouterLink>
      </div>

      <div v-else class="ios-surface rounded-[1.35rem] p-4 sm:p-5">
        <div class="flex items-center gap-2">
          <h2 class="truncate text-lg font-semibold" style="color: var(--color-text);">{{ user.nombre }}</h2>
          <span
            class="inline-flex shrink-0 items-center gap-1 rounded-pill px-2.5 py-0.5 text-[0.65rem] font-medium"
            :style="user.suscripcion?.estado === 'active'
              ? { background: 'var(--color-accent-bg)', color: 'var(--color-accent-text)' }
              : { background: 'var(--color-surface)', color: 'var(--color-text-secondary)' }"
          >
            <ShieldCheck :size="11" />
            {{ user.suscripcion?.estado === 'active' ? 'Activa' : user.suscripcion?.estado }}
          </span>
        </div>

        <p class="mt-0.5 inline-flex items-center gap-1.5 text-sm" style="color: var(--color-text-muted);">
          <Mail :size="13" />
          {{ user.correo }}
        </p>

        <div class="mt-3 flex flex-wrap gap-3">
          <div class="flex-1 min-w-[14rem] rounded-xl border p-3" style="border-color: var(--color-border); background: var(--color-surface-strong);">
            <p class="mb-0.5 text-[0.6rem] font-medium uppercase tracking-wider" style="color: var(--color-text-muted);">Plan</p>
            <p class="text-base font-semibold" style="color: var(--color-text);">{{ user.suscripcion?.nombre || user.suscripcion?.plan }}</p>
            <p class="text-xs" style="color: var(--color-text-muted);">{{ user.suscripcion?.precio }}/mes</p>
            <div class="mt-1.5 flex flex-wrap gap-x-3 gap-y-0.5">
              <span
                v-for="benefit in user.suscripcion?.beneficios"
                :key="benefit"
                class="inline-flex items-center gap-1 text-[0.65rem]"
                style="color: var(--color-text-secondary);"
              >
                <ShieldCheck :size="10" style="color: var(--color-accent);" />
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

        <div class="mt-3 flex flex-wrap gap-x-5 gap-y-1 border-t pt-3 text-[0.65rem]" style="border-color: var(--color-border-subtle);">
          <p class="inline-flex items-center gap-1.5" style="color: var(--color-text-muted);">
            <Calendar :size="12" />
            Inicio: {{ user.suscripcion?.fecha_inicio || '—' }}
          </p>
          <p
            class="inline-flex items-center gap-1.5 font-medium"
            :class="user.suscripcion?.fecha_expiracion ? expiracionColor(daysUntilExpiry(user.suscripcion.fecha_expiracion)) : ''"
            style="color: var(--color-text-muted);"
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
