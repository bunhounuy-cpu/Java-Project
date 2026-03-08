package controller;

import java.util.Scanner;
import user.Customer;
import user.Manager;
import user.Restocker;
import user.User;

public class vmMain {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        VendingMachine vm = new VendingMachine("Lobby", 12);
        int choice;
        
        do {
            if (!vm.isUserLoggedIn()) {
                printMainMenu();
                System.out.print("Choose: ");
                choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1: {
                        System.out.print("Username: ");
                        String username = sc.nextLine();

                        System.out.print("Password: ");
                        String password = sc.nextLine();

                        vm.login(username, password);
                        break;
                    }

                    case 2: {
                        vm.printMenu();
                        break;
                    }

                    case 3: {
                        // Customer purchase
                        System.out.print("Customer Name: ");
                        String name = sc.nextLine();

                        System.out.print("Card Number: ");
                        String cardNumber = sc.nextLine();
                        
                        System.out.print("Premium? (1=Yes, 0=No): ");
                        int premiumChoice = sc.nextInt();
                        sc.nextLine();
                        boolean isPremium = (premiumChoice == 1);
                        
                        System.out.print("Initial Balance: ");
                        double balance = sc.nextDouble();
                        sc.nextLine();
                        
                        User baseCustomer = new User("C001", name, cardNumber, cardNumber, cardNumber);
                        Customer customer = new Customer(baseCustomer);
                        customer.setPremium(isPremium);
                        customer.setBalance(balance);
                        
                        vm.addUser(customer);
                        
                        vm.printMenu();
                        System.out.print("Enter Slot ID: ");
                        String slotId = sc.nextLine();
                        
                        boolean success = vm.vend(slotId, customer);
                        if (success) {
                            System.out.println("Purchase successful! New balance: $" + customer.getBalance());
                        } else {
                            System.out.println("Purchase failed!");
                        }
                        break;
                    }
                }

            } else {
                printUserMenu(vm);

                System.out.print("Choose: ");
                choice = sc.nextInt();
                sc.nextLine();

                handleUserChoice(vm, sc, choice);
            }

        } while (choice != 0);

        sc.close();
    }

    // ===== Handle dynamic user menu choices =====
    private static void handleUserChoice(VendingMachine vm, Scanner sc, int choice) {
        User user = vm.getLoggedInUser();
        int optionNumber = 1;
        
        if (user.can(VendingMachine.VIEW_MENU)) {
            if (choice == optionNumber) {
                vm.printMenu();
                return;
            }
            optionNumber++;
        }
        
        if (user.can(VendingMachine.RESTOCK)) {
            if (choice == optionNumber) {
                System.out.print("Slot ID: ");
                String slotId = sc.nextLine();
                System.out.print("Amount to add: ");
                int amount = sc.nextInt();
                sc.nextLine();
                vm.restock(slotId, amount);
                return;
            }
            optionNumber++;
        }
        
        if (user.can(VendingMachine.VIEW_REVENUE)) {
            if (choice == optionNumber) {
                double revenue = vm.getRevenue();
                if (revenue > 0) {
                    System.out.println("Total Revenue: $" + revenue);
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
                double amount = sc.nextDouble();
                sc.nextLine();
                user.setBalance(user.getBalance() + amount);
                System.out.println("Balance updated: $" + user.getBalance());
                return;
            }
            optionNumber++;
        }
        
        if (user.can(VendingMachine.REDEEM_POINTS)) {
            if (choice == optionNumber) {
                System.out.println("Current Points: " + user.getLoyaltyPoints());
                System.out.print("Enter points to redeem: ");
                int points = sc.nextInt();
                sc.nextLine();
                if (points <= user.getLoyaltyPoints()) {
                    user.setLoyaltyPoints(user.getLoyaltyPoints() - points);
                    System.out.println("Points redeemed. Remaining: " + user.getLoyaltyPoints());
                } else {
                    System.out.println("Insufficient points!");
                }
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
    }

    // ===== Menu printing methods =====
    private static void printMainMenu() {
        System.out.println("\n=== MAIN MENU (Not Logged In) ===");
        System.out.println("1) User Login");
        System.out.println("2) View Menu Items");
        System.out.println("0) Exit");
    }

    private static void printUserMenu(VendingMachine vm) {
        System.out.println("\n=== USER MENU (Logged In) ===");
        System.out.println("Logged in as: " + getRoleName(vm.getLoggedInUser()) + " (" + vm.getLoggedInUser().getUsername() + ")");
        
        User user = vm.getLoggedInUser();
        int optionNumber = 1;
        
        // Show options based on permissions
        if (user.can(VendingMachine.VIEW_MENU)) {
            System.out.println(optionNumber + ") View Products");
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
        
        if (user.can(VendingMachine.REDEEM_POINTS)) {
            System.out.println(optionNumber + ") Redeem Points");
            optionNumber++;
        }
        
        System.out.println(optionNumber + ") Logout");
        System.out.println("0) Exit");
    }

    private static String getRoleName(User user) {
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
}
