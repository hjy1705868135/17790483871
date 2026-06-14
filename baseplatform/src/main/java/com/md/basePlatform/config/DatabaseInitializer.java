package com.md.basePlatform.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * 启动时执行 {@code classpath:db/schema-{dbType}.sql}，创建基础表结构。
 */
@Component
public class DatabaseInitializer implements ApplicationRunner {

    private final DataSource dataSource;
    private final String dbType;

    /**
     * @param dataSource 数据源
     * @param dbType 数据库类型（sqlite/mysql）
     */
    public DatabaseInitializer(DataSource dataSource, @Value("${app.db.type:sqlite}") String dbType) {
        this.dataSource = dataSource;
        this.dbType = dbType;
    }

    /**
     * 执行建表脚本。
     *
     * @param args 启动参数
     * @throws Exception SQL 或 IO 异常
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        String schemaPath = "db/schema-" + dbType + ".sql";
        ClassPathResource resource = new ClassPathResource(schemaPath);
        if (!resource.exists()) {
            return;
        }
        StringBuilder sql = new StringBuilder();
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sql.append(line).append('\n');
            }
        }

        String[] statements = sql.toString().split(";");
        try (Connection conn = dataSource.getConnection()) {
            for (String stmt : statements) {
                String trimmed = stmt.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                try (Statement s = conn.createStatement()) {
                    s.execute(trimmed);
                }
            }
        }
    }
}
