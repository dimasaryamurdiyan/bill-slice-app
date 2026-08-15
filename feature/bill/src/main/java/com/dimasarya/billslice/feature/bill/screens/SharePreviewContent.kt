package com.dimasarya.billslice.feature.bill.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.HorizontalDivider
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
import com.dimasarya.billslice.core.designsystem.theme.SubtleBorder
import com.dimasarya.billslice.core.designsystem.theme.WarmCanvas
import com.dimasarya.billslice.core.designsystem.theme.WarmSurface
import com.dimasarya.billslice.feature.bill.BillFlowUiEvent
import com.dimasarya.billslice.feature.bill.BillFlowUiState
import com.dimasarya.billslice.feature.bill.R
import com.dimasarya.billslice.feature.bill.components.BillFlowTopBar
import com.dimasarya.billslice.feature.bill.components.PrimaryActionButton
import com.dimasarya.billslice.feature.bill.components.SecondaryActionButton
import kotlinx.coroutines.launch

@Composable
fun SharePreviewContent(
    state: BillFlowUiState,
    onEvent: (BillFlowUiEvent) -> Unit,
    onBack: () -> Unit,
    onFinishFlow: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val shareText = state.shareText ?: ""
    val result = state.calculationResult
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val copiedMessage = stringResource(R.string.share_text_copied)
    val chooserTitle = stringResource(R.string.share_via_chooser_title)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCanvas),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            BillFlowTopBar(
                title = stringResource(R.string.title_share_preview),
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
                            text = stringResource(R.string.share_preview_headline),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight(800),
                                fontSize = 27.sp,
                                color = DeepInk,
                                lineHeight = 32.sp,
                            ),
                            modifier = Modifier.semantics { heading() },
                        )
                        Text(
                            text = stringResource(R.string.share_preview_supporting),
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
                        // WhatsApp Text Preview Card
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = WarmSurface,
                            border = BorderStroke(1.dp, SubtleBorder),
                            shadowElevation = 2.dp,
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Text(
                                    text = stringResource(R.string.share_result_header),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight(800),
                                        fontSize = 18.sp,
                                        color = DeepEmerald,
                                    ),
                                )
                                Text(
                                    text = state.draft.merchantName?.ifBlank { "Warung Sore" } ?: "Warung Sore",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight(750),
                                        fontSize = 15.sp,
                                        color = DeepInk,
                                    ),
                                )

                                result.participantSplits.forEach { split ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            text = "${split.participant.name}:",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight(600),
                                                fontSize = 14.sp,
                                                color = DeepInk,
                                            ),
                                        )
                                        Text(
                                            text = split.finalTotal.format(),
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight(750),
                                                fontSize = 14.sp,
                                                color = DeepInk,
                                            ),
                                        )
                                    }
                                }

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
                                        text = "Total:",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight(800),
                                            fontSize = 15.sp,
                                            color = DeepInk,
                                        ),
                                    )
                                    Text(
                                        text = result.total.format(),
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight(850),
                                            fontSize = 15.sp,
                                            color = DeepEmerald,
                                        ),
                                    )
                                }
                            }
                        }
                    }

                    item {
                        // Share Channel card
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = ReceiptMint,
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.Chat,
                                    contentDescription = null,
                                    tint = DeepEmerald,
                                    modifier = Modifier.size(22.dp),
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = stringResource(R.string.share_channel_title),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight(750),
                                            fontSize = 14.sp,
                                            color = DeepEmerald,
                                        ),
                                    )
                                    Text(
                                        text = stringResource(R.string.share_channel_detail),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight(500),
                                            fontSize = 11.sp,
                                            color = DeepEmerald,
                                        ),
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

            // Bottom Actions (Primary Share Now + Secondary Copy Text)
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
                        text = stringResource(R.string.action_share_now),
                        icon = Icons.Rounded.Share,
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, chooserTitle)
                            context.startActivity(shareIntent)
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
