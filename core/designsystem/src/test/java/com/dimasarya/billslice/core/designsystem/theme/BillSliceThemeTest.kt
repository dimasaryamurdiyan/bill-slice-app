package com.dimasarya.billslice.core.designsystem.theme

import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.luminance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BillSliceThemeTest {
    @Test
    fun lightTheme_usesApprovedPrimaryAndCanvas() {
        assertEquals(0xFF20C982.toInt(), BillSliceLightColorScheme.primary.toArgb())
        assertEquals(0xFFFAF9F6.toInt(), BillSliceLightColorScheme.background.toArgb())
        assertEquals(0xFF17211D.toInt(), BillSliceLightColorScheme.onBackground.toArgb())
        assertEquals(0xFFFFFEFC.toInt(), BillSliceLightColorScheme.surfaceContainer.toArgb())
        assertEquals(0xFFE5F9F0.toInt(), BillSliceLightColorScheme.secondaryContainer.toArgb())
    }

    @Test
    fun lightTheme_hasNoStarterPurple() {
        assertEquals(false, BillSliceLightColorScheme.primary.toArgb() == 0xFF6650A4.toInt())
    }

    @Test
    fun semanticTextPairsMeetNormalTextContrast() {
        assertTrue(contrast(MutedInk.luminance(), SoftSurface.luminance()) >= 4.5f)
        assertTrue(contrast(DeepInk.luminance(), TableEmerald.luminance()) >= 4.5f)
        assertTrue(contrast(DeepEmerald.luminance(), ReceiptMint.luminance()) >= 4.5f)
    }

    private fun contrast(first: Float, second: Float): Float {
        val lighter = maxOf(first, second)
        val darker = minOf(first, second)
        return (lighter + 0.05f) / (darker + 0.05f)
    }
}
