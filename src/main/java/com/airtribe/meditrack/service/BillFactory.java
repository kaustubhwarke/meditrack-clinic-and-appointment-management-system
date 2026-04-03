package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.entity.Bill;

/**
 * Factory class for creating different types of bills.
 * Demonstrates Factory Design Pattern.
 *
 * @author MediTrack Team
 * @version 1.0
 */
public class BillFactory {

    /**
     * Bill types enum
     */
    public enum BillType {
        STANDARD,
        EMERGENCY,
        FOLLOW_UP,
        CONSULTATION_ONLY
    }

    /**
     * Create a bill based on type
     * Factory method demonstrating Factory Pattern
     *
     * @param type bill type
     * @param billId bill ID
     * @param appointment appointment
     * @return created bill
     * @throws Exception if creation fails
     */
    public static Bill createBill(BillType type, String billId, Appointment appointment)
            throws Exception {
        Bill bill = new Bill(billId, appointment);

        double baseFee = appointment.getDoctor() != null ?
            appointment.getDoctor().getConsultationFee() : 500.0;

        switch (type) {
            case STANDARD:
                // Standard bill - no modifications
                bill.setConsultationFee(baseFee);
                break;

            case EMERGENCY:
                // Emergency - 50% surcharge
                bill.setConsultationFee(baseFee * 1.5);
                bill.setAdditionalCharges(200.0); // Emergency handling fee
                break;

            case FOLLOW_UP:
                // Follow-up - 30% discount
                bill.setConsultationFee(baseFee);
                bill.setDiscount(baseFee * 0.3);
                break;

            case CONSULTATION_ONLY:
                // Consultation only - base fee only
                bill.setConsultationFee(baseFee);
                bill.setAdditionalCharges(0);
                break;

            default:
                bill.setConsultationFee(baseFee);
        }

        return bill;
    }

    /**
     * Create standard bill
     * @param billId bill ID
     * @param appointment appointment
     * @return standard bill
     * @throws Exception if creation fails
     */
    public static Bill createStandardBill(String billId, Appointment appointment)
            throws Exception {
        return createBill(BillType.STANDARD, billId, appointment);
    }

    /**
     * Create emergency bill
     * @param billId bill ID
     * @param appointment appointment
     * @return emergency bill
     * @throws Exception if creation fails
     */
    public static Bill createEmergencyBill(String billId, Appointment appointment)
            throws Exception {
        return createBill(BillType.EMERGENCY, billId, appointment);
    }

    /**
     * Create follow-up bill
     * @param billId bill ID
     * @param appointment appointment
     * @return follow-up bill
     * @throws Exception if creation fails
     */
    public static Bill createFollowUpBill(String billId, Appointment appointment)
            throws Exception {
        return createBill(BillType.FOLLOW_UP, billId, appointment);
    }
}


