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
import xapics.app.Pic
import xapics.app.data.db.StateSnapshot
import xapics.app.presentation.AppState
import xapics.app.presentation.components.AsyncPic
import xapics.app.presentation.screens.picScreen.components.PicDetails
import xapics.app.presentation.screens.picScreen.components.PicTags

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PicPortraitLayout(
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
    appState: AppState,
    state: StateSnapshot,
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
            HorizontalPager(
                state = pagerState,
                pageSize = PageSize.Fill,
                beyondBoundsPageCount = 1,
//                        key = { appState.picsList[it].id } // FIXME crashes when clicking TAGS. Fix by assigning key=1 onTagsClick?
            ) {index ->
                val pic = state.picsList[index]
                Box {
                    AsyncPic(
                        url = pic.imageUrl,
                        description = pic.description,
                        indication = null,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { changeFullScreenMode() }
                    )
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
                    state = state,
                    picDetailsWidth = 1.dp,
                    goToAuthScreen = goToAuthScreen,
                    goToPicsListScreen = {}
                )

                PicTags(
                    search = search,
                    saveStateSnapshot = saveStateSnapshot,
                    getCollection = getCollection,
                    appState = appState,
                    state = state,
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