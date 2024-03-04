package xapics.app.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import xapics.app.Tag

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PicTagsList(
    tags: List<Tag>,
    getPicsList: (query: String) -> Unit,
    changeTagsState: (Tag) -> Unit,
    getTagColorAndName: (Tag) -> Pair<Color, String>,
    goToPicsListScreen: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        FlowRow(
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            tags.forEach {tag ->
                PicTag(tag, { getTagColorAndName(tag) }) {
                    getPicsList("${tag.type} = ${tag.value}")
                    changeTagsState(tag)
                    goToPicsListScreen()
                }
            }
        }
    }
}