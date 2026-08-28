`mattpocock/skills` 和 `superpowers` 不是简单的“工具箱 vs 方法论”，更准确地说是：

> `mattpocock/skills` 以“共同思考、澄清需求、建立项目知识”为核心；  
> `superpowers` 以“约束 Agent 按完整软件开发生命周期执行”为核心。

### 1. `mattpocock/skills` 的核心：先把问题想清楚

官方 README 明确强调它的 skill 应该是：

- 小型的
- 可组合的
- 容易适配和修改的
- 不替用户接管整个开发过程

它最有特色的入口确实是你指出的两个：

#### `grill-me`

这是一个强制深入访谈：

- 逐个问题推进
- 每个问题给出推荐答案
- 沿着决策树逐层展开
- 区分“可以查证的事实”和“必须由用户决定的选择”
- 在双方达成共同理解前，不执行实现

所以它解决的不是代码质量问题，而是：

> “我们是不是其实还没有想清楚要做什么？”

#### `grill-with-docs`

它等于：

```text
grill-me
+ domain-modeling
+ CONTEXT.md / glossary / ADR
```

它不只是把需求问清楚，还会在过程中：

- 建立项目的共享术语
- 修正模糊或重载的领域概念
- 把领域语言沉淀到 glossary / `CONTEXT.md`
- 把难以逆转的决定记录为 ADR

这是 `mattpocock/skills` 最独特的价值：它试图让 Agent 在多个会话中逐渐理解同一个代码库，而不是每次都重新猜术语和背景。

`ask-matt` 则是它的路由器，会把任务导向：

```text
grill-with-docs
→ to-spec
→ to-tickets
→ implement
→ tdd
→ code-review
```

因此，`mattpocock/skills` 不是没有流程，而是流程由多个可组合 skill 组成，用户可以选择和改造。

### 2. `superpowers` 的核心：完整的软件开发方法论

`superpowers` 官方 README 直接把自己定义为：

> a complete software development methodology for your coding agents

它的核心流程是：

```text
brainstorming
→ using-git-worktrees
→ writing-plans
→ subagent-driven-development
→ test-driven-development
→ requesting-code-review
→ finishing-a-development-branch
```

它关注的是：

- 什么阶段必须先完成
- 什么条件满足后才能进入下一阶段
- 每一步应该留下什么产物
- 实现过程如何使用 TDD
- 如何让子 Agent 执行任务并接受审查
- 如何在结束时验证、合并或清理

`superpowers` 的关键特点是：这些流程倾向于自动触发，并且是 mandatory workflows，而不只是可选建议。

### 3. 最容易混淆的地方

| 主题 | mattpocock | superpowers |
|---|---|---|
| 需求澄清 | `grill-me`，逐个问题拷问，直到共同理解 | `brainstorming`，澄清需求、比较方案、形成设计 |
| 代码库知识 | `grill-with-docs` 建 glossary、`CONTEXT.md`、ADR | 主要依靠 design doc、plan 和仓库上下文 |
| 领域建模 | 核心能力之一 | 不是主要特色 |
| 实现流程 | 通过 `ask-matt` 组合出流程 | 从设计到收尾有一条强约束主流程 |
| TDD | 一个独立可调用的工程 skill | 实现阶段的强制工作方式 |
| 调试 | `diagnosing-bugs`，强调先建立可失败的反馈循环 | `systematic-debugging`，强调系统化排查 |
| 代码审查 | `code-review` 分 Standards / Spec 两条轴 | `requesting-code-review`、`receiving-code-review`，强调审查门禁 |
| Agent 协作 | 有路由和 handoff，但不是所有流程都强制 | 子代理驱动开发是核心流程之一 |

`grill-me` 和 `superpowers/brainstorming` 虽然都先问问题，但目的不同：

- `grill-me` 的重点是**穷尽决策分支，逼出真实意图**
- `brainstorming` 的重点是**形成经过批准的设计，并进入实现计划**

可以把它们理解为：

```text
grill-me：我们到底要做什么？
brainstorming：在已经开始做设计后，应该怎样组织这个方案？
```

二者有交集，但不是同一个 skill 的两个版本。

### 4. 共同点

两者都：

- 反对 Agent 直接开始写代码
- 强调先理解上下文和目标
- 倾向于小步执行
- 重视测试、反馈和验证
- 使用可组合的 skill
- 能覆盖从想法到实现的多个阶段
- 都试图减少“Agent 自作主张”的概率

### 最终判断

如果你的主要问题是：

> 想法模糊、术语混乱、需求容易被 Agent 误解，希望通过持续对话建立共享理解

优先使用：

```text
mattpocock / grill-me
mattpocock / grill-with-docs
```

如果你的主要问题是：

> 需求已经相对明确，希望 Agent 严格经过设计、计划、TDD、审查和收尾流程

优先使用：

```text
superpowers
```

如果是已有代码库的长期开发，比较理想的组合是：

```text
grill-with-docs
→ to-spec / to-tickets
→ superpowers 的实现、TDD、审查和验证流程
```

一句话最终结论：

> `mattpocock/skills` 更擅长建立“人和 Agent 对问题、领域和代码库的共同理解”；`superpowers` 更擅长把这种理解转化为一套有阶段门禁、可验证、可持续执行的软件开发流程。

资料来源：

- [mattpocock/skills](https://github.com/mattpocock/skills)
- [grilling](https://github.com/mattpocock/skills/blob/main/skills/productivity/grilling/SKILL.md)
- [grill-me](https://github.com/mattpocock/skills/blob/main/skills/productivity/grill-me/SKILL.md)
- [grill-with-docs](https://github.com/mattpocock/skills/blob/main/skills/engineering/grill-with-docs/SKILL.md)
- [ask-matt](https://github.com/mattpocock/skills/blob/main/skills/engineering/ask-matt/SKILL.md)
- [obra/superpowers](https://github.com/obra/superpowers)

我也把这次核验结果保存到了 [.scratch/research-matt-vs-superpowers.md](/Users/che/workspace/hello-world/.scratch/research-matt-vs-superpowers.md)。