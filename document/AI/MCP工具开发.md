
自定义 MCP 工具的核心是：实现一个 MCP Server，并通过 `@mcp.tool()` 暴露函数。客户端会自动发现工具名称、参数 Schema 和描述，然后调用它。

下面是一个 Python 示例。

```bash
pip install "mcp[cli]"
```

创建 `server.py`：

```python
from mcp.server.fastmcp import FastMCP

mcp = FastMCP("my-tools")


@mcp.tool()
def calculate_discount(price: float, discount: float) -> float:
    """计算折后价格。discount 取值范围为 0 到 1。"""
    if price < 0:
        raise ValueError("price 不能小于 0")

    if not 0 <= discount <= 1:
        raise ValueError("discount 必须在 0 到 1 之间")

    return round(price * (1 - discount), 2)


@mcp.tool()
def search_user(user_id: str) -> dict:
    """根据用户 ID 查询用户信息。"""
    # 实际项目中可以调用数据库或内部 API
    return {
        "id": user_id,
        "name": "Alice",
        "status": "active",
    }


if __name__ == "__main__":
    mcp.run(transport="stdio")
```

运行：

```bash
python server.py
```

MCP 工具主要由以下部分组成：

```text
工具名称       calculate_discount
工具描述       让模型知道什么时候应该调用它
输入参数       price: float, discount: float
参数约束       discount 必须在 0 到 1 之间
业务逻辑       调用数据库、API、文件系统或其他服务
返回结果       字符串、数字、对象或结构化数据
```

以 Claude Desktop 为例，可以在配置文件中加入：

```json
{
  "mcpServers": {
    "my-tools": {
      "command": "python",
      "args": ["C:\\path\\to\\server.py"]
    }
  }
}
```

Node.js 版本的基本结构如下：

```bash
npm install @modelcontextprotocol/sdk
```

```javascript
import { McpServer } from "@modelcontextprotocol/sdk/server/mcp.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import { z } from "zod";

const server = new McpServer({
  name: "my-tools",
  version: "1.0.0"
});

server.tool(
  "calculate_discount",
  "计算折后价格",
  {
    price: z.number().nonnegative(),
    discount: z.number().min(0).max(1)
  },
  async ({ price, discount }) => ({
    content: [
      {
        type: "text",
        text: String(Math.round(price * (1 - discount) * 100) / 100)
      }
    ]
  })
);

const transport = new StdioServerTransport();
await server.connect(transport);
```

实际开发时建议注意：

- 工具描述要明确，模型依赖描述判断是否调用。
- 参数必须进行校验，尤其是路径、SQL、金额和权限参数。
- 不要直接把用户输入拼接进 SQL 或 Shell 命令。
- 错误应返回可理解的信息，便于模型决定下一步。
- 涉及写文件、删数据、发消息等操作时，应增加权限控制或二次确认。
- 复杂系统可以拆成多个小工具，例如 `query_order`、`cancel_order`，不要设计一个万能工具。
- 本地进程通常使用 `stdio`，远程服务可以使用 HTTP/SSE 或 Streamable HTTP 传输。

整体调用流程是：

```text
MCP Client
   -> initialize
   -> tools/list
   -> tools/call
MCP Server
   -> 参数校验
   -> 执行业务逻辑
   -> 返回结果
```

如果你的目标是让 ChatGPT/Codex 调用，还需要根据对应客户端的 MCP 配置格式注册这个 Server。