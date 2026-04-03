# JVM Architecture Report - MediTrack Project

## Executive Summary

This report provides a comprehensive understanding of the Java Virtual Machine (JVM) architecture, explaining how Java achieves platform independence and how the MediTrack application executes on the JVM.

---

## Table of Contents

1. [JVM Overview](#jvm-overview)
2. [JVM Architecture Components](#jvm-architecture-components)
3. [Class Loading Mechanism](#class-loading-mechanism)
4. [Runtime Data Areas](#runtime-data-areas)
5. [Execution Engine](#execution-engine)
6. [JIT Compiler vs Interpreter](#jit-compiler-vs-interpreter)
7. [Garbage Collection](#garbage-collection)
8. [Write Once, Run Anywhere](#write-once-run-anywhere)
9. [MediTrack Application on JVM](#meditrack-application-on-jvm)

---

## JVM Overview

The **Java Virtual Machine (JVM)** is an abstract computing machine that enables Java programs to run on any device or operating system. It acts as an intermediary between compiled Java bytecode and the underlying hardware/operating system.

### Key Characteristics:
- **Platform Independent**: Same bytecode runs on any JVM
- **Memory Management**: Automatic garbage collection
- **Security**: Bytecode verification and security manager
- **Performance**: JIT compilation for optimized execution

### JDK vs JRE vs JVM

```
┌─────────────────────────────────────────┐
│              JDK (Java Development Kit)  │
│  ┌───────────────────────────────────┐  │
│  │        JRE (Java Runtime Env.)    │  │
│  │  ┌─────────────────────────────┐  │  │
│  │  │         JVM                 │  │  │
│  │  │  (Java Virtual Machine)     │  │  │
│  │  └─────────────────────────────┘  │  │
│  │  + Java Class Libraries           │  │
│  └───────────────────────────────────┘  │
│  + Development Tools (javac, javadoc) │
└─────────────────────────────────────────┘
```

- **JVM**: Executes Java bytecode
- **JRE**: JVM + Standard libraries for running Java applications
- **JDK**: JRE + Development tools (compiler, debugger, etc.)

---

## JVM Architecture Components

The JVM consists of three main components:

```
┌─────────────────────────────────────────────┐
│         Java Virtual Machine (JVM)          │
├─────────────────────────────────────────────┤
│                                             │
│  ┌────────────────────────────────────┐    │
│  │      1. Class Loader Subsystem     │    │
│  │   - Loading                        │    │
│  │   - Linking (Verification, Prep.)  │    │
│  │   - Initialization                 │    │
│  └────────────────────────────────────┘    │
│                    ↓                        │
│  ┌────────────────────────────────────┐    │
│  │    2. Runtime Data Areas (Memory)  │    │
│  │   - Method Area                    │    │
│  │   - Heap                           │    │
│  │   - Stack (per thread)             │    │
│  │   - PC Register (per thread)       │    │
│  │   - Native Method Stack            │    │
│  └────────────────────────────────────┘    │
│                    ↓                        │
│  ┌────────────────────────────────────┐    │
│  │      3. Execution Engine           │    │
│  │   - Interpreter                    │    │
│  │   - JIT Compiler                   │    │
│  │   - Garbage Collector              │    │
│  └────────────────────────────────────┘    │
│                                             │
└─────────────────────────────────────────────┘
```

---

## Class Loading Mechanism

The **Class Loader** is responsible for loading Java classes into the JVM. It operates in three phases:

### 1. Loading Phase
- Reads `.class` files and generates binary data
- Stores in Method Area
- Creates `java.lang.Class` object in Heap

### 2. Linking Phase

#### a) Verification
- Ensures bytecode is valid and secure
- Checks structural correctness
- Verifies type safety
- Example: Ensures `Main.java` bytecode is properly formatted

#### b) Preparation
- Allocates memory for static variables
- Assigns default values
- Example: In MediTrack, `Constants.TAX_RATE` gets memory allocated

#### c) Resolution
- Replaces symbolic references with direct references
- Example: When `Main.java` references `DoctorService`, it resolves to actual memory address

### 3. Initialization Phase
- Executes static initializers and static blocks
- **MediTrack Example:**

```java
// Constants.java
static {
    System.out.println("Initializing MediTrack v" + APP_VERSION);
    initialized = true;
}
```

When `Constants` class is first referenced, this static block executes automatically.

### Class Loader Hierarchy

```
Bootstrap Class Loader (C++)
        ↓
Extension Class Loader
        ↓
Application Class Loader
        ↓
Custom Class Loaders
```

**MediTrack classes** are loaded by the **Application Class Loader**.

---

## Runtime Data Areas

The JVM divides memory into five key areas:

### 1. Method Area (Shared across all threads)

**Stores:**
- Class metadata (class name, parent class, methods, fields)
- Static variables
- Constant pool
- Method bytecode

**MediTrack Example:**
```java
public class Constants {
    public static final double TAX_RATE = 0.18;  // Stored in Method Area
    private static int totalObjectsCreated = 0;  // Stored in Method Area
}
```

All `Constants.TAX_RATE`, class definitions, and static variables are stored here.

**Memory Characteristics:**
- Created on JVM startup
- Shared by all threads
- Garbage collected when classes are unloaded

### 2. Heap Area (Shared across all threads)

**Stores:**
- All objects and their instance variables
- Arrays

**MediTrack Example:**
```java
Doctor doctor = new Doctor("Dr. Smith", 45, "D2001", Specialization.CARDIOLOGY, 1000.0);
Patient patient = new Patient("John Doe", 35, "P1001", "O+");
```

These objects (`doctor`, `patient`) and their instance fields are stored in the **Heap**.

**Memory Layout:**
```
Heap Memory
├── Young Generation
│   ├── Eden Space (new objects)
│   └── Survivor Spaces (S0, S1)
└── Old Generation (long-lived objects)
```

**Garbage Collection:** Objects no longer referenced are automatically reclaimed.

### 3. Stack Area (Per Thread)

**Stores:**
- Method call frames
- Local variables
- Partial results
- Method return values

**MediTrack Example:**
```java
public void createDoctor(String name, int age) {
    String id = idGenerator.generateDoctorId();  // 'id' is a local variable
    Doctor doctor = new Doctor(name, age, id);    // 'doctor' reference on stack
    // doctor object itself is on Heap, but the reference is on Stack
}
```

**Stack Frame Components:**
- **Local Variable Array**: Stores method parameters and local variables
- **Operand Stack**: Workspace for operations
- **Frame Data**: Class reference, exception table

**Stack Memory Characteristics:**
- Created per thread
- LIFO (Last In, First Out) structure
- Destroyed when thread completes
- Throws `StackOverflowError` if full

### 4. PC Register (Per Thread)

**Program Counter Register:**
- Stores address of current JVM instruction being executed
- Each thread has its own PC Register
- Points to the Method Area where instruction is located

**MediTrack Example:**
When executing `Main.main()`, the PC Register tracks which bytecode instruction is currently executing.

### 5. Native Method Stack (Per Thread)

**Stores:**
- Native method information
- Used for methods written in languages like C/C++

**MediTrack Example:**
When calling `System.out.println()`, which internally uses native code, this stack is used.

---

## Execution Engine

The Execution Engine executes the bytecode instructions. It consists of:

### Components:

```
┌────────────────────────────────┐
│      Execution Engine          │
├────────────────────────────────┤
│  1. Interpreter                │
│     - Reads bytecode           │
│     - Executes line by line    │
│                                │
│  2. JIT Compiler               │
│     - Compiles hot methods     │
│     - Optimizes performance    │
│                                │
│  3. Garbage Collector          │
│     - Reclaims memory          │
│     - Removes unused objects   │
└────────────────────────────────┘
```

### 1. Interpreter
- Reads bytecode instructions one by one
- Interprets and executes them
- **Slow** for repeated code

### 2. Just-In-Time (JIT) Compiler
- Identifies "hot spots" (frequently executed code)
- Compiles bytecode to native machine code
- Caches compiled code for reuse
- **Fast** execution after compilation

### 3. Garbage Collector
- Automatically manages memory
- Removes unreferenced objects
- Prevents memory leaks

---

## JIT Compiler vs Interpreter

### Interpreter

**How it works:**
1. Reads bytecode instruction
2. Interprets it
3. Executes it
4. Moves to next instruction

**Advantages:**
- ✅ Fast startup time
- ✅ Low memory overhead
- ✅ Good for code executed once

**Disadvantages:**
- ❌ Slow for repeated execution
- ❌ No optimization

**MediTrack Example:**
When you first run `Main.main()`, the interpreter executes the bytecode line by line.

### JIT Compiler

**How it works:**
1. Monitors code execution
2. Identifies "hot methods" (executed frequently)
3. Compiles bytecode → native machine code
4. Caches compiled code
5. Directly executes native code on subsequent calls

**Advantages:**
- ✅ Very fast execution after compilation
- ✅ Platform-specific optimizations
- ✅ Adaptive optimization

**Disadvantages:**
- ❌ Compilation overhead
- ❌ Higher memory usage

**MediTrack Example:**
If you repeatedly create appointments in a loop:
```java
for (int i = 0; i < 10000; i++) {
    appointmentService.createAppointment(patientId, doctorId, dateTime);
}
```
The JIT compiler will detect this hot method and compile it to native code for faster execution.

### Hybrid Approach

Modern JVMs use **both**:
1. **Interpreter** for initial execution (fast startup)
2. **JIT Compiler** for hot methods (optimized performance)

**Performance Comparison:**

| Execution Type | First Call | 10th Call | 1000th Call |
|---------------|-----------|-----------|-------------|
| Interpreter Only | Medium | Medium | Medium |
| JIT Only | Slow (compile) | Fast | Fast |
| **Hybrid (JVM)** | **Fast** | **Fast** | **Very Fast** |

---

## Garbage Collection

**Purpose:** Automatic memory management - removes unused objects from Heap.

### How GC Works:

1. **Mark Phase**: Identifies reachable objects (still referenced)
2. **Sweep Phase**: Removes unreachable objects
3. **Compact Phase**: Defragments memory

### GC Algorithms in Modern JVMs:

- **Serial GC**: Single-threaded (small applications)
- **Parallel GC**: Multiple threads (default for many JVMs)
- **G1 GC**: Garbage First (low latency)
- **ZGC**: Ultra-low latency (large heaps)

### MediTrack Example:

```java
public void processManyAppointments() {
    for (int i = 0; i < 1000; i++) {
        Appointment temp = new Appointment(...);
        // Process appointment
        // 'temp' goes out of scope after each iteration
        // GC will eventually reclaim memory from unreferenced objects
    }
}
```

Objects created in the loop become eligible for garbage collection when no longer referenced.

### Triggering GC:
```java
System.gc();  // Suggests JVM to run GC (not guaranteed)
```

---

## Write Once, Run Anywhere (WORA)

### The Java Promise

Java's slogan "Write Once, Run Anywhere" means you can compile Java code once and run it on any platform with a JVM.

### How It Works:

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│  Main.java   │────▶│  javac       │────▶│  Main.class  │
│ (Source code)│     │ (Compiler)   │     │ (Bytecode)   │
└──────────────┘     └──────────────┘     └──────────────┘
                                                  │
                          ┌───────────────────────┴───────────────────────┐
                          │                                               │
                          ▼                                               ▼
                    ┌──────────┐                                   ┌──────────┐
                    │ Windows  │                                   │  Linux   │
                    │   JVM    │                                   │   JVM    │
                    │          │                                   │          │
                    │ Executes │                                   │ Executes │
                    │ Bytecode │                                   │ Bytecode │
                    └──────────┘                                   └──────────┘
                          │                                               │
                          ▼                                               ▼
                    ┌──────────┐                                   ┌──────────┐
                    │ Windows  │                                   │  Linux   │
                    │   OS     │                                   │   OS     │
                    └──────────┘                                   └──────────┘
```

### Step-by-Step Process:

1. **Write**: Developer writes `Main.java` (platform-independent)
2. **Compile**: `javac` compiles to `Main.class` bytecode (platform-independent)
3. **Distribute**: Share `.class` files (not `.java`)
4. **Execute**: JVM on any platform executes the bytecode
5. **Native Execution**: JVM translates to platform-specific machine code

### MediTrack Example:

```bash
# Compile ONCE on Windows
javac -d out src/main/java/com/airtribe/meditrack/Main.java

# Run on Windows
java -cp out com.airtribe.meditrack.Main

# Same .class files can run on Linux
java -cp out com.airtribe.meditrack.Main

# Same .class files can run on macOS
java -cp out com.airtribe.meditrack.Main
```

The **same bytecode** (`.class` files) run on all three platforms without recompilation!

### Why It Works:

| Component | Platform-Specific? | Explanation |
|-----------|-------------------|-------------|
| Source Code (`.java`) | ❌ No | Pure Java syntax |
| Bytecode (`.class`) | ❌ No | JVM instruction set (universal) |
| JVM | ✅ Yes | Tailored for each OS |
| Native Machine Code | ✅ Yes | Generated by JVM for specific CPU |

### Benefits:

✅ **Portability**: Write once, deploy everywhere  
✅ **Consistency**: Same behavior across platforms  
✅ **Cost Efficiency**: No need to maintain multiple codebases  
✅ **Easy Distribution**: Share bytecode, not source  

---

## Class Loading Mechanism

### Three-Phase Process:

#### Phase 1: Loading

**What Happens:**
1. Class Loader locates `.class` file
2. Reads bytecode into memory
3. Stores in Method Area
4. Creates `java.lang.Class` object in Heap

**MediTrack Example:**
```java
// When this line executes
Doctor doctor = new Doctor(...);

// Class Loader:
1. Checks if Doctor.class is already loaded
2. If not, loads Doctor.class bytecode
3. Stores Doctor class metadata in Method Area
4. Creates Class<Doctor> object in Heap
```

#### Phase 2: Linking

**Verification:**
- Verifies bytecode structure
- Checks type safety
- Ensures no illegal operations
- Example: Verifies `Doctor` class extends `Person` correctly

**Preparation:**
- Allocates memory for static variables
- Assigns default values
- Example: `Person.totalPersonsCreated = 0` (default)

**Resolution:**
- Converts symbolic references to direct references
- Example: `doctorService.createDoctor()` resolved to actual method address

#### Phase 3: Initialization

**Executes:**
- Static initializers
- Static blocks

**MediTrack Example:**
```java
public class Constants {
    public static final double TAX_RATE = 0.18;  // Step 1: Assignment
    
    static {  // Step 2: Static block execution
        System.out.println("Initializing MediTrack v" + APP_VERSION);
        initialized = true;
    }
}
```

**Order of Execution:**
1. Static variable initialization
2. Static block execution (in order of appearance)

### Class Loader Hierarchy:

```
┌──────────────────────────────────┐
│  Bootstrap Class Loader          │
│  - Loads rt.jar (java.lang.*)    │
│  - Written in native code (C++)  │
└──────────────────────────────────┘
            ↓ parent
┌──────────────────────────────────┐
│  Extension Class Loader          │
│  - Loads ext directory           │
│  - javax.*, java.security.*      │
└──────────────────────────────────┘
            ↓ parent
┌──────────────────────────────────┐
│  Application Class Loader        │
│  - Loads CLASSPATH classes       │
│  - MediTrack classes loaded here │
└──────────────────────────────────┘
```

**Delegation Model:**
1. Application Class Loader asks Extension Class Loader
2. Extension Class Loader asks Bootstrap Class Loader
3. If not found, Application Class Loader loads it

---

## Runtime Data Areas

### 1. Method Area (Metaspace in Java 8+)

**Contents:**
- Class structures (metadata)
- Method bytecode
- Static variables
- Constant pool

**MediTrack Storage:**
```java
// Stored in Method Area:
- Constants.TAX_RATE (static)
- Person.totalPersonsCreated (static)
- Class metadata for Doctor, Patient, Appointment, etc.
- Method bytecode for all methods
```

**Size Configuration:**
```bash
java -XX:MaxMetaspaceSize=256m -cp out com.airtribe.meditrack.Main
```

### 2. Heap Area

**Contents:**
- All objects
- Instance variables
- Arrays

**MediTrack Storage:**
```java
// These objects are stored in Heap:
Doctor doctor = new Doctor(...);           // Doctor object in Heap
Patient patient = new Patient(...);        // Patient object in Heap
List<String> allergies = new ArrayList<>(); // ArrayList object in Heap
```

**Heap Generations:**

```
┌────────────────────────────────────────┐
│           Heap Memory                  │
├────────────────────────────────────────┤
│  Young Generation (Minor GC)           │
│  ├── Eden Space (new objects)          │
│  └── Survivor Spaces (S0, S1)          │
├────────────────────────────────────────┤
│  Old Generation (Major GC)             │
│  └── Long-lived objects                │
└────────────────────────────────────────┘
```

**Object Lifecycle:**
1. Created in **Eden Space**
2. Survives GC → moves to **Survivor Space**
3. Survives multiple GCs → moves to **Old Generation**

**Size Configuration:**
```bash
java -Xms512m -Xmx2048m -cp out com.airtribe.meditrack.Main
# -Xms: Initial heap size (512 MB)
# -Xmx: Maximum heap size (2048 MB)
```

### 3. Stack Area (Per Thread)

**Contents:**
- Method call frames (one per method call)
- Local variables
- Partial computation results
- Return addresses

**Stack Frame Structure:**

```
┌──────────────────────────────┐  ← Top of Stack (current method)
│  Frame: createDoctor()        │
│  - Local vars: name, age, id  │
│  - Operand stack             │
│  - Return address            │
├──────────────────────────────┤
│  Frame: main()               │
│  - Local vars: scanner, ...  │
│  - Operand stack             │
└──────────────────────────────┘  ← Bottom of Stack
```

**MediTrack Example:**
```java
public static void main(String[] args) {  // Frame 1
    DoctorService service = new DoctorService();
    service.createDoctor("Dr. Smith", 45, ...);  // Frame 2 pushed
}

public Doctor createDoctor(String name, int age, ...) {  // Frame 2
    String id = idGenerator.generateDoctorId();  // Frame 3 pushed
    return new Doctor(name, age, id);
}  // Frame 2 popped
```

**Memory Characteristics:**
- Fixed size per thread (default ~1 MB)
- LIFO structure
- Automatic cleanup when method returns
- `StackOverflowError` if too many nested calls

**Size Configuration:**
```bash
java -Xss2m -cp out com.airtribe.meditrack.Main
# -Xss: Stack size per thread (2 MB)
```

### 4. PC Register (Program Counter)

**Purpose:**
- Tracks current instruction execution
- One per thread

**Contents:**
- Address of current instruction in Method Area
- For native methods: undefined

**MediTrack Example:**
When executing:
```java
int age = patient.getAge();  // PC points to this instruction
String name = patient.getName();  // PC moves to next instruction
```

### 5. Native Method Stack

**Purpose:**
- Supports native methods (written in C/C++)
- Used by JNI (Java Native Interface)

**MediTrack Example:**
```java
System.out.println("Hello");  // Uses native method internally
LocalDateTime.now();          // May use native time functions
```

---

## JIT Compiler vs Interpreter

### Detailed Comparison

| Aspect | Interpreter | JIT Compiler |
|--------|------------|--------------|
| **Execution** | Line by line | Compiles entire method |
| **Speed** | Slower | Faster (after compilation) |
| **Memory** | Lower | Higher (stores compiled code) |
| **Startup** | Faster | Slower (compilation overhead) |
| **Optimization** | None | Aggressive |
| **Best For** | Code run once | Code run repeatedly |

### JIT Compilation Process:

```
Bytecode → Profiler → Hot Method Detection → JIT Compiler → Native Code → Cache
```

**Profiler monitors:**
- Method call frequency
- Loop iterations
- Branch predictions

**Optimization Techniques:**
- **Inlining**: Replaces method calls with method body
- **Dead Code Elimination**: Removes unused code
- **Loop Unrolling**: Optimizes loops
- **Escape Analysis**: Stack allocation instead of heap

### MediTrack JIT Example:

```java
// This method is called frequently in the menu loop
public List<Doctor> getAllDoctors() {
    return doctorStore.getAll();
}

// JIT Compiler:
1. Detects this is a "hot method" (called 1000+ times)
2. Compiles to native x86/ARM code
3. Caches compiled version
4. Future calls execute native code directly (very fast)
```

### Monitoring JIT:

```bash
# Run with JIT logging
java -XX:+PrintCompilation -cp out com.airtribe.meditrack.Main

# Output shows which methods JIT compiled:
# 72    1       java.lang.String::length (6 bytes)
# 103   2       com.airtribe.meditrack.service.DoctorService::getAllDoctors (10 bytes)
```

---

## Garbage Collection Deep Dive

### GC Process

**1. Mark Phase:**
- Starts from "GC Roots" (static variables, active thread stacks)
- Marks all reachable objects
- Unreachable objects = garbage

**2. Sweep Phase:**
- Removes unmarked objects
- Frees memory

**3. Compact Phase:**
- Defragments memory
- Moves objects together

### GC Roots in MediTrack:

```java
public class Main {
    private static DoctorService doctorService;  // GC Root (static variable)
    private static Scanner scanner;              // GC Root (static variable)
    
    public static void main(String[] args) {     // GC Root (active thread)
        Doctor doctor = new Doctor(...);         // Reachable from stack
        // doctor is reachable while main() executes
    }
    // doctor becomes unreachable when main() ends → eligible for GC
}
```

### Generational GC:

**Why generations?**
- Most objects die young (temporary objects)
- Few objects live long (cached data)
- Optimize GC for these patterns

**MediTrack Object Lifecycle:**

```java
// Short-lived object (Young Generation)
BillSummary summary = BillSummary.fromBill(bill);
// Used once, then discarded → Stays in Young Gen → GC quickly

// Long-lived object (Old Generation)
DataStore<Doctor> doctorStore = new DataStore<>("Doctors");
// Lives throughout application → Promoted to Old Gen → GC rarely
```

### GC Types:

**Minor GC:**
- Collects Young Generation
- Fast (milliseconds)
- Frequent

**Major GC (Full GC):**
- Collects entire Heap
- Slow (seconds)
- Infrequent

### MediTrack Memory Profile:

```
Young Generation:
- Temporary appointments
- Search results
- Bill summaries
- Method call objects

Old Generation:
- DataStore instances (doctorStore, patientStore)
- Long-lived service objects
- Cached data
```

---

## MediTrack Application on JVM

### Execution Flow

**1. JVM Startup:**
```bash
java -cp out com.airtribe.meditrack.Main
```

**2. Class Loading:**
```
Bootstrap CL loads:        java.lang.*, java.util.*
Application CL loads:      com.airtribe.meditrack.Main
                          com.airtribe.meditrack.constants.Constants
                          (Triggers static block in Constants)
```

**3. Static Initialization:**
```java
// Constants.java static block executes
static {
    System.out.println("Initializing MediTrack v1.0.0");
    initialized = true;
}
```

**4. Main Method Execution:**
```java
public static void main(String[] args) {
    // Stack frame created for main()
    doctorService = new DoctorService();  // Object created in Heap
    patientService = new PatientService();  // Object created in Heap
    // ...
}
```

### Memory Layout During Execution:

```
METHOD AREA:
├── Constants class metadata
│   └── TAX_RATE = 0.18 (static)
├── Person class metadata
│   └── totalPersonsCreated = 0 (static)
├── Doctor class metadata
├── Main class metadata
└── All method bytecode

HEAP:
├── DoctorService instance
├── PatientService instance
├── AppointmentService instance
├── DataStore<Doctor> instance
├── DataStore<Patient> instance
├── Doctor objects (created by user)
├── Patient objects (created by user)
└── Appointment objects (created by user)

STACK (main thread):
├── Frame: main()
│   └── args, scanner, doctorService, patientService (references)
├── Frame: createDoctor() [when called]
│   └── name, age, id (local variables)
└── Frame: generateDoctorId() [when called]
    └── counter (local variable)
```

### Performance Optimizations:

**JIT Compilation in MediTrack:**

Hot methods that get JIT compiled:
- `doctorService.getAllDoctors()` - called in menu loop
- `patientStore.getAll()` - called frequently
- `Validator.validateName()` - called for every name input
- `DateUtil.formatDateTime()` - called for display

**Garbage Collection:**

Temporary objects collected frequently:
- Search result lists
- Input validation strings
- Display format strings
- Temporary appointment clones

Long-lived objects kept in Old Gen:
- Service instances (doctorService, patientService)
- DataStore instances
- Static Constants

### JVM Tuning for MediTrack:

```bash
# Optimize for desktop application
java -Xms256m \                    # Initial heap: 256 MB
     -Xmx1024m \                   # Max heap: 1 GB
     -XX:+UseG1GC \                # Use G1 garbage collector
     -XX:MaxGCPauseMillis=200 \    # Target 200ms GC pauses
     -cp out com.airtribe.meditrack.Main
```

---

## JVM Configuration for MediTrack

### Recommended Settings:

```bash
# Development
java -Xms256m -Xmx1024m \
     -XX:+UseG1GC \
     -cp out com.airtribe.meditrack.Main

# Production (with monitoring)
java -Xms512m -Xmx2048m \
     -XX:+UseG1GC \
     -XX:+PrintGCDetails \
     -XX:+PrintGCDateStamps \
     -Xloggc:gc.log \
     -cp out com.airtribe.meditrack.Main
```

### Common JVM Options:

| Option | Purpose | Example |
|--------|---------|---------|
| `-Xms` | Initial heap size | `-Xms512m` |
| `-Xmx` | Maximum heap size | `-Xmx2048m` |
| `-Xss` | Thread stack size | `-Xss2m` |
| `-XX:+UseG1GC` | Use G1 Garbage Collector | |
| `-XX:MaxMetaspaceSize` | Max metaspace size | `-XX:MaxMetaspaceSize=256m` |
| `-XX:+PrintGCDetails` | Print GC logs | |

---

## Performance Analysis

### Memory Usage Estimation for MediTrack:

**Static/Constant Data (Method Area):**
- ~10 classes × ~10 KB = ~100 KB
- Static variables: ~1 KB
- **Total: ~100 KB**

**Service Objects (Heap - Old Gen):**
- DoctorService, PatientService, etc.: ~10 KB each
- DataStore instances: ~5 KB each
- **Total: ~50 KB**

**Entity Objects (Heap):**
- 100 Doctors: 100 × 1 KB = ~100 KB
- 500 Patients: 500 × 2 KB = ~1 MB
- 1000 Appointments: 1000 × 2 KB = ~2 MB
- **Total: ~3.1 MB**

**Stack (per thread):**
- Main thread: ~1 MB
- **Total: ~1 MB**

**Overall Memory:** ~4.3 MB for typical usage

### JIT Impact:

**Cold Start (first run):**
- Interpreted execution: ~500 ms for menu display
- Higher CPU, lower memory

**Warm Start (after 1000 menu cycles):**
- JIT compiled: ~50 ms for menu display
- Lower CPU, higher memory (compiled code cache)

**10x speedup** for hot methods after JIT compilation!

---

## Conclusion

The JVM provides a robust, platform-independent execution environment for Java applications. Key takeaways:

1. **Class Loader**: Loads, links, and initializes classes dynamically
2. **Memory Areas**: Organized into Method Area, Heap, Stack, PC Register, Native Stack
3. **Execution Engine**: Hybrid interpreter + JIT compiler for optimal performance
4. **Garbage Collection**: Automatic memory management with generational approach
5. **Platform Independence**: Bytecode runs on any JVM, achieving WORA

The MediTrack application leverages all these JVM features:
- Static initialization for configuration
- Heap for entity storage
- Stack for method execution
- JIT for performance optimization
- GC for automatic memory management

---

## References

- Oracle JVM Specification: https://docs.oracle.com/javase/specs/jvms/se17/html/
- Java Performance Tuning Guide
- Garbage Collection Handbook
- MediTrack Project Documentation

---

**Report Generated:** March 8, 2026  
**MediTrack Version:** 1.0.0  
**Java Version:** 17

