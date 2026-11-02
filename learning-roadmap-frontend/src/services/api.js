import axios from 'axios';

/**
 * API SERVICE — All HTTP calls to Spring Boot backend
 *
 * V2 CHANGES (JWT):
 *  - A request interceptor adds "Authorization: Bearer <token>" to every call
 *    when the user is logged in.
 *  - A response interceptor handles 401 (token missing/expired): it clears the
 *    saved login so the user is sent back to the login page.
 *  - New functions: registerUser, loginUser
 *  - getUserHistory no longer needs an email (the server reads it from the token)
 *  - getErrorMessage() turns a backend error into text we can show on screen
 *
 * baseURL: Spring Boot server. Change it with REACT_APP_API_URL in a .env file.
 */

const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8087/api',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000, // 10 second timeout
});

// ---- Token storage (localStorage) ----
// Wrapped in try/catch because storage can be blocked in private browsing.
const TOKEN_KEY = 'roadmapToken';
const USER_KEY = 'roadmapUser';

export const tokenStorage = {
  getToken() {
    try { return localStorage.getItem(TOKEN_KEY); } catch (e) { return null; }
  },
  getUser() {
    try { return JSON.parse(localStorage.getItem(USER_KEY)); } catch (e) { return null; }
  },
  save(token, user) {
    try {
      localStorage.setItem(TOKEN_KEY, token);
      localStorage.setItem(USER_KEY, JSON.stringify(user));
    } catch (e) { /* ignore */ }
  },
  clear() {
    try {
      localStorage.removeItem(TOKEN_KEY);
      localStorage.removeItem(USER_KEY);
      sessionStorage.removeItem('roadmapResult'); // don't keep a previous user's result
    } catch (e) { /* ignore */ }
  },
};

// Event name used to tell the app "the user was logged out" (see AuthContext)
export const LOGOUT_EVENT = 'auth:logout';

// ---- Interceptors ----

// Before every request: attach the token if we have one
api.interceptors.request.use((config) => {
  const token = tokenStorage.getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// After every response: if the server says 401 on a protected call, the token
// is missing/expired, so log the user out. (Login/register 401 are normal errors.)
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const url = error.config?.url || '';
    const isAuthCall = url.startsWith('/auth/');
    if (error.response?.status === 401 && !isAuthCall && tokenStorage.getToken()) {
      tokenStorage.clear();
      window.dispatchEvent(new Event(LOGOUT_EVENT));
    }
    return Promise.reject(error);
  }
);

/**
 * Turns an axios error into a message for the user.
 * Backend errors look like: { status, message, fieldErrors? }
 */
export const getErrorMessage = (err, fallback = 'Something went wrong. Please try again.') => {
  if (!err.response) {
    return 'Cannot reach the server. Is the backend running?';
  }
  const data = err.response.data;
  if (data?.fieldErrors) {
    return Object.values(data.fieldErrors).join(' · ');
  }
  return data?.message || fallback;
};

// =============================================
// AUTH endpoints (public)
// =============================================

/** POST /api/auth/register → { token, tokenType, expiresInMs, name, email } */
export const registerUser = (data) => api.post('/auth/register', data);

/** POST /api/auth/login → same response shape as register */
export const loginUser = (data) => api.post('/auth/login', data);

// =============================================
// ROLE endpoints (public)
// =============================================

// GET /api/roles → fetch all job roles for the dropdown
export const getAllRoles = () => api.get('/roles');

// GET /api/skills/role/{roleId} → fetch skills for a role
export const getSkillsByRole = (roleId) => api.get(`/skills/role/${roleId}`);

// =============================================
// ROADMAP endpoints (need login)
// =============================================

/**
 * POST /api/roadmap/generate
 * @param {Object} data - { targetRoleId, currentSkills[] }
 * (name and email now come from the token, so we don't send them)
 */
export const generateRoadmap = (data) => api.post('/roadmap/generate', data);

/** GET /api/roadmap/history → the logged-in user's past roadmaps */
export const getUserHistory = () => api.get('/roadmap/history');

export default api;
