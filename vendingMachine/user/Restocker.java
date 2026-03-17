package user;

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
                    String username, String password, float salary) {
        super(userId, fullName, phone, username, password);
        setRestockerSalary(salary);
    }

    public float getRestockerSalary() {
        return restockerSalary;
    }

    public void setRestockerSalary(float salary) {
        if(salary < 0) {
            System.out.println("Restocker salary cannot be less than zero!");
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