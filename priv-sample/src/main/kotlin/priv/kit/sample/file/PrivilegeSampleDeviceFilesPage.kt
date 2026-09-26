package priv.kit.sample.file

import hyper_ui.*
import priv.kit.sample.ui.*

import android.content.Context
import android.text.format.Formatter
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import priv.kit.core.file.PrivilegeFileEntry
import priv.kit.core.file.PrivilegeFileMetadata
import priv.kit.core.file.PrivilegeFileType
import priv.kit.sample.R

@Composable
internal fun PrivilegeSampleDeviceFilesPage(
    serverRunning: Boolean,
    viewModel: PrivilegeSampleDeviceFilesViewModel,
    onBackToHome: () -> Unit,
) {
    val state = viewModel.state
    val preview = state.preview
    val directoryControls = state.deviceDirectoryControls()
    val snackbarHostState = remember { SampleSnackbarHostState() }

    LaunchedEffect(serverRunning) {
        viewModel.setServerRunning(serverRunning)
    }
    LaunchedEffect(state.notice) {
        val notice = state.notice ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(notice)
        viewModel.consumeNotice()
    }
    BackHandler {
        if (preview != null) {
            viewModel.closePreview()
        } else if (!viewModel.navigateBack()) {
            onBackToHome()
        }
    }

    SampleScaffold(
        containerColor = SampleTheme.colorScheme.background,
        snackbarHost = { SampleSnackbarHost(snackbarHostState) },
        topBar = {
            SampleTopBar(
                navigationIcon = {
                    SampleTextButton(
                        onClick = if (preview == null) onBackToHome else viewModel::closePreview,
                    ) {
                        HyperText(if (preview == null) stringResource(R.string.sample_home) else stringResource(R.string.sample_files))
                    }
                },
                title = {
                    HyperText(
                        text = preview?.name ?: stringResource(R.string.sample_device_files),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                actions = {
                    if (preview == null) {
                        SampleTextButton(
                            enabled = directoryControls.enabled,
                            onClick = viewModel::refreshDirectory,
                        ) {
                            HyperText(stringResource(R.string.sample_refresh))
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        if (preview == null) {
            DeviceDirectoryContent(
                state = state,
                controls = directoryControls,
                innerPadding = innerPadding,
                onDirectoryTextChanged = viewModel::updateDirectoryText,
                onSubmitDirectory = viewModel::submitDirectory,
                onOpenParent = viewModel::openParentDirectory,
                onOpenEntry = viewModel::openEntry,
                onRetry = viewModel::refreshDirectory,
            )
        } else {
            DeviceFilePreviewContent(
                preview = preview,
                innerPadding = innerPadding,
                onSelectMode = viewModel::selectPreviewMode,
                onRetry = viewModel::retryPreview,
            )
        }
    }
}

@Composable
private fun DeviceDirectoryContent(
    state: PrivilegeSampleDeviceFilesState,
    controls: DeviceDirectoryControls,
    innerPadding: PaddingValues,
    onDirectoryTextChanged: (String) -> Unit,
    onSubmitDirectory: () -> Unit,
    onOpenParent: () -> Unit,
    onOpenEntry: (PrivilegeFileEntry) -> Unit,
    onRetry: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp),
    ) {
        HyperText(
            modifier = Modifier.padding(top = 8.dp),
            text = if (state.serverRunning) stringResource(R.string.sample_server_connected) else stringResource(R.string.sample_server_disconnected),
            color = if (state.serverRunning) {
                SampleTheme.colorScheme.tertiary
            } else {
                SampleTheme.colorScheme.error
            },
            style = SampleTheme.typography.labelLarge,
        )
        SampleOutlinedTextField(
            value = state.directoryText,
            onValueChange = onDirectoryTextChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp),
            enabled = controls.enabled,
            readOnly = controls.directoryReadOnly,
            singleLine = true,
            isError = state.pathError != null,
            label = { HyperText(stringResource(R.string.sample_current_directory)) },
            supportingText = state.pathError?.let { message ->
                { HyperText(message) }
            },
            textStyle = SampleTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
            keyboardActions = KeyboardActions(
                onGo = {
                    keyboardController?.hide()
                    onSubmitDirectory()
                },
            ),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            when {
                !state.serverRunning -> DeviceFilesStatusPanel(
                    title = stringResource(R.string.sample_privileged_server_required),
                    message = stringResource(R.string.sample_connect_a_privileged_server_before_browsing_device_files),
                    modifier = Modifier.fillMaxSize(),
                )

                state.hasDirectorySnapshot -> key(state.currentDirectory) {
                    DeviceDirectoryList(
                        state = state,
                        modifier = Modifier.fillMaxSize(),
                        interactionsEnabled = !state.isLoadingDirectory,
                        onOpenParent = onOpenParent,
                        onOpenEntry = onOpenEntry,
                    )
                }

                state.directoryError != null -> DeviceFilesStatusPanel(
                    title = stringResource(R.string.sample_unable_to_read_directory),
                    message = state.directoryError,
                    modifier = Modifier.fillMaxSize(),
                    actionLabel = stringResource(R.string.sample_retry),
                    onAction = onRetry,
                )

                else -> Unit
            }

            if (state.serverRunning && state.isLoadingDirectory) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    hyper_ui.HyperCircularProgressIndicator(progress = null)
                }
            }
        }
    }
}

internal data class DeviceDirectoryControls(
    val enabled: Boolean,
    val directoryReadOnly: Boolean,
)

internal fun PrivilegeSampleDeviceFilesState.deviceDirectoryControls(): DeviceDirectoryControls =
    DeviceDirectoryControls(
        enabled = serverRunning,
        directoryReadOnly = isLoadingDirectory,
    )

@Composable
private fun DeviceDirectoryList(
    state: PrivilegeSampleDeviceFilesState,
    modifier: Modifier,
    interactionsEnabled: Boolean,
    onOpenParent: () -> Unit,
    onOpenEntry: (PrivilegeFileEntry) -> Unit,
) {
    val context = LocalContext.current
    val dateFormat = remember {
        SimpleDateFormat(DIRECTORY_ENTRY_DATE_PATTERN, Locale.ROOT)
    }
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        item(key = PARENT_ENTRY_KEY, contentType = PrivilegeFileType.DIRECTORY) {
            DeviceFileRow(
                name = "..",
                details = DeviceFileDetails(text = stringResource(R.string.sample_parent_directory)),
                type = PrivilegeFileType.DIRECTORY,
                enabled = interactionsEnabled && state.currentDirectory != ROOT_DIRECTORY,
                dimmed = state.currentDirectory == ROOT_DIRECTORY,
                onClick = onOpenParent,
            )
        }
        if (state.directoryTruncated) {
            item(key = DIRECTORY_TRUNCATED_KEY) {
                HyperText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            SampleTheme.colorScheme.tertiaryContainer,
                            RoundedCornerShape(8.dp),
                        )
                        .padding(12.dp),
                    text = stringResource(R.string.sample_directory_truncated),
                    color = SampleTheme.colorScheme.onTertiaryContainer,
                    style = SampleTheme.typography.bodySmall,
                )
            }
        }
        if (state.entries.isEmpty()) {
            item(key = EMPTY_DIRECTORY_KEY) {
                HyperText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    text = stringResource(R.string.sample_this_directory_is_empty),
                    color = SampleTheme.colorScheme.onSurfaceVariant,
                    style = SampleTheme.typography.bodyMedium,
                )
            }
        }
        items(
            items = state.entries,
            key = PrivilegeFileEntry::absolutePath,
            contentType = { entry -> entry.metadata?.type },
        ) { entry ->
            DeviceFileRow(
                name = entry.name,
                details = entry.toDetails(context, dateFormat),
                type = entry.metadata?.type,
                enabled = interactionsEnabled,
                dimmed = false,
                onClick = { onOpenEntry(entry) },
            )
        }
    }
}

