import { useEffect, useState } from 'react';
import { getAnalysis, getResume } from '../api/client.js';

const TERMINAL_STATUSES = new Set(['DONE', 'FAILED']);
const POLL_INTERVAL_MS = 2000;

const STATUS_LABELS = {
  UPLOADED: 'Queued',
  PARSING: 'Parsing resume…',
  PARSED: 'Parsed, queued for analysis…',
  ANALYZING: 'Analyzing fit…',
  DONE: 'Done',
  FAILED: 'Failed',
};

/**
 * Polls one resume's status until it reaches a terminal state, then fetches and shows the
 * analysis. This is the client-side mirror of the async pipeline: nothing pushes updates to
 * the browser, so polling GET /api/resumes/{id} is how the multi-stage, event-driven backend
 * becomes visible as a simple progress indicator.
 */
export default function ResumeStatus({ resumeId }) {
  const [resume, setResume] = useState(null);
  const [analysis, setAnalysis] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    let cancelled = false;
    let timer;

    async function poll() {
      try {
        const latest = await getResume(resumeId);
        if (cancelled) return;
        setResume(latest);

        if (latest.status === 'DONE') {
          const result = await getAnalysis(resumeId);
          if (!cancelled) setAnalysis(result);
        }
        if (!cancelled && !TERMINAL_STATUSES.has(latest.status)) {
          timer = setTimeout(poll, POLL_INTERVAL_MS);
        }
      } catch (err) {
        if (!cancelled) setError(err.message);
      }
    }

    poll();
    return () => {
      cancelled = true;
      clearTimeout(timer);
    };
  }, [resumeId]);

  if (error) return <p className="error">{error}</p>;
  if (!resume) return <p className="muted">Loading…</p>;

  return (
    <div className="resume-status">
      <div className="resume-status-header">
        <strong>{resume.candidateName}</strong>
        <span className={`status-badge status-${resume.status.toLowerCase()}`}>
          {STATUS_LABELS[resume.status] ?? resume.status}
        </span>
      </div>
      {resume.status === 'FAILED' && resume.failureReason && (
        <p className="error">{resume.failureReason}</p>
      )}
      {analysis && (
        <div className="analysis">
          <p>
            Qualification score: <strong>{analysis.qualificationScore}%</strong>
          </p>
          {analysis.matchedSkills.length > 0 && (
            <p>Matched: {analysis.matchedSkills.join(', ')}</p>
          )}
          {analysis.missingSkills.length > 0 && (
            <p>Missing: {analysis.missingSkills.join(', ')}</p>
          )}
          <p className="suggestions">{analysis.suggestions}</p>
        </div>
      )}
    </div>
  );
}
