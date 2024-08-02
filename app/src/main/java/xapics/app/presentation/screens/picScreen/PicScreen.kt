package xapics.app.presentation.screens.picScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import xapics.app.StateSnapshot
import xapics.app.presentation.AppState
import xapics.app.presentation.components.ConnectionErrorButton
import xapics.app.presentation.screens.picScreen.layouts.PicLandscapeLayout
import xapics.app.presentation.screens.picScreen.layouts.PicPortraitLayout
import xapics.app.presentation.windowInfo

@OptIn(ExperimentalFoundationApi::class,)
@Composable
fun PicScreen(
    search: (query: String) -> Unit,
    saveStateSnapshot: (String) -> Unit,
    getCollection: (collection: String, () -> Unit) -> Unit,
    editCollection: (collection: String, picId: Int, () -> Unit) -> Unit,
    updateCollectionToSaveTo: (String) -> Unit,
    changeFullScreenMode: () -> Unit,
//    updateTopBarCaption: (query: String) -> Unit,
    updatePicState: (picIndex: Int) -> Unit,
    updateStateSnapshot: () -> Unit,
    showConnectionError: (Boolean) -> Unit,
    stateHistory: MutableList<StateSnapshot>,
//    topBarCaption: Flow
    appState: AppState,
    goToPicsListScreen: () -> Unit,
    goToAuthScreen: () -> Unit,
) {
    val context = LocalContext.current

    if (appState.picIndex != null) {
        val pagerState = rememberPagerState(
            initialPage = appState.picIndex,
            initialPageOffsetFraction = 0f
        ) {
            appState.picsList.size
        }

        /*
    //    val scope = rememberCoroutineScope()
        var animateFirstMove by remember { mutableStateOf(true) }
        var animateValue by remember { mutableStateOf(15.dp) }
        var alphaValue by remember { mutableFloatStateOf(0f) }

        val swipeArrowsOffset by animateDpAsState(
            targetValue = animateValue,
            animationSpec = tween(600, 0, easing = EaseInOutSine),
            finishedListener = {
                animateValue = (-5).dp
                animateFirstMove = false
            },
            label = "swipe offset"
        )
        val swipeArrowsAlpha by animateFloatAsState(
            targetValue = if (animateFirstMove) alphaValue else 0f,
            animationSpec = if (animateFirstMove) {
                tween(700, 200, easing = EaseInOutSine)
            } else {
                tween(1000, 200, easing = EaseInOutSine)
            },
            label = "swipe alpha",
        )

        LaunchedEffect(Unit) {
            animateValue = (-10).dp
            alphaValue = 1f
        }
         */

        LaunchedEffect(Unit) {
//        updateTopBarCaption(stateHistory.last().topBarCaption)
//        if (appState.picsList.size == 1 && appState.topBarCaption != "Random pic") { // TODO change to TopBarViewModel instance
//            Toast.makeText(
//                context,
//                "Showing the only pic found",
//                Toast.LENGTH_SHORT
//            ).show()
//        }
        }

        LaunchedEffect(pagerState.currentPage) {
            updatePicState(pagerState.currentPage)
            updateStateSnapshot() // fixme remove delay from UpdatePicUseCase and change the order of updating to "first dp then state"
        }

        when {
            appState.showConnectionError -> {
                ConnectionErrorButton {
                    updatePicState(appState.picIndex)
                    showConnectionError(false)
                }
            }
            windowInfo().isPortraitOrientation -> {
                PicPortraitLayout(
                    search = search,
                    saveStateSnapshot = saveStateSnapshot,
                    getCollection = getCollection,
                    editCollection = editCollection,
                    updateCollectionToSaveTo = updateCollectionToSaveTo,
                    changeFullScreenMode = changeFullScreenMode,
                    appState = appState,
                    pagerState = pagerState,
                    goToPicsListScreen = goToPicsListScreen,
                    goToAuthScreen = goToAuthScreen
                )
            }
            else -> {
                PicLandscapeLayout(
                    search = search,
                    saveStateSnapshot = saveStateSnapshot,
                    getCollection = getCollection,
                    editCollection = editCollection,
                    updateCollectionToSaveTo = updateCollectionToSaveTo,
                    changeFullScreenMode = changeFullScreenMode,
                    appState = appState,
                    pagerState = pagerState,
                    goToPicsListScreen = goToPicsListScreen,
                    goToAuthScreen = goToAuthScreen
                )
            }
        }
    }
}