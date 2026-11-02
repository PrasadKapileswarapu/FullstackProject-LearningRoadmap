import React, { createContext, useContext, useEffect, useState, useCallback } from 'react';
import { loginUser, registerUser, tokenStorage, LOGOUT_EVENT } from '../services/api';

/**
 * AUTH CONTEXT — keeps "who is logged in" available to every component.
 *
 * Why Context? Navbar, ProtectedRoute and the pages all need to know if the
 * user is logged in. Context lets them read it without passing props down.
 *
 * Usage:  const { user, isLoggedIn, login, register, logout } = useAuth();
 */
const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  // Start from whatever is saved, so a page refresh keeps you logged in
  const [user, setUser] = useState(() =>
    tokenStorage.getToken() ? tokenStorage.getUser() : null
  );

  // Save token + user after a successful login/register
  const handleAuthSuccess = useCallback((data) => {
    const profile = { name: data.name, email: data.email };
    tokenStorage.save(data.token, profile);
    setUser(profile);
  }, []);

  const login = useCallback(async (email, password) => {
    const response = await loginUser({ email, password });
    handleAuthSuccess(response.data);
  }, [handleAuthSuccess]);

  const register = useCallback(async (name, email, password) => {
    const response = await registerUser({ name, email, password });
    handleAuthSuccess(response.data);
  }, [handleAuthSuccess]);

  const logout = useCallback(() => {
    tokenStorage.clear();
    setUser(null);
  }, []);

  // api.js fires this event when the server rejects an expired/invalid token
  useEffect(() => {
    const onForcedLogout = () => setUser(null);
    window.addEventListener(LOGOUT_EVENT, onForcedLogout);
    return () => window.removeEventListener(LOGOUT_EVENT, onForcedLogout);
  }, []);

  const value = { user, isLoggedIn: !!user, login, register, logout };
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used inside <AuthProvider>');
  return ctx;
}
