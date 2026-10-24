<#
  Creates the remaining commits of the kata, one logical change per commit.

  Run from the project root (the folder that contains pom.xml):
      powershell -ExecutionPolicy Bypass -File .\commit-plan.ps1
  Useful options:
      -From 1 -To 12     run only part of the plan (the numbers are printed as it runs)
      -NoTrailer         leave out the Co-Authored-By lines at the end of each message
      -DryRun            print what would be committed without touching git
  Nothing is pushed. Run "git push" yourself when you are happy.
#>
param(
    [int]$From = 1,
    [int]$To = 999,
    [switch]$NoTrailer,
    [switch]$DryRun
)

$ErrorActionPreference = 'Stop'

$trailers = @(
    '--trailer', 'Co-Authored-By: Claude Sonnet 5.5 <noreply@anthropic.com>',
    '--trailer', 'Claude-Session: https://claude.ai/code/session_013YdopavUuiymrAHBaz2S9s'
)
if ($NoTrailer) { $trailers = @() }

$j = 'src/test/java/com/kata/booking'
$r = 'src/test/resources'

$plan = @(
    @{ Files = @("$r/junit-platform.properties"); Subject = 'test: configure Cucumber reports and default tag filter'; Body = 'HTML and JSON reports go to target/cucumber-reports. The default run skips @known-bug and @wip scenarios so the build stays green while known problems stay runnable.' },
    @{ Files = @("$j/runner/RunCucumberTest.java"); Subject = 'test: add Cucumber suite runner on the JUnit 5 platform'; Body = 'Hands the feature files to the Cucumber engine and sets com.kata.booking as the glue package.' },
    @{ Files = @("$r/config.properties"); Subject = 'test: add environment settings for the API under test'; Body = 'Base URL, admin credentials and a logging switch live in one file instead of being hardcoded in steps.' },
    @{ Files = @("$j/config/ApiConfig.java"); Subject = 'feat(config): load settings from properties with -D overrides'; Body = 'Plain class with a no-arg constructor so PicoContainer can inject it into the clients.' },
    @{ Files = @("$j/model/BookingDates.java"); Subject = 'feat(model): add BookingDates record'; Body = 'Java record with camelCase names mapped to the API field names.' },
    @{ Files = @("$j/model/BookingRequest.java"); Subject = 'feat(model): add BookingRequest record'; Body = 'Immutable create/replace payload with a withGuest helper for conflict tests.' },
    @{ Files = @("$j/model/BookingResponse.java"); Subject = 'feat(model): add BookingResponse record for the create result'; Body = 'Matches the flat body observed on the live API, which differs from the spec (BUG-03).' },
    @{ Files = @("$j/model/Credentials.java"); Subject = 'feat(model): add Credentials record for login'; Body = 'Small payload type for the authentication endpoint.' },
    @{ Files = @("$j/model/BookingOutcome.java"); Subject = 'feat(model): add sealed BookingOutcome with Created and Rejected'; Body = 'Lets callers use pattern matching instead of casts and keeps the raw response for assertions.' },
    @{ Files = @("$j/client/RequestSpecFactory.java"); Subject = 'feat(client): add shared Rest-Assured request specification'; Body = 'One place for base URI, JSON content type and optional request/response logging.' },
    @{ Files = @("$j/client/AuthClient.java"); Subject = 'feat(client): add AuthClient service object'; Body = 'Wraps POST /auth/login and provides an admin token helper for cleanup and protected calls.' },
    @{ Files = @("$j/client/BookingClient.java"); Subject = 'feat(client): add BookingClient service object'; Body = 'All booking HTTP calls live here; token is passed in (or null) so tests can call without signing in.' },
    @{ Files = @("$j/builder/StayDates.java"); Subject = 'feat(builder): generate random future stay dates'; Body = 'The site is shared and keeps bookings, so fixed dates would collide between runs.' },
    @{ Files = @("$j/builder/BookingRequestBuilder.java"); Subject = 'feat(builder): add BookingRequestBuilder with valid defaults'; Body = 'Each test overrides only the detail it checks; withField drives data-based negative tests.' },
    @{ Files = @("$j/builder/RawPayloads.java"); Subject = 'feat(builder): add raw JSON payloads as text blocks'; Body = 'Missing-field and malformed bodies that cannot be built from the typed request.' },
    @{ Files = @("$j/context/ScenarioContext.java"); Subject = 'feat(context): add per-scenario ScenarioContext'; Body = 'Replaces static fields. PicoContainer creates one instance per scenario and injects it into step classes.' },
    @{ Files = @("$j/steps/Hooks.java"); Subject = 'test(hooks): delete bookings created by a scenario after it ends'; Body = 'Keeps the shared site tidy. Cleanup problems are logged to the scenario and never fail the test.' },
    @{ Files = @("$j/steps/AuthSteps.java"); Subject = 'test(steps): add authentication steps'; Body = 'Sign-in steps phrased from the administrator point of view.' },
    @{ Files = @("$j/steps/CommonSteps.java"); Subject = 'test(steps): add reusable response assertions'; Body = 'Status and error-message checks shared by every feature.' },
    @{ Files = @("$r/features/auth.feature"); Subject = 'test(auth): add authentication scenarios'; Body = 'Valid login, a Scenario Outline of invalid credentials, and the empty-body case tagged @known-bug (BUG-02).' },
    @{ Files = @("$j/steps/BookingCreateSteps.java"); Subject = 'test(steps): add booking creation steps'; Body = 'Covers booking, validation, conflict, contract and data-table steps using the builder and client.' },
    @{ Files = @("$r/features/booking_create.feature"); Subject = 'test(booking): add create booking happy-path scenarios'; Body = 'Background, a Scenario Outline for stay lengths and a Data Table for several guests.' },
    @{ Files = @("$r/features/booking_create_validation.feature"); Subject = 'test(booking): add validation and boundary scenarios'; Body = 'Name, phone and email limits from the spec, values on the boundaries, bad dates and malformed bodies.' },
    @{ Files = @("$r/features/booking_create_conflict.feature"); Subject = 'test(booking): add double-booking conflict scenario'; Body = 'The same room and dates cannot be booked twice (409).' },
    @{ Files = @("$r/schemas/booking-created.json"); Subject = 'test(contract): add JSON schema for the create response'; Body = 'Describes the structure observed on the live API.' },
    @{ Files = @("$r/features/booking_create_contract.feature"); Subject = 'test(booking): add contract and response-time scenarios'; Body = 'Schema validation and a coarse response-time check.' },
    @{ Files = @("$j/steps/BookingRetrievalSteps.java"); Subject = 'test(steps): add booking retrieval steps'; Body = 'Lookup with and without a session token and with unknown or malformed ids.' },
    @{ Files = @("$r/features/booking_retrieve.feature"); Subject = 'test(booking): add retrieval scenarios tagged @known-bug'; Body = 'GET /booking/{id} returns 500 for every id on the live site (BUG-05, BUG-06).' },
    @{ Files = @("$j/steps/BookingManagementSteps.java"); Subject = 'test(steps): add update and cancel steps'; Body = 'Replace, patch and delete calls for signed-in and anonymous users.' },
    @{ Files = @("$r/features/booking_manage.feature"); Subject = 'test(booking): add update and cancel scenarios tagged @wip'; Body = 'Written from the spec but not verified against the live site yet.' },
    @{ Files = @("$r/features/booking_lifecycle.feature"); Subject = 'test(e2e): add booking lifecycle scenario tagged @wip'; Body = 'Proves a cancellation by booking the same room and dates again, avoiding the broken GET by id.' },
    @{ Files = @("$j/steps/HealthSteps.java"); Subject = 'test(steps): add service health steps'; Body = 'Checks the actuator health endpoint documented in the spec.' },
    @{ Files = @("$r/features/service_health.feature"); Subject = 'test(health): add health scenario tagged @known-bug'; Body = 'The documented health endpoint answers 500 on the live site (BUG-01).' },
    @{ Delete = @("$j/steps/.gitkeep", "$j/client/.gitkeep", "$j/model/.gitkeep", "$j/builder/.gitkeep", "$j/context/.gitkeep", "$j/config/.gitkeep", "$r/features/.gitkeep"); Subject = 'chore: remove .gitkeep placeholders'; Body = 'The packages now contain real files.' },
    @{ Files = @('BUG_REPORT.md'); Subject = 'docs: add bug report with evidence from API exploration'; Body = 'Six findings with requests, expected and actual behaviour, plus the list of unverified scenarios.' },
    @{ Files = @('.github/workflows/ci.yml'); Subject = 'ci: run the Cucumber suite on every push'; Body = 'Uploads the Cucumber HTML and JSON reports as a build artifact.' },
    @{ Files = @('README.md'); Subject = 'docs: write README with setup, tags, structure and assumptions'; Body = 'Explains how to build, run and filter the suite, the design choices and the assumptions made.' }
)

$i = 0
foreach ($step in $plan) {
    $i++
    if ($i -lt $From -or $i -gt $To) { continue }
    if ($DryRun) { Write-Host "[$i] $($step.Subject)"; continue }

    if ($step.ContainsKey('Delete')) {
        git rm -q -- $step.Delete
    } else {
        git add -- $step.Files
    }
    if ($LASTEXITCODE -ne 0) { throw "git add/rm failed at step $i" }

    git commit -m $step.Subject -m $step.Body @trailers
    if ($LASTEXITCODE -ne 0) { throw "git commit failed at step $i" }
    Write-Host "[$i] committed: $($step.Subject)"
}
