package com.kec.util;

import com.kec.model.EvaluationResult;
import com.kec.model.Grade;
import com.kec.service.EvaluationEngine;

import java.util.List;
import java.util.Map;

/**
 * ReportGenerator - Generates formal, beautifully formatted KEC R2024 academic reports:
 * 1. Student Semester Mark Sheet (with SGPA & Equivalent Percentage)
 * 2. Attendance Defaulter Report (< 65% flagged with SA)
 * 3. Class Performance & Department Result Summary
 */
public class ReportGenerator {

    private static final String LINE_DOUBLE = "=".repeat(105);
    private static final String LINE_SINGLE = "-".repeat(105);

    /**
     * Generates a formal Student Semester Mark Sheet for KEC R2024.
     */
    public static String generateStudentMarkSheet(String rollNumber, String studentName, 
                                                  String department, int semester, 
                                                  List<EvaluationResult> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(LINE_DOUBLE).append("\n");
        sb.append(centerText("KONGU ENGINEERING COLLEGE (AUTONOMOUS) - PERUNDURAI, ERODE 638060", 105)).append("\n");
        sb.append(centerText("OFFICE OF THE CONTROLLER OF EXAMINATIONS", 105)).append("\n");
        sb.append(centerText("STUDENT SEMESTER GRADE SHEET - REGULATION 2024 (R2024)", 105)).append("\n");
        sb.append(LINE_DOUBLE).append("\n");

        sb.append(String.format("  Roll Number : %-20s Student Name: %-30s\n", rollNumber, (studentName != null ? studentName : "N/A")));
        sb.append(String.format("  Department  : %-20s Semester    : %-5d Academic Year: 2024-2025\n", department, semester));
        sb.append(LINE_SINGLE).append("\n");

        sb.append(String.format(" %-9s | %-24s | %-9s | %-3s | %-6s | %-10s | %-10s | %-5s | %-4s | %-2s | %-6s\n",
                "Course", "Course Title", "Type", "Crd", "Att %", "CIA(Scale)", "ESE(Scale)", "Total", "Grd", "GP", "Status"));
        sb.append(LINE_SINGLE).append("\n");

        if (results == null || results.isEmpty()) {
            sb.append("   No course enrollments found for this semester.\n");
            sb.append(LINE_DOUBLE).append("\n");
            return sb.toString();
        }

        int totalCredits = 0;
        int creditsEarned = 0;
        int totalGradePointsEarned = 0;
        boolean hasBacklog = false;

        for (EvaluationResult r : results) {
            totalCredits += r.getCredits();
            if (r.isPass()) {
                creditsEarned += r.getCredits();
            } else {
                hasBacklog = true;
            }
            totalGradePointsEarned += r.getCreditPointsEarned();

            String ciaStr = (r.getCia1() == null && r.getCia2() == null) ? "PENDING" :
                    String.format("%.1f(%.1f)", (r.getRawCia100() != null ? r.getRawCia100() : 0.0), r.getScaledCia());
            String eseStr;
            if (r.isEseAbsent()) {
                eseStr = "ABSENT";
            } else if (r.getRawEse() == null) {
                eseStr = "PENDING";
            } else {
                eseStr = String.format("%.1f(%.1f)", r.getRawEse(), r.getScaledEse());
            }

            String totalStr = (r.getGrade() == Grade.PENDING || r.getGrade() == Grade.SA || r.getGrade() == Grade.AB) ?
                    "N/A" : String.format("%.1f", r.getTotalMarks());

            String statusStr = r.getGrade() == Grade.PENDING ? "PND" :
                               r.getGrade() == Grade.SA ? "SA" :
                               r.getGrade() == Grade.AB ? "AB" :
                               r.isPass() ? "PASS" : "RA";

            sb.append(String.format(" %-9s | %-24s | %-9s | %3d | %5.1f%% | %-10s | %-10s | %5s | %-4s | %2d | %-6s\n",
                    r.getCourseCode(),
                    truncate(r.getCourseTitle(), 24),
                    r.getCourseType().getDisplayName(),
                    r.getCredits(),
                    r.getAttendancePercentage(),
                    ciaStr,
                    eseStr,
                    totalStr,
                    r.getGrade().getLetter(),
                    r.getGradePoint(),
                    statusStr
            ));
        }

        double sgpa = EvaluationEngine.calculateSgpa(results);
        double percentage = EvaluationEngine.calculatePercentage(sgpa);

        sb.append(LINE_SINGLE).append("\n");
        sb.append(String.format("  Total Credits Registered : %-4d | Total Credits Earned : %-4d\n", totalCredits, creditsEarned));
        sb.append(String.format("  Semester GPA (SGPA)       : %-4.2f / 10.00\n", sgpa));
        sb.append(String.format("  Equivalent Percentage    : %-5.2f %%\n", percentage));

        String standing;
        if (hasBacklog) {
            standing = "RE-APPEARANCE (RA) / SHORTAGE (SA) - Course(s) to be Cleared";
        } else if (sgpa >= 8.5) {
            standing = "FIRST CLASS WITH DISTINCTION";
        } else if (sgpa >= 6.5) {
            standing = "FIRST CLASS";
        } else {
            standing = "SECOND CLASS";
        }
        sb.append(String.format("  Academic Standing        : %s\n", standing));
        sb.append(LINE_DOUBLE).append("\n");
        sb.append("  [Grade Legend: O=10(Outstanding), A+=9(Excellent), A=8(Very Good), B+=7(Good), B=6(Pass), RA=0, SA=0, AB=0]\n");
        sb.append(LINE_DOUBLE).append("\n");

        return sb.toString();
    }

