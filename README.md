# MotorPH_Payroll_System

Overview

The MotorPH Employee Payroll System is a Java-based desktop application developed as an academic project to demonstrate the application of Object-Oriented Programming (OOP) concepts, file-based data persistence, and modular system design.

The system manages employee records, attendance-based payroll computation, and government-mandated deductions in compliance with Philippine regulations. All data is stored and processed using CSV files, with no database dependency.

Technologies Used
- Java (JDK 11)
- Java Swing
- CSV file storage
- IntelliJ IDEA Ultimate

System Structure
- model – Data models (Employee, PayrollRecord, User, LeaveRequest, AttendanceEntry, AttendanceRecord)
- ui – Application panels and navigation logic (DashboardPanel, EmployeeDashboardPanel, PayrollPanel, etc.)
- util – Payroll computation, attendance processing, leave handling, service abstraction, and CSV file utilities

Features Implemented

Login Authentication
- Validates user credentials stored in a CSV file.
- Restricts system access to authenticated users.
- Uses session management to track logged-in users.

Role-Based Access Control

Implements different user roles:
- Admin
- HR
- Employee (Regular and Probitionary)
- IT
- Finance

Admin and HR can manage employees, compute payroll, and approve leave requests.
Employees can only access their dashboard, file leave requests, and view their own payslip.

The side menu dynamically changes based on the logged-in user’s role.

Dashboard
Central navigation point for Admin and HR users.
Employee users have a separate Employee Dashboard showing:
- Profile information
- Leave summary (Pending, Approved, Denied)
- Latest payroll summary
- 	Quick access to payslip and leave request

Employee Management (CRUD)
- Create, view, update, and delete employee records.
- Automatically generates employee numbers.
- Stores employee personal, employment, government, and compensation data.
- Supports searching by employee number or name.
- Uses CSV file storage only.

Attendance-Based Payroll Computation

Allows viewing of attendance per employee and per month.
Supports:
- Computes monthly payroll using attendance records.
- Applies a 10-minute grace period for late deductions.
- Supports individual and batch (monthly) payroll computation.

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

Attendance-Based Payroll Computation
- Computes monthly payroll using attendance records.
- If there is no attendance for a selected month, payroll will not compute.
- Supports individual payroll computation.
- Supports batch monthly payroll computation for all employees.

Government Deductions and Taxation
- Applies SSS, PhilHealth, and Pag-IBIG deductions.
- Computes withholding tax after deductions.
- Follows Philippine payroll computation rules.

Payslip and Payroll Records
- Generates a detailed payslip per employee.
- Allows saving payroll records to a CSV file.
- Prevents saving payroll data before computation.

Monthly Payroll Summary
- Displays total net pay, taxes, and deductions for all employees in a selected month.

Validation and Error Handling
- Ensures required fields, correct formats, and valid numeric inputs.
- Displays clear error messages for invalid actions.

Object-Oriented Programming Concepts Applied
- Encapsulation – Data fields are private and accessed via getters/setters.
- Abstraction – PayrollService interface separates computation logic from UI. Utility classes hide file handling logic.
- Inheritance – Structured class design separates models, utilities, and services for maintainability.
- Polymorphism – PayrollService interface allows different implementations of payroll logic.
- Separation of Concerns – UI, business logic, and data storage are clearly separated into packages.

Reusability – Payroll calculation, attendance processing, and CSV utilities are reusable across the system.
Limitations
- CSV-based storage does not support concurrent access.
- No encryption for stored credentials (academic use only).
- Designed for desktop use only (not web-based).

Conclusion
- The MotorPH Employee Payroll System successfully demonstrates the implementation of a complete payroll workflow using Java and Object-Oriented Programming principles. It integrates employee management, attendance tracking, payroll computation, and statutory deductions into a cohesive, file-based system suitable for academic evaluation.

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



