import { createRouter, createWebHashHistory } from 'vue-router'
import Home from '../views/HomeView.vue'
import StatusView from '../views/StatusView.vue'
import ResultsView from '../views/ResultsView.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/status/:id', component: StatusView, props: true },
  { path: '/results', component: ResultsView }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
