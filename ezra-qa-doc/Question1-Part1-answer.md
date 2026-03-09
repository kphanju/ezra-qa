# 🩺 Ezra Health — Booking Flow Test Cases
### Senior QA Take-Home Assignment

## Question 1
### Part1
The booking flow is integral to Ezra's business operation. Please go through the first
three steps of the booking process including payment and devise 15 test cases
throughout the entire process you think are the most important. When submitting the
assignment, please return the test cases from the most important to the least important.

---

## Legend

| Symbol | Meaning |
|--------|---------|
| 🔴 | Critical — blocks core business function |
| 🟠 | High — significant user-facing impact |
| 🟡 | Medium — important edge case or secondary flow |

---

## TC-01 🔴 Full Booking, Reschedule & Cancellation Flow

> End-to-end validation of the three most important appointment lifecycle actions.

### Scheduling
```
Given  a user selects a plan
And    the user selects "Service-Center-A"
And    the user selects available appointment date and time "Day1, Time1"
And    the user provides a successful payment
Then   appointment confirmation is provided with "Service-Center-A", "Day1, Time1"
And    appointment card is shown on Home → Appointments with "Service-Center-A" and "Day1, Time1"
```

### Rescheduling
```
When   user clicks "Reschedule"
And    user selects "Service-Center-B"
And    user selects schedule date/time "Day2, Time2"
Then   reschedule confirmation is displayed for "Service-Center-B" at "Day2, Time2"
```

### Cancellation
```
When   user clicks "Cancel"
Then   cancellation reason is requested
When   user provides a cancellation reason and confirms
Then   cancellation confirmation is displayed with:
         - Cancelled plan name
         - Cancelled appointment date and time
```

---

## TC-02 🔴 Only Available Date-Times Are Selectable

> Prevents double-booking and ensures slot integrity.

```
Given  a service center has appointments available at Time1, Time2, Time3 on DayA
And    the service center does NOT have availability at TimeA, TimeB on DayA
Then   the user should be able to select Time1, Time2, or Time3
And    TimeA and TimeB should NOT be selectable or displayed to the user
```

---

## TC-03 🔴 Successful Payment via Visa / Credit Card

> Core revenue path — must always work.

```
Given  a user has selected MRI Scan plan, service center, and appointment date/time
When   the user is on the payment screen
Then   the user should be able to complete payment using a Visa card
```

> ℹ️ **Note:** This test case should also be executed using:
> `Mastercard` · `American Express` · `Discover` · `any other supported card type`

---

## TC-04 🔴 Payment Failure — No Appointment Created

> A failed payment must never create or confirm an appointment.

```
Given  a user has selected MRI Scan plan, service center, and appointment date/time
When   the user provides a test credit card configured to fail
Then   the system should display a payment failed message
And    the appointment should NOT be created
And    no appointment card should appear on the dashboard
```

---

## TC-05 🔴 Promo Code Validation and Price Deduction

> Validates both the negative (invalid code) and positive (valid code) promo paths.

```
Given  MRI Scan plan price is $999
And    promo code "Code25" provides $25 off
And    a user has selected a plan, service center, and appointment date/time

# Invalid code
When   user enters random text in the promo code field
Then   an "Invalid Promo Code" error message should be displayed
And    the total cost should still display "$999"

# Valid code
When   user applies "Code25" in the promo code field
Then   a promo code successfully applied message should be displayed
And    the total cost should update to "$974"
```

---

## TC-06 🔴 Payment via Bank Account (ACH)

> Validates the Stripe Link bank connection and terms acceptance flow.

```
Given  a user has selected MRI Scan plan, service center, and appointment date/time
When   the user is on the payment screen and selects the "Bank" option
Then   a modal should appear prompting the user to log in to their bank account
And    the user must agree to terms and conditions before proceeding
When   the user connects their bank account and completes payment
Then   the appointment should be confirmed
```

---

## TC-07 🔴 Appointment View Is Isolated Per User (Access Control)

> Verifies that users cannot see each other's appointments.

```
Given  UserA has created an appointment: MRI Scan @ Service-Center-A at DateTime-A
And    UserB has created an appointment: MRI Scan with Spine @ Service-Center-2 at DateTime-2

When   UserA logs in
Then   UserA should see:   MRI Scan @ Service-Center-A at DateTime-A
And    UserA should NOT see: MRI Scan with Spine @ Service-Center-2 at DateTime-2

When   UserB logs in
Then   UserB should see:   MRI Scan with Spine @ Service-Center-2 at DateTime-2
And    UserB should NOT see: MRI Scan @ Service-Center-A at DateTime-A
```

