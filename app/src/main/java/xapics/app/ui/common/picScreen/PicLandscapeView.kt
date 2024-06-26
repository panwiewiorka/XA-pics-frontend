package xapics.app.ui.common.picScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import xapics.app.ui.AppState
import xapics.app.ui.MainViewModel
import xapics.app.ui.composables.AsyncPic

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PicLandscapeView(
    viewModel: MainViewModel,
    appState: AppState,
    pagerState: PagerState,
) {
    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(if (appState.isFullscreen) Color.Black else Color.Transparent)
    ) {
        if (appState.picIndex != null && appState.picsList != null && appState.pic != null) { // TODO
            HorizontalPager(
                state = pagerState,
                pageSize = PageSize.Fill,
                modifier = Modifier
                    .aspectRatio(3 / 2f)
                    .fillMaxSize()
//                        key = { appState.picsList[it].id } // FIXME crashes when clicking TAGS
            ) {index ->
                val pic = appState.picsList[index]
                BoxWithConstraints {
                    AsyncPic(
                        url = pic.imageUrl,
                        description = pic.description,
                    ) {
                        viewModel.changeFullScreenMode()
                    }

                    LaunchedEffect(maxWidth) {
                        viewModel.updatePicDetailsWidth(maxWidth)
                    }
                }
            }
        }
    }
}