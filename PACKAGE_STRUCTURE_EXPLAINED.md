# Java Package Structure Explained

## Why `com.airtribe.meditrack` NOT `src.main.com.airtribe.meditrack`

### Visual Explanation

```
PROJECT ROOT
│
└── meditrack-clinic-and-appointment-management-system/
    │
    └── src/
        └── main/
            └── java/  ⬅️ THIS IS THE SOURCE ROOT (where Java starts looking)
                │
                └── com/
                    └── airtribe/
                        └── meditrack/  ⬅️ THIS becomes the package name
                            ├── Main.java
                            ├── entity/
                            ├── service/
                            └── ...
```

---

## The Rule

In Java, **package name = directory structure AFTER the source root**

### Example with our project:

| Component | Value |
|-----------|-------|
| **Project Root** | `C:\...\meditrack-clinic-and-appointment-management-system\` |
| **Source Root** | `src/main/java/` |
| **Package Path** | `com/airtribe/meditrack/` |
| **Package Declaration** | `package com.airtribe.meditrack;` ✅ |
| **Import Statement** | `import com.airtribe.meditrack.entity.Person;` ✅ |

---

## What Happens with Wrong Package Name

### ❌ WRONG: `package src.main.com.airtribe.meditrack;`

When you declare the package as `src.main.com.airtribe.meditrack`, Java expects the file structure to be:

```
src/
└── main/
    └── java/
        └── src/           ⬅️ Java looks for this
            └── main/
                └── java/
                    └── com/
                        └── airtribe/
                            └── meditrack/
                                └── Main.java
```

But your actual structure is:

```
src/
└── main/
    └── java/
        └── com/           ⬅️ Your actual files are here
            └── airtribe/
                └── meditrack/
                    └── Main.java
```

**Result**: Java can't find the files! ❌

---

## ✅ CORRECT: `package com.airtribe.meditrack;`

With the correct package declaration, Java looks in the right place:

```
src/
└── main/
    └── java/              ⬅️ Source root
        └── com/           ⬅️ Java starts looking here
            └── airtribe/
                └── meditrack/
                    └── Main.java  ✅ Found!
```

---

## How javac Uses Source Root

When you compile:
```bash
javac -sourcepath src/main/java src/main/java/com/airtribe/meditrack/Main.java
```

1. `-sourcepath src/main/java` tells Java: "This is where packages start"
2. Java sees `package com.airtribe.meditrack;` in Main.java
3. Java looks for: `src/main/java/com/airtribe/meditrack/Main.java` ✅
4. Finds it! Compilation succeeds! ✅

---

## Maven/Gradle Convention

The `src/main/java` structure is a **Maven/Gradle convention**, not a Java requirement:

- **Maven/Gradle**: Uses `src/main/java` for source files
- **Java**: Doesn't care about `src/main/java`, only about what comes AFTER

So:
- Directory: `src/main/java/com/airtribe/meditrack/`
- Package: `com.airtribe.meditrack` (ignores `src/main/java`)

---

## Analogy

Think of it like a postal address:

```
Building Name: src/main/java/        ⬅️ Building (source root)
Floor: com/                           ⬅️ Floor
Department: airtribe/                 ⬅️ Department  
Room: meditrack/                      ⬅️ Room
```

When you send mail (import), you don't include the building name:
- ❌ WRONG: "Building.Floor.Department.Room"
- ✅ CORRECT: "Floor.Department.Room"

Similarly:
- ❌ WRONG: `src.main.com.airtribe.meditrack`
- ✅ CORRECT: `com.airtribe.meditrack`

---

## IntelliJ IDEA

In IntelliJ:
1. Right-click `src/main/java` folder
2. Select "Mark Directory as" → "Sources Root"
3. IntelliJ automatically uses correct package names

---

## Summary

✅ **Source Root**: `src/main/java/` (Maven convention)  
✅ **Package**: Everything AFTER source root = `com.airtribe.meditrack`  
✅ **File Location**: `src/main/java/com/airtribe/meditrack/Main.java`  
✅ **Package Declaration**: `package com.airtribe.meditrack;`  
✅ **Import**: `import com.airtribe.meditrack.entity.Person;`  

❌ **NEVER**: `package src.main.com.airtribe.meditrack;`

---

## MediTrack Project - Final Status

🎉 **All files now use the correct package structure!**

- ✅ Package: `com.airtribe.meditrack`
- ✅ Compilation: Successful
- ✅ Ready to run: `java -cp out com.airtribe.meditrack.Main`

---

**Remember**: The `src/main/java` path is just a folder structure convention. The Java package name starts from what comes AFTER the source root!

