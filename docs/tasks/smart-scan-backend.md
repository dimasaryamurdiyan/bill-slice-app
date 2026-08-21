# Smart Scan backend implementation plan

- Status: Proposed
- Specification: [`docs/specs/smart-scan-backend.md`](../specs/smart-scan-backend.md)
- Branch: `codex/smart-scan-backend`

## Outcome contract

- **Outcome:** A staging Supabase Edge Function implements the exact Smart Scan API with server-only OpenAI parsing, atomic success-only quota, idempotent replay, and strict OCR privacy.
- **Acceptance criteria:** `AC-001`–`AC-008`.
- **Non-goals:** See specification.
- **Verification:** Local function/parser/database/contract/concurrency/privacy tests, deployed staging evidence, gates/review.

## Preconditions

Human-approved Smart Scan backend specification and API contract; sanitized contract fixtures; Supabase CLI/local runtime; isolated staging project and publishable configuration; staging deployment access; server-side OpenAI test secret/budget; clean baseline/draft PR. No production credential is required or allowed.

## Vertical slices

- [ ] `T-001` — Establish backend baseline and sanitized contract fixtures
  - Covers: `AC-008`
  - Result: Local Supabase prerequisites are documented/verified, no private receipt data or secret is present, and exact API examples are available as sanitized backend fixtures.
  - Likely scope: read-only baseline, `supabase/` local config, sanitized fixtures.
  - Verification: baseline commands, fixture/secret inspection, local runtime smoke check.
  - Depends on: none
- [ ] `T-002` — Scaffold the Edge Function boundary test-first
  - Covers: `FR-001`–`FR-003`, `FR-010`; `AC-001`
  - Result: `smart-scan-parse` accepts only the exact method/headers/body and returns sanitized contract envelopes through injected fake seams, without OpenAI or database side effects for rejected input.
  - Likely scope: `supabase/functions/smart-scan-parse`, shared test helpers only when reused.
  - Verification: local HTTP contract/auth/validation tests.
  - Depends on: `T-001`
- [ ] `T-003` — Add minimal versioned schema and atomic quota operations
  - Covers: `FR-006`, `FR-007`; `AC-003`
  - Result: Migrations create only documented usage/request/log state and atomic operations enforce Jakarta month, success-only counting, and five-scan concurrency without exposing tables to mobile writes.
  - Likely scope: `supabase/migrations`, database integration tests.
  - Verification: clean migration, constraint, time-boundary, policy-failure, and fifth/sixth concurrency tests.
  - Depends on: `T-001`
- [ ] `T-004` — Implement the server-only receipt parser adapter
  - Covers: `FR-004`, `FR-005`, `FR-010`; `AC-002`, `AC-006`
  - Result: A small OpenAI adapter uses the server secret and structured schema, maps provider outcomes to internal typed results, validates usable drafts/warnings, and exposes no provider detail or raw text outside request memory.
  - Likely scope: parser/schema files under `supabase/functions/smart-scan-parse`, fake adapter/tests.
  - Verification: fake-adapter success, nullable warning, unusable, malformed, upstream, timeout, and secret/log tests.
  - Depends on: `T-002`
- [ ] `T-005` — Deliver the successful parse and quota vertical slice
  - Covers: `FR-002`, `FR-004`–`FR-007`; `AC-001`–`AC-003`
  - Result: A valid request below quota produces the exact editable draft/warnings/quota response and atomically consumes one scan only after usable schema validation.
  - Likely scope: function orchestration, parser/quota seams, integration tests.
  - Verification: end-to-end local success and failure-before-acceptance tests.
  - Depends on: `T-003`, `T-004`
- [ ] `T-006` — Implement idempotency, replay, and all typed failures
  - Covers: `FR-008`, `FR-010`; `AC-001`, `AC-004`
  - Result: Same-ID in-progress/replay/conflict/expiry and all API error codes behave exactly, failed requests may retry, and concurrent requests never double-call successfully or exceed quota.
  - Likely scope: request state/orchestration, database operations, contract/concurrency tests.
  - Verification: complete error matrix plus duplicate and different-ID concurrency tests.
  - Depends on: `T-005`
- [ ] `T-007` — Enforce retention, cleanup, privacy, and observability
  - Covers: `FR-009`, `FR-012`, `FR-013`; `AC-005`, `AC-006`
  - Result: Raw OCR/request bodies never persist or log, structured replay data clears within one hour, the charge marker survives, cleanup retries safely, and only allowed sanitized metadata is observable.
  - Likely scope: cleanup function/schedule or database operation, log mapping, privacy tests/docs.
  - Verification: time-controlled storage/cleanup tests plus database/log/artifact/secret inspection.
  - Depends on: `T-006`
- [ ] `T-008` — Deploy and verify the isolated staging backend
  - Covers: `FR-011`; `AC-006`, `AC-007`
  - Result: Migrations/function/secrets are applied only to staging and the deployed endpoint passes Android-compatible contract, quota, replay, auth, dependency-failure, and privacy scenarios.
  - Likely scope: staging deployment/evidence; no production mutation.
  - Verification: exact deployed staging contract suite and dashboard/log/storage inspection.
  - Depends on: `T-007`
- [ ] `T-009` — Run backend gates and reconcile documentation
  - Covers: `AC-001`–`AC-008`
  - Result: Backend formatting, type checks, tests, migration verification, staging tests, and repository documentation agree with implemented behavior.
  - Likely scope: verification and narrow architecture/contract corrections only if behavior is unchanged.
  - Verification: all documented backend/repository commands and diff/link/secret checks.
  - Depends on: `T-008`
- [ ] `T-010` — Review the complete backend slice and record acceptance evidence
  - Covers: `AC-001`–`AC-008`
  - Result: A fresh reviewer reports no unresolved blocking contract, quota, concurrency, privacy, secret, migration, rollback, or standards finding; every criterion has exact evidence before human review.
  - Likely scope: complete merge-base diff and PR evidence.
  - Verification: fresh two-axis review, affected reruns, required CI on final head.
  - Depends on: `T-009`

## Risks and rollback

Risks are OCR leakage, secret exposure, double charging, quota races, retained structured receipt data, provider cost, and staging/production confusion. Function deployment can roll back to the last known version; database changes use forward, non-destructive migrations. Disable the staging function or OpenAI secret to stop parsing while Android Manual Entry remains operational. Never roll back by deleting usage/idempotency evidence or applying destructive SQL.

## Completion checklist

- [ ] `AC-001`–`AC-008` evidenced.
- [ ] Exact API contract passes locally and on staging with no production mutation.
- [ ] Only valid usable parses consume quota, once per install-scoped request ID, under concurrent load.
- [ ] Image/raw OCR/secrets are absent from persistence, logs, fixtures, artifacts, and PR evidence.
- [ ] One-hour replay cleanup preserves the no-double-charge marker.
- [ ] Backend gates, migrations, staging integration, CI, privacy/security review, and complete-diff review pass.
