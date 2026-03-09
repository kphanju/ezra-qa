# Question2
## Part1
### Being privacy focused is integral to our culture and business model. Please devise an integration test case that prevents members from accessing other’s medical data.
Hint: Begin Medical Questionnaire.
We can follow patterns like following
## TC-08  Saved Card Info Is Isolated Per User (Data Privacy)

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

## Part2
### Please devise HTTP requests from Part 1 to implement your test case. Submitting written HTTP requisitions is fine, you do not need to submit a postman project.

#### SCAN_SELECTION_PAGE
```
curl -X POST "https://stage-api.ezra.com/platform/api/members/bookingstage" \
  -H "Authorization: Bearer ....... \
  -H "Content-Type: application/json" \
  -H "Accept: application/json, text/plain, */*" \
  -d '{
    "memberId": "56d24ae9-4fd9-4c71-849a-74fa4d00fed7",
    "encounterId": "fde765bd-904a-45eb-b6e3-8f237015e3ab",
    "stage": "SCAN_SELECTION_PAGE",
    "visitedOn": "2026-03-09T05:15:35.955Z"
  }'
  
```

#### PACKAGE_SELECTED
```
curl -X POST "https://stage-api.ezra.com/platform/api/members/bookingstage" \
  -H "Authorization: Bearer ....." \
  -H "Content-Type: application/json" \
  -H "Accept: application/json, text/plain, */*" \
  --compressed \
  -d '{
    "memberId": "56d24ae9-4fd9-4c71-849a-74fa4d00fed7",
    "encounterId": "fde765bd-904a-45eb-b6e3-8f237015e3ab",
    "stage": "PACKAGE_SELECTED",
    "details": "FB30",
    "visitedOn": "2026-03-09T05:20:33.951Z"
  }'
```

#### LOCATION_PAGE
```
curl -X POST "https://stage-api.ezra.com/platform/api/members/bookingstage" \
  -H "Authorization: Bearer ....." \
  -H "Content-Type: application/json" \
  -H "Accept: application/json, text/plain, */*" \
  --compressed \
  -d '{
    "memberId": "56d24ae9-4fd9-4c71-849a-74fa4d00fed7",
    "encounterId": "fde765bd-904a-45eb-b6e3-8f237015e3ab",
    "stage": "LOCATION_PAGE",
    "visitedOn": "2026-03-09T05:20:34.443Z"
  }'
```

#### CENTER_SELECTED
```
curl -X POST "https://stage-api.ezra.com/platform/api/members/bookingstage" \
  -H "Authorization: Bearer ....." \
  -H "Content-Type: application/json" \
  -H "Accept: application/json, text/plain, */*" \
  --compressed \
  -d '{
    "memberId": "56d24ae9-4fd9-4c71-849a-74fa4d00fed7",
    "encounterId": "fde765bd-904a-45eb-b6e3-8f237015e3ab",
    "stage": "CENTER_SELECTED",
    "details": "AMRIC",
    "visitedOn": "2026-03-09T05:24:16.002Z"
  }'
  ```

#### PAYMENT_PAGE
```
{
    "memberId": "56d24ae9-4fd9-4c71-849a-74fa4d00fed7",
    "encounterId": "fde765bd-904a-45eb-b6e3-8f237015e3ab",
    "stage": "PAYMENT_PAGE",
    "visitedOn": "2026-03-09T05:24:17.315Z"
}
```

#### create-pending
```
curl -X POST "https://stage-api.ezra.com/packages/api/payments/fde765bd-904a-45eb-b6e3-8f237015e3ab/create-pending" \
  -H "Authorization: Bearer ....." \
  -H "Content-Type: application/json" \
  -H "Accept: application/json, text/plain, */*" \
  --compressed \
  -d '{
    "creditAppliedCents": 0,
    "paymentPlan": "oneTime",
    "promotionCode": "",
    "isDeferred": false
  }'
```

## Part3
### At Ezra, we have over 100 endpoints that transfer sensitive data. What is your thought process around managing the security quality of these endpoints? What are the tradeoffs and potential risks of your solution?

#### Extensive unit tests
We can request extensive unit tests by developers.
These days AI can help out with unit test creation.
Make unit test part of the CI. If any test fails, developer should fix the test if needed or fix the bug caught by the failure.
No code should be merged without all unit tests are passed. This is can be done via github configuration.

#### Automated API test using cucumber. 
Using Cucumber + a CI pipeline (Jenkins), we can run cucumber api automation tests daily. 
QA can monitor the test results daily and flag the failures. If possible, resolve automation test if test has a bug.
Cucumber test wont be able to capture bugs in Production env configuration.

#### Annual penetration testing 
We can hire third party expertise who can help with extensive security testing.
Based on the results, we can create tickets and developers can fix those issues and add unit tests.
Problem is cost to the company and 12 month gap window. 
