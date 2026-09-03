# JobFit Checker

AI-driven job qualification and skill-improvement suggestions, built as three independently
deployable Spring Boot microservices around an event-driven, multi-stage resume pipeline:

- **api-service** -- the only service the React frontend talks to. Job posting CRUD, resume
  upload, and status/result lookups.
- **parsing-service** -- consumes uploaded resumes, extracts raw text (PDF or plain text) and
  then structured fields (skills, years of experience, education).
- **analysis-service** -- consumes parsed resumes, scores skill coverage against the job
  posting, and generates improvement suggestions.

Services never call each other directly. They're decoupled by a Postgres-backed
**transactional outbox**, which is also what this project uses instead of a cloud message
queue -- see "Design decisions" below for why, and what that trades away.

## Architecture

```
                 ┌──────────────┐        resume.uploaded        ┌──────────────────┐
  React  ──HTTP──▶  api-service  │───────────outbox─────────────▶  parsing-service   │
frontend         └──────────────┘                                └──────────────────┘
                        │                                                  │
                        │ writes Resume row                    resume.parsed (outbox)
                        │ + file bytes (BlobStorage)                       │
                        ▼                                                  ▼
                 ┌──────────────────────── Postgres ─────────────────────────────┐
                 │  jobs, resumes, parsed_resumes, analysis_results, event_outbox │
                 └──────────────────────────────────────────────────────────────┘
                                                                             ▲
                                                                             │
                                                              ┌──────────────────────┐
                                                              │  analysis-service      │
                                                              │  (consumes resume.parsed,│
                                                              │   writes analysis_results)│
                                                              └──────────────────────┘
```

A resume upload writes a `Resume` row, stores the file, and inserts a `resume.uploaded` outbox
event in one transaction. parsing-service polls for that event, extracts text and fields, writes
a `ParsedResume` row, and inserts `resume.parsed`. analysis-service polls for that, scores the
candidate against the job's required skills, and writes the final `AnalysisResult`. The
frontend never sees any of this machinery -- it just polls `GET /api/resumes/{id}` until
`status` reaches `DONE` or `FAILED`, then fetches `GET /api/resumes/{id}/analysis`.

## Design decisions: what replaced the cloud services, and why

The original design (see the resume bullet this project implements) called for AWS S3 and SQS.
This build deliberately does not wire in AWS -- everything runs on a JDK, Postgres, and Node,
no cloud account or credentials required to run it end to end. The event-driven,
multi-stage, independently-scalable *architecture* the bullet describes is still fully real;
only the specific AWS services are swapped for equivalents that demonstrate the same
mechanisms without a cloud dependency:

- **SQS → Postgres transactional outbox** (`event_outbox` table, `SELECT ... FOR UPDATE SKIP
  LOCKED`). A producer inserts an outbox row in the same transaction as its business write, so
  an event can never be silently lost between the write committing and the event becoming
  visible. A consumer claims a row with `FOR UPDATE SKIP LOCKED`, which is what lets several
  instances of the same service run concurrently and safely split the backlog -- this is what
  "independent horizontal scaling" means in this codebase: start a second parsing-service pod,
  and it starts claiming rows the first hasn't gotten to, with no coordination beyond the
  database. The claim/complete logic is `common`'s `OutboxPoller`.
- **S3 → a pluggable `BlobStorage` interface**, defaulting to a local/shared-volume
  implementation (`LocalFileBlobStorage`). api-service writes through it on upload;
  parsing-service reads through it before extracting text. Neither depends on which
  implementation is wired up -- an S3-backed (or Azure Blob-backed) implementation is a
  drop-in swap behind the same interface, not a rewrite, if a real object store is ever wanted.

Everything else in the bullet is implemented as described: three independently deployable
Spring Boot services, a React frontend, PostgreSQL, Docker, GitHub Actions CI, and a Terraform
+ Kubernetes deployment targeting Azure Kubernetes Service with a blue-green rollout pattern.

## Repository layout

```
common/             Shared JPA entities, repositories, the outbox publisher/poller, BlobStorage
api-service/        Job CRUD, resume upload, status/analysis read endpoints
parsing-service/    Text + field extraction, resume.uploaded consumer
analysis-service/   Skill-gap scoring + suggestions, resume.parsed consumer
frontend/           React (Vite) single-page app
db/schema.sql       The schema all three services share (see "Known limitations")
infra/
  docker-compose.yml      Postgres + all four services, one command to run locally
  terraform/              Reference Azure infra (AKS + ACR) -- not applied, see its README
  k8s/                    Plain Kubernetes manifests + a blue-green rollout example
.github/workflows/ci.yml  Build + test on push, no cloud credentials required
```

## Running it locally

```bash
cd infra
docker compose up --build
```

This starts Postgres (seeded from `db/schema.sql`), all three backend services, and the
frontend, wired together. Open http://localhost:8080, post a job, upload a resume against it,
and watch its status move from `UPLOADED` → `PARSING` → `PARSED` → `ANALYZING` → `DONE` as the
two background pollers pick it up.

Running services individually (e.g. from an IDE) instead of via Compose: each module has its
own `application.yml` with sane localhost defaults (`JOBFIT_DB_URL`, `JOBFIT_DB_USER`,
`JOBFIT_DB_PASSWORD`, `JOBFIT_STORAGE_ROOT` env vars override them). You'll need a local
Postgres with `db/schema.sql` applied, and api-service and parsing-service need to agree on
`JOBFIT_STORAGE_ROOT` since they share the uploaded files through it.

For the frontend alone: `cd frontend && npm install && npm run dev` (proxies `/api` to
`localhost:8081` by default; override with `VITE_API_PROXY_TARGET`).

## Known limitations

- **Shared schema, not database-per-service.** All three services point at the same Postgres
  database and schema (`db/schema.sql`). A stricter microservices boundary would give each
  service its own schema/database and have them talk only through the outbox and each other's
  APIs, never a shared table. This project takes the simpler shared-schema shortcut
  deliberately, since the services still only communicate through the outbox in practice (no
  service does a cross-service JOIN); splitting the schema later doesn't change any application
  code, just the connection strings and migration scripts.
- **This sandbox could not compile-verify the Spring Boot services.** Maven Central was not
  reachable from the environment this was built in, so `common`, `api-service`,
  `parsing-service`, and `analysis-service` were written and manually reviewed but not run
  through `mvn test` or `mvn package` here. Run `mvn -B verify` yourself (or let the CI
  workflow do it on push) before relying on this code. By contrast: the transactional-outbox
  claim mechanism (`SELECT ... FOR UPDATE SKIP LOCKED`) *was* verified for real against a live
  Postgres instance with raw JDBC and 8 concurrent consumers before being encoded into the
  Spring code, and the frontend was built and `npm run build`-verified in full.
- **Skill/field extraction is a fixed keyword vocabulary, not NLP.** `KeywordSkillExtractor`
  matches resume text against a hardcoded list of common tech skills; years-of-experience and
  education are small regexes. Both are behind interfaces (`SkillExtractor`,
  `QualificationScorer`, `SuggestionGenerator`) specifically so an LLM-backed implementation
  can be swapped in later without touching the pipeline around them.
- **Terraform is reference-only.** It describes the AKS + ACR infrastructure the bullet calls
  for but has not been applied against a real Azure subscription from here -- see
  `infra/terraform/README.md`.
- **Blue-green is illustrated, not wired into CI.** `infra/k8s/blue-green-example.yaml` shows
  the two-Deployment-plus-selector-flip pattern for api-service; the CI workflow builds and
  tests but doesn't deploy, since there's no cluster or registry connected to this project to
  deploy to.

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
