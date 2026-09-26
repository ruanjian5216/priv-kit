package priv.kit.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import hyper_ui.HyperAlertDialog
import hyper_ui.HyperColors
import hyper_ui.HyperIcon
import hyper_ui.HyperIconButton
import hyper_ui.HyperIconButtonDefaults
import hyper_ui.HyperPanel
import hyper_ui.HyperPanelColors
import hyper_ui.HyperText
import hyper_ui.HyperTheme
import hyper_ui.LocalHyperContentColor
import priv.kit.ui.PrivilegeUiPermissionRestrictionStatus
import priv.kit.ui.PrivilegeUiExternalStartSnapshot
import priv.kit.ui.PrivilegeUiRuntimeStartPhase
import priv.kit.ui.PrivilegeUiRuntimeStatus
import priv.kit.ui.PrivilegeUiScreenScope
import priv.kit.ui.PrivilegeUiScreenState
import priv.kit.ui.R

/** Danger 色语义容器（对应 material3 errorContainer 的角色）。 */
@Composable
internal fun hyperDangerContainer(): Color =
    HyperColors.danger.copy(alpha = if (HyperColors.isLight) 0.12f else 0.22f)

/** Success 色语义容器（对应 material3 tertiaryContainer 的“服务已就绪”角色）。 */
@Composable
internal fun hyperSuccessContainer(): Color =
    HyperColors.success.copy(alpha = if (HyperColors.isLight) 0.14f else 0.24f)

@Composable
internal fun Panel(content: @Composable ColumnScope.() -> Unit) {
    HyperPanel(
        modifier = Modifier.fillMaxWidth(),
        contentModifier = Modifier.padding(PrivilegeUiSpacing.large),
        colors = HyperPanelColors(containerColor = HyperColors.softContainer),
        shape = HyperTheme.shapes.medium,
        verticalArrangement = Arrangement.spacedBy(PrivilegeUiSpacing.medium),
        content = content,
    )
}

@Composable
internal fun ItemPanel(content: @Composable ColumnScope.() -> Unit) {
    HyperPanel(
        modifier = Modifier.fillMaxWidth(),
        contentModifier = Modifier.padding(PrivilegeUiSpacing.medium),
        colors = HyperPanelColors(containerColor = HyperColors.fieldContainer),
        shape = HyperTheme.shapes.small,
        verticalArrangement = Arrangement.spacedBy(PrivilegeUiSpacing.small),
        content = content,
    )
}

@Composable
@Suppress("DEPRECATION")
internal fun PrivilegeUiScreenScope.PermissionRestrictionWarning() {
    var permissionsDialogVisible by remember(state.connectionSerial) { mutableStateOf(false) }
    val permissions = state.deniedServerPermissions
    val clipboard = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current
    val solutionsUrl = stringResource(R.string.priv_ui_permission_solutions_url)
    if (permissionsDialogVisible) {
        val permissionText = permissions.joinToString("\n")
        HyperAlertDialog(
            visible = true,
            onDismissRequest = { permissionsDialogVisible = false },
            title = stringResource(R.string.priv_ui_denied_permissions_title),
            bodyContent = {
                Column(verticalArrangement = Arrangement.spacedBy(PrivilegeUiSpacing.medium)) {
                    HyperText(stringResource(R.string.priv_ui_denied_permissions_message))
                    SelectionContainer {
                        HyperText(
                            text = permissionText,
                            fontFamily = FontFamily.Monospace,
                            style = HyperTheme.typography.bodySmall,
                            softWrap = false,
                            modifier = Modifier.fillMaxWidth().heightIn(max = 320.dp)
                                .background(HyperColors.fieldContainer, HyperTheme.shapes.small)
                                .verticalScroll(rememberScrollState())
                                .horizontalScroll(rememberScrollState())
                                .padding(PrivilegeUiSpacing.medium),
                        )
                    }
                }
            },
            actionContent = {
                TextButton(onClick = { clipboard.setText(AnnotatedString(permissionText)) }) {
                    HyperText(stringResource(R.string.priv_ui_denied_permissions_copy))
                }
                TextButton(onClick = { permissionsDialogVisible = false }) {
                    HyperText(stringResource(R.string.priv_ui_denied_permissions_close))
                }
            },
        )
    }
    CompositionLocalProvider(LocalHyperContentColor provides HyperColors.danger) {
        HyperPanel(
            modifier = Modifier.fillMaxWidth(),
            contentModifier = Modifier
                .fillMaxWidth()
                .padding(PrivilegeUiSpacing.large),
            colors = HyperPanelColors(containerColor = hyperDangerContainer()),
            shape = HyperTheme.shapes.medium,
            verticalArrangement = Arrangement.spacedBy(PrivilegeUiSpacing.extraSmall),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HyperIcon(
                    modifier = Modifier.size(24.dp),
                    imageVector = PrivilegeUiIcons.Warning,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(PrivilegeUiSpacing.medium))
                HyperText(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.priv_ui_permission_restricted_title),
                    style = HyperTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            HyperText(
                text = stringResource(R.string.priv_ui_permission_restricted_message),
                style = HyperTheme.typography.bodyMedium,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    onClick = onViewPermissionSolutions ?: { uriHandler.openUri(solutionsUrl) },
                    modifier = Modifier.weight(1f, fill = false),
                ) {
                    HyperText(stringResource(R.string.priv_ui_permission_solutions_view))
                }
                TextButton(
                    onClick = { permissionsDialogVisible = true },
                    modifier = Modifier.weight(1f, fill = false),
                ) {
                    HyperText(stringResource(R.string.priv_ui_denied_permissions_view))
                }
            }
        }
    }
}

