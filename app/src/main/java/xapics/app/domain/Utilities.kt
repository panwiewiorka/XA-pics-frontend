package xapics.app.domain

import xapics.app.getTagColorAndName
import xapics.app.toTagsList

fun String.transformTopBarCaption(): String {
    val query = this
    val caption: String

    if (!query.contains(" = ")) {
        caption = query
    } else {
        val tags = query.toTagsList()
        val searchIndex = tags.indexOfFirst{it.type == "search"}
        val isSearchQuery = searchIndex != -1
        val isFilteredList = tags.size > 1

        caption = when {
            tags.isEmpty() -> "??? $query"
            isSearchQuery -> "\"${tags[searchIndex].value}\""
            isFilteredList -> "Filtered pics"
            else -> { // single category
                val theTag = tags[0].value
                when (tags[0].type) {
                    "filmType" -> when (theTag) {
                        "BW" -> "Black and white films"
                        "NEGATIVE" -> "Negative films"
                        "SLIDE" -> "Slide films"
                        else -> ""
                    }
                    "filmName" -> "film: $theTag"
                    "hashtag" -> "#$theTag"
                    else -> getTagColorAndName(tags[0]).second
                }
            }
        }
    }

    return caption
}