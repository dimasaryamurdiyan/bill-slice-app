# Local bill history

- Status: Approved
- Owner: Human
- Last updated: 2026-08-14

## Context

v0.1 must store full editable bill data locally, show Free users their five most recent bills, and reopen a saved bill. `:feature:history` owns History UI; `:core:domain` owns repository/use-case and visibility interfaces; `:core:data` maps; `:core:database` owns Room/schema/migrations ([`ARCHITECTURE.md`](../../ARCHITECTURE.md)). The relevant Pencil screen is History, with recent context also consumed by Home.

## Outcome

After calculating a bill, users can save it offline, see newest recent bills, and reopen the same complete editable draft without receipt images, OCR text, duplicates, or data loss.

## Reference screen

![History](../assets/screens/11-history.png)

This export is the phone reference from `billslice.pen`; empty, failure, adaptive, and accessibility states remain part of the specification even when not pictured.

## User scenarios

- Given a calculated bill, Save stores it and reports success only after persistence succeeds.
- Given five or more saved bills, Free History shows the five newest accessible bills newest first.
- Given a saved bill is reopened, every editable field, assignment, payer, warning, and result is restored offline.
- Given an existing bill is edited and saved, it updates that bill rather than duplicating it.

## Functional requirements

- `FR-001` — Saved data must include stable bill ID, merchant/title, date/time, currency, items, participants, one-owner assignments, payer, service, tax, discount, confirmed receipt total, useful structured warnings, and final totals.
- `FR-002` — Receipt images and full OCR text must not be stored in history by default.
- `FR-003` — Save must be atomic from the caller’s perspective and return typed success/failure; failure retains the in-memory bill and offers retry.
- `FR-004` — Saving an existing ID updates it; starting a new bill creates a new ID.
- `FR-005` — History orders newest first and has loading, empty, populated, retryable failure, and unrecoverable local-storage states.
- `FR-006` — Free users can access the five most recent bills; active Pro can access all locally retained bills. Visibility policy belongs in domain behavior, not DAO limits or screen conditionals alone.
- `FR-007` — Reopen restores full editable state and invalidates/recalculates derived output only when persisted input/result consistency requires it.
- `FR-008` — Manual save/reopen/history use must work without network, account, billing availability, OCR, or ads.
- `FR-009` — Database changes require versioned schemas and non-destructive migrations; release code must not use destructive fallback.
- `FR-010` — Structured BillSlice history must be excluded from cloud backup/device transfer until a separate human-approved backup contract exists.
- `FR-011` — Home may observe a recent-bill summary through the domain interface without depending on `:feature:history` or Room.

## Business rules and invariants

Full editable structured bills are retained locally; Free visibility is five newest and Pro visibility is unlimited retained history ([`PRODUCT.md`](../../PRODUCT.md), “Must Have”; [`docs/product-plan.md`](../product-plan.md), “Local History”). Receipt images remain absent. The database is the source of truth; feature modules never call DAOs.

## States and failure behavior

Cover database initialization, empty, populated, saving, saved, updating, retryable failure, migration failure, missing bill ID, and partially corrupt row mapping. Never report a save before commit or erase the in-memory result on failure.

## UX requirements

Match History/empty state in `billslice.pen` and general `DESIGN.md`. Rows expose merchant/date/people/total and reopen affordance with semantic labels. Long text and large font must remain readable; ads are specified separately.

## Data and boundary contracts

`BillRepository`, `SaveBillUseCase`, `ObserveRecentBillsUseCase`, and `GetBillUseCase` are domain-facing test surfaces. Room entities/DAOs and mapping remain behind `:core:data`/`:core:database`. No cloud service is called. Backup rules exclude the database and any related structured history files.

## Acceptance criteria

- `AC-001` — Covers `FR-001`, `FR-007`: Repository round-trip restores every editable field and final result.
- `AC-002` — Covers `FR-002`, `FR-010`: Database/schema/backup inspection finds no image/full OCR storage or cloud backup path.
- `AC-003` — Covers `FR-003`, `FR-004`: Save/update/retry tests prove atomic truthful behavior without duplicates.
- `AC-004` — Covers `FR-005`, `FR-006`: Free History shows five newest; Pro policy exposes all retained; empty/failure states render.
- `AC-005` — Covers `FR-008`, `FR-011`: Save, Home recent context, History, and reopen work offline without feature-to-feature dependency.
- `AC-006` — Covers `FR-009`: Schema export and migration tests pass without destructive fallback.
- `AC-007` — History actual-app/adaptive/accessibility inspection passes.
- `AC-008` — Targeted repository/database/UI tests, compilation, lint/build, CI, and complete-diff review pass.

## Non-goals

Receipt gallery, image storage, cloud sync/backup, accounts, deletion/retention-management UI, saved groups, search/filter, or duplicate-group workflows.

## Assumptions

Rows may be retained beyond five so later Pro activation can expose them; Free visibility does not imply destructive deletion. Cloud backup remains disabled for structured history.

## Open decisions

None.

## Verification matrix

| Acceptance criterion | Evidence required | Proposed verification |
|---|---|---|
| `AC-001` | Full round trip | Repository/Room integration tests |
| `AC-002` | Privacy/backup | Schema, file, and backup-rule inspection |
| `AC-003` | Atomic save/update | Failure/transaction integration tests |
| `AC-004` | Visibility/states | Domain policy and Compose tests |
| `AC-005` | Offline/module isolation | Offline integration and dependency report |
| `AC-006` | Migration safety | Room schema/migration tests |
| `AC-007` | UX | Phone/tablet actual-app evidence |
| `AC-008` | Gates | Gradle/Fastlane/CI and diff review |
