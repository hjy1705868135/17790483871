-- SQLite 初始化脚本

-- 用户表
CREATE TABLE IF NOT EXISTS t_user (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    username         TEXT    NOT NULL,
    password         TEXT    NOT NULL,
    nickname         TEXT,
    roles            TEXT    NOT NULL DEFAULT 'USER',
    status           TEXT    NOT NULL DEFAULT 'ACTIVE',
    failed_attempts  INTEGER NOT NULL DEFAULT 0,
    locked_until     TEXT,
    created_at       TEXT    NOT NULL DEFAULT (datetime('now','localtime')),
    updated_at       TEXT    NOT NULL DEFAULT (datetime('now','localtime')),
    deleted          INTEGER NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_username ON t_user(username);
CREATE INDEX IF NOT EXISTS idx_user_status ON t_user(status);

-- 刷新令牌表
CREATE TABLE IF NOT EXISTS t_refresh_token (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id     INTEGER NOT NULL,
    token       TEXT    NOT NULL,
    expiry_date TEXT    NOT NULL,
    revoked     INTEGER NOT NULL DEFAULT 0,
    created_at  TEXT    NOT NULL DEFAULT (datetime('now','localtime')),
    updated_at  TEXT    NOT NULL DEFAULT (datetime('now','localtime')),
    deleted     INTEGER NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_token ON t_refresh_token(token);
CREATE INDEX IF NOT EXISTS idx_rt_user_id ON t_refresh_token(user_id);

-- 订单表
CREATE TABLE IF NOT EXISTS t_order (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    order_no        TEXT    NOT NULL,
    user_id         INTEGER NOT NULL,
    total_amount    REAL    NOT NULL,
    status          TEXT    NOT NULL,
    payment_method  TEXT    NOT NULL,
    payment_status  TEXT    NOT NULL DEFAULT 'UNPAID',
    remark          TEXT,
    created_at      TEXT    NOT NULL DEFAULT (datetime('now','localtime')),
    updated_at      TEXT    NOT NULL DEFAULT (datetime('now','localtime')),
    deleted         INTEGER NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_order_no ON t_order(order_no);
CREATE INDEX IF NOT EXISTS idx_order_user_id ON t_order(user_id);
CREATE INDEX IF NOT EXISTS idx_order_status ON t_order(status);

-- 订单商品表
CREATE TABLE IF NOT EXISTS t_order_item (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id       INTEGER NOT NULL,
    product_id     INTEGER NOT NULL,
    product_name   TEXT    NOT NULL,
    product_image  TEXT,
    quantity       INTEGER NOT NULL,
    price          REAL    NOT NULL,
    subtotal       REAL    NOT NULL,
    created_at     TEXT    NOT NULL DEFAULT (datetime('now','localtime'))
);
CREATE INDEX IF NOT EXISTS idx_oi_order_id ON t_order_item(order_id);

-- 订单收货地址表
CREATE TABLE IF NOT EXISTS t_order_address (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id        INTEGER NOT NULL UNIQUE,
    receiver_name   TEXT    NOT NULL,
    phone           TEXT    NOT NULL,
    province        TEXT    NOT NULL,
    city            TEXT    NOT NULL,
    district        TEXT    NOT NULL,
    detail_address  TEXT    NOT NULL,
    postal_code     TEXT
);

-- 订单状态历史表
CREATE TABLE IF NOT EXISTS t_order_status_history (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id   INTEGER NOT NULL,
    status     TEXT    NOT NULL,
    remark     TEXT,
    operator   TEXT,
    created_at TEXT    NOT NULL DEFAULT (datetime('now','localtime'))
);
CREATE INDEX IF NOT EXISTS idx_osh_order_id ON t_order_status_history(order_id);

-- 用户收货地址表
CREATE TABLE IF NOT EXISTS t_address (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id         INTEGER NOT NULL,
    receiver_name   TEXT    NOT NULL,
    phone           TEXT    NOT NULL,
    province        TEXT    NOT NULL,
    city            TEXT    NOT NULL,
    district        TEXT    NOT NULL,
    detail_address  TEXT    NOT NULL,
    postal_code     TEXT,
    is_default      INTEGER NOT NULL DEFAULT 0,
    created_at      TEXT    NOT NULL DEFAULT (datetime('now','localtime')),
    updated_at      TEXT    NOT NULL DEFAULT (datetime('now','localtime')),
    deleted         INTEGER NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_addr_user_id ON t_address(user_id);

-- 商品评价表
CREATE TABLE IF NOT EXISTS t_review (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id       INTEGER NOT NULL,
    order_item_id  INTEGER NOT NULL,
    product_id     INTEGER NOT NULL,
    user_id        INTEGER NOT NULL,
    rating         INTEGER NOT NULL,
    content        TEXT,
    images         TEXT,
    reply          TEXT,
    reply_time     TEXT,
    created_at     TEXT    NOT NULL DEFAULT (datetime('now','localtime')),
    updated_at     TEXT    NOT NULL DEFAULT (datetime('now','localtime')),
    deleted        INTEGER NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_rev_product_id ON t_review(product_id);
CREATE INDEX IF NOT EXISTS idx_rev_user_id ON t_review(user_id);
CREATE INDEX IF NOT EXISTS idx_rev_order_item_id ON t_review(order_item_id);

-- 购物车表（可选，如果需要持久化购物车）
CREATE TABLE IF NOT EXISTS t_cart (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id        INTEGER NOT NULL,
    product_id     INTEGER NOT NULL,
    quantity       INTEGER NOT NULL DEFAULT 1,
    selected       INTEGER NOT NULL DEFAULT 1,
    created_at     TEXT    NOT NULL DEFAULT (datetime('now','localtime')),
    updated_at     TEXT    NOT NULL DEFAULT (datetime('now','localtime')),
    deleted        INTEGER NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_cart_user_id ON t_cart(user_id);
CREATE INDEX IF NOT EXISTS idx_cart_product_id ON t_cart(product_id);

-- 商品分类表
CREATE TABLE IF NOT EXISTS t_category (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    name        TEXT    NOT NULL,
    icon        TEXT,
    parent_id   INTEGER DEFAULT 0,
    sort_order  INTEGER DEFAULT 0,
    enabled     INTEGER NOT NULL DEFAULT 1,
    created_at  TEXT    NOT NULL DEFAULT (datetime('now','localtime'))
);

-- 商品表
CREATE TABLE IF NOT EXISTS t_product (
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    name                TEXT    NOT NULL,
    description         TEXT,
    image               TEXT,
    price               REAL    NOT NULL,
    original_price      REAL,
    discount            INTEGER DEFAULT 0,
    sales               INTEGER NOT NULL DEFAULT 0,
    rating              REAL    DEFAULT 0,
    review_count        INTEGER NOT NULL DEFAULT 0,
    stock               INTEGER NOT NULL DEFAULT 0,
    brand               TEXT,
    spec                TEXT,
    category_id         INTEGER DEFAULT 0,
    flash_sale_end_time TEXT,
    enabled             INTEGER NOT NULL DEFAULT 1,
    sort_order          INTEGER DEFAULT 0,
    created_at          TEXT    NOT NULL DEFAULT (datetime('now','localtime')),
    updated_at          TEXT    NOT NULL DEFAULT (datetime('now','localtime'))
);
CREATE INDEX IF NOT EXISTS idx_p_category_id ON t_product(category_id);
CREATE INDEX IF NOT EXISTS idx_p_enabled ON t_product(enabled);

-- 插入测试用户（密码：password123，BCrypt加密后）
INSERT OR IGNORE INTO t_user (id, username, password, nickname, roles, status) VALUES
(1, 'admin', '$2a$10$uN8m1SOdxkP9HPVbe1t52uK1bxfOTMQjWoweMVDfbpfNyyuMHim8.', '管理员', 'ADMIN,USER', 'ACTIVE'),
(2, 'user', '$2a$10$uN8m1SOdxkP9HPVbe1t52uK1bxfOTMQjWoweMVDfbpfNyyuMHim8.', '普通用户', 'USER', 'ACTIVE'),
(3, 'admin@example.com', '$2a$10$uN8m1SOdxkP9HPVbe1t52uK1bxfOTMQjWoweMVDfbpfNyyuMHim8.', '邮箱管理员', 'ADMIN,USER', 'ACTIVE'),
(4, 'user@example.com', '$2a$10$uN8m1SOdxkP9HPVbe1t52uK1bxfOTMQjWoweMVDfbpfNyyuMHim8.', '邮箱用户', 'USER', 'ACTIVE');

-- 插入商品分类
INSERT OR IGNORE INTO t_category (id, name, icon, parent_id, sort_order, enabled) VALUES
(1, '电子产品', '📱', 0, 1, 1),
(2, '服装鞋帽', '👔', 0, 2, 1),
(3, '美妆护肤', '💄', 0, 3, 1),
(4, '家居用品', '🏠', 0, 4, 1),
(5, '食品零食', '🍎', 0, 5, 1),
(6, '图书文具', '📚', 0, 6, 1);

-- 插入商品数据
INSERT OR IGNORE INTO t_product (id, name, description, image, price, original_price, discount, sales, rating, review_count, stock, brand, spec, category_id, enabled, sort_order) VALUES
(1, '无线蓝牙耳机', '高品质无线蓝牙耳机，支持降噪功能', 'https://picsum.photos/seed/headphones/400/400', 199.00, 299.00, 33, 2580, 4.8, 1256, 500, '某品牌', '黑色/白色', 1, 1, 1),
(2, '智能手表', '多功能智能手表，支持心率监测', 'https://picsum.photos/seed/watch/400/400', 899.00, 1299.00, 31, 1890, 4.7, 892, 300, '某品牌', '银色/金色', 1, 1, 2),
(3, '轻薄笔记本电脑', '14英寸轻薄本，搭载最新处理器', 'https://picsum.photos/seed/laptop/400/400', 4999.00, 5999.00, 17, 1250, 4.9, 456, 150, '某品牌', '8GB+256GB', 1, 1, 3),
(4, '机械键盘', '青轴机械键盘，适合游戏和办公', 'https://picsum.photos/seed/keyboard/400/400', 399.00, 499.00, 20, 3200, 4.6, 1567, 400, '某品牌', '87键/104键', 1, 1, 4),
(5, '无线鼠标', '人体工学无线鼠标，长时间使用不累', 'https://picsum.photos/seed/mouse/400/400', 129.00, 169.00, 24, 4500, 4.5, 2341, 600, '某品牌', '标准版', 1, 1, 5),
(6, '男士T恤', '纯棉透气男士T恤，休闲百搭', 'https://picsum.photos/seed/tshirt/400/400', 89.00, 129.00, 31, 5600, 4.4, 2890, 800, '某品牌', 'S/M/L/XL', 2, 1, 1),
(7, '运动休闲鞋', '轻便透气运动鞋，适合跑步和健身', 'https://picsum.photos/seed/sneakers/400/400', 299.00, 399.00, 25, 3200, 4.6, 1678, 450, '某品牌', '38-44码', 2, 1, 2),
(8, '女士连衣裙', '时尚碎花连衣裙，优雅大方', 'https://picsum.photos/seed/dress/400/400', 259.00, 359.00, 28, 2800, 4.7, 1456, 300, '某品牌', 'S/M/L', 2, 1, 3),
(9, '护肤套装', '保湿补水护肤套装，呵护肌肤', 'https://picsum.photos/seed/skincare/400/400', 399.00, 599.00, 33, 1800, 4.8, 945, 250, '某品牌', '5件套', 3, 1, 1),
(10, '家居收纳盒', '多功能收纳盒，整洁有序', 'https://picsum.photos/seed/storage/400/400', 59.00, 89.00, 34, 8900, 4.5, 4567, 1200, '某品牌', '中号/大号', 4, 1, 1);
