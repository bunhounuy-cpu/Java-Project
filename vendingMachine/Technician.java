public class Technician implements IUser {
    private String id;
    private String username;
    private String password;
    private String name;
    private String department;
    
    public Technician(String id, String username, String password, String name, String department) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.department = department;
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
        return "TECHNICIAN";
    }
    
    @Override
    public boolean can(String action) {
        switch (action) {
            case VendingMachine.RESTOCK:
            case VendingMachine.VIEW_MENU:
            case VendingMachine.POWER_CONTROL:
            case VendingMachine.VIEW_TRANSACTIONS:
                return true;
            case VendingMachine.PURCHASE:
            case VendingMachine.VIEW_REVENUE:
            case VendingMachine.MANAGE_PRODUCTS:
            case VendingMachine.VIEW_BALANCE:
            case VendingMachine.TOP_UP:
            case VendingMachine.REDEEM_POINTS:
                return false;
            default:
                return false;
        }
    }
    
    public String getName() {
        return name;
    }
    
    public String getDepartment() {
        return department;
    }
    
    @Override
    public String toString() {
        return "Technician{id='" + id + "', username='" + username + "', name='" + name + "', department='" + department + "'}";
    }
}
