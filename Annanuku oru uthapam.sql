USE bxohvsqqznggomjetfir;

-- 1. Admin login table
CREATE TABLE admin_login (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- adding admin and the manager cred--
INSERT INTO admin_login (username, password) 
VALUES ('admin', 'admin123');

-- Insert Manager (done by Admin)
INSERT INTO manager_login (username, password)
VALUES ('manager', 'manager123');

-- 2. Menu items table
CREATE TABLE menu_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    rating DOUBLE,
    meal_time ENUM('MORNING', 'AFTERNOON', 'NIGHT', 'SNACKS'),
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);




ALTER TABLE menu_items
ADD COLUMN product_id VARCHAR(50) UNIQUE AFTER id,
ADD COLUMN price DOUBLE AFTER rating;

-- 3. Customer table (admin views this)
CREATE TABLE customer_login (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Customer past orders (admin views this)
CREATE TABLE customer_orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    order_details VARCHAR(255),
    total_amount DOUBLE,
    status ENUM('PLACED','PREPARING','OUT_FOR_DELIVERY','DELIVERED','CANCELLED'),
    ordered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customer_login(id)
);

-- 5. Customer reports (admin views this)
CREATE TABLE customer_reports (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    report_message VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customer_login(id)
);

-- 6. Delivery boy table (admin views this)
CREATE TABLE delivery_boy_login (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 7. Delivery boy assigned orders (admin manages this)
CREATE TABLE delivery_assignments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    delivery_boy_id INT NOT NULL,
    order_id INT NOT NULL,
    status ENUM('ASSIGNED','PICKED_UP','DELIVERED'),
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    delivered_at TIMESTAMP NULL,
    FOREIGN KEY (delivery_boy_id) REFERENCES delivery_boy_login(id),
    FOREIGN KEY (order_id) REFERENCES customer_orders(id)
);

-- 8. Delivery boy history
CREATE TABLE delivery_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    delivery_boy_id INT NOT NULL,
    order_id INT NOT NULL,
    delivered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (delivery_boy_id) REFERENCES delivery_boy_login(id),
    FOREIGN KEY (order_id) REFERENCES customer_orders(id)
);

-- 9. Discounts (admin manages)
CREATE TABLE discounts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    discount_percent DOUBLE,
    valid_from DATE,
    valid_until DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 10. Notifications (admin sends)
CREATE TABLE notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    message VARCHAR(255),
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 11. Reports (admin generates)
CREATE TABLE reports (
    id INT AUTO_INCREMENT PRIMARY KEY,
    admin_id INT NOT NULL,
    report_type VARCHAR(100),
    report_data TEXT,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_id) REFERENCES admin_login(id)
);
SELECT * FROM delivery_boy_login;
