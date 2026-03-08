package com.airtribe.meditrack.util;

import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.constants.Constants;

/**
 * Centralized validation utility class.
 * Demonstrates encapsulation through centralized validation logic.
 *
 * @author MediTrack Team
 * @version 1.0
 */
public class Validator {

    /**
     * Private constructor to prevent instantiation
     */
    private Validator() {
        throw new AssertionError("Validator class cannot be instantiated");
    }

    /**
     * Validate name field
     * @param name the name to validate
     * @param fieldName the name of the field being validated
     * @throws InvalidDataException if validation fails
     */
    public static void validateName(String name, String fieldName) throws InvalidDataException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException(fieldName, name,
                fieldName + " cannot be null or empty");
        }

        String trimmedName = name.trim();
        if (trimmedName.length() < Constants.MIN_NAME_LENGTH) {
            throw new InvalidDataException(fieldName, name,
                fieldName + " must be at least " + Constants.MIN_NAME_LENGTH + " characters long");
        }

        if (trimmedName.length() > Constants.MAX_NAME_LENGTH) {
            throw new InvalidDataException(fieldName, name,
                fieldName + " must not exceed " + Constants.MAX_NAME_LENGTH + " characters");
        }

        // Check if name contains only letters, spaces, and common name characters
        if (!trimmedName.matches("^[a-zA-Z\\s.'-]+$")) {
            throw new InvalidDataException(fieldName, name,
                fieldName + " must contain only letters, spaces, dots, hyphens, and apostrophes");
        }
    }

    /**
     * Validate age
     * @param age the age to validate
     * @param fieldName the name of the field being validated
     * @throws InvalidDataException if validation fails
     */
    public static void validateAge(int age, String fieldName) throws InvalidDataException {
        if (age < Constants.MIN_AGE) {
            throw new InvalidDataException(fieldName, age,
                fieldName + " cannot be negative");
        }

        if (age > Constants.MAX_AGE) {
            throw new InvalidDataException(fieldName, age,
                fieldName + " cannot exceed " + Constants.MAX_AGE);
        }
    }

    /**
     * Validate ID format (must not be null or empty)
     * @param id the ID to validate
     * @param fieldName the name of the field being validated
     * @throws InvalidDataException if validation fails
     */
    public static void validateId(String id, String fieldName) throws InvalidDataException {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidDataException(fieldName, id,
                fieldName + " cannot be null or empty");
        }

        // Check if ID contains only alphanumeric characters and hyphens
        if (!id.matches("^[a-zA-Z0-9-]+$")) {
            throw new InvalidDataException(fieldName, id,
                fieldName + " must contain only alphanumeric characters and hyphens");
        }
    }

    /**
     * Validate phone number
     * @param phone the phone number to validate
     * @param fieldName the name of the field being validated
     * @throws InvalidDataException if validation fails
     */
    public static void validatePhone(String phone, String fieldName) throws InvalidDataException {
        if (phone == null || phone.trim().isEmpty()) {
            throw new InvalidDataException(fieldName, phone,
                fieldName + " cannot be null or empty");
        }

        // Remove spaces, hyphens, and parentheses for validation
        String cleanPhone = phone.replaceAll("[\\s\\-()]", "");

        // Check if it contains only digits and has appropriate length (10 digits for India)
        if (!cleanPhone.matches("^\\d{10}$")) {
            throw new InvalidDataException(fieldName, phone,
                fieldName + " must be a valid 10-digit phone number");
        }
    }

    /**
     * Validate email address
     * @param email the email to validate
     * @param fieldName the name of the field being validated
     * @throws InvalidDataException if validation fails
     */
    public static void validateEmail(String email, String fieldName) throws InvalidDataException {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidDataException(fieldName, email,
                fieldName + " cannot be null or empty");
        }

        // Simple email validation regex
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!email.matches(emailRegex)) {
            throw new InvalidDataException(fieldName, email,
                fieldName + " must be a valid email address");
        }
    }

    /**
     * Validate amount (must be positive)
     * @param amount the amount to validate
     * @param fieldName the name of the field being validated
     * @throws InvalidDataException if validation fails
     */
    public static void validateAmount(double amount, String fieldName) throws InvalidDataException {
        if (amount < 0) {
            throw new InvalidDataException(fieldName, amount,
                fieldName + " cannot be negative");
        }

        if (Double.isNaN(amount) || Double.isInfinite(amount)) {
            throw new InvalidDataException(fieldName, amount,
                fieldName + " must be a valid number");
        }
    }

    /**
     * Validate consultation fee
     * @param fee the fee to validate
     * @throws InvalidDataException if validation fails
     */
    public static void validateConsultationFee(double fee) throws InvalidDataException {
        validateAmount(fee, "Consultation fee");

        if (fee < Constants.CONSULTATION_BASE_FEE) {
            throw new InvalidDataException("Consultation fee", fee,
                "Consultation fee cannot be less than base fee: " + Constants.CONSULTATION_BASE_FEE);
        }
    }

    /**
     * Validate non-null object
     * @param obj the object to validate
     * @param fieldName the name of the field being validated
     * @throws InvalidDataException if object is null
     */
    public static void validateNotNull(Object obj, String fieldName) throws InvalidDataException {
        if (obj == null) {
            throw new InvalidDataException(fieldName, null,
                fieldName + " cannot be null");
        }
    }

    /**
     * Validate string is not empty
     * @param str the string to validate
     * @param fieldName the name of the field being validated
     * @throws InvalidDataException if string is null or empty
     */
    public static void validateNotEmpty(String str, String fieldName) throws InvalidDataException {
        if (str == null || str.trim().isEmpty()) {
            throw new InvalidDataException(fieldName, str,
                fieldName + " cannot be null or empty");
        }
    }
}


