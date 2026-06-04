import React from 'react';
import { Link } from 'react-router-dom';
import './HomePage.css';

/**
 * HOME PAGE — Landing page
 * No API calls here — purely presentational.
 * Shows what the app does and leads user to /generate
 */
function HomePage() {
  // Feature cards data — stored as array so we can .map() over them
  const features = [
    {
      icon: '🎯',
      title: 'Pick Your Target Role',
      desc: 'Choose from curated tech roles like Full Stack Developer, Data Scientist, DevOps Engineer, and more.',
    },
    {
      icon: '✅',
      title: 'Tell Us What You Know',
      desc: 'Select the skills you already have. Our engine compares them against what the role actually requires.',
    },
    {
      icon: '🗺️',
      title: 'Get Your Roadmap',
      desc: 'Receive a prioritized learning plan with resources and estimated hours — so you know exactly what to study next.',
    },
  ];

  const roles = [
    'Full Stack Java Developer',
    'Frontend Developer',
    'Data Scientist',
    'DevOps Engineer',
  ];

  return (
    <div className="home">

      {/* HERO SECTION */}
      <section className="hero">
        <div className="hero__badge animate-up">🚀 Career Clarity Tool</div>
        <h1 className="hero__title animate-up">
          Know Exactly What<br />
          <span className="hero__title--accent">to Learn Next</span>
        </h1>
        <p className="hero__subtitle animate-up">
          Stop guessing which skills to learn. Enter your target role, mark what you already know,
          and get a personalized, prioritized roadmap — instantly.
        </p>
        <div className="hero__actions animate-up">
          <Link to="/generate" className="btn btn-primary">Generate My Roadmap →</Link>
          <Link to="/history"  className="btn btn-outline">View History</Link>
        </div>

        {/* Floating role pills */}
        <div className="hero__roles animate-up">
          {roles.map((role) => (
            <span key={role} className="role-pill">{role}</span>
          ))}
        </div>
      </section>

      {/* HOW IT WORKS */}
      <section className="how-it-works container">
        <h2 className="section-title">How It Works</h2>
        <div className="features-grid">
          {features.map((f, i) => (
            <div key={i} className="feature-card" style={{ animationDelay: `${i * 0.1}s` }}>
              <div className="feature-icon">{f.icon}</div>
              <h3 className="feature-title">{f.title}</h3>
              <p className="feature-desc">{f.desc}</p>
            </div>
          ))}
        </div>
      </section>

      {/* CTA */}
      <section className="cta-section">
        <div className="cta-box container">
          <h2 className="cta-title">Ready to build your path?</h2>
          <p className="cta-sub">It takes less than 2 minutes. No account needed.</p>
          <Link to="/generate" className="btn btn-primary">Get Started Free →</Link>
        </div>
      </section>

    </div>
  );
}

export default HomePage;
