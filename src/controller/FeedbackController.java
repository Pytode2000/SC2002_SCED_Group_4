package controller;

import entity.Feedback;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import utility.FileUtils;
import utility.PrintUtils;

public class FeedbackController {

    private static final String STAFF_FILE = "data/staff.txt";
    private static final String FEEDBACK_FILE = "data/feedback.txt"; 
    private Scanner scanner = new Scanner(System.in);

    // Display doctors, collect feedback for selected doctor
    public void provideFeedback(String patientId) {
        List<String> doctorList = getDoctorList();

        if (doctorList.isEmpty()) {
            System.out.println("No doctors available.");
            PrintUtils.pause();
            return;
        }

        System.out.println("\n--- Available Doctors ---");
        for (int i = 0; i < doctorList.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, doctorList.get(i));
        }
        System.out.println("0. Exit");

        int doctorIndex = getValidSelection(doctorList.size(), "Select a doctor (1-" + doctorList.size() + " or 0 to exit): ");
        if (doctorIndex == 0) {
            System.out.println("Exiting...");
            PrintUtils.pause();
            return;
        }

        String doctorId = doctorList.get(doctorIndex - 1).split(" ")[0]; 
        collectFeedback(patientId, doctorId);
    }

    private void collectFeedback(String patientId, String doctorId) {
        int rating = getValidSelection(10, "Enter a rating (1-10): ");
        System.out.print("Enter your comments (press Enter to skip): ");
        
        // Capture comment in a single line to avoid double reading issues
        String comments = scanner.nextLine().trim();
        if (comments.isEmpty()) {
            comments = "-"; // Set to "-" if no comment is provided
        }
    
        // Save feedback to the file
        FileUtils.writeToFile(FEEDBACK_FILE, String.join("|", patientId, doctorId, String.valueOf(rating), comments));
        System.out.println("Thank you for your feedback!");
        PrintUtils.pause();
    }
    

    // Retrieve and display all ratings for a doctor with average rating
    public void viewDoctorRatings(String doctorId) {
        List<Feedback> feedbackList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FEEDBACK_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");
                if (fields.length >= 4 && fields[1].equals(doctorId)) {
                    feedbackList.add(new Feedback(fields[0], doctorId, Integer.parseInt(fields[2]), fields[3]));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading feedback file: " + e.getMessage());
        }

        if (feedbackList.isEmpty()) {
            System.out.println("No feedback available for Doctor ID: " + doctorId);
            PrintUtils.pause();
            return;
        }

        System.out.println("\n--- Feedback for Doctor ID: " + doctorId + " ---");
        System.out.printf("%-15s %-10s %-32s%n", "Patient ID", "Rating", "Comments");
        System.out.println("-----------------------------------------------------------");

        int totalRating = 0;
        for (Feedback feedback : feedbackList) {
            String comments = feedback.getComments().equals("-") ? "No comments" : feedback.getComments();
            System.out.printf("%-15s %-10d %-32s%n", feedback.getPatientId(), feedback.getRating(), comments);
            totalRating += feedback.getRating();
        }
        System.out.printf("\nAverage Rating: %.2f/10\n", (double) totalRating / feedbackList.size());
        PrintUtils.pause();
    }

    // Retrieve doctors' list from the staff file
    private List<String> getDoctorList() {
        List<String> doctorList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(STAFF_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");
                if ("Doctor".equalsIgnoreCase(fields[7])) { 
                    doctorList.add(fields[0] + " " + fields[1] + " " + fields[2]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading staff file: " + e.getMessage());
        }
        return doctorList;
    }

    // Validate input for selections
    private int getValidSelection(int max, String prompt) {
        int selection;
        while (true) {
            try {
                System.out.print(prompt);
                selection = Integer.parseInt(scanner.nextLine().trim());
                if (selection >= 0 && selection <= max) return selection;
                System.out.println("Invalid selection. Please enter a number between 0 and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
}
