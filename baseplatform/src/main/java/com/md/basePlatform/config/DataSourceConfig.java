package com.md.basePlatform.config;

import com.alibaba.druid.pool.DruidDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 多数据源条件装配：按 {@code app.db.type} 选择 SQLite 或 MySQL。
 */
@Configuration
public class DataSourceConfig {

    /**
     * 当 {@code app.db.type=sqlite}（默认）时创建 SQLite 数据源。
     *
     * @param driverClassName JDBC 驱动类名
     * @param url JDBC URL
     * @param username 用户名（可为空）
     * @param password 密码（可为空）
     * @return 数据源
     */
    @Bean
    @ConditionalOnProperty(name = "app.db.type", havingValue = "sqlite", matchIfMissing = true)
    public DataSource sqliteDataSource(
            @Value("${app.db.sqlite.driverClassName}") String driverClassName,
            @Value("${app.db.sqlite.url}") String url,
            @Value("${app.db.sqlite.username:}") String username,
            @Value("${app.db.sqlite.password:}") String password) {
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName(driverClassName);
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        return ds;
    }

    /**
     * 当 {@code app.db.type=mysql} 时创建 MySQL 数据源。
     *
     * @param driverClassName JDBC 驱动类名
     * @param url JDBC URL
     * @param username 用户名
     * @param password 密码
     * @return 数据源
     */
    @Bean
    @ConditionalOnProperty(name = "app.db.type", havingValue = "mysql")
    public DataSource mysqlDataSource(
            @Value("${app.db.mysql.driverClassName}") String driverClassName,
            @Value("${app.db.mysql.url}") String url,
            @Value("${app.db.mysql.username}") String username,
            @Value("${app.db.mysql.password}") String password) {
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName(driverClassName);
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        return ds;
    }
}
