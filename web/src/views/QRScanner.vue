<template>
  <div class="p-6 max-w-7xl mx-auto">
    <!-- Products Grid -->
    <div class="bg-white rounded-lg shadow-md p-6">
      <div class="mb-6 flex justify-between items-center">
        <h2 class="text-2xl font-semibold text-gray-800">Menu des Snacks</h2>
        <select
          v-model="selectedCategory"
          class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
        >
          <option value="">Toutes les catégories</option>
          <option v-for="category in categories" :key="category" :value="category">
            {{ category }}
          </option>
        </select>
      </div>

      <!-- Products Grid -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div
          v-for="product in products"
          :key="product.snackId"
          class="border rounded-lg overflow-hidden hover:shadow-lg transition-shadow"
        >
          <!-- QR Code -->
          <div class="p-4 flex justify-center bg-gray-50">
            <canvas :id="'qr-' + product.snackId" class="w-48 h-48"></canvas>
          </div>

          <!-- Product Info -->
          <div class="p-4">
            <h3 class="text-lg font-semibold text-gray-800">{{ product.name }}</h3>
            <div class="mt-2 flex justify-between items-center">
              <span class="text-lg font-bold text-blue-600">
                {{ formatPrice(product.price) }}
              </span>
              <span class="px-3 py-1 text-sm font-medium rounded-full bg-blue-100 text-blue-800">
                {{ product.category }}
              </span>
            </div>

            <div class="mt-4 text-sm text-gray-600">
              <p>Scannez le code QR pour voir les détails</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import QRCode from 'qrcode'

const API_BASE_URL = 'http://localhost:8888'
const products = ref([])
const categories = ref([])
const selectedCategory = ref('')

// Fetch products
const fetchProducts = async () => {
  try {
    const url = selectedCategory.value
      ? `${API_BASE_URL}/products?category=${selectedCategory.value}`
      : `${API_BASE_URL}/products`

    const response = await fetch(url)
    if (!response.ok) throw new Error('Failed to fetch products')

    const data = await response.json()
    products.value = data.products || []

    // Extract unique categories
    const uniqueCategories = new Set(products.value.map(p => p.category))
    categories.value = [...uniqueCategories]

    // Generate QR codes after products are loaded
    generateQRCodes()
  } catch (error) {
    console.error('Error fetching products:', error)
  }
}

// Generate QR codes for products
const generateQRCodes = () => {
  products.value.forEach(product => {
    const canvas = document.getElementById(`qr-${product.snackId}`)
    if (canvas) {
      // L'URL que le client visitera après avoir scanné le QR code
      const productUrl = `${window.location.origin}/menu/${product.snackId}`

      QRCode.toCanvas(canvas, productUrl, {
        width: 200,
        margin: 1,
        color: {
          dark: '#000',
          light: '#FFF'
        }
      }, (error) => {
        if (error) console.error('Error generating QR code:', error)
      })
    }
  })
}

const formatPrice = (price) => {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'EUR'
  }).format(price)
}

// Watch for category changes
watch(selectedCategory, () => {
  fetchProducts()
})

// Initial fetch
onMounted(() => {
  fetchProducts()
})
</script>
