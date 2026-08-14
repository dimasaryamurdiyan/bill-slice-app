# Lifetime Pro implementation plan

- Status: Proposed
- Specification: [`docs/specs/lifetime-pro.md`](../specs/lifetime-pro.md)
- Branch: `codex/lifetime-pro`

## Outcome contract

- **Outcome:** Implement reactive Lifetime Pro purchase/restore behind a RevenueCat adapter while Free behavior remains usable.
- **Acceptance criteria:** `AC-001`–`AC-008`.
- **Non-goals:** See specification.
- **Verification:** Policy/adapter/UI tests, sandbox actual app, security/build/review gates.

## Preconditions

App shell, history, and Smart Scan policy seams available; RevenueCat test project/offering with `pro`/`pro_lifetime`; publishable config only; clean baseline/draft PR; test purchaser environment.

## Vertical slices

- [ ] `T-001` — Establish green baseline and fake entitlement scenarios
  - Covers: `AC-008`
  - Result: Baseline green and deterministic Free/Pro/purchase/restore fixtures exist.
  - Likely scope: read-only checks and `:core:testing` fakes.
  - Verification: existing gates and fake tests.
  - Depends on: none
- [ ] `T-002` — Implement entitlement model/policy test-first
  - Covers: `FR-005`–`FR-009`; `AC-004`, `AC-005`
  - Result: Pure entitlement and capability policy drives consumers without SDK types.
  - Likely scope: `:core:model`, `:core:domain`, tests.
  - Verification: JVM policy/reactivity tests.
  - Depends on: `T-001`
- [ ] `T-003` — Implement RevenueCat adapter and safe app wiring
  - Covers: `FR-002`–`FR-006`, `FR-009`, `FR-010`; `AC-002`–`AC-004`, `AC-006`
  - Result: `:core:billing` maps offering/customer/purchase/restore states through domain seam using install ID.
  - Likely scope: `:core:billing`, `:app` DI/config, catalog, adapter tests.
  - Verification: adapter tests, compilation, dependency/secret inspection.
  - Depends on: `T-002`
- [ ] `T-004` — Implement Lifetime Pro UI states
  - Covers: `FR-001`–`FR-004`, `FR-009`; `AC-001`–`AC-003`
  - Result: Paywall shows localized offering and complete purchase/restore state machine.
  - Likely scope: `:feature:paywall`, resources, ViewModel/Compose tests.
  - Verification: fake-offering/state UI tests.
  - Depends on: `T-003`
- [ ] `T-005` — Integrate entitlement consumers and Settings
  - Covers: `FR-007`, `FR-008`; `AC-005`
  - Result: History/quota/Settings/ad policy observe entitlement without feature coupling or restart.
  - Likely scope: domain/data wiring and affected feature tests.
  - Verification: integration/reactivity tests and dependency report.
  - Depends on: `T-004`
- [ ] `T-006` — Cover offline/stale/cancellation/failure behavior
  - Covers: `FR-003`, `FR-004`, `FR-009`; `AC-002`, `AC-003`, `AC-005`
  - Result: Optional billing failure never blocks Free use or falsely activates Pro.
  - Likely scope: adapter/ViewModel tests/fixes.
  - Verification: failure/coroutine/UI tests.
  - Depends on: `T-005`
- [ ] `T-007` — Update entitlement implementation docs
  - Covers: `FR-001`, `FR-005`–`FR-010`; `AC-004`, `AC-006`
  - Result: Implemented seam/config/benefits accurately documented without secrets.
  - Likely scope: `ARCHITECTURE.md` and contract docs if changed.
  - Verification: docs/secret review.
  - Depends on: `T-006`
- [ ] `T-008` — Run targeted and aggregate gates
  - Covers: `AC-008`
  - Result: Billing/domain/UI tests, compilation, lint, and assembly pass.
  - Likely scope: verification.
  - Verification: affected tasks and repository gates.
  - Depends on: `T-007`
- [ ] `T-009` — Inspect sandbox purchase/restore and adaptive UI
  - Covers: `AC-001`–`AC-003`, `AC-005`, `AC-007`
  - Result: Test purchase/restore and phone/tablet/accessibility evidence pass; otherwise PR stays draft with blocker.
  - Likely scope: actual app/sandbox evidence.
  - Verification: RevenueCat/Play test flow and screenshots.
  - Depends on: `T-008`
- [ ] `T-010` — Review the complete diff against spec and standards
  - Covers: `AC-001`–`AC-008`
  - Result: A fresh reviewer reports no unresolved blocking billing, identity, security, entitlement, or standards finding.
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

Risks are stale entitlement, wrong identity, misleading price/benefits, and leaked config. Roll back adapter/paywall wiring at the seam; Free/manual behavior remains.

## Completion checklist

- [ ] `AC-001`–`AC-008` evidenced.
- [ ] Sandbox purchase/restore or explicit draft blocker recorded.
- [ ] Tests/build/security/actual-app/CI/review pass.
