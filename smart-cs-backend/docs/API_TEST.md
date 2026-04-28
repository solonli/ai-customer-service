# API接口测试文档

## 测试环境

- 基础URL: `http://localhost:8080`
- 认证方式: JWT Token

## 1. 认证接口测试

### 1.1 登录测试

**请求**
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "Admin@123"
}
```

**预期响应**
```json
{
  "success": true,
  "code": 200,
  "data": {
    "accessToken": "TOKEN_xxx",
    "refreshToken": "REFRESH_xxx",
    "expiresIn": 7200,
    "userInfo": {
      "userId": 1,
      "userNo": "U001",
      "nickName": "管理员",
      "userType": 1
    }
  }
}
```

### 1.2 刷新Token测试

**请求**
```http
POST /api/v1/auth/refresh?refreshToken=REFRESH_xxx
```

**预期响应**
```json
{
  "success": true,
  "data": {
    "accessToken": "NEW_TOKEN_xxx",
    "refreshToken": "NEW_REFRESH_xxx",
    "expiresIn": 7200
  }
}
```

## 2. 会话接口测试

### 2.1 创建会话

**请求**
```http
POST /api/v1/sessions
Content-Type: application/json
Authorization: Bearer {token}

{
  "userId": 100,
  "channel": "web"
}
```

**预期响应**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "sessionNo": "S20240115001",
    "userId": 100,
    "channel": "web",
    "status": 1
  }
}
```

### 2.2 获取会话信息

**请求**
```http
GET /api/v1/sessions/1
Authorization: Bearer {token}
```

### 2.3 关闭会话

**请求**
```http
PUT /api/v1/sessions/1/close
Authorization: Bearer {token}
```

### 2.4 转人工

**请求**
```http
POST /api/v1/sessions/1/transfer
Content-Type: application/json
Authorization: Bearer {token}

{
  "reason": "用户请求转人工"
}
```

## 3. NLP接口测试

### 3.1 完整NLU分析

**请求**
```http
POST /api/v1/nlu/analyze
Content-Type: application/json

{
  "text": "你好，我想查询订单JD20240115001"
}
```

**预期响应**
```json
{
  "success": true,
  "data": {
    "topIntent": {
      "code": "query_order",
      "name": "订单查询",
      "confidence": 0.92
    },
    "entities": [
      {
        "type": "order_id",
        "value": "JD20240115001",
        "confidence": 0.9
      }
    ],
    "keywords": ["订单", "查询"],
    "sentiment": {
      "label": "neutral",
      "score": 0.5
    }
  }
}
```

### 3.2 意图识别测试用例

| 输入文本 | 预期意图 | 预期置信度 |
|---------|---------|-----------|
| 你好 | greeting | > 0.5 |
| 我想查订单 | query_order | > 0.5 |
| 我要退款 | refund_request | > 0.5 |
| 转人工 | transfer_human | > 0.5 |
| 谢谢 | thanks | > 0.5 |

### 3.3 实体提取测试用例

| 输入文本 | 预期实体类型 | 预期实体值 |
|---------|-------------|-----------|
| 订单号JD20240115001 | order_id | JD20240115001 |
| 手机号13812345678 | phone | 13812345678 |
| 价格99.9元 | money | 99.9元 |
| 2024年1月15日 | date | 2024年1月15日 |

## 4. 对话接口测试

### 4.1 处理消息

**请求**
```http
POST /api/v1/dialogue/process
Content-Type: application/json

{
  "sessionId": 1,
  "userId": 100,
  "content": "我想查询订单",
  "messageType": "text"
}
```

**预期响应**
```json
{
  "success": true,
  "data": {
    "success": true,
    "strategy": "DIRECT_ANSWER",
    "replyContent": "收到您的订单查询请求，请提供订单号或更多信息，我来帮您查询。",
    "nluResult": {
      "topIntent": {
        "code": "query_order",
        "confidence": 0.92
      }
    }
  }
}
```

### 4.2 对话策略测试用例

| 输入内容 | 预期策略 | 说明 |
|---------|---------|------|
| 你好 | DIRECT_ANSWER | 直接回答问候 |
| 查订单 | DIRECT_ANSWER | 直接回答订单查询 |
| 转人工 | TRANSFER_HUMAN | 转人工服务 |
| 随机文本 | FALLBACK/CLARIFY | 追问或回退 |

## 5. 知识库接口测试

### 5.1 创建知识

**请求**
```http
POST /api/v1/knowledge
Content-Type: application/json
Authorization: Bearer {token}

{
  "title": "如何查询订单",
  "content": "登录账户后，点击我的订单即可查看所有订单信息",
  "categoryId": 1,
  "keywords": "订单,查询"
}
```

### 5.2 搜索知识

**请求**
```http
POST /api/v1/knowledge/search
Content-Type: application/json

{
  "keyword": "订单",
  "page": 1,
  "size": 10
}
```

## 6. 工单接口测试

### 6.1 创建工单

**请求**
```http
POST /api/v1/tickets
Content-Type: application/json
Authorization: Bearer {token}

{
  "title": "用户投诉",
  "description": "用户对服务不满意",
  "categoryId": 1,
  "priority": 2,
  "sourceSessionId": 1
}
```

### 6.2 分配工单

**请求**
```http
POST /api/v1/tickets/1/assign
Content-Type: application/json
Authorization: Bearer {token}

{
  "agentId": 10
}
```

## 7. 性能测试指标

| 接口 | 预期响应时间 | 并发数 |
|------|-------------|--------|
| 登录 | < 200ms | 100 |
| NLU分析 | < 100ms | 500 |
| 对话处理 | < 500ms | 200 |
| 知识搜索 | < 300ms | 300 |

## 8. 错误码定义

| 错误码 | 说明 |
|-------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未授权 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |
