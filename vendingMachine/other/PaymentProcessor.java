package other;

/**
 * Functional Interface for payment processing.
 * A functional interface has exactly one abstract method.
 * The @FunctionalInterface annotation ensures the compiler enforces this rule.
 */
@FunctionalInterface
public interface PaymentProcessor {
    /**
     * Process a payment and return a transaction ID
     * @param customerId The customer making the payment
     * @param amount The amount to charge
     * @return A unique transaction ID
     */
    String processPayment(String customerId, double amount);
}
