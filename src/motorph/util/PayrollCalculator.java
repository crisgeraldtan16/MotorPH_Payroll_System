package motorph.util;

import motorph.model.Employee;
import motorph.model.PayrollRecord;

import java.time.YearMonth;

public class PayrollCalculator {

    public static PayrollRecord computeMonthlyPayroll(Employee emp, YearMonth month, int daysPresent, int lateMinutes) {
        PayrollRecord pr = new PayrollRecord();
        pr.setEmployeeNumber(emp.getEmployeeNumber());
        pr.setEmployeeName(emp.getFullName());
        pr.setMonth(month);

        double monthlyBasic = emp.getBasicSalary(); // assume Basic Salary stored is MONTHLY
        double monthlyAllowances = emp.getRiceSubsidy() + emp.getPhoneAllowance() + emp.getClothingAllowance();

        pr.setMonthlyBasicSalary(monthlyBasic);
        pr.setTotalAllowancesMonthly(monthlyAllowances);
        pr.setDaysPresent(daysPresent);
        pr.setLateMinutes(lateMinutes);

        // Late deduction based on hourly rate
        double hourlyRate = emp.getHourlyRate();
        double lateDeduction = (lateMinutes / 60.0) * hourlyRate;
        pr.setLateDeduction(round2(lateDeduction));

        // Gross pay (monthly): basic + allowances - late
        double grossPay = (monthlyBasic + monthlyAllowances) - lateDeduction;
        if (grossPay < 0) grossPay = 0;
        pr.setGrossPay(round2(grossPay));

        // Government deductions (use MONTHLY BASIC salary as base, common in schedules)
        double sss = computeSSS(monthlyBasic);
        double philHealthEmployeeShare = computePhilHealthEmployeeShare(monthlyBasic);
        double pagIbigEmployee = computePagIbigEmployeeShare(monthlyBasic);

        pr.setSss(round2(sss));
        pr.setPhilHealth(round2(philHealthEmployeeShare));
        pr.setPagIbig(round2(pagIbigEmployee));

        double totalBeforeTax = sss + philHealthEmployeeShare + pagIbigEmployee;
        pr.setTotalDeductionsBeforeTax(round2(totalBeforeTax));

        // Taxable income = Salary - gov deductions (your note: tax computed after deductions)
        double taxable = monthlyBasic - totalBeforeTax;
        if (taxable < 0) taxable = 0;
        pr.setTaxableIncome(round2(taxable));

        double withholdingTax = computeWithholdingTax(taxable);
        pr.setWithholdingTax(round2(withholdingTax));

        // Net pay = Gross Pay - gov deductions - withholding tax
        double net = grossPay - totalBeforeTax - withholdingTax;
        pr.setNetPay(round2(Math.max(net, 0)));

        return pr;
    }

    // ------------------ SSS (your table) ------------------
    public static double computeSSS(double monthlyCompensation) {
        double c = monthlyCompensation;

        if (c < 3250) return 135.00;

        // Each range is 500 wide after 3250 up to 24750+, with increments of 22.50
        // We'll implement it exactly as discrete brackets to match your schedule.
        if (c >= 3250 && c < 3750) return 157.50;
        if (c >= 3750 && c < 4250) return 180.00;
        if (c >= 4250 && c < 4750) return 202.50;
        if (c >= 4750 && c < 5250) return 225.00;
        if (c >= 5250 && c < 5750) return 247.50;
        if (c >= 5750 && c < 6250) return 270.00;
        if (c >= 6250 && c < 6750) return 292.50;
        if (c >= 6750 && c < 7250) return 315.00;
        if (c >= 7250 && c < 7750) return 337.50;
        if (c >= 7750 && c < 8250) return 360.00;
        if (c >= 8250 && c < 8750) return 382.50;
        if (c >= 8750 && c < 9250) return 405.00;
        if (c >= 9250 && c < 9750) return 427.50;
        if (c >= 9750 && c < 10250) return 450.00;
        if (c >= 10250 && c < 10750) return 472.50;
        if (c >= 10750 && c < 11250) return 495.00;
        if (c >= 11250 && c < 11750) return 517.50;
        if (c >= 11750 && c < 12250) return 540.00;
        if (c >= 12250 && c < 12750) return 562.50;
        if (c >= 12750 && c < 13250) return 585.00;
        if (c >= 13250 && c < 13750) return 607.50;
        if (c >= 13750 && c < 14250) return 630.00;
        if (c >= 14250 && c < 14750) return 652.50;
        if (c >= 14750 && c < 15250) return 675.00;
        if (c >= 15250 && c < 15750) return 697.50;
        if (c >= 15750 && c < 16250) return 720.00;
        if (c >= 16250 && c < 16750) return 742.50;
        if (c >= 16750 && c < 17250) return 765.00;
        if (c >= 17250 && c < 17750) return 787.50;
        if (c >= 17750 && c < 18250) return 810.00;
        if (c >= 18250 && c < 18750) return 832.50;
        if (c >= 18750 && c < 19250) return 855.00;
        if (c >= 19250 && c < 19750) return 877.50;
        if (c >= 19750 && c < 20250) return 900.00;
        if (c >= 20250 && c < 20750) return 922.50;
        if (c >= 20750 && c < 21250) return 945.00;
        if (c >= 21250 && c < 21750) return 967.50;
        if (c >= 21750 && c < 22250) return 990.00;
        if (c >= 22250 && c < 22750) return 1012.50;
        if (c >= 22750 && c < 23250) return 1035.00;
        if (c >= 23250 && c < 23750) return 1057.50;
        if (c >= 23750 && c < 24250) return 1080.00;
        if (c >= 24250 && c < 24750) return 1102.50;

        // 24,750 - Over
        return 1125.00;
    }

    // ------------------ PhilHealth (your notes) ------------------
    // Premium rate: 3%, min 300, max 1800 monthly premium
    // Employee share is 50%
    public static double computePhilHealthEmployeeShare(double monthlyBasicSalary) {
        double premium = monthlyBasicSalary * 0.03;

        if (monthlyBasicSalary <= 10000) premium = 300;
        if (monthlyBasicSalary >= 60000) premium = 1800;

        // Also clamp 300..1800 for mid-range just in case
        if (premium < 300) premium = 300;
        if (premium > 1800) premium = 1800;

        return premium * 0.5;
    }

    // ------------------ Pag-IBIG (your table) ------------------
    // At least 1000 to 1500: 1% employee
    // Over 1500: 2% employee
    public static double computePagIbigEmployeeShare(double monthlyBasicSalary) {
        if (monthlyBasicSalary <= 0) return 0;

        double rate = (monthlyBasicSalary <= 1500) ? 0.01 : 0.02;
        return monthlyBasicSalary * rate;
    }

    // ------------------ Withholding Tax (your table) ------------------
    // Input is TAXABLE INCOME (salary - gov deductions)
    public static double computeWithholdingTax(double taxableMonthlyIncome) {
        double x = taxableMonthlyIncome;

        if (x <= 20832) return 0;

        if (x >= 20833 && x < 33333) {
            return (x - 20833) * 0.20;
        }

        if (x >= 33333 && x < 66667) {
            return 2500 + (x - 33333) * 0.25;
        }

        if (x >= 66667 && x < 166667) {
            return 10833 + (x - 66667) * 0.30;
        }

        if (x >= 166667 && x < 666667) {
            return 40833.33 + (x - 166667) * 0.32;
        }

        // 666,667 and above
        return 200833.33 + (x - 666667) * 0.35;
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
