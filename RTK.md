# BillSlice Agent Harness

This is the repository-wide operating guide for coding agents. Keep it accurate as the project evolves. A more specific `AGENTS.md` in a subdirectory may add local rules for that subtree.

## Instruction order

1. Platform, safety, and sandbox instructions.
2. The user's current request.
3. The nearest applicable `AGENTS.md`, including this file through the root `AGENTS.md`.
4. Repository product, architecture, and design documents.
5. Existing code and tests for implementation conventions.

If repository documents disagree, do not silently choose. Prefer the document dedicated to the concern, call out the conflict, and make the smallest reversible decision that satisfies the current request.

## Repository map and sources of truth

BillSlice is an Android receipt-splitting app for Indonesian restaurant groups. The current repository is an early single-module Jetpack Compose app; `ARCHITECTURE.md` describes a target architecture, not a requirement to create every module now.

- `PRODUCT.md`: product promise, current release scope, invariants, and acceptance tests.
- `docs/product-plan.md`: detailed behavior, bill-math rules, backend contract, and test fixtures.
- `ARCHITECTURE.md`: dependency direction, module responsibilities, seams, and implementation phases.
- `DESIGN.md`: visual tokens, component behavior, copy tone, and UI constraints.
- `SKILL.md`: project-local Modern Android Development index. Read only the relevant linked file(s) under `references/` for the task.
- `gradle/libs.versions.toml`: dependency and plugin versions.
- `settings.gradle.kts` and module `build.gradle.kts` files: modules and actual build configuration.
- Existing source and tests: the truth for what is implemented today.

Do not treat future-roadmap text as permission to implement future scope. Do not invent modules, abstractions, dependencies, or infrastructure merely because a target document names them.

## Repository rules

- Keep changes narrowly scoped to the requested behavior. Preserve unrelated and uncommitted work.
- Prefer a small, complete vertical slice over speculative framework work.
- New UI is Jetpack Compose with Material 3; do not add XML layouts.
- Keep the active split flow free of ads: Scan -> Review -> Add People -> Assign -> Calculate.
- Manual item entry is a first-class fallback. OCR or AI failure must not block bill splitting.
- Treat OCR/AI output as an editable draft that requires user confirmation.
- Receipt images stay on-device. The Smart Scan backend receives OCR text only. Do not persist full OCR text or receipt images by default.
- Never put OpenAI, Supabase service-role, RevenueCat, AdMob, signing, or other secrets in source, resources, generated artifacts, logs, or tests.
- Use test ad IDs in development until production configuration is explicitly requested and available.
- Bill math must be deterministic and exact. Avoid `Float` and `Double` for money; model minor units or use another exact representation with an explicit rounding policy.
- Default product assumptions are IDR, Indonesian receipt conventions, and text/WhatsApp sharing, while domain models should remain global-ready.
- Add a dependency only when it is required by the current slice. Use the version catalog and verify it does not violate a module boundary.
- Update architecture, product, or design documentation when a change intentionally alters a documented contract.

## Inspect before coding

Before editing, establish the smallest relevant context:

1. Read this harness and run `git status --short` so user-owned changes are visible.
2. Before writing, reviewing, or refactoring source or build code, invoke and follow the available `karpathy-guidelines` skill. Surface assumptions and ambiguity, choose the simplest sufficient approach, keep every changed line tied to the request, and define verifiable success criteria before implementation. Use judgment for trivial documentation-only edits.
3. Locate the affected module, neighboring implementation, tests, resources, and build file. Search with `rg`/`rg --files` before assuming a file or API exists.
4. Read the relevant section of `PRODUCT.md` or `docs/product-plan.md` for user-visible behavior.
5. Read the relevant boundary in `ARCHITECTURE.md` for domain, data, UI, SDK, or module work.
6. For UI work, read the relevant component/state rules in `DESIGN.md` and the applicable accessibility/adaptive guidance.
7. For Android-specific work, use `SKILL.md` as an index and read only the task-relevant files in `references/` (for example testing, navigation, state, security, or local data).
8. Inspect the actual Gradle configuration and dependency versions before using an API from memory.
9. Identify how the change will be verified before implementation. For a bug, reproduce it or add a failing regression test when practical.

