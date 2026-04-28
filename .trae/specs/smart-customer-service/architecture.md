# 智能客服系统 - 技术架构设计文档

## 1. 系统架构概述

### 1.1 整体架构

智能客服系统采用分层解耦的微服务架构，划分为四大层级：

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
│  │运营管理   │ │统计分析   │ │业务流程  │                        │
│  │服务      │ │服务      │ │管理服务  │                        │
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
                                ↓
┌─────────────────────────────────────────────────────────────────┐
│                         管理层 (Management)                      │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐           │
│  │Nacos     │ │Sentinel  │ │Skywalking│ │ELK       │           │
│  │注册配置   │ │限流熔断   │ │链路追踪   │ │日志分析  │           │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘           │
└─────────────────────────────────────────────────────────────────┘
```

### 1.2 技术选型

| 层级 | 技术栈 | 版本 | 说明 |
|-----|-------|-----|------|
| 前端框架 | React + TypeScript | 18.x | 用户端、客服工作台 |
| 前端框架 | Vue3 + TypeScript | 3.x | 管理后台（可选） |
| UI组件库 | Ant Design | 5.x | 企业级UI组件 |
| 状态管理 | Zustand / Pinia | 最新 | 轻量级状态管理 |
| 后端框架 | Spring Cloud Alibaba | 2022.x | 微服务框架 |
| 服务网关 | Spring Cloud Gateway | 4.x | API网关 |
| 注册中心 | Nacos | 2.x | 服务注册与配置中心 |
| 限流熔断 | Sentinel | 1.8.x | 流量控制 |
| 数据库 | MySQL | 8.0+ | 主从集群 |
| 缓存 | Redis | 6.0+ | 哨兵模式 |
| 搜索引擎 | Elasticsearch | 7.17.x | 全文检索 |
| 消息队列 | RocketMQ / Kafka | 最新 | 异步消息 |
| 对象存储 | MinIO / 阿里云OSS | 最新 | 文件存储 |
| 容器化 | Docker + Kubernetes | 最新 | 容器编排 |

## 2. 前端架构设计

### 2.1 前端项目结构

```
smart-cs-frontend/
├── packages/
│   ├── user-chat/          # 用户端聊天应用
│   │   ├── src/
│   │   │   ├── components/  # 组件
│   │   │   │   ├── ChatWindow/    # 聊天窗口
│   │   │   │   ├── MessageList/   # 消息列表
│   │   │   │   ├── MessageInput/  # 消息输入框
│   │   │   │   ├── SatisfactionModal/ # 满意度弹窗
│   │   │   │   └── TransferTip/    # 转人工提示
│   │   │   ├── hooks/       # 自定义Hooks
│   │   │   │   ├── useWebSocket.ts
│   │   │   │   ├── useChat.ts
│   │   │   │   └── useSession.ts
│   │   │   ├── services/    # API服务
│   │   │   ├── stores/      # 状态管理
│   │   │   ├── types/       # 类型定义
│   │   │   └── utils/       # 工具函数
│   │   └── package.json
│   │
│   ├── agent-workspace/    # 客服工作台应用
│   │   ├── src/
│   │   │   ├── components/
│   │   │   │   ├── SessionList/     # 会话列表
│   │   │   │   ├── ChatPanel/       # 聊天面板
│   │   │   │   ├── UserInfoSidebar/ # 用户信息侧边栏
│   │   │   │   ├── ScriptPanel/     # 话术面板
│   │   │   │   ├── TicketModal/     # 工单弹窗
│   │   │   │   └── AgentStatus/      # 坐席状态
│   │   │   ├── hooks/
│   │   │   ├── services/
│   │   │   ├── stores/
│   │   │   └── types/
│   │   └── package.json
│   │
│   └── admin-console/      # 管理后台应用
│       ├── src/
│       │   ├── components/
│       │   │   ├── KnowledgeManage/  # 知识库管理
│       │   │   ├── TicketManage/     # 工单管理
│       │   │   ├── UserManage/       # 用户管理
│       │   │   ├── RoleManage/       # 角色管理
│       │   │   ├── Statistics/       # 统计报表
│       │   │   └── SystemConfig/     # 系统配置
│       │   ├── layouts/     # 布局组件
│       │   ├── pages/       # 页面
│       │   ├── hooks/
│       │   ├── services/
│       │   └── stores/
│       └── package.json
│
├── shared/                  # 共享代码
│   ├── components/          # 共享组件
│   ├── hooks/               # 共享Hooks
│   ├── types/               # 共享类型
│   └── utils/               # 共享工具
│
├── package.json
└── pnpm-workspace.yaml
```

### 2.2 核心页面设计

#### 2.2.1 用户端聊天界面

```
┌─────────────────────────────────────────────────────────────┐
│  [Logo] 智能客服                          [最小化] [关闭]    │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  🤖 智能客服                            10:30:00   │   │
│  │  您好！我是智能客服小助手，请问有什么可以帮您？      │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  👤 我                                  10:30:15   │   │
│  │  我想查询我的订单状态                               │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  🤖 智能客服                            10:30:15   │   │
│  │  好的，请提供您的订单号，我来帮您查询。              │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐ │
│  │  [📎] [😊] 请输入消息...                    [发送] ➤ │ │
│  └───────────────────────────────────────────────────────┘ │
│                                                             │
│  [转人工]                                                   │
└─────────────────────────────────────────────────────────────┘
```

**组件说明**：
- `ChatWindow`: 聊天窗口容器，支持拖拽、最小化
- `MessageList`: 消息列表，支持文本、图片、卡片等多种消息类型
- `MessageInput`: 消息输入框，支持表情、附件上传
- `SatisfactionModal`: 满意度评价弹窗（会话结束后弹出）
- `TransferTip`: 转人工提示组件

#### 2.2.2 客服工作台界面

```
┌──────────────────────────────────────────────────────────────────────────┐
│  [Logo] 客服工作台          [在线 ▼]  张三  [设置] [退出]               │
├────────────────┬─────────────────────────────────────┬───────────────────┤
│                │                                     │                   │
│  会话列表 (5)   │         聊天面板                    │   用户信息        │
│                │                                     │                   │
│  ┌────────────┐│  ┌───────────────────────────────┐ │  👤 李四          │
│  │🔴 李四     ││  │ 🤖 您好，请问有什么可以帮您？ │ │  ID: U10001      │
│  │订单查询... ││  │ 👤 我想退款                   │ │  ────────────    │
│  │10:30       ││  │ 🤖 请提供订单号               │ │  会员等级: VIP   │
│  └────────────┘│  │ 👤 ORD123456                  │ │  注册时间:       │
│                │  │ 🤖 好的，正在为您查询...       │ │  2025-01-15      │
│  ┌────────────┐│  └───────────────────────────────┘ │  ────────────    │
│  │🟢 王五     ││                                     │  历史会话: 3次   │
│  │产品咨询... ││  ┌───────────────────────────────┐ │  ────────────    │
│  │10:25       ││  │ [📎] [😊] 输入消息... [发送]  │ │  标签:          │
│  └────────────┘│  └───────────────────────────────┘ │  高价值客户      │
│                │                                     │                   │
│  ┌────────────┐│  ┌───────────────────────────────┐ │  ────────────    │
│  │⚪ 赵六     ││  │ 📝 话术库                      │ │  [创建工单]     │
│  │投诉建议... ││  │ ┌───────────────────────────┐ │ │                   │
│  │10:20       ││  │ │ 问候语                    │ │                   │
│  └────────────┘│  │ │ 安抚话术                  │ │                   │
│                │  │ │ 退款说明                  │ │                   │
│                │  │ └───────────────────────────┘ │                   │
│                │  └───────────────────────────────┘ │                   │
└────────────────┴─────────────────────────────────────┴───────────────────┘
```

**组件说明**：
- `SessionList`: 会话列表，显示待处理会话，支持状态标识（新消息/处理中）
- `ChatPanel`: 聊天面板，支持多会话切换，显示消息历史
- `UserInfoSidebar`: 用户信息侧边栏，显示用户画像、历史会话
- `ScriptPanel`: 话术面板，快捷回复话术
- `AgentStatus`: 坐席状态切换（在线/忙碌/离线）

#### 2.2.3 管理后台界面

```
┌──────────────────────────────────────────────────────────────────────────┐
│  [Logo] 智能客服管理后台                              管理员 [退出]     │
├────────────────┬─────────────────────────────────────────────────────────┤
│                │                                                         │
│  📊 数据概览   │   数据概览                                              │
│                │   ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐      │
│  📈 统计分析   │   │今日会话  │ │转人工率 │ │工单数量  │ │满意度   │      │
│    ├ 对话统计  │   │  1,234  │ │  15.2%  │ │   89    │ │  4.8分  │      │
│    ├ 工单统计  │   └─────────┘ └─────────┘ └─────────┘ └─────────┘      │
│    └ 客服绩效  │                                                         │
│                │   会话趋势                                              │
│  📚 知识库管理 │   ┌─────────────────────────────────────────────────┐  │
│    ├ 知识列表  │   │                    📈                           │  │
│    ├ 分类管理  │   │              📈      📈     📈                  │  │
│    └ 审核管理  │   │         📈     📈          📈     📈            │  │
│                │   │    📈          📈                📈              │  │
│  🎫 工单管理   │   └─────────────────────────────────────────────────┘  │
│    ├ 工单列表  │     周一   周二   周三   周四   周五   周六   周日      │
│    └ 工单统计  │                                                         │
│                │   热门问题TOP10            工单分布                   │
│  👥 用户管理   │   ┌───────────────────┐   ┌───────────────────┐      │
│    ├ 用户列表  │   │ 1. 订单查询  234  │   │   ┌───┐           │      │
│    ├ 角色管理  │   │ 2. 退款咨询  189  │   │   │   │  45%      │      │
│    └ 权限管理  │   │ 3. 物流查询  156  │   │   └───┘           │      │
│                │   │ ...               │   │       ┌───┐       │      │
│  ⚙ 系统设置   │   └───────────────────┘   │       │   │  30%  │      │
│    ├ 基础配置  │                           │       └───┘       │      │
│    ├ 机器人配置│                           │           ┌───┐  │      │
│    └ 话术管理  │                           │           │   │  │      │
│                │                           │           └───┘  │      │
│                │                           └───────────────────┘      │
└────────────────┴─────────────────────────────────────────────────────────┘
```

**页面说明**：
- `数据概览`: 核心指标卡片、趋势图表
- `统计分析`: 对话统计、工单统计、客服绩效
- `知识库管理`: 知识CRUD、分类管理、审核流程
- `工单管理`: 工单列表、状态流转、统计
- `用户管理`: 用户列表、角色权限
- `系统设置`: 基础配置、机器人配置、话术管理

### 2.3 前端技术要点

#### 2.3.1 WebSocket连接管理

```typescript
// hooks/useWebSocket.ts
interface WebSocketOptions {
  url: string;
  token: string;
  onMessage: (data: any) => void;
  onOpen?: () => void;
  onClose?: () => void;
  onError?: (error: Event) => void;
}

