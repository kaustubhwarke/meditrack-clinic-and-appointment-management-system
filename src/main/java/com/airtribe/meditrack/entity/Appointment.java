package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.constants.AppointmentStatus;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.util.DateUtil;
import com.airtribe.meditrack.util.Validator;
import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.MedicalEntity;
import com.airtribe.meditrack.entity.Patient;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Appointment class representing a medical appointment.
 * Demonstrates Cloneable for deep copying, enum usage for status.
 * Extends MedicalEntity.
 *
 * @author MediTrack Team
 * @version 1.0
 */
public class Appointment extends MedicalEntity implements Cloneable {

    private static final long serialVersionUID = 1L;

    private Patient patient;       // Reference to patient (deep copy needed)
    private Doctor doctor;         // Reference to doctor (deep copy needed)
    private LocalDateTime appointmentDateTime;
    private AppointmentStatus status;  // Using enum instead of String
    private String symptoms;
    private String diagnosis;
    private String prescription;
    private int durationMinutes;

    // Static counter
    private static int totalAppointmentsCreated = 0;

    /**
     * Default constructor
     */
    public Appointment() {
        super();
        this.status = AppointmentStatus.PENDING;
        this.durationMinutes = 30; // Default 30 minutes
        incrementAppointmentCount();
    }

    /**
     * Constructor with basic information
     *
     * @param id appointment ID
     * @param patient patient
     * @param doctor doctor
     * @param appointmentDateTime date and time
     * @throws InvalidDataException if validation fails
     */
    public Appointment(String id, Patient patient, Doctor doctor,
                      LocalDateTime appointmentDateTime) throws InvalidDataException {
        this();
        setId(id);
        setPatient(patient);
        setDoctor(doctor);
        setAppointmentDateTime(appointmentDateTime);
    }

    /**
     * Get patient
     * @return patient
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * Set patient
     * @param patient the patient
     * @throws InvalidDataException if validation fails
     */
    public void setPatient(Patient patient) throws InvalidDataException {
        Validator.validateNotNull(patient, "Patient");
        this.patient = patient;
        updateModifiedTimestamp();
    }

    /**
     * Get doctor
     * @return doctor
     */
    public Doctor getDoctor() {
        return doctor;
    }

    /**
     * Set doctor
     * @param doctor the doctor
     * @throws InvalidDataException if validation fails
     */
    public void setDoctor(Doctor doctor) throws InvalidDataException {
        Validator.validateNotNull(doctor, "Doctor");
        this.doctor = doctor;
        updateModifiedTimestamp();
    }

    /**
     * Get appointment date and time
     * @return appointment date time
     */
    public LocalDateTime getAppointmentDateTime() {
        return appointmentDateTime;
    }

    /**
     * Set appointment date and time
     * @param appointmentDateTime the date and time
     * @throws InvalidDataException if validation fails
     */
    public void setAppointmentDateTime(LocalDateTime appointmentDateTime)
            throws InvalidDataException {
        Validator.validateNotNull(appointmentDateTime, "Appointment date time");
        this.appointmentDateTime = appointmentDateTime;
        updateModifiedTimestamp();
    }

    /**
     * Get appointment status (enum)
     * @return status
     */
    public AppointmentStatus getStatus() {
        return status;
    }

    /**
     * Set appointment status (enum)
     * @param status the status
     */
    public void setStatus(AppointmentStatus status) {
        if (status != null) {
            this.status = status;
            updateModifiedTimestamp();
        }
    }

    /**
     * Get symptoms
     * @return symptoms
     */
    public String getSymptoms() {
        return symptoms;
    }

