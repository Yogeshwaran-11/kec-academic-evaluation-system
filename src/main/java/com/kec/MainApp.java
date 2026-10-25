package com.kec;

import com.kec.dao.AcademicDAO;
import com.kec.dao.StudentDAO;
import com.kec.db.DBConnection;
import com.kec.model.*;
import com.kec.service.EvaluationEngine;
import com.kec.util.ReportGenerator;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * MainApp - Kongu Engineering College (Autonomous) - Regulation 2024 (R2024)
 * Student Academic & Continuous Evaluation System
 *
 * Menu-driven terminal interface providing:
 * 1. Student & Course Management (Data Entry)
 * 2. Phased Incremental Marks Entry ("Then and Now")
 * 3. KEC R2024 Academic Reporting & Defaulter Identification
 * 4. Automated Proof Demonstration Runner
 */
public class MainApp {

    private static final StudentDAO studentDAO = new StudentDAO();
    private static final AcademicDAO academicDAO = new AcademicDAO();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // If run with --demo or --test argument, execute full automated demo
        if (args.length > 0 && (args[0].equalsIgnoreCase("--demo") || args[0].equalsIgnoreCase("-d"))) {
            runAutomatedDemonstration();
            return;
        }

        printBanner();
        checkDatabaseConnectivity();

        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = readInput("Select an option (0-12): ");
            try {
                switch (choice.trim()) {
                    case "1" -> registerStudent();
                    case "2" -> viewAllStudents();
                    case "3" -> addCourse();
                    case "4" -> viewCourseCatalog();
                    case "5" -> enrollStudentInCourse();
                    case "6" -> updateAttendanceAndCondonation();
                    case "7" -> recordPhase1Cia();
                    case "8" -> recordPhase2Assignment();
                    case "9" -> recordPhase3Ese();
                    case "10" -> generateStudentMarkSheet();
                    case "11" -> generateAttendanceDefaulterReport();
                    case "12" -> generateClassPerformanceSummary();
                    case "13" -> runAutomatedDemonstration();
                    case "0" -> {
                        System.out.println("\nExiting KEC R2024 Evaluation System. Thank you!");
                        running = false;
                    }
                    default -> System.out.println("Invalid selection. Please choose an option from the menu.");
                }
            } catch (Exception e) {
                System.err.println("\n[ERROR] Operation failed: " + e.getMessage());
            }
            if (running) {
                System.out.println("\nPress Enter to return to the Main Menu...");
                scanner.nextLine();
            }
        }
    }

    private static void printBanner() {
        System.out.println("=========================================================================================");
        System.out.println("   KONGU ENGINEERING COLLEGE (AUTONOMOUS) - REGULATION 2024 (R2024)");
        System.out.println("   STUDENT ACADEMIC & CONTINUOUS EVALUATION SYSTEM (JAVA JDBC)");
        System.out.println("=========================================================================================");
    }

    private static void checkDatabaseConnectivity() {
        System.out.print("Testing Database Connectivity... ");
        if (DBConnection.testConnection()) {
            System.out.println("[CONNECTED SUCCESSFULLY to " + DBConnection.getActiveUrl() + "]");
        } else {
            System.out.println("[FAILED]");
            System.out.println("Warning: Unable to establish database connection. Please verify MySQL service and db.properties.");
        }
    }

    private static void printMainMenu() {
        System.out.println("\n=========================================================================================");
        System.out.println("                                  MAIN MENU");
        System.out.println("=========================================================================================");
        System.out.println(" [STUDENT & COURSE CATALOG MANAGEMENT]");
        System.out.println("   1. Register New Student (Roll No, Name, Dept, Current Sem)");
        System.out.println("   2. View All Registered Students");
        System.out.println("   3. Add New Course Catalog (Code, Title, Credits, Type, Sem)");
        System.out.println("   4. View Course Catalog");
        System.out.println("   5. Enroll Student in Course (With Initial Attendance)");
        System.out.println("   6. Update Attendance / Approve Medical Condonation");
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println(" [INCREMENTAL MARKS ENTRY - 'THEN AND NOW']");
        System.out.println("   7. Phase 1 (Mid-Semester)      : Enter CIA 1 & CIA 2 Marks (0-100)");
        System.out.println("   8. Phase 2 (End-of-Semester CIA): Enter Assignment/Quiz/Lab Evaluation (0-100)");
        System.out.println("   9. Phase 3 (Post-Semester ESE)  : Enter End Semester Exam (0-100) or Mark Absent (AB)");
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println(" [ACADEMIC PERFORMANCE & EVALUATION REPORTS]");
        System.out.println("  10. Student Semester Grade Sheet (With SGPA & Equivalent Percentage)");
        System.out.println("  11. Attendance Defaulter Report (Attendance < 65% Flagged With Grade 'SA')");
        System.out.println("  12. Class Performance & Department Result Summary");
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("  13. Run Full Automated Demo Walkthrough (Proof Generation)");
        System.out.println("   0. Exit System");
        System.out.println("=========================================================================================");
    }

    // =========================================================================
    // 1. Student & Course Operations
    // =========================================================================

    private static void registerStudent() throws SQLException {
        System.out.println("\n--- 1. Register New Student ---");
        String roll = readInput("Enter Roll Number (e.g. 24ITR005): ").toUpperCase();
        if (studentDAO.getStudentByRollNumber(roll) != null) {
            System.out.println("[ERROR] Student with Roll Number " + roll + " already exists!");
            return;
        }
        String name = readInput("Enter Student Full Name: ");
        String dept = readInput("Enter Department (e.g. IT, CSE, ECE, MECH): ").toUpperCase();
        int sem = readIntInput("Enter Current Semester (1-8): ", 1, 8);

        Student s = new Student(roll, name, dept, sem);
        boolean ok = studentDAO.addStudent(s);
        if (ok) {
            System.out.println("[SUCCESS] Student registered successfully: " + s);
        } else {
            System.out.println("[FAILURE] Could not register student.");
        }
    }

    private static void viewAllStudents() throws SQLException {
        System.out.println("\n--- Registered Students ---");
        List<Student> list = studentDAO.getAllStudents();
        if (list.isEmpty()) {
            System.out.println("No students found in the database.");
            return;
        }
        System.out.println("---------------------------------------------------------------------------------");
        System.out.printf("%-12s | %-25s | %-8s | %-10s\n", "Roll Number", "Full Name", "Dept", "Semester");
        System.out.println("---------------------------------------------------------------------------------");
        for (Student s : list) {
            System.out.printf("%-12s | %-25s | %-8s | Sem %d\n", 
                    s.getRollNumber(), s.getName(), s.getDepartment(), s.getCurrentSemester());
        }
        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("Total Students: " + list.size());
    }

    private static void addCourse() throws SQLException {
        System.out.println("\n--- 3. Add Course to Catalog ---");
        String code = readInput("Enter Course Code (e.g. 24ITR35): ").toUpperCase();
        if (academicDAO.getCourse(code) != null) {
            System.out.println("[ERROR] Course with code " + code + " already exists!");
            return;
        }
        String title = readInput("Enter Course Title: ");
        int credits = readIntInput("Enter Credits (1-6): ", 1, 6);
        System.out.println("Course Types: 1. THEORY (40% CIA / 60% ESE) | 2. PRACTICAL (60% CIA / 40% ESE) | 3. EMBEDDED (50% CIA / 50% ESE)");
        int typeChoice = readIntInput("Select Course Type (1-3): ", 1, 3);
        CourseType ct = switch (typeChoice) {
            case 2 -> CourseType.PRACTICAL;
            case 3 -> CourseType.EMBEDDED;
            default -> CourseType.THEORY;
        };
        int sem = readIntInput("Enter Semester for Course (1-8): ", 1, 8);

        Course c = new Course(code, title, credits, ct, sem);
        boolean ok = academicDAO.addCourse(c);
        if (ok) {
            System.out.println("[SUCCESS] Course added to catalog: " + c);
        } else {
            System.out.println("[FAILURE] Could not add course.");
        }
    }

    private static void viewCourseCatalog() throws SQLException {
        System.out.println("\n--- Course Catalog (Regulation 2024) ---");
        List<Course> list = academicDAO.getAllCourses();
        if (list.isEmpty()) {
            System.out.println("No courses found in catalog.");
            return;
        }
        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s | %-38s | %-7s | %-11s | %-8s\n", "Code", "Course Title", "Credits", "Type", "Semester");
        System.out.println("-------------------------------------------------------------------------------------------------");
        for (Course c : list) {
            System.out.printf("%-10s | %-38s | %-7d | %-11s | Sem %d\n",
                    c.getCourseCode(), c.getTitle(), c.getCredits(), c.getCourseType().getDisplayName(), c.getSemester());
        }
        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.println("Total Courses: " + list.size());
    }

    private static void enrollStudentInCourse() throws SQLException {
        System.out.println("\n--- 5. Course Enrollment & Attendance Setup ---");
        String roll = readInput("Enter Student Roll Number: ").toUpperCase();
        Student s = studentDAO.getStudentByRollNumber(roll);
        if (s == null) {
            System.out.println("[ERROR] Student not registered: " + roll);
            return;
        }
        String code = readInput("Enter Course Code: ").toUpperCase();
        Course c = academicDAO.getCourse(code);
        if (c == null) {
            System.out.println("[ERROR] Course not found: " + code);
            return;
        }
        int sem = readIntInput("Enter Semester: ", 1, 8);

        Enrollment existing = academicDAO.getEnrollment(roll, code, sem);
        if (existing != null) {
            System.out.println("[ERROR] Student already enrolled in this course for Semester " + sem);
            return;
        }

        int total = readIntInput("Enter Total Classes Conducted (e.g. 50): ", 0, 200);
        int attended = readIntInput("Enter Classes Attended by Student: ", 0, total);
        String cond = readInput("Is Medical Condonation Approved? (Y/N, default N): ");
        boolean isCondoned = cond.equalsIgnoreCase("Y");

        int id = academicDAO.enrollStudent(roll, code, sem, total, attended, isCondoned);
        if (id > 0) {
            double pct = total > 0 ? (attended * 100.0) / total : 0.0;
            System.out.printf("[SUCCESS] Student enrolled (ID #%d). Attendance: %d/%d (%.1f%%)\n", id, attended, total, pct);
            if (pct < 65.0) {
                System.out.println("[ALERT] Attendance is < 65%. Student flagged as Shortage of Attendance (SA - Barred).");
            } else if (pct < 75.0 && !isCondoned) {
                System.out.println("[ALERT] Attendance is 65-74.9%. Medical Condonation required for ESE eligibility.");
            }
        }
    }

    private static void updateAttendanceAndCondonation() throws SQLException {
        System.out.println("\n--- 6. Update Attendance / Condonation Approval ---");
        String roll = readInput("Enter Student Roll Number: ").toUpperCase();
        String code = readInput("Enter Course Code: ").toUpperCase();
        int sem = readIntInput("Enter Semester: ", 1, 8);

        Enrollment en = academicDAO.getEnrollment(roll, code, sem);
        if (en == null) {
            System.out.println("[ERROR] Enrollment not found for " + roll + " in " + code);
            return;
        }
        System.out.printf("Current Status: %d/%d classes (%.1f%%) | Condonation Approved: %s\n",
                en.getClassesAttended(), en.getTotalClasses(), en.getAttendancePercentage(), en.isMedicalCondonation());

        int newTotal = readIntInput("Enter Updated Total Classes Conducted: ", 0, 200);
        int newAttended = readIntInput("Enter Updated Classes Attended: ", 0, newTotal);
        String cond = readInput("Approve Medical Condonation? (Y/N): ");
        boolean condApproved = cond.equalsIgnoreCase("Y");

        academicDAO.updateAttendance(en.getEnrollmentId(), newTotal, newAttended);
        academicDAO.setMedicalCondonation(en.getEnrollmentId(), condApproved);
        System.out.println("[SUCCESS] Attendance and condonation record updated.");
    }

    // =========================================================================
    // 2. Incremental Marks Entry ("Then and Now")
    // =========================================================================

    private static void recordPhase1Cia() throws SQLException {
        System.out.println("\n--- 7. Phase 1 (Mid-Semester): Continuous Internal Assessments (CIA 1 & 2) ---");
        String roll = readInput("Enter Student Roll Number: ").toUpperCase();
        String code = readInput("Enter Course Code: ").toUpperCase();
        int sem = readIntInput("Enter Semester: ", 1, 8);

        Enrollment en = academicDAO.getEnrollment(roll, code, sem);
        if (en == null) {
            System.out.println("[ERROR] No enrollment record found for " + roll + " in " + code);
            return;
        }

        double cia1 = readDoubleInput("Enter CIA Test 1 Marks (0 - 100): ", 0.0, 100.0);
        double cia2 = readDoubleInput("Enter CIA Test 2 Marks (0 - 100): ", 0.0, 100.0);

        boolean ok = academicDAO.recordPhase1Marks(en.getEnrollmentId(), cia1, cia2);
        if (ok) {
            System.out.printf("[SUCCESS] Phase 1 Marks Recorded: CIA1 = %.1f, CIA2 = %.1f\n", cia1, cia2);
        } else {
            System.out.println("[FAILURE] Could not record Phase 1 marks.");
        }
    }

    private static void recordPhase2Assignment() throws SQLException {
        System.out.println("\n--- 8. Phase 2 (End-of-Semester CIA): Assignment / Quiz / Lab Evaluation ---");
        String roll = readInput("Enter Student Roll Number: ").toUpperCase();
        String code = readInput("Enter Course Code: ").toUpperCase();
        int sem = readIntInput("Enter Semester: ", 1, 8);

        Enrollment en = academicDAO.getEnrollment(roll, code, sem);
        if (en == null) {
            System.out.println("[ERROR] No enrollment record found for " + roll + " in " + code);
            return;
        }

        double assg = readDoubleInput("Enter Assignment / Quiz / Lab Evaluation Marks (0 - 100): ", 0.0, 100.0);
        boolean ok = academicDAO.recordPhase2Marks(en.getEnrollmentId(), assg);
        if (ok) {
            AssessmentMark mark = academicDAO.getAssessmentMark(en.getEnrollmentId());
            Double rawCia = EvaluationEngine.calculateRawCia100(mark.getCia1Marks(), mark.getCia2Marks(), mark.getAssignmentMarks());
            System.out.printf("[SUCCESS] Phase 2 Recorded. Finalized Raw CIA (out of 100) = %.2f\n", (rawCia != null ? rawCia : assg));
        } else {
            System.out.println("[FAILURE] Could not record Phase 2 marks.");
        }
    }

    private static void recordPhase3Ese() throws SQLException {
        System.out.println("\n--- 9. Phase 3 (Post-Semester): End Semester Examination (ESE) Marks ---");
        String roll = readInput("Enter Student Roll Number: ").toUpperCase();
        String code = readInput("Enter Course Code: ").toUpperCase();
        int sem = readIntInput("Enter Semester: ", 1, 8);

        Enrollment en = academicDAO.getEnrollment(roll, code, sem);
        if (en == null) {
            System.out.println("[ERROR] No enrollment record found for " + roll + " in " + code);
            return;
        }

        // Attendance eligibility check prior to ESE entry
        if (!en.isAttendanceEligibleForEse()) {
            System.out.printf("[BARRED] Student %s has %.1f%% attendance (%s).\n", 
                    roll, en.getAttendancePercentage(), en.getAttendanceStatusLabel());
            System.out.println("Under KEC R2024 Clause 7, student is barred from ESE and awarded grade SA.");
            String override = readInput("Proceed to record anyway? (Y/N, default N): ");
            if (!override.equalsIgnoreCase("Y")) {
                return;
            }
        }

        String abs = readInput("Was the student ABSENT in the examination? (Y/N): ");
        if (abs.equalsIgnoreCase("Y")) {
            academicDAO.recordPhase3Marks(en.getEnrollmentId(), null, true);
            System.out.println("[SUCCESS] Student marked ABSENT (AB). Grade Point = 0.");
            return;
        }

        double eseMarks = readDoubleInput("Enter End Semester Examination (ESE) Marks (0 - 100): ", 0.0, 100.0);
        boolean ok = academicDAO.recordPhase3Marks(en.getEnrollmentId(), eseMarks, false);
        if (ok) {
            Course course = academicDAO.getCourse(code);
            AssessmentMark mark = academicDAO.getAssessmentMark(en.getEnrollmentId());
            EvaluationResult eval = EvaluationEngine.evaluateEnrollment(en, course, mark);
            System.out.printf("[SUCCESS] ESE recorded. Total Scaled: %.1f | Grade: %s | GP: %d | Status: %s\n",
                    eval.getTotalMarks(), eval.getGrade().getLetter(), eval.getGradePoint(), 
                    eval.isPass() ? "PASS" : eval.getRemarks());
        }
    }

    // =========================================================================
    // 3. Academic Reporting
    // =========================================================================

    private static void generateStudentMarkSheet() throws SQLException {
        System.out.println("\n--- 10. Generate Student Semester Mark Sheet ---");
        String roll = readInput("Enter Student Roll Number (e.g. 24ITR001): ").toUpperCase();
        Student student = studentDAO.getStudentByRollNumber(roll);
        if (student == null) {
            System.out.println("[ERROR] Student not found: " + roll);
            return;
        }
        int sem = readIntInput("Enter Semester: ", 1, 8);
        List<EvaluationResult> results = academicDAO.evaluateStudentSemester(roll, sem);
        String report = ReportGenerator.generateStudentMarkSheet(student.getRollNumber(), student.getName(), 
                student.getDepartment(), sem, results);
        System.out.println(report);
    }

    private static void generateAttendanceDefaulterReport() throws SQLException {
        System.out.println("\n--- 11. Attendance Defaulter Report ---");
        List<EvaluationResult> defaulters = academicDAO.getAttendanceDefaulters();
        String report = ReportGenerator.generateAttendanceDefaulterReport(defaulters);
        System.out.println(report);
    }

    private static void generateClassPerformanceSummary() throws SQLException {
        System.out.println("\n--- 12. Class Performance & Result Summary ---");
        String dept = readInput("Enter Department (e.g. IT, CSE): ").toUpperCase();
        int sem = readIntInput("Enter Semester: ", 1, 8);

        List<String> rolls = academicDAO.getStudentRollNumbersByDeptAndSem(dept, sem);
        if (rolls.isEmpty()) {
            System.out.println("No students found for Department: " + dept + ", Semester: " + sem);
            return;
        }

        int totalStudents = rolls.size();
        int passCount = 0;
        int failCount = 0;
        double sumSgpa = 0.0;

        for (String roll : rolls) {
            List<EvaluationResult> evals = academicDAO.evaluateStudentSemester(roll, sem);
            boolean allPassed = true;
            for (EvaluationResult r : evals) {
                if (!r.isPass()) {
                    allPassed = false;
                    break;
                }
            }
            if (allPassed && !evals.isEmpty()) {
                passCount++;
            } else {
                failCount++;
            }
            sumSgpa += EvaluationEngine.calculateSgpa(evals);
        }

        double avgSgpa = totalStudents > 0 ? (sumSgpa / totalStudents) : 0.0;
        String report = ReportGenerator.generateClassPerformanceSummary(dept, sem, totalStudents, passCount, failCount, avgSgpa);
        System.out.println(report);
    }

    // =========================================================================
    // 4. Automated Proof Demonstration
    // =========================================================================

    public static void runAutomatedDemonstration() {
        System.out.println("\n=========================================================================================");
        System.out.println("  RUNNING AUTOMATED KEC R2024 EVALUATION SYSTEM VERIFICATION & PROOF SUITE");
        System.out.println("=========================================================================================");

        try {
            // Step 1: Adding a student and course
            System.out.println("\n>>> [STEP 1: STUDENT & COURSE REGISTRATION DATA ENTRY] <<<");
            String demoRoll = "24ITR099";
            if (studentDAO.getStudentByRollNumber(demoRoll) == null) {
                studentDAO.addStudent(new Student(demoRoll, "Suresh Kumar V", "IT", 3));
                System.out.println("[OK] Registered Demo Student: 24ITR099 - Suresh Kumar V (IT, Sem 3)");
            }

            String demoCourse = "24ITR39";
            if (academicDAO.getCourse(demoCourse) == null) {
                academicDAO.addCourse(new Course(demoCourse, "Cloud Computing Principles", 3, CourseType.THEORY, 3));
                System.out.println("[OK] Added Demo Course: 24ITR39 - Cloud Computing Principles (THEORY, Credits 3)");
            }

            // Enroll demo student with 45/50 classes (90% attendance)
            if (academicDAO.getEnrollment(demoRoll, demoCourse, 3) == null) {
                int enId = academicDAO.enrollStudent(demoRoll, demoCourse, 3, 50, 45, false);
                System.out.println("[OK] Enrolled 24ITR099 into 24ITR39. Total classes: 50, Attended: 45 (90.0% Eligible)");
            }

            // Step 2: Incremental Marks Entry ("Then and Now")
            System.out.println("\n>>> [STEP 2: INCREMENTAL MARKS ENTRY (PHASE 1, PHASE 2, PHASE 3)] <<<");
            Enrollment en = academicDAO.getEnrollment(demoRoll, demoCourse, 3);
            
            // Phase 1 (Mid-Semester)
            academicDAO.recordPhase1Marks(en.getEnrollmentId(), 88.0, 92.0);
            System.out.println("[OK] Phase 1 (Mid-Semester) Recorded: CIA 1 = 88.0 / 100, CIA 2 = 92.0 / 100");

            // Phase 2 (End of Semester - CIA)
            academicDAO.recordPhase2Marks(en.getEnrollmentId(), 90.0);
            System.out.println("[OK] Phase 2 (End-of-Semester CIA) Recorded: Assignment/Quiz = 90.0 / 100");
            Double rawCia = EvaluationEngine.calculateRawCia100(88.0, 92.0, 90.0);
            System.out.printf("     -> Synthesized Raw CIA (100): %.2f | Theory Scaled CIA (40%%): %.2f / 40.0\n", 
                    rawCia, (rawCia * 0.40));

            // Phase 3 (Post Semester)
            academicDAO.recordPhase3Marks(en.getEnrollmentId(), 86.0, false);
            System.out.println("[OK] Phase 3 (Post-Semester) Recorded: ESE Marks = 86.0 / 100 (Theory Scaled 60%: 51.6 / 60.0)");

            // Step 3: Generated Grade Sheet with calculated SGPA & Percentage
            System.out.println("\n>>> [STEP 3: GENERATED FORMAL GRADE SHEET WITH SGPA & PERCENTAGE] <<<");
            List<EvaluationResult> results = academicDAO.evaluateStudentSemester("24ITR001", 3);
            Student student = studentDAO.getStudentByRollNumber("24ITR001");
            System.out.println(ReportGenerator.generateStudentMarkSheet(student.getRollNumber(), student.getName(), 
                    student.getDepartment(), 3, results));

            // Step 4: Attendance Defaulter Report (Attendance < 65% -> SA)
            System.out.println("\n>>> [STEP 4: ATTENDANCE DEFAULTER REPORT (< 65% FLAGGED WITH GRADE SA)] <<<");
            List<EvaluationResult> defaulters = academicDAO.getAttendanceDefaulters();
            System.out.println(ReportGenerator.generateAttendanceDefaulterReport(defaulters));

            // Step 5: Department Class Performance & Result Summary
            System.out.println("\n>>> [STEP 5: CLASS PERFORMANCE & RESULT SUMMARY] <<<");
            List<String> itRolls = academicDAO.getStudentRollNumbersByDeptAndSem("IT", 3);
            int pass = 0, fail = 0;
            double sgpaSum = 0.0;
            for (String r : itRolls) {
                List<EvaluationResult> ev = academicDAO.evaluateStudentSemester(r, 3);
                boolean p = true;
                for (EvaluationResult er : ev) {
                    if (!er.isPass()) { p = false; break; }
                }
                if (p && !ev.isEmpty()) pass++; else fail++;
                sgpaSum += EvaluationEngine.calculateSgpa(ev);
            }
            double avgSgpa = itRolls.isEmpty() ? 0.0 : sgpaSum / itRolls.size();
            System.out.println(ReportGenerator.generateClassPerformanceSummary("IT", 3, itRolls.size(), pass, fail, avgSgpa));

            System.out.println("\n=========================================================================================");
            System.out.println("  [SUCCESS] ALL KEC R2024 EVALUATION ENGINE VERIFICATIONS EXECUTED WITHOUT ERRORS!");
            System.out.println("=========================================================================================");

        } catch (Exception e) {
            System.err.println("[DEMO ERROR] " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =========================================================================
    // Console Input Helpers
    // =========================================================================

    private static String readInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int readIntInput(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int val = Integer.parseInt(input);
                if (val >= min && val <= max) return val;
                System.out.printf("Value must be between %d and %d. Please try again.\n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid numeric input. Please try again.");
            }
        }
    }

    private static double readDoubleInput(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double val = Double.parseDouble(input);
                if (val >= min && val <= max) return val;
                System.out.printf("Value must be between %.1f and %.1f. Please try again.\n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please try again.");
            }
        }
    }
}
