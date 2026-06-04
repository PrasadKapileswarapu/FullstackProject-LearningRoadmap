import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getAllRoles, getSkillsByRole, generateRoadmap } from '../services/api';
import './GeneratePage.css';

/**
 * GENERATE PAGE — Multi-step form to create a roadmap
 *
 * REACT HOOKS USED:
 *  useState  → manage form data, loading states, API data
 *  useEffect → fetch roles when page loads, fetch skills when role changes
 *  useNavigate → redirect to /result after successful API call
 *
 * FORM FLOW:
 *  Step 1: Enter name + email
 *  Step 2: Select target role
 *  Step 3: Check off skills you know → Submit
 */
function GeneratePage() {
  const navigate = useNavigate();

  // ---- STATE ----
  const [step, setStep] = useState(1);                  // Current step (1, 2, or 3)
  const [roles, setRoles] = useState([]);               // Roles from API
  const [skills, setSkills] = useState([]);             // Skills for selected role
  const [loading, setLoading] = useState(false);        // API call in progress
  const [error, setError] = useState('');               // Error message

  // Form data — all collected across 3 steps
  const [formData, setFormData] = useState({
    userName: '',
    userEmail: '',
    targetRoleId: '',
    currentSkills: [],   // Array of skill names the user knows
  });

  // ---- EFFECTS ----

  // Fetch all roles when the page first loads
  useEffect(() => {
    const fetchRoles = async () => {
      try {
        const response = await getAllRoles();
        setRoles(response.data); // response.data is the array from Spring Boot
      } catch (err) {
        setError('Could not load roles. Is the backend running?');
      }
    };
    fetchRoles();
  }, []); // [] = run once on mount

  // Fetch skills whenever user picks a different role
  useEffect(() => {
    if (!formData.targetRoleId) return;

    const fetchSkills = async () => {
      try {
        const response = await getSkillsByRole(formData.targetRoleId);
        setSkills(response.data);
        setFormData(prev => ({ ...prev, currentSkills: [] })); // reset skill choices
      } catch (err) {
        setError('Could not load skills for this role.');
      }
    };
    fetchSkills();
  }, [formData.targetRoleId]); // runs whenever targetRoleId changes

  // ---- HANDLERS ----

  // Handle text input changes
  const handleInputChange = (e) => {
    setFormData(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  // Toggle a skill checkbox on/off
  const toggleSkill = (skillName) => {
    setFormData(prev => {
      const already = prev.currentSkills.includes(skillName);
      return {
        ...prev,
        currentSkills: already
          ? prev.currentSkills.filter(s => s !== skillName)  // remove
          : [...prev.currentSkills, skillName],               // add
      };
    });
  };

  // Step navigation
  const nextStep = () => { setError(''); setStep(s => s + 1); };
  const prevStep = () => { setError(''); setStep(s => s - 1); };

  // Validate step 1 fields
  const isStep1Valid = () =>
    formData.userName.trim() !== '' && formData.userEmail.includes('@');

  // Submit to Spring Boot
  const handleSubmit = async () => {
    setLoading(true);
    setError('');
    try {
      const payload = {
        userName: formData.userName,
        userEmail: formData.userEmail,
        targetRoleId: parseInt(formData.targetRoleId),
        currentSkills: formData.currentSkills,
      };
      const response = await generateRoadmap(payload);

      // Store result in sessionStorage so ResultPage can read it
      sessionStorage.setItem('roadmapResult', JSON.stringify(response.data));

      navigate('/result'); // Go to result page
    } catch (err) {
      setError('Failed to generate roadmap. Please try again.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  // Group skills by category for easier reading
  const skillsByCategory = skills.reduce((acc, skill) => {
    if (!acc[skill.category]) acc[skill.category] = [];
    acc[skill.category].push(skill);
    return acc;
  }, {});

  // ---- RENDER ----
  return (
    <div className="generate-page">
      <div className="generate-container">

        {/* Progress bar */}
        <div className="progress-bar">
          {[1, 2, 3].map(n => (
            <div key={n} className={`progress-step ${step >= n ? 'active' : ''} ${step > n ? 'done' : ''}`}>
              <div className="progress-dot">{step > n ? '✓' : n}</div>
              <span className="progress-label">
                {n === 1 ? 'Your Info' : n === 2 ? 'Target Role' : 'Your Skills'}
              </span>
            </div>
          ))}
        </div>

        {/* Error message */}
        {error && <div className="error-banner">⚠️ {error}</div>}

        {/* ---- STEP 1: Name & Email ---- */}
        {step === 1 && (
          <div className="form-step animate-up">
            <h2 className="form-title">Let's get to know you</h2>
            <p className="form-subtitle">We'll save your roadmap so you can track progress.</p>

            <div className="form-group">
              <label className="form-label">Your Name</label>
              <input
                type="text"
                name="userName"
                className="form-input"
                placeholder="e.g. Rahul Sharma"
                value={formData.userName}
                onChange={handleInputChange}
              />
            </div>

            <div className="form-group">
              <label className="form-label">Email Address</label>
              <input
                type="email"
                name="userEmail"
                className="form-input"
                placeholder="e.g. rahul@email.com"
                value={formData.userEmail}
                onChange={handleInputChange}
              />
            </div>

            <button
              className="btn btn-primary full-width"
              onClick={nextStep}
              disabled={!isStep1Valid()}
            >
              Continue →
            </button>
          </div>
        )}

        {/* ---- STEP 2: Target Role ---- */}
        {step === 2 && (
          <div className="form-step animate-up">
            <h2 className="form-title">What role do you want?</h2>
            <p className="form-subtitle">Pick the job title you're aiming for.</p>

            <div className="roles-grid">
              {roles.map(role => (
                <div
                  key={role.id}
                  className={`role-card ${formData.targetRoleId === String(role.id) ? 'selected' : ''}`}
                  onClick={() => setFormData(prev => ({ ...prev, targetRoleId: String(role.id) }))}
                >
                  <div className="role-card__name">{role.name}</div>
                  <div className="role-card__category">{role.category}</div>
                  <p className="role-card__desc">{role.description}</p>
                </div>
              ))}
            </div>

            <div className="form-nav">
              <button className="btn btn-outline" onClick={prevStep}>← Back</button>
              <button
                className="btn btn-primary"
                onClick={nextStep}
                disabled={!formData.targetRoleId}
              >
                Continue →
              </button>
            </div>
          </div>
        )}

        {/* ---- STEP 3: Current Skills ---- */}
        {step === 3 && (
          <div className="form-step animate-up">
            <h2 className="form-title">What do you already know?</h2>
            <p className="form-subtitle">
              Check all skills you're comfortable with.
              <span className="selected-count"> ({formData.currentSkills.length} selected)</span>
            </p>

            {Object.entries(skillsByCategory).map(([category, categorySkills]) => (
              <div key={category} className="skill-group">
                <h4 className="skill-group__title">{category}</h4>
                <div className="skill-checkboxes">
                  {categorySkills.map(skill => (
                    <label
                      key={skill.id}
                      className={`skill-checkbox ${formData.currentSkills.includes(skill.name) ? 'checked' : ''}`}
                    >
                      <input
                        type="checkbox"
                        checked={formData.currentSkills.includes(skill.name)}
                        onChange={() => toggleSkill(skill.name)}
                      />
                      <span className="skill-checkbox__name">{skill.name}</span>
                      <span className="skill-checkbox__hours">{skill.estimatedHours}h</span>
                    </label>
                  ))}
                </div>
              </div>
            ))}

            <div className="form-nav">
              <button className="btn btn-outline" onClick={prevStep}>← Back</button>
              <button
                className="btn btn-primary"
                onClick={handleSubmit}
                disabled={loading}
              >
                {loading ? '⏳ Generating...' : '🗺️ Generate Roadmap'}
              </button>
            </div>
          </div>
        )}

      </div>
    </div>
  );
}

export default GeneratePage;
