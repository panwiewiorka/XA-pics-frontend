package xapics.app

import java.util.Date

enum class PicType { FIRST, NEXT, PREV }

enum class FilmType { SLIDE, NEGATIVE, BW, NULL }

data class Pic(
    val year: Int,
    val description: String,
    val imageUrl: String,
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
//    val film: Film? = null,
//    val date: Date,
    val nonXa: Boolean = false,
)