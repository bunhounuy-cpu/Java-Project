package exceptions;

/**
 * Exception thrown when authentication fails (invalid username/password).
 */
public class AuthenticationException extends VendingMachineException {
    
    private String username;
    
    public AuthenticationException(String message) {
        super(message);
    }
    
    public AuthenticationException(String message, String username) {
        super(message);
        this.username = username;
    }
    
    public String getUsername() {
        return username;
    }
}
