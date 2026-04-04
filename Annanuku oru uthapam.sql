-- ============================================================
-- DATABASE: bxohvsqqznggomjetfir  —  Full optimized schema
-- ============================================================

USE bxohvsqqznggomjetfir;
SET SQL_SAFE_UPDATES  = 0;
SET FOREIGN_KEY_CHECKS = 0;

-- ─────────────────────────────────────────────────────────────
-- 1. AUTH TABLES  (id = INT, auto-increment, unique per role)
-- ─────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS admin_login (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(100) NOT NULL,
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS manager_login (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(100) NOT NULL,
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS chef_login (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(100) NOT NULL,
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS delivery_boy_login (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    username     VARCHAR(100) NOT NULL UNIQUE,
    password     VARCHAR(100) NOT NULL,
    phone        VARCHAR(15),
    is_available BOOLEAN   DEFAULT TRUE,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ─────────────────────────────────────────────────────────────
-- 2. CUSTOMER  — id is the unique number, username is the name
-- ─────────────────────────────────────────────────────────────
-- FIX: customer_id is INT AUTO_INCREMENT (unique, numeric).
--      username holds the person's name / login name.
--      Both are UNIQUE so no duplicates either way.

CREATE TABLE IF NOT EXISTS customer_login (
    id         INT AUTO_INCREMENT PRIMARY KEY,   -- unique numeric ID
    username   VARCHAR(100) NOT NULL UNIQUE,      -- login name (unique)
    full_name  VARCHAR(150),                      -- display name (can repeat)
    password   VARCHAR(100) NOT NULL,
    email      VARCHAR(100) UNIQUE,
    phone      VARCHAR(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ─────────────────────────────────────────────────────────────
-- 3. INVENTORY & MENU
-- ─────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS inventory (
    product_id VARCHAR(50)  PRIMARY KEY,
    name       VARCHAR(100) NOT NULL UNIQUE,
    quantity   INT          DEFAULT 0,
    unit       VARCHAR(20),
    price      DOUBLE,
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS menu_items (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    product_id   VARCHAR(50) UNIQUE,
    name         VARCHAR(100) NOT NULL UNIQUE,
    description  VARCHAR(255),
    rating       DOUBLE,
    price        DOUBLE,
    is_available BOOLEAN   DEFAULT TRUE,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_menu_inventory
        FOREIGN KEY (product_id) REFERENCES inventory(product_id)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

-- ─────────────────────────────────────────────────────────────
-- 4. MEAL TIMES  — named slots, not an ENUM so you can add more
-- ─────────────────────────────────────────────────────────────
-- FIX: A menu item can appear in MULTIPLE meal times
--      (e.g. idli in both MORNING and EVENING).
--      The junction table menu_item_meal_times handles this.
--      Never store meal_time directly on menu_items.

CREATE TABLE IF NOT EXISTS meal_times (
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE    -- MORNING | AFTERNOON | EVENING | SNACKS | NIGHT
);

-- Junction: one menu item → many meal times, many items per slot
CREATE TABLE IF NOT EXISTS menu_item_meal_times (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    menu_item_id INT NOT NULL,
    meal_time_id INT NOT NULL,
    UNIQUE KEY uq_item_slot (menu_item_id, meal_time_id),   -- no duplicate pairs
    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id)  ON DELETE CASCADE,
    FOREIGN KEY (meal_time_id) REFERENCES meal_times(id)  ON DELETE CASCADE
);

-- ─────────────────────────────────────────────────────────────
-- 5. DISCOUNTS
-- ─────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS discounts (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    code             VARCHAR(50) NOT NULL UNIQUE,
    discount_percent DOUBLE      NOT NULL DEFAULT 0,
    min_order_amount DOUBLE      DEFAULT 0,          -- minimum cart value to apply
    max_uses         INT         DEFAULT NULL,        -- NULL = unlimited
    used_count       INT         DEFAULT 0,
    valid_from       DATE,
    valid_until      DATE,
    is_active        BOOLEAN     DEFAULT TRUE,
    created_at       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
);

-- ─────────────────────────────────────────────────────────────
-- 6. CART
-- ─────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS cart (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    customer_id  INT NOT NULL,
    menu_item_id INT NOT NULL,
    quantity     INT DEFAULT 1,
    added_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_cart_item (customer_id, menu_item_id),
    FOREIGN KEY (customer_id)  REFERENCES customer_login(id) ON DELETE CASCADE,
    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id)     ON DELETE CASCADE
);

-- ─────────────────────────────────────────────────────────────
-- 7. CHECKOUT  — enriched with address + discount application
-- ─────────────────────────────────────────────────────────────
-- FIX: address fields added (state, pincode, phone).
--      discount_id links to discounts table.
--      final_amount = subtotal after discount applied in app logic.

CREATE TABLE IF NOT EXISTS checkout (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    customer_id       INT    NOT NULL,

    -- delivery address
    delivery_address  VARCHAR(255),
    city              VARCHAR(100),
    state             VARCHAR(100),
    pincode           VARCHAR(10),
    phone             VARCHAR(15),         -- contact number for delivery

    -- pricing
    subtotal          DOUBLE NOT NULL DEFAULT 0,
    discount_id       INT    DEFAULT NULL,           -- which discount was applied
    discount_amount   DOUBLE DEFAULT 0,              -- how much was deducted
    final_amount      DOUBLE NOT NULL DEFAULT 0,     -- subtotal - discount_amount

    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (customer_id)  REFERENCES customer_login(id) ON DELETE CASCADE,
    FOREIGN KEY (discount_id)  REFERENCES discounts(id)      ON DELETE SET NULL
);

-- ─────────────────────────────────────────────────────────────
-- 8. ORDERS  — the central table; id stays VARCHAR for custom codes
-- ─────────────────────────────────────────────────────────────
-- STATUS LIFECYCLE (matches your requirement exactly):
--   PLACED → PREPARING → READY → PICKED_UP → OUT_FOR_DELIVERY → DELIVERED
--   Any stage can move to → CANCELLED

CREATE TABLE IF NOT EXISTS customer_orders (
    id             VARCHAR(30) PRIMARY KEY,   -- e.g. ORD-20240601-001
    customer_id    INT         NOT NULL,
    checkout_id    INT         DEFAULT NULL,  -- links back to the checkout record
    total_amount   DOUBLE,
    discount_amount DOUBLE     DEFAULT 0,
    final_amount   DOUBLE,
    status         ENUM(
                     'PLACED',
                     'PREPARING',
                     'READY',
                     'PICKED_UP',
                     'OUT_FOR_DELIVERY',
                     'DELIVERED',
                     'CANCELLED'
                   )           DEFAULT 'PLACED',
    special_notes  VARCHAR(255),
    ordered_at     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (customer_id) REFERENCES customer_login(id),
    FOREIGN KEY (checkout_id) REFERENCES checkout(id) ON DELETE SET NULL
);

-- Order line items (what was ordered)
CREATE TABLE IF NOT EXISTS order_items (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    order_id     VARCHAR(30) NOT NULL,
    menu_item_id INT         NOT NULL,
    quantity     INT         NOT NULL DEFAULT 1,
    unit_price   DOUBLE      NOT NULL,
    FOREIGN KEY (order_id)     REFERENCES customer_orders(id) ON DELETE CASCADE,
    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id)
);

-- Payment

CREATE TABLE IF NOT EXISTS payments (
                    payment_id     VARCHAR(30) PRIMARY KEY,
                    order_id       VARCHAR(30) NOT NULL,
                    customer_id    VARCHAR(100) NOT NULL,
                    payment_method VARCHAR(50),
                    payment_status VARCHAR(50),
                    transaction_id VARCHAR(50),
                    amount         DOUBLE,
                    payment_date   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (order_id) REFERENCES customer_orders(id) ON DELETE CASCADE
);
                
-- ─────────────────────────────────────────────────────────────
-- 9. CHEF ASSIGNMENTS
-- ─────────────────────────────────────────────────────────────
-- When chef is assigned  → order status becomes PREPARING
-- When chef marks done   → order status becomes READY

CREATE TABLE IF NOT EXISTS chef_assignments (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    chef_id       INT         NOT NULL,          -- FK to chef_login.id (not username)
    order_id      VARCHAR(30) NOT NULL,
    status        ENUM('PREPARING','PREPARED')   DEFAULT 'PREPARING',
    assigned_at   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    prepared_at   TIMESTAMP   NULL,
    FOREIGN KEY (chef_id)  REFERENCES chef_login(id)        ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES customer_orders(id)   ON DELETE CASCADE
);

-- ─────────────────────────────────────────────────────────────
-- 10. DELIVERY ASSIGNMENTS
-- ─────────────────────────────────────────────────────────────
-- When delivery boy picks up  → order status = PICKED_UP
-- When marked out             → order status = OUT_FOR_DELIVERY
-- When delivered              → order status = DELIVERED

CREATE TABLE IF NOT EXISTS delivery_assignments (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    delivery_boy_id INT         NOT NULL,
    order_id        VARCHAR(30) NOT NULL,
    status          ENUM('ASSIGNED','PICKED_UP','OUT_FOR_DELIVERY','DELIVERED')
                                DEFAULT 'ASSIGNED',
    assigned_at     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    picked_up_at    TIMESTAMP   NULL,
    delivered_at    TIMESTAMP   NULL,
    FOREIGN KEY (delivery_boy_id) REFERENCES delivery_boy_login(id),
    FOREIGN KEY (order_id)        REFERENCES customer_orders(id)
);

-- ─────────────────────────────────────────────────────────────
-- 11. DELIVERY HISTORY  (permanent log after delivery)
-- ─────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS delivery_history (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    delivery_boy_id INT         NOT NULL,
    order_id        VARCHAR(30) NOT NULL,
    delivered_at    TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (delivery_boy_id) REFERENCES delivery_boy_login(id),
    FOREIGN KEY (order_id)        REFERENCES customer_orders(id)
);

-- ─────────────────────────────────────────────────────────────
-- 12. ADMIN/MANAGER ORDER VIEW  (use this query, not a table)
-- ─────────────────────────────────────────────────────────────
-- Admin and Manager see ALL orders via:
--
-- SELECT
--   o.id            AS order_id,
--   cl.username     AS customer_username,
--   cl.full_name    AS customer_name,
--   cl.phone        AS customer_phone,
--   ch.delivery_address,
--   ch.state,
--   ch.pincode,
--   o.final_amount,
--   o.status,
--   o.ordered_at,
--   o.updated_at,
--   ca.chef_id,
--   da.delivery_boy_id
-- FROM customer_orders o
-- JOIN customer_login cl   ON cl.id = o.customer_id
-- LEFT JOIN checkout ch    ON ch.id = o.checkout_id
-- LEFT JOIN chef_assignments ca ON ca.order_id = o.id
-- LEFT JOIN delivery_assignments da ON da.order_id = o.id
-- ORDER BY o.ordered_at DESC;

-- ─────────────────────────────────────────────────────────────
-- 13. NOTIFICATIONS & REPORTS
-- ─────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS notifications (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    user_id    INT,
    user_role  ENUM('CUSTOMER','CHEF','DELIVERY','ADMIN','MANAGER'),
    message    VARCHAR(255),
    is_read    BOOLEAN   DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS reports (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    admin_id     INT NOT NULL,
    report_type  VARCHAR(100),
    report_data  TEXT,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_id) REFERENCES admin_login(id)
);

CREATE TABLE IF NOT EXISTS customer_reports (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    customer_id    INT NOT NULL,
    order_id       VARCHAR(30) DEFAULT NULL,
    report_message VARCHAR(255),
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customer_login(id),
    FOREIGN KEY (order_id)    REFERENCES customer_orders(id) ON DELETE SET NULL
);




-- ─────────────────────────────────────────────────────────────
-- SEED DATA
-- ─────────────────────────────────────────────────────────────

INSERT IGNORE INTO admin_login   (username, password) VALUES ('admin',   'admin123');
INSERT IGNORE INTO manager_login (username, password) VALUES ('manager', 'manager123');

INSERT IGNORE INTO meal_times (name) VALUES
  ('MORNING'), ('AFTERNOON'), ('EVENING'), ('SNACKS'), ('NIGHT');

-- Example: idli available in BOTH morning and evening
-- INSERT INTO menu_items (product_id, name, price, is_available) VALUES ('PRD001','Idli',30,1);
-- INSERT INTO menu_item_meal_times (menu_item_id, meal_time_id)
--   SELECT m.id, mt.id FROM menu_items m, meal_times mt
--   WHERE m.name='Idli' AND mt.name IN ('MORNING','EVENING');

-- ─────────────────────────────────────────────────────────────
-- DISCOUNT APPLICATION LOGIC (in your backend/app layer)
-- ─────────────────────────────────────────────────────────────
-- 1. Customer enters discount code at checkout
-- 2. Validate: is_active=1, NOW() BETWEEN valid_from AND valid_until,
--              used_count < max_uses (if max_uses IS NOT NULL),
--              cart subtotal >= min_order_amount
-- 3. discount_amount = subtotal * (discount_percent / 100)
-- 4. final_amount    = subtotal - discount_amount
-- 5. INSERT INTO checkout (..., discount_id, discount_amount, final_amount)
-- 6. UPDATE discounts SET used_count = used_count + 1 WHERE id = ?
-- 7. INSERT INTO customer_orders with final_amount, discount_amount

SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES   = 1;

