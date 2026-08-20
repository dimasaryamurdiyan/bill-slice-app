# Smart Scan backend

- Status: Draft
- Owner: Human
- Last updated: 2026-08-20

## Context

The approved Smart Scan product flow requires a server boundary for quota enforcement and receipt-text parsing. This slice owns the Supabase Edge Function, database migrations, OpenAI adapter, idempotency/replay behavior, privacy controls, and staging deployment behind the exact [`Smart Scan API contract`](../contracts/smart-scan-api.md). Backend source lives in this repository under `supabase/`; Android OCR, networking, session state, and Review UI remain owned by the existing [`Smart Scan` specification](smart-scan.md) and implementation plan.

## Outcome

A staging Supabase endpoint accepts only the documented OCR-text request, returns the exact typed draft/quota/error contract, consumes Free quota once only for a structurally valid usable parse, safely replays successful requests, and retains neither receipt images nor raw OCR text.

## User scenarios

- Given an allowed request below quota, the backend returns a schema-valid editable draft and post-consumption quota snapshot, then counts exactly one successful scan.
- Given OCR, parser, policy, dependency, or schema failure, the backend returns the documented typed error and consumes no quota.
- Given the client retries the same request after losing a response, the backend replays the same successful result within one hour without another OpenAI call or quota use.
- Given concurrent requests compete for the fifth Free allowance, at most five successful usable requests are accepted for the install/month.
- Given missing/invalid access or malformed input, the backend rejects it without calling OpenAI, persisting OCR, or consuming quota.

## Functional requirements

- `FR-001` — Backend source, local configuration, migrations, functions, and sanitized tests live under `supabase/` in this repository; Android modules never depend on backend source.
- `FR-002` — `smart-scan-parse` implements the exact transport, request, success, warning, quota, error, and status-code behavior in [`docs/contracts/smart-scan-api.md`](../contracts/smart-scan-api.md).
- `FR-003` — The function accepts only HTTPS JSON carrying the environment publishable key and exact allowed request fields; malformed, missing, null, wrongly typed, or unknown fields fail before OpenAI or quota mutation.
- `FR-004` — OpenAI is called only server-side through a small parser seam using a server secret and schema-constrained structured output; provider types, messages, prompts, and raw exceptions never cross the API boundary.
- `FR-005` — Parser output is independently validated against the API schema. Only a structurally valid draft with at least one usable item is successful; nullable/uncertain fields require documented warning codes and are never silently replaced with zero.
- `FR-006` — Versioned migrations create the minimal quota, request/idempotency, and sanitized log records documented by the product/API contracts, with constraints and atomic database behavior that prevent duplicate charging and quota overflow.
- `FR-007` — Free policy allows exactly five successful usable parses per server-controlled `Asia/Jakarta` calendar month and returns the next Jakarta month boundary; unavailable quota/entitlement state fails closed.
- `FR-008` — Idempotency is scoped to `(install_id, request_id)`: different request content conflicts, concurrent duplicates cannot double-process successfully, successful results replay for one hour, expired results return `REPLAY_EXPIRED`, and failed requests may retry without quota use.
- `FR-009` — Receipt images are never accepted. Request bodies/raw OCR are excluded from persistence and all logs; raw OCR exists only in request memory. Successful structured result data is cleared within one hour while the minimal no-double-charge marker remains.
- `FR-010` — Every backend failure maps to the documented sanitized error envelope with correct retryability and quota behavior; no failure returns provider, database, stack-trace, secret, or raw OCR text.
- `FR-011` — Staging and production Supabase projects use separate configuration, databases, functions, logs, secrets, and OpenAI budgets. This slice deploys and verifies staging only; no secret enters source, generated artifacts, test fixtures, or PR evidence.
- `FR-012` — Cleanup of expired structured replay data is deterministic, retry-safe, observable through sanitized metrics, and cannot delete the minimal marker required to prevent another charge.
- `FR-013` — Minimal observability contains only documented hashes/codes/timing/model/quota metadata; logs support contract and failure diagnosis without reconstructing receipt content.

## Business rules and invariants

The API contract is the wire source of truth. A Smart Scan is charged only after a usable response is atomically accepted, at most once per install-scoped request ID. Free quota uses server time and Jakarta calendar months. Manual fallback remains an Android responsibility and must remain available when any backend outcome fails. The backend parses an editable draft; it never calculates the authoritative bill split.

## States and failure behavior

