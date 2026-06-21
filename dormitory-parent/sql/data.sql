-- =============================================
-- 宿舍管理系统 - 初始数据
-- =============================================

USE dormitory;

-- ----------------------------
-- 预设角色
-- ----------------------------
INSERT INTO sys_role (id, name, code, description, status) VALUES
(1, '系统管理员', 'admin', '系统最高权限管理员', 1),
(2, '宿舍管理员', 'dorm_admin', '负责楼栋和房间管理', 1),
(3, '辅导员', 'counselor', '负责学生管理和审批', 1),
(4, '学生', 'student', '普通学生用户', 1),
(5, '财务人员', 'finance', '负责水电费管理', 1);

-- ----------------------------
-- 预设管理员账号（密码：admin123，BCrypt加密）
-- ----------------------------
INSERT INTO sys_user (id, username, password, nickname, phone, email, gender, status) VALUES
(1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '超级管理员', '13800000000', 'admin@dorm.com', 1, 1);

-- ----------------------------
-- 管理员角色关联
-- ----------------------------
INSERT INTO sys_user_role (id, user_id, role_id) VALUES
(1, 1, 1);

-- ----------------------------
-- 预设权限数据
-- ----------------------------
-- 一级菜单
INSERT INTO sys_permission (id, parent_id, name, code, type, sort, status) VALUES
(1,  0, '系统管理', 'system',         1, 1, 1),
(2,  0, '楼栋管理', 'building',       1, 2, 1),
(3,  0, '房间管理', 'room',           1, 3, 1),
(4,  0, '住宿管理', 'student',        1, 4, 1),
(5,  0, '报修管理', 'repair',         1, 5, 1),
(6,  0, '水电费管理', 'fee',          1, 6, 1),
(7,  0, '来访管理', 'visitor',        1, 7, 1),
(8,  0, '公告管理', 'notice',         1, 8, 1);

-- 二级菜单
INSERT INTO sys_permission (id, parent_id, name, code, type, sort, status) VALUES
(100, 1, '用户管理', 'system:user',       1, 1, 1),
(101, 1, '角色管理', 'system:role',       1, 2, 1),
(102, 1, '权限管理', 'system:permission', 1, 3, 1),
(200, 2, '楼栋列表', 'building:list',     1, 1, 1),
(300, 3, '房间列表', 'room:list',         1, 1, 1),
(400, 4, '学生列表', 'student:list',      1, 1, 1),
(401, 4, '入住管理', 'student:checkin',   1, 2, 1),
(402, 4, '换房管理', 'student:change',    1, 3, 1),
(500, 5, '报修列表', 'repair:list',       1, 1, 1),
(600, 6, '水电费列表', 'fee:list',        1, 1, 1),
(700, 7, '来访记录', 'visitor:list',      1, 1, 1),
(800, 8, '公告列表', 'notice:list',       1, 1, 1);

-- 按钮权限
INSERT INTO sys_permission (id, parent_id, name, code, type, sort, status) VALUES
(1001, 100, '用户新增', 'system:user:add',       2, 1, 1),
(1002, 100, '用户编辑', 'system:user:edit',      2, 2, 1),
(1003, 100, '用户删除', 'system:user:delete',    2, 3, 1),
(1004, 100, '用户查询', 'system:user:query',     2, 4, 1),
(1101, 101, '角色新增', 'system:role:add',       2, 1, 1),
(1102, 101, '角色编辑', 'system:role:edit',      2, 2, 1),
(1103, 101, '角色删除', 'system:role:delete',    2, 3, 1),
(1201, 102, '权限新增', 'system:permission:add', 2, 1, 1),
(1202, 102, '权限编辑', 'system:permission:edit',2, 2, 1),
(1203, 102, '权限删除', 'system:permission:delete',2,3,1),
(2001, 200, '楼栋新增', 'building:add',          2, 1, 1),
(2002, 200, '楼栋编辑', 'building:edit',         2, 2, 1),
(2003, 200, '楼栋删除', 'building:delete',       2, 3, 1),
(3001, 300, '房间新增', 'room:add',              2, 1, 1),
(3002, 300, '房间编辑', 'room:edit',             2, 2, 1),
(3003, 300, '房间删除', 'room:delete',           2, 3, 1),
(4001, 400, '学生新增', 'student:add',           2, 1, 1),
(4002, 400, '学生编辑', 'student:edit',          2, 2, 1),
(4003, 400, '学生删除', 'student:delete',        2, 3, 1),
(4101, 401, '办理入住', 'student:checkin:add',   2, 1, 1),
(4102, 401, '办理退宿', 'student:checkin:out',   2, 2, 1),
(4201, 402, '换房审核', 'student:change:audit',  2, 1, 1),
(5001, 500, '报修处理', 'repair:handle',         2, 1, 1),
(5002, 500, '报修关闭', 'repair:close',          2, 2, 1),
(6001, 600, '费用录入', 'fee:add',               2, 1, 1),
(6002, 600, '费用缴费', 'fee:pay',                2, 2, 1),
(6003, 600, '费用删除', 'fee:delete',             2, 3, 1),
(7001, 700, '来访登记', 'visitor:add',           2, 1, 1),
(7002, 700, '离开登记', 'visitor:leave',         2, 2, 1),
(7003, 700, '来访删除', 'visitor:delete',         2, 3, 1),
(8001, 800, '公告新增', 'notice:add',            2, 1, 1),
(8002, 800, '公告编辑', 'notice:edit',           2, 2, 1),
(8003, 800, '公告删除', 'notice:delete',         2, 3, 1),
(8004, 800, '公告发布', 'notice:publish',        2, 4, 1);

