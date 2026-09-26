package priv.kit.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import hyper_ui.HyperButton
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import hyper_ui.HyperAlertDialog
import hyper_ui.HyperText
import androidx.compose.ui.res.stringResource
import priv.kit.ui.component.PrivilegeTopBar

/**
 * Displays the shared page with an in-memory simulation; no runtime is created.
 * [adbRestricted] controls permission restrictions for simulated Shell connections.
 * [batteryOptimizationExempt] and [localNetworkPermissionGranted] control simulated permission cards.
 * [onPermissionsChanged] reports successful simulated requests to the host controls.
 * [onViewPermissionSolutions] overrides opening the localized permission troubleshooting page.
 */
@Composable
@Suppress("DEPRECATION")
public fun PrivilegePreviewScaffold(
    modifier: Modifier = Modifier,
    adbRestricted: Boolean = true,
    batteryOptimizationExempt: Boolean = true,
    localNetworkPermissionGranted: Boolean = true,
    onPermissionsChanged: ((batteryOptimizationExempt: Boolean, localNetworkPermissionGranted: Boolean) -> Unit)? = null,
    onViewPermissionSolutions: (() -> Unit)? = null,
) {
    var batteryExempt by remember(batteryOptimizationExempt) { mutableStateOf(batteryOptimizationExempt) }
    var networkGranted by remember(localNetworkPermissionGranted) { mutableStateOf(localNetworkPermissionGranted) }
    val permissionsChanged by rememberUpdatedState(onPermissionsChanged)
    val grantBatteryExemption by rememberUpdatedState<() -> Unit>({
        batteryExempt = true
        permissionsChanged?.invoke(true, networkGranted)
    })
    val grantNetworkPermission by rememberUpdatedState<() -> Unit>({
        networkGranted = true
        permissionsChanged?.invoke(batteryExempt, true)
    })
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboardManager.current
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    val externalLabel = stringResource(R.string.priv_ui_simulation_external)
    val startingText = mapOf(
        PrivilegeUiRuntimeStartSource.ROOT to stringResource(R.string.priv_ui_starting_root),
        PrivilegeUiRuntimeStartSource.ADB_WIRELESS to stringResource(R.string.priv_ui_wireless_adb_starting),
        PrivilegeUiRuntimeStartSource.ADB_STATIC_TCP to stringResource(R.string.priv_ui_tcp_starting),
        PrivilegeUiRuntimeStartSource.EXTERNAL to stringResource(R.string.priv_ui_external_starting),
    )
    val pairingText = stringResource(R.string.priv_ui_pairing_service_found_text)
    val simulation = remember(externalLabel, startingText, pairingText) {
        PrivilegeUiSimulation(scope, externalLabel, startingText, pairingText,
            requestBatteryOptimization = { grantBatteryExemption() },
            requestLocalNetworkPermission = { grantNetworkPermission() },
        ) {
            clipboard.setText(AnnotatedString(it))
        }
    }
    SideEffect {
        simulation.setAdbRestricted(adbRestricted)
        simulation.setPermissions(batteryExempt, networkGranted)
    }
    if (simulation.externalAuthorizationRequested) {
        HyperAlertDialog(
            visible = true,
            onDismissRequest = simulation::cancelOperation,
            title = stringResource(R.string.priv_ui_external_authorize_start),
            bodyContent = {
                HyperText(stringResource(R.string.priv_ui_simulation_external_confirmation))
            },
            actionContent = {
                HyperButton(onClick = simulation::confirmExternalAuthorization) {
                    HyperText(stringResource(R.string.priv_ui_ok))
                }
                HyperButton(onClick = simulation::cancelOperation) {
                    HyperText(stringResource(R.string.priv_ui_pairing_cancel_action))
                }
            },
        )
    }
    PrivilegeScaffoldContent(
        modifier = modifier,
        screenScope = PrivilegeUiScreenScope(
            state = simulation.state,
            actions = simulation.actions,
            interactionEnabled = true,
            showFeedback = { feedbackMessage = it },
            onViewPermissionSolutions = onViewPermissionSolutions,
        ),
        topBar = { PrivilegeTopBar(onBack = {}, backEnabled = false) },
        feedbackMessage = feedbackMessage,
    )
}