If current code and the target architecture differ, preserve working behavior and migrate only the seam needed for the task.

## AI engineering loop

For every task whose requested deliverable is a material repository change, use this loop:

```text
Human outcome and acceptance criteria
-> inspect
-> state the outcome contract and verification evidence
-> plan
-> create or reuse a codex/* branch and draft PR
-> implement in small vertical slices
-> targeted tests
-> lint
-> build
-> run and inspect the actual app when UI is affected
-> fresh-agent review of the complete branch diff
-> fix failures and blocking findings
-> repeat affected verification
-> GitHub Actions verify
-> mark ready for human review only when completion criteria pass
```

### Outcome contract

Before implementation, state:

- **Outcome:** the user-visible or engineering result.
- **Acceptance criteria:** observable facts that must be true.
- **Non-goals:** adjacent work intentionally excluded.
- **Verification:** evidence mapped to each acceptance criterion.

Infer this contract from the request and proceed unless ambiguity would materially change the result. Human judgment owns intent and final merge readiness; AI generation owns implementation and deterministic verification.

### Spec- and task-driven feature work

When asked to implement a feature, treat its matching files under `docs/specs/` and `docs/tasks/` as the implementation contract and progress ledger.

Resolve the feature in this order:

1. An explicit spec path, task-plan path, or feature name in the request.
2. The current branch matching a task plan's `Branch:` metadata.
3. Exactly one task plan with `Status: In Progress`.
4. Exactly one pair whose spec is `Approved` and task plan is `Ready`.
5. Otherwise stop and ask which feature to implement; never guess between eligible pairs.

Before coding, read the resolved spec and task plan completely, then verify:

- The spec has `Status: Approved` and the task plan has been explicitly authorized by a human. Only a human may authorize `Draft` to `Approved`, `Proposed` to `Ready`, or superseding/reapproving an approved spec.
- The task plan has `Status: Ready` or `Status: In Progress`.
- The spec has no unresolved open decision and its assumptions still match the repository.
- The spec and task plan link to each other and describe the same outcome.
- The next task's declared dependencies are checked.

Use these status transitions:

```text
Spec: Draft -> Approved -> Superseded
Task plan: Proposed -> Ready -> In Progress -> Complete
```

Implementation agents may perform only `Ready` to `In Progress` and `In Progress` to `Complete`. The first implementation change sets `Ready` to `In Progress`. Set the plan to `Complete` only when every vertical slice and completion-checklist item is checked and all required local, device, CI, and review evidence passes. `Complete` means implemented and verified on the PR branch, not merged. Keep the approved spec `Approved`.

For the prompt "implement this feature":

- Reuse or create the one feature branch and one long-lived draft PR declared by the task plan. One spec maps to one branch and one PR.
- Reconcile the plan against existing code before generating new code. For consecutive already-satisfied tasks, verify each exact Result and Verification before backfilling `[ ]` to `[x]`; file presence alone is not evidence.
- Select the lowest-numbered unchecked task whose dependencies are checked, unless the user explicitly names another dependency-ready task.
- Implement one coherent material vertical slice per prompt. A directly related verification-only or documentation-only task may accompany it when it adds no behavior or scope.
- Change only tasks whose stated Result is true and whose task-specific Verification passed from `[ ]` to `[x]`. The checkbox means implemented and verified on the PR branch, not merged. Keep detailed evidence in the PR, not the task file.
- Stop after the selected slice is verified, committed, pushed, and reflected in the draft PR. A later prompt continues the same branch and PR.

Make small coherent commits that tell the slice's implementation story. Every pushed commit must build and pass its relevant targeted tests; prove the red TDD state locally rather than pushing a deliberately failing commit. Put the checkbox/status update in the commit that completes its slice. Human judgment still decides whether and how to merge, with squash merge preferred.

