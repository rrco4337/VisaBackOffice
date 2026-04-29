<template>
  <div class="search-form">
    <form @submit.prevent="onSubmit">
      <div>
        <input v-model="query" placeholder="Numéro de demande ou de passeport" style="width: 300px; padding: 5px;" />
      </div>

      <button type="submit" :disabled="loading" style="margin-top: 10px;">
        {{ loading ? 'Chargement...' : 'Générer le QR Code' }}
      </button>
    </form>

    <div v-if="qrDataUrl" class="qr" style="margin-top: 20px;">
      <h4>QR Code généré !</h4>
      <img :src="qrDataUrl" alt="QR Code" />
      <p>Scannez-le pour voir les détails ou <router-link :to="'/results?q=' + encodeURIComponent(query)">cliquez ici pour voir les demandes</router-link></p>
    </div>

    <div v-if="error" class="error">{{ error }}</div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import QRCode from 'qrcode'
import { searchByPassport, getById } from '../api'

const query = ref('')
const qrDataUrl = ref('')
const loading = ref(false)
const error = ref(null)

async function onSubmit() {
  error.value = null
  qrDataUrl.value = ''
  loading.value = true

  if (!query.value) {
    error.value = 'Veuillez saisir un numéro de demande ou de passeport.'
    loading.value = false
    return
  }

  try {
    let items = []

    if (/^\d+$/.test(query.value)) {
      try {
        const item = await getById(query.value)
        if (item) items.push(item)
      } catch(e) {}
    }

    try {
      const passportItems = await searchByPassport(query.value)
      if (Array.isArray(passportItems) && passportItems.length > 0) {
        items.push(...passportItems)
      }
    } catch(e) {}

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
      loading.value = false
      return
    }

    const baseUrl = `${window.location.origin}${window.location.pathname}`
    let textData = `Résultats pour: ${query.value}\n`
    items.forEach(it => {
      const detailUrl = `${baseUrl}#/status/${encodeURIComponent(it.id)}`
      textData += `\nDemande N°${it.id}\nType: ${it.type || 'Non spécifié'}\nStatut: ${it.status}\nDate: ${new Date(it.date).toLocaleDateString()}\nURL: ${detailUrl}`
    })

    qrDataUrl.value = await QRCode.toDataURL(textData)
  } catch (e) {
    error.value = 'Erreur lors de la génération du QR Code.'
    console.error(e)
  } finally {
    loading.value = false
  }
}
</script>
