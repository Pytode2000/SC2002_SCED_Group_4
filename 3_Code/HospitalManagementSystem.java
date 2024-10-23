
public class HospitalManagementSystem {

    public static void main(String[] args) {
        // Create an AccountController to manage login
        AccountController accountController = new AccountController();

        // Simulate user input for login
        String inputUserId = "U001";  // Replace with input
        String inputPassword = "password123";  // Replace with input

        // Attempt login
        Account loggedInAccount = accountController.login(inputUserId, inputPassword);

        // Check if login was successful
        if (loggedInAccount != null) {
            System.out.println("Login successful. Welcome, " + loggedInAccount.getUser().getFirstName());
        } else {
            System.out.println("Login failed.");
        }

    }
}
