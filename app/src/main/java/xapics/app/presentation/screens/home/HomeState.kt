package xapics.app.presentation.screens.home

import xapics.app.Pic
import xapics.app.Tag
import xapics.app.Thumb


data class HomeState(
    val randomPic: Pic? = null,
    val rollThumbnails: List<Thumb>? = null,
    val tags: List<Tag> = emptyList(),
    val connectionError: Boolean = false,
    val isLoading: Boolean = false
)