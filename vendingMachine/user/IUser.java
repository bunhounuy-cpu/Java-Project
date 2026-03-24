package user;

public interface IUser {
    String getUserId();
    String getUsername();
    boolean isActive();
    boolean checkPassword(String input);
    String getFullName();
    boolean isPremium();
    double getBalance();
    int getItemsBought();
    
    boolean can(String action);
}