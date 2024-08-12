package xapics.app.presentation.screens.pic

import xapics.app.Pic
import xapics.app.Thumb

data class PicScreenState(
    val isLoading: Boolean = false,
    val connectionError: Boolean = false,
    val userCollections: List<Thumb>? = null,
    val picCollections: List<String> = emptyList(),
    val collectionToSaveTo: String = "Favourites",
    val picsList: List<Pic> = emptyList(),
    val picIndex: Int? = null,
)
