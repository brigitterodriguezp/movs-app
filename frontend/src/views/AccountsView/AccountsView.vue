<script setup>
import { onMounted, ref } from 'vue'
import { BadgeCheck, Calendar, Clock, Mail, ShieldCheck, User } from '@lucide/vue'
import { currentUser, getMyProfile, getSubscriptionByUser } from '@/services/api'

const session = currentUser()
const user = ref(null)
const subscription = ref(null)
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

onMounted(async () => {
  try {
    const [profile, sub] = await Promise.all([
      getMyProfile(),
      getSubscriptionByUser(session.usuarioId),
    ])
    user.value = profile
    subscription.value = sub
  } catch {
    user.value = null
    subscription.value = null
  } finally {
    isLoading.value = false
  }
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
        <RouterLink class="btn rounded-pill px-5 py-2 soft-button icon-link glass-accent-btn" to="/signup">
          <User :size="17" />
          <span>Crear cuenta</span>
        </RouterLink>
      </div>

      <div v-else class="ios-surface rounded-[1.35rem] p-4 sm:p-5">
        <div class="flex items-center gap-2">
          <h2 class="truncate text-lg font-semibold" style="color: var(--color-text);">{{ user.nombre }}</h2>
          <span
            v-if="subscription"
            class="inline-flex shrink-0 items-center gap-1 rounded-pill px-2.5 py-0.5 text-[0.65rem] font-medium"
            :style="subscription.estado === 'ACTIVA'
              ? { background: 'var(--color-accent-bg)', color: 'var(--color-accent-text)' }
              : { background: 'var(--color-surface)', color: 'var(--color-text-secondary)' }"
          >
            <ShieldCheck :size="11" />
            {{ subscription.estado === 'ACTIVA' ? 'Activa' : subscription.estado }}
          </span>
        </div>

        <p class="mt-0.5 inline-flex items-center gap-1.5 text-sm" style="color: var(--color-text-muted);">
          <Mail :size="13" />
          {{ user.correo }}
        </p>

        <div v-if="subscription" class="mt-3 flex flex-wrap gap-3">
          <div class="flex-1 min-w-[14rem] rounded-xl border p-3" style="border-color: var(--color-border); background: var(--color-surface-strong);">
            <p class="mb-0.5 text-[0.6rem] font-medium uppercase tracking-wider" style="color: var(--color-text-muted);">Plan</p>
            <p class="text-base font-semibold" style="color: var(--color-text);">{{ subscription.plan }}</p>
            <p class="text-xs" style="color: var(--color-text-muted);">{{ subscription.id }}</p>
          </div>

          <div class="flex-1 min-w-[14rem] rounded-xl border p-3" style="border-color: var(--color-border); background: var(--color-surface-strong);">
            <p class="mb-0.5 text-[0.6rem] font-medium uppercase tracking-wider" style="color: var(--color-text-muted);">Detalles</p>
            <p class="text-xs" style="color: var(--color-text-muted);">
              Inicio: {{ subscription.fechaInicio }}
            </p>
            <p class="text-xs" style="color: var(--color-text-muted);">
              Expira: {{ subscription.fechaExpiracion }}
              <span v-if="daysUntilExpiry(subscription.fechaExpiracion) >= 0">
                ({{ daysUntilExpiry(subscription.fechaExpiracion) }}d)
              </span>
              <span v-else style="color: var(--color-error);">(vencida)</span>
            </p>
          </div>
        </div>

        <div v-if="!subscription" class="mt-3 rounded-xl border p-3 text-center" style="border-color: var(--color-border); background: var(--color-surface-strong);">
          <p class="text-sm" style="color: var(--color-text-muted);">Sin suscripción activa</p>
          <RouterLink class="btn rounded-pill px-4 py-2 mt-2 soft-button glass-accent-btn" to="/signup">
            Adquirir plan
          </RouterLink>
        </div>

        <div v-if="subscription" class="mt-3 flex flex-wrap gap-x-5 gap-y-1 border-t pt-3 text-[0.65rem]" style="border-color: var(--color-border-subtle);">
          <p class="inline-flex items-center gap-1.5" style="color: var(--color-text-muted);">
            <Calendar :size="12" />
            Inicio: {{ subscription.fechaInicio || '—' }}
          </p>
          <p
            class="inline-flex items-center gap-1.5 font-medium"
            :class="subscription.fechaExpiracion ? expiracionColor(daysUntilExpiry(subscription.fechaExpiracion)) : ''"
            style="color: var(--color-text-muted);"
          >
            <Clock :size="12" />
            <template v-if="subscription.fechaExpiracion">
              Expira: {{ subscription.fechaExpiracion }}
              <template v-if="daysUntilExpiry(subscription.fechaExpiracion) >= 0">
                ({{ daysUntilExpiry(subscription.fechaExpiracion) }}d)
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
