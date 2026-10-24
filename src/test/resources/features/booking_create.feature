@booking @create
Feature: Create a booking
  A guest can book a room for a stay and receives a confirmation with a booking id.

  Background:
    Given a guest with valid contact details

  @smoke @positive
  Scenario: Guest books a free room
    When the guest books a room
    Then the booking is confirmed
    And the confirmation matches the guest and stay details

  @positive
  Scenario Outline: Guests can stay for different lengths of time
    When the guest books room 1 for <nights> nights
    Then the booking is confirmed
    And the confirmation matches the guest and stay details

    Examples:
      | nights |
      | 1      |
      | 3      |
      | 14     |

  @positive
  Scenario: Several guests book rooms one after another
    When the following guests book a room
      | firstname | lastname | depositpaid |
      | Alice     | Walker   | true        |
      | Brian     | Stone    | false       |
      | Chitra    | Menon    | true        |
    Then every booking is confirmed
