package com.airtribe.meditrack.constants;

/**
 * Enum representing medical specializations available in the clinic.
 * Demonstrates enum usage as per project requ irements.
 *
 * @author MediTrack Team
 * @version 1.0
 */
public enum Specialization {
    CARDIOLOGY("Cardiology", "Heart and cardiovascular system"),
    NEUROLOGY("Neurology", "Brain and nervous system"),
    ORTHOPEDICS("Orthopedics", "Bones, joints, and muscles"),
    PEDIATRICS("Pediatrics", "Children's health"),
    DERMATOLOGY("Dermatology", "Skin conditions"),
    GYNECOLOGY("Gynecology", "Women's reproductive health"),
    PSYCHIATRY("Psychiatry", "Mental health"),
    GENERAL_MEDICINE("General Medicine", "General health and wellness"),
    OPHTHALMOLOGY("Ophthalmology", "Eye care"),
    ENT("ENT", "Ear, Nose, and Throat"),
    ONCOLOGY("Oncology", "Cancer treatment"),
    RADIOLOGY("Radiology", "Medical imaging");

    private final String displayName;
    private final String description;

    /**
     * Constructor for Specialization enum
     * @param displayName the display name of the specialization
     * @param description brief description of the specialization
     */
    Specialization(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Get the display name of the specialization
     * @return display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get the description of the specialization
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get specialization from string value (case-insensitive)
     * @param value string value to match
     * @return matching Specialization or null if not found
     */
    public static Specialization fromString(String value) {
        if (value == null) return null;

        for (Specialization spec : Specialization.values()) {
            if (spec.name().equalsIgnoreCase(value) ||
                spec.displayName.equalsIgnoreCase(value)) {
                return spec;
            }
        }
        return null;
    }

    /**
     * Get all specialization names as array
     * @return array of specialization display names
     */
    public static String[] getAllDisplayNames() {
        Specialization[] values = values();
        String[] names = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            names[i] = values[i].displayName;
        }
        return names;
    }

    @Override
    public String toString() {
        return displayName + " - " + description;
    }
}


