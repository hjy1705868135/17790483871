/**
 * 数据源配置类（空实现）
 * 使用Spring Boot默认数据源配置，移除Druid以避免兼容性问题
 */
package com.md.basePlatform.config;

import org.springframework.context.annotation.Configuration;

/**
 * 数据源配置类
 * Spring Boot会自动配置数据源，无需手动配置
 */
@Configuration
public class DataSourceConfig {
    // Spring Boot自动配置数据源，无需额外配置
}