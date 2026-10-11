package priv.kit.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import hyper_ui.HyperAlertDialog
import hyper_ui.HyperButton
import hyper_ui.HyperColors
import hyper_ui.HyperText
import hyper_ui.HyperTextField
import hyper_ui.HyperTheme
import priv.kit.ui.PrivilegeUiAdbPairingStatus
import priv.kit.ui.PrivilegeUiScreenScope
import priv.kit.ui.R
import priv.kit.ui.adb.pairing.isPrivilegeUiPairingCode
import priv.kit.ui.adb.pairing.isPrivilegeUiPairingSessionActive

@Composable
internal fun PrivilegeUiScreenScope.WirelessAdbPairingNotificationPermissionWarningDialog() {
    HyperAlertDialog(
        visible = true,
        onClose = {
            if (interactionEnabled) actions.cancelPendingPairingStart()
        },
        dismissOnClickOutside = false,
        title = stringResource(R.string.priv_ui_notification_permission_unavailable_title),
        content = {
            HyperText(stringResource(R.string.priv_ui_notification_permission_unavailable_message))
        },
        actions = {
            HyperButton(
                enabled = interactionEnabled,
                onPressed = actions.continuePairingWithoutNotification,
                colorStyle = "primary", type = "filled",
            ) {
                HyperText(stringResource(R.string.priv_ui_pairing_continue_action))
            }
            HyperButton(
                enabled = interactionEnabled,
                onPressed = actions.cancelPendingPairingStart,
                colorStyle = "primary", type = "filled",
            ) {
                HyperText(stringResource(R.string.priv_ui_pairing_cancel_action))
            }
            HyperButton(
                enabled = interactionEnabled,
                onPressed = {
                    actions.openNotificationSettings()
                },
                colorStyle = "primary", type = "filled",
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
        onClose = ::dismissOrStop,
        dismissOnClickOutside = false,
        title = stringResource(R.string.priv_ui_wireless_pair_dialog_title),
        content = {
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
                    onChanged = actions.updatePairingCode,
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
                    label = {
                        HyperText(stringResource(R.string.priv_ui_pairing_code))
                    },
                )
            }
        },
        actions = {
            HyperButton(
                enabled = canSubmit,
                onPressed = actions.submitNotificationPairingCode,
                colorStyle = "primary", type = "filled",
            ) {
                HyperText(stringResource(R.string.priv_ui_pairing_submit_action))
            }
            HyperButton(
                enabled = interactionEnabled,
                onPressed = ::dismissOrStop,
                colorStyle = "primary", type = "filled",
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
