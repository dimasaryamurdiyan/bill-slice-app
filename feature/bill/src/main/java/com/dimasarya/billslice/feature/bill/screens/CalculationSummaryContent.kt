package com.dimasarya.billslice.feature.bill.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.tooling.preview.Preview
import com.dimasarya.billslice.core.designsystem.theme.BillSliceTheme
import com.dimasarya.billslice.core.domain.CalculateBillSplitUseCase
import com.dimasarya.billslice.feature.bill.SampleBillData
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dimasarya.billslice.core.designsystem.theme.DeepEmerald
import com.dimasarya.billslice.core.designsystem.theme.DeepInk
import com.dimasarya.billslice.core.designsystem.theme.MutedInk
import com.dimasarya.billslice.core.designsystem.theme.ReceiptMint
import com.dimasarya.billslice.core.designsystem.theme.SoftSurface
import com.dimasarya.billslice.core.designsystem.theme.SubtleBorder
import com.dimasarya.billslice.core.designsystem.theme.TableEmerald
import com.dimasarya.billslice.core.designsystem.theme.WarmCanvas
import com.dimasarya.billslice.core.designsystem.theme.WarmSurface
import com.dimasarya.billslice.core.model.ReceiptTotalStatus
import com.dimasarya.billslice.feature.bill.BillFlowStep
import com.dimasarya.billslice.feature.bill.BillFlowUiEvent
import com.dimasarya.billslice.feature.bill.BillFlowUiState
import com.dimasarya.billslice.feature.bill.R
import com.dimasarya.billslice.feature.bill.components.BannerType
import com.dimasarya.billslice.feature.bill.components.BillFlowTopBar
import com.dimasarya.billslice.feature.bill.components.PrimaryActionButton
import com.dimasarya.billslice.feature.bill.components.StatusBanner

