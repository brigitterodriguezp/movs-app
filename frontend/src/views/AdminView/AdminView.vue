<script setup>
import { onMounted, ref, computed, watch } from 'vue'
import {
  BadgeCheck, ChevronLeft, ChevronRight, Clapperboard, Edit3, Plus, Search, ShieldCheck, ShieldX, Trash2, UserPlus, Users, X
} from '@lucide/vue'
import { currentUser, getUsers, getSubscriptions, createUser, updateUser, deleteUser as apiDeleteUser, createSubscription, updateSubscription, getPlans } from '@/services/api'
import AdminMoviesSection from '@/components/AdminMoviesSection/AdminMoviesSection.vue'

const session = currentUser()
const isAdmin = session?.rol === 'admin'

const allUsers = ref([])
const subscriptions = ref([])
const plans = ref([])
const isLoading = ref(true)
const activeSection = ref('users')
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = 5
const showModal = ref(false)
const editingUser = ref(null)

const form = ref({
  nombre: '',
  correo: '',
  password: '',
  rol: 'usuario',
  planApiId: null,
})

const deleteTarget = ref(null)
const selfDeleteUser = ref(null)
const adminDeleteBlocked = ref(null)

function clearSelfDelete() {
  selfDeleteUser.value = null
}

function getUserSubscription(userId) {
  return subscriptions.value.find((s) => s.usuarioId === userId) || null
}

function getUserPlanName(userId) {
  const sub = getUserSubscription(userId)
  return sub?.plan || '—'
}

function getUserPlanEstado(userId) {
  const sub = getUserSubscription(userId)
  return sub?.estado || null
}

function getUserExpiry(userId) {
  const sub = getUserSubscription(userId)
  return sub?.fechaExpiracion || null
}

const mergedUsers = computed(() => {
  return allUsers.value.map((u) => {
    const sub = getUserSubscription(u.id)
    return { ...u, suscripcion: sub ? { plan: sub.plan, estado: sub.estado, fechaExpiracion: sub.fechaExpiracion, fechaInicio: sub.fechaInicio, id: sub.id, planId: sub.planId } : null }
  })
})

const filteredUsers = computed(() => {
  const q = searchQuery.value.toLowerCase().trim()
  if (!q) return mergedUsers.value
  return mergedUsers.value.filter(
    (u) =>
      u.nombre?.toLowerCase().includes(q) ||
      u.correo?.toLowerCase().includes(q) ||
      u.rol?.toLowerCase().includes(q) ||
      (u.suscripcion?.plan?.toLowerCase() || '').includes(q) ||
      (u.suscripcion?.estado?.toLowerCase() || '').includes(q)
  )
})

const totalPages = computed(() => Math.max(1, Math.ceil(filteredUsers.value.length / pageSize)))
const paginatedUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredUsers.value.slice(start, start + pageSize)
})
const visiblePages = computed(() => {
  const windowSize = 5
  const start = Math.max(1, Math.min(currentPage.value - 2, totalPages.value - windowSize + 1))
  const end = Math.min(totalPages.value, start + windowSize - 1)
  return Array.from({ length: end - start + 1 }, (_, index) => start + index)
})
const visibleFrom = computed(() => filteredUsers.value.length ? (currentPage.value - 1) * pageSize + 1 : 0)
const visibleTo = computed(() => Math.min(currentPage.value * pageSize, filteredUsers.value.length))

function goToPage(page) {
  currentPage.value = Math.min(Math.max(Number(page), 1), totalPages.value)
}

watch(searchQuery, () => { currentPage.value = 1 })
watch(totalPages, (pages) => {
  if (currentPage.value > pages) currentPage.value = pages
})

function daysUntilExpiry(expiracion) {
  if (!expiracion) return Infinity
  const diff = new Date(expiracion) - new Date()
  return Math.ceil(diff / 86400000)
}

function expiracionColor(dias) {
  if (dias < 0) return 'text-red-500'
  if (dias < 30) return 'text-amber-600'
  return ''
}

async function loadData() {
  try {
    const [usersData, subsData, plansData] = await Promise.all([
      getUsers(),
      getSubscriptions(),
      getPlans(),
    ])
    allUsers.value = usersData
    subscriptions.value = subsData
    plans.value = plansData.map((p) => ({
      id: p.id,
      codigo: p.codigo,
      name: p.nombre,
      price: `$${Number(p.precio).toFixed(2)}`,
      benefits: p.beneficios || [],
      duracionDias: p.duracionDias,
    }))
  } catch {
    allUsers.value = []
    subscriptions.value = []
    plans.value = []
  }
}

