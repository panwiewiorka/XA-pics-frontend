package xapics.app.presentation.screens.pic

import android.os.Build
import android.view.WindowInsetsController
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsCompat
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
    showConnectionError: (Boolean) -> Unit,
    picScreenState: PicScreenState,
    goToPicsListScreen: (searchQuery: String) -> Unit,
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
                    picScreenState = picScreenState,
                    pagerState = pagerState,
                    goToPicsListScreen = goToPicsListScreen,
                    goToAuthScreen = goToAuthScreen
                )
            }
        }
    }
}