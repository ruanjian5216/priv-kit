package priv.kit.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import hyper_ui.HyperColors
import hyper_ui.HyperTheme
import hyper_ui.HyperThemeConfig
import priv.kit.ui.component.AdbPanel
import priv.kit.ui.component.PermissionRestrictionWarning
import priv.kit.ui.component.AuthorizationModeTabs
import priv.kit.ui.component.AutoRecoveryWarning
import priv.kit.ui.component.ExternalStartPanel
import priv.kit.ui.component.ManualShellPanel
import priv.kit.ui.component.PrivilegeUiSpacing
import priv.kit.ui.component.RootPanel
import priv.kit.ui.component.RestartConfirmationDialog
import priv.kit.ui.component.ServiceStatusPanel
import priv.kit.ui.component.StartupLogPanel
import priv.kit.ui.component.privilegeUiAutoRecoveryWarningVisible
import priv.kit.ui.component.privilegeUiPermissionRestrictionWarningVisible

@Composable
internal fun PrivilegeScaffoldContent(
    screenScope: PrivilegeUiScreenScope,
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    containerColor: Color = HyperColors.pageBackground,
    contentColor: Color = HyperColors.primaryText,
    feedbackMessage: String? = null,
) {
    val state = screenScope.state
    val interactionEnabled = screenScope.interactionEnabled
    screenScope.RestartConfirmationDialog()
    HyperThemeConfig(darkTheme = isSystemInDarkTheme()) {
      androidx.compose.runtime.CompositionLocalProvider(
          hyper_ui.LocalHyperContentColor provides contentColor,
      ) {
        Box(modifier = modifier.fillMaxSize().background(containerColor)) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier.windowInsetsPadding(
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Top),
                    ),
                ) {
                    topBar()
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(
                            start = PrivilegeUiSpacing.large,
                            top = PrivilegeUiSpacing.medium,
                            end = PrivilegeUiSpacing.large,
                            bottom = PrivilegeUiSpacing.extraLarge,
                        ),
                    verticalArrangement = Arrangement.spacedBy(PrivilegeUiSpacing.large),
                ) {
                    Column {
                        AnimatedVisibility(
                            visible = privilegeUiAutoRecoveryWarningVisible(
                                state = state,
                                interactionEnabled = interactionEnabled,
                            ),
                        ) {
                            Column {
                                screenScope.AutoRecoveryWarning()
                                Spacer(Modifier.height(PrivilegeUiSpacing.large))
                            }
                        }
                        screenScope.ServiceStatusPanel()
                        AnimatedVisibility(
                            visible = privilegeUiPermissionRestrictionWarningVisible(
                                runtimeStatus = state.runtimeStatus,
                                restrictionStatus = state.permissionRestrictionStatus,
                            ),
                            enter = expandVertically(expandFrom = Alignment.Top),
                            exit = shrinkVertically(shrinkTowards = Alignment.Top),
                        ) {
                            Column {
                                Spacer(Modifier.height(PrivilegeUiSpacing.large))
                                screenScope.PermissionRestrictionWarning()
                            }
                        }
                    }
                    screenScope.AuthorizationModeTabs()
                    screenScope.AuthorizationModePanel()
                    if (state.startupLogLines.isNotEmpty()) {
                        screenScope.StartupLogPanel()
                    }
                }
            }
            if (feedbackMessage != null) {
                HyperFeedbackBanner(
                    message = feedbackMessage,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .zIndex(2f),
                )
            }
        }
      }
    }
}

@Composable
private fun HyperFeedbackBanner(message: String, modifier: Modifier = Modifier) {
    hyper_ui.HyperCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = PrivilegeUiSpacing.large, vertical = PrivilegeUiSpacing.medium),
        colors = hyper_ui.HyperCardColors(containerColor = HyperColors.primaryText),
        shape = HyperTheme.shapes.medium,
    ) {
        hyper_ui.HyperText(message, color = HyperColors.pageBackground)
    }
}
internal fun privilegeUiAutoRecoveryWarningVisible(
    state: PrivilegeUiScreenState,
    interactionEnabled: Boolean,
): Boolean = interactionEnabled &&
    privilegeUiAutoRecoveryWarningVisible(
        desiredEnabled = state.desiredEnabled,
        runtimeStatus = state.runtimeStatus,
        runtimeStartPhase = state.runtimeStartPhase,
    )

internal class PrivilegeUiScreenScope(
    val state: PrivilegeUiScreenState,
    val actions: PrivilegeUiActions,
    val interactionEnabled: Boolean,
    val showFeedback: (String) -> Unit,
    val onViewPermissionSolutions: (() -> Unit)? = null,
)

@Composable
private fun PrivilegeUiScreenScope.AuthorizationModePanel() {
    val mode = state.selectedStartupMode.takeIf { it in state.startupModes }
        ?: state.startupModes.first()
    when (mode) {
        PrivilegeUiStartupMode.ROOT -> RootPanel()
        PrivilegeUiStartupMode.MANUAL_SHELL -> ManualShellPanel()
        PrivilegeUiStartupMode.ADB -> AdbPanel()
        PrivilegeUiStartupMode.EXTERNAL -> ExternalStartPanel()
    }
}
