package exceptions;

/**
 * Exception thrown when a requested product or slot is not found.
 */
public class ProductNotFoundException extends VendingMachineException {
    
    private String slotId;
    
    public ProductNotFoundException(String message) {
        super(message);
    }
    
    public ProductNotFoundException(String message, String slotId) {
        super(message);
        this.slotId = slotId;
    }
    
    public String getSlotId() {
        return slotId;
    }
}
