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

