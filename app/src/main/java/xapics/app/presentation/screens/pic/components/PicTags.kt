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
import xapics.app.data.db.StateSnapshot
import xapics.app.presentation.components.PicTag
import xapics.app.presentation.screens.pic.PicScreenState
import xapics.app.toTagsList

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PicTags(
    search: (query: String) -> Unit,
    getCollection: (collection: String, onClick: () -> Unit) -> Unit,
    picScreenState: PicScreenState,
    state: StateSnapshot,
    goToPicsListScreen: () -> Unit,
    goToAuthScreen: () -> Unit,
) {

    AnimatedContent(
        targetState = state,
        transitionSpec = {
            fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(200, 50))
        },
        label = "tags"
    ) { theState ->
        FlowRow(
            modifier = Modifier.padding(horizontal = 28.dp)
        ) {
//            if(theState.pic != null) {
                val tags = theState.pic!!.tags.toTagsList()

                tags.forEach {
                    PicTag(it) {
                        search("${it.type} = ${it.value}")
                        goToPicsListScreen()
                    }
                }
//            }

            // TODO uncomment / fix (merge states, ???)
//            theState.picCollections.forEach {
//                PicTag(Tag("collection", it)) {
////                    saveStateSnapshot("TAGGAGAG") // TODO needed? vv getCollection() saves snapshot
//                    getCollection(it, goToAuthScreen)
//                    goToPicsListScreen()
//                }
//            }
        }
    }
}