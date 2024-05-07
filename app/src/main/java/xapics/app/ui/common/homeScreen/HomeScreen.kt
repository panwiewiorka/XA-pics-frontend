package xapics.app.ui.common.homeScreen

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
import xapics.app.ui.AppState
import xapics.app.ui.MainViewModel
import xapics.app.ui.WindowInfo.WindowType.Compact
import xapics.app.ui.composables.ConnectionErrorButton
import xapics.app.ui.windowInfo

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    appState: AppState,
    goToPicsListScreen: () -> Unit,
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
            appState.showConnectionError -> {
                ConnectionErrorButton {
                    viewModel.authenticate()
                    viewModel.getRollThumbs()
                    viewModel.getRandomPic()
                    viewModel.getAllTags()

                    viewModel.showConnectionError(false)
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
                    isCompact && isPortrait -> HomePortraitCompactView(viewModel, appState, goToPicsListScreen, updateAndGoToPicScreen, maxWidth, padding, gridState)
                    isCompact -> HomeLandscapeCompactView(viewModel, appState, goToPicsListScreen, updateAndGoToPicScreen, padding, gridState)
                    isPortrait -> HomePortraitMediumView(viewModel, appState, goToPicsListScreen, updateAndGoToPicScreen, goToSearchScreen, maxWidth, padding, tagsScrollState, gridState)
                    else -> HomeLandscapeMediumView(viewModel, appState, goToPicsListScreen, updateAndGoToPicScreen, goToSearchScreen, maxHeight, padding, tagsScrollState, gridState)
                }

                if (appState.isLoading) CircularProgressIndicator()
            }
        }
    }
}