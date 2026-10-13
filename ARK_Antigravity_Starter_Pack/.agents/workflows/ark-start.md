---
description: Start or continue the ARK Real Estate build in small, reviewable phases
---

# ARK Real Estate Start Workflow

When the user runs `/ark-start`, follow `.agents/AGENTS.md`.

1. Inspect the actual project root and files. Identify available Git, Java/JDK, Android SDK, Node.js and package-manager tools. Do not install tools or run destructive commands without explaining and getting permission.
2. If the project is empty, perform Phase 0 only. Create/confirm `README.md`, `.gitignore`, `docs/ARCHITECTURE.md`, `docs/DATABASE_SCHEMA.md`, `docs/API_CONTRACT.md`, `docs/DECISIONS.md`, `docs/IMPLEMENTATION_LOG.md`, and `design/`. Preserve existing files.
3. Keep design handoff separate from code and technical docs. Check `design/` for Stitch exports. If only `STITCH_FINAL_PROMPT.md` exists, treat it as a prompt, not a finished design. Never claim design was imported when exports are absent.
4. Ensure docs agree on user roles, listing approval, PKR money values, manual rent records, English/Urdu RTL, marketplace and property-management scope.
5. Report structure created, checks performed, unresolved decisions and next step. Stop and ask the user to review Phase 0 before starting Phase 1. Do not continue without clear approval.
