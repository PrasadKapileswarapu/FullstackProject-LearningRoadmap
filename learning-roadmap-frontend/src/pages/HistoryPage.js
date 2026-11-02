import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { getUserHistory, getErrorMessage } from '../services/api';
import './HistoryPage.css';

/**
 * HISTORY PAGE — Your past roadmaps
 *
 * V2 (JWT): no email search any more. The page is login-protected and calls
 * GET /api/roadmap/history, and the backend returns only the logged-in user's
 * roadmaps (it reads the email from the token).
 *
 * Each item: { roadmapId, targetRole, currentSkills[], skillsToLearn[],
 *              totalEstimatedHours, completionPercentage, createdAt }
 */
function HistoryPage() {
  const { user } = useAuth();
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError]     = useState('');

  // Load history once when the page opens
  useEffect(() => {
    const fetchHistory = async () => {
      try {
        const response = await getUserHistory();
        setHistory(response.data);
      } catch (err) {
        // A 401 is handled globally (user is sent to /login); show other errors here
        setError(getErrorMessage(err, 'Could not fetch history.'));
      } finally {
        setLoading(false);
      }
    };
    fetchHistory();
  }, []);

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
          <p className="history-sub">
            All roadmaps you generated{user?.email ? <> as <strong>{user.email}</strong></> : null}.
          </p>
        </div>

        {error && <div className="error-banner">⚠️ {error}</div>}

        {loading && <div className="empty-state">Loading your roadmaps...</div>}

        {/* Results */}
        {!loading && !error && (
          <div className="history-results animate-up">
            {history.length === 0 ? (
              <div className="empty-state">
                You haven't generated any roadmaps yet.
                <br /><Link to="/generate">Generate your first one →</Link>
              </div>
            ) : (
              <>
                <p className="results-count">
                  Found <strong>{history.length}</strong> roadmap(s)
                </p>
                <div className="history-grid">
                  {history.map((item) => (
                    <div key={item.roadmapId} className="history-card">

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

                      {/* Known skills (the backend now sends an array, not a comma-separated string) */}
                      {item.currentSkills && item.currentSkills.length > 0 && (
                        <div className="history-card__skills">
                          <span className="skills-label">Known:</span>
                          {item.currentSkills.slice(0, 4).map(s => (
                            <span key={s} className="skill-tag">{s}</span>
                          ))}
                          {item.currentSkills.length > 4 && (
                            <span className="skill-tag skill-tag--more">
                              +{item.currentSkills.length - 4} more
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
