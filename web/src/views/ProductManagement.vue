<template>
  <div class="p-6 max-w-7xl mx-auto">
    <!-- Upload Section -->
    <div class="mb-8 bg-white rounded-lg shadow-md p-6">
      <h2 class="text-2xl font-semibold text-gray-800 mb-4">Import Products</h2>
      <div class="flex items-center justify-center w-full">
        <label
          class="flex flex-col items-center justify-center w-full h-64 border-2 border-gray-300 border-dashed rounded-lg cursor-pointer bg-gray-50 hover:bg-gray-100 transition-colors"
          :class="{ 'border-blue-500 bg-blue-50': isDragging }"
          @dragenter.prevent="isDragging = true"
          @dragleave.prevent="isDragging = false"
          @dragover.prevent
          @drop.prevent="handleDrop"
        >
          <div class="flex flex-col items-center justify-center pt-5 pb-6">
            <svg class="w-10 h-10 mb-3 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
            </svg>
            <p class="mb-2 text-sm text-gray-500">
              <span class="font-semibold">Click to upload</span> or drag and drop
            </p>
            <p class="text-xs text-gray-500">CSV files only</p>
          </div>
          <input
            type="file"
            class="hidden"
            accept=".csv"
            @change="handleFileUpload"
            ref="fileInput"
          />
        </label>
      </div>

      <!-- Upload Progress -->
      <div v-if="isUploading" class="mt-4">
        <div class="w-full bg-gray-200 rounded-full h-2.5">
          <div class="bg-blue-600 h-2.5 rounded-full" :style="{ width: uploadProgress + '%' }"></div>
        </div>
        <p class="text-sm text-gray-600 mt-2">Uploading... {{ uploadProgress }}%</p>
      </div>
    </div>

    <!-- Products Table -->
    <div class="bg-white rounded-lg shadow-md overflow-hidden">
      <div class="p-6 border-b border-gray-200">
        <div class="flex justify-between items-center">
          <h2 class="text-2xl font-semibold text-gray-800">Products</h2>
          <div class="flex gap-4">
            <select
              v-model="selectedCategory"
              class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="">All Categories</option>
              <option v-for="category in categories" :key="category" :value="category">
                {{ category }}
              </option>
            </select>
            <button
              @click="refreshProducts"
              class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              Refresh
            </button>
          </div>
        </div>
      </div>

      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th v-for="header in tableHeaders"
                  :key="header"
                  class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                {{ header }}
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="product in products"
                :key="product.snackId"
                class="hover:bg-gray-50 transition-colors cursor-pointer"
                @click="navigateToProduct(product.snackId)"
            >
              <td class="px-6 py-4 whitespace-nowrap">{{ product.snackId }}</td>
              <td class="px-6 py-4 whitespace-nowrap">{{ product.name }}</td>
              <td class="px-6 py-4 whitespace-nowrap">
                {{ formatPrice(product.price) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-blue-100 text-blue-800">
                  {{ product.category }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const API_BASE_URL = 'http://localhost:8888'

const products = ref([])
const categories = ref([])
const selectedCategory = ref('')
const isDragging = ref(false)
const isUploading = ref(false)
const uploadProgress = ref(0)
const fileInput = ref(null)

const tableHeaders = ['ID', 'Name', 'Price', 'Category']

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
  } catch (error) {
    console.error('Error fetching products:', error)
  }
}

// Handle file upload
const handleFileUpload = async (event) => {
  const file = event.target.files[0]
  if (file) {
    await uploadFile(file)
  }
}

const handleDrop = async (event) => {
  isDragging.value = false
  const file = event.dataTransfer.files[0]
  if (file && file.name.endsWith('.csv')) {
    await uploadFile(file)
  }
}

const uploadFile = async (file) => {
  isUploading.value = true
  uploadProgress.value = 0

  const formData = new FormData()
  formData.append('file', file)

  try {
    const response = await fetch(`${API_BASE_URL}/upload`, {
      method: 'POST',
      body: formData
    })

    if (!response.ok) throw new Error('Upload failed')

    // Reset file input
    if (fileInput.value) {
      fileInput.value.value = ''
    }

    // Refresh products list
    await fetchProducts()

  } catch (error) {
    console.error('Error uploading file:', error)
  } finally {
    isUploading.value = false
  }
}

const navigateToProduct = (snackId) => {
  router.push(`/products/${snackId}`)
}

const refreshProducts = () => {
  fetchProducts()
}

const formatPrice = (price) => {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'EUR'
  }).format(price)
}

// Initial fetch
onMounted(() => {
  fetchProducts()
})
</script>
