-- ================================================
-- 电商平台订单模块数据库表结构设计
-- 版本: 1.0.0
-- 创建时间: 2024-01-10
-- 描述: 订单模块核心表结构，包含订单主表、订单详情表
-- ================================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS ecommerce
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE ecommerce;

-- ================================================
-- 1. 订单主表 (orders)
-- ================================================
DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
    -- 主键
    id BIGINT NOT NULL COMMENT '订单ID',

    -- 订单基本信息
    order_no VARCHAR(32) NOT NULL COMMENT '订单编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_name VARCHAR(100) NOT NULL COMMENT '用户名',

    -- 订单金额信息
    total_amount DECIMAL(12,2) NOT NULL COMMENT '订单总金额',
    discount_amount DECIMAL(12,2) DEFAULT 0.00 COMMENT '优惠金额',
    freight_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '运费金额',
    pay_amount DECIMAL(12,2) NOT NULL COMMENT '实际支付金额',

    -- 订单状态
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '订单状态: PENDING-待支付,PAID-已支付,SHIPPED-已发货,DELIVERED-已送达,CANCELLED-已取消,REFUNDED-已退款',

    -- 收货信息
    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收货人电话',
    receiver_address VARCHAR(255) NOT NULL COMMENT '收货地址',
    receiver_postal_code VARCHAR(10) DEFAULT '' COMMENT '邮政编码',

    -- 支付信息
    payment_method VARCHAR(20) DEFAULT NULL COMMENT '支付方式: WECHAT-微信,ALIPAY-支付宝,CARD-银行卡',
    payment_time DATETIME DEFAULT NULL COMMENT '支付时间',
    payment_no VARCHAR(64) DEFAULT NULL COMMENT '支付流水号',

    -- 物流信息
    express_company VARCHAR(50) DEFAULT NULL COMMENT '快递公司',
    express_no VARCHAR(50) DEFAULT NULL COMMENT '快递单号',
    ship_time DATETIME DEFAULT NULL COMMENT '发货时间',
    delivery_time DATETIME DEFAULT NULL COMMENT '收货时间',

    -- 订单备注
    remark VARCHAR(500) DEFAULT NULL COMMENT '订单备注',

    -- 时间戳
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    -- 逻辑删除
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记: 0-未删除, 1-已删除',

    -- 主键约束
    PRIMARY KEY (id),

    -- 唯一约束
    UNIQUE KEY uk_order_no (order_no),

    -- 索引设计
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_payment_time (payment_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单主表';

-- ================================================
-- 2. 订单详情表 (order_items)
-- ================================================
DROP TABLE IF EXISTS order_items;
CREATE TABLE order_items (
    -- 主键
    id BIGINT NOT NULL COMMENT '订单详情ID',

    -- 关联订单
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL COMMENT '订单编号',

    -- 商品信息
    product_id BIGINT NOT NULL COMMENT '商品ID',
    product_name VARCHAR(200) NOT NULL COMMENT '商品名称',
    product_image VARCHAR(500) DEFAULT NULL COMMENT '商品图片',
    sku_id BIGINT DEFAULT NULL COMMENT 'SKU ID',
    sku_code VARCHAR(64) DEFAULT NULL COMMENT 'SKU编码',

    -- 价格信息
    price DECIMAL(12,2) NOT NULL COMMENT '商品单价',
    quantity INT NOT NULL COMMENT '购买数量',
    subtotal DECIMAL(12,2) NOT NULL COMMENT '小计金额',

    -- 时间戳
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    -- 逻辑删除
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记: 0-未删除, 1-已删除',

    -- 主键约束
    PRIMARY KEY (id),

    -- 索引设计
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单详情表';

-- ================================================
-- 3. 订单状态流转表 (可选，用于订单状态日志)
-- ================================================
DROP TABLE IF EXISTS order_status_log;
CREATE TABLE order_status_log (
    -- 主键
    id BIGINT NOT NULL COMMENT '日志ID',

    -- 关联订单
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL COMMENT '订单编号',

    -- 状态流转
    from_status VARCHAR(20) DEFAULT NULL COMMENT '原状态',
    to_status VARCHAR(20) NOT NULL COMMENT '新状态',

    -- 操作信息
    operator_type VARCHAR(20) DEFAULT 'SYSTEM' COMMENT '操作类型: SYSTEM-系统,USER-用户,ADMIN-管理员',
    operator_id BIGINT DEFAULT NULL COMMENT '操作人ID',
    operator_name VARCHAR(100) DEFAULT NULL COMMENT '操作人姓名',

    -- 备注
    remark VARCHAR(500) DEFAULT NULL COMMENT '状态变更备注',

    -- 时间戳
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    -- 主键约束
    PRIMARY KEY (id),

    -- 索引设计
    INDEX idx_order_id (order_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单状态流转日志表';

-- ================================================
-- 4. 初始化测试数据
-- ================================================

-- 插入测试订单数据
INSERT INTO orders (id, order_no, user_id, user_name, total_amount, discount_amount, freight_amount, pay_amount, status, receiver_name, receiver_phone, receiver_address, payment_method, payment_time, payment_no, created_at, updated_at)
VALUES
(1, 'ORD202401100001', 1001, '张三', 299.00, 20.00, 10.00, 289.00, 'PAID', '张三', '13800138001', '北京市朝阳区XX街道XX号', 'ALIPAY', '2024-01-10 10:00:00', 'ZF20240110001', '2024-01-10 09:00:00', '2024-01-10 10:00:00'),
(2, 'ORD202401100002', 1001, '张三', 599.00, 50.00, 0.00, 549.00, 'SHIPPED', '张三', '13800138001', '北京市朝阳区XX街道XX号', 'WECHAT', '2024-01-11 11:00:00', 'WX20240111002', '2024-01-11 10:00:00', '2024-01-12 09:00:00'),
(3, 'ORD202401100003', 1002, '李四', 1299.00, 100.00, 15.00, 1214.00, 'DELIVERED', '李四', '13900139002', '上海市浦东新区XX路XX弄', 'CARD', '2024-01-12 14:00:00', 'CARD20240112', '2024-01-12 12:00:00', '2024-01-13 18:00:00'),
(4, 'ORD202401130001', 1003, '王五', 89.00, 0.00, 10.00, 99.00, 'PENDING', '王五', '13700137003', '广州市天河区XX大道XX号', NULL, NULL, NULL, '2024-01-13 15:00:00', '2024-01-13 15:00:00'),
(5, 'ORD202401130002', 1002, '李四', 199.00, 10.00, 0.00, 189.00, 'CANCELLED', '李四', '13900139002', '上海市浦东新区XX路XX弄', NULL, NULL, NULL, '2024-01-13 16:00:00', '2024-01-13 16:30:00');

-- 插入测试订单详情数据
INSERT INTO order_items (id, order_id, order_no, product_id, product_name, product_image, sku_id, sku_code, price, quantity, subtotal, created_at, updated_at)
VALUES
(1, 1, 'ORD202401100001', 10001, 'iPhone 15 Pro', 'https://example.com/iphone15.jpg', 20001, 'SKU-IP15P-256', 7999.00, 1, 7999.00, '2024-01-10 09:00:00', '2024-01-10 09:00:00'),
(2, 2, 'ORD202401100002', 10002, 'MacBook Pro 14寸', 'https://example.com/macbook.jpg', 20002, 'SKU-MBP14-512', 15999.00, 1, 15999.00, '2024-01-11 10:00:00', '2024-01-11 10:00:00'),
(3, 3, 'ORD202401100003', 10003, 'AirPods Pro', 'https://example.com/airpods.jpg', 20003, 'SKU-APP-2', 1899.00, 1, 1899.00, '2024-01-12 12:00:00', '2024-01-12 12:00:00'),
(4, 4, 'ORD202401130001', 10004, 'iPad Mini', 'https://example.com/ipadmini.jpg', 20004, 'SKU-IPM-64', 3999.00, 1, 3999.00, '2024-01-13 15:00:00', '2024-01-13 15:00:00'),
(5, 5, 'ORD202401130002', 10005, 'Apple Watch', 'https://example.com/watch.jpg', 20005, 'SKU-AW-SE', 2199.00, 1, 2199.00, '2024-01-13 16:00:00', '2024-01-13 16:00:00');
