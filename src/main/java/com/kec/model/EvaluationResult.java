package com.kec.model;

public class EvaluationResult {
    private String rollNumber;
    private String studentName;
    private String department;
    private int semester;
    private String courseCode;
    private String courseTitle;
    private int credits;
    private CourseType courseType;

    // Attendance
    private int totalClasses;
    private int classesAttended;
    private double attendancePercentage;
    private boolean medicalCondonation;
    private String attendanceEligibilityStatus;

    // Assessment Marks
    private Double cia1;
    private Double cia2;
    private Double assignment;
    private Double rawCia100;
    private double scaledCia;

    private Double rawEse;
    private double scaledEse;
    private boolean eseAbsent;

    private double totalMarks;
    private Grade grade;
    private int gradePoint;
    private boolean isPass;
    private String remarks;

    public EvaluationResult() {
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public CourseType getCourseType() {
        return courseType;
    }

    public void setCourseType(CourseType courseType) {
        this.courseType = courseType;
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

    public double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }

    public boolean isMedicalCondonation() {
        return medicalCondonation;
    }

    public void setMedicalCondonation(boolean medicalCondonation) {
        this.medicalCondonation = medicalCondonation;
    }

    public String getAttendanceEligibilityStatus() {
        return attendanceEligibilityStatus;
    }

    public void setAttendanceEligibilityStatus(String attendanceEligibilityStatus) {
        this.attendanceEligibilityStatus = attendanceEligibilityStatus;
    }

    public Double getCia1() {
        return cia1;
    }

    public void setCia1(Double cia1) {
        this.cia1 = cia1;
    }

    public Double getCia2() {
        return cia2;
    }

    public void setCia2(Double cia2) {
        this.cia2 = cia2;
    }

    public Double getAssignment() {
        return assignment;
    }

    public void setAssignment(Double assignment) {
        this.assignment = assignment;
    }

    public Double getRawCia100() {
        return rawCia100;
    }

    public void setRawCia100(Double rawCia100) {
        this.rawCia100 = rawCia100;
    }

    public double getScaledCia() {
        return scaledCia;
    }

    public void setScaledCia(double scaledCia) {
        this.scaledCia = scaledCia;
    }

    public Double getRawEse() {
        return rawEse;
    }

    public void setRawEse(Double rawEse) {
        this.rawEse = rawEse;
    }

    public double getScaledEse() {
        return scaledEse;
    }

    public void setScaledEse(double scaledEse) {
        this.scaledEse = scaledEse;
    }

    public boolean isEseAbsent() {
        return eseAbsent;
    }

    public void setEseAbsent(boolean eseAbsent) {
        this.eseAbsent = eseAbsent;
    }

    public double getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(double totalMarks) {
        this.totalMarks = totalMarks;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public int getGradePoint() {
        return gradePoint;
    }

    public void setGradePoint(int gradePoint) {
        this.gradePoint = gradePoint;
    }

    public boolean isPass() {
        return isPass;
    }

    public void setPass(boolean pass) {
        isPass = pass;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getCreditPointsEarned() {
        return credits * gradePoint;
    }
}
