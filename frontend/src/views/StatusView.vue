<template>
  <div class="status-view">
    <button @click="$router.push('/')">← Retour</button>
    <h2>Statut de la demande</h2>

    <div v-if="loading">Chargement…</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else>
      <p><strong>Numéro demande:</strong> {{ demande.id }}</p>
      <p><strong>Statut:</strong> {{ demande.status }}</p>
      <p><strong>Date:</strong> {{ formatDate(demande.date) }}</p>
      <pre>{{ demande }}</pre>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getById } from '../api'
import { useRoute } from 'vue-router'

const route = useRoute()
const id = route.params.id
const demande = ref(null)
const loading = ref(true)
const error = ref(null)

function formatDate(s) {
  try { return new Date(s).toLocaleString() } catch { return s }
}

onMounted(async () => {
  try {
    const data = await getById(id)
    demande.value = data
  } catch (e) {
    error.value = 'Erreur lors de la récupération du statut.'
  } finally {
    loading.value = false
  }
})
</script>
