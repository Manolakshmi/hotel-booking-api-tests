@booking @create @negative
Feature: Booking conflicts
  A room cannot be booked twice for the same dates.

  Background:
    Given a guest with valid contact details

  Scenario: Second guest cannot book the same room for the same dates
    Given a room has been booked
    When another guest books the same room for the same dates
    Then the request is refused with status 409
