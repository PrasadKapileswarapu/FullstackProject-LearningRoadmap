import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import SkillCard from '../components/SkillCard';
import './ResultPage.css';

/**
 * RESULT PAGE — Shows the generated roadmap
 *
 * Data comes from sessionStorage (saved in GeneratePage after API call).
 * If no data found, redirect back to /generate.
 *
 * KEY CONCEPTS shown here:
 *  - sessionStorage: temporary browser storage (clears when tab closes)
 *  - Conditional rendering: show different UI based on state
 *  - Component composition: using <SkillCard> for each skill
 */
function ResultPage() {
  const navigate = useNavigate();
  const [roadmap, setRoadmap] = useState(null);
  const [activeTab, setActiveTab] = useState('learn'); // 'learn' or 'known'

  useEffect(() => {
    // Read result saved by GeneratePage
    const saved = sessionStorage.getItem('roadmapResult');
    if (!saved) {
      navigate('/generate'); // No data? Send them back
      return;
    }
    setRoadmap(JSON.parse(saved));
  }, [navigate]);

  if (!roadmap) return null; // Show nothing while loading

  const percent = roadmap.completionPercentage;

  // Determine color for the progress ring
  const getProgressColor = () => {
    if (percent >= 70) return '#22c55e';
    if (percent >= 40) return '#f59e0b';
    return '#f43f5e';
  };

  return (
    <div className="result-page">
      <div className="container">

        {/* ---- HEADER ---- */}
        <div className="result-header animate-up">
          <div className="result-header__left">
            <div className="result-tag">✅ Roadmap Ready</div>
            <h1 className="result-title">
              Hello, <span className="accent">{roadmap.userName}</span>!
            </h1>
            <p className="result-role">
              Your path to: <strong>{roadmap.targetRole}</strong>
            </p>
            <p className="result-category">{roadmap.roleCategory}</p>
          </div>

          {/* Circular progress */}
          <div className="progress-ring-wrap">
            <svg className="progress-ring" viewBox="0 0 120 120">
              {/* Background circle */}
              <circle cx="60" cy="60" r="50" fill="none" stroke="rgba(255,255,255,0.07)" strokeWidth="10" />
              {/* Progress circle */}
              <circle
                cx="60" cy="60" r="50"
                fill="none"
                stroke={getProgressColor()}
                strokeWidth="10"
                strokeLinecap="round"
                strokeDasharray={`${2 * Math.PI * 50}`}
                strokeDashoffset={`${2 * Math.PI * 50 * (1 - percent / 100)}`}
                transform="rotate(-90 60 60)"
                style={{ transition: 'stroke-dashoffset 1s ease' }}
              />
            </svg>
            <div className="progress-ring__label">
              <span className="progress-ring__pct">{percent}%</span>
              <span className="progress-ring__sub">complete</span>
            </div>
          </div>
        </div>

        {/* ---- STATS ROW ---- */}
        <div className="stats-row animate-up">
          {[
            { label: 'Total Skills Required', value: roadmap.totalSkillsRequired, icon: '📚' },
            { label: 'Skills You Know',       value: roadmap.skillsAlreadyKnown,  icon: '✅' },
            { label: 'Skills to Learn',       value: roadmap.skillsToLearnCount,  icon: '🎯' },
            { label: 'Hours to Complete',     value: `${roadmap.totalHoursToLearn}h`, icon: '🕐' },
          ].map((stat, i) => (
            <div key={i} className="stat-card">
              <span className="stat-icon">{stat.icon}</span>
              <span className="stat-value">{stat.value}</span>
              <span className="stat-label">{stat.label}</span>
            </div>
          ))}
        </div>

        {/* ---- TABS ---- */}
        <div className="tabs animate-up">
          <button
            className={`tab ${activeTab === 'learn' ? 'tab--active' : ''}`}
            onClick={() => setActiveTab('learn')}
          >
            🎯 Skills to Learn ({roadmap.skillsToLearnCount})
          </button>
          <button
            className={`tab ${activeTab === 'known' ? 'tab--active' : ''}`}
            onClick={() => setActiveTab('known')}
          >
            ✅ Skills You Know ({roadmap.skillsAlreadyKnown})
          </button>
        </div>

        {/* ---- SKILLS GRID ---- */}
        {activeTab === 'learn' && (
          <div>
            {/* Priority legend */}
            <div className="priority-legend animate-up">
              <span className="badge badge-must">Must Know</span>
              <span className="badge badge-should">Should Know</span>
              <span className="badge badge-nice">Good to Know</span>
              <span className="legend-note">Skills sorted by priority</span>
            </div>

            {roadmap.skillsToLearn.length === 0 ? (
              <div className="empty-state">
                🎉 You already know all the required skills! You're ready for this role.
              </div>
            ) : (
              <div className="skills-grid animate-up">
                {roadmap.skillsToLearn.map(skill => (
                  <SkillCard key={skill.id} skill={skill} />
                ))}
              </div>
            )}
          </div>
        )}

        {activeTab === 'known' && (
          <div>
            {roadmap.matchedSkills.length === 0 ? (
              <div className="empty-state">
                No matching skills yet — that's okay! Everyone starts somewhere.
              </div>
            ) : (
              <div className="skills-grid animate-up">
                {roadmap.matchedSkills.map(skill => (
                  <SkillCard key={skill.id} skill={skill} />
                ))}
              </div>
            )}
          </div>
        )}

        {/* ---- ACTIONS ---- */}
        <div className="result-actions animate-up">
          <button className="btn btn-outline" onClick={() => navigate('/generate')}>
            ← Generate Another
          </button>
          <button className="btn btn-outline" onClick={() => navigate('/history')}>
            View History
          </button>
          <button className="btn btn-primary" onClick={() => window.print()}>
            🖨️ Print Roadmap
          </button>
        </div>

      </div>
    </div>
  );
}

export default ResultPage;
