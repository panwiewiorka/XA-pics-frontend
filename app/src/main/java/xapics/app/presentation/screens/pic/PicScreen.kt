package xapics.app.presentation.screens.pic

import android.widget.Toast
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.Flow
import xapics.app.presentation.screens.pic.layouts.PicLandscapeLayout
import xapics.app.presentation.screens.pic.layouts.PicPortraitLayout
import xapics.app.presentation.windowInfo

@Composable
fun PicScreen(
    messages: Flow<String>,
    editCollection: (collection: String, picId: Int, () -> Unit) -> Unit,
    updateCollectionToSaveTo: (String) -> Unit,
    updatePicInfo: (Int) -> Unit,
    changeFullScreenMode: () -> Unit,
    isFullscreen: Boolean,
    picScreenState: PicScreenState,
    goToPicsListScreen: (searchQuery: String) -> Unit,
    goToAuthScreen: () -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(messages) {
        messages.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    if (picScreenState.picIndex != null && picScreenState.picsList.isNotEmpty()) {
        val pagerState = rememberPagerState(
            initialPage = picScreenState.picIndex,
            initialPageOffsetFraction = 0f
        ) {
            picScreenState.picsList.size
        }

        LaunchedEffect(pagerState.currentPage) {
            updatePicInfo(pagerState.currentPage)
        }

        when {
//            picScreenState.connectionError -> {
//                ConnectionErrorButton {
//                    onConnectionError()
//                    showConnectionError(false)
//                }
//            }
            windowInfo().isPortraitOrientation -> {
                PicPortraitLayout(
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