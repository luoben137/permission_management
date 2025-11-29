# 权限管理系统

基于 Vue3 + Vite 前端和 Spring Boot + Spring Security 后端的权限管理系统。

## 功能特性

- 用户登录（Session认证）
- 角色管理
- 用户管理
- 权限点管理及鉴权
- 审计日志

## 技术栈

### 后端
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway（数据库迁移）
- Maven

### 前端
- Vue 3
- Vite
- Vue Router
- Pinia
- Axios
- Element Plus

## 数据库配置

数据库使用 PostgreSQL，配置信息：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://127.0.0.1:5432/roben
    username: postgres
    password: mysecretpassword
```

## 默认账户

- 用户名：`admin`
- 密码：`1234`

## 快速开始

### 1. 数据库准备

确保 PostgreSQL 已安装并运行，创建数据库：

```sql
CREATE DATABASE roben;
```

### 2. 启动后端

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动。

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端服务将在 `http://localhost:5173` 启动。

### 4. 访问系统

打开浏览器访问 `http://localhost:5173`，使用默认账户登录。

## 项目结构

```
permission_management/
├── backend/                 # Spring Boot 后端
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/permission/
│   │   │   │       ├── aspect/      # AOP切面
│   │   │   │       ├── config/      # 配置类
│   │   │   │       ├── controller/  # 控制器
│   │   │   │       ├── dto/         # 数据传输对象
│   │   │   │       ├── entity/      # 实体类
│   │   │   │       ├── repository/  # 数据访问层
│   │   │   │       ├── security/    # 安全相关
│   │   │   │       └── service/    # 服务层
│   │   │   └── resources/
│   │   │       ├── db/migration/    # 数据库迁移脚本
│   │   │       └── application.yml # 应用配置
│   │   └── pom.xml
└── frontend/                # Vue3 前端
    ├── src/
    │   ├── api/            # API接口
    │   ├── components/     # 组件
    │   ├── layouts/        # 布局
    │   ├── router/         # 路由
    │   ├── stores/         # 状态管理
    │   ├── utils/          # 工具函数
    │   └── views/          # 页面
    ├── package.json
    └── vite.config.js
```

## API 接口

### 认证接口
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出
- `GET /api/auth/check` - 检查登录状态

### 用户管理
- `GET /api/users` - 获取用户列表
- `GET /api/users/{id}` - 获取用户详情
- `POST /api/users` - 创建用户
- `PUT /api/users/{id}` - 更新用户
- `DELETE /api/users/{id}` - 删除用户

### 角色管理
- `GET /api/roles` - 获取角色列表
- `GET /api/roles/{id}` - 获取角色详情
- `POST /api/roles` - 创建角色
- `PUT /api/roles/{id}` - 更新角色
- `DELETE /api/roles/{id}` - 删除角色

### 权限点管理
- `GET /api/permissions` - 获取权限点列表
- `GET /api/permissions/{id}` - 获取权限点详情
- `POST /api/permissions` - 创建权限点
- `PUT /api/permissions/{id}` - 更新权限点
- `DELETE /api/permissions/{id}` - 删除权限点

### 审计日志
- `GET /api/audit-logs` - 获取审计日志列表

## 权限说明

系统使用URL级别的权限控制，主要权限点：

- `USER_MANAGE` - 用户管理权限
- `ROLE_MANAGE` - 角色管理权限
- `PERMISSION_MANAGE` - 权限点管理权限
- `AUDIT_VIEW` - 审计日志查看权限

## 开发说明

### 后端开发

1. 数据库迁移：使用 Flyway 管理数据库版本，SQL 脚本位于 `src/main/resources/db/migration/`
2. 权限控制：在 `SecurityConfig` 中配置 URL 权限映射
3. 审计日志：通过 AOP 切面自动记录 CRUD 操作

### 前端开发

1. 路由守卫：在 `router/index.js` 中配置路由权限
2. 权限检查：使用 Pinia store 中的 `hasPermission` 方法检查权限
3. API 调用：所有 API 调用通过 `utils/request.js` 统一处理

## 注意事项

1. 确保 PostgreSQL 数据库已创建并运行
2. 首次启动会自动执行数据库迁移脚本
3. admin 用户不能删除
4. 所有操作都会记录到审计日志中

