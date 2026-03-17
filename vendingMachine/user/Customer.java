package user;

public class Customer extends User {

   private String email; 


    public Customer(String userId, String fullName, String phone,
                    String username, String password, String email) {
        super(userId, fullName, phone, username, password);
        setEmail(email);
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (email == null || !email.matches(emailRegex)) {
            System.out.println("Invalid email format. Update failed.");  
        }else{
            this.email = email;
        }
    }
    
    @Override
    public boolean can(String action) {
        if (action.equals("PURCHASE") 
            || action.equals("VIEW_MENU") 
            || action.equals("VIEW_BALANCE") 
            || action.equals("TOP_UP") 
            || action.equals("REDEEM_POINTS")) {
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