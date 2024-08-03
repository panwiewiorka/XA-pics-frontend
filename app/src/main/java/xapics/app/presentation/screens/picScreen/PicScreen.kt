package xapics.app.presentation.screens.picScreen

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import xapics.app.Pic
import xapics.app.data.db.StateSnapshot
import xapics.app.presentation.AppState
import xapics.app.presentation.components.ConnectionErrorButton
import xapics.app.presentation.screens.picScreen.layouts.PicLandscapeLayout
import xapics.app.presentation.screens.picScreen.layouts.PicPortraitLayout
import xapics.app.presentation.windowInfo

@OptIn(ExperimentalFoundationApi::class,)
@Composable
fun PicScreen(
    search: (query: String) -> Unit,
    saveStateSnapshot: (
        replaceExisting: Boolean,
        picsList: List<Pic>?,
        pic: Pic?,
        picIndex: Int?,
        topBarCaption: String?
    ) -> Unit,
    getCollection: (collection: String, () -> Unit) -> Unit,
    editCollection: (collection: String, picId: Int, () -> Unit) -> Unit,
    updateCollectionToSaveTo: (String) -> Unit,
    changeFullScreenMode: () -> Unit,
    updatePicState: (picIndex: Int) -> Unit,
//    updateStateSnapshot: () -> Unit,
    showConnectionError: (Boolean) -> Unit,
    appState: AppState,
    state: StateSnapshot,
    goToPicsListScreen: () -> Unit,
    goToAuthScreen: () -> Unit,
) {
    val context = LocalContext.current

    if (state.picIndex != null) {
        val pagerState = rememberPagerState(
            initialPage = state.picIndex,
            initialPageOffsetFraction = 0f
        ) {
            state.picsList.size
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
        if (state.picsList.size == 1 && state.topBarCaption != "Random pic") {
            Toast.makeText(
                context,
                "Showing the only pic found",
                Toast.LENGTH_SHORT
            ).show()
        }
        }

        LaunchedEffect(pagerState.currentPage) {
//            updatePicState(pagerState.currentPage)
            saveStateSnapshot(true, null, state.picsList[pagerState.currentPage], pagerState.currentPage, null)
        }

        when {
            appState.showConnectionError -> {
                ConnectionErrorButton {
//                    updatePicState(state.picIndex) // todo add dao action, not only state?
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
                    state = state,
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
                    state = state,
                    pagerState = pagerState,
                    goToPicsListScreen = goToPicsListScreen,
                    goToAuthScreen = goToAuthScreen
                )
            }
        }
    }
}