package com.airtribe.meditrack.constants;

/**
 * Enum representing the status of an appointment.
 * Replaces string-based status with type-safe enum.
 *
 * @author MediTrack Team
 * @version 1.0
 */
public enum AppointmentStatus {
    PENDING("Pending", "Appointment is scheduled but not confirmed"),
    CONFIRMED("Confirmed", "Appointment has been confirmed"),
    IN_PROGRESS("In Progress", "Patient is currently being seen"),
    COMPLETED("Completed", "Appointment has been completed"),
    CANCELLED("Cancelled", "Appointment has been cancelled"),
    NO_SHOW("No Show", "Patient did not show up"),
    RESCHEDULED("Rescheduled", "Appointment has been rescheduled");

    private final String displayName;
    private final String description;

    /**
     * Constructor for AppointmentStatus enum
     * @param displayName the display name of the status
     * @param description brief description of the status
     */
    AppointmentStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Get the display name of the status
     * @return display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get the description of the status
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Check if appointment is active (not cancelled or completed)
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return this == PENDING || this == CONFIRMED || this == IN_PROGRESS;
    }

    /**
     * Check if appointment is finalized (completed, cancelled, or no show)
     * @return true if finalized, false otherwise
     */
    public boolean isFinalized() {
        return this == COMPLETED || this == CANCELLED || this == NO_SHOW;
    }

    /**
     * Get status from string value (case-insensitive)
     * @param value string value to match
     * @return matching AppointmentStatus or PENDING as default
     */
    public static AppointmentStatus fromString(String value) {
        if (value == null) return PENDING;

        for (AppointmentStatus status : AppointmentStatus.values()) {
            if (status.name().equalsIgnoreCase(value) ||
                status.displayName.equalsIgnoreCase(value)) {
                return status;
            }
        }
        return PENDING;
    }

    @Override
    public String toString() {
        return displayName;
    }
}


