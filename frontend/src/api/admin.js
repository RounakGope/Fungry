import api from './client'

/**
 * Admin-only: list all users, paginated/sortable/filterable by role.
 * Hits /admin/users/all — @PreAuthorize("hasRole('ADMIN')") on the backend,
 * so this will 403 if called by a non-admin session.
 * @param {{ page?: number, size?: number, dir?: string, sort?: string, role?: import('./types').UserRole }} params
 * @returns {Promise<import('./types').UserDTO[]>}
 */
export const getAllUsers = (params = {}) =>
  api.get('/admin/users/all', { params }).then((r) => r.data)

/**
 * Admin-only: fetch any user's profile by id.
 * @param {number} id
 * @returns {Promise<import('./types').UserDTO>}
 */
export const getUserById = (id) =>
  api.get(`/admin/users/${id}`).then((r) => r.data)