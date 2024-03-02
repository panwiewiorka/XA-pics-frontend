package xapics.app.ui

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.imageLoader
import coil.request.ImageRequest
import xapics.app.AppState
import xapics.app.MainViewModel
import xapics.app.TAG
import xapics.app.data.PicsApi.Companion.BASE_URL
import xapics.app.ui.composables.AsyncPic
import xapics.app.ui.composables.ConnectionErrorButton

@Composable
fun PicsListScreen(
    viewModel: MainViewModel, appState: AppState, goToPicScreen: () -> Unit, caption: String
) {
    Text(text = caption)

    LaunchedEffect(Unit) {
//        Log.d(TAG, "PicsListScreen: topBarCaption = ${appState.topBarCaption}, captionsList.last = ${if(appState.captionsList.isNotEmpty()) appState.captionsList.last() else ""}")
//        if(appState.captionsList.isNotEmpty() && appState.topBarCaption != appState.captionsList.last()) {
//            viewModel.replaceTopBarCaptionWithPrevious()
//        }
        Log.d(TAG, caption)
        viewModel.updateTopBarCaption(caption)
    }


    val context = LocalContext.current

    // preloading images
    LaunchedEffect(appState.picsList) {
        appState.picsList?.forEach {
            val request = ImageRequest.Builder(context)
                .data(it.imageUrl)
                // Optional, but setting a ViewSizeResolver will conserve memory by limiting the size the image should be preloaded into memory at.
//            .size(ViewSizeResolver(imageView))
                .build()
            context.imageLoader.enqueue(request)
        }
    }

    if (appState.showConnectionError) {
        ConnectionErrorButton {
            appState.onRefresh()
            viewModel.changeConnectionErrorVisibility(false)
        }
    } else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            val scrollState = rememberLazyListState()

            if (appState.picsList == null) {
                // TODO
            } else if (appState.picsList.isEmpty() && !appState.isLoading) {
                Text("Nothing found :(")
            } else {
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        count = appState.picsList.size,
                        key = { appState.picsList[it].id },
                    ) {
                        BoxWithConstraints {
                            val height = ((maxWidth - 64.dp).value / 1.5).dp
                            val pic = appState.picsList[it]

                            AsyncPic(
                                url = BASE_URL + pic.imageUrl,
                                description = pic.description,
                                modifier = Modifier
                                    .padding(horizontal = 32.dp)
                                    .padding(bottom = 16.dp)
                                    .height(height)
                                    .clip(RoundedCornerShape(2.dp))
                            ) {
                                viewModel.updatePicState(it)
                                goToPicScreen()
                            }
                        }
                    }
                }
            }

            if(appState.isLoading) {
                CircularProgressIndicator()
            }
        }
    }
}