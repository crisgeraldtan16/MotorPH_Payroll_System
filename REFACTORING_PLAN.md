# MotorPH Payroll System — Refactoring Plan

> Maps every business logic / formula from its original procedural (CP2) location to its refactored OOP class and method.

---

| # | CP2 Logic / Formula | CP2 Location (GUI / Event / Procedure) | OOP Class | OOP Method | Notes |
|---|---|---|---|---|---|
| 1 | **User Login – credential check** (compare username + password from CSV) | `LoginPanel` → Login button `ActionListener` (`authenticate()`) | `UserDao` | `findByUsername(String username)` | Credential lookup moved out of the GUI into a DAO; `Session.setCurrentUser()` stores the result. Plain-text password comparison is preserved as-is from CP2. |
| 2 | **Show / hide password toggle** | `LoginPanel` → `showPasswordCheck` `ActionListener` | `LoginPanel` | `authenticate()` (same class) | UI-only behaviour; no logic change needed. Stays in the panel. |
| 3 | **Role-based screen access control** | Inline `if/else` blocks scattered across menu event handlers | `AccessPolicy` *(interface)* | `canOpenScreen(String screen)` | Extracted into a full polymorphic hierarchy: `BaseAccessPolicy`, `AdminAccessPolicy`, `HrAccessPolicy`, `FinanceAccessPolicy`, `ItAccessPolicy`, `RegularEmployeeAccessPolicy`, `ProbationaryEmployeeAccessPolicy`. |
| 4 | **Navigation routing / screen switching** | `SideMenuPanel` → per-button `ActionListener` | `MainFrame` | `showContent(String screen)` | `CardLayout` switching centralised in `MainFrame`; each button simply calls `mainFrame.showContent(key)`. |
| 5 | **Post-login landing page** (decide first screen by role) | `LoginPanel.authenticate()` – direct panel swap | `MainFrame` | `showMainApp()` | Route decision (employee vs. admin) moved to `MainFrame`; avoids bypassing access control. |
| 6 | **Session management** (store / clear logged-in user) | Global static variable in `LoginPanel` | `Session` | `setCurrentUser(User)` · `getCurrentUser()` · `clear()` | Singleton-style utility; keeps session state off the UI class. |
| 7 | **Access-policy factory** (instantiate correct policy by role) | `switch` inside `User.getAccessPolicy()` | `AccessPolicyFactory` | `forUser(User u)` | Factory pattern; decouples role → policy mapping from the `User` model. |
| 8 | **Employee list load / display** | Procedural CSV read + loop inside a GUI procedure | `EmployeeService` → `EmployeeDao` → `CSVUtil` | `getAllEmployees()` → `findAll()` → `loadEmployees()` | Three-layer retrieval: Service → DAO → Util. GUI (`EmployeePanel`) only calls `EmployeeService`. |
| 9 | **Auto-generate next Employee #** | `if/else` loop directly inside the Add form handler | `CSVUtil` | `generateNextEmployeeNumber(List<Employee>)` | Logic isolated in the utility class; strips non-digits, finds max, returns `max + 1`. |
| 10 | **Save new / edited employee** (write to CSV) | Form `saveBtn` `ActionListener` in the GUI | `EmployeeService` → `EmployeeDao` → `CSVUtil` | `saveEmployees(List<Employee>)` → `saveAll(List<Employee>)` → `saveEmployees(List<Employee>)` | Full-rewrite strategy: all records overwritten on each save. GUI builds the `Employee` object, then hands off to the service. |
| 11 | **Delete employee** (remove from list + rewrite) | Delete button `ActionListener` in `EmployeePanel` | `EmployeeService` → `EmployeeDao` → `CSVUtil` | `saveEmployees(List<Employee>)` | Same save pipeline as add/edit; GUI removes item from the in-memory list before calling save. |
| 12 | **Employee form validation** (required fields, date format, non-negative salary) | `saveBtn` handler in `EmployeePanel` | `EmployeePanel` | `saveEmployee()` · `isValidDate(String)` · `parseMoney(String, String)` | Validation helpers kept in the panel since they are purely UI-input guards. |
| 13 | **Real-time employee search / filter** | `DocumentListener` on `searchField` in `EmployeePanel` | `EmployeePanel` | `applySearch()` (uses `TableRowSorter`) | UI-layer filtering only; no business logic involved. |
| 14 | **Payroll computation – proration factor** `(daysPresent / workingDaysInMonth)` | Inline arithmetic inside a monolithic payroll procedure | `PayrollCalculator` | `computeMonthlyPayroll(Employee, YearMonth, int, int)` | Proration is capped between 0 and 1 to avoid edge cases. |
| 15 | **Count weekdays in month** (Mon–Fri only) | Inline loop inside payroll procedure | `PayrollCalculator` | `countWeekdaysInMonth(YearMonth)` *(private)* | Uses `DayOfWeek` enum; fallback to 22 if result is 0. |
| 16 | **Earned basic salary** `= monthlyBasic × prorationFactor` | Inline inside payroll procedure | `PayrollCalculator` | `computeMonthlyPayroll(…)` | Proportional to actual days present vs. total weekdays in the month. |
| 17 | **Earned allowances** `= (riceSubsidy + phoneAllowance + clothingAllowance) × prorationFactor` | Inline inside payroll procedure | `PayrollCalculator` | `computeMonthlyPayroll(…)` | All three allowance types prorated together as one total. |
| 18 | **Late deduction** `= (lateMinutes / 60) × hourlyRate` | Inline inside payroll procedure | `PayrollCalculator` | `computeMonthlyPayroll(…)` | Capped at earned pay so deduction never exceeds total earnings. |
| 19 | **Gross Pay** `= earnedBasic + earnedAllowances − lateDeduction` | Inline inside payroll procedure | `PayrollCalculator` | `computeMonthlyPayroll(…)` | Result is floored at 0. |
| 20 | **SSS contribution** (bracket table look-up; ₱135 – ₱1,125) | If/else cascade inside payroll procedure | `PayrollCalculator` | `computeSSS(double monthlyCompensation)` | Bracket table directly encodes the PhilSys SSS contribution schedule. |
| 21 | **PhilHealth employee share** `= (salary × 3%) ÷ 2` capped ₱150–₱900 | Inline inside payroll procedure | `PayrollCalculator` | `computePhilHealthEmployeeShare(double)` | Premium floors at ₱300, ceilings at ₱1,800; employee pays 50%. |
| 22 | **Pag-IBIG employee share** `= salary × 1% (≤₱1,500) or 2% (>₱1,500)` | Inline inside payroll procedure | `PayrollCalculator` | `computePagIbigEmployeeShare(double)` | Rate switches at ₱1,500 monthly basic salary. |
| 23 | **Total government deductions** `= SSS + PhilHealth + Pag-IBIG` | Inline sum inside payroll procedure | `PayrollCalculator` | `computeMonthlyPayroll(…)` | Aggregated after each contribution is individually computed. |
| 24 | **Taxable income** `= grossPay − totalGovDeductions` | Inline inside payroll procedure | `PayrollCalculator` | `computeMonthlyPayroll(…)` | Floored at 0. |
| 25 | **Withholding tax** (BIR monthly tax table; 0% – 35%) | If/else cascade inside payroll procedure | `PayrollCalculator` | `computeWithholdingTax(double taxableMonthlyIncome)` | Six brackets: ≤₱20,832 = 0; up to 35% for >₱666,667. |
| 26 | **Net Pay** `= grossPay − totalGovDeductions − withholdingTax` | Inline inside payroll procedure | `PayrollCalculator` | `computeMonthlyPayroll(…)` | Floored at 0; stored in `PayrollRecord.netPay`. |
| 27 | **Attendance summary** (days present + total late minutes for a month) | Loop over CSV rows inside GUI or payroll procedure | `AttendanceUtil` | `summarizeForEmployeeMonth(String empNo, YearMonth ym)` | Returns `AttendanceSummary` value object; decoupled from GUI. |
| 28 | **Late minutes with grace period** `= max(0, timeIn − (08:00 + 10 min))` | Inline ternary inside attendance loop | `AttendanceUtil` | `computeLateMinutesWithGrace(LocalTime timeIn, int graceMinutes)` | Grace period constant (`GRACE_MINUTES = 10`) defined once in `AttendanceUtil`. |
| 29 | **Worked hours** `= (logOut − logIn).toMinutes() / 60` | Inline inside attendance display procedure | `AttendanceUtil` | `computeWorkedHours(LocalTime in, LocalTime out)` | Returns 0 if either time is null or duration is negative. |
| 30 | **Load attendance records** from CSV | CSV read loop duplicated in multiple GUI methods | `AttendanceUtil` | `loadAllRecords()` | Single source of truth for CSV parsing; sorted by empNo → date → logIn. |
| 31 | **Save attendance records** to CSV (add / update / delete) | Inline file-write inside event handlers | `AttendanceUtil` | `addRecord(AttendanceRecord)` · `updateRecord(…)` · `deleteRecord(…)` · `saveAllRecords(List<AttendanceRecord>)` | Full-rewrite-on-save strategy; DAO delegates to these utils. |
| 32 | **Time In** (record today's log-in, prevent duplicate) | Direct CSV write in employee dashboard event handler | `AttendanceService` | `timeIn(Employee emp)` | Guards: checks employee is valid; checks today's record not already present; delegates to `AttendanceDao.add()`. |
| 33 | **Time Out** (record today's log-out, require prior Time In) | Direct CSV write in employee dashboard event handler | `AttendanceService` | `timeOut(Employee emp)` | Guards: must have timed in; must not have already timed out; delegates to `AttendanceDao.update()`. |
| 34 | **Get today's attendance record** | Inline search inside GUI refresh method | `AttendanceService` | `getTodayRecord(String empNo)` | Filters `findRecordsForEmployeeMonth` by `LocalDate.now()`. |
| 35 | **Manual timecard CRUD** (Payroll > Timecard tab) | `ActionListener` blocks in `PayrollPanel` | `AttendanceDao` | `add(AttendanceRecord)` · `update(AttendanceRecord, AttendanceRecord)` · `delete(AttendanceRecord)` | GUI collects date/time strings, parses them, then calls the DAO. |
| 36 | **Timecard totals display** (days, late minutes, worked hours) | Inline accumulator loop in `PayrollPanel` | `AttendanceUtil` | `computeWorkedHours(…)` · `computeLateMinutesWithGrace(…)` | Aggregation loop still in `PayrollPanel.refreshTimecard()`; candidates for extraction to a `TimecardSummary` service method. |
| 37 | **Compute payroll for one employee** (orchestration) | Button `ActionListener` in `PayrollPanel` | `PayrollAppService` → `DefaultPayrollService` → `PayrollCalculator` | `computeForEmployeeMonth(Employee, YearMonth)` | Service checks attendance exists before calling the calculator; returns `null` if no attendance. |
| 38 | **Compute payroll for all employees** (monthly batch) | Button `ActionListener` in `PayrollPanel` | `PayrollAppService` → `DefaultPayrollService` | `computeForAllEmployeesMonth(List<Employee>, YearMonth)` | Iterates all employees; skips those without attendance records. |
| 39 | **Save payroll record** to CSV | Direct `PrintWriter` call in payroll GUI procedure | `PayrollAppService` → `PayrollDao` → `PayrollIOUtil` | `saveRecord(PayrollRecord)` → `append(PayrollRecord)` → `appendPayrollRecord(PayrollRecord)` | Append-only write; one row per computation. |
| 40 | **Load payroll records** for an employee/month | Inline CSV scan inside GUI or payslip procedure | `PayrollDao` → `PayrollIOUtil` | `findForEmployeeMonth(String, YearMonth)` → `loadPayrollRecordsForEmployeeMonth(…)` | Filtered on `empNo` and `YearMonth.toString()`. |
| 41 | **Find latest payroll record** for an employee | Linear scan inline in GUI | `PayrollAppService` → `PayrollDao` → `PayrollIOUtil` | `findLatestForEmployee(String)` | Compares `YearMonth` successively; returns last/most-recent record. |
| 42 | **Format payslip text** (ASCII receipt layout) | String concatenation / `printf` inside GUI procedure | `PayrollIOUtil` | `formatPayslipText(PayrollRecord pr)` | Returns a formatted `String`; `PayrollPanel` just sets it into `payslipArea`. |
| 43 | **Leave request filing** (generate ID, record timestamp, append to CSV) | Inline block inside `LeaveRequestPanel` submit handler | `LeaveService` → `LeaveDao` → `LeaveIOUtil` | `saveAllRequests(List<LeaveRequest>)` → `saveAll(…)` → `overwriteAll(…)` | `LeaveIOUtil.newRequestId()` and `LeaveIOUtil.now()` generate unique ID and timestamp. |
| 44 | **Load leave requests** for an employee | Inline CSV loop inside GUI | `LeaveService` → `LeaveDao` → `LeaveIOUtil` | `getRequestsForEmployee(String empNo)` → `findForEmployee(…)` → `loadForEmployee(…)` | Filters from full CSV by `employeeNumber`. |
| 45 | **Leave approval / denial** (update status, set reviewer & timestamp) | Inline mutation + rewrite inside `LeaveApprovalPanel` button handler | `LeaveService` → `LeaveDao` → `LeaveIOUtil` | `saveAllRequests(List<LeaveRequest>)` → `saveAll(…)` → `overwriteAll(…)` | Full-rewrite-on-approval strategy; reviewer info written from `Session.getCurrentUser()`. |
| 46 | **Dashboard statistics** (total, regular, probationary, avg salary) | Inline accumulator loop inside GUI | `DashboardPanel` | `refreshDashboard()` | Still in the panel; candidate for extraction to a `DashboardService` or `EmployeeService.getSummary()` method in a future OOP pass. |
| 47 | **Recent employees sort** (last 5 by numeric employee #) | Inline `.stream().sorted(…).limit(5)` in GUI | `DashboardPanel` | `refreshRecentEmployees(List<Employee>)` | Candidate for extraction to `EmployeeService`. Helper `parseEmpNo()` strips non-digit chars for reliable numeric sort. |
| 48 | **File existence check** (employees.csv, attendance.csv) | Inline `new File(path).exists()` in GUI | `DashboardPanel` | `refreshSystemStatus()` | UI-level health indicator; no business logic. |
| 49 | **Employee dashboard payroll summary** | Inline `PayrollIOUtil.findLatestForEmployee()` call in GUI | `EmployeeDashboardPanel` | `refresh()` | Reads latest `PayrollRecord` from CSV; displays gross, gov deductions, tax, net. |
| 50 | **Employee dashboard leave summary** (count pending / approved / denied) | Inline loop in GUI refresh | `EmployeeDashboardPanel` | `refresh()` | Candidate for extraction to `LeaveService.summarizeForEmployee(String)`. |
| 51 | **User account management** (list / add user accounts) | Inline CSV read+write inside `UserAccountsPanel` | `UserDao` → `UserIOUtil` | `findAll()` → `loadUsers()` · `appendUser(User)` | `UserIOUtil.usernameExists()` prevents duplicate usernames before append. |
| 52 | **Load users from CSV** | Inline CSV parse inside `LoginPanel` and `UserAccountsPanel` | `CSVUtil` → `UserIOUtil` | `loadUsers()` | Both utilities parse the same `data/users.csv`; `CSVUtil.loadUsers()` is a legacy path; `UserIOUtil.loadUsers()` is the canonical DAO-backed version. |
| 53 | **Round to 2 decimal places** | Inline `Math.round(v * 100) / 100.0` scattered across payroll code | `PayrollCalculator` | `round2(double v)` *(private)* | Applied to every monetary output to ensure consistent precision. |
| 54 | **CSV safe-stringify** (escape commas, newlines before writing) | Inline `String.replace` calls inside file-write procedures | `PayrollIOUtil` · `LeaveIOUtil` · `UserIOUtil` | `csv(String... vals)` *(private, each util)* | Ensures values containing commas do not corrupt CSV row structure. |
| 55 | **Ensure CSV file exists** (create with header if missing) | Inline `File.createNewFile()` blocks before every read | `AttendanceUtil` · `PayrollIOUtil` · `LeaveIOUtil` · `UserIOUtil` | `ensureFileExists()` / `ensureFile()` *(private, each util)* | Each data-file utility is self-initialising; no manual pre-creation required. |
| 56 | **Flexible date/time parsing** (multiple format fallbacks) | `try/catch` around `SimpleDateFormat` inline in GUI or procedure | `AttendanceUtil` | `parseDateFlexible(String)` · `parseTimeFlexible(String)` *(private)* | Supports `MM/dd/yyyy` and `ISO_LOCAL_DATE` for dates; `HH:mm` and `H:mm` for times. |

---

## Layer Architecture (OOP Mapping Summary)

```
UI Layer (javax.swing panels)
    └─ calls ──► Service Layer (AttendanceService, EmployeeService, LeaveService, PayrollAppService)
                    └─ calls ──► DAO Layer (AttendanceDao, EmployeeDao, LeaveDao, PayrollDao, UserDao)
                                    └─ calls ──► Util / IO Layer (AttendanceUtil, CSVUtil, LeaveIOUtil,
                                                                   PayrollIOUtil, UserIOUtil)
                                                      └─ reads/writes ──► data/*.csv

Business Logic (pure computation, no I/O):
    PayrollCalculator       – all payroll formulas
    AccessPolicy hierarchy  – all role-permission rules
    Session                 – runtime login state
```

---

## Key OOP Principles Applied

| Principle | Where Applied |
|---|---|
| **Encapsulation** | `Employee`, `PayrollRecord`, `LeaveRequest`, `AttendanceRecord`, `User` — all fields private with getters/setters |
| **Inheritance** | `BaseAccessPolicy` → `AdminAccessPolicy`, `HrAccessPolicy`, `FinanceAccessPolicy`, `ItAccessPolicy`, `RegularEmployeeAccessPolicy`, `ProbationaryEmployeeAccessPolicy`; `BasePanel` → all UI panels |
| **Polymorphism** | `AccessPolicy` interface; `User.getAccessPolicy()` returns the correct subtype at runtime; `PayrollService` interface implemented by `DefaultPayrollService` |
| **Abstraction** | `AccessPolicy` (interface), `PayrollService` (interface), `BaseAccessPolicy` (abstract class), `BasePanel` (abstract class) |
| **Separation of Concerns** | UI panels contain only layout + event wiring; Services contain orchestration logic; DAOs contain persistence; Utils contain pure computation or CSV I/O |
| **Single Responsibility** | `PayrollCalculator` computes only; `PayrollIOUtil` reads/writes only; `Session` manages login state only |
| **Factory Pattern** | `AccessPolicyFactory.forUser(User)` |
| **DAO Pattern** | `AttendanceDao`, `EmployeeDao`, `LeaveDao`, `PayrollDao`, `UserDao` |
| **Service Layer Pattern** | `AttendanceService`, `EmployeeService`, `LeaveService`, `PayrollAppService` |
