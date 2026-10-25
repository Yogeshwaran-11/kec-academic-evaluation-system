-- ============================================================================
-- Kongu Engineering College (Autonomous) - Regulation 2024 (R2024)
-- Student Academic & Continuous Evaluation System
-- Database Schema Definition: kec_schema.sql
-- ============================================================================

CREATE DATABASE IF NOT EXISTS kec_eval_db;
USE kec_eval_db;

-- Drop tables if existing (in reverse dependency order)
DROP TABLE IF EXISTS assessment_marks;
DROP TABLE IF EXISTS enrollments;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS students;

-- ----------------------------------------------------------------------------
-- 1. Students Table
-- Stores student profile information (Roll Number, Name, Department, Semester)
-- ----------------------------------------------------------------------------
CREATE TABLE students (
    roll_number VARCHAR(20) NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(50) NOT NULL,
    current_semester INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_semester CHECK (current_semester BETWEEN 1 AND 8)
);

-- ----------------------------------------------------------------------------
-- 2. Courses Table (Course Catalog)
-- Course types: THEORY, PRACTICAL, EMBEDDED
-- ----------------------------------------------------------------------------
CREATE TABLE courses (
    course_code VARCHAR(20) NOT NULL PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    credits INT NOT NULL,
    course_type ENUM('THEORY', 'PRACTICAL', 'EMBEDDED') NOT NULL,
    semester INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_credits CHECK (credits > 0),
    CONSTRAINT chk_course_semester CHECK (semester BETWEEN 1 AND 8)
);

-- ----------------------------------------------------------------------------
-- 3. Enrollments Table (Course Enrollment & Attendance Tracking)
-- Tracks total classes held vs. classes attended and medical condonation approval
-- ----------------------------------------------------------------------------
CREATE TABLE enrollments (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    roll_number VARCHAR(20) NOT NULL,
    course_code VARCHAR(20) NOT NULL,
    semester INT NOT NULL,
    total_classes INT NOT NULL DEFAULT 0,
    classes_attended INT NOT NULL DEFAULT 0,
    medical_condonation BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_enrollment_student FOREIGN KEY (roll_number) 
        REFERENCES students(roll_number) ON DELETE CASCADE,
    CONSTRAINT fk_enrollment_course FOREIGN KEY (course_code) 
        REFERENCES courses(course_code) ON DELETE CASCADE,
    CONSTRAINT uk_student_course_sem UNIQUE (roll_number, course_code, semester),
    CONSTRAINT chk_attendance_values CHECK (classes_attended >= 0 AND total_classes >= 0 AND classes_attended <= total_classes)
);

-- ----------------------------------------------------------------------------
-- 4. Assessment Marks Table (Phased Incremental Entry)
-- Phase 1 (Mid-Sem): CIA 1 & CIA 2 (0-100)
-- Phase 2 (End CIA): Assignments / Quizzes / Lab Evaluations (0-100)
-- Phase 3 (Post-Sem): End Semester Examination (ESE) (0-100) and Absenteeism (AB)
-- ----------------------------------------------------------------------------
CREATE TABLE assessment_marks (
    mark_id INT AUTO_INCREMENT PRIMARY KEY,
    enrollment_id INT NOT NULL UNIQUE,
    cia1_marks DECIMAL(5, 2) NULL,
    cia2_marks DECIMAL(5, 2) NULL,
    assignment_marks DECIMAL(5, 2) NULL,
    ese_marks DECIMAL(5, 2) NULL,
    is_ese_absent BOOLEAN NOT NULL DEFAULT FALSE,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_marks_enrollment FOREIGN KEY (enrollment_id) 
        REFERENCES enrollments(enrollment_id) ON DELETE CASCADE,
    CONSTRAINT chk_cia1 CHECK (cia1_marks IS NULL OR (cia1_marks >= 0 AND cia1_marks <= 100)),
    CONSTRAINT chk_cia2 CHECK (cia2_marks IS NULL OR (cia2_marks >= 0 AND cia2_marks <= 100)),
    CONSTRAINT chk_assignment CHECK (assignment_marks IS NULL OR (assignment_marks >= 0 AND assignment_marks <= 100)),
    CONSTRAINT chk_ese CHECK (ese_marks IS NULL OR (ese_marks >= 0 AND ese_marks <= 100))
);

