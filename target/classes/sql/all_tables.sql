-- 为项目中所有实体类生成对应的表结构SQL语句

-- 如果不存在则创建eblog数据库
CREATE DATABASE IF NOT EXISTS eblog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 选择使用eblog数据库
USE eblog;

-- 创建MCategory表
CREATE TABLE m_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    created DATETIME COMMENT '创建时间',
    modified DATETIME COMMENT '修改时间',
    name VARCHAR(255) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容描述',
    summary TEXT COMMENT '摘要信息',
    icon VARCHAR(255) COMMENT '图标',
    post_count INT DEFAULT 0 COMMENT '该分类的内容数量',
    order_num INT DEFAULT 0 COMMENT '排序编码',
    parent_id BIGINT COMMENT '父级分类的ID',
    meta_keywords VARCHAR(500) COMMENT 'SEO关键字',
    meta_description TEXT COMMENT 'SEO描述内容',
    INDEX idx_parent_id (parent_id),
    INDEX idx_order_num (order_num),
    INDEX idx_post_count (post_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- 创建MComment表
CREATE TABLE m_comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    created DATETIME COMMENT '创建时间',
    modified DATETIME COMMENT '修改时间',
    content TEXT COMMENT '评论的内容',
    parent_id BIGINT COMMENT '回复的评论ID',
    post_id BIGINT COMMENT '评论的内容ID',
    user_id BIGINT COMMENT '评论的用户ID',
    vote_up INT DEFAULT 0 COMMENT '顶的数量',
    vote_down INT DEFAULT 0 COMMENT '踩的数量',
    level INT DEFAULT 0 COMMENT '置顶等级',
    INDEX idx_parent_id (parent_id),
    INDEX idx_post_id (post_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 创建MPost表
CREATE TABLE m_post (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    created DATETIME COMMENT '创建时间',
    modified DATETIME COMMENT '修改时间',
    title VARCHAR(500) NOT NULL COMMENT '标题',
    content LONGTEXT COMMENT '内容',
    edit_mode VARCHAR(50) COMMENT '编辑模式：html可视化，markdown等',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    user_id BIGINT COMMENT '用户ID',
    vote_up INT DEFAULT 0 COMMENT '支持人数',
    vote_down INT DEFAULT 0 COMMENT '反对人数',
    view_count INT DEFAULT 0 COMMENT '访问量',
    comment_count INT DEFAULT 0 COMMENT '评论数量',
    recommend TINYINT(1) DEFAULT 0 COMMENT '是否为精华',
    level INT DEFAULT 0 COMMENT '置顶等级',
    INDEX idx_category_id (category_id),
    INDEX idx_user_id (user_id),
    INDEX idx_view_count (view_count),
    INDEX idx_recommend (recommend)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- 创建MUser表
CREATE TABLE m_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    created DATETIME COMMENT '创建时间',
    modified DATETIME COMMENT '修改时间',
    username VARCHAR(100) NOT NULL COMMENT '昵称',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    email VARCHAR(255) COMMENT '邮件',
    mobile VARCHAR(20) COMMENT '手机电话',
    point INT DEFAULT 0 COMMENT '积分',
    sign VARCHAR(500) COMMENT '个性签名',
    gender VARCHAR(10) COMMENT '性别',
    wechat VARCHAR(100) COMMENT '微信号',
    vip_level INT DEFAULT 0 COMMENT 'vip等级',
    birthday DATE COMMENT '生日',
    avatar VARCHAR(500) COMMENT '头像',
    post_count INT DEFAULT 0 COMMENT '内容数量',
    comment_count INT DEFAULT 0 COMMENT '评论数量',
    lasted DATETIME COMMENT '最后的登录时间',
    role VARCHAR(50) COMMENT '用户权限',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_point (point)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 创建MUserAction表
CREATE TABLE m_user_action (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    created DATETIME COMMENT '创建时间',
    modified DATETIME COMMENT '修改时间',
    user_id VARCHAR(50) COMMENT '用户ID',
    action VARCHAR(100) COMMENT '动作类型',
    point INT DEFAULT 0 COMMENT '得分',
    post_id VARCHAR(50) COMMENT '关联的帖子ID',
    comment_id VARCHAR(50) COMMENT '关联的评论ID',
    INDEX idx_user_id (user_id),
    INDEX idx_action (action)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户行为表';

-- 创建MUserCollection表
CREATE TABLE m_user_collection (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    created DATETIME COMMENT '创建时间',
    modified DATETIME COMMENT '修改时间',
    user_id BIGINT COMMENT '收藏的用户ID',
    post_id BIGINT COMMENT '被收藏的文章ID',
    post_user_id BIGINT COMMENT '文章作者ID',
    INDEX idx_user_id (user_id),
    INDEX idx_post_id (post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏表';

-- 创建MUserMessage表
CREATE TABLE m_user_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    created DATETIME COMMENT '创建时间',
    modified DATETIME COMMENT '修改时间',
    from_user_id BIGINT COMMENT '发送消息的用户ID',
    to_user_id BIGINT COMMENT '接收消息的用户ID',
    post_id BIGINT COMMENT '消息可能关联的帖子',
    comment_id BIGINT COMMENT '消息可能关联的评论',
    content TEXT COMMENT '消息内容',
    type INT DEFAULT 0 COMMENT '消息类型: 0系统消息 1评论文章 2评论评论',
    status INT DEFAULT 0 COMMENT '状态',
    INDEX idx_from_user_id (from_user_id),
    INDEX idx_to_user_id (to_user_id),
    INDEX idx_post_id (post_id),
    INDEX idx_comment_id (comment_id),
    INDEX idx_type (type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户消息表';