package priv.kit.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import hyper_ui.HyperAlertDialog
import hyper_ui.HyperButton
import hyper_ui.HyperButtonTone
import hyper_ui.HyperColors
import hyper_ui.HyperDialog
import hyper_ui.HyperIcon
import hyper_ui.HyperPanel
import hyper_ui.HyperPanelColors
import hyper_ui.HyperText
import hyper_ui.HyperTheme
import hyper_ui.LocalHyperContentColor
import priv.kit.ui.PrivilegeUiAdbTcpPolicy
import priv.kit.ui.PrivilegeUiRuntimeStartPhase
import priv.kit.ui.PrivilegeUiRuntimeStartSource
import priv.kit.ui.PrivilegeUiScreenScope
import priv.kit.ui.PrivilegeUiWirelessAdbStatus
import priv.kit.ui.adb.PrivilegeUiStaticTcpPanelStatus
import priv.kit.ui.adb.PrivilegeUiStaticTcpSwitchAction
import priv.kit.ui.adb.staticTcpActionLabel
import priv.kit.ui.adb.staticTcpStartActionEnabled
import priv.kit.ui.adb.staticTcpCommandHelpVisible
import priv.kit.ui.adb.staticTcpPanelStatus
import priv.kit.ui.state.privilegeUiStaticTcpOpenCommand
import priv.kit.ui.R

@Composable
internal fun PrivilegeUiScreenScope.TcpAuthorizationFailureDialog() {
    HyperDialog(
        visible = true,
        onDismissRequest = {},
        dismissOnBackPress = false,
        dismissOnClickOutside = false,
        title = stringResource(R.string.priv_ui_system_prompt_tcp_authorization_title),
        actionContent = {
            TextButton(
                enabled = interactionEnabled,
                onClick = actions.dismissTcpAuthorizationFailureDialog,
            ) {
                HyperText(stringResource(R.string.priv_ui_ok))
            }
        },
    ) {
        HyperText(stringResource(R.string.priv_ui_tcp_authorization_timeout_message))
    }
}

@Composable
internal fun PrivilegeUiScreenScope.StaticTcpSwitchConfirmationDialog(
    action: PrivilegeUiStaticTcpSwitchAction,
) {
    HyperAlertDialog(
        visible = true,
        onDismissRequest = {
            if (interactionEnabled) actions.cancelStaticTcpSwitch()
        },
        dismissOnClickOutside = false,
        title = stringResource(R.string.priv_ui_adb_static_switch_confirmation_title),
        bodyContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(PrivilegeUiSpacing.medium),
            ) {
                HyperText(
                    text = stringResource(R.string.priv_ui_adb_static_switch_confirmation_message),
                    style = HyperTheme.typography.bodyMedium,
                )
                AdbConnectionWarning()
            }
        },
        actionContent = {
            TextButton(
                enabled = interactionEnabled,
                onClick = actions.confirmStaticTcpSwitch,
            ) {
                HyperText(
                    stringResource(
                        when (action) {
                            PrivilegeUiStaticTcpSwitchAction.START_SERVICE ->
                                R.string.priv_ui_adb_static_switch_continue_start_action
                            PrivilegeUiStaticTcpSwitchAction.ENABLE_PORT ->
                                R.string.priv_ui_adb_static_switch_continue_enable_action
                        },
                    ),
                )
            }
            TextButton(
                enabled = interactionEnabled,
                onClick = actions.cancelStaticTcpSwitch,
            ) {
                HyperText(stringResource(R.string.priv_ui_adb_static_switch_cancel_action))
            }
        },
    )
}

