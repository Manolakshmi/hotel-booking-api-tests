# BUG-05 / BUG-06: on the live site GET /booking/{id} answers 500 for every id, so the whole
# feature is tagged @known-bug. Run it with: mvn test -Dcucumber.filter.tags="@known-bug"
@booking @retrieve @known-bug
Feature: Retrieve a booking
  Hotel staff look up a booking by its id.

  Background:
    Given the administrator is signed in
    And a guest with valid contact details
    And a room has been booked

  Scenario: Administrator retrieves a booking
    When the administrator retrieves the booking
    Then the booking details are returned

  Scenario Outline: Unknown or malformed booking ids are rejected cleanly
    When the administrator retrieves booking "<id>"
    Then the request is rejected as a client error

    Examples:
      | id     |
      | 999999 |
      | abc    |

  @security
  Scenario: Booking cannot be retrieved without signing in
    When the booking is retrieved without signing in
    Then the request is refused with status 401
