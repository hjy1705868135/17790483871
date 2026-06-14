package com.md.basePlatform.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Mapper 扫描配置。
 */
@Configuration
@MapperScan("com.md.basePlatform.repository")
public class MyBatisConfig {
}
