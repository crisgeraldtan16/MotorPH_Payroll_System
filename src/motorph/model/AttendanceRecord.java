package motorph.model;

import java.time.LocalDate;
import java.time.LocalTime;


/*
 * This class represents an employee's attendance record.
 * It stores basic employee details along with the date,
 * login time, and logout time.
 * Setter and getter methods are provided to allow
 * updating and accessing the attendance information.
 */

public class AttendanceRecord {
    private String employeeNumber;
    private String lastName;
    private String firstName;
    private LocalDate date;
    private LocalTime logIn;
    private LocalTime logOut;

    public AttendanceRecord() {}

    public AttendanceRecord(String employeeNumber, String lastName, String firstName,
                            LocalDate date, LocalTime logIn, LocalTime logOut) {
        this.employeeNumber = employeeNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.date = date;
        this.logIn = logIn;
        this.logOut = logOut;
    }

    public String getEmployeeNumber() { return employeeNumber; }
    public void setEmployeeNumber(String employeeNumber) { this.employeeNumber = employeeNumber; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getLogIn() { return logIn; }
    public void setLogIn(LocalTime logIn) { this.logIn = logIn; }

    public LocalTime getLogOut() { return logOut; }
    public void setLogOut(LocalTime logOut) { this.logOut = logOut; }
}
