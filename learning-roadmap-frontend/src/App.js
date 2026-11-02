import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Navbar from './components/Navbar';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import GeneratePage from './pages/GeneratePage';
import ResultPage from './pages/ResultPage';
import HistoryPage from './pages/HistoryPage';

/**
 * APP.JS — Root component
 *
 * React Router v6 handles navigation between pages.
 * <AuthProvider> makes the logged-in user available everywhere (V2: JWT login).
 * <ProtectedRoute> sends visitors who are not logged in to /login.
 *
 * Pages:
 *  /           → Landing page            (public)
 *  /login      → Log in                  (public)
 *  /register   → Create account          (public)
 *  /generate   → Form to create roadmap  (login required)
 *  /result     → Roadmap result display  (login required)
 *  /history    → Your past roadmaps      (login required)
 */
function App() {
  return (
    <Router>
      <AuthProvider>
        <Navbar />
        <Routes>
          <Route path="/"         element={<HomePage />} />
          <Route path="/login"    element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/generate" element={<ProtectedRoute><GeneratePage /></ProtectedRoute>} />
          <Route path="/result"   element={<ProtectedRoute><ResultPage /></ProtectedRoute>} />
          <Route path="/history"  element={<ProtectedRoute><HistoryPage /></ProtectedRoute>} />
        </Routes>
      </AuthProvider>
    </Router>
  );
}

export default App;
