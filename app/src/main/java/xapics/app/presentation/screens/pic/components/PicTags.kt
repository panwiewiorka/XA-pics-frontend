package xapics.app.presentation.screens.pic.components

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

    // todo on PicsListScreen init: fetch all pics collections and add them to respectable pics tags, then uncomment.
//    AnimatedContent(
//        targetState = picScreenState,
//        transitionSpec = {
//            fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(200, 50))
//        },
//        label = "tags"
//    ) { theState ->
        FlowRow(
            modifier = Modifier.padding(horizontal = 28.dp)
        ) {
            val tags = picScreenState.picsList[picScreenState.picIndex!!].tags.toTagsList()

            tags.forEach {
                PicTag(it) {
                    goToPicsListScreen("${it.type} = ${it.value}")
                }
            }

            picScreenState.picCollections.forEach {
                PicTag(Tag("collection", it)) {
                    goToPicsListScreen("collection = $it")
                }
            }
        }
//    }
}