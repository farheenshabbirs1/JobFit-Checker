import { useState } from 'react';
import { uploadResume } from '../api/client.js';

export default function ResumeUpload({ jobId, onUploaded }) {
  const [candidateName, setCandidateName] = useState('');
  const [file, setFile] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  async function handleSubmit(e) {
    e.preventDefault();
    if (!file) {
      setError('Choose a resume file first.');
      return;
    }
    setSubmitting(true);
    setError(null);
    try {
      const resume = await uploadResume(jobId, candidateName, file);
      setCandidateName('');
      setFile(null);
      e.target.reset();
      onUploaded(resume);
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form className="card" onSubmit={handleSubmit}>
      <h3>Upload a resume</h3>
      <label>
        Candidate name
        <input value={candidateName} onChange={(e) => setCandidateName(e.target.value)} required />
      </label>
      <label>
        Resume file (PDF or TXT)
        <input
          type="file"
          accept=".pdf,.txt,application/pdf,text/plain"
          onChange={(e) => setFile(e.target.files[0])}
          required
        />
      </label>
      {error && <p className="error">{error}</p>}
      <button type="submit" disabled={submitting}>
        {submitting ? 'Uploading…' : 'Upload'}
      </button>
    </form>
  );
}