@Composable
internal fun PrivilegeUiScreenScope.StaticTcpAdbSection() {
    val adbInteractionEnabled = interactionEnabled
    val copiedMessage = stringResource(R.string.priv_ui_adb_static_command_copied)
    val controlStatusLoadingMessage = stringResource(R.string.priv_ui_adb_static_control_status_loading)
    var controlDialogVisible by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PrivilegeUiSpacing.medium),
    ) {
        val tcpPolicy = state.adbTcpPolicy
        val configuredTcpPort = state.tcpPort
        val paired = state.wirelessPairingCheckStatus == PrivilegeUiWirelessAdbStatus.ON
        val activeTcpPort = state.staticTcp.activePort
        val staticTcpActive = activeTcpPort != null
        val staticTcpConfigured = state.staticTcp.configuredPort != null
        val runtimeStartInProgress = state.runtimeStartPhase != PrivilegeUiRuntimeStartPhase.IDLE
        val wirelessAdbSupported = state.wirelessAdbSupported
        val staticTcpStatus = staticTcpPanelStatus(
            tcpModeConfigured = staticTcpConfigured,
            tcpModeActive = staticTcpActive,
            status = state.staticTcp.authorizationStatus,
        )
        val staticTcpCommand = privilegeUiStaticTcpOpenCommand(
            activeTcpPort ?: configuredTcpPort,
        )
        val prepareActionVisible = !wirelessAdbSupported &&
            tcpPolicy == PrivilegeUiAdbTcpPolicy.AUTO_ENABLE_AFTER_WIRELESS_PAIRED
        val prepareActionEnabled = prepareActionVisible &&
            state.staticTcp.loaded &&
            adbInteractionEnabled &&
            !runtimeStartInProgress &&
            !state.busy &&
            paired &&
            !staticTcpActive
        val startAction = state.startActionFor(
            source = PrivilegeUiRuntimeStartSource.ADB_STATIC_TCP,
            providerId = null,
        )
        val tcpStartActionEnabled = state.staticTcpStartActionEnabled(
            action = startAction,
            wirelessAdbSupported = wirelessAdbSupported,
            interactionEnabled = adbInteractionEnabled,
        )
        val controlActionAvailable = adbInteractionEnabled &&
            !runtimeStartInProgress &&
            !state.busy
        val controlActionEnabled = controlActionAvailable && staticTcpActive
        val commandHelpVisible = staticTcpCommandHelpVisible(
            wirelessAdbSupported = wirelessAdbSupported,
        )
        AdbStatusRow(
            label = stringResource(R.string.priv_ui_adb_tab_static),
            text = if (state.staticTcp.loaded) {
                staticTcpStatus.displayText()
            } else {
                null
            },
            color = if (state.staticTcp.loaded) {
                staticTcpStatus.displayColor()
            } else {
                HyperColors.secondaryText
            },
        )
        if (prepareActionVisible) {
            HyperButton(
                modifier = Modifier.fillMaxWidth(),
                tone = HyperButtonTone.Outline,
                enabled = prepareActionEnabled,
                onClick = actions.enableTcpMode,
            ) {
                HyperText(stringResource(R.string.priv_ui_adb_static_prepare_action))
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PrivilegeUiSpacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HyperButton(
                tone = HyperButtonTone.Outline,
                enabled = controlActionAvailable && (!state.staticTcp.loaded || staticTcpActive),
                onClick = {
                    if (!state.staticTcp.loaded) {
                        showFeedback(controlStatusLoadingMessage)
                    } else if (staticTcpActive) {
                        controlDialogVisible = true
                    }
                },
            ) {
                HyperText(stringResource(R.string.priv_ui_adb_static_control_action))
            }
            HyperButton(
                modifier = Modifier.weight(1f),
                enabled = tcpStartActionEnabled,
                onClick = {
                    when (startAction) {
                        PrivilegeUiStartAction.START ->
                            actions.startStaticTcpAdb()
                        PrivilegeUiStartAction.CANCEL -> actions.stopCurrentStart()
                        PrivilegeUiStartAction.CANCELLING,
                        PrivilegeUiStartAction.NONE,
                        -> Unit
                    }
                },
            ) {
                HyperText(
                    stringResource(
                        staticTcpActionLabel(
                            action = startAction,
                        ),
                    ),
                )
            }
        }
        if (commandHelpVisible) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(PrivilegeUiSpacing.medium),
            ) {
                HyperText(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.priv_ui_adb_static_command_desc),
                    style = HyperTheme.typography.bodySmall,
                    color = HyperColors.secondaryText,
                )
                CommandBlock(staticTcpCommand)
                HyperButton(
                    modifier = Modifier.fillMaxWidth(),
                    tone = HyperButtonTone.Outline,
                    enabled = adbInteractionEnabled &&
                        !runtimeStartInProgress &&
                        !state.busy,
                    onClick = {
                        if (!actions.canInteract()) return@HyperButton
                        actions.copyStaticTcpCommand()
                        showFeedback(copiedMessage)
                    },
                ) {
                    HyperText(stringResource(R.string.priv_ui_manual_copy_command))
                }
            }
        }
        if (controlDialogVisible) {
            StaticTcpControlDialog(
                commandLine = staticTcpCommand,
                commandVisible = commandHelpVisible,
                actionEnabled = controlActionEnabled,
                onDismiss = {
                    controlDialogVisible = false
                },
                onStop = {
                    controlDialogVisible = false
                    actions.disableTcpMode()
                },
                onRestart = {
                    controlDialogVisible = false
                    actions.restartTcpMode()
                },
            )
        }
    }
}