export function useWebSocket(options: WebSocketOptions) {
  const [status, setStatus] = useState<'connecting' | 'connected' | 'disconnected'>('disconnected');
  const [lastMessage, setLastMessage] = useState<any>(null);
  const wsRef = useRef<WebSocket | null>(null);
  const reconnectAttempts = useRef(0);
  const maxReconnectAttempts = 5;

  const connect = useCallback(() => {
    const wsUrl = `${options.url}?token=${options.token}`;
    const ws = new WebSocket(wsUrl);
    
    ws.onopen = () => {
      setStatus('connected');
      reconnectAttempts.current = 0;
      options.onOpen?.();
      startHeartbeat();
    };
    
    ws.onmessage = (event) => {
      const data = JSON.parse(event.data);
      setLastMessage(data);
      options.onMessage(data);
    };
    
    ws.onclose = () => {
      setStatus('disconnected');
      options.onClose?.();
      attemptReconnect();
    };
    
    ws.onerror = (error) => {
      options.onError?.(error);
    };
    
    wsRef.current = ws;
  }, [options]);

  const startHeartbeat = () => {
    const heartbeat = setInterval(() => {
      if (wsRef.current?.readyState === WebSocket.OPEN) {
        wsRef.current.send(JSON.stringify({ type: 'ping', timestamp: Date.now() }));
      } else {
        clearInterval(heartbeat);
      }
    }, 30000);
  };

  const attemptReconnect = () => {
    if (reconnectAttempts.current < maxReconnectAttempts) {
      const delay = Math.pow(2, reconnectAttempts.current) * 1000;
      setTimeout(() => {
        reconnectAttempts.current++;
        connect();
      }, delay);
    }
  };

  const send = useCallback((data: any) => {
    if (wsRef.current?.readyState === WebSocket.OPEN) {
      wsRef.current.send(JSON.stringify(data));
    }
  }, []);

  return { status, lastMessage, connect, send };
}
```

#### 2.3.2 状态管理设计

```typescript
// stores/chatStore.ts
import { create } from 'zustand';

