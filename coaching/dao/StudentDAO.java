package coaching.dao;

import coaching.model.Student;
import coaching.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public void addStudent(Student student) throws SQLException {
        String sql = "INSERT INTO students (full_name, dob, gender, contact, email, address, course_enrolled, enrollment_date, total_fees) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getFullName());
            stmt.setDate(2, student.getDob() != null ? Date.valueOf(student.getDob()) : null);
            stmt.setString(3, student.getGender());
            stmt.setString(4, student.getContact());
            stmt.setString(5, student.getEmail());
            stmt.setString(6, student.getAddress());
            stmt.setString(7, student.getCourseEnrolled());
            stmt.setDate(8, student.getEnrollmentDate() != null ? Date.valueOf(student.getEnrollmentDate()) : null);
            stmt.setBigDecimal(9, student.getTotalFees());
            stmt.executeUpdate();
        }
    }

    public List<Student> getAllStudents() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Student s = new Student();
                s.setStudentId(rs.getInt("student_id"));
                s.setFullName(rs.getString("full_name"));
                Date dob = rs.getDate("dob");
                s.setDob(dob != null ? dob.toLocalDate() : null);
                s.setGender(rs.getString("gender"));
                s.setContact(rs.getString("contact"));
                s.setEmail(rs.getString("email"));
                s.setAddress(rs.getString("address"));
                s.setCourseEnrolled(rs.getString("course_enrolled"));
                Date enrollment = rs.getDate("enrollment_date");
                s.setEnrollmentDate(enrollment != null ? enrollment.toLocalDate() : null);
                s.setTotalFees(rs.getBigDecimal("total_fees"));
                list.add(s);
            }
        }
        return list;
    }

    public void updateStudent(Student student) throws SQLException {
        String sql = "UPDATE students SET full_name=?, dob=?, gender=?, contact=?, email=?, address=?, course_enrolled=?, enrollment_date=?, total_fees=? WHERE student_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getFullName());
            stmt.setDate(2, student.getDob() != null ? Date.valueOf(student.getDob()) : null);
            stmt.setString(3, student.getGender());
            stmt.setString(4, student.getContact());
            stmt.setString(5, student.getEmail());
            stmt.setString(6, student.getAddress());
            stmt.setString(7, student.getCourseEnrolled());
            stmt.setDate(8, student.getEnrollmentDate() != null ? Date.valueOf(student.getEnrollmentDate()) : null);
            stmt.setBigDecimal(9, student.getTotalFees());
            stmt.setInt(10, student.getStudentId());
            stmt.executeUpdate();
        }
    }

    public void deleteStudent(int studentId) throws SQLException {
        String sql = "DELETE FROM students WHERE student_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.executeUpdate();
        }
    }

    public BigDecimal getTotalFeesPaidByStudent(int studentId) throws SQLException {
        String sql = "SELECT SUM(amount_paid) FROM fees WHERE student_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        }
        return BigDecimal.ZERO;
    }
}