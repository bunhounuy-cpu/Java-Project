package user;

public class User implements IUser {

    // ====== Fields (Encapsulation) ======
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

        setUserId(userId);
        setFullName(fullName);
        setPhone(phone);
        setUsername(username);
        setPassword(password);

        this.active = true;
        this.balance = 0;
        this.premium = false;
        this.itemsBought = 0;
        this.loyaltyPoints = 0;
    }

    protected String getPassword() {
        return password;
    }

    // ====== Interface Methods ======
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPhone() { return phone; }
    public boolean isActive() { return active; }
    public boolean checkPassword(String input) {
        return password != null && password.equals(input);
    }
    public String getFullName() { return fullName; }
    public boolean isPremium() { return premium; }
    public double getBalance() { return balance; }
    public int getItemsBought() { return itemsBought; }
    public int getLoyaltyPoints() { return loyaltyPoints; }
    
    @Override
    public String getRole() {
        return "User";
    }

    // ====== Purchase-related methods (available to all users) ======
    public boolean isCardActive() {
        return isActive();
    }
    
    public boolean debit(double amount) {
        if (amount <= 0) return false;
        if (getBalance() < amount) return false;
        setBalance(getBalance() - amount);
        return true;
    }
    
    public void addLoyaltyPoints(int points) {
        if (points > 0) {
            setLoyaltyPoints(getLoyaltyPoints() + points);
        }
    }
    
    public void incrementItems() {
        setItemsBought(getItemsBought() + 1);
    }

    @Override
    public boolean can(String action) {
        return false; // Default implementation - no permissions
    }

    // ====== Setters (with simple validation) ======
    public void setUserId(String userId) {
        if (isBlank(userId)) this.userId = "UNKNOWN";
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
        if (isBlank(username)) this.username = "user_" + this.userId;
        else this.username = username.trim();
    }

    public void setPassword(String password) {
        String pw = (password == null) ? "" : password;
        // simple rule for teaching: >= 4 chars
        if (pw.length() < 4) this.password = "0000";
        else this.password = pw;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public void setItemsBought(int itemsBought) {
        this.itemsBought = itemsBought;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    // ====== Helpers ======
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

    // ====== toString ======
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", username='" + username + '\'' +
                ", active=" + active +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        User other = (User) obj;
        if(other.userId.equals(userId)) {
            return true;
        }
        return false;
    }
}
