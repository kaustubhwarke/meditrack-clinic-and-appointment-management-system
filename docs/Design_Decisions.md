# Design Decisions - MediTrack Project

## Document Overview

This document explains the architectural and design decisions made during the development of MediTrack Clinic and Appointment Management System. It provides rationale for technology choices, design patterns, data structures, and implementation approaches.

---

## Table of Contents

1. [Project Architecture](#project-architecture)
2. [Package Organization](#package-organization)
3. [Design Patterns](#design-patterns)
4. [OOP Design Decisions](#oop-design-decisions)
5. [Data Structure Choices](#data-structure-choices)
6. [Exception Handling Strategy](#exception-handling-strategy)
7. [Memory Management](#memory-management)
8. [Concurrency Considerations](#concurrency-considerations)
9. [Persistence Strategy](#persistence-strategy)
10. [Future Scalability](#future-scalability)

---

## Project Architecture

### Layered Architecture

MediTrack follows a **layered architecture** pattern to separate concerns and improve maintainability:

```
┌─────────────────────────────────────────┐
│     Presentation Layer (Main.java)      │  ← User Interface
├─────────────────────────────────────────┤
│     Service Layer (service/*)           │  ← Business Logic
├─────────────────────────────────────────┤
│     Entity Layer (entity/*)             │  ← Domain Models
├─────────────────────────────────────────┤
│     Data Access Layer (util/DataStore)  │  ← Data Management
└─────────────────────────────────────────┘
```

### Why Layered Architecture?

**Advantages:**
✅ **Separation of Concerns**: Each layer has a specific responsibility  
✅ **Maintainability**: Changes in one layer don't affect others  
✅ **Testability**: Each layer can be tested independently  
✅ **Reusability**: Service layer methods can be reused across UI components  

**Example:**
```java
// Presentation Layer (Main.java)
private static void addDoctor() throws InvalidDataException {
    String name = getStringInput("Enter doctor name");
    int age = getIntInput("Enter age");
    // Call service layer
    Doctor doctor = doctorService.createDoctor(name, age, spec, fee);
}

// Service Layer (DoctorService.java)
public Doctor createDoctor(String name, int age, ...) {
    // Business logic
    String id = idGenerator.generateDoctorId();
    Doctor doctor = new Doctor(name, age, id, ...);
    doctorStore.add(id, doctor);
    return doctor;
}

// Data Layer (DataStore.java)
public boolean add(String id, T entity) {
    // Data storage logic
    return dataMap.put(id, entity);
}
```

**Rationale:**  
Layered architecture makes the codebase more professional and follows industry best practices. It allows future enhancements like switching from in-memory storage to database without touching business logic.

---

## Package Organization

### Package Structure

```
com.airtribe.meditrack/
├── constants/          # Enums, constants, configurations
├── entity/            # Domain models (Doctor, Patient, etc.)
├── exception/         # Custom exceptions
├── interfaces/        # Interfaces (Payable, Searchable)
├── service/           # Business logic layer
└── util/              # Helper utilities
```

### Design Decision: Package by Feature vs Package by Layer

**Chosen:** Hybrid approach - Package by Layer at top level

**Rationale:**

✅ **Clear Responsibility**: Easy to locate entity vs service vs utility  
✅ **Industry Standard**: Common in Java enterprise applications  
✅ **Educational Value**: Demonstrates proper package organization  
✅ **Scalability**: Easy to add new entities or services  

**Alternative Considered:** Package by feature (e.g., `doctor/`, `appointment/`)
- ❌ Less clear for educational purposes
- ❌ Harder to show OOP hierarchy

### Naming Convention Decisions

| Component | Convention | Example | Rationale |
|-----------|-----------|---------|-----------|
| **Entity Classes** | Noun, singular | `Doctor`, `Patient` | Represents single domain object |
| **Service Classes** | Noun + "Service" | `DoctorService` | Indicates business logic layer |
| **Utility Classes** | Noun/Function + "Util" | `DateUtil`, `Validator` | Helper/utility nature |
| **Interfaces** | Adjective (able) | `Payable`, `Searchable` | Describes capability |
| **Exceptions** | Noun + "Exception" | `InvalidDataException` | Standard Java convention |
| **Enums** | Singular noun | `Specialization`, `AppointmentStatus` | Type-safe constants |

---

## Design Patterns

### 1. Singleton Pattern (IdGenerator)

**Implementation:**
```java
public class IdGenerator {
    private static final IdGenerator INSTANCE = new IdGenerator();  // Eager initialization
    
    private IdGenerator() {}  // Private constructor
    
    public static IdGenerator getInstance() {
        return INSTANCE;
    }
}
```

**Design Decisions:**

**Choice: Eager Initialization**

**Rationale:**
✅ **Thread-safe by default**: Instance created at class loading (no synchronization needed)  
✅ **Simple implementation**: No double-checked locking complexity  
✅ **Guaranteed initialization**: Instance ready when first accessed  
✅ **Small memory footprint**: IdGenerator is lightweight  

**Alternative Considered:** Lazy initialization with double-checked locking
```java
private static volatile IdGenerator instance;
public static IdGenerator getInstance() {
    if (instance == null) {
        synchronized (IdGenerator.class) {
            if (instance == null) {
                instance = new IdGenerator();
            }
        }
    }
    return instance;
}
```
❌ **Rejected because:** Unnecessary complexity for lightweight object

**Why Singleton for IdGenerator?**
- Ensures **globally unique IDs** across application
- Single source of truth for ID counters
- Prevents duplicate ID generation
- Thread-safe counter management with `AtomicInteger`

### 2. Factory Pattern (BillFactory)

**Implementation:**
```java
public class BillFactory {
    public enum BillType { STANDARD, EMERGENCY, FOLLOW_UP }
    
    public static Bill createBill(BillType type, String billId, Appointment apt) {
        Bill bill = new Bill(billId, apt);
        switch (type) {
            case STANDARD:
                bill.setConsultationFee(baseFee);
                break;
            case EMERGENCY:
                bill.setConsultationFee(baseFee * 1.5);  // 50% surcharge
                bill.setAdditionalCharges(200.0);
                break;
            case FOLLOW_UP:
                bill.setConsultationFee(baseFee);
                bill.setDiscount(baseFee * 0.3);  // 30% discount
                break;
        }
        return bill;
    }
}
```

**Design Decisions:**

**Choice: Simple Factory (static methods)**

**Rationale:**
✅ **Encapsulates object creation**: Client doesn't need to know bill calculation logic  
✅ **Easy to extend**: Add new bill types without changing client code  
✅ **Centralized logic**: All billing strategies in one place  
✅ **Type-safe**: Enum ensures valid bill types  

**Alternative Considered:** Strategy Pattern with interface hierarchy
```java
interface BillingStrategy {
    double calculateFee(double baseFee);
}
class StandardBilling implements BillingStrategy { ... }
class EmergencyBilling implements BillingStrategy { ... }
```
❌ **Rejected because:** Over-engineering for current requirements; factory is simpler

**Why Factory for Bills?**
- Different **billing strategies** (Standard, Emergency, Follow-up)
- **Flexible pricing**: Easy to add seasonal discounts, insurance billing, etc.
- **Business logic encapsulation**: Calculation rules hidden from client

### 3. Template Method Pattern (Implied)

**Implementation in MedicalEntity:**
```java
public abstract class MedicalEntity {
    protected long createdTimestamp;
    protected long lastModifiedTimestamp;
    
    public MedicalEntity() {
        this.createdTimestamp = System.currentTimeMillis();
        this.lastModifiedTimestamp = this.createdTimestamp;
    }
    
    protected void updateModifiedTimestamp() {
        this.lastModifiedTimestamp = System.currentTimeMillis();
    }
    
    public abstract String getEntityType();  // Template method
    public abstract String getDisplayInfo(); // Template method
}
```

**Rationale:**
✅ **Common behavior in base class**: Timestamp management  
✅ **Forced implementation in subclasses**: `getEntityType()`, `getDisplayInfo()`  
✅ **Code reuse**: Avoids duplication in Doctor, Patient, Appointment  

---

## OOP Design Decisions

### 1. Inheritance Hierarchy

**Design:**
```
MedicalEntity (abstract)
    ↓
Person
    ↓
    ├── Doctor
    └── Patient
```

**Decision: Two-level inheritance**

**Rationale:**
✅ **Logical hierarchy**: All medical entities share common attributes (ID, timestamps)  
✅ **Person abstraction**: Doctors and patients are both people  
✅ **Avoids deep hierarchies**: More than 2-3 levels becomes hard to maintain  

**Alternative Considered:** Single inheritance (Person → Doctor, Person → Patient)
❌ **Rejected because:** Missing common `MedicalEntity` behavior (timestamps, validation)

**Why separate Person and MedicalEntity?**
- `MedicalEntity`: Technical concerns (ID, timestamps, serialization)
- `Person`: Domain concerns (name, age, contact info)
- **Separation of technical and domain concerns**

### 2. Interface Design

**Interfaces Created:**
- `Searchable`: Search functionality
- `Payable`: Payment/billing functionality

**Design Decision: Interface-based polymorphism**

```java
// Searchable
public interface Searchable {
    boolean matches(String query);
    default boolean partialMatch(String query) { ... }
    String getId();
}

// Implementations
public class Doctor extends Person implements Searchable { ... }
public class Patient extends Person implements Searchable { ... }
```

**Rationale:**
✅ **Multiple inheritance**: Java allows multiple interfaces  
✅ **Behavioral contracts**: Defines what an object can do  
✅ **Flexible design**: Any class can implement Searchable  
✅ **Default methods**: Shared implementation without abstract class  

**Why default methods?**
```java
default boolean partialMatch(String query) {
    return getSearchableContent().contains(query.toLowerCase());
}
```
- Provides common implementation
- Subclasses can override if needed
- Reduces code duplication
- Added in Java 8, demonstrates modern Java features

### 3. Encapsulation Strategy

**Decision: Private fields + Public getters/setters + Centralized validation**

```java
public class Person extends MedicalEntity {
    private String name;  // Private field
    
    public void setName(String name) throws InvalidDataException {
        Validator.validateName(name, "Name");  // Centralized validation
        this.name = name.trim();
        updateModifiedTimestamp();
    }
}
```

**Rationale:**
✅ **Data protection**: Fields cannot be modified directly  
✅ **Validation enforcement**: All changes go through setters  
✅ **Centralized logic**: `Validator` class handles all validation  
✅ **Maintainability**: Change validation rules in one place  

**Alternative Considered:** Validation in each setter
❌ **Rejected because:** Code duplication, inconsistent validation

### 4. Polymorphism Implementation

**Method Overloading in PatientService:**
```java
public Patient searchPatient(String id)                           // Version 1
public List<Patient> searchPatient(String name, boolean exact)    // Version 2
public List<Patient> searchPatient(int age)                       // Version 3
public List<Patient> searchPatient(int minAge, int maxAge)        // Version 4
public List<Patient> searchPatient(String name, int age)          // Version 5
```

**Rationale:**
✅ **Flexibility**: Multiple ways to search based on user needs  
✅ **Intuitive API**: Method name same, parameters vary  
✅ **Type safety**: Compiler ensures correct parameter types  
✅ **Educational**: Clearly demonstrates polymorphism  

**Design Decision:** Overloading in Service layer (not Entity layer)

**Why?**
- Service layer handles business logic
- Entity layer stays clean and focused
- Separation of concerns

---

## Data Structure Choices

### 1. Generic DataStore<T>

**Implementation:**
```java
public class DataStore<T> {
    private final Map<String, T> dataMap;
    
    public boolean add(String id, T entity) { ... }
    public T get(String id) { ... }
    public List<T> findAll(Predicate<T> predicate) { ... }
}
```

**Design Decisions:**

**Choice: HashMap for internal storage**

**Rationale:**
✅ **O(1) lookup by ID**: Fast retrieval using `get(id)`  
✅ **Unique keys**: Prevents duplicate IDs automatically  
✅ **Memory efficient**: Only stores entities, not indices  
✅ **Type-safe**: Generic `<T>` ensures compile-time safety  

**Alternative Considered:** ArrayList
```java
private final List<T> dataList;
```
❌ **Rejected because:** O(n) lookup, no ID uniqueness enforcement

**Why Generic DataStore?**
- **Code reuse**: Same storage logic for Doctor, Patient, Appointment
- **Type safety**: `DataStore<Doctor>` vs `DataStore<Patient>` are different types
- **Demonstrates generics**: Educational requirement

**Stream Integration:**
```java
public List<T> findAll(Predicate<T> predicate) {
    return dataMap.values().stream()
            .filter(predicate)
            .collect(Collectors.toList());
}
```
- Leverages Java 8 streams for filtering
- Functional programming approach
- Lazy evaluation for performance

### 2. Collections in Entity Classes

**Patient class collections:**
```java
private List<String> medicalHistory;
private List<String> allergies;
```

**Choice: ArrayList vs LinkedList**

**Decision: ArrayList**

**Rationale:**
✅ **Random access**: Fast retrieval by index  
✅ **Memory efficient**: Lower overhead than LinkedList  
✅ **Common use case**: Medical history typically read more than modified  

| Operation | ArrayList | LinkedList | Winner |
|-----------|-----------|------------|--------|
| Get by index | O(1) | O(n) | ArrayList ✅ |
| Add to end | O(1) amortized | O(1) | Tie |
| Insert at beginning | O(n) | O(1) | LinkedList |
| Memory overhead | Low | High (node objects) | ArrayList ✅ |

**For MediTrack:** Random access is more common than insertions at beginning.

### 3. Defensive Copying

**Implementation:**
```java
public List<String> getMedicalHistory() {
    return new ArrayList<>(medicalHistory);  // Returns copy
}

public void setMedicalHistory(List<String> medicalHistory) {
    this.medicalHistory = new ArrayList<>(medicalHistory);  // Stores copy
}
```

**Rationale:**
✅ **Encapsulation**: Prevents external modification of internal state  
✅ **Data integrity**: Original list cannot be changed from outside  
✅ **Thread safety**: Reduces concurrent modification risks  

**Trade-off:**
- ⚠️ **Memory overhead**: Creates copies of lists
- ⚠️ **Performance impact**: Extra allocation/copying

**Decision:** Worth the trade-off for data integrity in medical application.

---

## Design Patterns

### Singleton Pattern - Deep Dive

**Used in:** `IdGenerator`

**Problem Solved:**
- Need globally unique IDs across entire application
- Multiple instances would generate conflicting IDs

**Design Decisions:**

**1. Eager vs Lazy Initialization**

**Chosen: Eager Initialization**
```java
private static final IdGenerator INSTANCE = new IdGenerator();
```

**Alternatives Compared:**

| Approach | Thread-Safe? | Performance | Complexity |
|----------|--------------|-------------|------------|
| Eager (chosen) | ✅ Yes | Best | Simple |
| Lazy (synchronized) | ✅ Yes | Slower | Simple |
| Double-checked locking | ✅ Yes | Good | Complex |
| Holder pattern | ✅ Yes | Best | Medium |

**Rationale for Eager:**
- IdGenerator is lightweight (~1 KB)
- Used immediately on startup
- No performance penalty for early creation
- Simplest thread-safe approach

**2. AtomicInteger for Counters**

**Decision: Use AtomicInteger instead of synchronized int**

```java
private final AtomicInteger patientCounter = new AtomicInteger(1000);

public synchronized String generatePatientId() {
    return "P" + patientCounter.getAndIncrement();
}
```

**Rationale:**
✅ **Lock-free**: Better performance than synchronized blocks  
✅ **Thread-safe**: Atomic operations guaranteed  
✅ **Modern approach**: Java concurrent utilities  

### Factory Pattern - Deep Dive

**Used in:** `BillFactory`

**Problem Solved:**
- Different types of bills with different pricing strategies
- Encapsulate complex bill creation logic

**Design Decisions:**

**1. Simple Factory vs Factory Method vs Abstract Factory**

**Chosen: Simple Factory (static methods)**

**Comparison:**

```
Simple Factory:
✅ Static methods
✅ One class
✅ Simple to use
✅ Sufficient for current needs

Factory Method:
- Requires subclassing
- More flexible
- More complex

Abstract Factory:
- Creates families of objects
- Overkill for MediTrack
```

**Rationale:** Simple Factory is sufficient for 3-4 bill types and keeps code maintainable.

**2. Enum for Bill Types**

```java
public enum BillType {
    STANDARD, EMERGENCY, FOLLOW_UP, CONSULTATION_ONLY
}
```

**Rationale:**
✅ **Type safety**: Compile-time checking  
✅ **Clear options**: Developer knows available bill types  
✅ **Extensible**: Add new types easily  

**3. Pricing Strategy Implementation**

**Choice: Switch statement in factory**
```java
switch (type) {
    case EMERGENCY:
        bill.setConsultationFee(baseFee * 1.5);
        break;
    case FOLLOW_UP:
        bill.setDiscount(baseFee * 0.3);
        break;
}
```

**Alternative Considered:** Strategy Pattern with separate classes
```java
interface BillingStrategy {
    Bill createBill(Appointment apt);
}
class EmergencyBillingStrategy implements BillingStrategy { ... }
```

**Decision:** Switch statement chosen for simplicity

**Rationale:**
- ✅ All billing logic in one place
- ✅ Easy to understand
- ✅ Sufficient for 3-4 bill types
- ⚠️ Strategy pattern better for 10+ types or complex logic

### 3. Strategy Pattern (Implicit in Bill Factory)

While not fully implemented, the Factory pattern serves as a lightweight Strategy:

**Strategy aspects:**
- Different algorithms (pricing strategies)
- Selected at runtime (based on bill type)
- Encapsulated behavior

**Future Enhancement:**
Could evolve into full Strategy pattern if billing logic becomes more complex.

---

## OOP Design Decisions

### 1. Abstract Classes vs Interfaces

**Decision Matrix:**

| Scenario | Chosen | Rationale |
|----------|--------|-----------|
| Common state + behavior | Abstract class (`MedicalEntity`) | Needs fields (timestamps) + methods |
| Behavioral contract | Interface (`Searchable`, `Payable`) | Defines capability, no state |
| Multiple inheritance needed | Interface | Java allows multiple interfaces |

**MedicalEntity (Abstract Class):**
```java
public abstract class MedicalEntity {
    protected String id;                      // Common state
    protected long createdTimestamp;          // Common state
    
    protected void updateModifiedTimestamp() { // Common behavior
        this.lastModifiedTimestamp = System.currentTimeMillis();
    }
    
    public abstract String getEntityType();   // Force implementation
}
```

**Rationale:**
- All medical entities need ID and timestamps
- Cannot use interface (interfaces can't have state before Java 16)
- Abstract class provides base implementation

**Searchable (Interface):**
```java
public interface Searchable {
    boolean matches(String query);           // Contract
    default boolean partialMatch(String query) { ... }  // Default impl
}
```

**Rationale:**
- Defines capability, not identity
- Doctor and Patient have different search implementations
- Default methods provide common functionality

### 2. Cloneable Implementation (Deep Copy)

**Used in:** `Patient`, `Appointment`

**Implementation:**
```java
@Override
public Patient clone() {
    try {
        Patient cloned = (Patient) super.clone();  // Shallow copy
        // Deep copy collections
        cloned.medicalHistory = new ArrayList<>(this.medicalHistory);
        cloned.allergies = new ArrayList<>(this.allergies);
        return cloned;
    } catch (CloneNotSupportedException e) {
        throw new AssertionError("Clone not supported", e);
    }
}
```

**Design Decisions:**

**Why implement Cloneable?**
- **Use case**: Need to create snapshot of patient data before modification
- **Requirement**: Demonstrate deep vs shallow copy
- **Medical safety**: Preserve original data when making changes

**Deep Copy Requirements:**
- Collections (`medicalHistory`, `allergies`) must be copied
- Otherwise, clone and original share same list (shallow copy)

**Alternative Considered:** Copy constructor
```java
public Patient(Patient other) {
    this.name = other.name;
    this.medicalHistory = new ArrayList<>(other.medicalHistory);
}
```

**Decision:** Both approaches implemented (Cloneable for educational value)

### 3. Immutability (BillSummary)

**Implementation:**
```java
public final class BillSummary implements Serializable {
    private final String billId;        // final field
    private final double totalAmount;   // final field
    
    public BillSummary(String billId, double totalAmount, ...) {
        this.billId = billId;
        this.totalAmount = totalAmount;
    }
    
    // Only getters, no setters
}
```

**Design Decisions:**

**Why immutable BillSummary?**

✅ **Thread-safe**: Can be shared across threads safely  
✅ **Cacheable**: Safe to cache without worry of modification  
✅ **Predictable**: State cannot change after creation  
✅ **Good for reports**: Bill summaries should never change  

**Immutability Checklist:**
1. ✅ Class declared `final` (cannot be subclassed)
2. ✅ All fields declared `final` (cannot be reassigned)
3. ✅ No setters (cannot modify fields)
4. ✅ Defensive copying for mutable fields (LocalDateTime is already immutable)
5. ✅ Initialization in constructor only

**Use Case:**
```java
// Safe to share immutable summary
BillSummary summary = BillSummary.fromBill(bill);
// Can be cached, passed to multiple threads, etc.
// No risk of accidental modification
```

### 4. Enum Usage

**Enums Created:**
- `Specialization`: Medical specializations
- `AppointmentStatus`: Appointment lifecycle states

**Decision: Enums with fields and methods**

```java
public enum Specialization {
    CARDIOLOGY("Cardiology", "Heart and cardiovascular system"),
    NEUROLOGY("Neurology", "Brain and nervous system");
    
    private final String displayName;
    private final String description;
    
    Specialization(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public static Specialization fromString(String value) { ... }
}
```

**Rationale:**
✅ **Type safety**: Cannot pass invalid specialization  
✅ **Compile-time checking**: Catches errors early  
✅ **Meaningful values**: Not just strings  
✅ **Associated data**: Each enum value carries description  

**Alternative Considered:** String constants
```java
public static final String CARDIOLOGY = "Cardiology";
```
❌ **Rejected because:** No type safety, can pass any string

**Benefits in MediTrack:**
```java
// Type-safe
doctor.setSpecialization(Specialization.CARDIOLOGY);  // ✅ Compile-time safe

// vs String approach
doctor.setSpecialization("Cardology");  // ❌ Typo not caught at compile time
```

---

## Data Structure Choices

### 1. HashMap for DataStore

**Decision Factors:**

```java
private final Map<String, T> dataMap = new HashMap<>();
```

**Comparison:**

| Structure | Lookup | Insert | Delete | Memory | Order |
|-----------|--------|--------|--------|--------|-------|
| **HashMap** (chosen) | O(1) | O(1) | O(1) | Medium | No |
| TreeMap | O(log n) | O(log n) | O(log n) | High | Yes (sorted) |
| LinkedHashMap | O(1) | O(1) | O(1) | High | Yes (insertion) |
| ArrayList | O(n) | O(1) | O(n) | Low | Yes (insertion) |

**Rationale:**
✅ **Performance**: O(1) for ID-based lookup (most common operation)  
✅ **Sufficient**: Don't need sorted order for doctors/patients  
✅ **Memory**: Acceptable overhead  

**When we need order:**
Use streams to sort on-demand:
```java
doctorStore.getAll().stream()
    .sorted(Comparator.comparing(Doctor::getName))
    .collect(Collectors.toList());
```

### 2. ArrayList for Collections

**Used for:**
- `List<String> medicalHistory` in Patient
- `List<Doctor> getAllDoctors()` return type

**Rationale:**
✅ **Default choice**: Best general-purpose list  
✅ **Fast iteration**: Common operation in MediTrack  
✅ **Dynamic sizing**: Grows as needed  

### 3. AtomicInteger for Counters

**Used in:** IdGenerator, static counters

**Choice: AtomicInteger vs int with synchronized**

```java
// Chosen approach
private final AtomicInteger patientCounter = new AtomicInteger(1000);
public String generatePatientId() {
    return "P" + patientCounter.getAndIncrement();  // Atomic operation
}

// Alternative (rejected)
private int patientCounter = 1000;
public synchronized String generatePatientId() {
    return "P" + (patientCounter++);  // Requires synchronization
}
```

**Rationale:**
✅ **Lock-free**: Better performance under contention  
✅ **Modern Java**: Uses java.util.concurrent  
✅ **Cleaner code**: No explicit synchronization needed  
✅ **Educational**: Demonstrates concurrent programming  

---

## Exception Handling Strategy

### Custom Exception Hierarchy

```
Exception
    ↓
├── InvalidDataException      (validation errors)
└── AppointmentNotFoundException (business logic errors)
```

**Design Decisions:**

**1. Checked vs Unchecked Exceptions**

**Decision: Use checked exceptions (extends Exception)**

**Rationale:**
✅ **Explicit error handling**: Forces caller to handle errors  
✅ **Medical domain**: Data validation critical, errors must be handled  
✅ **Documented behavior**: Method signature shows possible exceptions  

**Alternative Considered:** Runtime exceptions (extends RuntimeException)
❌ **Rejected because:** Medical data is critical, errors should not be silently propagated

**2. Exception Context**

```java
public class InvalidDataException extends Exception {
    private final String fieldName;
    private final Object invalidValue;
    
    public InvalidDataException(String fieldName, Object invalidValue, String message) {
        super(message);
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
    }
}
```

**Rationale:**
✅ **Rich context**: Knows which field failed and why  
✅ **Better debugging**: Detailed error messages  
✅ **User-friendly**: Can show specific field errors in UI  

**3. Exception Chaining**

```java
public InvalidDataException(String message, Throwable cause) {
    super(message, cause);
}
```

**Rationale:**
✅ **Preserves root cause**: Doesn't lose original error  
✅ **Better debugging**: Full stack trace available  
✅ **Standard practice**: Follows Java conventions  

**Example:**
```java
try {
    patient.setEmail(email);
} catch (IllegalArgumentException e) {
    throw new InvalidDataException("Email validation failed", e);
}
```

### Try-with-Resources

**Used in:** CSVUtil

```java
public static List<String[]> readCSV(String filePath) throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
        // Read file
    }  // Automatically closes BufferedReader
}
```

**Rationale:**
✅ **Automatic resource management**: No need for finally blocks  
✅ **Clean code**: Less boilerplate  
✅ **Safety**: Resources closed even if exception occurs  
✅ **Modern Java**: Demonstrates Java 7+ features  

**Alternative (old approach):**
```java
BufferedReader br = null;
try {
    br = new BufferedReader(new FileReader(filePath));
    // Read file
} finally {
    if (br != null) br.close();
}
```
❌ **Rejected:** Verbose, error-prone

---

## Memory Management

### Garbage Collection Considerations

**Design Decisions:**

**1. Object Lifecycle Management**

**Short-lived objects (Young Generation):**
- Search results
- Temporary clones
- Bill summaries
- Display strings

**Long-lived objects (Old Generation):**
- Service instances
- DataStore instances
- Static constants

**Rationale:**
- Design aligns with generational GC
- Short-lived objects cleaned quickly
- Long-lived objects persist in Old Gen

**2. Reference Management**

**Decision: No explicit nulling of references**

```java
// We don't do this:
doctor = null;
System.gc();

// Instead, rely on scope:
public void processAppointment() {
    Appointment temp = getAppointment();
    // Process
}  // temp goes out of scope, eligible for GC
```

**Rationale:**
✅ **Trust GC**: Modern GC is sophisticated  
✅ **Cleaner code**: No manual memory management  
✅ **Standard practice**: Explicit nulling rarely needed  

**3. Defensive Copying Impact**

```java
public List<String> getMedicalHistory() {
    return new ArrayList<>(medicalHistory);  // Creates new object
}
```

**Trade-off:**
- ⚠️ More memory allocations
- ⚠️ More GC pressure
- ✅ Better encapsulation
- ✅ Thread safety

**Decision:** Acceptable for medical application where data integrity > performance.

---

## Concurrency Considerations

### Thread Safety Strategy

**Current Implementation:**

**1. Singleton with synchronized methods**
```java
public synchronized String generatePatientId() {
    return "P" + patientCounter.getAndIncrement();
}
```

**Rationale:**
- Console application = single-threaded UI
- But IdGenerator could be called from multiple threads in future
- Better safe than sorry

**2. Immutable objects (BillSummary)**
```java
public final class BillSummary {
    private final String billId;
    // Thread-safe by design
}
```

**Rationale:**
- Can be shared across threads safely
- No synchronization needed
- Good for caching and reporting

**3. No shared mutable state**

**DataStore is NOT thread-safe:**
```java
private final Map<String, T> dataMap = new HashMap<>();
```

**Rationale:**
- Console application is single-threaded
- No concurrent requests
- Adding synchronization would decrease performance unnecessarily

**Future Enhancement:**
If MediTrack becomes a web application:
```java
// Option 1: ConcurrentHashMap
private final Map<String, T> dataMap = new ConcurrentHashMap<>();

// Option 2: Synchronized methods
public synchronized boolean add(String id, T entity) { ... }

// Option 3: ReadWriteLock
private final ReadWriteLock lock = new ReentrantReadWriteLock();
```

---

## Persistence Strategy

### Current Approach: In-Memory with CSV Support

**Design Decisions:**

**1. In-Memory Primary Storage**

**Choice: HashMap in DataStore**

**Rationale:**
✅ **Fast**: No disk I/O overhead  
✅ **Simple**: No database setup required  
✅ **Educational**: Focus on Java concepts, not DB  
✅ **Sufficient**: Console app with small dataset  

**Trade-off:**
- ⚠️ Data lost on exit
- ⚠️ No persistence by default
- ⚠️ Limited to memory capacity

**2. CSV for Optional Persistence**

**Implementation:** CSVUtil with try-with-resources

**Rationale:**
✅ **Human-readable**: Can open in Excel/text editor  
✅ **Simple format**: Easy to parse with `String.split()`  
✅ **No dependencies**: Pure Java implementation  
✅ **Educational**: Demonstrates File I/O  

**Alternative Considered:** Java Serialization
```java
ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data.ser"));
oos.writeObject(doctorStore);
```

**Comparison:**

| Approach | Readability | Size | Version Safe | Chosen |
|----------|-------------|------|--------------|--------|
| CSV | ✅ High | Medium | ✅ Yes | ✅ Primary |
| Serialization | ❌ Low | Small | ❌ No | ❌ |
| JSON | ✅ High | Medium | ✅ Yes | Future |
| Database | ❌ Low | Small | ✅ Yes | Future |

**Decision:** CSV for current version, JSON/DB for future.

**3. Data Directory Structure**

```
data/
├── doctors.csv
├── patients.csv
├── appointments.csv
└── bills.csv
```

**Rationale:**
- Separate files for each entity type
- Easy to manage and debug
- Can be version controlled (gitignore data/ in production)

---

## Validation Strategy

### Centralized Validation

**Design Decision: Utility class vs validation in entities**

**Chosen: Centralized `Validator` utility class**

```java
public class Validator {
    public static void validateName(String name, String fieldName) { ... }
    public static void validateAge(int age, String fieldName) { ... }
    public static void validateEmail(String email, String fieldName) { ... }
}
```

**Rationale:**
✅ **DRY Principle**: Single source of validation logic  
✅ **Consistency**: Same validation rules everywhere  
✅ **Maintainability**: Change rule in one place  
✅ **Testability**: Easy to unit test validation  
✅ **Reusability**: Can validate before object creation  

**Alternative Considered:** Validation in entity setters
```java
public void setName(String name) {
    if (name == null || name.length() < 2) {
        throw new InvalidDataException("Invalid name");
    }
    this.name = name;
}
```
❌ **Rejected because:** Duplicated logic across multiple classes

**Validation Rules:**

| Field | Rules | Rationale |
|-------|-------|-----------|
| Name | 2-100 chars, letters only | Prevent empty/too long names |
| Age | 0-150 | Reasonable human age range |
| Phone | 10 digits | Indian phone number format |
| Email | Valid format | RFC 5322 simplified |
| Amount | ≥ 0 | Cannot have negative money |

---

## Class Design Decisions

### 1. Person as Base Class

**Design:**
```
MedicalEntity
    ↓
Person (concrete, but intended as base)
    ↓
├── Doctor
└── Patient
```

**Decision: Make Person concrete (not abstract)**

**Rationale:**
✅ **Flexibility**: Can instantiate Person if needed (e.g., admin user)  
✅ **Realistic**: Not all people in clinic are doctors or patients  
✅ **Extensibility**: Easy to add Nurse, Receptionist later  

**Alternative:** Make Person abstract
❌ **Rejected because:** Limits flexibility

### 2. Service Layer Design

**Decision: One service class per entity**

```
DoctorService     → Manages Doctor entities
PatientService    → Manages Patient entities
AppointmentService → Manages Appointment entities
BillService       → Manages Bill entities
```

**Rationale:**
✅ **Single Responsibility**: Each service focuses on one entity type  
✅ **Clear boundaries**: Easy to understand and maintain  
✅ **Parallel development**: Different developers can work on different services  

**Service Dependencies:**
```java
public class AppointmentService {
    private final DoctorService doctorService;
    private final PatientService patientService;
    
    public AppointmentService(DoctorService ds, PatientService ps) {
        this.doctorService = ds;
        this.patientService = ps;
    }
}
```

**Dependency Injection (manual):**
- Services passed via constructor
- Loose coupling
- Easy to test with mock services
- Foundation for future Spring Framework integration

### 3. Static Methods vs Instance Methods

**Decision Matrix:**

| Scenario | Chosen | Example |
|----------|--------|---------|
| Utility functions | Static | `Validator.validateName()` |
| State-dependent | Instance | `doctorService.getAllDoctors()` |
| Factory methods | Static | `BillFactory.createBill()` |
| Singleton access | Static | `IdGenerator.getInstance()` |

**Rationale:**
- **Static**: Stateless utility functions
- **Instance**: When working with object state

### 4. Method Overloading Design

**PatientService - 5 overloaded search methods:**

```java
searchPatient(String id)                    // By ID
searchPatient(String name, boolean exact)   // By name
searchPatient(int age)                      // By exact age
searchPatient(int minAge, int maxAge)       // By age range
searchPatient(String name, int age)         // By name and age
```

**Design Decisions:**

**Why 5 versions?**
- ✅ Demonstrates polymorphism clearly
- ✅ Each has distinct parameter signature
- ✅ Covers common search scenarios
- ✅ Intuitive API for developers

**Signature disambiguation:**
- Different parameter counts: `searchPatient(int)` vs `searchPatient(int, int)`
- Different parameter types: `searchPatient(String)` vs `searchPatient(int)`
- Additional parameters: `searchPatient(String, boolean)` vs `searchPatient(String, int)`

**Alternative Considered:** Builder pattern
```java
patientService.search()
    .byName("John")
    .byAge(35)
    .execute();
```
❌ **Rejected because:** Over-engineering for simple search

---

## Stream API and Functional Programming

### Design Decisions

**1. Streams for Analytics**

```java
// Calculate average consultation fee
public double calculateAverageFee() {
    return doctorStore.getAll().stream()
            .mapToDouble(Doctor::getConsultationFee)
            .average()
            .orElse(0.0);
}
```

**Rationale:**
✅ **Readable**: Declarative style (what, not how)  
✅ **Maintainable**: Easy to understand intent  
✅ **Functional**: Demonstrates Java 8+ features  
✅ **Efficient**: Lazy evaluation, optimized by JVM  

**2. Method References**

```java
.mapToDouble(Doctor::getConsultationFee)  // Method reference
// vs
.mapToDouble(doctor -> doctor.getConsultationFee())  // Lambda
```

**Decision: Use method references when possible**

**Rationale:**
✅ More concise  
✅ More readable  
✅ Shows advanced Java knowledge  

**3. Collectors**

```java
// Group patients by blood group
public Map<String, Long> getPatientsByBloodGroup() {
    return patientStore.getAll().stream()
            .collect(Collectors.groupingBy(
                Patient::getBloodGroup,
                Collectors.counting()
            ));
}
```

**Rationale:**
✅ **Powerful aggregation**: Complex operations in few lines  
✅ **Type-safe**: Generics ensure correctness  
✅ **Educational**: Demonstrates advanced streams  

---

## Input/Output Design

### Console UI Design

**Decision: Menu-driven interface**

**Rationale:**
✅ **User-friendly**: Clear options, numbered choices  
✅ **Error handling**: Invalid input caught and handled gracefully  
✅ **Flexible navigation**: Can go back, exit anytime  
✅ **Standard approach**: Common for console applications  

**Menu Hierarchy:**
```
Main Menu
├── 1. Doctor Management
│   ├── 1. Add Doctor
│   ├── 2. View All
│   └── 3. Search
├── 2. Patient Management
├── 3. Appointment Management
├── 4. Billing Management
└── 5. Reports & Analytics
```

**Alternative Considered:** Command-line arguments only
```bash
java Main add-doctor "Dr. Smith" 45 CARDIOLOGY 1000
```
❌ **Rejected because:** Less user-friendly for educational demo

### Input Validation

**Decision: Validate early, fail fast**

```java
private static String getStringInput(String prompt) {
    System.out.print(prompt + ": ");
    String input = scanner.nextLine().trim();
    
    if (input.isEmpty()) {
        throw new IllegalArgumentException("Input cannot be empty");
    }
    
    return input;
}
```

**Rationale:**
✅ **Prevents invalid state**: Bad data never enters system  
✅ **Better UX**: Immediate feedback  
✅ **Easier debugging**: Fails at input point, not deep in logic  

---

## Performance Optimization Decisions

### 1. Lazy Initialization

**Used in:** Some service methods

```java
public List<Doctor> getAllDoctors() {
    return doctorStore.getAll();  // No caching
}
```

**Decision: No caching in service layer**

**Rationale:**
- Small dataset (< 1000 entities)
- In-memory storage already fast
- Caching adds complexity
- Refresh data on every call (consistency)

**When to add caching:**
- Dataset > 10,000 entities
- Expensive computations
- External API calls

### 2. Stream Performance

**Decision: Use streams for readability, not premature optimization**

```java
// Using stream
return doctorStore.getAll().stream()
        .filter(doctor -> doctor.isAvailable())
        .collect(Collectors.toList());

// Traditional loop (slightly faster for small collections)
List<Doctor> result = new ArrayList<>();
for (Doctor doctor : doctorStore.getAll()) {
    if (doctor.isAvailable()) {
        result.add(doctor);
    }
}
```

**Rationale:**
✅ **Readability**: Stream version more declarative  
✅ **Maintainability**: Intent clearer  
✅ **Future optimization**: JVM optimizes streams over time  
⚠️ **Performance**: Traditional loop ~5-10% faster for small collections

**Decision:** Readability > micro-optimization for small datasets.

### 3. String Handling

**Decision: Use StringBuilder for multi-line strings**

```java
// In display methods
StringBuilder sb = new StringBuilder();
sb.append("Doctor Information:\n");
sb.append("  Name: ").append(doctor.getName()).append("\n");
sb.append("  Specialization: ").append(doctor.getSpecialization()).append("\n");
return sb.toString();
```

**Rationale:**
✅ **Performance**: Avoids string concatenation overhead  
✅ **Memory efficient**: Single buffer  
✅ **Best practice**: Standard for multi-line strings  

**Alternative:** String concatenation
```java
String info = "Doctor Information:\n" +
              "  Name: " + doctor.getName() + "\n" +
              "  Specialization: " + doctor.getSpecialization() + "\n";
```
⚠️ Creates multiple intermediate String objects

---

## Type Safety Decisions

### 1. Generics Usage

**Decision: Use generics for type safety**

```java
// Generic DataStore
public class DataStore<T> {
    private final Map<String, T> dataMap;
}

// Type-safe usage
DataStore<Doctor> doctorStore = new DataStore<>("Doctors");
Doctor doctor = doctorStore.get("D2001");  // No casting needed
```

**Rationale:**
✅ **Compile-time safety**: Catches type errors early  
✅ **No casting**: Cleaner code  
✅ **Refactoring-friendly**: IDE can track types  

**Alternative:** Raw types
```java
DataStore doctorStore = new DataStore();  // Raw type
Doctor doctor = (Doctor) doctorStore.get("D2001");  // Manual casting
```
❌ **Rejected because:** Runtime errors, no compile-time checking

### 2. Enum vs String Constants

**Decision: Use enums for fixed value sets**

**Comparison:**

```java
// Enum (chosen)
appointment.setStatus(AppointmentStatus.CONFIRMED);

// String constants
public static final String STATUS_CONFIRMED = "CONFIRMED";
appointment.setStatus(STATUS_CONFIRMED);

// Plain strings
appointment.setStatus("CONFIRMED");  // Typo risk: "CONFIRMEED"
```

**Rationale:**
✅ **Type safety**: Compiler prevents invalid values  
✅ **Refactoring**: IDE can rename all occurrences  
✅ **Auto-complete**: IDE suggests valid values  
✅ **Associated data**: Enums can have fields and methods  

---

## Code Organization Decisions

### 1. File Organization

**Decision: One public class per file**

**Rationale:**
✅ **Java convention**: File name matches public class name  
✅ **Navigability**: Easy to find classes  
✅ **IDE support**: Better tooling support  

**Exception:** Inner enums and classes allowed
```java
// BillFactory.java
public class BillFactory {
    public enum BillType { ... }  // Inner enum OK
}
```

### 2. Method Ordering in Classes

**Standard order:**
1. Static fields
2. Instance fields
3. Constructors
4. Public methods
5. Protected methods
6. Private methods
7. Static methods
8. Overridden methods (grouped together)

**Rationale:**
- Follows Java conventions
- Logical flow (construction → usage)
- Easy to navigate

### 3. Comment Strategy

**Decision: JavaDoc for public API, inline comments for complex logic**

```java
/**
 * Create a new doctor.
 * 
 * @param name doctor's name
 * @param age doctor's age
 * @return created doctor
 * @throws InvalidDataException if validation fails
 */
public Doctor createDoctor(String name, int age, ...) {
    // Generate unique ID for new doctor
    String id = idGenerator.generateDoctorId();
    
    // Create doctor entity with validation
    Doctor doctor = new Doctor(name, age, id, ...);
    
    return doctor;
}
```

**Rationale:**
✅ **JavaDoc**: API documentation, IDE tooltips  
✅ **Inline comments**: Explain non-obvious logic  
✅ **Balance**: Not over-commented, not under-commented  

---

## Security Considerations

### Input Validation

**Decision: Validate all user input**

**Layers of validation:**
1. **UI layer**: Basic format checking
2. **Service layer**: Business rule validation
3. **Entity layer**: Data integrity validation

**Example:**
```java
// UI Layer
String name = getStringInput("Enter name");  // Checks not empty

// Service Layer
public Doctor createDoctor(String name, ...) {
    // Business rules (e.g., no duplicate names)
}

// Entity Layer (via Validator)
public void setName(String name) throws InvalidDataException {
    Validator.validateName(name, "Name");  // Format, length, characters
    this.name = name.trim();
}
```

**Rationale:**
✅ **Defense in depth**: Multiple validation layers  
✅ **Data integrity**: Invalid data never enters system  
✅ **Better UX**: Errors caught early with clear messages  

### SQL Injection Prevention

**Current status:** Not applicable (no database)

**Future consideration:**
When adding database support, use **PreparedStatement** instead of string concatenation:

```java
// Future - Good practice
PreparedStatement ps = conn.prepareStatement(
    "SELECT * FROM doctors WHERE name = ?"
);
ps.setString(1, doctorName);

// Never do this:
Statement st = conn.createStatement();
st.executeQuery("SELECT * FROM doctors WHERE name = '" + doctorName + "'");
```

---

## API Design Decisions

### Service Layer API

**Design Principles:**

**1. Intuitive naming:**
- `createDoctor()` not `addDoctor()` - emphasizes object creation
- `getAllDoctors()` not `getDoctors()` - explicit about retrieving all
- `searchPatient()` not `findPatient()` - consistent terminology

**2. Return types:**
```java
public Doctor createDoctor(...)         // Returns created entity
public List<Doctor> getAllDoctors()    // Returns list
public boolean updateDoctor(Doctor d)  // Returns success flag
```

**Rationale:**
- **Entities returned**: Caller can use immediately
- **Lists for collections**: Standard Java convention
- **Booleans for operations**: Clear success/failure

**3. Exception handling:**
```java
// Methods that can fail throw checked exceptions
public Appointment getAppointmentById(String id) 
    throws AppointmentNotFoundException

// vs methods that handle errors internally
public Doctor getDoctorById(String id)  // Returns null if not found
```

**Rationale:**
- **Critical operations** (appointments): Force error handling
- **Optional operations** (get by ID): Null is acceptable

---

## Testing Strategy Decisions

### Current Approach: Manual Testing

**Decision: Interactive menu-based testing (no JUnit yet)**

**Rationale:**
✅ **Educational focus**: Learn core Java first  
✅ **Immediate feedback**: See results instantly  
✅ **User-friendly**: Non-developers can test  
✅ **Integration testing**: Tests entire flow  

**Sample data loading:**
```java
java -cp out com.airtribe.meditrack.Main --loadData
```

**Rationale:**
- Quick testing without manual data entry
- Consistent test data
- Demonstrates command-line argument handling

**Future Enhancement:** Add JUnit tests
```java
@Test
public void testCreateDoctor() {
    DoctorService service = new DoctorService();
    Doctor doctor = service.createDoctor("Dr. Smith", 45, ...);
    assertNotNull(doctor.getId());
}
```

---

## Code Quality Decisions

### 1. Error Messages

**Decision: Descriptive error messages with context**

```java
throw new InvalidDataException(
    "Age", 
    age, 
    "Age must be between 0 and 150"
);
```

**Output:**
```
InvalidDataException: Age must be between 0 and 150 [Field: Age, Value: 200]
```

**Rationale:**
✅ **Debugging**: Know exactly what failed and why  
✅ **User feedback**: Can show meaningful error to user  
✅ **Logging**: Error messages are self-documenting  

### 2. Null Handling

**Decision: Validate null early**

```java
public void setPatient(Patient patient) throws InvalidDataException {
    Validator.validateNotNull(patient, "Patient");
    this.patient = patient;
}
```

**Rationale:**
✅ **Fail fast**: Catch null references immediately  
✅ **Clear errors**: Better than NullPointerException  
✅ **Defensive programming**: Assume inputs may be invalid  

**Alternative:** Allow nulls and check before use
❌ **Rejected because:** Medical data should never be null; better to fail explicitly

### 3. Magic Numbers

**Decision: Constants for all magic numbers**

```java
// Good
private final AtomicInteger patientCounter = new AtomicInteger(Constants.PATIENT_ID_START);

// Bad
private final AtomicInteger patientCounter = new AtomicInteger(1000);
```

**Rationale:**
✅ **Maintainability**: Change in one place  
✅ **Readability**: Named constant explains purpose  
✅ **Configurability**: Easy to adjust  

---

## Scalability Considerations

### Current Limitations and Future Path

**Current Design:**
- In-memory storage (HashMap)
- Single-threaded
- Console UI
- No authentication
- No network capability

**Scalability Path:**

### Phase 1: Current (Educational)
```
Console UI → Service Layer → DataStore (HashMap) → No Persistence
```

**Capacity:** ~1,000 entities, single user

### Phase 2: Persistence (Next)
```
Console UI → Service Layer → DataStore → CSV Files
```

**Changes Needed:**
- Load data on startup
- Save data on exit
- Periodic auto-save

**Capacity:** ~10,000 entities, single user

### Phase 3: Database (Future)
```
Console UI → Service Layer → DAO Layer → Database (SQLite/MySQL)
```

**Changes Needed:**
- Add JDBC dependencies
- Create DAO classes
- Replace DataStore with database calls
- Service layer remains mostly unchanged ✅

**Capacity:** ~1,000,000 entities, single user

### Phase 4: Web Application (Future)
```
Web UI (React) → REST API (Spring Boot) → Service Layer → Database
```

**Changes Needed:**
- Add Spring Boot framework
- Convert services to Spring beans
- Add REST controllers
- Service layer remains mostly unchanged ✅

**Capacity:** 100,000+ concurrent users

### Design for Future Scalability

**Current decisions that enable scaling:**

✅ **Service layer abstraction**: Can swap DataStore with DAO  
✅ **Interface-based design**: Easy to add implementations  
✅ **Dependency injection**: Manual now, Spring-ready later  
✅ **Validation layer**: Reusable in web forms  
✅ **Entity design**: Can map to database tables  

---

## Technology Stack Decisions

### 1. Pure Java (No Frameworks)

**Decision: No Spring, Hibernate, or other frameworks**

**Rationale:**
✅ **Educational focus**: Learn Java fundamentals first  
✅ **Clarity**: See how things work under the hood  
✅ **No dependencies**: Easy to set up and run  
✅ **Transferable skills**: Concepts apply to any framework  

**When to add frameworks:**
- Adding database (Hibernate/JPA)
- Building web API (Spring Boot)
- Enterprise features (Spring Security)

### 2. Java 17 (LTS)

**Decision: Target Java 17**

**Rationale:**
✅ **Long-term support**: Stable, maintained until 2029  
✅ **Modern features**: Records, sealed classes available (not used yet)  
✅ **Performance**: Latest JVM optimizations  
✅ **Industry standard**: Most companies use Java 11 or 17  

**Features Used:**
- ✅ Streams and lambdas (Java 8)
- ✅ Default interface methods (Java 8)
- ✅ Try-with-resources (Java 7)
- ✅ LocalDateTime (Java 8)
- ✅ Method references (Java 8)

**Features Not Used (but available):**
- Records (Java 14+): Could replace BillSummary
- Sealed classes (Java 17): Could restrict MedicalEntity hierarchy
- Pattern matching (Java 17): Could simplify instanceof checks

### 3. Maven Build Tool

**Decision: Include pom.xml (even if optional)**

```xml
<properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
</properties>
```

**Rationale:**
✅ **Industry standard**: Most Java projects use Maven/Gradle  
✅ **Dependency management**: Easy to add libraries later  
✅ **Build automation**: Consistent builds  
✅ **IDE support**: Better integration  

---

## Documentation Decisions

### 1. JavaDoc Coverage

**Decision: JavaDoc for public APIs, not private methods**

**Rationale:**
✅ **Public API**: Others need to know how to use  
✅ **Not over-documented**: Private methods are internal  
✅ **Tool support**: IDEs show JavaDoc in tooltips  

**Example:**
```java
/**
 * Create a new doctor.
 * 
 * @param name doctor's name
 * @param age doctor's age
 * @param specialization medical specialization
 * @param consultationFee consultation fee
 * @return created doctor
 * @throws InvalidDataException if validation fails
 */
public Doctor createDoctor(String name, int age, Specialization specialization,
                          double consultationFee) throws InvalidDataException {
    // Implementation
}
```

### 2. README Structure

**Decision: Multi-level documentation**

- `README.md`: High-level overview, quick start
- `docs/Setup_Instructions.md`: Detailed installation
- `docs/JVM_Report.md`: JVM internals
- `docs/Design_Decisions.md`: This document
- `PROJECT_BRIEF.md`: Requirements and grading
- `PROJECT_STATUS.md`: Implementation checklist

**Rationale:**
✅ **Separation of concerns**: Each doc has specific purpose  
✅ **Progressive disclosure**: Start simple, dive deep as needed  
✅ **Educational value**: Explains not just what, but why  

---

## Specific Design Choices

### 1. ID Generation Strategy

**Decision: Prefix-based IDs (P1001, D2001, A3001)**

```java
public String generatePatientId() {
    return "P" + patientCounter.getAndIncrement();
}
```

**Rationale:**
✅ **Human-readable**: Know entity type from ID  
✅ **Debugging-friendly**: Easy to trace in logs  
✅ **No collisions**: Separate counters per entity type  
✅ **Sortable**: Numeric suffix allows chronological ordering  

**Alternative:** UUIDs
```java
String id = UUID.randomUUID().toString();  // e.g., "550e8400-e29b-41d4-a716-446655440000"
```

**Comparison:**

| Aspect | Prefix IDs (P1001) | UUIDs |
|--------|-------------------|-------|
| Readability | ✅ High | ❌ Low |
| Length | ✅ Short | ❌ Long (36 chars) |
| Collision risk | ⚠️ Medium (counter reset) | ✅ Near zero |
| Distributed systems | ❌ Needs coordination | ✅ No coordination |

**Decision:** Prefix IDs for simplicity and readability in single-instance app.

### 2. Date/Time Handling

**Decision: java.time API (Java 8+)**

```java
private LocalDateTime appointmentDateTime;
```

**Rationale:**
✅ **Immutable**: Thread-safe by design  
✅ **Rich API**: Easy date/time manipulation  
✅ **Type-safe**: Different types for date, time, datetime  

**Alternative:** java.util.Date
```java
private Date appointmentDateTime;  // Old API
```
❌ **Rejected because:** Mutable, outdated, poor API design

### 3. Validation Timing

**Decision: Validate in setters (fail fast)**

```java
public void setAge(int age) throws InvalidDataException {
    Validator.validateAge(age, "Age");  // Validate immediately
    this.age = age;
}
```

**Alternative:** Validate in constructor only
```java
public Person(String name, int age) {
    this.name = name;
    this.age = age;
    validate();  // Single validation point
}
```

**Decision:** Validate in setters

**Rationale:**
✅ **Integrity**: Object always in valid state  
✅ **Fail fast**: Invalid data rejected immediately  
✅ **Clear errors**: Know exactly which field failed  

**Trade-off:**
⚠️ Cannot create temporarily invalid objects (rare need)

### 4. Mutable vs Immutable Entities

**Design Decision:**

| Entity | Mutability | Rationale |
|--------|-----------|-----------|
| Doctor, Patient, Appointment | Mutable | Need to update availability, status, etc. |
| BillSummary | **Immutable** | Read-only report, thread-safe |
| Enums | Immutable | By design |

**Why not make all entities immutable?**
- Medical records need updates (diagnosis, prescription)
- Doctor availability changes
- Appointment status transitions
- Full immutability would require creating new objects for every change

**Why BillSummary immutable?**
- Snapshot of bill at specific time
- Used for reports (shouldn't change)
- Can be cached safely
- Thread-safe for multi-threaded reporting (future)

---

## Anti-Patterns Avoided

### 1. God Object

**❌ Avoided:**
```java
// Bad: One class does everything
public class ClinicManager {
    public void addDoctor() { ... }
    public void addPatient() { ... }
    public void createAppointment() { ... }
    public void generateBill() { ... }
    // 5000 lines of code
}
```

**✅ Instead:**
- DoctorService (doctor operations)
- PatientService (patient operations)
- AppointmentService (appointment operations)
- BillService (billing operations)

**Rationale:** Single Responsibility Principle

### 2. Primitive Obsession

**❌ Avoided:**
```java
// Bad: Using strings for everything
appointment.setStatus("CONFIRMED");  // String (typo-prone)
doctor.setSpecialization("Cardiology");  // String (no validation)
```

**✅ Instead:**
```java
appointment.setStatus(AppointmentStatus.CONFIRMED);  // Enum (type-safe)
doctor.setSpecialization(Specialization.CARDIOLOGY);  // Enum (validated)
```

**Rationale:** Type safety and compile-time checking

### 3. Anemic Domain Model

**❌ Avoided:**
```java
// Bad: Entities with only getters/setters, all logic in services
public class Doctor {
    private String name;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
```

**✅ Instead:**
```java
// Good: Entities with behavior
public class Doctor extends Person implements Searchable {
    @Override
    public boolean matches(String query) {
        // Search logic in entity
    }
    
    @Override
    public String getDisplayInfo() {
        // Formatting logic in entity
    }
}
```

**Rationale:**
- Entities encapsulate their own behavior
- Not just data bags
- Business logic close to data

### 4. Static Utility Hell

**❌ Avoided:**
```java
// Bad: Everything is static
public class DoctorUtil {
    private static List<Doctor> doctors = new ArrayList<>();
    public static void addDoctor(Doctor d) { doctors.add(d); }
}
```

**✅ Instead:**
```java
// Good: Instance-based services
public class DoctorService {
    private final DataStore<Doctor> doctorStore;
    public Doctor createDoctor(...) { ... }
}
```

**Rationale:**
- Testability (can create multiple instances)
- State management (each service has own state)
- Object-oriented design

---

## Trade-offs and Compromises

### 1. In-Memory Storage

**Trade-off:**
- ✅ **Pro**: Fast, simple, no dependencies
- ⚠️ **Con**: Data lost on exit
- ⚠️ **Con**: Limited capacity

**Decision:** Accept for educational project, add persistence later

### 2. No Dependency Injection Framework

**Trade-off:**
- ✅ **Pro**: Simpler, no framework learning curve
- ⚠️ **Con**: Manual dependency wiring
- ⚠️ **Con**: Less flexible than Spring

**Decision:** Manual DI sufficient for current scope

**Example:**
```java
// Manual dependency injection
DoctorService doctorService = new DoctorService();
PatientService patientService = new PatientService();
AppointmentService appointmentService = new AppointmentService(
    doctorService, 
    patientService
);
```

### 3. Console UI vs GUI

**Trade-off:**
- ✅ **Pro**: Simple, no UI framework needed
- ⚠️ **Con**: Less user-friendly
- ⚠️ **Con**: Limited formatting options

**Decision:** Console UI for educational project

**Rationale:**
- Focus on Java concepts, not UI frameworks
- Easier to demonstrate in terminal
- Service layer ready for any UI (web, desktop, mobile)

### 4. No Database

**Trade-off:**
- ✅ **Pro**: Zero setup, no database installation
- ⚠️ **Con**: No persistent storage
- ⚠️ **Con**: No concurrent access

**Decision:** CSV optional, database for future

**Migration Path:**
1. Current: In-memory (HashMap)
2. Phase 2: Add DAO layer
3. Phase 3: Replace DataStore with database DAOs
4. Service layer remains unchanged ✅

---

## Best Practices Followed

### 1. SOLID Principles

**Single Responsibility Principle (SRP):**
✅ Each class has one reason to change
- `DoctorService`: Doctor operations only
- `Validator`: Validation logic only
- `IdGenerator`: ID generation only

**Open/Closed Principle (OCP):**
✅ Open for extension, closed for modification
- `BillFactory`: Add new bill types without modifying existing code
- `Searchable` interface: New searchable entities without changing interface

**Liskov Substitution Principle (LSP):**
✅ Subclasses can replace parent classes
```java
Person person = new Doctor(...);  // Doctor can be used as Person
person.getName();  // Works correctly
```

**Interface Segregation Principle (ISP):**
✅ Small, focused interfaces
- `Searchable`: Only search-related methods
- `Payable`: Only payment-related methods
- Not one giant interface with 20 methods

**Dependency Inversion Principle (DIP):**
⚠️ Partially followed
- Services depend on concrete classes (DataStore)
- Future: Services should depend on interfaces (IDataStore)

### 2. DRY (Don't Repeat Yourself)

✅ **Centralized validation**: Validator class  
✅ **Generic DataStore**: Reused for all entities  
✅ **Base classes**: Common behavior in MedicalEntity, Person  
✅ **Utility classes**: DateUtil, CSVUtil  

### 3. KISS (Keep It Simple, Stupid)

✅ **Simple Factory**: Not over-engineered Abstract Factory  
✅ **HashMap**: Not complex data structures  
✅ **CSV**: Not complex serialization  
✅ **Manual DI**: Not complex framework  

### 4. YAGNI (You Aren't Gonna Need It)

✅ **No premature optimization**: No caching, connection pooling  
✅ **No unused features**: No notification system (yet)  
✅ **Start simple**: Add complexity when needed  

---

## Error Handling Philosophy

### Exception Design

**Decision: Use exceptions for exceptional conditions**

**What gets an exception:**
✅ Invalid data (wrong format, out of range)  
✅ Business rule violations (doctor not available)  
✅ Not found errors (appointment doesn't exist)  

**What doesn't:**
✅ Empty search results (return empty list)  
✅ Optional values (return null or Optional)  

**Example:**
```java
// Exception for critical error
public Appointment getAppointmentById(String id) 
    throws AppointmentNotFoundException {
    // Appointment MUST exist
}

// Null for optional lookup
public Doctor getDoctorById(String id) {
    return doctorStore.get(id);  // May return null
}
```

**Rationale:**
- Appointments should exist when retrieving by ID (booking context)
- Doctors may not exist when searching (exploratory context)

---

## Code Style Decisions

### 1. Naming Conventions

**Variables:**
- `camelCase` for variables: `doctorService`, `patientId`
- Descriptive names: `appointmentDateTime` not `dt`

**Constants:**
- `UPPER_SNAKE_CASE`: `TAX_RATE`, `MAX_AGE`

**Classes:**
- `PascalCase`: `DoctorService`, `InvalidDataException`

**Methods:**
- `camelCase`: `createDoctor()`, `getAllPatients()`
- Verb-based: `get`, `create`, `update`, `delete`, `search`

**Packages:**
- `lowercase`: `com.airtribe.meditrack.service`

**Rationale:** Follow standard Java conventions for consistency and readability.

### 2. Method Length

**Guideline: Keep methods < 30 lines**

**Rationale:**
✅ **Readability**: Can see entire method on screen  
✅ **Testability**: Easier to test small methods  
✅ **Maintainability**: Easier to understand and modify  

**When a method gets too long:**
- Extract helper methods
- Refactor into smaller units

**Example:**
```java
// Long method (bad)
public void displayDoctorInfo(Doctor doctor) {
    // 50 lines of printing code
}

// Refactored (good)
public void displayDoctorInfo(Doctor doctor) {
    displayBasicInfo(doctor);
    displaySpecialization(doctor);
    displayStatistics(doctor);
}
```

### 3. Import Organization

**Decision: Explicit imports, no wildcards**

```java
// Good
import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Patient;

// Avoided
import com.airtribe.meditrack.entity.*;
```

**Rationale:**
✅ **Clarity**: See exactly what's used  
✅ **No conflicts**: Avoid name collisions  
✅ **Intentional**: Each import is deliberate  

**Exception:** Static imports for constants (limited use)

---

## Future Enhancements - Design Foundation

### Ready for Migration

**1. Database Integration:**

Current:
```java
public class DoctorService {
    private final DataStore<Doctor> doctorStore;
}
```

Future:
```java
public class DoctorService {
    private final DoctorDAO doctorDAO;  // DAO replaces DataStore
}
```

Service layer methods remain the same ✅

**2. REST API:**

Current:
```java
doctorService.createDoctor(name, age, spec, fee);
```

Future REST Controller:
```java
@RestController
@RequestMapping("/api/doctors")
public class DoctorController {
    @Autowired
    private DoctorService doctorService;  // Same service!
    
    @PostMapping
    public ResponseEntity<Doctor> create(@RequestBody DoctorDTO dto) {
        return ResponseEntity.ok(doctorService.createDoctor(...));
    }
}
```

Service layer unchanged ✅

**3. Reactive Streams:**

Current:
```java
return doctorStore.getAll().stream()...
```

Future:
```java
return Flux.fromIterable(doctorStore.getAll())...
```

Similar patterns ✅

---

## Lessons Learned

### What Worked Well:

✅ **Layered architecture**: Easy to understand and maintain  
✅ **Generic DataStore**: Eliminated code duplication  
✅ **Centralized validation**: Consistent rules  
✅ **Enum usage**: Type safety prevented many bugs  
✅ **Factory pattern**: Flexible billing strategies  

### What Could Be Improved:

⚠️ **No unit tests**: Manual testing only  
⚠️ **No logging framework**: Uses System.out.println  
⚠️ **No configuration file**: Constants hardcoded  
⚠️ **No dependency injection**: Manual wiring  

### If Starting Over:

**Would keep:**
- Layered architecture
- Package organization
- Design patterns (Singleton, Factory)
- Generic DataStore

**Would change:**
- Add JUnit from start
- Use SLF4J for logging
- Properties file for configuration
- Interface-based DAO layer from start

---

## Design Philosophy

### Core Principles:

1. **Simplicity First**: Start simple, add complexity only when needed
2. **Type Safety**: Use Java's type system to prevent errors
3. **Fail Fast**: Catch errors early with validation
4. **Separation of Concerns**: Each class/layer has clear responsibility
5. **Future-Proof**: Decisions allow for future enhancements

### Guiding Questions for Design Decisions:

- ✅ Is it simple enough for beginners to understand?
- ✅ Does it demonstrate the required OOP concept?
- ✅ Can it scale to production later?
- ✅ Is it maintainable?
- ✅ Does it follow Java best practices?

---

## Conclusion

The MediTrack project demonstrates thoughtful design decisions across multiple dimensions:

- **Architecture**: Layered for separation of concerns
- **Patterns**: Singleton for ID generation, Factory for bill creation
- **OOP**: Comprehensive use of inheritance, polymorphism, encapsulation, abstraction
- **Data Structures**: Appropriate choices for performance and maintainability
- **Type Safety**: Enums and generics for compile-time checking
- **Exceptions**: Rich context and proper handling
- **Scalability**: Foundation for database and web migration

Every decision balances:
- Educational value (demonstrate concepts)
- Practical applicability (real-world patterns)
- Maintainability (clean, understandable code)
- Future extensibility (room to grow)

The result is a project that serves both as a learning tool and as a foundation for a production-ready application.

---

## Appendix: Decision Log

### Key Architectural Decisions (ADR Format)

**ADR-001: Use Layered Architecture**
- **Status**: Accepted
- **Context**: Need to separate UI, business logic, and data access
- **Decision**: Implement 4-layer architecture
- **Consequences**: Better maintainability, testability; more files

**ADR-002: Generic DataStore instead of separate DAOs**
- **Status**: Accepted
- **Context**: Repetitive storage logic for each entity
- **Decision**: Implement generic `DataStore<T>`
- **Consequences**: Code reuse, demonstrates generics; less flexibility per entity

**ADR-003: Eager Singleton for IdGenerator**
- **Status**: Accepted
- **Context**: Need thread-safe ID generation
- **Decision**: Eager initialization with AtomicInteger
- **Consequences**: Thread-safe, simple; small memory overhead

**ADR-004: Enum for AppointmentStatus**
- **Status**: Accepted
- **Context**: Status was string-based (error-prone)
- **Decision**: Replace with enum
- **Consequences**: Type safety, better IDE support; migration effort

**ADR-005: Immutable BillSummary**
- **Status**: Accepted
- **Context**: Need read-only bill reports
- **Decision**: Final class with final fields
- **Consequences**: Thread-safe, cacheable; requires factory method

**ADR-006: CSV for Persistence**
- **Status**: Accepted
- **Context**: Need simple persistence without database
- **Decision**: CSV files with try-with-resources
- **Consequences**: Human-readable, no dependencies; not suitable for large scale

**ADR-007: No Framework Dependencies**
- **Status**: Accepted
- **Context**: Educational project, learn core Java
- **Decision**: Pure Java 17, no Spring/Hibernate
- **Consequences**: Clear understanding of fundamentals; more boilerplate code

---

**Document Version:** 1.0  
**Last Updated:** March 8, 2026  
**Project:** MediTrack v1.0.0  
**Author:** MediTrack Development Team

---

*This document will evolve as the project grows and new design decisions are made.*

