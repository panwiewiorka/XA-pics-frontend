package xapics.app.presentation.screens.home

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xapics.app.presentation.WindowInfo.WindowType.Compact
import xapics.app.presentation.components.ConnectionErrorButton
import xapics.app.presentation.screens.home.layouts.HomeLandscapeCompactLayout
import xapics.app.presentation.screens.home.layouts.HomeLandscapeMediumLayout
import xapics.app.presentation.screens.home.layouts.HomePortraitCompactLayout
import xapics.app.presentation.screens.home.layouts.HomePortraitMediumLayout
import xapics.app.presentation.windowInfo

@Composable
fun HomeScreen(
    authenticate: () -> Unit,
    getRollThumbs: () -> Unit,
    getAllTags: () -> Unit,
    showConnectionError: (Boolean) -> Unit,
    getRandomPic: () -> Unit,
    homeState: HomeState,
    goToPicsListScreen: (String) -> Unit,
    updateAndGoToPicScreen: () -> Unit,
    goToSearchScreen: () -> Unit,
) {
    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 4.dp)
    ) {
        when {
            homeState.connectionError -> {
                ConnectionErrorButton {
                    authenticate()
                    getRandomPic()
                    getRollThumbs()
                    getAllTags()

                    showConnectionError(false)
                }
            }
            else -> {
                val windowInfo = windowInfo()
                val isPortrait = windowInfo.isPortraitOrientation
                val isCompact = windowInfo.windowType == Compact
                val padding = 12.dp
                val tagsScrollState = rememberScrollState()
                val gridState = rememberLazyGridState()

                when {
                    isCompact && isPortrait -> HomePortraitCompactLayout(getRandomPic, homeState, goToPicsListScreen, updateAndGoToPicScreen, maxWidth, padding, gridState)
                    isCompact -> HomeLandscapeCompactLayout(getRandomPic, homeState, goToPicsListScreen, updateAndGoToPicScreen, padding, gridState)
                    isPortrait -> HomePortraitMediumLayout(getRandomPic, homeState, goToPicsListScreen, updateAndGoToPicScreen, goToSearchScreen, maxWidth, padding, tagsScrollState, gridState)
                    else -> HomeLandscapeMediumLayout(getRandomPic, homeState, goToPicsListScreen, updateAndGoToPicScreen, goToSearchScreen, maxHeight, padding, tagsScrollState, gridState)
                }

                if (homeState.isLoading) CircularProgressIndicator()
            }
        }
    }
}