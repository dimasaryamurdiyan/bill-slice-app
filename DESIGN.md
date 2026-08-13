---
name: BillSlice
description: Fast, friendly receipt splitting for Indonesian restaurant tables.
colors:
  emerald-action: "#20C982"
  emerald-deep: "#087C4E"
  mint-highlight: "#E5F9F0"
  warm-canvas: "#FAF9F6"
  warm-surface: "#FFFEFC"
  soft-surface: "#F2F5F2"
  deep-ink: "#17211D"
  charcoal-action: "#202B26"
  muted-ink: "#6D7771"
  subtle-border: "#E2E7E3"
  warning-surface: "#FFF4D2"
  warning-ink: "#8A6200"
  error-ink: "#C44242"
typography:
  display:
    fontFamily: "Funnel Sans, sans-serif"
    fontSize: "34sp"
    fontWeight: 800
    lineHeight: 1.05
    letterSpacing: "-0.6sp"
  headline:
    fontFamily: "Funnel Sans, sans-serif"
    fontSize: "28sp"
    fontWeight: 800
    lineHeight: 1.12
  title:
    fontFamily: "Funnel Sans, sans-serif"
    fontSize: "18sp"
    fontWeight: 750
    lineHeight: 1.2
  body:
    fontFamily: "Funnel Sans, sans-serif"
    fontSize: "14sp"
    fontWeight: 500
    lineHeight: 1.25
  label:
    fontFamily: "Funnel Sans, sans-serif"
    fontSize: "12sp"
    fontWeight: 600
    lineHeight: 1.25
  navigation:
    fontFamily: "Funnel Sans, sans-serif"
    fontSize: "10sp"
    fontWeight: 500
    lineHeight: 1.2
rounded:
  control-sm: "8dp"
  control-md: "10dp"
  card: "12dp"
  hero: "16dp"
  full: "999dp"
spacing:
  xxs: "2dp"
  xs: "4dp"
  sm: "8dp"
  md: "12dp"
  lg: "16dp"
  xl: "24dp"
  screen-horizontal: "18dp"
  screen-major: "28dp"
components:
  button-primary:
    backgroundColor: "{colors.emerald-action}"
    textColor: "{colors.deep-ink}"
    typography: "{typography.body}"
    rounded: "{rounded.card}"
    height: "52dp"
    padding: "0 16dp"
  button-secondary:
    backgroundColor: "{colors.charcoal-action}"
    textColor: "{colors.warm-surface}"
    typography: "{typography.body}"
    rounded: "{rounded.card}"
    height: "52dp"
    padding: "0 16dp"
  button-text:
    backgroundColor: "#00000000"
    textColor: "{colors.emerald-deep}"
    typography: "{typography.body}"
    rounded: "{rounded.control-md}"
    height: "44dp"
    padding: "0 10dp"
  input-default:
    backgroundColor: "{colors.warm-surface}"
    textColor: "{colors.deep-ink}"
    typography: "{typography.body}"
    rounded: "{rounded.control-sm}"
    height: "48dp"
    padding: "0 14dp"
  chip-person:
    backgroundColor: "{colors.mint-highlight}"
    textColor: "{colors.emerald-deep}"
    typography: "{typography.label}"
    rounded: "{rounded.full}"
    padding: "8dp 12dp"
  row-standard:
    backgroundColor: "{colors.warm-surface}"
    textColor: "{colors.deep-ink}"
    typography: "{typography.body}"
    rounded: "{rounded.control-sm}"
    padding: "11dp 12dp"
  banner-success:
    backgroundColor: "{colors.mint-highlight}"
    textColor: "{colors.emerald-deep}"
    typography: "{typography.label}"
    rounded: "{rounded.control-sm}"
    padding: "12dp"
  banner-warning:
    backgroundColor: "{colors.warning-surface}"
    textColor: "{colors.warning-ink}"
    typography: "{typography.label}"
    rounded: "{rounded.control-sm}"
    padding: "12dp"
  bottom-navigation:
    backgroundColor: "#FFFEFCEB"
    textColor: "{colors.muted-ink}"
    typography: "{typography.navigation}"
    rounded: "{rounded.full}"
    height: "64dp"
    padding: "6dp 8dp"
