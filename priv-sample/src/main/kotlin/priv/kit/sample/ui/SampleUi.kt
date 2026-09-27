package priv.kit.sample.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import hyper_ui.HyperButton
import hyper_ui.HyperButtonVariant
import hyper_ui.HyperColors
import hyper_ui.HyperIconButton
import hyper_ui.HyperPanel
import hyper_ui.HyperPanelColors
import hyper_ui.HyperText
import hyper_ui.HyperTextField
import hyper_ui.HyperTheme

object SampleTheme {
    val colorScheme: SampleColors
        @Composable get() = SampleColors
    val typography: hyper_ui.HyperTypography
        @Composable get() = HyperTheme.typography
}

object SampleColors {
    val background: Color @Composable get() = HyperColors.pageBackground
    val primary: Color @Composable get() = HyperColors.accent
    val primaryContainer: Color @Composable get() = HyperColors.accentContainer
    val onPrimaryContainer: Color @Composable get() = HyperColors.primaryText
    val secondary: Color @Composable get() = HyperColors.secondaryText
    val onSecondary: Color @Composable get() = HyperColors.pageBackground
    val secondaryContainer: Color @Composable get() = HyperColors.softContainer
    val onSecondaryContainer: Color @Composable get() = HyperColors.primaryText
    val tertiary: Color @Composable get() = HyperColors.accent
    val error: Color @Composable get() = HyperColors.danger
    val errorContainer: Color @Composable get() = HyperColors.danger.copy(alpha = 0.16f)
    val onErrorContainer: Color @Composable get() = HyperColors.danger
    val onSurfaceVariant: Color @Composable get() = HyperColors.secondaryText
    val onSurface: Color @Composable get() = HyperColors.primaryText
    val onBackground: Color @Composable get() = HyperColors.primaryText
    val onPrimary: Color @Composable get() = HyperColors.pageBackground
    val onError: Color @Composable get() = HyperColors.pageBackground
    val surfaceContainerLow: Color @Composable get() = HyperColors.softContainer
    val surfaceContainerHighest: Color @Composable get() = HyperColors.fieldContainer
    val surfaceContainerHigh: Color @Composable get() = HyperColors.softContainer
    val tertiaryContainer: Color @Composable get() = HyperColors.accentContainer
    val onTertiaryContainer: Color @Composable get() = HyperColors.primaryText
}

@Composable
fun SampleScaffold(
    modifier: Modifier = Modifier,
    containerColor: Color = HyperColors.pageBackground,
    topBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Box(modifier.fillMaxSize().background(containerColor)) {
        Column(Modifier.fillMaxSize()) {
            Box(Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))) {
                topBar()
            }
            Box(Modifier.weight(1f).fillMaxWidth()) { content(PaddingValues(0.dp)) }
        }
        snackbarHost()
    }
}

class SampleSnackbarHostState {
    suspend fun showSnackbar(message: String) = Unit
}

@Composable
fun SampleSnackbarHost(state: SampleSnackbarHostState) = Unit

@Composable
fun SampleTopBar(
    title: @Composable () -> Unit,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        navigationIcon()
        Box(Modifier.weight(1f)) { title() }
        Row(content = actions)
    }
}

@Composable
fun SampleTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) = HyperButton(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    variant = HyperButtonVariant.Ghost,
    content = content,
)

@Composable
fun SampleOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    singleLine: Boolean = false,
    textStyle: androidx.compose.ui.text.TextStyle = HyperTheme.typography.bodyMedium,
    minLines: Int = 1,
    supportingText: (@Composable () -> Unit)? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    visualTransformation: VisualTransformation = VisualTransformation.None,
) = HyperTextField(
    value = value,
    onValueChange = onValueChange,
    modifier = modifier,
    enabled = enabled,
    readOnly = readOnly,
    labelContent = label?.let { { it() } },
    placeholderContent = placeholder,
    singleLine = singleLine,
    minLines = minLines,
    supportingContent = supportingText?.let { { it() } },
    isError = isError,
    keyboardOptions = keyboardOptions,
    keyboardActions = keyboardActions,
    visualTransformation = visualTransformation,
    textStyle = textStyle,
)

@Composable
fun SampleCard(
    modifier: Modifier = Modifier,
    headlineContent: (@Composable () -> Unit)? = null,
    supportingContent: (@Composable () -> Unit)? = null,
    leadingContent: (@Composable () -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
) {
    HyperPanel(
        modifier = modifier,
        colors = HyperPanelColors(containerColor = HyperColors.cardContainer),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            leadingContent?.invoke()
            Column(Modifier.weight(1f)) {
                headlineContent?.invoke()
                supportingContent?.invoke()
                content?.invoke()
            }
        }
    }
}
