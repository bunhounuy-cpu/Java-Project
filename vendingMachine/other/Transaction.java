package other;

import java.time.LocalDateTime;
import java.util.ArrayList;
import user.Customer;

public class Transaction {
    private Customer customer;
    
    private String machineLocation;
    private String slotID;
    private String productName;
    private double charged;
    private String timestamp;
    private boolean successful;
    
    // Snapshot lists of machine prices and quantities at time of transaction
    private ArrayList<Double> snapshotPrices;
    private ArrayList<Integer> snapshotQty;
    
    public Transaction(Customer customer, String machineLocation, ArrayList<Slot> slots) {
        setCustomer(customer);  // User input - use setter
        setMachineLocation(machineLocation);  // System field - use setter
        setTimestamp(LocalDateTime.now().toString());  // System field - use setter
        setSuccessful(false);  // System field - use setter
        
        // System fields - direct assignment
        snapshotPrices = new ArrayList<>();
        snapshotQty = new ArrayList<>();
        
        // Capture machine state snapshot
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i) != null) {
                snapshotPrices.add(slots.get(i).getProduct().getPrice());
                snapshotQty.add(slots.get(i).getQuantity());
            }
        }
    }
    
    public void saveTransaction(String slotID, String productName, double charged) {
        this.slotID = slotID;
        this.productName = productName;
        this.charged = charged;
        this.successful = true;
    }
    
    public String toString() {
        return "Transaction{customer='" + customer.getUsername() + "', machine='" + machineLocation + "', slot='" + slotID + "', product='" + productName + "', charged=$" + charged + ", successful=" + successful + ", time='" + timestamp + "'}";
    }
    
    // ====== Setters ======
    public void setCustomer(Customer customer) {
        if (customer != null) {
            this.customer = customer;
        }
    }

    public void setMachineLocation(String machineLocation) {
        if (machineLocation != null && !machineLocation.trim().isEmpty()) {
            this.machineLocation = machineLocation.trim();
        }
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public void printSnapshot() {
        System.out.println("--- Snapshot (prices, qty) ---");
        for (int i = 0; i < snapshotPrices.size() && i < snapshotQty.size(); i++) {
            System.out.println("Index " + i + ": $" + snapshotPrices.get(i) + ", qty=" + snapshotQty.get(i));
        }
    }
}
