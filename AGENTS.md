# BillSlice Agent Harness

This is the repository-wide operating guide for coding agents. Keep it accurate as the project evolves. A more specific `AGENTS.md` in a subdirectory may add local rules for that subtree.

## RTK command optimization

Use [RTK](https://github.com/rtk-ai/rtk) as the token-optimized proxy for shell commands whenever it supports the command being run.

```bash
rtk git status
rtk git diff
rtk test ./gradlew test
rtk gain --project
```

For commands without a dedicated RTK wrapper, use `rtk proxy <command>` when tracking is useful. If RTK filtering hides information needed to diagnose a failure, inspect the saved full output or rerun the narrow command without filtering.

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

## Conditional playbook routing

Keep the always-loaded context small. Read each playbook below only when its trigger applies; multiple playbooks may apply to one task. These files contain mandatory repository rules, not optional guidance.

- Any material repository change: read [`docs/agent/outcome-contract.md`](docs/agent/outcome-contract.md), [`docs/agent/delivery-loop.md`](docs/agent/delivery-loop.md), [`docs/agent/verification.md`](docs/agent/verification.md), [`docs/agent/operations.md`](docs/agent/operations.md), and [`docs/agent/handoff.md`](docs/agent/handoff.md). Material changes include executable code, tests, resources, manifests, build configuration, CI, repository policy, architecture/product contracts, and security/privacy documentation.
- Spec- and task-driven feature work: also read [`docs/agent/feature-workflow.md`](docs/agent/feature-workflow.md).
- Writing, reviewing, or refactoring source code, tests, resources, manifests, or build code—including pure Kotlin domain/model work: also read [`docs/agent/android-engineering.md`](docs/agent/android-engineering.md).
- Documentation-only changes that are not material: read [`docs/agent/verification.md`](docs/agent/verification.md), [`docs/agent/operations.md`](docs/agent/operations.md), and [`docs/agent/handoff.md`](docs/agent/handoff.md).
- Read-only diagnosis, review, research, planning, or status work: read the domain playbook relevant to the inspected material; do not load material-change playbooks solely because repository files are being read.
- Before any external effect, sandbox approval, credential-dependent action, or destructive operation: read [`docs/agent/operations.md`](docs/agent/operations.md).
- When extending the harness, adding a reusable Skill, or adding an MCP tool: read [`docs/agent/harness-extension.md`](docs/agent/harness-extension.md).
- Before handing completed repository work back to the user: read [`docs/agent/handoff.md`](docs/agent/handoff.md).

Do not read every playbook preemptively. Load a newly applicable playbook before taking the action that triggers it.
