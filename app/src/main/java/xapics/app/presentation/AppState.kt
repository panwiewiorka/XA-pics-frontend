package xapics.app.presentation

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xapics.app.Pic
import xapics.app.Tag
import xapics.app.Thumb


data class AppState(
    val picsList: List<Pic> = emptyList(),
    val showPicsList: Boolean = true,
    val tags: List<Tag> = emptyList(),
    val topBarCaption: String = "XA pics",
    val userName: String? = null,
    val picCollections: List<String> = emptyList(),
    val collectionToSaveTo: String = "Favourites",
    val userCollections: List<Thumb>? = null,
    val rollThumbnails: List<Thumb>? = null,
    val pic: Pic? = null,
    val picIndex: Int? = null,
    val showSearch: Boolean = false,
    val getBackAfterLoggingIn: Boolean = false,
    val showConnectionError: Boolean = false,
    val isFullscreen: Boolean = false,
    val picDetailsWidth: Dp = 0.dp,
    val blurContent: Boolean = false,
    val isLoading: Boolean = false
)