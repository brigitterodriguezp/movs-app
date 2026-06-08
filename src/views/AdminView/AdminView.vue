<script setup>
import { onMounted, ref, computed } from 'vue'
import {
  BadgeCheck, Edit3, Plus, Search, ShieldCheck, ShieldX, Trash2, UserPlus, Users, X
} from '@lucide/vue'
import plansData from '@/data/plans.json'

const session = JSON.parse(localStorage.getItem('movieSession') || '{}')
const isAdmin = session.rol === 'admin'

const allUsers = ref([])
const isLoading = ref(true)
const searchQuery = ref('')
const showModal = ref(false)
const editingUser = ref(null)
const plans = plansData

const form = ref({
  nombre: '',
  correo: '',
  password: '',
  rol: 'usuario',
  plan: 'basic',
})

const deleteTarget = ref(null)
const selfDeleteUser = ref(null)
const adminDeleteBlocked = ref(null)

function clearSelfDelete() {
  selfDeleteUser.value = null
}

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
  if (dias < 0) return 'text-red-500'
  if (dias < 30) return 'text-amber-600'
  return ''
}

function loadUsers() {
  allUsers.value = JSON.parse(localStorage.getItem('movieUsers') || '[]')
}

function openCreate() {
  editingUser.value = null
  form.value = { nombre: '', correo: '', password: '', rol: 'usuario', plan: 'basic' }
  showModal.value = true
}

function openEdit(user) {
  editingUser.value = user
  form.value = {
    nombre: user.nombre || '',
    correo: user.correo || '',
    password: '',
    rol: user.rol || 'usuario',
    plan: user.suscripcion?.plan || 'basic',
  }
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  editingUser.value = null
}

function saveUser() {
  if (!form.value.nombre.trim() || !form.value.correo.trim()) return

  const saved = JSON.parse(localStorage.getItem('movieUsers') || '[]')

  if (editingUser.value) {
    const idx = saved.findIndex((u) => u.id === editingUser.value.id)
    if (idx === -1) return
    saved[idx].nombre = form.value.nombre.trim()
    if (form.value.password) saved[idx].password = form.value.password
    if (saved[idx].suscripcion) {
      saved[idx].suscripcion.plan = form.value.plan
      const now = new Date()
      const expiry = new Date(now)
      expiry.setDate(expiry.getDate() + 30)
      saved[idx].suscripcion.fecha_inicio = now.toISOString().slice(0, 10)
      saved[idx].suscripcion.fecha_expiracion = expiry.toISOString().slice(0, 10)
      const selectedPlan = plans.find((p) => p.id === form.value.plan)
      if (selectedPlan) {
        saved[idx].suscripcion.nombre = selectedPlan.name
        saved[idx].suscripcion.precio = selectedPlan.price
        saved[idx].suscripcion.beneficios = selectedPlan.benefits
      }
    }
  } else {
    const existing = saved.find((u) => u.correo === form.value.correo.trim().toLowerCase())
    if (existing) return
    const nextId = saved.reduce((max, u) => Math.max(max, u.id || 0), 0) + 1
    const selectedPlan = plans.find((p) => p.id === form.value.plan)
    const now = new Date()
    const expiry = new Date(now); expiry.setDate(expiry.getDate() + 30)
    saved.push({
      id: nextId,
      nombre: form.value.nombre.trim(),
      correo: form.value.correo.trim().toLowerCase(),
      password: form.value.password || 'default123',
      rol: form.value.rol,
      suscripcion: {
        plan: form.value.plan,
        nombre: selectedPlan?.name || form.value.plan,
        precio: selectedPlan?.price || '',
        beneficios: selectedPlan?.benefits || [],
        estado: 'active',
        fecha_inicio: now.toISOString().slice(0, 10),
        fecha_expiracion: expiry.toISOString().slice(0, 10),
      },
    })
  }

  localStorage.setItem('movieUsers', JSON.stringify(saved))
  loadUsers()
  closeModal()
}

function deleteUser(user) {
  const session = JSON.parse(localStorage.getItem('movieSession') || '{}')
  if (session.correo === user.correo) {
    selfDeleteUser.value = user
    setTimeout(() => { selfDeleteUser.value = null }, 2500)
    return
  }
  if (user.rol === 'admin') {
    adminDeleteBlocked.value = user
    setTimeout(() => { adminDeleteBlocked.value = null }, 2500)
    return
  }
  deleteTarget.value = user
}

function confirmDelete() {
  if (!deleteTarget.value) return
  const saved = JSON.parse(localStorage.getItem('movieUsers') || '[]')
  const filtered = saved.filter((u) => u.id !== deleteTarget.value.id)
  localStorage.setItem('movieUsers', JSON.stringify(filtered))
  deleteTarget.value = null
  loadUsers()
}

function cancelDelete() {
  deleteTarget.value = null
}

