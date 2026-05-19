<script setup>
import { reactive } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { BadgeCheck, CreditCard, KeyRound, Mail, ShieldCheck, User, UserPlus, Users } from '@lucide/vue'

const router = useRouter()

const plans = [
  {
    id: 'basic',
    name: 'Basic',
    price: '$4.99',
    benefits: ['1 pantalla', 'Catálogo esencial', 'Calidad HD'],
  },
  {
    id: 'plus',
    name: 'Plus',
    price: '$8.99',
    benefits: ['3 pantallas', 'Estrenos destacados', 'Full HD y favoritos'],
  },
]

const form = reactive({
  nombres: '',
  apellidos: '',
  correo: '',
  clave: '',
  plan: 'basic',
  cardName: '',
  cardNumber: '',
  cardExpiry: '',
  cardCvv: '',
})

const errors = reactive({
  nombres: '',
  apellidos: '',
  correo: '',
  clave: '',
  cardName: '',
  cardNumber: '',
  cardExpiry: '',
  cardCvv: '',
})

function cardDigits() {
  return form.cardNumber.replace(/\D/g, '')
}

function detectCardBrand(number = cardDigits()) {
  if (/^4/.test(number)) return 'Visa'
  if (/^(5[1-5]|2[2-7])/.test(number)) return 'Mastercard'
  if (/^3[47]/.test(number)) return 'American Express'
  if (/^(6011|65|64[4-9])/.test(number)) return 'Discover'
  return number.length ? 'Otra tarjeta' : 'Tarjeta'
}

function isValidCardNumber(number) {
  if (number.length < 13 || number.length > 19) return false

  let sum = 0
  let shouldDouble = false

  for (let index = number.length - 1; index >= 0; index -= 1) {
    let digit = Number(number[index])

    if (shouldDouble) {
      digit *= 2
      if (digit > 9) digit -= 9
    }

    sum += digit
    shouldDouble = !shouldDouble
  }

  return sum % 10 === 0
}

function isValidExpiry(value) {
  const match = value.match(/^(\d{2})\/(\d{2})$/)
  if (!match) return false

  const month = Number(match[1])
  const year = Number(`20${match[2]}`)
  if (month < 1 || month > 12) return false

  const now = new Date()
  const expiry = new Date(year, month)
  return expiry > new Date(now.getFullYear(), now.getMonth())
}

function formatCardNumber() {
  form.cardNumber = cardDigits().slice(0, 19).replace(/(.{4})/g, '$1 ').trim()
}

function formatExpiry() {
  const value = form.cardExpiry.replace(/\D/g, '').slice(0, 4)
  form.cardExpiry = value.length > 2 ? `${value.slice(0, 2)}/${value.slice(2)}` : value
}

function submitSignup() {
  errors.nombres = ''
  errors.apellidos = ''
  errors.correo = ''
  errors.clave = ''
  errors.cardName = ''
  errors.cardNumber = ''
  errors.cardExpiry = ''
  errors.cardCvv = ''

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

  if (!form.cardName.trim()) {
    errors.cardName = 'Ingresa el titular.'
  }

  const cardNumber = cardDigits()

  if (!cardNumber) {
    errors.cardNumber = 'Ingresa el número de tarjeta.'
  } else if (!isValidCardNumber(cardNumber)) {
    errors.cardNumber = 'Número de tarjeta inválido.'
  }

  if (!form.cardExpiry) {
    errors.cardExpiry = 'Ingresa la fecha.'
  } else if (!isValidExpiry(form.cardExpiry)) {
    errors.cardExpiry = 'Fecha inválida o vencida.'
  }

  if (!/^\d{3,4}$/.test(form.cardCvv)) {
    errors.cardCvv = 'CVV inválido.'
  }

  if (
    errors.nombres ||
    errors.apellidos ||
    errors.correo ||
    errors.clave ||
    errors.cardName ||
    errors.cardNumber ||
    errors.cardExpiry ||
    errors.cardCvv
  ) {
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
    subscription: {
      plan: form.plan,
      status: 'active',
    },
    payment: {
      brand: detectCardBrand(cardNumber),
      last4: cardNumber.slice(-4),
    },
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
            <p class="m-0 text-sm leading-6 text-stone-500">Elige un plan y empieza a organizar tus películas favoritas.</p>
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

          <div class="md:col-span-2">
            <p class="mb-2 inline-flex items-center gap-2 text-sm font-medium text-stone-700">
              <BadgeCheck :size="15" />
              <span>Selecciona tu plan</span>
            </p>
            <div class="plan-grid">
              <label v-for="plan in plans" :key="plan.id" class="plan-option" :class="{ selected: form.plan === plan.id }">
                <input v-model="form.plan" class="visually-hidden" type="radio" name="plan" :value="plan.id" />
                <span class="plan-option-top">
                  <strong>{{ plan.name }}</strong>
                  <em>{{ plan.price }}/mes</em>
                </span>
                <span v-for="benefit in plan.benefits" :key="benefit" class="plan-benefit">
                  <ShieldCheck :size="13" />
                  {{ benefit }}
                </span>
              </label>
            </div>
          </div>

          <div class="payment-section md:col-span-2">
            <p class="mb-2 inline-flex items-center gap-2 text-sm font-medium text-stone-700">
              <CreditCard :size="15" />
              <span>Pago con tarjeta</span>
            </p>
            <div class="payment-box">
              <div class="md:col-span-2">
                <label class="form-label auth-field-label text-sm text-stone-700" for="cardName">
                  <User :size="15" />
                  <span>Titular</span>
                </label>
                <input id="cardName" v-model="form.cardName" class="form-control rounded-pill px-4 py-3" type="text" />
                <p v-if="errors.cardName" class="auth-field-error">{{ errors.cardName }}</p>
              </div>
              <div class="md:col-span-2">
                <label class="form-label auth-field-label text-sm text-stone-700" for="cardNumber">
                  <CreditCard :size="15" />
                  <span>Número de tarjeta</span>
                </label>
                <div class="card-number-wrap">
                  <input
                    id="cardNumber"
                    v-model="form.cardNumber"
                    class="form-control rounded-pill px-4 py-3"
                    inputmode="numeric"
                    placeholder="0000 0000 0000 0000"
                    type="text"
                    @input="formatCardNumber"
                  />
                  <span>{{ detectCardBrand() }}</span>
                </div>
                <p v-if="errors.cardNumber" class="auth-field-error">{{ errors.cardNumber }}</p>
              </div>
              <div>
                <label class="form-label auth-field-label text-sm text-stone-700" for="cardExpiry">
                  <CreditCard :size="15" />
                  <span>Vence</span>
                </label>
                <input
                  id="cardExpiry"
                  v-model="form.cardExpiry"
                  class="form-control rounded-pill px-4 py-3"
                  inputmode="numeric"
                  placeholder="MM/AA"
                  type="text"
                  @input="formatExpiry"
                />
                <p v-if="errors.cardExpiry" class="auth-field-error">{{ errors.cardExpiry }}</p>
              </div>
              <div>
                <label class="form-label auth-field-label text-sm text-stone-700" for="cardCvv">
                  <KeyRound :size="15" />
                  <span>CVV</span>
                </label>
                <input
                  id="cardCvv"
                  v-model="form.cardCvv"
                  class="form-control rounded-pill px-4 py-3"
                  inputmode="numeric"
                  maxlength="4"
                  placeholder="123"
                  type="password"
                />
                <p v-if="errors.cardCvv" class="auth-field-error">{{ errors.cardCvv }}</p>
              </div>
            </div>
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
