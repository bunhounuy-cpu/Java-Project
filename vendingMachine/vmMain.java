import java.util.Scanner;

public class vmMain {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        VendingMachine vm = new VendingMachine("Lobby", 12);
        
        // Initialize some products
        vm.addSlot("A1", new Product("Chips", "Snack", 1.50), 5);
        vm.addSlot("A2", new Product("Candy", "Snack", 1.00), 5);
        vm.addSlot("B1", new Product("Soda", "Drink", 2.00), 5);
        vm.addSlot("B2", new Product("Water", "Drink", 1.25), 6);
        vm.addSlot("C1", new Product("Gum", "Snack", 0.75), 10);
        vm.addSlot("D1", new Product("Juice", "Drink", 2.50), 4);
        
        // Add some users
        vm.addUser(new Technician("T001", "tech1", "techpass", "John Technician", "Maintenance"));
        vm.addUser(new Manager("M001", "mgr1", "mgrpass", "Sarah Manager", "Office A"));
        vm.addUser(new Customer("hou", "1234", true, 100, "1234hou"));
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

                        System.out.print("password: ");
                        String password = sc.nextLine();
                        
                        System.out.print("Premium? (1=Yes, 0=No): ");
                        int premiumChoice = sc.nextInt();
                        sc.nextLine();
                        boolean isPremium = (premiumChoice == 1);
                        
                        System.out.print("Initial Balance: ");
                        double balance = sc.nextDouble();
                        sc.nextLine();
                        
                        Customer customer = new Customer(name, cardNumber, isPremium, balance, password);
                        
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

                    case 4: {
                        // Collection demonstrations
                        vm.demonstrateArrayListOperations();
                        vm.demonstrateLinkedList();
                        vm.demonstrateHashMap();
                        break;
                    }

                    case 0: {
                        System.out.println("Goodbye!");
                        break;
                    }

                    default:
                        System.out.println("Invalid choice.");
                }

            } else {
                printUserMenu(vm);

                System.out.print("Choose: ");
                choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1: { // View Menu
                        vm.printMenu();
                        break;
                    }

                    case 2: { // Restock
                        System.out.print("Slot ID: ");
                        String slotId = sc.nextLine();

                        System.out.print("Amount to add: ");
                        int amount = sc.nextInt();
                        sc.nextLine();

                        vm.restock(slotId, amount);
                        break;
                    }

                    case 3: { // View Revenue
                        double revenue = vm.getRevenue();
                        if (revenue > 0) {
                            System.out.println("Total Revenue: $" + revenue);
                        }
                        break;
                    }

                    case 4: { // View Transactions
                        vm.printTransactions();
                        break;
                    }

                    case 5: { // View Inventory
                        vm.printInventory();
                        break;
                    }

                    case 6: { // Power Control
                        System.out.print("Turn on? (1=On, 0=Off): ");
                        int powerChoice = sc.nextInt();
                        sc.nextLine();
                        boolean powerOn = (powerChoice == 1);
                        
                        vm.power(powerOn);
                        System.out.println("Machine powered " + (powerOn ? "ON" : "OFF"));
                        break;
                    }

                    case 7: { // Logout
                        vm.logout();
                        break;
                    }

                    case 0: {
                        System.out.println("Goodbye!");
                        break;
                    }

                    default:
                        System.out.println("Invalid choice.");
                }
            }

        } while (choice != 0);

        sc.close();
    }

    // ===== Menu printing methods =====
    private static void printMainMenu() {
        System.out.println("\n=== VENDING MACHINE MAIN MENU ===");
        System.out.println("1) User Login");
        System.out.println("2) View Products");
        System.out.println("3) Customer Purchase");
        System.out.println("4) Collection Demonstrations");
        System.out.println("0) Exit");
    }

    private static void printUserMenu(VendingMachine vm) {
        System.out.println("\n=== USER MENU (Logged In) ===");
        System.out.println("Logged in as: " + vm.getLoggedInUser().getRole() + " (" + vm.getLoggedInUser().getUsername() + ")");
        System.out.println("1) View Products");
        System.out.println("2) Restock Products");
        System.out.println("3) View Revenue");
        System.out.println("4) View Transactions");
        System.out.println("5) View Inventory");
        System.out.println("6) Power Control");
        System.out.println("7) Logout");
        System.out.println("0) Exit");
    }
}
