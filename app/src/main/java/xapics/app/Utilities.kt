package xapics.app

import androidx.compose.ui.graphics.Color
import xapics.app.ui.theme.CollectionTag
import xapics.app.ui.theme.DefaultTag
import xapics.app.ui.theme.FilmTag
import xapics.app.ui.theme.GrayMedium
import xapics.app.ui.theme.RollAttribute
import xapics.app.ui.theme.RollTag
import xapics.app.ui.theme.YearTag
import java.util.Locale

const val TAG = "mytag"

fun String.toTagsList(): List<Tag> {
    return this.split(", ")
        .map { it.split(" = ") }
        .map { Tag(it[0], it[1]) }
        .filterNot { it.value == "" }
}

fun String.capitalize(): String {
    return this.replaceFirstChar { char -> char.titlecase(Locale.ROOT) }
}

fun getTagColorAndName(tag: Tag): Pair<Color, String> {
    return when(tag.type) {
        "filmName" -> Pair(FilmTag, tag.value)
        "filmType" -> Pair(GrayMedium, if (tag.value == "BW") "black and white" else tag.value.lowercase())
        "iso" -> Pair(GrayMedium, "iso ${tag.value}")
        "roll" -> Pair(RollTag, tag.value)
        "expired" -> Pair(RollAttribute, if(tag.value == "false") "not expired" else "expired")
        "xpro" -> Pair(RollAttribute, if(tag.value == "false") "no cross-process" else "cross-process")
        "year" -> Pair(YearTag, tag.value)
        "hashtag" -> Pair(DefaultTag, tag.value)
        "collection" -> Pair(CollectionTag, tag.value)
        else -> Pair(Color.Transparent, tag.value)
    }
}