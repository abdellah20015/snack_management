<template>
    <div class="p-6 bg-gray-50 min-h-screen">
      <div class="max-w-6xl mx-auto">
        <!-- Barre de recherche -->
        <div class="flex items-center gap-4 mb-8">
          <div class="relative flex-1">
            <input
              v-model="searchQuery"
              type="text"
              class="w-full px-4 py-2 pl-10 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Rechercher des vidéos de nourriture..."
              @keyup.enter="fetchVideos"
            />
            <span class="absolute left-3 top-2.5 text-gray-400">
              <i class="fas fa-search h-5 w-5"></i>
            </span>
          </div>
          <button
            @click="fetchVideos"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition-colors"
            :disabled="loading"
          >
            <span v-if="loading" class="flex items-center gap-2">
              <i class="fas fa-spinner fa-spin"></i>
              Chargement...
            </span>
            <span v-else>Rechercher</span>
          </button>
        </div>

        <!-- Message d'erreur -->
        <div v-if="error" class="mb-6 p-4 bg-red-100 text-red-700 rounded-lg">
          {{ error }}
        </div>

        <!-- Grille de vidéos -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div
            v-for="video in videos"
            :key="video.url"
            class="bg-white rounded-xl shadow-lg overflow-hidden hover:shadow-xl transition-shadow duration-300"
          >
            <a :href="video.url" target="_blank" class="block">
              <div class="relative">
                <img
                  :src="video.thumbnail"
                  :alt="video.title"
                  class="w-full h-48 object-cover"
                />
                <div class="absolute bottom-2 right-2 bg-black bg-opacity-75 px-2 py-1 rounded text-white text-sm">
                  {{ video.views }}
                </div>
              </div>
              <div class="p-4">
                <h3 class="text-lg font-semibold line-clamp-2 mb-2">{{ video.title }}</h3>
                <div class="flex justify-between items-center text-sm text-gray-600">
                  <span>{{ video.channel }}</span>
                  <span>{{ video.uploadDate }}</span>
                </div>
              </div>
            </a>
          </div>
        </div>

        <!-- État de chargement -->
        <div v-if="loading" class="flex justify-center my-8">
          <div class="animate-spin rounded-full h-12 w-12 border-4 border-blue-500 border-t-transparent"></div>
        </div>
      </div>
    </div>
  </template>

  <script>
  export default {
    name: 'VideoGallery',
    data() {
      return {
        videos: [],
        searchQuery: 'snacks food review',
        loading: false,
        error: null
      }
    },
    methods: {
      async fetchVideos() {
        this.loading = true;
        this.error = null;
        try {
          const response = await fetch(`http://localhost:3000/api/videos/${encodeURIComponent(this.searchQuery)}`);
          if (!response.ok) throw new Error('Erreur lors de la récupération des vidéos');
          this.videos = await response.json();
        } catch (err) {
          this.error = err.message;
        } finally {
          this.loading = false;
        }
      }
    },
    mounted() {
      this.fetchVideos();
    }
  }
  </script>