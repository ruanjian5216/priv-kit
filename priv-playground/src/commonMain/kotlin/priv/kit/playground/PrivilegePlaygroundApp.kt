package priv.kit.playground

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import priv.kit.ui.PrivilegePreviewScaffold

@Composable
internal fun PrivilegePlaygroundApp(
    dark: Boolean = isSystemInDarkTheme(),
    useLegacyPackaging: Boolean = true,
    adbRestricted: Boolean = true,
    batteryOptimizationExempt: Boolean = true,
    localNetworkPermissionGranted: Boolean = true,
    onPermissionsChanged: ((Boolean, Boolean) -> Unit)? = null,
) {
    PrivilegePreviewScaffold(
        useLegacyPackaging = useLegacyPackaging,
        adbRestricted = adbRestricted,
        batteryOptimizationExempt = batteryOptimizationExempt,
        localNetworkPermissionGranted = localNetworkPermissionGranted,
        onPermissionsChanged = onPermissionsChanged,
    )
}
