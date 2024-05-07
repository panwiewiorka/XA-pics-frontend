package xapics.app.ui.common.homeScreen

import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.ui.Modifier
import xapics.app.ui.AppState
import xapics.app.ui.composables.RollCard

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