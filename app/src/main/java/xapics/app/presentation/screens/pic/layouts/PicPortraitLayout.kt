package xapics.app.presentation.screens.pic.layouts

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
import xapics.app.presentation.components.AsyncPic
import xapics.app.presentation.screens.pic.PicScreenState
import xapics.app.presentation.screens.pic.components.PicDetails
import xapics.app.presentation.screens.pic.components.PicTags

@Composable
fun PicPortraitLayout(
    editCollection: (collection: String, picId: Int, () -> Unit) -> Unit,
    updateCollectionToSaveTo: (String) -> Unit,
    changeFullScreenMode: () -> Unit,
    isFullscreen: Boolean,
    picScreenState: PicScreenState,
    pagerState: PagerState,
    goToPicsListScreen: (searchQuery: String) -> Unit,
    goToAuthScreen: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(if (isFullscreen) Color.Black else Color.Transparent)
            .padding(vertical = 32.dp)
    ) {
        if (isFullscreen) {
            Spacer(modifier = Modifier.weight(1f))
        }

        Box {
            HorizontalPager(
                state = pagerState,
                pageSize = PageSize.Fill,
                beyondViewportPageCount = 1,
                key = { picScreenState.picsList[it].id }
            ) {index ->
                val pic = picScreenState.picsList[index]
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

        if (isFullscreen) {
            Spacer(modifier = Modifier.weight(1f))
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                PicDetails(
                    editCollection = editCollection,
                    updateCollectionToSaveTo = updateCollectionToSaveTo,
                    blurContent = {},
                    picScreenState = picScreenState,
                    picDetailsWidth = 1.dp,
                    goToAuthScreen = goToAuthScreen
                ) {}

                PicTags(
                    picScreenState = picScreenState,
                    goToPicsListScreen = goToPicsListScreen
                )
            }
        }

//            if(appState.isLoading) {
//                CircularProgressIndicator()
//            }
    }
}