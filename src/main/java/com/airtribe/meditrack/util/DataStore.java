package com.airtribe.meditrack.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Generic data storage class using generics.
 * Provides type-safe storage with CRUD operations.
 * Demonstrates generics, collections, and streams.
 *
 * @param <T> the type of entities to store
 * @author MediTrack Team
 * @version 1.0
 */
public class DataStore<T> {

    private final Map<String, T> dataMap;
    private final String storeName;

    /**
     * Constructor with store name
     * @param storeName name of this data store
     */
    public DataStore(String storeName) {
        this.dataMap = new HashMap<>();
        this.storeName = storeName;
    }

    /**
     * Add an entity to the store
     * @param id unique identifier
     * @param entity the entity to store
     * @return true if added successfully, false if ID already exists
     */
    public boolean add(String id, T entity) {
        if (id == null || entity == null) {
            return false;
        }

        if (dataMap.containsKey(id)) {
            return false; // ID already exists
        }

        dataMap.put(id, entity);
        return true;
    }

    /**
     * Update an existing entity
     * @param id unique identifier
     * @param entity the updated entity
     * @return true if updated successfully, false if ID doesn't exist
     */
    public boolean update(String id, T entity) {
        if (id == null || entity == null || !dataMap.containsKey(id)) {
            return false;
        }

        dataMap.put(id, entity);
        return true;
    }

    /**
     * Get an entity by ID
     * @param id unique identifier
     * @return the entity or null if not found
     */
    public T get(String id) {
        return dataMap.get(id);
    }

    /**
     * Delete an entity by ID
     * @param id unique identifier
     * @return the deleted entity or null if not found
     */
    public T delete(String id) {
        return dataMap.remove(id);
    }

    /**
     * Check if an entity exists
     * @param id unique identifier
     * @return true if exists, false otherwise
     */
    public boolean exists(String id) {
        return dataMap.containsKey(id);
    }

    /**
     * Get all entities as a list
     * @return list of all entities
     */
    public List<T> getAll() {
        return new ArrayList<>(dataMap.values());
    }

    /**
     * Get all IDs
     * @return list of all IDs
     */
    public List<String> getAllIds() {
        return new ArrayList<>(dataMap.keySet());
    }

    /**
     * Get count of entities
     * @return number of entities stored
     */
    public int size() {
        return dataMap.size();
    }

    /**
     * Check if store is empty
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return dataMap.isEmpty();
    }

    /**
     * Clear all entities from the store
     */
    public void clear() {
        dataMap.clear();
    }

    /**
     * Find entities matching a predicate (using Java 8 streams)
     * @param predicate the condition to match
     * @return list of matching entities
     */
    public List<T> findAll(Predicate<T> predicate) {
        return dataMap.values().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * Find first entity matching a predicate
     * @param predicate the condition to match
     * @return the first matching entity or null
     */
    public T findFirst(Predicate<T> predicate) {
        return dataMap.values().stream()
                .filter(predicate)
                .findFirst()
                .orElse(null);
    }

    /**
     * Count entities matching a predicate
     * @param predicate the condition to match
     * @return count of matching entities
     */
    public long count(Predicate<T> predicate) {
        return dataMap.values().stream()
                .filter(predicate)
                .count();
    }

    /**
     * Check if any entity matches the predicate
     * @param predicate the condition to match
     * @return true if at least one entity matches
     */
    public boolean anyMatch(Predicate<T> predicate) {
        return dataMap.values().stream()
                .anyMatch(predicate);
    }

    /**
     * Get store name
     * @return store name
     */
    public String getStoreName() {
        return storeName;
    }

    /**
     * Get a copy of the internal map (defensive copy)
     * @return copy of the data map
     */
    public Map<String, T> getDataMap() {
        return new HashMap<>(dataMap);
    }

    @Override
    public String toString() {
        return String.format("DataStore[%s: %d items]", storeName, size());
    }
}


