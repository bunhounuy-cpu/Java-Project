package controller;

import java.util.Scanner;
import java.io.Console;
import user.Customer;
import user.User;
import exceptions.*;

public class vmMain {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        VendingMachine vm = null;
        int choice = -1;

        try {
            vm = new VendingMachine("Main Lobby", 12);
        } catch (Exception e) {
            System.out.println("Failed to initialize vending machine: " + e.getMessage());
            sc.close();
            return;
        }

        do {
            try {
                if (!vm.isUserLoggedIn()) {
                    printMainMenu();
                    System.out.print("Choose: ");
                    choice = readMenuChoice(sc, 0, 2);

                    switch (choice) {
                        case 1:
                            registerUser(vm, sc);
                            break;
                        case 2:
                            loginUser(vm, sc);
                            break;
                        case 0:
                            System.out.println("Goodbye!");
                            break;
                        default:
                            System.out.println("Invalid option. Please choose 0, 1, or 2.");
                            break;
                    }

                } else {
                    printUserMenu(vm);
                    int maxOption = countUserOptions(vm.getLoggedInUser());
                    System.out.print("Choose: ");
                    choice = readMenuChoice(sc, 0, maxOption);
                    handleUserChoice(vm, sc, choice);
                }

            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
                choice = -1;
            }

        } while (choice != 0);

