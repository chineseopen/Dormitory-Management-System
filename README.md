# 宿舍管理系统 / Dormitory Management System

## 项目介绍

### 中文简介
本项目是一个面向学校宿舍管理的完整系统，包含后台管理端（`dormitory-parent/admin`）和前端展示端（`dormitory-web`）。系统功能覆盖学生入住、房间管理、维修报修、公告发布、费用管理、访客登记等核心宿舍管理业务，同时支持管理员与学生两类角色。

本项目基于 Java 后端和 Vue 前端开发：
- 后端采用 Spring Boot / Spring MVC 等企业级框架，模块化管理，包含 `admin`、`api`、`common`、`framework`、`system` 等子模块。
- 前端采用 Vite + Vue 3，提供响应式页面、权限控制和常见宿舍管理功能入口。

### English Overview
This project is a complete dormitory management system designed for school dormitory operations. It includes a backend management module (`dormitory-parent/admin`) and a frontend user interface (`dormitory-web`). The system covers key dormitory management features such as student check-in, room management, repair requests, announcements, fee management, and visitor registration, supporting both administrator and student roles.

The project is implemented with Java backend and Vue frontend:
- Backend uses Spring Boot/Spring MVC and modular architecture, consisting of `admin`, `api`, `common`, `framework`, and `system` modules.
- Frontend uses Vite + Vue 3, delivering responsive pages, permission control, and dormitory service interfaces.

## 核心功能

### 中文功能点
- 学生信息管理：学生档案维护、房间分配与调整
- 房间与楼栋管理：房间信息维护、空房查询、楼栋管理
- 入住与退宿：学生入住登记、退宿处理、异动记录
- 费用管理：宿舍费用、违章罚款、缴费记录查询
- 报修与维修：学生报修申请、维修工单跟踪、处理记录
- 公告通知：公告发布、查看、推送管理
- 访客管理：访客登记、来访审批、历史记录查询
- 系统权限：管理员登录、用户权限控制、菜单权限管理

### English Features
- Student Management: maintains student records, room assignments, and adjustments
- Room & Building Management: room details, vacancy queries, and building administration
- Check-in & Check-out: dormitory check-in registration, checkout processing, and change records
- Fee Management: dormitory fees, fines, payment history tracking
- Repair & Maintenance: repair requests, work order tracking, and processing records
- Announcements: publish and view notifications, manage announcements
- Visitor Management: visitor registration, visit approval, and history query
- System Permissions: admin login, user permission control, and menu permissions

## 项目结构

### 中文结构说明
- `dormitory-parent/`：后端 Maven 父工程
  - `admin/`：主应用模块，包含后台管理业务代码
  - `api/`：接口定义模块，提供服务接口和 DTO
  - `common/`：公共工具类、通用组件
  - `framework/`：基础框架模块，封装通用基础库
  - `system/`：系统级服务模块，如用户、权限、配置等
  - `sql/`：数据库初始化脚本
- `dormitory-web/`：前端项目，使用 Vite + Vue 3
  - `src/api/`：前端请求接口封装
  - `src/views/`：页面视图模块
  - `src/layout/`：布局组件
  - `src/store/`：状态管理
  - `src/utils/`：工具函数与请求处理

### English Structure
- `dormitory-parent/`: backend Maven parent project
  - `admin/`: main application module containing admin business logic
  - `api/`: API definition module, service interfaces and DTOs
  - `common/`: common utilities and shared components
  - `framework/`: base framework module, common libraries and utilities
  - `system/`: system service module including user, permission, and configuration services
  - `sql/`: database initialization scripts
- `dormitory-web/`: frontend Vite + Vue 3 project
  - `src/api/`: frontend API request wrappers
  - `src/views/`: page views
  - `src/layout/`: layout components
  - `src/store/`: state management
  - `src/utils/`: helper functions and request handling

## 许可证
本项目采用 MIT 开源许可证，详细内容见根目录 `LICENSE` 文件。

## License
This project is licensed under the MIT License. See the root `LICENSE` file for full details.

## 部署教程

### 中文部署指南
1. 环境准备
   - JDK 11 或更高版本
   - Maven 3.6+ 或更高版本
   - Node.js 16+ 和 npm/yarn
   - MySQL 数据库（可根据应用配置调整）

2. 后端部署
   - 进入后端父工程目录：`cd dormitory-parent`
   - 构建项目：`mvn clean package -DskipTests`
   - 运行主应用：
     - 直接执行 JAR：`java -jar admin/target/admin-1.0.0.jar`
     - 或在 IDE 中运行 `admin` 模块的启动类
   - 检查 `application.yml` 或 `application-dev.yml`，确认数据库、端口等配置
   - 如果使用数据库脚本，执行：`dormitory-parent/sql/init.sql` 或 `data.sql` 初始化表结构与测试数据

3. 前端部署
   - 进入前端目录：`cd dormitory-web`
   - 安装依赖：`npm install` 或 `yarn install`
   - 启动开发：`npm run dev` 或 `yarn dev`
   - 打包生产：`npm run build` 或 `yarn build`
   - 生产部署：将 `dist/` 目录内容上传到 Web 服务器或静态托管服务

4. 访问方式
   - 后端默认监听 `http://localhost:8080`（请查看实际配置）
   - 前端开发地址通常为 `http://localhost:5173`
   - 生产环境部署后，通过静态站点地址访问前端页面，前端将调用后端 API

5. 常见问题
   - 如果后端启动失败，先确认数据库地址、用户名密码和端口是否正确
   - 前端请求失败时，检查 `src/utils/request.js` 中的 API 基础路径配置
   - 如果出现跨域问题，请在后端或代理中配置跨域和接口代理

### English Deployment Guide
1. Prerequisites
   - JDK 11 or above
   - Maven 3.6+ or above
   - Node.js 16+ with npm/yarn
   - MySQL database (or adjust according to your application configuration)

2. Backend Deployment
   - Change to backend parent directory: `cd dormitory-parent`
   - Build the project: `mvn clean package -DskipTests`
   - Run the application:
     - Run JAR directly: `java -jar admin/target/admin-1.0.0.jar`
     - Or run the `admin` module startup class in your IDE
   - Verify database, port, and other settings in `application.yml` or `application-dev.yml`
   - If using database scripts, execute `dormitory-parent/sql/init.sql` or `data.sql` to initialize schema and test data

3. Frontend Deployment
   - Change to frontend directory: `cd dormitory-web`
   - Install dependencies: `npm install` or `yarn install`
   - Start development server: `npm run dev` or `yarn dev`
   - Build for production: `npm run build` or `yarn build`
   - Deploy the `dist/` folder to a web server or static hosting service

4. Access URLs
   - Backend default address is usually `http://localhost:8080` (check your actual config)
   - Frontend development address is usually `http://localhost:5173`
   - In production, access the frontend from the static hosting URL, and the frontend will call backend APIs

5. Common Issues
   - If backend startup fails, confirm database credentials, host, and port
   - If frontend requests fail, check the API base URL in `src/utils/request.js`
   - If CORS issues occur, configure CORS in backend or proxy settings