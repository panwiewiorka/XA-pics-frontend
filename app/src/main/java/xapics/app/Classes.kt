package xapics.app

//enum class PicType { FIRST, NEXT, PREV }

enum class FilmType { SLIDE, NEGATIVE, BW, NULL }

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