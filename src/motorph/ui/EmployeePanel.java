package motorph.ui;

import motorph.model.Employee;
import motorph.util.CSVUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class EmployeePanel extends JPanel {

    private final MainFrame mainFrame;

    private final CardLayout viewLayout = new CardLayout();
    private final JPanel viewPanel = new JPanel(viewLayout);

    // List view components
    private DefaultTableModel tableModel;
    private JTable employeeTable;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;

    private List<Employee> employees;

    // Form view components
    private JTextField empNoField, lastNameField, firstNameField, birthdayField, addressField, phoneField;
    private JTextField sssField, philhealthField, tinField, pagibigField;
    private JComboBox<String> statusCombo;
    private JTextField positionField, supervisorField;
    private JTextField basicSalaryField, riceField, phoneAllowField, clothingAllowField, grossSemiField, hourlyField;

    private JButton saveBtn, cancelBtn;
    private JLabel formTitle;

    private enum Mode { ADD, EDIT, VIEW }
    private Mode mode = Mode.VIEW;
    private Employee selectedEmployee = null;

    public EmployeePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        employees = CSVUtil.loadEmployees();

        viewPanel.add(buildListView(), "LIST");
        viewPanel.add(buildFormView(), "FORM");

        add(viewPanel, BorderLayout.CENTER);
        showListView();
    }

    // ------------------- LIST VIEW -------------------
    private JPanel buildListView() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // ======== TOP AREA (2 ROWS) ========
        JPanel topArea = new JPanel();
        topArea.setLayout(new BoxLayout(topArea, BoxLayout.Y_AXIS));

        // Row 1: Title (left) + Back button (right)
        JPanel row1 = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Employee Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton backBtn = new JButton("Back to Main Menu");
        backBtn.addActionListener(e -> mainFrame.showContent("DASHBOARD"));

        row1.add(titleLabel, BorderLayout.WEST);
        row1.add(backBtn, BorderLayout.EAST);

        // Row 2: Action buttons (left) + Search (right)
        JPanel row2 = new JPanel(new BorderLayout(10, 10));

        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton addBtn = new JButton("Add New Employee");
        JButton viewBtn = new JButton("View Details");
        JButton editBtn = new JButton("Edit Employee");
        JButton deleteBtn = new JButton("Delete Employee");

        actionButtons.add(addBtn);
        actionButtons.add(viewBtn);
        actionButtons.add(editBtn);
        actionButtons.add(deleteBtn);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchPanel.add(new JLabel("Search: "));
        searchField = new JTextField(22);
        searchPanel.add(searchField);

        row2.add(actionButtons, BorderLayout.WEST);
        row2.add(searchPanel, BorderLayout.EAST);

        topArea.add(row1);
        topArea.add(Box.createVerticalStrut(8));
        topArea.add(row2);

        panel.add(topArea, BorderLayout.NORTH);

        // ======== TABLE ========
        tableModel = new DefaultTableModel(
                new Object[]{"Employee #", "Name", "Department/Position", "Status"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sorter = new TableRowSorter<>(tableModel);
        employeeTable.setRowSorter(sorter);

        refreshTable();
        panel.add(new JScrollPane(employeeTable), BorderLayout.CENTER);

        // Search behavior
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { applySearch(); }
            @Override public void removeUpdate(DocumentEvent e) { applySearch(); }
            @Override public void changedUpdate(DocumentEvent e) { applySearch(); }
        });

        // Button actions
        addBtn.addActionListener(e -> startAdd());
        viewBtn.addActionListener(e -> startView());
        editBtn.addActionListener(e -> startEdit());
        deleteBtn.addActionListener(e -> deleteEmployee());

        return panel;
    }

    private void applySearch() {
        String text = searchField.getText().trim();
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Employee e : employees) {
            tableModel.addRow(new Object[]{
                    e.getEmployeeNumber(),
                    e.getFullName(),
                    e.getPosition(),
                    e.getStatus()
            });
        }
    }

    private Employee getSelectedEmployeeFromTable() {
        int viewRow = employeeTable.getSelectedRow();
        if (viewRow == -1) return null;

        int modelRow = employeeTable.convertRowIndexToModel(viewRow);
        String empNo = String.valueOf(tableModel.getValueAt(modelRow, 0));

        for (Employee e : employees) {
            if (e.getEmployeeNumber().equals(empNo)) return e;
        }
        return null;
    }

    // ------------------- FORM VIEW -------------------
    private JPanel buildFormView() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        formTitle = new JLabel("Employee Details");
        formTitle.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(formTitle, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        // Basic info
        empNoField = new JTextField(15); empNoField.setEditable(false);
        lastNameField = new JTextField(15);
        firstNameField = new JTextField(15);
        birthdayField = new JTextField(15);
        addressField = new JTextField(15);
        phoneField = new JTextField(15);

        row = addSection(form, gbc, row, "Basic Information");
        row = addRow(form, gbc, row, "Employee #", empNoField);
        row = addRow(form, gbc, row, "Last Name", lastNameField);
        row = addRow(form, gbc, row, "First Name", firstNameField);
        row = addRow(form, gbc, row, "Birthday (MM/dd/yyyy)", birthdayField);
        row = addRow(form, gbc, row, "Address", addressField);
        row = addRow(form, gbc, row, "Phone Number", phoneField);

        // Government IDs
        sssField = new JTextField(15);
        philhealthField = new JTextField(15);
        tinField = new JTextField(15);
        pagibigField = new JTextField(15);

        row = addSection(form, gbc, row, "Government IDs");
        row = addRow(form, gbc, row, "SSS #", sssField);
        row = addRow(form, gbc, row, "PhilHealth #", philhealthField);
        row = addRow(form, gbc, row, "TIN #", tinField);
        row = addRow(form, gbc, row, "Pag-IBIG #", pagibigField);

        // Status / Position / Supervisor
        statusCombo = new JComboBox<>(new String[]{"Regular", "Probationary"});
        positionField = new JTextField(15);
        supervisorField = new JTextField(15);

        row = addSection(form, gbc, row, "Employment Details");
        row = addRow(form, gbc, row, "Status", statusCombo);
        row = addRow(form, gbc, row, "Position", positionField);
        row = addRow(form, gbc, row, "Immediate Supervisor", supervisorField);

        // Compensation
        basicSalaryField = new JTextField(15);
        riceField = new JTextField(15);
        phoneAllowField = new JTextField(15);
        clothingAllowField = new JTextField(15);
        grossSemiField = new JTextField(15);
        hourlyField = new JTextField(15);

        row = addSection(form, gbc, row, "Compensation");
        row = addRow(form, gbc, row, "Basic Salary", basicSalaryField);
        row = addRow(form, gbc, row, "Rice Subsidy", riceField);
        row = addRow(form, gbc, row, "Phone Allowance", phoneAllowField);
        row = addRow(form, gbc, row, "Clothing Allowance", clothingAllowField);
        row = addRow(form, gbc, row, "Gross Semi-monthly Rate", grossSemiField);
        row = addRow(form, gbc, row, "Hourly Rate", hourlyField);

        panel.add(new JScrollPane(form), BorderLayout.CENTER);

        // Bottom buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveBtn = new JButton("Save");
        cancelBtn = new JButton("Cancel");
        bottom.add(saveBtn);
        bottom.add(cancelBtn);

        saveBtn.addActionListener(e -> saveEmployee());
        cancelBtn.addActionListener(e -> showListView());

        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    private int addSection(JPanel form, GridBagConstraints gbc, int row, String title) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        form.add(label, gbc);
        gbc.gridwidth = 1;
        return row + 1;
    }

    private int addRow(JPanel form, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        form.add(new JLabel(labelText), gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        form.add(field, gbc);
        return row + 1;
    }

    // ------------------- ACTIONS -------------------
    private void showListView() {
        refreshTable();
        viewLayout.show(viewPanel, "LIST");
        clearForm();
        selectedEmployee = null;
    }

    private void showFormView() {
        viewLayout.show(viewPanel, "FORM");
    }

    private void startAdd() {
        mode = Mode.ADD;
        formTitle.setText("Add New Employee");
        clearForm();

        String nextId = CSVUtil.generateNextEmployeeNumber(employees);
        empNoField.setText(nextId);

        setFormEditable(true);
        saveBtn.setEnabled(true);

        showFormView();
    }

    private void startView() {
        Employee e = getSelectedEmployeeFromTable();
        if (e == null) {
            JOptionPane.showMessageDialog(this, "Please select an employee first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        mode = Mode.VIEW;
        formTitle.setText("View Employee Details");
        selectedEmployee = e;

        loadToForm(e);
        setFormEditable(false);
        saveBtn.setEnabled(false);

        showFormView();
    }

    private void startEdit() {
        Employee e = getSelectedEmployeeFromTable();
        if (e == null) {
            JOptionPane.showMessageDialog(this, "Please select an employee first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        mode = Mode.EDIT;
        formTitle.setText("Edit Employee");
        selectedEmployee = e;

        loadToForm(e);
        setFormEditable(true);
        empNoField.setEditable(false);
        saveBtn.setEnabled(true);

        showFormView();
    }

    private void deleteEmployee() {
        Employee e = getSelectedEmployeeFromTable();
        if (e == null) {
            JOptionPane.showMessageDialog(this, "Please select an employee first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete employee " + e.getEmployeeNumber() + " (" + e.getFullName() + ")?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            employees.remove(e);
            CSVUtil.saveEmployees(employees);
            refreshTable();
        }
    }

    private void saveEmployee() {
        // Validate required fields
        String empNo = empNoField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String birthday = birthdayField.getText().trim();

        if (empNo.isEmpty() || lastName.isEmpty() || firstName.isEmpty() || birthday.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Employee #, Last Name, First Name, and Birthday are required.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidDate(birthday)) {
            JOptionPane.showMessageDialog(this,
                    "Birthday must be in MM/dd/yyyy format (example: 01/25/2002).",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Parse numeric fields
        Double basic = parseMoney(basicSalaryField.getText(), "Basic Salary"); if (basic == null) return;
        Double rice = parseMoney(riceField.getText(), "Rice Subsidy"); if (rice == null) return;
        Double phoneA = parseMoney(phoneAllowField.getText(), "Phone Allowance"); if (phoneA == null) return;
        Double cloth = parseMoney(clothingAllowField.getText(), "Clothing Allowance"); if (cloth == null) return;
        Double grossSemi = parseMoney(grossSemiField.getText(), "Gross Semi-monthly Rate"); if (grossSemi == null) return;
        Double hourly = parseMoney(hourlyField.getText(), "Hourly Rate"); if (hourly == null) return;

        if (mode == Mode.ADD) {
            Employee e = new Employee();
            fillFromForm(e, basic, rice, phoneA, cloth, grossSemi, hourly);
            employees.add(e);
        } else if (mode == Mode.EDIT && selectedEmployee != null) {
            fillFromForm(selectedEmployee, basic, rice, phoneA, cloth, grossSemi, hourly);
        } else {
            return;
        }

        CSVUtil.saveEmployees(employees);
        showListView();
    }

    private void fillFromForm(Employee e, double basic, double rice, double phoneA, double cloth, double grossSemi, double hourly) {
        e.setEmployeeNumber(empNoField.getText().trim());
        e.setLastName(lastNameField.getText().trim());
        e.setFirstName(firstNameField.getText().trim());
        e.setBirthday(birthdayField.getText().trim());
        e.setAddress(addressField.getText().trim());
        e.setPhoneNumber(phoneField.getText().trim());

        e.setSssNumber(sssField.getText().trim());
        e.setPhilHealthNumber(philhealthField.getText().trim());
        e.setTinNumber(tinField.getText().trim());
        e.setPagIbigNumber(pagibigField.getText().trim());

        e.setStatus(String.valueOf(statusCombo.getSelectedItem()));
        e.setPosition(positionField.getText().trim());
        e.setImmediateSupervisor(supervisorField.getText().trim());

        e.setBasicSalary(basic);
        e.setRiceSubsidy(rice);
        e.setPhoneAllowance(phoneA);
        e.setClothingAllowance(cloth);
        e.setGrossSemiMonthlyRate(grossSemi);
        e.setHourlyRate(hourly);
    }

    private void loadToForm(Employee e) {
        empNoField.setText(e.getEmployeeNumber());
        lastNameField.setText(e.getLastName());
        firstNameField.setText(e.getFirstName());
        birthdayField.setText(e.getBirthday());
        addressField.setText(e.getAddress());
        phoneField.setText(e.getPhoneNumber());

        sssField.setText(e.getSssNumber());
        philhealthField.setText(e.getPhilHealthNumber());
        tinField.setText(e.getTinNumber());
        pagibigField.setText(e.getPagIbigNumber());

        statusCombo.setSelectedItem(e.getStatus() == null || e.getStatus().isBlank() ? "Regular" : e.getStatus());
        positionField.setText(e.getPosition());
        supervisorField.setText(e.getImmediateSupervisor());

        basicSalaryField.setText(String.valueOf(e.getBasicSalary()));
        riceField.setText(String.valueOf(e.getRiceSubsidy()));
        phoneAllowField.setText(String.valueOf(e.getPhoneAllowance()));
        clothingAllowField.setText(String.valueOf(e.getClothingAllowance()));
        grossSemiField.setText(String.valueOf(e.getGrossSemiMonthlyRate()));
        hourlyField.setText(String.valueOf(e.getHourlyRate()));
    }

    private void clearForm() {
        empNoField.setText("");
        lastNameField.setText("");
        firstNameField.setText("");
        birthdayField.setText("");
        addressField.setText("");
        phoneField.setText("");

        sssField.setText("");
        philhealthField.setText("");
        tinField.setText("");
        pagibigField.setText("");

        statusCombo.setSelectedItem("Regular");
        positionField.setText("");
        supervisorField.setText("");

        basicSalaryField.setText("0");
        riceField.setText("0");
        phoneAllowField.setText("0");
        clothingAllowField.setText("0");
        grossSemiField.setText("0");
        hourlyField.setText("0");
    }

    private void setFormEditable(boolean editable) {
        lastNameField.setEditable(editable);
        firstNameField.setEditable(editable);
        birthdayField.setEditable(editable);
        addressField.setEditable(editable);
        phoneField.setEditable(editable);

        sssField.setEditable(editable);
        philhealthField.setEditable(editable);
        tinField.setEditable(editable);
        pagibigField.setEditable(editable);

        statusCombo.setEnabled(editable);
        positionField.setEditable(editable);
        supervisorField.setEditable(editable);

        basicSalaryField.setEditable(editable);
        riceField.setEditable(editable);
        phoneAllowField.setEditable(editable);
        clothingAllowField.setEditable(editable);
        grossSemiField.setEditable(editable);
        hourlyField.setEditable(editable);
    }

    private boolean isValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        sdf.setLenient(false);
        try {
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private Double parseMoney(String text, String fieldName) {
        String t = text.trim();
        if (t.isEmpty()) t = "0";
        try {
            double val = Double.parseDouble(t);
            if (val < 0) {
                JOptionPane.showMessageDialog(this,
                        fieldName + " cannot be negative.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }
            return val;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    fieldName + " must be a number (example: 15000 or 15000.50).",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
