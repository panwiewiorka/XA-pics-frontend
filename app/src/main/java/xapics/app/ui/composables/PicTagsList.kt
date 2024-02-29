package xapics.app.ui.composables

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xapics.app.TAG
import xapics.app.Tag
import xapics.app.ui.theme.CollectionTag
import xapics.app.ui.theme.DefaultTag
import xapics.app.ui.theme.FilmTag
import xapics.app.ui.theme.GrayMedium
import xapics.app.ui.theme.RollAttribute
import xapics.app.ui.theme.YearTag

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PicTagsList(
    tags: List<Tag>,
    getPicsList: (query: String) -> Unit,
    goToPicsListScreen: () -> Unit,
) {
    LaunchedEffect(tags) {
        Log.d(TAG, "PicTagsList: $tags")
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        FlowRow(
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            tags.forEach {
                val (color, text) = when (it.type) {
                    "filmType" -> Pair(GrayMedium, it.value.lowercase())
                    "nonXa" -> Pair(RollAttribute, if(it.value == "false") "XA" else "non-XA")
                    "expired" -> Pair(RollAttribute, if(it.value == "false") "not expired" else "expired")
                    "xpro" -> Pair(RollAttribute, if(it.value == "false") "no cross-process" else "cross-process")
                    "iso" -> Pair(GrayMedium, "iso ${it.value}")
                    "filmName" -> Pair(FilmTag, it.value)
                    "year" -> Pair(YearTag, it.value)
                    else -> Pair(DefaultTag, it.value)
                }

                PicTag(text = text, color = color) {
                    getPicsList("${it.type} = ${it.value}")
                    goToPicsListScreen()
                }
            }
        }
    }
}