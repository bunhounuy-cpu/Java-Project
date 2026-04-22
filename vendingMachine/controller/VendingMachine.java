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

    // Action Constants
    public static final String PURCHASE          = "PURCHASE";
    public static final String VIEW_MENU         = "VIEW_MENU";
    public static final String RESTOCK           = "RESTOCK";
    public static final String VIEW_REVENUE      = "VIEW_REVENUE";
    public static final String MANAGE_PRODUCTS   = "MANAGE_PRODUCTS";
    public static final String VIEW_TRANSACTIONS = "VIEW_TRANSACTIONS";
    public static final String VIEW_INVENTORY    = "VIEW_INVENTORY";
    public static final String VIEW_BALANCE      = "VIEW_BALANCE";
    public static final String TOP_UP            = "TOP_UP";
    public static final String ADD_NEW_PRODUCT          = "ADD_NEW_PRODUCT";
    public static final String REMOVE_PRODUCT       = "REMOVE_PRODUCT";
    public static final String CHANGE_PRODUCT    = "CHANGE_PRODUCT";

    private String location;
    private int capacity;
    private int slotCapacity;
    private ArrayList<Slot> slots;
    private double revenue;
    private ArrayList<Transaction> transactions;
    private ArrayList<User> users;
    private User loggedInUser;

    private static int machineCount = 0;

    // ====== Constructor ======
    public VendingMachine(String location, int capacity, int slotCapacity) throws VendingMachineException {
        try {
            machineCount++;
            setLocation(location);
            setCapacity(capacity);
            setSlotCapacity(slotCapacity);
            setSlots(new ArrayList<>());
            setRevenue(0.0);
            setTransactions(new ArrayList<>());
            setUsers(new ArrayList<>());
            setLoggedInUser(null);
            seedDefaultAdmin();
            seedDefaultSlots();
        } catch (Exception e) {
            throw new VendingMachineException("Failed to initialize vending machine: " + e.getMessage(), e);
        }
    }

    // =========================
    // BOOTSTRAP
    // =========================
    private void seedDefaultSlots() {
        try {
            int machineId = getMachineIdByLocation(location);
            if (machineId != -1) {
                ArrayList<Slot> dbSlots = MySQL_DATABASE.loadSlots(machineId);
                for (Slot slot : dbSlots) slots.add(slot);
            }
        } catch (Exception e) {
            System.out.println("Could not load slots from database: " + e.getMessage());
        }
    }

    private void seedDefaultAdmin() {
        try {
            ArrayList<User> dbUsers = MySQL_DATABASE.loadUsers();
            for (User user : dbUsers) users.add(user);
        } catch (Exception e) {
            System.out.println("Could not load users from database: " + e.getMessage());
        }
    }

    public static int getMachineCount() { return machineCount; }

    private int getMachineIdByLocation(String location) throws DatabaseConnectionException {
        if (isBlank(location)) throw new DatabaseConnectionException("Location cannot be null or empty");
        String query = "SELECT machine_id FROM vending_machines WHERE location = '" + location + "'";
        try {
            ResultSet rs = MySQL_DATABASE.executeQuery(query);
            if (rs != null && rs.next()) return rs.getInt("machine_id");
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Error getting machine ID: " + e.getMessage(), e);
        }
        return -1;
    }

    // ====== Revenue ======
    public double getRevenue() {
        if (!requirePermission(VIEW_REVENUE)) return 0.0;
        return revenue;
    }

    public ArrayList<Slot> getSlots() { return slots; }
    public int getSlotCapacity() { return slotCapacity; }
    public ArrayList<Transaction> getTransactions() { return transactions; }

    // ====== Slot Management ======

    /**
     * Add a brand-new slot with a new product.
     * Requires ADD_NEW_PRODUCT permission.
     */
    public void addSlot(String slotID, Product product, int quantity) throws VendingMachineException {
        if (!requirePermission(ADD_NEW_PRODUCT)) return;
        if (isBlank(slotID))         throw new InvalidInputException("Slot ID cannot be empty");
        if (product == null)         throw new InvalidInputException("Product cannot be null");
        if (quantity < 0)            throw new InvalidInputException("Quantity cannot be negative");
        if (quantity > slotCapacity) throw new InvalidInputException("Quantity cannot exceed slot capacity of " + slotCapacity);
        if (findSlot(slotID) != null)throw new InvalidInputException("Slot '" + slotID + "' already exists");
        if (slots.size() >= capacity)throw new VendingMachineException("Machine is full (" + capacity + " slots max)");

        slots.add(new Slot(slotID, product, quantity));
        System.out.println("Slot " + slotID + " added: " + product.getName() + " (qty: " + quantity + ")");
    }

    /**
     * Remove a slot entirely.
     * Requires REMOVE_PRODUCT permission.
     */
    public void removeSlot(String slotID) throws VendingMachineException {
        if (!requirePermission(REMOVE_PRODUCT)) return;
        if (isBlank(slotID)) throw new InvalidInputException("Slot ID cannot be empty");

        Slot s = findSlot(slotID);
        if (s == null) throw new ProductNotFoundException("Slot not found: " + slotID, slotID);

        slots.remove(s);
        System.out.println("Slot " + slotID + " removed.");
    }

    /**
     * Change the product stored in an existing slot and reset its stock.
     * Real-world use: slot A1 was Coca-Cola but you want to put Pepsi there instead.
     * Requires CHANGE_PRODUCT permission.
     */
    public void changeProduct(String slotID, Product newProduct, int newQuantity) throws VendingMachineException {
        if (!requirePermission(CHANGE_PRODUCT)) return;
        if (isBlank(slotID))    throw new InvalidInputException("Slot ID cannot be empty");
        if (newProduct == null) throw new InvalidInputException("Product cannot be null");
        if (newQuantity < 0)    throw new InvalidInputException("Quantity cannot be negative");
        if (newQuantity > slotCapacity) throw new InvalidInputException("Quantity cannot exceed slot capacity of " + slotCapacity);

        Slot s = findSlot(slotID);
        if (s == null) throw new ProductNotFoundException("Slot not found: " + slotID, slotID);

        String oldName = s.getProduct() != null ? s.getProduct().getName() : "empty";
        s.setProduct(newProduct);
        s.setQuantity(newQuantity);
        System.out.println("Slot " + slotID + ": changed from '" + oldName
                + "' to '" + newProduct.getName() + "' with qty " + newQuantity);
    }

    public void restock(String slotID, int amount) throws VendingMachineException {
        if (!requirePermission(RESTOCK)) return;
        if (isBlank(slotID)) throw new InvalidInputException("Slot ID cannot be empty");
        if (amount <= 0)     throw new InvalidInputException("Amount must be positive");

        Slot s = findSlot(slotID);
        if (s == null) throw new ProductNotFoundException("Slot not found: " + slotID, slotID);

        if (s.getQuantity() + amount > slotCapacity) {
            throw new InvalidInputException("Restock would exceed slot capacity of " + slotCapacity + ". Current: " + s.getQuantity() + ", attempted: +" + amount);
        }

        s.addQuantity(amount);
        System.out.println("Restocked slot " + slotID + " +" + amount + ". New qty: " + s.getQuantity());
    }

    public Slot findSlot(String slotID) {
        for (Slot s : slots) {
            if (s != null && s.getSlotID().equalsIgnoreCase(slotID)) return s;
        }
        return null;
    }

    // ====== Vending ======

    public boolean vendBulk(String slotID, Customer customer, int quantity) throws VendingMachineException {
        if (!requirePermission(PURCHASE)) return false;
        if (isBlank(slotID))  throw new InvalidInputException("Slot ID cannot be empty");
        if (customer == null) throw new InvalidInputException("Customer cannot be null");
        if (quantity <= 0)    throw new InvalidInputException("Quantity must be positive");

        Slot s = findSlot(slotID);
        if (s == null) throw new ProductNotFoundException("Product not found in slot: " + slotID, slotID);
        if (s.getQuantity() < quantity)
            throw new InsufficientStockException("Not enough stock", slotID, quantity, s.getQuantity());

        double pricePerItem = PaymentService.computeFinalPrice(
                s.getProduct().getPrice(), customer.isPremium(), customer.getItemsBought());
        double totalPrice = pricePerItem * quantity;

        if (customer.getBalance() < totalPrice)
            throw new InsufficientFundsException("Insufficient funds", totalPrice, customer.getBalance());

        if (!PaymentService.charge(customer, totalPrice)) return false;

        s.addQuantity(-quantity);
        revenue += totalPrice;
        for (int i = 0; i < quantity; i++) customer.incrementItems();

        // Remove slot completely if quantity reaches 0
        if (s.getQuantity() == 0) {
            slots.remove(s);
            System.out.println("Slot " + slotID + " removed - product out of stock.");
        }

        Transaction t = new Transaction(customer, location);
        t.saveTransaction(slotID, s.getProduct().getName() + " (x" + quantity + ")", totalPrice);
        transactions.add(t);
        return true;
    }

    // ====== Display ======

    public void printMenu() {
        System.out.println("=== " + location + " Menu ===");
        if (slots.isEmpty()) { System.out.println("  No products available."); return; }
        for (Slot s : slots) {
            if (s != null)
                System.out.printf("  [%-4s]  %-20s  [%-10s]  $%.2f   qty: %d%n",
                        s.getSlotID(), s.getProduct().getName(),
                        s.getProduct().getCategory(), s.getProduct().getPrice(), s.getQuantity());
        }
    }

    public void printInventory() {
        System.out.println("=== " + location + " Inventory ===");
        if (slots.isEmpty()) { System.out.println("  No slots found."); return; }
        for (Slot s : slots) { if (s != null && s.getQuantity() < slotCapacity ) System.out.println("  " + s.toString()); }
    }

    public void printTransactions() {
        if (!requirePermission(VIEW_TRANSACTIONS)) return;
        System.out.println("=== " + location + " Transactions ===");
        if (transactions.isEmpty()) { System.out.println("  No transactions yet."); return; }
        for (Transaction t : transactions) { if (t != null) System.out.println("  " + t.toString()); }
    }

    // ====== Users ======

    public void addUser(User user) { users.add(user); }
    public ArrayList<User> getUsers() { return users; }

    public boolean login(String username, String password) throws AuthenticationException {
        if (isBlank(username)) throw new AuthenticationException("Username cannot be empty");
        if (isBlank(password)) throw new AuthenticationException("Password cannot be empty");
        for (User user : users) {
            if (user.getUsername().equals(username) && user.checkPassword(password)) {
                if (!user.isActive()) throw new AuthenticationException("Account is suspended", username);
                loggedInUser = user;
                System.out.println("Login successful. Welcome, " + user.getFullName() + "!");
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

    public User getLoggedInUser()   { return loggedInUser; }
    public boolean isUserLoggedIn() { return loggedInUser != null; }

    // ====== Setters ======

    public void setLocation(String location) throws InvalidInputException {
        if (isBlank(location)) throw new InvalidInputException("Location cannot be empty");
        this.location = location.trim();
    }
    public void setRevenue(double revenue)                    { if (revenue >= 0) this.revenue = revenue; }
    public void setLoggedInUser(User user)                    { this.loggedInUser = user; }
    public void setCapacity(int capacity) throws InvalidInputException {
        if (capacity > 0) this.capacity = capacity;
        else throw new InvalidInputException("Capacity must be positive");
    }
    public void setSlotCapacity(int slotCapacity) throws InvalidInputException {
        if (slotCapacity > 0) this.slotCapacity = slotCapacity;
        else throw new InvalidInputException("Slot capacity must be positive");
    }
    public void setSlots(ArrayList<Slot> slots)               { this.slots = slots; }
    public void setTransactions(ArrayList<Transaction> t)     { this.transactions = t; }
    public void setUsers(ArrayList<User> users)               { this.users = users; }

    // ====== Helpers ======
    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    private boolean requirePermission(String action) {
        if (!isUserLoggedIn()) {
            System.out.println("Access denied: no user logged in.");
            return false;
        }
        if (!getLoggedInUser().can(action)) {
            System.out.println("Access denied: you do not have permission to " + action);
            return false;
        }
        return true;
    }
}