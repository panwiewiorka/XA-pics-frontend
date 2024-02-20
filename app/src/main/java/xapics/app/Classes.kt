package xapics.app

//enum class PicType { FIRST, NEXT, PREV }

enum class FilmType { SLIDE, NEGATIVE, BW, NULL }

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
    val year: Int,
    val description: String,
    val imageUrl: String,
    val tags: String,
    val film: String,
)

data class Film(
    val filmName: String = "",
    val iso: Int? = null,
    val type: FilmType = FilmType.NULL,
    val xpro: Boolean = false,
    val expired: Boolean = false,
)

data class Roll(
    val title: String = "",
    val film: String = "",
//    val date: Date,
    val nonXa: Boolean = false,
)

data class Thumb(
    val title: String,
    val thumbUrl: String
)

data class TheString(
    val text: String
)