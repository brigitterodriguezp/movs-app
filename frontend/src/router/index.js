import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView/HomeView.vue'
import SigninView from '../views/SigninView/SigninView.vue'
import SignupView from '../views/SignupView/SignupView.vue'
import MovieAppView from '../views/MovieAppView/MovieAppView.vue'
import AboutView from '../views/AboutView/AboutView.vue'
import AccountsView from '../views/AccountsView/AccountsView.vue'
import AdminView from '../views/AdminView/AdminView.vue'
import UnauthorizedView from '../views/UnauthorizedView/UnauthorizedView.vue'
import NotFoundView from '../views/NotFoundView/NotFoundView.vue'
import { getSession } from '../services/api'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/home',
      redirect: '/',
    },
    {
      path: '/signin',
      name: 'signin',
      component: SigninView,
    },
    {
      path: '/signup',
      name: 'signup',
      component: SignupView,
    },
    {
      path: '/about',
      name: 'about',
      component: AboutView,
    },
    {
      path: '/app',
      name: 'accounts',
      component: AccountsView,
    },
    {
      path: '/movies',
      name: 'movie-app',
      component: MovieAppView,
    },
    {
      path: '/admin',
      name: 'admin',
      component: AdminView,
    },
    {
      path: '/unauthorized',
      name: 'unauthorized',
      component: UnauthorizedView,
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: NotFoundView,
    },
  ],
})

router.beforeEach((to) => {
  const sessionData = getSession()

  if ((to.path === '/app' || to.path === '/movies' || to.path === '/admin') && !sessionData) {
    return '/signin'
  }

  if (to.path === '/admin' && sessionData?.rol !== 'admin') {
    return '/unauthorized'
  }
})

export default router
