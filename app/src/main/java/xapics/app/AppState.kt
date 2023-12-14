package xapics.app


data class AppState(
    val picsList: List<Pic>? = null,
    val rollThumbnails: List<Pair<String, String>>? = null,
    val filmsList: List<Film>? = null, //listOf(Film("Ektachrome", 100, FilmType.SLIDE), Film("Aerocolor", 125, FilmType.NEGATIVE)),
    val rollsList: List<Roll>? = null,
    val pic: Pic? = null,
    val picIndex: Int? = null,
    val filmToEdit: Film? = null,
    val rollToEdit: Roll? = null,
    val isLoading: Boolean = false
)