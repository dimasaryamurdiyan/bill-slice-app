# Skeleton architecture implementation plan

- Status: Ready
- Specification: [`docs/specs/skeleton-architecture.md`](../specs/skeleton-architecture.md)
- Branch: `codex/skeleton-architecture`

## Outcome contract

- **Outcome:** Establish the minimal compilable path `:app → :feature:bill → :core:domain → :core:model`, preserve the current placeholder launcher behavior, document the implemented graph, and add no speculative BillSlice behavior or integration.
- **Acceptance criteria:** `AC-001` through `AC-008` in the specification.
- **Non-goals:** All items in the specification’s Non-goals section, especially bill behavior, design-system work, `:core:testing`, data/external SDK modules, Hilt, and Gradle convention plugins.
- **Verification:** Use the specification’s verification matrix. Compilation proves module compatibility; tests, lint, and assembly prove the aggregate baseline; actual-app inspection proves placeholder preservation; complete-diff review proves scope and dependency constraints.

## Preconditions

- Human approval of [`docs/specs/skeleton-architecture.md`](../specs/skeleton-architecture.md), including the four-module scope and deferred `:core:testing` module.
- An explicitly approved baseline commit on `main` and a clean worktree, as required by [`RTK.md`](../../RTK.md). At plan creation, the observed baseline is `3d751a8 chore: establish project baseline` and the worktree is clean; recheck before implementation.
- A `codex/skeleton-architecture` branch created from that baseline and a draft PR opened before material build/source changes.
- JDK/Android SDK support for the repository’s pinned AGP 9.2.1, Gradle 9.4.1, Kotlin 2.2.10, compile SDK 36.1, and min SDK 24 configuration.
- Access to Gradle’s existing user cache or permission to resolve declared build dependencies. No product credential, backend, camera, receipt fixture, signing secret, or external-service account is required.
- `Medium_Phone_API_36` available for final actual-app inspection. If unavailable, keep the PR draft and record the missing device evidence rather than treating previews as acceptance evidence.

## Vertical slices

- [ ] `T-001` — Establish and record a green implementation baseline
  - Covers: `FR-007`, `FR-009`, `FR-011`; `AC-006`
  - Result: The implementation branch starts from the approved clean baseline, current generated tests are recognized only as build smoke tests, and existing compilation, JVM tests, lint, and debug assembly are known-good before extraction.
  - Likely scope: read-only inspection of `settings.gradle.kts`, root and app Gradle files, `app/src`, and Git state; no production edit.
  - Verification: `git status --short`; `git log -1 --oneline`; `./gradlew help :app:testDebugUnitTest :app:lintDebug :app:assembleDebug`
  - Depends on: none

- [ ] `T-002` — Add the minimum Gradle module types and declarations
  - Covers: `FR-001`, `FR-003`, `FR-004`, `FR-005`, `FR-008`; `AC-001`, `AC-002`, `AC-004`
  - Result: Gradle recognizes `:core:model` and `:core:domain` as pure Kotlin modules and `:feature:bill` as an Android library; only plugin aliases and compile dependencies required for those module types are added.
  - Likely scope: `settings.gradle.kts`, root `build.gradle.kts`, `gradle/libs.versions.toml`, `core/model/build.gradle.kts`, `core/domain/build.gradle.kts`, `feature/bill/build.gradle.kts`, and minimal Android-library manifest only if AGP requires it.
  - Verification: `./gradlew projects`; `./gradlew :core:model:compileKotlin :core:domain:compileKotlin :feature:bill:compileDebugKotlin`
  - Depends on: `T-001`

- [ ] `T-003` — Wire the approved inward dependency path
  - Covers: `FR-002`, `FR-003`, `FR-004`, `FR-005`, `FR-006`; `AC-001`, `AC-002`
  - Result: Project dependencies are exactly `:app → :feature:bill`, `:feature:bill → :core:domain` and `:core:model`, and `:core:domain → :core:model`; pure modules contain no Android or external SDK dependency and no reverse edge exists.
  - Likely scope: `app/build.gradle.kts`, `feature/bill/build.gradle.kts`, `core/domain/build.gradle.kts`, `core/model/build.gradle.kts`.
  - Verification: manual build-file review; `./gradlew :app:dependencies --configuration debugRuntimeClasspath`; `./gradlew :feature:bill:dependencies --configuration debugRuntimeClasspath`; pure-module dependency reports using the configuration names exposed by `./gradlew :core:model:tasks :core:domain:tasks`
  - Depends on: `T-002`

