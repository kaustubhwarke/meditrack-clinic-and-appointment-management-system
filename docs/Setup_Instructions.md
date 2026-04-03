# MediTrack - Setup Instructions

## Complete Guide to Installing and Running MediTrack Clinic Management System

---

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Java Development Kit (JDK) Installation](#java-development-kit-jdk-installation)
3. [Environment Variable Configuration](#environment-variable-configuration)
4. [Verification](#verification)
5. [Project Setup](#project-setup)
6. [Compilation](#compilation)
7. [Running the Application](#running-the-application)
8. [IDE Setup (Optional)](#ide-setup-optional)
9. [Troubleshooting](#troubleshooting)

---

## Prerequisites

Before installing MediTrack, ensure your system meets these requirements:

### System Requirements:
- **Operating System**: Windows 10/11, macOS 10.14+, or Linux (Ubuntu 18.04+)
- **RAM**: Minimum 4 GB (8 GB recommended)
- **Disk Space**: At least 500 MB free space
- **Internet Connection**: For downloading JDK

---

## Java Development Kit (JDK) Installation

### Step 1: Download JDK

MediTrack requires **JDK 17** or higher.

#### Windows:

1. Visit Oracle's official website:
   ```
   https://www.oracle.com/java/technologies/downloads/
   ```

2. Navigate to **Java 17** section

3. Download **Windows x64 Installer**:
   - File: `jdk-17_windows-x64_bin.exe` (or latest version)
   - Size: ~150 MB

4. Alternative (OpenJDK):
   ```
   https://adoptium.net/
   ```
   - Download **Temurin 17** (Windows x64 MSI)

#### macOS:

1. **Using Homebrew (Recommended):**
   ```bash
   # Install Homebrew if not already installed
   /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
   
   # Install OpenJDK 17
   brew install openjdk@17
   ```

2. **Manual Installation:**
   - Download from Oracle or Adoptium
   - File: `jdk-17_macos-x64_bin.dmg`

#### Linux (Ubuntu/Debian):

```bash
# Update package index
sudo apt update

# Install OpenJDK 17
sudo apt install openjdk-17-jdk -y

# Verify installation
java -version
```

### Step 2: Install JDK (Windows Detailed Steps)

1. **Run the Installer:**
   - Double-click `jdk-17_windows-x64_bin.exe`
   - Click **Next**

2. **Choose Installation Directory:**
   - Default: `C:\Program Files\Java\jdk-17`
   - **Note this path** - you'll need it for environment variables
   - Click **Next**

3. **Installation Progress:**
   - Wait for installation to complete
   - Click **Close**

4. **Verify Installation Folder:**
   ```
   C:\Program Files\Java\jdk-17\
   ├── bin\
   │   ├── java.exe
   │   ├── javac.exe
   │   └── ...
   ├── lib\
   └── ...
   ```

### Step 3: Install JDK (macOS Detailed Steps)

1. **Using Homebrew:**
   ```bash
   brew install openjdk@17
   
   # Link OpenJDK to system Java
   sudo ln -sfn /usr/local/opt/openjdk@17/libexec/openjdk.jdk \
        /Library/Java/JavaVirtualMachines/openjdk-17.jdk
   ```

2. **Manual Installation:**
   - Open the `.dmg` file
   - Run the `.pkg` installer
   - Follow on-screen instructions
   - Default location: `/Library/Java/JavaVirtualMachines/jdk-17.jdk/`

---

## Environment Variable Configuration

### Windows Configuration

#### Step 1: Set JAVA_HOME

1. **Open System Properties:**
   - Right-click **This PC** → **Properties**
   - Click **Advanced system settings**
   - Click **Environment Variables**

2. **Create JAVA_HOME Variable:**
   - Under **System Variables**, click **New**
   - **Variable name:** `JAVA_HOME`
   - **Variable value:** `C:\Program Files\Java\jdk-17`
   - Click **OK**

   ![Setting JAVA_HOME](https://via.placeholder.com/600x300?text=Environment+Variables+Screenshot)

#### Step 2: Update PATH

1. **Edit PATH Variable:**
   - Under **System Variables**, select **Path**
   - Click **Edit**
   - Click **New**
   - Add: `%JAVA_HOME%\bin`
   - Move this entry to the top (click **Move Up**)
   - Click **OK** on all dialogs

2. **Verify PATH includes:**
   ```
   %JAVA_HOME%\bin
   ```

#### Step 3: Verify Configuration

Open **new** PowerShell/Command Prompt window:

```powershell
# Check JAVA_HOME
echo $env:JAVA_HOME
# Output: C:\Program Files\Java\jdk-17

# Check java command
java -version
# Output: java version "17.0.x" ...

# Check javac command
javac -version
# Output: javac 17.0.x
```

### macOS Configuration

#### Step 1: Set JAVA_HOME in Shell Profile

1. **Determine Your Shell:**
   ```bash
   echo $SHELL
   # Output: /bin/zsh (or /bin/bash)
   ```

2. **Edit Profile File:**
   
   **For zsh (default on macOS Catalina+):**
   ```bash
   nano ~/.zshrc
   ```
   
   **For bash:**
   ```bash
   nano ~/.bash_profile
   ```

3. **Add These Lines:**
   ```bash
   # Java Configuration
   export JAVA_HOME=$(/usr/libexec/java_home -v 17)
   export PATH=$JAVA_HOME/bin:$PATH
   ```

4. **Save and Exit:**
   - Press `Ctrl + X`
   - Press `Y` to confirm
   - Press `Enter`

5. **Reload Profile:**
   ```bash
   source ~/.zshrc  # or source ~/.bash_profile
   ```

#### Step 2: Verify Configuration

```bash
# Check JAVA_HOME
echo $JAVA_HOME
# Output: /Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home

# Check java
java -version

# Check javac
javac -version
```

### Linux Configuration

#### Step 1: Set JAVA_HOME

1. **Edit Profile:**
   ```bash
   nano ~/.bashrc
   ```

2. **Add These Lines:**
   ```bash
   # Java Configuration
   export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
   export PATH=$JAVA_HOME/bin:$PATH
   ```

3. **Reload:**
   ```bash
   source ~/.bashrc
   ```

#### Step 2: Verify Configuration

```bash
java -version
javac -version
echo $JAVA_HOME
```

---

## Verification

### Comprehensive Verification Steps:

```powershell
# 1. Check Java version
java -version
# Expected Output:
# java version "17.0.x" 2024-xx-xx LTS
# Java(TM) SE Runtime Environment (build 17.0.x+x)
# Java HotSpot(TM) 64-Bit Server VM (build 17.0.x+x, mixed mode, sharing)

# 2. Check Java Compiler
javac -version
# Expected Output:
# javac 17.0.x

# 3. Verify JAVA_HOME (Windows)
echo $env:JAVA_HOME

# 3. Verify JAVA_HOME (macOS/Linux)
echo $JAVA_HOME

# 4. Test simple compilation
echo "public class Test { public static void main(String[] args) { System.out.println(\"Java works!\"); } }" > Test.java
javac Test.java
java Test
# Expected Output: Java works!

# Cleanup
del Test.java Test.class  # Windows
rm Test.java Test.class   # macOS/Linux
```

---

## Project Setup

### Step 1: Download MediTrack Project

**Option A: Using Git (Recommended)**
```bash
git clone https://github.com/kaustubhwarke/meditrack-clinic-and-appointment-management-system.git
cd meditrack-clinic-and-appointment-management-system
```

**Option B: Download ZIP**
1. Download the project ZIP file
2. Extract to a folder (e.g., `C:\Projects\meditrack`)
3. Navigate to the folder

### Step 2: Verify Project Structure

```powershell
# Navigate to project root
cd C:\Actions\kaustubhwarke\meditrack-clinic-and-appointment-management-system

# List files (Windows PowerShell)
dir

# Expected structure:
# meditrack-clinic-and-appointment-management-system/
# ├── docs/
# ├── src/
# │   └── main/
# │       └── java/
# │           └── com/
# │               └── airtribe/
# │                   └── meditrack/
# ├── pom.xml
# └── README.md
```

### Step 3: Understand Package Structure

**Source Root:** `src/main/java/`  
**Package:** `com.airtribe.meditrack`  
**Main Class:** `com.airtribe.meditrack.Main`

**Important:** The package name is `com.airtribe.meditrack`, NOT `src.main.com.airtribe.meditrack`

---

## Compilation

### Method 1: Using Maven (Recommended)

```bash
# Navigate to project root
cd meditrack-clinic-and-appointment-management-system

# Compile with Maven
mvn clean compile

# Package into JAR
mvn package

# Run from JAR
java -jar target/meditrack-1.0-SNAPSHOT.jar
```

### Method 2: Manual Compilation (Windows)

```powershell
# Create output directory
New-Item -ItemType Directory -Force -Path out

# Compile all Java files with UTF-8 encoding
javac -encoding UTF-8 `
      -d out `
      -sourcepath src/main/java `
      -cp src/main/java `
      src/main/java/com/airtribe/meditrack/**/*.java `
      src/main/java/com/airtribe/meditrack/Main.java

# Alternative: Compile main class (automatically compiles dependencies)
javac -encoding UTF-8 `
      -d out `
      -sourcepath src/main/java `
      src/main/java/com/airtribe/meditrack/Main.java
```

### Method 3: Manual Compilation (macOS/Linux)

```bash
# Create output directory
mkdir -p out

# Compile with UTF-8 encoding
javac -encoding UTF-8 \
      -d out \
      -sourcepath src/main/java \
      src/main/java/com/airtribe/meditrack/**/*.java \
      src/main/java/com/airtribe/meditrack/Main.java

# Or compile main class only (auto-compiles dependencies)
javac -encoding UTF-8 \
      -d out \
      -sourcepath src/main/java \
      src/main/java/com/airtribe/meditrack/Main.java
```

### Expected Output:

✅ **Success:** No output (silent compilation)  
❌ **Error:** Compilation errors displayed

### Verify Compilation:

```powershell
# Check compiled classes (Windows)
dir out\com\airtribe\meditrack\*.class

# Check compiled classes (macOS/Linux)
ls -la out/com/airtribe/meditrack/*.class

# Expected output:
# Main.class
# and subdirectories: entity/, service/, util/, constants/, exception/, interfaces/
```

---

## Running the Application

### Method 1: Normal Start (Empty Database)

```powershell
# Windows PowerShell
java -cp out com.airtribe.meditrack.Main

# macOS/Linux
java -cp out com.airtribe.meditrack.Main
```

### Method 2: Start with Sample Data

```powershell
# Load pre-populated sample data
java -cp out com.airtribe.meditrack.Main --loadData
```

**Sample data includes:**
- 3 Doctors (various specializations)
- 3 Patients (different blood groups)
- 2 Sample appointments

### Expected Startup Output:

```
************************************************************
  Initializing MediTrack v1.0.0
  Loading application constants...
  Tax Rate: 18.0%
  Data Directory: data/
************************************************************
[IdGenerator] Singleton instance initialized
[Person] Class loaded - initializing Person class

============================================================
MediTrack v1.0.0 - Clinic and Appointment Management System
============================================================

Loading sample data...
[DoctorService] Doctor created: Dr. Smith
[DoctorService] Doctor created: Dr. Johnson
[DoctorService] Doctor created: Dr. Williams
[PatientService] Patient created: John Doe
[PatientService] Patient created: Jane Smith
[PatientService] Patient created: Bob Wilson
[AppointmentService] Appointment created: A3000
[AppointmentService] Appointment created: A3001
Sample data loaded successfully!
- 3 doctors
- 3 patients
- 2 appointments

Press Enter to continue...

============================================================
  MEDITRACK - MAIN MENU
============================================================
  1. Doctor Management
  2. Patient Management
  3. Appointment Management
  4. Billing Management
  5. Reports & Analytics
  6. About & Statistics
  0. Exit
============================================================
```

---

## IDE Setup (Optional)

### IntelliJ IDEA Setup

#### Step 1: Install IntelliJ IDEA

1. Download from: https://www.jetbrains.com/idea/download/
2. Choose **Community Edition** (free) or **Ultimate Edition**
3. Run installer and follow instructions

#### Step 2: Open Project

1. **Open IntelliJ IDEA**
2. Click **Open**
3. Navigate to `meditrack-clinic-and-appointment-management-system` folder
4. Click **OK**

#### Step 3: Configure Project Structure

1. **Mark Source Root:**
   - Right-click `src/main/java` folder
   - Select **Mark Directory as** → **Sources Root**
   - Folder icon turns blue

2. **Set Project SDK:**
   - **File** → **Project Structure** (Ctrl+Alt+Shift+S)
   - **Project** → **SDK**
   - Select **JDK 17** (or **Add SDK** → **Download JDK**)
   - Click **OK**

3. **Set Language Level:**
   - In Project Structure → **Project**
   - **Language level:** 17 - Sealed types, always-strict floating-point semantics
   - Click **OK**

#### Step 4: Build Project

1. **Build** → **Build Project** (Ctrl+F9)
2. Wait for compilation
3. Check **Build** tab for errors

#### Step 5: Run Configuration

1. **Run** → **Edit Configurations**
2. Click **+** → **Application**
3. **Configuration:**
   - **Name:** MediTrack
   - **Main class:** `com.airtribe.meditrack.Main`
   - **Program arguments:** `--loadData` (optional)
   - **Working directory:** Project root
4. Click **OK**

#### Step 6: Run Application

- Click **Run** button (green triangle) or press **Shift+F10**
- Application starts in console

### Visual Studio Code Setup

#### Step 1: Install VS Code

1. Download from: https://code.visualstudio.com/
2. Run installer

#### Step 2: Install Java Extension

1. Open VS Code
2. Go to **Extensions** (Ctrl+Shift+X)
3. Search: **Extension Pack for Java**
4. Click **Install**
5. Extensions included:
   - Language Support for Java
   - Debugger for Java
   - Test Runner for Java
   - Maven for Java
   - Project Manager for Java

#### Step 3: Open Project

1. **File** → **Open Folder**
2. Select `meditrack-clinic-and-appointment-management-system`
3. VS Code detects Java project automatically

#### Step 4: Run Application

1. Open `Main.java`
2. Click **Run** above the `main()` method
3. Or press **F5** to run with debugger

### Eclipse Setup

#### Step 1: Install Eclipse IDE

1. Download: https://www.eclipse.org/downloads/
2. Choose **Eclipse IDE for Java Developers**
3. Run installer

#### Step 2: Import Project

1. **File** → **Import**
2. Select **Existing Projects into Workspace**
3. Browse to project folder
4. Click **Finish**

#### Step 3: Configure Build Path

1. Right-click project → **Properties**
2. **Java Build Path** → **Source**
3. Ensure `src/main/java` is listed as source folder
4. **Libraries** → Ensure JDK 17 is added

#### Step 4: Run

1. Right-click `Main.java`
2. **Run As** → **Java Application**

---

## Compilation

### Understanding the Compilation Process

```
Source Code (.java) → javac → Bytecode (.class) → JVM → Execution
```

### Compilation Commands

#### Windows PowerShell:

```powershell
# Navigate to project root
cd C:\Actions\kaustubhwarke\meditrack-clinic-and-appointment-management-system

# Create output directory
New-Item -ItemType Directory -Force -Path out

# Compile with explicit encoding
javac -encoding UTF-8 `
      -d out `
      -sourcepath src/main/java `
      src/main/java/com/airtribe/meditrack/Main.java

# Verify compilation
dir out\com\airtribe\meditrack\Main.class
```

#### macOS/Linux Terminal:

```bash
# Navigate to project root
cd ~/meditrack-clinic-and-appointment-management-system

# Create output directory
mkdir -p out

# Compile
javac -encoding UTF-8 \
      -d out \
      -sourcepath src/main/java \
      src/main/java/com/airtribe/meditrack/Main.java

# Verify compilation
ls -l out/com/airtribe/meditrack/Main.class
```

### Compilation Options Explained:

| Option | Purpose | Example |
|--------|---------|---------|
| `-encoding UTF-8` | Character encoding for source files | Handles special characters (₹, etc.) |
| `-d out` | Output directory for .class files | Compiled classes go to `out/` |
| `-sourcepath src/main/java` | Where to find source files | Java searches here for dependencies |
| `-cp` or `-classpath` | Additional libraries/dependencies | Not needed for MediTrack (no external deps) |

### Batch Compilation (Windows)

Create `compile.bat`:

```batch
@echo off
echo Compiling MediTrack...

if not exist out mkdir out

javac -encoding UTF-8 ^
      -d out ^
      -sourcepath src/main/java ^
      src/main/java/com/airtribe/meditrack/Main.java

if %errorlevel% equ 0 (
    echo Compilation successful!
) else (
    echo Compilation failed!
    exit /b 1
)
```

Run:
```powershell
.\compile.bat
```

### Shell Script (macOS/Linux)

Create `compile.sh`:

```bash
#!/bin/bash
echo "Compiling MediTrack..."

mkdir -p out

javac -encoding UTF-8 \
      -d out \
      -sourcepath src/main/java \
      src/main/java/com/airtribe/meditrack/Main.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
else
    echo "Compilation failed!"
    exit 1
fi
```

Make executable and run:
```bash
chmod +x compile.sh
./compile.sh
```

---

## Running the Application

### Basic Execution

```powershell
# Windows PowerShell
java -cp out com.airtribe.meditrack.Main

# macOS/Linux
java -cp out com.airtribe.meditrack.Main
```

### With Command-Line Arguments

```powershell
# Load sample data
java -cp out com.airtribe.meditrack.Main --loadData

# Custom arguments can be added in future
java -cp out com.airtribe.meditrack.Main --loadData --debug --port 8080
```

### With JVM Options

```bash
# Set memory limits
java -Xms256m -Xmx1024m \
     -cp out com.airtribe.meditrack.Main

# Enable GC logging
java -Xms256m -Xmx1024m \
     -XX:+PrintGCDetails \
     -XX:+PrintGCDateStamps \
     -Xloggc:gc.log \
     -cp out com.airtribe.meditrack.Main

# With G1 garbage collector
java -Xms512m -Xmx2048m \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -cp out com.airtribe.meditrack.Main
```

### Batch Scripts

**Windows: `run.bat`**
```batch
@echo off
echo Running MediTrack...
java -cp out com.airtribe.meditrack.Main %*
```

Usage:
```powershell
.\run.bat
.\run.bat --loadData
```

**macOS/Linux: `run.sh`**
```bash
#!/bin/bash
echo "Running MediTrack..."
java -cp out com.airtribe.meditrack.Main "$@"
```

Usage:
```bash
chmod +x run.sh
./run.sh
./run.sh --loadData
```

---

## Maven Setup (Alternative)

### Prerequisites:
- Maven 3.6+ installed
- JAVA_HOME configured

### Commands:

```bash
# Navigate to project root (where pom.xml is)
cd meditrack-clinic-and-appointment-management-system

# Clean previous builds
mvn clean

# Compile
mvn compile

# Package into JAR
mvn package

# Run the JAR
java -jar target/meditrack-1.0-SNAPSHOT.jar

# Run with Maven exec plugin
mvn exec:java -Dexec.mainClass="com.airtribe.meditrack.Main"

# Run with sample data
mvn exec:java -Dexec.mainClass="com.airtribe.meditrack.Main" -Dexec.args="--loadData"
```

### Install Maven:

**Windows (using Chocolatey):**
```powershell
choco install maven
```

**macOS (using Homebrew):**
```bash
brew install maven
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt install maven
```

**Verify:**
```bash
mvn -version
```

---

## IDE Setup (Optional)

### IntelliJ IDEA - Detailed Configuration

#### Import as Maven Project:

1. **File** → **New** → **Project from Existing Sources**
2. Select `meditrack-clinic-and-appointment-management-system` folder
3. Choose **Import project from external model** → **Maven**
4. Click **Next** and **Finish**
5. IntelliJ auto-configures based on `pom.xml`

#### Configure Run Configuration:

1. **Run** → **Edit Configurations**
2. Click **+** → **Application**
3. **Configuration:**
   - **Name:** MediTrack
   - **Module:** meditrack
   - **Main class:** `com.airtribe.meditrack.Main` (click browse to find)
   - **VM options:** `-Xms256m -Xmx1024m` (optional)
   - **Program arguments:** `--loadData` (optional)
   - **Working directory:** `$PROJECT_DIR$`
4. Click **Apply** → **OK**

#### Run Application:

- Press **Shift+F10** or click **Run** button
- Console opens with MediTrack menu

### Visual Studio Code - Additional Tips

#### Settings for Java:

Create `.vscode/settings.json`:

```json
{
    "java.project.sourcePaths": ["src/main/java"],
    "java.project.outputPath": "out",
    "java.configuration.runtimes": [
        {
            "name": "JavaSE-17",
            "path": "C:\\Program Files\\Java\\jdk-17",
            "default": true
        }
    ]
}
```

#### Launch Configuration:

Create `.vscode/launch.json`:

```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "MediTrack",
            "request": "launch",
            "mainClass": "com.airtribe.meditrack.Main",
            "projectName": "meditrack",
            "args": "--loadData"
        }
    ]
}
```

---

## Troubleshooting

### Issue 1: "javac is not recognized"

**Problem:** PATH not configured correctly.

**Solution:**

**Windows:**
```powershell
# Add to PATH temporarily
$env:PATH += ";C:\Program Files\Java\jdk-17\bin"

# Verify
javac -version
```

**Permanent fix:** Add `%JAVA_HOME%\bin` to System PATH (see Environment Variables section)

**macOS/Linux:**
```bash
# Add to PATH temporarily
export PATH=$JAVA_HOME/bin:$PATH

# Permanent fix: Add to ~/.zshrc or ~/.bashrc
```

### Issue 2: "Error: Could not find or load main class"

**Problem:** Wrong classpath or package name.

**Solution:**

```powershell
# Ensure you're using correct package name
java -cp out com.airtribe.meditrack.Main

# NOT: java -cp out Main
# NOT: java -cp out src.main.com.airtribe.meditrack.Main
```

**Verify:**
```powershell
# Check if Main.class exists at correct location
dir out\com\airtribe\meditrack\Main.class
```

### Issue 3: "UnsupportedClassVersionError"

**Problem:** `.class` file compiled with newer Java version than runtime.

**Solution:**

```bash
# Check Java versions
java -version     # Runtime version
javac -version    # Compiler version

# Ensure both are Java 17+

# Recompile if needed
javac -encoding UTF-8 -d out -sourcepath src/main/java src/main/java/com/airtribe/meditrack/Main.java
```

### Issue 4: "package com.airtribe.meditrack does not exist"

**Problem:** Incorrect sourcepath or package structure.

**Solution:**

```bash
# Ensure correct source path
javac -encoding UTF-8 \
      -d out \
      -sourcepath src/main/java \
      src/main/java/com/airtribe/meditrack/Main.java

# Verify package declaration in Main.java:
# Should be: package com.airtribe.meditrack;
# NOT: package src.main.com.airtribe.meditrack;
```

### Issue 5: Unicode Characters Display Incorrectly

**Problem:** Console encoding issues (Windows).

**Solution:**

```powershell
# Set console to UTF-8
chcp 65001

# Run with UTF-8 encoding
java -Dfile.encoding=UTF-8 -cp out com.airtribe.meditrack.Main
```

### Issue 6: "Cannot find symbol" errors during compilation

**Problem:** Missing dependencies or incorrect package references.

**Solution:**

```bash
# Clean and recompile
rm -rf out  # macOS/Linux
Remove-Item -Recurse -Force out  # Windows

# Recompile with verbose output
javac -encoding UTF-8 -verbose \
      -d out \
      -sourcepath src/main/java \
      src/main/java/com/airtribe/meditrack/Main.java
```

### Issue 7: Out of Memory Errors

**Problem:** Heap size too small.

**Solution:**

```bash
# Increase heap size
java -Xms512m -Xmx2048m -cp out com.airtribe.meditrack.Main
```

---

## Quick Start Commands

### Windows PowerShell:

```powershell
# Full setup in one go
cd C:\Actions\kaustubhwarke\meditrack-clinic-and-appointment-management-system
New-Item -ItemType Directory -Force -Path out
javac -encoding UTF-8 -d out -sourcepath src/main/java src/main/java/com/airtribe/meditrack/Main.java
java -cp out com.airtribe.meditrack.Main --loadData
```

### macOS/Linux Terminal:

```bash
# Full setup in one go
cd ~/meditrack-clinic-and-appointment-management-system
mkdir -p out
javac -encoding UTF-8 -d out -sourcepath src/main/java src/main/java/com/airtribe/meditrack/Main.java
java -cp out com.airtribe.meditrack.Main --loadData
```

---

## Testing the Installation

### 1. Verify Menu Navigation:

After starting the application:

```
1. Press '1' → Doctor Management
2. Press '1' → Add New Doctor
3. Enter sample data
4. Verify doctor is created
5. Press '0' to go back
```

### 2. Test Search Functionality:

```
1. Press '2' → Patient Management
2. Press '3' → Search Patient
3. Try different search methods
```

### 3. Test Appointment Creation:

```
1. Press '3' → Appointment Management
2. Press '1' → Create Appointment
3. Follow prompts
```

### 4. Verify Design Patterns:

Check console output for:
```
[IdGenerator] Singleton instance initialized  ← Singleton Pattern
[DoctorService] Doctor created: Dr. Smith
```

### 5. Test Exception Handling:

Try invalid inputs:
- Empty name
- Negative age
- Invalid date

Verify proper error messages are displayed.

---

## Performance Tuning

### Recommended JVM Options for MediTrack:

```bash
# Development
java -Xms256m -Xmx1024m \
     -XX:+UseG1GC \
     -cp out com.airtribe.meditrack.Main

# Production (if deployed)
java -Xms512m -Xmx2048m \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:+PrintGCDetails \
     -Xloggc:logs/gc.log \
     -cp out com.airtribe.meditrack.Main
```

### Options Explained:

| Option | Purpose | Recommended Value |
|--------|---------|-------------------|
| `-Xms` | Initial heap size | 256m - 512m |
| `-Xmx` | Maximum heap size | 1024m - 2048m |
| `-Xss` | Thread stack size | 1m - 2m |
| `-XX:+UseG1GC` | Use G1 garbage collector | Recommended for low latency |
| `-XX:MaxGCPauseMillis` | Target GC pause time | 200 (ms) |

---

## Project Structure Reference

```
meditrack-clinic-and-appointment-management-system/
├── docs/                              # Documentation
│   ├── Setup_Instructions.md          # This file
│   ├── JVM_Report.md                 # JVM architecture report
│   └── Design_Decisions.md           # Design documentation
├── src/
│   └── main/
│       └── java/                      # Source root
│           └── com/
│               └── airtribe/
│                   └── meditrack/
│                       ├── Main.java
│                       ├── constants/
│                       ├── entity/
│                       ├── exception/
│                       ├── interfaces/
│                       ├── service/
│                       └── util/
├── out/                               # Compiled classes (generated)
├── data/                              # CSV data files (generated)
├── pom.xml                           # Maven configuration
└── README.md                         # Project overview
```

---

## Next Steps

After successful setup:

1. ✅ **Explore Features:**
   - Navigate through all menu options
   - Create doctors, patients, appointments
   - Generate bills

2. ✅ **Read Documentation:**
   - `README.md` - Project overview
   - `docs/JVM_Report.md` - Understand JVM internals
   - `docs/Design_Decisions.md` - Design rationale

3. ✅ **Explore Source Code:**
   - Start with `Main.java`
   - Review entity classes
   - Study design patterns (Singleton, Factory)
   - Analyze service layer

4. ✅ **Test Advanced Features:**
   - Method overloading in PatientService
   - Deep copy in Patient/Appointment
   - Immutability in BillSummary
   - Streams and lambdas in analytics

---

## Additional Resources

### Official Documentation:
- Oracle Java Documentation: https://docs.oracle.com/javase/17/
- Java Tutorials: https://docs.oracle.com/javase/tutorial/
- JVM Specification: https://docs.oracle.com/javase/specs/

### Learning Resources:
- Effective Java by Joshua Bloch
- Java Concurrency in Practice
- Head First Java

### Tools:
- IntelliJ IDEA: https://www.jetbrains.com/idea/
- Visual Studio Code: https://code.visualstudio.com/
- Eclipse: https://www.eclipse.org/

---

## Support

### Common Commands Cheat Sheet:

```powershell
# Compilation
javac -encoding UTF-8 -d out -sourcepath src/main/java src/main/java/com/airtribe/meditrack/Main.java

# Run (normal)
java -cp out com.airtribe.meditrack.Main

# Run (with sample data)
java -cp out com.airtribe.meditrack.Main --loadData

# Run (with memory settings)
java -Xms256m -Xmx1024m -cp out com.airtribe.meditrack.Main

# Clean compiled files
Remove-Item -Recurse -Force out  # Windows
rm -rf out                        # macOS/Linux
```

---

## Conclusion

You should now have a fully functional MediTrack application running on your system. The setup process covered:

✅ JDK installation and configuration  
✅ Environment variable setup  
✅ Project compilation  
✅ Application execution  
✅ IDE configuration (optional)  
✅ Troubleshooting common issues  

If you encounter any issues not covered here, please refer to:
- `README.md` for project-specific information
- `docs/JVM_Report.md` for JVM architecture details
- `docs/Design_Decisions.md` for design rationale

---

**Document Version:** 1.0  
**Last Updated:** March 8, 2026  
**Project:** MediTrack v1.0.0  
**Maintained By:** MediTrack Development Team

