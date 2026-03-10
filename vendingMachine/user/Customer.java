package user;

public class Customer extends User {

    public Customer(String userId, String fullName, String phone,
                    String username, String password) {
        super(userId, fullName, phone, username, password);
    }
    
    @Override
    public String getRole() {
        return "Customer";
    }
    
    @Override
    public boolean can(String action) {
        if (action.equals("PURCHASE") || action.equals("VIEW_MENU") || action.equals("VIEW_BALANCE") || action.equals("TOP_UP") || action.equals("REDEEM_POINTS")) {
            return true;
        }
        return false;
    }
    
    // ====== Customer-Specific Methods ======
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
}