-- ----------------------------
-- 系统管理员角色 - 全部权限
-- ----------------------------
INSERT INTO sys_role_permission (id, role_id, permission_id)
SELECT id + 10000, 1, id FROM sys_permission;

-- ----------------------------
-- 宿舍管理员角色 - 楼栋/房间/住宿/报修/来访/公告权限
-- ----------------------------
INSERT INTO sys_role_permission (id, role_id, permission_id) VALUES
(20001, 2, 2),
(20002, 2, 200),
(20003, 2, 2001),
(20004, 2, 2002),
(20005, 2, 3),
(20006, 2, 300),
(20007, 2, 3001),
(20008, 2, 3002),
(20009, 2, 4),
(20010, 2, 400),
(20011, 2, 4001),
(20012, 2, 4002),
(20013, 2, 401),
(20014, 2, 4101),
(20015, 2, 4102),
(20016, 2, 402),
(20017, 2, 4201),
(20018, 2, 5),
(20019, 2, 500),
(20020, 2, 5001),
(20021, 2, 5002),
(20022, 2, 7),
(20023, 2, 700),
(20024, 2, 7001),
(20025, 2, 7002),
(20026, 2, 8),
(20027, 2, 800),
(20028, 2, 8001),
(20029, 2, 8002),
(20030, 2, 8003),
(20031, 2, 8004),
(20032, 2, 7003);

-- ----------------------------
-- 辅导员角色 - 住宿/报修查看权限
-- ----------------------------
INSERT INTO sys_role_permission (id, role_id, permission_id) VALUES
(30001, 3, 4),
(30002, 3, 400),
(30003, 3, 4001),
(30004, 3, 4002),
(30005, 3, 401),
(30006, 3, 4101),
(30007, 3, 402),
(30008, 3, 4201),
(30009, 3, 5),
(30010, 3, 500),
(30011, 3, 8),
(30012, 3, 800);

-- ----------------------------
-- 财务人员角色 - 水电费权限
-- ----------------------------
INSERT INTO sys_role_permission (id, role_id, permission_id) VALUES
(40001, 5, 6),
(40002, 5, 600),
(40003, 5, 6001),
(40004, 5, 6002),
(40005, 5, 6003);

-- =============================
-- 测试数据
-- =============================

