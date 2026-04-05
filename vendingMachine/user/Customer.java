package user;

import exceptions.*;

public class Customer extends User {

    private String email; 

    public Customer(String userId, String fullName, String phone, String username, String password, String email) throws InvalidInputException {
        super(userId, fullName, phone, username, password);
        setEmail(email);
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email) throws InvalidInputException {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidInputException("Email cannot be empty");
        }
        
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!email.matches(emailRegex)) {
            throw new InvalidInputException("Invalid email format");
        }
        
        if (email.length() > 100) {
            throw new InvalidInputException("Email too long (max 100 characters)");
        }
        
        this.email = email.trim();
    }
    
    @Override
    public boolean can(String action) {
        if (action.equals("PURCHASE") 
            || action.equals("VIEW_MENU") 
            || action.equals("VIEW_BALANCE") 
            || action.equals("TOP_UP")) {
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
        try {
            setBalance(getBalance() - amount);
            return true;
        } catch (InvalidInputException e) {
            // This should not happen since we're subtracting, but handle it anyway
            System.out.println("Error during debit: " + e.getMessage());
            return false;
        }
    }
    
    public void incrementItems() throws InvalidInputException {
        setItemsBought(getItemsBought() + 1);
    }
}