- [ ] `T-004` — Move the existing placeholder UI behind the bill-feature seam
  - Covers: `FR-005`, `FR-006`, `FR-007`; `AC-003`
  - Result: `MainActivity` and app theme application remain in `:app`; the existing `Greeting` placeholder content and preview move to `:feature:bill`; `:app` invokes the feature-owned composable with no intentional copy, interaction, semantic, layout, or theme change.
  - Likely scope: `app/src/main/java/com/dimasarya/billslice/MainActivity.kt`; new source under `feature/bill/src/main/java/com/dimasarya/billslice/feature/bill/`; feature Compose dependencies. Existing theme files remain in `:app` for this slice.
  - Verification: `./gradlew :feature:bill:compileDebugKotlin :app:compileDebugKotlin`; source comparison of the composable before/after; Compose preview may assist development but is not acceptance evidence.
  - Depends on: `T-003`

- [ ] `T-005` — Exercise the architecture acceptance scenarios without inventing domain tests
  - Covers: `FR-002`, `FR-007`, `FR-009`, `FR-011`; `AC-001`, `AC-003`, `AC-005`
  - Result: Targeted build checks demonstrate that every module compiles independently and together. Any retained or renamed generated tests are clearly treated as smoke checks, and no fake bill-domain assertion, marker production type, repository interface, or canonical fixture is added merely to create coverage.
  - Likely scope: affected module test source sets only if an existing smoke test must move with its owner; otherwise commands and PR evidence rather than new test files.
  - Verification: `./gradlew :core:model:compileKotlin :core:domain:compileKotlin :feature:bill:compileDebugKotlin :app:compileDebugKotlin`; `./gradlew testDebugUnitTest`
  - Depends on: `T-004`

- [ ] `T-006` — Verify boundary and failure constraints
  - Covers: `FR-002`, `FR-003`, `FR-004`, `FR-008`; `AC-001`, `AC-002`, `AC-004`, `AC-008`
  - Result: Inspection confirms no cycle, outward pure-module dependency, new permission, secret, service identifier, runtime integration, speculative interface, or unapproved module/dependency; deliberately breaking a build is not required.
  - Likely scope: all changed Gradle files, module manifests, source files, and `app/src/main/AndroidManifest.xml`.
  - Verification: `./gradlew projects`; dependency reports from `T-003`; `rg -n "(api_key|service_role|OPENAI|SUPABASE|REVENUECAT|ADMOB|uses-permission)" --glob '!**/build/**'`; manual interface/dependency inspection
  - Depends on: `T-003`, `T-004`

- [ ] `T-007` — Document the implemented graph without changing product contracts
  - Covers: `FR-010`; `AC-007`
  - Result: `ARCHITECTURE.md` gains a concise current-implementation section naming the four implemented modules and deferred modules, while its target graph and product/design documents remain unchanged.
  - Likely scope: `ARCHITECTURE.md` only. Update the specification/task links only if paths changed during implementation.
  - Verification: manual inspection that current and target graphs are distinguishable; link/path validation with `test -f docs/specs/skeleton-architecture.md` and `test -f docs/tasks/skeleton-architecture.md`
  - Depends on: `T-003`

- [ ] `T-008` — Run targeted and aggregate local verification
  - Covers: `FR-009`; `AC-005`, `AC-006`
  - Result: All affected module compilation, repository JVM tests, Android lint, and debug APK assembly pass on the completed implementation.
  - Likely scope: verification only; fixes stay within files already justified by failing evidence.
  - Verification: `./gradlew :core:model:compileKotlin :core:domain:compileKotlin :feature:bill:compileDebugKotlin :app:compileDebugKotlin`; then `./gradlew testDebugUnitTest lintDebug assembleDebug`
  - Depends on: `T-005`, `T-006`, `T-007`

