# Sample cases created by kphanju at 3/8/26
Feature: Payment processing test cases
  As a product that requires payment processing
  We want to make sure that payment info are properly processed
  And the payment calculations are accurate

  @tc-pm-001 @p1 @critical @payment @visa @happy-path
  Scenario: TC-PM-001 Successful payment with saved Visa card confirms appointment
    Given the member is on the payment page
    And the order summary shows "MRI Scan" at "AMRIC" for "Mar 19, 2026 • 6:00 PM EDT"
    And the total displays "$999"
    And the Visa Credit card ending in "4242" is shown via Stripe Link

    When the member clicks "Use this card"
    And the member clicks "Continue"
    Then the payment should be processed successfully
    And a booking confirmation should be displayed
    And the appointment card on the dashboard should show:
      | Plan     | MRI Scan                   |
      | Center   | AMRIC                      |
      | DateTime | Mar 19, 2026 • 6:00 PM EDT |
    And the charged amount should match the order summary total of "$999"


  @tc-pm-002 @p1 @critical @payment @visa @negative
  Scenario: TC-PM-002 Declined Visa card shows error and does not confirm booking
    Given the member is on the payment page
    And the member is using a test card configured to decline

    When the member submits payment
    Then a payment declined error message should be displayed on the payment page
    And the member should remain on the payment page
    And no appointment confirmation page should appear
    And the dashboard should not show a new appointment for "Mar 19, 2026"
    And the order summary total should still display "$999"


  @tc-pm-003 @p1 @payment @visa
  Scenario: TC-PM-003 Total price on payment page matches the price shown on the plan selection page
    Given the member selected "MRI Scan" at "$999" on the plan selection page
    When the member arrives on the payment page
    Then the order summary total should display "$999"
    And the total should exactly match the price shown on the "MRI Scan" plan card
    And no additional fees should be added without disclosure


  # ─── BANK PAYMENT ────────────────────────────────────────────────

  @tc-pm-004 @p1 @critical @payment @bank @happy-path
  Scenario: TC-PM-004 Member successfully connects bank account via Stripe Link and pays
    Given the member is on the payment page
    When the member selects the "Bank" payment tab
    Then the green cashback banner should display "Get $5 back when you pay by bank. See terms"
    And the "$5 back" badge should be visible on the Bank tab

    When the member clicks the "Success" bank tile
    Then a Stripe Link modal should appear with the title "Log in to Success"
    And the modal should display "Fast and simple — Ezra uses Link to connect your accounts"
    And the modal should display "Data is encrypted — Your data is protected, and you can disconnect at any time"
    And a "disconnect" link should be visible
    And a legal note should read "By continuing, you agree to Link's Terms and Privacy Policy"
    And an "Agree and continue" button should be present

    When the member clicks "Agree and continue"
    Then a "Select account" modal should appear
    And the connected bank account "Success ••••6789" should be listed with a checkmark
    And an "Add bank account" option should be visible
    And a security note should read "Your login and financial details are never shared with Ezra. Learn more"
    And a "Connect account" button should be present

    When the member selects "Success ••••6789" account
    And the member clicks "Connect account"
    Then the bank account should be connected to the payment
    And the member should be returned to the payment page with the bank selected

    When the member clicks "Continue"
    Then the payment should be processed successfully
    And a booking confirmation should be displayed
    And the appointment card on the dashboard should show:
      | Plan     | MRI Scan                   |
      | Center   | AMRIC                      |
      | DateTime | Mar 19, 2026 • 6:00 PM EDT |
    And the $5 cashback should be noted in the confirmation


  @tc-pm-005 @p1 @payment @bank
  Scenario: TC-PM-005 Member can add a new bank account during payment
    Given the member is on the "Select account" Stripe Link modal
    When the member clicks "Add bank account"
    Then a flow to connect a new bank account should be initiated
    And the member should be able to search and authorize a new bank


  @tc-pm-006 @p1 @payment @bank @negative
  Scenario: TC-PM-006 Blocked bank tile returns payment blocked error
    Given the member is on the payment page with Bank tab selected
    When the member clicks the "Blocked" bank tile
    And the member completes the connection flow
    And the member clicks "Continue"
    Then a payment error should be displayed indicating the bank is blocked
    And the member should remain on the payment page
    And no appointment confirmation should be generated
    And the appointment should NOT appear on the dashboard


  @tc-pm-007 @p1 @payment @bank @negative
  Scenario: TC-PM-007 Disputed bank tile returns payment disputed error
    Given the member is on the payment page with Bank tab selected
    When the member clicks the "Disputed" bank tile
    And the member completes the connection flow
    And the member clicks "Continue"
    Then a payment disputed error message should be displayed
    And the member should remain on the payment page
    And no appointment should be confirmed or shown on the dashboard


  @tc-pm-008 @p2 @payment @bank @negative
  Scenario: TC-PM-008 Down bank tiles show appropriate unavailability messages
    Given the member is on the payment page with Bank tab selected
    When the member clicks the "Down (Scheduled)" bank tile
    Then a message should indicate the bank is undergoing scheduled maintenance
    And the member should be prompted to try a different payment method

    When the member clicks the "Down (Unscheduled)" bank tile
    Then a message should indicate an unexpected outage
    And the member should be prompted to try a different payment method

    When the member clicks the "Down (Error)" bank tile
    Then a generic connection error should be displayed
    And the member should be prompted to try a different payment method


  @tc-pm-009 @p1 @payment @bank
  Scenario: TC-PM-009 Bank tab displays all 8 test tiles in correct layout
    Given the member is on the payment page
    When the member selects the "Bank" payment tab
    Then the following 8 bank tiles should be displayed in a 4x2 grid:
      | Row 1              | Row 2              |
      | Success            | Bank (OAuth)       |
      | Blocked            | Down (Scheduled)   |
      | Disputed           | Down (Unscheduled) |
      | Bank (Non-OAuth)   | Down (Error)       |
    And each tile should show its icon and label
    And a search bar with placeholder "Search for your bank" should be visible


  @tc-pm-010 @p1 @payment @bank
  Scenario: TC-PM-010 Stripe Link security disclosures are shown before bank connection
    Given the member has clicked on the "Success" bank tile
    When the "Log in to Success" modal appears
    Then the modal should display the Stripe Link logo with "Test" badge
    And the modal should confirm "Ezra uses Link to connect your accounts"
    And the modal should state data is encrypted and user can disconnect at any time
    And the "Terms and Privacy Policy" link should be visible and clickable
    And the member must click "Agree and continue" before proceeding


  # ─── PAYMENT METHOD TABS ─────────────────────────────────────────

  @tc-pm-011 @p1 @payment @tabs
  Scenario: TC-PM-011 All three payment method tabs are displayed and switchable
    Given the member is on the payment page
    When the payment section loads
    Then three payment method options should be visible:
      | Method | Radio Button |
      | Card   | ○ Card       |
      | Affirm | ○ Affirm     |
      | Bank   | ● Bank       |
    When the member selects "Card" radio button
    Then the Card payment interface should load
    When the member selects "Affirm" radio button
    Then the Affirm payment interface should load
    When the member selects "Bank" radio button
    Then the Bank tile grid should display with the $5 cashback banner


  # ─── PROMO CODE ──────────────────────────────────────────────────

  @tc-pm-012 @p1 @critical @payment @promo @negative
  Scenario: TC-PM-012 Invalid promo code shows inline error and total remains unchanged
    Given the member is on the payment page
    And the order total shows "$999"

    When the member enters "asdf" in the promo code field
    And the member clicks "Apply Code"
    Then the text "Invalid Promo Code" should appear in red inline next to the input
    And the order total should still display "$999"
    And the original price should not be crossed out
    And the member should remain on the payment page
    And the Continue button should still be available


  @tc-pm-013 @p1 @critical @payment @promo @happy-path
  Scenario: TC-PM-013 Valid promo code reduces the total with strikethrough on original price
    Given the member is on the payment page
    And the order total shows "$999"

    When the member enters a valid promo code in the promo code field
    And the member clicks "Apply Code"
    Then no error message should appear
    And the original price "$999" should be displayed with a strikethrough
    And a discounted total lower than "$999" should be displayed below
    And the discount amount should be mathematically correct
    And the Continue button should be available with the discounted amount as the final charge


  @tc-pm-014 @p1 @payment @promo
  Scenario: TC-PM-014 Promo loading spinner appears during code validation
    Given the member is on the payment page
    When the member enters any code in the promo field
    And clicks "Apply Code"
    Then a loading spinner should appear inside the promo field while validating
    And the spinner should disappear once the validation result is returned
    And either a success or error state should be displayed


  @tc-pm-015 @p2 @payment @promo
  Scenario: TC-PM-015 Applied promo persists when switching payment method tabs
    Given the member has applied a valid promo code reducing the total to a discounted amount
    When the member switches from "Card" tab to "Bank" tab
    Then the discounted total should still be displayed in the order summary
    And the strikethrough on "$999" should still be visible
    And the promo code should remain applied

    When the member switches back to "Card" tab
    Then the discounted total should still be active
    And no re-entry of the promo code should be required


  @tc-pm-016 @p1 @payment @promo
  Scenario: TC-PM-016 Promo-applied total is the amount actually charged on successful payment
    Given the member has applied a valid promo code
    And the discounted total displays "$X" (less than $999)
    When the member completes payment with the Visa card
    Then the payment should be processed for the discounted amount "$X"
    And NOT for the original amount "$999"
    And the appointment confirmation should reflect the discounted total charged
    And the dashboard appointment card should show the correct appointment details


  # ─── ORDER SUMMARY VALIDATION ────────────────────────────────────

  @tc-pm-017 @p1 @critical @payment @validation
  Scenario: TC-PM-017 Order summary on payment page exactly matches all prior booking selections
    Given the member selected "MRI Scan" at "$999"
    And the member selected "AMRIC" at "New York, city, NY 10022"
    And the member selected "Mar 19, 2026" at "6:00 PM EDT"
    When the member arrives on the payment page
    Then the order summary panel must display ALL of the following:
      | Field    | Expected Value             |
      | Plan     | MRI Scan                   |
      | Center   | AMRIC                      |
      | Address  | New York, city, NY 10022   |
      | DateTime | Mar 19, 2026 • 6:00 PM EDT |
      | Total    | $999                       |
    And no field in the summary should be blank, missing, or show placeholder text


  @tc-pm-018 @p1 @payment @validation
  Scenario: TC-PM-018 Payment page total matches the price displayed on the plan card
    Given the "MRI Scan" plan card showed "Available at $999" on the plan selection page
    When the member reaches the payment page
    Then the order summary total should display exactly "$999"
    And the total should not include any undisclosed fees or taxes
    And the total should not differ from the price shown during plan selection


  @tc-pm-019 @p1 @payment @confirmation
  Scenario: TC-PM-019 Appointment date and time shown on dashboard card after successful payment
    Given the member has completed a successful payment for "MRI Scan" at "AMRIC"
    And the appointment was booked for "Mar 19, 2026 at 6:00 PM EDT"
    When the member navigates to the dashboard
    And the member views the "Appointments" tab
    Then an appointment card should be displayed showing:
      | Plan     | MRI Scan                   |
      | Center   | AMRIC                      |
      | DateTime | Mar 19, 2026 • 6:00 PM EDT |
    And a "Reschedule or Cancel" option should be visible on the card


  @tc-pm-020 @p1 @payment @confirmation @reschedule
  Scenario: TC-PM-020 Dashboard appointment card updates to new date/time after reschedule
    Given the member's original appointment was "Mar 20, 2026 at 8:00 PM EDT"
    And the member has successfully rescheduled to "Mar 25, 2026 at 6:30 PM EDT"
    When the member views the dashboard appointment card
    Then the card should display the updated date and time "Mar 25, 2026 • 6:30 PM EDT"
    And the old date "Mar 20, 2026" should no longer appear on the card


  @tc-pm-021 @p1 @payment @confirmation @cancellation
  Scenario: TC-PM-021 Cancelled appointment is removed from dashboard and shows correct details on cancellation screen
    Given the member cancels the appointment for "Mar 20, 2026 at 8:00 PM EDT" (MRI Scan)
    When the cancellation is confirmed
    Then the cancellation screen should show:
      | Heading  | Your appointment has been cancelled |
      | DateTime | Mar 20, 2026 • 8:00 PM EDT          |
      | Plan     | MRI Scan                            |
    And a "Back to Dashboard" button should be present
    When the member clicks "Back to Dashboard"
    Then no appointment for "Mar 20, 2026" should appear in the Appointments tab
    And the dashboard should display an empty state or prompt to book a new scan
