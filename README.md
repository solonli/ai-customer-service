# 智能客服系统

基于自然语言处理（NLP）技术的智能化客户服务平台，采用"AI+人工"协同模式，提供7×24小时智能问答、工单流转、知识库管理、数据分析等功能。

## 项目架构

### 系统架构

```
┌─────────────────────────────────────────────────────────────────┐
│                         接入层 (Gateway)                         │
│   Nginx + API Gateway (Spring Cloud Gateway) + WebSocket        │
└─────────────────────────────────────────────────────────────────┘
                                  ↓
┌─────────────────────────────────────────────────────────────────┐
│                         服务层 (Services)                        │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐           │
│  │用户交互   │ │对话管理   │ │知识库管理│ │智能处理  │           │
│  │服务      │ │服务      │ │服务      │ │服务      │           │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘           │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐                        │
│  │运营管理   │ │统计分析   │ │认证授权  │                        │
│  │服务      │ │服务      │ │服务      │                        │
│  └──────────┘ └──────────┘ └──────────┘                        │
└─────────────────────────────────────────────────────────────────┘
                                  ↓
┌─────────────────────────────────────────────────────────────────┐
│                         数据层 (Data)                            │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐           │
│  │MySQL     │ │Redis     │ │Elasticsearch│ │MinIO/OSS│           │
│  │主从集群   │ │哨兵集群   │ │集群       │ │对象存储  │           │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘           │
└─────────────────────────────────────────────────────────────────┘
```

### 技术栈

#### 后端
- Java 17
- Spring Boot 3.2
- Spring Cloud Alibaba 2023
- MyBatis Plus
- MySQL 8.0
- Redis 6.0
- Redisson
- Elasticsearch 8.x
- Nacos（服务注册发现/配置中心）

#### 前端
- React 18 + TypeScript
- Vite
- Ant Design 5
- Zustand（状态管理）
- React Router 6
- Axios
- ahooks

## 项目结构

```
smart-customer-service/
├── smart-cs-backend/           # 后端项目
│   ├── gateway-service/        # API网关服务
│   ├── user-interaction-service/ # 用户交互服务（WebSocket）
│   ├── dialogue-service/       # 对话管理服务
│   ├── knowledge-service/      # 知识库服务
│   ├── nlu-service/            # 智能处理服务
│   ├── operation-service/      # 运营管理服务（工单、话术）
│   ├── statistics-service/     # 统计分析服务
│   ├── auth-service/           # 认证授权服务
│   ├── common/                 # 公共模块
│   └── sql/                    # 数据库脚本
│       ├── init.sql            # MySQL初始化脚本
│       ├── es_knowledge_index.json # ES索引配置
│       └── redis_design.md     # Redis设计文档
│
├── smart-cs-frontend/          # 前端项目
│   ├── packages/
│   │   ├── user-chat/          # 用户端聊天应用
│   │   ├── agent-workspace/    # 客服工作台应用（预留）
│   │   └── admin-console/      # 管理后台应用
│   └── shared/                 # 共享代码
│
└── docs/                       # 文档
    ├── requirements/           # 需求文档
    └── architecture/           # 架构设计文档
```

## 核心功能

### 1. 用户交互模块
- Web渠道接入
- WebSocket实时消息推送
- 会话管理（创建/保持/结束）
- 会话转人工
- 满意度评价

### 2. 对话管理模块
- 多轮对话上下文管理
- 意图识别
- 实体提取
- 对话策略引擎（直接回答/追问/转人工）
- 知识库检索

### 3. 知识库管理模块
- 知识条目CRUD
- 知识分类管理
- 知识审核流程
- 语义检索（关键词/模糊匹配）
- 知识版本管理

### 4. 工单系统
- 工单创建/分配/处理/跟踪
- 工单状态流转
- 工单分类管理
- 工单统计

### 5. 运营管理模块
- RBAC权限管理
- 用户管理
- 角色管理
- 话术模板管理
- 客服坐席管理（预留）

### 6. 统计分析模块
- 对话统计
- 工单统计
- 客服绩效统计
- 数据导出

## 快速开始

