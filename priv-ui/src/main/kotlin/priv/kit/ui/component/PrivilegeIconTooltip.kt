package priv.kit.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import hyper_ui.HyperTooltip

@Composable
internal fun PrivilegeIconTooltip(
    text: String,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    HyperTooltip(
        message = text,
        modifier = modifier,
        child = content,
    )
}
