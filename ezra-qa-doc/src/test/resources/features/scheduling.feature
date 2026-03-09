# Sample cases created by kphanju at 3/8/26
Feature: Available Appointment date and time test cases
  As a product which presents appointment date and time for various centers
  We want to make sure application provides available dtae and times only based on plan selection

  @tc-sc-001 @p1 @critical @scheduling @availability
  Scenario: TC-SC-001 Only centers compatible with selected plan are selectable
    Given the member has selected the "MRI Scan" plan
    When the member is on the schedule page
    Then "AMRIC" should display as "Recommended" and be selectable
    And "Park Ave" should display badge "Available instead · MRI Scan with Spine"
    And "Upper East Side" should display badge "Available instead · MRI Scan with Spine"
    And "Kraken Radiology" should display badge "Available instead · MRI Scan with Spine"
    And the "Available instead" centers should not be selectable for "MRI Scan"
    And clicking an "Available instead" center should not enable the Continue button


  @tc-sc-002 @p1 @critical @scheduling @availability
  Scenario: TC-SC-002 Already reserved time slot is not displayed as selectable
    Given the member has selected the "MRI Scan" plan
    And the member has selected "AMRIC" as the service center
    And another member has already reserved "AMRIC" for "Mar 19, 2026 at 12:00 PM"
    When the member selects "Mar 19, 2026" on the calendar
    Then the "12:00 PM" time slot should NOT appear in the available time list
    And only time slots with no existing reservations should be displayed
    And the member should not be able to select or book the already-reserved time


  @tc-sc-003 @p1 @critical @scheduling @availability
  Scenario: TC-SC-003 Fully booked date shows no available time slots
    Given the member has selected "AMRIC" as the service center
    And all time slots for "Mar 20, 2026" at "AMRIC" are already reserved
    When the member selects "Mar 20, 2026" on the calendar
    Then no time slot pills should be displayed on the right panel
    And an empty state message should indicate no availability on this date
    And the Continue button should remain disabled


  @tc-sc-004 @p1 @scheduling @availability
  Scenario: TC-SC-004 Partially booked date shows only remaining available time slots
    Given the member has selected "AMRIC" as the service center
    And the following time slots are already reserved for "Mar 19, 2026":
      | 9:00 AM | 12:00 PM | 3:00 PM |
    When the member selects "Mar 19, 2026" on the calendar
    Then the time slot list should NOT include "9:00 AM", "12:00 PM", or "3:00 PM"
    And all other available time slots should be displayed and selectable


  @tc-sc-005 @p1 @scheduling @calendar
  Scenario: TC-SC-005 Past dates are greyed out and cannot be selected on booking calendar
    Given the member is on the schedule page at /book-scan/schedule-scan
    When the member views the "March 2026" calendar
    Then all dates before today (Mar 8, 2026) should be displayed in grey
    And dates 2 through 7 should appear greyed out
    When the member attempts to click on "Mar 7, 2026"
    Then no time slots should load
    And the Continue button should remain disabled


  @tc-sc-006 @p1 @scheduling @calendar
  Scenario: TC-SC-006 Available future dates are bold and selectable on booking calendar
    Given the member is on the schedule page
    When the member views the "March 2026" calendar
    Then dates from Mar 18 onwards should be displayed in bold
    When the member clicks on "Mar 19, 2026"
    Then the date should become highlighted in yellow
    And available time slots should appear on the right panel
    And all times should note "displayed in Eastern Standard Time"


  @tc-sc-007 @p1 @scheduling @availability
  Scenario: TC-SC-007 Time slots displayed match the selected center's actual availability
    Given the member has selected "AMRIC" and "Mar 19, 2026"
    When the time slot panel loads
    Then the displayed times should exactly match the slots available in the system for "AMRIC" on "Mar 19, 2026"
    And no time slot should appear that has already been reserved by another member
    And no time slot should appear that is outside the center's operating hours


  @tc-sc-008 @p1 @scheduling @state-filter
  Scenario: TC-SC-008 State dropdown filters available centers by location
    Given the member is on the schedule page
    And the State dropdown shows "All Available"
    When the member selects "New York" from the State dropdown
    Then only centers located in New York state should be visible
    And centers in other states should be hidden from the list
    And "AMRIC" with address "New York, city, NY 10022" should remain visible


  @tc-sc-009 @p1 @scheduling @geolocation
  Scenario: TC-SC-009 Find closest centers reorders list by proximity to member's location
    Given the member is on the schedule page
    When the member clicks "Find closest centers to me"
    Then the browser should request geolocation permission
    When the member grants location permission
    Then the center list should be reordered by proximity to the member's location
    And the nearest center should appear first in the list


  @tc-sc-010 @p1 @scheduling @reschedule @availability
  Scenario: TC-SC-010 Reschedule calendar shows past dates as greyed out and not selectable
    Given the member is on the reschedule calendar page
    When the member views "March 2026"
    Then all dates before today (Mar 8) should be greyed out
    And dates Mar 2 through Mar 7 should not be clickable
    When the member attempts to click "Mar 7"
    Then no time selection screen should appear
    And the Submit button should remain disabled


  @tc-sc-011 @p1 @scheduling @reschedule @availability
  Scenario: TC-SC-011 Current appointment time is not shown as an option during reschedule
    Given the member's current appointment is "Mar 20, 2026 at 8:00 PM EDT"
    When the member navigates to the reschedule flow
    And the member selects "Mar 20, 2026" on the reschedule calendar
    Then the "8:00 PM" time slot should NOT be selectable
  Or an indicator should show "This is your current appointment time"
    And the member should be required to select a different time


  @tc-sc-012 @p2 @scheduling @reschedule @availability
  Scenario: TC-SC-012 Reschedule time page shows all available slots for selected date
    Given the member is on the reschedule flow
    When the member selects "Mar 25, 2026" on the reschedule calendar
    Then the time selection page should appear with header "Mar 25, 2026 at 00:00*"
    And available time slots should be displayed in a grid layout
    And already-reserved time slots should not appear
    And a footnote should display "* All times are provided in Eastern Standard Time"
    And the Submit button should be disabled until a time is selected
