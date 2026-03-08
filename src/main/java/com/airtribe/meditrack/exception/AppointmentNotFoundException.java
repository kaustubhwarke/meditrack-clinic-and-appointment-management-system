package com.airtribe.meditrack.exception;

/**
 * Custom exception thrown when an appointment cannot be found.
 * Demonstrates custom exception handling with chaining support.
 *
 * @author MediTrack Team
 * @version 1.0
 */
public class AppointmentNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;
    private final String appointmentId;

    /**
     * Constructor with appointment ID
     * @param appointmentId the ID of the appointment that wasn't found
     */
    public AppointmentNotFoundException(String appointmentId) {
        super("Appointment not found with ID: " + appointmentId);
        this.appointmentId = appointmentId;
    }

    /**
     * Constructor with custom message
     * @param appointmentId the ID of the appointment that wasn't found
     * @param message custom error message
     */
    public AppointmentNotFoundException(String appointmentId, String message) {
        super(message);
        this.appointmentId = appointmentId;
    }

    /**
     * Constructor with cause for exception chaining
     * @param appointmentId the ID of the appointment that wasn't found
     * @param cause the underlying cause
     */
    public AppointmentNotFoundException(String appointmentId, Throwable cause) {
        super("Appointment not found with ID: " + appointmentId, cause);
        this.appointmentId = appointmentId;
    }

    /**
     * Constructor with message and cause
     * @param appointmentId the ID of the appointment that wasn't found
     * @param message custom error message
     * @param cause the underlying cause
     */
    public AppointmentNotFoundException(String appointmentId, String message, Throwable cause) {
        super(message, cause);
        this.appointmentId = appointmentId;
    }

    /**
     * Get the appointment ID that caused this exception
     * @return appointment ID
     */
    public String getAppointmentId() {
        return appointmentId;
    }

    @Override
    public String toString() {
        return "AppointmentNotFoundException: " + getMessage() +
               " [Appointment ID: " + appointmentId + "]";
    }
}


