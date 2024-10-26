package controller;

import interfaces.MenuInterface;
import boundary.PatientMenu;
import boundary.DoctorMenu;
import boundary.PharmacistMenu;
import boundary.AdministratorMenu;

import entity.User;
import entity.Patient;
import entity.Doctor;
import entity.Pharmacist;
import entity.Administrator;

public class MenuController implements MenuInterface {

    private User user;

    public MenuController(User user) {
        this.user = user;
    }

    // Method to display the appropriate menu based on user role
    public void displayMenu() {
        switch (user.getUserRole()) {
            case "Patient":
                PatientMenu patientMenu = new PatientMenu((Patient) user);
                patientMenu.displayMenu();
                break;

            case "Doctor":
                DoctorMenu doctorMenu = new DoctorMenu((Doctor) user);
                doctorMenu.displayMenu();
                break;
            case "Pharmacist":
                PharmacistMenu pharmacistMenu = new PharmacistMenu((Pharmacist) user);
                pharmacistMenu.displayMenu();
                break;
            case "Administrator":
                AdministratorMenu administratorMenu = new AdministratorMenu((Administrator) user);
                administratorMenu.displayMenu();
                break;
            default:
                System.out.println("Invalid user role. No menu available.");
                break;
        }
    }
}
