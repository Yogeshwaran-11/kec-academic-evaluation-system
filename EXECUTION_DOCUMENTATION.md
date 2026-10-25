# Kongu Engineering College (Autonomous) - Regulation 2024 (R2024)
## Student Academic & Continuous Evaluation System using Java JDBC
### Comprehensive Execution Documentation & Verification Proofs

---

## Local Database Configuration

Copy `db.properties.example` to `db.properties` and set the values for your local MySQL instance. The local `db.properties` file is ignored by Git and should not be committed.

---

## 1. System Architecture & Regulation 2024 Compliance Summary

The application is built using **Java 21**, **JDBC (Java Database Connectivity)**, and **MySQL 26.10** under the autonomous academic framework of **Kongu Engineering College - Regulation 2024**.

### A. Evaluation Rules & Weightage Engine
| Course Type | CIA Weightage | ESE Weightage | ESE Minimum Threshold | Aggregate Pass Minimum |
| :--- | :---: | :---: | :---: | :---: |
| **THEORY** | **40%** | **60%** | **45% (27.0 / 60.0)** | **50.0 Marks** |
| **PRACTICAL** | **60%** | **40%** | **50% (20.0 / 40.0)** | **50.0 Marks** |
| **EMBEDDED** | **50%** | **50%** | **45% (22.5 / 50.0)** | **50.0 Marks** |

*Continuous Internal Assessment (CIA) Synthesis:*
$$\text{Raw CIA (100)} = (\text{CIA1} \times 0.40) + (\text{CIA2} \times 0.40) + (\text{Assignment/Lab} \times 0.20)$$

### B. Attendance Filtering Rules (Clause 7)
- **$\ge 75\%$ Attendance**: Fully Eligible for End Semester Examination (ESE).
- **$65\% \text{ to } 74.9\%$ Attendance**: Conditionally eligible. Requires **Medical Condonation** approval. Barred until approved.
- **$< 65\%$ Attendance**: Barred from appearing in ESE. Mandatory award of **`SA` (Shortage of Attendance)** grade with **0 Grade Points**.

### C. 10-Point Absolute Grading System
| Range of Marks | Letter Grade | Grade Point (GP) | Status |
| :--- | :---: | :---: | :--- |
| **$91 - 100$** | **O** | **10** | Outstanding |
| **$81 - 90$** | **A+** | **9** | Excellent |
| **$71 - 80$** | **A** | **8** | Very Good |
| **$61 - 70$** | **B+** | **7** | Good |
| **$50 - 60$** | **B** | **6** | Average / Pass |
| **$< 50$ (or failed ESE min)** | **RA** | **0** | Re-Appearance |
| **Attendance $< 65\%$** | **SA** | **0** | Shortage of Attendance |
| **Absent in Exam** | **AB** | **0** | Absent |

### D. SGPA & Percentage Formulas
$$\text{SGPA} = \frac{\sum (\text{Course Credits} \times \text{Grade Points Earned})}{\sum (\text{Total Enrolled Credits})}$$
$$\text{Equivalent Percentage} = \text{SGPA} \times 10$$

---

## 2. Terminal Execution Proofs

### Proof 1: Database Initialization & Table Verification (MySQL)
```sql
mysql> USE kec_eval_db;
Database changed

mysql> SHOW TABLES;
+-----------------------+
| Tables_in_kec_eval_db |
+-----------------------+
| assessment_marks      |
| courses               |
| enrollments           |
| students              |
+-----------------------+
4 rows in set (0.01 sec)

mysql> SELECT COUNT(*) AS total_students FROM students;
+----------------+
| total_students |
+----------------+
|              5 |
+----------------+
1 row in set (0.00 sec)

mysql> SELECT course_code, title, credits, course_type, semester FROM courses;
+-------------+-----------------------------------------+---------+-------------+----------+
| course_code | title                                   | credits | course_type | semester |
+-------------+-----------------------------------------+---------+-------------+----------+
| 24ITR31     | Database Design & Management            |       3 | THEORY      |        3 |
| 24ITR32     | Object Oriented Programming with Java   |       4 | EMBEDDED    |        3 |
| 24ITR33     | Data Structures & Algorithms Laboratory |       2 | PRACTICAL   |        3 |
| 24ITR34     | Operating Systems & Architecture        |       3 | THEORY      |        3 |
+-------------+-----------------------------------------+---------+-------------+----------+
4 rows in set (0.00 sec)
```

