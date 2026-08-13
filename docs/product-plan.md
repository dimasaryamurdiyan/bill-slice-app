# BillSlice Product Plan

Last updated: 2026-08-12

## Product Direction

BillSlice is a mobile app for splitting restaurant receipts with friends. The first target market is small friend groups eating out in Indonesia. The product should be global-ready in its data model, but v0.1 is Indonesia-first.

Core promise:

> Split restaurant receipts with friends in under a minute.

Shipaton is the launch deadline, not the product definition. The app should be built as a real product that can be submitted to Shipaton.

## Launch Goals

Closed testing target: 2026-08-16.

Production target depends on Google Play review timing. For new personal Play Console accounts, plan around the 12-tester closed testing requirement and the 14-day testing window before production access.

Shipaton submission should not depend exclusively on Play production being live. Prepare a closed testing build plus demo video and product assets.

Closed-test success metric:

- At least 80% of testers complete one real bill split without help.
- With 12 testers, this means at least 10 successful completions.

Primary completion flow:

```text
Start bill
-> scan or enter items
-> review
-> add people
-> assign items
-> calculate
-> share result
```

## v0.1 Scope

Must have:

- Receipt photo import or camera entry point.
- On-device OCR draft using ML Kit.
- Automatic cloud-AI parsing after OCR.
- Editable receipt review.
- Manual item entry as a first-class fallback.
- Add people.
- Assign an item to one person.
- One payer.
- Tax, service, and receipt-level discount.
- Proportional allocation by item subtotal.
- Per-person total.
- WhatsApp/text share.
- Last 5 bills stored locally.
- Single currency per bill, default `IDR`.
- Warning states when totals do not match.
- Minimal RevenueCat lifetime Pro setup.
- Test ads only, placed outside the active split flow.

Cut from v0.1:

- User accounts.
- Cloud sync.
- Saved groups.
- Receipt gallery.
- Full multi-currency or FX conversion.
- Image share cards.
- Interstitial ads.
- Subscription.

## Smart Scan

v0.1 flow:

```text
Receipt image stays on device
-> ML Kit extracts OCR text
-> OCR text is sent to Supabase Edge Function
-> OpenAI parses structured receipt data
-> App shows editable review
-> User confirms before assignment
```

Rules:

- Receipt images stay on device.
- Backend receives OCR text only.
- Backend does not receive the original image.
- AI output is always treated as a draft.
- User confirmation is required before splitting.
- If OCR or AI fails, user can manually enter items.

Use unbundled ML Kit OCR first to keep app size lower. During closed testing, measure whether the one-time scanner model download causes unacceptable friction. Switch to bundled OCR only if reliability requires it.

## Backend Architecture

Use Supabase Edge Functions for v0.1.

Initial endpoint:

```http
POST /smart-scan/parse
```

Request:

```json
{
  "installId": "anonymous-device-id",
  "locale": "id-ID",
  "currency": "IDR",
  "timezone": "Asia/Jakarta",
  "ocrText": "raw receipt OCR text here"
}
```

Backend responsibilities:

- Check monthly Smart Scan quota.
- Check Pro entitlement when available.
- Call OpenAI using a server-side API key.
- Return structured receipt JSON.
- Return parse warnings.
- Store minimal usage/log data.

Minimal tables:

```text
scan_usage
- install_id
- month_key
- scan_count
- updated_at

smart_scan_logs
- id
- install_id
- created_at
- success
- model
- input_hash
- error_code
```

Do not store full OCR text by default unless the user explicitly opts into debugging or support diagnostics.

RevenueCat identity:

```text
installId == RevenueCat appUserId
```

For closed testing, app-side Pro gating is acceptable. Before production, backend entitlement checks should be hardened.

## Local History

Free users can access the last 5 bills. Pro later unlocks unlimited history.

v0.1 stores full editable bill data locally:

- Merchant/title.
- Date/time.
- Participants.
- Items.
- Assignments.
- Tax.
- Service.
- Discount.
- Payer.
- Final totals.
- Currency.
- OCR/AI warnings if useful.

Do not store receipt images by default in v0.1.

## Currency

v0.1 rules:

- Each bill has one selected currency.
- Default currency is `IDR`.
- No exchange-rate conversion.
- No mixed-currency bill.
- Format totals based on selected currency.
- Backend receives `currency` and `locale` to help parse OCR.

## Bill Math

Default formula:

```text
subtotal = sum(items)
service = subtotal * serviceRate
tax = (subtotal + service) * taxRate
total = subtotal + service + tax - discount
```

Allocation rules:

- Item assignment determines each person subtotal.
- Tax is split proportionally by item subtotal.
- Service is split proportionally by item subtotal.
- Receipt-level discount is split proportionally by item subtotal.
- Discount reduces each person's share.
- Round each person's total to the nearest rupiah.
- Assign leftover rounding difference to the payer.

Total validation states:

