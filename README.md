# 📚 Library Management System

A library management system built in **Java** and **Spring Boot**, developed with **Test Driven Development (TDD)**.
It contains the core library logic (books, loans, late fees) and a **REST API** for managing loan transactions, stored in an **H2 database**.

---

## 📖 Project Description

Many small libraries, such as those in schools, universities and municipalities, still keep their
records in notebooks or Excel sheets. This causes several problems:

- **Record errors**: the same book is registered twice, or data such as the ISBN or title is missing.
- **Lack of information**: the librarian cannot immediately tell whether a book is available or on loan.
- **Lost books**: it is unclear who borrowed a book and whether it has been returned.
- **Wasted time**: searching manually for books and loans takes a long time.

**Library Management System** solves these problems with a centralized system in which every book
is registered with validated data and every loan, return and late fee is tracked automatically.

---

## 🎯 Main Goal

To **digitalize and automate the core processes of a library**: registering books, borrowing and returning them,
calculating late fees and storing loan transactions.

Every feature is developed with **TDD**: the test is written first (RED), then the minimal code that makes it
pass (GREEN), and finally the code is improved without changing its behavior (REFACTOR).

---

## ✨ Features

| # | Feature | Description | Status |
|---|---------|-------------|--------|
| 1 | Add a book | Register a book with ISBN, title and author. Missing ISBN or title and duplicate ISBNs are rejected. | ✅ Implemented (TDD) |
| 2 | Borrow a book | A member borrows an available book. Unknown books, books already on loan and members without an ID are rejected. | ✅ Implemented (TDD) |
| 3 | Return a book | A borrowed book becomes available again. Books that are not on loan or do not exist are rejected. | ✅ Implemented (TDD) |
| 4 | Late fee calculation | 14 free days, 0.50 € per late day, capped at 10 €. | ✅ Implemented (TDD) |
| 5 | Borrow limit | A member can borrow at most 3 books at the same time. | ✅ Implemented (TDD) |
| 6 | Transactions REST API | Create, list, read, update and delete loan transactions stored in H2. | ✅ Implemented (TDD) |
| 7 | Book search | Search by title or author. | 🔜 Planned |
| 8 | Member management | Register and manage library members. | 🔜 Planned |

---

## 🛠️ Technologies

| Technology | Purpose |
|------------|---------|
| **Java 17** | Main programming language |
| **Spring Boot 3** | Application framework and REST API |
| **Spring Data JPA** | Repository layer and database access |
| **H2 Database** | In-memory database |
| **Bean Validation** | Validation of request bodies |
| **Maven** | Project management, dependencies and running the tests |
| **JUnit 5** | Unit tests, including parameterized tests |
| **Spring Boot Test / MockMvc** | REST API tests and integration tests |
| **Mockito** | Mocking the service layer in controller tests |
| **JaCoCo** | Code and branch coverage report |
| **Git & GitHub** | Version control; the commit history documents the TDD cycles |

---

## 🏗️ Architecture

```
HTTP request
    │
    ▼
TransactionController   (REST endpoints, validation, HTTP status codes)
    │
    ▼
TransactionService      (business logic, 404 handling)
    │
    ▼
TransactionRepository   (Spring Data JPA)
    │
    ▼
H2 database             (table: transactions)
```

`GlobalExceptionHandler` converts exceptions to JSON error responses (400 and 404).
The fee of a transaction is calculated automatically by `FeeCalculator`.

---

## 🌐 REST API

Base URL: `http://localhost:8080`

| Method | Endpoint | Description | Success | Errors |
|--------|----------|-------------|---------|--------|
| POST | `/api/transactions` | Create a transaction | 201 | 400 |
| GET | `/api/transactions` | List all transactions | 200 | |
| GET | `/api/transactions/{id}` | Get a transaction by id | 200 | 404 |
| PUT | `/api/transactions/{id}` | Update a transaction | 200 | 400, 404 |
| DELETE | `/api/transactions/{id}` | Delete a transaction | 204 | 404 |

**Request body (POST and PUT):**
```json
{
  "bookIsbn": "111",
  "memberId": "M001",
  "daysBorrowed": 20
}
```

Validation rules: `bookIsbn` and `memberId` must not be blank, and `daysBorrowed` must be 0 or more.

**Response example (201 Created):**
```json
{
  "id": 1,
  "bookIsbn": "111",
  "memberId": "M001",
  "daysBorrowed": 20,
  "fee": 3.0
}
```

**Error examples:**
```json
{ "error": "Transaction not found: 99" }
```
```json
{ "error": "Validation failed", "details": { "daysBorrowed": "daysBorrowed cannot be negative" } }
```

---

## 📁 Project Structure