---

# Design System: BillSlice

## Overview

**Creative North Star: "The Calm Table Companion"**

BillSlice should feel like a capable friend helping at a busy restaurant table: quick to understand, calm under imperfect receipt data, and trustworthy with the final numbers. The light, warm canvas remains quiet while emerald marks the next action, selected state, successful result, and AI-assisted moments. Familiar Android patterns and direct language keep the product usable without instruction.

The system is task-first rather than promotional. Every screen has one clear job, manual entry remains as visible and credible as Smart Scan, and AI output is always presented as an editable draft. Information density may rise during receipt review and calculation, but spacing, typography, and semantic color must preserve scanability.

**Key Characteristics:**

- Warm light surfaces built for use under mixed restaurant lighting.
- Bright emerald actions balanced by deep charcoal and green-tinted neutrals.
- Rounded, confident controls with compact repeated rows.
- One focused task and one dominant action per screen.
- Transparent loading, warning, disabled, empty, and success states.
- IDR-first formatting and WhatsApp-first sharing.

## Colors

The palette pairs a warm neutral foundation with an energetic emerald signal color. Emerald is functional, not decorative.

### Primary

- **Table Emerald** (`#20C982`): Primary buttons, large scan actions, selected avatars, progress, success marks, and the logo tile.
- **Deep Emerald** (`#087C4E`): Accessible text and icons on mint or neutral surfaces, active navigation, links, currency prefixes, and supporting copy inside emerald areas.
- **Receipt Mint** (`#E5F9F0`): Selected states, success banners, quota chips, payer selection, and calm AI/privacy notes.

### Neutral

- **Warm Canvas** (`#FAF9F6`): Default app background. Use instead of pure white.
- **Warm Surface** (`#FFFEFC`): Inputs, rows, cards, and elevated content areas.
- **Soft Surface** (`#F2F5F2`): Empty states, quiet notes, inactive avatars, and secondary grouping.
- **Deep Ink** (`#17211D`): Primary text and icons. Use instead of black.
- **Table Charcoal** (`#202B26`): Secondary action buttons and the Lifetime Pro hero.
- **Muted Ink** (`#6D7771`): Supporting labels, metadata, inactive navigation, and tertiary icons.
- **Subtle Border** (`#E2E7E3`): One-dp outlines and dividers.

### Semantic

- **Warning Surface** (`#FFF4D2`) with **Warning Ink** (`#8A6200`): Mismatched totals and unassigned items.
- **Error Ink** (`#C44242`): Invalid or destructive states. Pair with clear text and an icon, never color alone.

**The Emerald Means Action Rule.** Use `#20C982` for primary actions, progress, selected states, and success. Do not scatter it across inactive decoration.

**The Tinted Neutral Rule.** Use `#FAF9F6`, `#FFFEFC`, and `#17211D` instead of pure white or black.

**Theme scope.** The current system defines a light theme only. Do not auto-generate a dark theme by mechanically inverting these tokens.

## Typography

**Display Font:** Funnel Sans (sans-serif fallback)
**Body Font:** Funnel Sans (sans-serif fallback)
**Label Font:** Funnel Sans (sans-serif fallback)

**Character:** A single rounded sans family makes the interface friendly without weakening numeric clarity. Weight and size create hierarchy; decorative font pairing is intentionally absent.

### Hierarchy

