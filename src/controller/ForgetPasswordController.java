package controller;

import entity.ForgetPassword;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import utility.PrintUtils;

/**
 * The ForgetPasswordController class manages the forget password functionality.
 * It allows users to submit password reset requests and enables administrators 
 * to process these requests, either resetting the password to a default value 
 * or rejecting the request.
 */
public class ForgetPasswordController {

    private static final String ACCOUNT_FILE = "data/account.txt";
    private static final String FORGET_PASSWORD_FILE = "data/forgetPassword.txt";
    private static final String DEFAULT_PASSWORD = "password";
    private AccountController accountController = new AccountController();

    // Handle a forget password request from the user
     /**
     * Handles a forget password request from the user.
     * Prompts the user for their User ID and a message, validates the input,
     * and stores the request in the forget password file.
     */
    public void handleForgetPasswordRequest() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your User ID (0 to cancel): ");
        String userId = scanner.nextLine().trim().toUpperCase();
        if (userId.equals("0")) {
            System.out.println("Exiting request.");
            return;
        }

        if (!isUserIdExist(userId)) {
            System.out.println("User ID not found. Please try again.");
            PrintUtils.pause();
            return;
        }

        System.out.print("Enter a message for the reset request (0 to cancel): ");
        String message = scanner.nextLine().trim();
        if (message.equals("0")) {
            System.out.println("Exiting request.");
            return;
        }

        ForgetPassword forgetPasswordRequest = new ForgetPassword(userId, message.isEmpty() ? "-" : message);
        writeRequestToFile(forgetPasswordRequest);
        System.out.println("Your password reset request has been submitted.");
        PrintUtils.pause();
    }

    // Admin view and process forget password requests
    /**
     * Admin view and processing of forget password requests.
     * Allows administrators to view all pending requests and process them
     * by resetting the password or rejecting the request.
     */
    public void processForgetPasswordRequests() {
        List<ForgetPassword> requests = readAllRequests();
        if (requests.isEmpty()) {
            System.out.println("No password reset requests found.");
            PrintUtils.pause();
            return;
        }
        Scanner scanner = new Scanner(System.in);

        while (true) {
            displayRequests(requests);
            int index = getRequestIndex(scanner, requests.size());
            if (index == 0) {
                return;
            }

            ForgetPassword selectedRequest = requests.get(index - 1);
            if (processRequestSelection(scanner, selectedRequest)) {
                requests.remove(index - 1);
                updateRequestsFile(requests);
            }
        }
    }

    // Check if the user ID exists in the account file
    /**
     * Checks if the given User ID exists in the account file.
     * 
     * @param userId the User ID to check
     * @return true if the User ID exists, false otherwise
     */
    private boolean isUserIdExist(String userId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.split("\\|")[0].equalsIgnoreCase(userId)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading account file: " + e.getMessage());
        }
        return false;
    }

    // Read all forget password requests from file
    /**
     * Reads all forget password requests from the forget password file.
     * 
     * @return a list of {@link ForgetPassword} objects representing the requests
     */
    private List<ForgetPassword> readAllRequests() {
        List<ForgetPassword> requests = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FORGET_PASSWORD_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");
                if (fields.length == 3) { // Ensure correct number of fields
                    requests.add(new ForgetPassword(fields[0], fields[1], LocalDateTime.parse(fields[2], DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) // Date/Time
                    ));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading ForgetPassword.txt: " + e.getMessage());
        }
        return requests;
    }

    // Display all requests for selection
    /**
     * Displays all forget password requests in a formatted table.
     * 
     * @param requests the list of requests to display
     */
    private void displayRequests(List<ForgetPassword> requests) {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         Password Reset Requests        ║");
        System.out.println("╚════════════════════════════════════════╝");

        System.out.printf("%-5s %-10s %-32s %-22s%n", "No.", "User ID", "Message", "Request Date");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        for (int i = 0; i < requests.size(); i++) {
            ForgetPassword request = requests.get(i);
            System.out.printf("%-5d %-10s %-32s %-22s%n",
                    i + 1, request.getUserId(), request.getMessage(),
                    request.getRequestDateTime().format(formatter));
        }
    }

    /**
     * Gets a valid request index from the administrator.
     * 
     * @param scanner the scanner for reading input
     * @param maxIndex the maximum valid index
     * @return the valid request index or 0 to cancel
     */
    private int getRequestIndex(Scanner scanner, int maxIndex) {
        while (true) {
            System.out.print("\nEnter the request number to process (or 0 to cancel): ");
            try {
                int index = Integer.parseInt(scanner.nextLine().trim());
                if (index == 0) {
                    return 0;
                }
                if (index >= 1 && index <= maxIndex) {
                    return index;
                }
                System.out.println("Invalid index. Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    /**
     * Processes the selected forget password request based on the admin's choice.
     * 
     * @param scanner the scanner for reading input
     * @param selectedRequest the selected request to process
     * @return true if the request was processed successfully, false otherwise
     */
    private boolean processRequestSelection(Scanner scanner, ForgetPassword selectedRequest) {
        System.out.println("\nSelected Request: " + selectedRequest.getUserId());
        System.out.println("1. Reset to default password");
        System.out.println("2. Reject request");
        System.out.println("0. Exit");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                resetPassword(selectedRequest.getUserId());
                System.out.println("Password has been reset to default.");
                PrintUtils.pause();
                return true;
            case "2":
                System.out.println("Request has been rejected.");
                PrintUtils.pause();
                return true;
            case "0":
                System.out.println("Exiting.");
                PrintUtils.pause();
                return false;
            default:
                System.out.println("Invalid choice. Please enter 1, 2, or 0.");
                return false;
        }
    }

    /**
     * Resets the password of the given User ID to the default password.
     * 
     * @param userId the User ID whose password will be reset
     */
    // Reset the password in the account file to the default password
    private void resetPassword(String userId) {
        accountController.updatePassword(userId, DEFAULT_PASSWORD);
    }

    /**
     * Updates the forget password file after processing requests.
     * 
     * @param requests the updated list of requests to write back to the file
     */
    // Update ForgetPassword file after processing requests
    private void updateRequestsFile(List<ForgetPassword> requests) {
        try (FileWriter writer = new FileWriter(FORGET_PASSWORD_FILE, false)) { // Overwrite the file
            for (ForgetPassword request : requests) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                // Write each request back to the file without changing the date/time
                writer.write(String.format("%s|%s|%s%n",
                        request.getUserId(),
                        request.getMessage(),
                        request.getRequestDateTime().format(formatter)));
            }
        } catch (IOException e) {
            System.out.println("Error updating ForgetPassword.txt: " + e.getMessage());
        }
    }

    /**
     * Writes a new forget password request to the forget password file.
     * 
     * @param request the request to write to the file
     */
    // Write a forget password request to the file
    private void writeRequestToFile(ForgetPassword request) {
        try (FileWriter writer = new FileWriter(FORGET_PASSWORD_FILE, true)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            writer.write(String.format("%s|%s|%s%n",
                    request.getUserId(),
                    request.getMessage(),
                    request.getRequestDateTime().format(formatter)));
        } catch (IOException e) {
            System.out.println("Error writing to ForgetPassword.txt: " + e.getMessage());
        }
    }
}
