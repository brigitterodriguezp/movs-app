const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
const SESSION_KEY = 'movsSession'
const REMEMBERED_EMAIL_KEY = 'movsRememberedEmail'

function frontendRole(role) {
  return String(role || '').toUpperCase() === 'ADMIN' ? 'admin' : 'usuario'
}

function apiRole(role) {
  return frontendRole(role) === 'admin' ? 'ADMIN' : 'USER'
}

function normalizeSession(session) {
  return session ? { ...session, rol: frontendRole(session.rol) } : null
}

function normalizeUser(user) {
  return user ? { ...user, rol: frontendRole(user.rol) } : null
}

export function getSession() {
  const raw = sessionStorage.getItem(SESSION_KEY) || localStorage.getItem(SESSION_KEY)
  if (!raw) return null

  const session = normalizeSession(JSON.parse(raw))
  if (session?.tokenExpira && Date.parse(session.tokenExpira) <= Date.now()) {
    clearSession()
    return null
  }
  return session
}

export function setSession(session, remember = false) {
  sessionStorage.removeItem(SESSION_KEY)
  localStorage.removeItem(SESSION_KEY)
  const storage = remember ? localStorage : sessionStorage
  storage.setItem(SESSION_KEY, JSON.stringify(normalizeSession(session)))
}

export function clearSession() {
  sessionStorage.removeItem(SESSION_KEY)
  localStorage.removeItem(SESSION_KEY)
}

export function getRememberedEmail() {
  return localStorage.getItem(REMEMBERED_EMAIL_KEY) || ''
}

export function isAuthenticated() {
  return Boolean(getSession()?.token)
}

export function currentUser() {
  return getSession()
}

async function request(path, options = {}, timeoutMs = 5000) {
  const session = getSession()
  const headers = {
    Accept: 'application/json',
    ...(options.body ? { 'Content-Type': 'application/json' } : {}),
    ...(session?.token ? { Authorization: `Bearer ${session.token}` } : {}),
    ...(options.headers || {}),
  }

  const controller = new AbortController()
  const timer = setTimeout(() => controller.abort(), timeoutMs)

  let response
  try {
    response = await fetch(`${API_BASE_URL}${path}`, {
      ...options,
      headers,
      signal: controller.signal,
    })
  } catch (err) {
    if (err.name === 'AbortError') throw new Error('El servidor no respondió a tiempo.')
    throw new Error('Error interno en el servidor.')
  } finally {
    clearTimeout(timer)
  }

  if (response.status === 204) return null

  const contentType = response.headers.get('content-type') || ''
  const data = contentType.includes('application/json') ? await response.json() : await response.text()

  if (!response.ok) {
    const message = data?.mensaje || data?.message || 'Error interno en el servidor.'
    const err = new Error(message)
    err.validaciones = data?.validaciones || null
    err.status = response.status
    throw err
  }

  return data
}

export const api = {
  get: (path) => request(path),
  post: (path, body) => request(path, { method: 'POST', body: JSON.stringify(body) }),
  put: (path, body) => request(path, { method: 'PUT', body: JSON.stringify(body) }),
  delete: (path) => request(path, { method: 'DELETE' }),
}

export const PLAN_IDS = { basic: 1, plus: 2 }

export async function login(correo, password, remember = false) {
  const session = normalizeSession(await api.post('/api/auth/login', { correo, password }))
  setSession(session, remember)
  if (remember) localStorage.setItem(REMEMBERED_EMAIL_KEY, correo)
  else localStorage.removeItem(REMEMBERED_EMAIL_KEY)
  return session
}

export async function register(data) {
  return api.post('/api/registro', data)
}

export async function logout() {
  try {
    if (getSession()?.token) await api.post('/api/auth/logout')
  } finally {
    clearSession()
  }
}

export async function getUsers() {
  const users = await api.get('/api/usuarios')
  return users.map(normalizeUser)
}

export async function getUser(id) {
  return normalizeUser(await api.get(`/api/usuarios/${id}`))
}

export async function getMyProfile() {
  return normalizeUser(await api.get('/api/usuarios/me'))
}

export async function createUser(data) {
  return normalizeUser(await api.post('/api/usuarios', { ...data, rol: apiRole(data.rol) }))
}

export async function updateUser(id, data) {
  return normalizeUser(await api.put(`/api/usuarios/${id}`, { ...data, rol: apiRole(data.rol) }))
}

export async function deleteUser(id) {
  return api.delete(`/api/usuarios/${id}`)
}

export async function getPlans() {
  return api.get('/api/planes')
}

export async function getPlan(id) {
  return api.get(`/api/planes/${id}`)
}

export async function createPlan(data) {
  return api.post('/api/planes', data)
}

export async function getSubscriptions() {
  return api.get('/api/suscripciones')
}

export async function getSubscription(id) {
  return api.get(`/api/suscripciones/${id}`)
}

export async function getSubscriptionByUser(userId) {
  return api.get(`/api/suscripciones/usuario/${userId}`)
}

export async function createSubscription(data) {
  return api.post('/api/suscripciones', data)
}

export async function updateSubscription(id, data) {
  return api.put(`/api/suscripciones/${id}`, data)
}

export async function deleteSubscription(id) {
  return api.delete(`/api/suscripciones/${id}`)
}

export async function getMovies() {
  return api.get('/api/peliculas')
}

export async function getMovie(id) {
  return api.get(`/api/peliculas/${id}`)
}

export async function createMovie(data) {
  return api.post('/api/peliculas', data)
}

export async function updateMovie(id, data) {
  return api.put(`/api/peliculas/${id}`, data)
}

export async function deleteMovie(id) {
  return api.delete(`/api/peliculas/${id}`)
}

export async function getSessionStatus(userId) {
  return api.get(`/api/auth/sesion/${userId}`)
}
