## Permissions, sandboxing, and external effects

- Work inside the provided repository and writable temporary directories. Respect the active sandbox and tool approval model.
- Read-only inspection within scope is allowed. Do not read unrelated personal files, credential stores, signing material, or environment secrets.
- If a required command is blocked by sandboxing or restricted network access, request the narrowest appropriate approval. Do not bypass restrictions or substitute an unsafe mechanism.
- Do not install global tools, change machine settings, launch GUI applications, or access external services unless the task requires it and permission is granted.
- Do not delete files, rewrite history, discard worktree changes, or run destructive Git commands without explicit authorization and an exact target review.
- A material coding task authorizes the [branch, commit, push, and draft-PR lifecycle](delivery-loop.md#branch-and-pr-lifecycle) after the repository baseline exists. Do not publish, deploy, upload builds to distribution services, send unrelated external messages, or merge unless the user explicitly asks.
- Never modify `local.properties`, signing configs, credentials, or production service configuration unless explicitly requested. Prefer documented placeholders and local-only configuration.
- Treat generated build outputs as disposable; do not add them to source control.
