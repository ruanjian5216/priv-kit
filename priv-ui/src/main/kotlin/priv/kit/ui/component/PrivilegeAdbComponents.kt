package priv.kit.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import hyper_ui.HyperColors
import hyper_ui.HyperText
import hyper_ui.HyperTheme
import priv.kit.ui.R

@Composable
internal fun AdbFingerprintRow(
    fingerprint: String?,
) {
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
                style = HyperTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                color = HyperColors.secondaryText,
                // Text wraps naturally; layout results must not toggle text style or line limits.
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
