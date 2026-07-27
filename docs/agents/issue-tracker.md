# Issue 跟踪：本地 Markdown

本仓库的 issues 和 specs（也可理解为 PRD）以 Markdown 文件形式存放在 `.scratch/` 下。

## 约定

- 每个功能一个目录：`.scratch/<feature-slug>/`
- Spec 文件为：`.scratch/<feature-slug>/spec.md`
- 实现 issue 按 ticket 拆成多个文件，位置为 `.scratch/<feature-slug>/issues/<NN>-<slug>.md`，编号从 `01` 开始；不要使用一个合并的 tickets 文件
- Triage 状态记录在每个 issue 文件顶部附近的 `Status:` 行中（角色字符串见 `triage-labels.md`）
- 评论和对话历史追加到文件底部的 `## Comments` 标题下

## 当某个 skill 说“publish to the issue tracker”时

在 `.scratch/<feature-slug>/` 下创建一个新文件；如果目录不存在，先创建目录。

## 当某个 skill 说“fetch the relevant ticket”时

读取被引用路径对应的文件。通常用户会直接提供路径或 issue 编号。

## Wayfinding 操作

供 `/wayfinder` 使用。**Map** 是一个文件，每个 ticket 对应一个**子文件**。

- **Map**：`.scratch/<effort>/map.md` —— Notes / Decisions-so-far / Fog 的正文。
- **子 ticket**：`.scratch/<effort>/issues/NN-<slug>.md`，编号从 `01` 开始，正文中写入问题。`Type:` 行记录 ticket 类型（`research`/`prototype`/`grilling`/`task`）；`Status:` 行记录 `claimed`/`resolved`。
- **阻塞关系**：在文件顶部附近使用 `Blocked by: NN, NN` 行记录。只有当它列出的所有文件都为 `resolved` 时，该 ticket 才解除阻塞。
- **Frontier**：扫描 `.scratch/<effort>/issues/`，寻找 open、未阻塞、未 claimed 的文件；编号最小者优先。
- **Claim**：开始任何工作前，先将 `Status:` 设置为 `claimed` 并保存。
- **Resolve**：在 `## Answer` 标题下追加答案，将 `Status:` 设置为 `resolved`，然后把上下文指针（gist + link）追加到 `map.md` 的 Decisions-so-far 中。
