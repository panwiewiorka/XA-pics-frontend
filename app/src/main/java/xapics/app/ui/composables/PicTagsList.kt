package xapics.app.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xapics.app.ui.theme.CollectionTag
import xapics.app.ui.theme.DefaultTag
import xapics.app.ui.theme.FilmTag
import xapics.app.ui.theme.GrayMedium
import xapics.app.ui.theme.YearTag

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PicTagsList(tagsList: List<List<String>>?) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        tagsList?.forEachIndexed { index, list ->
            val color = when(index) {
                0 -> CollectionTag
                1 -> FilmTag
                2 -> GrayMedium
                3 -> YearTag
                else -> DefaultTag
            }
            FlowRow(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
//                .align(Alignment.Start)
            ) {
                list.forEach {
                    PicTag(text = it.trim(), color = color) {
                        // TODO
                    }
                }
                if (index == 0) {
                    PicTag(text = "XA-only", color = color) {
                        // TODO
                    }
                    PicTag(text = "non-XA", color = color) {
                        // TODO
                    }
                    PicTag(text = "expired", color = color) {
                        // TODO
                    }
                    PicTag(text = "non-expired", color = color) {
                        // TODO
                    }
                    PicTag(text = "xpro", color = color) {
                        // TODO
                    }
                    PicTag(text = "non-xpro", color = color) {
                        // TODO
                    }
                }
            }
        }
    }
}