package other;

import user.Customer;

public class PaymentService {
    private static final double PREMIUM_DISCOUNT = 0.10;
    private static final double REGULAR_DISCOUNT = 0.0;
    private static final double TAX_RATE = 0.08;
    private static final double LOYALTY_POINTS_MULTIPLIER = 0.01;
    private static final double LOYALTY_TIER_1_DISCOUNT = 0.05;
    private static final double LOYALTY_TIER_2_DISCOUNT = 0.03;
    
    // Static counter for generating transaction IDs
    private static int transactionCounter = 0;
    
    /**
     * Demonstrates ANONYMOUS INNER CLASS with PaymentProcessor functional interface.
     * Using anonymous class here is appropriate because:
     * 1. This is a one-time/temporary payment processor for special promotions
     * 2. We don't need to reuse this logic elsewhere
     * 3. Creating a separate named class would add unnecessary file clutter
     * 4. The behavior is specific to this context only
     */
    public static String processWithAnonymousClass(String customerId, double amount) {
        // Anonymous inner class implementing PaymentProcessor
        PaymentProcessor processor = new PaymentProcessor() {
            @Override
            public String processPayment(String customerId, double amount) {
                transactionCounter++;
                String txId = "TXN-ANON-" + System.currentTimeMillis() + "-" + transactionCounter;
                System.out.println("[Anonymous Class] Processing payment for " + customerId);
                System.out.println("Amount: $" + amount + " | Transaction ID: " + txId);
                return txId;
            }
        };
        
        return processor.processPayment(customerId, amount);
    }
    
    /**
     * Demonstrates LAMBDA EXPRESSION replacing the anonymous inner class.
     * The lambda implements the same PaymentProcessor functional interface
     * but with cleaner, more concise syntax.
     * 
     * This works because PaymentProcessor is a functional interface
     * (has exactly one abstract method), so the compiler knows
     * which method the lambda is implementing.
     */
    public static String processWithLambda(String customerId, double amount) {
        // Lambda expression implementing PaymentProcessor
        // (params) -> { implementation }
        PaymentProcessor processor = (id, amt) -> {
            transactionCounter++;
            String txId = "TXN-LAMBDA-" + System.currentTimeMillis() + "-" + transactionCounter;
            System.out.println("[Lambda] Processing payment for " + id);
            System.out.println("Amount: $" + amt + " | Transaction ID: " + txId);
            return txId;
        };
        
        return processor.processPayment(customerId, amount);
    }
    
    /**
     * Simplified lambda with implicit return (single expression)
     * Even shorter syntax when the lambda body is a single expression
     */
    public static String processQuick(String customerId, double amount) {
        PaymentProcessor processor = (id, amt) -> "TXN-QUICK-" + id + "-" + (int)amt;
        return processor.processPayment(customerId, amount);
    }
    
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