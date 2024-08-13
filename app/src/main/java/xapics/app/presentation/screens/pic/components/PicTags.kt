package xapics.app.presentation.screens.pic.components

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
import xapics.app.presentation.components.PicTag
import xapics.app.presentation.screens.pic.PicScreenState
import xapics.app.toTagsList

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PicTags(
    picScreenState: PicScreenState,
    goToPicsListScreen: (searchQuery: String) -> Unit,
) {

    AnimatedContent(
        targetState = picScreenState,
        transitionSpec = {
            fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(200, 50))
        },
        label = "tags"
    ) { theState ->
        FlowRow(
            modifier = Modifier.padding(horizontal = 28.dp)
        ) {
            val tags = theState.picsList[theState.picIndex!!].tags.toTagsList()

            tags.forEach {
                PicTag(it) {
                    goToPicsListScreen("${it.type} = ${it.value}")
                }
            }

            theState.picCollections.forEach {
                PicTag(Tag("collection", it)) {
                    goToPicsListScreen("collection = $it")
                }
            }
        }
    }
}