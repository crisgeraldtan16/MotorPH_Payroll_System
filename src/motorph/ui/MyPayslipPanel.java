package motorph.ui;

import motorph.model.PayrollRecord;
import motorph.model.User;
import motorph.util.PayrollIOUtil;
import motorph.util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.YearMonth;
import java.util.List;

public class MyPayslipPanel extends JPanel {

    private static final Color BG = new Color(245, 247, 252);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER = new Color(225, 230, 240);
    private static final Color TEXT = new Color(28, 35, 50);
    private static final Color MUTED = new Color(110, 120, 140);

    private JComboBox<Integer> yearCombo;
    private JComboBox<String> monthCombo;

    private DefaultTableModel model;
    private JTextArea payslipArea;

    public MyPayslipPanel() {
        setLayout(new BorderLayout(14, 14));
        setBackground(BG);
        setBorder(new EmptyBorder(18, 18, 18, 18));

        add(header(), BorderLayout.NORTH);
        add(body(), BorderLayout.CENTER);

        refresh();
    }

    private JComponent header() {
        JPanel header = new JPanel(new BorderLayout(12, 12));
        header.setOpaque(false);

        JLabel title = new JLabel("My Payslip");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("View your payroll records saved by HR/Admin");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(title);
        left.add(Box.createVerticalStrut(3));
        left.add(subtitle);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        yearCombo = new JComboBox<>(new Integer[]{2024, 2025, 2026, 2027});
        yearCombo.setSelectedItem(2024);

        monthCombo = new JComboBox<>(new String[]{
                "01 - January", "02 - February", "03 - March", "04 - April",
                "05 - May", "06 - June", "07 - July", "08 - August",
                "09 - September", "10 - October", "11 - November", "12 - December"
        });
        monthCombo.setSelectedIndex(0);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> refresh());

        JButton viewBtn = new JButton("View Selected");
        viewBtn.setFocusPainted(false);
        viewBtn.addActionListener(e -> showSelectedPayslip());

        right.add(new JLabel("Year:"));
        right.add(yearCombo);
        right.add(new JLabel("Month:"));
        right.add(monthCombo);
        right.add(viewBtn);
        right.add(refreshBtn);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JComponent body() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableCard(), payslipCard());
        split.setResizeWeight(0.45);
        split.setDividerLocation(520);
        split.setBorder(null);
        return split;
    }

    private JComponent tableCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JLabel t = new JLabel("My Payroll Records");
        t.setFont(new Font("Arial", Font.BOLD, 14));
        t.setForeground(TEXT);

        model = new DefaultTableModel(
                new Object[]{"Month", "Days Present", "Late (min)", "Gov", "Tax", "Net"},
                0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(model);
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        card.add(t, BorderLayout.NORTH);
        card.add(new JScrollPane(table), BorderLayout.CENTER);

        // store table reference in component for selection
        card.putClientProperty("table", table);

        return card;
    }

    private JComponent payslipCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JLabel t = new JLabel("Payslip Preview");
        t.setFont(new Font("Arial", Font.BOLD, 14));
        t.setForeground(TEXT);

        payslipArea = new JTextArea();
        payslipArea.setEditable(false);
        payslipArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        payslipArea.setText("Select a payroll record then click “View Selected”.\n");

        card.add(t, BorderLayout.NORTH);
        card.add(new JScrollPane(payslipArea), BorderLayout.CENTER);

        return card;
    }

    private YearMonth getSelectedMonth() {
        int year = (int) yearCombo.getSelectedItem();
        String m = (String) monthCombo.getSelectedItem();
        int month = Integer.parseInt(m.substring(0, 2));
        return YearMonth.of(year, month);
    }

    private void refresh() {
        model.setRowCount(0);
        payslipArea.setText("Select a payroll record then click “View Selected”.\n");

        User u = Session.getCurrentUser();
        if (u == null || !u.isEmployee()) return;

        YearMonth ym = getSelectedMonth();
        List<PayrollRecord> list = PayrollIOUtil.loadPayrollRecordsForEmployeeMonth(u.getEmployeeNumber(), ym);

        for (PayrollRecord pr : list) {
            model.addRow(new Object[]{
                    pr.getMonth(),
                    pr.getDaysPresent(),
                    pr.getLateMinutes(),
                    money(pr.getTotalDeductionsBeforeTax()),
                    money(pr.getWithholdingTax()),
                    money(pr.getNetPay())
            });
        }
    }

    private void showSelectedPayslip() {
        User u = Session.getCurrentUser();
        if (u == null || !u.isEmployee()) return;

        JPanel tableCard = (JPanel) ((JSplitPane) getComponent(1)).getLeftComponent();
        JTable table = (JTable) tableCard.getClientProperty("table");
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a record first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        YearMonth ym = getSelectedMonth();
        List<PayrollRecord> list = PayrollIOUtil.loadPayrollRecordsForEmployeeMonth(u.getEmployeeNumber(), ym);
        if (list.isEmpty()) {
            payslipArea.setText("No payroll record found for " + ym + ".\n");
            return;
        }

        PayrollRecord pr = list.get(0); // you save one per month per employee
        payslipArea.setText(PayrollIOUtil.formatPayslipText(pr));
        payslipArea.setCaretPosition(0);
    }

    private String money(double v) {
        return String.format("%,.2f", v);
    }
}