        sc.close();
    }

    // =========================================================
    // LOGIN
    // =========================================================
    private static void loginUser(VendingMachine vm, Scanner sc) {
        System.out.println("\n=== Login ===");

        System.out.print("Username: ");
        String username = sc.nextLine();

        String password = readPassword(sc, "Password: ");

        try {
            vm.login(username, password);
        } catch (AuthenticationException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    // =========================================================
    // REGISTRATION  – field-by-field, retry on every bad input
    // =========================================================
    private static void registerUser(VendingMachine vm, Scanner sc) {
        System.out.println("\n=== User Registration ===");

        // ---- Full Name (letters + spaces only) ----
        String fullName;
        while (true) {
            System.out.print("Enter Full Name: ");
            fullName = sc.nextLine().trim();
            if (fullName.isEmpty()) {
                System.out.println("  Full name cannot be empty. Please try again.");
                continue;
            }
            if (!fullName.matches("[A-Za-z ]+")) {
                System.out.println("  Full name must contain letters only (no numbers or special characters). Please try again.");
                continue;
            }
            break;
        }

        // ---- Phone (digits only, 8-15 chars, unique) ----
        String phone;
        while (true) {
            System.out.print("Enter Phone Number: ");
            phone = sc.nextLine().trim();
            if (phone.isEmpty()) {
                System.out.println("  Phone number cannot be empty. Please try again.");
                continue;
            }
            if (!phone.matches("\\d+")) {
                System.out.println("  Phone number must contain digits only (no letters or spaces). Please try again.");
                continue;
            }
            if (phone.length() < 8 || phone.length() > 15) {
                System.out.println("  Phone number must be 8–15 digits. Please try again.");
                continue;
            }
            if (isPhoneTaken(vm, phone)) {
                System.out.println("  Phone number already registered. Please use a different number.");
                continue;
            }
            break;
        }

        // ---- Username (3-50 chars, unique) ----
        String username;
        while (true) {
            System.out.print("Enter Username: ");
            username = sc.nextLine().trim();
            if (username.isEmpty()) {
                System.out.println("  Username cannot be empty. Please try again.");
                continue;
            }
            if (username.length() < 3) {
                System.out.println("  Username must be at least 3 characters. Please try again.");
                continue;
            }
            if (username.length() > 50) {
                System.out.println("  Username must be at most 50 characters. Please try again.");
                continue;
            }
            if (isUsernameTaken(vm, username)) {
                System.out.println("  Username '" + username + "' is already taken. Please choose a different one.");
                continue;
            }
            break;
        }

        // ---- Password (hidden, min 4 chars) ----
        String password;
        while (true) {
            password = readPassword(sc, "Enter Password: ");
            if (password.length() < 4) {
                System.out.println("  Password must be at least 4 characters. Please try again.");
                continue;
            }
            if (password.length() > 100) {
                System.out.println("  Password too long (max 100 characters). Please try again.");
                continue;
            }
            String confirm = readPassword(sc, "Confirm Password: ");
            if (!password.equals(confirm)) {
                System.out.println("  Passwords do not match. Please try again.");
                continue;
            }
            break;
        }

        // ---- Email (regex validated, unique) ----
        String email;
        while (true) {
            System.out.print("Enter Email: ");
            email = sc.nextLine().trim();
            if (email.isEmpty()) {
                System.out.println("  Email cannot be empty. Please try again.");
                continue;
            }
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                System.out.println("  Invalid email format (e.g. user@example.com). Please try again.");
                continue;
            }
            if (email.length() > 100) {
                System.out.println("  Email too long (max 100 characters). Please try again.");
                continue;
            }
            if (isEmailTaken(vm, email)) {
                System.out.println("  Email '" + email + "' is already registered. Please use a different email.");
                continue;
            }
            break;
        }

        // ---- Initial Balance (non-negative double) ----
        double balance = 0.0;
        while (true) {
            System.out.print("Initial Balance (press Enter for $0.00): ");
            String balanceInput = sc.nextLine().trim();
            if (balanceInput.isEmpty()) {
                balance = 0.0;
                break;
            }
            try {
                balance = Double.parseDouble(balanceInput);
                if (balance < 0) {
                    System.out.println("  Balance cannot be negative. Please try again.");
                    continue;
                }
                if (balance > 1_000_000) {
                    System.out.println("  Balance too high (max $1,000,000). Please try again.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("  Invalid amount. Please enter a valid number (e.g. 50.00).");
            }
        }

        // ---- Create & add user ----
        try {
            String userId = "CUST" + (System.currentTimeMillis() % 100000);
            Customer newCustomer = new Customer(userId, fullName, phone, username, password, email);
            newCustomer.setBalance(balance);
            vm.addUser(newCustomer);

            System.out.println("\n  Registration successful!");
            System.out.println("  Your User ID : " + userId);
            System.out.println("  You can now log in with your username and password.");
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
            // 1) View Products
            if (user.can(VendingMachine.VIEW_MENU)) {
                if (choice == opt) { vm.printMenu(); return; }
                opt++;
            }

            // 2) Purchase
            if (user.can(VendingMachine.PURCHASE)) {
                if (choice == opt) {
                    vm.printMenu();

                    // Slot ID – must exist in machine
                    String slotId;
                    while (true) {
                        System.out.print("Enter Slot ID: ");
                        slotId = sc.nextLine().trim();
                        if (slotId.isEmpty()) {
                            System.out.println("  Slot ID cannot be empty. Please try again.");
                            continue;
                        }
                        if (vm.findSlot(slotId) == null) {
                            System.out.println("  Slot '" + slotId + "' does not exist. Please enter a valid Slot ID.");
                            continue;
                        }
                        break;
                    }

                    // Quantity – positive integer, must not exceed available stock
                    int availableStock = vm.findSlot(slotId).getQuantity();
                    int quantity;
                    while (true) {
                        System.out.print("Enter Quantity: ");
                        String qInput = sc.nextLine().trim();
                        if (!qInput.matches("\\d+")) {
                            System.out.println("  Quantity must be a positive integer. Please try again.");
                            continue;
                        }
                        quantity = Integer.parseInt(qInput);
                        if (quantity <= 0) {
                            System.out.println("  Quantity must be greater than 0. Please try again.");
                            continue;
                        }
                        if (availableStock == 0) {
                            System.out.println("  This item is out of stock. Returning to menu.");
                            return;
                        }
                        if (quantity > availableStock) {
                            System.out.println("  Not enough stock. Only " + availableStock + " item(s) available. Please enter a valid quantity.");
                            continue;
                        }
                        break;
                    }

                    try {
                        Customer proxy = new Customer(
                            user.getUserId(), user.getFullName(),
                            "0000000000", user.getUsername(), "temp", "temp@email.com");
                        proxy.setBalance(user.getBalance());
                        proxy.setPremium(user.isPremium());
                        proxy.setItemsBought(user.getItemsBought());

                        boolean success = vm.vendBulk(slotId, proxy, quantity);
                        if (success) {
                            user.setBalance(proxy.getBalance());
                            user.setItemsBought(proxy.getItemsBought());
                            System.out.printf("  Purchased %d item(s)! New balance: $%.2f%n", quantity, user.getBalance());
                        } else {
                            System.out.println("  Purchase failed. Check balance or availability.");
                        }
                    } catch (ProductNotFoundException e) {
                        System.out.println("  " + e.getMessage());
                    } catch (InsufficientStockException e) {
                        System.out.println("  " + e.getMessage() + " (Available: " + e.getAvailableQuantity() + ")");
                    } catch (InsufficientFundsException e) {
                        System.out.printf("  %s (Your balance: $%.2f)%n", e.getMessage(), e.getAvailableBalance());
                    }
                    return;
                }
                opt++;
            }

            // 3) Restock
            if (user.can(VendingMachine.RESTOCK)) {
                if (choice == opt) {
                    // Slot ID
                    String slotId;
                    while (true) {
                        System.out.print("Enter Slot ID to restock: ");
                        slotId = sc.nextLine().trim();
                        if (slotId.isEmpty()) {
                            System.out.println("  Slot ID cannot be empty. Please try again.");
                            continue;
                        }
                        if (vm.findSlot(slotId) == null) {
                            System.out.println("  Slot '" + slotId + "' does not exist. Please enter a valid Slot ID.");
                            continue;
                        }
                        break;
                    }

                    // Amount
                    int amount;
                    while (true) {
                        System.out.print("Amount to add: ");
                        String aInput = sc.nextLine().trim();
                        if (!aInput.matches("\\d+")) {
                            System.out.println("  Amount must be a positive integer. Please try again.");
                            continue;
                        }
                        amount = Integer.parseInt(aInput);
                        if (amount <= 0) {
                            System.out.println("  Amount must be greater than 0. Please try again.");
                            continue;
                        }
                        break;
                    }

                    try {
                        vm.restock(slotId, amount);
                    } catch (ProductNotFoundException e) {
                        System.out.println("  Restock failed: " + e.getMessage());
                    }
                    return;
                }
                opt++;
            }

            // 4) View Revenue
            if (user.can(VendingMachine.VIEW_REVENUE)) {
                if (choice == opt) {
                    double rev = vm.getRevenue();
                    System.out.printf("Total Revenue: $%.2f%n", rev);
                    return;
                }
                opt++;
            }

            // 5) View Transactions
            if (user.can(VendingMachine.VIEW_TRANSACTIONS)) {
                if (choice == opt) { vm.printTransactions(); return; }
                opt++;
            }

            // 6) View Inventory
            if (user.can(VendingMachine.VIEW_INVENTORY)) {
                if (choice == opt) { vm.printInventory(); return; }
                opt++;
            }

            // 7) View Balance
            if (user.can(VendingMachine.VIEW_BALANCE)) {
                if (choice == opt) {
                    System.out.printf("Current Balance: $%.2f%n", user.getBalance());
                    return;
                }
                opt++;
            }

            // 8) Top Up
            if (user.can(VendingMachine.TOP_UP)) {
                if (choice == opt) {
                    double amount;
                    while (true) {
                        System.out.print("Enter amount to top up: $");
                        String aInput = sc.nextLine().trim();
                        try {
                            amount = Double.parseDouble(aInput);
                            if (amount <= 0) {
                                System.out.println("  Amount must be positive. Please try again.");
                                continue;
                            }
                            if (user.getBalance() + amount > 1_000_000) {
                                System.out.println("  Balance would exceed maximum ($1,000,000). Please enter a smaller amount.");
                                continue;
                            }
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("  Invalid amount. Please enter a valid number (e.g. 20.00).");
                        }
                    }
                    user.setBalance(user.getBalance() + amount);
                    System.out.printf("  Balance updated: $%.2f%n", user.getBalance());
                    return;
                }
                opt++;
            }

            // Logout
            if (choice == opt) {
                vm.logout();
                return;
            }

            // Exit
            if (choice == 0) {
                System.out.println("Goodbye!");
                return;
            }

            System.out.println("  Invalid choice. Please select a valid option.");

        } catch (InvalidInputException e) {
            System.out.println("  Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("  Unexpected error: " + e.getMessage());
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
        System.out.println("\n=== USER MENU ===");
        System.out.println("Welcome, " + user.getFullName() + " (" + user.getUsername() + ")");

        int opt = 1;
        if (user.can(VendingMachine.VIEW_MENU))         { System.out.println(opt++ + ") View Products"); }
        if (user.can(VendingMachine.PURCHASE))           { System.out.println(opt++ + ") Purchase Product"); }
        if (user.can(VendingMachine.RESTOCK))            { System.out.println(opt++ + ") Restock Products"); }
        if (user.can(VendingMachine.VIEW_REVENUE))       { System.out.println(opt++ + ") View Revenue"); }
        if (user.can(VendingMachine.VIEW_TRANSACTIONS))  { System.out.println(opt++ + ") View Transactions"); }
        if (user.can(VendingMachine.VIEW_INVENTORY))     { System.out.println(opt++ + ") View Inventory"); }
        if (user.can(VendingMachine.VIEW_BALANCE))       { System.out.println(opt++ + ") View Balance"); }
        if (user.can(VendingMachine.TOP_UP))             { System.out.println(opt++ + ") Top Up Balance"); }
        System.out.println(opt + ") Logout");
        System.out.println("0) Exit");
    }

    // =========================================================
    // HELPERS
    // =========================================================

    /** Count the maximum selectable option number in the user menu (logout = last). */
    private static int countUserOptions(User user) {
        int count = 0;
        if (user.can(VendingMachine.VIEW_MENU))         count++;
        if (user.can(VendingMachine.PURCHASE))           count++;
        if (user.can(VendingMachine.RESTOCK))            count++;
        if (user.can(VendingMachine.VIEW_REVENUE))       count++;
        if (user.can(VendingMachine.VIEW_TRANSACTIONS))  count++;
        if (user.can(VendingMachine.VIEW_INVENTORY))     count++;
        if (user.can(VendingMachine.VIEW_BALANCE))       count++;
        if (user.can(VendingMachine.TOP_UP))             count++;
        count++; // logout
        return count;
    }

    /**
     * Read an integer menu choice in [min, max] range, reprompting on invalid input.
     * 0 is always allowed as the exit/cancel option.
     */
    private static int readMenuChoice(Scanner sc, int min, int max) {
        while (true) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) {
                System.out.print("  Please enter a number (" + min + "–" + max + "): ");
                continue;
            }
            try {
                int val = Integer.parseInt(line);
                if (val == 0 || (val >= min && val <= max)) {
                    return val;
                }
                System.out.print("  Invalid choice. Please enter a number between " + min + " and " + max + ": ");
            } catch (NumberFormatException e) {
                System.out.print("  Invalid input. Please enter a number (" + min + "–" + max + "): ");
            }
        }
    }

    /**
     * Read a password without echoing it to the console.
     * Falls back to plain Scanner read if Console is not available (e.g. IDE).
     */
    private static String readPassword(Scanner sc, String prompt) {
        Console console = System.console();
        if (console != null) {
            char[] pwd = console.readPassword(prompt);
            return pwd != null ? new String(pwd) : "";
        } else {
            // IDE / piped environment – no Console available
            System.out.print(prompt + " (Note: password will be visible in IDE) ");
            return sc.nextLine();
        }
    }

    /** Check if a username is already registered in the vending machine. */
    private static boolean isUsernameTaken(VendingMachine vm, String username) {
        for (User u : vm.getUsers()) {
            if (u.getUsername().equalsIgnoreCase(username)) return true;
        }
        return false;
    }

    /** Check if a phone number is already registered. */
    private static boolean isPhoneTaken(VendingMachine vm, String phone) {
        for (User u : vm.getUsers()) {
            if (u.getPhone().equals(phone)) return true;
        }
        return false;
    }

    /** Check if an email is already registered (Customer only). */
    private static boolean isEmailTaken(VendingMachine vm, String email) {
        for (User u : vm.getUsers()) {
            if (u instanceof user.Customer) {
                user.Customer c = (user.Customer) u;
                if (c.getEmail().equalsIgnoreCase(email)) return true;
            }
        }
        return false;
    }
}