- **Display** (800, `34sp`, 1.05): Home headline, splash wordmark, and rare outcome totals. Use sparingly.
- **Headline** (800, `26–28sp`, 1.12): The main task statement on focused screens.
- **Title** (750, `18sp`, 1.2): App-bar titles and section-level emphasis.
- **Body** (500, `14–15sp`, 1.25): Instructions, inputs, button labels, and primary row content. Button labels use weight 700.
- **Data emphasis** (800–850, `16–32sp`): Person totals and combined totals. Keep `Rp` formatting intact, for example `Rp219.450`.
- **Label** (600–700, `11–12sp`, 1.25): Metadata, field labels, banner copy, and supporting status text.
- **Navigation** (500 inactive, 700 active, `10sp`): Bottom navigation labels.

**The One Family Rule.** Use Funnel Sans across product UI, labels, and numeric data. Do not introduce a display serif, script, or monospaced font.

**The Numbers Stay Legible Rule.** Never sacrifice currency clarity for style. Keep person totals right-aligned where rows compare values, and use Indonesian grouping punctuation by default.

## Elevation

BillSlice is flat by default and uses tonal layering plus one-dp borders for most structure. Shadows are ambient and reserved for a small number of elements that truly float or deserve immediate action emphasis.

### Shadow Vocabulary

- **Floating navigation** (`0 4dp 16dp #17211D12`): Bottom navigation only.
- **Primary action lift** (`0 4dp 14dp #087C4E20`): The large Scan Receipt action when it needs separation from the canvas.
- **Logo aura** (`0 12dp 32dp #087C4E1A`): Splash logo presentation only.
- **Quiet panel lift** (`0 3dp 12dp #17211D0D`): Share preview or another singular floating preview.

**The Flat-by-Default Rule.** Rows, fields, banners, and ordinary cards use color and a `1dp` border, not shadows. Never stack shadows on nested containers.

## Components

All touchable controls must have a clear enabled, pressed, focused, loading, and disabled state. Android implementations should use Material 3 semantics and Compose `Modifier` ordering consistently while matching these visual tokens.

### Buttons

- **Shape:** `12dp` radius for primary and secondary buttons; `10dp` for text buttons.
- **Primary:** `52dp` high, Table Emerald background, Deep Ink label and `20dp` rounded Material icon, `8dp` icon gap. Label is `15sp`, weight 700.
- **Secondary:** Same geometry as primary with Table Charcoal background and warm off-white content.
- **Text:** `44dp` minimum height, transparent background, Deep Emerald content, `18dp` icon, and `10dp` horizontal padding.
- **Pressed:** Reduce visual brightness slightly or apply a subtle tonal overlay. Do not change geometry.
- **Focus:** Use the platform focus indication in Deep Emerald with sufficient contrast.
- **Disabled:** Use Soft Surface and Muted Ink. Keep the label readable and remove elevation.
- **Loading:** Preserve the button width and label position. Replace or accompany the icon with compact progress feedback; do not resize the control.

### Chips

- **Person chip:** Receipt Mint background, full-pill shape, `8dp × 12dp` padding, `7dp` gap.
- **Avatar:** `24dp` emerald circle with a `12sp`, weight-800 Deep Ink initial.
- **Selected:** Deep Emerald label, weight 700.
- **Unselected:** Use Warm Surface with a Subtle Border and Muted Ink. Do not use saturated green for inactive chips.

### Cards / Containers

- **Large action card:** `82dp` high, `12dp` radius, `16dp` padding. Use emerald for Scan Receipt and charcoal for Enter Manually.
- **Task card:** `12dp` radius for receipt capture, assignment, calculation, and result groupings.
- **Repeated row:** `8dp` radius, Warm Surface, `1dp` Subtle Border, typically `11–14dp` internal padding.
- **Hero / success area:** Up to `16dp` radius. A subtle mint-to-warm gradient is allowed only on splash, home hero, or result success areas.
- **Avoid card inflation:** Related labels and totals may sit directly in a layout. Do not wrap every line in its own surface.

### Inputs / Fields

