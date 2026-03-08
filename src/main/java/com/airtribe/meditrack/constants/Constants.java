package com.airtribe.meditrack.constants;

/**
 * Application-wide constants for MediTrack system.
 * Uses static initialization block to demonstrate static initialization.
 *
 * @author MediTrack Team
 * @version 1.0
 */
public class Constants {

    // Tax and billing constants
    public static final double TAX_RATE = 0.18; // 18% GST
    public static final double CONSULTATION_BASE_FEE = 500.0;
    public static final double EMERGENCY_SURCHARGE = 1.5; // 50% extra for emergency

    // File paths for data persistence
    public static final String DATA_DIRECTORY = "data/";
    public static final String DOCTORS_FILE = DATA_DIRECTORY + "doctors.csv";
    public static final String PATIENTS_FILE = DATA_DIRECTORY + "patients.csv";
    public static final String APPOINTMENTS_FILE = DATA_DIRECTORY + "appointments.csv";
    public static final String BILLS_FILE = DATA_DIRECTORY + "bills.csv";

    // Application configuration
    public static final String APP_NAME = "MediTrack";
    public static final String APP_VERSION = "1.0.0";
    public static final int MAX_APPOINTMENTS_PER_DAY = 20;
    public static final int APPOINTMENT_DURATION_MINUTES = 30;

    // Validation constants
    public static final int MIN_AGE = 0;
    public static final int MAX_AGE = 150;
    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 100;

    // Static counter for tracking application statistics
    private static int totalObjectsCreated = 0;
    private static boolean initialized = false;

    // Static initialization block - executes when class is loaded
    static {
        System.out.println("************************************************************");
        System.out.println("  Initializing " + APP_NAME + " v" + APP_VERSION);
        System.out.println("  Loading application constants...");
        System.out.println("  Tax Rate: " + (TAX_RATE * 100) + "%");
        System.out.println("  Data Directory: " + DATA_DIRECTORY);
        System.out.println("************************************************************");
        initialized = true;
    }

    /**
     * Private constructor to prevent instantiation of utility class
     */
    private Constants() {
        throw new AssertionError("Constants class cannot be instantiated");
    }

    /**
     * Increment the total objects created counter
     */
    public static synchronized void incrementObjectCount() {
        totalObjectsCreated++;
    }

    /**
     * Get total objects created in the application
     * @return total number of objects created
     */
    public static int getTotalObjectsCreated() {
        return totalObjectsCreated;
    }

    /**
     * Check if constants have been initialized
     * @return true if initialized, false otherwise
     */
    public static boolean isInitialized() {
        return initialized;
    }

    /**
     * Get application information string
     * @return formatted application info
     */
    public static String getAppInfo() {
        return String.format("%s v%s - Clinic and Appointment Management System",
                           APP_NAME, APP_VERSION);
    }
}


