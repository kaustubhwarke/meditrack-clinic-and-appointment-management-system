package com.airtribe.meditrack.interfaces;

/**
 * Interface for entities that can generate and process payments/bills.
 * Demonstrates interface with default methods.
 *
 * @author MediTrack Team
 * @version 1.0
 */
public interface Payable {

    /**
     * Calculate the base amount before tax.
     * @return base amount
     */
    double calculateBaseAmount();

    /**
     * Calculate tax amount.
     * Default implementation uses standard tax rate.
     * @return tax amount
     */
    default double calculateTax() {
        return calculateBaseAmount() * getTaxRate();
    }

    /**
     * Calculate total amount including tax.
     * Default implementation adds base amount and tax.
     * @return total amount
     */
    default double calculateTotalAmount() {
        return calculateBaseAmount() + calculateTax();
    }

    /**
     * Get the tax rate applicable to this payable entity.
     * Default implementation returns standard rate.
     * @return tax rate as decimal (e.g., 0.18 for 18%)
     */
    default double getTaxRate() {
        return 0.18; // 18% GST
    }

    /**
     * Get description of the payable item.
     * @return description
     */
    String getPayableDescription();

    /**
     * Get the unique identifier for this payable entity.
     * @return unique ID
     */
    String getPayableId();

    /**
     * Check if payment has been made.
     * Default implementation returns false (unpaid).
     * @return true if paid, false otherwise
     */
    default boolean isPaid() {
        return false;
    }

    /**
     * Get formatted amount string.
     * Default helper method for display purposes.
     * @param amount the amount to format
     * @return formatted string with currency symbol
     */
    default String formatAmount(double amount) {
        return String.format("Ã¢â€šÂ¹%.2f", amount);
    }

    /**
     * Get payment summary as formatted string.
     * Default implementation provides standard format.
     * @return payment summary
     */
    default String getPaymentSummary() {
        return String.format(
            "Payment Summary:\n" +
            "  Description: %s\n" +
            "  Base Amount: %s\n" +
            "  Tax (%.0f%%): %s\n" +
            "  Total: %s\n" +
            "  Status: %s",
            getPayableDescription(),
            formatAmount(calculateBaseAmount()),
            getTaxRate() * 100,
            formatAmount(calculateTax()),
            formatAmount(calculateTotalAmount()),
            isPaid() ? "PAID" : "UNPAID"
        );
    }
}


