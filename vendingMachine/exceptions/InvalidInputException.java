package exceptions;

/**
 * Exception thrown when user input is invalid or malformed.
 * This includes invalid numbers, empty strings, or malformed data.
 */
public class InvalidInputException extends VendingMachineException {
    
    public InvalidInputException(String message) {
        super(message);
    }
    
    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
}
