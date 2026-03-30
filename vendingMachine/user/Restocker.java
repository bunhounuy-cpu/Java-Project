package user;

import exceptions.*;

public class Restocker extends User {

    private float restockerSalary;

    @Override
    public boolean can(String action) {
        switch (action) {
            case "VIEW_MENU":
            case "VIEW_INVENTORY":
            case "RESTOCK":
            case "ADD_SLOT":
            case "REMOVE_SLOT":
            case "CHANGE_PRODUCT":
            case "PURCHASE":
            case "VIEW_BALANCE":
            case "TOP_UP":
            case "MANAGE_PRODUCTS":
                return true;
            default:
                return false;
        }
    }

    public Restocker(String userId, String fullName, String phone,
                     String username, String password, float salary) throws InvalidInputException {
        super(userId, fullName, phone, username, password);
        setRestockerSalary(salary);
    }

    public float getRestockerSalary() { return restockerSalary; }

    public void setRestockerSalary(float salary) throws InvalidInputException {
        if (salary < 0)
            throw new InvalidInputException("Restocker salary cannot be negative");
        if (salary > 500_000)
            throw new InvalidInputException("Restocker salary too high (max $500,000)");
        this.restockerSalary = salary;
    }

    @Override
    public String toString() {
        return super.toString() + " Restocker[salary=" + restockerSalary + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        Restocker other = (Restocker) obj;
        return Float.floatToIntBits(restockerSalary) == Float.floatToIntBits(other.restockerSalary);
    }
}