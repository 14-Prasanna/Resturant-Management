# Admin Login - OOP Concepts Implementation Guide

## Overview
The Admin Login system now implements key Object-Oriented Programming (OOP) concepts with robust validation.

---

## 1. **ENCAPSULATION** 
*Hiding internal details and providing controlled access through methods*

### Location: `AdminLogin.java` (Model)

```java
private String username;        // Private - not directly accessible
private String password;        // Private - encapsulated

// Public setter with validation
public void setUsername(String username) {
    // Validation logic inside setter
    if (username.matches("^[0-9]+$")) {
        throw new IllegalArgumentException("Only string value is allowed...");
    }
    this.username = username;
}
```

**Benefits:**
- Data is protected from invalid states
- Validation happens before data is stored
- Changes to validation logic only affect setters

---

## 2. **ABSTRACTION**
*Hiding complex implementation details behind simple interfaces*

### Location: `InputValidator.java` (Utility Class)

```java
// Complex validation logic is hidden
public static boolean isValidUsername(String username) {
    return username.matches("^[a-zA-Z][a-zA-Z0-9_]*$");
}

// Simple public method - internal regex complexity hidden
```

**Benefits:**
- Users of the validator don't need to understand regex
- Validation can be updated without affecting calling code
- Reusable across the application

---

## 3. **SINGLE RESPONSIBILITY PRINCIPLE (SRP)**
*Each class has one, well-defined responsibility*

### Separation of Concerns:

| Component | Responsibility |
|-----------|-----------------|
| **AdminLogin** (Model) | Data storage + field validation |
| **InputValidator** (Utility) | Input validation logic |
| **AdminLoginService** | Business logic + authentication |
| **AdminLoginRepository** | Data persistence/retrieval |
| **AdminLoginController** | User interaction + input masking |

---

## 4. **INPUT VALIDATION** ✓

### Username Validation (String only, no pure numbers)

**Controller Level** (`AdminLoginController.java`):
```java
// Validate before calling service
if (username.matches("^[0-9]+$")) {
    System.out.println("❌ Error: Only string value is allowed...");
    username = null;
}
```

**Service Level** (`AdminLoginService.java`):
```java
private boolean isValidInput(String username, String password) {
    if (username != null && username.matches("^[0-9]+$")) {
        System.out.println("❌ Error: Only string value is allowed...");
        return false;
    }
    return true;
}
```

**Model Level** (`AdminLogin.java`):
```java
public void setUsername(String username) {
    if (username.matches("^[0-9]+$")) {
        throw new IllegalArgumentException("❌ Only string value is allowed...");
    }
    this.username = username;
}
```

---

## 5. **PASSWORD MASKING** 🔐

### Location: `AdminLoginController.java`

```java
private String getHiddenPassword() {
    // Uses System.console().readPassword() for secure input
    // Characters are NOT displayed as you type
    char[] passwordChars = System.console().readPassword();
    
    // Secure: clear sensitive data from memory after use
    java.util.Arrays.fill(passwordChars, ' ');
}
```

**How it works:**
- When you type the password, no characters appear on screen
- Automatic fallback for IDE environments
- Secure memory clearing after reading

---

## 6. **LAYERED ARCHITECTURE**

```
┌─────────────────────────────────────────┐
│    AdminLoginController (Presentation)  │ ← User interaction
│    - Input validation                   │ ← Password masking
├─────────────────────────────────────────┤
│    AdminLoginService (Business Logic)   │ ← Authentication logic
│    - Input validation                   │ ← Business rules
├─────────────────────────────────────────┤
│    AdminLoginRepository (Data Access)   │ ← Database/Storage
├─────────────────────────────────────────┤
│    InputValidator (Utility)             │ ← Reusable validation
└─────────────────────────────────────────┘
```

---

## 7. **VALIDATION FLOW**

```
User Input
    ↓
Controller Layer:
  ├─ Check if username is empty
  ├─ Check if username is only numbers ❌
  └─ Get hidden password
    ↓
Service Layer:
  ├─ Validate input format
  ├─ Find admin in repository
  └─ Compare credentials
    ↓
Model Layer:
  ├─ Setter validation runs (if direct creation)
  └─ Ensure data integrity
    ↓
Result: Login Success/Failure
```

---

## 8. **ERROR MESSAGES**

| Scenario | Error Message |
|----------|---------------|
| Pure numbers as username | ❌ Error: Only string value is allowed. Numbers are not permitted as username. |
| Empty username | ❌ Error: Username cannot be empty |
| Empty password | ❌ Error: Username and password cannot be empty. |
| Invalid credentials | ❌ Invalid credentials. Returning to main menu... |
| Success | ✓ Login successful! Welcome, [username] |

---

## 9. **KEY OOP CONCEPTS DEMONSTRATED**

✅ **Encapsulation** - Private fields with controlled access  
✅ **Abstraction** - Hiding complex validation logic  
✅ **Single Responsibility** - Each class has one job  
✅ **Validation** - Multiple layers of validation  
✅ **Security** - Password masking (System.console().readPassword())  
✅ **Reusability** - InputValidator used across application  
✅ **Exception Handling** - IllegalArgumentException for invalid data  

---

## 10. **HOW TO USE**

### Standard Login:
```
--- Admin Login ---
Username: admin
Password: ········  (masked while typing)
✓ Login successful! Welcome, admin
```

### Invalid Username (Pure Numbers):
```
--- Admin Login ---
Username: 12345
❌ Error: Only string value is allowed. Numbers are not permitted as username.
Username: admin
Password: ········
✓ Login successful! Welcome, admin
```

---

## 11. **EXTENDING THE SYSTEM**

To add validation to other login types (Customer, Chef, etc.):

1. **Create model class** with setters that use InputValidator
2. **Create service class** with business logic
3. **Create controller** that uses password masking
4. **Reuse InputValidator** utility class

Example:
```java
public class CustomerLogin {
    private String email;
    
    public void setEmail(String email) {
        if (!InputValidator.isValidEmail(email)) {  // Add this method to InputValidator
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email;
    }
}
```

---

## Summary

The Admin Login system now demonstrates professional OOP practices:
- **Data Protection** through Encapsulation
- **Code Reusability** through Abstraction & Utilities
- **Clear Structure** through Layered Architecture
- **Robust Validation** at multiple levels
- **Security** through password masking
- **Maintainability** through proper separation of concerns

