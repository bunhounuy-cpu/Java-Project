package exceptions;

/**
 * Exception thrown when there is insufficient stock for a requested purchase.
 */
public class InsufficientStockException extends VendingMachineException {
    
    private String slotId;
    private int requestedQuantity;
    private int availableQuantity;
    
    public InsufficientStockException(String message, String slotId, int requestedQuantity, int availableQuantity) {
        super(message);
        this.slotId = slotId;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }
    
    public String getSlotId() {
        return slotId;
    }
    
    public int getRequestedQuantity() {
        return requestedQuantity;
    }
    
    public int getAvailableQuantity() {
        return availableQuantity;
    }
}
