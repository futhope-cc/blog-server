# 前台 API 接口文档

本项目的所有接口均由 `src/mock/index.ts` 在浏览器内拦截模拟返回（axios 请求拦截器设置 `config.adapter`），不会真正访问网络。接口的封装位于 `src/api/index.ts`，类型定义位于 `src/types/index.ts`。

## 通用约定

- 请求基路径：`/api`（如 `/api/article/list`）。
- 统一响应包装：

  ```json
  {
    "code": 200,
    "message": "success",
    "data": {}
  }
  ```

  - `code === 200` 表示成功；`request()` 封装会自动抛出并提示错误。
  - 未匹配到接口或资源不存在时返回 `code = 404`（对应 HTTP 404）。

- 分页参数、筛选条件、关键字等均通过查询参数传递（前端以 `config.params` 传入，mock 会将其与 URL 查询串合并解析）。
- 仅提供读取类接口（GET），无登录/鉴权，无写操作接口。

## 接口一览

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/article/list` | 文章分页列表（支持分类/标签/关键字筛选） |
| GET | `/api/article/:id` | 文章详情（阅读量 +1） |
| GET | `/api/category/list` | 分类列表（含文章数） |
| GET | `/api/tag/list` | 标签列表（含文章数） |
| GET | `/api/project/list` | 项目列表 |
| GET | `/api/project/:id` | 项目详情 |

---

## 1. 文章列表

`GET /api/article/list`

### 入参（查询参数）

| 参数 | 类型 | 必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| `page` | number | 否 | 1 | 页码，从 1 开始 |
| `pageSize` | number | 否 | 10 | 每页条数 |
| `categoryId` | number | 否 | - | 按分类筛选 |
| `tagId` | number | 否 | - | 按标签筛选 |
| `keyword` | string | 否 | - | 关键字，匹配标题/摘要/正文/标签名 |

### 返回

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "title": "文章标题",
        "summary": "摘要",
        "cover": "https://picsum.photos/seed/1/1200/675",
        "categoryId": 1,
        "categoryName": "后端开发",
        "tags": [{ "id": 1, "name": "Redis" }],
        "viewCount": 100,
        "likeCount": 20,
        "commentCount": 5,
        "status": 1,
        "createTime": "2026-01-01 10:00:00",
        "updateTime": "2026-01-02 10:00:00"
      }
    ],
    "total": 100,
    "page": 1,
    "pageSize": 10
  }
}
```

> `status === 1` 的文章才会被返回（仅返回已发布文章）。`commentCount` 字段在数据源中存在，但评论功能已移除，目前恒为 0。

---

## 2. 文章详情

`GET /api/article/:id`

- 路径参数 `id`：文章 ID（number）。
- 无查询参数。
- 副作用：读取后 mock 中该文章的 `viewCount` 会自增。
- 文章不存在或未发布时返回 `code = 404`，`message = "文章不存在"`。

### 返回

在 `ArticleListItem` 基础上增加 `content` 字段：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "文章标题",
    "summary": "摘要",
    "cover": "https://picsum.photos/seed/1/1200/675",
    "categoryId": 1,
    "categoryName": "后端开发",
    "tags": [{ "id": 1, "name": "Redis" }],
    "viewCount": 101,
    "likeCount": 20,
    "commentCount": 0,
    "status": 1,
    "createTime": "2026-01-01 10:00:00",
    "updateTime": "2026-01-02 10:00:00",
    "content": "# Markdown 正文内容..."
  }
}
```

---

## 3. 分类列表

`GET /api/category/list`

- 无入参。
- 返回分类数组，附带每类已发布文章的数量 `articleCount`。

### 返回

```json
{
  "code": 200,
  "message": "success",
  "data": [
    { "id": 1, "name": "后端开发", "sort": 1, "articleCount": 12, "createTime": "2026-01-01 10:00:00" }
  ]
}
```

---

## 4. 标签列表

`GET /api/tag/list`

- 无入参。
- 返回标签数组，附带每个标签在已发布文章中的使用次数 `articleCount`。

### 返回

```json
{
  "code": 200,
  "message": "success",
  "data": [
    { "id": 1, "name": "Redis", "articleCount": 3 }
  ]
}
```

---

## 5. 项目列表

`GET /api/project/list`

- 无入参。
- 返回全部项目（未分页、无筛选）。前端自行过滤 `featured === 1` 取首页展示。

### 返回

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "项目名称",
      "description": "项目描述",
      "technology": "Vue3 / Go / Redis",
      "githubUrl": "https://github.com/devpanda/demo",
      "image": "https://picsum.photos/seed/project1/1200/675",
      "deployment": "Docker Compose 一键部署",
      "featured": 1,
      "createTime": "2026-01-01 10:00:00"
    }
  ]
}
```

---

## 6. 项目详情

`GET /api/project/:id`

- 路径参数 `id`：项目 ID（number）。
- 无查询参数。
- 项目不存在时返回 `code = 404`，`message = "项目不存在"`。

### 返回

`data` 为单个 `Project` 对象，结构与项目列表项一致。

---

## 类型定义参考（`src/types/index.ts`）

```ts
export interface Category {
  id: number
  name: string
  sort: number
  articleCount?: number
  createTime: string
}

export interface Tag {
  id: number
  name: string
  articleCount?: number
}

export interface ArticleListItem {
  id: number
  title: string
  summary: string
  cover: string
  categoryId: number
  categoryName?: string
  tags: Tag[]
  viewCount: number
  likeCount: number
  commentCount: number
  status: number
  createTime: string
  updateTime?: string
}

export interface ArticleDetail extends ArticleListItem {
  content: string
}

export interface PageResult<T> {
  list: T[]
  total: number
  page: number
  pageSize: number
}

export interface ArticleQuery {
  page: number
  pageSize: number
  categoryId?: number
  tagId?: number
  keyword?: string
}

export interface Project {
  id: number
  name: string
  description: string
  technology: string
  githubUrl: string
  image: string
  deployment?: string
  featured?: number
  createTime: string
}
```

---

## 各页面使用到的接口

| 页面 | 使用的接口 |
| --- | --- |
| 首页（Home） | 文章列表（pageSize=6）、项目列表 |
| 文章列表（ArticleList） | 文章列表、分类列表、标签列表 |
| 文章详情（ArticleDetail） | 文章详情 |
| 搜索（Search） | 文章列表（keyword 筛选，分页） |
| 项目列表（ProjectList） | 项目列表 |
| 项目详情（ProjectDetail） | 项目详情 |
| 关于我（About） | 项目列表 |
