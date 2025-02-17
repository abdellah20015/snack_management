import { createRouter, createWebHistory } from 'vue-router'
import ProductManagement from '../views/ProductManagement.vue'
import ProductDetail from '../views/ProductDetail.vue'
import QRScanner from '../views/QRScanner.vue'
import VideoGallery from '../views/VideoGallery.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/products'
    },
    {
      path: '/products',
      name: 'products',
      component: ProductManagement
    },
    {
      path: '/menu/:id',
      name: 'menu-detail',
      component: () => import('../views/MenuDetail.vue')
    },
    {
      path:'/scanner',
      name: 'scanner',
      component: QRScanner
    },
    {
      path: '/videos',
      name: 'videos',
      component: VideoGallery
    },
    {
      path: '/products/:id',
      name: 'product-detail',
      component: ProductDetail,
      props: true
    },
  ],
})

export default router
