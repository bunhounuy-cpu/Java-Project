package user;

import exceptions.*;

public class Restocker extends User {

    private float restockerSalary;

    @Override
    public boolean can(String action) {
        if (action.equals("VIEW_MENU") 
            || action.equals("RESTOCK") 
            || action.equals("VIEW_INVENTORY") 
            || action.equals("PURCHASE") 
            || action.equals("VIEW_BALANCE") 
            || action.equals("TOP_UP") 
            || action.equals("REDEEM_POINTS") 
            || action.equals("MANAGE_PRODUCTS")) {
            return true;
        }
        return false;
    }

    // ====== Constructor ======
    public Restocker(String userId, String fullName, String phone,
                    String username, String password, float salary) throws InvalidInputException {
        super(userId, fullName, phone, username, password);
        setRestockerSalary(salary);
    }

    public float getRestockerSalary() {
        return restockerSalary;
    }

    public void setRestockerSalary(float salary) throws InvalidInputException {
        if(salary < 0) {
            throw new InvalidInputException("Restocker salary cannot be negative");
        } else if (salary > 500000) {
            throw new InvalidInputException("Restocker salary too high (max $500,000)");
        } else {
            this.restockerSalary = salary;
        }
    }

    @Override
    public String toString() {
        return super.toString() + 
                "Restocker [salary=" + restockerSalary +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        
        Restocker other = (Restocker) obj;
        return Float.floatToIntBits(restockerSalary) == Float.floatToIntBits(other.restockerSalary);
    }
}