// Thin fetch wrapper around api-service's REST endpoints. All of these hit api-service only --
// the frontend never talks to parsing-service or analysis-service directly, since the whole
// point of the outbox-driven pipeline is that they're invisible to the client.
const BASE = '/api';

async function request(path, options) {
  const res = await fetch(`${BASE}${path}`, {
    headers: options?.body instanceof FormData ? undefined : { 'Content-Type': 'application/json' },
    ...options,
  });
  if (!res.ok) {
    let message = `${res.status} ${res.statusText}`;
    try {
      const body = await res.json();
      if (body?.message) message = body.message;
    } catch {
      // response wasn't JSON -- fall back to the status line
    }
    throw new Error(message);
  }
  if (res.status === 204) return null;
  return res.json();
}

export function listJobs() {
  return request('/jobs');
}

export function getJob(jobId) {
  return request(`/jobs/${jobId}`);
}

export function createJob(job) {
  return request('/jobs', { method: 'POST', body: JSON.stringify(job) });
}

export function listResumesForJob(jobId) {
  return request(`/jobs/${jobId}/resumes`);
}

export function getResume(resumeId) {
  return request(`/resumes/${resumeId}`);
}

export function getAnalysis(resumeId) {
  return request(`/resumes/${resumeId}/analysis`);
}

export function uploadResume(jobId, candidateName, file) {
  const form = new FormData();
  form.append('candidateName', candidateName);
  form.append('file', file);
  return request(`/jobs/${jobId}/resumes`, { method: 'POST', body: form });
}
