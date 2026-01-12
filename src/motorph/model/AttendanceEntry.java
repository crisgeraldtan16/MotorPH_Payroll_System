package motorph.model;

import java.time.LocalDate;
import java.time.LocalTime;

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