interface Message {
  id: string;
  sessionId: string;
  senderType: 'user' | 'bot' | 'agent' | 'system';
  content: string;
  messageType: 'text' | 'image' | 'card';
  createdAt: string;
}

interface Session {
  id: string;
  status: 'active' | 'transferred' | 'closed';
  messages: Message[];
  user?: UserInfo;
}

interface ChatState {
  currentSession: Session | null;
  sessions: Session[];
  addMessage: (sessionId: string, message: Message) => void;
  setCurrentSession: (session: Session) => void;
  updateSessionStatus: (sessionId: string, status: Session['status']) => void;
}

export const useChatStore = create<ChatState>((set) => ({
  currentSession: null,
  sessions: [],
  addMessage: (sessionId, message) => set((state) => {
    const sessions = state.sessions.map(s => 
      s.id === sessionId ? { ...s, messages: [...s.messages, message] } : s
    );
    const currentSession = state.currentSession?.id === sessionId
      ? { ...state.currentSession, messages: [...state.currentSession.messages, message] }
      : state.currentSession;
    return { sessions, currentSession };
  }),
  setCurrentSession: (session) => set({ currentSession: session }),
  updateSessionStatus: (sessionId, status) => set((state) => {
    const sessions = state.sessions.map(s =>
      s.id === sessionId ? { ...s, status } : s
    );
    const currentSession = state.currentSession?.id === sessionId
      ? { ...state.currentSession, status }
      : state.currentSession;
    return { sessions, currentSession };
  }),
}));
```

## 3. 后端架构设计

### 3.1 微服务拆分

```
smart-cs-backend/
├── gateway-service/           # API网关服务
│   ├── src/main/java/
│   │   └── com/smartcs/gateway/
│   │       ├── config/        # 网关配置
│   │       ├── filter/        # 过滤器（认证、限流、日志）
│   │       └── handler/       # 异常处理
│   └── pom.xml
│
├── user-interaction-service/  # 用户交互服务
│   ├── src/main/java/
│   │   └── com/smartcs/interaction/
│   │       ├── controller/    # REST接口
│   │       ├── websocket/     # WebSocket处理
│   │       ├── service/       # 业务逻辑
│   │       └── mapper/        # 数据访问
│   └── pom.xml
│
├── dialogue-service/           # 对话管理服务
│   ├── src/main/java/
│   │   └── com/smartcs/dialogue/
│   │       ├── controller/
│   │       ├── service/
│   │       │   ├── SessionService.java
│   │       │   ├── MessageService.java
│   │       │   └── DialogueContextService.java
│   │       ├── strategy/      # 对话策略
│   │       └── mapper/
│   └── pom.xml
│
├── knowledge-service/          # 知识库服务
│   ├── src/main/java/
│   │   └── com/smartcs/knowledge/
│   │       ├── controller/
│   │       ├── service/
│   │       │   ├── EntryService.java
│   │       │   ├── CategoryService.java
│   │       │   ├── AuditService.java
│   │       │   └── SearchService.java
│   │       └── mapper/
│   └── pom.xml
│
├── nlu-service/                # 智能处理服务
│   ├── src/main/java/
│   │   └── com/smartcs/nlu/
│   │       ├── controller/
│   │       ├── service/
│   │       │   ├── IntentService.java
│   │       │   ├── EntityService.java
│   │       │   └── NLUClientService.java
│   │       └── provider/      # NLU提供者（第三方适配）
│   └── pom.xml
│
├── operation-service/          # 运营管理服务
│   ├── src/main/java/
│   │   └── com/smartcs/operation/
│   │       ├── controller/
│   │       ├── service/
│   │       │   ├── TicketService.java
│   │       │   ├── AgentService.java
│   │       │   ├── ScriptService.java
│   │       │   └── SkillGroupService.java
│   │       └── mapper/
│   └── pom.xml
│
├── statistics-service/         # 统计分析服务
│   ├── src/main/java/
│   │   └── com/smartcs/statistics/
│   │       ├── controller/
│   │       ├── service/
│   │       ├── job/           # 定时任务
│   │       └── mapper/
│   └── pom.xml
│
├── auth-service/               # 认证授权服务
│   ├── src/main/java/
│   │   └── com/smartcs/auth/
│   │       ├── controller/
│   │       ├── service/
│   │       │   ├── AuthService.java
│   │       │   ├── UserService.java
│   │       │   ├── RoleService.java
│   │       │   └── PermissionService.java
│   │       └── security/
│   └── pom.xml
│
└── common/                     # 公共模块
    ├── common-core/           # 核心工具类
    ├── common-redis/          # Redis封装
    ├── common-es/             # ES封装
    ├── common-mq/             # 消息队列封装
    └── common-security/       # 安全组件
