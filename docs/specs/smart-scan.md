# Smart Scan

- Status: Approved
- Owner: Human
- Last updated: 2026-08-14

## Context

Smart Scan accelerates receipt entry but cannot be required for splitting. It covers Receipt Capture, Smart Scan loading, and Receipt Review in `billslice.pen`. The image stays on-device; `:core:ocr` extracts text with unbundled ML Kit; `:core:network` sends OCR text to `POST /smart-scan/parse`; `:core:data` implements the domain repository; `:feature:bill` owns UI/session state ([`ARCHITECTURE.md`](../../ARCHITECTURE.md)).

## Outcome

Users can capture or import a receipt, see transparent OCR/cloud progress, receive an editable confirmed draft, and recover through retry or manual entry from every permission, model, OCR, quota, network, or parse failure without uploading the image.

## Reference screens

| Receipt Capture | Smart Scan loading | Receipt Review |
|---|---|---|
| ![Receipt Capture](../assets/screens/02-receipt-capture.png) | ![Smart Scan loading](../assets/screens/03-smart-scan-loading.png) | ![Receipt Review](../assets/screens/04-receipt-review.png) |

These exports show the successful phone journey. Permission denial, quota, cancellation, offline, retry, and other failure states are required by this specification even where the Pencil artifact does not show a separate frame.

## User scenarios

- Capture/import succeeds → OCR text → backend draft/warnings → editable Review → explicit confirmation.
- Camera permission denied, OCR/model/network/backend fails, or quota is exhausted → actionable state with manual entry always available.
- User cancels loading → no duplicate request, no persisted OCR/image, predictable return.

## Functional requirements

- `FR-001` — Capture supports camera and supported system image import; camera permission is requested only after choosing camera and denial does not affect import/manual entry.
- `FR-002` — Receipt images remain on-device and are not sent to backend, stored in History, or logged.
- `FR-003` — Unbundled ML Kit OCR is the initial adapter behind `ReceiptOcr`; model-download, recognition, cancellation, and unexpected failures are typed.
- `FR-004` — Only install ID, locale, currency, timezone, and OCR text may be sent over HTTPS to `POST /smart-scan/parse`.
- `FR-005` — Backend authoritatively enforces five Free Smart Scans per calendar month, returns dynamic reset/quota outcome, checks entitlement when available, calls OpenAI server-side, and stores only minimal documented logs.
- `FR-006` — Full OCR text is not persisted or logged by default on device or backend; secrets never enter app source/logs/tests/artifacts.
- `FR-007` — Loading communicates OCR, structuring, and total-checking steps, prevents duplicate submissions, and supports cancellation without a generic unexplained spinner.
- `FR-008` — Success maps to a structured receipt draft plus warnings; every calculation input is editable and explicit confirmation is required before people/assignment.
- `FR-009` — Review validates malformed/invalid fields individually and never silently replaces them with zero or presents AI output as final.
- `FR-010` — Distinct failure states cover permission, import/image, model download, OCR, offline, timeout, server rejection, quota, malformed response, cancellation, and unexpected failure without raw exception copy.
- `FR-011` — Every terminal failure/quota state exposes Manual Item Entry; retry appears only when meaningful and preserves user-confirmed structured values.
- `FR-012` — Transient image references and unconfirmed OCR text survive only as needed for the active local session and are not restored into long-term state.

## Business rules and invariants

Manual fallback and confirmation are mandatory. Image remains local; backend receives text only; no full OCR retention by default ([`PRODUCT.md`](../../PRODUCT.md), “Smart Scan”; [`docs/product-plan.md`](../product-plan.md), “Smart Scan” and “Backend Architecture”). Free quota is five/month with dynamic reset. Domain models remain exact/global-ready; parsing never calculates the authoritative split.

## States and failure behavior

Cover idle, permission request/denial, import/capture, model download, OCR, structuring, total checking, editable success, quota, offline, timeout, server/malformed failure, cancellation, retry, and unrecoverable configuration. Preserve coroutine cancellation and prevent duplicate submissions.

## UX requirements

Match the three Pencil screens and Review draft treatment in `DESIGN.md`. Manual entry stays visible. Progress uses step rows/skeletons. AI draft/privacy/warnings use explicit text and non-color cues. Test compact, large font, landscape, and expanded layouts.

## Data and boundary contracts

Request contract is exactly the JSON documented in `docs/product-plan.md`; image never crosses `ReceiptOcr`. Response produces structured draft/warnings/quota or typed failure. Android/backend meet only at `:core:network`. Server owns OpenAI secret, prompt/schema, quota persistence, and minimal logs. Backend source/deployment availability is an implementation precondition, not permission to embed secrets.

## Acceptance criteria

- `AC-001` — Covers `FR-001`: Camera/import work; permission is narrow/recoverable; manual entry remains available.
- `AC-002` — Covers `FR-002`, `FR-004`, `FR-006`: Request/storage/log inspection proves image never leaves device and full OCR is not persisted/logged.
- `AC-003` — Covers `FR-003`, `FR-010`: OCR adapter tests cover success, model, recognition, cancellation, and failure.
- `AC-004` — Covers `FR-004`, `FR-005`: Client/backend contract tests cover allowed request fields, five-scan quota/reset, entitlement input, warnings, success, and typed errors.
- `AC-005` — Covers `FR-007`, `FR-010`, `FR-011`: Loading/failure UI tests prove steps, cancellation, no duplicate, retry, quota, and manual fallback.
- `AC-006` — Covers `FR-008`, `FR-009`: Parsed data is visibly editable, individually validated, and confirmation-gated.
- `AC-007` — Covers `FR-012`: Restoration/storage tests find no long-term image/unconfirmed OCR persistence.
- `AC-008` — All three screens and key states pass actual-app adaptive/accessibility inspection.
- `AC-009` — Targeted OCR/network/domain/UI tests, compile, lint/build, CI, privacy review, and complete-diff review pass.

## Non-goals

Bundled OCR without measured approval, image upload/storage/gallery, OCR diagnostics opt-in, direct OpenAI calls, final bill math, manual split implementation, production entitlement hardening, or offline AI parsing.

## Assumptions

Use unbundled ML Kit first. Backend endpoint/test environment is supplied separately if source is not in this Android repository. Pro scan allowance is backend policy; Free is exactly five/month.

## Open decisions

None.

## Verification matrix

| Acceptance criterion | Evidence required | Proposed verification |
|---|---|---|
| `AC-001` | Capture/permission | Instrumented/UI tests and actual app |
| `AC-002` | Privacy | Request, storage, logs, diff inspection |
| `AC-003` | OCR outcomes | Adapter tests with sanitized fixtures |
| `AC-004` | Client/backend/quota | Fake-server and test-backend contract tests |
| `AC-005` | State machine/fallback | ViewModel/coroutine/Compose tests |
| `AC-006` | Editable confirmation | Review UI/integration tests |
| `AC-007` | Transient retention | Restoration and storage inspection |
| `AC-008` | UX | Phone/tablet actual-app evidence |
| `AC-009` | Gates/review | Gradle/Fastlane/CI and privacy-focused diff review |
