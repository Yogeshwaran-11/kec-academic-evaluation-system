package com.kec.service;

import com.kec.model.*;

import java.util.List;

/**
 * Kongu Engineering College (Autonomous) - Regulation 2024 (R2024)
 * Academic Evaluation & Weightage Engine
 *
 * Rules implemented:
 * 1. THEORY: CIA = 40% | ESE = 60% | ESE Min = 45% (27/60) | Pass Min = 50 Marks
 * 2. PRACTICAL: CIA = 60% | ESE = 40% | ESE Min = 50% (20/40) | Pass Min = 50 Marks
 * 3. EMBEDDED: CIA = 50% | ESE = 50% | ESE Min = 45% (22.5/50) | Pass Min = 50 Marks
 *
 * Attendance Filter:
 * - >= 75%: Eligible for ESE
 * - 65% - 74.9%: Conditionally eligible (Requires Medical Condonation approval)
 * - < 65%: SA (Shortage of Attendance, 0 GP), Barred from ESE
 *
 * 10-Point Grading System:
 * - O (91-100) -> 10 GP
 * - A+ (81-90) -> 9 GP
 * - A (71-80) -> 8 GP
 * - B+ (61-70) -> 7 GP
 * - B (50-60) -> 6 GP
 * - RA (< 50 or failed ESE threshold) -> 0 GP
 * - SA (Shortage of Attendance) -> 0 GP
 * - AB (Absent in ESE) -> 0 GP
 *
 * Formulas:
 * - SGPA = Sum(Credits * Grade Points) / Sum(Enrolled Credits)
 * - Percentage = SGPA * 10
 */
public class EvaluationEngine {

    /**
     * Calculates the Continuous Internal Assessment (CIA) score normalized to 100.
     * Weights: CIA Test 1 (40%), CIA Test 2 (40%), Assignments/Quizzes/Lab (20%).
     */
    public static Double calculateRawCia100(Double cia1, Double cia2, Double assignment) {
        if (cia1 == null || cia2 == null) {
            return null;
        }
        double assgVal = (assignment != null) ? assignment : 0.0;
        return (cia1 * 0.40) + (cia2 * 0.40) + (assgVal * 0.20);
    }

