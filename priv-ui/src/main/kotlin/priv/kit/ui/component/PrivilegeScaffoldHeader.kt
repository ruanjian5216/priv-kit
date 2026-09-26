package priv.kit.ui.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import hyper_ui.HyperButton
import hyper_ui.HyperButtonTone
import hyper_ui.HyperIcon
import hyper_ui.HyperIconButton
import hyper_ui.HyperText
import hyper_ui.HyperTheme
import priv.kit.ui.PrivilegeUiScreenScope
import priv.kit.ui.PrivilegeUiStartupMode
import priv.kit.ui.R

@Composable
internal fun PrivilegeTopBar(onBack: () -> Unit, backEnabled: Boolean = true) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val backDescription = stringResource(R.string.priv_ui_nav_back)
        PrivilegeIconTooltip(text = backDescription, modifier = Modifier) {
            HyperIconButton(enabled = backEnabled, onClick = onBack) {
                HyperIcon(
                    imageVector = PrivilegeUiIcons.ArrowBack,
                    contentDescription = backDescription,
                )
            }
        }
        HyperText(
            text = stringResource(R.string.priv_ui_title),
            modifier = Modifier.weight(1f),
            style = HyperTheme.typography.titleLarge,
        )
    }
}

@Composable
internal fun PrivilegeUiScreenScope.AuthorizationModeTabs() {
    val items = privilegeUiAuthorizationModeItems(
        modes = state.startupModes,
        selectedMode = state.selectedStartupMode,
        busy = state.busy,
        interactionEnabled = interactionEnabled,
    )
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items.forEach { item ->
            HyperButton(
                enabled = item.enabled,
                onClick = { actions.selectStartupMode(item.mode) },
                tone = if (item.selected) HyperButtonTone.Primary else HyperButtonTone.Plain,
            ) {
                HyperText(
                    text = stringResource(item.mode.labelRes()),
                    maxLines = 1,
                )
            }
        }
    }
}

internal data class PrivilegeUiAuthorizationModeItem(
    val mode: PrivilegeUiStartupMode,
    val selected: Boolean,
    val enabled: Boolean,
)

internal fun privilegeUiAuthorizationModeItems(
    modes: List<PrivilegeUiStartupMode>,
    selectedMode: PrivilegeUiStartupMode,
    busy: Boolean,
    interactionEnabled: Boolean,
): List<PrivilegeUiAuthorizationModeItem> {
    val resolvedMode = selectedMode.takeIf { it in modes } ?: modes.firstOrNull()
    return modes.map { mode ->
        PrivilegeUiAuthorizationModeItem(
            mode = mode,
            selected = mode == resolvedMode,
            enabled = interactionEnabled && (!busy || mode == resolvedMode),
        )
    }
}

internal fun PrivilegeUiStartupMode.labelRes(): Int =
    when (this) {
        PrivilegeUiStartupMode.ROOT -> R.string.priv_ui_auth_method_root
        PrivilegeUiStartupMode.MANUAL_SHELL -> R.string.priv_ui_auth_method_manual_shell
        PrivilegeUiStartupMode.ADB -> R.string.priv_ui_auth_method_adb
        PrivilegeUiStartupMode.EXTERNAL -> R.string.priv_ui_auth_method_external
    }
