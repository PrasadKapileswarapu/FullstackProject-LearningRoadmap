import axios from 'axios';

/**
 * API SERVICE — All HTTP calls to Spring Boot backend
 *
 * WHY AXIOS?
 * - Cleaner syntax than fetch()
 * - Auto JSON parsing
 * - Better error handling
 *
 * baseURL: matches Spring Boot server (localhost:8080)
 */

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000, // 10 second timeout
});

// =============================================
// ROLE endpoints
// =============================================

// GET /api/roles → fetch all job roles for the dropdown
export const getAllRoles = () => api.get('/roles');

// GET /api/skills/role/{roleId} → fetch skills for a role
export const getSkillsByRole = (roleId) => api.get(`/skills/role/${roleId}`);

// =============================================
// ROADMAP endpoints
// =============================================

/**
 * POST /api/roadmap/generate
 * @param {Object} data - { userName, userEmail, targetRoleId, currentSkills[] }
 */
export const generateRoadmap = (data) => api.post('/roadmap/generate', data);

/**
 * GET /api/roadmap/history?email=...
 * @param {string} email
 */
export const getUserHistory = (email) => api.get(`/roadmap/history?email=${email}`);

export default api;
