package motorph.ui;

import motorph.model.Employee;
import motorph.util.CSVUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class EmployeePanel extends JPanel {

    private final MainFrame mainFrame;

    private final CardLayout viewLayout = new CardLayout();
    private final JPanel viewPanel = new JPanel(viewLayout);

    // THEME (light)
    private static final Color BG = new Color(245, 247, 252);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER = new Color(225, 230, 240);
    private static final Color TEXT = new Color(35, 45, 65);
    private static final Color MUTED = new Color(110, 120, 145);

    // Data
    private List<Employee> employees;

    // List view
    private DefaultTableModel tableModel;
    private JTable employeeTable;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;

    private JButton addBtn, viewBtn, editBtn, deleteBtn;

    // Form view (tabs)
    private JTextField empNoField, lastNameField, firstNameField, addressField;
    private JFormattedTextField birthdayField, phoneField;

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
        setBackground(BG);

        viewPanel.setOpaque(true);
        viewPanel.setBackground(BG);

        employees = CSVUtil.loadEmployees();

        viewPanel.add(buildListView(), "LIST");
        viewPanel.add(buildFormView(), "FORM");

        add(viewPanel, BorderLayout.CENTER);
        showListView();
    }

    // ===================== LIST VIEW =====================
    private JPanel buildListView() {
        JPanel root = new JPanel(new BorderLayout(14, 14));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(18, 18, 18, 18));

        // Header (not a card)
        JPanel headerRow = new JPanel(new BorderLayout(12, 12));
        headerRow.setOpaque(false);

        JLabel titleLabel = new JLabel("Employees");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(TEXT);

        JLabel subtitleLabel = new JLabel("Manage employee records (CSV-based storage)");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitleLabel.setForeground(MUTED);

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(titleLabel);
        titleBox.add(Box.createVerticalStrut(3));
        titleBox.add(subtitleLabel);

        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> mainFrame.showContent("DASHBOARD"));

        headerRow.add(titleBox, BorderLayout.WEST);
        headerRow.add(backBtn, BorderLayout.EAST);

        root.add(headerRow, BorderLayout.NORTH);

        // Main card (toolbar + table)
        JPanel card = createCard(new BorderLayout(12, 12));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(14, 14, 14, 14)
        ));

        // Toolbar row inside card
        JPanel toolbar = new JPanel(new BorderLayout(12, 12));
        toolbar.setOpaque(false);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonRow.setOpaque(false);

        addBtn = new JButton("Add New");
        viewBtn = new JButton("View");
        editBtn = new JButton("Edit");
        deleteBtn = new JButton("Delete");

        Dimension btnSize = new Dimension(110, 34);
        styleButton(addBtn, btnSize);
        styleButton(viewBtn, btnSize);
        styleButton(editBtn, btnSize);
        styleDangerButton(deleteBtn, btnSize);

        buttonRow.add(addBtn);
        buttonRow.add(viewBtn);
        buttonRow.add(editBtn);
        buttonRow.add(deleteBtn);

        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setForeground(MUTED);

        searchField = new JTextField(22);
        searchField.setPreferredSize(new Dimension(240, 34));

        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        toolbar.add(buttonRow, BorderLayout.WEST);
        toolbar.add(searchPanel, BorderLayout.EAST);

        // Table
        tableModel = new DefaultTableModel(
                new Object[]{"Employee #", "Name", "Department/Position", "Status"},
                0
        ) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setRowHeight(26);

        sorter = new TableRowSorter<>(tableModel);
        employeeTable.setRowSorter(sorter);

        refreshTable();

        JScrollPane sp = new JScrollPane(employeeTable);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));

        // Add to card
        card.add(toolbar, BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);

        root.add(card, BorderLayout.CENTER);

        // Search filter
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { applySearch(); }
            @Override public void removeUpdate(DocumentEvent e) { applySearch(); }
            @Override public void changedUpdate(DocumentEvent e) { applySearch(); }
        });

        // Enable/disable buttons based on selection
        setActionButtonsEnabled(false);
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                setActionButtonsEnabled(employeeTable.getSelectedRow() != -1);
            }
        });

        // Actions
        addBtn.addActionListener(e -> startAdd());
        viewBtn.addActionListener(e -> startView());
        editBtn.addActionListener(e -> startEdit());
        deleteBtn.addActionListener(e -> deleteEmployee());

        return root;
    }

    private JPanel createCard(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(CARD_BG);
        return p;
    }

    private void styleButton(JButton btn, Dimension size) {
        btn.setFocusPainted(false);
        btn.setPreferredSize(size);
        btn.setMinimumSize(size);
        btn.setMaximumSize(size);
    }

    private void styleDangerButton(JButton btn, Dimension size) {
        styleButton(btn, size);
        btn.setForeground(new Color(120, 40, 40));
        btn.setBackground(new Color(245, 225, 225));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
    }

    private void setActionButtonsEnabled(boolean enabled) {
        viewBtn.setEnabled(enabled);
        editBtn.setEnabled(enabled);
        deleteBtn.setEnabled(enabled);
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
        setActionButtonsEnabled(false);
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

    // ===================== FORM VIEW (TABBED) =====================
    private JPanel buildFormView() {
        JPanel root = new JPanel(new BorderLayout(14, 14));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(18, 18, 18, 18));

        // Header
        JPanel header = new JPanel(new BorderLayout(12, 12));
        header.setOpaque(false);

        formTitle = new JLabel("Employee Details");
        formTitle.setFont(new Font("Arial", Font.BOLD, 22));
        formTitle.setForeground(TEXT);

        JButton backToListBtn = new JButton("Back to List");
        backToListBtn.setFocusPainted(false);
        backToListBtn.addActionListener(e -> showListView());

        header.add(formTitle, BorderLayout.WEST);
        header.add(backToListBtn, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);

        // Card with tabs
        JPanel card = createCard(new BorderLayout(12, 12));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(CARD_BG);

        tabs.addTab("Basic", buildBasicTab());
        tabs.addTab("Gov IDs", buildGovTab());
        tabs.addTab("Compensation", buildCompTab());

        card.add(tabs, BorderLayout.CENTER);

        // Bottom buttons (inside the card for cleaner UI)
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottom.setOpaque(false);

        saveBtn = new JButton("Save");
        cancelBtn = new JButton("Cancel");

        Dimension btnSize = new Dimension(110, 34);
        styleButton(saveBtn, btnSize);
        styleButton(cancelBtn, btnSize);

        saveBtn.addActionListener(e -> saveEmployee());
        cancelBtn.addActionListener(e -> showListView());

        bottom.add(cancelBtn);
        bottom.add(saveBtn);

        card.add(bottom, BorderLayout.SOUTH);

        root.add(card, BorderLayout.CENTER);

        return root;
    }

    private JPanel buildBasicTab() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(CARD_BG);
        form.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = baseGbc();

        empNoField = new JTextField(18); empNoField.setEditable(false);
        lastNameField = new JTextField(18);
        firstNameField = new JTextField(18);

        birthdayField = createMaskedField("##/##/####"); // MM/dd/yyyy
        phoneField = createMaskedField("###-###-###");   // example format

        addressField = new JTextField(18);

        statusCombo = new JComboBox<>(new String[]{"Regular", "Probationary"});
        positionField = new JTextField(18);
        supervisorField = new JTextField(18);

        int row = 0;

        row = addRow(form, gbc, row, required("Employee #"), empNoField);
        row = addRow(form, gbc, row, required("Last Name"), lastNameField);
        row = addRow(form, gbc, row, required("First Name"), firstNameField);
        row = addRow(form, gbc, row, required("Birthday (MM/dd/yyyy)"), birthdayField);

        row = addRow(form, gbc, row, "Phone Number (###-###-###)", phoneField);
        row = addRow(form, gbc, row, "Address", addressField);

        row = addSeparator(form, gbc, row, "Employment Details");
        row = addRow(form, gbc, row, "Status", statusCombo);
        row = addRow(form, gbc, row, "Position", positionField);
        row = addRow(form, gbc, row, "Immediate Supervisor", supervisorField);

        row = addHint(form, gbc, row, "Fields marked with * are required. Birthday uses MM/dd/yyyy format.");

        return wrapScrollable(form);
    }

    private JPanel buildGovTab() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(CARD_BG);
        form.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = baseGbc();

        sssField = new JTextField(18);
        philhealthField = new JTextField(18);
        tinField = new JTextField(18);
        pagibigField = new JTextField(18);

        int row = 0;
        row = addRow(form, gbc, row, "SSS #", sssField);
        row = addRow(form, gbc, row, "PhilHealth #", philhealthField);
        row = addRow(form, gbc, row, "TIN #", tinField);
        row = addRow(form, gbc, row, "Pag-IBIG #", pagibigField);

        row = addHint(form, gbc, row, "Tip: You can store IDs as numbers or formatted strings. They will be saved to CSV.");

        return wrapScrollable(form);
    }

    private JPanel buildCompTab() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(CARD_BG);
        form.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = baseGbc();

        basicSalaryField = new JTextField(18);
        riceField = new JTextField(18);
        phoneAllowField = new JTextField(18);
        clothingAllowField = new JTextField(18);
        grossSemiField = new JTextField(18);
        hourlyField = new JTextField(18);

        int row = 0;
        row = addRow(form, gbc, row, "Basic Salary (Monthly)", basicSalaryField);
        row = addRow(form, gbc, row, "Rice Subsidy", riceField);
        row = addRow(form, gbc, row, "Phone Allowance", phoneAllowField);
        row = addRow(form, gbc, row, "Clothing Allowance", clothingAllowField);
        row = addRow(form, gbc, row, "Gross Semi-monthly Rate", grossSemiField);
        row = addRow(form, gbc, row, "Hourly Rate", hourlyField);

        row = addHint(form, gbc, row, "Enter numeric values only (e.g., 25000 or 25000.50). Negative values are not allowed.");

        return wrapScrollable(form);
    }

    private GridBagConstraints baseGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        return gbc;
    }

    private String required(String label) {
        return label + " *";
    }

    private int addSeparator(JPanel form, GridBagConstraints gbc, int row, String title) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(MUTED);
        form.add(label, gbc);
        gbc.gridwidth = 1;
        return row + 1;
    }

    private int addRow(JPanel form, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        JLabel label = new JLabel(labelText);
        label.setForeground(TEXT);
        form.add(label, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        form.add(field, gbc);

        return row + 1;
    }

    private int addHint(JPanel form, GridBagConstraints gbc, int row, String hint) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel label = new JLabel("<html><span style='font-size:10px;color:#6E788C;'>" + hint + "</span></html>");
        form.add(label, gbc);
        gbc.gridwidth = 1;
        return row + 1;
    }

    private JPanel wrapScrollable(JPanel inner) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        JScrollPane sp = new JScrollPane(inner);
        sp.setBorder(BorderFactory.createEmptyBorder());
        wrapper.add(sp, BorderLayout.CENTER);
        return wrapper;
    }

    private JFormattedTextField createMaskedField(String mask) {
        try {
            MaskFormatter formatter = new MaskFormatter(mask);
            formatter.setPlaceholderCharacter('_');
            JFormattedTextField field = new JFormattedTextField(formatter);
            field.setColumns(18);
            return field;
        } catch (ParseException e) {
            return new JFormattedTextField();
        }
    }

    // ===================== VIEW SWITCH =====================
    private void showListView() {
        employees = CSVUtil.loadEmployees();
        refreshTable();
        viewLayout.show(viewPanel, "LIST");
        clearForm();
        selectedEmployee = null;
    }

    private void showFormView() {
        viewLayout.show(viewPanel, "FORM");
    }

    // ===================== CRUD ACTIONS =====================
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
        String empNo = empNoField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String birthday = birthdayField.getText().trim();

        if (empNo.isEmpty() || lastName.isEmpty() || firstName.isEmpty() || birthday.isEmpty() || birthday.contains("_")) {
            JOptionPane.showMessageDialog(this,
                    "Please complete all required fields marked with *.",
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

        String phone = phoneField.getText().trim();
        if (!phone.isEmpty() && phone.contains("_")) {
            JOptionPane.showMessageDialog(this,
                    "Phone number format is incomplete. Use ###-###-###.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

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
        e.setPhoneNumber(phoneField.getText().trim().replace("_", "").trim());

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
        if (empNoField != null) empNoField.setText("");
        if (lastNameField != null) lastNameField.setText("");
        if (firstNameField != null) firstNameField.setText("");
        if (birthdayField != null) birthdayField.setText("");
        if (addressField != null) addressField.setText("");
        if (phoneField != null) phoneField.setText("");

        if (sssField != null) sssField.setText("");
        if (philhealthField != null) philhealthField.setText("");
        if (tinField != null) tinField.setText("");
        if (pagibigField != null) pagibigField.setText("");

        if (statusCombo != null) statusCombo.setSelectedItem("Regular");
        if (positionField != null) positionField.setText("");
        if (supervisorField != null) supervisorField.setText("");

        if (basicSalaryField != null) basicSalaryField.setText("0");
        if (riceField != null) riceField.setText("0");
        if (phoneAllowField != null) phoneAllowField.setText("0");
        if (clothingAllowField != null) clothingAllowField.setText("0");
        if (grossSemiField != null) grossSemiField.setText("0");
        if (hourlyField != null) hourlyField.setText("0");
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
