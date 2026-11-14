# 📚 Library Management System

A library management system built in **Java** using **Test Driven Development (TDD)**.

---

## 📖 Project Description

Many small libraries, such as those in schools, universities and municipalities, still keep their
book records in notebooks or Excel sheets. This approach causes several problems:

- **Record errors**: the same book is registered twice, or data such as the ISBN or title is missing.
- **Lack of information**: the librarian cannot immediately tell whether a book is available or on loan.
- **Lost books**: it is unclear who borrowed a book and whether it has been returned.
- **Wasted time**: searching manually for books and loans takes a long time.

**Library Management System** solves these problems with a centralized system in which every book
is registered with validated data and every loan and return is tracked automatically.

---

## 🎯 Main Goal

The main goal is to **digitalize and automate the core processes of a library**:
registering books, borrowing them and returning them. This makes record keeping accurate, fast and reliable.

The application is developed with **TDD**: for every feature, the test is written first (RED), then the
minimal code that makes it pass (GREEN), and finally the code is improved without changing its behavior (REFACTOR).

---

## ✨ Features

| # | Feature | Description | Status |
|---|---------|-------------|--------|
| 1 | Add a book | Register a book with ISBN, title and author. Books with no ISBN, no title or a duplicate ISBN are rejected. | ✅ Implemented (TDD) |
| 2 | Borrow a book | A member borrows an available book. Unknown books, books already on loan and members without an ID are rejected. | ✅ Implemented (TDD) |
| 3 | Return a book | A borrowed book is returned and becomes available again. Books that are not on loan or do not exist are rejected. | ✅ Implemented (TDD) |
| 4 | Late fee calculation | 14 free days, 0.50 € per late day, capped at 10 €. | ✅ Implemented (TDD) |
| 5 | Borrow limit | A member can borrow at most 3 books at the same time. | ✅ Implemented (TDD) |
| 6 | Book search | Search by title or author. | 🔜 Planned |
| 7 | Member management | Register and manage library members. | 🔜 Planned |

---

## 🛠️ Technologies

| Technology | Purpose |
|------------|---------|
| **Java 17** | Main programming language |
| **Maven** | Project management, dependencies and running the tests |
| **JUnit 5 (Jupiter)** | Writing and running unit tests, including parameterized tests |
| **JaCoCo** | Code and branch coverage report |
| **Git & GitHub** | Version control; the commit history documents the TDD cycles |
| **VS Code / IntelliJ IDEA** | Development environment |

---

## 📁 Project Structure

```
library-management-system
├── pom.xml
├── README.md
└── src
    ├── main/java/com/library
    │   ├── Book.java                 # Book model
    │   ├── Library.java              # Library logic (add, borrow, return, loan limit)
    │   └── FeeCalculator.java        # Late fee calculation
    └── test/java/com/library
        ├── AddBookTest.java          # Tests for adding a book
        ├── BorrowBookTest.java       # Tests for borrowing
        ├── ReturnBookTest.java       # Tests for returning
        ├── FeeCalculatorTest.java    # Boundary and parameterized tests for fees
        ├── BorrowLimitTest.java      # Branch and boundary tests for the loan limit
        └── ValidationTest.java       # Parameterized negative tests
```

---

## ▶️ Setup and Running

Requirements: **Java 17+** and **Maven 3.8+**.

```bash
git clone https://github.com/SaraBajraktari1/library-managment-system.git
cd library-managment-system
mvn clean test
```

Expected result:

```
Tests run: ..., Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Coverage report (including branch coverage): after `mvn clean test`, open `target/site/jacoco/index.html`.

---

## 🧪 Types of Tests

| Type | Where it is demonstrated |
|------|--------------------------|
| Positive testing | `AddBookTest`, `BorrowBookTest`, `ReturnBookTest` |
| Negative testing | `ValidationTest`, `FeeCalculatorTest.negativeDaysThrow` |
| Boundary value testing | `FeeCalculatorTest` (13/14/15 and 33/34/35 days), `BorrowLimitTest` (3rd and 4th book) |
| Branch testing | Branches of `addBook`, `borrowBook`, `returnBook` and `calculateFee`; JaCoCo report |
| Parameterized tests | `@CsvSource`, `@ValueSource`, `@NullAndEmptySource` |

---

## 🔁 TDD Process (Red → Green → Refactor)

The commit history shows five complete TDD cycles:

| Cycle | 🔴 RED | 🟢 GREEN | 🔵 REFACTOR |
|-------|--------|----------|-------------|
| 1. Add a book | Tests for `addBook` fail | Implement `addBook` | Extract the `requireText` method |
| 2. Borrow | Tests for `borrowBook` fail | Implement `borrowBook` | Extract the `findBookOrThrow` method |
| 3. Return | Tests for `returnBook` fail | Implement `returnBook` | Move the state checks into `Book` |
| 4. Late fee | Tests for `FeeCalculator` fail | Implement `calculateFee` | Constants `DAILY_FEE`, `MAX_FEE` |
| 5. Borrow limit | Tests for `MAX_LOANS` fail | Add the check in `borrowBook` | `getBorrowedCount` with a stream |

View the history with `git log --oneline`.

---

## 👩‍💻 Author

**Sara Bajraktari**, Master's degree, Semester 3
Course: *Test Driven Development*