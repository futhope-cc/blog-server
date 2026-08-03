# Spring Boot 项目开发规范

## 1. 总体代码质量要求

### 1.1 单一职责原则

- 每个方法只负责一个明确业务功能
- 一个方法建议控制在 50 行以内
- 复杂业务必须拆分多个私有方法

### 1.2 代码复用规范

- 禁止复制粘贴业务代码
- 通用逻辑抽取公共方法
- 工具类统一放入 utils
- 常量统一放入 constant

### 1.3 注释规范

必须为以下内容添加说明：

- 复杂业务流程
- 核心算法
- 状态转换
- 特殊兼容逻辑

注释说明"为什么这样做"，而不是简单描述代码。

### 1.4 异常处理

要求：

- 禁止空 catch
- 禁止捕获异常后不处理
- 异常必须转换为业务异常或统一处理

### 1.5 空值判断

对象：

```java
Objects.isNull()
Objects.nonNull()
```

集合：

```java
CollectionUtils.isEmpty()
CollectionUtils.isNotEmpty()
```

禁止：

```java
obj == null
list.size() == 0
```

---

# 2. 技术栈规范

默认：

类型       技术

---

Java       Java 17+
框架       Spring Boot 3.x
ORM        MyBatis-Plus
构建       Maven
数据库     MySQL
缓存       Redis
JSON       Jackson
工具       Lombok
参数校验   Hibernate Validator

---

# 3. 标准Maven目录结构

```text
project-root/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com.xxx.project/
│   │   │       ├── ProjectApplication.java
│   │   │       ├── controller/
│   │   │       ├── service/
│   │   │       │   └── impl/
│   │   │       ├── mapper/
│   │   │       ├── domain/
│   │   │       ├── dto/
│   │   │       │   ├── requestDTO/
│   │   │       │   └── responseDTO/
│   │   │       ├── converter/
│   │   │       ├── exception/
│   │   │       ├── handler/
│   │   │       ├── config/
│   │   │       ├── interceptor/
│   │   │       ├── websocket/
│   │   │       ├── utils/
│   │   │       └── constant/
│   │   └── resources/
│   │       └── application.yaml
│   └── test/
└── pom.xml
```

---

# 4. 分层架构规范

## Controller层

职责：

- 接收HTTP请求
- 参数校验
- 调用Service
- 返回统一结果

禁止：

- 编写业务逻辑
- 操作数据库
- 处理复杂转换

## Service层

职责：

- 核心业务处理
- 事务控制
- 多模块业务编排

要求：

接口：

service/UserService.java
实现：

service/impl/UserServiceImpl.java

## Mapper层

职责：

- 数据访问
- SQL封装

要求：

```java
@Mapper
public interface UserMapper extends BaseMapper<User>{

}
```

禁止：

- 编写业务逻辑
- 调用Service

---

# 5. DTO规范

目录：

dto/
├── requestDTO/
└── responseDTO/

## Request DTO

要求：

- 类名必须以 Request 结尾
- 使用 Lombok
- 添加校验注解

例如：

```java
@Data
public class UserCreateRequest {

    @NotBlank(message="用户名不能为空")
    private String username;

}
```

## Response DTO

要求：

- 类名必须以 Response 结尾
- Controller禁止直接返回Domain

---

# 6. Domain实体规范

要求：

- 所有实体继承 BaseDomain
- 使用 MyBatis-Plus 注解
- 使用 Lombok

示例：

```java
@Data
@TableName("sys_user")
public class User extends BaseDomain {

}
```

---

# 7. Converter规范

职责：

- DTO与Domain转换

目录：

converter
禁止：

Controller中直接：

```java
BeanUtils.copyProperties()
```

---

# 8. 统一返回结果

所有接口：

```java
Result<T>
```

格式：

```json
{
 "code":0,
 "message":"success",
 "data":{}
}
```

禁止：

直接返回 String/Object。

---

# 9. 异常规范

结构：

exception
├── BusinessException
├── ErrorCode

handler
└── GlobalExceptionHandler
业务异常：

```java
throw new BusinessException(ErrorCode.USER_NOT_EXIST);
```

---

# 10. 日志规范

级别：

级别    用途

---

ERROR   系统错误
WARN    风险情况
INFO    关键业务
DEBUG   调试信息

要求：

日志必须包含上下文。

正确：

```java
log.info("user create success,userId={}", userId);
```

禁止：

```java
log.info("success");
```

禁止输出：

- 密码
- Token
- 用户敏感信息

---

# 11. 事务规范

数据库写操作：

```java
@Transactional
```

要求：

- 事务只能放Service层
- Controller禁止事务

---

# 12. 数据库规范

表：

sys_user
字段：

create_time
update_time
禁止：

- SQL字符串拼接
- 硬编码SQL参数

---

# 13. 参数校验规范

Controller入口：

```java
@Valid
```

DTO使用：

```java
@NotNull
@NotBlank
@Size
@Pattern
```

---

# 14. Redis规范

Key统一管理。

禁止：

```java
"user:"+id
```

推荐：

```java
RedisKey.USER_INFO.format(id)
```

---

# 15. 配置规范

统一：

application.yaml
禁止：

```java
private String url="xxx";
```

---

# 16.接口文档生成规范

使用springdoc作为接口文档生成相关的框架/工具链

1.所有接口都需要配置springdoc注解，实现在网页上进行调试

2.接口的入参也要加上注解，描述入参含义

---

# 17. AI代码生成规范

生成代码必须：

1. 优先复用已有代码
2. 不重复创建工具类
3. 遵循项目目录
4. 保持已有风格
5. 不修改无关代码

新增功能必须包含：

- Controller
- Service
- ServiceImpl
- Request DTO
- Response DTO
- Domain
- Mapper
- Converter
- Exception处理
- 日志

---

# 18. 禁止事项

禁止：

- Controller写业务
- Service直接返回Entity
- Mapper调用Service
- System.out.println
- Thread.sleep
- 魔法数字
- 空catch
- 随意创建Utils

---

# 19. Commit规范

格式：

type: description
类型：

feat     新功能
fix      修复
refactor 重构
docs     文档
test     测试
示例：

feat: add user register api

