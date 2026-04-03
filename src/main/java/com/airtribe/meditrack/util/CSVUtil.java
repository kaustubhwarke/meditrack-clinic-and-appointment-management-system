package com.airtribe.meditrack.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for CSV file operations.
 * Demonstrates File I/O with try-with-resources.
 *
 * @author MediTrack Team
 * @version 1.0
 */
public class CSVUtil {

    private static final String COMMA_DELIMITER = ",";
    private static final String LINE_SEPARATOR = System.lineSeparator();

    /**
     * Private constructor to prevent instantiation
     */
    private CSVUtil() {
        throw new AssertionError("CSVUtil class cannot be instantiated");
    }

    /**
     * Read CSV file and return list of rows (each row is a String array)
     * Uses try-with-resources for automatic resource management
     *
     * @param filePath path to the CSV file
     * @return list of rows, each row is an array of strings
     * @throws IOException if file reading fails
     */
    public static List<String[]> readCSV(String filePath) throws IOException {
        List<String[]> records = new ArrayList<>();

        // try-with-resources ensures BufferedReader is closed automatically
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Split by comma and trim each field
                String[] values = line.split(COMMA_DELIMITER);
                for (int i = 0; i < values.length; i++) {
                    values[i] = values[i].trim();
                }
                records.add(values);
            }
        }

        return records;
    }

    /**
     * Write data to CSV file
     * Uses try-with-resources for automatic resource management
     *
     * @param filePath path to the CSV file
     * @param records list of rows to write
     * @param append true to append, false to overwrite
     * @throws IOException if file writing fails
     */
    public static void writeCSV(String filePath, List<String[]> records, boolean append)
            throws IOException {

        // Ensure parent directory exists
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        // try-with-resources ensures BufferedWriter is closed automatically
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, append))) {
            for (String[] record : records) {
                bw.write(String.join(COMMA_DELIMITER, record));
                bw.write(LINE_SEPARATOR);
            }
        }
    }

    /**
     * Write a single record to CSV file
     *
     * @param filePath path to the CSV file
     * @param record array of values to write
     * @param append true to append, false to overwrite
     * @throws IOException if file writing fails
     */
    public static void writeRecord(String filePath, String[] record, boolean append)
            throws IOException {
        List<String[]> records = new ArrayList<>();
        records.add(record);
        writeCSV(filePath, records, append);
    }

    /**
     * Check if CSV file exists and is readable
     *
     * @param filePath path to the CSV file
     * @return true if file exists and is readable
     */
    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.canRead();
    }

    /**
     * Create an empty CSV file with header
     *
     * @param filePath path to the CSV file
     * @param header header row
     * @throws IOException if file creation fails
     */
    public static void createWithHeader(String filePath, String[] header) throws IOException {
        List<String[]> records = new ArrayList<>();
        records.add(header);
        writeCSV(filePath, records, false);
    }

    /**
     * Read CSV file skipping the header row
     *
     * @param filePath path to the CSV file
     * @return list of data rows (excluding header)
     * @throws IOException if file reading fails
     */
    public static List<String[]> readCSVWithoutHeader(String filePath) throws IOException {
        List<String[]> records = readCSV(filePath);
        if (!records.isEmpty()) {
            records.remove(0); // Remove header
        }
        return records;
    }

    /**
     * Escape special characters in CSV field
     *
     * @param field the field to escape
     * @return escaped field
     */
    public static String escapeField(String field) {
        if (field == null) {
            return "";
        }

        // If field contains comma, quote, or newline, wrap in quotes
        if (field.contains(COMMA_DELIMITER) || field.contains("\"") || field.contains("\n")) {
            // Escape quotes by doubling them
            field = field.replace("\"", "\"\"");
            return "\"" + field + "\"";
        }

        return field;
    }

    /**
     * Parse a CSV line handling quoted fields
     * More robust than simple split
     *
     * @param line the CSV line to parse
     * @return array of fields
     */
    public static String[] parseLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder field = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                // Check for escaped quote
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    field.append('"');
                    i++; // Skip next quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(field.toString().trim());
                field = new StringBuilder();
            } else {
                field.append(c);
            }
        }

        result.add(field.toString().trim());
        return result.toArray(new String[0]);
    }

    /**
     * Count number of records in CSV file (excluding header)
     *
     * @param filePath path to the CSV file
     * @return number of records
     * @throws IOException if file reading fails
     */
    public static int countRecords(String filePath) throws IOException {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // Skip header
            while (br.readLine() != null) {
                count++;
            }
        }
        return count;
    }

    /**
     * Delete CSV file
     *
     * @param filePath path to the CSV file
     * @return true if deleted successfully
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.delete();
    }
}


