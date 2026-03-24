package controller;

import java.util.Scanner;
import user.Customer;
import user.User;
import exceptions.*;

public class vmMain {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        VendingMachine vm = null;
        int choice;
        
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
                    choice = getValidIntInput(sc);

                    switch (choice) {
                        case 1: {
                            registerUser(vm, sc);
                            break;
                        }

                        case 2: {
                            System.out.print("Username: ");
                            String username = sc.nextLine();

                            System.out.print("Password: ");
                            String password = sc.nextLine();

                            try {
                                vm.login(username, password);
                            } catch (AuthenticationException e) {
                                System.out.println("Login failed: " + e.getMessage());
                            }
                            break;
                        }
                    }

                } else {
                    printUserMenu(vm);

                    System.out.print("Choose: ");
                    choice = getValidIntInput(sc);

                    handleUserChoice(vm, sc, choice);
                }
            } catch (InvalidInputException e) {
                System.out.println("Invalid input: " + e.getMessage());
                choice = -1; // Continue loop
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
                choice = -1; // Continue loop
            }

        } while (choice != 0);

        sc.close();
    }


    // ===== User Registration Method =====
    private static void registerUser(VendingMachine vm, Scanner sc) {
        System.out.println("\n=== User Registration ===");
        
        try {
            System.out.print("Enter Full Name: ");
            String fullName = sc.nextLine();
            if (fullName.trim().isEmpty()) {
                throw new InvalidInputException("Full name cannot be empty");
            }
            
            System.out.print("Enter Phone Number: ");
            String phone = sc.nextLine();
            if (phone.trim().isEmpty()) {
                throw new InvalidInputException("Phone number cannot be empty");
            }
            
            System.out.print("Enter Username: ");
            String username = sc.nextLine();
            if (username.trim().isEmpty()) {
                throw new InvalidInputException("Username cannot be empty");
            }
            
            System.out.print("Enter Password: ");
            String password = sc.nextLine();
            if (password.trim().isEmpty()) {
                throw new InvalidInputException("Password cannot be empty");
            }
            
            System.out.print("Enter Email: ");
            String email = sc.nextLine();
            if (email.trim().isEmpty()) {
                throw new InvalidInputException("Email cannot be empty");
            }
            
            System.out.print("Initial Balance (optional, press Enter for $0): ");
            String balanceInput = sc.nextLine();
            double balance = 0.0;
            if (!balanceInput.isEmpty()) {
                try {
                    balance = Double.parseDouble(balanceInput);
                    if (balance < 0) {
                        throw new InvalidInputException("Balance cannot be negative");
                    }
                } catch (NumberFormatException e) {
                    throw new InvalidInputException("Invalid balance format. Please enter a valid number.");
                }
            }
            
            // Generate unique user ID
            String userId = "CUST" + System.currentTimeMillis() % 10000;
            
            // Create new customer
            Customer newCustomer = new Customer(userId, fullName, phone, username, password, email);
            newCustomer.setBalance(balance);
            
            // Add user to vending machine
            vm.addUser(newCustomer);
            
            System.out.println("Registration successful!");
            System.out.println("Your User ID: " + userId);
            System.out.println("You can now login with your username and password.");
            
        } catch (InvalidInputException e) {
            System.out.println("Registration failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred during registration: " + e.getMessage());
        }
    }

    // ===== Handle dynamic user menu choices =====
    private static void handleUserChoice(VendingMachine vm, Scanner sc, int choice) {
        User user = vm.getLoggedInUser();
        int optionNumber = 1;
        
        try {
            if (user.can(VendingMachine.VIEW_MENU)) {
                if (choice == optionNumber) {
                    vm.printMenu();
                    return;
                }
                optionNumber++;
            }
            
            if (user.can(VendingMachine.PURCHASE)) {
                if (choice == optionNumber) {
                    vm.printMenu();
                    System.out.print("Enter Slot ID: ");
                    String slotId = sc.nextLine();
                    
                    System.out.print("Enter Quantity: ");
                    int quantity = getValidIntInput(sc);
                    
                    if (quantity <= 0) {
                        throw new InvalidInputException("Quantity must be positive");
                    }
                    
                    // Create a copy for purchase simulation
                    Customer purchaseCustomer = new Customer(user.getUserId(), user.getFullName(), "0000000000", user.getUsername(), "temp", "temp@email.com");
                    purchaseCustomer.setBalance(user.getBalance());
                    purchaseCustomer.setPremium(user.isPremium());
                    purchaseCustomer.setItemsBought(user.getItemsBought());
                    
                    try {
                        boolean success = vm.vendBulk(slotId, purchaseCustomer, quantity);
                        
                        if (success) {
                            // Update the actual user's balance and stats
                            user.setBalance(purchaseCustomer.getBalance());
                            user.setItemsBought(purchaseCustomer.getItemsBought());
                            System.out.println("Successfully purchased " + quantity + " item(s)! New balance: $" + user.getBalance());
                        } else {
                            System.out.println("Purchase failed! Check your balance or product availability.");
                        }
                    } catch (ProductNotFoundException e) {
                        System.out.println("Purchase failed: " + e.getMessage());
                    } catch (InsufficientStockException e) {
                        System.out.println("Purchase failed: " + e.getMessage() + " (Available: " + e.getAvailableQuantity() + ")");
                    } catch (InsufficientFundsException e) {
                        System.out.println("Purchase failed: " + e.getMessage() + " (Your balance: $" + e.getAvailableBalance() + ")");
                    }
                    return;
                }
                optionNumber++;
            }
            
            if (user.can(VendingMachine.RESTOCK)) {
                if (choice == optionNumber) {
                    System.out.print("Slot ID: ");
                    String slotId = sc.nextLine();
                    System.out.print("Amount to add: ");
                    int amount = getValidIntInput(sc);
                    
                    if (amount <= 0) {
                        throw new InvalidInputException("Amount must be positive");
                    }
                    
                    try {
                        vm.restock(slotId, amount);
                    } catch (ProductNotFoundException e) {
                        System.out.println("Restock failed: " + e.getMessage());
                    }
                    return;
                }
                optionNumber++;
            }
            
            if (user.can(VendingMachine.VIEW_REVENUE)) {
                if (choice == optionNumber) {
                    double revenue = vm.getRevenue();
                    if (revenue > 0) {
                        System.out.println("Total Revenue: $" + revenue);
                    } else {
                        System.out.println("No revenue recorded yet.");
                    }
                    return;
                }
                optionNumber++;
            }
            
            if (user.can(VendingMachine.VIEW_TRANSACTIONS)) {
                if (choice == optionNumber) {
                    vm.printTransactions();
                    return;
                }
                optionNumber++;
            }
            
            if (user.can(VendingMachine.VIEW_INVENTORY)) {
                if (choice == optionNumber) {
                    vm.printInventory();
                    return;
                }
                optionNumber++;
            }
            
            if (user.can(VendingMachine.VIEW_BALANCE)) {
                if (choice == optionNumber) {
                    System.out.println("Current Balance: $" + user.getBalance());
                    return;
                }
                optionNumber++;
            }
            
            if (user.can(VendingMachine.TOP_UP)) {
                if (choice == optionNumber) {
                    System.out.print("Enter amount to top up: $");
                    double amount = getValidDoubleInput(sc);
                    
                    if (amount <= 0) {
                        throw new InvalidInputException("Top-up amount must be positive");
                    }
                    
                    user.setBalance(user.getBalance() + amount);
                    System.out.println("Balance updated: $" + user.getBalance());
                    return;
                }
                optionNumber++;
            }
            
            // Logout option (always last before exit)
            if (choice == optionNumber) {
                vm.logout();
                return;
            }
            
            if (choice == 0) {
                System.out.println("Goodbye!");
                return;
            }
            
            System.out.println("Invalid choice.");
        } catch (InvalidInputException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    // ===== Menu printing methods =====
    private static void printMainMenu() {
        System.out.println("\n=== MAIN MENU (Not Logged In) ===");
        System.out.println("1) Register");
        System.out.println("2) User Login");
        System.out.println("0) Exit");
    }

    private static void printUserMenu(VendingMachine vm) {
        System.out.println("\n=== USER MENU (Logged In) ===");
        System.out.println("Welcome, " + vm.getLoggedInUser().getFullName() + " (" + vm.getLoggedInUser().getUsername() + ")");
        
        User user = vm.getLoggedInUser();
        int optionNumber = 1;
        
        // Show options based on permissions
        if (user.can(VendingMachine.VIEW_MENU)) {
            System.out.println(optionNumber + ") View Products");
            optionNumber++;
        }
        
        if (user.can(VendingMachine.PURCHASE)) {
            System.out.println(optionNumber + ") Purchase Product");
            optionNumber++;
        }
        
        if (user.can(VendingMachine.RESTOCK)) {
            System.out.println(optionNumber + ") Restock Products");
            optionNumber++;
        }
        
        if (user.can(VendingMachine.VIEW_REVENUE)) {
            System.out.println(optionNumber + ") View Revenue");
            optionNumber++;
        }
        
        if (user.can(VendingMachine.VIEW_TRANSACTIONS)) {
            System.out.println(optionNumber + ") View Transactions");
            optionNumber++;
        }
        
        if (user.can(VendingMachine.VIEW_INVENTORY)) {
            System.out.println(optionNumber + ") View Inventory");
            optionNumber++;
        }
        
        if (user.can(VendingMachine.VIEW_BALANCE)) {
            System.out.println(optionNumber + ") View Balance");
            optionNumber++;
        }
        
        if (user.can(VendingMachine.TOP_UP)) {
            System.out.println(optionNumber + ") Top Up Balance");
            optionNumber++;
        }
        
        System.out.println(optionNumber + ") Logout");
        System.out.println("0) Exit");
    }
    
    // ===== Helper methods for input validation =====
    private static int getValidIntInput(Scanner sc) throws InvalidInputException {
        try {
            int value = sc.nextInt();
            sc.nextLine(); // Clear the newline character
            return value;
        } catch (Exception e) {
            sc.nextLine(); // Clear the invalid input from buffer
            throw new InvalidInputException("Please enter a valid integer");
        }
    }
    
    private static double getValidDoubleInput(Scanner sc) throws InvalidInputException {
        try {
            double value = sc.nextDouble();
            sc.nextLine(); // Clear the newline character
            return value;
        } catch (Exception e) {
            sc.nextLine(); // Clear the invalid input from buffer
            throw new InvalidInputException("Please enter a valid number");
        }
    }
}
