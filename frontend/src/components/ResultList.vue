<template>
  <div class="results">
    <h3>Résultats</h3>
    <div v-if="!items || items.length===0">Aucun résultat.</div>
    <table v-else>
      <thead>
        <tr>
          <th>Numéro demande</th>
          <th>Type</th>
          <th>Statut</th>
          <th>Date</th>
          <th>Demandeur</th>
          <th>Contact</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="it in items" :key="it.id">
          <td><a @click.prevent="$router.push(`/status/${it.id}`)" href="#">{{ it.id }}</a></td>
          <td>{{ it.type || '-' }}</td>
          <td>{{ it.status }}</td>
          <td>{{ formatDateTime(it.date) }}</td>
          <td>
            <div>{{ formatDemandeurName(it.demandeur) }}</div>
            <small>{{ formatDemandeurMeta(it.demandeur) }}</small>
          </td>
          <td>
            <div>{{ it.demandeur && it.demandeur.email ? it.demandeur.email : '-' }}</div>
            <div>{{ it.demandeur && it.demandeur.telephone ? it.demandeur.telephone : '-' }}</div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
const props = defineProps({ items: { type: Array, default: () => [] } })

function formatDateTime(s) {
  try { return new Date(s).toLocaleString() } catch { return s }
}

function formatDateOnly(s) {
  try { return new Date(s).toLocaleDateString() } catch { return s }
}

function formatDemandeurName(demandeur) {
  if (!demandeur) return '-'
  const parts = [demandeur.prenom, demandeur.nom].filter(Boolean)
  return parts.length ? parts.join(' ') : '-'
}

function formatDemandeurMeta(demandeur) {
  if (!demandeur) return '-'
  const parts = []
  if (demandeur.nationalite) parts.push(demandeur.nationalite)
  if (demandeur.dateNaissance) parts.push(formatDateOnly(demandeur.dateNaissance))
  return parts.length ? parts.join(' • ') : '-'
}
</script>