```
library-management-system
├── pom.xml
├── README.md
└── src
    ├── main
    │   ├── java/com/library
    │   │   ├── LibraryApplication.java       # Spring Boot entry point
    │   │   ├── Book.java                     # Book model
    │   │   ├── Library.java                  # Library logic (add, borrow, return, loan limit)
    │   │   ├── FeeCalculator.java            # Late fee calculation
    │   │   ├── Transaction.java              # JPA entity
    │   │   ├── TransactionRequest.java       # Request body with validation
    │   │   ├── TransactionRepository.java    # Repository layer
    │   │   ├── TransactionService.java       # Service layer
    │   │   ├── TransactionController.java    # REST controller
    │   │   ├── TransactionNotFoundException.java
    │   │   └── GlobalExceptionHandler.java   # 400 and 404 responses
    │   └── resources
    │       └── application.properties        # H2 configuration
    └── test/java/com/library
        ├── AddBookTest.java
        ├── BorrowBookTest.java
        ├── ReturnBookTest.java
        ├── FeeCalculatorTest.java            # Boundary and parameterized tests
        ├── BorrowLimitTest.java              # Branch and boundary tests
        ├── ValidationTest.java               # Parameterized negative tests
        ├── TransactionRepositoryTest.java    # @DataJpaTest: save, read, update, delete
        ├── TransactionControllerTest.java    # @WebMvcTest + MockMvc
        └── TransactionIntegrationTest.java   # @SpringBootTest: controller → service → repository → H2
```

---

## ▶️ Setup and Running

Requirements: **Java 17+** and **Maven 3.8+**.

**Clone and run all tests:**
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

**Start the API:**
```bash
mvn spring-boot:run
```
Then open http://localhost:8080/api/transactions.

**Try it from PowerShell:**
```powershell
$h = @{"Content-Type"="application/json"}
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/transactions -Headers $h -Body '{"bookIsbn":"111","memberId":"M001","daysBorrowed":20}'
Invoke-RestMethod http://localhost:8080/api/transactions
Invoke-RestMethod -Method Delete -Uri http://localhost:8080/api/transactions/1
```

**H2 console:** http://localhost:8080/h2-console
JDBC URL `jdbc:h2:mem:librarydb`, user `sa`, empty password. The data is kept in memory and resets when the app stops.

**Coverage report:** after `mvn clean test`, open `target/site/jacoco/index.html`.

---

## 🧪 Types of Tests

| Type | Where it is demonstrated |
|------|--------------------------|
| Positive testing | `AddBookTest`, `BorrowBookTest`, `ReturnBookTest`, `TransactionControllerTest` |
| Negative testing | `ValidationTest`, `FeeCalculatorTest`, invalid bodies and negative `daysBorrowed` in `TransactionControllerTest` |
| Boundary value testing | `FeeCalculatorTest` (13/14/15 and 33/34/35 days), `BorrowLimitTest` (3rd and 4th book), `daysBorrowed` = 0 and -1 |
| Branch testing | Branches of `addBook`, `borrowBook`, `returnBook` and `calculateFee`; JaCoCo report |
| Parameterized tests | `@CsvSource`, `@ValueSource`, `@NullAndEmptySource` |
| Repository tests | `TransactionRepositoryTest` (save, read, update, delete on H2) |
| REST API tests (MockMvc) | `TransactionControllerTest`: create, list, get by id, update, delete, invalid input (400), unknown id (404) |
| Integration tests | `TransactionIntegrationTest`: full chain Controller → Service → Repository → H2 |

---

## 🔁 TDD Process (Red → Green → Refactor)

The commit history shows complete TDD cycles:

| Cycle | 🔴 RED | 🟢 GREEN | 🔵 REFACTOR |
|-------|--------|----------|-------------|
| 1. Add a book | Tests for `addBook` fail | Implement `addBook` | Extract `requireText` |
| 2. Borrow | Tests for `borrowBook` fail | Implement `borrowBook` | Extract `findBookOrThrow` |
| 3. Return | Tests for `returnBook` fail | Implement `returnBook` | Move state checks into `Book` |
| 4. Late fee | Tests for `FeeCalculator` fail | Implement `calculateFee` | Constants `DAILY_FEE`, `MAX_FEE` |
| 5. Borrow limit | Tests for `MAX_LOANS` fail | Check in `borrowBook` | `getBorrowedCount` with a stream |
| 6. Persistence | Tests for `TransactionRepository` fail | `Transaction` entity and repository | |
| 7. REST create/read | MockMvc tests for POST/GET fail | Service and controller | Extract `GlobalExceptionHandler` |
| 8. Update/delete | MockMvc tests for PUT/DELETE fail | Service and controller methods | Remove request boilerplate in tests |
| 9. Integration | | 2 `@SpringBootTest` tests added | |

View the history with `git log --oneline`.

---

## 👩‍💻 Author

**Sara Bajraktari**, Master's degree, Semester 3
Course: *Test Driven Development*