package user;

public class Manager extends User {

    private float salary;

    @Override
    public boolean can(String action) {
        return true; // Manager can do everything
    }

    // ====== Constructor ======
    public Manager(User u, float salary) {
        super(u.getUserId(), u.getFullName(), u.getPhone(), u.getUsername(), u.getPassword());
        this.setSalary(salary);
    }

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        if(salary < 1000) {
            System.out.println("Salary can not be negative!");
        } else {
            this.salary = salary;
        }
    }

    @Override
    public String toString() {
        return super.toString() + "ManagerStaff [\"Position: Manager salary=" + salary + "]";
    }
    
    @Override
    public String getRole() {
        return "Manager";
    }

    @Override
    public boolean equals(Object obj) {
        Manager other = (Manager) obj;
        
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
