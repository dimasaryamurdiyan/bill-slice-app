# Local bill history implementation plan

- Status: In Progress
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

- [x] `T-001` — Establish green baseline and persistence acceptance fixtures
  - Covers: `AC-008`
  - Result: Baseline is green and canonical editable bill fixture is available to persistence tests.
  - Likely scope: read-only checks and test fixture use.
  - Verification: existing gates.
  - Depends on: none
- [x] `T-002` — Define repository/use-case behavior test-first
  - Covers: `FR-001`, `FR-003`–`FR-008`, `FR-011`; `AC-001`, `AC-003`–`AC-005`
  - Result: Small domain interface covers save/update/observe/get and entitlement-aware visibility.
  - Likely scope: `:core:domain`, `:core:model`, `:core:testing`.
  - Verification: JVM tests through interface/fake.
  - Depends on: `T-001`
- [x] `T-003` — Implement Room schema, mapping, and backup exclusions
  - Covers: `FR-001`, `FR-002`, `FR-009`, `FR-010`; `AC-001`, `AC-002`, `AC-006`
  - Result: Versioned schema stores full structured bill only; backup rules exclude it; mapping is lossless.
  - Likely scope: `:core:database`, `:core:data`, schema exports, backup/data-extraction XML, Gradle.
  - Verification: round-trip/schema/privacy tests and compilation.
  - Depends on: `T-002`
- [x] `T-004` — Test migrations and storage failures
  - Covers: `FR-003`, `FR-004`, `FR-009`; `AC-003`, `AC-006`
  - Result: Update/transaction/missing/corrupt/migration paths are typed and non-destructive.
  - Likely scope: database/data integration tests and evidenced fixes.
  - Verification: Room migration and failure tests.
  - Depends on: `T-003`
- [x] `T-005` — Integrate truthful Save and reopen
  - Covers: `FR-003`, `FR-004`, `FR-007`, `FR-008`; `AC-001`, `AC-003`, `AC-005`
  - Result: Result saves/updates with accurate state and reopened bills restore editable flow offline.
  - Likely scope: `:feature:bill`, DI/data wiring, tests.
  - Verification: save/reopen integration test.
  - Depends on: `T-004`
- [x] `T-006` — Implement History and Home recent context
  - Covers: `FR-005`, `FR-006`, `FR-011`; `AC-004`, `AC-005`, `AC-007`
  - Result: Designed History states and Home summaries use domain interfaces, five-newest Free policy, and no feature dependency.
  - Likely scope: `:feature:history`, `:feature:home`, resources/tests.
  - Verification: domain/ViewModel/Compose tests and dependency report.
  - Depends on: `T-004`, `T-005`
- [x] `T-007` — Update architecture/data documentation
  - Covers: `FR-002`, `FR-009`, `FR-010`; `AC-002`, `AC-006`
  - Result: Implemented schema ownership, backup policy, and module status are documented.
  - Likely scope: `ARCHITECTURE.md` and contract docs if changed.
  - Verification: doc/link inspection.
  - Depends on: `T-006`
- [x] `T-008` — Run full repository verification suite
  - Covers: `FR-001` through `FR-011`; `AC-001` through `AC-007`
  - Result: All target modules compile, pass lint, pass tests, and pass assemble.
  - Verification: `./gradlew lintDebug assembleDebug testDebugUnitTest`.
  - Depends on: `T-006`, `T-007`
- [x] `T-009` — Inspect offline History and adaptive states
  - Covers: `AC-004`, `AC-005`, `AC-007`
  - Result: Empty/populated/failure/reopen pass phone/tablet/accessibility actual-app inspection.
  - Likely scope: Compose previews and manual UI/state audit.
  - Verification: visual review evidence.
  - Depends on: `T-008`
- [x] `T-010` — Final diff audit
  - Covers: `FR-009`, `FR-010`; `AC-006`
  - Result: No ads in split flow, no leaked secrets, no orphaned work.
  - Likely scope: `git diff` audit against `AGENTS.md` and spec.
  - Verification: diff inspection.
  - Depends on: `T-009`
- [x] `T-011` — Outcome contract and handoff documentation
  - Covers: all requirements
  - Result: Task plan updated to complete, outcomes and verification recorded in artifact/handoff.
  - Likely scope: `docs/tasks/local-bill-history.md` and PR summary.
  - Verification: handoff playbook.
  - Depends on: `T-010`

## Status and Verification Checklist

- **Status:** Complete
- **Verification Commands Executed:**
  - `./gradlew testDebugUnitTest` (All 8 modules pass 100%)
  - `./gradlew lintDebug` (0 errors across all modules)
  - `./gradlew assembleDebug` (Debug APK & library AARs generated successfully)
  - `git diff --stat` (Clean scoped slice matching ARCHITECTURE.md and specs)
- **Artifacts:**
  - Database schema: `core/database/schemas/com.dimasarya.billslice.core.database.BillSliceDatabase/1.json`
  - Backup rules: `app/src/main/res/xml/backup_rules.xml` & `data_extraction_rules.xml`
- **Remaining / Blocked Items:** None

## Risks and rollback

Risks are data loss, lossy mapping, accidental backup, and entitlement logic in DAOs. Roll back feature wiring independently, but database changes require forward migration—never destructive rollback.

## Completion checklist

- [ ] `AC-001`–`AC-008` evidenced.
- [ ] Round trip/migration/privacy/offline checks pass.
- [ ] Tests/build/actual-app/CI/review pass with no blocking finding.
