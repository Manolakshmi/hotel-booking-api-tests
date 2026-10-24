# Bug report: Hotel Booking API

Environment: <https://automationintesting.online/api> (shared public site)
Explored on: 24 Oct 2026 with Postman, then covered by `@known-bug` scenarios in this repository.
Reference: `src/test/resources/spec/booking.yaml` (OpenAPI spec).

| ID | Severity | Area | Summary | Automated as |
|----|----------|------|---------|--------------|
| BUG-01 | Medium | Health | Health endpoint returns 500 | `service_health.feature` |
| BUG-02 | Medium | Auth | Login without a body returns 500 | `auth.feature` (`@known-bug`) |
| BUG-03 | Low | Booking | Create response differs from the spec | asserted in `booking_create*.feature` |
| BUG-04 | Low | Booking | 409 conflict is not documented | `booking_create_conflict.feature` |
| BUG-05 | High | Booking | `GET /booking/{id}` returns 500 for every id | `booking_retrieve.feature` |
| BUG-06 | Medium | Booking | Malformed id returns 500 instead of a client error | `booking_retrieve.feature` |

## BUG-01 Health endpoint returns 500
* **Request:** `GET /api/booking/actuator/health`
* **Expected (spec):** 200 with `{"status":"UP"}`
* **Actual:** 500, plain text body `Internal Server Error`

## BUG-02 Login without a body returns 500
* **Request:** `POST /api/auth/login` with no body
* **Expected:** a client error (400 or 401) with a clear message
* **Actual:** 500 with `{"error":"An unexpected error occurred"}`
* **Note:** login with valid credentials works (200 with a token).

## BUG-03 Create response differs from the spec
* **Request:** `POST /api/booking` with a valid body (room 1, 2027-05-12 to 2027-05-14)
* **Expected (spec):** 200 with `{"bookingid": ..., "booking": {...}}`
* **Actual:** 201 Created with a flat body: `bookingdates`, `bookingid`, `depositpaid`,
  `firstname`, `lastname`, `roomid`. There is no `booking` wrapper, and `email` and `phone` are
  not returned.
* **Impact:** a client written from the spec cannot parse the response. Either the spec or the
  service should change. 201 is the better status for a create, so the spec is probably outdated.

## BUG-04 Conflict response is not documented
* **Request:** send the same valid booking twice
* **Actual:** the second call returns 409 with `{"error":"Failed to create booking"}`
* **Expected:** the spec lists only 200 and 400. The error text does not say why the booking failed.

## BUG-05 GET booking by id returns 500
* **Requests:** `GET /api/booking/4` and `GET /api/booking/5` (booking 5 had just been created)
* **Expected (spec):** 200 with the booking, or 401 without a token
* **Actual:** 500, plain text `Internal Server Error`
* **Ruled out:** the response is identical with a freshly issued `token` cookie, without any
  cookie, and for a booking created seconds earlier.
* **Impact:** a created booking cannot be read back through the API.

## BUG-06 Malformed id returns 500
* **Request:** `GET /api/booking/abc`
* **Expected:** 400 or 404
* **Actual:** 500, plain text `Internal Server Error`

## Not yet verified
Scenarios tagged `@wip` are written from the spec but have not been confirmed on the live site:
replace, partial update and cancel of a booking, 401 without a token on cancel, past check-in
dates being rejected, and the exact 400 messages for the validation rules.
