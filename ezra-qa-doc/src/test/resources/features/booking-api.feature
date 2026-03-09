# Created by kphanju at 3/8/26
Feature: API tests for Booking

  Background:
    Given User A is authenticated with valid credentials

  Scenario: Successful booking with valid payment method
    When User A tracks booking stage "SCAN_SELECTION_PAGE"
    And  User A tracks booking stage "PACKAGE_SELECTED"
    And  User A creates an encounter for the default MRI Scan package
    Then the encounter is created successfully
    And  the encounter status is "INCOMPLETE"
    And  the encounter contains at least 1 appointment
