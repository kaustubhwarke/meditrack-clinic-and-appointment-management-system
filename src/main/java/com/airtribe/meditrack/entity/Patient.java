package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.interfaces.Searchable;
import com.airtribe.meditrack.util.Validator;
import com.airtribe.meditrack.entity.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Patient class extending Person.
 * Demonstrates inheritance, Cloneable interface for deep copying.
 * Implements Searchable interface.
 *
 * @author MediTrack Team
 * @version 1.0
 */
public class Patient extends Person implements Searchable, Cloneable {

    private static final long serialVersionUID = 1L;

    private String bloodGroup;
    private List<String> medicalHistory;  // For deep copy demonstration
    private List<String> allergies;        // For deep copy demonstration
    private String emergencyContact;
    private String emergencyContactPhone;

    // Static counter for patients
    private static int totalPatientsCreated = 0;

    /**
     * Default constructor
     */
    public Patient() {
        super();
        this.medicalHistory = new ArrayList<>();
        this.allergies = new ArrayList<>();
        incrementPatientCount();
    }

    /**
     * Constructor with basic information
     *
     * @param name patient's name
     * @param age patient's age
     * @param id unique identifier
     * @throws InvalidDataException if validation fails
     */
    public Patient(String name, int age, String id) throws InvalidDataException {
        super(name, age, id);
        this.medicalHistory = new ArrayList<>();
        this.allergies = new ArrayList<>();
        incrementPatientCount();
    }

    /**
     * Constructor with full information
     *
     * @param name patient's name
     * @param age patient's age
     * @param id unique identifier
     * @param bloodGroup blood group
     * @throws InvalidDataException if validation fails
     */
    public Patient(String name, int age, String id, String bloodGroup)
            throws InvalidDataException {
        this(name, age, id);
        setBloodGroup(bloodGroup);
    }

    /**
     * Get blood group
     * @return blood group
     */
    public String getBloodGroup() {
        return bloodGroup;
    }

    /**
     * Set blood group
     * @param bloodGroup the blood group to set
     */
    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup != null ? bloodGroup.trim().toUpperCase() : null;
        updateModifiedTimestamp();
    }

    /**
     * Get medical history
     * Returns defensive copy to prevent external modification
     * @return copy of medical history list
     */
    public List<String> getMedicalHistory() {
        return new ArrayList<>(medicalHistory);
    }

    /**
     * Set medical history
     * Creates defensive copy of the input list
     * @param medicalHistory the medical history list
     */
    public void setMedicalHistory(List<String> medicalHistory) {
        this.medicalHistory = medicalHistory != null ?
            new ArrayList<>(medicalHistory) : new ArrayList<>();
        updateModifiedTimestamp();
    }

    /**
     * Add medical history entry
     * @param entry medical history entry
     */
    public void addMedicalHistory(String entry) {
        if (entry != null && !entry.trim().isEmpty()) {
            this.medicalHistory.add(entry.trim());
            updateModifiedTimestamp();
        }
    }

    /**
     * Get allergies
     * Returns defensive copy to prevent external modification
     * @return copy of allergies list
     */
    public List<String> getAllergies() {
        return new ArrayList<>(allergies);
    }

    /**
     * Set allergies
     * Creates defensive copy of the input list
     * @param allergies the allergies list
     */
    public void setAllergies(List<String> allergies) {
        this.allergies = allergies != null ?
            new ArrayList<>(allergies) : new ArrayList<>();
        updateModifiedTimestamp();
    }

    /**
     * Add allergy
     * @param allergy allergy to add
     */
    public void addAllergy(String allergy) {
        if (allergy != null && !allergy.trim().isEmpty()) {
            this.allergies.add(allergy.trim());
            updateModifiedTimestamp();
        }
    }

    /**
     * Get emergency contact name
     * @return emergency contact
     */
    public String getEmergencyContact() {
        return emergencyContact;
    }

    /**
     * Set emergency contact name
     * @param emergencyContact the emergency contact name
     */
    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
        updateModifiedTimestamp();
    }

    /**
     * Get emergency contact phone
     * @return emergency contact phone
     */
    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    /**
     * Set emergency contact phone
     * @param emergencyContactPhone the phone number
     * @throws InvalidDataException if validation fails
     */
    public void setEmergencyContactPhone(String emergencyContactPhone)
            throws InvalidDataException {
        if (emergencyContactPhone != null && !emergencyContactPhone.trim().isEmpty()) {
            Validator.validatePhone(emergencyContactPhone, "Emergency contact phone");
            this.emergencyContactPhone = emergencyContactPhone.trim();
            updateModifiedTimestamp();
        }
    }

    /**
     * Get total patients created
     * @return total count
     */
    public static int getTotalPatientsCreated() {
        return totalPatientsCreated;
    }

    /**
     * Increment patient count
     */
    private static synchronized void incrementPatientCount() {
        totalPatientsCreated++;
    }

    /**
     * Deep copy implementation using Cloneable
     * Demonstrates deep vs shallow copy by cloning nested collections
     * @return deep copy of this patient
     */
    @Override
    public Patient clone() {
        try {
            // Shallow copy first (primitives and references)
            Patient cloned = (Patient) super.clone();

            // Deep copy for nested objects (collections)
            cloned.medicalHistory = new ArrayList<>(this.medicalHistory);
            cloned.allergies = new ArrayList<>(this.allergies);

            return cloned;
        } catch (CloneNotSupportedException e) {
            // This should never happen since we implement Cloneable
            throw new AssertionError("Clone not supported", e);
        }
    }

    // Override parent methods

    @Override
    public String getEntityType() {
        return "Patient";
    }

    @Override
    public String getDisplayInfo() {
        StringBuilder info = new StringBuilder();
        info.append(String.format("%s - Age: %d", getName(), getAge()));
        if (bloodGroup != null) {
            info.append(String.format(", Blood: %s", bloodGroup));
        }
        if (!allergies.isEmpty()) {
            info.append(String.format(", Allergies: %d", allergies.size()));
        }
        return info.toString();
    }

    // Implement Searchable interface

    @Override
    public boolean matches(String query) {
        if (query == null || query.trim().isEmpty()) {
            return false;
        }
        return partialMatch(query);
    }

    @Override
    public String getSearchableContent() {
        StringBuilder sb = new StringBuilder();
        sb.append(getId()).append(" ");
        sb.append(getName()).append(" ");
        if (bloodGroup != null) {
            sb.append(bloodGroup).append(" ");
        }
        if (getPhoneNumber() != null) {
            sb.append(getPhoneNumber()).append(" ");
        }
        if (getEmail() != null) {
            sb.append(getEmail()).append(" ");
        }
        return sb.toString().toLowerCase();
    }

    @Override
    public String getDisplayName() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Patient patient = (Patient) o;
        return Objects.equals(bloodGroup, patient.bloodGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), bloodGroup);
    }

    @Override
    public String toString() {
        return String.format("Patient[ID=%s, Name=%s, Age=%d, Blood=%s]",
            getId(), getName(), getAge(), bloodGroup != null ? bloodGroup : "N/A");
    }
}


