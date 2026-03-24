package exceptions;

/**
 * Base exception class for all vending machine related errors.
 * This is a custom exception that serves as the parent class for all
 * vending machine specific exceptions.
 */
public class VendingMachineException extends Exception {
    
    public VendingMachineException(String message) {
        super(message);
    }
    
    public VendingMachineException(String message, Throwable cause) {
        super(message, cause);
    }
}
