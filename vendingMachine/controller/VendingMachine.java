package controller;

import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
import other.Slot;
import other.Transaction;
import other.Product;
import other.PaymentService;
import user.Customer;
import user.User;
import exceptions.*;

public class VendingMachine {
    
    // Step 2: Action Constants
    public static final String PURCHASE = "PURCHASE";
    public static final String VIEW_MENU = "VIEW_MENU";
    public static final String RESTOCK = "RESTOCK";
    public static final String VIEW_REVENUE = "VIEW_REVENUE";
    public static final String MANAGE_PRODUCTS = "MANAGE_PRODUCTS";
    public static final String VIEW_TRANSACTIONS = "VIEW_TRANSACTIONS";
    public static final String VIEW_INVENTORY = "VIEW_INVENTORY";
    public static final String VIEW_BALANCE = "VIEW_BALANCE";
    public static final String TOP_UP = "TOP_UP";
    
    private String location;
    private int capacity;
    private ArrayList<Slot> slots;
    private double revenue;
    private ArrayList<Transaction> transactions;
    private ArrayList<User> users;
    private User loggedInUser;
    
    private static int machineCount = 0;
    
    // ====== Constructor ======
    public VendingMachine(String location, int capacity) throws VendingMachineException {
        try {
            machineCount++;
            setLocation(location);
            setCapacity(capacity);
            setSlots(new ArrayList<>());
            setRevenue(0.0);
            setTransactions(new ArrayList<>());
            setUsers(new ArrayList<>());
            setLoggedInUser(null);
            seedDefaultAdmin();
            TestUsers();
            
            // Initialize slots and default users
            seedDefaultSlots();
        } catch (Exception e) {
            throw new VendingMachineException("Failed to initialize vending machine: " + e.getMessage(), e);
        }
    }
    
    // =========================
    // DEFAULT SLOTS (BOOTSTRAP)
    // =========================
    private void seedDefaultSlots() {
        // Load slots from database for this machine
        System.out.println("DEBUG: Attempting to load slots for machine location: " + location);
        try {
            // Find machine ID by location
            int machineId = getMachineIdByLocation(location);
            System.out.println("DEBUG: Found machine ID: " + machineId);
            if (machineId != -1) {
                ArrayList<Slot> dbSlots = MySQL_DATABASE.loadSlots(machineId);
                for (Slot slot : dbSlots) {
                    slots.add(slot);
                }
                System.out.println("DEBUG: Loaded " + dbSlots.size() + " slots from database for machine: " + location);
            } else {
                System.out.println("DEBUG: No machine found with location: " + location);
            }
        } catch (DatabaseConnectionException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            // Continue without database slots - machine will work with empty inventory
        } catch (Exception e) {
            System.out.println("Error loading slots from database: " + e.getMessage());
            // Continue without database slots - machine will work with empty inventory
        }
    }
    
    // =========================
    // DEFAULT USERS (BOOTSTRAP)
    // =========================
    private void seedDefaultAdmin() {
        // Load all users from database
        try {
            ArrayList<User> dbUsers = MySQL_DATABASE.loadUsers();
            for (User user : dbUsers) {
                users.add(user);
            }
            System.out.println("Loaded " + dbUsers.size() + " users from database");
        } catch (DatabaseConnectionException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            // Continue without database users - machine will work with no users
        } catch (Exception e) {
            System.out.println("Error loading users from database: " + e.getMessage());
            // Continue without database users - machine will work with no users
        }
    }

    // =========================
    // DEFAULT USERS (BOOTSTRAP)
    // =========================
    private void TestUsers() {
        // Users are now loaded from database in seedDefaultAdmin()
    }

    
    public static int getMachineCount() {
        return machineCount;
    }
    
