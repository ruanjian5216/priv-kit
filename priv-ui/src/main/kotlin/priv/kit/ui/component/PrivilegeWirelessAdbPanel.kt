package priv.kit.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import hyper_ui.HyperButton
import hyper_ui.HyperButtonTone
import hyper_ui.HyperColors
import hyper_ui.HyperText
import priv.kit.ui.PrivilegeUiRuntimeStartPhase
import priv.kit.ui.PrivilegeUiRuntimeStartSource
import priv.kit.ui.PrivilegeUiScreenScope
import priv.kit.ui.adb.PrivilegeUiWirelessAdbPanelStatus
import priv.kit.ui.adb.privilegeUiWirelessAdbStartActionLabel
import priv.kit.ui.adb.wirelessAdbPanelStatus
import priv.kit.ui.adb.pairing.isPrivilegeUiPairingSessionActive
import priv.kit.ui.R

@Composable
internal fun PrivilegeUiScreenScope.WirelessAdbSection() {
    val adbInteractionEnabled = interactionEnabled
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PrivilegeUiSpacing.medium),
    ) {
        val wirelessStatus = wirelessAdbPanelStatus(
            wifiConnected = state.wifiConnected,
            wirelessDebuggingStatus = state.wirelessDebuggingStatus,
            wirelessPairingServiceStatus = state.wirelessPairingServiceStatus,
            wirelessPairingCheckStatus = state.wirelessPairingCheckStatus,
        )
        val runtimeStartInProgress = state.runtimeStartPhase != PrivilegeUiRuntimeStartPhase.IDLE
        val pairingActionEnabled = adbInteractionEnabled &&
            !runtimeStartInProgress &&
            (!state.busy || state.pairingStatus.isPrivilegeUiPairingSessionActive())
        AdbStatusRow(
            label = stringResource(R.string.priv_ui_adb_tab_wireless),
            text = if (state.wirelessAdbStatusLoaded) {
                wirelessStatus.displayText()
            } else {
                null
            },
            color = if (state.wirelessAdbStatusLoaded) {
                wirelessStatus.displayColor()
            } else {
                HyperColors.secondaryText
            },
        )
        if (state.pairingNotificationPermissionWarningVisible) {
            WirelessAdbPairingNotificationPermissionWarningDialog()
        } else if (state.pairingDialogVisible) {
            WirelessAdbPairingDialog()
        }
        val startAction = state.startActionFor(
            source = PrivilegeUiRuntimeStartSource.ADB_WIRELESS,
            providerId = null,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PrivilegeUiSpacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HyperButton(
                tone = HyperButtonTone.Outline,
                enabled = pairingActionEnabled,
                onClick = actions.startNotificationPairing,
            ) {
                HyperText(stringResource(R.string.priv_ui_wireless_pair_action))
            }
            HyperButton(
                modifier = Modifier.weight(1f),
                enabled = state.startActionEnabled(
                    action = startAction,
                    startAvailable = true,
                ) && adbInteractionEnabled,
                onClick = {
                    when (startAction) {
                        PrivilegeUiStartAction.START ->
                            actions.startWirelessAdb()
                        PrivilegeUiStartAction.CANCEL -> actions.stopCurrentStart()
                        PrivilegeUiStartAction.CANCELLING,
                        PrivilegeUiStartAction.NONE,
                        -> Unit
                    }
                },
            ) {
                HyperText(
                    stringResource(
                        privilegeUiWirelessAdbStartActionLabel(startAction),
                    ),
                )
            }
        }
    }
}

@Composable
private fun PrivilegeUiWirelessAdbPanelStatus.displayText(): String =
    when (this) {
        PrivilegeUiWirelessAdbPanelStatus.WIFI_REQUIRED ->
            stringResource(R.string.priv_ui_wireless_status_wifi_required)
        PrivilegeUiWirelessAdbPanelStatus.OFF -> stringResource(R.string.priv_ui_wireless_status_off)
        PrivilegeUiWirelessAdbPanelStatus.UNPAIRED -> stringResource(R.string.priv_ui_wireless_status_unpaired)
        PrivilegeUiWirelessAdbPanelStatus.PAIRABLE -> stringResource(R.string.priv_ui_wireless_status_pairable)
        PrivilegeUiWirelessAdbPanelStatus.PAIRED -> stringResource(R.string.priv_ui_wireless_status_paired)
    }

@Composable
private fun PrivilegeUiWirelessAdbPanelStatus.displayColor(): Color =
    when (this) {
        PrivilegeUiWirelessAdbPanelStatus.PAIRED -> HyperColors.success
        PrivilegeUiWirelessAdbPanelStatus.PAIRABLE -> HyperColors.accent
        PrivilegeUiWirelessAdbPanelStatus.WIFI_REQUIRED,
        PrivilegeUiWirelessAdbPanelStatus.OFF,
        PrivilegeUiWirelessAdbPanelStatus.UNPAIRED,
        -> HyperColors.secondaryText
    }
