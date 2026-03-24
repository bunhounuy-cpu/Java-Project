-- Create database
CREATE DATABASE IF NOT EXISTS vending_machine;
USE vending_machine;

-- Users table (base user information)
CREATE TABLE users (
    user_id VARCHAR(20) PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    balance DECIMAL(10,2) DEFAULT 0.00,
    premium BOOLEAN DEFAULT FALSE,
    items_bought INT DEFAULT 0,
    user_type ENUM('CUSTOMER', 'MANAGER', 'RESTOCKER') NOT NULL
);

-- Customer-specific table
CREATE TABLE customers (
    user_id VARCHAR(20) PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Manager-specific table
CREATE TABLE managers (
    user_id VARCHAR(20) PRIMARY KEY,
    salary DECIMAL(10,2) NOT NULL CHECK (salary >= 1000),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Restocker-specific table
CREATE TABLE restockers (
    user_id VARCHAR(20) PRIMARY KEY,
    salary DECIMAL(10,2) NOT NULL CHECK (salary >= 0),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Products table
CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    price DECIMAL(5,2) NOT NULL CHECK (price >= 0)
);

-- Vending machines table
CREATE TABLE vending_machines (
    machine_id INT AUTO_INCREMENT PRIMARY KEY,
    location VARCHAR(100) NOT NULL UNIQUE,
    status ENUM('ACTIVE', 'MAINTENANCE', 'OUT_OF_ORDER') DEFAULT 'ACTIVE'
);

-- Slots table (links products to vending machines)
CREATE TABLE slots (
    slot_id VARCHAR(10) NOT NULL,
    machine_id INT NOT NULL,
    product_id INT,
    quantity INT DEFAULT 0 CHECK (quantity >= 0),
    PRIMARY KEY (slot_id, machine_id),
    FOREIGN KEY (machine_id) REFERENCES vending_machines(machine_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE SET NULL
);

-- Transactions table
CREATE TABLE transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id VARCHAR(20) NOT NULL,
    machine_id INT NOT NULL,
    slot_id VARCHAR(10) NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    charged_amount DECIMAL(5,2) NOT NULL,
    transaction_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    successful BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (customer_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (machine_id) REFERENCES vending_machines(machine_id) ON DELETE CASCADE,
    FOREIGN KEY (slot_id, machine_id) REFERENCES slots(slot_id, machine_id) ON DELETE CASCADE
);

-- Transaction snapshots table
CREATE TABLE transaction_snapshots (
    snapshot_id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_id INT NOT NULL,
    slot_index INT NOT NULL,
    price DECIMAL(5,2) NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_type ON users(user_type);
CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_transactions_customer ON transactions(customer_id);
CREATE INDEX idx_transactions_machine ON transactions(machine_id);
CREATE INDEX idx_transactions_time ON transactions(transaction_time);
CREATE INDEX idx_slots_machine_product ON slots(machine_id, product_id);