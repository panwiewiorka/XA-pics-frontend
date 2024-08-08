package xapics.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import xapics.app.presentation.theme.AlphaGray
import xapics.app.presentation.theme.CollectionTag
import xapics.app.presentation.theme.DefaultTag
import xapics.app.presentation.theme.FilmTag
import xapics.app.presentation.theme.RollAttribute
import xapics.app.presentation.theme.RollTag
import xapics.app.presentation.theme.YearTag

const val TAG = "mytag"

val Int.nonScaledSp
    @Composable
    get() = (this / LocalDensity.current.fontScale).sp

fun String.toTagsList(): List<Tag> {
    val list = this.split(", ").toMutableList()
    for (i in list.indices.reversed()) {
        if (i > 0 && !list[i].contains(" = ")) list[i - 1] += ", ${list[i]}"
    }
    return list
        .filterNot { !it.contains(" = ") }
        .map { it.split(" = ") }
        .map { Tag(it[0], it[1]) }
        .filterNot { it.value == "" }
}

fun getTagColorAndName(tag: Tag): Pair<Color, String> {
    return when(tag.type) {
        "filmName" -> Pair(FilmTag, tag.value)
        "filmType" -> Pair(AlphaGray, if (tag.value == "BW") "black and white" else tag.value.lowercase())
        "iso" -> Pair(AlphaGray, "iso ${tag.value}")
        "roll" -> Pair(RollTag, tag.value)
        "expired" -> Pair(RollAttribute, if(tag.value == "false") "not expired" else "expired")
        "xpro" -> Pair(RollAttribute, if(tag.value == "false") "no cross-process" else "cross-process")
        "year" -> Pair(YearTag, tag.value)
        "hashtag" -> Pair(DefaultTag, tag.value)
        "collection" -> Pair(CollectionTag, tag.value)
        else -> Pair(Color.Transparent, tag.value)
    }
}