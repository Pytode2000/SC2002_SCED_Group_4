package utility;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    // Appends data to the specified file
    public static void writeToFile(String filePath, String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(data);
            writer.newLine();
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    // Updates an entry in the file by replacing the line matching the specified ID
    public static void updateToFile(String filePath, String data, String id) {
        File file = new File(filePath);
        List<String> lines = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line.startsWith(id) ? data : line);
            }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        
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

    // Deletes an entry in the file with the specified ID
    public static void deleteFromFile(String filePath, String id) {
        File file = new File(filePath);
        List<String> lines = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith(id)) lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        
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

    // Finds and returns an entry in the file with the specified ID
    public static String findEntryReturnString(String filePath, String id) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(id)) return line;
            }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("No entry found with ID: " + id);
        return null;
    }

    // Updates a specific field in an entry in the file by ID and index
    public static void updateEntry(String filePath, String id, String updatedText, int index) {
        File file = new File(filePath);
        List<String> lines = new ArrayList<>();
        boolean updated = false;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(id)) {
                    String[] fields = line.split("\\|");
                    if (index >= 0 && index < fields.length) {
                        fields[index] = updatedText;
                        line = String.join("|", fields);
                        updated = true;
                        System.out.println("Updated entry in file.");
                    } else {
                        System.out.println("Index out of bounds for entry: " + line);
                        return;
                    }
                }
                lines.add(line);
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
