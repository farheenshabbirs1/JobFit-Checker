import { useState } from 'react';
import { createJob } from '../api/client.js';

export default function JobForm({ onCreated }) {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [skillsText, setSkillsText] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  async function handleSubmit(e) {
    e.preventDefault();
    setSubmitting(true);
    setError(null);
    try {
      const requiredSkills = skillsText
        .split(',')
        .map((s) => s.trim())
        .filter(Boolean);
      const job = await createJob({ title, description, requiredSkills });
      setTitle('');
      setDescription('');
      setSkillsText('');
      onCreated(job);
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form className="card" onSubmit={handleSubmit}>
      <h2>Post a job</h2>
      <label>
        Title
        <input value={title} onChange={(e) => setTitle(e.target.value)} required />
      </label>
      <label>
        Description
        <textarea
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          rows={4}
          required
        />
      </label>
      <label>
        Required skills (comma separated)
        <input
          value={skillsText}
          onChange={(e) => setSkillsText(e.target.value)}
          placeholder="Java, Spring Boot, PostgreSQL"
          required
        />
      </label>
      {error && <p className="error">{error}</p>}
      <button type="submit" disabled={submitting}>
        {submitting ? 'Posting…' : 'Post job'}
      </button>
    </form>
  );
}
