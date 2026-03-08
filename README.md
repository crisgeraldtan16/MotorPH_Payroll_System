# MotorPH_Payroll_System

Overview

The MotorPH Employee Payroll System is a Java-based desktop application developed as an academic project to demonstrate the application of Object-Oriented Programming (OOP) concepts, file-based data persistence, and modular system design.

The system manages employee records, attendance tracking, leave requests, payroll computation, and government-mandated deductions in compliance with Philippine regulations. All data is stored and processed using CSV files, with no database dependency.

Recent improvements include attendance time-in/time-out recording, automatic dashboard refreshing, and IT-managed user account credential creation.

Technologies Used
- Java (JDK 11)
- Java Swing
- CSV file storage
- IntelliJ IDEA Ultimate

System Structure
- dao – Data Access Objects responsible for handling file operations and reading/writing CSV data (EmployeeDao, PayrollDao, AttendanceDao, etc.)
- model – Data models (Employee, PayrollRecord, User, LeaveRequest, AttendanceEntry, AttendanceRecord)
- service – Business logic layer that processes system operations and calls DAO methods (PayrollService, EmployeeService, AttendanceService, etc.)
- ui – Application panels and navigation logic (DashboardPanel, EmployeeDashboardPanel, PayrollPanel, LeaveApprovalPanel, SideMenuPanel, etc.)
- util – Utility classes used for payroll computation, attendance processing, session management, access control policies, and helper functions

Features Implemented

Login Authentication
- Validates user credentials stored in a CSV file.
- Restricts system access to authenticated users.
- Uses session management to track logged-in users.
- Displays the logged-in user’s name and role in the sidebar.

Role-Based Access Control

Implements different user roles:
- Admin
- HR
- Employee (Regular and Probitionary)
- IT
- Finance

Admin and HR can manage employees, compute payroll, and approve leave requests.
Employees can only access their dashboard, file leave requests, and view their own payslip.

IT users can manage employee login credentials.

The side menu dynamically changes based on the logged-in user’s role.

Dashboard
Central navigation point for Admin and HR users.
Employee users have a separate Employee Dashboard showing:
- Profile information
- Leave summary (Pending, Approved, Denied)
- Latest payroll summary
- 	Quick access to:
  - 		Payslip
  - 		Leave Request
  - 		Attendance actions (Time In / Time Out)
Dashboards automatically refresh when opened so users always see updated information.

Employee Management (CRUD)
- Create, view, update, and delete employee records.
- Automatically generates employee numbers.
- Stores employee personal, employment, government, and compensation data.
- Supports searching by employee number or name.
- Uses CSV file storage only.

Attendance Tracking

Employees can record their daily attendance through the system.
Features include:
- Time In
- Time Out
- Automatic saving of attendance records into the attendance CSV file
- Attendance records linked to employee numbers
Attendance records are used for payroll computation.


Attendance-Based Payroll Computation

Allows viewing of attendance per employee and per month.
Supports:
- Computes monthly payroll using attendance records.
- Applies a 10-minute grace period for late deductions.
- Supports individual and batch (monthly) payroll computation.
If there is no attendance for a selected month, payroll will not compute.

Leave Functionality

Employees can:
- File leave requests
- Provide detailed reason (full text supported)
- View leave status

Admin and HR can:
- View all leave requests
- Approve or deny leave
- Change decision if needed
- View full leave reason when selected
Leave records are stored in a CSV file.

Leave approval lists automatically refresh when opened.

Attendance-Based Payroll Computation
- Computes monthly payroll using attendance records.
- If there is no attendance for a selected month, payroll will not compute.
- Supports individual payroll computation.
- Supports batch monthly payroll computation for all employees.

IT User Account Management
IT users are responsible for creating login credentials for employees.
Features include:
- Creating usernames and passwords
- Assigning roles (Regular, Probationary, HR, Finance, IT, Admin)
- Linking credentials to employee numbers
- Saving login credentials to the users.csv file
This separates employee data from authentication data.


Government Deductions and Taxation
- Applies SSS, PhilHealth, and Pag-IBIG deductions.
- Computes withholding tax after deductions.
- Follows Philippine payroll computation rules.

Payslip and Payroll Records
- Generates a detailed payslip per employee.
- Allows saving payroll records to a CSV file.
- Prevents saving payroll data before computation.
Employees can view their latest payslip from their dashboard.

Monthly Payroll Summary
- Displays total net pay, taxes, and deductions for all employees in a selected month.

Validation and Error Handling
- Ensures required fields, correct formats, and valid numeric inputs.
- Displays clear error messages for invalid actions.
- Prevents duplicate usernames during account creation.

Object-Oriented Programming Concepts Applied
- Encapsulation – Data fields are private and accessed via getters/setters.
- Abstraction – Service classes and utility classes hide complex business logic and file operations from the UI.
- Inheritance – AccessPolicy classes extend a base access policy to support different roles.
- Polymorphism – Different role-based access policies implement role-specific behavior.
- Separation of Concerns – UI, business logic, and data storage are clearly separated into packages.

Reusability – Payroll calculation, attendance processing, and CSV utilities are reusable across the system.
Limitations
- CSV-based storage does not support concurrent access.
- No encryption for stored credentials (academic use only).
- Designed for desktop use only (not web-based).

Limitations
- CSV-based storage does not support concurrent access.
- No encryption for stored credentials (academic use only).
- Designed for desktop use only (not web-based).


Conclusion
- The MotorPH Employee Payroll System successfully demonstrates the implementation of a complete payroll workflow using Java and Object-Oriented Programming principles. It integrates employee management, attendance tracking, leave processing, payroll computation, statutory deductions, and role-based access control into a cohesive, file-based system suitable for academic evaluation.

## References / Data Sources

The following materials were used as reference data sources for this project:
- Employee master list and compensation spreadsheet (provided for academic use)
- Attendance records spreadsheet for payroll computation (Year 2024)
- SSS Contribution Table (Philippine Social Security System)
- PhilHealth Premium Contribution Table
- Pag-IBIG Contribution Table
- Bureau of Internal Revenue (BIR) Withholding Tax Table

Reference links:
- SSS Contribution Table: https://docs.google.com/spreadsheets/d/17HDLoJoYBOuXfbOMd9X-QPEohSqnB9U8RI_b3tz-oY8/edit?usp=sharing
- PhilHealth Premium Contribution: https://docs.google.com/spreadsheets/d/1f5IumqRUQWjtMCpg93bBZSanDlDtQkhYpwi8Pq4J3pc/edit?usp=sharing
- Pag-IBIG Contribution Table: https://docs.google.com/spreadsheets/d/1T46Ob-IsEgCod2dlHhWkt9v-m4FJxvWrEmJxRH1QW7A/edit?usp=sharing
- BIR Withholding Tax Table: https://docs.google.com/spreadsheets/d/1gUyIQ7HiO-LAoYkbns65U4cGygT-AVvEb8a0ptK1fpI/edit?usp=sharing



