package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.util.Validator;
import com.airtribe.meditrack.constants.Constants;

import java.util.Objects;

/**
 * Base Person class demonstrating inheritance hierarchy.
 * Extends MedicalEntity and serves as parent for Doctor and Patient.
 * Demonstrates encapsulation with private fields and validation.
 *
 * @author MediTrack Team
 * @version 1.0
 */
public class Person extends MedicalEntity {

    private static final long serialVersionUID = 1L;

    // Private fields demonstrating encapsulation
    private String name;
    private int age;
    private String phoneNumber;
    private String email;
    private String address;

    // Static variable demonstrating static scope
    private static int totalPersonsCreated = 0;

    // Static initialization block
    static {
        System.out.println("[Person] Class loaded - initializing Person class");
    }

    /**
     * Default constructor
     */
    public Person() {
        super();
        incrementPersonCount();
    }

    /**
     * Constructor with basic information
     * Demonstrates constructor chaining with this()
     *
     * @param name person's name
     * @param age person's age
     * @param id unique identifier
     * @throws InvalidDataException if validation fails
     */
    public Person(String name, int age, String id) throws InvalidDataException {
        this(); // Call default constructor
        setId(id);
        setName(name);
        setAge(age);
    }

    /**
     * Constructor with full information
     * Demonstrates constructor chaining with this()
     *
     * @param name person's name
     * @param age person's age
     * @param id unique identifier
     * @param phoneNumber phone number
     * @param email email address
     * @param address physical address
     * @throws InvalidDataException if validation fails
     */
    public Person(String name, int age, String id, String phoneNumber,
                  String email, String address) throws InvalidDataException {
        this(name, age, id); // Chain to simpler constructor
        setPhoneNumber(phoneNumber);
        setEmail(email);
        setAddress(address);
    }

    // Getters and setters with validation (demonstrating encapsulation)

    /**
     * Get person's name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set person's name with validation
     * @param name the name to set
     * @throws InvalidDataException if validation fails
     */
    public void setName(String name) throws InvalidDataException {
        Validator.validateName(name, "Name");
        this.name = name.trim();
        updateModifiedTimestamp();
    }

    /**
     * Get person's age
     * @return age
     */
    public int getAge() {
        return age;
    }

    /**
     * Set person's age with validation
     * @param age the age to set
     * @throws InvalidDataException if validation fails
     */
    public void setAge(int age) throws InvalidDataException {
        Validator.validateAge(age, "Age");
        this.age = age;
        updateModifiedTimestamp();
    }

    /**
     * Get phone number
     * @return phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Set phone number with validation
     * @param phoneNumber the phone number to set
     * @throws InvalidDataException if validation fails
     */
    public void setPhoneNumber(String phoneNumber) throws InvalidDataException {
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            Validator.validatePhone(phoneNumber, "Phone number");
            this.phoneNumber = phoneNumber.trim();
            updateModifiedTimestamp();
        }
    }

    /**
     * Get email address
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set email address with validation
     * @param email the email to set
     * @throws InvalidDataException if validation fails
     */
    public void setEmail(String email) throws InvalidDataException {
        if (email != null && !email.trim().isEmpty()) {
            Validator.validateEmail(email, "Email");
            this.email = email.trim().toLowerCase();
            updateModifiedTimestamp();
        }
    }

    /**
     * Get address
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set address
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address != null ? address.trim() : null;
        updateModifiedTimestamp();
    }

    /**
     * Static method to get total persons created
     * Demonstrates static method accessing static variable
     * @return total count
     */
    public static int getTotalPersonsCreated() {
        return totalPersonsCreated;
    }

    /**
     * Increment person count (called from constructor)
     */
    private static synchronized void incrementPersonCount() {
        totalPersonsCreated++;
        Constants.incrementObjectCount();
    }

    @Override
    public String getEntityType() {
        return "Person";
    }

    @Override
    public String getDisplayInfo() {
        return String.format("Name: %s, Age: %d, ID: %s", name, age, id);
    }

    @Override
    public boolean validate() {
        return super.validate() &&
               name != null && !name.trim().isEmpty() &&
               age >= Constants.MIN_AGE && age <= Constants.MAX_AGE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Person person = (Person) o;
        return age == person.age &&
               Objects.equals(name, person.name) &&
               Objects.equals(email, person.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, age, email);
    }

    @Override
    public String toString() {
        return String.format("Person[ID=%s, Name=%s, Age=%d]", id, name, age);
    }
}

