package com.dimasarya.billslice.core.model

data class ParticipantSplit(
    val participant: Participant,
    val itemSubtotal: Money,
    val serviceShare: Money,
    val taxShare: Money,
    val discountShare: Money,
    val finalTotal: Money,
    val roundingAdjustment: Money,
    val isPayer: Boolean,
    val owesPayer: Money,
)
