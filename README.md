# 🎓 KEC R2024 — Student Academic & Continuous Evaluation System

> **Kongu Engineering College (Autonomous), Perundurai, Erode — Regulation 2024**
> A full-featured, console-based academic evaluation engine built with **Java 21** and **JDBC/MySQL**, faithfully implementing the KEC R2024 grading, attendance, and SGPA computation rules.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [R2024 Evaluation Rules](#r2024-evaluation-rules)
- [Features](#features)
- [Project Structure](#project-structure)
- [Database Schema](#database-schema)
- [Setup & Run](#setup--run)
- [Application Menu](#application-menu)
- [Sample Output](#sample-output)
- [Screenshots](#screenshots)
- [Contributing](#contributing)
- [License](#license)

---

## Overview

This system automates the entire academic evaluation lifecycle for a semester, from student registration and course enrollment to phased marks entry and final grade-sheet generation — all aligned with **KEC Regulation 2024**.

Key capabilities:
- 📌 Register students & manage course catalog
- 📊 Three-phase incremental marks entry (CIA 1 → CIA 2 → Assignment → ESE)
- 🚦 Attendance eligibility filtering (≥75%, 65–74.9% condonation, <65% barred)
- 🧮 Automatic SGPA & percentage computation
- 📄 Formatted semester grade sheet generation
- 🔴 Attendance defaulter reports

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Database Connectivity | JDBC (Java Database Connectivity) |
| Database | MySQL 8.x |
| JDBC Driver | `mysql-connector-j` 8.3.0 |
| Build Tool | Apache Maven 3.x |
| Runner | `exec-maven-plugin` / `build_and_run.bat` |

---

## R2024 Evaluation Rules

### A. Weightage Engine

| Course Type | CIA Weightage | ESE Weightage | ESE Min Threshold | Aggregate Pass Min |
|---|:---:|:---:|:---:|:---:|
| **THEORY** | 40% | 60% | 45% (27/60) | 50.0 Marks |
| **PRACTICAL** | 60% | 40% | 50% (20/40) | 50.0 Marks |
| **EMBEDDED** | 50% | 50% | 45% (22.5/50) | 50.0 Marks |

**CIA Synthesis Formula:**
```
Raw CIA (out of 100) = (CIA1 x 0.40) + (CIA2 x 0.40) + (Assignment/Lab x 0.20)
```

### B. Attendance Rules (Clause 7)

| Attendance Range | Status |
|---|---|
| >= 75% | Fully eligible for ESE |
| 65% to 74.9% | Conditionally eligible (requires Medical Condonation) |
| < 65% | **Barred** — Grade `SA`, 0 Grade Points |

### C. 10-Point Absolute Grading System

| Marks Range | Letter Grade | Grade Points | Standing |
|---|:---:|:---:|---|
| 91 – 100 | **O** | 10 | Outstanding |
| 81 – 90 | **A+** | 9 | Excellent |
| 71 – 80 | **A** | 8 | Very Good |
| 61 – 70 | **B+** | 7 | Good |
| 50 – 60 | **B** | 6 | Average / Pass |
| < 50 or failed ESE min | **RA** | 0 | Re-Appearance |
| Attendance < 65% | **SA** | 0 | Shortage of Attendance |
| Absent in Exam | **AB** | 0 | Absent |

### D. SGPA & Percentage

```
SGPA = Sum(Course Credits x Grade Points Earned) / Sum(Total Enrolled Credits)
Equivalent Percentage = SGPA x 10
```

---

## Features

| # | Feature |
|---|---|
| 1 | Register new student (Roll No, Name, Department, Semester) |
| 2 | View all registered students |
| 3 | Add new course to catalog (Code, Title, Credits, Type, Sem) |
| 4 | View full course catalog |
| 5 | Enroll student in course with initial attendance setup |
| 6 | Update attendance / approve medical condonation |
| 7 | Phase 1 — Enter CIA 1 & CIA 2 marks |
| 8 | Phase 2 — Enter Assignment / Quiz / Lab evaluation marks |
| 9 | Phase 3 — Enter End Semester Examination (ESE) marks |
| 10 | Generate individual student semester grade sheet |
| 11 | Generate attendance defaulter report |
| 12 | Generate class performance & department result summary |

---

## Project Structure

```
kec-academic-evaluation-system/
├── src/
│   └── main/
│       └── java/
│           └── com/kec/
│               └── MainApp.java          # Main application entry point & all logic
├── IMAGES/                               # Terminal screenshots
├── kec_schema.sql                        # MySQL database schema & seed data
├── db.properties.example                 # DB config template (copy to db.properties)
├── EXECUTION_DOCUMENTATION.md            # Detailed execution proofs & calculations
├── build_and_run.bat                     # One-click Windows build & run script
├── run.bat                               # Run pre-built JAR directly
├── pom.xml                               # Maven project configuration
└── README.md                             # This file
```

---

## Database Schema

Four core tables power the system:

```sql
students          -- Roll No, Name, Department, Semester
courses           -- Code, Title, Credits, Type (THEORY/PRACTICAL/EMBEDDED), Semester
enrollments       -- Student <-> Course mapping + Attendance (held, attended, condonation)
assessment_marks  -- CIA1, CIA2, Assignment, ESE per enrollment + computed grades
```

> The full DDL with sample seed data is in `kec_schema.sql`.

---

## Setup & Run

### Prerequisites

- Java 21 JDK
- Apache Maven 3.6+
- MySQL 8.x running locally

### 1. Clone the Repository

```bash
git clone https://github.com/Yogeshwaran-11/kec-academic-evaluation-system.git
cd kec-academic-evaluation-system
```

### 2. Configure Database

```bash
# Create the database and tables
mysql -u root -p < kec_schema.sql

# Copy the config template
copy db.properties.example db.properties
```

Edit `db.properties` with your local MySQL credentials:

```properties
db.url=jdbc:mysql://localhost:3306/kec_eval_db
db.username=root
db.password=YOUR_PASSWORD
```

> **Note:** `db.properties` is listed in `.gitignore` and will never be committed.

### 3. Build & Run

**Option A — One-click batch script (Windows):**
```bat
build_and_run.bat
```

**Option B — Maven:**
```bash
mvn compile exec:java
```

---

## Application Menu

```
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
 [MARKS ENTRY - THREE-PHASE WORKFLOW]
   7. Phase 1 (Mid-Semester)    : Enter CIA 1 & CIA 2 Marks
   8. Phase 2 (End-of-Sem CIA)  : Enter Assignment / Quiz / Lab Marks
   9. Phase 3 (Post-Semester)   : Enter ESE Marks & Finalize Result
-----------------------------------------------------------------------------------------
 [REPORTS & ANALYTICS]
  10. Generate Student Semester Grade Sheet
  11. View Attendance Defaulter Report  (< 65%)
  12. Class Performance & Department Result Summary
   0. Exit
=========================================================================================
```

---

## Sample Output

### Student Semester Grade Sheet

```
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
  Equivalent Percentage     : 85.83 %
  Academic Standing         : FIRST CLASS WITH DISTINCTION
=========================================================================================================
```

---

## Screenshots

Terminal screenshots of the running application are available in the `IMAGES/` directory.

For detailed step-by-step execution proofs with calculations, see `EXECUTION_DOCUMENTATION.md`.

---

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m "feat: describe your change"`
4. Push to the branch: `git push origin feature/your-feature`
5. Open a Pull Request

---

## License

This project is submitted as an academic assignment for **Kongu Engineering College (Autonomous)** under **Regulation 2024**. All rights reserved (c) 2024 Yogeshwaran V.
