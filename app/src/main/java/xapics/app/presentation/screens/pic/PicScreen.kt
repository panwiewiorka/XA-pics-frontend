package xapics.app.presentation.screens.pic

import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import xapics.app.presentation.components.ConnectionErrorButton
import xapics.app.presentation.screens.pic.layouts.PicLandscapeLayout
import xapics.app.presentation.screens.pic.layouts.PicPortraitLayout
import xapics.app.presentation.windowInfo

@Composable
fun PicScreen(
    getCollection: (collection: String, () -> Unit) -> Unit,
    editCollection: (collection: String, picId: Int, () -> Unit) -> Unit,
    updateCollectionToSaveTo: (String) -> Unit,
    updatePicInfo: (Int) -> Unit,
    changeFullScreenMode: () -> Unit,
    isFullscreen: Boolean,
    showConnectionError: (Boolean) -> Unit,
    picScreenState: PicScreenState,
    goToPicsListScreen: (searchQuery: String) -> Unit,
    goToAuthScreen: () -> Unit,
) {
    val context = LocalContext.current

    if (picScreenState.picIndex != null && picScreenState.picsList.isNotEmpty()) {
        val pagerState = rememberPagerState(
            initialPage = picScreenState.picIndex,
            initialPageOffsetFraction = 0f
        ) {
            picScreenState.picsList.size
        }

        LaunchedEffect(Unit) {
//            if (picScreenState.picsList.size == 1 && state.topBarCaption != "Random pic") { // todo how to know it's a random pic without topBarCaption? index = -1 ?
//                Toast.makeText(
//                    context,
//                    "Showing the only pic found",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
        }

        LaunchedEffect(pagerState.currentPage) {
            updatePicInfo(pagerState.currentPage)
        }

        when {
            picScreenState.connectionError -> {
                ConnectionErrorButton {
//                    updatePicState(state.picIndex) // todo add dao action, not only state?
                    showConnectionError(false)
                }
            }
            windowInfo().isPortraitOrientation -> {
                PicPortraitLayout(
                    getCollection = getCollection,
                    editCollection = editCollection,
                    updateCollectionToSaveTo = updateCollectionToSaveTo,
                    changeFullScreenMode = changeFullScreenMode,
                    isFullscreen = isFullscreen,
                    picScreenState = picScreenState,
                    pagerState = pagerState,
                    goToPicsListScreen = goToPicsListScreen,
                    goToAuthScreen = goToAuthScreen
                )
            }
            else -> {
                PicLandscapeLayout(
                    getCollection = getCollection,
                    editCollection = editCollection,
                    updateCollectionToSaveTo = updateCollectionToSaveTo,
                    changeFullScreenMode = changeFullScreenMode,
                    isFullscreen = isFullscreen,
                    picScreenState = picScreenState,
                    pagerState = pagerState,
                    goToPicsListScreen = goToPicsListScreen,
                    goToAuthScreen = goToAuthScreen
                )
            }
        }
    }
}