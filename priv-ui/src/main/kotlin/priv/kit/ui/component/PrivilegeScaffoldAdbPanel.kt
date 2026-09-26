package priv.kit.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import hyper_ui.HyperColors
import hyper_ui.HyperDivider
import priv.kit.ui.PrivilegeUiAdbTcpPolicy
import priv.kit.ui.PrivilegeUiScreenScope
import priv.kit.ui.R

@Composable
internal fun PrivilegeUiScreenScope.AdbPanel() {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        AnimatedVisibility(
            visible = state.localNetworkPermissionMissing,
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically(shrinkTowards = Alignment.Top),
        ) {
            Column {
                LocalNetworkPermissionPanel()
                Spacer(Modifier.height(PrivilegeUiSpacing.large))
            }
        }
        AnimatedVisibility(visible = state.batteryOptimizationPromptVisible) {
            Column {
                BatteryOptimizationPromptPanel()
                Spacer(Modifier.height(PrivilegeUiSpacing.large))
            }
        }
        Panel {
            val wirelessAdbVisible = state.wirelessAdbSupported
            val staticTcpVisible = state.adbTcpPolicy != PrivilegeUiAdbTcpPolicy.DISABLED
            AdbFingerprintRow(
                fingerprint = state.adbKeyFingerprint,
            )
            if (!wirelessAdbVisible && !staticTcpVisible) {
                StatusText(stringResource(R.string.priv_ui_adb_unavailable))
            } else {
                HyperDivider(color = HyperColors.divider)
                if (wirelessAdbVisible) {
                    WirelessAdbSection()
                }
                if (wirelessAdbVisible && staticTcpVisible) {
                    HyperDivider(color = HyperColors.divider)
                }
                if (staticTcpVisible) {
                    StaticTcpAdbSection()
                }
            }
        }
    }
    state.staticTcpSwitchConfirmation?.let { action ->
        StaticTcpSwitchConfirmationDialog(action)
    }
    if (state.tcpAuthorizationFailureDialogVisible) {
        TcpAuthorizationFailureDialog()
    }
}
