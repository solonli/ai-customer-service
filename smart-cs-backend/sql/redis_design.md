# Redis缓存设计

## Key命名规范

### 基础格式
```
{业务模块}:{资源类型}:{资源ID}:{属性}
```

### 示例
- `session:info:S123456` - 会话信息
- `session:context:S123456` - 会话上下文
- `user:info:U10001` - 用户信息
- `agent:status:A001` - 客服状态

## 缓存结构设计

### 1. 会话相关

#### 会话信息
- Key: `session:info:{session_id}`
- Type: Hash
- TTL: 30分钟
- Fields:
  - user_id: 用户ID
  - status: 会话状态
  - agent_id: 客服ID
  - created_at: 创建时间
  - last_message_time: 最后消息时间

#### 会话上下文
- Key: `session:context:{session_id}`
- Type: Hash
- TTL: 30分钟
- Fields:
  - current_intent: 当前意图
  - slots: 槽位信息(JSON)
  - history: 对话历史(JSON)
  - turn_count: 对话轮次

#### 用户会话列表
- Key: `user:sessions:{user_id}`
- Type: List
- TTL: 24小时
- Value: 会话ID列表

### 2. 用户相关

#### 用户信息缓存
- Key: `user:info:{user_id}`
- Type: Hash
- TTL: 24小时
- Fields:
  - nick_name: 昵称
  - avatar_url: 头像
  - user_type: 用户类型
  - status: 状态

#### 用户Token
- Key: `user:token:{user_id}`
- Type: String
- TTL: 2小时
- Value: JWT Token

#### Refresh Token
- Key: `user:refresh_token:{user_id}`
- Type: String
- TTL: 7天
- Value: Refresh Token

### 3. 客服相关

#### 客服状态
- Key: `agent:status:{agent_id}`
- Type: Hash
- TTL: 无（持久化）
- Fields:
  - status: 在线状态(online/busy/offline)
  - current_sessions: 当前会话数
  - max_sessions: 最大会话数
  - skill_groups: 技能组ID列表

#### 客服在线列表
- Key: `agent:online:{skill_group_id}`
- Type: Sorted Set
- TTL: 无
- Score: 当前会话数（用于负载均衡）
- Member: 客服ID

#### 排队队列
- Key: `queue:waiting:{skill_group_id}`
- Type: List
- TTL: 无
- Value: 排队记录ID

### 4. 知识库相关

#### 知识条目缓存
- Key: `knowledge:entry:{entry_id}`
- Type: Hash
- TTL: 1小时
- Fields:
  - title: 标题
  - content: 内容
  - keywords: 关键词
  - status: 状态

#### 热门知识
- Key: `knowledge:hot`
- Type: Sorted Set
- TTL: 24小时
- Score: 命中次数
- Member: 知识条目ID

### 5. 限流相关

#### 接口限流
- Key: `ratelimit:{api}:{user_id}`
- Type: String
- TTL: 1分钟
- Value: 请求次数

#### 登录失败次数
- Key: `login:fail:{account}`
- Type: String
- TTL: 15分钟
- Value: 失败次数

### 6. 分布式锁

#### 会话锁
- Key: `lock:session:{session_id}`
- Type: String
- TTL: 10秒
- Value: 锁持有者标识

#### 工单锁
- Key: `lock:ticket:{ticket_id}`
- Type: String
- TTL: 30秒
- Value: 锁持有者标识

## 缓存策略

### 缓存更新策略
1. **写穿透(Write-Through)**: 先更新数据库，再更新缓存
2. **写回(Write-Behind)**: 先更新缓存，异步更新数据库
3. **失效(Invalidation)**: 更新数据库时删除缓存

### 缓存穿透防护
- 空值缓存: 查询为空时缓存空值，TTL设置为5分钟
- 布隆过滤器: 对热点数据使用布隆过滤器

### 缓存雪崩防护
- 随机过期时间: 在基础TTL上增加随机值
- 互斥锁: 缓存失效时使用分布式锁防止并发重建

### 缓存击穿防护
- 热点数据永不过期
- 使用分布式锁重建缓存

## Redis配置建议

```conf
# 内存配置
maxmemory 4gb
maxmemory-policy allkeys-lru

# 持久化配置
appendonly yes
appendfsync everysec

# 主从配置
replica-serve-stale-data yes
replica-read-only yes

# 集群配置(如需要)
cluster-enabled yes
cluster-config-file nodes.conf
cluster-node-timeout 5000
```
