package coaching.dao;

import coaching.model.Fee;
import coaching.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeeDAO {

    public void addFeePayment(Fee fee) throws SQLException {
        String sql = "INSERT INTO fees (student_id, amount_paid, payment_date, payment_mode, remarks) VALUES (?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fee.getStudentId());
            stmt.setBigDecimal(2, fee.getAmountPaid());
            stmt.setDate(3, Date.valueOf(fee.getPaymentDate()));
            stmt.setString(4, fee.getPaymentMode());
            stmt.setString(5, fee.getRemarks());
            stmt.executeUpdate();
        }
    }

    public List<Fee> getAllFeeTransactions() throws SQLException {
        List<Fee> list = new ArrayList<>();
        String sql = "SELECT f.*, s.full_name, s.course_enrolled FROM fees f JOIN students s ON f.student_id = s.student_id ORDER BY f.payment_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Fee fee = new Fee();
                fee.setTransactionId(rs.getInt("transaction_id"));
                fee.setStudentId(rs.getInt("student_id"));
                fee.setStudentName(rs.getString("full_name"));
                fee.setCourse(rs.getString("course_enrolled"));
                fee.setAmountPaid(rs.getBigDecimal("amount_paid"));
                fee.setPaymentDate(rs.getDate("payment_date").toLocalDate());
                fee.setPaymentMode(rs.getString("payment_mode"));
                fee.setRemarks(rs.getString("remarks"));
                list.add(fee);
            }
        }
        return list;
    }

    // Additional methods for summary stats
    public BigDecimal getTotalFeesCollected() throws SQLException {
        String sql = "SELECT SUM(amount_paid) FROM fees";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getBigDecimal(1);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getTotalPendingDues() throws SQLException {
        String sql = "SELECT SUM(s.total_fees - COALESCE((SELECT SUM(amount_paid) FROM fees WHERE fees.student_id = s.student_id),0)) FROM students s";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getBigDecimal(1);
        }
        return BigDecimal.ZERO;
    }
}