- [ ] `T-009` — Inspect the actual app and retain visual evidence
  - Covers: `FR-007`; `AC-003`
  - Result: The debug APK installs and launches on `Medium_Phone_API_36`; the same placeholder copy and behavior are visible with no crash, clipping, lost edge-to-edge handling, or navigation dead end; one screenshot is attached to the draft PR and not committed.
  - Likely scope: emulator/device and PR evidence only; any regression fix remains within app/feature placeholder wiring.
  - Verification: install/run the debug app on `Medium_Phone_API_36`, inspect the launcher scenario, and record device/API plus screenshot. Run `./gradlew connectedDebugAndroidTest` only if an applicable retained/moved instrumented smoke test exists and the emulator is connected.
  - Depends on: `T-008`

- [ ] `T-010` — Review the complete diff against the specification and repository standards
  - Covers: all functional requirements; `AC-001` through `AC-008`
  - Result: The complete merge-base-to-HEAD diff has no accidental churn, blocking architecture/spec/standards finding, secret, debug artifact, unjustified dependency, placeholder production type, or unsupported product claim.
  - Likely scope: complete branch diff; fixes only where a concrete finding requires them.
  - Verification: `git diff --check`; `git diff --stat main...HEAD`; fresh-agent read-only review along specification and repository-standards axes; rerun every check affected by review fixes
  - Depends on: `T-009`

- [ ] `T-011` — Perform final acceptance-criteria verification
  - Covers: all functional requirements; `AC-001` through `AC-008`
  - Result: Every acceptance criterion has recorded evidence in the draft PR, all applicable repository gates pass, no blocking review finding remains, and the PR is ready for human review but is not merged by the implementing agent.
  - Likely scope: PR description/evidence and verification only.
  - Verification: walk the specification verification matrix row by row; run `bundle exec fastlane android verify` for the canonical local/CI gate; confirm required GitHub Actions `verify` result; record passed, failed, and not-run checks exactly
  - Depends on: `T-010`

## Risks and rollback

- **AGP 9.2/Kotlin plugin shape:** Pure Kotlin and Android-library plugin aliases may interact differently with AGP’s built-in Kotlin support. Mitigate by adding only the catalog/plugin entries required by the actual module types and compiling each module immediately after declaration.
- **False architecture confidence:** Empty build modules do not prove future bill behavior. The plan explicitly uses compilation/dependency evidence only and forbids presenting generated tests as domain evidence.
- **Premature interfaces:** Placeholder repositories, use cases, adapters, and common wrappers would freeze guesses before behavior exists. The safe response is omission, not a fake abstraction.
- **UI regression during extraction:** Moving the placeholder composable can alter imports, theme ownership, preview behavior, or padding. Preserve the existing surface and verify it in the installed app.
- **Target/current documentation drift:** Readers may mistake the full architecture graph for implemented code. Add only a small current-state note and keep target responsibilities intact.
- **Rollback boundary:** The smallest safe rollback is one coherent skeleton slice: revert app-to-feature source wiring, the three new module declarations/build files, related plugin catalog aliases, and the current-state architecture note together. There is no data migration, persisted schema, permission, external service, or user data to roll back.

## Completion checklist

- [ ] `AC-001` through `AC-008` each have explicit evidence.
- [ ] All four approved modules compile independently and as the application graph.
- [ ] `testDebugUnitTest`, `lintDebug`, and `assembleDebug` pass.
- [ ] The canonical `bundle exec fastlane android verify` gate and required GitHub Actions `verify` pass, or the PR remains draft with exact failure evidence.
- [ ] Actual-app inspection on `Medium_Phone_API_36` is recorded with an uncommitted screenshot.
- [ ] `git diff --check` passes and complete-diff review finds no secret, accidental churn, unjustified module/dependency, or speculative interface.
- [ ] No P0/P1/P2 review finding remains unresolved.
- [ ] `ARCHITECTURE.md` clearly distinguishes implemented and target graphs.
- [ ] The PR description records the outcome contract, verification evidence, screenshot, review result, and remaining limitations.
- [ ] The implementing agent has not merged the PR; human judgment owns approval and merge readiness.
