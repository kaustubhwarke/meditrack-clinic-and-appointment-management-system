package com.airtribe.meditrack;

import com.airtribe.meditrack.constants.Constants;
import com.airtribe.meditrack.constants.Specialization;
import com.airtribe.meditrack.entity.*;
import com.airtribe.meditrack.exception.AppointmentNotFoundException;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.service.*;
import com.airtribe.meditrack.util.DateUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

/**
 * Main application class with menu-driven console UI.
 * Demonstrates complete application integration with all OOP concepts.
 *
 * @author MediTrack Team
 * @version 1.0
 */
public class Main {

    private static DoctorService doctorService;
    private static PatientService patientService;
    private static AppointmentService appointmentService;
    private static BillService billService;
    private static Scanner scanner;
    private static boolean running = true;

    /**
     * Main entry point
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        // Initialize services
        doctorService = new DoctorService();
        patientService = new PatientService();
        appointmentService = new AppointmentService(doctorService, patientService);
        billService = new BillService(appointmentService);
        scanner = new Scanner(System.in);

        // Display welcome message
        displayWelcome();

        // Check for command-line arguments
        boolean loadData = checkCommandLineArgs(args);

        if (loadData) {
            loadSampleData();
        }

        // Main menu loop
        while (running) {
            try {
                displayMainMenu();
                int choice = getIntInput("Enter your choice");
                handleMainMenuChoice(choice);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
            }
        }

        scanner.close();
        System.out.println("\nThank you for using " + Constants.getAppInfo());
    }

    /**
     * Display welcome message
     */
    private static void displayWelcome() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println(Constants.getAppInfo());
        System.out.println("=".repeat(60));
        System.out.println();
    }

    /**
     * Check command-line arguments
     * @param args command-line arguments
     * @return true if --loadData flag is present
     */
    private static boolean checkCommandLineArgs(String[] args) {
        for (String arg : args) {
            if ("--loadData".equalsIgnoreCase(arg)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Load sample data for testing
     */
    private static void loadSampleData() {
        System.out.println("Loading sample data...\n");

        try {
            // Create doctors
            Doctor d1 = doctorService.createDoctor("Dr. Smith", 45,
                    Specialization.CARDIOLOGY, 1000.0);
            Doctor d2 = doctorService.createDoctor("Dr. Johnson", 38,
                    Specialization.PEDIATRICS, 800.0);
            Doctor d3 = doctorService.createDoctor("Dr. Williams", 50,
                    Specialization.ORTHOPEDICS, 1200.0);

            // Create patients
            Patient p1 = patientService.createPatient("John Doe", 35, "O+");
            Patient p2 = patientService.createPatient("Jane Smith", 28, "A+");
            Patient p3 = patientService.createPatient("Bob Wilson", 42, "B+");

            // Create appointments
            LocalDateTime apt1Time = DateUtil.getNextAvailableSlot(LocalDateTime.now());
            appointmentService.createAppointment(p1.getId(), d1.getId(), apt1Time);

            LocalDateTime apt2Time = DateUtil.addMinutes(apt1Time, 60);
            appointmentService.createAppointment(p2.getId(), d2.getId(), apt2Time);

            System.out.println("Sample data loaded successfully!");
            System.out.println("- " + doctorService.getDoctorCount() + " doctors");
            System.out.println("- " + patientService.getPatientCount() + " patients");
            System.out.println("- " + appointmentService.getAppointmentCount() + " appointments\n");

        } catch (Exception e) {
            System.err.println("Error loading sample data: " + e.getMessage());
        }

        pause();
    }

    /**
     * Display main menu
     */
    private static void displayMainMenu() {
        clearScreen();
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  MEDITRACK - MAIN MENU");
        System.out.println("=".repeat(60));
        System.out.println("  1. Doctor Management");
        System.out.println("  2. Patient Management");
        System.out.println("  3. Appointment Management");
        System.out.println("  4. Billing Management");
        System.out.println("  5. Reports & Analytics");
        System.out.println("  6. About & Statistics");
        System.out.println("  0. Exit");
        System.out.println("=".repeat(60));
    }

    /**
     * Handle main menu choice
     * @param choice user choice
     */
    private static void handleMainMenuChoice(int choice) {
        switch (choice) {
            case 1: doctorManagementMenu(); break;
            case 2: patientManagementMenu(); break;
            case 3: appointmentManagementMenu(); break;
            case 4: billingManagementMenu(); break;
            case 5: reportsMenu(); break;
            case 6: displayAboutInfo(); break;
            case 0: running = false; break;
            default: System.out.println("Invalid choice!");
        }
    }

    /**
     * Doctor management menu
     */
    private static void doctorManagementMenu() {
        while (true) {
            clearScreen();
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  DOCTOR MANAGEMENT");
            System.out.println("=".repeat(60));
            System.out.println("  1. Add New Doctor");
            System.out.println("  2. View All Doctors");
            System.out.println("  3. Search Doctor");
            System.out.println("  4. View Doctors by Specialization");
            System.out.println("  5. Update Doctor Availability");
            System.out.println("  0. Back to Main Menu");
            System.out.println("=".repeat(60));

            int choice = getIntInput("Enter your choice");

            try {
                switch (choice) {
                    case 1: addDoctor(); break;
                    case 2: viewAllDoctors(); break;
                    case 3: searchDoctor(); break;
                    case 4: viewDoctorsBySpecialization(); break;
                    case 5: updateDoctorAvailability(); break;
                    case 0: return;
                    default: System.out.println("Invalid choice!");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }

            if (choice != 0) pause();
        }
    }

    /**
     * Add new doctor
     */
    private static void addDoctor() throws InvalidDataException {
        System.out.println("\n--- Add New Doctor ---");

        String name = getStringInput("Enter doctor name");
        int age = getIntInput("Enter age");

        // Display specializations
        System.out.println("\nAvailable Specializations:");
        Specialization[] specializations = Specialization.values();
        for (int i = 0; i < specializations.length; i++) {
            System.out.println((i + 1) + ". " + specializations[i].getDisplayName());
        }

        int specChoice = getIntInput("Select specialization (1-" + specializations.length + ")");
        if (specChoice < 1 || specChoice > specializations.length) {
            throw new InvalidDataException("Invalid specialization choice");
        }
        Specialization spec = specializations[specChoice - 1];

        double fee = getDoubleInput("Enter consultation fee");

        Doctor doctor = doctorService.createDoctor(name, age, spec, fee);
        System.out.println("\nDoctor added successfully!");
        System.out.println(doctor.getDisplayInfo());
    }

    /**
     * View all doctors
     */
    private static void viewAllDoctors() {
        System.out.println("\n--- All Doctors ---");
        List<Doctor> doctors = doctorService.getAllDoctors();

        if (doctors.isEmpty()) {
            System.out.println("No doctors found.");
            return;
        }

        System.out.println();
        for (Doctor doctor : doctors) {
            System.out.println(doctor.getDisplayInfo());
            System.out.println("  ID: " + doctor.getId());
            System.out.println("  " + "-".repeat(50));
        }

        System.out.println("\nTotal doctors: " + doctors.size());
    }

    /**
     * Search doctor
     */
    private static void searchDoctor() {
        String query = getStringInput("Enter search query (name/specialization/ID)");
        List<Doctor> results = doctorService.searchDoctors(query);

        System.out.println("\n--- Search Results ---");
        if (results.isEmpty()) {
            System.out.println("No doctors found.");
        } else {
            for (Doctor doctor : results) {
                System.out.println(doctor.getDisplayInfo());
                System.out.println("  " + "-".repeat(50));
            }
            System.out.println("\nFound " + results.size() + " doctor(s)");
        }
    }

    /**
     * View doctors by specialization
     */
    private static void viewDoctorsBySpecialization() {
        System.out.println("\nSelect Specialization:");
        Specialization[] specs = Specialization.values();
        for (int i = 0; i < specs.length; i++) {
            System.out.println((i + 1) + ". " + specs[i].getDisplayName());
        }

        int choice = getIntInput("Enter choice");
        if (choice < 1 || choice > specs.length) {
            System.out.println("Invalid choice!");
            return;
        }

        Specialization spec = specs[choice - 1];
        List<Doctor> doctors = doctorService.findBySpecialization(spec);

        System.out.println("\n--- " + spec.getDisplayName() + " Doctors ---");
        if (doctors.isEmpty()) {
            System.out.println("No doctors found for this specialization.");
        } else {
            for (Doctor doctor : doctors) {
                System.out.println(doctor.getDisplayInfo());
            }
            System.out.println("\nTotal: " + doctors.size());
        }
    }

    /**
     * Update doctor availability
     */
    private static void updateDoctorAvailability() {
        String doctorId = getStringInput("Enter doctor ID");
        Doctor doctor = doctorService.getDoctorById(doctorId);

        if (doctor == null) {
            System.out.println("Doctor not found!");
            return;
        }

        System.out.println("\nCurrent status: " +
                (doctor.isAvailable() ? "Available" : "Unavailable"));

        String input = getStringInput("Mark as available? (yes/no)");
        boolean available = input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("y");

        doctor.setAvailable(available);
        doctorService.updateDoctor(doctor);

        System.out.println("Doctor availability updated!");
    }

    /**
     * Patient management menu
     */
    private static void patientManagementMenu() {
        while (true) {
            clearScreen();
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  PATIENT MANAGEMENT");
            System.out.println("=".repeat(60));
            System.out.println("  1. Add New Patient");
            System.out.println("  2. View All Patients");
            System.out.println("  3. Search Patient (by ID)");
            System.out.println("  4. Search Patient (by Name)");
            System.out.println("  5. Search Patient (by Age)");
            System.out.println("  6. View Patient Details");
            System.out.println("  0. Back to Main Menu");
            System.out.println("=".repeat(60));

            int choice = getIntInput("Enter your choice");

            try {
                switch (choice) {
                    case 1: addPatient(); break;
                    case 2: viewAllPatients(); break;
                    case 3: searchPatientById(); break;
                    case 4: searchPatientByName(); break;
                    case 5: searchPatientByAge(); break;
                    case 6: viewPatientDetails(); break;
                    case 0: return;
                    default: System.out.println("Invalid choice!");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }

            if (choice != 0) pause();
        }
    }

    /**
     * Add new patient
     */
    private static void addPatient() throws InvalidDataException {
        System.out.println("\n--- Add New Patient ---");

        String name = getStringInput("Enter patient name");
        int age = getIntInput("Enter age");
        String bloodGroup = getStringInput("Enter blood group (e.g., O+, A-, B+)");

        Patient patient = patientService.createPatient(name, age, bloodGroup);
        System.out.println("\nPatient added successfully!");
        System.out.println(patient.getDisplayInfo());
        System.out.println("Patient ID: " + patient.getId());
    }

    /**
     * View all patients
     */
    private static void viewAllPatients() {
        System.out.println("\n--- All Patients ---");
        List<Patient> patients = patientService.getAllPatients();

        if (patients.isEmpty()) {
            System.out.println("No patients found.");
            return;
        }

        System.out.println();
        for (Patient patient : patients) {
            System.out.println(patient.getDisplayInfo());
            System.out.println("  ID: " + patient.getId());
            System.out.println("  " + "-".repeat(50));
        }

        System.out.println("\nTotal patients: " + patients.size());
    }

    /**
     * Search patient by ID (overloaded method)
     */
    private static void searchPatientById() {
        String id = getStringInput("Enter patient ID");
        Patient patient = patientService.searchPatient(id); // Overloaded method #1

        if (patient == null) {
            System.out.println("Patient not found!");
        } else {
            System.out.println("\n" + patient.getDisplayInfo());
            System.out.println("ID: " + patient.getId());
        }
    }

    /**
     * Search patient by name (overloaded method)
     */
    private static void searchPatientByName() {
        String name = getStringInput("Enter patient name");
        boolean exact = getStringInput("Exact match? (yes/no)").equalsIgnoreCase("yes");

        List<Patient> results = patientService.searchPatient(name, exact); // Overloaded method #2

        System.out.println("\n--- Search Results ---");
        if (results.isEmpty()) {
            System.out.println("No patients found.");
        } else {
            for (Patient patient : results) {
                System.out.println(patient.getDisplayInfo());
                System.out.println("  ID: " + patient.getId());
                System.out.println("  " + "-".repeat(50));
            }
            System.out.println("\nFound " + results.size() + " patient(s)");
        }
    }

    /**
     * Search patient by age (overloaded method)
     */
    private static void searchPatientByAge() {
        String rangeInput = getStringInput("Search by range? (yes/no)");

        if (rangeInput.equalsIgnoreCase("yes")) {
            int minAge = getIntInput("Enter minimum age");
            int maxAge = getIntInput("Enter maximum age");
            List<Patient> results = patientService.searchPatient(minAge, maxAge); // Overloaded method #4
            displayPatientSearchResults(results);
        } else {
            int age = getIntInput("Enter patient age");
            List<Patient> results = patientService.searchPatient(age); // Overloaded method #3
            displayPatientSearchResults(results);
        }
    }

    /**
     * Display patient search results
     */
    private static void displayPatientSearchResults(List<Patient> results) {
        System.out.println("\n--- Search Results ---");
        if (results.isEmpty()) {
            System.out.println("No patients found.");
        } else {
            for (Patient patient : results) {
                System.out.println(patient.getDisplayInfo());
                System.out.println("  " + "-".repeat(50));
            }
            System.out.println("\nFound " + results.size() + " patient(s)");
        }
    }

    /**
     * View patient details
     */
    private static void viewPatientDetails() {
        String id = getStringInput("Enter patient ID");
        Patient patient = patientService.getPatientById(id);

        if (patient == null) {
            System.out.println("Patient not found!");
            return;
        }

        System.out.println("\n" + "=".repeat(50));
        System.out.println("  PATIENT DETAILS");
        System.out.println("=".repeat(50));
        System.out.println("ID: " + patient.getId());
        System.out.println("Name: " + patient.getName());
        System.out.println("Age: " + patient.getAge());
        System.out.println("Blood Group: " + (patient.getBloodGroup() != null ? patient.getBloodGroup() : "N/A"));

        List<String> allergies = patient.getAllergies();
        System.out.println("Allergies: " + (allergies.isEmpty() ? "None" : String.join(", ", allergies)));

        List<String> history = patient.getMedicalHistory();
        System.out.println("Medical History: " + (history.isEmpty() ? "None" : String.join(", ", history)));
        System.out.println("=".repeat(50));
    }

    /**
     * Appointment management menu
     */
    private static void appointmentManagementMenu() {
        while (true) {
            clearScreen();
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  APPOINTMENT MANAGEMENT");
            System.out.println("=".repeat(60));
            System.out.println("  1. Create Appointment");
            System.out.println("  2. View All Appointments");
            System.out.println("  3. View Today's Appointments");
            System.out.println("  4. Confirm Appointment");
            System.out.println("  5. Cancel Appointment");
            System.out.println("  6. Complete Appointment");
            System.out.println("  0. Back to Main Menu");
            System.out.println("=".repeat(60));

            int choice = getIntInput("Enter your choice");

            try {
                switch (choice) {
                    case 1: createAppointment(); break;
                    case 2: viewAllAppointments(); break;
                    case 3: viewTodaysAppointments(); break;
                    case 4: confirmAppointment(); break;
                    case 5: cancelAppointment(); break;
                    case 6: completeAppointment(); break;
                    case 0: return;
                    default: System.out.println("Invalid choice!");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }

            if (choice != 0) pause();
        }
    }

    /**
     * Create appointment
     */
    private static void createAppointment() throws InvalidDataException {
        System.out.println("\n--- Create Appointment ---");

        String patientId = getStringInput("Enter patient ID");
        String doctorId = getStringInput("Enter doctor ID");

        System.out.println("\nNext available slots will be suggested...");
        LocalDateTime nextSlot = DateUtil.getNextAvailableSlot(LocalDateTime.now());
        System.out.println("Suggested time: " + DateUtil.formatForDisplay(nextSlot));

        String useDefault = getStringInput("Use this time? (yes/no)");
        LocalDateTime appointmentTime;

        if (useDefault.equalsIgnoreCase("yes")) {
            appointmentTime = nextSlot;
        } else {
            // For simplicity, use next slot (in real app, would allow custom input)
            appointmentTime = nextSlot;
        }

        Appointment appointment = appointmentService.createAppointment(
                patientId, doctorId, appointmentTime);

        System.out.println("\nAppointment created successfully!");
        System.out.println(appointment.getDisplayInfo());
    }

    /**
     * View all appointments
     */
    private static void viewAllAppointments() {
        List<Appointment> appointments = appointmentService.getAllAppointments();

        System.out.println("\n--- All Appointments ---");
        if (appointments.isEmpty()) {
            System.out.println("No appointments found.");
            return;
        }

        for (Appointment apt : appointments) {
            System.out.println(apt.getDisplayInfo());
            System.out.println("  " + "-".repeat(50));
        }

        System.out.println("\nTotal: " + appointments.size());
    }

    /**
     * View today's appointments
     */
    private static void viewTodaysAppointments() {
        List<Appointment> appointments = appointmentService.getTodaysAppointments();

        System.out.println("\n--- Today's Appointments ---");
        if (appointments.isEmpty()) {
            System.out.println("No appointments scheduled for today.");
            return;
        }

        for (Appointment apt : appointments) {
            System.out.println(apt.getDisplayInfo());
            System.out.println("  " + "-".repeat(50));
        }

        System.out.println("\nTotal: " + appointments.size());
    }

    /**
     * Confirm appointment
     */
    private static void confirmAppointment() throws AppointmentNotFoundException {
        String appointmentId = getStringInput("Enter appointment ID");
        appointmentService.confirmAppointment(appointmentId);
        System.out.println("Appointment confirmed!");
    }

    /**
     * Cancel appointment
     */
    private static void cancelAppointment() throws AppointmentNotFoundException {
        String appointmentId = getStringInput("Enter appointment ID");
        appointmentService.cancelAppointment(appointmentId);
        System.out.println("Appointment cancelled!");
    }

    /**
     * Complete appointment
     */
    private static void completeAppointment() throws AppointmentNotFoundException {
        String appointmentId = getStringInput("Enter appointment ID");
        String diagnosis = getStringInput("Enter diagnosis");
        String prescription = getStringInput("Enter prescription");

        appointmentService.completeAppointment(appointmentId, diagnosis, prescription);
        System.out.println("Appointment completed!");
    }

    /**
     * Billing management menu
     */
    private static void billingManagementMenu() {
        while (true) {
            clearScreen();
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  BILLING MANAGEMENT");
            System.out.println("=".repeat(60));
            System.out.println("  1. Generate Bill (Standard)");
            System.out.println("  2. Generate Bill (Emergency)");
            System.out.println("  3. Generate Bill (Follow-up)");
            System.out.println("  4. View Bill");
            System.out.println("  5. Process Payment");
            System.out.println("  6. View All Bills");
            System.out.println("  0. Back to Main Menu");
            System.out.println("=".repeat(60));

            int choice = getIntInput("Enter your choice");

            try {
                switch (choice) {
                    case 1: generateBill(BillFactory.BillType.STANDARD); break;
                    case 2: generateBill(BillFactory.BillType.EMERGENCY); break;
                    case 3: generateBill(BillFactory.BillType.FOLLOW_UP); break;
                    case 4: viewBill(); break;
                    case 5: processPayment(); break;
                    case 6: viewAllBills(); break;
                    case 0: return;
                    default: System.out.println("Invalid choice!");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }

            if (choice != 0) pause();
        }
    }

    /**
     * Generate bill
     */
    private static void generateBill(BillFactory.BillType billType) throws Exception {
        String appointmentId = getStringInput("Enter appointment ID");
        Bill bill = billService.generateBill(appointmentId, billType);

        System.out.println(bill.generateBill(true));
    }

    /**
     * View bill
     */
    private static void viewBill() {
        String billId = getStringInput("Enter bill ID");
        Bill bill = billService.getBillById(billId);

        if (bill == null) {
            System.out.println("Bill not found!");
            return;
        }

        System.out.println(bill.generateBill(true));
    }

    /**
     * Process payment
     */
    private static void processPayment() {
        String billId = getStringInput("Enter bill ID");
        String paymentMethod = getStringInput("Enter payment method (Cash/Card/UPI)");

        if (billService.processPayment(billId, paymentMethod)) {
            System.out.println("Payment processed successfully!");
        } else {
            System.out.println("Failed to process payment. Bill not found!");
        }
    }

    /**
     * View all bills
     */
    private static void viewAllBills() {
        List<BillSummary> summaries = billService.getAllBillSummaries();

        System.out.println("\n--- All Bills ---");
        if (summaries.isEmpty()) {
            System.out.println("No bills found.");
            return;
        }

        for (BillSummary summary : summaries) {
            System.out.println(summary.getSummary());
        }

        System.out.println("\nTotal: " + summaries.size());
    }

    /**
     * Reports and analytics menu
     */
    private static void reportsMenu() {
        clearScreen();
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  REPORTS & ANALYTICS");
        System.out.println("=".repeat(60));

        // Doctor analytics
        System.out.println("\n[DOCTOR STATISTICS]");
        System.out.println("Total Doctors: " + doctorService.getDoctorCount());
        System.out.println("Average Consultation Fee: Rs." +
                String.format("%.2f", doctorService.calculateAverageFee()));

        // Patient analytics
        System.out.println("\n[PATIENT STATISTICS]");
        System.out.println("Total Patients: " + patientService.getPatientCount());
        System.out.println(patientService.getAgeStatistics());

        // Appointment analytics
        System.out.println("\n[APPOINTMENT STATISTICS]");
        System.out.println("Total Appointments: " + appointmentService.getAppointmentCount());
        System.out.println("Completion Rate: " +
                String.format("%.1f%%", appointmentService.getCompletionRate()));
        System.out.println("Cancellation Rate: " +
                String.format("%.1f%%", appointmentService.getCancellationRate()));

        // Billing analytics
        System.out.println("\n[BILLING STATISTICS]");
        System.out.println("Total Bills: " + billService.getBillCount());
        System.out.println("Total Revenue: Rs." +
                String.format("%.2f", billService.calculateTotalRevenue()));
        System.out.println("Pending Revenue: Rs." +
                String.format("%.2f", billService.calculatePendingRevenue()));
        System.out.println("Average Bill Amount: Rs." +
                String.format("%.2f", billService.getAverageBillAmount()));

        System.out.println("=".repeat(60));

        pause();
    }

    /**
     * Display about information
     */
    private static void displayAboutInfo() {
        clearScreen();
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  ABOUT " + Constants.APP_NAME);
        System.out.println("=".repeat(60));
        System.out.println("\nVersion: " + Constants.APP_VERSION);
        System.out.println("Description: Clinic and Appointment Management System");
        System.out.println("\nObject Statistics:");
        System.out.println("- Total Persons Created: " + Person.getTotalPersonsCreated());
        System.out.println("- Total Doctors Created: " + Doctor.getTotalDoctorsCreated());
        System.out.println("- Total Patients Created: " + Patient.getTotalPatientsCreated());
        System.out.println("- Total Appointments Created: " + Appointment.getTotalAppointmentsCreated());
        System.out.println("- Total Objects (tracked): " + Constants.getTotalObjectsCreated());
        System.out.println("\nDeveloped by: MediTrack Team");
        System.out.println("=".repeat(60));

        pause();
    }

    // === UTILITY METHODS ===

    private static void clearScreen() {
        // Simple clear (works in most terminals)
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                int value = Integer.parseInt(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            }
        }
    }

    private static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                double value = Double.parseDouble(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            }
        }
    }
}

