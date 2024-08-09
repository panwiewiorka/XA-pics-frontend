package xapics.app.presentation

import xapics.app.Pic
import xapics.app.Tag
import xapics.app.Thumb


data class AppState(
    val randomPic: Pic? = null,
    val rollThumbnails: List<Thumb>? = null,
    val tags: List<Tag> = emptyList(),
    val userName: String? = null,
    val collectionToSaveTo: String = "Favourites",
    val userCollections: List<Thumb>? = null,
    val picCollections: List<String> = emptyList(),
    val showSearch: Boolean = false,
    val getBackAfterLoggingIn: Boolean = false,
    val connectionError: Boolean = false,
    val isFullscreen: Boolean = false,
    val isLoading: Boolean = false
)