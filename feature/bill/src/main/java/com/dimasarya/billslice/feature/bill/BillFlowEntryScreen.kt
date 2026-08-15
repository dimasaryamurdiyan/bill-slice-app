package com.dimasarya.billslice.feature.bill

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.dimasarya.billslice.core.designsystem.theme.BillSliceThemeTokens

enum class BillFlowEntryMode(
    @StringRes val title: Int,
    @StringRes val body: Int,
) {
    Scan(R.string.scan_entry_title, R.string.scan_entry_body),
    Manual(R.string.manual_entry_title, R.string.manual_entry_body),
}

@Composable
fun BillFlowEntryScreen(
    mode: BillFlowEntryMode,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
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
            text = stringResource(mode.title),
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.headlineLarge,
        )
        Text(
            text = stringResource(mode.body),
            style = MaterialTheme.typography.bodyLarge,
        )
        Button(onClick = onBack) {
            Icon(Icons.Rounded.ArrowBack, contentDescription = null)
            Text(stringResource(R.string.back_to_home))
        }
    }
}
