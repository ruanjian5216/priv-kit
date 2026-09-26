package priv.kit.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import hyper_ui.HyperColors
import hyper_ui.HyperText
import hyper_ui.HyperTheme
import priv.kit.ui.R

internal data class PrivilegeUiFingerprintTextPolicy(
    val minFontSize: TextUnit,
    val maxFontSize: TextUnit,
    val stepSize: TextUnit,
)

internal fun privilegeUiFingerprintTextPolicy(
    typography: hyper_ui.HyperTypography,
): PrivilegeUiFingerprintTextPolicy? {
    val minFontSize = typography.labelSmall.fontSize
    val maxFontSize = typography.bodySmall.fontSize
    if (!minFontSize.isSp || !maxFontSize.isSp) return null
    if (minFontSize.value < 0f || maxFontSize.value < 0f || minFontSize >= maxFontSize) return null
    return PrivilegeUiFingerprintTextPolicy(
        minFontSize = minFontSize,
        maxFontSize = maxFontSize,
        stepSize = 0.25.sp,
    )
}

internal fun privilegeUiFingerprintShouldWrap(
    currentlyWrapped: Boolean,
    didOverflowWidth: Boolean,
    didOverflowHeight: Boolean,
    lineCount: Int,
): Boolean = if (currentlyWrapped) lineCount > 1 else didOverflowWidth || didOverflowHeight

@Composable
internal fun AdbFingerprintRow(
    fingerprint: String?,
) {
    val typography = HyperTheme.typography
    val fingerprintTextStyle = typography.bodySmall
    val textPolicy = privilegeUiFingerprintTextPolicy(typography)
    var fingerprintWrapped by remember(fingerprint, textPolicy) { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PrivilegeUiSpacing.extraSmall),
    ) {
        HyperText(
            text = stringResource(R.string.priv_ui_adb_key_fingerprint),
            style = HyperTheme.typography.bodyMedium,
            color = HyperColors.secondaryText,
        )
        SelectionContainer {
            HyperText(
                modifier = Modifier.fillMaxWidth(),
                text = fingerprint ?: stringResource(
                    R.string.priv_ui_adb_key_fingerprint_unavailable,
                ),
                style = if (fingerprintWrapped && textPolicy != null) {
                    fingerprintTextStyle.copy(fontSize = textPolicy.minFontSize)
                } else {
                    fingerprintTextStyle
                },
                fontFamily = FontFamily.Monospace,
                color = HyperColors.secondaryText,
                maxLines = if (fingerprintWrapped) Int.MAX_VALUE else 1,
                overflow = TextOverflow.Visible,
                onTextLayout = { result ->
                    fingerprintWrapped = privilegeUiFingerprintShouldWrap(
                        currentlyWrapped = fingerprintWrapped,
                        didOverflowWidth = result.didOverflowWidth,
                        didOverflowHeight = result.didOverflowHeight,
                        lineCount = result.lineCount,
                    )
                },
            )
        }
    }
}

@Composable
internal fun AdbStatusRow(
    label: String,
    text: String?,
    color: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HyperText(
            text = label,
            style = HyperTheme.typography.bodyMedium,
            color = HyperColors.secondaryText,
        )
        if (text != null) {
            HyperText(
                text = text,
                style = HyperTheme.typography.labelMedium,
                color = color,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
