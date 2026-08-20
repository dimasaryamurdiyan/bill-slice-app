# Smart Scan API contract

- Status: Draft
- Owner: Human
- Contract version: 1
- Last updated: 2026-08-20

## Purpose

This is the wire contract between the BillSlice Android client and the Supabase Edge Function for Smart Scan v0.1. It refines the approved [`Smart Scan` specification](../specs/smart-scan.md) without changing its product behavior.

The contract covers one logical endpoint:

```http
POST /smart-scan/parse
```

The deployed Supabase URL is:

```text
https://<project-ref>.supabase.co/functions/v1/smart-scan-parse
```

## Transport and caller identity

Requests use HTTPS and UTF-8 JSON.

```http
Content-Type: application/json
apikey: <environment-specific Supabase publishable key>
```

BillSlice v0.1 has no user account. The Edge Function therefore uses a publishable key in the `apikey` header, disables platform JWT verification for this function, and validates publishable-key access in the handler. A publishable key is safe to distribute in a mobile binary but is not a user credential. The app must never send a Supabase secret/service-role key or OpenAI key. See Supabase's [authorization-header guidance](https://supabase.com/docs/guides/functions/auth-headers).

`installId` is a pseudonymous quota key, not authenticated identity. App-side install identity and Pro gating are acceptable for the closed test; stronger production abuse prevention is outside this contract and remains a release-hardening requirement.

## Parse request

The request body contains exactly these fields:

```json
{
  "requestId": "7dbf4c63-49c0-46c4-9db2-85dd30f583dd",
  "installId": "83b8dbf6-46c1-4440-9b5f-0aa914773462",
  "locale": "id-ID",
  "currency": "IDR",
  "timezone": "Asia/Jakarta",
  "ocrText": "KEDAI NUSANTARA\nNasi Goreng 1 x 40000\nTOTAL 40000"
}
```

| Field | Type | Contract |
|---|---|---|
| `requestId` | string | Required opaque UUID generated once for an active scan and reused for its explicit retries. |
| `installId` | string | Required anonymous installation UUID. Together with `requestId`, it scopes idempotency. |
| `locale` | string | Required BCP 47 language tag used as parsing context. |
| `currency` | string | Required ISO 4217 code supported by the app; initially `IDR`, `USD`, or `SGD`. |
| `timezone` | string | Required IANA timezone used as parsing context only. It does not control quota reset. |
| `ocrText` | string | Required non-blank local OCR output. It is treated as data, never logged or persisted. |

The server rejects missing, null, wrongly typed, or unknown fields with `INVALID_REQUEST`. Neither client nor server silently truncates OCR text as a Smart Scan product rule.

## Successful response

A successful response uses HTTP `200`. Money is always an integer in the currency's minor unit; rates use integer basis points. No JSON floating-point value is allowed for money or rates.

```json
{
  "requestId": "7dbf4c63-49c0-46c4-9db2-85dd30f583dd",
  "status": "success",
  "draft": {
    "merchantName": "Kedai Nusantara",
    "purchasedAt": "2026-08-20T19:30:00+07:00",
    "currency": "IDR",
    "items": [
      {
        "name": "Nasi Goreng",
        "quantity": 1,
        "unitPriceMinor": 40000,
        "lineTotalMinor": 40000
      },
      {
        "name": "Chicken Steak",
        "quantity": 1,
        "unitPriceMinor": 60000,
        "lineTotalMinor": 60000
      },
      {
        "name": "Pizza",
        "quantity": 1,
        "unitPriceMinor": 90000,
        "lineTotalMinor": 90000
      }
    ],
    "serviceRateBasisPoints": 500,
    "serviceAmountMinor": 9500,
    "taxRateBasisPoints": 1000,
    "taxAmountMinor": 19950,
    "discountMinor": 0,
    "receiptTotalMinor": 219450
  },
  "warnings": [],
  "quota": {
    "plan": "free",
    "policy": "monthly_5",
    "used": 3,
    "limit": 5,
    "remaining": 2,
    "resetsAt": "2026-09-01T00:00:00+07:00"
  }
}
```

### Draft fields

