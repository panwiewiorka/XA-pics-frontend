package xapics.app.presentation.common.homeScreen

import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.ui.Modifier
import xapics.app.presentation.AppState
import xapics.app.presentation.composables.RollCard

fun LazyGridScope.rollCardsGrid(appState: AppState, search: (String) -> Unit, goToPicsListScreen: () -> Unit, isPortrait: Boolean, modifier: Modifier) {
    items(appState.rollThumbnails?.size ?: 0) {
        val imageUrl = appState.rollThumbnails!![it].thumbUrl
        val rollTitle = appState.rollThumbnails[it].title
        RollCard(
            imageUrl = imageUrl,
            rollTitle = rollTitle,
            isPortrait = isPortrait,
            modifier = modifier
        ) {
            search("roll = $rollTitle")
            goToPicsListScreen()
        }
    }
}