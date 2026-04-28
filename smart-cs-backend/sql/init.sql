-- 智能客服系统数据库初始化脚本
-- 版本: V1.0
-- 日期: 2026-04-28

-- 创建数据库
CREATE DATABASE IF NOT EXISTS smart_cs DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE smart_cs;

-- =====================================================
-- 用户表
-- =====================================================
CREATE TABLE IF NOT EXISTS cs_user (
    id BIGINT PRIMARY KEY COMMENT '用户ID',
    user_no VARCHAR(32) NOT NULL COMMENT '用户编号',
    user_type TINYINT NOT NULL DEFAULT 1 COMMENT '用户类型: 1-终端用户 2-客服 3-管理员',
    nick_name VARCHAR(64) COMMENT '昵称',
    real_name VARCHAR(64) COMMENT '真实姓名',
    phone VARCHAR(64) COMMENT '手机号(AES加密)',
    phone_hash VARCHAR(64) COMMENT '手机号哈希',
    email VARCHAR(128) COMMENT '邮箱',
    avatar_url VARCHAR(512) COMMENT '头像URL',
    password VARCHAR(128) COMMENT '密码(bcrypt加密)',
    channel VARCHAR(32) NOT NULL COMMENT '注册渠道: web/wechat/app/phone/sms',
    channel_user_id VARCHAR(128) COMMENT '渠道用户标识',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用 1-正常 2-注销',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(45) COMMENT '最后登录IP',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at DATETIME COMMENT '软删除时间',
    UNIQUE KEY uk_user_no (user_no),
    KEY idx_phone_hash (phone_hash),
    KEY idx_channel_user (channel, channel_user_id),
    KEY idx_status_created (status, created_at),
    KEY idx_user_type (user_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =====================================================
-- 会话表
-- =====================================================
CREATE TABLE IF NOT EXISTS cs_session (
    id BIGINT PRIMARY KEY COMMENT '会话ID',
    session_no VARCHAR(64) NOT NULL COMMENT '会话编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    channel VARCHAR(32) NOT NULL COMMENT '接入渠道',
    session_type TINYINT NOT NULL COMMENT '会话类型: 1-机器人 2-人工 3-机器人转人工',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-排队中 1-进行中 2-已结束 3-已超时',
    bot_id BIGINT COMMENT '关联机器人ID',
    agent_id BIGINT COMMENT '关联客服ID',
    queue_id BIGINT COMMENT '排队ID',
    first_message_time DATETIME COMMENT '首条消息时间',
    last_message_time DATETIME COMMENT '最后消息时间',
    transfer_reason VARCHAR(256) COMMENT '转人工原因',
    satisfaction_score TINYINT COMMENT '满意度评分: 1-5分',
    satisfaction_feedback TEXT COMMENT '满意度反馈',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    ended_at DATETIME COMMENT '结束时间',
    UNIQUE KEY uk_session_no (session_no),
    KEY idx_user_id (user_id),
    KEY idx_agent_id (agent_id),
    KEY idx_status_time (status, created_at),
    KEY idx_channel_status (channel, status),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会话表';

-- =====================================================
-- 消息表
-- =====================================================
CREATE TABLE IF NOT EXISTS cs_message (
    id BIGINT PRIMARY KEY COMMENT '消息ID',
    message_no VARCHAR(64) NOT NULL COMMENT '消息编号',
    session_id BIGINT NOT NULL COMMENT '会话ID',
    sender_type TINYINT NOT NULL COMMENT '发送者类型: 1-用户 2-机器人 3-客服 4-系统',
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    msg_type VARCHAR(32) NOT NULL COMMENT '消息类型: text/image/file/audio/video/rich_text/card',
    content TEXT COMMENT '消息内容',
    content_summary VARCHAR(512) COMMENT '内容摘要',
    nlu_result JSON COMMENT 'NLU解析结果',
    knowledge_id BIGINT COMMENT '关联知识库条目ID',
    reply_template_id BIGINT COMMENT '关联话术模板ID',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-发送中 1-已送达 2-已读 3-发送失败',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_message_no (message_no),
    KEY idx_session_id (session_id),
    KEY idx_session_created (session_id, created_at),
    KEY idx_sender (sender_type, sender_id),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- =====================================================
-- 工单表
-- =====================================================
CREATE TABLE IF NOT EXISTS cs_ticket (
    id BIGINT PRIMARY KEY COMMENT '工单ID',
    ticket_no VARCHAR(64) NOT NULL COMMENT '工单编号',
    session_id BIGINT COMMENT '关联会话ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(256) NOT NULL COMMENT '工单标题',
    description TEXT COMMENT '工单描述',
    category_id BIGINT COMMENT '工单分类ID',
    priority TINYINT NOT NULL DEFAULT 2 COMMENT '优先级: 1-低 2-中 3-高 4-紧急',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-待处理 1-处理中 2-待确认 3-已解决 4-已关闭 5-已撤销',
    source VARCHAR(32) NOT NULL COMMENT '来源: chat/phone/email/system',
    handler_id BIGINT COMMENT '处理人ID',
    handler_group_id BIGINT COMMENT '处理组ID',
    sla_deadline DATETIME COMMENT 'SLA截止时间',
    resolved_at DATETIME COMMENT '解决时间',
    closed_at DATETIME COMMENT '关闭时间',
    satisfaction_score TINYINT COMMENT '满意度评分',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_ticket_no (ticket_no),
    KEY idx_user_id (user_id),
    KEY idx_session_id (session_id),
    KEY idx_handler (handler_id, status),
    KEY idx_status_priority (status, priority),
    KEY idx_category (category_id),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单表';

-- =====================================================
-- 工单处理记录表
-- =====================================================
CREATE TABLE IF NOT EXISTS cs_ticket_log (
    id BIGINT PRIMARY KEY COMMENT '记录ID',
    ticket_id BIGINT NOT NULL COMMENT '工单ID',
    operator_id BIGINT NOT NULL COMMENT '操作人ID',
    operator_type TINYINT NOT NULL COMMENT '操作人类型: 1-客服 2-系统 3-用户',
    action VARCHAR(32) NOT NULL COMMENT '操作类型: create/assign/transfer/reply/resolve/close/reopen',
    content TEXT COMMENT '操作内容',
    attachment_ids VARCHAR(512) COMMENT '附件ID列表',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    KEY idx_ticket_id (ticket_id),
    KEY idx_ticket_created (ticket_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单处理记录表';

-- =====================================================
-- 知识库分类表
-- =====================================================
CREATE TABLE IF NOT EXISTS cs_kb_category (
    id BIGINT PRIMARY KEY COMMENT '分类ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    category_name VARCHAR(128) NOT NULL COMMENT '分类名称',
    category_path VARCHAR(512) NOT NULL COMMENT '分类路径',
    level TINYINT NOT NULL COMMENT '层级深度',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_parent_id (parent_id),
    KEY idx_category_path (category_path),
    KEY idx_status_sort (status, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库分类表';

-- =====================================================
-- 知识库条目表
-- =====================================================
CREATE TABLE IF NOT EXISTS cs_kb_entry (
    id BIGINT PRIMARY KEY COMMENT '条目ID',
    entry_no VARCHAR(64) NOT NULL COMMENT '条目编号',
    category_id BIGINT NOT NULL COMMENT '所属分类ID',
    title VARCHAR(256) NOT NULL COMMENT '标题/问题',
    content TEXT NOT NULL COMMENT '内容/标准答案',
    content_type VARCHAR(16) DEFAULT 'text' COMMENT '内容类型: text/markdown/rich_text',
    keywords VARCHAR(512) COMMENT '关键词',
    similar_questions JSON COMMENT '相似问题列表',
    intent_id VARCHAR(64) COMMENT '关联NLU意图ID',
    vector_id VARCHAR(128) COMMENT '向量库中的ID',
    effective_count INT DEFAULT 0 COMMENT '有效命中次数',
    ineffective_count INT DEFAULT 0 COMMENT '无效反馈次数',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-草稿 1-已发布 2-已下架',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    publisher_id BIGINT COMMENT '发布人ID',
    published_at DATETIME COMMENT '发布时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_entry_no (entry_no),
    KEY idx_category_id (category_id),
    KEY idx_intent_id (intent_id),
    KEY idx_status (status),
    FULLTEXT KEY idx_keywords (keywords),
    KEY idx_effectiveness (effective_count, ineffective_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库条目表';

-- =====================================================
-- 知识库版本表
-- =====================================================
CREATE TABLE IF NOT EXISTS cs_kb_entry_version (
    id BIGINT PRIMARY KEY COMMENT '版本记录ID',
    entry_id BIGINT NOT NULL COMMENT '条目ID',
    version INT NOT NULL COMMENT '版本号',
    title VARCHAR(256) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容',
    change_desc VARCHAR(512) COMMENT '变更说明',
    operator_id BIGINT NOT NULL COMMENT '操作人ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_entry_version (entry_id, version),
    KEY idx_entry_id (entry_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库版本表';

-- =====================================================
-- 话术模板表
-- =====================================================
CREATE TABLE IF NOT EXISTS cs_script_template (
    id BIGINT PRIMARY KEY COMMENT '模板ID',
    template_name VARCHAR(128) NOT NULL COMMENT '模板名称',
    category_id BIGINT COMMENT '所属分类ID',
    content TEXT NOT NULL COMMENT '模板内容',
    content_type VARCHAR(16) DEFAULT 'text' COMMENT '内容类型: text/rich_text/card',
    trigger_type TINYINT NOT NULL COMMENT '触发类型: 1-手动 2-自动 3-流程节点',
    trigger_condition JSON COMMENT '触发条件',
    variables JSON COMMENT '变量定义列表',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    created_by BIGINT NOT NULL COMMENT '创建人ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_category (category_id),
    KEY idx_trigger_type (trigger_type),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='话术模板表';

-- =====================================================
-- 角色表
-- =====================================================
CREATE TABLE IF NOT EXISTS cs_role (
    id BIGINT PRIMARY KEY COMMENT '角色ID',
    role_name VARCHAR(64) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(64) NOT NULL COMMENT '角色编码',
    description VARCHAR(256) COMMENT '角色描述',
    data_scope TINYINT NOT NULL DEFAULT 3 COMMENT '数据范围: 1-全部 2-本部门 3-本人',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- =====================================================
-- 权限表
-- =====================================================
CREATE TABLE IF NOT EXISTS cs_permission (
    id BIGINT PRIMARY KEY COMMENT '权限ID',
    perm_name VARCHAR(64) NOT NULL COMMENT '权限名称',
    perm_code VARCHAR(128) NOT NULL COMMENT '权限编码',
    resource_type VARCHAR(32) NOT NULL COMMENT '资源类型: menu/button/api/data',
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID',
    perm_path VARCHAR(512) COMMENT '权限路径',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_perm_code (perm_code),
    KEY idx_parent_id (parent_id),
    KEY idx_resource_type (resource_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- =====================================================
-- 角色权限关联表
-- =====================================================
CREATE TABLE IF NOT EXISTS cs_role_permission (
    id BIGINT PRIMARY KEY COMMENT '主键',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    perm_id BIGINT NOT NULL COMMENT '权限ID',
    UNIQUE KEY uk_role_perm (role_id, perm_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- =====================================================
-- 用户角色关联表
-- =====================================================
CREATE TABLE IF NOT EXISTS cs_user_role (
    id BIGINT PRIMARY KEY COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- =====================================================
-- 系统配置表
-- =====================================================
CREATE TABLE IF NOT EXISTS cs_system_config (
    id BIGINT PRIMARY KEY COMMENT '配置ID',
    config_group VARCHAR(64) NOT NULL COMMENT '配置分组',
    config_key VARCHAR(128) NOT NULL COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    value_type VARCHAR(16) DEFAULT 'string' COMMENT '值类型: string/number/boolean/json',
    description VARCHAR(256) COMMENT '配置说明',
    is_editable TINYINT DEFAULT 0 COMMENT '是否可在线编辑: 0-否 1-是',
    updated_by BIGINT COMMENT '最后修改人',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_group_key (config_group, config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- =====================================================
-- 机器人配置表
-- =====================================================
CREATE TABLE IF NOT EXISTS cs_bot_config (
    id BIGINT PRIMARY KEY COMMENT '机器人ID',
    bot_name VARCHAR(64) NOT NULL COMMENT '机器人名称',
    bot_code VARCHAR(64) NOT NULL COMMENT '机器人编码',
    welcome_msg TEXT COMMENT '欢迎语',
    unknown_msg TEXT COMMENT '未知回复语',
    transfer_msg TEXT COMMENT '转人工提示语',
    nlu_model_id VARCHAR(64) COMMENT '关联NLU模型ID',
    knowledge_base_ids VARCHAR(512) COMMENT '关联知识库ID列表',
    max_round INT DEFAULT 20 COMMENT '最大对话轮次',
    confidence_threshold DECIMAL(3,2) DEFAULT 0.75 COMMENT '置信度阈值',
    transfer_threshold DECIMAL(3,2) DEFAULT 0.40 COMMENT '转人工置信度阈值',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-停用 1-启用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_bot_code (bot_code),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='机器人配置表';

-- =====================================================
-- NLU意图表
-- =====================================================
CREATE TABLE IF NOT EXISTS cs_nlu_intent (
    id BIGINT PRIMARY KEY COMMENT '意图ID',
    intent_name VARCHAR(128) NOT NULL COMMENT '意图名称',
    intent_code VARCHAR(64) NOT NULL COMMENT '意图编码',
    description VARCHAR(256) COMMENT '意图描述',
    bot_id BIGINT NOT NULL COMMENT '所属机器人ID',
    training_samples JSON COMMENT '训练语料样本',
    response_type VARCHAR(32) NOT NULL COMMENT '响应类型: knowledge/script/flow/api',
    response_config JSON COMMENT '响应配置',
    is_fallback TINYINT DEFAULT 0 COMMENT '是否兜底意图: 0-否 1-是',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_intent_code (intent_code),
    KEY idx_bot_id (bot_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='NLU意图表';

-- =====================================================
-- 排队记录表
-- =====================================================
CREATE TABLE IF NOT EXISTS cs_queue_record (
    id BIGINT PRIMARY KEY COMMENT '记录ID',
    session_id BIGINT NOT NULL COMMENT '会话ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    channel VARCHAR(32) NOT NULL COMMENT '渠道',
    queue_no INT NOT NULL COMMENT '排队序号',
    skill_group_id BIGINT NOT NULL COMMENT '技能组ID',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-排队中 1-已接入 2-已取消 3-已超时',
    agent_id BIGINT COMMENT '接入客服ID',
    wait_start_time DATETIME NOT NULL COMMENT '开始排队时间',
    wait_end_time DATETIME COMMENT '结束排队时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_session_id (session_id),
    KEY idx_skill_group (skill_group_id, status),
    KEY idx_status_time (status, wait_start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='排队记录表';

-- =====================================================
-- 技能组表
-- =====================================================
CREATE TABLE IF NOT EXISTS cs_skill_group (
    id BIGINT PRIMARY KEY COMMENT '技能组ID',
    group_name VARCHAR(64) NOT NULL COMMENT '技能组名称',
    group_code VARCHAR(64) NOT NULL COMMENT '技能组编码',
    parent_id BIGINT DEFAULT 0 COMMENT '父技能组ID',
    max_queue_size INT DEFAULT 50 COMMENT '最大排队人数',
    avg_response_time INT DEFAULT 60 COMMENT '平均响应时间目标(秒)',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_group_code (group_code),
    KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='技能组表';

-- =====================================================
-- 客服技能组关联表
-- =====================================================
CREATE TABLE IF NOT EXISTS cs_agent_skill_group (
    id BIGINT PRIMARY KEY COMMENT '主键',
    agent_id BIGINT NOT NULL COMMENT '客服ID',
    skill_group_id BIGINT NOT NULL COMMENT '技能组ID',
    level TINYINT DEFAULT 1 COMMENT '技能等级: 1-初级 2-中级 3-高级',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-离线 1-在线 2-忙碌',
    UNIQUE KEY uk_agent_skill (agent_id, skill_group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客服技能组关联表';

-- =====================================================
-- 操作日志表
-- =====================================================
CREATE TABLE IF NOT EXISTS cs_operation_log (
    id BIGINT PRIMARY KEY COMMENT '日志ID',
    operator_id BIGINT NOT NULL COMMENT '操作人ID',
    operator_name VARCHAR(64) COMMENT '操作人姓名',
    operator_type TINYINT NOT NULL COMMENT '操作人类型: 1-客服 2-管理员 3-系统',
    module VARCHAR(32) NOT NULL COMMENT '操作模块',
    action VARCHAR(64) NOT NULL COMMENT '操作动作',
    target_type VARCHAR(32) COMMENT '操作对象类型',
    target_id VARCHAR(64) COMMENT '操作对象ID',
    before_value JSON COMMENT '操作前值',
    after_value JSON COMMENT '操作后值',
    ip VARCHAR(45) COMMENT '操作IP',
    user_agent VARCHAR(512) COMMENT '用户代理',
    result TINYINT NOT NULL COMMENT '操作结果: 0-失败 1-成功',
    error_msg VARCHAR(512) COMMENT '错误信息',
    duration_ms INT COMMENT '操作耗时(毫秒)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    KEY idx_operator (operator_id, created_at),
    KEY idx_module_action (module, action),
    KEY idx_target (target_type, target_id),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- =====================================================
-- 初始化基础数据
-- =====================================================

-- 初始化角色
INSERT INTO cs_role (id, role_name, role_code, description, data_scope, status) VALUES
(1, '超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限', 1, 1),
(2, '运营管理员', 'OPERATION_ADMIN', '运营管理员，负责知识库和工单管理', 2, 1),
(3, '客服组长', 'AGENT_LEADER', '客服组长，可查看本组数据', 2, 1),
(4, '客服坐席', 'AGENT', '普通客服坐席', 3, 1),
(5, '审核人员', 'AUDITOR', '知识库审核人员', 3, 1);

-- 初始化管理员账号 (密码: Admin@123)
INSERT INTO cs_user (id, user_no, user_type, nick_name, real_name, phone, phone_hash, password, channel, status) VALUES
(1, 'U000001', 3, '系统管理员', '管理员', '', '', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4.VTtYqVqYqVqYq', 'system', 1);

-- 初始化用户角色关联
INSERT INTO cs_user_role (id, user_id, role_id) VALUES
(1, 1, 1);

-- 初始化权限
INSERT INTO cs_permission (id, perm_name, perm_code, resource_type, parent_id, status) VALUES
(1, '系统管理', 'system', 'menu', 0, 1),
(2, '用户管理', 'system:user', 'menu', 1, 1),
(3, '查看用户', 'system:user:view', 'button', 2, 1),
(4, '新增用户', 'system:user:add', 'button', 2, 1),
(5, '编辑用户', 'system:user:edit', 'button', 2, 1),
(6, '删除用户', 'system:user:delete', 'button', 2, 1),
(10, '角色管理', 'system:role', 'menu', 1, 1),
(11, '查看角色', 'system:role:view', 'button', 10, 1),
(12, '新增角色', 'system:role:add', 'button', 10, 1),
(13, '编辑角色', 'system:role:edit', 'button', 10, 1),
(14, '删除角色', 'system:role:delete', 'button', 10, 1),
(20, '知识库管理', 'knowledge', 'menu', 0, 1),
(21, '知识列表', 'knowledge:entry', 'menu', 20, 1),
(22, '查看知识', 'knowledge:entry:view', 'button', 21, 1),
(23, '新增知识', 'knowledge:entry:add', 'button', 21, 1),
(24, '编辑知识', 'knowledge:entry:edit', 'button', 21, 1),
(25, '删除知识', 'knowledge:entry:delete', 'button', 21, 1),
(26, '发布知识', 'knowledge:entry:publish', 'button', 21, 1),
(27, '审核知识', 'knowledge:entry:audit', 'button', 21, 1),
(30, '工单管理', 'ticket', 'menu', 0, 1),
(31, '工单列表', 'ticket:list', 'menu', 30, 1),
(32, '查看工单', 'ticket:view', 'button', 31, 1),
(33, '处理工单', 'ticket:handle', 'button', 31, 1),
(34, '分配工单', 'ticket:assign', 'button', 31, 1),
(40, '统计分析', 'statistics', 'menu', 0, 1),
(41, '对话统计', 'statistics:session', 'menu', 40, 1),
(42, '工单统计', 'statistics:ticket', 'menu', 40, 1),
(43, '客服绩效', 'statistics:agent', 'menu', 40, 1);

-- 初始化角色权限关联 (超级管理员拥有所有权限)
INSERT INTO cs_role_permission (id, role_id, perm_id)
SELECT ROW_NUMBER() OVER () + 100, 1, id FROM cs_permission;

-- 初始化机器人配置
INSERT INTO cs_bot_config (id, bot_name, bot_code, welcome_msg, unknown_msg, transfer_msg, max_round, confidence_threshold, transfer_threshold, status) VALUES
(1, '智能客服小助手', 'default_bot', '您好！我是智能客服小助手，请问有什么可以帮您？', '抱歉，我没有理解您的问题，您可以换个方式描述，或者选择转人工客服。', '正在为您转接人工客服，请稍候...', 20, 0.75, 0.40, 1);

-- 初始化技能组
INSERT INTO cs_skill_group (id, group_name, group_code, max_queue_size, avg_response_time, status) VALUES
(1, '综合服务组', 'general', 50, 60, 1),
(2, '技术支持组', 'technical', 30, 120, 1),
(3, '投诉处理组', 'complaint', 20, 180, 1);

-- 初始化系统配置
INSERT INTO cs_system_config (id, config_group, config_key, config_value, value_type, description, is_editable) VALUES
(1, 'system', 'site_name', '智能客服系统', 'string', '系统名称', 1),
(2, 'system', 'session_timeout', '30', 'number', '会话超时时间(分钟)', 1),
(3, 'system', 'max_concurrent_session', '5', 'number', '客服最大并发会话数', 1),
(4, 'chat', 'enable_transfer', 'true', 'boolean', '是否启用转人工', 1),
(5, 'chat', 'enable_satisfaction', 'true', 'boolean', '是否启用满意度评价', 1),
(6, 'security', 'login_fail_limit', '5', 'number', '登录失败锁定阈值', 1),
(7, 'security', 'password_expire_days', '90', 'number', '密码过期天数', 1);

-- 初始化知识库分类
INSERT INTO cs_kb_category (id, parent_id, category_name, category_path, level, sort_order, status) VALUES
(1, 0, '常见问题', '/1/', 1, 1, 1),
(2, 0, '产品咨询', '/2/', 1, 2, 1),
(3, 0, '订单相关', '/3/', 1, 3, 1),
(4, 0, '售后服务', '/4/', 1, 4, 1),
(5, 0, '账户管理', '/5/', 1, 5, 1);

-- 初始化NLU意图
INSERT INTO cs_nlu_intent (id, intent_name, intent_code, description, bot_id, response_type, is_fallback, status) VALUES
(1, '问候', 'greeting', '用户问候意图', 1, 'script', 0, 1),
(2, '查询订单', 'query_order', '用户查询订单状态', 1, 'knowledge', 0, 1),
(3, '退款申请', 'refund_request', '用户申请退款', 1, 'flow', 0, 1),
(4, '产品咨询', 'product_inquiry', '用户咨询产品信息', 1, 'knowledge', 0, 1),
(5, '投诉建议', 'complaint', '用户投诉或建议', 1, 'flow', 0, 1),
(6, '转人工', 'transfer_human', '用户请求转人工', 1, 'script', 0, 1),
(7, '未知意图', 'unknown', '兜底意图', 1, 'script', 1, 1);

-- 初始化话术模板
INSERT INTO cs_script_template (id, template_name, category_id, content, content_type, trigger_type, status, created_by) VALUES
(1, '欢迎语', NULL, '您好！我是智能客服小助手，请问有什么可以帮您？', 'text', 1, 1, 1),
(2, '问候回复', NULL, '您好！很高兴为您服务，请问有什么可以帮您的吗？', 'text', 1, 1, 1),
(3, '转人工提示', NULL, '正在为您转接人工客服，请稍候...', 'text', 1, 1, 1),
(4, '等待提示', NULL, '请稍候，正在为您查询...', 'text', 1, 1, 1),
(5, '感谢语', NULL, '感谢您的咨询，祝您生活愉快！', 'text', 1, 1, 1),
(6, '安抚话术', NULL, '非常抱歉给您带来不便，我们会尽快为您处理。', 'text', 1, 1, 1);
