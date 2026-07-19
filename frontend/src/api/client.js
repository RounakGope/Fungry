import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api-v1.0',
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
})

let onUnauthorized = null

export const setUnauthorizedHandler = (handler) => {
  onUnauthorized = handler
}

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && onUnauthorized) {
      onUnauthorized()
    }
    return Promise.reject(error)
  }
)

export default api