After each slice, run targeted verification and a focused self-review. Require an immediate fresh reviewer for money, persistence/migrations, privacy/security, permissions, billing, ads, or external-SDK boundaries. Before PR readiness, always require a fresh two-axis review of the complete branch diff.

For the terminal transition, first obtain green CI and final review on the completed implementation head. Then commit the completion-checklist and `In Progress` to `Complete` metadata update, push it, and require CI to pass again on that exact commit before marking the PR ready. The metadata commit needs documentation checks and exact-head CI; repeat independent review only if it changes more than completion metadata.

An approved spec is immutable during implementation. If the outcome, acceptance criteria, business rules, privacy/security contract, or non-goals must change, keep the PR draft, stop the affected slice, propose the exact revision, and wait for human reapproval. Task-level implementation details such as file paths, commands, or dependency ordering may be corrected in the same PR when the approved behavior does not change; explain the drift in the PR.

### Test policy

- Bug fixes start with a failing regression test when the behavior can reasonably be automated.
- Deterministic domain logic uses test-first vertical slices where practical: one behavior test, minimal implementation, then the next behavior.
- UI and integration work defines acceptance scenarios first; implementation may precede automated UI tests when a test-first seam would be artificial.
- Refactors establish a green baseline before changes and preserve observable behavior throughout.
- Tests verify behavior through stable public seams, not private implementation details.

### Failure loop

- Continue autonomously while an iteration produces new diagnostic information or measurable progress.
- After the same root failure survives three focused fix attempts, stop changing code, keep the PR in draft, and report the failing check, relevant error, attempted fixes, and best root-cause hypothesis.
- Ask only for the missing decision, permission, credential, external service, or environment change. Resume from the failed gate once unblocked.

### Independent diff review

- At each review point required by the risk-based cadence above, wait for local gates to pass, then delegate a read-only review to a fresh reviewer agent that did not implement the change.
- Review the complete merge-base-to-HEAD diff along two axes: requested specification and repository standards.
- P0/P1 correctness, security, privacy, data-loss, architecture, or acceptance failures block readiness.
- P2 maintainability and test gaps introduced by the PR block readiness. P3 suggestions and unrelated pre-existing issues do not block.
- The implementer may challenge a finding with concrete evidence but may not silently dismiss it. An unresolved blocking disagreement keeps the PR in draft for human judgment.
- After fixes, rerun affected checks and the final verification gate, then have a fresh reviewer recheck the resolution.

### Branch and PR lifecycle

- A material change includes executable code, tests, resources, manifests, build configuration, CI, repository policy, architecture/product contracts, or security/privacy documentation.
- Read-only diagnosis, review, research, planning, status, and explanation do not create branches or PRs. Minor incidental documentation changes create a PR only when requested.
- For a material non-feature task, create or reuse a `codex/<task-name>` branch and open a draft PR automatically. Spec-driven feature work uses the single branch and draft PR declared by its task plan.
- Make small coherent commits at meaningful checkpoints. Temporary fix commits are acceptable while the PR is draft; recommend squash merge, but only the human decides whether and how to merge.
- New commits invalidate affected local and CI evidence; rerun those gates in proportion to the change. Repeat independent review according to the risk-based cadence above and always after the final non-metadata change before readiness.
- Never merge a PR. Mark it ready only after every applicable completion criterion passes.
- This repository needs an explicitly approved baseline commit on `main` before the automatic branch/PR lifecycle can begin. Do not hide the uncommitted baseline inside a feature PR.

The PR description must contain the outcome contract, change summary, verification evidence, actual-app screenshots when required, independent-review result, and known risks or limitations. Prefer attaching screenshots to the PR description; do not commit them to the repository.

### Human merge gate

Treat the loop as operational only when `main` branch protection requires the `verify` GitHub Actions check, at least one approving human review, dismissal of stale approvals, and resolution of review conversations. Block direct and force pushes to `main` and do not allow the implementing agent to bypass protection.

## Architecture and coding conventions

### Boundaries

