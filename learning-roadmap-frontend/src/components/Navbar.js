import React, { useState, useEffect } from 'react';
import { Link, useLocation } from 'react-router-dom';
import './Navbar.css';

/**
 * NAVBAR — Top navigation bar
 * useLocation() hook detects the current URL to highlight the active link
 */
function Navbar() {
  const location = useLocation();
  const [scrolled, setScrolled] = useState(false);

  // Add shadow when user scrolls down
  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 20);
    window.addEventListener('scroll', onScroll);
    return () => window.removeEventListener('scroll', onScroll); // cleanup
  }, []);

  // Helper: returns 'active' class if this is the current page
  const isActive = (path) => location.pathname === path ? 'nav-link active' : 'nav-link';

  return (
    <nav className={`navbar ${scrolled ? 'scrolled' : ''}`}>
      <div className="nav-container">

        {/* Logo */}
        <Link to="/" className="nav-logo">
          <span className="logo-icon">⬡</span>
          <span className="logo-text">RoadMap<span className="logo-accent">.dev</span></span>
        </Link>

        {/* Navigation links */}
        <div className="nav-links">
          <Link to="/"         className={isActive('/')}>Home</Link>
          <Link to="/generate" className={isActive('/generate')}>Generate</Link>
          <Link to="/history"  className={isActive('/history')}>History</Link>
          <Link to="/generate" className="btn btn-primary nav-cta">Start Now →</Link>
        </div>

      </div>
    </nav>
  );
}

export default Navbar;
