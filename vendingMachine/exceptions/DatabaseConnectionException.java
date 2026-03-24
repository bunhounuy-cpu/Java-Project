package exceptions;

/**
 * Exception thrown when database connection or operations fail.
 */
public class DatabaseConnectionException extends VendingMachineException {
    
    public DatabaseConnectionException(String message) {
        super(message);
    }
    
    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
