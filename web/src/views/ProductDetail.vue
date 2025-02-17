<template>
  <div class="p-6 max-w-4xl mx-auto bg-white rounded-lg shadow-md">
    <div class="mb-4">
      <router-link
        to="/products"
        class="text-blue-600 hover:text-blue-800 flex items-center gap-2"
      >
        ← Retour aux produits
      </router-link>
    </div>

    <div v-if="loading" class="text-center py-8">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
    </div>

    <div v-else-if="product" class="space-y-6">
      <h1 class="text-3xl font-bold text-gray-900">{{ product.name }}</h1>
      <div class="grid grid-cols-2 gap-4">
        <div class="space-y-2">
          <p class="text-sm text-gray-500">ID</p>
          <p class="font-medium">{{ product.snackId }}</p>
        </div>
        <div class="space-y-2">
          <p class="text-sm text-gray-500">Prix</p>
          <p class="font-medium">{{ formatPrice(product.price) }}</p>
        </div>
        <div class="space-y-2">
          <p class="text-sm text-gray-500">Catégorie</p>
          <span class="px-3 py-1 text-sm font-semibold rounded-full bg-blue-100 text-blue-800">
            {{ product.category }}
          </span>
        </div>
      </div>
    </div>

    <div v-else class="text-center py-8 text-gray-500">
      Produit non trouvé
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const product = ref(null)
const loading = ref(true)

const formatPrice = (price) => {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'EUR'
  }).format(price)
}

onMounted(async () => {
  try {
    const response = await fetch(`http://localhost:8888/menu/${route.params.id}`)
    if (!response.ok) throw new Error('Product not found')
    product.value = await response.json()
  } catch (error) {
    console.error('Error fetching product:', error)
  } finally {
    loading.value = false
  }
})
</script>