- Dependencies point inward: app -> feature -> domain/model; data implements domain-facing interfaces.
- Feature modules must not depend on other feature modules.
- Domain and model code stay pure Kotlin where practical and must not depend on Compose, Android UI, Room, network clients, ML Kit, RevenueCat, or AdMob.
- ViewModels coordinate UI state and use cases; they do not call external SDKs or database/network APIs directly.
- SDK integrations sit behind small interfaces (`ReceiptOcr`, repositories, billing/ads adapters) so tests can use fakes.
- Keep `:app` focused on application wiring, navigation, theme entry, and build configuration.
- Do not create a `common` dumping ground. Put behavior with the concept that owns it.

### Kotlin and coroutines

- Follow Kotlin official style and the formatting already used in the touched module.
- Prefer immutable data, explicit types at public boundaries, constructor injection, and exhaustive sealed states/events.
- Name screen state `*UiState`, events `*UiEvent`, and use cases `VerbNounUseCase`.
- Use structured concurrency. Inject dispatchers when production scheduling would otherwise make logic difficult to test.
- Expose observable state as `Flow`/`StateFlow`; collect it in Compose with lifecycle awareness.
- Do not swallow cancellation or broad exceptions. Map expected failures into typed domain/UI outcomes and preserve actionable diagnostics.
- Avoid clever generic abstractions, boolean flag clusters, and pass-through wrappers. Prefer cohesive interfaces that hide meaningful behavior.

### Compose UI

- Use unidirectional data flow: state down, events up. Prefer stateless screen/content composables with state hoisted to the caller.
- Accept `Modifier` on reusable UI elements and apply it to the outermost node. Put optional `modifier` before trailing callback/slot parameters.
- Use theme tokens and the BillSlice palette/type/shape rules from `DESIGN.md`; do not scatter raw colors, dimensions, or text styles through feature code.
- Keep user-facing text in resources. Use concise, calm, practical language and locale-aware currency formatting.
- Model loading, empty, error, disabled, warning, and success states explicitly. OCR loading should explain progress rather than show an unexplained spinner.
- Preserve edge-to-edge insets and keyboard behavior. Check compact and expanded widths, landscape, large font scale, and long text when layout is affected.
- Make controls accessible: meaningful labels, semantic roles/state, adequate contrast, non-color cues, logical traversal, and at least 48dp touch targets.
- Add or update previews when they materially speed review of meaningful UI states.

### Product-domain invariants

- Keep bill calculation outside composables and make it independently unit-testable.
- Tax defaults to subtotal plus service. Allocate tax, service, and receipt-level discount proportionally by item subtotal.
- Round each person's result to the nearest rupiah and assign the leftover rounding difference to the payer.
- Validate missing payer/participants, invalid amounts, unassigned items, and receipt-total mismatches as explicit outcomes.
- Preserve full editable bill data in history. Free-history visibility and Pro entitlement policy belong behind domain-facing seams, not in database queries or screen conditionals alone.

## Testing conventions

- Put fast JVM tests under `src/test` and device/Compose integration tests under `src/androidTest`.
- Prioritize unit coverage for bill math, allocation, rounding, quota, validation, parsing, mapping, and state reducers.
- Use deterministic fixtures and fakes; keep camera, OCR, network AI, billing, and ads out of the golden-path test.
- For Compose tests, query user-visible semantics (text, role, content description) before adding test tags. Never use `Thread.sleep`.
- Add a regression test for fixed bugs unless the behavior cannot reasonably be automated; explain that exception in the handoff.
- Test boundary and failure cases, not only the happy path. For bill math include zero/invalid inputs, shared items, discounts, and rounding remainders.
- Instrumented checks require a connected emulator/device. If unavailable, report that limitation and still run all relevant local compile, unit, lint, and assembly checks.

Canonical product fixture:

```text
Nasi Goreng Rp40.000 -> Dimas
Chicken Steak Rp60.000 -> Arya
Pizza Rp90.000 -> Dimas + Budi
Service 5%, Tax 10%, Total Rp219.450
Expected: Dimas Rp98.175, Arya Rp69.300, Budi Rp51.975
```

