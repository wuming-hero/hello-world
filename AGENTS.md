# 仓库指南

## 项目概览

本仓库是个人 Java 学习与技术笔记项目。它包含 Java 示例、工具类、JUnit 实验、Markdown 笔记、图片以及示例资源文件。请把它当作学习与知识库仓库，而不是生产服务。

## 仓库结构

- `src/main/java/com/wuming`：按主题组织的 Java 示例与工具类，例如基础语言特性、线程、算法、JSON/XML、文件、日期、流以及通用工具辅助类。
- `src/test/java`：JUnit 4 测试和小型可执行实验。
- `src/main/resources`：日志配置、示例文件、静态图片，以及示例运行时使用的其他资源。
- `document`：中文技术笔记和学习材料。
- `static`：额外静态资源，尤其是笔记中使用的图片。

## 构建与测试命令

- 使用 `mvn test` 运行全部测试。
- 使用 `mvn -Dtest=ClassName test` 运行单个测试类。
- 除非用户明确要求修改 JDK 级别，否则保持 Java 8 兼容性。

## 代码规范

- 遵循 `com.wuming` 下现有的按主题组织的包结构。
- 示例应保持小而聚焦。与其把无关示例混入现有类中，更推荐在相关包下新增类。
- 除非任务明确要求升级，否则保留现有依赖版本。
- 除非请求的变更确实需要，否则避免大范围重构或包结构重组。
- 不要提交生成的构建产物、IDE 文件或 `target`。

## 文档规范

- 中文技术笔记放在 `document` 下。
- 笔记图片尽量放入 `static/image` 或 `src/main/resources/static/image` 下已有的主题目录。
- 编辑笔记时，保留现有 Markdown 风格和相对图片引用。
- 除非用户要求清理，否则不要重写或重组大段笔记内容。

## 验证

- 对 Java 变更，运行最相关的测试命令，通常是 `mvn test` 或 `mvn -Dtest=ClassName test`。
- 对仅 Markdown 的变更，检查已编辑文件并确认引用路径；除非修改了代码或构建文件，否则不需要运行 Maven 测试。

## Agent 技能

### Issue 跟踪

Issue 使用本地 Markdown 文件跟踪，存放在 `.scratch/<feature-slug>/` 下。详见 `docs/agents/issue-tracker.md`。

### Triage 标签

Triage 使用 mattpocock/skills 的默认标签词汇。详见 `docs/agents/triage-labels.md`。

### 领域文档

领域文档使用单上下文布局：根目录 `CONTEXT.md` 加 `docs/adr/`。详见 `docs/agents/domain.md`。
