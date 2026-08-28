


下载对应的版本
jdk 1.8 MAT 1.14.x


MAT 与 JDK 主要有两层关系：

1. **MAT 自身运行所需的 JDK**
2. **MAT 分析的 Heap Dump 来源 JDK**

截至目前，官方版本大致对应如下：

| MAT 版本 | MAT 运行最低 JDK | 建议 |
|---|---:|---|
| MAT 1.17.x | JDK 21 | 推荐直接使用 |
| MAT 1.16.x | JDK 17 | 适合 JDK 8/11/17/21 堆转储 |
| MAT 1.15.x | JDK 11 | 老项目常用 |
| MAT 1.14.x 及更早 | 通常为 JDK 8/11，需以对应发行说明为准 | 不建议用于新环境 |

关键结论：

- **JDK 版本较新的 MAT，通常可以分析较老 JDK 生成的堆转储**。例如用 JDK 21 运行 MAT 1.17，分析 JDK 8、11、17 或 21 生成的 `.hprof` 文件。
- **MAT 运行时 JDK 与被分析应用的 JDK 不要求一致**。
- 生产环境建议使用与目标 JVM 接近或更高版本的 MAT，例如：
    - JDK 8 应用：MAT 1.16/1.17
    - JDK 11 应用：MAT 1.16/1.17
    - JDK 17/21 应用：MAT 1.17
- 生成 Dump 时，`jcmd`、`jmap` 等工具最好使用与目标 JVM 相同大版本的 JDK。
- MAT 1.17 的 standalone 版本官方要求最低 **Java 21**。如果机器只有 JDK 8 或 JDK 11，不能直接运行该版本。

官方说明：

- [MAT 下载与版本信息](https://eclipse.dev/mat/download/)
- [MAT 1.17.0 Release Notes](https://eclipse.dev/mat/1.17.0/noteworthy.html)
mat 下载地址 https://eclipse.dev/mat/download/