```

### 3.2 核心服务设计

#### 3.2.1 对话管理服务

```java
// dialogue-service/service/DialogueService.java
@Service
@Slf4j
public class DialogueService {
    
    @Autowired
    private SessionService sessionService;
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private NLUClientService nluClient;
    
    @Autowired
    private KnowledgeSearchService knowledgeSearch;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Value("${dialogue.confidence.threshold:0.85}")
    private Double confidenceThreshold;
    
    @Value("${dialogue.transfer.threshold:0.70}")
    private Double transferThreshold;
    
    public DialogueResult processMessage(String sessionId, String userId, String content) {
        Session session = sessionService.getOrCreateSession(sessionId, userId);
        
        DialogueContext context = getDialogueContext(sessionId);
        
        Message userMessage = messageService.saveMessage(sessionId, "user", content);
        
        NLUResult nluResult = nluClient.parse(content, context);
        
        updateContextWithNLU(sessionId, nluResult);
        
        DialogueStrategy strategy = determineStrategy(nluResult, context);
        
        return executeStrategy(session, strategy, nluResult, context);
    }
    
    private DialogueContext getDialogueContext(String sessionId) {
        String key = "session:context:" + sessionId;
        DialogueContext context = (DialogueContext) redisTemplate.opsForHash().get(key, "context");
        if (context == null) {
            context = new DialogueContext();
            context.setSessionId(sessionId);
            context.setDialogueHistory(new ArrayList<>());
            context.setSlots(new HashMap<>());
        }
        return context;
    }
    
