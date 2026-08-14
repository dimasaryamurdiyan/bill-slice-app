package com.dimasarya.billslice.feature.home

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dimasarya.billslice.core.designsystem.theme.BillSliceTheme
import com.dimasarya.billslice.core.designsystem.theme.BillSliceThemeTokens
import com.dimasarya.billslice.core.designsystem.theme.DeepEmerald
import com.dimasarya.billslice.core.designsystem.theme.DeepInk
import com.dimasarya.billslice.core.designsystem.theme.ReceiptMint
import com.dimasarya.billslice.core.designsystem.theme.TableCharcoal
import com.dimasarya.billslice.core.designsystem.theme.TableEmerald
import com.dimasarya.billslice.core.designsystem.theme.WarmSurface

@Composable
fun HomeScreen(
    state: HomeUiState,
    modifier: Modifier = Modifier,
    onScanReceipt: () -> Unit,
    onEnterManually: () -> Unit,
    onHistory: () -> Unit,
    onLifetimePro: () -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = BillSliceThemeTokens.spacing.screenHorizontal,
            vertical = BillSliceThemeTokens.spacing.large,
        ),
        verticalArrangement = Arrangement.spacedBy(BillSliceThemeTokens.spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 840.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.home_brand),
                    modifier = Modifier.semantics { heading() },
                    style = MaterialTheme.typography.headlineLarge,
                )
                Spacer(Modifier.weight(1f))
                QuotaChip(
                    quota = state.quota,
                    isOfflineReady = state.isOfflineReady,
                    onClick = onLifetimePro,
                )
            }
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 840.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(ReceiptMint, MaterialTheme.colorScheme.background),
                        ),
                    )
                    .padding(horizontal = 18.dp, vertical = 22.dp),
                verticalArrangement = Arrangement.spacedBy(BillSliceThemeTokens.spacing.small),
            ) {
                Text(
                    text = stringResource(R.string.home_headline),
                    modifier = Modifier.semantics { heading() },
                    style = MaterialTheme.typography.displayLarge,
                )
                Text(
                    text = stringResource(R.string.home_supporting),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
        item {
            HomeActionCard(
                title = stringResource(R.string.scan_receipt),
                supporting = stringResource(R.string.scan_receipt_supporting),
                icon = Icons.Rounded.CameraAlt,
                containerColor = TableEmerald,
                contentColor = DeepInk,
                minHeight = 104.dp,
                onClickLabel = stringResource(R.string.scan_receipt),
                onClick = onScanReceipt,
            )
        }
        item {
            HomeActionCard(
                title = stringResource(R.string.enter_manually),
                supporting = stringResource(R.string.enter_manually_supporting),
                icon = Icons.Rounded.Edit,
                containerColor = TableCharcoal,
                contentColor = WarmSurface,
                minHeight = 82.dp,
                onClickLabel = stringResource(R.string.enter_manually),
                onClick = onEnterManually,
            )
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 840.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.recent_bills),
                    modifier = Modifier.semantics { heading() },
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.see_all),
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .clickable(
                            role = Role.Button,
                            onClickLabel = stringResource(R.string.see_all),
                            onClick = onHistory,
                        )
                        .padding(horizontal = BillSliceThemeTokens.spacing.small),
                    color = DeepEmerald,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
        if (state.recentBills.isEmpty()) {
            item { EmptyRecentBills() }
        } else {
            items(state.recentBills, key = RecentBillUi::id) { bill ->
                RecentBillRow(bill)
            }
        }
    }
}

@Composable
private fun QuotaChip(
    quota: SmartScanQuotaUiState,
    isOfflineReady: Boolean,
    onClick: () -> Unit,
) {
    val label = when (quota) {
        is SmartScanQuotaUiState.Available -> stringResource(
            R.string.smart_scans_left,
            quota.remaining,
        )
        SmartScanQuotaUiState.Unavailable -> stringResource(R.string.offline_ready)
    }
    Surface(
        modifier = Modifier
            .heightIn(min = 48.dp)
            .clickable(
                role = Role.Button,
                onClickLabel = stringResource(R.string.view_lifetime_pro),
                onClick = onClick,
            ),
        color = ReceiptMint,
        contentColor = DeepEmerald,
        shape = RoundedCornerShape(999.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(BillSliceThemeTokens.spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = if (isOfflineReady) Icons.Rounded.Star else Icons.Rounded.ReceiptLong,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Text(label, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun HomeActionCard(
    title: String,
    supporting: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    minHeight: androidx.compose.ui.unit.Dp,
    onClickLabel: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 840.dp)
            .heightIn(min = minHeight)
            .clickable(
                role = Role.Button,
                onClickLabel = onClickLabel,
                onClick = onClick,
            ),
        color = containerColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(BillSliceThemeTokens.spacing.extraSmall),
            ) {
                Text(title, style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = supporting,
                    color = contentColor.copy(alpha = 0.72f),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
private fun EmptyRecentBills() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 840.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier.padding(BillSliceThemeTokens.spacing.large),
            horizontalArrangement = Arrangement.spacedBy(BillSliceThemeTokens.spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Rounded.ReceiptLong,
                contentDescription = null,
                tint = DeepEmerald,
            )
            Column(verticalArrangement = Arrangement.spacedBy(BillSliceThemeTokens.spacing.extraSmall)) {
                Text(
                    text = stringResource(R.string.recent_bills_empty_title),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = stringResource(R.string.recent_bills_empty_body),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun RecentBillRow(bill: RecentBillUi) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 840.dp),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(bill.merchantName, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = stringResource(
                        R.string.recent_bill_meta,
                        bill.dateLabel,
                        bill.peopleCount,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            Text(bill.totalLabel, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 900)
@Composable
private fun HomeScreenPreview() {
    BillSliceTheme {
        HomeScreen(
            state = HomeUiState(
                quota = SmartScanQuotaUiState.Available(5),
                recentBills = listOf(
                    RecentBillUi("1", "Warung Sore", "13 Aug 2026", 3, "Rp219.450"),
                    RecentBillUi("2", "Kopi Tepi", "9 Aug 2026", 2, "Rp128.000"),
                ),
            ),
            onScanReceipt = {},
            onEnterManually = {},
            onHistory = {},
            onLifetimePro = {},
        )
    }
}