function deleteExpired() {
  const saved = JSON.parse(localStorage.getItem('movieUsers') || '[]')
  const now = new Date()
  const filtered = saved.filter((u) => {
    if (!u.suscripcion?.fecha_expiracion) return true
    return new Date(u.suscripcion.fecha_expiracion) > now
  })
  const count = saved.length - filtered.length
  if (!count) return
  if (!confirm(`¿Eliminar ${count} usuario(s) con suscripción vencida?`)) return
  localStorage.setItem('movieUsers', JSON.stringify(filtered))
  loadUsers()
}

onMounted(() => {
  window.setTimeout(() => {
    loadUsers()
    isLoading.value = false
  }, 650)
})
</script>

<template>
  <main v-if="isAdmin" class="page-shell px-4 pb-4 pt-32 sm:px-6 lg:px-10">
    <section class="mx-auto max-w-7xl">
      <div class="mb-6 flex flex-wrap items-end justify-between gap-4">
        <div>
          <p class="mb-2 inline-flex items-center gap-2 text-sm font-medium" style="color: var(--color-accent-text);">
            <ShieldCheck :size="17" />
            <span>Administración</span>
          </p>
          <h1 class="text-4xl font-semibold tracking-normal sm:text-5xl" style="color: var(--color-text);">Panel de administración</h1>
          <p class="mt-2" style="color: var(--color-text-muted);">Gestión de usuarios registrados.</p>
        </div>
        <div class="flex flex-wrap items-center gap-3">
          <button
            class="btn rounded-pill px-4 py-2 soft-button icon-link glass-accent-btn"
            type="button"
            @click="openCreate"
          >
            <Plus :size="16" />
            <span>Añadir usuario</span>
          </button>
          <div class="relative w-full sm:w-60">
            <Search :size="15" class="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2" style="color: var(--color-text-muted);" />
            <input
              v-model="searchQuery"
              class="form-control rounded-pill py-2 pl-9 pr-4 text-sm"
              placeholder="Buscar..."
              type="text"
            />
          </div>
        </div>
      </div>

      <!-- skeleton -->
      <div v-if="isLoading" class="page-skeleton">
        <div class="ios-surface rounded-[1.35rem] p-6">
          <div class="page-skeleton-line page-skeleton-heading compact mb-4" />
          <div class="page-skeleton-line page-skeleton-text mb-2" />
          <div class="page-skeleton-line page-skeleton-text mb-2" />
          <div class="page-skeleton-line page-skeleton-text short" />
        </div>
      </div>

      <!-- empty -->
      <div v-else-if="!allUsers.length" class="overflow-hidden rounded-[1.35rem] p-8 text-center" style="background: var(--color-surface-strong); border: 1px solid var(--color-border); -webkit-backdrop-filter: var(--glass-blur); backdrop-filter: var(--glass-blur);">
        <Users :size="40" class="mx-auto mb-4" style="color: var(--color-text-muted);" />
        <h2 class="mb-2 text-xl font-semibold" style="color: var(--color-text);">No hay usuarios registrados</h2>
        <p style="color: var(--color-text-muted);">Añade el primer usuario desde el botón superior.</p>
      </div>

      <!-- table -->
      <div v-else class="overflow-hidden rounded-[1.35rem]" style="background: var(--color-surface-strong); border: 1px solid var(--color-border); -webkit-backdrop-filter: var(--glass-blur); backdrop-filter: var(--glass-blur);">
        <div class="overflow-x-auto">
          <table class="w-full text-left text-sm">
            <thead>
              <tr class="border-b" style="border-color: var(--color-border);">
                <th class="px-4 py-3 font-medium sm:px-6" style="color: var(--color-text-muted);">ID</th>
                <th class="px-4 py-3 font-medium sm:px-6" style="color: var(--color-text-muted);">Nombre</th>
                <th class="px-4 py-3 font-medium sm:px-6" style="color: var(--color-text-muted);">Correo</th>
                <th class="px-4 py-3 font-medium sm:px-6" style="color: var(--color-text-muted);">Rol</th>
                <th class="px-4 py-3 font-medium sm:px-6" style="color: var(--color-text-muted);">Plan</th>
                <th class="px-4 py-3 font-medium sm:px-6" style="color: var(--color-text-muted);">Estado</th>
                <th class="px-4 py-3 font-medium sm:px-6" style="color: var(--color-text-muted);">Expira</th>
                <th class="px-4 py-3 font-medium sm:px-6" style="color: var(--color-text-muted);">Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="(user, index) in filteredUsers"
                :key="user.id || index"
                class="border-b last:border-0"
                style="border-color: var(--color-border-subtle);"
              >
                <td class="px-4 py-3 sm:px-6" style="color: var(--color-text-secondary);">{{ user.id || index + 1 }}</td>
                <td class="px-4 py-3 font-medium sm:px-6" style="color: var(--color-text);">{{ user.nombre }}</td>
                <td class="px-4 py-3 sm:px-6" style="color: var(--color-text-secondary);">{{ user.correo }}</td>
                <td class="px-4 py-3 sm:px-6">
                  <span
                    class="inline-flex items-center gap-1 rounded-pill px-2.5 py-0.5 text-xs font-medium"
                    :style="user.rol === 'admin'
                      ? { background: 'var(--color-accent-bg)', color: 'var(--color-accent-text)' }
                      : { background: 'var(--color-surface)', color: 'var(--color-text-secondary)' }"
                  >
                    {{ user.rol === 'admin' ? 'Admin' : 'Usuario' }}
                  </span>
                </td>
                <td class="px-4 py-3 sm:px-6" style="color: var(--color-text-secondary);">
                  <span class="inline-flex items-center gap-1">
                    <BadgeCheck :size="13" style="color: var(--color-accent);" />
                    {{ user.suscripcion?.nombre || user.suscripcion?.plan || '—' }}
                  </span>
                </td>
                <td class="px-4 py-3 sm:px-6">
                  <span
                    class="inline-flex items-center gap-1 rounded-pill px-2.5 py-0.5 text-xs font-medium"
                    :style="user.suscripcion?.estado === 'active'
                      ? { background: 'var(--color-accent-bg)', color: 'var(--color-accent-text)' }
                      : { background: 'var(--color-surface)', color: 'var(--color-text-secondary)' }"
                  >
                    {{ user.suscripcion?.estado === 'active' ? 'Activa' : user.suscripcion?.estado || '—' }}
                  </span>
                </td>
                <td
                  class="px-4 py-3 font-medium sm:px-6"
                  :class="user.suscripcion?.fecha_expiracion ? expiracionColor(daysUntilExpiry(user.suscripcion.fecha_expiracion)) : ''"
                >
                  <template v-if="user.suscripcion?.fecha_expiracion">
                    {{ user.suscripcion.fecha_expiracion }}
                    <span v-if="daysUntilExpiry(user.suscripcion.fecha_expiracion) >= 0">
                      ({{ daysUntilExpiry(user.suscripcion.fecha_expiracion) }}d)
                    </span>
                    <span v-else style="color: var(--color-error);">(vencida)</span>
                  </template>
                  <span v-else style="color: var(--color-text-muted);">—</span>
                </td>
                <td class="px-4 py-3 sm:px-6">
                  <template v-if="deleteTarget === user">
                    <div class="flex items-center gap-1.5 text-xs whitespace-nowrap">
                      <span style="color: var(--color-text-muted);">¿Eliminar?</span>
                      <button
                        class="btn rounded-pill px-2 py-1 soft-button"
                        style="color: var(--color-error); border-color: transparent; background: transparent;"
                        title="Confirmar"
                        type="button"
                        @click="confirmDelete"
                      >
                        <Trash2 :size="13" />
                      </button>
                      <button
                        class="btn rounded-pill px-2 py-1 soft-button"
                        style="color: var(--color-text-secondary); border-color: transparent; background: transparent;"
                        title="Cancelar"
                        type="button"
                        @click="cancelDelete"
                      >
                        <X :size="13" />
                      </button>
                    </div>
                  </template>
                  <template v-else-if="selfDeleteUser === user">
                    <Transition name="msg-fade">
                      <div class="flex items-center gap-1.5 text-xs whitespace-nowrap">
                        <span style="color: var(--color-error);">Este es tu usuario, no lo puedes borrar</span>
                      </div>
                    </Transition>
                  </template>
                  <template v-else-if="adminDeleteBlocked === user">
                    <Transition name="msg-fade">
                      <div class="flex items-center gap-1.5 text-xs whitespace-nowrap">
                        <span style="color: var(--color-error);">Es administrador, no lo puedes borrar</span>
                      </div>
                    </Transition>
                  </template>
                  <template v-else>
                    <div class="flex items-center gap-1">
                      <button
                        class="btn rounded-pill px-2 py-1 soft-button"
                        style="color: var(--color-accent); border-color: transparent; background: transparent;"
                        title="Editar"
                        type="button"
                        @click="openEdit(user)"
                      >
                        <Edit3 :size="14" />
                      </button>
                      <button
                        class="btn rounded-pill px-2 py-1 soft-button"
                        style="color: var(--color-error); border-color: transparent; background: transparent;"
                        title="Eliminar"
                        type="button"
                        @click="deleteUser(user)"
                      >
                        <Trash2 :size="14" />
                      </button>
                    </div>
                  </template>
                </td>
              </tr>
              <tr v-if="!filteredUsers.length">
                <td class="px-4 py-8 text-center" colspan="8" style="color: var(--color-text-muted);">
                  No se encontraron usuarios.
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div
          class="flex items-center justify-between border-t px-4 py-3 text-xs sm:px-6"
          style="border-color: var(--color-border); color: var(--color-text-muted);"
        >
          <span>{{ filteredUsers.length }} de {{ allUsers.length }} usuarios</span>
          <button
            class="btn rounded-pill px-3 py-1 soft-button"
            style="color: var(--color-error); border-color: transparent; background: transparent;"
            type="button"
            @click="deleteExpired"
          >
            Limpiar vencidos
          </button>
        </div>
      </div>
    </section>

    <!-- modal -->
    <Teleport to="body">
      <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
        <div class="modal-panel">
          <div class="modal-header">
            <h2 class="text-lg font-semibold" style="color: var(--color-text);">
              {{ editingUser ? 'Editar usuario' : 'Añadir usuario' }}
            </h2>
            <button
              class="btn rounded-pill px-2 py-1 soft-button"
              style="color: var(--color-text-muted); border-color: transparent; background: transparent;"
              type="button"
              @click="closeModal"
            >
              <X :size="18" />
            </button>
          </div>
          <form class="modal-body" @submit.prevent="saveUser">
            <div>
              <label class="form-label auth-field-label text-sm" style="color: var(--color-text);" for="modal-nombre">
                <UserPlus :size="15" />
                <span>Nombre completo</span>
              </label>
              <input id="modal-nombre" v-model="form.nombre" class="form-control rounded-pill px-4 py-2.5 text-sm" type="text" required />
            </div>
            <div>
              <label class="form-label auth-field-label text-sm" style="color: var(--color-text);" for="modal-correo">
                <Users :size="15" />
                <span>Correo electrónico</span>
              </label>
              <input
                id="modal-correo"
                v-model="form.correo"
                class="form-control rounded-pill px-4 py-2.5 text-sm"
                type="email"
                :readonly="!!editingUser"
                :required="!editingUser"
                :style="editingUser ? { opacity: 0.6 } : {}"
              />
            </div>
            <div v-if="!editingUser">
              <label class="form-label auth-field-label text-sm" style="color: var(--color-text);" for="modal-password">
                <span>Contraseña</span>
              </label>
              <input
                id="modal-password"
                v-model="form.password"
                class="form-control rounded-pill px-4 py-2.5 text-sm"
                type="password"
                required
              />
            </div>
            <div v-if="!editingUser">
              <label class="form-label auth-field-label text-sm" style="color: var(--color-text);" for="modal-rol">
                <ShieldCheck :size="15" />
                <span>Rol</span>
              </label>
              <select id="modal-rol" v-model="form.rol" class="form-control rounded-pill px-4 py-2.5 text-sm">
                <option value="usuario">Usuario</option>
                <option value="admin">Administrador</option>
              </select>
            </div>
            <div>
              <label class="form-label auth-field-label text-sm" style="color: var(--color-text);" for="modal-plan">
                <BadgeCheck :size="15" />
                <span>Plan de suscripción</span>
              </label>
              <select id="modal-plan" v-model="form.plan" class="form-control rounded-pill px-4 py-2.5 text-sm">
                <option v-for="plan in plans" :key="plan.id" :value="plan.id">{{ plan.name }} — {{ plan.price }}/mes</option>
              </select>
            </div>
            <div class="modal-footer" style="padding: 0; border: none;">
              <button
                class="btn rounded-pill px-4 py-2 soft-button"
                type="button"
                style="border: 1px solid var(--color-border); color: var(--color-text); background: transparent;"
                @click="closeModal"
              >
                Cancelar
              </button>
              <button
                class="btn rounded-pill px-4 py-2 soft-button icon-link glass-accent-btn"
                type="submit"
              >
                <UserPlus :size="16" />
                <span>{{ editingUser ? 'Guardar cambios' : 'Crear usuario' }}</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </Teleport>
  </main>

  <main v-else class="page-shell flex min-h-screen items-center justify-center px-4 pb-14 pt-36">
    <div class="ios-surface w-full max-w-lg rounded-[1.75rem] p-8 text-center">
      <ShieldX :size="56" class="mx-auto mb-4" style="color: var(--color-error);" />
      <h1 class="mb-2 text-3xl font-semibold" style="color: var(--color-text);">403</h1>
      <h2 class="mb-3 text-xl font-semibold" style="color: var(--color-text-secondary);">Acceso no autorizado</h2>
      <p class="mb-6" style="color: var(--color-text-muted);">No tienes permisos para acceder a esta sección.</p>
      <RouterLink class="btn rounded-pill px-5 py-2 soft-button" to="/" style="background: var(--color-accent); color: #fff; border-color: var(--color-accent);">
        Volver al inicio
      </RouterLink>
    </div>
  </main>
</template>

<style scoped src="./AdminView.css"></style>
