package com.airtribe.meditrack.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Immutable BillSummary class.
 * Demonstrates immutability pattern - thread-safe, no setters, final fields.
 * Used for read-only bill summary reports.
 *
 * @author MediTrack Team
 * @version 1.0
 */
public final class BillSummary implements Serializable {

    private static final long serialVersionUID = 1L;

    // All fields are final - cannot be modified after construction
    private final String billId;
    private final String patientName;
    private final String doctorName;
    private final double totalAmount;
    private final double taxAmount;
    private final boolean paid;
    private final LocalDateTime billDate;
    private final LocalDateTime paymentDate;

    /**
     * Constructor - all fields must be provided at creation time
     * This is the only way to set field values (no setters)
     *
     * @param billId bill ID
     * @param patientName patient name
     * @param doctorName doctor name
     * @param totalAmount total amount
     * @param taxAmount tax amount
     * @param paid payment status
     * @param billDate bill date
     * @param paymentDate payment date (can be null if unpaid)
     */
    public BillSummary(String billId, String patientName, String doctorName,
                      double totalAmount, double taxAmount, boolean paid,
                      LocalDateTime billDate, LocalDateTime paymentDate) {
        // Defensive copying for mutable objects
        this.billId = billId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.totalAmount = totalAmount;
        this.taxAmount = taxAmount;
        this.paid = paid;
        // LocalDateTime is immutable, so direct assignment is safe
        this.billDate = billDate;
        this.paymentDate = paymentDate;
    }

    /**
     * Factory method to create BillSummary from Bill
     * @param bill the bill to summarize
     * @return immutable BillSummary
     */
    public static BillSummary fromBill(Bill bill) {
        if (bill == null) {
            throw new IllegalArgumentException("Bill cannot be null");
        }

        String patientName = "N/A";
        String doctorName = "N/A";

        if (bill.getAppointment() != null) {
            if (bill.getAppointment().getPatient() != null) {
                patientName = bill.getAppointment().getPatient().getName();
            }
            if (bill.getAppointment().getDoctor() != null) {
                doctorName = "Dr. " + bill.getAppointment().getDoctor().getName();
            }
        }

        return new BillSummary(
            bill.getId(),
            patientName,
            doctorName,
            bill.calculateTotalAmount(),
            bill.calculateTax(),
            bill.isPaid(),
            bill.getBillDate(),
            bill.getPaymentDate()
        );
    }

    // Only getters, no setters - immutability enforced

    /**
     * Get bill ID
     * @return bill ID
     */
    public String getBillId() {
        return billId;
    }

    /**
     * Get patient name
     * @return patient name
     */
    public String getPatientName() {
        return patientName;
    }

    /**
     * Get doctor name
     * @return doctor name
     */
    public String getDoctorName() {
        return doctorName;
    }

    /**
     * Get total amount
     * @return total amount
     */
    public double getTotalAmount() {
        return totalAmount;
    }

    /**
     * Get tax amount
     * @return tax amount
     */
    public double getTaxAmount() {
        return taxAmount;
    }

    /**
     * Check if paid
     * @return true if paid
     */
    public boolean isPaid() {
        return paid;
    }

    /**
     * Get bill date (defensive copy)
     * @return bill date (immutable LocalDateTime)
     */
    public LocalDateTime getBillDate() {
        // LocalDateTime is immutable, so we can return it directly
        return billDate;
    }

    /**
     * Get payment date (defensive copy)
     * @return payment date (can be null)
     */
    public LocalDateTime getPaymentDate() {
        // LocalDateTime is immutable, so we can return it directly
        return paymentDate;
    }

    /**
     * Get base amount (total - tax)
     * @return base amount
     */
    public double getBaseAmount() {
        return totalAmount - taxAmount;
    }

    /**
     * Get formatted summary string
     * @return formatted summary
     */
    public String getSummary() {
        return String.format(
            "Bill %s: %s (Patient: %s, Doctor: %s) - Ã¢â€šÂ¹%.2f [%s]",
            billId,
            paid ? "PAID" : "UNPAID",
            patientName,
            doctorName,
            totalAmount,
            billDate != null ? billDate.toLocalDate().toString() : "N/A"
        );
    }

    /**
     * Create a new BillSummary with updated payment status
     * Since class is immutable, this creates a new instance
     * @param newPaid new payment status
     * @param newPaymentDate new payment date
     * @return new BillSummary instance
     */
    public BillSummary withPaymentStatus(boolean newPaid, LocalDateTime newPaymentDate) {
        return new BillSummary(
            this.billId,
            this.patientName,
            this.doctorName,
            this.totalAmount,
            this.taxAmount,
            newPaid,
            this.billDate,
            newPaymentDate
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BillSummary that = (BillSummary) o;
        return Double.compare(that.totalAmount, totalAmount) == 0 &&
               Double.compare(that.taxAmount, taxAmount) == 0 &&
               paid == that.paid &&
               Objects.equals(billId, that.billId) &&
               Objects.equals(patientName, that.patientName) &&
               Objects.equals(doctorName, that.doctorName) &&
               Objects.equals(billDate, that.billDate) &&
               Objects.equals(paymentDate, that.paymentDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(billId, patientName, doctorName, totalAmount,
                          taxAmount, paid, billDate, paymentDate);
    }

    @Override
    public String toString() {
        return String.format(
            "BillSummary[ID=%s, Patient=%s, Doctor=%s, Total=Ã¢â€šÂ¹%.2f, Paid=%s]",
            billId, patientName, doctorName, totalAmount, paid
        );
    }
}