internal fun privilegeUiPermissionRestrictionWarningVisible(
    runtimeStatus: PrivilegeUiRuntimeStatus,
    restrictionStatus: PrivilegeUiPermissionRestrictionStatus,
): Boolean =
    runtimeStatus == PrivilegeUiRuntimeStatus.CONNECTED &&
        restrictionStatus == PrivilegeUiPermissionRestrictionStatus.RESTRICTED

@Composable
internal fun CommandBlock(commandLine: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = HyperColors.fieldContainer,
                shape = HyperTheme.shapes.small,
            )
            .padding(PrivilegeUiSpacing.medium),
    ) {
        SelectionContainer {
            HyperText(
                modifier = Modifier.fillMaxWidth(),
                text = commandLine,
                style = HyperTheme.typography.bodySmall,
                color = HyperColors.primaryText,
                fontFamily = FontFamily.Monospace,
            )
        }
    }
}

@Composable
internal fun PrivilegeUiScreenScope.AutoRecoveryWarning() {
    CompositionLocalProvider(LocalHyperContentColor provides HyperColors.primaryText) {
        HyperPanel(
            modifier = Modifier.fillMaxWidth(),
            contentModifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = PrivilegeUiSpacing.large,
                    vertical = PrivilegeUiSpacing.medium,
                ),
            colors = HyperPanelColors(containerColor = HyperColors.softContainer),
            shape = HyperTheme.shapes.medium,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(PrivilegeUiSpacing.extraSmall),
            ) {
                HyperText(
                    text = stringResource(R.string.priv_ui_auto_recovery_disconnected_title),
                    style = HyperTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                HyperText(
                    text = stringResource(R.string.priv_ui_auto_recovery_disconnected_message),
                    style = HyperTheme.typography.bodySmall,
                )
            }
            TextButton(
                modifier = Modifier.align(Alignment.End),
                enabled = interactionEnabled,
                onClick = actions.disableAutoRecovery,
            ) {
                HyperText(
                    text = stringResource(R.string.priv_ui_auto_recovery_disable_action),
                    maxLines = 1,
                )
            }
        }
    }
}

internal fun privilegeUiAutoRecoveryWarningVisible(
    desiredEnabled: Boolean,
    runtimeStatus: PrivilegeUiRuntimeStatus,
    runtimeStartPhase: PrivilegeUiRuntimeStartPhase,
): Boolean =
    desiredEnabled &&
        runtimeStartPhase == PrivilegeUiRuntimeStartPhase.IDLE &&
        (
            runtimeStatus == PrivilegeUiRuntimeStatus.DISCONNECTED ||
                runtimeStatus == PrivilegeUiRuntimeStatus.FAILED
        )

