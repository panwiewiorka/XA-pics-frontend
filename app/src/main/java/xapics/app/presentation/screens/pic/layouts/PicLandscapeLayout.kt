package xapics.app.presentation.screens.pic.layouts

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import xapics.app.presentation.components.AsyncPic
import xapics.app.presentation.screens.pic.PicScreenState
import xapics.app.presentation.screens.pic.components.PicDetails

@Composable
fun PicLandscapeLayout(
    getCollection: (collection: String, () -> Unit) -> Unit,
    editCollection: (collection: String, picId: Int, () -> Unit) -> Unit,
    updateCollectionToSaveTo: (String) -> Unit,
    changeFullScreenMode: () -> Unit,
    isFullscreen: Boolean,
    picScreenState: PicScreenState,
    pagerState: PagerState,
    goToPicsListScreen: (searchQuery: String) -> Unit,
    goToAuthScreen: () -> Unit
) {
    var picDetailsWidth by remember { mutableStateOf(1.dp) }

    var blurTarget by remember { mutableStateOf(0.dp) }

    val blurAmount by animateDpAsState(
        targetValue = blurTarget,
        label = "user collections blur"
    )

    fun blurContent(blur: Boolean) {
        blurTarget = if (blur) 10.dp else 0.dp
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .blur(blurAmount)
            .background(if (isFullscreen) Color.Black else Color.Transparent)
    ) {
        HorizontalPager(
            state = pagerState,
            pageSize = PageSize.Fill,
            beyondViewportPageCount = 1,
            modifier = Modifier
                .weight(1f)
                .aspectRatio(3 / 2f)
//                        key = { appState.picsList[it].id } // FIXME crashes when clicking TAGS. Fix by assigning key=1 onTagsClick?
        ) {index ->
            val pic = picScreenState.picsList[index]
            BoxWithConstraints {
                AsyncPic(
                    url = pic.imageUrl,
                    description = pic.description,
                    indication = null,
                    onClick = changeFullScreenMode
                )

                LaunchedEffect(maxWidth) {
                    picDetailsWidth = maxWidth
                }
            }
        }

        if (!isFullscreen) {
            PicDetails(
                getCollection = getCollection,
                editCollection = editCollection,
                updateCollectionToSaveTo = updateCollectionToSaveTo,
                blurContent = ::blurContent,
                picScreenState = picScreenState,
                picDetailsWidth = picDetailsWidth,
                goToAuthScreen = goToAuthScreen,
                goToPicsListScreen = goToPicsListScreen
            )
        }
    }
}