package com.airtribe.meditrack.service;

import com.airtribe.meditrack.constants.AppointmentStatus;
import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.exception.AppointmentNotFoundException;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.util.DataStore;
import com.airtribe.meditrack.util.DateUtil;
import com.airtribe.meditrack.util.IdGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for managing Appointments.
 * Demonstrates business logic, streams, and exception handling.
 *
 * @author MediTrack Team
 * @version 1.0
 */
public class AppointmentService {

    private final DataStore<Appointment> appointmentStore;
    private final IdGenerator idGenerator;
    private final DoctorService doctorService;
    private final PatientService patientService;

    /**
     * Constructor
     * @param doctorService doctor service
     * @param patientService patient service
     */
    public AppointmentService(DoctorService doctorService, PatientService patientService) {
        this.appointmentStore = new DataStore<>("Appointments");
        this.idGenerator = IdGenerator.getInstance();
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    /**
     * Create a new appointment
     * @param patientId patient ID
     * @param doctorId doctor ID
     * @param appointmentDateTime appointment date and time
     * @return created appointment
     * @throws InvalidDataException if validation fails or IDs not found
     */
    public Appointment createAppointment(String patientId, String doctorId,
                                        LocalDateTime appointmentDateTime)
            throws InvalidDataException {
        // Validate patient and doctor exist
        Patient patient = patientService.getPatientById(patientId);
        if (patient == null) {
            throw new InvalidDataException("Patient not found: " + patientId);
        }

        Doctor doctor = doctorService.getDoctorById(doctorId);
        if (doctor == null) {
            throw new InvalidDataException("Doctor not found: " + doctorId);
        }

        if (!doctor.isAvailable()) {
            throw new InvalidDataException("Doctor is not available: " + doctor.getName());
        }

        // Check if time slot is available
        if (!isTimeSlotAvailable(doctorId, appointmentDateTime)) {
            throw new InvalidDataException("Time slot not available");
        }

        String id = idGenerator.generateAppointmentId();
        Appointment appointment = new Appointment(id, patient, doctor, appointmentDateTime);
        appointment.setStatus(AppointmentStatus.PENDING);

        if (appointmentStore.add(id, appointment)) {
            System.out.println("[AppointmentService] Appointment created: " + id);
            return appointment;
        } else {
            throw new InvalidDataException("Failed to create appointment");
        }
    }

    /**
     * Get appointment by ID
     * @param appointmentId the appointment ID
     * @return appointment
     * @throws AppointmentNotFoundException if not found
     */
    public Appointment getAppointmentById(String appointmentId)
            throws AppointmentNotFoundException {
        Appointment appointment = appointmentStore.get(appointmentId);
        if (appointment == null) {
            throw new AppointmentNotFoundException(appointmentId);
        }
        return appointment;
    }

    /**
     * Update appointment
     * @param appointment the updated appointment
     * @return true if updated
     */
    public boolean updateAppointment(Appointment appointment) {
        if (appointment == null || appointment.getId() == null) {
            return false;
        }
        return appointmentStore.update(appointment.getId(), appointment);
    }

    /**
     * Cancel appointment
     * @param appointmentId the appointment ID
     * @throws AppointmentNotFoundException if not found
     */
    public void cancelAppointment(String appointmentId) throws AppointmentNotFoundException {
        Appointment appointment = getAppointmentById(appointmentId);
        appointment.cancel();
        updateAppointment(appointment);
        System.out.println("[AppointmentService] Appointment cancelled: " + appointmentId);
    }

    /**
     * Confirm appointment
     * @param appointmentId the appointment ID
     * @throws AppointmentNotFoundException if not found
     */
    public void confirmAppointment(String appointmentId) throws AppointmentNotFoundException {
        Appointment appointment = getAppointmentById(appointmentId);
        appointment.confirm();
        updateAppointment(appointment);
        System.out.println("[AppointmentService] Appointment confirmed: " + appointmentId);
    }

    /**
     * Complete appointment
     * @param appointmentId the appointment ID
     * @param diagnosis diagnosis
     * @param prescription prescription
     * @throws AppointmentNotFoundException if not found
     */
    public void completeAppointment(String appointmentId, String diagnosis, String prescription)
            throws AppointmentNotFoundException {
        Appointment appointment = getAppointmentById(appointmentId);
        appointment.setDiagnosis(diagnosis);
        appointment.setPrescription(prescription);
        appointment.complete();
        updateAppointment(appointment);
        System.out.println("[AppointmentService] Appointment completed: " + appointmentId);
    }

    /**
     * Get all appointments
     * @return list of appointments
     */
    public List<Appointment> getAllAppointments() {
        return appointmentStore.getAll();
    }

    /**
     * Get appointments for a patient
     * @param patientId patient ID
     * @return list of appointments
     */
    public List<Appointment> getAppointmentsByPatient(String patientId) {
        return appointmentStore.findAll(apt ->
            apt.getPatient() != null && apt.getPatient().getId().equals(patientId));
    }

    /**
     * Get appointments for a doctor
     * @param doctorId doctor ID
     * @return list of appointments
     */
    public List<Appointment> getAppointmentsByDoctor(String doctorId) {
        return appointmentStore.findAll(apt ->
            apt.getDoctor() != null && apt.getDoctor().getId().equals(doctorId));
    }

    /**
     * Get appointments by status
     * @param status appointment status
     * @return list of appointments
     */
    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentStore.findAll(apt -> apt.getStatus() == status);
    }