### 前置要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Redis 6.0+
- Elasticsearch 7.x/8.x

### 后端启动

1. 创建数据库
```bash
mysql -u root -p
source smart-cs-backend/sql/init.sql
```

2. 启动服务
```bash
cd smart-cs-backend
mvn clean install

# 依次启动各服务
cd auth-service && mvn spring-boot:run
cd user-interaction-service && mvn spring-boot:run
cd dialogue-service && mvn spring-boot:run
cd knowledge-service && mvn spring-boot:run
cd operation-service && mvn spring-boot:run
cd gateway-service && mvn spring-boot:run
```

### 前端启动

```bash
cd smart-cs-frontend

# 启动用户端
cd packages/user-chat
pnpm install
pnpm dev

# 启动管理后台
cd packages/admin-console
pnpm install
pnpm dev
```

## 服务端口

| 服务 | 端口 | 说明 |
|------|------|------|
| gateway-service | 8080 | API网关 |
| auth-service | 8084 | 认证授权 |
| user-interaction-service | 8081 | 用户交互 |
| dialogue-service | 8082 | 对话管理 |
| knowledge-service | 8083 | 知识库 |
| operation-service | 8085 | 运营管理 |
| statistics-service | 8086 | 统计分析 |
| user-chat | 3001 | 用户端前端 |
| admin-console | 3003 | 管理后台前端 |

## 测试账号

| 账号类型 | 用户名 | 密码 | 说明 |
|---------|--------|------|------|
| 管理员 | admin | Admin@123 | 系统管理员（测试使用） |

## 核心API文档

### 认证接口
- `POST /api/v1/auth/login` - 登录
- `POST /api/v1/auth/logout` - 登出
- `POST /api/v1/auth/refresh` - 刷新Token

### 会话接口
- `POST /api/v1/sessions` - 创建会话
- `GET /api/v1/sessions/{id}` - 获取会话信息
- `PUT /api/v1/sessions/{id}/close` - 关闭会话
- `POST /api/v1/sessions/{id}/transfer` - 请求转人工

### 知识库接口
- `GET /api/v1/knowledge` - 知识列表
- `POST /api/v1/knowledge` - 创建知识
- `POST /api/v1/knowledge/{id}/publish` - 发布知识
- `POST /api/v1/knowledge/search` - 知识检索

### 工单接口
- `GET /api/v1/tickets` - 工单列表
- `POST /api/v1/tickets` - 创建工单
- `POST /api/v1/tickets/{id}/assign` - 分配工单
- `POST /api/v1/tickets/{id}/status` - 更新状态

## 开发说明

### 后端开发规范
- 遵循阿里巴巴Java开发手册
- 使用MyBatis Plus进行数据库操作
- 统一使用Result包装返回结果
- 使用Redisson进行Redis操作
- 接口文档：Swagger/OpenAPI

### 前端开发规范
- 使用TypeScript
- 组件化开发
- 使用Ant Design组件库
- 使用Zustand进行状态管理
- 使用Axios进行HTTP请求

## 安全说明

- HTTPS传输加密
- JWT认证
- 敏感字段加密存储
- SQL注入/XSS防护
- 接口限流熔断
- 操作日志审计

## 性能指标

- 智能客服响应时间 ≤ 1秒
- 支持并发 ≥ 500 QPS
- 知识库检索时间 ≤ 300ms
- 数据库慢查询比例 ≤ 1%
- Redis缓存命中率 ≥ 90%

## 项目文档

- [需求文档](file:///workspace/智能客服系统功能需求说明书_完整版.md) - 完整的功能需求说明书
- [设计文档](file:///workspace/.trae/specs/smart-customer-service/) - 包含PRD、实现计划、架构设计等
- [数据库设计](file:///workspace/smart-cs-backend/sql/init.sql) - MySQL初始化脚本
- [Redis设计](file:///workspace/smart-cs-backend/sql/redis_design.md) - Redis缓存设计文档

## 许可证

MIT License

## 贡献

欢迎提交Issue和Pull Request！

---

*本项目基于React + Spring Cloud微服务架构构建，提供完整的智能客服解决方案。*
