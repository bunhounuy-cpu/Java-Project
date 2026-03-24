package exceptions;

/**
 * Exception thrown when a user has insufficient funds for a purchase.
 */
public class InsufficientFundsException extends VendingMachineException {
    
    private double requestedAmount;
    private double availableBalance;
    
    public InsufficientFundsException(String message, double requestedAmount, double availableBalance) {
        super(message);
        this.requestedAmount = requestedAmount;
        this.availableBalance = availableBalance;
    }
    
    public double getRequestedAmount() {
        return requestedAmount;
    }
    
    public double getAvailableBalance() {
        return availableBalance;
    }
}
