# BUG-01: the health endpoint documented in the spec answers 500 on the live site.
@health @known-bug
Feature: Service health
  Operations can check whether the booking service is running.

  Scenario: Booking service reports that it is up
    When the service health is checked
    Then the service reports that it is up
