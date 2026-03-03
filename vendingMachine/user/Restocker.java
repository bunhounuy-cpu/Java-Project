package user;

import controller.VendingMachine;

public class Restocker extends User {

    private float salary;

    @Override
    public boolean can(String action) {
        switch (action) {
            case VendingMachine.RESTOCK:
            case VendingMachine.VIEW_MENU:
            case VendingMachine.VIEW_INVENTORY:
            case VendingMachine.PURCHASE:
            case VendingMachine.VIEW_BALANCE:
            case VendingMachine.TOP_UP:
            case VendingMachine.REDEEM_POINTS:
            case VendingMachine.MANAGE_PRODUCTS:
                return true;
            case VendingMachine.VIEW_REVENUE:
            case VendingMachine.VIEW_TRANSACTIONS:
                return false;
            default:
                return false;
        }
    }

    // ====== Constructor ======
    public Restocker(User u, float salary) {
        super(u.getUserId(), u.getFullName(), u.getPhone(), u.getUsername(), u.getPassword());
        this.setSalary(salary);
    }

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        if(salary < 0) {
            System.out.println("Salary can not be negative!");
        } else {
            this.salary = salary;
        }
    }

    @Override
    public String toString() {
        return super.toString() + 
                ", salary=" + salary +
                '}';
    }
    
    @Override
    public String getRole() {
        return "Restocker";
    }

    @Override
    public boolean equals(Object obj) {
        Restocker other = (Restocker) obj;
        
        if (!super.equals(obj)) {
            return false;
        } else {
            if (Float.floatToIntBits(salary) != Float.floatToIntBits(other.salary)) {
                return false;
            }
            return true;
        }
    }
}
