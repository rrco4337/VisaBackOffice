import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
})

export async function searchByPassport(passportNumber) {
  const resp = await api.get('/demandes', { params: { passportNumber } })
  return resp.data
}

export async function getById(id) {
  const resp = await api.get(`/demandes/${id}`)
  return resp.data
}

export default api
