/**
 * Spring Boot应用启动类
 * 无人机信息管理系统的入口类
 * 使用@SpringBootApplication注解标记，自动配置Spring Boot应用
 */
package com.md.basePlatform;

// 导入Spring Boot启动类
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 无人机信息管理系统启动类
 * 
 * 系统技术栈：
 * - Java EE 8 / Servlet 3.0
 * - Spring Boot 2.2.x / Spring Framework 5.2.x
 * - Apache Shiro 1.7（安全框架）
 * - Apache MyBatis 3.5.x（持久层框架）
 * - Hibernate Validation 6.0.x（参数验证）
 * - Alibaba Druid 1.2.x（数据库连接池）
 * - Vue3 + Bootstrap4（前端）
 * 
 * 核心功能：
 * 1. 无人机信息的CRUD操作（增删改查）
 * 2. 支持SQLite和MySQL数据库无缝切换
 * 3. 请求日志拦截器，记录所有HTTP请求
 * 4. Shiro安全框架集成
 * 5. 赛博朋克风格的Vue3前端页面
 */
@SpringBootApplication
public class BasePlatformApplication {

    /**
     * 应用主入口方法
     * Java程序的入口点，main方法
     * 
     * @param args 命令行参数（可选）
     */
    public static void main(String[] args) {
        // 启动Spring Boot应用
        // SpringApplication.run()方法会：
        // 1. 创建Spring应用上下文
        // 2. 自动配置所有Bean
        // 3. 启动嵌入式Web服务器（如Tomcat）
        SpringApplication.run(BasePlatformApplication.class, args);
    }
}