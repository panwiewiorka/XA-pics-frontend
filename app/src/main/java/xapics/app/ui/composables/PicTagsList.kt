package xapics.app.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import xapics.app.ui.theme.CollectionTag
import xapics.app.ui.theme.DefaultTag
import xapics.app.ui.theme.FilmTag
import xapics.app.ui.theme.GrayMedium
import xapics.app.ui.theme.YearTag

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PicTagsList(
    tagsList: List<List<String>>?,
    getPicsList: (year: Int?, roll: String?, film: String?, tag: String?, description: String?) -> Unit,
    goToPicsListScreen: () -> Unit,
) {
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
                        when(index) {
                            0 -> {}
                            1 -> getPicsList(null, null, it, null, null, )
                            2 -> {}//getPicsList(null, null, null, null, null, )
                            3 -> getPicsList(it.toInt(), null, null, null, null, )
                            4 -> getPicsList(null, null, null, it, null, )
                        }
                        goToPicsListScreen()
                    }
                }
                if (index == 0) {
                    PicTag(text = "XA", color = color) {
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
                    PicTag(text = "x-pro", color = color) {
                        // TODO
                    }
                    PicTag(text = "non-x-pro", color = color) {
                        // TODO
                    }
                }
            }
        }
    }
}