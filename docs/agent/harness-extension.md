## Reusable Skills and MCP tools

Keep stable repo rules in `AGENTS.md` or the applicable task-routed file under `docs/agent/`. Extract a reusable Skill only when a multi-step workflow recurs across tasks and needs its own scripts, templates, or detailed decision tree. Add or use an MCP tool only when the workflow needs structured access to an external system (for example issue tracking, design files, CI, backend data, or release services).

When extending the harness:

- Put always-applicable BillSlice-specific invariants and commands in `AGENTS.md`; put task-specific ones in the applicable file under `docs/agent/`.
- Put reusable Android expertise in a Skill, not duplicated prose.
- Prefer a small, auditable tool interface with least privilege.
- Document the trigger, inputs, outputs, permissions, failure behavior, and verification path.
- Do not add a Skill or MCP integration speculatively; first identify at least one concrete repeated workflow it simplifies.
