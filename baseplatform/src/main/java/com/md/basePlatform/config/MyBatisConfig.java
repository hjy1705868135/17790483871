/**
 * MyBatis配置类
 * 配置MyBatis的Mapper扫描和相关设置
 * MyBatis是一个优秀的持久层框架，支持定制化SQL、存储过程以及高级映射
 */
package com.md.basePlatform.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置类
 * 配置Mapper接口的扫描路径（统一使用 repository 包）
 * 注意：SqlSessionFactory由Spring Boot自动配置处理，无需手动创建
 */
@Configuration
@MapperScan("com.md.basePlatform.repository")
public class MyBatisConfig {
    // SqlSessionFactory由Spring Boot自动配置，无需手动创建
    // 配置信息从application.properties中读取
}
