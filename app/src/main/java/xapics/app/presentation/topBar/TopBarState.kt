package xapics.app.presentation.topBar

import xapics.app.Pic
import xapics.app.Tag

data class TopBarState(
    val tags: List<Tag> = emptyList(),
//    val topBarCaption: String = "XA pics",
    val picsList: List<Pic> = emptyList(),
    val userName: String? = null,
    val showSearch: Boolean = false,
)
