@booking @create @contract
Feature: Booking confirmation contract
  The confirmation keeps a stable structure and arrives quickly.

  Background:
    Given a guest with valid contact details

  Scenario: Confirmation follows the agreed structure
    When the guest books a room
    Then the confirmation follows the agreed structure

  Scenario: Booking is confirmed within an acceptable time
    When the guest books a room
    Then the response arrives within 5000 ms
