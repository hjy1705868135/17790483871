-- =============================================================================
-- 无人机信息管理系统 - 数据库初始化脚本
-- 支持：SQLite / MySQL
-- =============================================================================

-- ------------------------------ 创建无人机表 ------------------------------
-- 表名：drone
-- 说明：存储无人机的基本信息
CREATE TABLE IF NOT EXISTS drone (
    id INTEGER PRIMARY KEY AUTOINCREMENT,  -- 主键ID，自增
    name VARCHAR(100) NOT NULL,            -- 无人机名称（必填）
    model VARCHAR(100) NOT NULL,           -- 无人机型号（必填）
    manufacturer VARCHAR(100),             -- 制造商名称
    weight DECIMAL(10,2) DEFAULT 0,        -- 重量（单位：kg），默认值0
    max_altitude INTEGER DEFAULT 0,        -- 最大飞行高度（单位：米），默认值0
    max_flight_time INTEGER DEFAULT 0,     -- 续航时间（单位：分钟），默认值0
    max_speed DECIMAL(10,1) DEFAULT 0,     -- 最大速度（单位：km/h），默认值0
    status VARCHAR(20) DEFAULT 'ACTIVE',   -- 状态：ACTIVE（正常）/ INACTIVE（停用）
    description TEXT,                      -- 描述信息
    create_time VARCHAR(50),               -- 创建时间
    update_time VARCHAR(50)                -- 更新时间
);

-- ------------------------------ 创建索引 ------------------------------
-- 为常用查询字段创建索引，提升查询性能
CREATE INDEX IF NOT EXISTS idx_drone_name ON drone(name);      -- 名称索引
CREATE INDEX IF NOT EXISTS idx_drone_model ON drone(model);    -- 型号索引
CREATE INDEX IF NOT EXISTS idx_drone_status ON drone(status);  -- 状态索引

-- ------------------------------ 插入示例数据 ------------------------------
-- 仅在表为空时插入示例数据
INSERT INTO drone (name, model, manufacturer, weight, max_altitude, max_flight_time, max_speed, status, description, create_time, update_time)
SELECT '幻影-2000', 'Phantom X 1', '大疆创新', 2.5, 5000, 35, 95.5, 'ACTIVE', '高性能消费级无人机，支持4K高清拍摄', '2024-01-15 10:30:00', '2024-01-15 10:30:00'
WHERE NOT EXISTS (SELECT * FROM drone);

INSERT INTO drone (name, model, manufacturer, weight, max_altitude, max_flight_time, max_speed, status, description, create_time, update_time)
SELECT '猎鹰-3000', 'Mavic Cyber 2', '大疆创新', 1.2, 6000, 40, 120.0, 'ACTIVE', '专业级航拍无人机，超长续航', '2024-01-16 14:20:00', '2024-01-16 14:20:00'
WHERE NOT EXISTS (SELECT * FROM drone WHERE name = '猎鹰-3000');

INSERT INTO drone (name, model, manufacturer, weight, max_altitude, max_flight_time, max_speed, status, description, create_time, update_time)
SELECT '幽灵-1000', 'Inspire Neo 5', '极飞科技', 4.8, 8000, 25, 110.5, 'INACTIVE', '工业级测绘无人机，高精度定位', '2024-01-17 09:15:00', '2024-01-18 16:45:00'
WHERE NOT EXISTS (SELECT * FROM drone WHERE name = '幽灵-1000');