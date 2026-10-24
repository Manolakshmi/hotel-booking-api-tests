# End-to-end flow. Not verified yet (@wip). It avoids GET by id, which is broken (BUG-05),
# and proves the cancellation by booking the same room and dates again.
@booking @e2e @wip
Feature: Booking lifecycle
  From booking a room to cancelling it, the room becomes available again.

  Scenario: A cancelled booking frees the room for the same dates
    Given a guest with valid contact details
    And the administrator is signed in
    And a room has been booked
    When another guest books the same room for the same dates
    Then the request is refused with status 409
    When the administrator cancels the booking
    Then the cancellation is accepted
    And the room can be booked again for the same dates