---

## TC-08 🔴 Saved Card Info Is Isolated Per User (Data Privacy)

> Prevents one member from seeing or using another member's saved payment details.

```
Given  UserA has saved CardA to their account
And    UserB has saved CardB to their account

When   UserA logs in and reaches the payment screen
Then   CardA should be presented as a payment option
And    CardB should NOT be visible to UserA

When   UserB logs in and reaches the payment screen
Then   CardB should be presented as a payment option
And    CardA should NOT be visible to UserB
```

---

## TC-09 🟠 Nearby Service Centers Are Surfaced First

> The user's state should influence which centers appear at the top of the list.

```
Given  MA state has: Service-Center-A, Service-Center-B, Service-Center-C (for MRI Scan)
And    CA state has: Service-Center-1, Service-Center-2
And    the logged-in member resides in MA

When   the user selects the "MRI Scan" plan
Then   Service-Center-A, Service-Center-B, and Service-Center-C should appear
       at the top of the service center list
And    CA centers should appear below or not be featured
```

---

## TC-10 🟠 Service Center Incompatibility Warning

> Informs the user when their selected center does not support their chosen plan.

```
Given  Service-Center-A offers "MRI Scan" but NOT "MRI Scan with Spine"
When   user selects "MRI Scan" plan
Then   Service-Center-A card should display the badge:
       "Available instead · MRI Scan with Spine"

When   user clicks on Service-Center-A
Then   a warning message should appear:
       "The imaging center you have selected does not have the MRI Scan available
        at this time. By pressing Continue, we will be changing your package
        selection to the MRI Scan with Spine."

When   user continues through appointment date selection to payment
Then   the system should display "MRI Scan with Spine" — NOT "MRI Scan"
And    the total should display "$1,699"
```

---

## TC-11 🟠 Multi-Appointment Plan Requires Two Date Selections

> Validates that plans requiring multiple visits properly collect and display both appointments.

```
Given  "MRI Scan with Skeletal and Neurological Assessment" requires 2 appointment dates
When   user selects this plan and an appropriate service center
Then   the system should prompt for Appointment 1 date/time
And    the system should prompt for Appointment 2 date/time

When   user successfully pays and booking is confirmed
Then   the confirmation should display both appointment times
And    the Appointments view should show 2 appointment cards for this plan
And    Appointment 1 should show both "Reschedule" and "Cancel" options
And    Appointment 2 should show "Reschedule" only
```

---

## TC-12 🟠 Payment Using Affirm

> Validates the Affirm installment payment method end-to-end.

```
Given  user has selected plan, service center, and appointment date/time
When   user selects the "Affirm" payment option
Then   the user should be directed to the Affirm payment process
When   the user completes the Affirm flow
Then   the system should create and confirm the appointment
```

---

## TC-13 🟠 Appointment Cannot Be Created for a Past Date

> Calendar date enforcement — past dates must never be bookable.

```
Given  a user has selected a plan and service center
When   the appointment calendar is displayed
Then   all dates before today should be greyed out and non-selectable
And    the user should not be able to select or submit a past date
```

---

## TC-14 🟡 All Appointments Are Displayed on the Dashboard

> Confirms the dashboard reflects a member's complete appointment history.

```
Given  UserA has created an appointment for "MRI Scan" at DateTime-1
And    UserA has also created an appointment for "MRI Scan with Spine" at DateTime-2
When   UserA views their home page
Then   two appointment cards should be displayed:
       - Card 1: MRI Scan at DateTime-1
       - Card 2: MRI Scan with Spine at DateTime-2
```

---

## TC-15 🟡 Back Button Retains Prior Selections

> State is preserved when navigating backwards through the booking flow.

```
Given  user has selected "MRI Scan" plan
And    user has selected "Date-Time-1" on the schedule page
When   user is on "Reserve your appointment" and clicks "Back"
Then   user should be brought back to "Schedule your scan"
And    the "MRI Scan" plan selection should still be active
And    the "Date-Time-1" selection should still be retained
```

---

## TC-16 🟡 Cancel Returns User to Home Page

> Basic navigation safety net — cancel should never leave the user stranded.

```
Given  a user clicks the "Cancel" button during the booking flow
Then   the user should be redirected to the home / dashboard page
And    no appointment or partial booking should be created
```

---

*Total: 16 test cases · Ordered by business criticality*
*Assignment submitted by: Krischan Phanju*
