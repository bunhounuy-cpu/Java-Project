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
    int getLoyaltyPoints();
    
    boolean can(String action);
}