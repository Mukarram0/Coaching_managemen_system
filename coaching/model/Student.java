package coaching.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Student {
    private int studentId;
    private String fullName;
    private LocalDate dob;
    private String gender;
    private String contact;
    private String email;
    private String address;
    private String courseEnrolled;
    private LocalDate enrollmentDate;
    private BigDecimal totalFees;

    public Student() {}

    public Student(int studentId, String fullName, LocalDate dob, String gender,
                   String contact, String email, String address, String courseEnrolled,
                   LocalDate enrollmentDate, BigDecimal totalFees) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.dob = dob;
        this.gender = gender;
        this.contact = contact;
        this.email = email;
        this.address = address;
        this.courseEnrolled = courseEnrolled;
        this.enrollmentDate = enrollmentDate;
        this.totalFees = totalFees;
    }

    // Getters and Setters
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCourseEnrolled() {
        return courseEnrolled;
    }

    public void setCourseEnrolled(String courseEnrolled) {
        this.courseEnrolled = courseEnrolled;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public BigDecimal getTotalFees() {
        return totalFees;
    }

    public void setTotalFees(BigDecimal totalFees) {
        this.totalFees = totalFees;
    }
}