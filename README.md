# JobFit Checker

AI-driven job qualification and skill-improvement suggestions, built as three independently
deployable Spring Boot microservices around an event-driven, multi-stage resume pipeline:

- **api-service** -- the only service the React frontend talks to. Job posting CRUD, resume
  upload, and status/result lookups.
- **parsing-service** -- consumes uploaded resumes, extracts raw text (PDF or plain text) and
  then structured fields (skills, years of experience, education).
- **analysis-service** -- consumes parsed resumes, scores skill coverage against the job
  posting, and generates improvement suggestions.

## Running it locally

```bash
cd infra
docker compose up --build
```

## API summary

| Method | Path | Description |
| --- | --- | --- |
| POST | `/api/jobs` | Create a job posting |
| GET | `/api/jobs` | List job postings |
| GET | `/api/jobs/{id}` | Get one job posting |
| POST | `/api/jobs/{jobId}/resumes` | Upload a resume (`candidateName`, `file` multipart fields) |
| GET | `/api/jobs/{jobId}/resumes` | List resumes uploaded against a job |
| GET | `/api/resumes/{id}` | Resume status (poll this) |
| GET | `/api/resumes/{id}/analysis` | Qualification score, matched/missing skills, suggestions |
