package xapics.app.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.imageLoader
import coil.request.ImageRequest
import xapics.app.AppState
import xapics.app.OnPicsListScreenRefresh.*
import xapics.app.MainViewModel
import xapics.app.ShowHide.*
import xapics.app.data.PicsApi.Companion.BASE_URL
import xapics.app.ui.composables.AsyncPic
import xapics.app.ui.composables.ConnectionErrorButton

@Composable
fun PicsListScreen(
    viewModel: MainViewModel, appState: AppState, goToPicScreen: () -> Unit, popBackStack: () -> Unit, previousPage: String?
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.showPicsList(SHOW)
    }

    LaunchedEffect(appState.picsList) {
        if (appState.picsList?.size == 1) {
            popBackStack()
            if (previousPage != "PicScreen") goToPicScreen()
        } else {
            appState.picsList?.forEach {// preloading images
                val request = ImageRequest.Builder(context)
                    .data(it.imageUrl)
                    // Optional, but setting a ViewSizeResolver will conserve memory by limiting the size the image should be preloaded into memory at.
//            .size(ViewSizeResolver(imageView))
                    .build()
                context.imageLoader.enqueue(request)
            }
        }
    }

    when {
        appState.connectionError.isShown -> {
            ConnectionErrorButton {
                val toDo = viewModel.onPicsListScreenRefresh
                if (toDo.first == SEARCH) {
                    viewModel.search(toDo.second)
                } else {
                    viewModel.getCollection(toDo.second)
                }
                viewModel.showConnectionError(HIDE)
            }
        }
        !appState.picsListColumn.isShown -> { }
        else -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                val scrollState = rememberLazyListState()

                when {
                    appState.picsList == null -> {} // TODO what?
                    appState.picsList.isEmpty() && !appState.isLoading -> Text("Nothing found :(")
                    appState.picsList.size == 1 -> {} // going to PicScreen
                    else -> {
                        LazyColumn(
                            state = scrollState,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                count = appState.picsList.size,
                                key = { appState.picsList[it].id },
                            ) {
                                BoxWithConstraints {
                                    val height = ((maxWidth - 64.dp).value / 1.5).dp // TODO replace with vv .aspectRatio(3f / 2f, true/false) ?
                                    val pic = appState.picsList[it]

                                    AsyncPic(
                                        url = pic.imageUrl,
                                        description = pic.description,
                                        modifier = Modifier
                                            .padding(horizontal = 32.dp)
                                            .padding(bottom = 16.dp)
//                                            .aspectRatio(3f / 2f, true)
                                            .height(height)
                                            .clip(RoundedCornerShape(2.dp))
                                    ) {
                                        viewModel.updatePicState(it)
                                        viewModel.saveStateSnapshot()
                                        goToPicScreen()
                                    }
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
}