-- ----------------------------------------------------------------------------
-- Indexes for High Performance Queries
-- ----------------------------------------------------------------------------
CREATE INDEX idx_student_dept ON students(department);
CREATE INDEX idx_enrollment_roll ON enrollments(roll_number);
CREATE INDEX idx_enrollment_course ON enrollments(course_code);
CREATE INDEX idx_enrollment_sem ON enrollments(semester);

-- ============================================================================
-- Seed Sample Data (Kongu Engineering College - Regulation 2024 Demo Data)
-- ============================================================================

-- Courses
INSERT INTO courses (course_code, title, credits, course_type, semester) VALUES
('24ITR31', 'Database Design & Management', 3, 'THEORY', 3),
('24ITR32', 'Object Oriented Programming with Java', 4, 'EMBEDDED', 3),
('24ITR33', 'Data Structures & Algorithms Laboratory', 2, 'PRACTICAL', 3),
('24ITR34', 'Operating Systems & Architecture', 3, 'THEORY', 3);

-- Students
INSERT INTO students (roll_number, name, department, current_semester) VALUES
('24ITR001', 'Arun Kumar S', 'IT', 3),
('24ITR002', 'Bhavani M', 'IT', 3),
('24ITR003', 'Chandran K', 'IT', 3),
('24ITR004', 'Deepa R', 'IT', 3),
('24CSR015', 'Karthik N', 'CSE', 3);

-- Enrollments & Attendance
-- 24ITR001: High attendance (48/50 = 96%)
INSERT INTO enrollments (roll_number, course_code, semester, total_classes, classes_attended, medical_condonation) VALUES
('24ITR001', '24ITR31', 3, 50, 48, FALSE),
('24ITR001', '24ITR32', 3, 60, 58, FALSE),
('24ITR001', '24ITR33', 3, 30, 29, FALSE),
('24ITR001', '24ITR34', 3, 50, 47, FALSE);

-- 24ITR002: Borderline condonation case (35/50 = 70%, condonation approved)
INSERT INTO enrollments (roll_number, course_code, semester, total_classes, classes_attended, medical_condonation) VALUES
('24ITR002', '24ITR31', 3, 50, 35, TRUE),
('24ITR002', '24ITR32', 3, 60, 43, TRUE);

-- 24ITR003: Shortage of Attendance (SA defaulter) (25/50 = 50% < 65%)
INSERT INTO enrollments (roll_number, course_code, semester, total_classes, classes_attended, medical_condonation) VALUES
('24ITR003', '24ITR31', 3, 50, 25, FALSE),
('24ITR003', '24ITR32', 3, 60, 28, FALSE);

-- 24ITR004: Absenteeism in ESE demo (45/50 = 90% attendance)
INSERT INTO enrollments (roll_number, course_code, semester, total_classes, classes_attended, medical_condonation) VALUES
('24ITR004', '24ITR31', 3, 50, 45, FALSE);

-- Marks: Phase 1, Phase 2, and Phase 3
-- 24ITR001 (Enrollment 1 - Theory): CIA1=85, CIA2=90, Assg=90, ESE=88 (High Pass -> Grade A+/O)
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_ese_absent) VALUES
(1, 85.00, 90.00, 90.00, 88.00, FALSE);

-- 24ITR001 (Enrollment 2 - Embedded): CIA1=80, CIA2=82, Assg=85, ESE=78 (Grade A)
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_ese_absent) VALUES
(2, 80.00, 82.00, 85.00, 78.00, FALSE);

-- 24ITR001 (Enrollment 3 - Practical): CIA1=92, CIA2=94, Assg=95, ESE=90 (Grade O)
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_ese_absent) VALUES
(3, 92.00, 94.00, 95.00, 90.00, FALSE);

-- 24ITR001 (Enrollment 4 - Theory): CIA1=70, CIA2=75, Assg=80, ESE=72 (Grade A)
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_ese_absent) VALUES
(4, 70.00, 75.00, 80.00, 72.00, FALSE);

-- 24ITR002 (Enrollment 5 - Theory): Condoned -> CIA1=60, CIA2=65, Assg=70, ESE=55 (Grade B/B+)
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_ese_absent) VALUES
(5, 60.00, 65.00, 70.00, 55.00, FALSE);

-- 24ITR003 (Enrollment 7 - Theory): SA Defaulter (Marks entered or not, barred from ESE -> SA)
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_ese_absent) VALUES
(7, 40.00, 45.00, 50.00, NULL, FALSE);

-- 24ITR004 (Enrollment 9 - Theory): Absent in ESE -> AB
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_ese_absent) VALUES
(9, 75.00, 78.00, 80.00, NULL, TRUE);
