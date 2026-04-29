<template>
  <div class="status-view">
    <div class="status-header">
      <button class="status-back" @click="$router.push('/')">← Retour</button>
      <div class="status-title">
        <span class="status-kicker">Espace demandeur</span>
        <h2>Bienvenue, voici les informations de votre demande</h2>
        <p class="status-subtitle">Nous restons a votre ecoute pendant tout le traitement.</p>
      </div>
    </div>

    <div v-if="loading">Chargement...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else>
      <div class="status-summary">
        <div class="summary-card">
          <div class="summary-label">Numero demande</div>
          <div class="summary-value">#{{ demande.id }}</div>
        </div>
        <div class="summary-card">
          <div class="summary-label">Statut</div>
          <div class="summary-value">
            <span :class="['badge', statusBadgeClass(demande.status)]">{{ formatStatus(demande.status) }}</span>
          </div>
        </div>
        <div class="summary-card">
          <div class="summary-label">Date de demande</div>
          <div class="summary-value">{{ formatDateTime(demande.date) }}</div>
        </div>
        <div class="summary-card">
          <div class="summary-label">Type</div>
          <div class="summary-value">{{ formatType(demande.type) }}</div>
        </div>
      </div>

      <div class="status-grid">
        <section class="status-card">
          <h3>Identite du demandeur</h3>
          <div class="info-row">
            <span class="info-label">Nom complet</span>
            <span class="info-value">{{ formatName(demande.demandeur) }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">Nationalite</span>
            <span class="info-value">{{ getDemandeurValue(demande.demandeur, 'nationalite') }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">Date de naissance</span>
            <span class="info-value">{{ formatDateOnly(getDemandeurValue(demande.demandeur, 'dateNaissance')) }}</span>
          </div>
        </section>

        <section class="status-card">
          <h3>Contact</h3>
          <div class="info-row">
            <span class="info-label">Email</span>
            <span class="info-value">{{ getDemandeurValue(demande.demandeur, 'email') }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">Telephone</span>
            <span class="info-value">{{ getDemandeurValue(demande.demandeur, 'telephone') }}</span>
          </div>
        </section>

        <section class="status-card">
          <h3>Conseil</h3>
          <p class="status-tip">Conservez ce numero et votre QR code pour tout suivi ulterieur.</p>
          <div class="status-note">
            <div class="note-badge">Info</div>
            <div>
              <div class="note-title">Besoin d'aide ?</div>
              <div class="note-text">Vous pouvez revenir sur cette page a tout moment pour consulter le statut.</div>
            </div>
          </div>
        </section>
      </div>

      <section class="status-card status-list">
        <div class="status-list-header">
          <h3>Demandes du demandeur</h3>
          <div class="status-list-controls" v-if="relatedDemandes.length">
            <label for="statusPageSize">Par page</label>
            <select id="statusPageSize" v-model.number="pageSize">
              <option v-for="size in pageSizes" :key="size" :value="size">{{ size }}</option>
            </select>
          </div>
        </div>

        <div v-if="relatedLoading">Chargement des demandes...</div>
        <div v-else-if="relatedError" class="error">{{ relatedError }}</div>
        <div v-else-if="relatedDemandes.length === 0">Aucune autre demande trouvee.</div>
        <div v-else>
          <div class="table-responsive">
            <table class="status-table">
              <thead>
                <tr>
                  <th>Numero</th>
                  <th>Statut</th>
                  <th>Type</th>
                  <th>Date</th>
                  <th>Acces</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="it in pagedDemandes" :key="it.id" :class="{ 'is-current': it.id === demande.id }">
                  <td>#{{ it.id }}</td>
                  <td>
                    <span :class="['badge', statusBadgeClass(it.status)]">{{ formatStatus(it.status) }}</span>
                  </td>
                  <td>{{ formatType(it.type) }}</td>
                  <td>{{ formatDateTime(it.date) }}</td>
                  <td>
                    <a @click.prevent="$router.push(`/status/${it.id}`)" href="#">Voir</a>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="pagination">
            <button type="button" @click="goPrev" :disabled="page === 1">Precedent</button>
            <span>Page {{ page }} / {{ totalPages }}</span>
            <button type="button" @click="goNext" :disabled="page === totalPages">Suivant</button>
          </div>
          <div class="pagination-info">
            Affichage {{ pageStart }}-{{ pageEnd }} sur {{ relatedDemandes.length }}
          </div>
        </div>
      </section>

      <details class="status-details">
        <summary>Details techniques</summary>
        <pre>{{ demande }}</pre>
      </details>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { getById, searchByPassport } from '../api'
import { useRoute } from 'vue-router'

const route = useRoute()
const id = route.params.id
const demande = ref(null)
const loading = ref(true)
const error = ref(null)

const relatedDemandes = ref([])
const relatedLoading = ref(false)
const relatedError = ref(null)
const pageSizes = [5, 10, 20]
const pageSize = ref(5)
const page = ref(1)

const totalPages = computed(() => {
  const total = Math.ceil((relatedDemandes.value || []).length / pageSize.value)
  return Math.max(total, 1)
})

const pagedDemandes = computed(() => {
  const start = (page.value - 1) * pageSize.value
  return (relatedDemandes.value || []).slice(start, start + pageSize.value)
})

const pageStart = computed(() => {
  if (!relatedDemandes.value || relatedDemandes.value.length === 0) return 0
  return (page.value - 1) * pageSize.value + 1
})

const pageEnd = computed(() => {
  if (!relatedDemandes.value || relatedDemandes.value.length === 0) return 0
  return Math.min(page.value * pageSize.value, relatedDemandes.value.length)
})

watch([relatedDemandes, pageSize], () => {
  page.value = 1
})

watch(totalPages, (nextTotal) => {
  if (page.value > nextTotal) page.value = nextTotal
})

function formatDateTime(s) {
  try { return new Date(s).toLocaleString() } catch { return s || '-' }
}

function formatDateOnly(s) {
  try { return new Date(s).toLocaleDateString() } catch { return s || '-' }
}

function formatName(demandeur) {
  if (!demandeur) return '-'
  const parts = [demandeur.prenom, demandeur.nom].filter(Boolean)
  return parts.length ? parts.join(' ') : '-'
}

function formatType(type) {
  if (!type) return '-'
  return String(type).replace(/_/g, ' ')
}

function formatStatus(status) {
  if (!status) return '-'
  return String(status).replace(/_/g, ' ')
}

function statusBadgeClass(status) {
  const value = String(status || '').toLowerCase()
  if (value.includes('rejet') || value.includes('refus') || value.includes('annul')) return 'badge-danger'
  if (value.includes('valid') || value.includes('accept') || value.includes('approuv')) return 'badge-success'
  if (value.includes('en_cours') || value.includes('en cours') || value.includes('trait')) return 'badge-info'
  return 'badge-primary'
}

function getDemandeurValue(demandeur, key) {
  if (!demandeur || demandeur[key] === null || demandeur[key] === undefined || demandeur[key] === '') return '-'
  return demandeur[key]
}

function goPrev() {
  if (page.value > 1) page.value -= 1
}

function goNext() {
  if (page.value < totalPages.value) page.value += 1
}

async function fetchRelatedDemandes(passportNumber) {
  if (!passportNumber) {
    relatedDemandes.value = []
    return
  }

  relatedLoading.value = true
  relatedError.value = null

  try {
    const list = await searchByPassport(passportNumber)
    const items = Array.isArray(list) ? list : []

    const uniqueItems = []
    const ids = new Set()
    for (const item of items) {
      if (!ids.has(item.id)) {
        ids.add(item.id)
        uniqueItems.push(item)
      }
    }

    uniqueItems.sort((a, b) => new Date(b.date) - new Date(a.date))
    relatedDemandes.value = uniqueItems
  } catch (e) {
    relatedError.value = 'Erreur lors du chargement des demandes.'
    relatedDemandes.value = []
  } finally {
    relatedLoading.value = false
  }
}

onMounted(async () => {
  try {
    const data = await getById(id)
    demande.value = data
    await fetchRelatedDemandes(data && data.passportNumber ? data.passportNumber : null)
  } catch (e) {
    error.value = 'Erreur lors de la récupération du statut.'
  } finally {
    loading.value = false
  }
})
</script>
