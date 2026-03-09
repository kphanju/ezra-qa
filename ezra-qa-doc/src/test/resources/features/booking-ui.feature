# Sample cases created by kphanju at 3/8/26
Feature: Ezra Scan Booking — Comprehensive Test Suite
  As a member of the Ezra health platform
  I want to book, reschedule, and cancel my scan appointment
  So that I can manage my health screening effectively

  Background:
    Given User A is logged in to the Ezra member portal
    And the member is on the home dashboard at myezra-staging.ezra.com


# ╔═══════════════════════════════════════════════════════════════════╗
# ║  CATEGORY 1 — HAPPY PATH: SCHEDULING, RESCHEDULING, CANCELLATION ║
# ╚═══════════════════════════════════════════════════════════════════╝

  @tc-hp-001 @p1 @critical @happy-path @scheduling @e2e
  Scenario: TC-HP-001 Member completes full booking flow end-to-end
    Given the member clicks "Book a scan" on the dashboard
    And the member is on the plan selection page at /book-scan/select-plan

    When the member clicks on the "MRI Scan" plan card
    Then the "MRI Scan" card should be highlighted with a yellow border
    And the Continue button should become enabled

    When the member clicks Continue
    Then the member should be on the schedule page at /book-scan/schedule-scan
    And the page heading should read "Schedule your scan"
    And the subheading should read "Select a location, date and time to book your scan"
    And "AMRIC" should be displayed with a "Recommended" badge
    And the Continue button should be disabled

    When the member clicks the "AMRIC" center card
    Then the "AMRIC" card should appear selected

    When the member selects "Mar 19, 2026" on the calendar
    Then the date "Mar 19" should be highlighted in yellow on the calendar
    And a list of available time slots should appear on the right
    And a note should state "The time(s) are displayed in Eastern Standard Time"

    When the member selects "6:00 PM" from the time slot list
    Then the "6:00 PM" pill should be highlighted in yellow
    And the Continue button should become enabled

    When the member clicks Continue
    Then the member should be on the payment page at /book-scan/reserve-appointment
    And the order summary panel should show:
      | Field    | Value                      |
      | Plan     | MRI Scan                   |
      | Center   | AMRIC                      |
      | Address  | New York, city, NY 10022   |
      | DateTime | Mar 19, 2026 • 6:00 PM EDT |
      | Total    | $999                       |

    When the member clicks "Use this card" for the Visa Credit card ending in "4242"
    And the member clicks the "Continue" button
    Then the booking should be confirmed
    And a confirmation page or success state should be displayed
    And the member should see a "Go to dashboard" or confirmation button
    And the appointment card on the dashboard should show:
      | Plan     | MRI Scan                   |
      | Center   | AMRIC                      |
      | DateTime | Mar 19, 2026 • 6:00 PM EDT |


  @tc-hp-002 @p1 @critical @happy-path @reschedule @e2e
  Scenario: TC-HP-002 Member successfully reschedules an existing appointment
    Given the member has a confirmed "MRI Scan" appointment at "AMRIC"
    And the appointment is for "Mar 20, 2026 at 8:00 PM EDT"
    And the member is on the home dashboard

    When the member navigates to their appointment
    And the member clicks the "Reschedule or Cancel" action
    Then a page should appear with two options:
      | Option                | Description                                          |
      | Reschedule appointment | If you want to change the date or time of your appointment this is the way to go. |
      | Cancel appointment    | Before clicking cancel please be aware this action cannot be undone.              |

    When the member clicks the "Reschedule" button
    Then a calendar should appear showing "March 2026"
    And available dates should be displayed in bold
    And past dates should be greyed out and not selectable

    When the member selects "Mar 25, 2026" on the reschedule calendar
    Then the page should transition to the time selection view
    And the header should show "Mar 25, 2026 at 00:00*"
    And an "Edit" button should be visible to return to the calendar
    And the following time slots should be available:
      | 9:00 AM  | 9:30 AM  | 10:00 AM | 10:30 AM |
      | 11:00 AM | 11:30 AM | 12:00 PM | 12:30 PM |
      | 1:00 PM  | 1:30 PM  | 3:00 PM  | 3:30 PM  |
      | 4:00 PM  | 4:30 PM  | 5:00 PM  | 5:30 PM  |
      | 6:00 PM  | 6:30 PM  | 7:00 PM  | 7:30 PM  |
      | 8:00 PM  | 8:30 PM  |          |          |
    And a note should read "* All times are provided in Eastern Standard Time"

    When the member selects "8:30 PM" from the time slots
    Then the "8:30 PM" pill should be highlighted in yellow
    And the "Submit" button should become enabled

    When the member clicks "Submit"
    Then a reschedule confirmation page should appear
    And the heading should read "Scan Rescheduled"
    And the confirmation should state "Your scan is confirmed for:"
    And the new date and time should display "Mar 25, 2026 • 6:30 PM EDT"
    And a reminder should display "Please bring your ID to your scan appointment for verification"
    And a "Go to dashboard" button should be visible

    When the member clicks "Go to dashboard"
    Then the dashboard appointment card should reflect the updated appointment:
      | Plan     | MRI Scan                   |
      | DateTime | Mar 25, 2026 • 6:30 PM EDT |


  @tc-hp-003 @p1 @critical @happy-path @cancellation @e2e
  Scenario: TC-HP-003 Member successfully cancels an appointment with a reason
    Given the member has a confirmed "MRI Scan" appointment at "AMRIC"
    And the appointment is for "Mar 20, 2026 at 8:00 PM EDT"

    When the member navigates to their appointment actions
    And the member clicks the "Cancel" button on the reschedule-or-cancel page
    Then a cancel confirmation page should appear
    And the heading should read "Cancel confirmation"
    And the page should warn "This action will also cancel all related appointments"
    And the warning should state "This action cannot be undone, and you will have to book a new appointment"
    And a list of cancellation reasons should be displayed
    And a follow-up question should be visible

    When the member selects "The screening is too expensive" as the reason
    Then the selected reason should be highlighted with a yellow border

    When the member enters "maybe $50" in the "How much would you be willing to pay?" field
    And the member clicks "Cancel Scan"
    Then the cancellation confirmation page should appear
    And the heading should read "Your appointment has been cancelled"
    And the cancelled appointment details should show:
      | DateTime | Mar 20, 2026 • 8:00 PM EDT |
      | Plan     | MRI Scan                   |
    And a "Back to Dashboard" button should be visible
    And a calendar icon with a red X should be displayed

    When the member clicks "Back to Dashboard"
    Then no upcoming appointment for "Mar 20, 2026" should appear on the dashboard
    And the "Appointments" tab should show an empty state or prompt to book a new scan


  @tc-hp-004 @p1 @happy-path @cancellation
  Scenario: TC-HP-004 Member dismisses cancel flow and appointment remains intact
    Given the member has a confirmed "MRI Scan" appointment at "AMRIC"
    And the member is on the reschedule-or-cancel page

    When the member clicks the "Cancel" button
    Then the cancel confirmation page should appear with a "Back" button

    When the member clicks "Back"
    Then the member should be returned to the previous page
    And the appointment should remain confirmed for "Mar 20, 2026 at 8:00 PM EDT"
    And no cancellation should have been processed


  @tc-hp-005 @p1 @happy-path @reschedule
  Scenario: TC-HP-005 Member uses Edit button to go back to calendar during reschedule
    Given the member is on the reschedule time selection page showing "Mar 25, 2026"

    When the member clicks the "Edit" button
    Then the member should be returned to the reschedule calendar view
    And "March 2026" should be displayed
    And no time should be locked in yet
    And the member should be able to select a different date
