# BillSlice Product Roadmap

Last updated: 2026-08-12

## Product Promise

BillSlice helps friend groups split restaurant receipts in under a minute.

The first market is small friend groups eating out in Indonesia. The product should stay global-ready, but v0.1 optimizes for IDR, Indonesian restaurant receipts, WhatsApp sharing, and a fast closed-test release.

Shipaton is the launch deadline, not the product definition. BillSlice should be a real product that can be submitted to Shipaton with a closed-testing build, demo video, and product assets if Google Play production review is still pending.

## Success Metric

The first closed-test success metric is:

> At least 80% of testers complete one real bill split without help.

With 12 closed testers, this means at least 10 testers should finish:

```text
Start bill
-> scan or enter items
-> review
-> add people
-> assign items
-> calculate
-> share result
```

## v0.1 Closed Test MVP

Target: 2026-08-16 closed testing build.

### Goal

Ship a usable Android MVP that lets Indonesian friend groups split a restaurant receipt quickly and reliably, even when OCR or AI fails.

### Core Flow

```text
Take/import receipt photo
-> on-device ML Kit OCR draft
-> automatic cloud-AI parsing
-> user reviews/corrects
-> add people
-> assign items
-> calculate tax/service/discount
-> show each person's total
-> share result
```

Manual item entry must be a first-class fallback.

### Must Have

- Receipt photo import or camera entry point.
- On-device OCR draft using ML Kit.
- Automatic cloud-AI parsing after OCR.
- Editable receipt review screen.
- Manual item entry.
- Add people.
- Assign an item to one person.
- One payer.
- Tax, service, and receipt-level discount.
- Proportional allocation by item subtotal.
- Tax default formula: tax on subtotal plus service.
- Round each person's total to the nearest rupiah.
- Assign leftover rounding difference to the payer.
- Per-person total screen.
- WhatsApp/text share.
- Last 5 bills stored locally as full editable bill data.
- One selected currency per bill, default `IDR`.
- Warning states when totals do not match.
- Minimal RevenueCat lifetime Pro setup.
- Test AdMob ad slots outside the active split flow.

### Cut From v0.1

- User accounts.
- Cloud sync.
- Saved groups.
- Receipt gallery.
- Full multi-currency support.
- FX conversion.
- Image share cards.
- Interstitial ads.
- Subscriptions.

### Smart Scan

v0.1 uses this privacy-preserving flow:

```text
Receipt image stays on device
-> ML Kit extracts OCR text
-> OCR text is sent to Supabase Edge Function
-> OpenAI parses structured receipt data
-> App shows editable review
-> User confirms before assignment
```

Rules:

- Backend receives OCR text only.
- Backend does not receive the original receipt image.
- AI output is always a draft.
- User confirmation is always required.
- If OCR or AI fails, user can manually enter the bill.

### Backend

Use Supabase Edge Functions.

Initial endpoint:

```http
POST /smart-scan/parse
```

Responsibilities:

- Check monthly Smart Scan quota.
- Check Pro entitlement when available.
- Call OpenAI with a server-side API key.
- Return structured receipt JSON and warnings.
- Store minimal usage/log data.

Do not store full OCR text by default unless the user opts into debugging or support diagnostics.

### Monetization

Launch with Free plus Lifetime Pro. No subscription in v1.0.

Free:

- Unlimited manual splitting.
- 5 Smart Scans per month.
- Basic text sharing.
- Last 5 bills.
- Basic split options.
- Ads outside active split flow.

Pro Lifetime:

- Everything in Free.
- No ads.
- Unlimited history.
- Saved groups.
- Advanced splitting.
- Premium share layouts.
- Generous/fair-use Smart Scan.

Initial Pro price hypothesis:

- Rp79k.
- Test range: Rp69k-Rp99k.

### Ads

Use AdMob test ads in v0.1.

Ads must never appear between:

```text
Scan -> Review -> Add People -> Assign -> Calculate
```

Allowed placements:

- Home screen native/banner.
- History screen native ad, maximum 1-2 ads per screen.
- Result screen native/banner only after totals are visible.

### Required Tests

Unit tests:

- Bill math.
- Proportional tax/service/discount.
- Rounding and leftover assignment.
- Smart Scan quota logic.
- Parser validation.

