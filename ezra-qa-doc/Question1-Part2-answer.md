# Question 1
## Part 2 
### For the top 3 test cases from part 1, please provide a description explaining why they are indicated as your most important.

---

### 🥇 TC-01 · Full Booking, Reschedule & Cancellation Flow

The booking flow is not just a feature — it is the core business transaction. A member who cannot book, reschedule, or cancel their scan has no functional product to interact with.

---

### 🥈 TC-03 · Successful Payment via Visa / Credit Card

No revenue flows without a working payment step. A member can complete the entire booking flow correctly, but if payment fails or behaves inconsistently across card types, the appointment is never confirmed and the transaction is lost.

---

### 🥉 TC-07 · Appointment View Is Isolated Per User

Ezra operates in a healthcare environment where appointment data is sensitive. A missing authorization check is not just a bug; it is a potential HIPAA-relevant data exposure that affects every user on the platform simultaneously.

---
