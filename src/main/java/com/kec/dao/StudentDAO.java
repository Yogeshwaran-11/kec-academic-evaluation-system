package com.kec.dao;

import com.kec.db.DBConnection;
import com.kec.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * StudentDAO - Data Access Object for Student CRUD operations.
 */
public class StudentDAO {

    /**
     * Registers a new student record.
     */
    public boolean addStudent(Student student) throws SQLException {
        String sql = "INSERT INTO students (roll_number, name, department, current_semester) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getRollNumber().trim().toUpperCase());
            ps.setString(2, student.getName().trim());
            ps.setString(3, student.getDepartment().trim().toUpperCase());
            ps.setInt(4, student.getCurrentSemester());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves student by roll number.
     */
    public Student getStudentByRollNumber(String rollNumber) throws SQLException {
        String sql = "SELECT roll_number, name, department, current_semester, created_at FROM students WHERE roll_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNumber.trim().toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Student s = new Student();
                    s.setRollNumber(rs.getString("roll_number"));
                    s.setName(rs.getString("name"));
                    s.setDepartment(rs.getString("department"));
                    s.setCurrentSemester(rs.getInt("current_semester"));
                    s.setCreatedAt(rs.getTimestamp("created_at"));
                    return s;
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all registered students.
     */
    public List<Student> getAllStudents() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT roll_number, name, department, current_semester, created_at FROM students ORDER BY roll_number ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Student s = new Student();
                s.setRollNumber(rs.getString("roll_number"));
                s.setName(rs.getString("name"));
                s.setDepartment(rs.getString("department"));
                s.setCurrentSemester(rs.getInt("current_semester"));
                s.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(s);
            }
        }
        return list;
    }

    /**
     * Retrieves students enrolled in a specific department.
     */
    public List<Student> getStudentsByDepartment(String dept) throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT roll_number, name, department, current_semester, created_at FROM students WHERE department = ? ORDER BY roll_number ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dept.trim().toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Student s = new Student();
                    s.setRollNumber(rs.getString("roll_number"));
                    s.setName(rs.getString("name"));
                    s.setDepartment(rs.getString("department"));
                    s.setCurrentSemester(rs.getInt("current_semester"));
                    s.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(s);
                }
            }
        }
        return list;
    }

    /**
     * Updates an existing student record.
     */
    public boolean updateStudent(Student student) throws SQLException {
        String sql = "UPDATE students SET name = ?, department = ?, current_semester = ? WHERE roll_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getName().trim());
            ps.setString(2, student.getDepartment().trim().toUpperCase());
            ps.setInt(3, student.getCurrentSemester());
            ps.setString(4, student.getRollNumber().trim().toUpperCase());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Deletes student by roll number (cascades to enrollments and marks).
     */
    public boolean deleteStudent(String rollNumber) throws SQLException {
        String sql = "DELETE FROM students WHERE roll_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNumber.trim().toUpperCase());
            return ps.executeUpdate() > 0;
        }
    }
}
