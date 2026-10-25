package com.kec.model;

import java.sql.Timestamp;

public class AssessmentMark {
    private int markId;
    private int enrollmentId;
    private Double cia1Marks;        // Phase 1 (0-100)
    private Double cia2Marks;        // Phase 1 (0-100)
    private Double assignmentMarks;  // Phase 2 (0-100)
    private Double eseMarks;         // Phase 3 (0-100)
    private boolean eseAbsent;       // Mark Absence (AB)
    private Timestamp updatedAt;

    public AssessmentMark() {
    }

    public AssessmentMark(int enrollmentId, Double cia1Marks, Double cia2Marks, Double assignmentMarks, Double eseMarks, boolean eseAbsent) {
        this.enrollmentId = enrollmentId;
        this.cia1Marks = cia1Marks;
        this.cia2Marks = cia2Marks;
        this.assignmentMarks = assignmentMarks;
        this.eseMarks = eseMarks;
        this.eseAbsent = eseAbsent;
    }

    public int getMarkId() {
        return markId;
    }

    public void setMarkId(int markId) {
        this.markId = markId;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public Double getCia1Marks() {
        return cia1Marks;
    }

    public void setCia1Marks(Double cia1Marks) {
        this.cia1Marks = cia1Marks;
    }

    public Double getCia2Marks() {
        return cia2Marks;
    }

    public void setCia2Marks(Double cia2Marks) {
        this.cia2Marks = cia2Marks;
    }

    public Double getAssignmentMarks() {
        return assignmentMarks;
    }

    public void setAssignmentMarks(Double assignmentMarks) {
        this.assignmentMarks = assignmentMarks;
    }

    public Double getEseMarks() {
        return eseMarks;
    }

    public void setEseMarks(Double eseMarks) {
        this.eseMarks = eseMarks;
    }

    public boolean isEseAbsent() {
        return eseAbsent;
    }

    public void setEseAbsent(boolean eseAbsent) {
        this.eseAbsent = eseAbsent;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean hasPhase1() {
        return cia1Marks != null && cia2Marks != null;
    }

    public boolean hasPhase2() {
        return hasPhase1() && assignmentMarks != null;
    }

    public boolean hasPhase3() {
        return hasPhase2() && (eseMarks != null || eseAbsent);
    }
}
