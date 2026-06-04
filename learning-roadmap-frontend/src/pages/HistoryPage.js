import React, { useState } from 'react';
import { getUserHistory } from '../services/api';
import './HistoryPage.css';

/**
 * HISTORY PAGE — Lookup past roadmaps by email
 *
 * Simple search page: user enters their email,
 * we call GET /api/roadmap/history?email=...
 * and display all their past roadmaps.
 */
function HistoryPage() {
  const [email, setEmail]       = useState('');
  const [history, setHistory]   = useState([]);
  const [loading, setLoading]   = useState(false);
  const [searched, setSearched] = useState(false);
  const [error, setError]       = useState('');

  const handleSearch = async () => {
    if (!email.includes('@')) {
      setError('Please enter a valid email address.');
      return;
    }
    setLoading(true);
    setError('');
    try {
      const response = await getUserHistory(email);
      setHistory(response.data);
      setSearched(true);
    } catch (err) {
      setError('Could not fetch history. Is the backend running?');
    } finally {
      setLoading(false);
    }
  };

  // Format date: "2024-01-15T10:30:00" → "Jan 15, 2024"
  const formatDate = (dateStr) => {
    return new Date(dateStr).toLocaleDateString('en-IN', {
      year: 'numeric', month: 'short', day: 'numeric'
    });
  };

  return (
    <div className="history-page">
      <div className="container">

        <div className="history-header animate-up">
          <h1 className="history-title">Roadmap History</h1>
          <p className="history-sub">Enter your email to see all your past roadmaps.</p>
        </div>

        {/* Search */}
        <div className="search-box animate-up">
          <input
            type="email"
            className="form-input search-input"
            placeholder="Enter your email address..."
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
          />
          <button className="btn btn-primary" onClick={handleSearch} disabled={loading}>
            {loading ? 'Searching...' : '🔍 Search'}
          </button>
        </div>

        {error && <div className="error-banner">⚠️ {error}</div>}

        {/* Results */}
        {searched && (
          <div className="history-results animate-up">
            {history.length === 0 ? (
              <div className="empty-state">
                No roadmaps found for <strong>{email}</strong>.
                <br />Try generating one first!
              </div>
            ) : (
              <>
                <p className="results-count">
                  Found <strong>{history.length}</strong> roadmap(s) for <strong>{email}</strong>
                </p>
                <div className="history-grid">
                  {history.map((item) => (
                    <div key={item.id} className="history-card">

                      <div className="history-card__header">
                        <h3 className="history-card__role">{item.targetRole}</h3>
                        <span className="history-card__date">{formatDate(item.createdAt)}</span>
                      </div>

                      {/* Progress bar */}
                      <div className="history-card__progress">
                        <div className="progress-bar-wrap">
                          <div
                            className="progress-bar-fill"
                            style={{ width: `${item.completionPercentage}%` }}
                          />
                        </div>
                        <span className="progress-pct">{item.completionPercentage}% complete</span>
                      </div>

                      <div className="history-card__meta">
                        <span>🕐 {item.totalEstimatedHours}h to learn</span>
                      </div>

                      {/* Known skills */}
                      {item.currentSkills && (
                        <div className="history-card__skills">
                          <span className="skills-label">Known:</span>
                          {item.currentSkills.split(', ').slice(0, 4).map(s => (
                            <span key={s} className="skill-tag">{s}</span>
                          ))}
                          {item.currentSkills.split(', ').length > 4 && (
                            <span className="skill-tag skill-tag--more">
                              +{item.currentSkills.split(', ').length - 4} more
                            </span>
                          )}
                        </div>
                      )}

                    </div>
                  ))}
                </div>
              </>
            )}
          </div>
        )}

      </div>
    </div>
  );
}

export default HistoryPage;
