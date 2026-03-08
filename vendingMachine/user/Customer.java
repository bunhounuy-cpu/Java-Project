package user;
import controller.VendingMachine;

public class Customer extends User {

    @Override
    public boolean can(String action) {
        if (VendingMachine.PURCHASE.equals(action) ||
            VendingMachine.VIEW_MENU.equals(action) ||
            VendingMachine.VIEW_BALANCE.equals(action) ||
            VendingMachine.TOP_UP.equals(action) ||
            VendingMachine.REDEEM_POINTS.equals(action)) {
            return true;
        }
        return false;
    }

    // ====== Constructor ======
    public Customer(User u) {
        super(u.getUserId(), u.getFullName(), u.getPhone(), u.getUsername(), u.getPassword());
    }

    @Override
    public String toString() {
        return super.toString() + 
                ", role=Customer" +
                '}';
    }
    
    @Override
    public String getRole() {
        return "Customer";
    }

    @Override
    public boolean equals(Object obj) {
        Customer other = (Customer) obj;
        
        if (!super.equals(obj)) {
            return false;
        } else {
            return true; // Customers only need base class equality
        }
    }
}