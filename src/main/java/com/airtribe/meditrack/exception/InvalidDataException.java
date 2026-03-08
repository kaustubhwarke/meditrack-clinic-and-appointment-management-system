package com.airtribe.meditrack.exception;

/**
 * Custom exception thrown when invalid data is encountered.
 * Demonstrates custom exception with validation context.
 *
 * @author MediTrack Team
 * @version 1.0
 */
public class InvalidDataException extends Exception {

    private static final long serialVersionUID = 1L;
    private final String fieldName;
    private final Object invalidValue;

    /**
     * Constructor with message
     * @param message error message
     */
    public InvalidDataException(String message) {
        super(message);
        this.fieldName = null;
        this.invalidValue = null;
    }

    /**
     * Constructor with field context
     * @param fieldName name of the invalid field
     * @param invalidValue the invalid value
     * @param message error message
     */
    public InvalidDataException(String fieldName, Object invalidValue, String message) {
        super(message);
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
    }

    /**
     * Constructor with cause for exception chaining
     * @param message error message
     * @param cause the underlying cause
     */
    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
        this.fieldName = null;
        this.invalidValue = null;
    }

    /**
     * Constructor with full context
     * @param fieldName name of the invalid field
     * @param invalidValue the invalid value
     * @param message error message
     * @param cause the underlying cause
     */
    public InvalidDataException(String fieldName, Object invalidValue,
                                String message, Throwable cause) {
        super(message, cause);
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
    }

    /**
     * Get the name of the field that contains invalid data
     * @return field name or null if not specified
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Get the invalid value that caused this exception
     * @return invalid value or null if not specified
     */
    public Object getInvalidValue() {
        return invalidValue;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("InvalidDataException: ");
        sb.append(getMessage());

        if (fieldName != null) {
            sb.append(" [Field: ").append(fieldName);
            if (invalidValue != null) {
                sb.append(", Value: ").append(invalidValue);
            }
            sb.append("]");
        }

        return sb.toString();
    }
}


