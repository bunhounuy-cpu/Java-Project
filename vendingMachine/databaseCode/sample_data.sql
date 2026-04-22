USE vending_machine;

-- Insert Users
INSERT INTO users (user_id, full_name, phone, username, password, user_type) VALUES
('CUST001', 'Seng Dina', '5551234567', 'dina', 'dina123', 'CUSTOMER'),
('CUST002', 'Sor Channorakpitou', '5559876543', 'pitou', 'pitou123', 'CUSTOMER'),
('MGR001', 'Chan Siekfong', '5551112222', 'fong', 'fong123', 'MANAGER'),
('REST001', 'Nuy Bunhou', '5553334444', 'hou', 'hou123', 'RESTOCKER');

-- Insert Customer-specific data
INSERT INTO customers (user_id, email) VALUES
('CUST001', 'john.doe@email.com'),
('CUST002', 'jane.smith@email.com');

-- Insert Manager data
INSERT INTO managers (user_id, salary) VALUES
('MGR001', 5000.00);

-- Insert Restocker data
INSERT INTO restockers (user_id, salary) VALUES
('REST001', 2500.00);

-- Insert Products
INSERT INTO products (name, category, price) VALUES
('Coca Cola', 'Beverages', 1.50),
('Pepsi', 'Beverages', 1.50),
('Water Bottle', 'Beverages', 1.00),
('Chips', 'Snacks', 1.25),
('Chocolate Bar', 'Snacks', 1.75),
('Gum', 'Snacks', 0.75),
('Sandwich', 'Food', 3.50),
('Cookies', 'Snacks', 2.00);

-- Insert Vending Machines
INSERT INTO vending_machines (location, status) VALUES
('Main Lobby', 'ACTIVE'),
('Break Room', 'ACTIVE'),
('Gym Area', 'ACTIVE');

-- Insert Slots (Machine 1 - Main Lobby)
INSERT INTO slots (slot_id, machine_id, product_id, quantity) VALUES
('A1', 1, 1, 10),  -- Coca Cola
('A2', 1, 2, 8),   -- Pepsi
('A3', 1, 3, 10),  -- Water Bottle
('B1', 1, 4, 10),  -- Chips
('B2', 1, 5, 6),   -- Chocolate Bar
('B3', 1, 6, 10),  -- Gum
('C1', 1, 7, 4),   -- Sandwich
('C2', 1, 8, 10);  -- Cookies

-- Insert Slots (Machine 2 - Break Room)
INSERT INTO slots (slot_id, machine_id, product_id, quantity) VALUES
('A1', 2, 1, 5),   -- Coca Cola
('A2', 2, 3, 10),  -- Water Bottle
('B1', 2, 4, 8),   -- Chips
('B2', 2, 6, 10);  -- Gum

-- Insert Slots (Machine 3 - Gym Area)
INSERT INTO slots (slot_id, machine_id, product_id, quantity) VALUES
('A1', 3, 3, 10),  -- Water Bottle
('A2', 3, 1, 8),   -- Coca Cola
('B1', 3, 7, 6),   -- Sandwich
('B2', 3, 4, 10);  -- Chips

-- Sample Transactions
INSERT INTO transactions (customer_id, machine_id, slot_id, product_name, charged_amount, successful) VALUES
('CUST001', 1, 'A1', 'Coca Cola', 1.50, TRUE),
('CUST002', 1, 'B1', 'Chips', 1.25, TRUE),
('CUST001', 2, 'A2', 'Water Bottle', 1.00, TRUE),
('CUST002', 3, 'A1', 'Water Bottle', 1.00, TRUE);

-- Sample Transaction Snapshots (for first transaction)
INSERT INTO transaction_snapshots (transaction_id, slot_index, price, quantity) VALUES
(1, 0, 1.50, 10),  -- A1: Coca Cola
(1, 1, 1.50, 8),   -- A2: Pepsi
(1, 2, 1.00, 15),  -- A3: Water Bottle
(1, 3, 1.25, 12),  -- B1: Chips
(1, 4, 1.75, 6),   -- B2: Chocolate Bar
(1, 5, 0.75, 20),  -- B3: Gum
(1, 6, 3.50, 4),   -- C1: Sandwich
(1, 7, 2.00, 10);  -- C2: Cookies

-- Update user balances
UPDATE users SET balance = 50.00 WHERE user_id = 'CUST001';
UPDATE users SET balance = 75.00 WHERE user_id = 'CUST002';
UPDATE users SET balance = 100.00 WHERE user_id = 'MGR001';
UPDATE users SET balance = 60.00 WHERE user_id = 'REST001';