@Composable
internal fun PrivilegeUiScreenScope.RestartConfirmationDialog() {
    if (state.restartConfirmationTarget == null) return
    HyperAlertDialog(
        visible = true,
        onDismissRequest = {
            if (interactionEnabled) actions.cancelServerRestart()
        },
        title = stringResource(R.string.priv_ui_restart_service_dialog_title),
        bodyContent = {
            HyperText(stringResource(R.string.priv_ui_restart_service_dialog_message))
        },
        actionContent = {
            TextButton(
                enabled = interactionEnabled,
                onClick = actions.confirmServerRestart,
            ) {
                HyperText(stringResource(R.string.priv_ui_restart_service_confirm))
            }
            TextButton(
                enabled = interactionEnabled,
                onClick = actions.cancelServerRestart,
            ) {
                HyperText(stringResource(R.string.priv_ui_restart_service_cancel))
            }
        },
    )
}

@Composable
internal fun PrivilegeUiScreenScope.ServiceStatusPanel() {
    var showStopConfirmation by remember { mutableStateOf(false) }
    val action = privilegeUiServiceStatusAction(
        runtimeStatus = state.runtimeStatus,
        runtimeStartPhase = state.runtimeStartPhase,
    )
    val (
        title,
        detail,
        background,
        foreground,
        icon,
        iconDescription,
        actionContainer,
        actionForeground,
    ) = when (action) {
        PrivilegeUiServiceStatusAction.STOP ->
        StatusUi(
            title = stringResource(R.string.priv_ui_service_started),
            detail = stringResource(
                R.string.priv_ui_service_source,
                runtimeSourceText(state.serverUid),
            ),
            background = hyperSuccessContainer(),
            foreground = HyperColors.success,
            icon = PrivilegeUiIcons.Stop,
            iconDescription = stringResource(R.string.priv_ui_service_stop_action_description),
            actionContainer = hyperDangerContainer(),
            actionForeground = HyperColors.danger,
        )
        PrivilegeUiServiceStatusAction.CANCEL -> StatusUi(
            title = stringResource(R.string.priv_ui_service_not_started),
            detail = state.runtimeStatusDetail(),
            background = HyperColors.softContainer,
            foreground = HyperColors.primaryText,
            icon = PrivilegeUiIcons.Stop,
            iconDescription = stringResource(R.string.priv_ui_start_cancel_action),
            actionContainer = hyperDangerContainer(),
            actionForeground = HyperColors.danger,
        )
        PrivilegeUiServiceStatusAction.CANCELLING -> StatusUi(
            title = stringResource(R.string.priv_ui_service_not_started),
            detail = state.runtimeStatusDetail(),
            background = HyperColors.softContainer,
            foreground = HyperColors.primaryText,
            icon = PrivilegeUiIcons.Stop,
            iconDescription = stringResource(R.string.priv_ui_start_cancelling_action),
            actionContainer = hyperDangerContainer(),
            actionForeground = HyperColors.danger,
        )
        PrivilegeUiServiceStatusAction.START ->
        StatusUi(
            title = stringResource(R.string.priv_ui_service_not_started),
            detail = state.runtimeStatusDetail(),
            background = HyperColors.softContainer,
            foreground = HyperColors.primaryText,
            icon = PrivilegeUiIcons.PlayArrow,
            iconDescription = stringResource(R.string.priv_ui_service_start_action_description),
            actionContainer = HyperColors.accentContainer,
            actionForeground = HyperColors.accent,
        )
    }

    if (showStopConfirmation) {
        HyperAlertDialog(
            visible = true,
            onDismissRequest = {
                if (interactionEnabled) showStopConfirmation = false
            },
            title = stringResource(R.string.priv_ui_stop_service_dialog_title),
            bodyContent = {
                HyperText(stringResource(R.string.priv_ui_stop_service_dialog_message))
            },
            actionContent = {
                TextButton(
                    enabled = interactionEnabled,
                    onClick = {
                        if (!actions.canInteract()) return@TextButton
                        showStopConfirmation = false
                        actions.stopServer()
                    },
                ) {
                    HyperText(stringResource(R.string.priv_ui_stop_service_confirm))
                }
                TextButton(
                    enabled = interactionEnabled,
                    onClick = { showStopConfirmation = false },
                ) {
                    HyperText(stringResource(R.string.priv_ui_stop_service_cancel))
                }
            },
        )
    }

    CompositionLocalProvider(LocalHyperContentColor provides foreground) {
        HyperPanel(
            modifier = Modifier.fillMaxWidth(),
            contentModifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = PrivilegeUiSpacing.large,
                    top = PrivilegeUiSpacing.medium,
                    end = PrivilegeUiSpacing.medium,
                    bottom = PrivilegeUiSpacing.medium,
                ),
            colors = HyperPanelColors(containerColor = background),
            shape = HyperTheme.shapes.medium,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(PrivilegeUiSpacing.extraSmall),
                ) {
                    HyperText(
                        text = title,
                        style = HyperTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    HyperText(
                        text = detail,
                        style = HyperTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.width(PrivilegeUiSpacing.medium))
                PrivilegeIconTooltip(
                    text = iconDescription,
                    modifier = Modifier,
                ) {
                    HyperIconButton(
                        modifier = Modifier.size(PrivilegeUiSize.minimumTouchTarget),
                        enabled = interactionEnabled &&
                            privilegeUiServiceStatusActionEnabled(
                                action = action,
                                busy = state.busy,
                            ),
                        colors = HyperIconButtonDefaults.colors(
                            containerColor = actionContainer,
                            contentColor = actionForeground,
                        ),
                        onClick = {
                            if (!actions.canInteract()) return@HyperIconButton
                            when (action) {
                                PrivilegeUiServiceStatusAction.STOP -> showStopConfirmation = true
                                PrivilegeUiServiceStatusAction.CANCEL -> actions.stopCurrentStart()
                                PrivilegeUiServiceStatusAction.CANCELLING -> Unit
                                PrivilegeUiServiceStatusAction.START -> actions.startInteractive()
                            }
                        },
                    ) {
                        HyperIcon(
                            modifier = Modifier.size(22.dp),
                            imageVector = icon,
                            contentDescription = iconDescription,
                        )
                    }
                }
            }
        }
    }
}

