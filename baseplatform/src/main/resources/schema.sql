-- 创建数据库
CREATE DATABASE IF NOT EXISTS ecommerce DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ecommerce;

-- 用户表
CREATE TABLE IF NOT EXISTS `t_user` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`         VARCHAR(100) NOT NULL                COMMENT '用户名（邮箱）',
    `password`         VARCHAR(255) NOT NULL                COMMENT '密码（BCrypt加密）',
    `nickname`         VARCHAR(50)                          COMMENT '昵称',
    `roles`            VARCHAR(255) NOT NULL DEFAULT 'USER'  COMMENT '角色（逗号分隔）',
    `status`           VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE/LOCKED/DISABLED',
    `failed_attempts`  INT          NOT NULL DEFAULT 0       COMMENT '登录失败次数',
    `locked_until`     DATETIME                             COMMENT '锁定到期时间',
    `created_at`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`          TINYINT(1)   NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-正常，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 刷新令牌表
CREATE TABLE IF NOT EXISTS `t_refresh_token` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`     BIGINT       NOT NULL                COMMENT '用户ID',
    `token`       VARCHAR(500) NOT NULL                COMMENT '刷新令牌',
    `expiry_date` DATETIME     NOT NULL                COMMENT '过期时间',
    `revoked`     TINYINT(1)   NOT NULL DEFAULT 0      COMMENT '是否已撤销',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT(1)   NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-正常，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_token` (`token`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_expiry_date` (`expiry_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='刷新令牌表';

-- 订单表
CREATE TABLE IF NOT EXISTS `t_order` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_no`        VARCHAR(32)  NOT NULL                COMMENT '订单号',
    `user_id`         BIGINT       NOT NULL                COMMENT '用户ID',
    `total_amount`    DECIMAL(10,2) NOT NULL               COMMENT '订单总金额',
    `status`          VARCHAR(32)  NOT NULL                COMMENT '订单状态',
    `payment_method`  VARCHAR(32)  NOT NULL                COMMENT '支付方式',
    `payment_status`  VARCHAR(32)  NOT NULL DEFAULT 'UNPAID' COMMENT '支付状态',
    `remark`          VARCHAR(500)                         COMMENT '订单备注',
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT(1)   NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-正常，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单商品表
CREATE TABLE IF NOT EXISTS `t_order_item` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_id`       BIGINT       NOT NULL                COMMENT '订单ID',
    `product_id`     BIGINT       NOT NULL                COMMENT '商品ID',
    `product_name`   VARCHAR(200) NOT NULL                COMMENT '商品名称',
    `product_image`  VARCHAR(500)                         COMMENT '商品图片',
    `quantity`       INT          NOT NULL                COMMENT '购买数量',
    `price`          DECIMAL(10,2) NOT NULL                COMMENT '商品单价',
    `subtotal`       DECIMAL(10,2) NOT NULL                COMMENT '小计金额',
    `created_at`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品表';

-- 订单收货地址表
CREATE TABLE IF NOT EXISTS `t_order_address` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_id`        BIGINT       NOT NULL UNIQUE         COMMENT '订单ID',
    `receiver_name`   VARCHAR(50)  NOT NULL                COMMENT '收货人姓名',
    `phone`           VARCHAR(20)  NOT NULL                COMMENT '收货人电话',
    `province`        VARCHAR(50)  NOT NULL                COMMENT '省',
    `city`            VARCHAR(50)  NOT NULL                COMMENT '市',
    `district`        VARCHAR(50)  NOT NULL                COMMENT '区',
    `detail_address`  VARCHAR(200) NOT NULL                COMMENT '详细地址',
    `postal_code`     VARCHAR(10)                          COMMENT '邮政编码',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单收货地址表';

-- 订单状态历史表
CREATE TABLE IF NOT EXISTS `t_order_status_history` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_id`   BIGINT       NOT NULL                COMMENT '订单ID',
    `status`     VARCHAR(32)  NOT NULL                COMMENT '订单状态',
    `remark`     VARCHAR(500)                         COMMENT '备注说明',
    `operator`   VARCHAR(50)                          COMMENT '操作人',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单状态历史表';

-- 商品分类表
CREATE TABLE IF NOT EXISTS `t_category` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        VARCHAR(100) NOT NULL                COMMENT '分类名称',
    `icon`        VARCHAR(200)                         COMMENT '分类图标',
    `parent_id`   BIGINT       DEFAULT 0               COMMENT '父分类ID',
    `sort_order`  INT          DEFAULT 0               COMMENT '排序号',
    `enabled`     TINYINT(1)   NOT NULL DEFAULT 1      COMMENT '是否启用',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 商品表
CREATE TABLE IF NOT EXISTS `t_product` (
    `id`                BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`              VARCHAR(200) NOT NULL                COMMENT '商品名称',
    `description`       TEXT                              COMMENT '商品描述',
    `image`             VARCHAR(500)                         COMMENT '商品图片',
    `price`             DECIMAL(10,2) NOT NULL               COMMENT '商品价格',
    `original_price`    DECIMAL(10,2)                      COMMENT '原价',
    `discount`          INT          DEFAULT 0              COMMENT '折扣',
    `sales`             INT          NOT NULL DEFAULT 0     COMMENT '销量',
    `rating`            DECIMAL(3,1) DEFAULT 0              COMMENT '评分',
    `review_count`      INT          NOT NULL DEFAULT 0     COMMENT '评价数量',
    `stock`             INT          NOT NULL DEFAULT 0     COMMENT '库存',
    `brand`             VARCHAR(100)                       COMMENT '品牌',
    `spec`              VARCHAR(200)                       COMMENT '规格',
    `category_id`       BIGINT       DEFAULT 0              COMMENT '分类ID',
    `flash_sale_end_time` DATETIME                        COMMENT '闪购结束时间',
    `enabled`           TINYINT(1)   NOT NULL DEFAULT 1     COMMENT '是否上架',
    `sort_order`        INT          DEFAULT 0              COMMENT '排序号',
    `created_at`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_enabled` (`enabled`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 插入测试用户（密码：password123，BCrypt加密后）
INSERT INTO `t_user` (`username`, `password`, `nickname`, `roles`, `status`) VALUES
('admin@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '管理员', 'ADMIN,USER', 'ACTIVE'),
('user@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '普通用户', 'USER', 'ACTIVE')
ON DUPLICATE KEY UPDATE `updated_at` = CURRENT_TIMESTAMP;

-- 插入商品分类
INSERT INTO `t_category` (`name`, `icon`, `parent_id`, `sort_order`, `enabled`) VALUES
('电子产品', '📱', 0, 1, 1),
('服装鞋帽', '👔', 0, 2, 1),
('美妆护肤', '💄', 0, 3, 1),
('家居用品', '🏠', 0, 4, 1),
('食品零食', '🍎', 0, 5, 1),
('图书文具', '📚', 0, 6, 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 插入商品数据
INSERT INTO `t_product` (`name`, `description`, `image`, `price`, `original_price`, `discount`, `sales`, `rating`, `review_count`, `stock`, `brand`, `spec`, `category_id`, `enabled`, `sort_order`) VALUES
('无线蓝牙耳机', '高品质无线蓝牙耳机，支持降噪功能', 'https://via.placeholder.com/400x400.png?text=Bluetooth+Headset', 199.00, 299.00, 33, 2580, 4.8, 1256, 500, '某品牌', '黑色/白色', 1, 1, 1),
('智能手表', '多功能智能手表，支持心率监测', 'https://via.placeholder.com/400x400.png?text=Smart+Watch', 899.00, 1299.00, 31, 1890, 4.7, 892, 300, '某品牌', '银色/金色', 1, 1, 2),
('轻薄笔记本电脑', '14英寸轻薄本，搭载最新处理器', 'https://via.placeholder.com/400x400.png?text=Laptop', 4999.00, 5999.00, 17, 1250, 4.9, 456, 150, '某品牌', '8GB+256GB', 1, 1, 3),
('机械键盘', '青轴机械键盘，适合游戏和办公', 'https://via.placeholder.com/400x400.png?text=Keyboard', 399.00, 499.00, 20, 3200, 4.6, 1567, 400, '某品牌', '87键/104键', 1, 1, 4),
('无线鼠标', '人体工学无线鼠标，长时间使用不累', 'https://via.placeholder.com/400x400.png?text=Mouse', 129.00, 169.00, 24, 4500, 4.5, 2341, 600, '某品牌', '标准版', 1, 1, 5),
('男士T恤', '纯棉透气男士T恤，休闲百搭', 'https://via.placeholder.com/400x400.png?text=Mens+T-Shirt', 89.00, 129.00, 31, 5600, 4.4, 2890, 800, '某品牌', 'S/M/L/XL', 2, 1, 1),
('运动休闲鞋', '轻便透气运动鞋，适合跑步和健身', 'https://via.placeholder.com/400x400.png?text=Sneakers', 299.00, 399.00, 25, 3200, 4.6, 1678, 450, '某品牌', '38-44码', 2, 1, 2),
('女士连衣裙', '时尚碎花连衣裙，优雅大方', 'https://via.placeholder.com/400x400.png?text=Dress', 259.00, 359.00, 28, 2800, 4.7, 1456, 300, '某品牌', 'S/M/L', 2, 1, 3),
('护肤套装', '保湿补水护肤套装，呵护肌肤', 'https://via.placeholder.com/400x400.png?text=Skincare', 399.00, 599.00, 33, 1800, 4.8, 945, 250, '某品牌', '5件套', 3, 1, 1),
('家居收纳盒', '多功能收纳盒，整洁有序', 'https://via.placeholder.com/400x400.png?text=Storage+Box', 59.00, 89.00, 34, 8900, 4.5, 4567, 1200, '某品牌', '中号/大号', 4, 1, 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);