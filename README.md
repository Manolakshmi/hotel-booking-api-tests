# Hotel Booking API Tests

API test automation framework for the booking endpoint of <https://automationintesting.online/>,
built with **Java 17, Rest-Assured and Cucumber (JUnit 5)**.

The scenarios are written in Gherkin so they can be read without knowing Java. All HTTP details
live in small client classes, and the step definitions only describe business behaviour.

## Prerequisites

| Tool  | Version |
|-------|---------|
| JDK   | 17 or higher |
| Maven | 3.9 or higher |
| Git   | any recent version |

Lombok is used for constructor injection. Maven applies it automatically; if you open the project
in IntelliJ, enable *Settings > Build, Execution, Deployment > Compiler > Annotation Processors >
Enable annotation processing*.

## Build and run

```bash
mvn clean test                      # default run (see "Tags" below)
mvn clean test -Dcucumber.filter.tags="@smoke"
mvn clean test -Dcucumber.filter.tags="@known-bug"   # scenarios that currently fail on the live site
mvn clean test -Dapi.logging=false  # quieter console output
```

Reports are written to `target/cucumber-reports/`:

* `cucumber.html` is the human-readable execution report
* `cucumber.json` is the machine-readable result

## Tags

| Tag | Meaning |
|-----|---------|
| `@smoke` | Quick checks that the main flows work |
| `@positive` / `@negative` | Expected-success and expected-rejection scenarios |
| `@contract` | Response structure (JSON schema) and response time |
| `@security` | Behaviour without a valid session |
| `@e2e` | Multi-step flows |
| `@known-bug` | Fails today because of a confirmed defect in the API, see `BUG_REPORT.md` |
| `@wip` | Written from the OpenAPI spec but not verified against the live site yet |

The default run excludes `@known-bug` and `@wip` (`cucumber.filter.tags` in
`src/test/resources/junit-platform.properties`), so the build stays green while the known
problems stay visible and runnable.

## Project structure

```
src/test
├── java/com/kata/booking
│   ├── runner/    RunCucumberTest         JUnit 5 suite that starts Cucumber
│   ├── config/    ApiConfig               base URL and credentials, with -D overrides
│   ├── model/     records for requests and responses, sealed BookingOutcome
│   ├── client/    AuthClient, BookingClient, RequestSpecFactory   (all HTTP calls)
│   ├── builder/   BookingRequestBuilder, StayDates, RawPayloads   (test data)
│   ├── context/   ScenarioContext         state shared between steps of one scenario
│   └── steps/     step definitions and Hooks (cleanup)
└── resources
    ├── features/  Gherkin feature files
    ├── schemas/   JSON schema for the create-booking response
    ├── config.properties
    └── junit-platform.properties
```

## Design choices

* **Service Object pattern.** `BookingClient` and `AuthClient` wrap the endpoints. A change to a
  path or header is made in one place.
* **Builder pattern.** `BookingRequestBuilder` starts from a valid booking, so each test states only
  the detail it checks.
* **Dependency injection.** PicoContainer injects `ScenarioContext` and the clients through
  constructors (Lombok `@RequiredArgsConstructor`). Every scenario gets its own context, so there
  is no shared static state.
* **Modern Java.** Records for models, a sealed interface (`BookingOutcome`) with pattern matching
  (`instanceof Created created`), switch expressions in the builder, and text blocks for raw
  JSON payloads.
* **Gherkin features used.** Background, Scenario Outline with several Examples blocks, Data
  Tables, Tags and comments.
* **Repeatable data.** Stay dates are generated from today's date with a random offset, because the
  site is shared and keeps bookings. An `@After` hook deletes the bookings a scenario created.

## Assumptions

* The test site is a public practice environment, so admin credentials are kept in
  `config.properties`.
* Business limits come from the OpenAPI spec (`src/test/resources/spec/booking.yaml`) and were
  checked against the live API: first name 3 to 18 characters, phone 11 to 21 characters, a
  well-formed email address. The live API allows 3 to 30 characters for the last name, although the
  spec says 18 (BUG-09), so the tests use 30.
* Observed behaviour wins over the spec when the two differ. A successful create returns
  **201** (the spec says 200), so the tests assert 201 and the difference is listed in the bug report.
* Two bookings for the same room and overlapping dates are expected to conflict (409).
* Cancellation is expected to return 201, as documented in the spec; this is unverified (`@wip`).

## Known issues

See [BUG_REPORT.md](BUG_REPORT.md) for the defects found while exploring the API, with the exact
requests and responses.

## CI

`.github/workflows/ci.yml` runs `mvn clean test` on every push and uploads the Cucumber report as a
build artifact.
