import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class VendingMachine {
    
    // Step 2: Action Constants
    public static final String PURCHASE = "PURCHASE";
    public static final String VIEW_MENU = "VIEW_MENU";
    public static final String RESTOCK = "RESTOCK";
    public static final String VIEW_REVENUE = "VIEW_REVENUE";
    public static final String MANAGE_PRODUCTS = "MANAGE_PRODUCTS";
    public static final String VIEW_TRANSACTIONS = "VIEW_TRANSACTIONS";
    public static final String POWER_CONTROL = "POWER_CONTROL";
    public static final String VIEW_BALANCE = "VIEW_BALANCE";
    public static final String TOP_UP = "TOP_UP";
    public static final String REDEEM_POINTS = "REDEEM_POINTS";
    public static final String VIEW_INVENTORY = "VIEW_INVENTORY";
    
    private String location;
    private ArrayList<Slot> slots;
    private double revenue;
    private boolean isOn;
    private ArrayList<Transaction> transactions;
    private ArrayList<IUser> users;
    private IUser loggedInUser;
    
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
    
    public void power(boolean on) {
        if (!requirePermission(POWER_CONTROL)) return;
        this.isOn = on;
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
    
    public void addUser(IUser user) {
        users.add(user);
    }
    
    public boolean login(String username, String password) {
        for (IUser user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
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
    
    public IUser getLoggedInUser() {
        return loggedInUser;
    }
    
    public boolean isUserLoggedIn() {
        return loggedInUser != null;
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
    
    public ArrayList<IUser> getUsers() {
        return users;
    }
    
    // LinkedList demonstration - Proof D: add duplicate and show size increases
    public void demonstrateLinkedList() {
        System.out.println("\n=== LinkedList Demonstration (Allows Duplicates) ===");
        LinkedList<String> productCategories = new LinkedList<>();
        
        // Add categories
        productCategories.add("Snack");
        productCategories.add("Drink");
        productCategories.add("Dessert");
        System.out.println("After adding 3 categories: " + productCategories);
        System.out.println("Size: " + productCategories.size());
        
        // Add duplicate (LinkedList allows duplicates)
        boolean added = productCategories.add("Snack"); // duplicate
        System.out.println("Adding duplicate 'Snack': " + (added ? "Success" : "Failed"));
        System.out.println("Size after duplicate: " + productCategories.size());
        System.out.println("Final categories: " + productCategories);
        
        // LinkedList specific operations
        System.out.println("First element: " + productCategories.getFirst());
        System.out.println("Last element: " + productCategories.getLast());
        
        // Add to beginning and end
        productCategories.addFirst("Starter");
        productCategories.addLast("Finisher");
        System.out.println("After addFirst/addLast: " + productCategories);
        
        // Remove first and last
        productCategories.removeFirst();
        productCategories.removeLast();
        System.out.println("After removeFirst/removeLast: " + productCategories);
        
        // Loop through LinkedList
        System.out.println("Looping through categories:");
        for (String category : productCategories) {
            System.out.println("  - " + category);
        }
        
        // Get by index
        System.out.println("Element at index 1: " + productCategories.get(1));
    }
    
    // HashMap demonstration - Proof E: put + get + update + remove + loop entries
    public void demonstrateHashMap() {
        System.out.println("\n=== HashMap Demonstration (Key-Value Operations) ===");
        Map<String, Double> productPrices = new HashMap<>();
        
        // Put operations
        productPrices.put("Chips", 1.50);
        productPrices.put("Soda", 2.00);
        productPrices.put("Candy", 1.00);
        System.out.println("After put operations: " + productPrices);
        
        // Get operation
        Double chipsPrice = productPrices.get("Chips");
        System.out.println("Get 'Chips' price: $" + chipsPrice);
        
        // Update operation
        productPrices.put("Chips", 1.75); // update existing
        System.out.println("After updating 'Chips' price: " + productPrices);
        
        // Remove operation
        Double removedPrice = productPrices.remove("Candy");
        System.out.println("Removed 'Candy' with price: $" + removedPrice);
        System.out.println("After remove: " + productPrices);
        
        // Loop through entries
        System.out.println("Looping through entries:");
        for (Map.Entry<String, Double> entry : productPrices.entrySet()) {
            System.out.println("  " + entry.getKey() + ": $" + entry.getValue());
        }
        
        // Demonstrate containsKey and containsValue
        System.out.println("Contains key 'Soda': " + productPrices.containsKey("Soda"));
        System.out.println("Contains value $2.00: " + productPrices.containsValue(2.00));
    }
    
    // ArrayList operations demonstration
    public void demonstrateArrayListOperations() {
        System.out.println("\n=== ArrayList Operations Demonstration ===");
        ArrayList<String> operations = new ArrayList<>();
        
        // Add operations
        operations.add(PURCHASE);
        operations.add(VIEW_MENU);
        operations.add(RESTOCK);
        System.out.println("After add: " + operations);
        
        // Get operation
        String firstOp = operations.get(0);
        System.out.println("Get index 0: " + firstOp);
        
        // Set operation (update)
        operations.set(1, "VIEW_CATALOG");
        System.out.println("After set index 1: " + operations);
        
        // Remove operation
        operations.remove(RESTOCK);
        System.out.println("After remove '" + RESTOCK + "': " + operations);
        
        // Loop with for-each
        System.out.println("Loop with for-each:");
        for (String op : operations) {
            System.out.println("  " + op);
        }
        
        // Loop with traditional for
        System.out.println("Loop with traditional for:");
        for (int i = 0; i < operations.size(); i++) {
            System.out.println("  Index " + i + ": " + operations.get(i));
        }
    }
}
