package xapics.app.presentation.screens.home.components

import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.ui.Modifier
import xapics.app.Thumb
import xapics.app.presentation.components.RollCard

fun LazyGridScope.rollCardsGrid(
    rollThumbnails: List<Thumb>?,
    goToPicsListScreen: (searchQuery: String) -> Unit,
    isPortrait: Boolean,
    modifier: Modifier
) {
    items(rollThumbnails?.size ?: 0) {
        val imageUrl = rollThumbnails!![it].thumbUrl
        val rollTitle = rollThumbnails[it].title
        RollCard(
            imageUrl = imageUrl,
            rollTitle = rollTitle,
            isPortrait = isPortrait,
            modifier = modifier,
            onClick = {
                goToPicsListScreen("roll = $rollTitle")
            }
        )
    }
}