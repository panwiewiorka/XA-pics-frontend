package xapics.app.ui.common.picScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xapics.app.Tag
import xapics.app.toTagsList
import xapics.app.ui.AppState
import xapics.app.ui.MainViewModel
import xapics.app.ui.composables.PicTag

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PicTags(
    viewModel: MainViewModel,
    appState: AppState,
    goToPicsListScreen: () -> Unit,
    goToAuthScreen: () -> Unit,
) {
    AnimatedContent(
        targetState = appState,
        transitionSpec = {
            fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(200, 50))
        },
        label = "tags"
    ) { state ->
        FlowRow(
            modifier = Modifier.padding(horizontal = 28.dp)
        ) {
            if(state.pic != null) {
                val tags = state.pic.tags.toTagsList()

                tags.forEach {
                    PicTag(it) {
                        viewModel.search("${it.type} = ${it.value}")
                        goToPicsListScreen()
                    }
                }
            }

            state.picCollections.forEach {
                PicTag(Tag("collection", it)) {
                    viewModel.saveStateSnapshot()
                    viewModel.getCollection(it, goToAuthScreen)
                    goToPicsListScreen()
                }
            }
        }
    }
}