    private DialogueStrategy determineStrategy(NLUResult nluResult, DialogueContext context) {
        Double confidence = nluResult.getTopIntent().getConfidence();
        
        if (confidence >= confidenceThreshold) {
            return DialogueStrategy.DIRECT_ANSWER;
        } else if (confidence >= transferThreshold) {
            return DialogueStrategy.CLARIFY;
        } else {
            return DialogueStrategy.TRANSFER_HUMAN;
        }
    }
    
    private DialogueResult executeStrategy(Session session, DialogueStrategy strategy, 
                                           NLUResult nluResult, DialogueContext context) {
        switch (strategy) {
            case DIRECT_ANSWER:
                return handleDirectAnswer(session, nluResult, context);
            case CLARIFY:
                return handleClarify(session, nluResult, context);
            case TRANSFER_HUMAN:
                return handleTransfer(session, nluResult, context);
            default:
                return handleUnknown(session);
        }
    }
    
    private DialogueResult handleDirectAnswer(Session session, NLUResult nluResult, DialogueContext context) {
        String intentCode = nluResult.getTopIntent().getIntentCode();
        
        List<KnowledgeEntry> entries = knowledgeSearch.searchByIntent(intentCode);
        
        if (entries.isEmpty()) {
            entries = knowledgeSearch.searchByKeywords(nluResult.getKeywords());
        }
        
        if (!entries.isEmpty()) {
            KnowledgeEntry entry = entries.get(0);
            Message botMessage = messageService.saveMessage(
                session.getId(), "bot", entry.getContent());
            botMessage.setKnowledgeId(entry.getId());
            
            return DialogueResult.success(botMessage, DialogueStrategy.DIRECT_ANSWER);
        }
        
        return handleUnknown(session);
    }
    
    private DialogueResult handleTransfer(Session session, NLUResult nluResult, DialogueContext context) {
        sessionService.updateStatus(session.getId(), SessionStatus.TRANSFERRED);
        
        String transferReason = String.format("意图识别置信度较低(%.2f)", 
            nluResult.getTopIntent().getConfidence());
        
        return DialogueResult.transfer(transferReason, context);
    }
}
```

#### 3.2.2 知识库检索服务

```java
// knowledge-service/service/SearchService.java
@Service
@Slf4j
public class KnowledgeSearchService {
    
