package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.constants.Specialization;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.interfaces.Searchable;
import com.airtribe.meditrack.util.Validator;
import com.airtribe.meditrack.entity.Person;

import java.util.Objects;

/**
 * Doctor class extending Person.
 * Demonstrates inheritance with super, this, constructor chaining.
 * Implements Searchable interface.
 *
 * @author MediTrack Team
 * @version 1.0
 */
public class Doctor extends Person implements Searchable {

    private static final long serialVersionUID = 1L;

    private Specialization specialization;
    private double consultationFee;
    private String licenseNumber;
    private int experienceYears;
    private boolean available;

    // Static counter for doctors
    private static int totalDoctorsCreated = 0;

    /**
     * Default constructor
     */
    public Doctor() {
        super();
        this.available = true;
        incrementDoctorCount();
    }

    /**
     * Constructor with basic information
     * Demonstrates constructor chaining using super()
     *
     * @param name doctor's name
     * @param age doctor's age
     * @param id unique identifier
     * @param specialization medical specialization
     * @throws InvalidDataException if validation fails
     */
    public Doctor(String name, int age, String id, Specialization specialization)
            throws InvalidDataException {
        super(name, age, id); // Call parent constructor
        setSpecialization(specialization);
        this.available = true;
        incrementDoctorCount();
    }

    /**
     * Constructor with full information
     *
     * @param name doctor's name
     * @param age doctor's age
     * @param id unique identifier
     * @param specialization medical specialization
     * @param consultationFee consultation fee
     * @throws InvalidDataException if validation fails
     */
    public Doctor(String name, int age, String id, Specialization specialization,
                  double consultationFee) throws InvalidDataException {
        this(name, age, id, specialization);
        setConsultationFee(consultationFee);
    }

    /**
     * Get specialization
     * @return specialization
     */
    public Specialization getSpecialization() {
        return specialization;
    }

    /**
     * Set specialization
     * @param specialization the specialization to set
     * @throws InvalidDataException if validation fails
     */
    public void setSpecialization(Specialization specialization) throws InvalidDataException {
        Validator.validateNotNull(specialization, "Specialization");
        this.specialization = specialization;
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
     * @param consultationFee the fee to set
     * @throws InvalidDataException if validation fails
     */
    public void setConsultationFee(double consultationFee) throws InvalidDataException {
        Validator.validateConsultationFee(consultationFee);
        this.consultationFee = consultationFee;
        updateModifiedTimestamp();
    }

    /**
     * Get license number
     * @return license number
     */
    public String getLicenseNumber() {
        return licenseNumber;
    }

    /**
     * Set license number
     * @param licenseNumber the license number to set
     */
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
        updateModifiedTimestamp();
    }

    /**
     * Get experience in years
     * @return experience years
     */
    public int getExperienceYears() {
        return experienceYears;
    }

    /**
     * Set experience years
     * @param experienceYears the experience years to set
     * @throws InvalidDataException if negative
     */
    public void setExperienceYears(int experienceYears) throws InvalidDataException {
        if (experienceYears < 0) {
            throw new InvalidDataException("Experience years", experienceYears,
                "Experience years cannot be negative");
        }
        this.experienceYears = experienceYears;
        updateModifiedTimestamp();
    }

    /**
     * Check if doctor is available
     * @return true if available
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Set availability status
     * @param available availability status
     */
    public void setAvailable(boolean available) {
        this.available = available;
        updateModifiedTimestamp();
    }

    /**
     * Get total doctors created
     * @return total count
     */
    public static int getTotalDoctorsCreated() {
        return totalDoctorsCreated;
    }

    /**
     * Increment doctor count
     */
    private static synchronized void incrementDoctorCount() {
        totalDoctorsCreated++;
    }

    // Override parent methods to demonstrate polymorphism

    @Override
    public String getEntityType() {
        return "Doctor";
    }

    @Override
    public String getDisplayInfo() {
        return String.format("Dr. %s - %s (Ã¢â€šÂ¹%.2f) [%s]",
            getName(),
            specialization != null ? specialization.getDisplayName() : "N/A",
            consultationFee,
            available ? "Available" : "Unavailable");
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
        if (specialization != null) {
            sb.append(specialization.name()).append(" ");
            sb.append(specialization.getDisplayName()).append(" ");
        }
        if (licenseNumber != null) {
            sb.append(licenseNumber).append(" ");
        }
        return sb.toString().toLowerCase();
    }

    @Override
    public String getDisplayName() {
        return "Dr. " + getName();
    }

    @Override
    public boolean validate() {
        return super.validate() &&
               specialization != null &&
               consultationFee > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Doctor doctor = (Doctor) o;
        return Objects.equals(licenseNumber, doctor.licenseNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), licenseNumber);
    }

    @Override
    public String toString() {
        return String.format("Doctor[ID=%s, Name=%s, Specialization=%s, Fee=Ã¢â€šÂ¹%.2f]",
            getId(), getName(),
            specialization != null ? specialization.getDisplayName() : "N/A",
            consultationFee);
    }
}


