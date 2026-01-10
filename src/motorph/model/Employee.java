package motorph.model;

public class Employee {
    private String employeeNumber; // auto-generated
    private String lastName;
    private String firstName;
    private String birthday; // MM/dd/yyyy
    private String address;
    private String phoneNumber;

    private String sssNumber;
    private String philHealthNumber;
    private String tinNumber;
    private String pagIbigNumber;

    private String status; // Regular or Probationary
    private String position;
    private String immediateSupervisor;

    private double basicSalary;
    private double riceSubsidy;
    private double phoneAllowance;
    private double clothingAllowance;

    private double grossSemiMonthlyRate;
    private double hourlyRate;

    public Employee() {}

    // --- getters/setters ---
    public String getEmployeeNumber() { return employeeNumber; }
    public void setEmployeeNumber(String employeeNumber) { this.employeeNumber = employeeNumber; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getSssNumber() { return sssNumber; }
    public void setSssNumber(String sssNumber) { this.sssNumber = sssNumber; }

    public String getPhilHealthNumber() { return philHealthNumber; }
    public void setPhilHealthNumber(String philHealthNumber) { this.philHealthNumber = philHealthNumber; }

    public String getTinNumber() { return tinNumber; }
    public void setTinNumber(String tinNumber) { this.tinNumber = tinNumber; }

    public String getPagIbigNumber() { return pagIbigNumber; }
    public void setPagIbigNumber(String pagIbigNumber) { this.pagIbigNumber = pagIbigNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getImmediateSupervisor() { return immediateSupervisor; }
    public void setImmediateSupervisor(String immediateSupervisor) { this.immediateSupervisor = immediateSupervisor; }

    public double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }

    public double getRiceSubsidy() { return riceSubsidy; }
    public void setRiceSubsidy(double riceSubsidy) { this.riceSubsidy = riceSubsidy; }

    public double getPhoneAllowance() { return phoneAllowance; }
    public void setPhoneAllowance(double phoneAllowance) { this.phoneAllowance = phoneAllowance; }

    public double getClothingAllowance() { return clothingAllowance; }
    public void setClothingAllowance(double clothingAllowance) { this.clothingAllowance = clothingAllowance; }

    public double getGrossSemiMonthlyRate() { return grossSemiMonthlyRate; }
    public void setGrossSemiMonthlyRate(double grossSemiMonthlyRate) { this.grossSemiMonthlyRate = grossSemiMonthlyRate; }

    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }

    // Convenience for table display
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
