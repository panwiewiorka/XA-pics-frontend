package xapics.app


data class AppState(
    val picsList: List<Pic>? = null,
    val topBarCaption: String = "",
    val picCollections: List<String> = emptyList(),
    val collectionToSaveTo: String = "Favourites",
    val userCollections: List<Thumb>? = null,
    val rollThumbnails: List<Thumb>? = null,
    val filmsList: List<Film>? = null,
    val rollsList: List<Roll>? = null,
    val pic: Pic? = null,
    val picIndex: Int? = null,
    val filmToEdit: Film? = null,
    val rollToEdit: Roll? = null,
    val isLoading: Boolean = false
)