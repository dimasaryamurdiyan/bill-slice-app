package com.dimasarya.billslice.core.designsystem.theme

import android.os.Build
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.dimasarya.billslice.core.designsystem.R

@OptIn(ExperimentalTextApi::class)
private fun funnelSansFont(weight: FontWeight): Font = Font(
    resId = R.font.funnel_sans,
    weight = weight,
    variationSettings = FontVariation.Settings(
        FontVariation.weight(weight.weight),
    ),
)

val FunnelSans = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    FontFamily(
        funnelSansFont(FontWeight.Medium),
        funnelSansFont(FontWeight.SemiBold),
        funnelSansFont(FontWeight.Bold),
        funnelSansFont(FontWeight.ExtraBold),
    )
} else {
    FontFamily.SansSerif
}

val BillSliceTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FunnelSans,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 34.sp,
        lineHeight = 36.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = FunnelSans,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp,
        lineHeight = 32.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = FunnelSans,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 26.sp,
        lineHeight = 30.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FunnelSans,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 22.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FunnelSans,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 20.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FunnelSans,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 20.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FunnelSans,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FunnelSans,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 19.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = FunnelSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 15.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = FunnelSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp,
        lineHeight = 13.sp,
    ),
)
