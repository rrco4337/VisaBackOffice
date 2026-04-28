<template>
  <div class="results-view">
    <button @click="$router.push('/')" style="margin-bottom: 20px;">← Nouvelle recherche</button>
    
    <h2>Résultats pour : {{ query }}</h2>

    <div v-if="loading">Chargement des demandes…</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else>
      <ResultList :items="results" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { searchByPassport, getById } from '../api'
import ResultList from '../components/ResultList.vue'

const route = useRoute()
const query = ref(route.query.q || '')
const results = ref([])
const loading = ref(false)
const error = ref(null)

async function fetchResults() {
  if (!query.value) return
  loading.value = true
  error.value = null
  
  try {
    let items = []

    // 1. Recherche par numéro de demande (si c'est un nombre)
    if (/^\d+$/.test(query.value)) {
      try {
        const item = await getById(query.value)
        if (item) items.push(item)
      } catch(e) {} // Ignorer si non trouvé
    }

    // 2. Recherche par numéro de passeport
    try {
      const passportItems = await searchByPassport(query.value)
      if (Array.isArray(passportItems) && passportItems.length > 0) {
        items.push(...passportItems)
      }
    } catch(e) {}

    // 3. Dédupliquer les résultats par ID
    const uniqueItems = []
    const ids = new Set()
    for (const item of items) {
      if (!ids.has(item.id)) {
        ids.add(item.id)
        uniqueItems.push(item)
      }
    }

    items = uniqueItems

    if (items.length === 0) {
      error.value = 'Aucune demande trouvée pour ce numéro.'
      results.value = []
    } else {
      items.sort((a,b)=> new Date(b.date) - new Date(a.date))
      results.value = items
    }
  } catch (e) {
    error.value = 'Erreur de connexion à l\'API.'
  } finally {
    loading.value = false
  }
}

onMounted(fetchResults)
watch(() => route.query.q, (newQ) => {
  query.value = newQ || ''
  fetchResults()
})
</script>
