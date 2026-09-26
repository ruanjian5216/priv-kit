package priv.kit.sample.command

import hyper_ui.*
import priv.kit.sample.ui.*

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import priv.kit.sample.R

@Composable
internal fun PrivilegeSampleCommandPage(
    serverRunning: Boolean,
    viewModel: PrivilegeSampleCommandViewModel,
    onBackToHome: () -> Unit,
) {
    LaunchedEffect(serverRunning) {
        if (!serverRunning) viewModel.onServerDisconnected()
    }
    val state = viewModel.state
    val canStart = serverRunning && !state.isRunning

    SampleScaffold(
        containerColor = SampleTheme.colorScheme.background,
        topBar = {
            SampleTopBar(
                navigationIcon = {
                    SampleTextButton(onClick = onBackToHome) {
                        HyperText(stringResource(R.string.sample_home))
                    }
                },
                title = {
                    HyperText(
                        text = stringResource(R.string.sample_test_command_api),
                        fontWeight = FontWeight.SemiBold,
                    )
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HyperText(
                text = if (serverRunning) stringResource(R.string.sample_server_connected) else stringResource(R.string.sample_server_disconnected),
                color = if (serverRunning) {
                    SampleTheme.colorScheme.tertiary
                } else {
                    SampleTheme.colorScheme.error
                },
                style = SampleTheme.typography.titleMedium,
            )
            HyperText(
                text = stringResource(R.string.sample_command_description),
                style = SampleTheme.typography.bodyMedium,
                color = SampleTheme.colorScheme.onSurfaceVariant,
            )
            SampleOutlinedTextField(
                value = state.commandText,
                onValueChange = viewModel::updateCommand,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isRunning,
                minLines = 4,
                label = { HyperText(stringResource(R.string.sample_shell_command)) },
                textStyle = SampleTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                ),
                supportingText = state.inputError?.let { error ->
                    { HyperText(error) }
                },
                isError = state.inputError != null,
            )
            SampleOutlinedTextField(
                value = state.timeoutText,
                onValueChange = viewModel::updateTimeout,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isRunning,
                singleLine = true,
                label = { HyperText(stringResource(R.string.sample_timeout_milliseconds_0_none)) },
            )
            HyperButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = canStart,
                onClick = viewModel::runStreaming,
            ) {
                HyperText(stringResource(R.string.sample_run_with_streaming_output))
            }
            HyperButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = canStart,
                onClick = viewModel::runForResult,
            ) {
                HyperText(stringResource(R.string.sample_run_and_wait_for_result))
            }
            HyperButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = state.isRunning,
                onClick = { viewModel.cancel() },
            ) {
                HyperText(stringResource(R.string.sample_cancel))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                HyperText(
                    text = state.status,
                    style = SampleTheme.typography.titleSmall,
                )
                SampleTextButton(
                    enabled = !state.isRunning,
                    onClick = viewModel::clearOutput,
                ) {
                    HyperText(stringResource(R.string.sample_clear_output))
                }
            }
            CommandOutput(
                title = "stdout",
                text = state.stdout,
                truncated = state.stdoutTruncated,
            )
            CommandOutput(
                title = "stderr",
                text = state.stderr,
                truncated = state.stderrTruncated,
            )
        }
    }
}

@Composable
private fun CommandOutput(
    title: String,
    text: String,
    truncated: Boolean,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        HyperText(
            text = if (truncated) stringResource(R.string.sample_output_truncated, title) else title,
            style = SampleTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
        SelectionContainer {
            HyperText(
                text = text.ifEmpty { stringResource(R.string.sample_empty) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = SampleTheme.colorScheme.surfaceContainerHighest,
                        shape = RoundedCornerShape(8.dp),
                    )
                    .padding(16.dp),
                fontFamily = FontFamily.Monospace,
                style = SampleTheme.typography.bodySmall,
            )
        }
    }
}
