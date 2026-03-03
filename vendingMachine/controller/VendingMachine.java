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
    public static final String VIEW_BALANCE = "VIEW_BALANCE";
    public static final String TOP_UP = "TOP_UP";
    public static final String REDEEM_POINTS = "REDEEM_POINTS";
    public static final String VIEW_INVENTORY = "VIEW_INVENTORY";
    
    private String location;
    private ArrayList<Slot> slots;
    private double revenue;
    private boolean isOn;
    private ArrayList<Transaction> transactions;
    private ArrayList<User> users;
    private User loggedInUser;
    
    private static int machineCount = 0;
    
    public VendingMachine(String location, int capacity) {
        this.location = location;
        this.slots = new ArrayList<>();
        this.revenue = 0.0;
        this.isOn = true;
        this.transactions = new ArrayList<>();
        this.users = new ArrayList<>();
        this.loggedInUser = null;
        machineCount++;
        
        // Default admin (so system can start)
        seedDefaultAdmin();
        
        // Initialize default products
        initializeDefaultProducts();
    }
    
    // =========================
    // DEFAULT PRODUCTS INITIALIZATION
    // =========================
    private void initializeDefaultProducts() {
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
        User adminUser = new User("M001", "System Admin", "admin123", "admin", "admin123");
        Manager admin = new Manager(adminUser, 5000.0f);
        users.add(admin);
    }
    
    public static int getMachineCount() {
        return machineCount;
    }
    
    public String getLocation() {
        return location;
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
            if (slots.get(i) != null && slots.get(i).getSlotID().equals(slotID)) {
                return slots.get(i);
            }
        }
        return null;
    }
    
    public void printMenu() {
        if (!requirePermission(VIEW_MENU)) return;
        System.out.println("=== " + location + " Menu ===");
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i) != null) {
                Slot s = slots.get(i);
                System.out.println(s.getSlotID() + " - " + s.getProduct().getName() + " [" + s.getProduct().getCategory() + "] $" + s.getProduct().getPrice() + " x" + s.getQuantity());
            }
        }
    }
    
    public boolean vend(String slotID, Customer customer) {
        // Customer class doesn't implement IUser, so no permission check needed
        if (!isOn) {
            return false;
        }
        Slot s = findSlot(slotID);
        if (s == null || s.getQuantity() <= 0 || customer == null || !customer.isCardActive()) {
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
        t.record(slotID, s.getProduct().getName(), priceToPay);
        transactions.add(t);
        return true;
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
        System.out.println("=== Transactions ===");
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i) != null) {
                System.out.println(transactions.get(i).toString());
            }
        }
    }
    
    public void addUser(User user) {
        users.add(user);
    }
    
    public boolean login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.checkPassword(password)) {
                loggedInUser = user;
                System.out.println("Login success as " + getRoleName(user));
                return true;
            }
        }
        System.out.println("Login failed: Invalid username or password");
        return false;
    }
    
    public void logout() {
        if (loggedInUser != null) {
            System.out.println("Logged out as " + getRoleName(loggedInUser));
            loggedInUser = null;
        }
    }
    
    public User getLoggedInUser() {
        return loggedInUser;
    }
    
    public boolean isUserLoggedIn() {
        return loggedInUser != null;
    }
    
    private String getRoleName(User user) {
        if (user instanceof Manager) {
            return "Manager";
        } else if (user instanceof Restocker) {
            return "Restocker";
        } else if (user instanceof Customer) {
            return "Customer";
        } else {
            return "Unknown";
        }
    }
    
    public boolean requirePermission(String action) {
        if (loggedInUser == null) {
            System.out.println("Access denied: No user logged in");
            return false;
        }
        if (!loggedInUser.can(action)) {
            System.out.println("Access denied: " + loggedInUser.getRole() + " cannot perform " + action);
            return false;
        }
        return true;
    }
    
    public ArrayList<User> getUsers() {
        return users;
    }
    
}
