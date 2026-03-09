package com.ezra.qa.model.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class AuthResponse(
    val access_token: String = "",
    val token_type: String = "",
    val expires_in: Int = 0,
    val refresh_token: String = ""
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class EncounterResponse(
    val id: String = "",
    val memberId: String = "",
    val packageId: String = "",
    val packageName: String = "",
    val encounterStatus: String = "",
    val encounterAppointments: List<AppointmentResponse> = emptyList(),
    val requiredAppointmentUnits: List<String> = emptyList(),
    val created: String = ""
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AppointmentResponse(
    val id: String = "",
    val memberId: String = "",
    val appointmentStatus: String = "",
    val appointmentDisplayName: String = "",
    val encounterId: String = "",
    val centerName: String = "",
    val start: String = "",
    val end: String = ""
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class BookingStageResponse(
    val memberId: String = "",
    val encounterId: String = "",
    val stage: String = "",
    val visitedOn: String = ""
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PendingPaymentResponse(
    val encounterId: String = "",
    val status: String = "",
    val amountCents: Int = 0,
    val paymentPlan: String = "",
    val error: String? = null,
    val errorCode: String? = null
)
