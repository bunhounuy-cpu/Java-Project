package user;

public class Manager extends User {

    private float managerSalary;

    @Override
    public boolean can(String action) {
        // Manager can do everything
        return true;
    }

    // ====== Constructor ======
    public Manager(User u, float salary) {
        super(u.getUserId(), u.getFullName(), u.getPhone(), u.getUsername(), u.getPassword());
        this.setManagerSalary(salary);
    }

    // ====== Manager-Specific Methods ======
    public float getManagerSalary() {
        return managerSalary;
    }

    public void setManagerSalary(float salary) {
        if (salary < 1000) {
            System.out.println("Manager salary cannot be less than $1000!");
        } else {
            this.managerSalary = salary;
        }
    }

    @Override
    public String toString() {
        return super.toString() + "Manager [salary=" + managerSalary + "]";
    }

    @Override
    public String getRole() {
        return "Manager";
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        Manager other = (Manager) obj;
        return Float.floatToIntBits(managerSalary) == Float.floatToIntBits(other.managerSalary);
    }
}