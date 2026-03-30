package controller;

import java.util.Scanner;
import java.io.Console;
import other.Product;
import user.Customer;
import user.User;
import exceptions.*;

public class vmMain {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        VendingMachine vm = null;

        try {
            vm = new VendingMachine("Main Lobby", 12);
        } catch (Exception e) {
            System.out.println("Failed to initialize vending machine: " + e.getMessage());
            sc.close();
            return;
        }

        int choice = -1;
        do {
            try {
                if (!vm.isUserLoggedIn()) {
                    printMainMenu();
                    choice = readInt(sc, "Choose: ", 0, 2);

                    switch (choice) {
                        case 1: registerUser(vm, sc); break;
                        case 2: loginUser(vm, sc);    break;
                        case 0: System.out.println("Goodbye!"); break;
                    }

                } else {
                    printUserMenu(vm);
                    int max = countOptions(vm.getLoggedInUser());
                    choice = readInt(sc, "Choose: ", 0, max);
                    if (choice == 0) {
                        System.out.println("Goodbye!");
                    } else {
                        handleUserChoice(vm, sc, choice);
                    }
                }
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
                choice = -1;
            }

        } while (choice != 0);

        sc.close();
    }

    // =========================================================
    // LOGIN  — stays in the login loop until success or user
    //          types "back" to return to the main menu
    // =========================================================
    private static void loginUser(VendingMachine vm, Scanner sc) {
        System.out.println("\n=== Login === (type 'back' to cancel)");
        while (true) {
            System.out.print("Username: ");
            String username = sc.nextLine().trim();
            if (username.equalsIgnoreCase("back")) return;
            if (username.isEmpty()) {
                System.out.println("Username cannot be empty. Please try again.");
                continue;
            }

            String password = readPassword(sc, "Password: ");
            if (password.equalsIgnoreCase("back")) return;
            if (password.isEmpty()) {
                System.out.println("Password cannot be empty. Please try again.");
                continue;
            }

            try {
                vm.login(username, password);
                return; // success
            } catch (AuthenticationException e) {
                System.out.println("Login failed: " + e.getMessage() + ". Please try again.");
                // stays in the loop — does NOT go back to main menu
            }
        }
    }

    // =========================================================
    // REGISTRATION — field-by-field, each field loops until valid
    // =========================================================
    private static void registerUser(VendingMachine vm, Scanner sc) {
        System.out.println("\n=== User Registration ===");

        // Full Name — letters and spaces only
        String fullName;
        while (true) {
            System.out.print("Full Name: ");
            fullName = sc.nextLine().trim();
            if (fullName.isEmpty()) {
                System.out.println("Full name cannot be empty. Please try again.");
                continue;
            }
            if (!fullName.matches("[A-Za-z ]+")) {
                System.out.println("Full name must contain letters only (no numbers or symbols). Please try again.");
                continue;
            }
            break;
        }

        // Phone — digits only, 8-15 chars, unique
        String phone;
        while (true) {
            System.out.print("Phone Number: ");
            phone = sc.nextLine().trim();
            if (phone.isEmpty()) {
                System.out.println("Phone number cannot be empty. Please try again.");
                continue;
            }
            if (!phone.matches("\\d+")) {
                System.out.println("Phone number must contain digits only (no letters or spaces). Please try again.");
                continue;
            }
            if (phone.length() < 8 || phone.length() > 15) {
                System.out.println("Phone number must be 8 to 15 digits. Please try again.");
                continue;
            }
            if (isPhoneTaken(vm, phone)) {
                System.out.println("Phone number already registered. Please use a different number.");
                continue;
            }
            break;
        }

        // Username — 3-50 chars, unique
        String username;
        while (true) {
            System.out.print("Username: ");
            username = sc.nextLine().trim();
            if (username.isEmpty()) {
                System.out.println("Username cannot be empty. Please try again.");
                continue;
            }
            if (username.length() < 3) {
                System.out.println("Username must be at least 3 characters. Please try again.");
                continue;
            }
            if (username.length() > 50) {
                System.out.println("Username must be at most 50 characters. Please try again.");
                continue;
            }
            if (isUsernameTaken(vm, username)) {
                System.out.println("Username '" + username + "' is already taken. Please choose another.");
                continue;
            }
            break;
        }

        // Password — hidden, min 4 chars, confirmed
        String password;
        while (true) {
            password = readPassword(sc, "Password: ");
            if (password.length() < 4) {
                System.out.println("Password must be at least 4 characters. Please try again.");
                continue;
            }
            if (password.length() > 100) {
                System.out.println("Password too long (max 100 characters). Please try again.");
                continue;
            }
            String confirm = readPassword(sc, "Confirm Password: ");
            if (!password.equals(confirm)) {
                System.out.println("Passwords do not match. Please try again.");
                continue;
            }
            break;
        }

        // Email — regex validated, unique
        String email;
        while (true) {
            System.out.print("Email: ");
            email = sc.nextLine().trim();
            if (email.isEmpty()) {
                System.out.println("Email cannot be empty. Please try again.");
                continue;
            }
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                System.out.println("Invalid email format (e.g. user@example.com). Please try again.");
                continue;
            }
            if (email.length() > 100) {
                System.out.println("Email too long (max 100 characters). Please try again.");
                continue;
            }
            if (isEmailTaken(vm, email)) {
                System.out.println("Email '" + email + "' is already registered. Please use a different one.");
                continue;
            }
            break;
        }

        // Initial Balance — non-negative, optional (Enter = 0)
        double balance = 0.0;
        while (true) {
            System.out.print("Initial Balance (press Enter for $0.00): ");
            String input = sc.nextLine().trim();
            if (input.isEmpty()) { balance = 0.0; break; }
            try {
                balance = Double.parseDouble(input);
                if (balance < 0) {
                    System.out.println("Balance cannot be negative. Please try again.");
                    continue;
                }
                if (balance > 1_000_000) {
                    System.out.println("Balance too high (max $1,000,000). Please try again.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount. Please enter a valid number (e.g. 50.00).");
            }
        }

        // Create and add
        try {
            String userId = "CUST" + (System.currentTimeMillis() % 100000);
            Customer c = new Customer(userId, fullName, phone, username, password, email);
            c.setBalance(balance);
            vm.addUser(c);
            System.out.println("Registration successful! Your ID: " + userId);
            System.out.println("You can now log in with your username and password.");
        } catch (Exception e) {
            System.out.println("Registration error: " + e.getMessage());
        }
    }

    // =========================================================
    // USER MENU HANDLER
    // =========================================================
    private static void handleUserChoice(VendingMachine vm, Scanner sc, int choice) {
        User user = vm.getLoggedInUser();
        int opt = 1;

        try {

            // --- View Products ---
            if (user.can(VendingMachine.VIEW_MENU)) {
                if (choice == opt) { vm.printMenu(); return; }
                opt++;
            }

            // --- Purchase ---
            if (user.can(VendingMachine.PURCHASE)) {
                if (choice == opt) {
                    vm.printMenu();

                    // Slot ID — must exist
                    String slotId;
                    while (true) {
                        System.out.print("Enter Slot ID: ");
                        slotId = sc.nextLine().trim();
                        if (slotId.isEmpty()) {
                            System.out.println("Slot ID cannot be empty. Please try again.");
                            continue;
                        }
                        if (vm.findSlot(slotId) == null) {
                            System.out.println("Slot '" + slotId + "' does not exist. Please enter a valid Slot ID.");
                            continue;
                        }
                        break;
                    }

                    // Quantity — default 1, must not exceed stock
                    int available = vm.findSlot(slotId).getQuantity();
                    if (available == 0) {
                        System.out.println("This item is out of stock.");
                        return;
                    }
                    int quantity;
                    while (true) {
                        System.out.print("Quantity (press Enter for 1): ");
                        String q = sc.nextLine().trim();
                        if (q.isEmpty()) { quantity = 1; }
                        else if (!q.matches("\\d+")) {
                            System.out.println("Quantity must be a positive integer. Please try again.");
                            continue;
                        } else { quantity = Integer.parseInt(q); }

                        if (quantity <= 0) {
                            System.out.println("Quantity must be greater than 0. Please try again.");
                            continue;
                        }
                        if (quantity > available) {
                            System.out.println("Not enough stock. Only " + available + " available. Please try again.");
                            continue;
                        }
                        break;
                    }

                    try {
                        Customer proxy = new Customer(user.getUserId(), user.getFullName(),
                                "0000000000", user.getUsername(), "temp", "temp@email.com");
                        proxy.setBalance(user.getBalance());
                        proxy.setPremium(user.isPremium());
                        proxy.setItemsBought(user.getItemsBought());

                        boolean ok = vm.vendBulk(slotId, proxy, quantity);
                        if (ok) {
                            user.setBalance(proxy.getBalance());
                            user.setItemsBought(proxy.getItemsBought());
                            System.out.printf("Purchase successful! x%d item(s). New balance: $%.2f%n",
                                    quantity, user.getBalance());
                        } else {
                            System.out.println("Purchase failed. Please check your balance.");
                        }
                    } catch (InsufficientFundsException e) {
                        System.out.printf("Purchase failed: %s (your balance: $%.2f)%n",
                                e.getMessage(), e.getAvailableBalance());
                    } catch (VendingMachineException e) {
                        System.out.println("Purchase failed: " + e.getMessage());
                    }
                    return;
                }
                opt++;
            }

            // --- Restock ---
            if (user.can(VendingMachine.RESTOCK)) {
                if (choice == opt) {
                    vm.printInventory();
                    String slotId;
                    while (true) {
                        System.out.print("Slot ID to restock: ");
                        slotId = sc.nextLine().trim();
                        if (slotId.isEmpty()) {
                            System.out.println("Slot ID cannot be empty. Please try again.");
                            continue;
                        }
                        if (vm.findSlot(slotId) == null) {
                            System.out.println("Slot '" + slotId + "' does not exist. Please enter a valid Slot ID.");
                            continue;
                        }
                        break;
                    }
                    int amount = readPositiveInt(sc, "Amount to add: ");
                    try {
                        vm.restock(slotId, amount);
                    } catch (VendingMachineException e) {
                        System.out.println("Restock failed: " + e.getMessage());
                    }
                    return;
                }
                opt++;
            }

            // --- Add Slot ---
            if (user.can(VendingMachine.ADD_SLOT)) {
                if (choice == opt) {
                    addSlotFlow(vm, sc);
                    return;
                }
                opt++;
            }

            // --- Remove Slot ---
            if (user.can(VendingMachine.REMOVE_SLOT)) {
                if (choice == opt) {
                    removeSlotFlow(vm, sc);
                    return;
                }
                opt++;
            }

            // --- Change Product in Slot ---
            if (user.can(VendingMachine.CHANGE_PRODUCT)) {
                if (choice == opt) {
                    changeProductFlow(vm, sc);
                    return;
                }
                opt++;
            }

            // --- View Revenue ---
            if (user.can(VendingMachine.VIEW_REVENUE)) {
                if (choice == opt) {
                    System.out.printf("Total Revenue: $%.2f%n", vm.getRevenue());
                    return;
                }
                opt++;
            }

            // --- View Transactions ---
            if (user.can(VendingMachine.VIEW_TRANSACTIONS)) {
                if (choice == opt) { vm.printTransactions(); return; }
                opt++;
            }

            // --- View Inventory ---
            if (user.can(VendingMachine.VIEW_INVENTORY)) {
                if (choice == opt) { vm.printInventory(); return; }
                opt++;
            }

            // --- View Balance ---
            if (user.can(VendingMachine.VIEW_BALANCE)) {
                if (choice == opt) {
                    System.out.printf("Current Balance: $%.2f%n", user.getBalance());
                    return;
                }
                opt++;
            }

            // --- Top Up ---
            if (user.can(VendingMachine.TOP_UP)) {
                if (choice == opt) {
                    double amount;
                    while (true) {
                        System.out.print("Amount to top up: $");
                        String a = sc.nextLine().trim();
                        try {
                            amount = Double.parseDouble(a);
                            if (amount <= 0) {
                                System.out.println("Amount must be positive. Please try again.");
                                continue;
                            }
                            if (user.getBalance() + amount > 1_000_000) {
                                System.out.println("Balance would exceed maximum ($1,000,000). Please enter a smaller amount.");
                                continue;
                            }
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid amount. Please enter a valid number (e.g. 20.00).");
                        }
                    }
                    user.setBalance(user.getBalance() + amount);
                    System.out.printf("Balance updated: $%.2f%n", user.getBalance());
                    return;
                }
                opt++;
            }

            // --- Logout ---
            if (choice == opt) {
                vm.logout();
                return;
            }

            System.out.println("Invalid choice. Please select a valid option.");

        } catch (InvalidInputException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    // =========================================================
    // ADD SLOT FLOW
    // =========================================================
    private static void addSlotFlow(VendingMachine vm, Scanner sc) {
        System.out.println("\n--- Add New Slot ---");

        // Slot ID — must not already exist
        String slotId;
        while (true) {
            System.out.print("New Slot ID (e.g. A1, B3): ");
            slotId = sc.nextLine().trim().toUpperCase();
            if (slotId.isEmpty()) {
                System.out.println("Slot ID cannot be empty. Please try again.");
                continue;
            }
            if (vm.findSlot(slotId) != null) {
                System.out.println("Slot '" + slotId + "' already exists. Please use a different ID.");
                continue;
            }
            break;
        }

        Product product = readProductDetails(sc);
        int qty = readPositiveInt(sc, "Initial Quantity: ");

        try {
            vm.addSlot(slotId, product, qty);
        } catch (VendingMachineException e) {
            System.out.println("Failed to add slot: " + e.getMessage());
        }
    }

    // =========================================================
    // REMOVE SLOT FLOW
    // =========================================================
    private static void removeSlotFlow(VendingMachine vm, Scanner sc) {
        System.out.println("\n--- Remove Slot ---");
        vm.printInventory();

        if (vm.getSlots().isEmpty()) return;

        String slotId;
        while (true) {
            System.out.print("Slot ID to remove: ");
            slotId = sc.nextLine().trim().toUpperCase();
            if (slotId.isEmpty()) {
                System.out.println("Slot ID cannot be empty. Please try again.");
                continue;
            }
            if (vm.findSlot(slotId) == null) {
                System.out.println("Slot '" + slotId + "' does not exist. Please enter a valid Slot ID.");
                continue;
            }
            break;
        }

        // Confirm
        while (true) {
            System.out.print("Are you sure you want to remove slot " + slotId + "? (yes/no): ");
            String confirm = sc.nextLine().trim().toLowerCase();
            if (confirm.equals("yes")) {
                try {
                    vm.removeSlot(slotId);
                } catch (VendingMachineException e) {
                    System.out.println("Failed to remove slot: " + e.getMessage());
                }
                return;
            } else if (confirm.equals("no")) {
                System.out.println("Removal cancelled.");
                return;
            } else {
                System.out.println("Please type 'yes' or 'no'.");
            }
        }
    }

    // =========================================================
    // CHANGE PRODUCT FLOW
    // =========================================================
    private static void changeProductFlow(VendingMachine vm, Scanner sc) {
        System.out.println("\n--- Change Product in Slot ---");
        vm.printInventory();

        if (vm.getSlots().isEmpty()) return;

        // Pick the slot to change
        String slotId;
        while (true) {
            System.out.print("Slot ID to change product: ");
            slotId = sc.nextLine().trim().toUpperCase();
            if (slotId.isEmpty()) {
                System.out.println("Slot ID cannot be empty. Please try again.");
                continue;
            }
            if (vm.findSlot(slotId) == null) {
                System.out.println("Slot '" + slotId + "' does not exist. Please enter a valid Slot ID.");
                continue;
            }
            break;
        }

        System.out.println("Current product: " + vm.findSlot(slotId).getProduct().getName()
                + "  (qty: " + vm.findSlot(slotId).getQuantity() + ")");
        System.out.println("Enter new product details:");

        Product newProduct = readProductDetails(sc);
        int newQty = readPositiveInt(sc, "New Quantity: ");

        try {
            vm.changeProduct(slotId, newProduct, newQty);
        } catch (VendingMachineException e) {
            System.out.println("Failed to change product: " + e.getMessage());
        }
    }

    // =========================================================
    // MENU PRINTING
    // =========================================================
    private static void printMainMenu() {
        System.out.println("\n=== VENDING MACHINE ===");
        System.out.println("1) Register");
        System.out.println("2) Login");
        System.out.println("0) Exit");
    }

    private static void printUserMenu(VendingMachine vm) {
        User user = vm.getLoggedInUser();
        System.out.println("\n=== USER MENU === Welcome, " + user.getFullName()
                + " (" + user.getUsername() + ")");
        int opt = 1;
        if (user.can(VendingMachine.VIEW_MENU))         System.out.println(opt++ + ") View Products");
        if (user.can(VendingMachine.PURCHASE))           System.out.println(opt++ + ") Purchase Product");
        if (user.can(VendingMachine.RESTOCK))            System.out.println(opt++ + ") Restock Products");
        if (user.can(VendingMachine.ADD_SLOT))           System.out.println(opt++ + ") Add Slot");
        if (user.can(VendingMachine.REMOVE_SLOT))        System.out.println(opt++ + ") Remove Slot");
        if (user.can(VendingMachine.CHANGE_PRODUCT))     System.out.println(opt++ + ") Change Product in Slot");
        if (user.can(VendingMachine.VIEW_REVENUE))       System.out.println(opt++ + ") View Revenue");
        if (user.can(VendingMachine.VIEW_TRANSACTIONS))  System.out.println(opt++ + ") View Transactions");
        if (user.can(VendingMachine.VIEW_INVENTORY))     System.out.println(opt++ + ") View Inventory");
        if (user.can(VendingMachine.VIEW_BALANCE))       System.out.println(opt++ + ") View Balance");
        if (user.can(VendingMachine.TOP_UP))             System.out.println(opt++ + ") Top Up Balance");
        System.out.println(opt + ") Logout");
        System.out.println("0) Exit");
    }

    private static int countOptions(User user) {
        int c = 0;
        if (user.can(VendingMachine.VIEW_MENU))         c++;
        if (user.can(VendingMachine.PURCHASE))           c++;
        if (user.can(VendingMachine.RESTOCK))            c++;
        if (user.can(VendingMachine.ADD_SLOT))           c++;
        if (user.can(VendingMachine.REMOVE_SLOT))        c++;
        if (user.can(VendingMachine.CHANGE_PRODUCT))     c++;
        if (user.can(VendingMachine.VIEW_REVENUE))       c++;
        if (user.can(VendingMachine.VIEW_TRANSACTIONS))  c++;
        if (user.can(VendingMachine.VIEW_INVENTORY))     c++;
        if (user.can(VendingMachine.VIEW_BALANCE))       c++;
        if (user.can(VendingMachine.TOP_UP))             c++;
        c++; // logout
        return c;
    }

    // =========================================================
    // SHARED INPUT HELPERS
    // =========================================================

    /** Read a product name, category, and price — all with retry loops. */
    private static Product readProductDetails(Scanner sc) {
        String name;
        while (true) {
            System.out.print("Product Name: ");
            name = sc.nextLine().trim();
            if (name.isEmpty()) { System.out.println("Product name cannot be empty. Please try again."); continue; }
            break;
        }

        String category;
        while (true) {
            System.out.print("Category: ");
            category = sc.nextLine().trim();
            if (category.isEmpty()) { System.out.println("Category cannot be empty. Please try again."); continue; }
            break;
        }

        double price;
        while (true) {
            System.out.print("Price: $");
            String p = sc.nextLine().trim();
            try {
                price = Double.parseDouble(p);
                if (price < 0) { System.out.println("Price cannot be negative. Please try again."); continue; }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid price. Please enter a valid number (e.g. 1.50).");
            }
        }

        return new Product(name, category, price);
    }

    /** Read a positive integer with a custom prompt, retrying on bad input. */
    private static int readPositiveInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (!input.matches("\\d+")) {
                System.out.println("Please enter a positive integer. Try again.");
                continue;
            }
            int val = Integer.parseInt(input);
            if (val <= 0) {
                System.out.println("Value must be greater than 0. Try again.");
                continue;
            }
            return val;
        }
    }

    /**
     * Read an integer in [0, max] range, reprompting on bad or out-of-range input.
     * 0 is always allowed as exit.
     */
    private static int readInt(Scanner sc, String prompt, int min, int max) {
        System.out.print(prompt);
        while (true) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) {
                System.out.print("Please enter a number (" + min + "-" + max + "): ");
                continue;
            }
            try {
                int val = Integer.parseInt(line);
                if (val == 0 || (val >= min && val <= max)) return val;
                System.out.print("Invalid choice. Please enter a number between " + min + " and " + max + ": ");
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number (" + min + "-" + max + "): ");
            }
        }
    }

    /** Read password without echoing. Falls back to visible input in IDEs. */
    private static String readPassword(Scanner sc, String prompt) {
        Console console = System.console();
        if (console != null) {
            char[] pwd = console.readPassword(prompt);
            return pwd != null ? new String(pwd) : "";
        } else {
            System.out.print(prompt);
            return sc.nextLine();
        }
    }

    // =========================================================
    // UNIQUENESS CHECKS
    // =========================================================
    private static boolean isUsernameTaken(VendingMachine vm, String username) {
        for (User u : vm.getUsers())
            if (u.getUsername().equalsIgnoreCase(username)) return true;
        return false;
    }

    private static boolean isPhoneTaken(VendingMachine vm, String phone) {
        for (User u : vm.getUsers())
            if (u.getPhone().equals(phone)) return true;
        return false;
    }

    private static boolean isEmailTaken(VendingMachine vm, String email) {
        for (User u : vm.getUsers()) {
            if (u instanceof Customer) {
                if (((Customer) u).getEmail().equalsIgnoreCase(email)) return true;
            }
        }
        return false;
    }
}