- `Looks good`: item subtotal + service + tax - discount equals grand total.
- `Needs review`: detected difference exists.
- `Missing total`: grand total was not detected.

Warnings do not block the user. The final split uses user-confirmed values.

## Sharing

v0.1 default sharing format is WhatsApp-friendly text.

Example:

```text
BillSlice result

Paid by: Rina

Budi owes Rina: Rp58.000
Ayu owes Rina: Rp91.000
Rina's share: Rp72.000

Tax/service included.
```

v1.2 can add branded image share cards. Text sharing should remain available as fallback.

## Monetization

Launch with Free plus Lifetime Pro. Do not add subscriptions in v1.0.

Suggested initial hypothesis:

- Pro Lifetime: Rp79k.
- Acceptable test range: Rp69k-Rp99k.

Free:

- Unlimited manual splitting.
- 5 Smart Scans per month.
- Basic text sharing.
- Last 5 bills.
- Basic split options.
- Ads outside active split flow.
- Limited AI enhancement.

Pro Lifetime:

- Everything in Free.
- No ads.
- Unlimited history.
- Saved groups.
- Advanced splitting.
- Premium share layouts.
- Generous/fair-use Smart Scan.

RevenueCat setup:

- SDK integrated in v0.1.
- Entitlement: `pro`.
- Lifetime product: `pro_lifetime`.
- Simple paywall.
- Hide ads and unlock Pro gates when `pro` is active.

Smart Scan quota copy:

```text
You've used your 5 free Smart Scans this month.
Your free scans reset on September 1.
Upgrade to Pro for more Smart Scans, or continue by entering the bill manually.
```

The reset date must be dynamic.

## Ads

Use AdMob for ad serving. Use Google test ad unit IDs in v0.1 because there is no approved AdMob account yet.

Do not use RevenueCat Ads in v0.1. Consider it later for ad revenue analytics after AdMob is approved and real ads are live.

Important rule:

> Ads must never interrupt someone while they are splitting a bill.

No ads between:

```text
Scan -> Review -> Add People -> Assign -> Calculate
```

Allowed placements:

- Home screen: small native/banner ad.
- History screen: native ad, maximum 1-2 ads per screen.
- Result screen: optional native/banner after totals are already visible.

Avoid:

- Interstitials.
- App-open ads.
- Rewarded ads.
- Ads before totals.
- Ads before sharing.

## Free vs Pro Split Options

Free basic splitting:

- Assign an item to one person.
- Proportional tax, service, and discount.
- One payer.
- WhatsApp/text share.

Pro advanced splitting:

- Split one item between multiple people.
- Quantity/custom split.
- Manual tax/service allocation override.
- Rounding adjustment.
- Saved groups.
- Premium image share layouts.

## Testing Strategy

Use three layers:

- Unit tests for bill math, proportional allocation, rounding, quotas, and parser validation.
- Compose UI tests for screen states, form validation, assignment behavior, warnings, and state restoration.
- Maestro tests for full user journeys.

Real receipt image testing:

- Use a small sanitized fixture set.
- Include clean receipt, messy OCR receipt, discount receipt, tax/service receipt, and missing-total receipt.
- Keep private receipt data out of the repo unless sanitized.
- Keep OCR, AI, RevenueCat, ads, and camera out of the main golden-path test.

## Maestro Golden Path

Use a deterministic fixture, not real OCR/camera.

Flow:

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

Fixture:

```text
Nasi Goreng Rp40.000 -> Dimas
Chicken Steak Rp60.000 -> Arya
Pizza Rp90.000 -> Dimas + Budi
Service 5%
Tax 10%
Total Rp219.450
```

Tax calculation uses subtotal plus service:

```text
Subtotal: Rp190.000
Service 5%: Rp9.500
Tax 10% of Rp199.500: Rp19.950
Total: Rp219.450
```

Expected split:

```text
Dimas: Rp98.175
Arya: Rp69.300
Budi: Rp51.975
Combined: Rp219.450
```

The test passes only if:

- Calculations are correct.
- Bill saves successfully.
- Reopening history restores editable items.
- Reopening history restores participants.
- Reopening history restores assignments.

## Roadmap

### v0.2 Shipaton Polish

- Improve scan/review UX.
- Improve empty, loading, and error states.
- Add demo receipt mode.
- Improve result screen for screenshots/video.
- Improve share text formatting.
- Prepare app store screenshots and demo video.
- Measure release AAB/APK size.

### v1.1

- More reliable AI OCR correction.
- Split individual item between people.
- Multi-currency formatting presets.
- Better receipt parsing by locale.
- Advanced split options start moving behind Pro.

### v1.2

- History improvements.
- Saved groups.
- Receipt gallery with explicit user permission.
- Reopen/edit past bills.
- Duplicate a past group.
- Premium image share cards.

### v1.3

- Subscription only if recurring cloud value exists.
- Cloud sync.
- Group history.
- Higher/fair-use AI scan limits.
- Cross-device backup.
- Export/share history.
