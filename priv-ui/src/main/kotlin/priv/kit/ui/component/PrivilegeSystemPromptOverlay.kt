package priv.kit.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import hyper_ui.HyperCard
import hyper_ui.HyperCardColors
import hyper_ui.HyperColors
import hyper_ui.HyperText
import hyper_ui.HyperTheme
import hyper_ui.LocalHyperContentColor
import priv.kit.ui.PrivilegeUiSystemPrompt
import priv.kit.ui.asString

@Composable
internal fun PrivilegeSystemPromptOverlay(
    prompt: PrivilegeUiSystemPrompt?,
    modifier: Modifier = Modifier,
) {
    var displayedPrompt by remember { mutableStateOf(prompt) }
    val visibility = remember {
        MutableTransitionState(prompt != null)
    }
    LaunchedEffect(prompt) {
        if (prompt != null) {
            displayedPrompt = prompt
        }
        visibility.targetState = prompt != null
    }
    LaunchedEffect(
        prompt,
        visibility.currentState,
        visibility.isIdle,
    ) {
        if (prompt == null && visibility.isIdle && !visibility.currentState) {
            displayedPrompt = null
        }
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter,
    ) {
        AnimatedVisibility(
            visibleState = visibility,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically(),
        ) {
            displayedPrompt?.let { currentPrompt ->
                PrivilegeSystemPromptCard(currentPrompt)
            }
        }
    }
}

@Composable
private fun PrivilegeSystemPromptCard(prompt: PrivilegeUiSystemPrompt) {
    CompositionLocalProvider(LocalHyperContentColor provides HyperColors.pageBackground) {
        HyperCard(
            modifier = Modifier
                .widthIn(max = PROMPT_MAX_WIDTH)
                .fillMaxWidth()
                .semantics(mergeDescendants = true) {
                    liveRegion = LiveRegionMode.Polite
                },
            colors = HyperCardColors(containerColor = HyperColors.primaryText),
            shape = HyperTheme.shapes.medium,
            elevation = 6.dp,
            contentModifier = Modifier.padding(
                horizontal = PrivilegeUiSpacing.large,
                vertical = PrivilegeUiSpacing.medium,
            ),
            verticalArrangement = Arrangement.spacedBy(PrivilegeUiSpacing.extraSmall),
        ) {
            HyperText(
                text = prompt.title.asString(),
                style = HyperTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            HyperText(
                text = prompt.message.asString(),
                style = HyperTheme.typography.bodyMedium,
            )
        }
    }
}

private val PROMPT_MAX_WIDTH = 560.dp
