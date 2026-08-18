## AI engineering loop

For every task whose requested deliverable is a material repository change, use this loop:

```text
Human outcome and acceptance criteria
-> inspect
-> state the outcome contract and verification evidence
-> plan
-> create or reuse a codex/* branch and draft PR
-> implement in small vertical slices
-> targeted tests
-> lint
-> build
-> run and inspect the actual app when UI is affected
-> fresh-agent review of the complete branch diff
-> fix failures and blocking findings
-> repeat affected verification
-> GitHub Actions verify
-> mark ready for human review only when completion criteria pass
```

### Outcome contract

Before implementation, state:

- **Outcome:** the user-visible or engineering result.
- **Acceptance criteria:** observable facts that must be true.
- **Non-goals:** adjacent work intentionally excluded.
- **Verification:** evidence mapped to each acceptance criterion.

Infer this contract from the request and proceed unless ambiguity would materially change the result. Human judgment owns intent and final merge readiness; AI generation owns implementation and deterministic verification.

