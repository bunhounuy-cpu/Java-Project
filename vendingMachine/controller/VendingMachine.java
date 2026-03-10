package controller;

import java.util.ArrayList;
import other.Slot;
import other.Transaction;
import other.Product;
import other.PaymentService;
import user.Customer;
import user.Manager;
import user.User;
import user.Restocker;

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
    public static final String REDEEM_POINTS = "REDEEM_POINTS";
    
    private String location;
    private ArrayList<Slot> slots;
    private double revenue;
    private ArrayList<Transaction> transactions;
    private ArrayList<User> users;
    private User loggedInUser;
    
    private static int machineCount = 0;
    
    // ====== Constructor ======
    public VendingMachine(String location, int capacity) {
        machineCount++;
        setLocation(location);
        this.slots = new ArrayList<>();
        this.revenue = 0.0;
        this.transactions = new ArrayList<>();
        this.users = new ArrayList<>();
        this.loggedInUser = null;
        seedDefaultAdmin();
        
        // Initialize slots and default users
        seedDefaultSlots();
    }
    
    // =========================
    // DEFAULT SLOTS (BOOTSTRAP)
    // =========================
    private void seedDefaultSlots() {
        addSlot("A1", new Product("Chips", "Snack", 1.50), 5);
        addSlot("A2", new Product("Candy", "Snack", 1.00), 5);
        addSlot("B1", new Product("Soda", "Drink", 2.00), 5);
        addSlot("B2", new Product("Water", "Drink", 1.25), 6);
        addSlot("C1", new Product("Gum", "Snack", 0.75), 10);
        addSlot("D1", new Product("Juice", "Drink", 2.50), 4);
    }
    
    // =========================
    // DEFAULT USERS (BOOTSTRAP)
    // =========================
    private void seedDefaultAdmin() {
        User adminUser = new Customer("M001", "System Admin", "admin123", "admin", "admin123");
        Manager admin = new Manager(adminUser, 5000.0f);
        users.add(admin);
    }
    
    public static int getMachineCount() {
        return machineCount;
    }
    
    public double getRevenue() {
        if (!requirePermission(VIEW_REVENUE)) return 0.0;
        return revenue;
    }
    
    public void addSlot(String slotID, Product product, int quantity) {
        Slot s = new Slot(slotID, product, quantity);
        slots.add(s);
    }
    
    public void restock(String slotID, int amount) {
        if (!requirePermission(RESTOCK)) return;
        Slot s = findSlot(slotID);
        if (s != null && amount > 0) {
            s.addQuantity(amount);
        }
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
    
    public boolean vend(String slotID, Customer customer) {
        if (!requirePermission(PURCHASE)) return false;
        
        Slot s = findSlot(slotID);
        if (s == null || s.getQuantity() <= 0) {
            return false;
        }
        
        double priceToPay = PaymentService.computeWithLoyalty(s.getProduct().getPrice(), customer.isPremium(), customer.getItemsBought());
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
    
    public boolean login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.checkPassword(password)) {
                loggedInUser = user;
                System.out.println("Login success as " + user.getRole());
                return true;
            }
        }
        System.out.println("Login failed: Invalid username or password");
        return false;
    }
    
    public void logout() {
        if (loggedInUser != null) {
            System.out.println("Logged out as " + loggedInUser.getRole());
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
    public void setLocation(String location) {
        if (!isBlank(location)) {
            this.location = location.trim();
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
            System.out.println("Access denied: " + user.getRole() + " cannot perform " + action);
            return false;
        }
        return true;
    }
}
