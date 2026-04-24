package coaching.ui;

import coaching.dao.FeeDAO;
import coaching.dao.StudentDAO;
import coaching.model.Fee;
import coaching.model.Student;
import coaching.ui.components.RoundedButton;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class FeesPanel extends JPanel {
    private JTable feesTable;
    private DefaultTableModel tableModel;
    private FeeDAO feeDAO;
    private StudentDAO studentDAO;

    // Summary labels
    private JLabel totalStudentsLabel;
    private JLabel totalCollectedLabel;
    private JLabel pendingDuesLabel;

    // Form fields
    private JComboBox<String> studentComboBox;
    private Map<String, Integer> studentMap; // Maps "Name (ID)" -> studentId
    private JTextField amountField;
    private JTextField paymentDateField;
    private JComboBox<String> modeComboBox;
    private JTextField remarksField;

    public FeesPanel() {
        feeDAO = new FeeDAO();
        studentDAO = new StudentDAO();
        studentMap = new HashMap<>();
        initUI();
        loadSummaryData();
        loadFeeTransactions();
        loadStudentComboBox();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // Top panel: Summary Cards
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        summaryPanel.add(createSummaryCard("Total Students", "0", new Color(33, 150, 243)));
        summaryPanel.add(createSummaryCard("Total Fees Collected", "$0.00", new Color(76, 175, 80)));
        summaryPanel.add(createSummaryCard("Pending Dues", "$0.00", new Color(244, 67, 54)));

        // Store references to the value labels
        totalStudentsLabel = (JLabel) ((JPanel) summaryPanel.getComponent(0)).getComponent(1);
        totalCollectedLabel = (JLabel) ((JPanel) summaryPanel.getComponent(1)).getComponent(1);
        pendingDuesLabel = (JLabel) ((JPanel) summaryPanel.getComponent(2)).getComponent(1);

        add(summaryPanel, BorderLayout.NORTH);

        // Center: Split pane with form and table
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.3);
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);

        JPanel formPanel = createPaymentForm();
        splitPane.setTopComponent(formPanel);

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Fee Transactions"));

        tableModel = new DefaultTableModel(
                new String[]{"Trans ID", "Student", "Course", "Amount Paid", "Payment Date", "Mode", "Remarks"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        feesTable = new JTable(tableModel);
        feesTable.setRowHeight(25);
        feesTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        feesTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        feesTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(feesTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        splitPane.setBottomComponent(tablePanel);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createSummaryCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        titleLabel.setForeground(Color.GRAY);
        card.add(titleLabel, BorderLayout.NORTH);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        valueLabel.setForeground(color);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createPaymentForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Add Fee Payment"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Student selection
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Select Student:"), gbc);
        studentComboBox = new JComboBox<>();
        gbc.gridx = 1; gbc.gridwidth = 2;
        panel.add(studentComboBox, gbc);

        // Amount
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(new JLabel("Amount Paid:"), gbc);
        amountField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(amountField, gbc);

        // Payment Date
        gbc.gridx = 2;
        panel.add(new JLabel("Payment Date (YYYY-MM-DD):"), gbc);
        paymentDateField = new JTextField(10);
        paymentDateField.setText(LocalDate.now().toString());
        gbc.gridx = 3;
        panel.add(paymentDateField, gbc);

        // Payment Mode
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Payment Mode:"), gbc);
        modeComboBox = new JComboBox<>(new String[]{"Cash", "Card", "Online"});
        gbc.gridx = 1;
        panel.add(modeComboBox, gbc);

        // Remarks
        gbc.gridx = 2;
        panel.add(new JLabel("Remarks:"), gbc);
        remarksField = new JTextField(15);
        gbc.gridx = 3;
        panel.add(remarksField, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setBackground(Color.WHITE);

        RoundedButton addPaymentBtn = new RoundedButton("Add Payment");
        addPaymentBtn.setBackground(new Color(76, 175, 80));
        addPaymentBtn.setForeground(Color.WHITE);
        addPaymentBtn.addActionListener(e -> addPayment());

        RoundedButton clearBtn = new RoundedButton("Clear");
        clearBtn.addActionListener(e -> clearForm());

        btnPanel.add(addPaymentBtn);
        btnPanel.add(clearBtn);
        panel.add(btnPanel, gbc);

        return panel;
    }

    private void loadStudentComboBox() {
        SwingWorker<List<Student>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Student> doInBackground() throws Exception {
                return studentDAO.getAllStudents();
            }
            @Override
            protected void done() {
                try {
                    List<Student> students = get();
                    studentComboBox.removeAllItems();
                    studentMap.clear();
                    for (Student s : students) {
                        String display = s.getFullName() + " (" + s.getStudentId() + ") - " + s.getCourseEnrolled();
                        studentComboBox.addItem(display);
                        studentMap.put(display, s.getStudentId());
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(FeesPanel.this, "Error loading students: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void loadSummaryData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            int studentCount = 0;
            BigDecimal collected = BigDecimal.ZERO;
            BigDecimal pending = BigDecimal.ZERO;
            @Override
            protected Void doInBackground() throws Exception {
                List<Student> students = studentDAO.getAllStudents();
                studentCount = students.size();
                collected = feeDAO.getTotalFeesCollected();
                pending = feeDAO.getTotalPendingDues();
                return null;
            }
            @Override
            protected void done() {
                try {
                    get();
                    totalStudentsLabel.setText(String.valueOf(studentCount));
                    totalCollectedLabel.setText(String.format("$%.2f", collected));
                    pendingDuesLabel.setText(String.format("$%.2f", pending));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void loadFeeTransactions() {
        SwingWorker<List<Fee>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Fee> doInBackground() throws Exception {
                return feeDAO.getAllFeeTransactions();
            }
            @Override
            protected void done() {
                try {
                    List<Fee> fees = get();
                    tableModel.setRowCount(0);
                    for (Fee f : fees) {
                        tableModel.addRow(new Object[]{
                                f.getTransactionId(),
                                f.getStudentName(),
                                f.getCourse(),
                                f.getAmountPaid(),
                                f.getPaymentDate(),
                                f.getPaymentMode(),
                                f.getRemarks()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(FeesPanel.this, "Error loading transactions: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void addPayment() {
        String selected = (String) studentComboBox.getSelectedItem();
        if (selected == null || selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a student.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Integer studentId = studentMap.get(selected);
        if (studentId == null) {
            JOptionPane.showMessageDialog(this, "Invalid student selection.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String amountText = amountField.getText().trim();
        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter amount paid.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount format.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate paymentDate;
        try {
            paymentDate = LocalDate.parse(paymentDateField.getText().trim());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String mode = (String) modeComboBox.getSelectedItem();
        String remarks = remarksField.getText().trim();

        Fee fee = new Fee();
        fee.setStudentId(studentId);
        fee.setAmountPaid(amount);
        fee.setPaymentDate(paymentDate);
        fee.setPaymentMode(mode);
        fee.setRemarks(remarks);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                feeDAO.addFeePayment(fee);
                return null;
            }
            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(FeesPanel.this, "Payment recorded successfully.");
                    clearForm();
                    loadFeeTransactions();
                    loadSummaryData();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(FeesPanel.this, "Error adding payment: " + e.getCause().getMessage());
                }
            }
        };
        worker.execute();
    }

    private void clearForm() {
        amountField.setText("");
        paymentDateField.setText(LocalDate.now().toString());
        remarksField.setText("");
        modeComboBox.setSelectedIndex(0);
    }
}