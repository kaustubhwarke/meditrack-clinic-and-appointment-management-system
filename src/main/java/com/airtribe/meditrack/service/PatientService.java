package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.util.DataStore;
import com.airtribe.meditrack.util.IdGenerator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing Patient entities.
 * Demonstrates method overloading for search (polymorphism).
 *
 * @author MediTrack Team
 * @version 1.0
 */
public class PatientService {

    private final DataStore<Patient> patientStore;
    private final IdGenerator idGenerator;

    /**
     * Constructor
     */
    public PatientService() {
        this.patientStore = new DataStore<>("Patients");
        this.idGenerator = IdGenerator.getInstance();
    }

    /**
     * Create a new patient
     * @param name patient's name
     * @param age patient's age
     * @param bloodGroup blood group
     * @return created patient
     * @throws InvalidDataException if validation fails
     */
    public Patient createPatient(String name, int age, String bloodGroup)
            throws InvalidDataException {
        String id = idGenerator.generatePatientId();
        Patient patient = new Patient(name, age, id, bloodGroup);

        if (patientStore.add(id, patient)) {
            System.out.println("[PatientService] Patient created: " + patient.getName());
            return patient;
        } else {
            throw new InvalidDataException("Failed to create patient - ID already exists: " + id);
        }
    }

    /**
     * Get patient by ID
     * @param patientId the patient ID
     * @return patient or null if not found
     */
    public Patient getPatientById(String patientId) {
        return patientStore.get(patientId);
    }

    /**
     * Update patient information
     * @param patient the updated patient
     * @return true if updated successfully
     */
    public boolean updatePatient(Patient patient) {
        if (patient == null || patient.getId() == null) {
            return false;
        }
        return patientStore.update(patient.getId(), patient);
    }

    /**
     * Delete patient by ID
     * @param patientId the patient ID
     * @return deleted patient or null
     */
    public Patient deletePatient(String patientId) {
        Patient deleted = patientStore.delete(patientId);
        if (deleted != null) {
            System.out.println("[PatientService] Patient deleted: " + deleted.getName());
        }
        return deleted;
    }

    /**
     * Get all patients
     * @return list of all patients
     */
    public List<Patient> getAllPatients() {
        return patientStore.getAll();
    }

    // ============= METHOD OVERLOADING FOR POLYMORPHISM =============

    /**
     * Search patients by ID (overloaded method #1)
     * Demonstrates polymorphism through method overloading
     * @param id patient ID
     * @return patient or null
     */
    public Patient searchPatient(String id) {
        return patientStore.get(id);
    }

    /**
     * Search patients by name (overloaded method #2)
     * Demonstrates polymorphism through method overloading
     * @param name patient name
     * @param exactMatch whether to match exactly
     * @return list of matching patients
     */
    public List<Patient> searchPatient(String name, boolean exactMatch) {
        if (name == null || name.trim().isEmpty()) {
            return getAllPatients();
        }

        String searchName = name.trim().toLowerCase();

        if (exactMatch) {
            return patientStore.findAll(patient ->
                patient.getName().toLowerCase().equals(searchName));
        } else {
            return patientStore.findAll(patient ->
                patient.getName().toLowerCase().contains(searchName));
        }
    }

    /**
     * Search patients by age (overloaded method #3)
     * Demonstrates polymorphism through method overloading
     * @param age patient age
     * @return list of patients with given age
     */
    public List<Patient> searchPatient(int age) {
        return patientStore.findAll(patient -> patient.getAge() == age);
    }

    /**
     * Search patients by age range (overloaded method #4)
     * Demonstrates polymorphism through method overloading
     * @param minAge minimum age
     * @param maxAge maximum age
     * @return list of patients within age range
     */
    public List<Patient> searchPatient(int minAge, int maxAge) {
        return patientStore.findAll(patient ->
            patient.getAge() >= minAge && patient.getAge() <= maxAge);
    }

