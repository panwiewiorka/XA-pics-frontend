package xapics.app


data class AppState(
    val picsList: List<Pic>? = null,
//    val picsListQuery: PicsListQuery? = null,
    val tags: List<Tag> = emptyList(),
    val captionsList: List<String> = emptyList(),
    val topBarCaption: String = "XA pics",
    val userName: String? = null,
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
    val showSearch: Boolean = false,
    val getBackAfterLoggingIn: Boolean = false,
    val showConnectionError: Boolean = false,
    val isLoading: Boolean = false
)