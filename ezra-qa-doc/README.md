# ezra-qa-doc

Kotlin + Cucumber test module for the Ezra booking platform.

---

## Prerequisites

| Requirement | Version |
|---|---|
| JDK | 17 or higher |
| Chrome | Latest stable |
| Gradle | Bundled via wrapper — no install needed |

> Chrome is only required for UI tests (`@ui` tag).  
> API tests run without a browser.

---

## Environment setup

All credentials are passed as environment variables — never hardcoded.
```bash
# API base URL
export EZRA_BASE_URL=https://stage-api.ezra.com

# Member portal URL (UI tests only)
export EZRA_PORTAL_URL=https://myezra-staging.ezra.com

# Primary test member (User A)
export EZRA_USER_A_EMAIL=your-member-a@example.com
export EZRA_USER_A_PASSWORD=yourpassword

# Secondary test member (User B) — used in IDOR isolation tests
export EZRA_USER_B_EMAIL=your-member-b@example.com
export EZRA_USER_B_PASSWORD=yourpassword

# Package and center IDs for booking tests
export EZRA_PACKAGE_ID=your-package-uuid
export EZRA_CENTER_ID=your-center-uuid
```

> Tip: create a local `.env` file and source it with `source .env`.  
> The `.gitignore` already blocks `.env` files from being committed.

---

## Running tests

All commands run from the `ezra-qa-doc/` directory.
```bash
# All tests
./gradlew test

# API tests only (no browser)
./gradlew test -Dcucumber.options="--tags @booking or @security"

# UI tests only (headless Chrome)
./gradlew test -Dcucumber.options="--tags @ui"

# UI tests with visible browser — useful for debugging
./gradlew test -Dcucumber.options="--tags @ui" -Dezra.headless=false

# Single feature file
./gradlew test -Dcucumber.options="classpath:features/booking/tc001_happy_path.feature"

# Single scenario by tag
./gradlew test -Dcucumber.options="--tags @tc001"
```

---

## Test reports

After each run, open the HTML report:
```
build/reports/cucumber/cucumber.html
```

JSON and JUnit XML reports are also generated at:
```
build/reports/cucumber/cucumber.json
build/reports/cucumber/cucumber.xml
```

---

## Folder structure
```
src/test/
├── kotlin/com/ezra/qa/
│   ├── client/
│   │   └── EzraApiClient.kt        # OkHttp wrapper — one method per API call
│   │                               # Returns Pair<Response, String> so steps
│   │                               # can assert on status code AND body
│   │
│   ├── config/
│   │   └── TestConfig.kt           # All env vars / system properties in one place
│   │                               # Priority: system prop → env var → error
│   │
│   ├── context/
│   │   └── ScenarioContext.kt      # PicoContainer shared state — one instance
│   │                               # per scenario, auto-reset between scenarios
│   │
│   ├── driver/
│   │   └── DriverFactory.kt        # ChromeDriver setup via WebDriverManager
│   │                               # Lazy — browser only opens for UI scenarios
│   │
│   ├── model/
│   │   ├── request/Requests.kt     # Jackson data classes for request bodies
│   │   └── response/Responses.kt  # Jackson data classes for response parsing
│   │
│   ├── pages/                      # Selenium Page Objects — all XPath lives here
│   │   ├── BasePage.kt             # Shared WebDriver helpers and wait utilities
│   │   ├── LoginPage.kt            # /sign-in
│   │   ├── ScanSelectionPage.kt    # /book-scan/select-plan  (Step 1)
│   │   ├── SchedulePage.kt         # /book-scan/schedule     (Step 2)
│   │   ├── PaymentPage.kt          # /book-scan/reserve-appointment (Step 3)
│   │   └── ConfirmationPage.kt     # Post-payment confirmation screen
│   │
│   ├── runner/
│   │   └── TestRunner.kt           # @RunWith(Cucumber) entry point
│   │
│   └── steps/
│       ├── AuthSteps.kt            # Authentication step definitions (API)
│       ├── BookingSteps.kt         # TC-001, TC-002 step definitions (API)
│       ├── SecuritySteps.kt        # TC-SEC-001 IDOR step definitions (API)
│       └── ui/
│           ├── BookingUiSteps.kt   # All UI step definitions
│           └── Hooks.kt            # After{} browser teardown + screenshot on fail
│
└── resources/features/
    ├── booking/
    │   ├── tc001_happy_path.feature
    │   └── tc002_declined_card.feature
    ├── security/
    │   └── tc_sec_001_encounter_isolation.feature
    └── ui/
        ├── tc_ui_001_happy_path.feature
        ├── tc_ui_003_promo_code.feature
        └── tc_ui_navigation.feature
```

---

## Architecture decisions

### PicoContainer for dependency injection
`ScenarioContext` is injected into every step class by PicoContainer. This means:
- No static state anywhere
- Each scenario gets a clean context automatically
- Step classes stay small — each focused on one domain (auth, booking, security, UI)

### Page Object Model
All XPath locators and WebDriver interactions live in `pages/`. Step definitions
only call page object methods — no `By.xpath()` calls in step classes. This means:
- When the DOM changes, only the page object needs updating
- Step definitions read like plain English — easy to review and maintain

### Lazy browser initialisation
`DriverFactory` and all page objects in `ScenarioContext` are `by lazy`. Chrome only
launches when a UI step actually runs. API-only scenarios (`@booking`, `@security`)
complete without ever opening a browser window.

### XPath over CSS selectors
All locators use XPath because the Ezra portal uses dynamic CSS class names
(Tailwind / CSS-in-JS) that change between builds. XPath targets stable attributes:
`data-testid`, `aria-label`, `type`, `role`, and text content.
