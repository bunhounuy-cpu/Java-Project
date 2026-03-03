package other;

import user.Customer;

public class PaymentService {
    private static final double PREMIUM_DISCOUNT = 0.10;
    private static final double REGULAR_DISCOUNT = 0.0;
    private static final double TAX_RATE = 0.08;
    private static final double LOYALTY_POINTS_MULTIPLIER = 0.01;
    private static final double LOYALTY_TIER_1_DISCOUNT = 0.05;
    private static final double LOYALTY_TIER_2_DISCOUNT = 0.03;
    
    public static double computeDiscount(double price, boolean premium) {
        return premium ? price * (1 - PREMIUM_DISCOUNT) : price * (1 - REGULAR_DISCOUNT);
    }
    
    public static double computeTax(double price) {
        return price * TAX_RATE;
    }
    
    public static double computeFinalPrice(double price, boolean premium, int itemsBought) {
        double discountedPrice = computeDiscount(price, premium);
        double tax = computeTax(discountedPrice);
        double finalPrice = discountedPrice + tax;
        
        // Apply loyalty discounts
        if (itemsBought >= 10) finalPrice *= (1 - LOYALTY_TIER_1_DISCOUNT);
        else if (itemsBought >= 5) finalPrice *= (1 - LOYALTY_TIER_2_DISCOUNT);
        
        return finalPrice;
    }
    
    public static double computeWithLoyalty(double price, boolean premium, int itemsBought) {
        return computeFinalPrice(price, premium, itemsBought);
    }
    
    public static int getLoyaltyPoints(double chargedAmount) {
        return (int) Math.floor(chargedAmount * LOYALTY_POINTS_MULTIPLIER);
    }
    
    public static boolean charge(Customer customer, double amount) {
        if (customer == null || !customer.isCardActive()) return false;
        if (amount <= 0) return false; // Validate amount
        
        boolean ok = customer.debit(amount);
        if (!ok) return false;
        
        // Award loyalty points: 1 point per whole dollar charged
        int points = (int) Math.floor(amount);
        if (points > 0) {
            customer.addLoyaltyPoints(points);
        }
        return true;
    }
}