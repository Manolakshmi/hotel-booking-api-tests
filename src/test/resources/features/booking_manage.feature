# Written from the OpenAPI spec but not verified against the live site yet, hence @wip.
@booking @manage @wip
Feature: Change and cancel a booking
  Hotel staff change guest details and cancel bookings. These actions need a signed-in administrator.

  Background:
    Given the administrator is signed in
    And a guest with valid contact details
    And a room has been booked

  Scenario: Administrator replaces the guest details
    When the administrator replaces the booking with new guest details
    Then the booking shows the new guest details

  Scenario: Administrator marks the deposit as paid
    When the administrator marks the deposit as paid
    Then the deposit is recorded as paid

  Scenario: Administrator cancels a booking
    When the administrator cancels the booking
    Then the cancellation is accepted

  @security
  Scenario: Booking cannot be cancelled without signing in
    When the booking is cancelled without signing in
    Then the request is refused with status 401