    /**
     * Set symptoms
     * @param symptoms the symptoms
     */
    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
        updateModifiedTimestamp();
    }

    /**
     * Get diagnosis
     * @return diagnosis
     */
    public String getDiagnosis() {
        return diagnosis;
    }

    /**
     * Set diagnosis
     * @param diagnosis the diagnosis
     */
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
        updateModifiedTimestamp();
    }

    /**
     * Get prescription
     * @return prescription
     */
    public String getPrescription() {
        return prescription;
    }

    /**
     * Set prescription
     * @param prescription the prescription
     */
    public void setPrescription(String prescription) {
        this.prescription = prescription;
        updateModifiedTimestamp();
    }

    /**
     * Get duration in minutes
     * @return duration
     */
    public int getDurationMinutes() {
        return durationMinutes;
    }

    /**
     * Set duration in minutes
     * @param durationMinutes the duration
     * @throws InvalidDataException if negative
     */
    public void setDurationMinutes(int durationMinutes) throws InvalidDataException {
        if (durationMinutes <= 0) {
            throw new InvalidDataException("Duration", durationMinutes,
                "Duration must be positive");
        }
        this.durationMinutes = durationMinutes;
        updateModifiedTimestamp();
    }

    /**
     * Confirm the appointment
     */
    public void confirm() {
        setStatus(AppointmentStatus.CONFIRMED);
    }

    /**
     * Cancel the appointment
     */
    public void cancel() {
        setStatus(AppointmentStatus.CANCELLED);
    }

    /**
     * Complete the appointment
     */
    public void complete() {
        setStatus(AppointmentStatus.COMPLETED);
    }

    /**
     * Check if appointment is in the future
     * @return true if future appointment
     */
    public boolean isFutureAppointment() {
        return DateUtil.isInFuture(appointmentDateTime);
    }

    /**
     * Check if appointment is today
     * @return true if today
     */
    public boolean isToday() {
        return appointmentDateTime != null &&
               DateUtil.isToday(appointmentDateTime.toLocalDate());
    }

    /**
     * Get total appointments created
     * @return total count
     */
    public static int getTotalAppointmentsCreated() {
        return totalAppointmentsCreated;
    }

    /**
     * Increment appointment count
     */
    private static synchronized void incrementAppointmentCount() {
        totalAppointmentsCreated++;
    }

    /**
     * Deep copy implementation
     * Clones nested objects (Patient and Doctor)
     * @return deep copy of appointment
     */
    @Override
    public Appointment clone() {
        try {
            Appointment cloned = (Appointment) super.clone();

            // Deep copy nested objects
            if (this.patient != null) {
                cloned.patient = this.patient.clone();
            }
            if (this.doctor != null) {
                // Doctor doesn't implement clone, so we keep reference
                // In real app, you might want to implement clone for Doctor too
                cloned.doctor = this.doctor;
            }
            // LocalDateTime is immutable, so no need to clone

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Clone not supported", e);
        }
    }

    @Override
    public String getEntityType() {
        return "Appointment";
    }

    @Override
    public String getDisplayInfo() {
        return String.format("Appointment %s: %s with Dr. %s on %s [%s]",
            getId(),
            patient != null ? patient.getName() : "N/A",
            doctor != null ? doctor.getName() : "N/A",
            appointmentDateTime != null ? DateUtil.formatForDisplay(appointmentDateTime) : "N/A",
            status.getDisplayName());
    }

    @Override
    public boolean validate() {
        return super.validate() &&
               patient != null &&
               doctor != null &&
               appointmentDateTime != null &&
               status != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Appointment that = (Appointment) o;
        return Objects.equals(patient, that.patient) &&
               Objects.equals(doctor, that.doctor) &&
               Objects.equals(appointmentDateTime, that.appointmentDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), patient, doctor, appointmentDateTime);
    }

    @Override
    public String toString() {
        return String.format("Appointment[ID=%s, Patient=%s, Doctor=%s, DateTime=%s, Status=%s]",
            getId(),
            patient != null ? patient.getName() : "N/A",
            doctor != null ? doctor.getName() : "N/A",
            appointmentDateTime != null ? DateUtil.formatDateTime(appointmentDateTime) : "N/A",
            status.getDisplayName());
    }
}


