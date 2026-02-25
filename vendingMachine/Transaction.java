import java.time.LocalDateTime;
import java.util.ArrayList;

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
        this.customer = customer;
        this.machineLocation = machineLocation;
        this.timestamp = LocalDateTime.now().toString();
        this.successful = false;
        snapshotPrices = new ArrayList<>();
        snapshotQty = new ArrayList<>();
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i) != null) {
                snapshotPrices.add(slots.get(i).getProduct().getPrice());
                snapshotQty.add(slots.get(i).getQuantity());
            }
        }
    }
    
    public void record(String slotID, String productName, double charged) {
        this.slotID = slotID;
        this.productName = productName;
        this.charged = charged;
        this.timestamp = LocalDateTime.now().toString();
        this.successful = true;
    }
    
    @Override
    public String toString() {
        return "Transaction{customer='" + customer.getUsername() + "', machine='" + machineLocation + "', slot='" + slotID + "', product='" + productName + "', charged=$" + charged + ", successful=" + successful + ", time='" + timestamp + "'}";
    }
    
    public void printSnapshot() {
        System.out.println("--- Snapshot (prices, qty) ---");
        for (int i = 0; i < snapshotPrices.size() && i < snapshotQty.size(); i++) {
            System.out.println("Index " + i + ": $" + snapshotPrices.get(i) + ", qty=" + snapshotQty.get(i));
        }
    }
}
