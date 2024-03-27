package xapics.app.ui.common.homeScreen

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xapics.app.AppState
import xapics.app.MainViewModel
import xapics.app.ShowHide.HIDE
import xapics.app.ui.WindowInfo.WindowType.Compact
import xapics.app.ui.composables.ConnectionErrorButton
import xapics.app.ui.windowInfo

@Composable
fun HomeScreen(
    viewModel: MainViewModel, appState: AppState, goToPicsListScreen: () -> Unit, goToSearchScreen: () -> Unit,
) {
    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 4.dp)
    ) {
        when {
            appState.connectionError.isShown -> {
                ConnectionErrorButton {
                    viewModel.authenticate()
                    viewModel.getUserInfo {} // TODO needed?
//                        getPicsList(2020)
                    viewModel.getRollsList()
                    viewModel.getRandomPic()
                    viewModel.getAllTags()

                    viewModel.showConnectionError(HIDE)
                }
            }
            else -> {
                val windowInfo = windowInfo()
                val isPortrait = windowInfo.isPortraitOrientation
                val isCompact = windowInfo.windowType == Compact
                val lowestDimension = if (isPortrait) maxWidth else maxHeight
                val padding = 12.dp
//                val gridCellSize = (when (windowInfo.windowType) {
//                    is Compact -> lowestDimension / 2
//                    is Medium -> lowestDimension / 4
//                    is Expanded -> lowestDimension / 6
//                }) - padding

                when {
                    isCompact && isPortrait -> HomePortraitCompactView(viewModel, appState, goToPicsListScreen)
                    isCompact -> HomeLandscapeCompactView(viewModel, appState, goToPicsListScreen, padding)
                    isPortrait -> HomePortraitMediumView(viewModel, appState, goToPicsListScreen, goToSearchScreen, maxWidth, padding)
                    else -> HomeLandscapeMediumView(viewModel, appState, goToPicsListScreen, goToSearchScreen, maxHeight, padding)
                }

                if (appState.isLoading) CircularProgressIndicator()
            }
        }
    }
}