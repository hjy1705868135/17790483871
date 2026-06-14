/**
 * MyBatis配置类
 * 配置MyBatis的Mapper扫描和相关设置
 * MyBatis是一个优秀的持久层框架，支持定制化SQL、存储过程以及高级映射
 */
package com.md.basePlatform.config;

// 导入MyBatis的Mapper扫描注解
import org.mybatis.spring.annotation.MapperScan;
// 导入Spring框架配置注解
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置类
 * 配置Mapper接口的扫描路径
 * 使用@Configuration注解标记，Spring会自动扫描并加载配置
 * 
 * 注意：SqlSessionFactory由Spring Boot自动配置处理，无需手动创建
 */
@Configuration
// @MapperScan注解指定要扫描的Mapper接口所在的包
// Spring会自动为包下的所有接口创建实现类
@MapperScan("com.md.basePlatform.mapper")
public class MyBatisConfig {
    // SqlSessionFactory由Spring Boot自动配置，无需手动创建
    // 配置信息从application.properties中读取
}