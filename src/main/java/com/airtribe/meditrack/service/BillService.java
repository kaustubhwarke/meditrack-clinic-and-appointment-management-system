package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.entity.Bill;
import com.airtribe.meditrack.entity.BillSummary;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.util.DataStore;
import com.airtribe.meditrack.util.IdGenerator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing Bills.
 * Demonstrates use of Factory pattern for bill creation.
 *
 * @author MediTrack Team
 * @version 1.0
 */
public class BillService {

    private final DataStore<Bill> billStore;
    private final IdGenerator idGenerator;
    private final AppointmentService appointmentService;

    /**
     * Constructor
     * @param appointmentService appointment service
     */
    public BillService(AppointmentService appointmentService) {
        this.billStore = new DataStore<>("Bills");
        this.idGenerator = IdGenerator.getInstance();
        this.appointmentService = appointmentService;
    }

    /**
     * Generate bill for appointment using Factory pattern
     * @param appointmentId appointment ID
     * @param billType bill type
     * @return created bill
     * @throws Exception if generation fails
     */
    public Bill generateBill(String appointmentId, BillFactory.BillType billType)
            throws Exception {
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);

        String billId = idGenerator.generateBillId();
        Bill bill = BillFactory.createBill(billType, billId, appointment);

        if (billStore.add(billId, bill)) {
            System.out.println("[BillService] Bill generated: " + billId +
                             " Amount: Ã¢â€šÂ¹" + String.format("%.2f", bill.calculateTotalAmount()));
            return bill;
        } else {
            throw new InvalidDataException("Failed to generate bill");
        }
    }

    /**
     * Generate standard bill
     * @param appointmentId appointment ID
     * @return created bill
     * @throws Exception if generation fails
     */
    public Bill generateStandardBill(String appointmentId) throws Exception {
        return generateBill(appointmentId, BillFactory.BillType.STANDARD);
    }

    /**
     * Get bill by ID
     * @param billId bill ID
     * @return bill or null
     */
    public Bill getBillById(String billId) {
        return billStore.get(billId);
    }

    /**
     * Process payment for a bill
     * @param billId bill ID
     * @param paymentMethod payment method
     * @return true if processed
     */
    public boolean processPayment(String billId, String paymentMethod) {
        Bill bill = billStore.get(billId);
        if (bill == null) {
            return false;
        }

        bill.markAsPaid(paymentMethod);
        billStore.update(billId, bill);
        System.out.println("[BillService] Payment processed for bill: " + billId);
        return true;
    }

    /**
     * Get all bills
     * @return list of bills
     */
    public List<Bill> getAllBills() {
        return billStore.getAll();
    }

    /**
     * Get paid bills
     * @return list of paid bills
     */
    public List<Bill> getPaidBills() {
        return billStore.findAll(Bill::isPaid);
    }

    /**
     * Get unpaid bills
     * @return list of unpaid bills
     */
    public List<Bill> getUnpaidBills() {
        return billStore.findAll(bill -> !bill.isPaid());
    }

    /**
     * Get bills for a patient
     * @param patientId patient ID
     * @return list of bills
     */
    public List<Bill> getBillsByPatient(String patientId) {
        return billStore.findAll(bill ->
            bill.getAppointment() != null &&
            bill.getAppointment().getPatient() != null &&
            bill.getAppointment().getPatient().getId().equals(patientId));
    }

    /**
     * Calculate total revenue (all paid bills)
     * @return total revenue
     */
    public double calculateTotalRevenue() {
        return billStore.getAll().stream()
                .filter(Bill::isPaid)
                .mapToDouble(Bill::calculateTotalAmount)
                .sum();
    }

    /**
     * Calculate pending revenue (unpaid bills)
     * @return pending revenue
     */
    public double calculatePendingRevenue() {
        return billStore.getAll().stream()
                .filter(bill -> !bill.isPaid())
                .mapToDouble(Bill::calculateTotalAmount)
                .sum();
    }

    /**
     * Get average bill amount
     * @return average amount
     */
    public double getAverageBillAmount() {
        return billStore.getAll().stream()
                .mapToDouble(Bill::calculateTotalAmount)
                .average()
                .orElse(0.0);
    }

    /**
     * Create bill summary (immutable)
     * @param billId bill ID
     * @return immutable BillSummary
     */
    public BillSummary createBillSummary(String billId) {
        Bill bill = billStore.get(billId);
        if (bill == null) {
            return null;
        }
        return BillSummary.fromBill(bill);
    }

    /**
     * Get all bill summaries
     * @return list of immutable bill summaries
     */
    public List<BillSummary> getAllBillSummaries() {
        return billStore.getAll().stream()
                .map(BillSummary::fromBill)
                .collect(Collectors.toList());
    }

    /**
     * Get bill count
     * @return total bills
     */
    public int getBillCount() {
        return billStore.size();
    }

    /**
     * Get data store (for persistence)
     * @return bill data store
     */
    public DataStore<Bill> getDataStore() {
        return billStore;
    }

    /**
     * Clear all bills (for testing)
     */
    public void clearAll() {
        billStore.clear();
    }
}