---

### Proof 2: Adding a Student, Course, and Course Enrollment
```text
=========================================================================================
                                  MAIN MENU
=========================================================================================
 [STUDENT & COURSE CATALOG MANAGEMENT]
   1. Register New Student (Roll No, Name, Dept, Current Sem)
   2. View All Registered Students
   3. Add New Course Catalog (Code, Title, Credits, Type, Sem)
   4. View Course Catalog
   5. Enroll Student in Course (With Initial Attendance)
   6. Update Attendance / Approve Medical Condonation
-----------------------------------------------------------------------------------------

Select an option (0-12): 1

--- 1. Register New Student ---
Enter Roll Number (e.g. 24ITR005): 24ITR099
Enter Student Full Name: Suresh Kumar V
Enter Department (e.g. IT, CSE, ECE, MECH): IT
Enter Current Semester (1-8): 3
[SUCCESS] Student registered successfully: 24ITR099     | Suresh Kumar V            | Dept: IT     | Sem: 3

Select an option (0-12): 3

--- 3. Add Course to Catalog ---
Enter Course Code (e.g. 24ITR35): 24ITR39
Enter Course Title: Cloud Computing Principles
Enter Credits (1-6): 3
Course Types: 1. THEORY (40% CIA / 60% ESE) | 2. PRACTICAL (60% CIA / 40% ESE) | 3. EMBEDDED (50% CIA / 50% ESE)
Select Course Type (1-3): 1
Enter Semester for Course (1-8): 3
[SUCCESS] Course added to catalog: 24ITR39    | Cloud Computing Principles          | Credits: 3 | Type: THEORY     | Sem: 3

Select an option (0-12): 5

--- 5. Course Enrollment & Attendance Setup ---
Enter Student Roll Number: 24ITR099
Enter Course Code: 24ITR39
Enter Semester: 3
Enter Total Classes Conducted (e.g. 50): 50
Enter Classes Attended by Student: 45
Is Medical Condonation Approved? (Y/N, default N): N
[SUCCESS] Student enrolled (ID #10). Attendance: 45/50 (90.0%)
```

---

### Proof 3: Phased Incremental Marks Entry ("Then and Now")

#### Phase 1 (Mid-Semester): Recording CIA 1 & CIA 2
```text
Select an option (0-12): 7

--- 7. Phase 1 (Mid-Semester): Continuous Internal Assessments (CIA 1 & 2) ---
Enter Student Roll Number: 24ITR099
Enter Course Code: 24ITR39
Enter Semester: 3
Enter CIA Test 1 Marks (0 - 100): 88.0
Enter CIA Test 2 Marks (0 - 100): 92.0
[SUCCESS] Phase 1 Marks Recorded: CIA1 = 88.0, CIA2 = 92.0
```

#### Phase 2 (End-of-Semester CIA): Continuous Evaluation Components
```text
Select an option (0-12): 8

--- 8. Phase 2 (End-of-Semester CIA): Assignment / Quiz / Lab Evaluation ---
Enter Student Roll Number: 24ITR099
Enter Course Code: 24ITR39
Enter Semester: 3
Enter Assignment / Quiz / Lab Evaluation Marks (0 - 100): 90.0
[SUCCESS] Phase 2 Recorded. Finalized Raw CIA (out of 100) = 90.00
Theory Scaled CIA (40% Weightage) = 36.00 / 40.00
```