-- 测试用户
INSERT INTO sys_user (id, username, password, nickname, phone, email, gender, status) VALUES
(2, 'dormadmin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '楼管员', '13800000001', 'dormadmin@dorm.com', 1, 1),
(3, 'counselor1', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '辅导员', '13800000002', 'counselor@dorm.com', 1, 1),
(4, 'finance1', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '财务员', '13800000003', 'finance@dorm.com', 2, 1),
(5, 'student1', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '张三', '13800000004', 'student1@dorm.com', 1, 1),
(6, 'student2', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '李四', '13800000005', 'student2@dorm.com', 1, 1),
(7, 'student3', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '王五', '13800000006', 'student3@dorm.com', 2, 1),
(8, 'student4', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '赵六', '13800000007', 'student4@dorm.com', 2, 1);

-- 系统角色关联
INSERT INTO sys_user_role (id, user_id, role_id) VALUES
(2, 2, 2),
(3, 3, 3),
(4, 4, 5),
(5, 5, 4),
(6, 6, 4),
(7, 7, 4),
(8, 8, 4);

-- 楼栋与房间数据
INSERT INTO dorm_building (id, name, type, floors, rooms_per_floor, manager_id, description) VALUES
(1, 'A栋', 1, 5, 10, 2, '男生宿舍楼'),
(2, 'B栋', 2, 5, 10, 3, '女生宿舍楼'),
(3, 'C栋', 1, 3, 8, 2, '临时住宿楼');

INSERT INTO dorm_floor (id, building_id, floor_number) VALUES
(1, 1, 1),
(2, 1, 2),
(3, 1, 3),
(4, 1, 4),
(5, 1, 5),
(6, 2, 1),
(7, 2, 2),
(8, 2, 3),
(9, 2, 4),
(10, 2, 5),
(11, 3, 1),
(12, 3, 2),
(13, 3, 3);

INSERT INTO dorm_room (id, building_id, floor_id, room_number, capacity, current_count, status) VALUES
(1, 1, 1, '101', 4, 2, 1),
(2, 1, 1, '102', 4, 1, 1),
(3, 2, 6, '201', 4, 1, 1),
(4, 2, 6, '202', 4, 0, 1),
(5, 3, 11, '101', 4, 0, 1);

INSERT INTO dorm_bed (id, room_id, bed_number, status, student_id) VALUES
(1, 1, 1, 2, 5),
(2, 1, 2, 2, 6),
(3, 1, 3, 1, NULL),
(4, 1, 4, 1, NULL),
(5, 2, 1, 2, 7),
(6, 2, 2, 1, NULL),
(7, 2, 3, 1, NULL),
(8, 2, 4, 1, NULL),
(9, 3, 1, 2, 8),
(10, 3, 2, 1, NULL),
(11, 3, 3, 1, NULL),
(12, 3, 4, 1, NULL);

INSERT INTO dorm_student (id, user_id, student_no, name, gender, college, major, class_name, phone, email, building_id, room_id, bed_id, check_in_date, status) VALUES
(5, 5, '20230001', '张三', 1, '计算机学院', '软件工程', '软件1班', '15800000001', 'zhangsan@dorm.com', 1, 1, 1, '2024-09-01', 1),
(6, 6, '20230002', '李四', 1, '信息学院', '信息管理', '信管1班', '15800000002', 'lisi@dorm.com', 1, 1, 2, '2024-09-01', 1),
(7, 7, '20230003', '王五', 2, '经济学院', '财务管理', '财管1班', '15800000003', 'wangwu@dorm.com', 1, 2, 1, '2024-09-10', 1),
(8, 8, '20230004', '赵六', 2, '外语学院', '英语', '英1班', '15800000004', 'zhaoliu@dorm.com', 2, 3, 1, '2024-09-15', 1);

INSERT INTO dorm_check_in (id, student_id, building_id, room_id, bed_id, check_in_date, check_out_date, status, remark) VALUES
(1, 5, 1, 1, 1, '2024-09-01', NULL, 1, '新生入住'),
(2, 6, 1, 1, 2, '2024-09-01', NULL, 1, '新生入住'),
(3, 7, 1, 2, 1, '2024-09-10', NULL, 1, '调剂入住'),
(4, 8, 2, 3, 1, '2024-09-15', NULL, 1, '中转入住');

INSERT INTO dorm_repair (id, student_id, building_id, room_id, title, content, images, status, repair_user_id, repair_time, result) VALUES
(1, 5, 1, 1, '台灯损坏', '宿舍台灯无法开启', '', 2, NULL, NULL, NULL),
(2, 7, 1, 2, '空调漏水', '房间空调滴水，需维修', '', 1, NULL, NULL, NULL),
(3, 8, 2, 3, '门锁松动', '房门锁芯松动，开关不顺畅', '', 1, NULL, NULL, NULL);

INSERT INTO dorm_visitor (id, building_id, visitor_name, visitor_phone, visitor_id_card, target_student_id, reason, visit_time, leave_time, status) VALUES
(1, 1, '张老师', '13900000001', '430********', 5, '家长来访', '2024-10-05 14:30:00', NULL, 1),
(2, 2, '李阿姨', '13900000002', '430********', 6, '送生活用品', '2024-10-06 16:00:00', '2024-10-06 17:00:00', 2);

INSERT INTO dorm_fee (id, building_id, room_id, fee_type, amount, month, previous_reading, current_reading, status, pay_time) VALUES
(1, 1, 1, 1, 120.50, '2024-09', 15.00, 18.00, 2, '2024-10-05 12:00:00'),
(2, 1, 1, 2, 85.75, '2024-09', 12.00, 15.25, 1, NULL),
(3, 2, 3, 1, 98.00, '2024-09', 10.50, 14.00, 1, NULL),
(4, 2, 3, 2, 230.80, '2024-09', 78.00, 95.00, 2, '2024-10-08 09:30:00');

INSERT INTO dorm_notice (id, title, content, type, publisher_id, status) VALUES
(1, '开学报到通知', '请全体新生于9月1日前完成报到。', 1, 1, 1),
(2, '节能提醒', '请同学们注意关闭电灯和空调，节约用电。', 2, 1, 1);