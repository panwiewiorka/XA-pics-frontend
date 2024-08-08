package xapics.app.presentation.screens.pic

import xapics.app.Thumb

data class PicScreenState(
    val isLoading: Boolean = false,
    val isFullscreen: Boolean = false,
    val showConnectionError: Boolean = false,
    val userCollections: List<Thumb>? = null,
    val picCollections: List<String> = emptyList(),
    val collectionToSaveTo: String = "Favourites"
)
