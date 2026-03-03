package user;
import controller.VendingMachine;

public class Customer extends User {

    @Override
    public boolean can(String action) {
        switch (action) {
            case VendingMachine.PURCHASE:
            case VendingMachine.VIEW_MENU:
            case VendingMachine.VIEW_BALANCE:
            case VendingMachine.TOP_UP:
            case VendingMachine.REDEEM_POINTS:
                return true;
            case VendingMachine.RESTOCK:
            case VendingMachine.VIEW_REVENUE:
            case VendingMachine.MANAGE_PRODUCTS:
            case VendingMachine.VIEW_TRANSACTIONS:
            case VendingMachine.VIEW_INVENTORY:
                return false;
            default:
                return false;
        }
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