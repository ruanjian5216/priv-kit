package priv.kit.sample.debug

import hyper_ui.*
import priv.kit.sample.ui.*

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import priv.kit.sample.R

@Composable
internal fun SamplePageScaffold(
    title: String,
    selectedDestination: PrivilegeSampleDebugDestination,
    busy: Boolean,
    onDestinationSelected: (PrivilegeSampleDebugDestination) -> Unit,
    onBackToHome: () -> Unit,
    actions: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val colors = SampleTheme.colorScheme
    SampleScaffold(
        containerColor = colors.background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.background),
            ) {
                SampleTopBar(
                    navigationIcon = {
                        SampleTextButton(onClick = onBackToHome) {
                            HyperText(stringResource(R.string.sample_home))
                        }
                    },
                    title = {
                        HyperText(
                            text = title,
                            style = TextStyle(
                                color = colors.onBackground,
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold,
                            ),
                        )
                    },
                    actions = {
                        actions()
                    },
                )
                Column(
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp),
                ) {
                    DestinationTabs(
                        selectedDestination = selectedDestination,
                        busy = busy,
                        onDestinationSelected = onDestinationSelected,
                    )
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            content()
        }
    }
}

@Composable
private fun DestinationTabs(
    selectedDestination: PrivilegeSampleDebugDestination,
    busy: Boolean,
    onDestinationSelected: (PrivilegeSampleDebugDestination) -> Unit,
) {
    val selectedIndex = PrivilegeSampleDebugDestination.entries.indexOf(selectedDestination)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)
    LaunchedEffect(selectedIndex) {
        listState.animateScrollToItem(selectedIndex)
    }
    val colors = SampleTheme.colorScheme
    LazyRow(state = listState, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(PrivilegeSampleDebugDestination.entries, key = { it.titleRes }) { destination ->
            val selected = destination == selectedDestination
            HyperButton(
                onClick = { onDestinationSelected(destination) },
                enabled = !busy || selected,
                variant = if (selected) HyperButtonVariant.Filled else HyperButtonVariant.Tonal,
                height = 48.dp,
                contentPadding = PaddingValues(horizontal = 16.dp),
                role = Role.Tab,
            ) {
                HyperText(
                    text = stringResource(destination.titleRes),
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
internal fun SampleTopBarAction(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    HyperButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.widthIn(min = 104.dp),
        height = 40.dp,
        variant = HyperButtonVariant.Filled,
        role = Role.Button,
    ) {
        HyperText(
            text = label,
        )
    }
}

@Composable
internal fun SectionTitle(text: String) {
    val colors = SampleTheme.colorScheme
    HyperText(
        text = text,
        style = TextStyle(
            color = colors.onSurface,
            fontFamily = FontFamily.SansSerif,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
        ),
    )
}

@Composable
internal fun DiagnosticBlock(text: String) {
    val colors = SampleTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surfaceContainerHighest)
            .padding(16.dp),
    ) {
        SelectionContainer {
            HyperText(
                text = text,
                style = TextStyle(
                    color = colors.onSurface,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                ),
            )
        }
    }
}

@Composable
internal fun StatusPanel(
    state: PrivilegeSampleScreenState,
    onStopServer: () -> Unit,
) {
    val colors = SampleTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surfaceContainerLow)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatusPill(state.status, state.busy)
            Spacer(Modifier.width(12.dp))
            HyperText(
                modifier = Modifier.weight(1f),
                text = state.message,
                style = TextStyle(
                    color = colors.onSurfaceVariant,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 13.sp,
                    textAlign = TextAlign.End,
                ),
            )
        }
        RuntimeInfoRow(label = "uid", value = state.serverInfo?.uid?.toString() ?: "-")
        RuntimeInfoRow(label = "pid", value = state.serverInfo?.pid?.toString() ?: "-")
        RuntimeInfoRow(label = stringResource(R.string.sample_protocol), value = state.serverInfo?.protocolVersion?.toString() ?: "-")
        SampleAction(
            label = stringResource(R.string.sample_stop_server),
            enabled = !state.busy && state.status == PrivilegeSampleStatus.CONNECTED,
            tone = SampleActionTone.Destructive,
            modifier = Modifier,
            onClick = onStopServer,
        )
    }
}

@Composable
internal fun SampleField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    enabled: Boolean = true,
) {
    val colors = SampleTheme.colorScheme
    val foreground = if (enabled) colors.onSurface else colors.onSurface.copy(alpha = 0.38f)
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        HyperText(
            text = label,
            style = TextStyle(
                color = colors.onSurfaceVariant,
                fontFamily = FontFamily.SansSerif,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            ),
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = true,
            keyboardOptions = keyboardOptions,
            textStyle = TextStyle(
                color = foreground,
                fontFamily = FontFamily.Monospace,
                fontSize = 15.sp,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colors.surfaceContainerHighest)
                .padding(horizontal = 12.dp, vertical = 12.dp),
            cursorBrush = SolidColor(colors.primary),
        )
    }
}

internal enum class SampleActionTone {
    Primary,
    Secondary,
    Tonal,
    Destructive,
    Neutral,
}

@Composable
internal fun SampleAction(
    label: String,
    enabled: Boolean,
    tone: SampleActionTone,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val variant = when (tone) {
        SampleActionTone.Primary -> HyperButtonVariant.Filled
        SampleActionTone.Secondary -> HyperButtonVariant.Outline
        SampleActionTone.Tonal -> HyperButtonVariant.Tonal
        SampleActionTone.Destructive -> HyperButtonVariant.Danger
        SampleActionTone.Neutral -> HyperButtonVariant.Ghost
    }
    HyperButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        height = 48.dp,
        variant = variant,
        role = Role.Button,
    ) {
        HyperText(
            text = label,
        )
    }
}

@Composable
private fun StatusPill(
    status: PrivilegeSampleStatus,
    busy: Boolean,
) {
    val colors = SampleTheme.colorScheme
    val text = when {
        busy -> stringResource(R.string.sample_busy)
        status == PrivilegeSampleStatus.CONNECTED -> stringResource(R.string.sample_connected)
        status == PrivilegeSampleStatus.STARTING -> stringResource(R.string.sample_starting)
        else -> stringResource(R.string.sample_disconnected)
    }
    val background = when {
        busy -> colors.primaryContainer
        status == PrivilegeSampleStatus.CONNECTED -> colors.tertiaryContainer
        else -> colors.surfaceContainerHigh
    }
    val foreground = when {
        busy -> colors.onPrimaryContainer
        status == PrivilegeSampleStatus.CONNECTED -> colors.onTertiaryContainer
        else -> colors.onSurfaceVariant
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(
            modifier = Modifier
                .width(8.dp)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(foreground),
        )
        Spacer(modifier = Modifier.width(8.dp))
        HyperText(
            text = text,
            style = TextStyle(
                color = foreground,
                fontFamily = FontFamily.SansSerif,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}

@Composable
internal fun RuntimeInfoRow(
    label: String,
    value: String,
) {
    val colors = SampleTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HyperText(
            text = label,
            style = TextStyle(
                color = colors.onSurfaceVariant,
                fontFamily = FontFamily.SansSerif,
                fontSize = 14.sp,
            ),
        )
        Spacer(modifier = Modifier.width(16.dp))
        HyperText(
            text = value,
            style = TextStyle(
                color = colors.onSurface,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}
