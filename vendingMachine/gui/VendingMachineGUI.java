package gui;

import controller.VendingMachine;
import other.Product;
import other.Slot;
import other.Transaction;
import user.Customer;
import user.User;
import exceptions.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class VendingMachineGUI extends JFrame {

    private VendingMachine vm;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Panels
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private DashboardPanel dashboardPanel;
    private ProductGridPanel productGridPanel;

    // Colors
    public static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    public static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    public static final Color ACCENT_COLOR = new Color(231, 76, 60);
    public static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    public static final Color BG_COLOR = new Color(236, 240, 241);
    public static final Color DARK_BG = new Color(44, 62, 80);
    public static final Color TEXT_COLOR = new Color(44, 62, 80);
    public static final Color LIGHT_TEXT = new Color(189, 195, 199);

    public VendingMachineGUI() {
        try {
            vm = new VendingMachine("Main Lobby", 12, 10);
        } catch (VendingMachineException e) {
            JOptionPane.showMessageDialog(null, "Failed to initialize: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        setTitle("Vending Machine System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BG_COLOR);

        loginPanel = new LoginPanel();
        registerPanel = new RegisterPanel();
        dashboardPanel = new DashboardPanel();
        productGridPanel = new ProductGridPanel();

        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(registerPanel, "REGISTER");
        mainPanel.add(dashboardPanel, "DASHBOARD");
        mainPanel.add(productGridPanel, "PRODUCTS");

        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");

        setVisible(true);
    }

    // ==================== LOGIN PANEL ====================
    class LoginPanel extends JPanel {
        private JTextField usernameField;
        private JPasswordField passwordField;

        LoginPanel() {
            setLayout(new GridBagLayout());
            setBackground(BG_COLOR);
            setBorder(new EmptyBorder(40, 40, 40, 40));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Logo/Title
            JLabel title = new JLabel("VENDING MACHINE", SwingConstants.CENTER);
            title.setFont(new Font("Arial", Font.BOLD, 36));
            title.setForeground(PRIMARY_COLOR);
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            add(title, gbc);

            JLabel subtitle = new JLabel("Welcome Back! Please sign in to continue.", SwingConstants.CENTER);
            subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
            subtitle.setForeground(TEXT_COLOR);
            gbc.gridy = 1;
            add(subtitle, gbc);

            gbc.gridwidth = 1;
            gbc.anchor = GridBagConstraints.WEST;

            // Username
            gbc.gridy = 2; gbc.gridx = 0;
            add(createLabel("Username:"), gbc);
            usernameField = createTextField();
            gbc.gridx = 1;
            add(usernameField, gbc);

            // Password
            gbc.gridy = 3; gbc.gridx = 0;
            add(createLabel("Password:"), gbc);
            passwordField = createPasswordField();
            gbc.gridx = 1;
            add(passwordField, gbc);

            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
            buttonPanel.setBackground(BG_COLOR);

            JButton loginBtn = createButton("Login", PRIMARY_COLOR);
            loginBtn.addActionListener(e -> performLogin());

            JButton registerBtn = createButton("Register", SUCCESS_COLOR);
            registerBtn.addActionListener(e -> cardLayout.show(mainPanel, "REGISTER"));

            buttonPanel.add(loginBtn);
            buttonPanel.add(registerBtn);

            gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
            add(buttonPanel, gbc);
        }

        private void performLogin() {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                showError("Please enter both username and password");
                return;
            }

            try {
                vm.login(username, password);
                passwordField.setText("");
                dashboardPanel.refresh();
                cardLayout.show(mainPanel, "DASHBOARD");
            } catch (AuthenticationException e) {
                showError("Login failed: " + e.getMessage());
            }
        }
    }

    // ==================== REGISTER PANEL ====================
    class RegisterPanel extends JPanel {
        private JTextField fullNameField, phoneField, usernameField, emailField, balanceField;
        private JPasswordField passwordField, confirmPasswordField;

        RegisterPanel() {
            setLayout(new GridBagLayout());
            setBackground(BG_COLOR);
            setBorder(new EmptyBorder(30, 40, 30, 40));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 10, 8, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel title = new JLabel("CREATE ACCOUNT", SwingConstants.CENTER);
            title.setFont(new Font("Arial", Font.BOLD, 28));
            title.setForeground(PRIMARY_COLOR);
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            add(title, gbc);

            gbc.gridwidth = 1;

            // Form fields
            String[] labels = {"Full Name:", "Phone Number:", "Username:", "Email:", "Password:", "Confirm Password:", "Initial Balance ($):"};
            JComponent[] fields = {
                    fullNameField = createTextField(),
                    phoneField = createTextField(),
                    usernameField = createTextField(),
                    emailField = createTextField(),
                    passwordField = createPasswordField(),
                    confirmPasswordField = createPasswordField(),
                    balanceField = createTextField("0.00")
            };

            int row = 1;
            for (int i = 0; i < labels.length; i++) {
                gbc.gridy = row; gbc.gridx = 0;
                add(createLabel(labels[i]), gbc);
                gbc.gridx = 1;
                add(fields[i], gbc);
                row++;
            }

            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
            buttonPanel.setBackground(BG_COLOR);

            JButton registerBtn = createButton("Create Account", SUCCESS_COLOR);
            registerBtn.addActionListener(e -> performRegistration());

            JButton backBtn = createButton("Back to Login", Color.GRAY);
            backBtn.addActionListener(e -> {
                clearFields();
                cardLayout.show(mainPanel, "LOGIN");
            });

            buttonPanel.add(registerBtn);
            buttonPanel.add(backBtn);

            gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 2;
            add(buttonPanel, gbc);
        }

        private void performRegistration() {
            String fullName = fullNameField.getText().trim();
            String phone = phoneField.getText().trim();
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String balanceText = balanceField.getText().trim();

            // Validation
            if (fullName.isEmpty() || phone.isEmpty() || username.isEmpty() ||
                    email.isEmpty() || password.isEmpty()) {
                showError("All fields except balance are required");
                return;
            }

            if (!fullName.matches("[A-Za-z ]+")) {
                showError("Full name must contain letters only");
                return;
            }

            if (!phone.matches("\\d+") || phone.length() < 8 || phone.length() > 15) {
                showError("Phone number must be 8-15 digits");
                return;
            }

            if (username.length() < 3) {
                showError("Username must be at least 3 characters");
                return;
            }

            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                showError("Invalid email format");
                return;
            }

            if (password.length() < 4) {
                showError("Password must be at least 4 characters");
                return;
            }

            if (!password.equals(confirmPassword)) {
                showError("Passwords do not match");
                return;
            }

            double balance = 0.0;
            if (!balanceText.isEmpty()) {
                try {
                    balance = Double.parseDouble(balanceText);
                    if (balance < 0) {
                        showError("Balance cannot be negative");
                        return;
                    }
                } catch (NumberFormatException e) {
                    showError("Invalid balance amount");
                    return;
                }
            }

            // Check uniqueness
            for (User u : vm.getUsers()) {
                if (u.getUsername().equalsIgnoreCase(username)) {
                    showError("Username is already taken");
                    return;
                }
                if (u.getPhone().equals(phone)) {
                    showError("Phone number is already registered");
                    return;
                }
                if (u instanceof Customer && ((Customer) u).getEmail().equalsIgnoreCase(email)) {
                    showError("Email is already registered");
                    return;
                }
            }

            try {
                String userId = "CUST" + (System.currentTimeMillis() % 100000);
                Customer c = new Customer(userId, fullName, phone, username, password, email);
                c.setBalance(balance);
                vm.addUser(c);
                showSuccess("Registration successful! Your ID: " + userId);
                clearFields();
                cardLayout.show(mainPanel, "LOGIN");
            } catch (InvalidInputException e) {
                showError("Registration error: " + e.getMessage());
            }
        }

        private void clearFields() {
            fullNameField.setText("");
            phoneField.setText("");
            usernameField.setText("");
            emailField.setText("");
            passwordField.setText("");
            confirmPasswordField.setText("");
            balanceField.setText("0.00");
        }
    }

    // ==================== DASHBOARD PANEL ====================
    class DashboardPanel extends JPanel {
        private JLabel welcomeLabel, roleLabel, balanceLabel;
        private JPanel menuPanel;

        DashboardPanel() {
            setLayout(new BorderLayout());
            setBackground(BG_COLOR);

            // Header
            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(DARK_BG);
            header.setBorder(new EmptyBorder(20, 30, 20, 30));
            header.setPreferredSize(new Dimension(getWidth(), 100));

            welcomeLabel = new JLabel("Welcome");
            welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
            welcomeLabel.setForeground(Color.WHITE);

            roleLabel = new JLabel("Role");
            roleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            roleLabel.setForeground(LIGHT_TEXT);

            JPanel userInfo = new JPanel(new GridLayout(2, 1));
            userInfo.setBackground(DARK_BG);
            userInfo.add(welcomeLabel);
            userInfo.add(roleLabel);

            balanceLabel = new JLabel("Balance: $0.00");
            balanceLabel.setFont(new Font("Arial", Font.BOLD, 18));
            balanceLabel.setForeground(SUCCESS_COLOR);

            JButton logoutBtn = createButton("Logout", ACCENT_COLOR);
            logoutBtn.addActionListener(e -> {
                vm.logout();
                cardLayout.show(mainPanel, "LOGIN");
            });

            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            rightPanel.setBackground(DARK_BG);
            rightPanel.add(balanceLabel);
            rightPanel.add(Box.createHorizontalStrut(20));
            rightPanel.add(logoutBtn);

            header.add(userInfo, BorderLayout.WEST);
            header.add(rightPanel, BorderLayout.EAST);

            add(header, BorderLayout.NORTH);

            // Menu Panel
            menuPanel = new JPanel(new GridLayout(0, 2, 20, 20));
            menuPanel.setBackground(BG_COLOR);
            menuPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

            JScrollPane scrollPane = new JScrollPane(menuPanel);
            scrollPane.setBorder(null);
            scrollPane.setBackground(BG_COLOR);
            add(scrollPane, BorderLayout.CENTER);
        }

        void refresh() {
            User user = vm.getLoggedInUser();
            if (user == null) return;

            welcomeLabel.setText("Welcome, " + user.getFullName());
            roleLabel.setText("Role: " + user.getClass().getSimpleName() + " | @" + user.getUsername());
            balanceLabel.setText(String.format("Balance: $%.2f", user.getBalance()));

            menuPanel.removeAll();

            // Add menu buttons based on permissions
            if (user.can(VendingMachine.VIEW_MENU)) {
                menuPanel.add(createMenuCard("View Products", "Browse available products", "🛒",
                        e -> showProductGrid()));
            }

            if (user.can(VendingMachine.PURCHASE)) {
                menuPanel.add(createMenuCard("Quick Purchase", "Buy products instantly", "💳",
                        e -> showProductGrid()));
            }

            if (user.can(VendingMachine.VIEW_BALANCE)) {
                menuPanel.add(createMenuCard("My Balance", "View and manage your balance", "💰",
                        e -> showBalanceDialog()));
            }

            if (user.can(VendingMachine.TOP_UP)) {
                menuPanel.add(createMenuCard("Top Up", "Add funds to your account", "💵",
                        e -> showTopUpDialog()));
            }

            if (user.can(VendingMachine.VIEW_INVENTORY)) {
                menuPanel.add(createMenuCard("Inventory", "View current stock levels", "📦",
                        e -> showInventoryDialog()));
            }

            if (user.can(VendingMachine.RESTOCK)) {
                menuPanel.add(createMenuCard("Restock", "Add inventory to slots", "📥",
                        e -> showRestockDialog()));
            }

            if (user.can(VendingMachine.ADD_NEW_PRODUCT)) {
                menuPanel.add(createMenuCard("Add Product", "Create new product slot", "➕",
                        e -> showAddSlotDialog()));
            }

            if (user.can(VendingMachine.REMOVE_PRODUCT)) {
                menuPanel.add(createMenuCard("Remove Product", "Delete product slots", "❌",
                        e -> showRemoveSlotDialog()));
            }

            if (user.can(VendingMachine.CHANGE_PRODUCT)) {
                menuPanel.add(createMenuCard("Change Product", "Replace slot products", "🔄",
                        e -> showChangeProductDialog()));
            }

            if (user.can(VendingMachine.VIEW_REVENUE)) {
                menuPanel.add(createMenuCard("Revenue", "View total revenue", "📊",
                        e -> showRevenueDialog()));
            }

            if (user.can(VendingMachine.VIEW_TRANSACTIONS)) {
                menuPanel.add(createMenuCard("Transactions", "View transaction history", "📜",
                        e -> showTransactionsDialog()));
            }

            menuPanel.revalidate();
            menuPanel.repaint();
        }

        private JPanel createMenuCard(String title, String desc, String icon, ActionListener action) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(200, 200, 200), 1),
                    new EmptyBorder(20, 20, 20, 20)
            ));
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));

            JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            titleLabel.setForeground(TEXT_COLOR);

            JLabel descLabel = new JLabel(desc, SwingConstants.CENTER);
            descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            descLabel.setForeground(LIGHT_TEXT);

            JPanel center = new JPanel(new GridLayout(3, 1, 5, 5));
            center.setBackground(Color.WHITE);
            center.add(iconLabel);
            center.add(titleLabel);
            center.add(descLabel);

            card.add(center, BorderLayout.CENTER);

            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    action.actionPerformed(null);
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    card.setBackground(new Color(240, 240, 240));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    card.setBackground(Color.WHITE);
                }
            });

            return card;
        }
    }

    // ==================== PRODUCT GRID PANEL ====================
    class ProductGridPanel extends JPanel {
        private JPanel slotsPanel;

        ProductGridPanel() {
            setLayout(new BorderLayout());
            setBackground(BG_COLOR);

            // Header
            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(DARK_BG);
            header.setBorder(new EmptyBorder(15, 20, 15, 20));

            JLabel title = new JLabel("🛒 Available Products", SwingConstants.LEFT);
            title.setFont(new Font("Arial", Font.BOLD, 22));
            title.setForeground(Color.WHITE);

            JButton backBtn = createButton("← Back", SECONDARY_COLOR);
            backBtn.addActionListener(e -> cardLayout.show(mainPanel, "DASHBOARD"));

            header.add(title, BorderLayout.WEST);
            header.add(backBtn, BorderLayout.EAST);

            add(header, BorderLayout.NORTH);

            // Slots Grid
            slotsPanel = new JPanel(new GridLayout(0, 4, 15, 15));
            slotsPanel.setBackground(BG_COLOR);
            slotsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

            JScrollPane scrollPane = new JScrollPane(slotsPanel);
            scrollPane.setBorder(null);
            scrollPane.setBackground(BG_COLOR);
            add(scrollPane, BorderLayout.CENTER);
        }

        void refreshSlots() {
            slotsPanel.removeAll();
            ArrayList<Slot> slots = vm.getSlots();

            if (slots.isEmpty()) {
                JLabel emptyLabel = new JLabel("No products available", SwingConstants.CENTER);
                emptyLabel.setFont(new Font("Arial", Font.ITALIC, 18));
                emptyLabel.setForeground(LIGHT_TEXT);
                slotsPanel.add(emptyLabel);
            } else {
                for (Slot slot : slots) {
                    slotsPanel.add(createSlotCard(slot));
                }
            }

            slotsPanel.revalidate();
            slotsPanel.repaint();
        }

        private JPanel createSlotCard(Slot slot) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(slot.getQuantity() > 0 ? PRIMARY_COLOR : Color.GRAY, 2),
                    new EmptyBorder(15, 15, 15, 15)
            ));
            card.setPreferredSize(new Dimension(200, 180));

            // Slot ID badge
            JLabel idLabel = new JLabel(slot.getSlotID(), SwingConstants.CENTER);
            idLabel.setFont(new Font("Arial", Font.BOLD, 14));
            idLabel.setOpaque(true);
            idLabel.setBackground(slot.getQuantity() > 0 ? PRIMARY_COLOR : Color.GRAY);
            idLabel.setForeground(Color.WHITE);
            idLabel.setBorder(new EmptyBorder(5, 10, 5, 10));

            JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            idPanel.setBackground(Color.WHITE);
            idPanel.add(idLabel);

            // Product info
            Product p = slot.getProduct();
            JLabel nameLabel = new JLabel(p.getName(), SwingConstants.CENTER);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
            nameLabel.setForeground(TEXT_COLOR);

            JLabel catLabel = new JLabel(p.getCategory(), SwingConstants.CENTER);
            catLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            catLabel.setForeground(LIGHT_TEXT);

            JLabel priceLabel = new JLabel(String.format("$%.2f", p.getPrice()), SwingConstants.CENTER);
            priceLabel.setFont(new Font("Arial", Font.BOLD, 20));
            priceLabel.setForeground(SUCCESS_COLOR);

            JLabel qtyLabel = new JLabel("Stock: " + slot.getQuantity(), SwingConstants.CENTER);
            qtyLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            qtyLabel.setForeground(slot.getQuantity() > 0 ? TEXT_COLOR : ACCENT_COLOR);

            JPanel center = new JPanel(new GridLayout(5, 1, 3, 3));
            center.setBackground(Color.WHITE);
            center.add(idPanel);
            center.add(nameLabel);
            center.add(catLabel);
            center.add(priceLabel);
            center.add(qtyLabel);

            card.add(center, BorderLayout.CENTER);

            User user = vm.getLoggedInUser();
            if (user != null && user.can(VendingMachine.PURCHASE) && slot.getQuantity() > 0) {
                JButton buyBtn = createButton("Buy", SUCCESS_COLOR);
                buyBtn.setFont(new Font("Arial", Font.BOLD, 14));
                buyBtn.addActionListener(e -> performPurchase(slot.getSlotID()));
                card.add(buyBtn, BorderLayout.SOUTH);
            }

            return card;
        }

        private void performPurchase(String slotId) {
            User user = vm.getLoggedInUser();

            try {
                Customer proxy = new Customer(user.getUserId(), user.getFullName(),
                        "0000000000", user.getUsername(), "temp", "temp@email.com");
                proxy.setBalance(user.getBalance());
                proxy.setPremium(user.isPremium());
                proxy.setItemsBought(user.getItemsBought());

                boolean ok = vm.vendBulk(slotId, proxy, 1);
                if (ok) {
                    user.setBalance(proxy.getBalance());
                    user.setItemsBought(proxy.getItemsBought());
                    showSuccess("Purchase successful!");
                    refreshSlots();
                    dashboardPanel.refresh();
                } else {
                    showError("Purchase failed. Please check your balance.");
                }
            } catch (InsufficientFundsException e) {
                showError("Insufficient funds. Need: $" + String.format("%.2f", e.getRequestedAmount()) +
                        ", You have: $" + String.format("%.2f", e.getAvailableBalance()));
            } catch (VendingMachineException e) {
                showError("Purchase failed: " + e.getMessage());
            }
        }
    }

    // ==================== DIALOG METHODS ====================

    private void showProductGrid() {
        productGridPanel.refreshSlots();
        cardLayout.show(mainPanel, "PRODUCTS");
    }

    private void showBalanceDialog() {
        User user = vm.getLoggedInUser();
        JOptionPane.showMessageDialog(this,
                "<html><h2>Account Balance</h2>" +
                        "<p><b>Current Balance:</b> $" + String.format("%.2f", user.getBalance()) + "</p>" +
                        "<p><b>Items Purchased:</b> " + user.getItemsBought() + "</p>" +
                        "<p><b>Premium Status:</b> " + (user.isPremium() ? "Yes" : "No") + "</p></html>",
                "My Balance", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showTopUpDialog() {
        User user = vm.getLoggedInUser();

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.add(new JLabel("Current Balance:"));
        panel.add(new JLabel("$" + String.format("%.2f", user.getBalance())));
        panel.add(new JLabel("Amount to Add ($):"));

        JTextField amountField = new JTextField(10);
        panel.add(amountField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Top Up Balance",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                if (amount <= 0) {
                    showError("Amount must be positive");
                    return;
                }
                if (user.getBalance() + amount > 1_000_000) {
                    showError("Balance cannot exceed $1,000,000");
                    return;
                }
                user.setBalance(user.getBalance() + amount);
                showSuccess("Balance updated! New balance: $" + String.format("%.2f", user.getBalance()));
                dashboardPanel.refresh();
            } catch (NumberFormatException e) {
                showError("Invalid amount");
            } catch (InvalidInputException e) {
                showError(e.getMessage());
            }
        }
    }

    private void showInventoryDialog() {
        JTextArea textArea = new JTextArea(20, 50);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);

        StringBuilder sb = new StringBuilder();
        sb.append("=== INVENTORY ===\n\n");
        sb.append(String.format("%-6s %-20s %-12s %-10s %-8s\n", "Slot", "Product", "Category", "Price", "Qty"));
        sb.append("-".repeat(60)).append("\n");

        for (Slot s : vm.getSlots()) {
            sb.append(String.format("%-6s %-20s %-12s $%-9.2f %-8d\n",
                    s.getSlotID(),
                    s.getProduct().getName(),
                    s.getProduct().getCategory(),
                    s.getProduct().getPrice(),
                    s.getQuantity()));
        }

        textArea.setText(sb.toString());

        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(this, scrollPane, "Inventory",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showRestockDialog() {
        if (vm.getSlots().isEmpty()) {
            showError("No slots available to restock");
            return;
        }

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.add(new JLabel("Select Slot:"));

        JComboBox<String> slotBox = new JComboBox<>();
        for (Slot s : vm.getSlots()) {
            slotBox.addItem(s.getSlotID() + " - " + s.getProduct().getName() + " (Qty: " + s.getQuantity() + ")");
        }
        panel.add(slotBox);

        panel.add(new JLabel("Amount to Add:"));
        JTextField amountField = new JTextField(10);
        panel.add(amountField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Restock Product",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String slotId = ((String) slotBox.getSelectedItem()).split(" - ")[0];
                int amount = Integer.parseInt(amountField.getText().trim());
                vm.restock(slotId, amount);
                showSuccess("Restocked successfully!");
            } catch (NumberFormatException e) {
                showError("Invalid amount");
            } catch (VendingMachineException e) {
                showError(e.getMessage());
            }
        }
    }

    private void showAddSlotDialog() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        panel.add(new JLabel("Slot ID (e.g., A1):"));
        JTextField slotField = new JTextField(10);
        panel.add(slotField);

        panel.add(new JLabel("Product Name:"));
        JTextField nameField = new JTextField(10);
        panel.add(nameField);

        panel.add(new JLabel("Category:"));
        JTextField catField = new JTextField(10);
        panel.add(catField);

        panel.add(new JLabel("Price ($):"));
        JTextField priceField = new JTextField(10);
        panel.add(priceField);

        panel.add(new JLabel("Quantity:"));
        JTextField qtyField = new JTextField("10", 10);
        panel.add(qtyField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Slot",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String slotId = slotField.getText().trim().toUpperCase();
                String name = nameField.getText().trim();
                String category = catField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                int qty = Integer.parseInt(qtyField.getText().trim());

                Product p = new Product(name, category, price);
                vm.addSlot(slotId, p, qty);
                showSuccess("Slot added successfully!");
            } catch (NumberFormatException e) {
                showError("Invalid number format");
            } catch (VendingMachineException e) {
                showError(e.getMessage());
            }
        }
    }

    private void showRemoveSlotDialog() {
        if (vm.getSlots().isEmpty()) {
            showError("No slots to remove");
            return;
        }

        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.add(new JLabel("Select Slot to Remove:"));

        JComboBox<String> slotBox = new JComboBox<>();
        for (Slot s : vm.getSlots()) {
            slotBox.addItem(s.getSlotID() + " - " + s.getProduct().getName());
        }
        panel.add(slotBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Remove Slot",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String slotId = ((String) slotBox.getSelectedItem()).split(" - ")[0];
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to remove slot " + slotId + "?",
                    "Confirm Removal", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    vm.removeSlot(slotId);
                    showSuccess("Slot removed successfully!");
                } catch (VendingMachineException e) {
                    showError(e.getMessage());
                }
            }
        }
    }

    private void showChangeProductDialog() {
        if (vm.getSlots().isEmpty()) {
            showError("No slots available");
            return;
        }

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));

        panel.add(new JLabel("Select Slot:"));
        JComboBox<String> slotBox = new JComboBox<>();
        for (Slot s : vm.getSlots()) {
            slotBox.addItem(s.getSlotID() + " - " + s.getProduct().getName());
        }
        panel.add(slotBox);

        panel.add(new JLabel("New Product Name:"));
        JTextField nameField = new JTextField(10);
        panel.add(nameField);

        panel.add(new JLabel("New Category:"));
        JTextField catField = new JTextField(10);
        panel.add(catField);

        panel.add(new JLabel("New Price ($):"));
        JTextField priceField = new JTextField(10);
        panel.add(priceField);

        panel.add(new JLabel("New Quantity:"));
        JTextField qtyField = new JTextField(10);
        panel.add(qtyField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Change Product",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String slotId = ((String) slotBox.getSelectedItem()).split(" - ")[0];
                String name = nameField.getText().trim();
                String category = catField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                int qty = Integer.parseInt(qtyField.getText().trim());

                Product p = new Product(name, category, price);
                vm.changeProduct(slotId, p, qty);
                showSuccess("Product changed successfully!");
            } catch (NumberFormatException e) {
                showError("Invalid number format");
            } catch (VendingMachineException e) {
                showError(e.getMessage());
            }
        }
    }

    private void showRevenueDialog() {
        JOptionPane.showMessageDialog(this,
                "<html><h2>Total Revenue</h2>" +
                        "<p style='font-size:24px; color:#2ecc71;'><b>$" +
                        String.format("%.2f", vm.getRevenue()) + "</b></p></html>",
                "Revenue", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showTransactionsDialog() {
        JTextArea textArea = new JTextArea(20, 60);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);

        StringBuilder sb = new StringBuilder();
        sb.append("=== TRANSACTION HISTORY ===\n\n");

        for (Transaction t : vm.getTransactions()) {
            sb.append(t.toString()).append("\n");
        }

        if (vm.getTransactions().isEmpty()) {
            sb.append("No transactions recorded.");
        }

        textArea.setText(sb.toString());

        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(this, scrollPane, "Transactions",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ==================== UI HELPER METHODS ====================

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200)),
                new EmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private JTextField createTextField(String text) {
        JTextField field = createTextField();
        field.setText(text);
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200)),
                new EmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setBorder(new EmptyBorder(12, 25, 12, 25));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bgColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });

        return btn;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    // ==================== MAIN ====================
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new VendingMachineGUI());
    }
}
