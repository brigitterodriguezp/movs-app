const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
const SESSION_KEY = 'movsSession'

export function getSession() {
  const raw = sessionStorage.getItem(SESSION_KEY)
  return raw ? JSON.parse(raw) : null
}

export function setSession(session) {
  sessionStorage.setItem(SESSION_KEY, JSON.stringify(session))
}

export function clearSession() {
  sessionStorage.removeItem(SESSION_KEY)
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
    throw new Error(message)
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

export async function login(correo, password) {
  const session = await api.post('/api/auth/login', { correo, password })
  setSession(session)
  return session
}

export async function logout() {
  try {
    if (getSession()?.token) await api.post('/api/auth/logout')
  } finally {
    clearSession()
  }
}

export async function getUsers() {
  return api.get('/api/usuarios')
}

export async function getUser(id) {
  return api.get(`/api/usuarios/${id}`)
}

export async function getMyProfile() {
  return api.get('/api/usuarios/me')
}

export async function createUser(data) {
  return api.post('/api/usuarios', data)
}

export async function updateUser(id, data) {
  return api.put(`/api/usuarios/${id}`, data)
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

export async function getSessionStatus(userId) {
  return api.get(`/api/auth/sesion/${userId}`)
}
