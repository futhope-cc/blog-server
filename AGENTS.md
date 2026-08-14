# AGENTS.md

单模块 Spring Boot 博客后端项目。完整开发规范见 `.trae/rules/springboot-project-rule.md`（19 条：分层、DTO、Result、异常、日志、事务、Redis、springdoc 等，**必须遵守**）。本文只收录"不看会踩坑"的要点。

## 项目概览

- 技术栈：Spring Boot 3.4.3 / Java 17 / Maven（**无 mvnw，用系统 `mvn`**）/ MyBatis-Plus 3.5.17 / Sa-Token 1.45.0 / springdoc 2.7.0 / MapStruct 1.5.5 / Lombok / hutool-crypto(BCrypt) / MySQL / Redis
- 基础包 `com.cc.blogserver`，标准分层目录见规范第 3 节
- 业务模块：用户模块（已实现）+ 文章/分类/标签/项目/文件/数据统计/关于我（均已实现，接口定义以 `docs/前后台接口整合与评审.md` 为准）
- 文件存储：**本地磁盘**（非 MinIO），配置 `file.*`（`application.yaml`），上传目录 `./upload`（已 gitignore），静态映射 `/files/**`，业务层校验类型/大小
- 文章浏览量：Redis 计数（key 走 `RedisKey.ARTICLE_VIEW_COUNT`）+ 定时任务 `task/ArticleViewCountTask` 回写数据库

## 文档注意

- **权威规范**：`.trae/rules/springboot-project-rule.md`，所有新增代码必须符合。
- **陷阱**：`.trae/documents/用户模块实现计划.md` 是历史计划，与现状不符（计划写 SB4.1.0、`/admin` 路径、雪花 ID；实际是 SB3.4.3、`/user` 路径、自增 ID、手动逻辑删除）。新增模块以**现有代码 + 规范文件**为准，勿照搬该计划。
- `docs/`：需求文档与接口对接文档。

## 本地运行与验证

- 前置依赖：MySQL 库 `blog`（localhost:3306，root/123456）+ Redis（127.0.0.1:6379，密码 123456，db=1），配置在 `application.yaml`。Sa-Token 的 token 存 Redis，Redis 不可用则登录/鉴权全挂。
- 建表：**手动执行** `src/main/resources/sql/init.sql`（应用不会自动建表）。
- 默认管理员 admin/123456 **不会自动初始化**（无 DataInitializer）。手动初始化方式：运行测试方法 `mvn test -Dtest=BlogServerApplicationTests#initDefaultAdmin`。
- 启动：`mvn spring-boot:run`，端口 8081，context-path=`/api`。
- 接口文档：`http://localhost:8081/api/swagger-ui.html`（登录后在 Authorize 填 satoken token）。
- 编译校验：`mvn clean compile` 后确认 `target/generated-sources/annotations/com/cc/blogserver/converter/UserConverterImpl.java` 已生成（MapStruct 处理器异常时转换器 Bean 缺失，启动报 `No qualifying bean`）。
- 测试：`mvn test`（JUnit5，`@SpringBootTest` 需 MySQL+Redis 在线）。

## 架构约定（与规范默认有差异的）

- 包结构：`controller / service(+impl) / mapper / domain / dto/requestDTO / dto/responseDTO / converter / exception / handler / config / utils / constant / common`。
- DTO 命名实际为 `XxxRequestDTO` / `XxxResponseDTO`（规范文档写 `XxxRequest`，**以现有代码为准**）。
- Controller：`@RequestMapping` 只用单层前缀（如 `/user`），方法路径写死；禁止写业务逻辑、禁止 `BeanUtils.copyProperties`（统一走 converter）。
- 响应 DTO 的 `id` 用 **String**（Long→String 防前端精度丢失，MapStruct 自动转换）。
- 分页请求 DTO 继承 `PageRequestDTO`（current/size + `@Min` 校验），返回 `PageResult<T>`。

## 关键坑点（新模块必读）

- **逻辑删除是手动的，未用 `@TableLogic`**：插入时 `is_delete` 自动填 0；所有查询必须手动加 `.eq(Xxx::getIsDelete, 0)`；删除用 `updateById` 置 `is_delete=1`，**禁止 `deleteById` 物理删除**。用户名唯一索引为 `(username, is_delete)`，软删记录可复用同名。
- **Sa-Token 拦截器路径不含 `/api`**，Controller mapping 也不带 `/api`。拦截 `"/user/**"、"/article/**"、"/category/**"、"/tag/**"、"/project/**"、"/file/**"、"/stats/**"、"/profile/**"`；放行 `/user/login`、`/doc.html`、`/swagger-ui/**`、`/v3/api-docs/**`、`/webjars/**`。前台公开接口（如 `GET /article/list`、`GET /article/{数字id}`、`GET /category/list`、`GET /tag/list`、`GET /project/list`、`GET /profile`）在 `SaTokenConfig.isPublic()` 里按「方法+路径」精确放行（`/article/{id}` 用正则 `^/article/\d+$` 区分 `/article/page`，勿改成宽泛 `/article/*`）。新增模块需同步改 `SaTokenConfig`。
- 所有新接口必须配 springdoc 注解：类 `@Tag`、方法 `@Operation`、DTO 字段 `@Schema`、路径参数 `@Parameter`（规范第 16 条，否则网页调试不可用）。
- 改动 pom 依赖后，`annotationProcessorPaths` 顺序固定：lombok → mapstruct-processor → lombok-mapstruct-binding（`default-compile` 与 `default-testCompile` 两处都要改）。
- Redis key 统一走 `RedisKey` 枚举的 `format(...)`，禁止 `"user:"+id` 字符串拼接。
- 密码一律用 `PasswordUtils.hash/verify`（BCrypt cost=10），日志禁止输出密码与 token。

## 代码风格硬性要求（源自规范）

- 空值判断：`Objects.isNull/nonNull`、`CollectionUtils.isEmpty`；禁止 `== null`、`list.size()==0`。
- 禁止：空 catch、捕获后不处理、`System.out.println`、`Thread.sleep`、魔法数字（抽常量）、Controller 写业务、Service 返回 Entity、Mapper 调 Service、随意新建 Utils。
- 写操作加 `@Transactional(rollbackFor = Exception.class)`，事务只放 Service 层。
- 日志必须带上下文（`log.info("用户登录成功, userId={}", id)`），禁止 `log.info("success")` 这类无参数日志。
- 新增完整业务功能必须包含：Controller / Service / ServiceImpl / Request DTO / Response DTO / Domain / Mapper / Converter / 异常处理 / 日志。
- 异常统一 `throw new BusinessException(ErrorCode.XXX)`，由 `GlobalExceptionHandler` 转成 `Result`，禁止直接返回 String/Object。

## 提交规范

格式 `type: description`，type ∈ `feat/fix/refactor/docs/test`。仓库现有提交信息为中文，如 `fix：optimize user controller`。
