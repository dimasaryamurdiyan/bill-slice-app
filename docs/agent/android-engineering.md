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
Pizza Rp90.000 -> Budi
Service 5%, Tax 10%, Total Rp219.450
Expected: Dimas Rp46.200, Arya Rp69.300, Budi Rp103.950
```
