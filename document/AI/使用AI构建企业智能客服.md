# 智能客户系统技术设计

## 1. 目标与范围

系统同时服务内部员工和外部客户，回答来源包括公司 Markdown 文档和公司现有 HTTP API。第一阶段只读，覆盖订单查询、物流进度、售后政策咨询。

核心约束：

- 外部客户只能查询本人订单，内部员工按角色、组织和授权范围查询。
- 模型不能直接访问任意 URL、本地文件、数据库或未登记工具。
- 模型负责理解和生成，后端负责身份、权限、工作流、数据和最终校验。
- 文档只提供事实，不能改变系统指令。
- 没有可靠依据时拒答或转人工，不允许猜测。
- 外部输出和内部诊断信息分离。
- 每次请求都记录 requestId、traceId、模型版本、Prompt 版本和工作流版本。

本文是生产系统设计，不修改当前仓库的 Java 8 示例工程。生产服务建议使用 Spring Boot 3.x 和 Java 17+；如果必须保持 Java 8，使用 Spring Boot 2.x、RestTemplate 或 Apache HttpClient 替换文中的 WebClient。

## 2. 总体架构

~~~text
Web 前端
  -> 统一认证 / JWT
  -> Conversation API
  -> JWT 校验、用户上下文
  -> 意图识别和参数提取
  -> Java 白名单工作流
       -> Qdrant 知识检索
       -> 固定 HTTP Adapter
       -> 串行或并行调用
  -> 最小事实 DTO
  -> Spring AI 结构化回答
  -> 后端来源、权限、敏感字段校验
  -> 内部或外部响应渲染
  -> Redis 会话 / MySQL 审计 / 反馈 / 评测
~~~

建议模块：

~~~text
com.company.smartcustomer
├── conversation
├── intent
├── workflow
├── adapter
├── knowledge
├── security
├── audit
└── support
~~~

模块边界：

| 模块 | 职责 |
| --- | --- |
| conversation | 会话接口、流式响应、用户类型渲染 |
| intent | 意图识别、参数提取、格式校验 |
| workflow | 串行、并行流程和失败策略 |
| adapter | 固定 HTTP 调用、DTO 映射、容错 |
| knowledge | Markdown 导入、切分、Embedding、Qdrant 检索 |
| security | JWT、Scope、数据归属校验 |
| audit | 审计、用户反馈、评测记录 |

## 3. 请求处理流程

~~~text
1. Web 携带 JWT 请求 /api/v1/conversations
2. Spring Security 校验签名、Issuer、Audience、过期时间和 Scope
3. 构造不可变 UserContext，生成 requestId 和 traceId
4. 使用模型识别意图并提取结构化参数
5. 后端只从白名单选择工作流
6. 后端重新校验参数、用户权限和订单归属
7. 工作流调用 Qdrant 或固定 HTTP Adapter
8. 将最小必要事实 DTO 交给模型
9. 模型输出结构化回答对象
10. 后端校验来源、工具白名单和敏感字段
11. 根据内部或外部身份渲染结果
12. 写入审计和评测数据
~~~

意图结果示例：

~~~json
{
  "intent": "ORDER_QUERY",
  "arguments": {
    "orderId": "O202608020001"
  },
  "missingArguments": [],
  "confidence": "HIGH"
}
~~~

后端不能接受模型传入的 userId、customerId、tenantId 或 roles。用户身份必须来自 Spring Security 上下文。

## 4. Spring Boot 与 Spring AI

### 4.1 依赖示例

以下依赖用于生产服务，版本由 Spring Boot BOM 和公司依赖管理统一维护：

~~~xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-openai</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-vector-store-qdrant</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
</dependency>
~~~

公司使用其他模型供应商时只替换模型 Starter，不修改工作流和 Adapter 接口。模型地址、Key 和模型名只能从密钥管理系统或环境变量读取。

### 4.2 配置示例

~~~yaml
spring:
  ai:
    openai:
      base-url: AI_BASE_URL
      api-key: AI_API_KEY
      chat:
        options:
          model: AI_CHAT_MODEL
          temperature: 0.1
          max-tokens: 1200
      embedding:
        options:
          model: AI_EMBEDDING_MODEL
    vectorstore:
      qdrant:
        host: QDRANT_HOST
        port: 6334
        collection-name: customer_knowledge
  data:
    redis:
      host: REDIS_HOST
      port: 6379
~~~

生产配置还需要模型超时、Token 预算、最大并发、Prompt 版本、脱敏开关和数据保留期限。不得将真实 Key 写入 Git。

