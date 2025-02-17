<template>
  <div class="min-h-screen bg-gray-100 py-6">
    <div class="max-w-2xl mx-auto px-4">
      <div class="bg-white rounded-lg shadow-md overflow-hidden">
        <div v-if="loading" class="p-6 text-center">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
        </div>

        <div v-else-if="snack" class="divide-y divide-gray-200">
          <!-- En-tête -->
          <div class="p-6">
            <h1 class="text-3xl font-bold text-gray-900">{{ snack.name }}</h1>
            <div class="mt-4 flex justify-between items-center">
              <span class="text-2xl font-bold text-blue-600">
                {{ formatPrice(snack.price) }}
              </span>
              <span class="px-3 py-1 text-sm font-medium rounded-full bg-blue-100 text-blue-800">
                {{ snack.category }}
              </span>
            </div>
          </div>

          <!-- Description -->
          <div class="p-6">
            <h2 class="text-lg font-semibold text-gray-900 mb-2">Description</h2>
            <p class="text-gray-700">{{ snack.description || 'Aucune description disponible' }}</p>
          </div>
        </div>

        <div v-else class="p-6 text-center text-gray-500">
          Snack non trouvé
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const snack = ref(null)
const loading = ref(true)

const fetchSnackDetails = async () => {
  try {
    const response = await fetch(`http://localhost:8888/menu/${route.params.id}`)
    if (!response.ok) throw new Error('Snack non trouvé')
    snack.value = await response.json()
  } catch (error) {
    console.error('Error:', error)
  } finally {
    loading.value = false
  }
}

const formatPrice = (price) => {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'EUR'
  }).format(price)
}

onMounted(() => {
  fetchSnackDetails()
})
</script>