Cover request received, rejected, policy checking, parser working, schema validating, quota acceptance, successful response, replay available, replay expired, request in progress/conflict, quota exhausted, rate limited, parser unusable/invalid/upstream failure/timeout, policy unavailable, service unavailable, internal failure, cleanup success/failure, and deployment misconfiguration. Failures before atomic acceptance consume no quota; ambiguous client delivery is resolved by same-ID replay.

## Security and privacy requirements

Use a Supabase publishable key only as public project access for the accountless closed test; it is not user authentication and `installId` is not trusted identity. Function code validates allowed publishable-key access with platform JWT verification disabled as required by the API contract. OpenAI and Supabase secret/service-role keys stay in environment secret storage. Database access uses least privilege, tables are not directly writable by the mobile client, and server responses/logs never expose secrets or raw provider/database errors.

Production abuse prevention, device attestation, and hardened entitlement verification require a later human-approved contract before public release.

## Data and boundary contracts

`supabase/functions/smart-scan-parse` owns HTTP validation/orchestration and depends inward on small parser/quota/idempotency seams. Versioned SQL under `supabase/migrations` owns `scan_usage`, `smart_scan_requests`, and `smart_scan_logs` plus atomic operations and retention behavior. Sanitized backend fixtures mirror the API contract examples. Android and backend meet only through [`docs/contracts/smart-scan-api.md`](../contracts/smart-scan-api.md); no generated backend/client type dependency is required for v0.1.

## Acceptance criteria

- `AC-001` — Covers `FR-001`–`FR-003`, `FR-010`: Local contract tests prove exact request validation, publishable-key behavior, success/error envelopes, status codes, retryability, and rejection before side effects.
- `AC-002` — Covers `FR-004`, `FR-005`: Parser tests with a fake OpenAI adapter prove schema-valid success/warnings and typed unusable, malformed, upstream, and timeout outcomes without leaking provider details.
- `AC-003` — Covers `FR-006`, `FR-007`: Migration/database tests prove server-controlled Jakarta reset, success-only counting, atomic five-scan enforcement, policy failure behavior, and safe schema application from a clean database.
- `AC-004` — Covers `FR-006`, `FR-008`: Concurrent integration tests prove same-ID replay/conflict/in-progress/expiry behavior and different-ID fifth/sixth request serialization without double charge.
- `AC-005` — Covers `FR-009`, `FR-012`, `FR-013`: Storage, cleanup, and log inspection prove no image/raw OCR retention, structured result deletion within one hour, marker preservation, retry-safe cleanup, and sanitized observability.
- `AC-006` — Covers `FR-011`: Secret/config inspection proves staging isolation and absence of OpenAI/service-role/production credentials from repository, fixtures, logs, artifacts, and PR evidence.
- `AC-007` — The deployed staging endpoint passes sanitized Android-compatible success, warning, quota, replay, expiry, auth, policy-unavailable, and parser-failure contract tests.
- `AC-008` — Backend format/type/lint/tests, migration checks, staging integration, secret/privacy review, CI, and complete-diff review pass with no blocking finding.

## Non-goals

Android capture/OCR/network/UI implementation, receipt-image upload, full OCR persistence, user accounts, production deployment, production abuse hardening/device attestation, production entitlement hardening, offline parsing, final bill calculation, automatic client retry, support-diagnostics opt-in, multiple AI providers, or a general backend framework.

## Assumptions

The backend remains in this repository under `supabase/` to keep app, function, migrations, fixtures, and contract changes atomic. One staging Supabase project and server-side OpenAI test secret are supplied by the human owner. The API contract is approved before implementation begins. Free quota is in scope; Pro fair-use may use a controlled fake/policy seam until entitlement hardening is separately approved.

## Open decisions

None.

## Verification matrix

| Acceptance criterion | Evidence required | Proposed verification |
|---|---|---|
| `AC-001` | HTTP contract/access | Local Edge Function contract tests |
| `AC-002` | Parser/schema boundary | Unit tests with fake OpenAI adapter and sanitized fixtures |
| `AC-003` | Schema/quota correctness | Local Supabase migration and database integration tests |
| `AC-004` | Concurrency/idempotency | Concurrent request integration tests |
| `AC-005` | Privacy/retention | Storage, cleanup, logs, and diff inspection |
| `AC-006` | Environment/secret isolation | Config, repository, artifact, and log inspection |
| `AC-007` | Staging behavior | Deployed staging contract suite |
| `AC-008` | Gates/review | Backend commands, CI, and privacy-focused complete-diff review |
