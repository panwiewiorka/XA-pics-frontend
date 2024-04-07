package xapics.app.ui

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xapics.app.Film
import xapics.app.Pic
import xapics.app.Roll
import xapics.app.ShowHide
import xapics.app.Tag
import xapics.app.Thumb


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
    val isFullscreen: Boolean = false,
    val picDetailsWidth: Dp = 0.dp,
    val isLoading: Boolean = false
)