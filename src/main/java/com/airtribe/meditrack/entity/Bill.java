package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.constants.Constants;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.interfaces.Payable;
import com.airtribe.meditrack.util.DateUtil;
import com.airtribe.meditrack.util.Validator;
import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.entity.MedicalEntity;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Bill class for managing payments.
 * Implements Payable interface demonstrating polymorphism.
 * Supports different billing strategies.
 *
 * @author MediTrack Team
 * @version 1.0
 */
public class Bill extends MedicalEntity implements Payable {

    private static final long serialVersionUID = 1L;

    private Appointment appointment;
    private double consultationFee;
    private double additionalCharges;
    private double discount;
    private boolean paid;
    private LocalDateTime billDate;
    private LocalDateTime paymentDate;
    private String paymentMethod;

    /**
     * Default constructor
     */
    public Bill() {
        super();
        this.billDate = LocalDateTime.now();
        this.paid = false;
    }

    /**
     * Constructor with appointment
     *
     * @param id bill ID
     * @param appointment the appointment
     * @throws InvalidDataException if validation fails
     */
    public Bill(String id, Appointment appointment) throws InvalidDataException {
        this();
        setId(id);
        setAppointment(appointment);

        // Auto-set consultation fee from doctor
        if (appointment != null && appointment.getDoctor() != null) {
            this.consultationFee = appointment.getDoctor().getConsultationFee();
        }
    }

    /**
     * Get appointment
     * @return appointment
     */
    public Appointment getAppointment() {
        return appointment;
    }

    /**
     * Set appointment
     * @param appointment the appointment
     * @throws InvalidDataException if validation fails
     */
    public void setAppointment(Appointment appointment) throws InvalidDataException {
        Validator.validateNotNull(appointment, "Appointment");
        this.appointment = appointment;
        updateModifiedTimestamp();
    }

    /**
     * Get consultation fee
     * @return consultation fee
     */
    public double getConsultationFee() {
        return consultationFee;
    }

    /**
     * Set consultation fee
     * @param consultationFee the fee
     * @throws InvalidDataException if validation fails
     */
    public void setConsultationFee(double consultationFee) throws InvalidDataException {
        Validator.validateAmount(consultationFee, "Consultation fee");
        this.consultationFee = consultationFee;
        updateModifiedTimestamp();
    }

    /**
     * Get additional charges
     * @return additional charges
     */
    public double getAdditionalCharges() {
        return additionalCharges;
    }

    /**
     * Set additional charges
     * @param additionalCharges the charges
     * @throws InvalidDataException if validation fails
     */
    public void setAdditionalCharges(double additionalCharges) throws InvalidDataException {
        Validator.validateAmount(additionalCharges, "Additional charges");
        this.additionalCharges = additionalCharges;
        updateModifiedTimestamp();
    }

    /**
     * Get discount
     * @return discount amount
     */
    public double getDiscount() {
        return discount;
    }

    /**
     * Set discount
     * @param discount the discount amount
     * @throws InvalidDataException if validation fails
     */
    public void setDiscount(double discount) throws InvalidDataException {
        Validator.validateAmount(discount, "Discount");
        this.discount = discount;
        updateModifiedTimestamp();
    }

    /**
     * Check if bill is paid
     * @return true if paid
     */
    public boolean isPaid() {
        return paid;
    }

    /**
     * Mark bill as paid
     * @param paymentMethod the payment method used
     */
    public void markAsPaid(String paymentMethod) {
        this.paid = true;
        this.paymentDate = LocalDateTime.now();
        this.paymentMethod = paymentMethod;
        updateModifiedTimestamp();
    }

    /**
     * Get bill date
     * @return bill date
     */
    public LocalDateTime getBillDate() {
        return billDate;
    }

    /**
     * Get payment date
     * @return payment date
     */
    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    /**
     * Get payment method
     * @return payment method
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    // Implement Payable interface

    @Override
    public double calculateBaseAmount() {
        return consultationFee + additionalCharges - discount;
    }

    @Override
    public double calculateTax() {
        return calculateBaseAmount() * Constants.TAX_RATE;
    }

    @Override
    public double calculateTotalAmount() {
        return calculateBaseAmount() + calculateTax();
    }

    @Override
    public double getTaxRate() {
        return Constants.TAX_RATE;
    }

    @Override
    public String getPayableDescription() {
        if (appointment != null && appointment.getPatient() != null) {
            return "Medical consultation for " + appointment.getPatient().getName();
        }
        return "Medical consultation";
    }

    @Override
    public String getPayableId() {
        return getId();
    }

    /**
     * Generate bill with different strategies (method overloading for polymorphism)
     * @return formatted bill string
     */
    public String generateBill() {
        return generateBill(false);
    }

    /**
     * Generate bill with optional details (polymorphism through overloading)
     * @param detailed whether to include detailed breakdown
     * @return formatted bill string
     */
    public String generateBill(boolean detailed) {
        StringBuilder bill = new StringBuilder();
        bill.append("\n");
        bill.append("Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â\n");
        bill.append("                    MEDICAL BILL                        \n");
        bill.append("Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â\n");
        bill.append(String.format("Bill ID: %s\n", getId()));
        bill.append(String.format("Date: %s\n", DateUtil.formatForDisplay(billDate)));

        if (appointment != null) {
            if (appointment.getPatient() != null) {
                bill.append(String.format("Patient: %s\n", appointment.getPatient().getName()));
            }
            if (appointment.getDoctor() != null) {
                bill.append(String.format("Doctor: Dr. %s\n", appointment.getDoctor().getName()));
            }
        }

        bill.append("Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬\n");

        if (detailed) {
            bill.append(String.format("Consultation Fee:     %s\n", formatAmount(consultationFee)));
            if (additionalCharges > 0) {
                bill.append(String.format("Additional Charges:   %s\n", formatAmount(additionalCharges)));
            }
            if (discount > 0) {
                bill.append(String.format("Discount:           - %s\n", formatAmount(discount)));
            }
            bill.append("                      Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬\n");
            bill.append(String.format("Subtotal:             %s\n", formatAmount(calculateBaseAmount())));
            bill.append(String.format("Tax (%.0f%%):            %s\n",
                getTaxRate() * 100, formatAmount(calculateTax())));
            bill.append("                      Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬\n");
        }

        bill.append(String.format("TOTAL AMOUNT:         %s\n", formatAmount(calculateTotalAmount())));
        bill.append("Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬\n");
        bill.append(String.format("Status: %s\n", paid ? "PAID" : "UNPAID"));

        if (paid && paymentDate != null) {
            bill.append(String.format("Payment Date: %s\n", DateUtil.formatForDisplay(paymentDate)));
            if (paymentMethod != null) {
                bill.append(String.format("Payment Method: %s\n", paymentMethod));
            }
        }

        bill.append("Ã¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢ÂÃ¢â€¢Â\n");

        return bill.toString();
    }

    @Override
    public String getEntityType() {
        return "Bill";
    }

    @Override
    public String getDisplayInfo() {
        return String.format("Bill %s - Amount: %s [%s]",
            getId(),
            formatAmount(calculateTotalAmount()),
            paid ? "PAID" : "UNPAID");
    }

    @Override
    public boolean validate() {
        return super.validate() &&
               appointment != null &&
               consultationFee >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Bill bill = (Bill) o;
        return Objects.equals(appointment, bill.appointment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), appointment);
    }

    @Override
    public String toString() {
        return String.format("Bill[ID=%s, Amount=Ã¢â€šÂ¹%.2f, Status=%s]",
            getId(), calculateTotalAmount(), paid ? "PAID" : "UNPAID");
    }
}


