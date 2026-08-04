# mattpocock/skills 与 obra/superpowers 研究摘要

研究日期：2026-08-04（Asia/Shanghai）  
资料范围：仅使用两个官方 GitHub 仓库的 `README.md`、`SKILL.md` 及其官方路由/说明文件。为便于复查，链接固定到研究时的 `main` 提交：`mattpocock/skills@2ab958093e83e0ec752e6c1c5932da465bf23e0c`、`obra/superpowers@44c9b2d6e889982ac18c27d05a19fefe335194e1`。

## 一句话结论

两者不是同一种产品：**Matt 的 `skills` 更像可组合的工程实践技能库和路由手册，保留使用者对流程的控制；Superpowers 更像完整的软件开发方法论，通过启动规则、设计闸门和一条默认交付流水线约束代理行为。** 二者在 TDD、调试和证据化验证上高度相容，但强制程度、入口和上下文治理不同。

## 1. 项目定位

### mattpocock/skills

- README 将项目定位为 “Skills For Real Engineers”，强调技能应当小、易改、可组合、适配任何模型；它明确把自己与试图“拥有整个过程”的 GSD、BMAD、Spec-Kit 区分开，理由是完整流程的控制权和纠错成本可能变差。
- README 另以“谁能调用”为唯一主轴：`User-invoked` 技能只能由人显式输入，负责编排；`Model-invoked` 技能可以由模型或用户在任务匹配时自动触发，负责可复用的工程纪律。用户调用的技能可以调用模型调用的技能，但不能再调用另一个用户调用技能。
- `ask-matt` 把技能组织成“主流程、入口、代码库健康、底层词汇、独立技能”，说明这个仓库不仅是技能集合，也提供人工选择流程的路由层。

来源：

