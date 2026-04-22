package other;

import java.time.LocalDateTime;
import user.Customer;

public class Transaction {

    private Customer customer;
    private String machineLocation;
    private String slotID;
    private String productName;
    private double charged;
    private String timestamp;
    private boolean successful;

    public Transaction(Customer customer, String machineLocation) {
        setCustomer(customer);
        setMachineLocation(machineLocation);
        setTimestamp(LocalDateTime.now().toString());
        setSuccessful(false);
    }

    public void saveTransaction(String slotID, String productName, double charged) {
        setSlotID(slotID);
        setProductName(productName);
        setCharged(charged);
        setSuccessful(true);
    }

    @Override
    public String toString() {
        return "Transaction{customer='" + customer.getUsername()
                + "', machine='" + machineLocation
                + "', slot='" + slotID
                + "', product='" + productName
                + "', charged=$" + String.format("%.2f", charged)
                + "', successful=" + successful
                + ", time='" + timestamp + "'}";
    }

    // ====== Setters ======
    public void setCustomer(Customer customer) {
        if (customer != null) this.customer = customer;
    }

    public void setMachineLocation(String machineLocation) {
        if (machineLocation != null && !machineLocation.trim().isEmpty())
            this.machineLocation = machineLocation.trim();
    }

    public void setTimestamp(String timestamp)   { this.timestamp = timestamp; }
    public void setSuccessful(boolean successful){ this.successful = successful; }
    public void setSlotID(String slotID)         { this.slotID = slotID; }
    public void setProductName(String productName){ this.productName = productName; }
    public void setCharged(double charged)       { this.charged = charged; }
}