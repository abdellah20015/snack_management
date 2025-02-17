<template>
  <div class="min-h-screen bg-gray-100">
    <!-- Navigation -->
    <nav class="bg-white border-b">
      <div class="mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex h-16 items-center justify-between">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <span class="text-xl font-bold text-blue-600">SnackScan</span>
            </div>

            <!-- Navigation Links -->
            <div class="hidden md:ml-8 md:flex md:space-x-6">
              <router-link to="/products"
                class="flex items-center px-3 py-2 text-gray-700 hover:text-blue-600 transition-colors"
                :class="{ 'text-blue-600 font-medium': $route.path.includes('/products') }">
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
                </svg>
                Produits
              </router-link>

              <router-link to="/scanner"
                class="flex items-center px-3 py-2 text-gray-700 hover:text-blue-600 transition-colors"
                :class="{ 'text-blue-600 font-medium': $route.path === '/scanner' }">
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M12 4v1m6 11h2m-6 0h-2m2 0v4m-2-4h.01M5 20h14a2 2 0 002-2V6a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
                Scanner QR
              </router-link>

              <router-link to="/videos"
              class="flex items-center px-3 py-2 text-gray-700 hover:text-blue-600 transition-colors"
                :class="{ 'text-blue-600 font-medium': $route.path === '/videos' }">
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M12 4v1m6 11h2m-6 0h-2m2 0v4m-2-4h.01M5 20h14a2 2 0 002-2V6a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
              Videos
              </router-link>
              
              
            </div>
          </div>

          <!-- Mobile menu button -->
          <div class="flex md:hidden">
            <button @click="isMobileMenuOpen = !isMobileMenuOpen"
              class="inline-flex items-center justify-center p-2 rounded-md text-gray-700 hover:text-blue-600 hover:bg-gray-100 focus:outline-none">
              <svg class="h-6 w-6" :class="{'hidden': isMobileMenuOpen, 'block': !isMobileMenuOpen }" fill="none"
                stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
              </svg>
              <svg class="h-6 w-6" :class="{'block': isMobileMenuOpen, 'hidden': !isMobileMenuOpen }" fill="none"
                stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
        </div>
      </div>

      <!-- Mobile menu -->
      <div class="md:hidden" v-show="isMobileMenuOpen">
        <div class="pt-2 pb-3 space-y-1">
          <router-link to="/products"
            class="block px-3 py-2 text-base font-medium hover:bg-gray-100 hover:text-blue-600 transition-colors"
            :class="{ 'text-blue-600 bg-blue-50': $route.path.includes('/products') }"
            @click="isMobileMenuOpen = false">
            Produits
          </router-link>

          <router-link to="/scanner"
            class="block px-3 py-2 text-base font-medium hover:bg-gray-100 hover:text-blue-600 transition-colors"
            :class="{ 'text-blue-600 bg-blue-50': $route.path === '/scanner' }" @click="isMobileMenuOpen = false">
            Scanner QR
          </router-link>

          <router-link to="/videos"
            class="block px-3 py-2 text-base font-medium hover:bg-gray-100 hover:text-blue-600 transition-colors"
            :class="{ 'text-blue-600 bg-blue-50': $route.path === '/videos' }" @click="isMobileMenuOpen = false">
            Videos
          </router-link>

        </div>
      </div>
    </nav>

    <!-- Contenu principal -->
    <main class="container mx-auto py-6 px-4 sm:px-6 lg:px-8">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const isMobileMenuOpen = ref(false)
</script>

<style>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
