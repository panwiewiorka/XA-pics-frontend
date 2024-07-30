package xapics.app.presentation

import xapics.app.Pic
import xapics.app.Tag
import xapics.app.Thumb


data class AppState(
    val rollThumbnails: List<Thumb>? = null,
    val tags: List<Tag> = emptyList(),
    val topBarCaption: String = "XA pics",
    val picsList: List<Pic> = emptyList(),
    val showPicsList: Boolean = true,
    val userName: String? = null,
    val collectionToSaveTo: String = "Favourites",
    val userCollections: List<Thumb>? = null,
    val picCollections: List<String> = emptyList(),
    val pic: Pic? = null,
    val picIndex: Int? = null,
    val showSearch: Boolean = false,
    val getBackAfterLoggingIn: Boolean = false,
    val showConnectionError: Boolean = false,
    val isFullscreen: Boolean = false,
    val isLoading: Boolean = false
)