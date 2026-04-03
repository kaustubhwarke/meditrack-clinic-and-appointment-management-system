package com.airtribe.meditrack.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Abstract base class for medical entities.
 * Demonstrates abstraction with common behavior for all medical entities.
 * Implements Serializable for object persistence.
 *
 * @author MediTrack Team
 * @version 1.0
 */
public abstract class MedicalEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String id;
    protected String createdBy;
    protected long createdTimestamp;
    protected long lastModifiedTimestamp;

    /**
     * Default constructor
     */
    public MedicalEntity() {
        this.createdTimestamp = System.currentTimeMillis();
        this.lastModifiedTimestamp = this.createdTimestamp;
    }

    /**
     * Constructor with ID
     * @param id unique identifier
     */
    public MedicalEntity(String id) {
        this();
        this.id = id;
    }

    /**
     * Get unique identifier
     * @return ID
     */
    public String getId() {
        return id;
    }

    /**
     * Set unique identifier
     * @param id the ID
     */
    public void setId(String id) {
        this.id = id;
        updateModifiedTimestamp();
    }

    /**
     * Get creator information
     * @return creator
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Set creator information
     * @param createdBy the creator
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get creation timestamp
     * @return timestamp in milliseconds
     */
    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    /**
     * Get last modified timestamp
     * @return timestamp in milliseconds
     */
    public long getLastModifiedTimestamp() {
        return lastModifiedTimestamp;
    }

    /**
     * Update the last modified timestamp to current time
     */
    protected void updateModifiedTimestamp() {
        this.lastModifiedTimestamp = System.currentTimeMillis();
    }

    /**
     * Abstract method to get entity type
     * Must be implemented by subclasses
     * @return entity type name
     */
    public abstract String getEntityType();

    /**
     * Abstract method to get display information
     * Must be implemented by subclasses
     * @return formatted display string
     */
    public abstract String getDisplayInfo();

    /**
     * Validate entity data
     * Can be overridden by subclasses for specific validation
     * @return true if valid, false otherwise
     */
    public boolean validate() {
        return id != null && !id.trim().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicalEntity that = (MedicalEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s[ID=%s]", getEntityType(), id);
    }
}


