import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import HomePage from './pages/HomePage';
import GeneratePage from './pages/GeneratePage';
import ResultPage from './pages/ResultPage';
import HistoryPage from './pages/HistoryPage';

/**
 * APP.JS — Root component
 *
 * React Router v6 handles navigation between pages.
 * <Routes> replaces <Switch> from older versions.
 *
 * Pages:
 *  /           → Landing page
 *  /generate   → Form to create roadmap
 *  /result     → Roadmap result display
 *  /history    → Past roadmaps by email
 */
function App() {
  return (
    <Router>
      <Navbar />
      <Routes>
        <Route path="/"          element={<HomePage />} />
        <Route path="/generate"  element={<GeneratePage />} />
        <Route path="/result"    element={<ResultPage />} />
        <Route path="/history"   element={<HistoryPage />} />
      </Routes>
    </Router>
  );
}

export default App;
