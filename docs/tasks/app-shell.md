# App shell implementation plan

- Status: Complete
- Specification: [`docs/specs/app-shell.md`](../specs/app-shell.md)
- Branch: `codex/app-shell`

## Outcome contract

- **Outcome:** Implement Splash, Home, Settings, navigation, and the shared BillSlice theme without feature business logic.
- **Acceptance criteria:** `AC-001`–`AC-007`.
- **Non-goals:** See specification.
- **Verification:** Navigation/UI tests, module compilation, lint/build, actual-app adaptive evidence, and complete-diff review.

## Preconditions

- Approved skeleton implementation and this specification.
- Clean approved baseline and draft PR.
- `Medium_Phone_API_36` and `Pixel_Tablet`; no service credentials required.
- Re-read current `billslice.pen` before UI work.

## Vertical slices

- [x] `T-001` — Establish green post-skeleton baseline
  - Covers: `AC-007`
  - Result: Git, modules, tests, lint, and assembly are green before shell work.
  - Likely scope: read-only repository inspection.
  - Verification: `git status --short`; `./gradlew testDebugUnitTest lintDebug assembleDebug`
  - Depends on: none
- [x] `T-002` — Implement BillSlice design tokens and theme
  - Covers: `FR-006`, `FR-007`; `AC-004`
  - Result: `:core:designsystem` owns approved light tokens; `:core:ui` contains only proven shared patterns; starter theme behavior is removed.
  - Likely scope: `:core:designsystem`, `:core:ui`, app theme resources/wiring, Gradle catalog.
  - Verification: theme/UI tests and affected-module compilation.
  - Depends on: `T-001`
- [x] `T-003` — Define typed app routes and navigation acceptance tests
  - Covers: `FR-002`–`FR-005`, `FR-009`; `AC-001`–`AC-003`
  - Result: App-owned typed routes connect independent feature entry interfaces and tests prove idempotent navigation.
  - Likely scope: `:app`, route contracts, navigation test sources.
  - Verification: targeted navigation tests; dependency reports.
  - Depends on: `T-001`
- [x] `T-004` — Implement Splash and Home
  - Covers: `FR-001`, `FR-002`, `FR-009`; `AC-001`, `AC-002`
  - Result: Offline startup reaches designed Home with scan/manual actions and recoverable optional content.
  - Likely scope: `:feature:home`, `:app`, resources, ViewModel/Compose tests.
  - Verification: feature tests and app compilation.
  - Depends on: `T-002`, `T-003`
- [x] `T-005` — Implement Settings state and screen
  - Covers: `FR-008`; `AC-005`
  - Result: Settings renders typed currency/quota/privacy/Pro/build states including unavailable dependencies.
  - Likely scope: `:feature:settings`, domain-facing summary interfaces/fakes, resources/tests.
  - Verification: ViewModel and semantic Compose tests.
  - Depends on: `T-002`, `T-003`
- [x] `T-006` — Cover failure, restoration, and adaptive behavior
  - Covers: `FR-009`, `FR-010`; `AC-001`, `AC-006`
  - Result: Startup failure, repeated taps, rotation, large font, landscape, long text, and expanded width remain usable.
  - Likely scope: affected tests and narrowly evidenced UI fixes.
  - Verification: UI/integration tests and configuration overrides.
  - Depends on: `T-004`, `T-005`
- [x] `T-007` — Update implemented architecture status
  - Covers: `FR-004`–`FR-007`; `AC-003`
  - Result: `ARCHITECTURE.md` distinguishes implemented shell/design modules from target modules.
  - Likely scope: `ARCHITECTURE.md`.
  - Verification: documentation/link review.
  - Depends on: `T-006`
- [x] `T-008` — Run targeted tests, compile, lint, and build
  - Covers: `AC-007`
  - Result: Affected tests/compilation plus repository tests, lint, and assembly pass.
  - Likely scope: verification and evidence.
  - Verification: affected Gradle tasks; `./gradlew testDebugUnitTest lintDebug assembleDebug`.
  - Depends on: `T-007`
- [x] `T-009` — Inspect actual shell and capture screenshots
  - Covers: `AC-001`, `AC-002`, `AC-004`–`AC-006`
  - Result: Splash/Home/Settings pass phone, large-font/landscape, tablet, and accessibility inspection; screenshots remain PR evidence only.
  - Likely scope: emulator/device evidence and evidenced fixes.
  - Verification: actual app on required devices; applicable connected tests.
  - Depends on: `T-008`
- [x] `T-010` — Review the complete diff against spec and standards
  - Covers: `AC-001`–`AC-007`
  - Result: A fresh reviewer reports no unresolved blocking spec, architecture, accessibility, or standards finding.
  - Likely scope: complete branch diff and evidenced fixes.
  - Verification: `git diff --check`; fresh review; affected reruns.
  - Depends on: `T-009`
- [x] `T-011` — Perform final acceptance-criteria verification
  - Covers: `AC-001`–`AC-007`
  - Result: Every criterion has recorded evidence and Fastlane/CI gates pass before human review.
  - Likely scope: PR evidence only.
  - Verification: walk verification matrix; `bundle exec fastlane android verify`; required CI verify.
  - Depends on: `T-010`

## Risks and rollback

Risks are premature shared UI, route coupling, and adaptive drift from phone-focused Pencil screens. Roll back theme, navigation, Home, and Settings as coherent commits; no data migration exists.

## Completion checklist

- [x] `AC-001`–`AC-007` have evidence.
- [x] Targeted tests, compile, lint, assembly, actual-app checks, Fastlane/CI, and diff review pass.
- [x] No blocking finding or unapproved feature logic exists in the shell.
