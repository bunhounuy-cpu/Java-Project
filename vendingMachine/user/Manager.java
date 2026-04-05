package user;

import exceptions.*;

public class Manager extends User {

    private float managerSalary;

    // Manager can do everything
    @Override
    public boolean can(String action) {
        return true;
    }

    public Manager(String userId, String fullName, String phone, String username, String password, float salary) throws InvalidInputException {
        super(userId, fullName, phone, username, password);
        setManagerSalary(salary);
    }

    public float getManagerSalary() {
        return managerSalary;
    }

    public void setManagerSalary(float salary) throws InvalidInputException {
        if (salary < 1000)
            throw new InvalidInputException("Manager salary cannot be less than $1000");
        if (salary > 1_000_000)
            throw new InvalidInputException("Manager salary too high (max $1,000,000)");
        this.managerSalary = salary;
    }

    @Override
    public String toString() {
        return super.toString() + " Manager[salary=" + managerSalary + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        Manager other = (Manager) obj;
        return Float.floatToIntBits(managerSalary) == Float.floatToIntBits(other.managerSalary);
    }
}