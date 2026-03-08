package com.airtribe.meditrack.util;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for date and time operations.
 * Provides helper methods for formatting, parsing, and manipulating dates.
 *
 * @author MediTrack Team
 * @version 1.0
 */
public class DateUtil {

    // Date and time formatters
    public static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public static final DateTimeFormatter TIME_FORMATTER =
        DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter DATETIME_FORMATTER =
        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    public static final DateTimeFormatter DISPLAY_FORMATTER =
        DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    /**
     * Private constructor to prevent instantiation
     */
    private DateUtil() {
        throw new AssertionError("DateUtil class cannot be instantiated");
    }

    /**
     * Get current date and time
     * @return current LocalDateTime
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * Get current date
     * @return current LocalDate
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * Format date to string
     * @param date the date to format
     * @return formatted date string
     */
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }

    /**
     * Format time to string
     * @param time the time to format
     * @return formatted time string
     */
    public static String formatTime(LocalTime time) {
        return time != null ? time.format(TIME_FORMATTER) : "";
    }

    /**
     * Format date time to string
     * @param dateTime the date time to format
     * @return formatted date time string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : "";
    }

    /**
     * Format date time for display
     * @param dateTime the date time to format
     * @return formatted display string
     */
    public static String formatForDisplay(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DISPLAY_FORMATTER) : "";
    }

    /**
     * Parse date string
     * @param dateStr the date string to parse
     * @return parsed LocalDate or null if invalid
     */
    public static LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Parse time string
     * @param timeStr the time string to parse
     * @return parsed LocalTime or null if invalid
     */
    public static LocalTime parseTime(String timeStr) {
        try {
            return LocalTime.parse(timeStr, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Parse date time string
     * @param dateTimeStr the date time string to parse
     * @return parsed LocalDateTime or null if invalid
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Check if date is in the past
     * @param date the date to check
     * @return true if date is before today
     */
    public static boolean isInPast(LocalDate date) {
        return date != null && date.isBefore(LocalDate.now());
    }

    /**
     * Check if date time is in the past
     * @param dateTime the date time to check
     * @return true if date time is before now
     */
    public static boolean isInPast(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isBefore(LocalDateTime.now());
    }

    /**
     * Check if date is in the future
     * @param date the date to check
     * @return true if date is after today
     */
    public static boolean isInFuture(LocalDate date) {
        return date != null && date.isAfter(LocalDate.now());
    }

    /**
     * Check if date time is in the future
     * @param dateTime the date time to check
     * @return true if date time is after now
     */
    public static boolean isInFuture(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isAfter(LocalDateTime.now());
    }

    /**
     * Check if date is today
     * @param date the date to check
     * @return true if date is today
     */
    public static boolean isToday(LocalDate date) {
        return date != null && date.isEqual(LocalDate.now());
    }

    /**
     * Calculate age from date of birth
     * @param dateOfBirth the date of birth
     * @return age in years
     */
    public static int calculateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) return 0;
        return (int) ChronoUnit.YEARS.between(dateOfBirth, LocalDate.now());
    }

    /**
     * Calculate duration between two date times in minutes
     * @param start start date time
     * @param end end date time
     * @return duration in minutes
     */
    public static long durationInMinutes(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return 0;
        return ChronoUnit.MINUTES.between(start, end);
    }

    /**
     * Add days to a date
     * @param date the base date
     * @param days number of days to add
     * @return new date with days added
     */
    public static LocalDate addDays(LocalDate date, long days) {
        return date != null ? date.plusDays(days) : null;
    }

    /**
     * Add minutes to a date time
     * @param dateTime the base date time
     * @param minutes number of minutes to add
     * @return new date time with minutes added
     */
    public static LocalDateTime addMinutes(LocalDateTime dateTime, long minutes) {
        return dateTime != null ? dateTime.plusMinutes(minutes) : null;
    }

    /**
     * Get start of day for a date
     * @param date the date
     * @return LocalDateTime at start of day (00:00)
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        return date != null ? date.atStartOfDay() : null;
    }

    /**
     * Get end of day for a date
     * @param date the date
     * @return LocalDateTime at end of day (23:59:59)
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        return date != null ? date.atTime(23, 59, 59) : null;
    }

    /**
     * Check if a date time falls within business hours (9 AM to 6 PM)
     * @param dateTime the date time to check
     * @return true if within business hours
     */
    public static boolean isBusinessHours(LocalDateTime dateTime) {
        if (dateTime == null) return false;
        int hour = dateTime.getHour();
        return hour >= 9 && hour < 18;
    }

    /**
     * Check if a date is a weekend (Saturday or Sunday)
     * @param date the date to check
     * @return true if weekend
     */
    public static boolean isWeekend(LocalDate date) {
        if (date == null) return false;
        int dayOfWeek = date.getDayOfWeek().getValue();
        return dayOfWeek == 6 || dayOfWeek == 7; // Saturday or Sunday
    }

    /**
     * Get next available slot time (rounded to next 30 min interval)
     * @param from starting date time
     * @return next available slot time
     */
    public static LocalDateTime getNextAvailableSlot(LocalDateTime from) {
        if (from == null) from = LocalDateTime.now();

        int minute = from.getMinute();
        int roundedMinute = ((minute + 29) / 30) * 30; // Round up to next 30 min

        LocalDateTime nextSlot = from.withMinute(0).withSecond(0).withNano(0);
        nextSlot = nextSlot.plusMinutes(roundedMinute);

        // If we've moved to next hour, adjust
        if (roundedMinute >= 60) {
            nextSlot = nextSlot.plusHours(1).withMinute(roundedMinute - 60);
        }

        // Ensure it's in business hours
        if (nextSlot.getHour() >= 18) {
            nextSlot = nextSlot.plusDays(1).withHour(9).withMinute(0);
        }
        if (nextSlot.getHour() < 9) {
            nextSlot = nextSlot.withHour(9).withMinute(0);
        }

        // Skip weekends
        while (isWeekend(nextSlot.toLocalDate())) {
            nextSlot = nextSlot.plusDays(1);
        }

        return nextSlot;
    }
}


