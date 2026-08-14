package com.dimasarya.billslice.core.designsystem.theme

import androidx.compose.ui.graphics.toArgb
import org.junit.Assert.assertEquals
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
}
