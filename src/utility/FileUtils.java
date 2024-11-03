package utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    public static void writeToFile(String filePath, String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(data);
            writer.newLine();
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    public static void updateToFile(String filePath, String data, String id) {
        File file = new File(filePath);
        List<String> lines = new ArrayList<>();

        // Read the existing content of the file
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
            e.printStackTrace();
            return; // Exit if reading fails
        }

        // Find the line with the specified ID and update it
        boolean updated = false;
        for (int i = 0; i < lines.size(); i++) {
            String currentLine = lines.get(i);
            // Check if the current line starts with the specified ID
            if (currentLine.startsWith(id)) {
                lines.set(i, data); // Update the line with new data
                updated = true;
                break; // Exit loop after updating
            }
        }

        if (!updated) {
            System.out.println("No entry found with ID: " + id);
            return; // Exit if the ID was not found
        }

        // Write the updated content back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String updatedLine : lines) {
                writer.write(updatedLine);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to the file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void deleteFromFile(String filePath, String id) {
        File file = new File(filePath);
        List<String> lines = new ArrayList<>();

        // Read the existing content of the file
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Add lines that do not start with the specified ID
                if (!line.startsWith(id)) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
            e.printStackTrace();
            return; // Exit if reading fails
        }

        // Write the updated content back to the file (overwrite file with remaining lines)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String updatedLine : lines) {
                writer.write(updatedLine);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to the file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String findEntryReturnString(String filePath, String id) {
        File file = new File(filePath);
    
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(id)) {  // Check if the line starts with the specified ID
                    return line;  // Return the matching line as a string
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
            e.printStackTrace();
        }
    
        System.out.println("No entry found with ID: " + id);
        return null;  // Return null if no matching ID is found
    }

    public static void updateEntry(String filePath, String id, String updatedText, int index) {
        File file = new File(filePath);
        List<String> lines = new ArrayList<>();
        boolean updated = false;
    
        // Read the file and add lines to the list
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(id)) {  // Find the line with the specified ID
                    String[] fields = line.split("\\|");  // Split the line by "|"
    
                    // Check if the index is within bounds
                    if (index >= 0 && index < fields.length) {
                        fields[index] = updatedText;  // Update the specific field
                        line = String.join("|", fields);  // Join the fields back into a single string
                        updated = true;
                        System.out.println("Updated entry to file.");
                    } else {
                        System.out.println("Index out of bounds for entry: " + line);
                        return;  // Exit if the index is invalid
                    }
                }
                lines.add(line);  // Add the (possibly updated) line to the list
            }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
            e.printStackTrace();
            return;
        }
    
        if (!updated) {
            System.out.println("No entry found with ID: " + id);
            return;
        }
    
        // Write the updated lines back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to the file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
}