- [Matt README：定位、设计动机、Invocation 轴与技能目录](https://github.com/mattpocock/skills/blob/2ab958093e83e0ec752e6c1c5932da465bf23e0c/README.md)
- [Matt invocation：user-invoked/model-invoked 规则](https://github.com/mattpocock/skills/blob/2ab958093e83e0ec752e6c1c5932da465bf23e0c/.agents/invocation.md)
- [Matt ask-matt：官方流程路由](https://github.com/mattpocock/skills/blob/2ab958093e83e0ec752e6c1c5932da465bf23e0c/skills/engineering/ask-matt/SKILL.md)

### obra/superpowers

- README 直接称其为“面向 coding agents 的完整软件开发方法论”，建立在可组合技能和一组初始指令上，确保代理会使用这些技能。
- 它的默认行为是：代理看到“要构建东西”时先退一步询问真正目标；形成 spec 后再做计划；用户批准设计后进入实现；实现过程强调真正的 RED/GREEN TDD、YAGNI 和 DRY，并可进入 subagent-driven development。
- README 同时宣称技能会自动触发，因此安装后通常不需要额外的特殊操作。

来源：[Superpowers README：定位与工作方式](https://github.com/obra/superpowers/blob/44c9b2d6e889982ac18c27d05a19fefe335194e1/README.md)

## 2. `grill-me` 与 `grill-with-docs`

两者都不是模型自动触发的“纪律技能”，而是用户显式选择的入口：

| 技能 | 官方定义 | 关键边界 |
| --- | --- | --- |
| `grill-me` | 对计划或设计进行持续、严格的访谈，以澄清方案 | 只执行 `/grilling`，且 `disable-model-invocation: true`；无代码库文档落地 |
| `grill-with-docs` | 同样的访谈，但过程中额外创建 ADR 和 glossary | 执行 `/grilling`，并使用 `/domain-modeling`；同样禁止模型隐式调用 |

`grilling` 的原语要求一次只问一个问题、逐个解决决策树分支，事实应通过环境探索查明，决策则交给用户；在达成共同理解前不要行动。`grill-with-docs` 的增量不是“问题更多”，而是把领域词汇和重要决策保留下来：`domain-modeling` 会主动澄清术语、用边界场景压力测试关系，并在术语确定时更新 `CONTEXT.md`，仅在难以逆转、出乎预期且存在真实权衡时创建 ADR。

来源：

- [Matt grill-me](https://github.com/mattpocock/skills/blob/2ab958093e83e0ec752e6c1c5932da465bf23e0c/skills/productivity/grill-me/SKILL.md)
- [Matt grill-with-docs](https://github.com/mattpocock/skills/blob/2ab958093e83e0ec752e6c1c5932da465bf23e0c/skills/engineering/grill-with-docs/SKILL.md)
- [Matt grilling：单问题访谈与行动边界](https://github.com/mattpocock/skills/blob/2ab958093e83e0ec752e6c1c5932da465bf23e0c/skills/productivity/grilling/SKILL.md)
- [Matt domain-modeling：词汇、场景、CONTEXT 与 ADR](https://github.com/mattpocock/skills/blob/2ab958093e83e0ec752e6c1c5932da465bf23e0c/skills/engineering/domain-modeling/SKILL.md)

## 3. Brainstorming / 设计闸门

Superpowers 的 `brainstorming` 比 Matt 的 `grill-*` 更像一个完整的设计前置流程：先了解项目上下文，再一次问一个澄清问题，提出 2-3 个方案及权衡，分段呈现设计并取得用户批准，然后写设计文档、自审、让用户复核，最后才转入 `writing-plans`。

最强约束是 `<HARD-GATE>`：任何实现技能、代码、脚手架或实现动作，都必须等设计已呈现并得到用户批准；“事情很简单”不能豁免。其设计文档默认写入 `docs/superpowers/specs/YYYY-MM-DD-<topic>-design.md` 并提交，但用户偏好可以覆盖默认位置。

比较上，Matt 的 `grill-me`/`grill-with-docs` 更窄：它们提供访谈原语和（后者）领域文档沉淀，不在自身文件中规定 2-3 个方案、设计分段批准、设计文档自审和用户复核这一整套闸门。Matt 的 `ask-matt` 则把 `grill-with-docs` 放在主流程起点，之后再接 `to-spec`/`to-tickets`/`implement`。

来源：

- [Superpowers brainstorming：硬闸门、检查清单与流程](https://github.com/obra/superpowers/blob/44c9b2d6e889982ac18c27d05a19fefe335194e1/skills/brainstorming/SKILL.md)
- [Matt ask-matt：grill-with-docs 到 spec/tickets/implement 的衔接](https://github.com/mattpocock/skills/blob/2ab958093e83e0ec752e6c1c5932da465bf23e0c/skills/engineering/ask-matt/SKILL.md)

## 4. TDD

### Matt

Matt 的 `tdd` 是模型调用技能，强调测试行为而不是实现细节，并要求在写测试前先约定被测的公共 seam。它反对 implementation-coupled、tautological 和 horizontal slicing 测试，要求以“一个 seam、一个测试、一个最小实现”为垂直切片；每轮都是 red → green，重构属于后续 code-review 阶段，而非实现循环本身。

### Superpowers

Superpowers 的 `test-driven-development` 采用更强的不可违背表述：`NO PRODUCTION CODE WITHOUT A FAILING TEST FIRST`。每个行为都要先写一个最小测试，实际运行并确认因功能缺失而正确失败，再写最小代码使其通过，再重构；测试必须使用真实代码，除非 mock 不可避免。它还列出“代码先于测试、测试先通过、以后再补测试”等情形，并要求删除代码、重新开始。

### 判断

共同核心是“先红、再最小实现、持续反馈”；Matt 补充了测试 seam、领域语言和垂直切片的设计质量，Superpowers 补充了更硬的执行闸门和反合理化清单。对于已有项目，Matt 的“先确认公共 seam”更能防止测试锁定内部实现；对于执行纪律，Superpowers 的规则更明确、更难被代理绕开。

来源：

- [Matt tdd](https://github.com/mattpocock/skills/blob/2ab958093e83e0ec752e6c1c5932da465bf23e0c/skills/engineering/tdd/SKILL.md)
- [Superpowers test-driven-development](https://github.com/obra/superpowers/blob/44c9b2d6e889982ac18c27d05a19fefe335194e1/skills/test-driven-development/SKILL.md)

## 5. Debugging

### Matt：先建反馈回路，再诊断

`diagnosing-bugs` 面向难 bug、间歇性问题和性能回归。其中心判断是：如果没有一个紧、快、确定且能针对该症状变红的反馈回路，后续猜测都不可靠。它优先考虑失败测试、HTTP/CLI 脚本、无头浏览器、trace replay、throwaway harness、property/fuzz、二分和差分循环；如果确实无法建立回路，要求明确说明已尝试内容并向用户索取环境或捕获物，而不是继续猜。

它的六阶段是：建立反馈回路；复现并最小化；生成 3-5 个有可证伪预测的排序假设并先展示给用户；按假设做单变量 instrumentation；在正确 seam 上先写回归测试再修复；清理 instrumentation 并做 post-mortem。完成标准包括重跑原始复现、回归测试通过、删除 `[DEBUG-...]` 日志和 throwaway 原型。

### Superpowers：四阶段根因调查

`systematic-debugging` 的铁律是 `NO FIXES WITHOUT ROOT CAUSE INVESTIGATION FIRST`。四阶段依次为：读取错误并稳定复现、分析工作样例和差异、提出单一假设并最小化验证、写失败测试后实施单一根因修复并验证；连续三次修复失败时停止继续打补丁，转而质疑架构。它还要求多组件系统在边界处收集输入/输出证据，并在深调用栈中逆向追踪坏值来源。

### 判断

Matt 更重“把 bug 变成一个可重复实验”的反馈回路和多假设排序；Superpowers 更重“不找根因不许修”的通用四阶段流程、边界证据和失败次数后的架构升级。两者都拒绝 symptom fix，并都把回归测试放在修复之前；实践中可以把 Matt 的紧反馈回路作为 Superpowers Phase 1 的强化版本。

来源：

- [Matt diagnosing-bugs](https://github.com/mattpocock/skills/blob/2ab958093e83e0ec752e6c1c5932da465bf23e0c/skills/engineering/diagnosing-bugs/SKILL.md)
- [Superpowers systematic-debugging](https://github.com/obra/superpowers/blob/44c9b2d6e889982ac18c27d05a19fefe335194e1/skills/systematic-debugging/SKILL.md)

## 6. Verification

Superpowers 有单独的 `verification-before-completion` 技能，核心是 `NO COMPLETION CLAIMS WITHOUT FRESH VERIFICATION EVIDENCE`：在声称测试通过、构建成功、bug 已修复或任务完成前，必须先识别能证明该说法的完整命令，实际运行、读取完整输出和退出码，再根据证据表述；旧运行、局部检查、“应该可以”和代理自报成功都不算。

Matt 当前 README 的工程技能目录明确列出 `tdd` 与 `diagnosing-bugs`，但没有对应的独立 `verification-before-completion` 条目。其验证要求分散在各技能中：TDD 要观察 RED/GREEN、输出无错误和警告；diagnosing-bugs 要重跑原始回路、回归测试并清理调试痕迹；README 的总体原则也是反馈循环和证据。这意味着 Matt 更像把验证嵌入各实践，Superpowers 则把“完成前新鲜证据”提升为跨任务的统一门禁。

来源：

- [Superpowers verification-before-completion](https://github.com/obra/superpowers/blob/44c9b2d6e889982ac18c27d05a19fefe335194e1/skills/verification-before-completion/SKILL.md)
- [Matt README：反馈循环、TDD 与 diagnosing-bugs](https://github.com/mattpocock/skills/blob/2ab958093e83e0ec752e6c1c5932da465bf23e0c/README.md)
- [Matt tdd：RED/GREEN 与输出检查](https://github.com/mattpocock/skills/blob/2ab958093e83e0ec752e6c1c5932da465bf23e0c/skills/engineering/tdd/SKILL.md)

## 7. 工作流路由

### Matt 的路由：人工选择入口，技能组成主流程

`ask-matt` 定义的默认主流程是：

`grill-with-docs` →（必要时 handoff/prototype）→ 单会话直接 `implement`，或多会话时 `to-spec` → `to-tickets` → 每个 ticket 清理上下文后 `implement`；`implement` 内部驱动 `tdd`，结束时运行 `code-review`。

它还定义三个重要入口：新来的问题/请求先进 `triage`；难 bug 进 `diagnosing-bugs`；巨大且路线模糊、超出一个会话的工作进 `wayfinder`，待决策地图清晰后回到 `to-spec`。`grill-me` 是没有代码库时的独立入口；`research` 负责产生带引用的研究 Markdown，供后续 `grill-with-docs` 使用。整体特点是按情境分流、人工显式选择用户调用技能，模型调用技能在流程下方提供纪律。

### Superpowers 的路由：启动即检查技能，默认流水线更强

README 的基本工作流是：

`brainstorming` →（设计批准后）`using-git-worktrees` → `writing-plans` → `subagent-driven-development` 或 `executing-plans` → `test-driven-development` → `requesting-code-review` → `finishing-a-development-branch`。

README 明确说代理在任何任务前检查相关技能，且 mandatory workflow 是要求而不是建议；`using-superpowers` 又把“调用相关技能”放在任何行动之前。于是 Superpowers 的路由更像一个默认状态机：先设计、再隔离和计划、再实现、复核、收尾；TDD、系统化调试和完成前验证是可被自动触发的纪律技能，而不是只有用户知道命令名才会发生。

### 路由差异的实际含义

- 想保留流程选择权、只拿 TDD 或调试等局部纪律：Matt 更合适。
- 想让代理从第一条消息开始遵循设计批准、计划、审查和证据门禁：Superpowers 更完整。
- 两者可组合：用 Matt 的 `grill-with-docs` 建立项目术语和 ADR，用 Superpowers 的 brainstorming/verification 补设计与完成闸门；但需要避免两套路由同时强制启动造成重复访谈或重复计划。

来源：

- [Matt ask-matt：主流程、入口、跨会话与独立技能](https://github.com/mattpocock/skills/blob/2ab958093e83e0ec752e6c1c5932da465bf23e0c/skills/engineering/ask-matt/SKILL.md)
- [Matt README：User-invoked/Model-invoked 路由与工程目录](https://github.com/mattpocock/skills/blob/2ab958093e83e0ec752e6c1c5932da465bf23e0c/README.md)
- [Superpowers README：Basic Workflow 与 mandatory workflow](https://github.com/obra/superpowers/blob/44c9b2d6e889982ac18c27d05a19fefe335194e1/README.md)
- [Superpowers using-superpowers：任何行动前调用相关技能](https://github.com/obra/superpowers/blob/44c9b2d6e889982ac18c27d05a19fefe335194e1/skills/using-superpowers/SKILL.md)

## 关键结论汇总

1. `grill-me` 是无状态的用户访谈入口；`grill-with-docs` 是带领域模型、`CONTEXT.md` 和 ADR 沉淀的代码库入口。
2. Matt 把“可组合和可控”放在首位，并用 invocation 轴区分编排技能与自动触发的工程纪律。
3. Superpowers 把“完整方法论和强制路由”放在首位，`brainstorming` 的设计批准闸门和 `verification-before-completion` 的新鲜证据闸门是其显著标志。
4. TDD 共同要求 RED/GREEN；Matt 额外强调公共 seam、行为测试和垂直切片，Superpowers 额外强调无例外的失败测试前置规则。
5. 调试共同要求根因和回归测试；Matt 先建立紧反馈回路并排序多个假设，Superpowers 采用四阶段根因流程并规定三次失败后质疑架构。
6. 若只选一套：局部、可裁剪的工程技能选 Matt；希望代理遵循端到端开发流程选 Superpowers。混用时应明确唯一的启动路由，避免重复门禁。
