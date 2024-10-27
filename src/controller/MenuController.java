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

    // Method to display the appropriate menu based on user role
    public void displayMenu() {
        if (user instanceof Patient) {
            PatientMenu patientMenu = new PatientMenu((Patient) user);
            patientMenu.displayMenu();
        } else if (user instanceof Doctor) {
            DoctorMenu doctorMenu = new DoctorMenu((Doctor) user);
            doctorMenu.displayMenu();
        } else if (user instanceof Pharmacist) {
            PharmacistMenu pharmacistMenu = new PharmacistMenu((Pharmacist) user);
            pharmacistMenu.displayMenu();
        } else if (user instanceof Administrator) {
            AdministratorMenu administratorMenu = new AdministratorMenu((Administrator) user);
            administratorMenu.displayMenu();
        } else {
            System.out.println("Invalid user role. No menu available.");
        }
    }
}
