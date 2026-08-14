package com.dimasarya.billslice

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.dimasarya.billslice.core.designsystem.theme.BillSliceTheme
import com.dimasarya.billslice.core.designsystem.theme.BillSliceThemeTokens
import com.dimasarya.billslice.core.designsystem.theme.DeepEmerald
import com.dimasarya.billslice.core.designsystem.theme.ReceiptMint
import com.dimasarya.billslice.core.designsystem.theme.TableEmerald
import com.dimasarya.billslice.core.designsystem.theme.WarmCanvas
import com.dimasarya.billslice.core.designsystem.theme.WarmSurface

sealed interface StartupUiState {
    data object Initializing : StartupUiState
    data object Ready : StartupUiState
    data object RecoverableFailure : StartupUiState
    data object UnrecoverableConfigurationFailure : StartupUiState
}

@Composable
fun SplashScreen(
    state: StartupUiState,
    modifier: Modifier = Modifier,
    onReady: () -> Unit,
    onFailure: () -> Unit,
) {
    val logoDescription = stringResource(R.string.splash_logo_description)
    LaunchedEffect(state) {
        when (state) {
            StartupUiState.Ready -> onReady()
            StartupUiState.RecoverableFailure,
            StartupUiState.UnrecoverableConfigurationFailure,
            -> onFailure()
            StartupUiState.Initializing -> Unit
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to ReceiptMint,
                    0.55f to WarmCanvas,
                    1f to WarmCanvas,
                ),
            )
            .padding(horizontal = 18.dp, vertical = 16.dp),
    ) {
        val compactHeight = maxHeight < 600.dp
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.weight(1f))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(BillSliceThemeTokens.spacing.medium),
            ) {
                Surface(
                    modifier = Modifier.size(if (compactHeight) 144.dp else 196.dp),
                    color = WarmSurface.copy(alpha = 0.78f),
                    shape = RoundedCornerShape(52.dp),
                    shadowElevation = 8.dp,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Surface(
                            modifier = Modifier.size(if (compactHeight) 116.dp else 160.dp),
                            color = TableEmerald,
                            shape = RoundedCornerShape(28.dp),
                        ) {
                            ReceiptLogo(
                                modifier = Modifier
                                    .padding(if (compactHeight) 32.dp else 44.dp)
                                    .semantics {
                                        contentDescription = logoDescription
                                    },
                            )
                        }
                    }
                }
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.displayLarge,
                )
                Text(
                    text = stringResource(R.string.splash_tagline),
                    color = DeepEmerald,
                    style = MaterialTheme.typography.titleLarge,
                )
                if (!compactHeight) {
                    Text(
                        text = stringResource(R.string.splash_supporting),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            Row(
                horizontalArrangement = Arrangement.spacedBy(BillSliceThemeTokens.spacing.small),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Rounded.Shield,
                    contentDescription = null,
                    tint = DeepEmerald,
                )
                Text(
                    text = stringResource(R.string.splash_privacy),
                    color = DeepEmerald,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

@Composable
private fun ReceiptLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val strokeWidth = size.minDimension * 0.08f
        drawRoundRect(
            color = WarmSurface,
            size = size,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.minDimension * 0.08f),
        )
        drawLine(
            color = TableEmerald,
            start = Offset(size.width * 0.72f, 0f),
            end = Offset(size.width * 0.28f, size.height),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
        repeat(4) { index ->
            val y = size.height * (0.25f + index * 0.12f)
            drawLine(
                color = DeepEmerald.copy(alpha = 0.35f),
                start = Offset(size.width * 0.12f, y),
                end = Offset(size.width * (0.44f - index * 0.03f), y),
                strokeWidth = strokeWidth * 0.45f,
                cap = StrokeCap.Round,
            )
        }
        drawRoundRect(
            color = Color.Transparent,
            size = Size(size.width, size.height),
            style = Stroke(strokeWidth * 0.2f),
        )
    }
}

@Preview(name = "Phone", showBackground = true, widthDp = 400, heightDp = 900)
@Preview(name = "Landscape", showBackground = true, widthDp = 800, heightDp = 400)
@Composable
private fun SplashPreview() {
    BillSliceTheme {
        SplashScreen(
            state = StartupUiState.Initializing,
            onReady = {},
            onFailure = {},
        )
    }
}
