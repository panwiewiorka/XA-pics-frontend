package xapics.app.presentation.screens.pic

import android.os.Build
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsCompat
import xapics.app.Pic
import xapics.app.data.db.StateSnapshot
import xapics.app.presentation.components.ConnectionErrorButton
import xapics.app.presentation.screens.pic.layouts.PicLandscapeLayout
import xapics.app.presentation.screens.pic.layouts.PicPortraitLayout
import xapics.app.presentation.windowInfo

@OptIn(ExperimentalFoundationApi::class,)
@Composable
fun PicScreen(
    search: (query: String) -> Unit,
    saveStateSnapshot: (pic: Pic, picIndex: Int) -> Unit,
    getCollection: (collection: String, () -> Unit) -> Unit,
    editCollection: (collection: String, picId: Int, () -> Unit) -> Unit,
    updateCollectionToSaveTo: (String) -> Unit,
    changeFullScreenMode: () -> Unit,
    showConnectionError: (Boolean) -> Unit,
    picScreenState: PicScreenState,
    state: StateSnapshot,
    goToPicsListScreen: () -> Unit,
    goToAuthScreen: () -> Unit,
) {
    val context = LocalContext.current

    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
        val controller = LocalView.current.windowInsetsController

        LaunchedEffect(picScreenState.isFullscreen) {
            if (picScreenState.isFullscreen) {
                controller?.apply {
                    hide(WindowInsetsCompat.Type.statusBars())
                    hide(WindowInsetsCompat.Type.navigationBars())
                    systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            } else {
                controller?.apply {
                    show(WindowInsetsCompat.Type.statusBars())
                    show(WindowInsetsCompat.Type.navigationBars())
                    systemBarsBehavior = WindowInsetsController.BEHAVIOR_DEFAULT
                }
            }
        }
    }

    if (state.picIndex != null) {
        val pagerState = rememberPagerState(
            initialPage = state.picIndex,
            initialPageOffsetFraction = 0f
        ) {
            state.picsList.size
        }

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
            saveStateSnapshot(state.picsList[pagerState.currentPage], pagerState.currentPage)
        }

        when {
            picScreenState.showConnectionError -> {
                ConnectionErrorButton {
//                    updatePicState(state.picIndex) // todo add dao action, not only state?
                    showConnectionError(false)
                }
            }
            windowInfo().isPortraitOrientation -> {
                PicPortraitLayout(
                    search = search,
                    getCollection = getCollection,
                    editCollection = editCollection,
                    updateCollectionToSaveTo = updateCollectionToSaveTo,
                    changeFullScreenMode = changeFullScreenMode,
                    picScreenState = picScreenState,
                    state = state,
                    pagerState = pagerState,
                    goToPicsListScreen = goToPicsListScreen,
                    goToAuthScreen = goToAuthScreen
                )
            }
            else -> {
                PicLandscapeLayout(
                    search = search,
                    getCollection = getCollection,
                    editCollection = editCollection,
                    updateCollectionToSaveTo = updateCollectionToSaveTo,
                    changeFullScreenMode = changeFullScreenMode,
                    picScreenState = picScreenState,
                    state = state,
                    pagerState = pagerState,
                    goToPicsListScreen = goToPicsListScreen,
                    goToAuthScreen = goToAuthScreen
                )
            }
        }
    }
}