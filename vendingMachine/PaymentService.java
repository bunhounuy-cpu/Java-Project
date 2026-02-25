public class PaymentService {
    private static final double PREMIUM_DISCOUNT = 0.10;
    private static final double REGULAR_DISCOUNT = 0.0;
    private static final double TAX_RATE = 0.08;
    private static final double LOYALTY_POINTS_MULTIPLIER = 0.01;
    
    public static double computeDiscount(double price, boolean premium) {
        return premium ? price * (1 - PREMIUM_DISCOUNT) : price * (1 - REGULAR_DISCOUNT);
    }
    
    public static double computeTax(double price) {
        return price * TAX_RATE;
    }
    
    public static double compute(double price, boolean premium) {
        double discountedPrice = computeDiscount(price, premium);
        double tax = computeTax(discountedPrice);
        return discountedPrice + tax;
    }
    
    public static double computeWithLoyalty(double price, boolean premium, int itemsBought) {
        double basePrice = compute(price, premium);
        if (itemsBought >= 10) basePrice *= 0.95;
        else if (itemsBought >= 5) basePrice *= 0.97;
        return basePrice;
    }
    
    public static double getLoyaltyPoints(double chargedAmount) {
        return Math.round(chargedAmount * LOYALTY_POINTS_MULTIPLIER * 100.0) / 100.0;
    }
    
    public static boolean charge(Customer customer, double amount) {
        if (customer == null || !customer.isCardActive()) return false;
        boolean ok = customer.debit(amount);
        if (!ok) return false;
        // Award integer loyalty points: 1 point per whole dollar charged
        int points = (int) Math.floor(amount);
        if (points > 0) {
            customer.addLoyaltyPoints(points);
        }
        return true;
    }
}