| Field | Type | Contract |
|---|---|---|
| `merchantName` | string or null | Parsed merchant name; null when unknown. |
| `purchasedAt` | RFC 3339 string or null | Receipt date/time with offset when confidently available. |
| `currency` | string | Must equal the requested supported currency. |
| `items` | array | Non-empty parsed item candidates in receipt order. |
| `items[].name` | string or null | Item label; null when unreadable. |
| `items[].quantity` | positive integer or null | Parsed quantity. |
| `items[].unitPriceMinor` | non-negative integer or null | Parsed unit price in minor units. |
| `items[].lineTotalMinor` | non-negative integer or null | Parsed receipt line total in minor units. |
| `serviceRateBasisPoints` | non-negative integer or null | Parsed service rate; `500` means 5%. |
| `serviceAmountMinor` | non-negative integer or null | Parsed service amount. |
| `taxRateBasisPoints` | non-negative integer or null | Parsed tax rate; `1000` means 10%. |
| `taxAmountMinor` | non-negative integer or null | Parsed tax amount. |
| `discountMinor` | non-negative integer or null | Parsed receipt-level discount. |
| `receiptTotalMinor` | non-negative integer or null | Parsed printed receipt total. |

Null means unknown and must remain visibly editable; it must never be silently converted to zero. Item IDs and other local draft IDs are generated by the Android domain mapper and never accepted from the AI/backend.

A response is quota-counting and structurally usable only when `draft` matches this schema and contains at least one item with a non-blank name, positive quantity, and at least one non-negative price value. Other null or uncertain values are allowed only with warnings. Android still validates every field and requires explicit user confirmation.

### Warnings

Warnings are machine-readable. The Android app owns localized user-facing copy and must not display arbitrary backend or AI text.

```json
{
  "code": "TOTAL_MISMATCH",
  "field": "/draft/receiptTotalMinor"
}
```

`field` is a JSON Pointer or null when a warning applies to the whole draft.

Contract warning codes:

- `MERCHANT_UNCERTAIN`
- `PURCHASED_AT_UNCERTAIN`
- `ITEM_NAME_UNCERTAIN`
- `ITEM_QUANTITY_UNCERTAIN`
- `ITEM_PRICE_UNCERTAIN`
- `SERVICE_UNCERTAIN`
- `TAX_UNCERTAIN`
- `DISCOUNT_UNCERTAIN`
- `TOTAL_MISSING`
- `TOTAL_MISMATCH`

Unknown warning codes are preserved as a generic non-blocking review warning. They never bypass field validation.

### Quota fields

The quota object is a post-consumption snapshot for a successful response.

| Field | Type | Contract |
|---|---|---|
| `plan` | `free` or `pro` | Entitlement class used by backend policy. |
| `policy` | `monthly_5` or `fair_use` | Applied backend policy. |
| `used` | non-negative integer | Count after this response was accepted. |
| `limit` | non-negative integer or null | Null only when the active policy has no fixed client-visible limit. |
| `remaining` | non-negative integer or null | Null when `limit` is null. |
| `resetsAt` | RFC 3339 string or null | Free reset is next `Asia/Jakarta` month boundary; null when policy has no monthly reset. |

## Error response

Backend failures use one envelope and never return raw exception, Supabase, database, or OpenAI text.

```json
{
  "requestId": "7dbf4c63-49c0-46c4-9db2-85dd30f583dd",
  "status": "error",
  "error": {
    "code": "POLICY_UNAVAILABLE",
    "retryable": true,
    "retryAfterSeconds": null
  },
  "quota": null
}
```

`requestId` is null only when the request ID could not be parsed. `quota` is non-null for `QUOTA_EXHAUSTED` and otherwise null. `retryAfterSeconds` is non-null only when the backend has a concrete delay.