@Composable
private fun DeviceFileRow(
    name: String,
    details: DeviceFileDetails,
    type: PrivilegeFileType?,
    enabled: Boolean,
    dimmed: Boolean,
    onClick: () -> Unit,
) {
    val isDirectory = type == PrivilegeFileType.DIRECTORY
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (dimmed) 0.45f else 1f)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        SampleCard(
            headlineContent = {
                HyperText(
                    text = name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            supportingContent = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    HyperText(
                        modifier = if (details.sizeText == null) {
                            Modifier.fillMaxWidth()
                        } else {
                            Modifier.weight(1f)
                        },
                        text = details.text,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = if (details.useTabularNumerals) {
                            SampleTheme.typography.bodyMedium.copy(
                                fontFeatureSettings = "tnum",
                            )
                        } else {
                            SampleTheme.typography.bodyMedium
                        },
                    )
                    details.sizeText?.let { sizeText ->
                        HyperText(
                            text = sizeText,
                            maxLines = 1,
                            softWrap = false,
                            style = SampleTheme.typography.bodyMedium,
                        )
                    }
                }
            },
            leadingContent = {
                HyperIcon(
                    painter = painterResource(
                        when (type) {
                            PrivilegeFileType.DIRECTORY -> R.drawable.ic_priv_sample_folder
                            PrivilegeFileType.SYMBOLIC_LINK ->
                                R.drawable.ic_priv_sample_symbolic_link
                            null -> R.drawable.ic_priv_sample_unknown_file
                            else -> R.drawable.ic_priv_sample_file
                        },
                    ),
                    contentDescription = when (type) {
                        PrivilegeFileType.DIRECTORY -> stringResource(R.string.sample_directory)
                        PrivilegeFileType.SYMBOLIC_LINK -> stringResource(R.string.sample_symbolic_link)
                        null -> stringResource(R.string.sample_file_type_unavailable)
                        else -> stringResource(R.string.sample_file)
                    },
                    tint = if (isDirectory) {
                        SampleTheme.colorScheme.primary
                    } else {
                        SampleTheme.colorScheme.onSurfaceVariant
                    },
                )
            },
        )
        hyper_ui.HyperDivider()
    }
}

