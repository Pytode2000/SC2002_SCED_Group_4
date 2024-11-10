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

public class MenuController implements MenuInterface {

    private User user;

    public MenuController(User user) {
        this.user = user;
    }

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
