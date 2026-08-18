### Test policy

- Bug fixes start with a failing regression test when the behavior can reasonably be automated.
- Deterministic domain logic uses test-first vertical slices where practical: one behavior test, minimal implementation, then the next behavior.
- UI and integration work defines acceptance scenarios first; implementation may precede automated UI tests when a test-first seam would be artificial.
- Refactors establish a green baseline before changes and preserve observable behavior throughout.
- Tests verify behavior through stable public seams, not private implementation details.

### Failure loop

- Continue autonomously while an iteration produces new diagnostic information or measurable progress.
- After the same root failure survives three focused fix attempts, stop changing code, keep the PR in draft, and report the failing check, relevant error, attempted fixes, and best root-cause hypothesis.
- Ask only for the missing decision, permission, credential, external service, or environment change. Resume from the failed gate once unblocked.

### Independent diff review

- At each review point required by the applicable feature workflow or branch lifecycle, wait for local gates to pass, then delegate a read-only review to a fresh reviewer agent that did not implement the change.
- Review the complete merge-base-to-HEAD diff along two axes: requested specification and repository standards.
- P0/P1 correctness, security, privacy, data-loss, architecture, or acceptance failures block readiness.
- P2 maintainability and test gaps introduced by the PR block readiness. P3 suggestions and unrelated pre-existing issues do not block.
- The implementer may challenge a finding with concrete evidence but may not silently dismiss it. An unresolved blocking disagreement keeps the PR in draft for human judgment.
- After fixes, rerun affected checks and the final verification gate, then have a fresh reviewer recheck the resolution.

### Branch and PR lifecycle

- A material change includes executable code, tests, resources, manifests, build configuration, CI, repository policy, architecture/product contracts, or security/privacy documentation.
- Read-only diagnosis, review, research, planning, status, and explanation do not create branches or PRs. Minor incidental documentation changes create a PR only when requested.
- For a material non-feature task, create or reuse a `codex/<task-name>` branch and open a draft PR automatically. Spec-driven feature work uses the single branch and draft PR declared by its task plan.
- Make small coherent commits at meaningful checkpoints. Temporary fix commits are acceptable while the PR is draft; recommend squash merge, but only the human decides whether and how to merge.
- New commits invalidate affected local and CI evidence; rerun those gates in proportion to the change. Repeat independent review according to the applicable feature workflow and always after the final non-metadata change before readiness.
- Never merge a PR. Mark it ready only after every applicable completion criterion passes.
- This repository needs an explicitly approved baseline commit on `main` before the automatic branch/PR lifecycle can begin. Do not hide the uncommitted baseline inside a feature PR.

The PR description must contain the outcome contract, change summary, verification evidence, actual-app screenshots when required, independent-review result, and known risks or limitations. Prefer attaching screenshots to the PR description; do not commit them to the repository.

### Human merge gate

Treat the loop as operational only when `main` branch protection requires the `verify` GitHub Actions check, at least one approving human review, dismissal of stale approvals, and resolution of review conversations. Block direct and force pushes to `main` and do not allow the implementing agent to bypass protection.
