<template>
  <div class="results">
    <h3>Résultats</h3>
    <div v-if="!items || items.length===0">Aucun résultat.</div>
    <table v-else>
      <thead>
        <tr><th>Numéro demande</th><th>Statut</th><th>Date</th></tr>
      </thead>
      <tbody>
        <tr v-for="it in items" :key="it.id">
          <td><a @click.prevent="$router.push(`/status/${it.id}`)" href="#">{{ it.id }}</a></td>
          <td>{{ it.status }}</td>
          <td>{{ formatDate(it.date) }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const props = defineProps({ items: { type: Array, default: () => [] } })
const router = useRouter()

function formatDate(s) {
  try { return new Date(s).toLocaleString() } catch { return s }
}
</script>
