package com.dimasarya.billslice.core.model

data class BillCalculationResult(
    val subtotal: Money,
    val serviceAmount: Money,
    val taxAmount: Money,
    val discountAmount: Money,
    val roundingAdjustment: Money,
    val total: Money,
    val payer: Participant,
    val participantSplits: List<ParticipantSplit>,
)