    /**
     * Search patients by multiple criteria (overloaded method #5)
     * Demonstrates polymorphism through method overloading
     * @param name patient name (partial match)
     * @param age patient age (exact match)
     * @return list of matching patients
     */
    public List<Patient> searchPatient(String name, int age) {
        if (name == null || name.trim().isEmpty()) {
            return searchPatient(age);
        }

        String searchName = name.trim().toLowerCase();
        return patientStore.findAll(patient ->
            patient.getName().toLowerCase().contains(searchName) &&
            patient.getAge() == age);
    }

    // ============= END OF OVERLOADED METHODS =============

    /**
     * Find patients by blood group
     * @param bloodGroup the blood group
     * @return list of patients with given blood group
     */
    public List<Patient> findByBloodGroup(String bloodGroup) {
        if (bloodGroup == null) {
            return List.of();
        }

        String searchBloodGroup = bloodGroup.trim().toUpperCase();
        return patientStore.findAll(patient ->
            searchBloodGroup.equals(patient.getBloodGroup()));
    }

    /**
     * Find patients with specific allergy
     * @param allergy the allergy to search for
     * @return list of patients with the allergy
     */
    public List<Patient> findPatientsWithAllergy(String allergy) {
        if (allergy == null || allergy.trim().isEmpty()) {
            return List.of();
        }

        String searchAllergy = allergy.trim().toLowerCase();
        return patientStore.findAll(patient ->
            patient.getAllergies().stream()
                .anyMatch(a -> a.toLowerCase().contains(searchAllergy)));
    }

    /**
     * Calculate average age of patients (using streams)
     * @return average age
     */
    public double calculateAverageAge() {
        return patientStore.getAll().stream()
                .mapToInt(Patient::getAge)
                .average()
                .orElse(0.0);
    }

    /**
     * Get age statistics
     * @return formatted age statistics string
     */
    public String getAgeStatistics() {
        List<Patient> patients = patientStore.getAll();
        if (patients.isEmpty()) {
            return "No patients in system";
        }

        int minAge = patients.stream()
                .mapToInt(Patient::getAge)
                .min()
                .orElse(0);

        int maxAge = patients.stream()
                .mapToInt(Patient::getAge)
                .max()
                .orElse(0);

        double avgAge = calculateAverageAge();

        return String.format("Age Statistics - Min: %d, Max: %d, Average: %.1f",
                           minAge, maxAge, avgAge);
    }

    /**
     * Get patients grouped by blood group
     * @return map of blood group to count
     */
    public java.util.Map<String, Long> getPatientsByBloodGroup() {
        return patientStore.getAll().stream()
                .filter(patient -> patient.getBloodGroup() != null)
                .collect(Collectors.groupingBy(
                    Patient::getBloodGroup,
                    Collectors.counting()
                ));
    }

    /**
     * Clone patient (demonstrate deep copy)
     * @param patientId ID of patient to clone
     * @return cloned patient with new ID
     * @throws InvalidDataException if patient not found
     */
    public Patient clonePatient(String patientId) throws InvalidDataException {
        Patient original = patientStore.get(patientId);
        if (original == null) {
            throw new InvalidDataException("Patient not found: " + patientId);
        }

        // Demonstrate deep copy using clone()
        Patient cloned = original.clone();

        // Assign new ID to cloned patient
        String newId = idGenerator.generatePatientId();
        cloned.setId(newId);

        // Add to store
        if (patientStore.add(newId, cloned)) {
            System.out.println("[PatientService] Patient cloned: " + cloned.getName());
            return cloned;
        } else {
            throw new InvalidDataException("Failed to clone patient");
        }
    }

    /**
     * Check if patient exists
     * @param patientId the patient ID
     * @return true if exists
     */
    public boolean patientExists(String patientId) {
        return patientStore.exists(patientId);
    }

    /**
     * Get total number of patients
     * @return count
     */
    public int getPatientCount() {
        return patientStore.size();
    }

    /**
     * Get data store (for persistence)
     * @return patient data store
     */
    public DataStore<Patient> getDataStore() {
        return patientStore;
    }

    /**
     * Clear all patients (for testing)
     */
    public void clearAll() {
        patientStore.clear();
    }
}


