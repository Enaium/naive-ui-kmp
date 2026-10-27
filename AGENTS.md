# Contribution Rules

These rules apply to all contributors, including those using AI-assisted development.

## 1. AI Usage and Co-author Attribution

- AI-assisted development is allowed for code, tests, and documentation.
- Every commit produced with AI assistance must include a co-author trailer:

  ```text
  Co-authored-by: <AI name> <email>
  ```

  Example:

  ```text
  feat(button): add loading icon slot

  Co-authored-by: Example AI <ai@example.com>
  ```

- The submitting human is responsible for reviewing AI-generated content and for its
  correctness, security, and license compliance.
- Do not fabricate co-author identities. If no email address is available, omit the email but
  include the actual AI name.

## 2. No Merge Commits; Squash Commits

- Merge commits are prohibited. Rebase when syncing with the target branch; do not use `git pull`
  or any merge operation that creates a merge commit.
- A pull request must ultimately contain exactly one commit.
- Before merging, squash all commits in a PR, including multiple commits for the same change,
  `fixup!`/`squash!` commits, formatting commits, and documentation follow-ups, into a single
  commit.
- Prefer the PR's **Squash and merge** option, or squash locally with `git rebase -i` and merge via
  fast-forward.
- The resulting history must be linear and must not contain merge commits.

## 3. One Feature per Pull Request

- Each PR must address exactly one feature, one bug fix, or one independent refactor.
- Split multiple features into separate PRs so each can be reviewed and squashed into its own single
  commit.
- Do not mix unrelated changes in the same PR.
- Keep the commit message and PR title specific to the single change, for example:

  ```text
  feat(table): support fixed grouped headers
  fix(select): keep dropdown inside viewport
  docs(config-provider): document message defaults
  ```

## 4. Component and Demo Rules

- Every public component lives directly in
  `ui/src/commonMain/kotlin/cn/enaium/naiveui/components` and uses the package
  `cn.enaium.naiveui.components`. Do not introduce category subpackages.
- Use theme tokens from `NTheme`; do not hard-code colors, sizes, or fonts unless the upstream
  component does so in its theme files.
- Keep APIs controlled and state-hoisted: `value` plus `onValueChange`, and composable lambdas for
  slots.
- The sample docs live under `sample/src/commonMain/kotlin/cn/enaium/naiveui/sample/gallery`.
- Every demo listed in the upstream `naive-ui/src/<component>/demos/enUS/index.demo-entry.md` must
  have a real, interactive `DemoSection`. Text-only stubs and placeholders are not accepted.
- When a demo requires missing component behavior, implement it in the component instead of
  faking it in the docs.
- New or renamed docs pages must be added to `SampleCatalog.kt` and must be reachable from the
  component source map in `ComponentNames.kt`.
- README component names link to their sample Kotlin source files. Keep those links valid.

## 5. Verification

Run the relevant checks before submitting. All commands must pass:

```bash
./gradlew :sample:jvmTest
./gradlew :sample:androidApp:assembleDebug
./gradlew :sample:linkDebugFrameworkIosSimulatorArm64
./gradlew :sample:webApp:composeCompatibilityBrowserDistribution
```

- `:sample:jvmTest` renders every docs page and exercises the gallery and core interactions.
- Add or update tests when behavior changes. Pure documentation changes still need a successful
  compile and the docs rendering test.
- Report the commands you ran and their results in the PR description.

## Pre-submit Checklist

- [ ] The change contains exactly one feature, fix, or refactor.
- [ ] The branch is rebased onto the target branch and contains no merge commit.
- [ ] The PR contains exactly one commit.
- [ ] AI-assisted commits include a `Co-authored-by` trailer.
- [ ] Upstream demo coverage is complete and interactive.
- [ ] Relevant tests and platform builds were run and the results are described in the PR.
