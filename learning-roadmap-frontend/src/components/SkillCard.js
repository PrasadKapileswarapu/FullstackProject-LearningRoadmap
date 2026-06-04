import React from 'react';
import './SkillCard.css';

/**
 * SKILL CARD — Reusable component to display one skill
 *
 * Props:
 *  skill       → skill object from API response
 *  alreadyKnown → boolean: green "Known" badge or priority badge
 *
 * This is a great example of a REUSABLE COMPONENT in React.
 * One component, used in both "Skills You Know" and "Skills to Learn" sections.
 */
function SkillCard({ skill }) {
  // Determine which badge CSS class to use
  const getBadgeClass = () => {
    if (skill.alreadyKnown) return 'badge badge-known';
    switch (skill.priority) {
      case 1: return 'badge badge-must';
      case 2: return 'badge badge-should';
      case 3: return 'badge badge-nice';
      default: return 'badge';
    }
  };

  const getBadgeLabel = () => {
    if (skill.alreadyKnown) return '✓ Known';
    return skill.priorityLabel;
  };

  return (
    <div className={`skill-card ${skill.alreadyKnown ? 'skill-card--known' : ''}`}>

      {/* Top row: name + badge */}
      <div className="skill-card__header">
        <h4 className="skill-card__name">{skill.name}</h4>
        <span className={getBadgeClass()}>{getBadgeLabel()}</span>
      </div>

      {/* Category tag */}
      <p className="skill-card__category">{skill.category}</p>

      {/* Description */}
      <p className="skill-card__desc">{skill.description}</p>

      {/* Footer: hours + resource link */}
      <div className="skill-card__footer">
        <span className="skill-card__hours">
          🕐 {skill.estimatedHours}h to learn
        </span>
        {skill.resourceUrl && !skill.alreadyKnown && (
          <a
            href={skill.resourceUrl}
            target="_blank"
            rel="noreferrer"
            className="skill-card__link"
          >
            Learn →
          </a>
        )}
      </div>

    </div>
  );
}

export default SkillCard;
