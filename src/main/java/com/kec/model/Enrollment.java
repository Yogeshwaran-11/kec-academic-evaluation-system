package com.kec.model;

import java.sql.Timestamp;

public class Enrollment {
    private int enrollmentId;
    private String rollNumber;
    private String courseCode;
    private int semester;
    private int totalClasses;
    private int classesAttended;
    private boolean medicalCondonation;
    private Timestamp createdAt;

    // Joined fields for display
    private String studentName;
    private String courseTitle;
    private int courseCredits;
    private CourseType courseType;

    public Enrollment() {
    }

    public Enrollment(String rollNumber, String courseCode, int semester, int totalClasses, int classesAttended, boolean medicalCondonation) {
        this.rollNumber = rollNumber;
        this.courseCode = courseCode;
        this.semester = semester;
        this.totalClasses = totalClasses;
        this.classesAttended = classesAttended;
        this.medicalCondonation = medicalCondonation;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(int totalClasses) {
        this.totalClasses = totalClasses;
    }

    public int getClassesAttended() {
        return classesAttended;
    }

    public void setClassesAttended(int classesAttended) {
        this.classesAttended = classesAttended;
    }

    public boolean isMedicalCondonation() {
        return medicalCondonation;
    }

    public void setMedicalCondonation(boolean medicalCondonation) {
        this.medicalCondonation = medicalCondonation;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public int getCourseCredits() {
        return courseCredits;
    }

    public void setCourseCredits(int courseCredits) {
        this.courseCredits = courseCredits;
    }

    public CourseType getCourseType() {
        return courseType;
    }

    public void setCourseType(CourseType courseType) {
        this.courseType = courseType;
    }

    public double getAttendancePercentage() {
        if (totalClasses <= 0) return 0.0;
        return (classesAttended * 100.0) / totalClasses;
    }

    /**
     * Attendance Eligibility Status according to KEC R2024:
     * - >= 75%: Eligible
     * - 65% - 74.9%: Conditionally Eligible (Requires Medical Condonation)
     * - < 65%: Shortage of Attendance (SA - Barred)
     */
    public boolean isAttendanceEligibleForEse() {
        double pct = getAttendancePercentage();
        if (pct >= 75.0) {
            return true;
        } else if (pct >= 65.0) {
            return medicalCondonation;
        } else {
            return false;
        }
    }

    public String getAttendanceStatusLabel() {
        double pct = getAttendancePercentage();
        if (pct >= 75.0) {
            return "ELIGIBLE (>= 75%)";
        } else if (pct >= 65.0) {
            return medicalCondonation ? "CONDONED (Approved)" : "CONDITIONAL (Pending Approval)";
        } else {
            return "DEFICIT (SA - < 65% Barred)";
        }
    }
}