@Composable
private fun StaticTcpControlDialog(
    commandLine: String,
    commandVisible: Boolean,
    actionEnabled: Boolean,
    onDismiss: () -> Unit,
    onStop: () -> Unit,
    onRestart: () -> Unit,
) {
    HyperAlertDialog(
        visible = true,
        onDismissRequest = onDismiss,
        dismissOnClickOutside = false,
        title = stringResource(R.string.priv_ui_adb_static_control_dialog_title),
        bodyContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(PrivilegeUiSpacing.medium),
            ) {
                if (commandVisible) {
                    HyperText(
                        text = stringResource(R.string.priv_ui_adb_static_command_desc),
                        style = HyperTheme.typography.bodyMedium,
                    )
                    CommandBlock(commandLine)
                }
                AdbConnectionWarning()
            }
        },
        actionContent = {
            TextButton(
                enabled = actionEnabled,
                onClick = onRestart,
            ) {
                HyperText(stringResource(R.string.priv_ui_adb_static_control_restart_action))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(PrivilegeUiSpacing.extraSmall)) {
                TextButton(onClick = onDismiss) {
                    HyperText(stringResource(R.string.priv_ui_adb_static_switch_cancel_action))
                }
                TextButton(
                    enabled = actionEnabled,
                    onClick = onStop,
                ) {
                    HyperText(
                        text = stringResource(R.string.priv_ui_adb_static_control_stop_action),
                        color = HyperColors.danger,
                    )
                }
            }
        },
    )
}

@Composable
private fun AdbConnectionWarning() {
    CompositionLocalProvider(LocalHyperContentColor provides HyperColors.danger) {
        HyperPanel(
            modifier = Modifier.fillMaxWidth(),
            contentModifier = Modifier
                .fillMaxWidth()
                .padding(PrivilegeUiSpacing.medium),
            colors = HyperPanelColors(containerColor = hyperDangerContainer()),
            shape = HyperTheme.shapes.medium,
            verticalArrangement = Arrangement.spacedBy(PrivilegeUiSpacing.extraSmall),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HyperIcon(
                    modifier = Modifier.size(20.dp),
                    imageVector = PrivilegeUiIcons.Warning,
                    contentDescription = null,
                )
                Spacer(Modifier.width(PrivilegeUiSpacing.medium))
                HyperText(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.priv_ui_adb_connection_warning_title),
                    style = HyperTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            HyperText(
                text = stringResource(R.string.priv_ui_adb_connection_warning_message),
                style = HyperTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun PrivilegeUiStaticTcpPanelStatus.displayText(): String =
    when (this) {
        PrivilegeUiStaticTcpPanelStatus.PORT_NOT_CONFIGURED ->
            stringResource(R.string.priv_ui_adb_static_port_unavailable)
        PrivilegeUiStaticTcpPanelStatus.ADB_SERVICE_STOPPED ->
            stringResource(R.string.priv_ui_adb_static_service_stopped)
        PrivilegeUiStaticTcpPanelStatus.UNAUTHORIZED -> stringResource(R.string.priv_ui_adb_static_status_unauthorized)
        PrivilegeUiStaticTcpPanelStatus.AUTHORIZED -> stringResource(R.string.priv_ui_adb_static_status_authorized)
    }

@Composable
private fun PrivilegeUiStaticTcpPanelStatus.displayColor(): Color =
    when (this) {
        PrivilegeUiStaticTcpPanelStatus.AUTHORIZED -> HyperColors.success
        PrivilegeUiStaticTcpPanelStatus.PORT_NOT_CONFIGURED,
        PrivilegeUiStaticTcpPanelStatus.ADB_SERVICE_STOPPED,
        PrivilegeUiStaticTcpPanelStatus.UNAUTHORIZED,
        -> HyperColors.secondaryText
    }