### 4.3 ChatClient 封装

不要在业务类中到处直接调用 ChatClient，应集中封装模型参数、Prompt 版本、结构化输出和指标：

~~~java
@Configuration
public class AiClientConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("你是企业客户服务助手。只能依据提供的事实回答，不得猜测或编造。")
                .build();
    }
}
~~~

~~~java
@Service
public class AiAnswerService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public AiAnswerService(ChatClient chatClient, ObjectMapper objectMapper) {
        this.chatClient = chatClient;
        this.objectMapper = objectMapper;
    }

    public FinalAnswer generate(String question, List<AnswerFact> facts) {
        String factJson;
        try {
            factJson = objectMapper.writeValueAsString(facts);
        } catch (JsonProcessingException exception) {
            throw new AiResponseException("无法构造模型上下文", exception);
        }

        String prompt = "用户问题：\n" + question
                + "\n\n已验证事实：\n" + factJson
                + "\n\n只能依据已验证事实回答。事实不足时 needHuman 必须为 true。"
                + "只输出约定 JSON，不要输出代码围栏。";

        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(FinalAnswer.class);
    }
}
~~~

### 4.4 结构化输出

~~~java
public class FinalAnswer {

    private String answer;
    private String confidence;
    private List<String> sourceIds;
    private boolean needHuman;
    private List<String> usedTools;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public List<String> getSourceIds() {
        return sourceIds;
    }

    public void setSourceIds(List<String> sourceIds) {
        this.sourceIds = sourceIds;
    }

    public boolean isNeedHuman() {
        return needHuman;
    }

    public void setNeedHuman(boolean needHuman) {
        this.needHuman = needHuman;
    }

    public List<String> getUsedTools() {
        return usedTools;
    }

    public void setUsedTools(List<String> usedTools) {
        this.usedTools = usedTools;
    }
}
~~~

后端必须校验 sourceIds 来自真实知识块或 API 事实，usedTools 来自工具白名单，回答不能包含未授权字段。模型返回合法 JSON 不代表业务事实已被验证。

Spring AI 工具调用只暴露已审查的只读工具，工具内部仍调用 Adapter：

~~~java
public class CustomerTools {

    private final OrderApiAdapter orderApiAdapter;
    private final UserContext userContext;

    @Tool(description = "查询当前用户有权访问的订单摘要")
    public OrderFact queryOrder(String orderId) {
        orderIdValidator.validate(orderId);
        return orderApiAdapter.queryOrder(orderId, userContext);
    }
}
~~~

第一阶段优先由 Java 工作流显式调用 Adapter，模型只负责识别场景和参数。不要把所有公司 API 交给模型动态选择。

## 5. HTTP Adapter 实现

### 5.1 设计原则

现有 API 没有统一 Gateway 和 OpenAPI，第一阶段在智能客户系统内部实现固定 Adapter。它不是开放代理，不能接收模型提供的任意 URL。

Adapter 负责：

- 固定 URL、HTTP 方法和允许的请求头。
- 参数格式、长度和业务规则校验。
- JWT 透传或 Token Exchange。
- 连接超时、读取超时、有限重试和熔断。
- 原始响应到内部 DTO 的映射。
- 字段裁剪、脱敏和统一错误模型。
- 日志、指标、审计和 Trace ID。

### 5.2 接口和 DTO

~~~java
public interface OrderApiAdapter {

    OrderFact queryOrder(String orderId, UserContext userContext);
}
~~~

~~~java
public class OrderFact {

    private String factId;
    private String orderId;
    private String status;
    private String statusDescription;
    private String estimatedDeliveryDate;
    private String shipmentId;
    private boolean ownerVerified;
    private Instant fetchedAt;

    public OrderFact(String factId, String orderId, String status,
                     String statusDescription, String estimatedDeliveryDate,
                     String shipmentId, boolean ownerVerified, Instant fetchedAt) {
        this.factId = factId;
        this.orderId = orderId;
        this.status = status;
        this.statusDescription = statusDescription;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.shipmentId = shipmentId;
        this.ownerVerified = ownerVerified;
        this.fetchedAt = fetchedAt;
    }

    public String getFactId() {
        return factId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public String getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public boolean isOwnerVerified() {
        return ownerVerified;
    }

    public Instant getFetchedAt() {
        return fetchedAt;
    }
}
~~~

模型只接收最小必要字段，不接收完整下游响应。完整响应仅在符合合规要求时脱敏保存。

### 5.3 WebClient 实现

~~~java
@Component
public class OrderApiAdapterImpl implements OrderApiAdapter {

