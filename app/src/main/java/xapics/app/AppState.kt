package xapics.app


data class AppState(
    val picsList: List<Pic>? = null,
    val picsListColumn: ShowHide = ShowHide.SHOW,
    val tags: List<Tag> = emptyList(),
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
    val searchField: ShowHide = ShowHide.HIDE,
    val getBackAfterLoggingIn: Boolean = false,
    val connectionError: ShowHide = ShowHide.HIDE,
    val isLoading: Boolean = false
)