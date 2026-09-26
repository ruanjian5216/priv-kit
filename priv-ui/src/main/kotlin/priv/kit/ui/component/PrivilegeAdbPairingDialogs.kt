package priv.kit.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import hyper_ui.HyperAlertDialog
import hyper_ui.HyperColors
import hyper_ui.HyperText
import hyper_ui.HyperTextField
import hyper_ui.HyperTheme
import priv.kit.ui.PrivilegeUiAdbPairingStatus
import priv.kit.ui.PrivilegeUiScreenScope
import priv.kit.ui.adb.pairing.isPrivilegeUiPairingSessionActive
import priv.kit.ui.adb.pairing.isPrivilegeUiPairingCode
import priv.kit.ui.R

@Composable
internal fun PrivilegeUiScreenScope.WirelessAdbPairingNotificationPermissionWarningDialog() {
    HyperAlertDialog(
        visible = true,
        onDismissRequest = {
            if (interactionEnabled) actions.cancelPendingPairingStart()
        },
        dismissOnClickOutside = false,
        title = stringResource(R.string.priv_ui_notification_permission_unavailable_title),
        bodyContent = {
            HyperText(stringResource(R.string.priv_ui_notification_permission_unavailable_message))
        },
        actionContent = {
            TextButton(
                enabled = interactionEnabled,
                onClick = actions.continuePairingWithoutNotification,
            ) {
                HyperText(stringResource(R.string.priv_ui_pairing_continue_action))
            }
            TextButton(
                enabled = interactionEnabled,
                onClick = actions.cancelPendingPairingStart,
            ) {
                HyperText(stringResource(R.string.priv_ui_pairing_cancel_action))
            }
            TextButton(
                enabled = interactionEnabled,
                onClick = {
                    actions.openNotificationSettings()
                },
            ) {
                HyperText(stringResource(R.string.priv_ui_notification_permission_settings_action))
            }
        },
    )
}

@Composable
internal fun PrivilegeUiScreenScope.WirelessAdbPairingDialog() {
    val defaultPairingMessage = stringResource(R.string.priv_ui_pairing_default_message)
    val pairingInputHint = stringResource(
        privilegeUiPairingInputHint(state.notificationPairingRunning),
    )
    val pairing = state.pairingStatus == PrivilegeUiAdbPairingStatus.PAIRING
    val canSubmit = privilegeUiPairingCodeSubmitEnabled(
        pairingStatus = state.pairingStatus,
        pairingCode = state.pairingCode,
    ) && interactionEnabled
    fun dismissOrStop() {
        if (interactionEnabled) actions.stopNotificationPairing()
    }
    HyperAlertDialog(
        visible = true,
        onDismissRequest = ::dismissOrStop,
        dismissOnClickOutside = false,
        title = stringResource(R.string.priv_ui_wireless_pair_dialog_title),
        bodyContent = {
            Column(verticalArrangement = Arrangement.spacedBy(PrivilegeUiSpacing.medium)) {
                Column(verticalArrangement = Arrangement.spacedBy(PrivilegeUiSpacing.extraSmall)) {
                    HyperText(
                        text = state.pairingText

                            ?.takeIf(String::isNotBlank)
                            ?: defaultPairingMessage,
                        style = HyperTheme.typography.bodyMedium,
                    )
                    HyperText(
                        text = pairingInputHint,
                        style = HyperTheme.typography.bodyMedium,
                        color = HyperColors.secondaryText,
                    )
                }
                HyperTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.pairingCode,
                    enabled = interactionEnabled,
                    onValueChange = actions.updatePairingCode,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (canSubmit) {
                                actions.submitNotificationPairingCode()
                            }
                        },
                    ),
                    labelContent = {
                        HyperText(stringResource(R.string.priv_ui_pairing_code))
                    },
                    singleLine = true,
                )
            }
        },
        actionContent = {
            TextButton(
                enabled = canSubmit,
                onClick = actions.submitNotificationPairingCode,
            ) {
                HyperText(stringResource(R.string.priv_ui_pairing_submit_action))
            }
            TextButton(
                enabled = interactionEnabled,
                onClick = ::dismissOrStop,
            ) {
                HyperText(
                    stringResource(
                        if (pairing) {
                            R.string.priv_ui_pairing_stop_action
                        } else {
                            R.string.priv_ui_pairing_cancel_action
                        },
                    ),
                )
            }
        },
    )
}

internal fun privilegeUiPairingCodeSubmitEnabled(
    pairingStatus: PrivilegeUiAdbPairingStatus,
    pairingCode: String,
): Boolean =
    pairingStatus != PrivilegeUiAdbPairingStatus.PAIRING &&
        pairingStatus.isPrivilegeUiPairingSessionActive() &&
        pairingCode.isPrivilegeUiPairingCode()

internal fun privilegeUiPairingInputHint(notificationPairingRunning: Boolean): Int =
    if (notificationPairingRunning) {
        R.string.priv_ui_pairing_input_hint
    } else {
        R.string.priv_ui_pairing_split_screen_hint
    }
