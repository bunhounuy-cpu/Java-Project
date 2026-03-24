package user;

import exceptions.*;

public abstract class User implements IUser {
    private String userId;
    private String fullName;
    private String phone;
    private String username;
    private String password;
    private boolean active;
    private double balance;
    private boolean premium;
    private int itemsBought;
    
    // ====== Constructor ======
    public User(String userId, String fullName, String phone,
                 String username, String password) throws InvalidInputException {

        // User input fields - use setters (with validation)
        try {
            setUserId(userId);
            setFullName(fullName);
            setPhone(phone);
            setUsername(username);
            setPassword(password);

            // Internal fields - use setters
            setActive(true);
            setBalance(0);
            setPremium(false);
            setItemsBought(0);
        } catch (InvalidInputException e) {
            throw new InvalidInputException("Failed to create user: " + e.getMessage(), e);
        }
    }
    
    // ====== Abstract Methods (only these differ per subclass) ======
    public abstract boolean can(String action);
    
    // ====== Concrete implementations (shared by all users) ======
    @Override
    public boolean isActive() {
        return active;
    }
    
    @Override
    public String getFullName() {
        return fullName;
    }
    
    @Override
    public boolean isPremium() {
        return premium;
    }
    
    @Override
    public double getBalance() {
        return balance;
    }
    
    @Override
    public String getUserId() {
        return userId != null ? userId.trim() : "USER000";
    }
    
    @Override
    public String getUsername() {
        return username != null ? username.trim() : "user_" + getUserId();
    }
    
    @Override
    public int getItemsBought() {
        return itemsBought;
    }
    
    @Override
    public boolean checkPassword(String input) {
        return password != null && password.equals(input);
    }
    
    // ====== Advanced User Methods ======
    public void promote(String newRole) throws InvalidInputException {
        if (newRole == null || newRole.trim().isEmpty()) {
            throw new InvalidInputException("New role cannot be empty");
        }
        System.out.println(getFullName() + " promoted to " + newRole);
    }
    
    public void giveRaise(double percentage) throws InvalidInputException {
        if (percentage < 0) {
            throw new InvalidInputException("Raise percentage cannot be negative");
        }
        if (percentage > 1000) { // Reasonable limit
            throw new InvalidInputException("Raise percentage too high (max 1000%)");
        }
        
        double newBalance = getBalance() * (1 + percentage / 100);
        setBalance(newBalance);
        System.out.println(getFullName() + "'s balance updated from $" + getBalance() + " to $" + newBalance);
    }
    
    public void suspendAccount() throws VendingMachineException {
        if (!isActive()) {
            throw new VendingMachineException("Account is already suspended");
        }
        setActive(false);
        System.out.println(getFullName() + "'s account has been suspended");
    }
    
    public void upgradeToPremium() throws VendingMachineException {
        if (isPremium()) {
            throw new VendingMachineException("User is already premium");
        }
        setPremium(true);
        setBalance(getBalance() + 100);  // Bonus for upgrading
        System.out.println(getFullName() + " upgraded to premium status with $100 bonus");
    }
    
    // ====== Setters (with validation) ======
    public void setUserId(String userId) throws InvalidInputException {
        if (isBlank(userId)) {
            this.userId = "USER000";
        } else if (userId.length() > 50) {
            throw new InvalidInputException("User ID too long (max 50 characters)");
        } else {
            this.userId = userId.trim();
        }
    }
    
    public void setFullName(String fullName) throws InvalidInputException {
        if (isBlank(fullName)) {
            throw new InvalidInputException("Full name cannot be empty");
        } else if (fullName.length() > 100) {
            throw new InvalidInputException("Full name too long (max 100 characters)");
        } else {
            this.fullName = fullName.trim();
        }
    }
    
    public void setPhone(String phone) throws InvalidInputException {
        String p = (phone == null) ? "" : phone.trim();
        // simple validation: only digits, length 8–15
        if (!isDigits(p) || p.length() < 8 || p.length() > 15) {
            throw new InvalidInputException("Phone number must be 8-15 digits");
        } else {
            this.phone = p;
        }
    }
    
    public void setUsername(String username) throws InvalidInputException {
        if (isBlank(username)) {
            this.username = "user_" + getUserId();
        } else if (username.length() < 3) {
            throw new InvalidInputException("Username must be at least 3 characters");
        } else if (username.length() > 50) {
            throw new InvalidInputException("Username too long (max 50 characters)");
        } else {
            this.username = username.trim();
        }
    }
    
    public void setPassword(String password) throws InvalidInputException {
        String pw = (password == null) ? "" : password;
        if (pw.length() < 4) {
            throw new InvalidInputException("Password must be at least 4 characters");
        } else if (pw.length() > 100) {
            throw new InvalidInputException("Password too long (max 100 characters)");
        } else {
            this.password = pw;
        }
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public void setBalance(double balance) throws InvalidInputException {
        if (balance < 0) {
            throw new InvalidInputException("Balance cannot be negative");
        } else if (balance > 1000000) { // Reasonable limit
            throw new InvalidInputException("Balance too high (max $1,000,000)");
        } else {
            this.balance = balance;
        }
    }
    
    public void setPremium(boolean premium) {
        this.premium = premium;
    }
    
    public void setItemsBought(int itemsBought) throws InvalidInputException {
        if (itemsBought < 0) {
            throw new InvalidInputException("Items bought cannot be negative");
        } else if (itemsBought > 10000) { // Reasonable limit
            throw new InvalidInputException("Items bought count too high (max 10,000)");
        } else {
            this.itemsBought = itemsBought;
        }
    }
    
    public String getPhone() {
        return phone != null ? phone.trim() : "00000000";
    }
    
    protected String getPassword() {
        return password;
    }
    
    // ====== Helper Methods ======
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
    
    private boolean isDigits(String s) {
        if (isBlank(s)) return false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < '0' || c > '9') return false;
        }
        return true;
    }
}
