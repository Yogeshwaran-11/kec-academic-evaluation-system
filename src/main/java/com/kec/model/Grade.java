package com.kec.model;

/**
 * 10-Point Absolute Grading System under Kongu Engineering College (Autonomous) - Regulation 2024.
 *
 * Grading Scale:
 * - O  (91 - 100) -> Grade Point: 10
 * - A+ (81 - 90)  -> Grade Point: 9
 * - A  (71 - 80)  -> Grade Point: 8
 * - B+ (61 - 70)  -> Grade Point: 7
 * - B  (50 - 60)  -> Grade Point: 6
 * - RA (Re-Appearance / Fail if < 50 aggregate or failed ESE minimum) -> Grade Point: 0
 * - SA (Shortage of Attendance, attendance < 65%) -> Grade Point: 0
 * - AB (Absent in End Semester Examination) -> Grade Point: 0
 */
public enum Grade {
    O("O", 10, "Outstanding", true),
    A_PLUS("A+", 9, "Excellent", true),
    A("A", 8, "Very Good", true),
    B_PLUS("B+", 7, "Good", true),
    B("B", 6, "Average / Pass", true),
    RA("RA", 0, "Re-Appearance", false),
    SA("SA", 0, "Shortage of Attendance", false),
    AB("AB", 0, "Absent in ESE", false),
    PENDING("PND", 0, "Evaluation Incomplete", false);

    private final String letter;
    private final int gradePoint;
    private final String description;
    private final boolean isPass;

    Grade(String letter, int gradePoint, String description, boolean isPass) {
        this.letter = letter;
        this.gradePoint = gradePoint;
        this.description = description;
        this.isPass = isPass;
    }

    public String getLetter() {
        return letter;
    }

    public int getGradePoint() {
        return gradePoint;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPass() {
        return isPass;
    }

    /**
     * Determines the grade based on total marks obtained (rounded).
     */
    public static Grade fromMarks(double totalMarks) {
        long rounded = Math.round(totalMarks);
        if (rounded >= 91 && rounded <= 100) {
            return O;
        } else if (rounded >= 81 && rounded <= 90) {
            return A_PLUS;
        } else if (rounded >= 71 && rounded <= 80) {
            return A;
        } else if (rounded >= 61 && rounded <= 70) {
            return B_PLUS;
        } else if (rounded >= 50 && rounded <= 60) {
            return B;
        } else {
            return RA;
        }
    }
}