| HTTP | Code | Retryable | Meaning and quota behavior |
|---|---|---|---|
| `400` | `INVALID_REQUEST` | No | Request violates the exact request schema; no quota use. |
| `401` | `INVALID_API_KEY` | No | Missing or invalid publishable-key access; no quota use. |
| `409` | `REQUEST_IN_PROGRESS` | Yes | Same install/request is still processing; retry same ID after the supplied delay. |
| `409` | `REQUEST_CONFLICT` | No | Same install/request ID was reused with different request content; no new quota use. |
| `410` | `REPLAY_EXPIRED` | No | Successful result data expired; start a new scan and never reparse/recharge the old request. |
| `422` | `OCR_UNUSABLE` | No | OCR is blank or cannot yield receipt content; no quota use. |
| `422` | `PARSE_UNUSABLE` | Yes | Parser returned no structurally usable draft; explicit retry may reuse the ID; no quota use. |
| `429` | `QUOTA_EXHAUSTED` | No | Free monthly quota is exhausted; includes current quota/reset snapshot. |
| `429` | `RATE_LIMITED` | Yes | Short-term abuse/capacity limit; retry same ID after `retryAfterSeconds`; no quota use. |
| `502` | `PARSER_UPSTREAM_FAILURE` | Yes | AI provider failed; no quota use. |
| `502` | `PARSER_RESPONSE_INVALID` | Yes | AI output failed the backend schema; no quota use. |
| `503` | `POLICY_UNAVAILABLE` | Yes | Quota or entitlement could not be verified; Smart Scan fails closed; no quota use. |
| `503` | `SERVICE_UNAVAILABLE` | Yes | Backend dependency is unavailable; no quota use. |
| `504` | `PARSER_TIMEOUT` | Yes | Backend parsing timed out; no quota use unless a valid result had already been atomically accepted. Same-ID retry resolves the outcome. |
| `500` | `INTERNAL` | Yes | Unexpected sanitized backend failure; no quota use unless a valid result had already been atomically accepted. Same-ID retry resolves the outcome. |

Permission, camera/import, image decoding, ML Kit model, OCR recognition, Android connectivity, user cancellation, and process-death outcomes are local typed failures from the approved specification, not backend error codes.

## Idempotency and replay

Idempotency is scoped by `(installId, requestId)`.

1. The backend fingerprints the allowed request content without retaining `ocrText`.
2. A duplicate with different content returns `REQUEST_CONFLICT`.
3. A duplicate while processing returns `REQUEST_IN_PROGRESS`.
4. A successful usable result atomically consumes quota once and stores the response for replay.
5. A duplicate within one hour returns the stored successful draft, warnings, and original quota snapshot without another AI call or quota use.
6. After one hour, receipt-derived result data is cleared. The minimal request/count marker remains and returns `REPLAY_EXPIRED`, preventing reparsing and double charging.
7. A request that failed before successful acceptance may be explicitly retried with the same ID.

Concurrent different request IDs cannot exceed the Free limit. If another request consumes the final allowance before this result is atomically accepted, this request returns `QUOTA_EXHAUSTED` and is not counted.

## Privacy and retention

- Receipt images never enter this endpoint.
- Request bodies and raw OCR text are excluded from application, platform, database, analytics, and error logs.
- Raw OCR text is held only in request memory for parsing and is not persisted.
- Successful structured result data is retained outside logs for at most one hour.
- The remaining idempotency record contains only install ID, request ID, request fingerprint, month key, counted time, result expiry, and status.
- OpenAI and Supabase secret/service-role keys remain server-side.
- Sanitized hashes, codes, timing, model identifier, and quota counters may be logged as documented in [`docs/product-plan.md`](../product-plan.md).

## Contract verification

Android fake-server tests and Supabase integration tests must cover:

- Exact allowed request fields and rejection of unknown fields.
- Success with complete data and success with nullable/warned fields.
- Exact money/rate integer handling and malformed numeric values.
- Empty/unusable OCR and unusable/malformed AI results without quota use.
- Success-only atomic quota counting and the Jakarta reset boundary.
- Same-ID in-progress, replay, conflict, and expiry behavior.
- Concurrent fifth/sixth Free requests.
- Missing/invalid publishable key and unavailable policy dependencies.
- Absence of image/raw OCR in persistence and logs.
- Clearing structured result data within one hour while retaining the no-double-charge marker.

The examples in this document are sanitized contract fixtures. Implementation tests may copy them verbatim; they must not replace them with real receipt data.

## Non-goals

This contract does not define image upload, direct OpenAI access, user accounts, production device attestation, offline AI parsing, final bill calculation, automatic retry, OCR diagnostics opt-in, or a product-level OCR size/prompt-content rule.