    private final WebClient companyWebClient;

    public OrderApiAdapterImpl(WebClient companyWebClient) {
        this.companyWebClient = companyWebClient;
    }

    @Override
    @CircuitBreaker(name = "orderApi")
    @Retry(name = "orderApi")
    public OrderFact queryOrder(String orderId, UserContext userContext) {
        validateRequest(orderId, userContext);

        CompanyOrderResponse response = companyWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/company/order/query")
                        .queryParam("orderId", orderId)
                        .build())
                .headers(headers -> headers.setBearerAuth(userContext.getJwt()))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        responseEntity -> Mono.error(
                                new BusinessApiException("订单查询未授权")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        responseEntity -> Mono.error(
                                new DownstreamApiException("订单服务异常")))
                .bodyToMono(CompanyOrderResponse.class)
                .block();

        if (response == null || response.getData() == null) {
            throw new DownstreamApiException("订单接口返回为空");
        }
        return toFact(orderId, response);
    }

    private void validateRequest(String orderId, UserContext userContext) {
        if (orderId == null || !orderId.matches("[A-Za-z0-9_-]{1,64}")) {
            throw new IllegalArgumentException("订单号格式不正确");
        }
        if (userContext == null || userContext.getJwt() == null) {
            throw new AccessDeniedException("缺少用户身份");
        }
    }

    private OrderFact toFact(String orderId, CompanyOrderResponse response) {
        CompanyOrderData data = response.getData();
        return new OrderFact(
                "order:" + orderId,
                orderId,
                data.getStatus(),
                data.getStatusDescription(),
                data.getEstimatedDeliveryDate(),
                data.getShipmentId(),
                data.isOwnerVerified(),
                Instant.now());
    }
}
~~~

如果上层使用响应式链路，Adapter 应返回 Mono<OrderFact>，避免 block。若使用 Spring MVC，则可使用同步 HTTP 客户端，但必须使用专用线程池、连接池和超时，不能在公共线程池中无限等待下游。

### 5.4 WebClient 连接池

~~~java
@Configuration
public class CompanyHttpClientConfig {

    @Bean
    public WebClient companyWebClient(CompanyApiProperties properties) {
        ConnectionProvider provider = ConnectionProvider.builder("company-api")
                .maxConnections(200)
                .pendingAcquireMaxCount(400)
                .pendingAcquireTimeout(Duration.ofSeconds(1))
                .build();

        HttpClient httpClient = HttpClient.create(provider)
                .responseTimeout(Duration.ofSeconds(4))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 800);

