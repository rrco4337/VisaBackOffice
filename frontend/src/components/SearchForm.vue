<template>
  <div class="search-form">
    <form @submit.prevent="onSubmit">
      <div>
        <input v-model="query" placeholder="Numéro de demande ou de passeport" style="width: 300px; padding: 5px;" />
      </div>

      <button type="submit" style="margin-top: 10px;">Générer le QR Code</button>
    </form>

    <div v-if="qrDataUrl" class="qr" style="margin-top: 20px;">
      <h4>QR Code généré !</h4>
      <img :src="qrDataUrl" alt="QR Code" />
      <p>Scannez-le ou <a :href="qrUrl" target="_blank">cliquez ici pour voir les demandes</a></p>
    </div>

    <div v-if="error" class="error">{{ error }}</div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import QRCode from 'qrcode'

const query = ref('')
const qrDataUrl = ref('')
const qrUrl = ref('')
const error = ref(null)

async function onSubmit() {
  error.value = null
  qrDataUrl.value = ''
  qrUrl.value = ''

  if (!query.value) {
    error.value = 'Veuillez saisir un numéro de demande ou de passeport.'
    return
  }

  try {
    // L'URL du QR Code pointe vers la page de résultats
    qrUrl.value = `${window.location.origin}${window.location.pathname}#/results?q=${encodeURIComponent(query.value)}`
    qrDataUrl.value = await QRCode.toDataURL(qrUrl.value)
  } catch (e) {
    error.value = 'Erreur lors de la génération du QR Code.'
    console.error(e)
  }
}
</script>
