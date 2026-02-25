public class Manager implements IUser {
    private String id;
    private String username;        // Nickname
    private String password;
    private String name;            // Real Name
    private String office;
    
    public Manager(String id, String username, String password, String name, String office) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.office = office;
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
        return "MANAGER";
    }
    
    @Override
    public boolean can(String action) {
        switch (action) {
            case VendingMachine.VIEW_REVENUE:
            case VendingMachine.MANAGE_PRODUCTS:
            case VendingMachine.VIEW_TRANSACTIONS:
            case VendingMachine.VIEW_MENU:
            case VendingMachine.POWER_CONTROL:
            case VendingMachine.RESTOCK:
                return true;
            case VendingMachine.PURCHASE:
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
    
    public String getOffice() {
        return office;
    }
    
    @Override
    public String toString() {
        return "Manager{id='" + id + "', username='" + username + "', name='" + name + "', office='" + office + "'}";
    }
}
