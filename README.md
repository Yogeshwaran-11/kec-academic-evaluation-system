# Kongu Engineering College (Autonomous) - Regulation 2024 (R2024)
## Student Academic & Continuous Evaluation System using Java JDBC

A database-driven Java application utilizing JDBC to manage student records, perform incremental marks entry (Continuous Internal Assessments and End Semester Examinations), enforce attendance eligibility rules, and generate formal academic performance reports adhering to **Kongu Engineering College - Regulation 2024**.

---

## 📁 Repository Structure
```
d:/java_assignment/
├── lib/
│   └── mysql-connector-j-8.3.0.jar       # MySQL JDBC Type-4 Driver
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/kec/
│   │   │       ├── db/
│   │   │       │   └── DBConnection.java     # JDBC Connection Manager
│   │   │       ├── model/
│   │   │       │   ├── Student.java          # Student Entity
│   │   │       │   ├── Course.java           # Course Catalog Entity
│   │   │       │   ├── CourseType.java       # THEORY, PRACTICAL, EMBEDDED Enums
│   │   │       │   ├── Enrollment.java       # Enrollment & Attendance Model
│   │   │       │   ├── AssessmentMark.java   # Phased Assessment Mark Model
│   │   │       │   ├── EvaluationResult.java # Evaluation Computation Model
│   │   │       │   └── Grade.java            # 10-Point Absolute Grading Enum
│   │   │       ├── dao/
│   │   │       │   ├── StudentDAO.java       # Student CRUD SQL Queries
│   │   │       │   └── AcademicDAO.java      # Course, Enrollment, Marks & Evaluation DAO
│   │   │       ├── service/
│   │   │       │   └── EvaluationEngine.java # KEC R2024 Evaluation Engine
│   │   │       ├── util/
│   │   │       │   └── ReportGenerator.java  # Grade Sheet & Defaulter Formatter
│   │   │       └── MainApp.java              # Interactive Menu-Driven System
│   │   └── resources/
│   │       └── db.properties                 # Database Connection Configuration
├── kec_schema.sql                            # SQL Schema with DDL, Constraints & Seed Data
├── DBConnection.java                         # Root submission deliverable
├── MainApp.java                              # Root submission deliverable
├── pom.xml                                   # Maven Build Configuration
├── run.bat                                   # Quick Batch Launcher
├── build_and_run.bat                         # Build and Launch Batch Script
├── EXECUTION_DOCUMENTATION.md                # Detailed Execution Proofs & Math Verifications
└── README.md                                 # Project Overview & Usage Guide
```

---

## ⚙️ Prerequisites & Configuration
- **Java Development Kit**: JDK 21 (or JDK 17+)
- **Database**: MySQL 8.x / 9.x / 26.x
- **Build Tool**: Apache Maven 3.9+ (or direct `javac`)

### Database Connection Settings (`db.properties`)
```properties
db.url=jdbc:mysql://localhost:3307/kec_eval_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata
db.user=root
db.password=1011
db.driver=com.mysql.cj.jdbc.Driver
```
*(If your local MySQL runs on port 3306, DBConnection automatically falls back to 3306).*

---

## 🚀 How to Run the Application

### Option A: Using Maven (Recommended)
```powershell
# 1. Compile the project
mvn compile

# 2. Run the interactive menu-driven interface
mvn exec:java

# 3. Run the automated demonstration test suite
mvn exec:java -Dexec.args="--demo"
```

### Option B: Using Direct Java Command Line
```powershell
# 1. Compile
javac -cp "lib/mysql-connector-j-8.3.0.jar;." -d target/classes src/main/java/com/kec/**/*.java

# 2. Launch Interactive Menu
java -cp "target/classes;lib/mysql-connector-j-8.3.0.jar;." com.kec.MainApp

# 3. Launch Automated Demonstration
java -cp "target/classes;lib/mysql-connector-j-8.3.0.jar;." com.kec.MainApp --demo
```

### Option C: Using Windows Batch Script
Simply double-click `run.bat` or `build_and_run.bat`.

---

## 📊 Core Features Implemented

1. **Student Registration**: Supports Roll Number (e.g., `24ITR001`), Student Name, Department (`IT`, `CSE`, `ECE`, `MECH`), and Current Semester.
2. **Course Catalog Management**: Supports Course Code (e.g., `24ITR31`), Title, Credits, Course Type (`THEORY`, `PRACTICAL`, `EMBEDDED`), and Semester.
3. **Course Enrollment & Attendance Tracking**:
   - Continuous attendance tracking (total classes held vs. classes attended).
   - $\ge 75\%$: Eligible for ESE.
   - $65\% \text{ to } 74.9\%$: Conditionally eligible (with Medical Condonation approval toggle).
   - $< 65\%$: Barred from ESE, awarded grade `SA` (0 Grade Points).
4. **Phased Incremental Marks Entry ("Then and Now")**:
   - **Phase 1 (Mid-Semester)**: CIA Test 1 (0–100) and CIA Test 2 (0–100).
   - **Phase 2 (End of Semester - CIA)**: Continuous evaluation components (Assignments, Quizzes, Lab Evaluations out of 100).
   - **Phase 3 (Post Semester)**: End Semester Examination (ESE out of 100) and Absence (`AB`).
5. **KEC Regulation 2024 Evaluation Engine**:
   - **THEORY**: CIA = 40%, ESE = 60%, ESE Min = 45% (27/60), Aggregate Min = 50.
   - **PRACTICAL**: CIA = 60%, ESE = 40%, ESE Min = 50% (20/40), Aggregate Min = 50.
   - **EMBEDDED**: CIA = 50%, ESE = 50%, ESE Min = 45% (22.5/50), Aggregate Min = 50.
6. **10-Point Absolute Grading System**:
   - `O` (91-100, GP: 10), `A+` (81-90, GP: 9), `A` (71-80, GP: 8), `B+` (61-70, GP: 7), `B` (50-60, GP: 6).
   - `RA` (Re-Appearance, GP: 0), `SA` (Shortage of Attendance, GP: 0), `AB` (Absent in ESE, GP: 0).
7. **Formulas**:
   - $\text{SGPA} = \frac{\sum (\text{Course Credits} \times \text{Grade Points Earned})}{\sum (\text{Total Enrolled Credits})}$
   - $\text{Equivalent Percentage} = \text{SGPA} \times 10$
8. **Academic Reports**:
   - **Student Semester Mark Sheet**: Individual report with internal, external, total, letter grade, SGPA, %, and standing.
   - **Attendance Defaulter Report**: Identifies all students with attendance $< 65\%$ flagged with grade `SA`.
   - **Class Performance Summary**: Department-wise pass/fail count, pass percentage, and average SGPA.
