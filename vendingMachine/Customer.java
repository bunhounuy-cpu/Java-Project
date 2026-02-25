public class Customer implements IUser  {
    private String username;
    private String id;
    private String password;
    private boolean premium;
    private double balance;
    private boolean cardActive;
    private int itemsBought;
    private int loyaltyPoints;
    
    public Customer(String username, String id, boolean isPremium, double balance, String password) {
        this.username = username;
        this.id = id;
        this.password = password;
        this.premium = isPremium;
        this.balance = balance;
        this.cardActive = true;
        this.itemsBought = 0;
        this.loyaltyPoints = 0;
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public String getRole() {
        return "CUSTOMER";
    }

    
    public int getItemsBought() {
        return itemsBought;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }
    
    public boolean hasMembership() {
        return premium;
    }
    
    public boolean isCardActive() {
        return cardActive;
    }
    
    public double getBalance() {
        return balance;
    }
    
    public boolean isPremium() {
        return premium;
    }
    
    public boolean setCardActive(boolean on) {
        this.cardActive = on;
        return true;
    }
    
    public boolean debit(double amount) {
        if (!cardActive) return false;
        if (amount < 0) return false;
        if (balance < amount) return false;
        balance -= amount;
        return true;
    }
    
    public void topUp(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
    
    public void incrementItems() {
        itemsBought++;
    }

    public void addLoyaltyPoints(int points) {
        if (points > 0) {
            loyaltyPoints += points;
        }
    }


    public boolean redeemLoyaltyPoints(int pointsToRedeem) {
        if (pointsToRedeem <= 0) return false;
        if (pointsToRedeem > loyaltyPoints) return false;
        int dollars = pointsToRedeem / 100; // 100 points -> $1
        if (dollars <= 0) return false;
        int required = dollars * 100;
        loyaltyPoints -= required;
        balance += dollars;
        return true;
    }
    
    @Override
    public String toString() {
        String t = premium ? "PREMIUM" : "NORMAL";
        return "Customer{username='" + username + "', card='" + id + "', type=" + t + ", balance=$" + balance + ", active=" + cardActive + ", items=" + itemsBought + ", points=" + loyaltyPoints + "}";
    }

    
    @Override
    public boolean can(String action) {
        switch (action) {
            case VendingMachine.PURCHASE:
            case VendingMachine.VIEW_BALANCE:
            case VendingMachine.VIEW_MENU:
                return true;
            case VendingMachine.RESTOCK:
            case VendingMachine.VIEW_INVENTORY:
            case VendingMachine.POWER_CONTROL:
            case VendingMachine.VIEW_REVENUE:
            case VendingMachine.MANAGE_PRODUCTS:
            case VendingMachine.VIEW_TRANSACTIONS:
            case VendingMachine.TOP_UP:
            case VendingMachine.REDEEM_POINTS:
            default:
                return false;
        }
    }


}