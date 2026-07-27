# 领域文档

工程类 skills 在探索代码库时，应按本文件说明读取本仓库的领域文档。

## 探索前先读取这些文件

- 仓库根目录的 **`CONTEXT.md`**，或
- 如果根目录存在 **`CONTEXT-MAP.md`**，它会指向每个上下文对应的 `CONTEXT.md`；读取与当前主题相关的文件。
- **`docs/adr/`** —— 读取与你将要处理的区域相关的 ADR。在多上下文仓库中，也要检查 `src/<context>/docs/adr/` 中的上下文级决策。

如果这些文件不存在，**静默继续**。不要特别指出缺失，也不要一开始就建议创建它们。`/domain-modeling` skill（可通过 `/grill-with-docs` 和 `/improve-codebase-architecture` 触发）会在术语或决策真正被澄清时按需创建这些文件。

## 文件结构

单上下文仓库（大多数仓库）：

```text
/
├── CONTEXT.md
├── docs/adr/
│   ├── 0001-event-sourced-orders.md
│   └── 0002-postgres-for-write-model.md
└── src/
```

多上下文仓库（根目录存在 `CONTEXT-MAP.md`）：

```text
/
├── CONTEXT-MAP.md
├── docs/adr/                          ← 系统级决策
└── src/
    ├── ordering/
    │   ├── CONTEXT.md
    │   └── docs/adr/                  ← 上下文级决策
    └── billing/
        ├── CONTEXT.md
        └── docs/adr/
```

## 使用术语表中的词汇

当你的输出命名某个领域概念时（例如 issue 标题、重构提案、假设、测试名称），使用 `CONTEXT.md` 中定义的术语。不要漂移到术语表明确避免的同义词。

如果你需要的概念尚未出现在术语表中，这是一个信号：要么你正在发明项目并未使用的语言（应重新考虑），要么确实存在术语缺口（可记录给 `/domain-modeling`）。

## 标出 ADR 冲突

如果你的输出与现有 ADR 矛盾，请明确指出，而不是静默覆盖：

> _Contradicts ADR-0007 (event-sourced orders) — but worth reopening because…_
