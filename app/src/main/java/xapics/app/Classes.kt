package xapics.app

import coil.request.Tags

//enum class PicType { FIRST, NEXT, PREV }

enum class FilmType { SLIDE, NEGATIVE, BW, NULL }

enum class TagState { DISABLED, ENABLED, SELECTED }

enum class PicsListType { ROLL, FILM, YEAR, TAGS, SEARCH }

class PicsListQuery(
    private val year: Int?,
    private val roll: String?,
    private val film: String?,
    private val tag: String?,
    private val description: String?,
) {
    fun flattenToString():String {
        return (year?.toString() ?: "") + (roll ?: "") + if(film != null) "film: $film " else "" + if(tag != null) "#$tag " else "" +
                    if(description != null) "\"$description\" " else ""
    }
}

data class Pic(
    val id: Int,
    val imageUrl: String,
    val description: String,
    val tags: String
//    val collections: String? = null,
)
//data class Pic(
//    val id: Int,
//    val year: Int,
//    val description: String,
//    val imageUrl: String,
//    val tags: String,
//    val film: String,
//    val filmType: FilmType,
//    val iso: Int,
//    val expired: Boolean,
//    val xpro: Boolean,
//    val nonXa: Boolean,
////    val collections: String? = null,
//)

data class Film(
    val filmName: String = "",
    val iso: Int? = null,
    val type: FilmType = FilmType.NULL,
)

data class Roll(
    val title: String = "",
    val film: String = "",
//    val date: Date,
    val expired: Boolean = false,
    val xpro: Boolean = false,
    val nonXa: Boolean = false,
)

data class Thumb(
    val title: String,
    val thumbUrl: String
)

// needed to fix this (probably due to String containing special symbols):
// com.google.gson.stream.MalformedJsonException: Use JsonReader.setLenient(true) to accept malformed JSON at line 1 column 1 path $
data class TheString(
    val string: String
)

data class Tag(
    val type: String,
    val value: String,
    var state: TagState = TagState.ENABLED
)