package com.airtribe.meditrack.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Singleton class for generating unique IDs.
 * Demonstrates Singleton pattern (both eager and lazy initialization).
 * Uses AtomicInteger for thread-safe ID generation.
 *
 * @author MediTrack Team
 * @version 1.0
 */
public class IdGenerator {

    // Eager initialization - instance created at class loading
    private static final IdGenerator INSTANCE = new IdGenerator();

    // Thread-safe counters using AtomicInteger
    private final AtomicInteger patientCounter;
    private final AtomicInteger doctorCounter;
    private final AtomicInteger appointmentCounter;
    private final AtomicInteger billCounter;

    /**
     * Private constructor to prevent external instantiation
     */
    private IdGenerator() {
        this.patientCounter = new AtomicInteger(1000);
        this.doctorCounter = new AtomicInteger(2000);
        this.appointmentCounter = new AtomicInteger(3000);
        this.billCounter = new AtomicInteger(4000);
        System.out.println("[IdGenerator] Singleton instance initialized");
    }

    /**
     * Get the singleton instance (eager initialization)
     * @return the singleton instance
     */
    public static IdGenerator getInstance() {
        return INSTANCE;
    }

    /**
     * Generate a unique patient ID
     * @return patient ID (format: P####)
     */
    public synchronized String generatePatientId() {
        return "P" + patientCounter.getAndIncrement();
    }

    /**
     * Generate a unique doctor ID
     * @return doctor ID (format: D####)
     */
    public synchronized String generateDoctorId() {
        return "D" + doctorCounter.getAndIncrement();
    }

    /**
     * Generate a unique appointment ID
     * @return appointment ID (format: A####)
     */
    public synchronized String generateAppointmentId() {
        return "A" + appointmentCounter.getAndIncrement();
    }

    /**
     * Generate a unique bill ID
     * @return bill ID (format: B####)
     */
    public synchronized String generateBillId() {
        return "B" + billCounter.getAndIncrement();
    }

    /**
     * Set the starting counter for patients (useful for loading from persistence)
     * @param startValue starting value
     */
    public void setPatientCounterStart(int startValue) {
        patientCounter.set(Math.max(startValue, patientCounter.get()));
    }

    /**
     * Set the starting counter for doctors
     * @param startValue starting value
     */
    public void setDoctorCounterStart(int startValue) {
        doctorCounter.set(Math.max(startValue, doctorCounter.get()));
    }

    /**
     * Set the starting counter for appointments
     * @param startValue starting value
     */
    public void setAppointmentCounterStart(int startValue) {
        appointmentCounter.set(Math.max(startValue, appointmentCounter.get()));
    }

    /**
     * Set the starting counter for bills
     * @param startValue starting value
     */
    public void setBillCounterStart(int startValue) {
        billCounter.set(Math.max(startValue, billCounter.get()));
    }

    /**
     * Reset all counters (useful for testing)
     */
    public synchronized void resetCounters() {
        patientCounter.set(1000);
        doctorCounter.set(2000);
        appointmentCounter.set(3000);
        billCounter.set(4000);
    }

    /**
     * Get current patient counter value
     * @return current counter value
     */
    public int getCurrentPatientCounter() {
        return patientCounter.get();
    }

    /**
     * Get current doctor counter value
     * @return current counter value
     */
    public int getCurrentDoctorCounter() {
        return doctorCounter.get();
    }

    /**
     * Get current appointment counter value
     * @return current counter value
     */
    public int getCurrentAppointmentCounter() {
        return appointmentCounter.get();
    }

    /**
     * Get current bill counter value
     * @return current counter value
     */
    public int getCurrentBillCounter() {
        return billCounter.get();
    }
}

/**
 * Alternative Lazy Initialization Singleton (for demonstration)
 * This class shows lazy initialization with double-checked locking
 */
class LazyIdGenerator {
    private static volatile LazyIdGenerator instance;
    private final AtomicInteger counter;

    private LazyIdGenerator() {
        this.counter = new AtomicInteger(0);
    }

    /**
     * Get instance with double-checked locking (thread-safe lazy initialization)
     * @return singleton instance
     */
    public static LazyIdGenerator getInstance() {
        if (instance == null) {
            synchronized (LazyIdGenerator.class) {
                if (instance == null) {
                    instance = new LazyIdGenerator();
                }
            }
        }
        return instance;
    }

    public String generateId() {
        return "LAZY-" + counter.incrementAndGet();
    }
}


