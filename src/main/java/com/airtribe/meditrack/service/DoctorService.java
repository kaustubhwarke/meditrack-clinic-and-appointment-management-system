package com.airtribe.meditrack.service;

import com.airtribe.meditrack.constants.Specialization;
import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.util.DataStore;
import com.airtribe.meditrack.util.IdGenerator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing Doctor entities.
 * Demonstrates CRUD operations, streams, and lambdas.
 *
 * @author MediTrack Team
 * @version 1.0
 */
public class DoctorService {

    private final DataStore<Doctor> doctorStore;
    private final IdGenerator idGenerator;

    /**
     * Constructor
     */
    public DoctorService() {
        this.doctorStore = new DataStore<>("Doctors");
        this.idGenerator = IdGenerator.getInstance();
    }

    /**
     * Create a new doctor
     * @param name doctor's name
     * @param age doctor's age
     * @param specialization specialization
     * @param consultationFee consultation fee
     * @return created doctor
     * @throws InvalidDataException if validation fails
     */
    public Doctor createDoctor(String name, int age, Specialization specialization,
                              double consultationFee) throws InvalidDataException {
        String id = idGenerator.generateDoctorId();
        Doctor doctor = new Doctor(name, age, id, specialization, consultationFee);

        if (doctorStore.add(id, doctor)) {
            System.out.println("[DoctorService] Doctor created: " + doctor.getName());
            return doctor;
        } else {
            throw new InvalidDataException("Failed to create doctor - ID already exists: " + id);
        }
    }

    /**
     * Get doctor by ID
     * @param doctorId the doctor ID
     * @return doctor or null if not found
     */
    public Doctor getDoctorById(String doctorId) {
        return doctorStore.get(doctorId);
    }

    /**
     * Update doctor information
     * @param doctor the updated doctor
     * @return true if updated successfully
     */
    public boolean updateDoctor(Doctor doctor) {
        if (doctor == null || doctor.getId() == null) {
            return false;
        }
        return doctorStore.update(doctor.getId(), doctor);
    }

    /**
     * Delete doctor by ID
     * @param doctorId the doctor ID
     * @return deleted doctor or null
     */
    public Doctor deleteDoctor(String doctorId) {
        Doctor deleted = doctorStore.delete(doctorId);
        if (deleted != null) {
            System.out.println("[DoctorService] Doctor deleted: " + deleted.getName());
        }
        return deleted;
    }

    /**
     * Get all doctors
     * @return list of all doctors
     */
    public List<Doctor> getAllDoctors() {
        return doctorStore.getAll();
    }

    /**
     * Search doctors by query string (name, specialization, etc.)
     * Uses Searchable interface
     * @param query search query
     * @return list of matching doctors
     */
    public List<Doctor> searchDoctors(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllDoctors();
        }

        return doctorStore.findAll(doctor -> doctor.matches(query));
    }

    /**
     * Find doctors by specialization (using streams and lambdas)
     * Demonstrates Java 8+ features
     * @param specialization the specialization
     * @return list of doctors with given specialization
     */
    public List<Doctor> findBySpecialization(Specialization specialization) {
        return doctorStore.getAll().stream()
                .filter(doctor -> doctor.getSpecialization() == specialization)
                .collect(Collectors.toList());
    }

    /**
     * Find available doctors
     * @return list of available doctors
     */
    public List<Doctor> findAvailableDoctors() {
        return doctorStore.findAll(Doctor::isAvailable);
    }

    /**
     * Find doctors by specialization and availability
     * @param specialization the specialization
     * @param available availability status
     * @return list of matching doctors
     */
    public List<Doctor> findDoctors(Specialization specialization, boolean available) {
        return doctorStore.getAll().stream()
                .filter(doctor -> doctor.getSpecialization() == specialization)
                .filter(doctor -> doctor.isAvailable() == available)
                .collect(Collectors.toList());
    }

    /**
     * Calculate average consultation fee (using streams)
     * @return average fee
     */
    public double calculateAverageFee() {
        return doctorStore.getAll().stream()
                .mapToDouble(Doctor::getConsultationFee)
                .average()
                .orElse(0.0);
    }

    /**
     * Get doctor with highest consultation fee
     * @return doctor with max fee or null
     */
    public Doctor getDoctorWithHighestFee() {
        return doctorStore.getAll().stream()
                .max((d1, d2) -> Double.compare(d1.getConsultationFee(), d2.getConsultationFee()))
                .orElse(null);
    }

    /**
     * Get doctor with lowest consultation fee
     * @return doctor with min fee or null
     */
    public Doctor getDoctorWithLowestFee() {
        return doctorStore.getAll().stream()
                .min((d1, d2) -> Double.compare(d1.getConsultationFee(), d2.getConsultationFee()))
                .orElse(null);
    }

    /**
     * Count doctors by specialization
     * @param specialization the specialization
     * @return count
     */
    public long countBySpecialization(Specialization specialization) {
        return doctorStore.count(doctor -> doctor.getSpecialization() == specialization);
    }

    /**
     * Check if doctor exists
     * @param doctorId the doctor ID
     * @return true if exists
     */
    public boolean doctorExists(String doctorId) {
        return doctorStore.exists(doctorId);
    }

    /**
     * Get total number of doctors
     * @return count
     */
    public int getDoctorCount() {
        return doctorStore.size();
    }

    /**
     * Get data store (for persistence)
     * @return doctor data store
     */
    public DataStore<Doctor> getDataStore() {
        return doctorStore;
    }

    /**
     * Clear all doctors (for testing)
     */
    public void clearAll() {
        doctorStore.clear();
    }
}


