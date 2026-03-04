package motorph.dao;

import motorph.model.PayrollRecord;
import motorph.util.PayrollIOUtil;

import java.time.YearMonth;
import java.util.List;

public class PayrollDao {

    public void append(PayrollRecord record) {
        PayrollIOUtil.appendPayrollRecord(record);
    }

    public PayrollRecord findLatestForEmployee(String empNo) {
        return PayrollIOUtil.findLatestForEmployee(empNo);
    }

    public List<PayrollRecord> findForEmployeeMonth(String empNo, YearMonth ym) {
        return PayrollIOUtil.loadPayrollRecordsForEmployeeMonth(empNo, ym);
    }
}