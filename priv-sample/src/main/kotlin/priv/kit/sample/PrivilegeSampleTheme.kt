package priv.kit.sample

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import hyper_ui.HyperColors
import hyper_ui.HyperThemeConfig

@Composable
internal fun PrivilegeSampleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val view = LocalView.current
    val background = HyperColors.pageBackground
    if (!view.isInEditMode) {
        SideEffect {
            context.findActivity()?.let { activity ->
                WindowCompat.setDecorFitsSystemWindows(activity.window, false)
                activity.window.statusBarColor = background.toArgb()
                activity.window.navigationBarColor = background.toArgb()
                WindowCompat.getInsetsController(activity.window, view).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
        }
    }
    HyperThemeConfig(darkTheme = darkTheme, content = content)
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
