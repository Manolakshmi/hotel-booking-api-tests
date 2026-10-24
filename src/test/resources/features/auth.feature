@auth
Feature: Administrator authentication
  Hotel staff sign in to obtain a session token before they manage bookings.

  @smoke @positive
  Scenario: Administrator signs in with valid credentials
    When the administrator signs in with username "admin" and password "password"
    Then the sign-in is accepted
    And a session token is issued

  @negative
  Scenario Outline: Sign-in is refused for invalid credentials
    When the administrator signs in with username "<username>" and password "<password>"
    Then the request is refused with status <status>

    Examples:
      | username | password | status |
      | admin    | wrong    | 401    |
      | nobody   | password | 401    |
      | admin    |          | 401    |

  # BUG-02: the live API answers 500 instead of a client error.
  @negative @known-bug
  Scenario: Sign-in without any credentials is rejected as a client error
    When the administrator sends a sign-in request without credentials
    Then the request is rejected as a client error
