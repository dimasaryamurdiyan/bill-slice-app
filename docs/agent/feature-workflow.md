### Spec- and task-driven feature work

When asked to implement a feature, treat its matching files under `docs/specs/` and `docs/tasks/` as the implementation contract and progress ledger.

Resolve the feature in this order:

1. An explicit spec path, task-plan path, or feature name in the request.
2. The current branch matching a task plan's `Branch:` metadata.
3. Exactly one task plan with `Status: In Progress`.
4. Exactly one pair whose spec is `Approved` and task plan is `Ready`.
5. Otherwise stop and ask which feature to implement; never guess between eligible pairs.

Before coding, read the resolved spec and task plan completely, then verify:

- The spec has `Status: Approved` and the task plan has been explicitly authorized by a human. Only a human may authorize `Draft` to `Approved`, `Proposed` to `Ready`, or superseding/reapproving an approved spec.
- The task plan has `Status: Ready` or `Status: In Progress`.
- The spec has no unresolved open decision and its assumptions still match the repository.
- The spec and task plan link to each other and describe the same outcome.
- The next task's declared dependencies are checked.

Use these status transitions:

```text
Spec: Draft -> Approved -> Superseded
Task plan: Proposed -> Ready -> In Progress -> Complete
```

Implementation agents may perform only `Ready` to `In Progress` and `In Progress` to `Complete`. The first implementation change sets `Ready` to `In Progress`. Set the plan to `Complete` only when every vertical slice and completion-checklist item is checked and all required local, device, CI, and review evidence passes. `Complete` means implemented and verified on the PR branch, not merged. Keep the approved spec `Approved`.

For the prompt "implement this feature":

- Reuse or create the one feature branch and one long-lived draft PR declared by the task plan. One spec maps to one branch and one PR.
- Reconcile the plan against existing code before generating new code. For consecutive already-satisfied tasks, verify each exact Result and Verification before backfilling `[ ]` to `[x]`; file presence alone is not evidence.
- Select the lowest-numbered unchecked task whose dependencies are checked, unless the user explicitly names another dependency-ready task.
- Implement one coherent material vertical slice per prompt. A directly related verification-only or documentation-only task may accompany it when it adds no behavior or scope.
- Change only tasks whose stated Result is true and whose task-specific Verification passed from `[ ]` to `[x]`. The checkbox means implemented and verified on the PR branch, not merged. Keep detailed evidence in the PR, not the task file.
- Stop after the selected slice is verified, committed, pushed, and reflected in the draft PR. A later prompt continues the same branch and PR.

Make small coherent commits that tell the slice's implementation story. Every pushed commit must build and pass its relevant targeted tests; prove the red TDD state locally rather than pushing a deliberately failing commit. Put the checkbox/status update in the commit that completes its slice. Human judgment still decides whether and how to merge, with squash merge preferred.

After each slice, run targeted verification and a focused self-review. Require an immediate fresh reviewer for money, persistence/migrations, privacy/security, permissions, billing, ads, or external-SDK boundaries. Before PR readiness, always require a fresh two-axis review of the complete branch diff.

For the terminal transition, first obtain green CI and final review on the completed implementation head. Then commit the completion-checklist and `In Progress` to `Complete` metadata update, push it, and require CI to pass again on that exact commit before marking the PR ready. The metadata commit needs documentation checks and exact-head CI; repeat independent review only if it changes more than completion metadata.

An approved spec is immutable during implementation. If the outcome, acceptance criteria, business rules, privacy/security contract, or non-goals must change, keep the PR draft, stop the affected slice, propose the exact revision, and wait for human reapproval. Task-level implementation details such as file paths, commands, or dependency ordering may be corrected in the same PR when the approved behavior does not change; explain the drift in the PR.
