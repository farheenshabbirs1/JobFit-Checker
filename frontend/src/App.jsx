import { useCallback, useEffect, useState } from 'react';
import JobForm from './components/JobForm.jsx';
import JobList from './components/JobList.jsx';
import ResumeUpload from './components/ResumeUpload.jsx';
import ResumeStatus from './components/ResumeStatus.jsx';
import { listJobs, listResumesForJob } from './api/client.js';

export default function App() {
  const [jobs, setJobs] = useState([]);
  const [selectedJobId, setSelectedJobId] = useState(null);
  const [resumeIds, setResumeIds] = useState([]);
  const [error, setError] = useState(null);

  const refreshJobs = useCallback(async () => {
    try {
      const data = await listJobs();
      setJobs(data);
      if (!selectedJobId && data.length > 0) {
        setSelectedJobId(data[0].id);
      }
    } catch (err) {
      setError(err.message);
    }
  }, [selectedJobId]);

  const refreshResumes = useCallback(async (jobId) => {
    if (!jobId) return;
    try {
      const resumes = await listResumesForJob(jobId);
      setResumeIds(resumes.map((r) => r.id));
    } catch (err) {
      setError(err.message);
    }
  }, []);

  useEffect(() => {
    refreshJobs();
  }, [refreshJobs]);

  useEffect(() => {
    refreshResumes(selectedJobId);
  }, [selectedJobId, refreshResumes]);

  function handleJobCreated(job) {
    setJobs((prev) => [job, ...prev]);
    setSelectedJobId(job.id);
  }

  function handleResumeUploaded(resume) {
    setResumeIds((prev) => [resume.id, ...prev]);
  }

  return (
    <div className="app">
      <header>
        <h1>JobFit Checker</h1>
        <p className="muted">
          Post a job, upload resumes against it, and watch each one move through parsing and
          analysis asynchronously.
        </p>
      </header>

      {error && <p className="error">{error}</p>}

      <div className="layout">
        <aside>
          <JobForm onCreated={handleJobCreated} />
          <h2>Jobs</h2>
          <JobList jobs={jobs} selectedJobId={selectedJobId} onSelect={setSelectedJobId} />
        </aside>

        <main>
          {selectedJobId ? (
            <>
              <ResumeUpload jobId={selectedJobId} onUploaded={handleResumeUploaded} />
              <h2>Resumes</h2>
              {resumeIds.length === 0 && <p className="muted">No resumes uploaded for this job yet.</p>}
              <div className="resume-list">
                {resumeIds.map((id) => (
                  <ResumeStatus key={id} resumeId={id} />
                ))}
              </div>
            </>
          ) : (
            <p className="muted">Post or select a job to get started.</p>
          )}
        </main>
      </div>
    </div>
  );
}
