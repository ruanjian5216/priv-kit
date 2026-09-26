package priv.kit.sample.home

import hyper_ui.*
import priv.kit.sample.ui.*

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import priv.kit.sample.R

@Composable
internal fun PrivilegeSampleHomePage(
    serverRunning: Boolean,
    onOpenPrivilegeUi: () -> Unit,
    onOpenDebug: () -> Unit,
    onOpenDeviceFiles: () -> Unit,
    onOpenFileApi: () -> Unit,
    onOpenCommandApi: () -> Unit,
) {
    val colors = SampleTheme.colorScheme
    SampleScaffold(
        containerColor = colors.background,
        topBar = {
            SampleTopBar(
                title = {
                    HyperText(
                        text = "Priv Kit",
                        style = SampleTheme.typography.headlineSmall,
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
            ServerStatusRow(running = serverRunning)
            HyperText(
                modifier = Modifier.padding(top = 4.dp),
                text = stringResource(R.string.sample_choose_the_surface_you_want_to_inspect),
                style = SampleTheme.typography.bodyLarge,
                color = colors.onSurfaceVariant,
            )
            HyperButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onOpenPrivilegeUi,
            ) {
                HyperText(stringResource(R.string.sample_open_privilege_ui))
            }
            HyperButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onOpenDebug,
            ) {
                HyperText(stringResource(R.string.sample_open_debug_tools))
            }
            HyperButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onOpenFileApi,
            ) {
                HyperText(stringResource(R.string.sample_test_file_api))
            }
            HyperButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onOpenDeviceFiles,
            ) {
                HyperText(stringResource(R.string.sample_browse_device_files))
            }
            HyperButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onOpenCommandApi,
            ) {
                HyperText(stringResource(R.string.sample_test_command_api))
            }
        }
    }
}

@Composable
private fun ServerStatusRow(running: Boolean) {
    val colors = SampleTheme.colorScheme
    val statusColor = if (running) colors.tertiary else colors.onSurfaceVariant
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = colors.surfaceContainerLow,
                shape = RoundedCornerShape(12.dp),
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HyperText(
            text = stringResource(R.string.sample_server_status),
            style = SampleTheme.typography.titleMedium,
            color = colors.onSurface,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(
                modifier = Modifier
                    .width(8.dp)
                    .height(8.dp)
                    .background(statusColor, CircleShape),
            )
            Spacer(modifier = Modifier.width(8.dp))
            HyperText(
                text = if (running) stringResource(R.string.sample_running) else stringResource(R.string.sample_stopped),
                style = SampleTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = statusColor,
            )
        }
    }
}
