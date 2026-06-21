-- =============================================
-- 宿舍管理系统 - 数据库初始化脚本
-- =============================================

CREATE DATABASE IF NOT EXISTS dormitory DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE dormitory;

-- ----------------------------
-- 1. 楼栋表
-- ----------------------------
DROP TABLE IF EXISTS dorm_building;
CREATE TABLE dorm_building (
    id              BIGINT          NOT NULL COMMENT '主键ID',
    name            VARCHAR(50)     NOT NULL COMMENT '楼栋名称',
    type            TINYINT         NOT NULL DEFAULT 1 COMMENT '楼栋类型：1-男生 2-女生',
    floors          INT             NOT NULL DEFAULT 0 COMMENT '楼层数',
    rooms_per_floor INT             NOT NULL DEFAULT 0 COMMENT '每层房间数',
    manager_id      BIGINT          DEFAULT NULL COMMENT '楼管员ID',
    description     VARCHAR(255)    DEFAULT NULL COMMENT '描述',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='楼栋表';

-- ----------------------------
-- 2. 楼层表
-- ----------------------------
DROP TABLE IF EXISTS dorm_floor;
CREATE TABLE dorm_floor (
    id              BIGINT          NOT NULL COMMENT '主键ID',
    building_id     BIGINT          NOT NULL COMMENT '楼栋ID',
    floor_number    INT             NOT NULL COMMENT '楼层号',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (id),
    KEY idx_building_id (building_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='楼层表';

-- ----------------------------
-- 3. 房间表
-- ----------------------------
DROP TABLE IF EXISTS dorm_room;
CREATE TABLE dorm_room (
    id              BIGINT          NOT NULL COMMENT '主键ID',
    building_id     BIGINT          NOT NULL COMMENT '楼栋ID',
    floor_id        BIGINT          NOT NULL COMMENT '楼层ID',
    room_number     VARCHAR(20)     NOT NULL COMMENT '房间号',
    capacity        INT             NOT NULL DEFAULT 4 COMMENT '容纳人数',
    current_count   INT             NOT NULL DEFAULT 0 COMMENT '当前入住人数',
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '房间状态：1-空闲 2-已满 3-维修中 4-停用',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (id),
    KEY idx_building_id (building_id),
    KEY idx_floor_id (floor_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='房间表';

-- ----------------------------
-- 4. 床位表
-- ----------------------------
DROP TABLE IF EXISTS dorm_bed;
CREATE TABLE dorm_bed (
    id              BIGINT          NOT NULL COMMENT '主键ID',
    room_id         BIGINT          NOT NULL COMMENT '房间ID',
    bed_number      INT             NOT NULL COMMENT '床位号',
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '床位状态：1-空闲 2-已占用 3-维修中',
    student_id      BIGINT          DEFAULT NULL COMMENT '入住学生ID',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (id),
    KEY idx_room_id (room_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='床位表';

-- ----------------------------
-- 5. 学生表
-- ----------------------------
DROP TABLE IF EXISTS dorm_student;
CREATE TABLE dorm_student (
    id              BIGINT          NOT NULL COMMENT '主键ID',
    user_id         BIGINT          DEFAULT NULL COMMENT '关联系统用户ID',
    student_no      VARCHAR(30)     NOT NULL COMMENT '学号',
    name            VARCHAR(50)     NOT NULL COMMENT '姓名',
    gender          TINYINT         NOT NULL DEFAULT 1 COMMENT '性别：1-男 2-女',
    college         VARCHAR(100)    DEFAULT NULL COMMENT '学院',
    major           VARCHAR(100)    DEFAULT NULL COMMENT '专业',
    class_name      VARCHAR(100)    DEFAULT NULL COMMENT '班级',
    phone           VARCHAR(20)     DEFAULT NULL COMMENT '手机号',
    email           VARCHAR(100)    DEFAULT NULL COMMENT '邮箱',
    building_id     BIGINT          DEFAULT NULL COMMENT '所在楼栋ID',
    room_id         BIGINT          DEFAULT NULL COMMENT '所在房间ID',
    bed_id          BIGINT          DEFAULT NULL COMMENT '所在床位ID',
    check_in_date   DATE            DEFAULT NULL COMMENT '入住日期',
    status          TINYINT         NOT NULL DEFAULT 0 COMMENT '状态：0-未入住 1-在住 2-退宿 3-休学',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_student_no (student_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学生表';

-- ----------------------------
-- 6. 入住记录表
-- ----------------------------
DROP TABLE IF EXISTS dorm_check_in;
CREATE TABLE dorm_check_in (
    id              BIGINT          NOT NULL COMMENT '主键ID',
    student_id      BIGINT          NOT NULL COMMENT '学生ID',
    building_id     BIGINT          NOT NULL COMMENT '楼栋ID',
    room_id         BIGINT          NOT NULL COMMENT '房间ID',
    bed_id          BIGINT          NOT NULL COMMENT '床位ID',
    check_in_date   DATE            NOT NULL COMMENT '入住日期',
    check_out_date  DATE            DEFAULT NULL COMMENT '退宿日期',
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1-在住 2-已退宿',
    remark          VARCHAR(255)    DEFAULT NULL COMMENT '备注',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (id),
    KEY idx_student_id (student_id),
    KEY idx_room_id (room_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='入住记录表';

-- ----------------------------
-- 7. 换房记录表
-- ----------------------------
DROP TABLE IF EXISTS dorm_room_change;
CREATE TABLE dorm_room_change (
    id                  BIGINT      NOT NULL COMMENT '主键ID',
    student_id          BIGINT      NOT NULL COMMENT '学生ID',
    old_building_id     BIGINT      NOT NULL COMMENT '原楼栋ID',
    old_room_id         BIGINT      NOT NULL COMMENT '原房间ID',
    old_bed_id          BIGINT      NOT NULL COMMENT '原床位ID',
    new_building_id     BIGINT      NOT NULL COMMENT '新楼栋ID',
    new_room_id         BIGINT      NOT NULL COMMENT '新房间ID',
    new_bed_id          BIGINT      NOT NULL COMMENT '新床位ID',
    reason              VARCHAR(255) DEFAULT NULL COMMENT '换房原因',
    status              TINYINT     NOT NULL DEFAULT 1 COMMENT '状态：1-待审核 2-已通过 3-已拒绝',
    audit_user_id       BIGINT      DEFAULT NULL COMMENT '审核人ID',
    audit_time          DATETIME    DEFAULT NULL COMMENT '审核时间',
    audit_remark        VARCHAR(255) DEFAULT NULL COMMENT '审核备注',
    create_time         DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time         DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (id),
    KEY idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='换房记录表';

-- ----------------------------
-- 8. 报修表
-- ----------------------------
DROP TABLE IF EXISTS dorm_repair;
CREATE TABLE dorm_repair (
    id              BIGINT          NOT NULL COMMENT '主键ID',
    student_id      BIGINT          NOT NULL COMMENT '报修学生ID',
    building_id     BIGINT          NOT NULL COMMENT '楼栋ID',
    room_id         BIGINT          NOT NULL COMMENT '房间ID',
    title           VARCHAR(100)    NOT NULL COMMENT '报修标题',
    content         TEXT            DEFAULT NULL COMMENT '报修内容',
    images          VARCHAR(1000)   DEFAULT NULL COMMENT '图片地址，多个用逗号分隔',
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1-待处理 2-处理中 3-已完成 4-已关闭',
    repair_user_id  BIGINT          DEFAULT NULL COMMENT '维修人员ID',
    repair_time     DATETIME        DEFAULT NULL COMMENT '维修时间',
    result          VARCHAR(255)    DEFAULT NULL COMMENT '维修结果',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (id),
    KEY idx_student_id (student_id),
    KEY idx_building_id (building_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='报修表';

-- ----------------------------
-- 9. 来访登记表
-- ----------------------------
DROP TABLE IF EXISTS dorm_visitor;
CREATE TABLE dorm_visitor (
    id              BIGINT          NOT NULL COMMENT '主键ID',
    building_id     BIGINT          NOT NULL COMMENT '楼栋ID',
    visitor_name    VARCHAR(50)     NOT NULL COMMENT '来访人姓名',
    visitor_phone   VARCHAR(20)     DEFAULT NULL COMMENT '来访人手机号',
    visitor_id_card VARCHAR(20)     DEFAULT NULL COMMENT '来访人身份证号',
    target_student_id BIGINT        DEFAULT NULL COMMENT '被访学生ID',
    reason          VARCHAR(255)    DEFAULT NULL COMMENT '来访事由',
    visit_time      DATETIME        NOT NULL COMMENT '来访时间',
    leave_time      DATETIME        DEFAULT NULL COMMENT '离开时间',
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1-在访 2-已离开',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (id),
    KEY idx_building_id (building_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='来访登记表';

-- ----------------------------
-- 10. 水电费表
-- ----------------------------
DROP TABLE IF EXISTS dorm_fee;
CREATE TABLE dorm_fee (
    id              BIGINT          NOT NULL COMMENT '主键ID',
    building_id     BIGINT          NOT NULL COMMENT '楼栋ID',
    room_id         BIGINT          NOT NULL COMMENT '房间ID',
    fee_type        TINYINT         NOT NULL COMMENT '费用类型：1-水费 2-电费',
    amount          DECIMAL(10,2)   NOT NULL COMMENT '金额',
    month           VARCHAR(7)      NOT NULL COMMENT '账期月份，格式yyyy-MM',
    previous_reading DECIMAL(10,2)  DEFAULT NULL COMMENT '上期读数',
    current_reading  DECIMAL(10,2)  DEFAULT NULL COMMENT '本期读数',
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1-未缴费 2-已缴费',
    pay_time        DATETIME        DEFAULT NULL COMMENT '缴费时间',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (id),
    KEY idx_room_id (room_id),
    KEY idx_month (month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='水电费表';

-- ----------------------------
-- 11. 系统用户表
-- ----------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id              BIGINT          NOT NULL COMMENT '主键ID',
    username        VARCHAR(50)     NOT NULL COMMENT '用户名（登录账号）',
    password        VARCHAR(100)    NOT NULL COMMENT '密码（BCrypt加密）',
    nickname        VARCHAR(50)     DEFAULT NULL COMMENT '昵称',
    avatar          VARCHAR(255)    DEFAULT NULL COMMENT '头像地址',
    phone           VARCHAR(20)     DEFAULT NULL COMMENT '手机号',
    email           VARCHAR(100)    DEFAULT NULL COMMENT '邮箱',
    gender          TINYINT         DEFAULT 1 COMMENT '性别：1-男 2-女',
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统用户表';

-- ----------------------------
-- 12. 角色表
-- ----------------------------
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id              BIGINT          NOT NULL COMMENT '主键ID',
    name            VARCHAR(50)     NOT NULL COMMENT '角色名称',
    code            VARCHAR(50)     NOT NULL COMMENT '角色编码',
    description     VARCHAR(255)    DEFAULT NULL COMMENT '描述',
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色表';

-- ----------------------------
-- 13. 权限表
-- ----------------------------
DROP TABLE IF EXISTS sys_permission;
CREATE TABLE sys_permission (
    id              BIGINT          NOT NULL COMMENT '主键ID',
    parent_id       BIGINT          DEFAULT 0 COMMENT '父权限ID',
    name            VARCHAR(50)     NOT NULL COMMENT '权限名称',
    code            VARCHAR(100)    NOT NULL COMMENT '权限编码',
    type            TINYINT         NOT NULL COMMENT '类型：1-菜单 2-按钮',
    sort            INT             NOT NULL DEFAULT 0 COMMENT '排序',
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='权限表';

-- ----------------------------
-- 14. 用户角色关联表
-- ----------------------------
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id              BIGINT          NOT NULL COMMENT '主键ID',
    user_id         BIGINT          NOT NULL COMMENT '用户ID',
    role_id         BIGINT          NOT NULL COMMENT '角色ID',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户角色关联表';

-- ----------------------------
-- 15. 角色权限关联表
-- ----------------------------
DROP TABLE IF EXISTS sys_role_permission;
CREATE TABLE sys_role_permission (
    id              BIGINT          NOT NULL COMMENT '主键ID',
    role_id         BIGINT          NOT NULL COMMENT '角色ID',
    permission_id   BIGINT          NOT NULL COMMENT '权限ID',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (id),
    KEY idx_role_id (role_id),
    KEY idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色权限关联表';

-- ----------------------------
-- 16. 公告表
-- ----------------------------
DROP TABLE IF EXISTS dorm_notice;
CREATE TABLE dorm_notice (
    id              BIGINT          NOT NULL COMMENT '主键ID',
    title           VARCHAR(100)    NOT NULL COMMENT '公告标题',
    content         TEXT            DEFAULT NULL COMMENT '公告内容',
    type            TINYINT         NOT NULL DEFAULT 1 COMMENT '类型：1-通知 2-公告 3-紧急',
    publisher_id    BIGINT          NOT NULL COMMENT '发布人ID',
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：0-草稿 1-已发布 2-已下架',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='公告表';

-- ----------------------------
-- 17. 公告已读记录表
-- ----------------------------
DROP TABLE IF EXISTS dorm_notice_read;
CREATE TABLE dorm_notice_read (
    id              BIGINT          NOT NULL COMMENT '主键ID',
    notice_id       BIGINT          NOT NULL COMMENT '公告ID',
    user_id         BIGINT          NOT NULL COMMENT '用户ID',
    read_time       DATETIME        NOT NULL COMMENT '已读时间',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (id),
    KEY idx_notice_id (notice_id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='公告已读记录表';