internal enum class PrivilegeUiServiceStatusAction {
    START,
    CANCEL,
    CANCELLING,
    STOP,
}

internal fun privilegeUiServiceStatusAction(
    runtimeStatus: PrivilegeUiRuntimeStatus,
    runtimeStartPhase: PrivilegeUiRuntimeStartPhase,
): PrivilegeUiServiceStatusAction =
    when (runtimeStartPhase) {
        PrivilegeUiRuntimeStartPhase.RUNNING -> PrivilegeUiServiceStatusAction.CANCEL
        PrivilegeUiRuntimeStartPhase.CANCELLING -> PrivilegeUiServiceStatusAction.CANCELLING
        PrivilegeUiRuntimeStartPhase.IDLE -> when (runtimeStatus) {
            PrivilegeUiRuntimeStatus.CONNECTED -> PrivilegeUiServiceStatusAction.STOP
            PrivilegeUiRuntimeStatus.STARTING,
            PrivilegeUiRuntimeStatus.DISCONNECTED,
            PrivilegeUiRuntimeStatus.FAILED,
            -> PrivilegeUiServiceStatusAction.START
        }
    }

internal fun privilegeUiServiceStatusActionEnabled(
    action: PrivilegeUiServiceStatusAction,
    busy: Boolean,
): Boolean =
    when (action) {
        PrivilegeUiServiceStatusAction.CANCEL -> true
        PrivilegeUiServiceStatusAction.CANCELLING -> false
        PrivilegeUiServiceStatusAction.START,
        PrivilegeUiServiceStatusAction.STOP,
        -> !busy
    }

