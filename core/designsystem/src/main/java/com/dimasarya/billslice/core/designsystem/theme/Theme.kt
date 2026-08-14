package com.dimasarya.billslice.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val BillSliceLightColorScheme = lightColorScheme(
    primary = TableEmerald,
    onPrimary = DeepInk,
    primaryContainer = ReceiptMint,
    onPrimaryContainer = DeepEmerald,
    secondary = TableCharcoal,
    onSecondary = WarmSurface,
    secondaryContainer = ReceiptMint,
    onSecondaryContainer = DeepEmerald,
    tertiary = DeepEmerald,
    onTertiary = WarmSurface,
    tertiaryContainer = ReceiptMint,
    onTertiaryContainer = DeepEmerald,
    background = WarmCanvas,
    onBackground = DeepInk,
    surface = WarmSurface,
    onSurface = DeepInk,
    surfaceVariant = SoftSurface,
    onSurfaceVariant = MutedInk,
    surfaceTint = Color.Transparent,
    surfaceBright = WarmSurface,
    surfaceDim = SoftSurface,
    surfaceContainerLowest = WarmSurface,
    surfaceContainerLow = WarmCanvas,
    surfaceContainer = WarmSurface,
    surfaceContainerHigh = SoftSurface,
    surfaceContainerHighest = SubtleBorder,
    outline = SubtleBorder,
    outlineVariant = SubtleBorder,
    error = ErrorInk,
    onError = Color.White,
    errorContainer = SoftSurface,
    onErrorContainer = ErrorInk,
    inverseSurface = DeepInk,
    inverseOnSurface = WarmSurface,
    inversePrimary = TableEmerald,
    scrim = DeepInk,
)

val BillSliceShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

@Immutable
data class BillSliceExtendedColors(
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val actionSecondary: Color,
)

@Immutable
data class BillSliceSpacing(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 12.dp,
    val large: Dp = 16.dp,
    val extraLarge: Dp = 24.dp,
    val screenHorizontal: Dp = 18.dp,
)

private val LocalExtendedColors = staticCompositionLocalOf {
    BillSliceExtendedColors(
        successContainer = ReceiptMint,
        onSuccessContainer = DeepEmerald,
        warningContainer = WarningSurface,
        onWarningContainer = WarningInk,
        actionSecondary = TableCharcoal,
    )
}

private val LocalSpacing = staticCompositionLocalOf { BillSliceSpacing() }

object BillSliceThemeTokens {
    val colors: BillSliceExtendedColors
        @Composable get() = LocalExtendedColors.current

    val spacing: BillSliceSpacing
        @Composable get() = LocalSpacing.current
}

@Composable
fun BillSliceTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalExtendedColors provides LocalExtendedColors.current,
        LocalSpacing provides LocalSpacing.current,
    ) {
        MaterialTheme(
            colorScheme = BillSliceLightColorScheme,
            typography = BillSliceTypography,
            shapes = BillSliceShapes,
            content = content,
        )
    }
}