    /**
     * Get appointments for a specific date
     * @param date the date
     * @return list of appointments
     */
    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        return appointmentStore.findAll(apt ->
            apt.getAppointmentDateTime() != null &&
            apt.getAppointmentDateTime().toLocalDate().equals(date));
    }

    /**
     * Get today's appointments
     * @return list of today's appointments
     */
    public List<Appointment> getTodaysAppointments() {
        return getAppointmentsByDate(LocalDate.now());
    }

    /**
     * Get upcoming appointments (future)
     * @return list of future appointments
     */
    public List<Appointment> getUpcomingAppointments() {
        return appointmentStore.findAll(Appointment::isFutureAppointment);
    }

    /**
     * Check if time slot is available for a doctor
     * @param doctorId doctor ID
     * @param requestedTime requested time
     * @return true if available
     */
    public boolean isTimeSlotAvailable(String doctorId, LocalDateTime requestedTime) {
        List<Appointment> doctorAppointments = getAppointmentsByDoctor(doctorId);

        return doctorAppointments.stream()
                .filter(apt -> apt.getStatus().isActive())
                .noneMatch(apt -> {
                    LocalDateTime aptTime = apt.getAppointmentDateTime();
                    long diffMinutes = Math.abs(DateUtil.durationInMinutes(aptTime, requestedTime));
                    return diffMinutes < apt.getDurationMinutes();
                });
    }

    /**
     * Get appointments per doctor (analytics)
     * @return map of doctor ID to appointment count
     */
    public Map<String, Long> getAppointmentsPerDoctor() {
        return appointmentStore.getAll().stream()
                .filter(apt -> apt.getDoctor() != null)
                .collect(Collectors.groupingBy(
                    apt -> apt.getDoctor().getId(),
                    Collectors.counting()
                ));
    }

    /**
     * Get appointments per patient (analytics)
     * @return map of patient ID to appointment count
     */
    public Map<String, Long> getAppointmentsPerPatient() {
        return appointmentStore.getAll().stream()
                .filter(apt -> apt.getPatient() != null)
                .collect(Collectors.groupingBy(
                    apt -> apt.getPatient().getId(),
                    Collectors.counting()
                ));
    }

    /**
     * Get appointment completion rate
     * @return completion rate as percentage
     */
    public double getCompletionRate() {
        long total = appointmentStore.size();
        if (total == 0) return 0.0;

        long completed = appointmentStore.count(apt ->
            apt.getStatus() == AppointmentStatus.COMPLETED);

        return (completed * 100.0) / total;
    }

    /**
     * Get appointment cancellation rate
     * @return cancellation rate as percentage
     */
    public double getCancellationRate() {
        long total = appointmentStore.size();
        if (total == 0) return 0.0;

        long cancelled = appointmentStore.count(apt ->
            apt.getStatus() == AppointmentStatus.CANCELLED);

        return (cancelled * 100.0) / total;
    }

    /**
     * Clone appointment (demonstrate deep copy)
     * @param appointmentId ID of appointment to clone
     * @return cloned appointment with new ID and time
     * @throws AppointmentNotFoundException if not found
     * @throws InvalidDataException if cloning fails
     */
    public Appointment cloneAppointment(String appointmentId)
            throws AppointmentNotFoundException, InvalidDataException {
        Appointment original = getAppointmentById(appointmentId);

        // Deep copy using clone()
        Appointment cloned = original.clone();

        // Assign new ID and adjust time
        String newId = idGenerator.generateAppointmentId();
        cloned.setId(newId);

        // Schedule for next available slot
        LocalDateTime newTime = DateUtil.getNextAvailableSlot(
            original.getAppointmentDateTime().plusDays(1));
        cloned.setAppointmentDateTime(newTime);
        cloned.setStatus(AppointmentStatus.PENDING);

        if (appointmentStore.add(newId, cloned)) {
            System.out.println("[AppointmentService] Appointment cloned: " + newId);
            return cloned;
        } else {
            throw new InvalidDataException("Failed to clone appointment");
        }
    }

    /**
     * Get appointment count
     * @return total appointments
     */
    public int getAppointmentCount() {
        return appointmentStore.size();
    }

    /**
     * Get data store (for persistence)
     * @return appointment data store
     */
    public DataStore<Appointment> getDataStore() {
        return appointmentStore;
    }

    /**
     * Clear all appointments (for testing)
     */
    public void clearAll() {
        appointmentStore.clear();
    }
}


