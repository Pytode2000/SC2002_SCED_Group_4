package controller;

import boundary.AdministratorMenu;
import boundary.DoctorMenu;
import boundary.PatientMenu;
import boundary.PharmacistMenu;
import entity.Administrator;
import entity.Doctor;
import entity.Patient;
import entity.Pharmacist;
import entity.User;
import interfaces.MenuInterface;

/**
 * The MenuController class determines and displays the appropriate menu 
 * based on the user's role. It utilizes specific menu classes for 
 * Patients, Doctors, Pharmacists, and Administrators.
 */
public class MenuController implements MenuInterface {

    /**
     * The user for whom the menu is being managed.
     */
    private User user;

    /**
     * Constructs a MenuController with the specified user.
     *
     * @param user The user for whom the menu will be displayed.
     */
    public MenuController(User user) {
        this.user = user;
    }

    /**
     * Displays the menu corresponding to the user's role.
     * Calls the specific menu for each user type:
     * <ul>
     * <li>PatientMenu for Patients</li>
     * <li>DoctorMenu for Doctors</li>
     * <li>PharmacistMenu for Pharmacists</li>
     * <li>AdministratorMenu for Administrators</li>
     * </ul>
     * If the user's role does not match any of the predefined roles,
     * an error message is displayed.
     */
    // Display the appropriate menu based on user role
    public void displayMenu() {
        if (user instanceof Patient) {
            new PatientMenu((Patient) user).displayMenu();
        } else if (user instanceof Doctor) {
            new DoctorMenu((Doctor) user).displayMenu();
        } else if (user instanceof Pharmacist) {
            new PharmacistMenu((Pharmacist) user).displayMenu();
        } else if (user instanceof Administrator) {
            new AdministratorMenu((Administrator) user).displayMenu();
        } else {
            System.out.println("Invalid user role. No menu available.");
        }
    }
}
