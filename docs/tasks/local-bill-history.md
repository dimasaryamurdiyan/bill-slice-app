# Local bill history implementation plan

- Status: Ready
- Specification: [`docs/specs/local-bill-history.md`](../specs/local-bill-history.md)
- Branch: `codex/local-bill-history`

## Outcome contract

- **Outcome:** Save, list, and reopen full editable bills locally with Free/Pro visibility policy and no receipt-image/OCR retention.
- **Acceptance criteria:** `AC-001`–`AC-008`.
- **Non-goals:** See specification.
- **Verification:** Repository/schema/migration/UI tests, offline actual app, build gates, review.

## Preconditions

Approved skeleton, manual bill models/result, and app shell; clean baseline/draft PR; Room test/device environment. Entitlement may use a fake until Lifetime Pro lands.

## Vertical slices

- [ ] `T-001` — Establish green baseline and persistence acceptance fixtures
  - Covers: `AC-008`
  - Result: Baseline is green and canonical editable bill fixture is available to persistence tests.
  - Likely scope: read-only checks and test fixture use.
  - Verification: existing gates.
  - Depends on: none
- [ ] `T-002` — Define repository/use-case behavior test-first
  - Covers: `FR-001`, `FR-003`–`FR-008`, `FR-011`; `AC-001`, `AC-003`–`AC-005`
  - Result: Small domain interface covers save/update/observe/get and entitlement-aware visibility.
  - Likely scope: `:core:domain`, `:core:model`, `:core:testing`.
  - Verification: JVM tests through interface/fake.
  - Depends on: `T-001`
- [ ] `T-003` — Implement Room schema, mapping, and backup exclusions
  - Covers: `FR-001`, `FR-002`, `FR-009`, `FR-010`; `AC-001`, `AC-002`, `AC-006`
  - Result: Versioned schema stores full structured bill only; backup rules exclude it; mapping is lossless.
  - Likely scope: `:core:database`, `:core:data`, schema exports, backup/data-extraction XML, Gradle.
  - Verification: round-trip/schema/privacy tests and compilation.
  - Depends on: `T-002`
- [ ] `T-004` — Test migrations and storage failures
  - Covers: `FR-003`, `FR-004`, `FR-009`; `AC-003`, `AC-006`
  - Result: Update/transaction/missing/corrupt/migration paths are typed and non-destructive.
  - Likely scope: database/data integration tests and evidenced fixes.
  - Verification: Room migration and failure tests.
  - Depends on: `T-003`
- [ ] `T-005` — Integrate truthful Save and reopen
  - Covers: `FR-003`, `FR-004`, `FR-007`, `FR-008`; `AC-001`, `AC-003`, `AC-005`
  - Result: Result saves/updates with accurate state and reopened bills restore editable flow offline.
  - Likely scope: `:feature:bill`, DI/data wiring, tests.
  - Verification: save/reopen integration test.
  - Depends on: `T-004`
- [ ] `T-006` — Implement History and Home recent context
  - Covers: `FR-005`, `FR-006`, `FR-011`; `AC-004`, `AC-005`, `AC-007`
  - Result: Designed History states and Home summaries use domain interfaces, five-newest Free policy, and no feature dependency.
  - Likely scope: `:feature:history`, `:feature:home`, resources/tests.
  - Verification: domain/ViewModel/Compose tests and dependency report.
  - Depends on: `T-004`, `T-005`
- [ ] `T-007` — Update architecture/data documentation
  - Covers: `FR-002`, `FR-009`, `FR-010`; `AC-002`, `AC-006`
  - Result: Implemented schema ownership, backup policy, and module status are documented.
  - Likely scope: `ARCHITECTURE.md` and contract docs if changed.
  - Verification: doc/link inspection.
  - Depends on: `T-006`
- [ ] `T-008` — Run targeted tests, compile, lint, and build
  - Covers: `AC-008`
  - Result: Repository/database/UI tests and all affected gates pass.
  - Likely scope: verification.
  - Verification: affected Gradle tasks; repository tests/lint/assembly.
  - Depends on: `T-007`
- [ ] `T-009` — Inspect offline History and adaptive states
  - Covers: `AC-004`, `AC-005`, `AC-007`
  - Result: Empty/populated/failure/reopen pass phone/tablet/accessibility actual-app inspection.
  - Likely scope: device evidence/fixes.
  - Verification: actual app and applicable connected tests.
  - Depends on: `T-008`
- [ ] `T-010` — Review the complete diff against spec and standards
  - Covers: `AC-001`–`AC-008`
  - Result: A fresh reviewer reports no unresolved blocking data-loss, privacy, migration, architecture, or standards finding.
  - Likely scope: complete diff and evidenced fixes.
  - Verification: diff check, fresh review, affected reruns.
  - Depends on: `T-009`
- [ ] `T-011` — Perform final acceptance-criteria verification
  - Covers: `AC-001`–`AC-008`
  - Result: Every criterion has recorded evidence and Fastlane/CI gates pass before human review.
  - Likely scope: PR evidence only.
  - Verification: walk verification matrix; Fastlane and required CI verify.
  - Depends on: `T-010`

## Risks and rollback

Risks are data loss, lossy mapping, accidental backup, and entitlement logic in DAOs. Roll back feature wiring independently, but database changes require forward migration—never destructive rollback.

## Completion checklist

- [ ] `AC-001`–`AC-008` evidenced.
- [ ] Round trip/migration/privacy/offline checks pass.
- [ ] Tests/build/actual-app/CI/review pass with no blocking finding.