#### Phase 3 (Post-Semester): End Semester Examination (ESE) Marks
```text
Select an option (0-12): 9

--- 9. Phase 3 (Post-Semester): End Semester Examination (ESE) Marks ---
Enter Student Roll Number: 24ITR099
Enter Course Code: 24ITR39
Enter Semester: 3
Was the student ABSENT in the examination? (Y/N): N
Enter End Semester Examination (ESE) Marks (0 - 100): 86.0
[SUCCESS] ESE recorded. Total Scaled: 87.6 | Grade: A+ | GP: 9 | Status: PASS
```

---

### Proof 4: Generated Student Semester Mark Sheet (Roll No: 24ITR001)
```text
=========================================================================================================
                    KONGU ENGINEERING COLLEGE (AUTONOMOUS) - PERUNDURAI, ERODE 638060                    
                                OFFICE OF THE CONTROLLER OF EXAMINATIONS                                 
                         STUDENT SEMESTER GRADE SHEET - REGULATION 2024 (R2024)                          
=========================================================================================================
  Roll Number : 24ITR001             Student Name: Arun Kumar S                  
  Department  : IT                   Semester    : 3     Academic Year: 2024-2025
---------------------------------------------------------------------------------------------------------
 Course    | Course Title             | Type      | Crd | Att %  | CIA(Scale) | ESE(Scale) | Total | Grd  | GP | Status
---------------------------------------------------------------------------------------------------------
 24ITR31   | Database Design & Mana.. | Theory    |   3 |  96.0% | 88.0(35.2) | 88.0(52.8) |  88.0 | A+   |  9 | PASS  
 24ITR32   | Object Oriented Progra.. | Embedded  |   4 |  96.7% | 81.8(40.9) | 78.0(39.0) |  79.9 | A    |  8 | PASS  
 24ITR33   | Data Structures & Algo.. | Practical |   2 |  96.7% | 93.4(56.0) | 90.0(36.0) |  92.0 | O    | 10 | PASS  
 24ITR34   | Operating Systems & Ar.. | Theory    |   3 |  94.0% | 74.0(29.6) | 72.0(43.2) |  72.8 | A    |  8 | PASS  
---------------------------------------------------------------------------------------------------------
  Total Credits Registered : 12   | Total Credits Earned : 12  
  Semester GPA (SGPA)       : 8.58 / 10.00
  Equivalent Percentage    : 85.83 %
  Academic Standing        : FIRST CLASS WITH DISTINCTION
=========================================================================================================
  [Grade Legend: O=10(Outstanding), A+=9(Excellent), A=8(Very Good), B+=7(Good), B=6(Pass), RA=0, SA=0, AB=0]
=========================================================================================================
```

**Step-by-Step Calculation Verification for Roll No 24ITR001:**
1. **24ITR31 (Theory, 3 Credits):**
   - $\text{Raw CIA} = (85 \times 0.40) + (90 \times 0.40) + (90 \times 0.20) = 88.00$
   - $\text{Scaled CIA (40\%)} = 88.00 \times 0.40 = 35.20$
   - $\text{Raw ESE} = 88.00 \ge 45\%$ (Threshold Met)
   - $\text{Scaled ESE (60\%)} = 88.00 \times 0.60 = 52.80$
   - $\text{Total Marks} = 35.20 + 52.80 = 88.00 \implies \mathbf{A+} \text{ (GP: 9)}$
   - $\text{Credit Points} = 3 \times 9 = 27$

2. **24ITR32 (Embedded, 4 Credits):**
   - $\text{Raw CIA} = (80 \times 0.40) + (82 \times 0.40) + (85 \times 0.20) = 81.80$
   - $\text{Scaled CIA (50\%)} = 81.80 \times 0.50 = 40.90$
   - $\text{Raw ESE} = 78.00 \ge 45\%$ (Threshold Met)
   - $\text{Scaled ESE (50\%)} = 78.00 \times 0.50 = 39.00$
   - $\text{Total Marks} = 40.90 + 39.00 = 79.90 \implies \mathbf{A} \text{ (GP: 8)}$
   - $\text{Credit Points} = 4 \times 8 = 32$