@Composable
internal fun PrivilegeUiScreenScope.StartupLogPanel() {
    val lines = state.startupLogLines
    val copiedMessage = stringResource(R.string.priv_ui_startup_log_copied)
    Panel {
        val copyLogDescription = stringResource(R.string.priv_ui_startup_log_copy_description)
        val closeLogDescription = stringResource(R.string.priv_ui_startup_log_close_description)
        val logScrollState = rememberScrollState()
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HyperText(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.priv_ui_startup_log_title),
                style = HyperTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            PrivilegeIconTooltip(
                text = copyLogDescription,
                modifier = Modifier,
            ) {
                HyperIconButton(
                    modifier = Modifier.size(PrivilegeUiSize.minimumTouchTarget),
                    enabled = interactionEnabled && lines.isNotEmpty(),
                    onClick = {
                        if (!actions.canInteract()) return@HyperIconButton
                        actions.copyStartupLog()
                        showFeedback(copiedMessage)
                    },
                ) {
                    HyperIcon(
                        modifier = Modifier.size(18.dp),
                        imageVector = PrivilegeUiIcons.ContentCopy,
                        contentDescription = copyLogDescription,
                        tint = HyperColors.secondaryText,
                    )
                }
            }
            PrivilegeIconTooltip(
                text = closeLogDescription,
                modifier = Modifier,
            ) {
                HyperIconButton(
                    modifier = Modifier.size(PrivilegeUiSize.minimumTouchTarget),
                    enabled = interactionEnabled,
                    onClick = {
                        if (!actions.canInteract()) return@HyperIconButton
                        actions.clearStartupLog()
                    },
                ) {
                    HyperIcon(
                        modifier = Modifier.size(18.dp),
                        imageVector = PrivilegeUiIcons.Close,
                        contentDescription = closeLogDescription,
                        tint = HyperColors.secondaryText,
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 96.dp, max = 480.dp)
                .background(
                    color = HyperColors.fieldContainer,
                    shape = HyperTheme.shapes.small,
                )
                .padding(PrivilegeUiSpacing.medium),
        ) {
            SelectionContainer(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(logScrollState),
            ) {
                HyperText(
                    modifier = Modifier.fillMaxWidth(),
                    text = lines.joinToString("\n"),
                    style = HyperTheme.typography.bodySmall,
                    color = HyperColors.primaryText,
                    fontFamily = FontFamily.Monospace,
                )
            }
        }
    }
}

@Composable
internal fun StatusText(text: String) {
    HyperText(
        text = text,
        style = HyperTheme.typography.labelMedium,
        color = HyperColors.secondaryText,
    )
}

@Composable
internal fun PrivilegeUiExternalStartSnapshot.externalStartStatusText(): String =
    when {
        canStart -> stringResource(R.string.priv_ui_external_ready)
        available -> stringResource(R.string.priv_ui_external_permission_required)
        else -> stringResource(R.string.priv_ui_external_unavailable)
    }

private data class StatusUi(
    val title: String,
    val detail: String,
    val background: Color,
    val foreground: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val iconDescription: String,
    val actionContainer: Color,
    val actionForeground: Color,
)

@Composable
private fun PrivilegeUiScreenState.runtimeStatusDetail(): String =
    when (runtimeStartPhase) {
        PrivilegeUiRuntimeStartPhase.CANCELLING -> stringResource(R.string.priv_ui_startup_cancelling)
        PrivilegeUiRuntimeStartPhase.RUNNING -> runtimeProgressText

            ?.takeIf(String::isNotBlank)
            ?: stringResource(R.string.priv_ui_ready)
        PrivilegeUiRuntimeStartPhase.IDLE -> when (runtimeStatus) {
            PrivilegeUiRuntimeStatus.STARTING -> runtimeProgressText

                ?.takeIf(String::isNotBlank)
                ?: stringResource(R.string.priv_ui_ready)
            PrivilegeUiRuntimeStatus.DISCONNECTED,
            PrivilegeUiRuntimeStatus.FAILED,
            PrivilegeUiRuntimeStatus.CONNECTED,
            -> stringResource(R.string.priv_ui_ready)
        }
    }

@Composable
private fun runtimeSourceText(uid: Int?): String = stringResource(runtimeSourceLabel(uid), uid ?: 0)

internal fun runtimeSourceLabel(uid: Int?): Int = when (uid) {
    0 -> R.string.priv_ui_service_source_root
    1000 -> R.string.priv_ui_service_source_system
    2000 -> R.string.priv_ui_service_source_shell
    null -> R.string.priv_ui_service_source_unknown
    else -> R.string.priv_ui_service_source_uid
}