function openCreate() {
  editingUser.value = null
  form.value = { nombre: '', correo: '', password: '', rol: 'usuario', planApiId: plans.value[0]?.id || null }
  showModal.value = true
}

function openEdit(user) {
  editingUser.value = user
  const sub = getUserSubscription(user.id)
  form.value = {
    nombre: user.nombre || '',
    correo: user.correo || '',
    password: '',
    rol: user.rol || 'usuario',
    planApiId: sub?.planId || plans.value[0]?.id || null,
  }
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  editingUser.value = null
}

async function saveUser() {
  if (!form.value.nombre.trim() || !form.value.correo.trim()) return

  try {
    if (editingUser.value) {
      await updateUser(editingUser.value.id, {
        nombre: form.value.nombre.trim(),
        correo: form.value.correo.trim(),
        password: form.value.password || undefined,
        rol: form.value.rol,
      })
      const sub = getUserSubscription(editingUser.value.id)
      if (sub) {
        await updateSubscription(sub.id, {
          usuarioId: editingUser.value.id,
          planId: form.value.planApiId,
          fechaInicio: sub.fechaInicio,
          estado: sub.estado,
        })
      }
    } else {
      const user = await createUser({
        nombre: form.value.nombre.trim(),
        correo: form.value.correo.trim().toLowerCase(),
        password: form.value.password,
        rol: form.value.rol,
      })
      await createSubscription({
        usuarioId: user.id,
        planId: form.value.planApiId,
        fechaInicio: new Date().toISOString().slice(0, 10),
        estado: 'ACTIVA',
      })
    }
    await loadData()
    closeModal()
  } catch (err) {
    alert(err.message || 'Error al guardar usuario.')
  }
}

