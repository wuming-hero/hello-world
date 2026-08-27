# Spring AI

> 研究截至：2026-08-04。以下只使用 Spring 官方项目页、官方参考文档、Spring AI 官方 GitHub 仓库和 Maven Central。

## 最新版本

- **Spring AI 核心框架最新稳定版：`2.0.0`**。官方参考文档当前版本为 `2.0.0`，GitHub 最新正式 release 为 `v2.0.0`（发布于 2026-06-12），Maven Central 的 `spring-ai-model` 和 `spring-ai-bom` 元数据也将 `2.0.0` 标为 `latest`/`release`。
- `2.0.0` 是正式版，不要把 `2.0.0-RC2`、`2.0.0-M8` 等预发布版本当作最新稳定版。

来源：[Spring AI 项目页](https://spring.io/projects/spring-ai)、[参考文档](https://docs.spring.io/spring-ai/reference/)、[GitHub v2.0.0 release](https://github.com/spring-projects/spring-ai/releases/tag/v2.0.0)、[Maven Central spring-ai-model](https://central.sonatype.com/artifact/org.springframework.ai/spring-ai-model)、[Maven Central spring-ai-bom](https://central.sonatype.com/artifact/org.springframework.ai/spring-ai-bom)。

## Spring AI 与 Spring Boot 兼容性

这里的版本号是两套独立的版本：Spring AI 依赖版本由 Spring AI BOM 管理；Spring Boot 版本由应用自己的 Boot parent/BOM 管理。不要将“Spring AI `2.0.0`”写成“Spring Boot `2.0.0`”。

| Spring AI 核心版本线 | 官方支持的 Spring Boot 版本 |
| --- | --- |
| `2.0.x`（当前主线，最新稳定版 `2.0.0`） | `4.0.x`、`4.1.x` |
| `1.1.x`（发布线，最新已发布 `1.1.8`） | `3.5.x` |

来源：[官方 README 的兼容性说明](https://github.com/spring-projects/spring-ai/blob/v2.0.0/README.md)、[Getting Started](https://docs.spring.io/spring-ai/reference/getting-started.html)。当前文档没有在同一兼容性表中承诺 `1.0.x`，升级或维护旧项目时应以对应 release/tag 的文档为准。

## 设计思想

Spring AI 将 Spring 生态的设计原则带到 AI 领域：

- **可移植性**：用统一、可替换的 API 抽象 Chat、Embedding、图像、音频等模型，以及 Vector Store；更换供应商时尽量少改业务代码，同时保留模型特有能力。
- **模块化**：抽象有多个实现，组件可以替换；通过独立模块、BOM、Spring Boot Auto-configuration 和 Starters 组合使用。
- **强类型与 Spring 风格**：以强类型数据结构和 API 作为应用构件，提供类似 `WebClient`/`RestClient` 的 `ChatClient` 流式 API，并用 Advisors 封装可复用的生成式 AI 模式。
- **面向企业应用的工程能力**：把 RAG、会话记忆、Tool Calling、结构化输出、文档 ETL、可观测性、模型评估和 MCP 纳入可组合的 Java/Spring 应用开发模型。

来源：[官方 README](https://github.com/spring-projects/spring-ai/blob/v2.0.0/README.md)、[参考文档 Introduction](https://docs.spring.io/spring-ai/reference/index.html)、[AI Concepts](https://docs.spring.io/spring-ai/reference/concepts.html)。

## 它要解决的问题

官方将核心问题概括为：**把企业数据和 API 连接到 AI 模型**。Spring AI 因此主要解决：

1. 直接对接不同模型厂商、模型类型和向量数据库时 API 不一致、替换成本高的问题。
2. 在 Spring 应用中重复搭建提示词、结构化输出、工具调用、记忆、RAG、文档导入和观测等通用基础设施的问题。
3. 让生成式 AI 能力进入企业 Java 应用，而不要求应用团队转向 Python 或自行承担大量集成代码的问题。

它不是某个模型厂商 SDK 的直接移植，也不替应用决定具体模型或业务流程；它提供 Spring 友好的抽象、适配器和自动配置，让应用在统一编程模型下连接已有数据、API 和 AI 模型。

来源：[官方 Introduction](https://docs.spring.io/spring-ai/reference/index.html)、[官方 GitHub README](https://github.com/spring-projects/spring-ai/blob/v2.0.0/README.md)。
