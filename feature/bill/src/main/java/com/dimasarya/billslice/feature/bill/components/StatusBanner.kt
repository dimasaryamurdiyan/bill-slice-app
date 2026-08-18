package com.dimasarya.billslice.feature.bill.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dimasarya.billslice.core.designsystem.theme.DeepEmerald
import com.dimasarya.billslice.core.designsystem.theme.MutedInk
import com.dimasarya.billslice.core.designsystem.theme.ReceiptMint
import com.dimasarya.billslice.core.designsystem.theme.SoftSurface
import com.dimasarya.billslice.core.designsystem.theme.WarningInk
import com.dimasarya.billslice.core.designsystem.theme.WarningSurface

enum class BannerType {
    Success,
    Warning,
    Info,
}

@Composable
fun StatusBanner(
    message: String,
    type: BannerType,
    modifier: Modifier = Modifier,
) {
    val (bgColor, contentColor, icon) = when (type) {
        BannerType.Success -> Triple(ReceiptMint, DeepEmerald, Icons.Rounded.CheckCircle)
        BannerType.Warning -> Triple(WarningSurface, WarningInk, Icons.Rounded.Warning)
        BannerType.Info -> Triple(SoftSurface, MutedInk, Icons.Rounded.Info)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall.copy(
                color = contentColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                lineHeight = 16.sp,
            ),
            modifier = Modifier.weight(1f),
        )
    }
}
