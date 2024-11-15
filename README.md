# **SC2002 SCED Group 4 <2024/2025 Semester 1>**  

## **Overview**  
The Hospital Management System (HMS) is a command-line application developed for the SC2002 Object-Oriented Design & Programming course. 
The project applies OOP principles to model and manage key hospital operations, including patient records, appointments, staff, and billing.  

---

## **Table of Contents**  
1. [Introduction](#introduction)  
2. [Features](#features)  
    - [General Features](#general-features)  
    - [Role-Specific Features](#role-specific-features)
    - [Additional Features](#additional-features)
3. [Design](#design)  
    - [Design Approaches](#design-approaches)  
    - [Design Patterns](#design-patterns)  
4. [Testing](#testing)
5. [Reflection](#reflection) 
6. [Usage](#usage)  
    - [Prerequisites](#prerequisites)  
    - [Installation](#installation)
    - [Running the Program](#running-the-program)
7. [Contributors](#contributors)  


---

## **1. Introduction**  
The HMS streamlines hospital operations through a role-based architecture, ensuring secure and efficient data management. The system is built with:
- **Boundary-Control-Entity (BCE)** architecture to enforce high cohesion and loose coupling.
- Applied OOP principles like inheritance, polymorphism, encapsulation, and abstraction.
- Role-based access control for secure functionality segregation.  

---

## **2. Features**  

### **General Features**  
- **User Authentication**: Login with role-specific access and secure password management.  
- **Data Validation**: Stringent validation for input fields and system logic.  
- **Navigation**: User-friendly menu-driven CLI for seamless interaction.  

### **Role-Specific Features**  
#### **Patients**  
- View/update personal information (non-medical).  
- Schedule/reschedule/cancel appointments.  
- View medical records and appointment history.  

#### **Doctors**  
- View/update patient medical records.  
- Manage appointment schedules and availability.  
- Record appointment outcomes.  

#### **Pharmacists**  
- Process prescriptions and manage inventory.  
- Submit stock replenishment requests.  

#### **Administrators**  
- Manage hospital staff and inventory.  
- Oversee appointment details and approve replenishments.  

### **Additional Features**  
- Registration: Users can register themselves as patients.
- Forgot Password Request: Users can request password resets, pending administrator approval.
- Password Hashing: Passwords are hashed with SHA3-256 for enhanced security.
- Color-Coded User Interfaces: Each user role has a unique interface color scheme for better usability.
- Billing System: Patients can view and pay their bills directly via the system.
- Stringent Validation: Input fields are validated to ensure data integrity, including restricting invalid characters or logic (e.g., preventing stock dispensing if inventory is insufficient).
- Doctor Ratings: Patients can rate doctors post-appointment, and doctors can view these ratings to improve their services.

---

## **3. Design**  

### **Design Approaches**  
- **Boundary-Control-Entity (BCE)**: Clear separation of user interaction, logic, and data storage.  
- **Object-Oriented Principles**: Abstraction, encapsulation, inheritance, and polymorphism to ensure flexibility and maintainability.  

### **Design Patterns**  
- **Factory Pattern**: Dynamic creation of user roles during login.  
- **Singleton Pattern**: Ensures centralized control through a single-entry point.  
- **Observer Pattern**: Enables event-driven notifications for loosely coupled components.  

---

## **4. Testing**  
The system was tested across multiple scenarios to ensure reliability:  
- **Login System**: Tested with valid/invalid credentials and password reset functionality.  
- **Role Actions**: Validated functionalities for all user roles, including scheduling, inventory management, and staff management.  

---

## **5. Reflection**  
The HMS project provided invaluable hands-on experience in applying Object-Oriented Design principles. Concepts such as the Boundary-Control-Entity architecture, design patterns, and SOLID principles were pivotal in shaping the system’s design.

Key Takeaways
- Technical Growth: Translating abstract OOP concepts into functional elements helped us internalize design patterns like Factory and Singleton.
- Collaboration: Managing tasks as a team while navigating other academic commitments taught us project management and communication skills.
- Resilience: Despite challenges like tight deadlines, an unresponsive teammate, and extensive debugging, we learned to adapt and deliver a functional product.
  
This journey emphasized the importance of iterative design, fostering a mindset ready for tackling future projects.

---

## **6. Usage**  

### **Prerequisites**  
- **Java**: Version 7 or higher.  
- **IDE**: Any Java-supported IDE (Eclipse, IntelliJ IDEA, etc.).  

### **Installation** 
- Clone the repository:  
   ```bash
   git clone https://github.com/Pytode2000/SC2002_SCED_Group_4.git

### **Running the Program**
To execute the HMS, you have two options:
- Option 1: Run Java Program Directly
Navigate to the /src/ directory in your terminal and execute the following: `java HospitalManagementSystem`
- Option 2: Build and Run with PowerShell Script
Execute the provided PowerShell script to build and run the project: `.\build.ps1`
---

## **7. Contributors**  
- Liew Wei Jie
- Lim Zu Liang
- Jacob Ong Jia Chun
- Tan Yi Xiang, Dylan