@Composable
private fun DeviceFilePreviewContent(
    preview: PrivilegeSampleFilePreview,
    innerPadding: PaddingValues,
    onSelectMode: (PrivilegeSampleFilePreviewMode) -> Unit,
    onRetry: () -> Unit,
) {
    when (preview) {
        is PrivilegeSampleFilePreview.Loading -> Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            hyper_ui.HyperCircularProgressIndicator(progress = null)
        }

        is PrivilegeSampleFilePreview.Error -> DeviceFilesStatusPanel(
            title = stringResource(R.string.sample_unable_to_read_file),
            message = preview.message,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            actionLabel = stringResource(R.string.sample_retry),
            onAction = onRetry,
        )

        is PrivilegeSampleFilePreview.Content -> DeviceFilePreviewBody(
            preview = preview,
            innerPadding = innerPadding,
            onSelectMode = onSelectMode,
        )
    }
}

@Composable
private fun DeviceFilePreviewBody(
    preview: PrivilegeSampleFilePreview.Content,
    innerPadding: PaddingValues,
    onSelectMode: (PrivilegeSampleFilePreviewMode) -> Unit,
) {
    val context = LocalContext.current
    val text = remember(preview.bytes) {
        preview.bytes.toString(Charsets.UTF_8).removePrefix(UTF8_BOM)
    }
    val hexRowCount = preview.bytes.hexRowCount()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SelectionContainer {
            HyperText(
                text = preview.absolutePath,
                style = SampleTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                color = SampleTheme.colorScheme.onSurfaceVariant,
            )
        }
        HyperText(
            text = preview.previewSummary(context),
            style = SampleTheme.typography.bodySmall,
            color = if (preview.truncated) {
                SampleTheme.colorScheme.tertiary
            } else {
                SampleTheme.colorScheme.onSurfaceVariant
            },
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            PrivilegeSampleFilePreviewMode.entries.forEachIndexed { index, mode ->
                HyperButton(
                    onClick = { onSelectMode(mode) },
                    shape = HyperTheme.shapes.small,
                ) {
                    HyperText(stringResource(mode.labelRes))
                }
            }
        }
        when (preview.mode) {
            PrivilegeSampleFilePreviewMode.TEXT -> SelectionContainer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .background(
                        SampleTheme.colorScheme.surfaceContainerHighest,
                        RoundedCornerShape(8.dp),
                    )
                    .padding(12.dp),
            ) {
                HyperText(
                    text = text.ifEmpty { stringResource(R.string.sample_empty) },
                    modifier = Modifier.fillMaxWidth(),
                    fontFamily = FontFamily.Monospace,
                    style = SampleTheme.typography.bodySmall,
                )
            }

            PrivilegeSampleFilePreviewMode.HEX -> LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        SampleTheme.colorScheme.surfaceContainerHighest,
                        RoundedCornerShape(8.dp),
                    ),
                contentPadding = PaddingValues(12.dp),
            ) {
                if (hexRowCount == 0) {
                    item { HyperText(stringResource(R.string.sample_empty), fontFamily = FontFamily.Monospace) }
                } else {
                    items(
                        count = hexRowCount,
                        key = { rowIndex -> rowIndex },
                    ) { rowIndex ->
                        HyperText(
                            text = preview.bytes.formatHexRow(rowIndex),
                            maxLines = 1,
                            fontFamily = FontFamily.Monospace,
                            style = SampleTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceFilesStatusPanel(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            HyperText(
                text = title,
                style = SampleTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            if (actionLabel != null && onAction != null) {
                SelectionContainer {
                    HyperText(
                        text = message,
                        style = SampleTheme.typography.bodyMedium,
                        color = SampleTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                HyperText(
                    text = message,
                    style = SampleTheme.typography.bodyMedium,
                    color = SampleTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (actionLabel != null && onAction != null) {
                HyperButton(onClick = onAction) {
                    HyperText(actionLabel)
                }
            }
        }
    }
}

private fun PrivilegeFileEntry.toDetails(
    context: Context,
    dateFormat: DateFormat,
): DeviceFileDetails {
    val metadata = metadata ?: return DeviceFileDetails(text = context.getString(R.string.sample_metadata_unavailable))
    return metadata.toDetails(context, dateFormat)
}

private fun PrivilegeFileMetadata.toDetails(
    context: Context,
    dateFormat: DateFormat,
): DeviceFileDetails {
    val modified = if (lastModifiedMillis > 0L) {
        dateFormat.format(Date(lastModifiedMillis))
    } else {
        context.getString(R.string.sample_modified_time_unavailable)
    }
    return DeviceFileDetails(
        text = modified,
        sizeText = if (type != PrivilegeFileType.DIRECTORY) {
            Formatter.formatShortFileSize(context, sizeBytes.coerceAtLeast(0L))
        } else {
            null
        },
        useTabularNumerals = lastModifiedMillis > 0L,
    )
}

private data class DeviceFileDetails(
    val text: String,
    val sizeText: String? = null,
    val useTabularNumerals: Boolean = false,
)

private fun PrivilegeSampleFilePreview.Content.previewSummary(context: Context): String {
    val totalSize = Formatter.formatShortFileSize(context, metadata.sizeBytes.coerceAtLeast(0L))
    return if (truncated) {
        context.getString(R.string.sample_preview_summary, MAX_PREVIEW_BYTES / 1024, totalSize)
    } else {
        totalSize
    }
}

internal fun ByteArray.hexRowCount(): Int = (size + HEX_ROW_BYTES - 1) / HEX_ROW_BYTES

internal fun ByteArray.formatHexRow(rowIndex: Int): String = buildString(HEX_LINE_CAPACITY) {
    require(rowIndex in 0 until hexRowCount())
    val offset = rowIndex * HEX_ROW_BYTES
    append(offset.toString(16).uppercase().padStart(8, '0'))
    append("  ")
    for (column in 0 until HEX_ROW_BYTES) {
        val index = offset + column
        if (index < size) {
            val value = this@formatHexRow[index].toInt() and 0xff
            append(HEX_DIGITS[value ushr 4])
            append(HEX_DIGITS[value and 0x0f])
        } else {
            append("  ")
        }
        if (column != HEX_ROW_BYTES - 1) append(' ')
    }
    append("  |")
    for (column in 0 until HEX_ROW_BYTES) {
        val index = offset + column
        if (index < size) {
            val value = this@formatHexRow[index].toInt() and 0xff
            append(if (value in PRINTABLE_ASCII_RANGE) value.toChar() else '.')
        } else {
            append(' ')
        }
    }
    append('|')
}

private const val ROOT_DIRECTORY: String = "/"
private const val PARENT_ENTRY_KEY: String = "device-files-parent"
private const val DIRECTORY_TRUNCATED_KEY: String = "device-files-truncated"
private const val EMPTY_DIRECTORY_KEY: String = "device-files-empty"
private const val DIRECTORY_ENTRY_DATE_PATTERN: String = "yyyy-MM-dd HH:mm:ss"
private const val UTF8_BOM: String = "\uFEFF"
private const val HEX_ROW_BYTES: Int = 16
private const val HEX_LINE_CAPACITY: Int = 78
private const val HEX_DIGITS: String = "0123456789ABCDEF"
private val PRINTABLE_ASCII_RANGE: IntRange = 0x20..0x7e
