#  Inventory Management API Test Suite

This is an automated API test framework for the Shops Inventory Management System, implemented using Java, RestAssured, Cucumber (BDD), JUnit 5, and Maven.

---


---

##  Setup Instructions

### Prerequisites

- Java 11 or above
- Maven 3.6+
- IDE (e.g. IntelliJ IDEA)

### Steps

1. Clone the repository or unzip the file.
2. Ensure Java and Maven are available in your system path.
3. Open the project in IntelliJ or your preferred IDE.
4. Build the project:

```bash
mvn clean install
```

---

## Test Execution

### To run all tests and generate HTML report:

```bash
mvn clean test
```

### View the report:

Open in browser:
```
target/cucumber-reports.html
```

### To run tests by tag:

```bash
mvn clean verify -Dcucumber.filter.tags="@auth"
```

---

##  Testing Best Practices Used

###  BDD with Cucumber

- Feature files follow Gherkin syntax for human-readable test scenarios.
- Scenarios include happy paths and negative edge cases.

###  Reusability

- Shared steps (e.g., status, error message validation) in `CommonSteps.java`
- Response context shared via `SharedContext.java`

###  Logging & Debugging

- `APIHelper.java` logs full URLs, headers, and payloads using `prettyPeek()`.

###  Maintainability

- DTOs used for payloads: `ProductRequest`, `OrderRequest`, `LoginRequest`
- Centralized configuration and authentication handling
- Constructor-based injection using PicoContainer

###  Reporting

- Generates rich HTML reports with step-by-step breakdown
- Uses `maven-cucumber-reporting` plugin during the `verify` phase

---

##  Support
- Contact: Idris - cs97aad@yahoo.co.uk