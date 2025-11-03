# BnB Booking site – Java, Selenium, Cucumber (JUnit 5)

End‑to‑end UI tests for a public Bed & Breakfast web application, written in **Java 17**, **Selenium WebDriver**, **Cucumber BDD (Gherkin)** and **JUnit 5**, built with **Maven**.

The suite demonstrates a clean Page Object Model, dynamic date handling, positive and negative booking flows, Contact‑Us form validation, robust waits and safe clicking, and multi‑browser execution (Chrome, Firefox, Edge, Safari).

---

## 1) Project Structure

```
ui-banb-booking-jav-selen/
├─ pom.xml
└─ src
   ├─ main
   │  └─ java
   │     └─ pages/                  # Page Objects (HomePage, BookingPage, ContactPage, ...)
   └─ test
      ├─ java
      │  ├─ stepdefinitions/        # Cucumber step defs
      │  ├─ testrunner/             # CucumberTestRunner (JUnit 5)
      │  └─ utils/                  # BaseTest, WaitUtils, UiActions, ConfigLoader
      └─ resources
         └─ features/               # .feature files (Gherkin)
```

Key utilities:

- **BaseTest** – centralized WebDriver bootstrap, implicit wait, browser selection, headless, remote Grid.
- **WaitUtils** – explicit wait helpers (visibility, clickability, presence, scroll).
- **UiActions.safeClick** – scroll, offset and fallback (native → Actions → JS) to avoid sticky‑header interceptions.
- **ConfigLoader** – loads configuration from system properties, environment variables, or sensible defaults.

---

## 2) Prerequisites

- Java **17** or later (`java -version`)
- Maven **3.9+** (`mvn -v`)
- A desktop browser:
  - Chrome, Firefox, Edge, or Safari (macOS)
  - For Safari: run once on macOS – `safaridriver --enable`
- Internet access for Maven to download dependencies

No local driver binaries are required; the project uses **WebDriverManager** to resolve drivers automatically.

---

## 3) Configuration

Most settings can be supplied as **JVM system properties** or **environment variables**. The following keys are recognized by `ConfigLoader` and `BaseTest`:

| Property / Env Var            | Description                                                     | Default            |
|------------------------------|-----------------------------------------------------------------|--------------------|
| `baseUrl` / `BASE_URL`       | Target application base URL                                     | Public demo B&B    |
| `browser` / `BROWSER`        | `chrome`, `firefox`, `edge`, `safari`                           | `chrome`           |
| `headless` / `HEADLESS`      | `true` or `false`                                               | `false`            |
| `remoteUrl` / `REMOTE_URL`   | Selenium Grid/Selenoid endpoint (e.g., `http://localhost:4444/wd/hub`) | empty (local) |
| `implicitWaitSeconds`        | Global implicit wait in seconds                                  | `10`               |
| `pageLoadTimeoutSeconds`     | Page load timeout in seconds                                     | `60`               |

You can override any of these at runtime, for example:

```
mvn test -DbaseUrl=https://my.test.env -Dbrowser=firefox -Dheadless=true
```

---

## 4) Getting Started

### 4.1 Clone and open

```
git clone <your-repo-url> selenium-bnb-automation
cd selenium-bnb-automation
```

Open the folder in your IDE (IntelliJ IDEA recommended) and allow Maven to import the project.

### 4.2 Build

```
mvn -q -DskipTests clean install
```

This resolves all dependencies and validates the build.

---

## 5) Running the Tests

### 5.1 Run all scenarios (default: Chrome)

```
mvn test
```

### 5.2 Select a browser

```
mvn test -Dbrowser=chrome
mvn test -Dbrowser=firefox
mvn test -Dbrowser=edge
mvn test -Dbrowser=safari     # macOS only; run `safaridriver --enable` once
```

### 5.3 Headless execution

```
mvn test -Dbrowser=chrome -Dheadless=true
```

### 5.4 Remote Grid / Selenoid

```
mvn test -Dbrowser=chrome -DremoteUrl=http://localhost:4444/wd/hub
```

### 5.5 Filter by tags or name (Cucumber)

Run only scenarios tagged `@ui`:

```
mvn test -Dcucumber.filter.tags="@ui"
```

Run by scenario name substring:

```
mvn test -Dcucumber.filter.name="Check Availability"
```

### 5.6 Run from the IDE

- Open any `.feature` file and run the scenario or feature via the gutter icon
- Or run `CucumberTestRunner` under `src/test/java/testrunner`

---

## 6) What the Suite Covers

- **Check Availability**: validates home page title and header, scrolls to the booking widget, verifies default dates, sets dynamic dates (today + N; checkout = check‑in + nights), navigates to “Our Rooms” and verifies available room cards.
- **Book a Room (positive)**: selects a room, verifies the booking page sections and price summary, fills form fields with generated data (Faker), reserves, verifies confirmation and date range.
- **Booking validations (negative)**: submits an empty booking form and asserts returned validation messages (multiple phrases, order‑agnostic).
- **Cancel Booking (negative)**: clicks Cancel on the booking page and verifies return to the home page.
- **Contact Us**: navigates to the contact section, validates fields, enforces a minimum message length, submits, and verifies the acknowledgment panel.

The suite uses generated test data (Faker) with realistic constraints (phone numbers beginning with 07 and 11 digits).

---

## 7) Reports

By default, results are shown in the Maven console and standard Surefire output under `target/surefire-reports`.

To add a Cucumber HTML report, include a plugin in your `CucumberTestRunner` (example):

```java
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features") // looks in src/test/resources/features
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME,
        value = "pretty, html:target/cucumber-report.html, json:target/cucumber.json, junit:target/cucumber-reports/TEST-results.xml, summary"
)
public class CucumberTestRunner {
}
```

Then open `target/cucumber-report.html` after a run.

Allure or Extent can be integrated if you prefer; add the relevant Maven dependencies and plugins.

---

## 8) Troubleshooting

- **ElementClickInterceptedException** – the app uses a sticky navbar and dynamic content. The project provides `UiActions.safeClick` that performs scroll‑to‑center, header offset, native click, Actions click, and JS click fallback. Use it for buttons that are occasionally obscured.
- **Driver conflicts** – WebDriverManager downloads and caches drivers per browser version. If you see a mismatch, clear your cache or upgrade your browser.
- **Safari** – only on macOS. Enable once with `safaridriver --enable`. Headless is not supported by Safari.
- **Corporate proxies** – configure Maven’s proxy in `~/.m2/settings.xml` if dependency downloads fail.

---

## 9) Useful Maven Commands

```
# Clean build without tests
mvn -q -DskipTests clean install

# Run all tests headless in Firefox
mvn test -Dbrowser=firefox -Dheadless=true

# Run only @negative tests on Chrome
mvn test -Dbrowser=chrome -Dcucumber.filter.tags="@negative"

# Point to a different environment
mvn test -DbaseUrl=https://test.mycompany.com
```

---

## 10) Contributing

1. Create a feature and steps first (Gherkin → Step Definitions).
2. Implement Page Object methods with meaningful names; do not put locators in steps.
3. Prefer `WaitUtils` and `UiActions.safeClick` to reduce flakiness.
4. Keep data generation inside helpers to avoid duplication.
5. Run locally on multiple browsers before opening a pull request.

---

## 11) License

This test suite was created by Idris Odulami
