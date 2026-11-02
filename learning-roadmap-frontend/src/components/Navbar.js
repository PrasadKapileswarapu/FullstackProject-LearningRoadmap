import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Navbar.css';

/**
 * NAVBAR — Top navigation bar
 * useLocation() hook detects the current URL to highlight the active link
 * V2: shows Login / Sign Up when logged out, and the user's name + Logout when logged in.
 */
function Navbar() {
  const location = useLocation();
  const navigate = useNavigate();
  const { user, isLoggedIn, logout } = useAuth();
  const [scrolled, setScrolled] = useState(false);

  // Add shadow when user scrolls down
  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 20);
    window.addEventListener('scroll', onScroll);
    return () => window.removeEventListener('scroll', onScroll); // cleanup
  }, []);

  // Helper: returns 'active' class if this is the current page
  const isActive = (path) => location.pathname === path ? 'nav-link active' : 'nav-link';

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const firstName = user?.name ? user.name.split(' ')[0] : '';

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
          <Link to="/" className={isActive('/')}>Home</Link>

          {isLoggedIn ? (
            <>
              <Link to="/generate" className={isActive('/generate')}>Generate</Link>
              <Link to="/history"  className={isActive('/history')}>History</Link>
              <span className="nav-user">Hi, {firstName}</span>
              <button type="button" className="btn btn-outline nav-cta" onClick={handleLogout}>
                Logout
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className={isActive('/login')}>Login</Link>
              <Link to="/register" className="btn btn-primary nav-cta">Sign Up →</Link>
            </>
          )}
        </div>

      </div>
    </nav>
  );
}

export default Navbar;
