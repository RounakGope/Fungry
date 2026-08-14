import api from './client'

export const mapUser = (user) => {
  if (!user) return null
  return {
    id: user.userId,
    username: user.userName,
    email: user.userEmail,
    role: user.userRole,
    phone: user.phoneNumber,
  }
}

/** @param {{ userEmail: string, password: string }} credentials */
export const login = (credentials) =>
  api.post('/auth/login', credentials).then((r) => mapUser(r.data))

export const logout = () =>
  api.post('/auth/logout').then((r) => r.data)

export const getMe = () =>
  api.get('/auth/me').then((r) => mapUser(r.data))