    @Autowired
    private RestHighLevelClient esClient;
    
    @Autowired
    private KnowledgeEntryMapper entryMapper;
    
    private static final String INDEX_NAME = "cs_knowledge";
    
    public List<KnowledgeEntry> searchByIntent(String intentCode) {
        SearchRequest request = new SearchRequest(INDEX_NAME);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        
        sourceBuilder.query(QueryBuilders.termQuery("intent_code", intentCode));
        sourceBuilder.query(QueryBuilders.termQuery("status", 1));
        sourceBuilder.size(5);
        
        request.source(sourceBuilder);
        
        try {
            SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
            return parseSearchResponse(response);
        } catch (IOException e) {
            log.error("ES搜索失败", e);
            return Collections.emptyList();
        }
    }
    
    public List<KnowledgeEntry> searchByKeywords(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return Collections.emptyList();
        }
        
        SearchRequest request = new SearchRequest(INDEX_NAME);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        for (String keyword : keywords) {
            boolQuery.should(QueryBuilders.matchQuery("title", keyword));
            boolQuery.should(QueryBuilders.matchQuery("content", keyword));
            boolQuery.should(QueryBuilders.matchQuery("keywords", keyword));
        }
        boolQuery.minimumShouldMatch(1);
        boolQuery.filter(QueryBuilders.termQuery("status", 1));
        
        sourceBuilder.query(boolQuery);
        sourceBuilder.size(10);
        
        request.source(sourceBuilder);
        
        try {
            SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
            return parseSearchResponse(response);
        } catch (IOException e) {
            log.error("ES搜索失败", e);
            return Collections.emptyList();
        }
    }
    
    public List<KnowledgeEntry> fuzzySearch(String query, int size) {
        SearchRequest request = new SearchRequest(INDEX_NAME);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        
        MultiMatchQueryBuilder multiMatch = QueryBuilders.multiMatchQuery(query)
            .field("title", 2.0f)
            .field("content", 1.0f)
            .field("similar_questions", 1.5f)
            .type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
            .fuzziness(Fuzziness.AUTO);
        
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
            .must(multiMatch)
            .filter(QueryBuilders.termQuery("status", 1));
        
        sourceBuilder.query(boolQuery);
        sourceBuilder.size(size);
        
        request.source(sourceBuilder);
        
        try {
            SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
            return parseSearchResponse(response);
        } catch (IOException e) {
            log.error("ES模糊搜索失败", e);
            return Collections.emptyList();
        }
    }
    
    private List<KnowledgeEntry> parseSearchResponse(SearchResponse response) {
        return Arrays.stream(response.getHits().getHits())
            .map(hit -> {
                Map<String, Object> source = hit.getSourceAsMap();
                KnowledgeEntry entry = new KnowledgeEntry();
                entry.setId(Long.parseLong(source.get("entry_id").toString()));
                entry.setTitle((String) source.get("title"));
                entry.setContent((String) source.get("content"));
                entry.setScore(hit.getScore());
                return entry;
            })
            .collect(Collectors.toList());
    }
}
```

### 3.3 API接口设计

#### 3.3.1 RESTful API规范

```
基础路径: /api/v1

认证接口:
POST   /auth/login              # 登录
POST   /auth/logout             # 登出
POST   /auth/refresh            # 刷新Token

会话接口:
POST   /sessions                # 创建会话
GET    /sessions/{id}           # 获取会话信息
PUT    /sessions/{id}/close     # 关闭会话
POST   /sessions/{id}/messages  # 发送消息
GET    /sessions/{id}/messages  # 获取消息历史
POST   /sessions/{id}/satisfaction # 提交满意度评价
POST   /sessions/{id}/transfer  # 请求转人工

知识库接口:
GET    /knowledge               # 知识列表
POST   /knowledge               # 创建知识
GET    /knowledge/{id}          # 知识详情
PUT    /knowledge/{id}          # 更新知识
DELETE /knowledge/{id}          # 删除知识
POST   /knowledge/{id}/publish  # 发布知识
POST   /knowledge/{id}/audit    # 审核知识
GET    /knowledge/search        # 知识检索
POST   /knowledge/import        # 批量导入