        return WebClient.builder()
                .baseUrl(properties.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
~~~

### 5.5 Resilience4j

~~~yaml
resilience4j:
  circuitbreaker:
    instances:
      orderApi:
        sliding-window-size: 50
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10s
  retry:
    instances:
      orderApi:
        max-attempts: 2
        wait-duration: 100ms
  timelimiter:
    instances:
      orderApi:
        timeout-duration: 4s
~~~

只有幂等的只读查询允许有限重试。4xx 权限错误、参数错误和业务不存在不重试。不同下游 API 使用独立熔断器和线程池，防止一个故障系统拖垮整体服务。

### 5.6 JWT 和数据归属

~~~java
public class UserContext {

    private String userId;
    private String customerId;
    private Set<String> roles;
    private Set<String> scopes;
    private String jwt;
    private UserType userType;

    public boolean hasScope(String scope) {
        return scopes != null && scopes.contains(scope);
    }

    public String getJwt() {
        return jwt;
    }

    public String getCustomerId() {
        return customerId;
    }
}
~~~

Spring Security 校验签名、Issuer、Audience、过期时间和 Scope。下游 API 再次校验用户 JWT。模型不能传入或覆盖 customerId，订单归属由后端或下游 API 校验。日志不得记录完整 JWT。

## 6. 工作流编排

### 6.1 工作流接口

~~~java
public interface CustomerWorkflow {

    WorkflowType supports();

    WorkflowResult execute(WorkflowContext context);
}
~~~

WorkflowContext 保存用户上下文、原始问题、已校验参数、会话 ID、Trace ID 和截止时间，但不允许模型修改身份字段。

### 6.2 串行调用

物流查询需要先查询订单，再使用已校验的 shipmentId 查询物流：

~~~java
@Component
public class ShipmentQueryWorkflow implements CustomerWorkflow {

    private final OrderApiAdapter orderApiAdapter;
    private final ShipmentApiAdapter shipmentApiAdapter;

    @Override
    public WorkflowType supports() {
        return WorkflowType.SHIPMENT_QUERY;
    }

    @Override
    public WorkflowResult execute(WorkflowContext context) {
        OrderFact order = orderApiAdapter.queryOrder(
                context.requiredArgument("orderId"),
                context.getUserContext());

        if (!order.isOwnerVerified()) {
            throw new AccessDeniedException("用户无权查询该订单");
        }
        if (order.getShipmentId() == null) {
            return WorkflowResult.partial(order, "当前订单暂无物流单号");
        }

        ShipmentFact shipment = shipmentApiAdapter.queryShipment(
                order.getShipmentId(), context.getUserContext());
        return WorkflowResult.success(order, shipment);
    }
}
~~~

串行步骤中的前一步失败时停止后续调用，不能将不完整数据继续交给下一个 API。

### 6.3 并行调用

使用专用有界线程池，不使用 ForkJoinPool.commonPool：

~~~java
@Configuration
public class WorkflowExecutorConfig {

    @Bean("customerWorkflowExecutor")
    public Executor customerWorkflowExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(32);
        executor.setMaxPoolSize(64);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("customer-workflow-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
~~~

~~~java
public CompletableFuture<WorkflowResult> executeParallel(WorkflowContext context) {
    CompletableFuture<OrderFact> orderFuture = CompletableFuture.supplyAsync(
            () -> orderApiAdapter.queryOrder(context.requiredArgument("orderId"),
                    context.getUserContext()), workflowExecutor);

    CompletableFuture<AfterSaleFact> policyFuture = CompletableFuture.supplyAsync(
            () -> policyService.findPolicy(context.getQuestion(), context.getUserContext()),
            workflowExecutor);

    return orderFuture.handle(this::toStepResult)
            .thenCombine(policyFuture.handle(this::toStepResult), this::mergeResults)
            .orTimeout(8, TimeUnit.SECONDS);
}
~~~

并行调用允许返回成功部分，但要明确说明失败的辅助信息。核心 API 失败时不生成确定性结论。每个步骤记录 SUCCESS、FAILED、TIMEOUT、SKIPPED。

## 7. Markdown 知识库和 Qdrant

### 7.1 文档管理

Markdown 文件纳入 Git，建议目录如下：

~~~text
knowledge-repository/
├── public/
│   └── after-sales-policy.md
├── internal/
│   └── customer-service-guide.md
└── department/customer-service/
    └── escalation-guide.md
~~~

显式元数据示例：

~~~yaml
---
visibility: public
businessDomain: after-sales
allowedRoles: []
effectiveFrom: 2026-01-01
---
~~~

没有 visibility: public 的文档默认仅内部可见。Qdrant Payload 保存 documentId、chunkId、sourcePath、sectionPath、gitCommit、visibility、businessDomain、effectiveFrom 和 chunkVersion。

### 7.2 导入和切分

~~~text
Git commit
  -> 扫描变更文件
  -> Markdown 解析
  -> 按 H1/H2/H3 切分
  -> 500～800 tokens，重叠 80～120 tokens
  -> 脱敏和敏感内容扫描
  -> Embedding
  -> 写入 Qdrant
  -> 删除失效版本向量
  -> 保存索引版本和导入结果
~~~

标题层级、表格、列表和代码块尽量保持完整。文档和用户查询必须使用相同 Embedding 模型，记录模型版本和分块策略版本。

### 7.3 检索

采用关键词检索、向量检索、Payload 权限过滤、去重和重排的混合检索。订单号、政策编号、产品型号等精确内容优先关键词检索，自然语言问题使用向量检索。命中分数低于阈值时拒答或要求补充信息。

~~~java
public List<KnowledgeChunk> search(String question, UserContext userContext) {
    Filter filter = visibilityFilter(userContext);
    List<KnowledgeChunk> semanticHits = qdrantSearch(question, filter, 8);
    List<KnowledgeChunk> keywordHits = keywordSearch(question, filter, 8);
    return rerankAndDeduplicate(semanticHits, keywordHits).stream()
            .filter(hit -> hit.getScore() >= minimumScore)
            .limit(5)
            .collect(Collectors.toList());
}
~~~

权限必须在检索阶段过滤，不能先检索全部文档再让模型自行隐藏内部内容。

## 8. Redis、MySQL 和审计

Redis 保存短期会话和工作流状态，TTL 建议 30 分钟至 24 小时：

~~~text
customer:session:{sessionId}
customer:workflow:{traceId}
customer:rate:{userId}:{minute}
~~~

MySQL 保存会话索引、调用审计、用户反馈、评测结果和工作流执行记录：

~~~sql
CREATE TABLE ai_request_audit (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    request_id VARCHAR(64) NOT NULL,
    trace_id VARCHAR(64) NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    tenant_id VARCHAR(64),
    user_type VARCHAR(32) NOT NULL,
    intent VARCHAR(64),
    model_name VARCHAR(128),
    prompt_version VARCHAR(64),
    workflow_version VARCHAR(64),
    status VARCHAR(32) NOT NULL,
    latency_ms BIGINT,
    input_tokens INT,
    output_tokens INT,
    created_at DATETIME NOT NULL,
    INDEX idx_request_trace (request_id, trace_id),
    INDEX idx_user_created (user_id, created_at)
);
~~~

不保存完整 JWT、完整 API 响应或未经脱敏的对话内容。原始证据需要加密、最小权限和明确保留期限。

## 9. 安全、失败和转人工

安全要求：

- Spring Security 校验 JWT 签名、Issuer、Audience、过期时间和 Scope。
- 外部客户只能查询本人订单，内部用户按角色和组织范围查询。
- 用户身份从安全上下文获取，不能从 Prompt 或工具参数获取。
- 只注册只读工具，所有参数执行白名单和类型校验。
- Markdown 内容和用户输入视为不可信数据，不能改变系统行为。
- 模型输入前脱敏手机号、身份证号、地址、银行卡号、Token、Cookie 和内部 URL。
- 回答输出再做敏感字段扫描。

降级策略：

| 场景 | 行为 |
| --- | --- |
| 检索不到可靠内容 | 明确无法确认，不猜测，提供转人工入口 |
| 置信度不足 | 要求补充订单号或产品型号 |
| 核心 API 失败 | 不生成确定性结论，提示重试或转人工 |
| 辅助 API 失败 | 返回成功部分，说明辅助信息不可用 |
| 用户无权访问 | 返回无权查询，不泄露资源额外信息 |
| 模型服务不可用 | 返回固定降级提示 |
| 用户要求人工 | 创建工单并附脱敏会话摘要和 Trace ID |

## 10. 可观测性、评测和测试

每个请求贯穿 requestId、traceId、workflowId 和 stepId。记录开始时间、结束时间、状态、下游名称、重试次数和脱敏错误类型。

上线前准备 100～300 条脱敏真实问题，覆盖正常问答、意图歧义、缺少参数、无权限订单、API 超时、文档过期、无检索结果、Prompt Injection、敏感信息索取以及多 API 串并行。

评测指标包括意图准确率、参数提取准确率、工具选择率、答案有据率、幻觉率、越权率、延迟、Token 成本和转人工率。模型、Prompt、切分、Embedding 或工作流变更都必须回归评测。

测试重点：

- 参数校验、DTO 映射和 JWT Claim 转换。
- 订单归属、Scope 和内外部权限。
- 串行步骤顺序、并行部分成功和超时。
- 4xx 不重试、5xx 有限重试和熔断降级。
- Qdrant 权限过滤、Redis TTL 和 MySQL 审计。
- 外部访问内部文档、查询他人订单和未登记工具调用。

## 11. 性能、灰度和交付

第一阶段容量基线：

- 日活用户 10,000。
- 峰值并发 100。
- 峰值请求量 500 次/分钟。
- 普通知识问答首 Token 不超过 3 秒。
- 单 API 查询总耗时不超过 5 秒。
- 多 API 编排总耗时不超过 10 秒。
- 月度可用性目标 99.9%。

上线顺序为内部白名单、外部小流量、按用户或租户逐步放量。必须保留关闭 AI、关闭单个工作流和关闭单个工具的开关。

第一阶段交付清单：

- [ ] Spring Boot 服务骨架和统一认证。
- [ ] Web 对话 API 和 SSE 流式响应。
- [ ] 订单、物流、售后三个工作流。
- [ ] 固定 HTTP Adapter、JWT 透传和 Resilience4j。
- [ ] Markdown Git 导入、切分、Embedding 和 Qdrant 索引。
- [ ] Redis 会话、MySQL 审计、反馈和评测。
- [ ] 内外部回答渲染和敏感信息脱敏。
- [ ] 转人工工单、指标、链路追踪和灰度开关。

后续再考虑将 Adapter 拆为独立 AI Integration Gateway、为现有 HTTP API 补充 OpenAPI、工作流配置化、接入更多文档来源、实时坐席转接和多模型路由。
