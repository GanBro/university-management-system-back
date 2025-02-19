# 高校管理系统后端

## 项目介绍
本项目是高校管理系统的后端部分，基于 Spring Boot 开发，提供了完整的 RESTful API 接口和数据处理服务。

## 技术栈
- Spring Boot 3.x
- MyBatis-Plus
- MySQL 8.x
- Redis
- Spring Security
- Swagger/OpenAPI
- Maven

## 系统要求
- JDK 17 或以上
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

## 安装说明

1. 克隆项目
```bash
git clone [项目地址]
cd university-management-system-back
```

2. 配置数据库
- 创建 MySQL 数据库
```sql
CREATE DATABASE university_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
- 导入数据库脚本
```bash
mysql -u your_username -p university_management < university_management.sql
```

3. 配置应用
修改 `src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/university_management?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
  redis:
    host: localhost
    port: 6379
    password: your_redis_password
```

4. 编译运行
```bash
mvn clean package
java -jar target/university-management-system-back.jar
```

## 项目结构
```
src/main/java/com/wubo/api/
├── config/           # 配置类
├── controller/       # 控制器
├── service/         # 服务层
│   └── impl/        # 服务实现
├── mapper/          # MyBatis 接口
├── entity/          # 实体类
├── dto/             # 数据传输对象
├── vo/              # 视图对象
├── utils/           # 工具类
└── common/          # 公共组件
    ├── exception/   # 异常处理
    ├── response/    # 响应封装
    └── config/      # 通用配置
```

## API 文档
启动应用后访问：http://localhost:8080/swagger-ui.html

主要接口模块：
1. 用户认证
   - 登录
   - 注册
   - 密码重置

2. 高校管理
   - 高校信息 CRUD
   - 批量导入导出
   - 数据统计

3. 用户管理
   - 用户 CRUD
   - 角色权限
   - 用户统计

4. 互动管理
   - 咨询
   - 回复
   - 关注

## 开发指南

### 新增功能
1. 创建实体类
2. 创建 Mapper 接口
3. 创建 Service 接口和实现
4. 创建 Controller
5. 编写单元测试

### 开发规范
1. 遵循 RESTful API 设计规范
2. 使用统一的响应格式
3. 规范的异常处理
4. 添加详细的接口文档
5. 编写单元测试

### 部署说明
1. 测试环境部署
```bash
mvn clean package -P test
```

2. 生产环境部署
```bash
mvn clean package -P prod
```

## 性能优化
1. 数据库优化
   - 合理使用索引
   - 优化 SQL 语句
   - 使用分页查询

2. 缓存优化
   - Redis 缓存配置
   - 合理的缓存策略
   - 缓存预热

3. JVM 优化
   - 内存配置
   - GC 调优
   - 线程池配置

## 常见问题

1. 启动失败
   - 检查数据库连接
   - 检查 Redis 连接
   - 检查端口占用

2. 接口报错
   - 检查请求参数
   - 查看错误日志
   - 检查数据库连接

3. 性能问题
   - 检查慢查询日志
   - 检查缓存使用
   - 检查 JVM 状态

## 更新日志
- v1.0.0
  - 初始版本发布
  - 完成基础功能模块
  - 实现数据统计功能

## 维护者
[你的名字]

## 许可证
MIT 