    /**
     * Evaluates a single student course enrollment under KEC R2024.
     */
    public static EvaluationResult evaluateEnrollment(Enrollment enrollment, Course course, AssessmentMark mark) {
        EvaluationResult result = new EvaluationResult();
        result.setRollNumber(enrollment.getRollNumber());
        result.setStudentName(enrollment.getStudentName());
        result.setCourseCode(course.getCourseCode());
        result.setCourseTitle(course.getTitle());
        result.setCredits(course.getCredits());
        result.setCourseType(course.getCourseType());
        result.setTotalClasses(enrollment.getTotalClasses());
        result.setClassesAttended(enrollment.getClassesAttended());
        result.setMedicalCondonation(enrollment.isMedicalCondonation());

        double attPct = enrollment.getAttendancePercentage();
        result.setAttendancePercentage(attPct);

        // 1. Enforce Attendance Rules
        if (attPct < 65.0) {
            result.setAttendanceEligibilityStatus("BARRED (<65%)");
            result.setGrade(Grade.SA);
            result.setGradePoint(Grade.SA.getGradePoint());
            result.setPass(false);
            result.setRemarks("Shortage of Attendance (SA). Barred from ESE.");
            return result;
        } else if (attPct < 75.0) {
            if (!enrollment.isMedicalCondonation()) {
                result.setAttendanceEligibilityStatus("CONDITIONAL - PENDING CONDONATION");
                result.setGrade(Grade.SA);
                result.setGradePoint(Grade.SA.getGradePoint());
                result.setPass(false);
                result.setRemarks("Attendance between 65-74.9%. Barred pending Medical Condonation approval.");
                return result;
            } else {
                result.setAttendanceEligibilityStatus("CONDONED (Approved)");
            }
        } else {
            result.setAttendanceEligibilityStatus("ELIGIBLE (>=75%)");
        }

        // 2. Process Marks
        if (mark == null) {
            result.setGrade(Grade.PENDING);
            result.setGradePoint(0);
            result.setPass(false);
            result.setRemarks("No marks entered yet.");
            return result;
        }

        result.setCia1(mark.getCia1Marks());
        result.setCia2(mark.getCia2Marks());
        result.setAssignment(mark.getAssignmentMarks());
        result.setEseAbsent(mark.isEseAbsent());
        result.setRawEse(mark.getEseMarks());

        // Check Absenteeism
        if (mark.isEseAbsent()) {
            result.setGrade(Grade.AB);
            result.setGradePoint(Grade.AB.getGradePoint());
            result.setPass(false);
            result.setRemarks("Absent in End Semester Examination (AB).");
            return result;
        }

        // Calculate CIA
        Double rawCia = calculateRawCia100(mark.getCia1Marks(), mark.getCia2Marks(), mark.getAssignmentMarks());
        result.setRawCia100(rawCia);

        CourseType ct = course.getCourseType();
        double scaledCia = (rawCia != null) ? (rawCia * ct.getCiaWeightage()) : 0.0;
        result.setScaledCia(scaledCia);

        // Check if ESE is evaluated
        if (mark.getEseMarks() == null) {
            result.setGrade(Grade.PENDING);
            result.setGradePoint(0);
            result.setPass(false);
            result.setRemarks("ESE Marks Pending.");
            return result;
        }

        double rawEse = mark.getEseMarks();
        double scaledEse = rawEse * ct.getEseWeightage();
        result.setScaledEse(scaledEse);

        double totalMarks = scaledCia + scaledEse;
        result.setTotalMarks(totalMarks);

        // 3. Evaluation Rules & Minimum Passing Thresholds
        // ESE Minimum check
        boolean eseMinSatisfied = (rawEse >= ct.getEseMinThresholdRaw());
        boolean aggregateSatisfied = (Math.round(totalMarks) >= ct.getOverallPassMin());

        if (!eseMinSatisfied) {
            result.setGrade(Grade.RA);
            result.setGradePoint(Grade.RA.getGradePoint());
            result.setPass(false);
            result.setRemarks(String.format("Failed ESE Min Threshold (Scored %.1f / Min %.1f)", 
                    rawEse, ct.getEseMinThresholdRaw()));
        } else if (!aggregateSatisfied) {
            result.setGrade(Grade.RA);
            result.setGradePoint(Grade.RA.getGradePoint());
            result.setPass(false);
            result.setRemarks(String.format("Failed Aggregate Pass Mark (Total %.1f < %.1f)", 
                    totalMarks, ct.getOverallPassMin()));
        } else {
            Grade finalGrade = Grade.fromMarks(totalMarks);
            result.setGrade(finalGrade);
            result.setGradePoint(finalGrade.getGradePoint());
            result.setPass(true);
            result.setRemarks("Passed");
        }

        return result;
    }

    /**
     * Calculates Semester Grade Point Average (SGPA).
     * Formula: SGPA = Sum(Credits * Grade Points Earned) / Sum(Total Enrolled Credits)
     */
    public static double calculateSgpa(List<EvaluationResult> results) {
        if (results == null || results.isEmpty()) return 0.0;
        int totalCredits = 0;
        int weightedPoints = 0;

        for (EvaluationResult r : results) {
            totalCredits += r.getCredits();
            weightedPoints += r.getCreditPointsEarned();
        }

        if (totalCredits == 0) return 0.0;
        return (double) weightedPoints / totalCredits;
    }

    /**
     * Calculates Equivalent Percentage under KEC R2024.
     * Formula: Equivalent Percentage = SGPA * 10
     */
    public static double calculatePercentage(double sgpa) {
        return sgpa * 10.0;
    }
}
