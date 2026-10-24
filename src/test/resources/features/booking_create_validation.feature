@booking @create @negative
Feature: Booking validation
  The service rejects bookings with invalid guest or stay details and accepts values on the limits.

  Background:
    Given a guest with valid contact details

  Scenario Outline: Guest details outside the allowed limits are rejected
    When the guest books with <field> set to "<value>"
    Then the request is refused with status 400
    And the error message mentions "<message>"

    Examples: First name must have 3 to 18 characters
      | field     | value               | message                       |
      | firstname | Al                  | size must be between 3 and 18 |
      | firstname | Abcdefghijklmnopqrs | size must be between 3 and 18 |

    # The spec says 3 to 18, but the live API allows 3 to 30 characters (BUG-09).
    Examples: Last name must have 3 to 30 characters
      | field    | value                           | message                       |
      | lastname | Li                              | size must be between 3 and 30 |
      | lastname | Abcdefghijklmnopqrstuvwxyzabcde | size must be between 3 and 30 |

    Examples: Phone must have 11 to 21 characters
      | field | value                  | message                        |
      | phone | 12345                  | size must be between 11 and 21 |
      | phone | 1234567890123456789012 | size must be between 11 and 21 |

    Examples: Email must be well formed
      | field | value        | message                             |
      | email | not-an-email | must be a well-formed email address |

  @positive
  Scenario Outline: Values exactly on the limits are accepted
    When the guest books with <field> set to "<value>"
    Then the booking is confirmed

    Examples:
      | field     | value                 |
      | firstname | Abc                   |
      | firstname | Abcdefghijklmnopqr    |
      | lastname  | Abc                   |
      | lastname  | Abcdefghijklmnopqrstuvwxyzabcd |
      | phone     | 01234567890           |
      | phone     | 012345678901234567890 |

  # BUG-07: the spec lists 400 for invalid dates, the live API answers 409.
  @known-bug
  Scenario: Check-out before check-in is rejected
    When the guest books a room with check-out before check-in
    Then the request is refused with status 400

  Scenario: Request without the required guest details is rejected
    When a booking request without the required guest details is sent
    Then the request is refused with status 400

  # BUG-08: the live API answers 500 with an empty list instead of a client error.
  @known-bug
  Scenario: Malformed JSON is rejected as a client error
    When a booking request with malformed JSON is sent
    Then the request is rejected as a client error

  # Not verified yet: other candidates report that past dates are accepted.
  @wip
  Scenario: Check-in date in the past is rejected
    When the guest books a room with a check-in date in the past
    Then the request is refused with status 400
