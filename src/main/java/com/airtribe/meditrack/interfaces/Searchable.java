package com.airtribe.meditrack.interfaces;

/**
 * Interface for entities that support search functionality.
 * Demonstrates interface usage with default methods.
 *
 * @author MediTrack Team
 * @version 1.0
 */
public interface Searchable {

    /**
     * Check if this entity matches the given search query.
     * @param query the search query string
     * @return true if matches, false otherwise
     */
    boolean matches(String query);

    /**
     * Get searchable fields as a concatenated string.
     * Default method that can be overridden.
     * @return searchable content
     */
    default String getSearchableContent() {
        return toString().toLowerCase();
    }

    /**
     * Perform case-insensitive partial match.
     * Default implementation using getSearchableContent().
     * @param query the search query
     * @return true if query is found in searchable content
     */
    default boolean partialMatch(String query) {
        if (query == null || query.trim().isEmpty()) {
            return false;
        }
        return getSearchableContent().contains(query.toLowerCase().trim());
    }

    /**
     * Get the unique identifier for this searchable entity.
     * @return unique ID
     */
    String getId();

    /**
     * Get a display name or title for this entity.
     * Default implementation returns ID.
     * @return display name
     */
    default String getDisplayName() {
        return getId();
    }
}