Compose UI tests:

- Screen states.
- Form validation.
- Assignment behavior.
- Warning states.
- State restoration.

Maestro golden path:

```text
Launch
-> open test receipt
-> review items
-> add 3 people
-> assign items
-> calculate
-> verify totals
-> save
-> reopen from history
```

Deterministic fixture:

```text
Nasi Goreng Rp40.000 -> Dimas
Chicken Steak Rp60.000 -> Arya
Pizza Rp90.000 -> Budi
Service 5%
Tax 10%
Total Rp219.450
```

Expected:

```text
Dimas: Rp46.200
Arya: Rp69.300
Budi: Rp103.950
Combined: Rp219.450
```

Keep OCR, AI, RevenueCat, ads, and camera out of this golden-path test.

## v0.2 Shipaton Polish

Target: late August to early September 2026.

### Goal

Make the product demo feel reliable, clear, and submission-ready while the Google Play production timeline runs.

### Scope

- Improve scan and review UX.
- Improve empty, loading, and error states.
- Add deterministic demo receipt mode.
- Improve result screen for screenshots and demo video.
- Improve WhatsApp/text share formatting.
- Add compact and detailed share modes if time allows.
- Prepare app store screenshots.
- Prepare Shipaton demo video.
- Measure release AAB/APK size.
- Replace test ads with real AdMob units only if the AdMob account/app is approved.

### Success Criteria

- Closed testers can complete the golden path without guidance.
- Demo video clearly shows scan/review/split/share.
- Release build size is measured and documented.
- Shipaton submission does not depend on Play production approval.

## v1.0 Public Release

Target: after closed testing requirements and Google Play production access are complete.

### Goal

Publish a stable Android version with the v0.1 core flow, production-safe monetization, and enough polish for real users.

### Scope

- Harden Smart Scan quota enforcement.
- Harden RevenueCat Pro entitlement checks.
- Use real AdMob IDs only when approved.
- Keep manual splitting unlimited.
- Keep text sharing as the default share mode.
- Improve crash/error reporting if available.
- Confirm app size from release AAB/APK.
- Finalize Play Store listing copy and screenshots.

### Non-Goals

- No subscription.
- No cloud sync.
- No account system.
- No receipt gallery by default.
- No FX conversion.

## v1.1 Advanced Splitting and Global-Ready Parsing

### Goal

Improve the split cases that happen often in real restaurants and make receipt parsing more robust outside the initial Indonesian use case.

### Scope

- Split one item between multiple people.
- Quantity/custom split.
- Better AI OCR correction.
- Better receipt parsing by locale.
- Multi-currency formatting presets.
- Advanced split options begin moving behind Pro.
- Manual tax/service allocation override if ready.
- Rounding adjustment if ready.

### Monetization

Pro should become meaningfully valuable here:

- Advanced splitting.
- More Smart Scan usage.
- No ads.

## v1.2 History, Groups, and Share Cards

### Goal

Make repeat usage easier and make shared results feel polished.

### Scope

- Improved history.
- Unlimited history for Pro.
- Saved groups.
- Reopen and edit past bills.
- Duplicate a past group.
- Receipt gallery with explicit user permission.
- Premium image share cards.
- Branded visual summary layouts.
- Keep copyable text share as fallback.

### Privacy

- Ask before saving receipt images.
- Store receipt images locally first.
- Let users delete receipt images independently from bill history.

## v1.3 Cloud and Recurring Value

### Goal

Add recurring-value features only after local product usage is proven.

### Scope

- Cloud sync.
- Group history.
- Cross-device backup.
- Export/share history.
- Higher/fair-use AI scan limits.
- Optional subscription only if recurring cloud value exists.

### Subscription Gate

Do not add a subscription just because RevenueCat supports it. Add it only if users clearly value recurring features:

- Cloud sync.
- Cross-device backup.
- Group history.
- High ongoing Smart Scan usage.

## Later Bets

Consider only after the core product is working:

- FX conversion for travelers.
- Multi-currency bills.
- Item-specific discounts.
- Payment status tracking.
- Settlement tracking.
- Team/office reimbursement workflows.
- Cloud receipt image sync.
