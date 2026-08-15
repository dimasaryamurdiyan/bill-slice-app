# Smart Scan implementation plan

- Status: Ready
- Specification: [`docs/specs/smart-scan.md`](../specs/smart-scan.md)
- Branch: `codex/smart-scan`

## Outcome contract

- **Outcome:** Capture/import, local OCR, cloud parsing, editable confirmation, quota, and manual fallback with strict image/OCR privacy.
- **Acceptance criteria:** `AC-001`–`AC-009`.
- **Non-goals:** See specification.
- **Verification:** Adapter/contract/state/UI/privacy tests, actual-app evidence, gates/review.

## Preconditions

Manual draft/review interfaces and app shell implemented; sanitized images; test Supabase endpoint/publishable config; server secret stays server-side; device camera/import environment; clean baseline/draft PR.

## Vertical slices

- [ ] `T-001` — Establish green baseline and sanitized fixtures
  - Covers: `AC-009`
  - Result: Baseline green; private receipt data absent; expected contract recorded.
  - Likely scope: read-only checks and sanitized test assets.
  - Verification: existing gates and fixture review.
  - Depends on: none
- [ ] `T-002` — Define quota, parse, and OCR outcomes test-first
  - Covers: `FR-003`–`FR-006`, `FR-010`; `AC-003`, `AC-004`
  - Result: Pure models/use cases represent quota, parse result/warnings, and typed errors without SDK leakage.
  - Likely scope: `:core:model`, `:core:domain`, `:core:testing`.
  - Verification: JVM quota/time/error tests.
  - Depends on: `T-001`
- [ ] `T-003` — Implement capture/import and ML Kit adapter
  - Covers: `FR-001`–`FR-003`, `FR-010`, `FR-012`; `AC-001`, `AC-003`, `AC-007`
  - Result: Narrow permission, system import, unbundled OCR, cancellation, and transient cleanup work behind `ReceiptOcr`.
  - Likely scope: `:core:ocr`, `:feature:bill`, manifest/platform contracts, tests.
  - Verification: adapter/permission/storage tests and compilation.
  - Depends on: `T-002`
- [ ] `T-004` — Implement Smart Scan network/repository adapter
  - Covers: `FR-004`–`FR-006`, `FR-010`; `AC-002`, `AC-004`
  - Result: HTTPS client sends only allowed fields, maps all responses, and produces no sensitive logs/persistence.
  - Likely scope: `:core:network`, `:core:data`, domain repository, config/tests.
  - Verification: fake-server request/response/privacy tests.
  - Depends on: `T-002`
- [ ] `T-005` — Verify authoritative backend quota/privacy contract
  - Covers: `FR-005`, `FR-006`; `AC-002`, `AC-004`
  - Result: Test endpoint enforces five/month/reset and minimal logging with server-side secret ownership.
  - Likely scope: test backend/deployment evidence; backend source only in its approved repository/location.
  - Verification: test-backend integration/log inspection.
  - Depends on: `T-004`
- [ ] `T-006` — Implement loading state machine and cancellation
  - Covers: `FR-007`, `FR-010`–`FR-012`; `AC-005`, `AC-007`
  - Result: Explicit steps, retry/cancel, duplicate prevention, and every manual fallback state pass coroutine tests.
  - Likely scope: `:feature:bill` ViewModel/reducer/resources/tests.
  - Verification: coroutine/ViewModel tests without sleeps.
  - Depends on: `T-003`, `T-004`
- [ ] `T-007` — Implement editable Review handoff
  - Covers: `FR-008`, `FR-009`, `FR-011`; `AC-005`, `AC-006`
  - Result: Parsed draft/warnings are editable, validated, confirmation-gated, and can switch to manual entry.
  - Likely scope: `:feature:bill`, domain validation, Compose tests.
  - Verification: Review/state UI tests.
  - Depends on: `T-006`
- [ ] `T-008` — Update Smart Scan architecture/privacy docs
  - Covers: `FR-002`, `FR-004`–`FR-006`; `AC-002`, `AC-004`
  - Result: Implemented boundary/config/log behavior is accurately documented.
  - Likely scope: `ARCHITECTURE.md` and contract docs if changed.
  - Verification: doc/link/secret review.
  - Depends on: `T-007`
- [ ] `T-009` — Run targeted and aggregate gates
  - Covers: `AC-009`
  - Result: OCR/network/domain/UI tests, compilation, repository tests, lint, and assembly pass.
  - Likely scope: verification.
  - Verification: affected tasks and repository gates.
  - Depends on: `T-008`
- [ ] `T-010` — Inspect actual capture/loading/review/failure states
  - Covers: `AC-001`, `AC-005`, `AC-006`, `AC-008`
  - Result: Camera/import/offline/quota/success/manual fallback pass phone/tablet/accessibility inspection with PR screenshots.
  - Likely scope: device evidence/fixes.
  - Verification: actual app and applicable connected tests.
  - Depends on: `T-009`
- [ ] `T-011` — Review the complete diff against spec and standards
  - Covers: `AC-001`–`AC-009`
  - Result: A fresh reviewer reports no unresolved blocking secret, privacy, cancellation, contract, architecture, or standards finding.
  - Likely scope: complete diff and evidenced fixes.
  - Verification: diff check, fresh privacy/spec review, affected reruns.
  - Depends on: `T-010`
- [ ] `T-012` — Perform final acceptance-criteria verification
  - Covers: `AC-001`–`AC-009`
  - Result: Every criterion has recorded evidence and Fastlane/CI gates pass before human review.
  - Likely scope: PR evidence only.
  - Verification: walk verification matrix; Fastlane and required CI verify.
  - Depends on: `T-011`

## Risks and rollback

Risks are receipt/OCR leakage, model-download friction, duplicate requests, and unavailable backend. Roll back OCR/network/UI adapters at their seams; manual splitting remains operational.

## Completion checklist

- [ ] `AC-001`–`AC-009` evidenced.
- [ ] Image never uploaded; full OCR not persisted/logged.
- [ ] Every failure exposes manual entry; tests/build/actual-app/CI/review pass.