    // Helper method to get machine ID by location
    private int getMachineIdByLocation(String location) throws DatabaseConnectionException {
        if (location == null || location.trim().isEmpty()) {
            throw new DatabaseConnectionException("Location cannot be null or empty");
        }
        
        String query = "SELECT machine_id FROM vending_machines WHERE location = '" + location + "'";
        try {
            ResultSet rs = MySQL_DATABASE.executeQuery(query);
            if (rs != null && rs.next()) {
                return rs.getInt("machine_id");
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Error getting machine ID: " + e.getMessage(), e);
        }
        return -1; // Not found
    }
    
    public double getRevenue() {
        if (!requirePermission(VIEW_REVENUE)) return 0.0;
        return revenue;
    }
    
    public void addSlot(String slotID, Product product, int quantity) throws VendingMachineException {
        if (slotID == null || slotID.trim().isEmpty()) {
            throw new InvalidInputException("Slot ID cannot be empty");
        }
        
        if (product == null) {
            throw new InvalidInputException("Product cannot be null");
        }
        
        if (quantity < 0) {
            throw new InvalidInputException("Quantity cannot be negative");
        }
        
        if (slots.size() >= capacity) {
            throw new VendingMachineException("Cannot add slot: Machine capacity reached (" + capacity + " slots max)");
        }
        
        Slot s = new Slot(slotID, product, quantity);
        slots.add(s);
    }
    
    public void restock(String slotID, int amount) throws VendingMachineException {
        if (!requirePermission(RESTOCK)) return;
        
        if (slotID == null || slotID.trim().isEmpty()) {
            throw new InvalidInputException("Slot ID cannot be empty");
        }
        
        if (amount <= 0) {
            throw new InvalidInputException("Amount must be positive");
        }
        
        Slot s = findSlot(slotID);
        if (s == null) {
            throw new ProductNotFoundException("Slot not found: " + slotID, slotID);
        }
        
        s.addQuantity(amount);
        System.out.println("Restocked " + amount + " items to slot " + slotID + ". New quantity: " + s.getQuantity());
    }
        
    public Slot findSlot(String slotID) {
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i) != null) {
                Slot s = slots.get(i);
                if (s.getSlotID().equals(slotID)) {
                    return s;
                }
            }
        }
        return null;
    }
    
    public boolean vend(String slotID, Customer customer) throws VendingMachineException {
        if (!requirePermission(PURCHASE)) return false;
        
        if (slotID == null || slotID.trim().isEmpty()) {
            throw new InvalidInputException("Slot ID cannot be empty");
        }
        
        if (customer == null) {
            throw new InvalidInputException("Customer cannot be null");
        }
        
        Slot s = findSlot(slotID);
        if (s == null) {
            throw new ProductNotFoundException("Product not found in slot: " + slotID, slotID);
        }
        
        if (s.getQuantity() <= 0) {
            throw new InsufficientStockException("Product out of stock", slotID, 1, 0);
        }
        
        double priceToPay = PaymentService.computeWithLoyalty(s.getProduct().getPrice(), customer.isPremium(), customer.getItemsBought());
        
        if (customer.getBalance() < priceToPay) {
            throw new InsufficientFundsException("Insufficient funds for purchase", priceToPay, customer.getBalance());
        }
        
        if (!PaymentService.charge(customer, priceToPay)) {
            return false;
        }
        
        s.addQuantity(-1);
        revenue += priceToPay;
        customer.incrementItems();
        Transaction t = new Transaction(customer, location, slots);
        t.saveTransaction(slotID, s.getProduct().getName(), priceToPay);
        transactions.add(t);
        return true;
    }
    
    // New method for bulk purchase with single transaction
    public boolean vendBulk(String slotID, Customer customer, int quantity) throws VendingMachineException {
        if (!requirePermission(PURCHASE)) return false;
        
        if (slotID == null || slotID.trim().isEmpty()) {
            throw new InvalidInputException("Slot ID cannot be empty");
        }
        
        if (customer == null) {
            throw new InvalidInputException("Customer cannot be null");
        }
        
        if (quantity <= 0) {
            throw new InvalidInputException("Quantity must be positive");
        }
        
        Slot s = findSlot(slotID);
        if (s == null) {
            throw new ProductNotFoundException("Product not found in slot: " + slotID, slotID);
        }
        
        if (s.getQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock for bulk purchase", slotID, quantity, s.getQuantity());
        }
        
        double pricePerItem = PaymentService.computeWithLoyalty(s.getProduct().getPrice(), customer.isPremium(), customer.getItemsBought());
        double totalPrice = pricePerItem * quantity;
        
        if (customer.getBalance() < totalPrice) {
            throw new InsufficientFundsException("Insufficient funds for bulk purchase", totalPrice, customer.getBalance());
        }
        
        if (!PaymentService.charge(customer, totalPrice)) {
            return false;
        }
        
        s.addQuantity(-quantity);
        revenue += totalPrice;
        
        // Add items bought for loyalty calculation
        for (int i = 0; i < quantity; i++) {
            customer.incrementItems();
        }
        
        // Create single transaction for the bulk purchase
        Transaction t = new Transaction(customer, location, slots);
        t.saveTransaction(slotID, s.getProduct().getName() + " (x" + quantity + ")", totalPrice);
        transactions.add(t);
        
        return true;
    }
    
    public void printMenu() {
        System.out.println("=== " + location + " Menu ===");
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i) != null) {
                Slot s = slots.get(i);
                System.out.println(s.getSlotID() + " - " + s.getProduct().getName() + " [" + s.getProduct().getCategory() + "] $" + s.getProduct().getPrice() + " x" + s.getQuantity());
            }
        }
    }
    
    public void printInventory() {
        System.out.println("=== " + location + " Inventory ===");
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i) != null) {
                System.out.println(slots.get(i).toString());
            }
        }
    }
    
    public void printTransactions() {
        if (!requirePermission(VIEW_TRANSACTIONS)) return;
        System.out.println("=== " + location + " Transactions ===");
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i) != null) {
                System.out.println(transactions.get(i).toString());
            }
        }
    }
    
    public void addUser(User user) {
        users.add(user);
    }
    
    public ArrayList<User> getUsers() {
        return users;
    }
    
    public boolean login(String username, String password) throws AuthenticationException {
        if (username == null || username.trim().isEmpty()) {
            throw new AuthenticationException("Username cannot be empty");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new AuthenticationException("Password cannot be empty");
        }
        
        for (User user : users) {
            if (user.getUsername().equals(username) && user.checkPassword(password)) {
                if (!user.isActive()) {
                    throw new AuthenticationException("Account is suspended", username);
                }
                loggedInUser = user;
                System.out.println("Login success. Welcome " + user.getFullName() + "!");
                return true;
            }
        }
        throw new AuthenticationException("Invalid username or password", username);
    }
    
    public void logout() {
        if (loggedInUser != null) {
            System.out.println("Logged out successfully.");
            loggedInUser = null;
        }
    }
    
    public User getLoggedInUser() {
        return loggedInUser;
    }
    
    public boolean isUserLoggedIn() {
        return loggedInUser != null;
    }
    
    // ====== Setters ======
    public void setLocation(String location) throws InvalidInputException {
        if (!isBlank(location)) {
            this.location = location.trim();
        } else {
            throw new InvalidInputException("Location cannot be empty");
        }
    }
    
    public void setRevenue(double revenue) {
        if (revenue >= 0) {
            this.revenue = revenue;
        }
    }
    
    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }
    
    public void setCapacity(int capacity) throws InvalidInputException {
        if (capacity > 0) {
            this.capacity = capacity;
        } else {
            throw new InvalidInputException("Capacity must be positive");
        }
    }
    
    public void setSlots(ArrayList<Slot> slots) {
        this.slots = slots;
    }
    
    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }
    
    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
    
    // Helper method for validation
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
    
    // Permission checking method
    private boolean requirePermission(String action) {
        if (!isUserLoggedIn()) {
            System.out.println("Access denied: No user logged in");
            return false;
        }
        
        User user = getLoggedInUser();
        if (!user.can(action)) {
            System.out.println("Access denied: you cannot perform " + action);
            return false;
        }
        return true;
    }
}
