# Manual bill splitting implementation plan

- Status: Proposed
- Specification: [`docs/specs/manual-bill-splitting.md`](../specs/manual-bill-splitting.md)
- Branch: `codex/manual-bill-splitting`

## Outcome contract

- **Outcome:** Deliver the offline one-owner manual split from entry through text sharing.
- **Acceptance criteria:** `AC-001`–`AC-009`.
- **Non-goals:** See specification.
- **Verification:** Test-first domain behavior, UI/state tests, actual-app evidence, build gates, and review.

## Preconditions

Approved skeleton and app-shell theme/interfaces; approved one-owner spec; clean baseline/draft PR; phone/tablet devices. No external credentials.

## Vertical slices

- [ ] `T-001` — Establish green baseline and failing canonical acceptance test
  - Covers: `AC-003`, `AC-009`
  - Result: Baseline is green and the new one-owner test fails for missing behavior.
  - Likely scope: read-only checks and `:core:domain` tests.
  - Verification: existing gates plus focused red test.
  - Depends on: none
- [ ] `T-002` — Correct all shared-item fixtures/contracts
  - Covers: `FR-005`; `AC-003`, `AC-008`
  - Result: Repository and Pencil sample content use the approved one-owner fixture without visual redesign.
  - Likely scope: `PRODUCT.md`, product plan, `RTK.md`, `ARCHITECTURE.md`, `billslice.pen`, fixtures.
  - Verification: text search, Pencil inspection, doc diff.
  - Depends on: `T-001`
- [ ] `T-003` — Implement exact models and typed validation test-first
  - Covers: `FR-001`–`FR-007`, `FR-011`; `AC-001`, `AC-002`
  - Result: Pure models express draft concepts and invalid states without Android/SDK leakage.
  - Likely scope: `:core:model`, `:core:domain`, JVM tests.
  - Verification: focused tests and pure-module compilation.
  - Depends on: `T-002`
- [ ] `T-004` — Implement deterministic calculation use case
  - Covers: `FR-008`–`FR-012`; `AC-002`, `AC-003`
  - Result: One deep calculation interface passes canonical and boundary/rounding/discount tests.
  - Likely scope: `:core:domain`, `:core:model`, tests.
  - Verification: targeted `CalculateBillSplitUseCase` tests.
  - Depends on: `T-003`
- [ ] `T-005` — Implement validation and share-text use cases
  - Covers: `FR-011`, `FR-012`, `FR-015`, `FR-016`; `AC-002`, `AC-005`
  - Result: Receipt validation and safe deterministic sharing pass public-interface tests.
  - Likely scope: `:core:domain`, tests.
  - Verification: targeted JVM tests.
  - Depends on: `T-004`
- [ ] `T-006` — Add shared test fixtures when domain and feature both consume them
  - Covers: `AC-003`
  - Result: `:core:testing` holds only canonical fixture/fakes used by both test suites.
  - Likely scope: `:core:testing`, Gradle declarations, consuming tests.
  - Verification: dependency inspection and compilation.
  - Depends on: `T-005`
- [ ] `T-007` — Implement Manual Entry and editable session state
  - Covers: `FR-001`–`FR-003`, `FR-006`, `FR-017`; `AC-001`, `AC-006`
  - Result: Stateless UI and ViewModel/reducer preserve, validate, and invalidate draft state.
  - Likely scope: `:feature:bill`, resources, tests.
  - Verification: ViewModel and Compose tests; feature compilation.
  - Depends on: `T-003` and the app-shell theme precondition
- [ ] `T-008` — Implement people, payer, and one-owner assignment
  - Covers: `FR-004`, `FR-005`; `AC-001`
  - Result: Add People/Assign Items enforce unique people, one payer, and one owner per item.
  - Likely scope: `:feature:bill`, domain validation, tests.
  - Verification: semantic UI and state tests.
  - Depends on: `T-007`
- [ ] `T-009` — Implement Summary, Result, and Share Preview
  - Covers: `FR-013`–`FR-016`; `AC-004`, `AC-005`
  - Result: Designed values/actions and Android copy/share adapter work safely.
  - Likely scope: `:feature:bill`, app/share seam, resources/tests.
  - Verification: canonical UI integration and intent tests.
  - Depends on: `T-005`, `T-008`
- [ ] `T-010` — Cover failures, restoration, and boundary values
  - Covers: `FR-003`, `FR-006`, `FR-011`, `FR-012`, `FR-016`, `FR-017`; `AC-001`, `AC-002`, `AC-005`, `AC-006`
  - Result: Invalid/large/zero inputs, dirty back, mismatch, repeated taps, rotation/process restoration, and cancellation are covered.
  - Likely scope: affected tests/fixes only.
  - Verification: JVM/coroutine/Compose tests.
  - Depends on: `T-009`
- [ ] `T-011` — Run targeted and aggregate verification
  - Covers: `AC-009`
  - Result: Feature/domain tests and compilation, repository tests, lint, and assembly pass.
  - Likely scope: verification.
  - Verification: affected tasks; `./gradlew testDebugUnitTest lintDebug assembleDebug`.
  - Depends on: `T-010`
- [ ] `T-012` — Inspect six actual screens and capture evidence
  - Covers: `AC-003`–`AC-007`
  - Result: Compact, large-font/landscape, tablet, and accessibility inspection passes with PR screenshots.
  - Likely scope: device evidence and evidenced fixes.
  - Verification: actual app and applicable connected tests.
  - Depends on: `T-011`
- [ ] `T-013` — Review the complete diff against spec and standards
  - Covers: `AC-001`–`AC-009`
  - Result: A fresh reviewer reports no unresolved blocking correctness, money, state, accessibility, or standards finding.
  - Likely scope: complete diff and evidenced fixes.
  - Verification: diff check, fresh review, affected reruns.
  - Depends on: `T-012`
- [ ] `T-014` — Perform final acceptance-criteria verification
  - Covers: `AC-001`–`AC-009`
  - Result: Every criterion has recorded evidence and Fastlane/CI gates pass before human review.
  - Likely scope: PR evidence only.
  - Verification: walk verification matrix; Fastlane and required CI verify.
  - Depends on: `T-013`

## Risks and rollback

Main risks are incorrect money, stale shared fixtures, state loss, and shallow interfaces. Roll back domain calculation and feature UI in coherent commits; there is no persistence migration.

## Completion checklist

- [ ] `AC-001`–`AC-009` evidenced.
- [ ] Canonical one-owner totals pass pure, UI, and actual-app checks.
- [ ] Tests/compile/lint/build/Fastlane/CI/review pass with no blocking finding.
