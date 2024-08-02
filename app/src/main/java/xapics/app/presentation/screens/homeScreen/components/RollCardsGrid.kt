package xapics.app.presentation.screens.homeScreen.components

import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.ui.Modifier
import xapics.app.Thumb
import xapics.app.presentation.components.RollCard

fun LazyGridScope.rollCardsGrid(rollThumbnails: List<Thumb>?, search: (String) -> Unit, goToPicsListScreen: () -> Unit, isPortrait: Boolean, modifier: Modifier) {
    items(rollThumbnails?.size ?: 0) {
        val imageUrl = rollThumbnails!![it].thumbUrl
        val rollTitle = rollThumbnails[it].title
        RollCard(
            imageUrl = imageUrl,
            rollTitle = rollTitle,
            isPortrait = isPortrait,
            modifier = modifier,
            onClick = {
                search("roll = $rollTitle")
                goToPicsListScreen()
            }
        )
    }
}