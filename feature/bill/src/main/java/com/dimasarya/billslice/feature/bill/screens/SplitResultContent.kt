package com.dimasarya.billslice.feature.bill.screens

import android.content.ClipData
import androidx.compose.ui.tooling.preview.Preview
import com.dimasarya.billslice.core.designsystem.theme.BillSliceTheme
import com.dimasarya.billslice.core.domain.CalculateBillSplitUseCase
import com.dimasarya.billslice.core.domain.GenerateShareTextUseCase
import com.dimasarya.billslice.core.testing.CanonicalBillFixtures
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.dimasarya.billslice.core.model.ParticipantSplit
import com.dimasarya.billslice.feature.bill.BillFlowStep
import com.dimasarya.billslice.feature.bill.BillFlowUiEvent
import com.dimasarya.billslice.feature.bill.BillFlowUiState
import com.dimasarya.billslice.feature.bill.R
import com.dimasarya.billslice.feature.bill.components.BillFlowTopBar
import com.dimasarya.billslice.feature.bill.components.PrimaryActionButton
import com.dimasarya.billslice.feature.bill.components.SecondaryActionButton
import kotlinx.coroutines.launch

@Composable
fun SplitResultContent(
    state: BillFlowUiState,
    onEvent: (BillFlowUiEvent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val result = state.calculationResult
    val shareText = state.shareText ?: ""
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val copiedMessage = stringResource(R.string.share_text_copied)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCanvas),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            BillFlowTopBar(
                title = stringResource(R.string.title_your_split),
                onBack = onBack,
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                if (result != null) {
                    item {
                        // Hero card with gradient
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, SubtleBorder),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0xFFDDF8EB),
                                                Color(0xFFF8FBF8),
                                            ),
                                        ),
                                    )
                                    .padding(vertical = 20.dp, horizontal = 16.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(7.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = null,
                                        tint = DeepEmerald,
                                        modifier = Modifier.size(34.dp),
                                    )
                                    Text(
                                        text = state.draft.merchantName?.ifBlank { "Warung Sore" } ?: "Warung Sore",
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight(800),
                                            fontSize = 24.sp,
                                            color = DeepInk,
                                        ),
                                    )
                                    Text(
                                        text = stringResource(
                                            R.string.result_date_people,
                                            state.draft.participants.size,
                                        ),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight(600),
                                            fontSize = 12.sp,
                                            color = MutedInk,
                                        ),
                                    )
                                    Text(
                                        text = stringResource(R.string.combined_total_eyebrow),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight(800),
                                            fontSize = 10.sp,
                                            color = DeepEmerald,
                                            letterSpacing = 1.sp,
                                        ),
                                    )
                                    Text(
                                        text = result.total.format(),
                                        style = MaterialTheme.typography.headlineLarge.copy(
                                            fontWeight = FontWeight(850),
                                            fontSize = 32.sp,
                                            color = DeepInk,
                                        ),
                                    )
                                }
                            }
                        }
                    }

                    // Per Person Results
                    items(result.participantSplits, key = { it.participant.id }) { split ->
                        PersonResultRow(split = split)
                    }

                    // Result Rounding Note
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = SoftSurface,
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(11.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Info,
                                    contentDescription = null,
                                    tint = MutedInk,
                                    modifier = Modifier.size(18.dp),
                                )
                                Text(
                                    text = stringResource(
                                        R.string.result_rounding_note,
                                        result.roundingAdjustment.format(),
                                        result.payer.name,
                                    ),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight(600),
                                        fontSize = 11.sp,
                                        color = MutedInk,
                                        lineHeight = 15.sp,
                                    ),
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Bottom Actions (Primary Share to WhatsApp + Secondary Copy Text)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = WarmSurface,
                shadowElevation = 8.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    PrimaryActionButton(
                        text = stringResource(R.string.action_share_to_whatsapp),
                        icon = Icons.Rounded.Share,
                        onClick = {
                            onEvent(BillFlowUiEvent.NavigateToStep(BillFlowStep.SharePreview))
                        },
                    )

                    SecondaryActionButton(
                        text = stringResource(R.string.action_copy_text),
                        icon = Icons.Rounded.ContentCopy,
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("BillSlice Result", shareText)
                            clipboard.setPrimaryClip(clip)
                            scope.launch {
                                snackbarHostState.showSnackbar(copiedMessage)
                            }
                        },
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 140.dp),
        )
    }
}

@Composable
private fun PersonResultRow(split: ParticipantSplit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = if (split.isPayer) ReceiptMint else WarmSurface,
        border = BorderStroke(1.dp, if (split.isPayer) TableEmerald else SubtleBorder),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(if (split.isPayer) TableEmerald else SoftSurface, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = split.participant.name.take(1).uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight(800),
                            fontSize = 12.sp,
                            color = if (split.isPayer) DeepInk else MutedInk,
                        ),
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                    Text(
                        text = split.participant.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight(750),
                            fontSize = 14.sp,
                            color = DeepInk,
                        ),
                    )
                    if (split.isPayer) {
                        Text(
                            text = stringResource(R.string.payer_indicator),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight(750),
                                fontSize = 10.sp,
                                color = DeepEmerald,
                            ),
                        )
                    }
                }
            }

            Text(
                text = split.finalTotal.format(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight(850),
                    fontSize = 17.sp,
                    color = if (split.isPayer) DeepEmerald else DeepInk,
                ),
            )
        }
    }
}

@Preview(name = "Phone", showBackground = true, widthDp = 400, heightDp = 900)
@Preview(name = "Large font", showBackground = true, widthDp = 360, heightDp = 640, fontScale = 2f)
@Preview(name = "Tablet", showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
private fun SplitResultContentPreview() {
    val draft = CanonicalBillFixtures.createCanonicalDraft()
    val calc = CalculateBillSplitUseCase()(draft)
    val shareText = GenerateShareTextUseCase()(calc, draft.merchantName)
    BillSliceTheme {
        SplitResultContent(
            state = com.dimasarya.billslice.feature.bill.BillFlowUiState(
                draft = draft,
                calculationResult = calc,
                shareText = shareText,
            ),
            onEvent = {},
            onBack = {},
        )
    }
}

