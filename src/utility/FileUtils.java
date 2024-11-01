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
}
