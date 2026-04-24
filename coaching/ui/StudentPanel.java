package coaching.ui;

import coaching.dao.StudentDAO;
import coaching.model.Student;
import coaching.ui.components.RoundedButton;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class StudentPanel extends JPanel {
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private StudentDAO studentDAO;
    private List<Student> studentList;          // cache for easy lookup
    private JTextField nameField, dobField, contactField, emailField, addressField, courseField, enrollmentField, feesField;
    private JComboBox<String> genderCombo;
    private int editingStudentId = -1;

    public StudentPanel() {
        studentDAO = new StudentDAO();
        initUI();
        loadStudents();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Student Details"));
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Full Name:"), gbc);
        nameField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        // Row 1
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("DOB (YYYY-MM-DD):"), gbc);
        dobField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(dobField, gbc);

        // Row 2
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Gender:"), gbc);
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        gbc.gridx = 1;
        formPanel.add(genderCombo, gbc);

        // Row 3
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Contact:"), gbc);
        contactField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(contactField, gbc);

        // Row 4
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Email:"), gbc);
        emailField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        // Row 5
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Address:"), gbc);
        addressField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(addressField, gbc);

        // Row 6
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Course Enrolled:"), gbc);
        courseField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(courseField, gbc);

        // Row 7
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Enrollment Date (YYYY-MM-DD):"), gbc);
        enrollmentField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(enrollmentField, gbc);

        // Row 8
        gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(new JLabel("Total Fees:"), gbc);
        feesField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(feesField, gbc);

        // Button Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnPanel.setBackground(Color.WHITE);
        RoundedButton addBtn = new RoundedButton("Add");
        addBtn.setBackground(new Color(76, 175, 80));
        addBtn.setForeground(Color.WHITE);
        addBtn.addActionListener(e -> addStudent());

        RoundedButton updateBtn = new RoundedButton("Update");
        updateBtn.setBackground(new Color(33, 150, 243));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.addActionListener(e -> updateStudent());

        RoundedButton deleteBtn = new RoundedButton("Delete");
        deleteBtn.setBackground(new Color(244, 67, 54));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.addActionListener(e -> deleteStudent());

        RoundedButton clearBtn = new RoundedButton("Clear");
        clearBtn.addActionListener(e -> clearForm());

        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(clearBtn);

        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(btnPanel, gbc);

        add(formPanel, BorderLayout.NORTH);

        // Table Panel
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Course", "Enrollment Date", "Total Fees"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = studentTable.getSelectedRow();
                if (row >= 0) {
                    populateFormFromSelectedRow(row);
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Student List"));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void populateFormFromSelectedRow(int row) {
        if (row < 0 || studentList == null || row >= studentList.size()) return;
        Student s = studentList.get(row);
        editingStudentId = s.getStudentId();
        nameField.setText(s.getFullName());
        dobField.setText(s.getDob() != null ? s.getDob().toString() : "");
        genderCombo.setSelectedItem(s.getGender());
        contactField.setText(s.getContact());
        emailField.setText(s.getEmail());
        addressField.setText(s.getAddress());
        courseField.setText(s.getCourseEnrolled());
        enrollmentField.setText(s.getEnrollmentDate() != null ? s.getEnrollmentDate().toString() : "");
        feesField.setText(s.getTotalFees().toString());
    }

    private void loadStudents() {
        SwingWorker<List<Student>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Student> doInBackground() throws Exception {
                return studentDAO.getAllStudents();
            }

            @Override
            protected void done() {
                try {
                    studentList = get();
                    tableModel.setRowCount(0);
                    for (Student s : studentList) {
                        tableModel.addRow(new Object[]{
                                s.getStudentId(),
                                s.getFullName(),
                                s.getCourseEnrolled(),
                                s.getEnrollmentDate(),
                                s.getTotalFees()
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(StudentPanel.this,
                            "Error loading students: " + ex.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void addStudent() {
        // Validate required fields
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Full Name is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (feesField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Total Fees is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Student student = new Student();
        student.setFullName(nameField.getText().trim());
        student.setDob(parseDate(dobField.getText()));
        student.setGender((String) genderCombo.getSelectedItem());
        student.setContact(contactField.getText().trim());
        student.setEmail(emailField.getText().trim());
        student.setAddress(addressField.getText().trim());
        student.setCourseEnrolled(courseField.getText().trim());
        student.setEnrollmentDate(parseDate(enrollmentField.getText()));
        try {
            student.setTotalFees(new BigDecimal(feesField.getText().trim()));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Total Fees amount.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                studentDAO.addStudent(student);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    loadStudents();
                    clearForm();
                    JOptionPane.showMessageDialog(StudentPanel.this, "Student added successfully.");
                } catch (Exception ex) {
                    String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    JOptionPane.showMessageDialog(StudentPanel.this,
                            "Error adding student: " + msg,
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void updateStudent() {
        if (editingStudentId == -1) {
            JOptionPane.showMessageDialog(this, "No student selected. Please select a student from the table.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Student student = new Student();
        student.setStudentId(editingStudentId);
        student.setFullName(nameField.getText().trim());
        student.setDob(parseDate(dobField.getText()));
        student.setGender((String) genderCombo.getSelectedItem());
        student.setContact(contactField.getText().trim());
        student.setEmail(emailField.getText().trim());
        student.setAddress(addressField.getText().trim());
        student.setCourseEnrolled(courseField.getText().trim());
        student.setEnrollmentDate(parseDate(enrollmentField.getText()));
        try {
            student.setTotalFees(new BigDecimal(feesField.getText().trim()));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Total Fees amount.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                studentDAO.updateStudent(student);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    loadStudents();
                    clearForm();
                    JOptionPane.showMessageDialog(StudentPanel.this, "Student updated successfully.");
                } catch (Exception ex) {
                    String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    JOptionPane.showMessageDialog(StudentPanel.this,
                            "Error updating student: " + msg,
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void deleteStudent() {
        if (editingStudentId == -1) {
            JOptionPane.showMessageDialog(this, "No student selected. Please select a student from the table.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this student?\nAll fee records for this student will also be deleted.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                studentDAO.deleteStudent(editingStudentId);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    loadStudents();
                    clearForm();
                    JOptionPane.showMessageDialog(StudentPanel.this, "Student deleted successfully.");
                } catch (Exception ex) {
                    String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    JOptionPane.showMessageDialog(StudentPanel.this,
                            "Error deleting student: " + msg,
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void clearForm() {
        nameField.setText("");
        dobField.setText("");
        genderCombo.setSelectedIndex(0);
        contactField.setText("");
        emailField.setText("");
        addressField.setText("");
        courseField.setText("");
        enrollmentField.setText("");
        feesField.setText("");
        editingStudentId = -1;
        studentTable.clearSelection();
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(dateStr.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}