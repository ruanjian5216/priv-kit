package priv.kit.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import hyper_ui.HyperButton
import hyper_ui.HyperButtonDefaults
import hyper_ui.HyperButtonTone
import hyper_ui.HyperColors
import hyper_ui.HyperText
import hyper_ui.HyperTheme
import priv.kit.ui.PrivilegeUiScreenScope
import priv.kit.ui.R

@Composable
internal fun PrivilegeUiScreenScope.LocalNetworkPermissionPanel() {
    Panel {
        HyperText(
            text = stringResource(R.string.priv_ui_local_network_permission_required),
            style = HyperTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
        HyperText(
            text = stringResource(R.string.priv_ui_local_network_permission_message),
            style = HyperTheme.typography.bodyMedium,
            color = HyperColors.secondaryText,
        )
        HyperButton(
            modifier = Modifier.fillMaxWidth(),
            tone = HyperButtonTone.Outline,
            enabled = interactionEnabled,
            onClick = actions.requestLocalNetworkPermission,
        ) {
            HyperText(stringResource(
                if (state.localNetworkPermissionSettingsRequired) {
                    R.string.priv_ui_local_network_permission_settings_action
                } else {
                    R.string.priv_ui_local_network_permission_grant_action
                },
            ))
        }
    }
}

@Composable
internal fun PrivilegeUiScreenScope.BatteryOptimizationPromptPanel() {
    val settingsUnavailable = stringResource(
        R.string.priv_ui_battery_optimization_settings_unavailable,
    )
    Panel {
        HyperText(
            text = stringResource(R.string.priv_ui_battery_optimization_title),
            style = HyperTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
        HyperText(
            text = stringResource(R.string.priv_ui_battery_optimization_message),
            style = HyperTheme.typography.bodyMedium,
            color = HyperColors.secondaryText,
        )
        HyperButton(
            modifier = Modifier.fillMaxWidth(),
            tone = HyperButtonTone.Outline,
            enabled = interactionEnabled,
            onClick = {
                if (
                    actions.canInteract() &&
                    !actions.requestBatteryOptimization()
                ) {
                    showFeedback(settingsUnavailable)
                }
            },
        ) {
            HyperText(stringResource(R.string.priv_ui_battery_optimization_settings_action))
        }
    }
}
