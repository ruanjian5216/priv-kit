package priv.kit.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import hyper_ui.HyperButton
import hyper_ui.HyperColors
import hyper_ui.HyperText
import hyper_ui.HyperTheme
import priv.kit.ui.PrivilegeUiRuntimeStartSource
import priv.kit.ui.PrivilegeUiScreenScope
import priv.kit.ui.R

@Composable
internal fun PrivilegeUiScreenScope.RootPanel() {
    Panel {
        val action = state.startActionFor(
            source = PrivilegeUiRuntimeStartSource.ROOT,
            providerId = null,
        )
        HyperButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = state.startActionEnabled(
                action = action,
                startAvailable = interactionEnabled,
            ),
            onClick = {
                when (action) {
                    PrivilegeUiStartAction.START -> actions.startRoot()
                    PrivilegeUiStartAction.CANCEL -> actions.stopCurrentStart()
                    PrivilegeUiStartAction.CANCELLING,
                    PrivilegeUiStartAction.NONE,
                    -> Unit
                }
            },
        ) {
            HyperText(
                stringResource(
                    privilegeUiStartActionLabel(
                        action = action,
                        startLabel = R.string.priv_ui_root_authorization_action,
                    ),
                ),
            )
        }
    }
}

@Composable
internal fun PrivilegeUiScreenScope.ManualShellPanel() {
    val copiedMessage = stringResource(R.string.priv_ui_manual_command_copied)
    Panel {
        HyperText(
            text = stringResource(R.string.priv_ui_manual_authorization_desc),
            style = HyperTheme.typography.bodyMedium,
            color = HyperColors.secondaryText,
        )
        val commandLine = state.manualShellCommandLine
        if (commandLine == null) {
            HyperText(
                text = stringResource(R.string.priv_ui_manual_command_unavailable),
                style = HyperTheme.typography.bodyMedium,
                color = HyperColors.secondaryText,
            )
        } else {
            CommandBlock(commandLine)
            HyperButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = interactionEnabled && !state.busy,
                onClick = {
                    if (!actions.canInteract()) return@HyperButton
                    actions.copyManualCommand()
                    showFeedback(copiedMessage)
                },
            ) {
                HyperText(stringResource(R.string.priv_ui_manual_copy_command))
            }
        }
    }
}
