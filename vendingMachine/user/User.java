package user;

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
    private int loyaltyPoints;
    
    // ====== Constructor ======
    public User(String userId, String fullName, String phone,
                 String username, String password) {

        // User input fields - use setters (with validation)
        setUserId(userId);
        setFullName(fullName);
        setPhone(phone);
        setUsername(username);
        setPassword(password);

        // Internal fields - direct assignment
        this.active = true;
        this.balance = 0;
        this.premium = false;
        this.itemsBought = 0;
        this.loyaltyPoints = 0;
    }
    
    // ====== Abstract Methods (only these differ per subclass) ======
    public abstract boolean can(String action);
    public abstract String getRole();
    
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
        return userId != null ? userId : userId.trim();
    }
    
    @Override
    public String getUsername() {
        return username != null ? username : username.trim();
    }
    
    @Override
    public int getItemsBought() {
        return itemsBought;
    }
    
    @Override
    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }
    
    @Override
    public boolean checkPassword(String input) {
        return password != null && password.equals(input);
    }
    
    // ====== Advanced User Methods ======
    public void promote(String newRole) {
        System.out.println(getFullName() + " promoted to " + newRole);
    }
    
    public void giveRaise(double percentage) {
        double newBalance = getBalance() * (1 + percentage / 100);
        setBalance(newBalance);
        System.out.println(getFullName() + "'s balance updated from $" + getBalance() + " to $" + newBalance);
    }
    
    public void suspendAccount() {
        setActive(false);
        System.out.println(getFullName() + "'s account has been suspended");
    }
    
    public void upgradeToPremium() {
        setPremium(true);
        setBalance(getBalance() + 100);  // Bonus for upgrading
        System.out.println(getFullName() + " upgraded to premium status with $100 bonus");
    }
    
    // ====== Setters (with validation) ======
    public void setUserId(String userId) {
        if (isBlank(userId)) this.userId = "USER000";
        else this.userId = userId.trim();
    }
    
    public void setFullName(String fullName) {
        if (isBlank(fullName)) this.fullName = "No Name";
        else this.fullName = fullName.trim();
    }
    
    public void setPhone(String phone) {
        String p = (phone == null) ? "" : phone.trim();
        // simple validation: only digits, length 8–15
        if (!isDigits(p) || p.length() < 8 || p.length() > 15) this.phone = "00000000";
        else this.phone = p;
    }
    
    public void setUsername(String username) {
        if (isBlank(username)) this.username = "user_" + userId;
        else this.username = username.trim();
    }
    
    public void setPassword(String password) {
        String pw = (password == null) ? "" : password;
        if (pw.length() < 4) this.password = "0000";
        else this.password = pw;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public void setBalance(double balance) {
        if (balance >= 0) {
            this.balance = balance;
        }
    }
    
    public void setPremium(boolean premium) {
        this.premium = premium;
    }
    
    public void setItemsBought(int itemsBought) {
        if (itemsBought >= 0) {
            this.itemsBought = itemsBought;
        }
    }
    
    public void setLoyaltyPoints(int loyaltyPoints) {
        if (loyaltyPoints >= 0) {
            this.loyaltyPoints = loyaltyPoints;
        }
    }
    
    public String getPhone() {
        return phone != null ? phone : phone.trim();
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
