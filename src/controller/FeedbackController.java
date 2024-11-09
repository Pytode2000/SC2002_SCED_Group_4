package controller;

import entity.Feedback;
import utility.FileUtils;
import utility.PrintUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FeedbackController {

    private static final String STAFF_FILE = "data/staff.txt";
    private static final String FEEDBACK_FILE = "data/feedback.txt"; // Assuming feedback is saved here
    private Scanner scanner = new Scanner(System.in);

    // Method to display doctors and allow the patient to select one
    public void provideFeedback(String patientId) {
        List<String> doctorList = getDoctorList();

        if (doctorList.isEmpty()) {
            System.out.println("No doctors available.");
            PrintUtils.pause();
            return;
        }

        // Display doctors
        System.out.println("\n--- Available Doctors ---");
        for (int i = 0; i < doctorList.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, doctorList.get(i));
        }
        System.out.println("0. Exit");

        // Get doctor's index selection
        System.out.print("Select a doctor (1-" + doctorList.size() + " or 0 to exit): ");
        int doctorIndex = getValidDoctorSelection(doctorList.size());

        if (doctorIndex == 0) {
            System.out.println("Exiting...");
            PrintUtils.pause();
            return;
        }

        String doctorId = doctorList.get(doctorIndex - 1).split(" ")[0]; // Extract the doctor ID (assuming the format is "DRxxx Name")
        collectFeedback(patientId, doctorId);
    }

    // Collect feedback from the patient
    private void collectFeedback(String patientId, String doctorId) {
        int rating = getValidRating();
        System.out.print("Enter your comments (press Enter to skip): ");
        String comments = scanner.nextLine().trim();
        if (comments.isEmpty()) {
            comments = "-";
        }

        // Create Feedback object and save it
        Feedback feedback = new Feedback(patientId, doctorId, rating, comments);
        saveFeedback(feedback);
        System.out.println("Thank you for your feedback!");
        PrintUtils.pause();
    }

    // Save feedback to the file
    private void saveFeedback(Feedback feedback) {
        String feedbackData = String.join("|",
                feedback.getPatientId(),
                feedback.getDoctorId(),
                String.valueOf(feedback.getRating()),
                feedback.getComments());

        FileUtils.writeToFile(FEEDBACK_FILE, feedbackData);
    }

    // Get the list of doctors from the staff file
    private List<String> getDoctorList() {
        List<String> doctorList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(STAFF_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");
                if ("Doctor".equalsIgnoreCase(fields[7])) { // assuming the 8th field indicates the role
                    doctorList.add(fields[0] + " " + fields[1] + " " + fields[2]); // Assuming doctor ID is in the 1st field
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading staff file: " + e.getMessage());
        }

        return doctorList;
    }

    // Validate the doctor selection (1 to n, or 0 to exit)
    private int getValidDoctorSelection(int maxSelection) {
        while (true) {
            try {
                int selection = Integer.parseInt(scanner.nextLine().trim());
                if (selection >= 0 && selection <= maxSelection) {
                    return selection;
                } else {
                    System.out.println("Invalid selection. Please enter a number between 0 and " + maxSelection + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    // Get a valid rating (1 to 10)
    private int getValidRating() {
        while (true) {
            System.out.print("Enter a rating (1-10): ");
            try {
                int rating = Integer.parseInt(scanner.nextLine().trim());
                if (rating >= 1 && rating <= 10) {
                    return rating;
                } else {
                    System.out.println("Invalid rating. Please enter a number between 1 and 10.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    // Method to display all ratings for a doctor along with the average rating
    public void viewDoctorRatings(String doctorId) {
        List<Feedback> feedbackList = getFeedbackForDoctor(doctorId);

        if (feedbackList.isEmpty()) {
            System.out.println("No feedback available for Doctor ID: " + doctorId);
            PrintUtils.pause();
            return;
        }

        // Display feedback in a table format
        System.out.println("\n--- Feedback for Doctor ID: " + doctorId + " ---");
        System.out.printf("%-15s %-10s %-32s%n", "Patient ID", "Rating", "Comments");
        System.out.println("-----------------------------------------------------------");

        for (Feedback feedback : feedbackList) {
            String comments = feedback.getComments().equals("-") ? "No comments" : feedback.getComments();
            System.out.printf("%-15s %-10d %-32s%n", feedback.getPatientId(), feedback.getRating(), comments);
        }

        // Calculate and display average rating
        double averageRating = calculateAverageRating(feedbackList);
        System.out.printf("\nAverage Rating: %.2f/10\n", averageRating);
        PrintUtils.pause();
    }

    // Retrieve all feedback for a specific doctor from the feedback file
    private List<Feedback> getFeedbackForDoctor(String doctorId) {
        List<Feedback> feedbackList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FEEDBACK_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");
                if (fields.length >= 4 && fields[1].equals(doctorId)) {
                    String patientId = fields[0];
                    int rating = Integer.parseInt(fields[2]);
                    String comments = fields[3];
                    Feedback feedback = new Feedback(patientId, doctorId, rating, comments);
                    feedbackList.add(feedback);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading feedback file: " + e.getMessage());
        }

        return feedbackList;
    }

    // Calculate the average rating for a list of feedback
    private double calculateAverageRating(List<Feedback> feedbackList) {
        int totalRating = 0;
        for (Feedback feedback : feedbackList) {
            totalRating += feedback.getRating();
        }
        return feedbackList.isEmpty() ? 0.0 : (double) totalRating / feedbackList.size();
    }
}