## Verify before finishing

Choose checks proportional to the change, starting targeted and widening before handoff:

```bash
# Discover tasks or validate Gradle configuration
./gradlew help

# Fast app compilation
./gradlew :app:compileDebugKotlin

# JVM tests
./gradlew testDebugUnitTest

# Static Android checks
./gradlew lintDebug

# Build installable debug artifact
./gradlew assembleDebug

# Device/emulator only
./gradlew connectedDebugAndroidTest

# Canonical local and CI gate
bundle exec fastlane android verify
```

Required verification by change type:

- Documentation-only: inspect the diff and validate referenced paths/commands.
- Kotlin/domain logic: targeted unit tests plus affected-module compilation.
- Compose/UI: affected compilation, relevant unit/UI tests, and manual or screenshot inspection when available.
- Resources/manifest/build configuration: `assembleDebug` and `lintDebug` at minimum.
- Cross-module or release-sensitive work: affected tests, `testDebugUnitTest`, `lintDebug`, and `assembleDebug`.

For UI-affecting changes, install and run the actual debug app on `Medium_Phone_API_36`, navigate the real user flow, inspect the rendered result, and capture at least one screenshot proving the outcome. Use before/after evidence for visual bug fixes or redesigns and add `Pixel_Tablet` evidence when adaptive layout is affected. Compose previews and screenshot-test renders are development aids; they do not count as PR evidence. Record the device/API and scenario in the PR. If required actual-app evidence cannot be captured or attached, keep the PR in draft.

Before declaring completion:

- Review `git diff --check` and the final diff for accidental churn, secrets, debug code, stale imports, and unrelated edits.
- Confirm every requested behavior is implemented and documented product invariants still hold.
- Confirm new dependencies and permissions are necessary and narrowly scoped.
- Report exactly which checks passed, failed, or were not run, including the reason. Never imply a check ran when it did not.

## Permissions, sandboxing, and external effects

- Work inside the provided repository and writable temporary directories. Respect the active sandbox and tool approval model.
- Read-only inspection within scope is allowed. Do not read unrelated personal files, credential stores, signing material, or environment secrets.
- If a required command is blocked by sandboxing or restricted network access, request the narrowest appropriate approval. Do not bypass restrictions or substitute an unsafe mechanism.
- Do not install global tools, change machine settings, launch GUI applications, or access external services unless the task requires it and permission is granted.
- Do not delete files, rewrite history, discard worktree changes, or run destructive Git commands without explicit authorization and an exact target review.
- A material coding task authorizes the branch, commit, push, and draft-PR lifecycle defined above after the repository baseline exists. Do not publish, deploy, upload builds to distribution services, send unrelated external messages, or merge unless the user explicitly asks.
- Never modify `local.properties`, signing configs, credentials, or production service configuration unless explicitly requested. Prefer documented placeholders and local-only configuration.
- Treat generated build outputs as disposable; do not add them to source control.

## Reusable Skills and MCP tools

Keep stable repo rules here. Extract a reusable Skill only when a multi-step workflow recurs across tasks and needs its own scripts, templates, or detailed decision tree. Add or use an MCP tool only when the workflow needs structured access to an external system (for example issue tracking, design files, CI, backend data, or release services).

When extending the harness:

- Put BillSlice-specific invariants and commands in this file.
- Put reusable Android expertise in a Skill, not duplicated prose.
- Prefer a small, auditable tool interface with least privilege.
- Document the trigger, inputs, outputs, permissions, failure behavior, and verification path.
- Do not add a Skill or MCP integration speculatively; first identify at least one concrete repeated workflow it simplifies.

## Handoff format

Finish with a concise summary of:

1. What changed and why.
2. Files materially changed.
3. Verification performed and its result.
4. Remaining risks, device-only checks, or follow-up work.

Do not claim the whole app is correct merely because one task passed. State the evidence you actually have.
