package user;

public interface IUser {
    String getUserId();
    String getUsername();
    boolean isActive();
    boolean checkPassword(String input);
    String getFullName();
    boolean isPremium();
    double getBalance();
    String getRole();
    
    boolean can(String action);
}