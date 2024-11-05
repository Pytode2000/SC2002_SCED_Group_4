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

public class ForgetPasswordController {

    private static final String ACCOUNT_FILE = "data/account.txt";
    private static final String FORGET_PASSWORD_FILE = "data/forgetPassword.txt";
    private static final String DEFAULT_PASSWORD = "password"; // Default password for reset

    private AccountController accountController = new AccountController(); // Assuming you have this class

    // Method to handle the forget password request
    public void handleForgetPasswordRequest() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your User ID (or 0 to cancel): ");
        String userId = scanner.nextLine().trim();
        if (userId.equals("0")) {
            System.out.println("Exiting request.");
            return;
        }
        userId = userId.toUpperCase(); // Capitalize all letters

        if (!isUserIdExist(userId)) {
            System.out.println("User ID not found. Please try again.");
            PrintUtils.pause();
            return;
        }

        System.out.print("Enter a message for the reset request (or 0 to cancel): ");
        String message = scanner.nextLine().trim();
        if (message.equals("0")) {
            System.out.println("Exiting request.");
            return;
        }
        if (message.length() == 0) {
            message = "-";
        }

        ForgetPassword forgetPasswordRequest = new ForgetPassword(userId, message);

        // Step 5: Write the request details to ForgetPassword.txt
        writeRequestToFile(forgetPasswordRequest);
        System.out.println("Your password reset request has been submitted.");
        PrintUtils.pause();
    }

    // Method to view all forget password requests and allow admin to process them
    public void processForgetPasswordRequests() {
        List<ForgetPassword> requests = readAllRequests();

        if (requests.isEmpty()) {
            System.out.println("No password reset requests found.");
            PrintUtils.pause();
            return;
        }
        Scanner scanner = new Scanner(System.in);

        // Prompt admin to select a request by index
        while (true) {

            // Display all requests in a table
            System.out.println("\n--- Password Reset Requests ---");
            System.out.printf("%-5s %-10s %-32s %-22s%n", "No.", "User ID", "Message", "Request Date");
            for (int i = 0; i < requests.size(); i++) {
                ForgetPassword request = requests.get(i);
                System.out.printf("%-5d %-10s %-32s %-22s%n",
                        i + 1, request.getUserId(), request.getMessage(),
                        request.getRequestDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
            }

            System.out.print("\nEnter the request number to process (or 0 to cancel): ");
            int index;
            try {
                index = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                continue;
            }

            if (index == 0) {
                System.out.println("Exiting request processing.");
                PrintUtils.pause();

                break;
            }

            if (index < 1 || index > requests.size()) {
                System.out.println("Invalid index. Please try again.");
                continue;
            }

            // Process the selected request
            ForgetPassword selectedRequest = requests.get(index - 1);
            System.out.println("\nSelected Request: " + selectedRequest.getUserId());

            System.out.println("1. Reset to default password");
            System.out.println("2. Reject request");
            System.out.println("0. Exit");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    // Reset to default password
                    resetPassword(selectedRequest.getUserId());
                    requests.remove(index - 1);
                    updateRequestsFile(requests);
                    System.out.println("Password has been reset to default.");
                    PrintUtils.pause();

                    break;
                case "2":
                    // Reject request
                    requests.remove(index - 1);
                    updateRequestsFile(requests);
                    System.out.println("Request has been rejected.");
                    PrintUtils.pause();

                    break;
                case "0":
                    System.out.println("Exiting.");
                    PrintUtils.pause();

                    return;
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, or 0.");
                    break;
            }
        }
    }

    // Helper method to check if the userId exists in account.txt
    private boolean isUserIdExist(String userId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] accountData = line.split("\\|");
                if (accountData[0].equalsIgnoreCase(userId)) {
                    return true; // User ID found
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading account file: " + e.getMessage());
        }
        return false; // User ID not found
    }

    // Helper method to read all password reset requests from ForgetPassword.txt
    private List<ForgetPassword> readAllRequests() {
        List<ForgetPassword> requests = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FORGET_PASSWORD_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");
                if (fields.length >= 3) {
                    String userId = fields[0];
                    String message = fields[1];
                    requests.add(new ForgetPassword(userId, message));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading ForgetPassword.txt: " + e.getMessage());
        }
        return requests;
    }

    // Helper method to reset the password to the default value
    private void resetPassword(String userId) {
        accountController.updatePassword(userId, DEFAULT_PASSWORD);
    }

    // Helper method to update ForgetPassword.txt after processing requests
    private void updateRequestsFile(List<ForgetPassword> requests) {
        try (FileWriter writer = new FileWriter(FORGET_PASSWORD_FILE, false)) { // Overwrite the file
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

            for (ForgetPassword request : requests) {
                String formattedDate = request.getRequestDateTime().format(formatter);
                String data = request.getUserId() + "|" + request.getMessage() + "|" + formattedDate;
                writer.write(data + System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Error updating ForgetPassword.txt: " + e.getMessage());
        }
    }

    // Method to write the forget password request to ForgetPassword.txt
    private void writeRequestToFile(ForgetPassword request) {
        try (FileWriter writer = new FileWriter(FORGET_PASSWORD_FILE, true)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String formattedDate = request.getRequestDateTime().format(formatter);

            // Format the data as "UserId | Message | Request Date and Time"
            String data = request.getUserId() + "|" + request.getMessage() + "|" + formattedDate;
            writer.write(data + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Error writing to ForgetPassword.txt: " + e.getMessage());
        }
    }
}
