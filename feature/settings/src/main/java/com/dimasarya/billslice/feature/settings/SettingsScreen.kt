package com.dimasarya.billslice.feature.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.CurrencyExchange
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.DocumentScanner
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dimasarya.billslice.core.designsystem.theme.BillSliceTheme
import com.dimasarya.billslice.core.designsystem.theme.BillSliceThemeTokens
import com.dimasarya.billslice.core.designsystem.theme.DeepEmerald
import com.dimasarya.billslice.core.designsystem.theme.ReceiptMint
import com.dimasarya.billslice.core.designsystem.theme.TableCharcoal
import com.dimasarya.billslice.core.designsystem.theme.WarmSurface

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    modifier: Modifier = Modifier,
    onCurrency: (() -> Unit)? = null,
    onQuota: (() -> Unit)? = null,
    onPrivacy: (() -> Unit)? = null,
    onLifetimePro: () -> Unit,
    onAppInfo: (() -> Unit)? = null,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = BillSliceThemeTokens.spacing.screenHorizontal,
            vertical = 0.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(BillSliceThemeTokens.spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.settings_title),
                    modifier = Modifier.semantics { heading() },
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 840.dp),
                color = ReceiptMint,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(14.dp),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(BillSliceThemeTokens.spacing.extraSmall),
                ) {
                    Text(
                        text = stringResource(R.string.settings_brand),
                        modifier = Modifier.semantics { heading() },
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Text(
                        text = stringResource(R.string.settings_brand_supporting),
                        color = DeepEmerald,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
        item {
            SettingRow(
                icon = Icons.Rounded.CurrencyExchange,
                label = stringResource(R.string.default_currency),
                value = state.defaultCurrency,
                onClick = onCurrency,
            )
        }
        item {
            val quotaValue = when (val quota = state.quota) {
                is QuotaSettingUiState.Available -> stringResource(
                    R.string.quota_available,
                    quota.remaining,
                    quota.resetLabel,
                )
                QuotaSettingUiState.Unavailable -> stringResource(R.string.quota_unavailable)
            }
            SettingRow(
                icon = Icons.Rounded.DocumentScanner,
                iconColor = DeepEmerald,
                iconSize = 21.dp,
                label = stringResource(R.string.smart_scan_quota),
                value = quotaValue,
                onClick = onQuota,
            )
        }
        item {
            SettingRow(
                icon = Icons.Rounded.Shield,
                label = stringResource(R.string.privacy),
                value = stringResource(R.string.privacy_value),
                onClick = onPrivacy,
            )
        }
        item {
            val proLabel: String
            val proValue: String
            val proContainer: Color
            val proContent: Color
            when (state.pro) {
                ProSettingUiState.Available -> {
                    proLabel = stringResource(R.string.upgrade_and_restore)
                    proValue = stringResource(R.string.lifetime_pro_price)
                    proContainer = TableCharcoal
                    proContent = WarmSurface
                }
                ProSettingUiState.Active -> {
                    proLabel = stringResource(R.string.lifetime_pro_active)
                    proValue = stringResource(R.string.lifetime_pro_active_value)
                    proContainer = ReceiptMint
                    proContent = DeepEmerald
                }
                ProSettingUiState.Unavailable -> {
                    proLabel = stringResource(R.string.upgrade_and_restore)
                    proValue = stringResource(R.string.purchase_state_unavailable)
                    proContainer = TableCharcoal
                    proContent = WarmSurface
                }
            }
            SettingRow(
                icon = Icons.Rounded.WorkspacePremium,
                label = proLabel,
                value = proValue,
                containerColor = proContainer,
                contentColor = proContent,
                border = null,
                onClick = onLifetimePro,
            )
        }
        item {
            SettingRow(
                icon = Icons.Rounded.Info,
                label = stringResource(R.string.app_info),
                value = stringResource(
                    R.string.version_build,
                    state.buildInfo.versionName,
                    state.buildInfo.buildType,
                ),
                onClick = onAppInfo,
            )
        }
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 840.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = MaterialTheme.shapes.small,
            ) {
                Row(
                    modifier = Modifier.padding(BillSliceThemeTokens.spacing.medium),
                    horizontalArrangement = Arrangement.spacedBy(BillSliceThemeTokens.spacing.small),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Rounded.CloudOff, contentDescription = null)
                    Text(
                        text = stringResource(R.string.local_only_note),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    label: String,
    value: String,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    iconColor: Color = contentColor,
    iconSize: androidx.compose.ui.unit.Dp = 24.dp,
    border: BorderStroke? = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    onClick: (() -> Unit)?,
) {
    val interactionModifier = if (onClick == null) {
        Modifier
    } else {
        Modifier.clickable(
            role = Role.Button,
            onClickLabel = stringResource(R.string.open_setting, label),
            onClick = onClick,
        )
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 840.dp)
            .heightIn(min = 76.dp)
            .then(interactionModifier)
            .semantics(mergeDescendants = true) {},
        color = containerColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.small,
        border = border,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(iconSize),
                tint = iconColor,
            )
            Spacer(Modifier.size(BillSliceThemeTokens.spacing.medium))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(BillSliceThemeTokens.spacing.extraSmall),
            ) {
                Text(label, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = value,
                    color = contentColor,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            if (onClick != null) {
                Icon(Icons.Rounded.ChevronRight, contentDescription = null)
            }
        }
    }
}

@Preview(name = "Phone", showBackground = true, widthDp = 400, heightDp = 900)
@Preview(name = "Large font", showBackground = true, widthDp = 360, heightDp = 640, fontScale = 2f)
@Preview(name = "Tablet", showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
private fun SettingsScreenPreview() {
    BillSliceTheme {
        SettingsScreen(
            state = SettingsUiState(
                quota = QuotaSettingUiState.Available(5, "1 Sep"),
                pro = ProSettingUiState.Available,
                buildInfo = BuildInfoUi("0.1", "Closed test"),
            ),
            onLifetimePro = {},
        )
    }
}
