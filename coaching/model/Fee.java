package coaching.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Fee {
    private int transactionId;
    private int studentId;
    private String studentName;   // For display only (joined from students table)
    private String course;        // For display only (joined from students table)
    private BigDecimal amountPaid;
    private LocalDate paymentDate;
    private String paymentMode;
    private String remarks;

    public Fee() {}

    public Fee(int transactionId, int studentId, String studentName, String course,
               BigDecimal amountPaid, LocalDate paymentDate, String paymentMode, String remarks) {
        this.transactionId = transactionId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.course = course;
        this.amountPaid = amountPaid;
        this.paymentDate = paymentDate;
        this.paymentMode = paymentMode;
        this.remarks = remarks;
    }

    // Getters and Setters
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}