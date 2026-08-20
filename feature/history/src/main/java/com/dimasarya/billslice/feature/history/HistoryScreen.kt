package com.dimasarya.billslice.feature.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    Scaffold(
        modifier = modifier.background(WarmCanvas),
        containerColor = WarmCanvas,
        topBar = { HistoryTopBar() },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
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
}

@Composable
private fun HistoryTopBar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(HistoryMetrics.TopBarHeight),
        contentAlignment = Alignment.TopCenter,
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = HistoryMetrics.MaxContentWidth)
                .fillMaxWidth()
                .height(HistoryMetrics.TopBarHeight)
                .padding(horizontal = BillSliceThemeTokens.spacing.screenHorizontal),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ReceiptLong,
                contentDescription = null,
                tint = DeepEmerald,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(24.dp),
            )
            Text(
                text = stringResource(R.string.history_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight(750),
                    color = DeepInk,
                ),
                modifier = Modifier.align(Alignment.Center),
            )
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = null,
                tint = MutedInk,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(24.dp),
            )
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
            .widthIn(max = HistoryMetrics.MaxContentWidth)
            .fillMaxWidth()
            .fillMaxHeight(),
        contentPadding = PaddingValues(
            start = BillSliceThemeTokens.spacing.screenHorizontal,
            top = HistoryMetrics.ScreenGap,
            end = BillSliceThemeTokens.spacing.screenHorizontal,
            bottom = HistoryMetrics.BottomContentPadding,
        ),
        verticalArrangement = Arrangement.spacedBy(HistoryMetrics.ListGap),
    ) {
        item {
            HistoryHeader(
                isPro = state.isPro,
                modifier = Modifier.padding(bottom = HistoryMetrics.HeaderGapAdjustment),
            )
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
    }
}

@Composable
private fun HistoryHeader(
    isPro: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(HistoryMetrics.ScreenGap),
    ) {
        Text(
            text = stringResource(R.string.history_section_heading),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 33.sp,
                color = DeepInk,
            ),
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = stringResource(
                if (isPro) {
                    R.string.history_section_subtitle_pro
                } else {
                    R.string.history_section_subtitle
                },
            ),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MutedInk,
            ),
        )
    }
}

@Composable
private fun HistoryBillRow(
    bill: RecentBillSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dateString = formatDate(bill.createdAtEpochMillis)
    val peopleLabel = pluralStringResource(
        R.plurals.history_people_count,
        bill.participantCount,
        bill.participantCount,
    )
    val accessibilityLabel = stringResource(
        R.string.history_item_description,
        bill.merchantName,
        dateString,
        peopleLabel,
        bill.total.format(),
    )
    val reopenActionLabel = stringResource(R.string.history_reopen_action, bill.merchantName)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                role = Role.Button,
                onClickLabel = reopenActionLabel,
                onClick = onClick,
            )
            .semantics { contentDescription = accessibilityLabel },
        shape = MaterialTheme.shapes.extraSmall,
        color = WarmSurface,
        border = BorderStroke(1.dp, SubtleBorder),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HistoryMetrics.RowPadding),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = bill.merchantName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = DeepInk,
                        ),
                    )
                    Text(
                        text = "$dateString • $peopleLabel",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                            color = MutedInk,
                        ),
                    )
                }

                Text(
                    text = bill.total.format(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = DeepInk,
                    ),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = null,
                    tint = DeepEmerald,
                    modifier = Modifier.size(15.dp),
                )
                Text(
                    text = stringResource(R.string.history_reopen_edit),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = DeepEmerald,
                    ),
                )
            }
        }
    }
}

@Composable
private fun EmptyHistoryCard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = SoftSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HistoryMetrics.EmptyStatePadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(HistoryMetrics.EmptyStateGap),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ReceiptLong,
                contentDescription = null,
                tint = DeepEmerald,
                modifier = Modifier.size(30.dp),
            )
            Text(
                text = stringResource(R.string.history_empty_title),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = DeepInk,
                ),
            )
            Text(
                text = stringResource(R.string.history_empty_body),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    color = MutedInk,
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
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
    LazyColumn(
        modifier = modifier
            .widthIn(max = HistoryMetrics.MaxContentWidth)
            .fillMaxWidth()
            .fillMaxHeight(),
        contentPadding = PaddingValues(
            start = BillSliceThemeTokens.spacing.screenHorizontal,
            top = HistoryMetrics.ScreenGap,
            end = BillSliceThemeTokens.spacing.screenHorizontal,
            bottom = HistoryMetrics.BottomContentPadding,
        ),
        verticalArrangement = Arrangement.spacedBy(HistoryMetrics.ListGap),
    ) {
        item {
            HistoryHeader(modifier = Modifier.padding(bottom = HistoryMetrics.HeaderGapAdjustment))
        }

        item {
            EmptyHistoryCard()
        }
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
    val formatter = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
    return formatter.format(Date(epochMillis))
}

private object HistoryMetrics {
    val TopBarHeight = 44.dp
    val MaxContentWidth = 840.dp
    val ScreenGap = 14.dp
    val ListGap = 9.dp
    val HeaderGapAdjustment = ScreenGap - ListGap
    val RowPadding = 13.dp
    val EmptyStatePadding = 16.dp
    val EmptyStateGap = 7.dp
    val BottomContentPadding = 80.dp
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
            merchantName = "Kopi Tepi",
            createdAtEpochMillis = System.currentTimeMillis() - 86400000L * 4,
            total = Money.idr(128_000),
            participantCount = 2,
            currency = CurrencyCode.IDR,
        ),
        RecentBillSummary(
            id = "3",
            merchantName = "Bakmi 88",
            createdAtEpochMillis = System.currentTimeMillis() - 86400000L * 11,
            total = Money.idr(176_500),
            participantCount = 4,
            currency = CurrencyCode.IDR,
        ),
        RecentBillSummary(
            id = "4",
            merchantName = "Sate Senayan",
            createdAtEpochMillis = System.currentTimeMillis() - 86400000L * 17,
            total = Money.idr(245_300),
            participantCount = 3,
            currency = CurrencyCode.IDR,
        ),
        RecentBillSummary(
            id = "5",
            merchantName = "Ayam Bakar Madu",
            createdAtEpochMillis = System.currentTimeMillis() - 86400000L * 26,
            total = Money.idr(164_000),
            participantCount = 2,
            currency = CurrencyCode.IDR,
        ),
    )
    BillSliceTheme {
        HistoryScreen(
            state = HistoryUiState.Populated(
                bills = sampleBills,
                isPro = false,
                hasOlderBills = false,
            ),
            onOpenBill = {},
            onLifetimePro = {},
            onRetry = {},
        )
    }
}

@Preview(name = "Empty", showBackground = true, widthDp = 400, heightDp = 900)
@Composable
private fun HistoryScreenEmptyPreview() {
    BillSliceTheme {
        HistoryScreen(
            state = HistoryUiState.Empty,
            onOpenBill = {},
            onLifetimePro = {},
            onRetry = {},
        )
    }
}