function deleteUser(user) {
  if (session?.correo === user.correo) {
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

async function confirmDelete() {
  if (!deleteTarget.value) return
  try {
    await apiDeleteUser(deleteTarget.value.id)
    deleteTarget.value = null
    await loadData()
  } catch (err) {
    alert(err.message || 'Error al eliminar usuario.')
  }
}

function cancelDelete() {
  deleteTarget.value = null
}

async function deleteExpired() {
  const expired = mergedUsers.value.filter((u) => {
    if (!u.suscripcion?.fechaExpiracion) return false
    return new Date(u.suscripcion.fechaExpiracion) <= new Date()
  })
  if (!expired.length) return
  if (!confirm(`¿Eliminar ${expired.length} usuario(s) con suscripción vencida?`)) return
  try {
        await Promise.all(expired.map((u) => apiDeleteUser(u.id)))
    await loadData()
  } catch (err) {
    alert(err.message || 'Error al eliminar usuarios vencidos.')
  }
}

onMounted(async () => {
  await loadData()
  isLoading.value = false
})
</script>

<template>
  <main v-if="isAdmin" class="page-shell px-4 pb-4 pt-32 sm:px-6 lg:px-10">
    <div class="mx-auto mb-5 flex max-w-7xl justify-center">
      <div class="inline-flex rounded-pill p-1" style="background: var(--color-surface-strong); border: 1px solid var(--color-border);">
        <button
          class="btn admin-section-toggle rounded-pill px-4 py-2 icon-link"
          :class="{ active: activeSection === 'users' }"
          type="button"
          @click="activeSection = 'users'"
        >
          <Users :size="16" />
          Usuarios
        </button>
        <button
          class="btn admin-section-toggle rounded-pill px-4 py-2 icon-link"
          :class="{ active: activeSection === 'movies' }"
          type="button"
          @click="activeSection = 'movies'"
        >
          <Clapperboard :size="16" />
          Películas
        </button>
      </div>
    </div>

    <section v-if="activeSection === 'users'" class="mx-auto max-w-7xl">
      <div class="mb-6 flex flex-wrap items-end justify-between gap-4">
        <div>
          <p class="mb-2 inline-flex items-center gap-2 text-sm font-medium" style="color: var(--color-accent-text);">
            <ShieldCheck :size="17" />
            <span>Administración</span>
          </p>
          <h1 class="text-4xl font-semibold tracking-normal sm:text-5xl" style="color: var(--color-text);">Panel de administración</h1>
          <p class="mt-2" style="color: var(--color-text-muted);">
            {{ allUsers.length }} {{ allUsers.length === 1 ? 'usuario registrado' : 'usuarios registrados' }}.
          </p>
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
              class="form-control rounded-pill py-2 pl-10 pr-4 text-sm"
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
                <th class="px-4 py-3 font-medium sm:px-6" style="color: var(--color-text-muted);">No.</th>
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
                v-for="(user, index) in paginatedUsers"
                :key="user.id || index"
                class="border-b last:border-0"
                style="border-color: var(--color-border-subtle);"
              >
                <td class="px-4 py-3 sm:px-6" style="color: var(--color-text-secondary);">
                  {{ (currentPage - 1) * pageSize + index + 1 }}
                </td>
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
                    {{ user.suscripcion?.plan || '—' }}
                  </span>
                </td>
                <td class="px-4 py-3 sm:px-6">
                  <span
                    class="inline-flex items-center gap-1 rounded-pill px-2.5 py-0.5 text-xs font-medium"
                    :style="user.suscripcion?.estado === 'ACTIVA'
                      ? { background: 'var(--color-accent-bg)', color: 'var(--color-accent-text)' }
                      : { background: 'var(--color-surface)', color: 'var(--color-text-secondary)' }"
                  >
                    {{ user.suscripcion?.estado === 'ACTIVA' ? 'Activa' : user.suscripcion?.estado || '—' }}
                  </span>
                </td>
                <td
                  class="px-4 py-3 font-medium sm:px-6"
                  :class="user.suscripcion?.fechaExpiracion ? expiracionColor(daysUntilExpiry(user.suscripcion.fechaExpiracion)) : ''"
                >
                  <template v-if="user.suscripcion?.fechaExpiracion">
                    {{ user.suscripcion.fechaExpiracion }}
                    <span v-if="daysUntilExpiry(user.suscripcion.fechaExpiracion) >= 0">
                      ({{ daysUntilExpiry(user.suscripcion.fechaExpiracion) }}d)
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
          class="flex flex-wrap items-center justify-between gap-3 border-t px-4 py-3 text-xs sm:px-6"
          style="border-color: var(--color-border); color: var(--color-text-muted);"
        >
          <span>
            Mostrando {{ visibleFrom }}–{{ visibleTo }} de {{ filteredUsers.length }}
            <template v-if="filteredUsers.length !== allUsers.length">({{ allUsers.length }} totales)</template>
          </span>
          <div class="flex items-center gap-2" aria-label="Paginación de usuarios">
            <button
              class="btn admin-pagination-button rounded-pill px-2 py-1 soft-button icon-link"
              type="button"
              aria-label="Página anterior"
              :disabled="currentPage === 1"
              @click="goToPage(currentPage - 1)"
            >
              <ChevronLeft :size="14" />
            </button>
            <button
              v-for="page in visiblePages"
              :key="page"
              class="btn admin-pagination-button rounded-pill px-2.5 py-1 soft-button"
              type="button"
              :aria-label="`Ir a la página ${page}`"
              :aria-current="page === currentPage ? 'page' : undefined"
              :style="page === currentPage
                ? { background: 'var(--color-accent)', color: '#fff', borderColor: 'var(--color-accent)' }
                : {}"
              @click="goToPage(page)"
            >
              {{ page }}
            </button>
            <button
              class="btn admin-pagination-button rounded-pill px-2 py-1 soft-button icon-link"
              type="button"
              aria-label="Página siguiente"
              :disabled="currentPage === totalPages"
              @click="goToPage(currentPage + 1)"
            >
              <ChevronRight :size="14" />
            </button>
            <span class="ms-1 whitespace-nowrap">de {{ totalPages }}</span>
          </div>
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

    <AdminMoviesSection v-else />

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
            <div>
              <label class="form-label auth-field-label text-sm" style="color: var(--color-text);" for="modal-password">
                <span>Contraseña {{ editingUser ? '(dejar vacío para mantener)' : '' }}</span>
              </label>
              <input
                id="modal-password"
                v-model="form.password"
                class="form-control rounded-pill px-4 py-2.5 text-sm"
                type="password"
                :required="!editingUser"
              />
            </div>
            <div>
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
              <select id="modal-plan" v-model="form.planApiId" class="form-control rounded-pill px-4 py-2.5 text-sm">
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
