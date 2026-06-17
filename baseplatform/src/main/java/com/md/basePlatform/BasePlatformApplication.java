/**
 * Spring Boot应用启动类
 * 无人机信息管理系统的入口类
 *
 * 系统技术栈：
 * - Java EE 8 / Servlet 3.0
 * - Spring Boot 2.2.x / Spring Framework 5.2.x
 * - Apache Shiro 1.7（安全框架）
 * - Apache MyBatis 3.5.x（持久层框架）
 * - Hibernate Validation 6.0.x（参数验证）
 * - Alibaba Druid 1.2.x（数据库连接池）
 * - Thymeleaf 3.0.x + Bootstrap 3.3.7（视图层）
 *
 * 核心功能：
 * 1. 无人机信息的CRUD操作（增删改查）
 * 2. 支持SQLite和MySQL数据库无缝切换
 * 3. 请求日志拦截器（interceptor包），记录所有HTTP请求
 * 4. Shiro安全框架集成
 * 5. AI属性自动生成（服务层）
 *
 * 分层架构：
 * - controller/：控制器层，仅依赖Service接口
 * - service/：服务层接口
 * - service/impl/：服务层实现
 * - repository/：数据操作层（MyBatis Mapper接口）
 * - domain/：领域模型层（实体、表单、DTO）
 * - config/：配置层（数据源、Shiro、WebMVC等）
 * - interceptor/：拦截器层（独立包，请求日志）
 * - exception/：异常处理层（全局异常处理）
 */
package com.md.basePlatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BasePlatformApplication {

    /**
     * 应用主入口方法
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(BasePlatformApplication.class, args);
    }
}
