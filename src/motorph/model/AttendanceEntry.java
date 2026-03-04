package motorph.model;

import java.time.LocalDate;
import java.time.LocalTime;

/*
 * This class represents a single attendance record of an employee.
 * It stores the employee number, date, time-in, and time-out.
 * The fields are final to make the record immutable after it is created.
 * Getter methods are used to access the stored attendance details.
 */
public class AttendanceEntry {
    private final String employeeNumber;
    private final LocalDate date;
    private final LocalTime timeIn;
    private final LocalTime timeOut;

    public AttendanceEntry(String employeeNumber, LocalDate date, LocalTime timeIn, LocalTime timeOut) {
        this.employeeNumber = employeeNumber;
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
    }

    public String getEmployeeNumber() { return employeeNumber; }
    public LocalDate getDate() { return date; }
    public LocalTime getTimeIn() { return timeIn; }
    public LocalTime getTimeOut() { return timeOut; }
}
