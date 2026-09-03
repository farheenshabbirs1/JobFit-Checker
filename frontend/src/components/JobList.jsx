export default function JobList({ jobs, selectedJobId, onSelect }) {
  if (jobs.length === 0) {
    return <p className="muted">No jobs posted yet.</p>;
  }
  return (
    <ul className="job-list">
      {jobs.map((job) => (
        <li key={job.id}>
          <button
            className={job.id === selectedJobId ? 'job-item selected' : 'job-item'}
            onClick={() => onSelect(job.id)}
          >
            <strong>{job.title}</strong>
            <span className="muted">{job.requiredSkills.join(', ')}</span>
          </button>
        </li>
      ))}
    </ul>
  );
}
