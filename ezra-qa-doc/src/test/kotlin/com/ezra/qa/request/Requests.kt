package com.ezra.qa.model.request

data class CreateEncounterHubRequest(
    val memberId: String,
    val packageId: String,
    val centerId: String,
    val offlineBookings: List<OfflineBooking>,
    val onlineBookings: List<OnlineBooking> = emptyList(),
    val selectedAddons: List<String> = emptyList()
)

data class OfflineBooking(
    val appointmentTypeCode: String,
    val slots: List<Slot>,
    val onlineCenterDown: Boolean = false
)

data class Slot(
    val start: String,
    val end: String,
    val date: String
)

data class OnlineBooking(
    val startTime: String,
    val endTime: String,
    val lockIds: List<String>,
    val appointmentTypeCode: String,
    val partnerExternalData: Map<String, Any>? = null
)

data class BookingStageRequest(
    val memberId: String,
    val encounterId: String,
    val stage: String,
    val details: String? = null
)

data class CreatePendingPaymentRequest(
    val creditAppliedCents: Int = 0,
    val paymentPlan: String = "oneTime",
    val promotionCode: String = "",
    val isDeferred: Boolean = false,
    val paymentMethodId: String? = null
)
