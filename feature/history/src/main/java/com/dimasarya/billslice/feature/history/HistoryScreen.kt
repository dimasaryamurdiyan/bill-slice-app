package com.dimasarya.billslice.feature.history

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dimasarya.billslice.core.designsystem.theme.BillSliceTheme
import com.dimasarya.billslice.core.designsystem.theme.BillSliceThemeTokens
import com.dimasarya.billslice.core.designsystem.theme.DeepEmerald
import com.dimasarya.billslice.core.designsystem.theme.DeepInk
import com.dimasarya.billslice.core.designsystem.theme.MutedInk
import com.dimasarya.billslice.core.designsystem.theme.ReceiptMint
import com.dimasarya.billslice.core.designsystem.theme.SoftSurface
import com.dimasarya.billslice.core.designsystem.theme.SubtleBorder
import com.dimasarya.billslice.core.designsystem.theme.WarmCanvas
import com.dimasarya.billslice.core.designsystem.theme.WarmSurface
import com.dimasarya.billslice.core.model.CurrencyCode
import com.dimasarya.billslice.core.model.Money
import com.dimasarya.billslice.core.model.RecentBillSummary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    state: HistoryUiState,
    onOpenBill: (String) -> Unit,
    onLifetimePro: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCanvas),
        contentAlignment = Alignment.TopCenter,
    ) {
        when (state) {
            is HistoryUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = DeepEmerald)
                }
            }
            is HistoryUiState.Empty -> {
                EmptyHistoryContent()
            }
            is HistoryUiState.Error -> {
                ErrorHistoryContent(
                    message = state.message,
                    onRetry = onRetry,
                )
            }
            is HistoryUiState.Populated -> {
                PopulatedHistoryContent(
                    state = state,
                    onOpenBill = onOpenBill,
                    onLifetimePro = onLifetimePro,
                )
            }
        }
    }
}

@Composable
private fun PopulatedHistoryContent(
    state: HistoryUiState.Populated,
    onOpenBill: (String) -> Unit,
    onLifetimePro: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .widthIn(max = 840.dp)
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.history_title),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight(850),
                    fontSize = 32.sp,
                    color = DeepInk,
                ),
                modifier = Modifier.semantics { heading() },
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(state.bills, key = RecentBillSummary::id) { bill ->
            HistoryBillRow(
                bill = bill,
                onClick = { onOpenBill(bill.id) },
            )
        }

        if (state.hasOlderBills) {
            item {
                ProHistoryTeaserCard(onClick = onLifetimePro)
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun HistoryBillRow(
    bill: RecentBillSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dateString = formatDate(bill.createdAtEpochMillis)
    val accessibilityLabel = stringResource(
        R.string.history_item_description,
        bill.merchantName,
        dateString,
        bill.participantCount,
        bill.total.format(),
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                role = Role.Button,
                onClickLabel = stringResource(R.string.history_item_description, bill.merchantName, dateString, bill.participantCount, bill.total.format()),
                onClick = onClick,
            )
            .semantics { contentDescription = accessibilityLabel },
        shape = RoundedCornerShape(12.dp),
        color = WarmSurface,
        border = BorderStroke(1.dp, SubtleBorder),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = bill.merchantName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight(750),
                        fontSize = 15.sp,
                        color = DeepInk,
                    ),
                )
                Text(
                    text = "$dateString • ${stringResource(R.string.history_people_count, bill.participantCount)}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight(500),
                        fontSize = 12.sp,
                        color = MutedInk,
                    ),
                )
            }

            Text(
                text = bill.total.format(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight(800),
                    fontSize = 16.sp,
                    color = DeepEmerald,
                ),
            )
        }
    }
}

@Composable
private fun ProHistoryTeaserCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                role = Role.Button,
                onClick = onClick,
            ),
        shape = RoundedCornerShape(12.dp),
        color = ReceiptMint,
        border = BorderStroke(1.dp, SubtleBorder),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.Lock,
                contentDescription = null,
                tint = DeepEmerald,
                modifier = Modifier.size(24.dp),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = stringResource(R.string.history_pro_teaser_title),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight(800),
                        color = DeepEmerald,
                    ),
                )
                Text(
                    text = stringResource(R.string.history_pro_teaser_body),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight(500),
                        color = DeepInk,
                    ),
                )
            }
        }
    }
}

@Composable
private fun EmptyHistoryContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(BillSliceThemeTokens.spacing.extraLarge),
        verticalArrangement = Arrangement.spacedBy(
            BillSliceThemeTokens.spacing.large,
            Alignment.CenterVertically,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ReceiptLong,
            contentDescription = null,
            tint = DeepEmerald,
            modifier = Modifier.size(64.dp),
        )
        Text(
            text = stringResource(R.string.history_empty_title),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = stringResource(R.string.history_empty_body),
            style = MaterialTheme.typography.bodyLarge.copy(color = MutedInk),
        )
    }
}

@Composable
private fun ErrorHistoryContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(BillSliceThemeTokens.spacing.extraLarge),
        verticalArrangement = Arrangement.spacedBy(
            BillSliceThemeTokens.spacing.large,
            Alignment.CenterVertically,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.history_error_title),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge.copy(color = MutedInk),
        )
        Button(onClick = onRetry) {
            Icon(Icons.Rounded.Refresh, contentDescription = null)
            Text(stringResource(R.string.history_error_retry))
        }
    }
}

private fun formatDate(epochMillis: Long): String {
    if (epochMillis <= 0) return "Recent"
    val formatter = SimpleDateFormat("d MMM, HH:mm", Locale.getDefault())
    return formatter.format(Date(epochMillis))
}

@Preview(name = "Phone", showBackground = true, widthDp = 400, heightDp = 900)
@Preview(name = "Large font", showBackground = true, widthDp = 360, heightDp = 640, fontScale = 2f)
@Preview(name = "Tablet", showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
private fun HistoryScreenPreview() {
    val sampleBills = listOf(
        RecentBillSummary(
            id = "1",
            merchantName = "Warung Sore",
            createdAtEpochMillis = System.currentTimeMillis(),
            total = Money.idr(219_450),
            participantCount = 3,
            currency = CurrencyCode.IDR,
        ),
        RecentBillSummary(
            id = "2",
            merchantName = "Kopi Kenangan",
            createdAtEpochMillis = System.currentTimeMillis() - 86400000L,
            total = Money.idr(88_000),
            participantCount = 2,
            currency = CurrencyCode.IDR,
        ),
    )
    BillSliceTheme {
        HistoryScreen(
            state = HistoryUiState.Populated(
                bills = sampleBills,
                isPro = false,
                hasOlderBills = true,
            ),
            onOpenBill = {},
            onLifetimePro = {},
            onRetry = {},
        )
    }
}