- **Style:** `48dp` high, `8dp` radius, Warm Surface, `1dp` Subtle Border, `14dp` horizontal padding.
- **Label:** `12sp`, weight 600, Muted Ink, placed `5dp` above the field.
- **Value:** `15sp`, weight 500–600, Deep Ink.
- **Currency:** Use a Deep Emerald `Rp` prefix and preserve locale-aware numeric formatting.
- **Focus:** Change the border to Deep Emerald and show the platform cursor. Avoid glow effects.
- **Error:** Use Error Ink for border, icon, and concise supporting text. Preserve the field height when the error appears by placing the message below.
- **Disabled:** Soft Surface background, Muted Ink content, no shadow.

### Banners and States

- **Success:** Receipt Mint background, Deep Emerald `20dp` icon and text, `8dp` radius, `12dp` padding.
- **Warning:** Warning Surface, Warning Ink, `20dp` warning icon, `8dp` radius, `12dp` padding.
- **AI draft:** Use mint treatment and explicitly state that the result is a draft requiring review.
- **Loading:** Show step rows or skeleton content for OCR, structuring, and total checking. Do not use a generic empty spinner.
- **Empty:** Soft Surface, receipt icon, a useful title, and one sentence explaining the next action.

### Navigation

- **Top bar:** `48dp` high with `24dp` rounded Material icons and a centered `18sp`, weight-750 title.
- **Bottom navigation:** `64dp` high floating capsule with `6dp × 8dp` inner padding, `1dp` border, and ambient shadow.
- **Active destination:** Receipt Mint capsule, Deep Emerald `21dp` icon, and `10sp` weight-700 label.
- **Inactive destination:** Transparent background, Muted Ink icon and `10sp` weight-500 label.
- **Destinations:** Home, History, Settings only.

### Receipt and Result Rows

- **Receipt row:** Item name at `14sp` weight 650–700, metadata at `11sp` Muted Ink, and price at `14sp` weight 700.
- **Editable row:** Include a Deep Emerald edit icon and compact quantity, unit price, and subtotal metadata.
- **Result row:** Use Receipt Mint for the payer or highlighted person, with name at `14sp` weight 700 and total at `16–17sp` weight 800–850.
- **Assignment:** Make the selected person explicit with avatar, name, and chevron. Warn before calculation if any item remains unassigned.

## Do's and Don'ts

### Do:

- **Do** keep each screen focused on one task and one dominant action.
- **Do** keep manual entry visible anywhere Smart Scan can fail or be skipped.
- **Do** label AI parsing as an editable draft and require confirmation before splitting.
- **Do** format currency as IDR by default, such as `Rp40.000` and `Rp219.450`.
- **Do** use warning banners when calculated and receipt totals differ, and when items are unassigned.
- **Do** keep ads outside Scan → Review → Add People → Assign → Calculate. On Result, show totals before any ad.
- **Do** use Material Symbols Rounded at `18–24dp` for interface icons.
- **Do** maintain comfortable touch targets of at least `44dp`, with primary actions at `52dp`.
- **Do** use semantic labels, content descriptions, and non-color cues for accessibility.
- **Do** keep splash and hero gradients subtle, mint-based, and limited to empty brand space.

### Don't:

- **Don't** use the starter Material purple palette. The BillSlice source of truth is Table Emerald `#20C982` plus the documented warm neutrals.
- **Don't** use pure `#000000` or `#FFFFFF` as primary surfaces or text.
- **Don't** clutter screens, use heavy gradients, glassmorphism, decorative finance-dashboard styling, or marketing-only hero pages.
- **Don't** wrap every element in a card or nest cards inside cards.
- **Don't** use heavy elevation, colored side-stripe borders, gradient text, or decorative animation.
- **Don't** hide manual entry behind an error state or overflow menu.
- **Don't** present AI output as final or silently correct receipt totals.
- **Don't** show interstitial ads or ads during the active split flow.
- **Don't** introduce account creation, cloud sync, saved groups, receipt galleries, FX conversion, or subscriptions into v0.1 screens.
- **Don't** create a dark theme until it has been intentionally designed and contrast-tested.
