package xapics.app.presentation.screens.picScreen.layouts

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import xapics.app.presentation.AppState
import xapics.app.presentation.composables.AsyncPic
import xapics.app.presentation.screens.picScreen.composables.PicDetails
import xapics.app.presentation.screens.picScreen.composables.PicTags

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PicPortraitLayout(
    search: (query: String) -> Unit,
    saveStateSnapshot: () -> Unit,
    getCollection: (collection: String, () -> Unit) -> Unit,
    editCollection: (collection: String, picId: Int, () -> Unit) -> Unit,
    updateCollectionToSaveTo: (String) -> Unit,
    changeFullScreenMode: () -> Unit,
    appState: AppState,
    pagerState: PagerState,
    goToPicsListScreen: () -> Unit,
    goToAuthScreen: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(if (appState.isFullscreen) Color.Black else Color.Transparent)
            .padding(vertical = 32.dp)
    ) {
        if (appState.isFullscreen) {
            Spacer(modifier = Modifier.weight(1f))
        }

        Box {

            /*
            if (pagerState.currentPage > 0) {
                Icon(
                    Icons.Default.KeyboardArrowLeft,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = -swipeArrowsOffset)
                        .alpha(swipeArrowsAlpha)
                )
            }

            if (pagerState.currentPage < appState.picsList.lastIndex) {
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = swipeArrowsOffset)
                        .alpha(swipeArrowsAlpha)
                )
            }

             */

            HorizontalPager(
                state = pagerState,
                pageSize = PageSize.Fill,
                beyondBoundsPageCount = 1,
//                        key = { appState.picsList[it].id } // FIXME crashes when clicking TAGS. Fix by assigning key=1 onTagsClick?
            ) {index ->
                val pic = appState.picsList[index]
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AsyncPic(
                        url = pic.imageUrl,
                        description = pic.description,
                        indication = null,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        changeFullScreenMode()
                    }
                }
            }
        }

        if (appState.isFullscreen) {
            Spacer(modifier = Modifier.weight(1f))
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                PicDetails(
                    search = search,
                    saveStateSnapshot = saveStateSnapshot,
                    getCollection = getCollection,
                    editCollection = editCollection,
                    updateCollectionToSaveTo = updateCollectionToSaveTo,
                    blurContent = {},
                    appState = appState,
                    picDetailsWidth = 1.dp,
                    goToAuthScreen = goToAuthScreen,
                    goToPicsListScreen = {}
                )

                PicTags(
                    search = search,
                    saveStateSnapshot = saveStateSnapshot,
                    getCollection = getCollection,
                    appState = appState,
                    goToPicsListScreen = goToPicsListScreen,
                    goToAuthScreen = goToAuthScreen
                )
            }
        }

//            if(appState.isLoading) {
//                CircularProgressIndicator()
//            }
    }
}