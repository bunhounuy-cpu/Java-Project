package other;

import user.Customer;
import exceptions.*;

public class PaymentService {
    private static final double PREMIUM_DISCOUNT = 0.10;
    private static final double REGULAR_DISCOUNT = 0.0;
    private static final double TAX_RATE = 0.08;
    private static final double LOYALTY_TIER_1_DISCOUNT = 0.05;
    private static final double LOYALTY_TIER_2_DISCOUNT = 0.03;
    
    public static double computeDiscount(double price, boolean premium) throws InvalidInputException {
        if (price < 0) {
            throw new InvalidInputException("Price cannot be negative");
        }
        return premium ? price * (1 - PREMIUM_DISCOUNT) : price * (1 - REGULAR_DISCOUNT);
    }
    
    public static double computeTax(double price) throws InvalidInputException {
        if (price < 0) {
            throw new InvalidInputException("Price cannot be negative");
        }
        return price * TAX_RATE;
    }
    
    public static double computeFinalPrice(double price, boolean premium, int itemsBought) throws InvalidInputException {
        if (price < 0) {
            throw new InvalidInputException("Price cannot be negative");
        }
        if (itemsBought < 0) {
            throw new InvalidInputException("Items bought cannot be negative");
        }
        
        double discountedPrice = computeDiscount(price, premium);
        double tax = computeTax(discountedPrice);
        double finalPrice = discountedPrice + tax;
        
        // Apply loyalty discounts based on items bought
        if (itemsBought >= 10) finalPrice *= (1 - LOYALTY_TIER_1_DISCOUNT);
        else if (itemsBought >= 5) finalPrice *= (1 - LOYALTY_TIER_2_DISCOUNT);
        
        return finalPrice;
    }
    
    public static double computeWithLoyalty(double price, boolean premium, int itemsBought) throws InvalidInputException {
        return computeFinalPrice(price, premium, itemsBought);
    }
    
    public static boolean charge(Customer customer, double amount) throws InsufficientFundsException, InvalidInputException {
        if (customer == null) {
            throw new InvalidInputException("Customer cannot be null");
        }
        if (!customer.isCardActive()) {
            throw new InvalidInputException("Customer card is not active");
        }
        if (amount <= 0) {
            throw new InvalidInputException("Amount must be positive");
        }
        
        if (customer.getBalance() < amount) {
            throw new InsufficientFundsException("Insufficient funds for payment", amount, customer.getBalance());
        }
        
        boolean ok = customer.debit(amount);
        if (!ok) {
            throw new InsufficientFundsException("Payment processing failed", amount, customer.getBalance());
        }
        
        return true;
    }
}