工单接口:
GET    /tickets                 # 工单列表
POST   /tickets                 # 创建工单
GET    /tickets/{id}            # 工单详情
PUT    /tickets/{id}            # 更新工单
PUT    /tickets/{id}/assign     # 分配工单
PUT    /tickets/{id}/status     # 更新状态

用户管理接口:
GET    /users                   # 用户列表
POST   /users                   # 创建用户
GET    /users/{id}              # 用户详情
PUT    /users/{id}              # 更新用户
DELETE /users/{id}              # 删除用户

统计接口:
GET    /statistics/overview     # 数据概览
GET    /statistics/session      # 会话统计
GET    /statistics/ticket       # 工单统计
GET    /statistics/agent        # 客服绩效
```

#### 3.3.2 WebSocket消息协议

```json
// 客户端 -> 服务端
{
  "type": "chat_message",
  "msg_id": "uuid",
  "timestamp": 1700000000000,
  "data": {
    "message_type": "text",
    "content": "我想查询订单状态",
    "extra_info": {}
  }
}

// 服务端 -> 客户端
{
  "type": "chat_reply",
  "msg_id": "uuid",
  "timestamp": 1700000000000,
  "data": {
    "message_id": "M123456",
    "sender_type": "bot",
    "message_type": "text",
    "content": "好的，请提供您的订单号",
    "nlu_result": {
      "intent": "query_order",
      "confidence": 0.92
    }
  }
}

// 转人工通知
{
  "type": "session_transferred",
  "msg_id": "uuid",
  "timestamp": 1700000000000,
  "data": {
    "agent_name": "客服小王",
    "queue_position": 3,
    "estimated_wait_time": 60
  }
}
```

## 4. 数据库设计

数据库设计已在需求文档第5章详细定义，包括：
- MySQL核心业务表（20张核心表）
- Redis缓存设计
- Elasticsearch索引设计
- 分库分表策略

详见需求文档第5.2节。

## 5. 部署架构

### 5.1 容器化部署

```yaml
# docker-compose.yml (开发环境)
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: smart_cs
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  redis:
    image: redis:6.2
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

  elasticsearch:
    image: elasticsearch:7.17.0
    environment:
      - discovery.type=single-node
      - ES_JAVA_OPTS=-Xms512m -Xmx512m
    ports:
      - "9200:9200"
    volumes:
      - es_data:/usr/share/elasticsearch/data

  nacos:
    image: nacos/nacos-server:v2.2.0
    environment:
      - MODE=standalone
    ports:
      - "8848:8848"

volumes:
  mysql_data:
  redis_data:
  es_data:
```

### 5.2 Kubernetes部署

```yaml
# k8s/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: dialogue-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: dialogue-service
  template:
    metadata:
      labels:
        app: dialogue-service
    spec:
      containers:
      - name: dialogue-service
        image: smartcs/dialogue-service:latest
        ports:
        - containerPort: 8080
        resources:
          requests:
            cpu: 500m
            memory: 512Mi
          limits:
            cpu: 1000m
            memory: 1Gi
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: NACOS_SERVER_ADDR
          valueFrom:
            configMapKeyRef:
              name: smartcs-config
              key: nacos.server-addr
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5
```

## 6. 监控与运维

### 6.1 监控指标

| 指标类型 | 指标名称 | 告警阈值 |
|---------|---------|---------|
| 服务可用性 | 服务健康状态 | 不可用超过1分钟 |
| 响应时间 | P95响应时间 | >750ms |
| 错误率 | 接口错误率 | >1% |
| 并发数 | 当前并发会话数 | >800 |
| 数据库 | 慢查询比例 | >1% |
| 缓存 | Redis命中率 | <90% |

### 6.2 日志规范

```
日志格式: [时间] [级别] [TraceId] [服务名] [类名] - 日志内容

示例:
2026-04-28 10:30:15.123 INFO [a1b2c3d4] dialogue-service DialogueService - 会话创建成功, sessionId=S123456
```

## 7. 安全设计

安全设计已在需求文档第7章详细定义，包括：
- 传输加密（TLS 1.2+）
- 存储加密（AES-256）
- JWT认证
- 接口限流熔断
- SQL注入/XSS/CSRF防护

详见需求文档第7章。
