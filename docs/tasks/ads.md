# Ads implementation plan

- Status: Proposed
- Specification: [`docs/specs/ads.md`](../specs/ads.md)
- Branch: `codex/ads`

## Outcome contract

- **Outcome:** Add test ads only at approved Free placements with Pro suppression and failure-safe rendering.
- **Acceptance criteria:** `AC-001`–`AC-007`.
- **Non-goals:** See specification.
- **Verification:** Policy/adapter/UI tests, config review, actual app, gates/review.

## Preconditions

App shell, History, Result, and entitlement policy implemented; Google test ad IDs; clean baseline/draft PR; phone/tablet network environment. No production AdMob config.

## Vertical slices

- [ ] `T-001` — Establish green baseline and placement matrix tests
  - Covers: `FR-002`–`FR-004`; `AC-002`, `AC-003`, `AC-007`
  - Result: Baseline green and failing matrix enumerates every approved/forbidden placement for Free/Pro.
  - Likely scope: pure policy tests.
  - Verification: existing gates and red tests.
  - Depends on: none
- [ ] `T-002` — Implement pure ad visibility policy
  - Covers: `FR-002`–`FR-004`; `AC-002`, `AC-003`
  - Result: Typed placement/entitlement decision passes complete matrix.
  - Likely scope: `:core:model`/`:core:domain`, tests.
  - Verification: focused JVM tests.
  - Depends on: `T-001`
- [ ] `T-003` — Implement test-AdMob adapter
  - Covers: `FR-001`, `FR-005`, `FR-007`, `FR-009`; `AC-001`, `AC-004`, `AC-005`
  - Result: `:core:ads` uses test config, maps load/no-fill/failure, and exposes safe UI wrapper.
  - Likely scope: `:core:ads`, catalog/app wiring, tests.
  - Verification: adapter/Compose tests, compile, config/secret review.
  - Depends on: `T-002`
- [ ] `T-004` — Integrate only approved feature slots
  - Covers: `FR-002`, `FR-003`, `FR-006`, `FR-008`; `AC-002`, `AC-004`, `AC-005`
  - Result: Home/History/post-total Result call the seam; no forbidden screen/direct SDK call exists.
  - Likely scope: allowed feature modules and tests.
  - Verification: placement UI tests and dependency/source search.
  - Depends on: `T-003`
- [ ] `T-005` — Cover Pro/offline/no-fill/failure/adaptive behavior
  - Covers: `FR-004`, `FR-007`, `FR-008`; `AC-003`, `AC-005`, `AC-006`
  - Result: All suppressed/non-rendered states preserve content and traversal across sizes.
  - Likely scope: policy/Compose tests and evidenced fixes.
  - Verification: fake/no-fill/offline/UI tests.
  - Depends on: `T-004`
- [ ] `T-006` — Update ad implementation/config documentation
  - Covers: `FR-001`–`FR-009`; `AC-001`, `AC-004`
  - Result: Implemented placements, test config, ownership, and Pro suppression are documented.
  - Likely scope: `ARCHITECTURE.md`/contract docs if changed.
  - Verification: doc/config review.
  - Depends on: `T-005`
- [ ] `T-007` — Run targeted and aggregate gates
  - Covers: `AC-007`
  - Result: Policy/adapter/UI tests, compilation, lint, and assembly pass.
  - Likely scope: verification.
  - Verification: affected tasks and repository gates.
  - Depends on: `T-006`
- [ ] `T-008` — Inspect actual Free/Pro/failure placements
  - Covers: `AC-001`–`AC-003`, `AC-005`, `AC-006`
  - Result: Phone/tablet/accessibility test-ad evidence proves placement and suppression without blocked content.
  - Likely scope: actual-app evidence/fixes.
  - Verification: screenshots and applicable connected tests.
  - Depends on: `T-007`
- [ ] `T-009` — Review the complete diff against spec and standards
  - Covers: `AC-001`–`AC-007`
  - Result: A fresh reviewer reports no unresolved blocking placement, privacy, entitlement, accessibility, or standards finding.
  - Likely scope: complete diff and evidenced fixes.
  - Verification: diff check, fresh review, affected reruns.
  - Depends on: `T-008`
- [ ] `T-010` — Perform final acceptance-criteria verification
  - Covers: `AC-001`–`AC-007`
  - Result: Every criterion has recorded evidence and Fastlane/CI gates pass before human review.
  - Likely scope: PR evidence only.
  - Verification: walk verification matrix; Fastlane and required CI verify.
  - Depends on: `T-009`

## Risks and rollback

Risks are forbidden placement, production IDs, layout shifts, and unnecessary requests for Pro. Roll back feature slots and adapter wiring at the seam; core product remains operational.

## Completion checklist

- [ ] `AC-001`–`AC-007` evidenced.
- [ ] Only test IDs and approved placements exist; Pro makes no request.
- [ ] Tests/build/actual-app/CI/review pass.
