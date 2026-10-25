package com.kec.dao;

import com.kec.db.DBConnection;
import com.kec.model.*;
import com.kec.service.EvaluationEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AcademicDAO - Data Access Object for Courses, Enrollments,
 * Attendance tracking, Phased Marks Entry, and KEC R2024 Evaluation Engine queries.
 */
public class AcademicDAO {

    // =========================================================================
    // 1. Course Management
    // =========================================================================

    public boolean addCourse(Course course) throws SQLException {
        String sql = "INSERT INTO courses (course_code, title, credits, course_type, semester) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, course.getCourseCode().trim().toUpperCase());
            ps.setString(2, course.getTitle().trim());
            ps.setInt(3, course.getCredits());
            ps.setString(4, course.getCourseType().name());
            ps.setInt(5, course.getSemester());
            return ps.executeUpdate() > 0;
        }
    }

    public Course getCourse(String courseCode) throws SQLException {
        String sql = "SELECT course_code, title, credits, course_type, semester, created_at FROM courses WHERE course_code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseCode.trim().toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Course c = new Course();
                    c.setCourseCode(rs.getString("course_code"));
                    c.setTitle(rs.getString("title"));
                    c.setCredits(rs.getInt("credits"));
                    c.setCourseType(CourseType.fromString(rs.getString("course_type")));
                    c.setSemester(rs.getInt("semester"));
                    c.setCreatedAt(rs.getTimestamp("created_at"));
                    return c;
                }
            }
        }
        return null;
    }

    public List<Course> getAllCourses() throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT course_code, title, credits, course_type, semester, created_at FROM courses ORDER BY semester, course_code";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Course c = new Course();
                c.setCourseCode(rs.getString("course_code"));
                c.setTitle(rs.getString("title"));
                c.setCredits(rs.getInt("credits"));
                c.setCourseType(CourseType.fromString(rs.getString("course_type")));
                c.setSemester(rs.getInt("semester"));
                c.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(c);
            }
        }
        return list;
    }

    // =========================================================================
    // 2. Course Enrollment & Attendance Tracking
    // =========================================================================

    public int enrollStudent(String rollNumber, String courseCode, int semester, 
                             int totalClasses, int classesAttended, boolean condonation) throws SQLException {
        String sql = "INSERT INTO enrollments (roll_number, course_code, semester, total_classes, classes_attended, medical_condonation) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, rollNumber.trim().toUpperCase());
            ps.setString(2, courseCode.trim().toUpperCase());
            ps.setInt(3, semester);
            ps.setInt(4, totalClasses);
            ps.setInt(5, classesAttended);
            ps.setBoolean(6, condonation);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public boolean updateAttendance(int enrollmentId, int totalClasses, int classesAttended) throws SQLException {
        String sql = "UPDATE enrollments SET total_classes = ?, classes_attended = ? WHERE enrollment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, totalClasses);
            ps.setInt(2, classesAttended);
            ps.setInt(3, enrollmentId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean setMedicalCondonation(int enrollmentId, boolean approved) throws SQLException {
        String sql = "UPDATE enrollments SET medical_condonation = ? WHERE enrollment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, approved);
            ps.setInt(2, enrollmentId);
            return ps.executeUpdate() > 0;
        }
    }

    public Enrollment getEnrollment(int enrollmentId) throws SQLException {
        String sql = "SELECT e.enrollment_id, e.roll_number, e.course_code, e.semester, e.total_classes, " +
                     "e.classes_attended, e.medical_condonation, s.name as student_name, " +
                     "c.title as course_title, c.credits, c.course_type " +
                     "FROM enrollments e " +
                     "JOIN students s ON e.roll_number = s.roll_number " +
                     "JOIN courses c ON e.course_code = c.course_code " +
                     "WHERE e.enrollment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapEnrollment(rs);
                }
            }
        }
        return null;
    }

    public Enrollment getEnrollment(String rollNumber, String courseCode, int semester) throws SQLException {
        String sql = "SELECT e.enrollment_id, e.roll_number, e.course_code, e.semester, e.total_classes, " +
                     "e.classes_attended, e.medical_condonation, s.name as student_name, " +
                     "c.title as course_title, c.credits, c.course_type " +
                     "FROM enrollments e " +
                     "JOIN students s ON e.roll_number = s.roll_number " +
                     "JOIN courses c ON e.course_code = c.course_code " +
                     "WHERE e.roll_number = ? AND e.course_code = ? AND e.semester = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNumber.trim().toUpperCase());
            ps.setString(2, courseCode.trim().toUpperCase());
            ps.setInt(3, semester);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapEnrollment(rs);
                }
            }
        }
        return null;
    }

    public List<Enrollment> getEnrollmentsForStudent(String rollNumber, int semester) throws SQLException {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.enrollment_id, e.roll_number, e.course_code, e.semester, e.total_classes, " +
                     "e.classes_attended, e.medical_condonation, s.name as student_name, " +
                     "c.title as course_title, c.credits, c.course_type " +
                     "FROM enrollments e " +
                     "JOIN students s ON e.roll_number = s.roll_number " +
                     "JOIN courses c ON e.course_code = c.course_code " +
                     "WHERE e.roll_number = ? AND e.semester = ? " +
                     "ORDER BY e.course_code";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNumber.trim().toUpperCase());
            ps.setInt(2, semester);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapEnrollment(rs));
                }
            }
        }
        return list;
    }

    public List<Enrollment> getAllEnrollments() throws SQLException {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.enrollment_id, e.roll_number, e.course_code, e.semester, e.total_classes, " +
                     "e.classes_attended, e.medical_condonation, s.name as student_name, " +
                     "c.title as course_title, c.credits, c.course_type " +
                     "FROM enrollments e " +
                     "JOIN students s ON e.roll_number = s.roll_number " +
                     "JOIN courses c ON e.course_code = c.course_code " +
                     "ORDER BY e.semester, e.roll_number, e.course_code";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapEnrollment(rs));
            }
        }
        return list;
    }

    private Enrollment mapEnrollment(ResultSet rs) throws SQLException {
        Enrollment e = new Enrollment();
        e.setEnrollmentId(rs.getInt("enrollment_id"));
        e.setRollNumber(rs.getString("roll_number"));
        e.setCourseCode(rs.getString("course_code"));
        e.setSemester(rs.getInt("semester"));
        e.setTotalClasses(rs.getInt("total_classes"));
        e.setClassesAttended(rs.getInt("classes_attended"));
        e.setMedicalCondonation(rs.getBoolean("medical_condonation"));
        e.setStudentName(rs.getString("student_name"));
        e.setCourseTitle(rs.getString("course_title"));
        e.setCourseCredits(rs.getInt("credits"));
        e.setCourseType(CourseType.fromString(rs.getString("course_type")));
        return e;
    }

    // =========================================================================
    // 3. Incremental Marks Entry ("Then and Now")
    // =========================================================================

    /**
     * Phase 1 (Mid-Semester): Record CIA Test 1 (0-100) and CIA Test 2 (0-100).
     */
    public boolean recordPhase1Marks(int enrollmentId, double cia1, double cia2) throws SQLException {
        String sql = "INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks) " +
                     "VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE cia1_marks = VALUES(cia1_marks), cia2_marks = VALUES(cia2_marks)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            ps.setDouble(2, cia1);
            ps.setDouble(3, cia2);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Phase 2 (End of Semester - CIA): Record Assignments / Quizzes / Lab Evaluations (0-100).
     */
    public boolean recordPhase2Marks(int enrollmentId, double assignment) throws SQLException {
        String sql = "INSERT INTO assessment_marks (enrollment_id, assignment_marks) " +
                     "VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE assignment_marks = VALUES(assignment_marks)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            ps.setDouble(2, assignment);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Phase 3 (Post Semester): Record End Semester Examination (ESE) Marks (0-100) or Absence (AB).
     */
    public boolean recordPhase3Marks(int enrollmentId, Double eseMarks, boolean isAbsent) throws SQLException {
        String sql = "INSERT INTO assessment_marks (enrollment_id, ese_marks, is_ese_absent) " +
                     "VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE ese_marks = VALUES(ese_marks), is_ese_absent = VALUES(is_ese_absent)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            if (isAbsent || eseMarks == null) {
                ps.setNull(2, Types.DECIMAL);
                ps.setBoolean(3, isAbsent);
            } else {
                ps.setDouble(2, eseMarks);
                ps.setBoolean(3, false);
            }
            return ps.executeUpdate() > 0;
        }
    }

    public AssessmentMark getAssessmentMark(int enrollmentId) throws SQLException {
        String sql = "SELECT mark_id, enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_ese_absent, updated_at " +
                     "FROM assessment_marks WHERE enrollment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AssessmentMark m = new AssessmentMark();
                    m.setMarkId(rs.getInt("mark_id"));
                    m.setEnrollmentId(rs.getInt("enrollment_id"));
                    m.setCia1Marks(rs.getObject("cia1_marks") != null ? rs.getDouble("cia1_marks") : null);
                    m.setCia2Marks(rs.getObject("cia2_marks") != null ? rs.getDouble("cia2_marks") : null);
                    m.setAssignmentMarks(rs.getObject("assignment_marks") != null ? rs.getDouble("assignment_marks") : null);
                    m.setEseMarks(rs.getObject("ese_marks") != null ? rs.getDouble("ese_marks") : null);
                    m.setEseAbsent(rs.getBoolean("is_ese_absent"));
                    m.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return m;
                }
            }
        }
        return null;
    }

    // =========================================================================
    // 4. KEC R2024 Evaluation Engine & Reporting Queries
    // =========================================================================

    /**
     * Evaluates all enrolled courses for a student in a specific semester.
     */
    public List<EvaluationResult> evaluateStudentSemester(String rollNumber, int semester) throws SQLException {
        List<EvaluationResult> results = new ArrayList<>();
        List<Enrollment> enrollments = getEnrollmentsForStudent(rollNumber, semester);

        for (Enrollment en : enrollments) {
            Course course = getCourse(en.getCourseCode());
            AssessmentMark mark = getAssessmentMark(en.getEnrollmentId());
            EvaluationResult eval = EvaluationEngine.evaluateEnrollment(en, course, mark);
            eval.setDepartment(getStudentDepartment(rollNumber));
            eval.setSemester(semester);
            results.add(eval);
        }
        return results;
    }

    /**
     * Attendance Defaulter Report: List all students with attendance < 65% flagged with grade SA.
     */
    public List<EvaluationResult> getAttendanceDefaulters() throws SQLException {
        List<EvaluationResult> defaulters = new ArrayList<>();
        String sql = "SELECT e.enrollment_id, e.roll_number, s.name as student_name, s.department, " +
                     "e.course_code, c.title as course_title, c.credits, c.course_type, e.semester, " +
                     "e.total_classes, e.classes_attended, e.medical_condonation " +
                     "FROM enrollments e " +
                     "JOIN students s ON e.roll_number = s.roll_number " +
                     "JOIN courses c ON e.course_code = c.course_code " +
                     "WHERE (e.classes_attended * 100.0 / e.total_classes) < 65.0 " +
                     "ORDER BY e.semester, s.department, e.roll_number";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                EvaluationResult r = new EvaluationResult();
                r.setRollNumber(rs.getString("roll_number"));
                r.setStudentName(rs.getString("student_name"));
                r.setDepartment(rs.getString("department"));
                r.setCourseCode(rs.getString("course_code"));
                r.setCourseTitle(rs.getString("course_title"));
                r.setCredits(rs.getInt("credits"));
                r.setCourseType(CourseType.fromString(rs.getString("course_type")));
                r.setSemester(rs.getInt("semester"));
                r.setTotalClasses(rs.getInt("total_classes"));
                r.setClassesAttended(rs.getInt("classes_attended"));
                r.setMedicalCondonation(rs.getBoolean("medical_condonation"));
                double pct = (r.getClassesAttended() * 100.0) / r.getTotalClasses();
                r.setAttendancePercentage(pct);
                r.setAttendanceEligibilityStatus("BARRED (SA < 65%)");
                r.setGrade(Grade.SA);
                r.setGradePoint(Grade.SA.getGradePoint());
                r.setPass(false);
                r.setRemarks("Shortage of Attendance. Barred from End Semester Examination.");
                defaulters.add(r);
            }
        }
        return defaulters;
    }

    /**
     * Retrieves all student roll numbers for a department and semester.
     */
    public List<String> getStudentRollNumbersByDeptAndSem(String dept, int sem) throws SQLException {
        List<String> rolls = new ArrayList<>();
        String sql = "SELECT DISTINCT roll_number FROM students WHERE department = ? AND current_semester = ? ORDER BY roll_number";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dept.trim().toUpperCase());
            ps.setInt(2, sem);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rolls.add(rs.getString("roll_number"));
                }
            }
        }
        return rolls;
    }

    public List<String> getAllDepartments() throws SQLException {
        List<String> depts = new ArrayList<>();
        String sql = "SELECT DISTINCT department FROM students ORDER BY department";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                depts.add(rs.getString("department"));
            }
        }
        return depts;
    }

    private String getStudentDepartment(String rollNumber) throws SQLException {
        String sql = "SELECT department FROM students WHERE roll_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("department");
            }
        }
        return "UNKNOWN";
    }
}