3. **24ITR33 (Practical, 2 Credits):**
   - $\text{Raw CIA} = (92 \times 0.40) + (94 \times 0.40) + (95 \times 0.20) = 93.40$
   - $\text{Scaled CIA (60\%)} = 93.40 \times 0.60 = 56.04$
   - $\text{Raw ESE} = 90.00 \ge 50\%$ (Threshold Met)
   - $\text{Scaled ESE (40\%)} = 90.00 \times 0.40 = 36.00$
   - $\text{Total Marks} = 56.04 + 36.00 = 92.04 \implies \mathbf{O} \text{ (GP: 10)}$
   - $\text{Credit Points} = 2 \times 10 = 20$

4. **24ITR34 (Theory, 3 Credits):**
   - $\text{Raw CIA} = (70 \times 0.40) + (75 \times 0.40) + (80 \times 0.20) = 74.00$
   - $\text{Scaled CIA (40\%)} = 74.00 \times 0.40 = 29.60$
   - $\text{Raw ESE} = 72.00 \ge 45\%$ (Threshold Met)
   - $\text{Scaled ESE (60\%)} = 72.00 \times 0.60 = 43.20$
   - $\text{Total Marks} = 29.60 + 43.20 = 72.80 \implies \mathbf{A} \text{ (GP: 8)}$
   - $\text{Credit Points} = 3 \times 8 = 24$

**Semester Aggregates:**
- $\text{Total Credits} = 3 + 4 + 2 + 3 = 12$
- $\text{Weighted Points} = 27 + 32 + 20 + 24 = 103$
- $\mathbf{SGPA} = \frac{103}{12} = \mathbf{8.5833 \dots \approx 8.58}$
- $\mathbf{Equivalent\ Percentage} = 8.5833 \times 10 = \mathbf{85.83\%}$

---

### Proof 5: Attendance Defaulter Report (< 65% Attendance Flagged as SA)
```text
=========================================================================================================
                        KONGU ENGINEERING COLLEGE (AUTONOMOUS) - REGULATION 2024                         
                ATTENDANCE DEFAULTER REPORT (ATTENDANCE < 65% - FLAGGED WITH GRADE 'SA')                 
                    STATUS: BARRED FROM APPEARING IN END SEMESTER EXAMINATIONS (ESE)                     
=========================================================================================================
 Roll No    | Student Name           | Dept   | Course    | Course Title           | Held  | Attended | Att % | Grade 
---------------------------------------------------------------------------------------------------------
 24ITR003   | Chandran K             | IT     | 24ITR31   | Database Design & Ma.. |    50 |       25 |  50.0% | SA (SA)
 24ITR003   | Chandran K             | IT     | 24ITR32   | Object Oriented Prog.. |    60 |       28 |  46.7% | SA (SA)
---------------------------------------------------------------------------------------------------------
  Total Number of Defaulter Course Enrollments: 2
  Rule Note: In accordance with KEC R2024 Clause 7.2, students with attendance < 65%
             are barred from ESE and awarded grade SA (0 Grade Points).
=========================================================================================================
```

---

### Proof 6: Class Performance & Department Result Summary
```text
=========================================================================================================
                        KONGU ENGINEERING COLLEGE (AUTONOMOUS) - REGULATION 2024                         
                      CLASS PERFORMANCE & RESULT SUMMARY - DEPARTMENT OF IT (SEM 3)                      
=========================================================================================================
  Department Name               : IT                            
  Academic Semester             : Semester 3
  Total Students Evaluated      : 5    
  Students Passed (All Clear)   : 2    
  Students with RA / SA / AB    : 3    
  Class Pass Percentage         : 40.00 %
  Class Average SGPA            : 4.03 / 10.00
  Class Average Percentage      : 40.31 %
---------------------------------------------------------------------------------------------------------
  Department Benchmark Status   : ATTENTION REQUIRED (Special coaching recommended for RA students)
=========================================================================================================
```
