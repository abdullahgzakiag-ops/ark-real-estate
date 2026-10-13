# Start Here

This is an instruction pack, not the completed application code, and it does not create a GitHub repository automatically.

## First steps
1. Create a local folder named `ARK-Real-Estate`.
2. Extract/copy this pack into that folder, keeping the hidden `.agents` folder.
3. Open the folder in Antigravity.
4. Start an agent chat and run `/ark-start`. If the slash command is not recognized, open `.agents/workflows/ark-start.md` and paste its contents into chat.
5. Let Antigravity complete Phase 0 only; review the documents before approving database work.
6. Open `design/STITCH_FINAL_PROMPT.md`, copy the prompt, and paste it into Google Stitch.
7. Download/export the generated screens if Stitch offers those options, and put those files in the project's `design/` folder. The prompt file itself is not a finished design export.

## Create a GitHub repository
1. Sign in at https://github.com/
2. Click `+` → `New repository`.
3. Name it `ARK-Real-Estate`; choose **Private** while developing.
4. If you already have local files, do not create an extra README/license/gitignore on GitHub unless you know how to reconcile histories.
5. Click Create repository and copy the HTTPS URL GitHub displays, e.g. `https://github.com/YOUR-USERNAME/ARK-Real-Estate.git`. Replace the example username with the URL from your own account.
6. If you have not created a GitHub repo yet, there is no personal repository link to provide. In Antigravity, use Clone Repository only to download an existing remote repo. For an existing local folder, initialize Git and add the GitHub URL as a remote; ask the agent to explain one command at a time before running it.
7. Never share passwords, access tokens, `.env` files, signing keys or production secrets.

Official docs:
- Antigravity: https://antigravity.google/docs
- Antigravity workflows: https://www.antigravity.google/docs/ide/workflows/

## Terms
- Repository/local folder: project files and code.
- `.agents/AGENTS.md`: persistent instructions for the coding agent.
- `.agents/workflows/ark-start.md`: repeatable workflow; use `/ark-start` where supported.
- `design/STITCH_FINAL_PROMPT.md`: text prompt to paste into Stitch.
- Stitch exports: actual generated screens/assets, kept separately from the prompt.