@Composable
fun CalculationSummaryContent(
    state: BillFlowUiState,
    onEvent: (BillFlowUiEvent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val result = state.calculationResult
    var payerMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCanvas),
    ) {
        BillFlowTopBar(
            title = stringResource(R.string.title_calculation),
            onBack = onBack,
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(R.string.calculation_headline),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight(800),
                            fontSize = 28.sp,
                            color = DeepInk,
                            lineHeight = 32.sp,
                        ),
                        modifier = Modifier.semantics { heading() },
                    )
                    Text(
                        text = stringResource(R.string.calculation_supporting),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight(500),
                            fontSize = 13.sp,
                            color = MutedInk,
                        ),
                    )
                }
            }

            if (result != null) {
                item {
                    // Calculation Breakdown card matching Pencil
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = WarmSurface,
                        border = BorderStroke(1.dp, SubtleBorder),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(13.dp),
                        ) {
                            BreakdownRow(
                                label = stringResource(R.string.summary_subtotal),
                                value = result.subtotal.format(),
                            )
                            BreakdownRow(
                                label = stringResource(R.string.summary_service, state.draft.serviceRate.format()),
                                value = result.serviceAmount.format(),
                            )
                            BreakdownRow(
                                label = stringResource(R.string.summary_tax, state.draft.taxRate.format()),
                                value = result.taxAmount.format(),
                            )
                            BreakdownRow(
                                label = stringResource(R.string.summary_discount),
                                value = if (result.discountAmount.isZero) "− Rp0" else "− ${result.discountAmount.format()}",
                            )
                            BreakdownRow(
                                label = stringResource(R.string.summary_rounding),
                                value = result.roundingAdjustment.format(),
                            )

                            HorizontalDivider(
                                color = SubtleBorder,
                                modifier = Modifier.padding(vertical = 2.dp),
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = stringResource(R.string.combined_total_label),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight(800),
                                        fontSize = 15.sp,
                                        color = DeepInk,
                                    ),
                                )
                                Text(
                                    text = result.total.format(),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight(850),
                                        fontSize = 17.sp,
                                        color = DeepEmerald,
                                    ),
                                )
                            }
                        }
                    }
                }

                // Payer Selection Section
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(R.string.payer_label),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight(700),
                                fontSize = 13.sp,
                                color = DeepInk,
                            ),
                        )

                        Box {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .clickable { payerMenuExpanded = true },
                                shape = RoundedCornerShape(8.dp),
                                color = ReceiptMint,
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(9.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Payments,
                                            contentDescription = null,
                                            tint = DeepEmerald,
                                            modifier = Modifier.size(22.dp),
                                        )
                                        Text(
                                            text = result.payer.name,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight(750),
                                                fontSize = 14.sp,
                                                color = DeepEmerald,
                                            ),
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Rounded.KeyboardArrowDown,
                                        contentDescription = "Select payer",
                                        tint = DeepEmerald,
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = payerMenuExpanded,
                                onDismissRequest = { payerMenuExpanded = false },
                            ) {
                                state.draft.participants.forEach { participant ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = participant.name,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = if (participant.id == result.payer.id) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (participant.id == result.payer.id) DeepEmerald else DeepInk,
                                                ),
                                            )
                                        },
                                        onClick = {
                                            onEvent(BillFlowUiEvent.SelectPayer(participant.id))
                                            onEvent(BillFlowUiEvent.CalculateSplit)
                                            payerMenuExpanded = false
                                        },
                                    )
                                }
                            }
                        }
                    }
                }

                // Rounding Note
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = SoftSurface,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Info,
                                contentDescription = null,
                                tint = MutedInk,
                                modifier = Modifier.size(19.dp),
                            )
                            Text(
                                text = stringResource(R.string.rounding_note_text),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight(600),
                                    fontSize = 12.sp,
                                    color = MutedInk,
                                    lineHeight = 16.sp,
                                ),
                            )
                        }
                    }
                }

                // Receipt verification banner if present
                val validation = state.receiptValidationResult
                if (validation != null) {
                    item {
                        when (validation.status) {
                            ReceiptTotalStatus.LOOKS_GOOD -> {
                                StatusBanner(
                                    message = stringResource(R.string.status_looks_good),
                                    type = BannerType.Success,
                                )
                            }
                            ReceiptTotalStatus.NEEDS_REVIEW -> {
                                StatusBanner(
                                    message = stringResource(R.string.status_needs_review, validation.difference.format()),
                                    type = BannerType.Warning,
                                )
                            }
                            ReceiptTotalStatus.MISSING_TOTAL -> {
                                StatusBanner(
                                    message = stringResource(R.string.status_missing_total),
                                    type = BannerType.Info,
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Bottom CTA
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = WarmSurface,
            shadowElevation = 8.dp,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
            ) {
                PrimaryActionButton(
                    text = stringResource(R.string.action_calculate_split),
                    icon = Icons.Rounded.Calculate,
                    onClick = {
                        onEvent(BillFlowUiEvent.NavigateToStep(BillFlowStep.SplitResult))
                    },
                )
            }
        }
    }
}

@Composable
private fun BreakdownRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight(500),
                fontSize = 13.sp,
                color = MutedInk,
            ),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight(700),
                fontSize = 13.sp,
                color = DeepInk,
            ),
        )
    }
}

@Preview(name = "Phone", showBackground = true, widthDp = 400, heightDp = 900)
@Preview(name = "Large font", showBackground = true, widthDp = 360, heightDp = 640, fontScale = 2f)
@Preview(name = "Tablet", showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
private fun CalculationSummaryContentPreview() {
    val draft = SampleBillData.createSampleDraft()
    val calc = CalculateBillSplitUseCase()(draft)
    BillSliceTheme {
        CalculationSummaryContent(
            state = com.dimasarya.billslice.feature.bill.BillFlowUiState(
                draft = draft,
                calculationResult = calc,
            ),
            onEvent = {},
            onBack = {},
        )
    }
}