    /**
     * Generates the Attendance Defaulter Report (< 65% attendance flagged with SA).
     */
    public static String generateAttendanceDefaulterReport(List<EvaluationResult> defaulters) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(LINE_DOUBLE).append("\n");
        sb.append(centerText("KONGU ENGINEERING COLLEGE (AUTONOMOUS) - REGULATION 2024", 105)).append("\n");
        sb.append(centerText("ATTENDANCE DEFAULTER REPORT (ATTENDANCE < 65% - FLAGGED WITH GRADE 'SA')", 105)).append("\n");
        sb.append(centerText("STATUS: BARRED FROM APPEARING IN END SEMESTER EXAMINATIONS (ESE)", 105)).append("\n");
        sb.append(LINE_DOUBLE).append("\n");

        if (defaulters == null || defaulters.isEmpty()) {
            sb.append("\n   *** CONGRATULATIONS: No attendance defaulters found! All students met attendance criteria. ***\n\n");
            sb.append(LINE_DOUBLE).append("\n");
            return sb.toString();
        }

        sb.append(String.format(" %-10s | %-22s | %-6s | %-9s | %-22s | %-5s | %-8s | %-5s | %-6s\n",
                "Roll No", "Student Name", "Dept", "Course", "Course Title", "Held", "Attended", "Att %", "Grade"));
        sb.append(LINE_SINGLE).append("\n");

        for (EvaluationResult d : defaulters) {
            sb.append(String.format(" %-10s | %-22s | %-6s | %-9s | %-22s | %5d | %8d | %5.1f%% | %-6s\n",
                    d.getRollNumber(),
                    truncate(d.getStudentName(), 22),
                    d.getDepartment(),
                    d.getCourseCode(),
                    truncate(d.getCourseTitle(), 22),
                    d.getTotalClasses(),
                    d.getClassesAttended(),
                    d.getAttendancePercentage(),
                    d.getGrade().getLetter() + " (SA)"
            ));
        }

        sb.append(LINE_SINGLE).append("\n");
        sb.append(String.format("  Total Number of Defaulter Course Enrollments: %d\n", defaulters.size()));
        sb.append("  Rule Note: In accordance with KEC R2024 Clause 7.2, students with attendance < 65%\n");
        sb.append("             are barred from ESE and awarded grade SA (0 Grade Points).\n");
        sb.append(LINE_DOUBLE).append("\n");

        return sb.toString();
    }

    /**
     * Generates Department Class Performance & Result Summary.
     */
    public static String generateClassPerformanceSummary(String department, int semester, 
                                                         int totalStudents, int passCount, 
                                                         int failCount, double avgSgpa) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(LINE_DOUBLE).append("\n");
        sb.append(centerText("KONGU ENGINEERING COLLEGE (AUTONOMOUS) - REGULATION 2024", 105)).append("\n");
        sb.append(centerText(String.format("CLASS PERFORMANCE & RESULT SUMMARY - DEPARTMENT OF %s (SEM %d)", department, semester), 105)).append("\n");
        sb.append(LINE_DOUBLE).append("\n");

        double passPct = (totalStudents > 0) ? ((double) passCount / totalStudents) * 100.0 : 0.0;

        sb.append(String.format("  Department Name               : %-30s\n", department));
        sb.append(String.format("  Academic Semester             : Semester %d\n", semester));
        sb.append(String.format("  Total Students Evaluated      : %-5d\n", totalStudents));
        sb.append(String.format("  Students Passed (All Clear)   : %-5d\n", passCount));
        sb.append(String.format("  Students with RA / SA / AB    : %-5d\n", failCount));
        sb.append(String.format("  Class Pass Percentage         : %-5.2f %%\n", passPct));
        sb.append(String.format("  Class Average SGPA            : %-4.2f / 10.00\n", avgSgpa));
        sb.append(String.format("  Class Average Percentage      : %-5.2f %%\n", avgSgpa * 10.0));
        sb.append(LINE_SINGLE).append("\n");

        String assessment;
        if (passPct >= 90.0) {
            assessment = "EXCELLENT ACADEMIC PERFORMANCE (Tier 1 Outcome)";
        } else if (passPct >= 75.0) {
            assessment = "GOOD ACADEMIC PERFORMANCE (Meeting Accreditation Targets)";
        } else {
            assessment = "ATTENTION REQUIRED (Special coaching recommended for RA students)";
        }
        sb.append(String.format("  Department Benchmark Status   : %s\n", assessment));
        sb.append(LINE_DOUBLE).append("\n");

        return sb.toString();
    }

    private static String centerText(String text, int width) {
        if (text.length() >= width) return text;
        int leftPad = (width - text.length()) / 2;
        int rightPad = width - text.length() - leftPad;
        return " ".repeat(leftPad) + text + " ".repeat(rightPad);
    }

    private static String truncate(String text, int maxLen) {
        if (text == null) return "";
        if (text.length() <= maxLen) return text;
        return text.substring(0, maxLen - 2) + "..";
    }
}
