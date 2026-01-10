package motorph.model;

import java.time.YearMonth;

public class PayrollRecord {
    private String employeeNumber;
    private String employeeName;
    private YearMonth month;

    private double monthlyBasicSalary;
    private double totalAllowancesMonthly;

    private int daysPresent;
    private int lateMinutes;
    private double lateDeduction;

    private double grossPay; // after late deduction (and + allowances)
    private double sss;
    private double philHealth;
    private double pagIbig;
    private double totalDeductionsBeforeTax;

    private double taxableIncome;
    private double withholdingTax;

    private double netPay;

    public String getEmployeeNumber() { return employeeNumber; }
    public void setEmployeeNumber(String employeeNumber) { this.employeeNumber = employeeNumber; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public YearMonth getMonth() { return month; }
    public void setMonth(YearMonth month) { this.month = month; }

    public double getMonthlyBasicSalary() { return monthlyBasicSalary; }
    public void setMonthlyBasicSalary(double monthlyBasicSalary) { this.monthlyBasicSalary = monthlyBasicSalary; }

    public double getTotalAllowancesMonthly() { return totalAllowancesMonthly; }
    public void setTotalAllowancesMonthly(double totalAllowancesMonthly) { this.totalAllowancesMonthly = totalAllowancesMonthly; }

    public int getDaysPresent() { return daysPresent; }
    public void setDaysPresent(int daysPresent) { this.daysPresent = daysPresent; }

    public int getLateMinutes() { return lateMinutes; }
    public void setLateMinutes(int lateMinutes) { this.lateMinutes = lateMinutes; }

    public double getLateDeduction() { return lateDeduction; }
    public void setLateDeduction(double lateDeduction) { this.lateDeduction = lateDeduction; }

    public double getGrossPay() { return grossPay; }
    public void setGrossPay(double grossPay) { this.grossPay = grossPay; }

    public double getSss() { return sss; }
    public void setSss(double sss) { this.sss = sss; }

    public double getPhilHealth() { return philHealth; }
    public void setPhilHealth(double philHealth) { this.philHealth = philHealth; }

    public double getPagIbig() { return pagIbig; }
    public void setPagIbig(double pagIbig) { this.pagIbig = pagIbig; }

    public double getTotalDeductionsBeforeTax() { return totalDeductionsBeforeTax; }
    public void setTotalDeductionsBeforeTax(double totalDeductionsBeforeTax) { this.totalDeductionsBeforeTax = totalDeductionsBeforeTax; }

    public double getTaxableIncome() { return taxableIncome; }
    public void setTaxableIncome(double taxableIncome) { this.taxableIncome = taxableIncome; }

    public double getWithholdingTax() { return withholdingTax; }
    public void setWithholdingTax(double withholdingTax) { this.withholdingTax = withholdingTax; }

    public double getNetPay() { return netPay; }
    public void setNetPay(double netPay) { this.netPay = netPay; }
}
